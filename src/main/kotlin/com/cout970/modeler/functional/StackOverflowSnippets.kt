package com.cout970.modeler.functional

import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by cout970 on 2017/07/19.
 */
object StackOverflowSnippets {

    /**
     * https://stackoverflow.com/a/520344
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     * @param packageName The base package
     * *
     * @return The classes
     * *
     * @throws ClassNotFoundException
     * *
     * @throws IOException
     */
    @Throws(ClassNotFoundException::class, IOException::class)
    fun getClasses(packageName: String): List<Class<*>> {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val path = packageName.replace('.', '/')
        val resources = classLoader.getResources(path)
        val dirs = ArrayList<File>()
        while (resources.hasMoreElements()) {
            val resource = resources.nextElement()
            dirs.add(File(resource.file))
        }
        val classes = ArrayList<Class<*>>()
        for (directory in dirs) {
            classes.addAll(findClasses(directory, packageName))
        }
        return classes
    }

    /**
     * https://stackoverflow.com/a/520344
     * Recursive method used to find all classes in a given directory and subdirs.

     * @param directory   The base directory
     * *
     * @param packageName The package name for classes found inside the base directory
     * *
     * @return The classes
     * *
     * @throws ClassNotFoundException
     */
    @Throws(ClassNotFoundException::class)
    fun findClasses(directory: File, packageName: String): List<Class<*>> {
        val classes = ArrayList<Class<*>>()
        if (!directory.exists()) {
            return classes
        }
        val files = directory.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    assert(!file.name.contains("."))
                    classes.addAll(findClasses(file, packageName + "." + file.name))
                } else if (file.name.endsWith(".class")) {
                    classes.add(Class.forName(packageName + '.' + file.name.substring(0, file.name.length - 6)))
                }
            }
        }
        return classes
    }
}
