package com.spread.db.file

import com.spread.common.HowMuch
import com.spread.common.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import java.io.File

// TODO: flow support
abstract class JsonRepository<T>(
    private val path: String,
    private val serializer: KSerializer<T>
) {

    companion object {
        private val repoScope by lazy {
            CoroutineScope(Dispatchers.IO)
        }
    }

    private val filesDir = HowMuch.application.filesDir

    abstract val defaultData: List<T>

    private val _data: MutableList<T> = mutableListOf()

    val data: List<T> get() = _data

    fun loadRepository() {
        repoScope.launch {
            val file = File(filesDir, path)
            if (!file.exists()) {
                val dd = defaultData
                file.createNewFile()
                file.writeText(json.encodeToString(dd))
                _data.addAll(dd)
                return@launch
            }
            val text = file.readText()
            val initData: List<T> = try {
                json.decodeFromString(ListSerializer(serializer), text)
            } catch (e: SerializationException) {
                return@launch
            }
            _data.addAll(initData)
        }
    }

    protected fun addNewItem(item: T) {
        repoScope.launch {
            _data.add(item)
            flush()
        }
    }

    protected fun flush() {
        val file = File(filesDir, path)
        if (file.exists()) {
            file.writeText(json.encodeToString(ListSerializer(serializer), data))
        }
    }

}