package com.rspinoni.momclient.signal

import org.signal.libsignal.protocol.SessionBuilder
import org.signal.libsignal.protocol.SessionCipher
import org.signal.libsignal.protocol.SignalProtocolAddress
import org.signal.libsignal.protocol.UntrustedIdentityException
import org.signal.libsignal.protocol.message.CiphertextMessage
import org.signal.libsignal.protocol.message.PreKeySignalMessage
import org.signal.libsignal.protocol.state.PreKeyBundle
import org.signal.libsignal.protocol.state.SignalProtocolStore
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.InvalidKeyException


class Session(
    val self: SignalProtocolStore,
    val otherKeyBundle: PreKeyBundle,
    val otherAddress: SignalProtocolAddress,
) {
    private enum class Operation {
        ENCRYPT, DECRYPT
    }

    private var lastOp: Operation? = null
    private var cipher: SessionCipher? = null

    @Synchronized
    private fun getCipher(operation: Operation): SessionCipher? {
        if (operation == lastOp) {
            return cipher
        }
        val toAddress: SignalProtocolAddress = otherAddress
        val builder: SessionBuilder = SessionBuilder(self, toAddress)
        try {
            builder.process(otherKeyBundle)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: UntrustedIdentityException) {
            throw RuntimeException(e)
        }
        this.cipher = SessionCipher(self, toAddress)
        this.lastOp = operation
        return cipher
    }

    fun encrypt(message: String): PreKeySignalMessage {
        val cipher: SessionCipher? = getCipher(Operation.ENCRYPT)
        try {
            val ciphertext: CiphertextMessage = cipher!!.encrypt(message.toByteArray(UTF8))
            val rawCiphertext: ByteArray = ciphertext.serialize()
            return PreKeySignalMessage(rawCiphertext)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun decrypt(ciphertext: PreKeySignalMessage?): String {
        val cipher: SessionCipher? = getCipher(Operation.DECRYPT)
        try {
            val decrypted: ByteArray = cipher!!.decrypt(ciphertext)
            return String(decrypted, UTF8)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    companion object {
        private val UTF8: Charset = StandardCharsets.UTF_8
    }
}