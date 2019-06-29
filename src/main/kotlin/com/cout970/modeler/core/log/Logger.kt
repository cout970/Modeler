package com.cout970.modeler.core.log

import com.cout970.modeler.Debugger
import com.cout970.modeler.PathConstants
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.createParentsIfNeeded
import java.io.File
import java.io.PrintStream
import java.util.*

/**
 * Created by cout970 on 2016/12/28.
 */

object Logger {

    val level get() = Config.logLevel
    val logs = File(PathConstants.LOGS_FOLDER_PATH).apply { createParentsIfNeeded(true) }
    val stream = (if (Debugger.STATIC_DEBUG) System.out else object : PrintStream(File(logs, getFileName())) {
        override fun println() {
            super.println()
            System.out.println()
        }

        override fun println(any: Any?) {
            super.println(any)
            System.out.println(any)
        }

        override fun println(any: String?) {
            super.println(any)
            System.out.println(any)
        }
    })!!

    private fun getFileName(): String {
        val time = Calendar.getInstance()
        val year = time[Calendar.YEAR]
        val month = time[Calendar.MONTH]
        val day = time[Calendar.DAY_OF_MONTH]
        val hour = time[Calendar.HOUR_OF_DAY]
        val minute = time[Calendar.MINUTE]

        val prefix = "log_${year}_${month + 1}_${day}_$hour-$minute"
        var tryNum = 0
        if (File(logs, "$prefix.log").exists()) {
            while (File(logs, "${prefix}_$tryNum.log").exists()) {
                tryNum++
            }
            return "${prefix}_$tryNum.log"
        }
        return "$prefix.log"
    }
}

enum class Level(val priority: Int) {
    DEBUG(75),
    ERROR(1000),
    WARNING(500),
    NORMAL(250),
    FINE(100),
    FINEST(50),
    LOG_CLASSES(25)
}

inline fun log(level: Level, func: () -> String) {
    if (level.priority >= Logger.level.priority) {
        val time = Calendar.getInstance()
        val year = time[Calendar.YEAR]
        val month = time[Calendar.MONTH]
        val day = time[Calendar.DAY_OF_MONTH]
        val hour = time[Calendar.HOUR_OF_DAY]
        val minute = time[Calendar.MINUTE]
        val second = time[Calendar.SECOND]

        var extras = ""
        if (Logger.level == Level.LOG_CLASSES) {
            val clazz = Thread.currentThread().stackTrace[1].className
            extras = "[$clazz]"
        }
        Logger.stream.println("[$year-${month + 1}-$day][$hour:$minute:$second]$extras " + func())
        Logger.stream.flush()
    }
}

fun Throwable.print() {
    printStackTrace(Logger.stream)
    Logger.stream.flush()
}