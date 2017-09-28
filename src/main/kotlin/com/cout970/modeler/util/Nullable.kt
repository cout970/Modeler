package com.cout970.modeler.util

import org.funktionale.option.Option

/**
 * Created by cout970 on 2017/09/21.
 */

sealed class Nullable<T> {

    data class NonNull<T>(val value: T) : Nullable<T>()
    object Null : Nullable<Nothing>()

    inline fun <R> map(func: (T) -> R): Nullable<R> = when (this) {
        is Null -> castNull()
        is NonNull -> Nullable.NonNull(func(value))
    }

    inline fun <R> flatMap(func: (T) -> R?): Nullable<R> = when (this) {
        is Null -> castNull()
        is NonNull -> {
            val result = func(value)
            if (result == null) castNull() else Nullable.NonNull(result)
        }
    }

    inline fun <R> flatMapNullable(func: (T) -> Nullable<R>): Nullable<R> = when (this) {
        is Null -> castNull()
        is NonNull -> func(value)
    }

    inline fun getOrCompute(default: () -> T): T = when (this) {
        is Null -> default()
        is NonNull -> value
    }

    fun getOr(default: T): T = when (this) {
        is Null -> default
        is NonNull -> value
    }

    fun getOrNull(): T? = when (this) {
        is Null -> null
        is NonNull -> value
    }

    fun getNonNull(): T = when (this) {
        is Null -> throw NullPointerException("Value is Null!")
        is NonNull -> value
    }

    fun or(other: Nullable<T>): Nullable<T> = when (this) {
        is Null -> other
        is NonNull -> this
    }

    inline fun ifNotNull(func: (T) -> Unit) {
        if (this is NonNull) {
            func(this.value)
        }
    }

    inline fun ifNull(func: () -> Unit) {
        if (this is Null) {
            func()
        }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> castNull(): Nullable<T> = Null as Nullable<T>
    }
}

fun <T> T?.asNullable(): Nullable<T> = when (this) {
    null -> Nullable.castNull()
    else -> Nullable.NonNull(this)
}

fun <T> Option<T>.toNullable(): Nullable<T> = when (this) {
    is Option.None -> Nullable.castNull()
    is Option.Some -> Nullable.NonNull(get())
}

fun <T> Nullable<T>.toNullable(): Nullable<T> = this