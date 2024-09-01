package com.ifreeze.applock.utils

/**
 * Enum class representing the different types of data models used for communication.
 */
enum class DataModelType{
    SignIn, StartStreaming,EndCall, Offer, Answer, IceCandidates
}


/**
 * Data class representing a data model used for communication between clients and servers.
 *
 * @property type The type of the data model, which determines the kind of data it carries. This can be `SignIn`, `StartStreaming`, `EndCall`, `Offer`, `Answer`, or `IceCandidates`.
 * @property username The username of the sender or initiator of the data model.
 * @property target The username or identifier of the recipient or target of the data model, if applicable. Can be `null` if not applicable.
 * @property data Any additional data associated with the data model. This can be of any type and can be `null` if no additional data is present.
 */
data class DataModel(
    val type: DataModelType?=null,
    val username:String,
    val target:String?=null,
    val data:Any?=null
)
