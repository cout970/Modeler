package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.log.print
import org.liquidengine.legui.exception.LeguiExceptionTemplate
import org.liquidengine.legui.image.ImageChannels
import org.liquidengine.legui.image.LoadableImage
import org.liquidengine.leutil.io.IOUtil
import org.lwjgl.stb.STBImage
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Clone of BufferedImage without logging errors to and external file
 */
class LogFreeBufferedImage(path: String) : LoadableImage(path) {
    private var width: Int = 0
    private var height: Int = 0
    private var channels: ImageChannels? = null
    private var imageData: ByteBuffer? = null


    init {
        try {
            load()
        } catch (e: Exception) {
            e.print()
        }
    }

    /**
     * Should be used to load image data from source.
     */
    override fun load() {
        try {
            val byteBuffer = IOUtil.resourceToByteBuffer(path)
            val width = intArrayOf(0)
            val height = intArrayOf(0)
            val channels = intArrayOf(0)
            val imageData = STBImage.stbi_load_from_memory(byteBuffer, width, height, channels, 4)

            if (imageData != null) {
                this.width = width[0]
                this.height = height[0]
                this.channels = ImageChannels.instance(channels[0])
                this.imageData = imageData
            } else { // if error occurs
                throw LeguiExceptionTemplate.FAILED_TO_LOAD_IMAGE.create(STBImage.stbi_failure_reason())
            }
        } catch (e: IOException) {
            throw LeguiExceptionTemplate.FAILED_TO_LOAD_IMAGE.create(e, e.message)
        }

    }

    /**
     * Returns image width.
     *
     * @return image width.
     */
    override fun getWidth(): Int {
        return width
    }

    /**
     * Returns image height.
     *
     * @return image height.
     */
    override fun getHeight(): Int {
        return height
    }

    /**
     * Returns image data.
     *
     * @return image data.
     */
    override fun getImageData(): ByteBuffer? {
        return imageData
    }

    /**
     * Returns image channels.
     *
     * @return image channels.
     */
    override fun getChannels(): ImageChannels? {
        return channels
    }

}
