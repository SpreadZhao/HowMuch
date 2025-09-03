package com.spread.db.file

import com.spread.common.HowMuch
import com.spread.common.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import java.io.File

abstract class JsonRepository<T>(
    private val path: String,
    private val serializer: KSerializer<T>
) {

    companion object {
        private val repoScope by lazy {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }

    private val filesDir = HowMuch.application.filesDir

    abstract val defaultData: List<T>

    private val _dataFlow = MutableStateFlow<List<T>>(emptyList())
    val dataFlow: StateFlow<List<T>> get() = _dataFlow.asStateFlow()

    val data: List<T> get() = _dataFlow.value

    fun loadRepository() {
        repoScope.launch {
            val file = File(filesDir, path)
            if (!file.exists()) {
                val dd = defaultData
                file.createNewFile()
                file.writeText(json.encodeToString(dd))
                _dataFlow.value = dd
                return@launch
            }
            val text = file.readText()
            val initData: List<T> = try {
                json.decodeFromString(ListSerializer(serializer), text)
            } catch (e: SerializationException) {
                return@launch
            }
            _dataFlow.value = initData
        }
    }

    protected fun addNewItem(item: T) {
        repoScope.launch {
            val newList = _dataFlow.value.toMutableList().apply { add(item) }
            _dataFlow.value = newList
            flush(newList)
        }
    }

    protected fun flush(current: List<T> = _dataFlow.value) {
        val file = File(filesDir, path)
        if (file.exists()) {
            file.writeText(json.encodeToString(ListSerializer(serializer), current))
        }
    }
}