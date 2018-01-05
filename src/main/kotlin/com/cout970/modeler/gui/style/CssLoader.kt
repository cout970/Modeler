package com.cout970.modeler.gui.style

import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList


data class StyleRule(val key: String, val value: String) {
    override fun toString(): String = "\t$key: $value;"
}

data class StyleTarget(val selectors: List<String>, val rules: List<StyleRule>) {
    override fun toString(): String = "${selectors.first()} {\n" + rules.joinToString("\n") + "\n}"
}

data class StyleSheet(val targets: List<StyleTarget>) {
    override fun toString(): String = "StyleSheet(\n" + targets.joinToString("\n\n") + "\n)"
}

object CssLoader {

    private const val EOF = (-1).toChar()

    class Reader(val content: String, var index: Int, val lookAheadBuffer: Deque<Char>)

    fun loadStyleSheet(input: InputStream): StyleSheet {
        val content = input.bufferedReader().readText()
        val reader = Reader(content, 0, ArrayDeque<Char>())

        try {
            readUtf8Header(reader)

            return StyleSheet(readTargets(reader))

        } catch (e: Exception) {
            val before = reader.content.take(reader.index).takeLast(50)

            throw IllegalStateException("${e.message}\nLast 50 chars before error:\n'$before'")
        }
    }

    fun Reader.peekChar(): Char {
        if (lookAheadBuffer.isEmpty()) {
            if (index >= content.length) return EOF
            val character = content[index++]
            lookAheadBuffer.addFirst(character)
        }
        return lookAheadBuffer.peekFirst()
    }

    fun Reader.popChar(): Char {
        if (lookAheadBuffer.isEmpty()) {
            if (index >= content.length) return EOF
            val character = content[index++]
            lookAheadBuffer.addFirst(character)
        }
        return lookAheadBuffer.removeFirst()
    }

    fun Reader.pushChar(char: Char) = lookAheadBuffer.addFirst(char)

    fun Reader.consume(str: String): Boolean {
        val returnBuffer = ArrayList<Char>(str.length)
        for (c in str) {
            val read = popChar()
            returnBuffer.add(read)
            if (read != c) {
                returnBuffer.asReversed().forEach { pushChar(it) }
                return false
            }
        }
        return true
    }

    fun Reader.consumeChar(char: Char): Boolean {
        val read = peekChar()
        if (read == char) {
            popChar()
            return true
        }
        return false
    }

    fun Reader.trim() {
        var peek = peekChar()
        while (peek.isWhitespace()) {
            popChar()
            peek = peekChar()
        }
        trimComment()
    }

    fun Reader.trimComment() {
        if (peekChar() == '/') {
            val lastPop = popChar()
            if (peekChar() != '*') {
                pushChar(lastPop)
            } else {
                popChar()

                while (true) {
                    var pop = popChar()
                    if (pop == '*') {
                        pop = popChar()
                        if (pop == '/') {
                            break
                        }
                    }
                }
                trim()
            }
        }
    }

    fun Reader.expect(char: Char) {
        val peek = peekChar()
        if (peek != char) {
            throw IllegalStateException("Expected '$char' but found '$peek'")
        }
    }

    fun Reader.read(char: Char) {
        expect(char)
        popChar()
    }

    private fun readUtf8Header(reader: Reader) {

        if (reader.consume("@CHARSET")) {
            reader.trim()
            readString(reader)
            reader.consumeChar(';')
            reader.trim()
        }
    }

    private fun readTargets(reader: Reader): List<StyleTarget> {
        reader.trim()
        val list = mutableListOf<StyleTarget>()

        while (true) {
            val target = readTarget(reader) ?: break
            list.add(target)
        }

        return list
    }

    private fun readTarget(reader: Reader): StyleTarget? {
        reader.trim()
        val selector = readSelector(reader) ?: return null
        reader.trim()
        val rules = readRuleList(reader) ?: return null

        return StyleTarget(listOf(selector), rules)
    }

    private fun readRuleList(reader: Reader): List<StyleRule>? {
        if (reader.peekChar() != '{') return null
        reader.popChar()

        val list = mutableListOf<StyleRule>()

        while (true) {
            reader.trim()
            val nextRule = readRule(reader) ?: break
            list.add(nextRule)

            reader.consumeChar(';')
            reader.trim()
            if (reader.peekChar() == '}') break
        }

        reader.read('}')

        return list
    }

    private fun readRule(reader: Reader): StyleRule? {

        val key = readIdentifier(reader)
        reader.trim()
        reader.read(':')
        reader.trim()
        val value = readRuleValue(reader)
        reader.trim()

        return StyleRule(key, value)
    }

    private fun readRuleValue(reader: Reader): String {
        val peek = reader.peekChar()
        if (peek == '\n' || peek == ';' || peek == '\r') {
            val toString = when (peek) {
                '\n' -> "\\n"
                '\r' -> "\\r"
                else -> peek.toString()
            }
            throw IllegalStateException("Expected rule value, found $toString")
        }

        return buildString {
            append(reader.popChar())

            var newChar = reader.peekChar()
            while (newChar != ';' && newChar != '\n' && newChar != '\r') {
                append(reader.popChar())
                newChar = reader.peekChar()
            }
        }
    }

    private fun readIdentifier(reader: Reader): String {
        val char = reader.peekChar()
        if (!char.isLetter() && char != '-' && char != '_') {
            throw IllegalStateException("Expected identifier, found '$char'")
        }

        return buildString {
            append(reader.popChar())

            var newChar = reader.peekChar()
            while (newChar.isLetter() || newChar == '-' || newChar == '_') {
                append(reader.popChar())
                newChar = reader.peekChar()
            }
        }
    }

    private fun readSelector(reader: Reader): String? {
        val char = reader.peekChar()

        if (char.isLetter() || char == '.' || char == '#') {
            return buildString {
                append(reader.popChar())

                var newChar = reader.peekChar()
                while (newChar.isLetter() || newChar == '_' || newChar == '-') {
                    append(reader.popChar())
                    newChar = reader.peekChar()
                }
            }
        }
        return null
    }

    private fun readString(reader: Reader): String {

        return buildString {
            if (reader.peekChar() == '"') {
                reader.popChar()

                while (true) {
                    val lastChar = reader.popChar()

                    append(lastChar)
                    if (reader.popChar() == '"') {
                        break
                    }
                }
            }
        }
    }
}
