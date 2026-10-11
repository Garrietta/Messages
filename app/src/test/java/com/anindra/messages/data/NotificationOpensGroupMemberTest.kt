package com.anindra.messages.data

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * A notification for a group-only member opens the thread that holds the message.
 *
 * A member who exists only inside a group has no conversation row addressed to
 * them: their text is stored in the group, by `receiveMessage`, which falls back
 * to `soleGroupBlocking`. The notification was resolving the address with the
 * strict lookup instead, got null, and the caller "fixed" that by creating a
 * conversation -- so tapping the notification opened a brand new empty thread
 * and the incoming message was invisible. Mark-as-read and delete-from-
 * notification did the same and silently no-opped.
 *
 * Pinned at the source level because the behaviour lives in which of the two
 * lookups each call site names.
 */
class NotificationOpensGroupMemberTest {
    private fun locate(path: String): String {
        var dir: File? = File(System.getProperty("user.dir")).absoluteFile
        while (dir != null) {
            val candidate = File(dir, path)
            if (candidate.isFile) return candidate.readText()
            dir = dir.parentFile
        }
        error("could not locate $path")
    }

    private fun src(path: String): String =
        locate("app/src/main/java/com/anindra/messages/$path")

    private val repo: String get() = src("data/Repository.kt")
    private val mainActivity: String get() = src("MainActivity.kt")
    private val smsSupport: String get() = src("sms/SmsSupport.kt")
    private val markRead: String get() = src("sms/MarkReadReceiver.kt")
    private val deleteMessage: String get() = src("sms/DeleteMessageReceiver.kt")

    @Test
    fun `the incoming resolver is the same pair receiveMessage stores through`() {
        // One definition of "where does an incoming text land". If this drifts
        // from receiveMessage the notification opens a thread the message is not
        // in, which is the bug.
        val incoming = repo.substringAfter("fun conversationIdForIncoming")
            .substringBefore("fun conversationIdForAddress(")
        assertTrue(
            "the incoming resolver must delegate to the storage resolver",
            Regex("""conversationForInboundBlocking\(address\)""").containsMatchIn(incoming)
        )
        assertTrue(
            "it must stay off the IO dispatcher like its sibling",
            Regex("""runOnIo""").containsMatchIn(incoming)
        )
    }

    @Test
    fun `the notification tap resolves an incoming address, not a strict one`() {
        assertTrue(
            "the tap must use conversationIdForIncoming or it opens an empty thread",
            Regex("""conversationIdForIncoming\(addr\)""").containsMatchIn(mainActivity)
        )
        assertFalse(
            "the strict lookup still drops a group-only member, so it must not be here",
            Regex("""conversationIdForAddress\(addr\)""").containsMatchIn(mainActivity)
        )
    }

    @Test
    fun `the notification is keyed to the thread that holds the message`() {
        assertTrue(
            "notifId must come from the incoming resolver or it keys off the wrong thread",
            Regex("""conversationIdForIncoming\(from\)""").containsMatchIn(smsSupport)
        )
    }

    @Test
    fun `mark as read and delete reach a group-only member`() {
        // Both resolve the conversation to act on it. On the strict lookup they
        // got null and returned without doing anything at all.
        assertTrue(
            "mark-as-read must use the incoming resolver",
            Regex("""conversationIdForIncoming\(address\)""").containsMatchIn(markRead)
        )
        assertTrue(
            "delete must use the incoming resolver",
            Regex("""conversationIdForIncoming\(address\)""").containsMatchIn(deleteMessage)
        )
    }

    @Test
    fun `provider sync imports into the group, not a fresh private thread`() {
        // The second half of the bug, and the reason the first fix alone was not
        // enough. On startup the app mirrors every inbound SMS into the system
        // provider and imports it back. That import called getOrCreate directly,
        // which skips the group fallback, so it minted a private thread for a
        // group-only member -- and that thread then won the lookup the
        // notification used, opening an empty one even with the resolver fixed.
        val sync = repo.substringAfter("addrsWithPending.forEach")
            .substringBefore("pendingMessages.chunked")
        assertTrue(
            "sync must resolve through the inbound path so a group-only member's " +
                "provider row lands in the group",
            Regex("""conversationForInboundBlocking\(addr\)""").containsMatchIn(sync)
        )
        assertTrue(
            "sync must still create a conversation when there is genuinely none",
            Regex("""getOrCreateConversationBlocking\(addr\)""").containsMatchIn(sync)
        )
    }

    @Test
    fun `an outgoing message still resolves to the private thread`() {
        // The deliberate exception. A sent row lives in the 1:1, not in a group
        // the address also happens to be a member of; loosening this would file
        // sent messages into the group.
        assertTrue(
            "writeSentToSystem must keep the strict lookup",
            Regex("""conversationIdForAddress\(address\)""").containsMatchIn(repo)
        )
        val sentToSystem = repo.substringAfter("fun writeSentToSystem")
            .substringBefore("\n    fun ")
        assertFalse(
            "the sent-box writer must never use the group-fallback resolver",
            Regex("""conversationIdForIncoming""").containsMatchIn(sentToSystem)
        )
    }
}