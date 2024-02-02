package com.thesis.digital.mqtt.topic.outgoing;

import com.thesis.digital.mqtt.topic.MqttQosLevel;
import it.wldt.core.state.DigitalTwinStateAction;

public class ActionOutgoingTopic extends DigitalTwinOutgoingTopic<DigitalTwinStateAction> {
    public ActionOutgoingTopic(String topic, MqttQosLevel qosLevel, MqttPublishDigitalFunction<DigitalTwinStateAction> publishDigitalFunction) {
        super(topic, qosLevel, publishDigitalFunction);
    }
}
