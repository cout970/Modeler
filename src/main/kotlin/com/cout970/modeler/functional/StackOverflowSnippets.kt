package com.cout970.modeler.functional

import sun.net.www.protocol.file.FileURLConnection
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.JarURLConnection
import java.net.URL
import java.net.URLConnection
import java.net.URLDecoder
import java.util.*
import java.util.jar.JarEntry


/**
 * Created by cout970 on 2017/07/19.
 */
object StackOverflowSnippets {


    /**
     * https://stackoverflow.com/a/22462785
     * Private helper method

     * @param directory
     * *            The directory to start with
     * *
     * @param pckgname
     * *            The package name to search for. Will be needed for getting the
     * *            Class object.
     * *
     * @param classes
     * *            if a file isn't loaded but still is in the directory
     * *
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    private fun checkDirectory(directory: File, pckgname: String, classes: ArrayList<Class<*>>) {
        var tmpDirectory: File

        if (directory.exists() && directory.isDirectory) {
            val files = directory.list()

            for (file in files!!) {
                if (file.endsWith(".class")) {
                    try {
                        classes.add(Class.forName(pckgname + '.'
                                                  + file.substring(0, file.length - 6)))
                    } catch (e: NoClassDefFoundError) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }

                } else {
                    tmpDirectory = File(directory, file)
                    if (tmpDirectory.isDirectory) {
                        checkDirectory(tmpDirectory, pckgname + "." + file, classes)
                    }
                }
            }
        }
    }

    /**
     * https://stackoverflow.com/a/22462785
     * Private helper method.

     * @param connection
     * *            the connection to the jar
     * *
     * @param pckgname
     * *            the package name to search for
     * *
     * @param classes
     * *            the current ArrayList of all classes. This method will simply
     * *            add new classes.
     * *
     * @throws ClassNotFoundException
     * *             if a file isn't loaded but still is in the jar file
     * *
     * @throws IOException
     * *             if it can't correctly read from the jar file.
     */
    @Throws(ClassNotFoundException::class, IOException::class)
    private fun checkJarFile(connection: JarURLConnection, pckgname: String, classes: ArrayList<Class<*>>) {
        val jarFile = connection.jarFile
        val entries = jarFile.entries()
        var name: String

        var jarEntry: JarEntry? = null
        while (entries.hasMoreElements()) {
            jarEntry = entries.nextElement()
            if (jarEntry == null) break
            name = jarEntry.name

            if (name.contains(".class")) {
                name = name.substring(0, name.length - 6).replace('/', '.')

                if (name.contains(pckgname)) {
                    classes.add(Class.forName(name))
                }
            }
        }
    }

    /**
     * https://stackoverflow.com/a/22462785
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader

     * @param pckgname
     * *            the package name to search
     * *
     * @return a list of classes that exist within that package
     * *
     * @throws ClassNotFoundException
     * *             if something went wrong
     */
    @Throws(ClassNotFoundException::class)
    fun getClassesForPackage(pckgname: String): ArrayList<Class<*>> {
        val classes = ArrayList<Class<*>>()

        try {
            val cld = Thread.currentThread().contextClassLoader
                      ?: throw ClassNotFoundException("Can't get class loader.")

            val resources = cld.getResources(pckgname.replace('.', '/'))
            var connection: URLConnection
            var url: URL?

            while (resources.hasMoreElements()) {
                url = resources.nextElement()
                if (url == null) break
                try {
                    connection = url.openConnection()

                    if (connection is JarURLConnection) {
                        checkJarFile(connection, pckgname,
                                classes)
                    } else if (connection is FileURLConnection) {
                        try {
                            checkDirectory(
                                    File(URLDecoder.decode(url.path, "UTF-8")), pckgname, classes)
                        } catch (ex: UnsupportedEncodingException) {
                            throw ClassNotFoundException(
                                    pckgname + " does not appear to be a valid package (Unsupported encoding)",
                                    ex)
                        }

                    } else throw ClassNotFoundException(
                            pckgname + " (${url.path}) does not appear to be a valid package")

                } catch (ioex: IOException) {
                    throw ClassNotFoundException(
                            "IOException was thrown when trying to get all resources for " + pckgname, ioex)
                }

            }
        } catch (ex: NullPointerException) {
            throw ClassNotFoundException(
                    pckgname + " does not appear to be a valid package (Null pointer exception)",
                    ex)
        } catch (ioex: IOException) {
            throw ClassNotFoundException(
                    "IOException was thrown when trying to get all resources for " + pckgname, ioex)
        }
        return classes
    }
}
