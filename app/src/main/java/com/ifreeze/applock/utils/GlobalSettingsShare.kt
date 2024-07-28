package com.ifreeze.applock.utils

import android.text.TextUtils
import android.util.Log
import io.agora.rtc2.Constants
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.proxy.LocalAccessPointConfiguration
import io.agora.rtc2.proxy.LocalAccessPointConfiguration.AdvancedConfigInfo
import io.agora.rtc2.proxy.LocalAccessPointConfiguration.LogUploadServerInfo
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.FRAME_RATE
import io.agora.rtc2.video.VideoEncoderConfiguration.ORIENTATION_MODE
import io.agora.rtc2.video.VideoEncoderConfiguration.VideoDimensions

/**
 * The type Global settings.
 */
class GlobalSettingsShare {
    /**
     * Sets video encoding dimension.
     *
     * @param videoEncodingDimension the video encoding dimension
     */
    var videoEncodingDimension: String? = null
        /**
         * Gets video encoding dimension.
         *
         * @return the video encoding dimension
         */
        get() = if (field == null) {
            "VD_960x540"
        } else {
            field
        }

    /**
     * Sets video encoding frame rate.
     *
     * @param videoEncodingFrameRate the video encoding frame rate
     */
    var videoEncodingFrameRate: String? = null
        /**
         * Gets video encoding frame rate.
         *
         * @return the video encoding frame rate
         */
        get() = if (field == null) {
            FRAME_RATE.FRAME_RATE_FPS_15.name
        } else {
            field
        }

    /**
     * Sets video encoding orientation.
     *
     * @param videoEncodingOrientation the video encoding orientation
     */
    var videoEncodingOrientation: String? = null
        /**
         * Gets video encoding orientation.
         *
         * @return the video encoding orientation
         */
        get() = if (field == null) {
            ORIENTATION_MODE.ORIENTATION_MODE_ADAPTIVE.name
        } else {
            field
        }
    /**
     * Gets area code str.
     *
     * @return the area code str
     */
    /**
     * Sets area code str.
     *
     * @param areaCodeStr the area code str
     */
    var areaCodeStr: String = "GLOBAL"

    /**
     * The Private cloud ip.
     */
    // private cloud config
    var privateCloudIp: String = ""

    /**
     * The Private cloud log report enable.
     */
    var privateCloudLogReportEnable: Boolean = false

    /**
     * The Private cloud log server domain.
     */
    var privateCloudLogServerDomain: String = ""

    /**
     * The Private cloud log server port.
     */
    var privateCloudLogServerPort: Int = 80

    /**
     * The Private cloud log server path.
     */
    var privateCloudLogServerPath: String = ""

    /**
     * The Private cloud use https.
     */
    var privateCloudUseHttps: Boolean = false

    // public String privateCloudIp = "10.62.0.85";
    // public boolean privateCloudLogReportEnable = true;
    // public String privateCloudLogServerDomain = "10.72.0.29";
    // public int privateCloudLogServerPort = 442;
    // public String privateCloudLogServerPath = "/kafka/log/upload/v1";
    // public boolean privateCloudUseHttps = true;

    val privateCloudConfig: LocalAccessPointConfiguration?
        /**
         * Gets private cloud config.
         *
         * @return the private cloud config
         */
        get() {
            val config = LocalAccessPointConfiguration()
            if (TextUtils.isEmpty(privateCloudIp)) {
                return null
            }
            config.ipList = ArrayList()
            config.ipList.add(privateCloudIp)
            config.domainList = ArrayList()
            config.mode = Constants.LOCAL_RPOXY_LOCAL_ONLY
            if (privateCloudLogReportEnable) {
                val advancedConfig = AdvancedConfigInfo()
                val logUploadServer = LogUploadServerInfo()
                logUploadServer.serverDomain = privateCloudLogServerDomain
                logUploadServer.serverPort = privateCloudLogServerPort
                logUploadServer.serverPath = privateCloudLogServerPath
                logUploadServer.serverHttps = privateCloudUseHttps

                advancedConfig.logUploadServer = logUploadServer
                config.advancedConfig = advancedConfig
            }
            return config
        }

    val videoEncodingDimensionObject: VideoDimensions?
        /**
         * Gets video encoding dimension object.
         *
         * @return the video encoding dimension object
         */
        get() {
            var value = VideoEncoderConfiguration.VD_960x540
            try {
                val tmp = VideoEncoderConfiguration::class.java.getDeclaredField(
                    videoEncodingDimension
                )
                tmp.isAccessible = true
                value = tmp[null] as VideoDimensions
            } catch (e: NoSuchFieldException) {
                Log.e("Field", "Can not find field " + videoEncodingDimension)
            } catch (e: IllegalAccessException) {
                Log.e("Field", "Could not access field " + videoEncodingDimension)
            }
            return value
        }

    val areaCode: Int
        /**
         * Gets area code.
         *
         * @return the area code
         */
        get() = if ("CN" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_CN
        } else if ("NA" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_NA
        } else if ("EU" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_EU
        } else if ("AS" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_AS
        } else if ("JP" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_JP
        } else if ("IN" == areaCodeStr) {
            RtcEngineConfig.AreaCode.AREA_CODE_IN
        } else {
            RtcEngineConfig.AreaCode.AREA_CODE_GLOB
        }
}
