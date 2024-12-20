package com.example.iotalarmappandroid.mqtt

import android.content.Context
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

const val MQTT_BROKER = "ssl://ecf22a1dfa034798b1141f4961c7a591.s1.eu.hivemq.cloud:8883"
const val MQTT_USERNAME = "giang1601"
const val MQTT_PASSWORD = "Gb@298957"
const val MQTT_CLIENT_ID = "IoTAlarmAppAndroid"

class MqttClientHandler(context: Context) {
    private val client = MqttAndroidClient(context, MQTT_BROKER, MQTT_CLIENT_ID)

    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        val options = MqttConnectOptions().apply {
            userName = MQTT_USERNAME
            password = MQTT_PASSWORD.toCharArray()
            isAutomaticReconnect = true
        }

        client.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                onConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                onError(exception ?: Exception("Unknown error"))
            }
        })
    }

    fun subscribe(topic: String, callback: (String) -> Unit) {
        client.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                callback(message.toString())
            }

            override fun connectionLost(cause: Throwable?) {}
            override fun deliveryComplete(token: IMqttDeliveryToken?) {}
        })
        client.subscribe(topic, 1)
    }

    fun publish(topic: String, payload: String) {
        val message = MqttMessage(payload.toByteArray())
        client.publish(topic, message)
    }
}
