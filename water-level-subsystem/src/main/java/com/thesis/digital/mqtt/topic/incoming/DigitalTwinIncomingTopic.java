package com.thesis.digital.mqtt.topic.incoming;

import com.thesis.digital.mqtt.topic.MqttTopic;
import it.wldt.adapter.digital.event.DigitalActionWldtEvent;

public class DigitalTwinIncomingTopic extends MqttTopic {

    private final MqttSubscribeDigitalFunction subscribeDigitalFunction;

    public DigitalTwinIncomingTopic(String topic, MqttSubscribeDigitalFunction subscribeDigitalFunction) {
        super(topic);
        this.subscribeDigitalFunction = subscribeDigitalFunction;
    }

    public DigitalActionWldtEvent<?> applySubscribeFunction(String messagePayload) {
        return this.subscribeDigitalFunction.apply(messagePayload);
    }
}
