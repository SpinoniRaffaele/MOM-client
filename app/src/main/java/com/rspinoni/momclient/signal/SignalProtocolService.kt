package com.rspinoni.momclient.signal

class SignalProtocolService {

    fun createEntity(address: String): Entity {
        //todo: how to generate ids
        return Entity(1, 2, address)
    }
}