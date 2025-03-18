package com.rspinoni.momclient.di

const val BUNDLE_NAME_KEY: String = "name_"
const val BUNDLE_NUMBER_KEY: String = "number_"
//to find the localhost IP use `ipconfig` and check the IPv4 of the connection you have
const val DOMAIN: String = "192.168.128.206:8080"
const val HTTP_SERVER_URL: String = "http://$DOMAIN"
const val WEBSOCKET_URL: String = "ws://$DOMAIN/websocket"
const val SUBSCRIPTION_PATH: String = "/topic/notifications"

val DI: ApplicationComponent = DaggerApplicationComponent.create()