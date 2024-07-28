package com.ifreeze.applock.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.FragmentNewShareScreenBinding
import com.ifreeze.applock.utils.GlobalSettingsShare
import com.ifreeze.applock.utils.TokenUtils
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.RtcEngineEx
import io.agora.rtc2.ScreenCaptureParameters
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random

const val TAG = "islam"

class NewShareScreen : Fragment() {
    private lateinit var binding: FragmentNewShareScreenBinding
    lateinit var engine: RtcEngineEx
    var join = false
    val screenCaptureParameters = ScreenCaptureParameters()
    val globalSetting = GlobalSettingsShare()
    var uidL: Int = generateRandomUserId()
    var remoteUid = -1
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_share_screen, container, false)
        binding = FragmentNewShareScreenBinding.bind(view)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = RtcEngineConfig()
        config.mContext = requireContext()
        config.mAppId = getString(R.string.agora_app_id)
        config.mChannelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        config.mEventHandler = iRtcEngineEventHandler
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT)
        config.mAreaCode = globalSetting.areaCode
        engine = RtcEngine.create(config) as RtcEngineEx
        engine.setParameters(
            "{"
                    + "\"rtc.report_app_scenario\":"
                    + "{"
                    + "\"appScenario\":" + 100 + ","
                    + "\"serviceType\":" + 11 + ","
                    + "\"appVersion\":\"" + RtcEngine.getSdkVersion() + "\""
                    + "}"
                    + "}"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnJoin.setOnClickListener {
            if (join) {
                leaveChannel()
            } else {
                joinChannel()
            }
        }
    }

    private fun joinChannel() {
        engine.setParameters("test")
        engine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        engine.enableVideo()
        engine.enableAudio()
        engine.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_640x360,
                FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE
            )
        )
        engine.setDefaultAudioRoutetoSpeakerphone(true)
        val metrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getRealMetrics(metrics)
        screenCaptureParameters.captureVideo = true
        screenCaptureParameters.videoCaptureParameters.width = 720
        screenCaptureParameters.videoCaptureParameters.height =
            (720 * 1.0f / metrics.widthPixels * metrics.heightPixels).toInt()
        screenCaptureParameters.videoCaptureParameters.framerate = 15
        screenCaptureParameters.captureAudio = true
        screenCaptureParameters.audioCaptureParameters.captureSignalVolume = 60
        engine.startScreenCapture(screenCaptureParameters)
        startScreenSharePreview()
        TokenUtils.gen(requireContext(), "test", uidL) {
            val accessToken =
                "007eJxTYLD+6LF/a+9yhmindpcnnFumJK7e5rSQV1xy1+eV3Emp7/sVGBKTTS1MLZONzCyNjE1MU5KTjC1SLdKMk00MjS2NUpINfVYtS2sIZGRgkbnJwAiFYD5DSWpxCQMDABwJHoI="
            val options = ChannelMediaOptions()
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            options.autoSubscribeVideo = true
            options.autoSubscribeAudio = true
            options.publishCameraTrack = false
            options.publishMicrophoneTrack = false
            options.publishScreenCaptureVideo = true
            options.publishScreenCaptureAudio = true

            Log.d(TAG, "joinChannel: chan$uidL")
            val res = engine.joinChannel(accessToken, "test", uidL, options)
            Log.d(TAG, "joinChannel: res $res")
            binding.btnJoin.isEnabled = false
            engine.updateChannelMediaOptions(options)

        }
    }

    private fun startScreenSharePreview() {
        Log.d(TAG, "startScreenSharePreview: 0")

        val surfaceView = SurfaceView(requireContext())
        if (binding.flCamera.getChildCount() > 0) {
            Log.d(TAG, "startScreenSharePreview: getChildCount > 0")
            binding.flCamera.removeAllViews()
        }
        // Add to the local container
        binding.flCamera.addView(
            surfaceView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        // Setup local video to render your local camera preview
        val local = VideoCanvas(surfaceView, Constants.RENDER_MODE_FIT, uidL)
        local.mirrorMode = Constants.VIDEO_MIRROR_MODE_DISABLED
        local.sourceType = Constants.VIDEO_SOURCE_SCREEN_PRIMARY
        engine.setupLocalVideo(local)
        Log.d(TAG, "startScreenSharePreview: start preview")
        engine.startPreview(Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY)
    }

    private fun stopScreenSharePreview() {
        binding.flCamera.removeAllViews()
        engine.setupLocalVideo(VideoCanvas(null))
        engine.stopPreview(Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY)
    }

    private val iRtcEngineEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        override fun onError(err: Int) {
            Log.e(
                TAG,
                String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err))
            )
            Toast.makeText(
                requireContext(),
                "onError code %d message %s " + err + " " + RtcEngine.getErrorDescription(err),
                Toast.LENGTH_LONG
            ).show()
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.d(
                TAG,
                String.format("onJoinChannelSuccess channel %s uid %d", channel, uid)
            )
            Toast.makeText(
                requireContext(),
                "onJoinChannelSuccess channel %s uid %d $channel $uid", Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch(Dispatchers.Main) {
                binding.btnJoin.isEnabled = true
                binding.btnJoin.text = "leave"
            }


        }

        override fun onLocalVideoStateChanged(
            source: Constants.VideoSourceType,
            state: Int,
            error: Int
        ) {
            super.onLocalVideoStateChanged(source, state, error)
            Log.d(
                TAG,
                "onLocalVideoStateChanged source=$source, state=$state, error=$error"
            )
            if (source == Constants.VideoSourceType.VIDEO_SOURCE_SCREEN_PRIMARY) {
                if (state == Constants.LOCAL_VIDEO_STREAM_STATE_ENCODING) {
                    if (error == Constants.ERR_OK) {
                        Toast.makeText(
                            requireContext(),
                            "Screen sharing start successfully" + " ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (state == Constants.LOCAL_VIDEO_STREAM_STATE_FAILED) {
                    if (error == Constants.ERR_SCREEN_CAPTURE_SYSTEM_NOT_SUPPORTED) {
                        Toast.makeText(
                            requireContext(),
                            "Screen sharing has been cancelled",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Screen sharing start failed for error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    leaveChannel()
                }
            }
        }

        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
            Log.i(
                TAG,
                "onRemoteVideoStateChanged:uid->$uid, state->$state"
            )
        }

        override fun onRemoteVideoStats(stats: RemoteVideoStats) {
            super.onRemoteVideoStats(stats)
            Log.d(
                TAG,
                "onRemoteVideoStats: width:" + stats.width + " x height:" + stats.height
            )
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.d(TAG, "onUserJoined->$uid")
            if (remoteUid > 0) {
                Log.d(TAG, "onUserJoined-> remote uid$remoteUid")
                return
            }
            remoteUid = uid
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "user %d onUserJoined! reason:%d$uid $elapsed",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(TAG, "onUserJoined:runOnUIThread ")
                val renderView = SurfaceView(context)
                engine.setupRemoteVideo(VideoCanvas(renderView, Constants.RENDER_MODE_FIT, uid))
                Log.d(TAG, "onUserJoined remote: runOnUIThread $uid $renderView")
                binding.flScreenshare.removeAllViews()
                binding.flScreenshare.addView(renderView)
            }

        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d(
                TAG,
                String.format("user %d offline! reason:%d", uid, reason)
            )
            Toast.makeText(
                requireContext(),
                "user %d offline! reason:%d$uid $reason",
                Toast.LENGTH_LONG
            ).show()
            if (remoteUid == uid) {
                Log.d(
                    TAG,
                    String.format("user %d offline! remoteUid == uid :%d", uid, reason)
                )
                lifecycleScope.launch(Dispatchers.Main) {
                    if (remoteUid == uid) {
                        remoteUid = -1
                        binding.flScreenshare.removeAllViews()
                        engine.setupRemoteVideo(VideoCanvas(null, Constants.RENDER_MODE_FIT, uid))
                    }
                }

            }
        }
    }

    private fun leaveChannel() {
        join = false
        binding.btnJoin.text = "join"
        binding.flCamera.removeAllViews()
        binding.flScreenshare.removeAllViews()
        remoteUid = -1
        engine.leaveChannel()
        engine.stopScreenCapture()
        engine.stopPreview()
    }

    private fun generateRandomUserId(): Int {
        val random = Random()
        return random.nextInt(100)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::engine.isInitialized) {
            engine.leaveChannel()
            engine.stopScreenCapture()
            engine.stopPreview()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewShareScreen().apply {

            }
    }
}