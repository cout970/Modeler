package com.cout970.modeler.controller

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
     *
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

        var jarEntry: JarEntry?
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
                        checkJarFile(
                            connection, pckgname,
                            classes
                        )
                    } else if (connection::class.java.simpleName == "FileURLConnection") {
                        try {
                            checkDirectory(
                                File(URLDecoder.decode(url.path, "UTF-8")), pckgname, classes
                            )
                        } catch (ex: UnsupportedEncodingException) {
                            throw ClassNotFoundException(
                                pckgname + " does not appear to be a valid package (Unsupported encoding)",
                                ex
                            )
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

    // https://stackoverflow.com/a/16018452
    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    fun similarity(s1: String, s2: String): Double {
        var longer = s1
        var shorter = s2
        if (s1.length < s2.length) { // longer should always have greater length
            longer = s2
            shorter = s1
        }
        val longerLength = longer.length
        return if (longerLength == 0) {
            1.0 /* both strings are zero length */
        } else (longerLength - editDistance(longer, shorter)) / longerLength.toDouble()

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    fun editDistance(s1: String, s2: String): Int {
        val varS1 = s1.toLowerCase()
        val varS2 = s2.toLowerCase()

        val costs = IntArray(varS2.length + 1)
        for (i in 0..varS1.length) {
            var lastValue = i
            for (j in 0..varS2.length) {
                if (i == 0)
                    costs[j] = j
                else {
                    if (j > 0) {
                        var newValue = costs[j - 1]
                        if (varS1[i - 1] != varS2[j - 1])
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1
                        costs[j - 1] = lastValue
                        lastValue = newValue
                    }
                }
            }
            if (i > 0)
                costs[varS2.length] = lastValue
        }
        return costs[varS2.length]
    }
}

/** https://github.com/gazolla/Kotlin-Algorithm/blob/master/Shuffle/Shuffle.kt
 *
 */
fun <T : Comparable<T>> shuffle(items: MutableList<T>): List<T> {
    val rg = Random()
    for (i in 0 until items.size) {
        val randomPosition = rg.nextInt(items.size)
        val tmp: T = items[i]
        items[i] = items[randomPosition]
        items[randomPosition] = tmp
    }
    return items
}

/* extension version */
fun <T> Iterable<T>.shuffle(): List<T> {
    val list = this.toMutableList()
    Collections.shuffle(list)
    return list
}
