package com.rspinoni.momclient.signal

import org.signal.libsignal.protocol.IdentityKeyPair
import org.signal.libsignal.protocol.SignalProtocolAddress
import org.signal.libsignal.protocol.ecc.Curve
import org.signal.libsignal.protocol.ecc.ECPublicKey
import org.signal.libsignal.protocol.state.PreKeyBundle
import org.signal.libsignal.protocol.state.PreKeyRecord
import org.signal.libsignal.protocol.state.SignalProtocolStore
import org.signal.libsignal.protocol.state.SignedPreKeyRecord
import org.signal.libsignal.protocol.state.impl.InMemorySignalProtocolStore
import org.signal.libsignal.protocol.util.KeyHelper


class Entity(preKeyId: Int, signedPreKeyId: Int, address: String?) {
    val store: SignalProtocolStore
    val preKey: PreKeyBundle
    val address: SignalProtocolAddress = SignalProtocolAddress(address, 1)
    private var deviceId = 1

    init {
        val identityKeyPair = IdentityKeyPair.generate()
        this.store = InMemorySignalProtocolStore(
            identityKeyPair, KeyHelper.generateRegistrationId(false)
        )
        val registrationId = store.getLocalRegistrationId()

        val preKeyPair = Curve.generateKeyPair()
        val signedPreKeyPair = Curve.generateKeyPair()
        val timestamp = System.currentTimeMillis()

        val signedPreKeySignature = Curve.calculateSignature(
            identityKeyPair.privateKey,
            signedPreKeyPair.publicKey.serialize()
        )

        val identityKey = identityKeyPair.publicKey
        val preKeyPublic: ECPublicKey = preKeyPair.publicKey
        val signedPreKeyPublic: ECPublicKey = signedPreKeyPair.publicKey

        this.preKey = PreKeyBundle(
            registrationId,
            deviceId,
            preKeyId,
            preKeyPublic,
            signedPreKeyId,
            signedPreKeyPublic,
            signedPreKeySignature,
            identityKey
        )

        val preKeyRecord = PreKeyRecord(preKey.preKeyId, preKeyPair)
        val signedPreKeyRecord = SignedPreKeyRecord(
            signedPreKeyId, timestamp, signedPreKeyPair, signedPreKeySignature
        )

        store.storePreKey(preKeyId, preKeyRecord)
        store.storeSignedPreKey(signedPreKeyId, signedPreKeyRecord)
    }

    fun setDeviceId(deviceId: Int) {
        this.deviceId = deviceId
    }
}