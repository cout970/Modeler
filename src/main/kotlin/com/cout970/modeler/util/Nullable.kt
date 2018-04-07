package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/09/21.
 */

sealed class Nullable<T> {

    data class NonNull<T>(val value: T) : Nullable<T>()
    object Null : Nullable<Nothing>()

    inline fun <R> map(func: (T) -> R): Nullable<R> = when (this) {
        Null -> castNull()
        is NonNull -> Nullable.NonNull(func(value))
    }

    inline fun mapNull(func: () -> T): Nullable<T> = when (this) {
        Null -> func().asNullable()
        is NonNull -> this
    }

    inline fun <R> flatMap(func: (T) -> R?): Nullable<R> = when (this) {
        Null -> castNull()
        is NonNull -> {
            val result = func(value)
            if (result == null) castNull() else Nullable.NonNull(result)
        }
    }

    inline fun <R> flatMapNullable(func: (T) -> Nullable<R>): Nullable<R> = when (this) {
        Null -> castNull()
        is NonNull -> func(value)
    }

    inline fun getOrCompute(default: () -> T): T = when (this) {
        Null -> default()
        is NonNull -> value
    }

    fun getOrNull(): T? = when (this) {
        Null -> null
        is NonNull -> value
    }

    fun getNonNull(): T = when (this) {
        Null -> throw NullPointerException("Value is Null!")
        is NonNull -> value
    }

    fun or(other: Nullable<T>): Nullable<T> = when (this) {
        Null -> other
        is NonNull -> this
    }

    inline fun ifNotNull(func: (T) -> Unit) {
        if (this is NonNull) {
            func(this.value)
        }
    }

    inline fun ifNull(func: () -> Unit) {
        if (this == Null) {
            func()
        }
    }

    fun <A, B> split(func: (T) -> Pair<A, B>): Pair<Nullable<A>, Nullable<B>> = when (this) {
        Null -> castNull<A>() to castNull<B>()
        is NonNull -> func(value).let { it.first.asNullable() to it.second.asNullable() }
    }

    @Suppress("UNCHECKED_CAST")
    fun <R> zip(other: Nullable<R>): Nullable<Pair<T, R>> {
        if (this is NonNull<*> && other is NonNull<*>) {
            return (this.value as T to other.value as R).asNullable()
        }
        return castNull()
    }

    inline fun eval(func: (T) -> Boolean): Boolean = when (this) {
        Null -> false
        is NonNull -> func(value)
    }

    inline fun filter(func: (T) -> Boolean): Nullable<T> = when (this) {
        Null -> castNull()
        is NonNull -> if (func(value)) this else castNull()
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified P> filterIsInstance(): Nullable<P> = when (this) {
        Null -> castNull()
        is NonNull -> if (value is P) this as Nullable<P> else castNull<P>()
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> castNull(): Nullable<T> = Null as Nullable<T>
    }
}

fun <B> Nullable<List<B>>.flatMapList(): List<B> = when (this) {
    is Nullable.NonNull -> value
    else -> emptyList()
}

fun <P, R : P, T : P> Nullable<T>.getOr(default: R): P = when (this) {
    Nullable.Null -> default
    is Nullable.NonNull -> value
}

fun <T> T?.asNullable(): Nullable<T> = when (this) {
    null -> Nullable.castNull()
    else -> Nullable.NonNull(this)
}

fun <T> Nullable<T>.toNullable(): Nullable<T> = this