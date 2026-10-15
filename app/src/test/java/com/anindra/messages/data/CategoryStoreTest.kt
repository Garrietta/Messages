package com.anindra.messages.data

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class CategoryStoreTest {
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var store: CategoryStore
    private var storedJson: String? = null

    @Before
    fun setup() {
        prefs = mock()
        editor = mock()
        whenever(prefs.getString(eq(CategoryStore.KEY_CATEGORIES), isNull())).thenAnswer { storedJson }
        whenever(prefs.edit()).thenReturn(editor)
        whenever(editor.putString(any(), any())).thenAnswer { inv ->
            storedJson = inv.getArgument(1)
            editor
        }
        val context = mock<Context>()
        whenever(context.getSharedPreferences(eq("message_categories"), eq(Context.MODE_PRIVATE))).thenReturn(prefs)
        store = CategoryStore(context)
    }

    @Test
    fun `addCategory creates with unique id`() {
        val a = store.addCategory("Friends")
        val b = store.addCategory("Work")
        assertNotEquals(a.id, b.id)
        assertEquals(2, store.categories.value.size)
    }

    @Test
    fun `addCategory rejects blank names`() {
        org.junit.Assert.assertThrows(IllegalArgumentException::class.java) {
            store.addCategory("  ")
        }
    }

    @Test
    fun `renameCategory updates name`() {
        val c = store.addCategory("Old")
        store.renameCategory(c.id, "New")
        assertEquals("New", store.categories.value.first { it.id == c.id }.name)
    }

    @Test
    fun `deleteCategory removes it`() {
        val c = store.addCategory("Temp")
        store.deleteCategory(c.id)
        assertTrue(store.categories.value.none { it.id == c.id })
    }

    @Test
    fun `setConversationCategory assigns exclusively`() {
        val a = store.addCategory("A")
        val b = store.addCategory("B")
        store.setConversationCategory(42L, a.id)
        store.setConversationCategory(42L, b.id)
        assertEquals(b.id, store.categoryFor(42L))
        assertFalse(42L in store.categories.value.first { it.id == a.id }.conversationIds)
    }

    @Test
    fun `setConversationCategory null unassigns`() {
        val a = store.addCategory("A")
        store.setConversationCategory(42L, a.id)
        store.setConversationCategory(42L, null)
        assertNull(store.categoryFor(42L))
    }

    @Test
    fun `moveCategory reorders`() {
        val a = store.addCategory("A")
        val b = store.addCategory("B")
        val c = store.addCategory("C")
        store.moveCategory(a.id, 2)
        val ids = store.categories.value.map { it.id }
        assertEquals(listOf(b.id, c.id, a.id), ids)
    }

    @Test
    fun `moveCategory ignores unknown ids and empty categories`() {
        store.moveCategory("missing", 1)
        assertTrue(store.categories.value.isEmpty())
    }

    @Test
    fun `unknown category assignment does not remove existing assignment`() {
        val category = store.addCategory("Known")
        store.setConversationCategory(42L, category.id)
        store.setConversationCategory(42L, "missing")
        assertEquals(category.id, store.categoryFor(42L))
    }

    @Test
    fun `categories persist across instances`() {
        val c = store.addCategory("Persisted")
        store.setConversationCategory(7L, c.id)
        val context = mock<Context>()
        whenever(context.getSharedPreferences(eq("message_categories"), eq(Context.MODE_PRIVATE))).thenReturn(prefs)
        val store2 = CategoryStore(context)
        assertEquals(1, store2.categories.value.size)
        assertEquals("Persisted", store2.categories.value[0].name)
        assertEquals(c.id, store2.categoryFor(7L))
    }
}
