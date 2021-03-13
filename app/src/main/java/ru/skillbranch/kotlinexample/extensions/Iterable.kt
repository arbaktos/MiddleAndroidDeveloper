package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val list = mutableListOf<T>()
    this.forEach {
        if(!predicate(it)) list.add(it)
        else return list
    }
    return list
}

