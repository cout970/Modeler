package com.cout970.modeler.core.network

import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable

/**
 * Created by cout970 on 2016/12/20.
 */
data class Packet(var content: Serializable? = null) {

    fun encode(): ByteArray {
        return SerializationUtils.serialize(content)
    }

    fun decode(data: ByteArray) {
        content = SerializationUtils.deserialize(data)
    }
}