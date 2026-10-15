package com.anindra.messages.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class MessageCategory(
    val id: String,
    val name: String,
    val conversationIds: Set<Long> = emptySet()
)

class CategoryStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("message_categories", Context.MODE_PRIVATE)

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<List<MessageCategory>> = _categories.asStateFlow()

    private val _revision = MutableStateFlow(0)
    val revision: StateFlow<Int> = _revision.asStateFlow()

    companion object {
        const val KEY_CATEGORIES = "categories_json"
        const val ARCHIVE_ID = "__archive__"
    }

    private fun loadCategories(): List<MessageCategory> {
        val json = prefs.getString(KEY_CATEGORIES, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                val ids = o.optJSONArray("ids")
                MessageCategory(
                    id = o.getString("id"),
                    name = o.getString("name"),
                    conversationIds = if (ids != null)
                        (0 until ids.length()).mapTo(mutableSetOf()) { ids.getLong(it) }
                    else emptySet()
                )
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun save() {
        val arr = JSONArray()
        _categories.value.forEach { c ->
            arr.put(JSONObject()
                .put("id", c.id)
                .put("name", c.name)
                .put("ids", JSONArray(c.conversationIds.toList())))
        }
        prefs.edit().putString(KEY_CATEGORIES, arr.toString()).apply()
        _revision.value += 1
    }

    fun addCategory(name: String): MessageCategory {
        val c = MessageCategory(id = "cat_${System.currentTimeMillis()}", name = name)
        _categories.value = _categories.value + c
        save()
        return c
    }

    fun renameCategory(id: String, name: String) {
        _categories.value = _categories.value.map { if (it.id == id) it.copy(name = name) else it }
        save()
    }

    fun deleteCategory(id: String) {
        _categories.value = _categories.value.filterNot { it.id == id }
        save()
    }

    fun moveCategory(id: String, delta: Int) {
        val list = _categories.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        val to = (idx + delta).coerceIn(0, list.lastIndex)
        if (idx >= 0 && to != idx) {
            val c = list.removeAt(idx)
            list.add(to, c)
            _categories.value = list
            save()
        }
    }

    fun setConversationCategory(conversationId: Long, categoryId: String?) {
        _categories.value = _categories.value.map { c ->
            val ids = c.conversationIds.toMutableSet()
            if (c.id == categoryId) ids.add(conversationId) else ids.remove(conversationId)
            c.copy(conversationIds = ids)
        }
        save()
    }

    fun categoryFor(conversationId: Long): String? =
        _categories.value.firstOrNull { conversationId in it.conversationIds }?.id
}
