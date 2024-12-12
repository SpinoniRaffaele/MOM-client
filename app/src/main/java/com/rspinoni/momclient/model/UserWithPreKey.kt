package com.rspinoni.momclient.model

import org.signal.libsignal.protocol.state.PreKeyBundle

class UserWithPreKey(val deviceId: String, val phoneNumber: String, val preKey: String) {
}