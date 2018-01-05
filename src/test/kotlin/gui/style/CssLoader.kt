package gui.style

import com.cout970.modeler.gui.style.CssLoader
import com.cout970.modeler.gui.style.StyleTarget
import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayInputStream

class CssLoaderTest {

    @Test
    fun `load empty file`() {
        val stream = ByteArrayInputStream(byteArrayOf())

        val stylesheet = CssLoader.loadStyleSheet(stream)

        Assert.assertEquals(emptyList<StyleTarget>(), stylesheet.targets)
    }

    @Test
    fun `load file with charset header`() {
        val file = Thread.currentThread().contextClassLoader.getResourceAsStream("./gui/header.css")

        val stylesheet = CssLoader.loadStyleSheet(file)

        Assert.assertEquals(emptyList<StyleTarget>(), stylesheet.targets)
    }

    @Test
    fun `load complex file`() {
        val file = Thread.currentThread().contextClassLoader.getResourceAsStream("./gui/styles.css")

        val stylesheet = CssLoader.loadStyleSheet(file)

        println(stylesheet)
    }
}