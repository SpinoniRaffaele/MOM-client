package com.rspinoni.momclient.model

class UserWithPreKey(val preKey: String, deviceId: String, phoneNumber: String):
    User(deviceId, phoneNumber) {}