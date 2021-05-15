package ru.skillbranch.skillarticles.ui.delegates

import ru.skillbranch.skillarticles.data.PrefManager
import ru.skillbranch.skillarticles.data.adapters.JsonAdapter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PrefObjDelegate<T>(private val adapter: JsonAdapter<T>, private val customKey: String? = null) :
    ReadWriteProperty<PrefManager, T?> {
    override fun getValue(thisRef: PrefManager, property: KProperty<*>): T? {
        TODO("Not yet implemented")
    }

    override fun setValue(thisRef: PrefManager, property: KProperty<*>, value: T?) {
        TODO("Not yet implemented")
    }
}