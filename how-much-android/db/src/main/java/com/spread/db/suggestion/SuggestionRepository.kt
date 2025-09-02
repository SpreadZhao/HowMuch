package com.spread.db.suggestion

import com.spread.db.file.JsonRepository

class SuggestionRepository :
    JsonRepository<SuggestionItem>(FILE_PATH, SuggestionItem.serializer()) {

    companion object {
        const val FILE_PATH = "suggestion.json"
    }

    override val defaultData: List<SuggestionItem>
        get() = emptyList()

    fun markSuggestionUsed(suggestion: String) {
        val target = data.find { it.text == suggestion }
        if (target == null) {
            val newSuggestion = SuggestionItem(suggestion)
            addNewItem(newSuggestion)
        } else {
            target.useCount++
            target.lastUseTime = System.currentTimeMillis()
            flush()
        }
    }

}