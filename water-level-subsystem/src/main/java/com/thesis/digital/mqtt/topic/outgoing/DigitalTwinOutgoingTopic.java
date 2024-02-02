package com.thesis.digital.mqtt.topic.outgoing;

import com.thesis.digital.mqtt.topic.MqttQosLevel;
import com.thesis.digital.mqtt.topic.MqttTopic;
import it.wldt.core.state.DigitalTwinStateEventNotification;
import it.wldt.core.state.DigitalTwinStateProperty;

public class DigitalTwinOutgoingTopic<T> extends MqttTopic {
    private final MqttPublishDigitalFunction<T> publishDigitalFunction;

    public DigitalTwinOutgoingTopic(String topic, MqttQosLevel qosLevel, MqttPublishDigitalFunction<T> publishDigitalFunction) {
        super(topic, qosLevel);
        this.publishDigitalFunction = publishDigitalFunction;
    }

    public String applyPublishFunction(DigitalTwinStateProperty<?> digitalTwinStateComponent){
        return publishDigitalFunction.apply((T) digitalTwinStateComponent);
    }

    public String applyPublishFunction(DigitalTwinStateEventNotification<?> digitalTwinStateComponent){
        return publishDigitalFunction.apply((T) digitalTwinStateComponent);
    }
}
