package com.thesis.digital.mqtt.topic.outgoing;

import com.thesis.digital.mqtt.topic.MqttQosLevel;
import it.wldt.core.state.DigitalTwinStateEvent;

public class EventOutgoingTopic extends DigitalTwinOutgoingTopic<DigitalTwinStateEvent>{
    public EventOutgoingTopic(String topic, MqttQosLevel qosLevel, MqttPublishDigitalFunction<DigitalTwinStateEvent> publishDigitalFunction) {
        super(topic, qosLevel, publishDigitalFunction);
    }
}
