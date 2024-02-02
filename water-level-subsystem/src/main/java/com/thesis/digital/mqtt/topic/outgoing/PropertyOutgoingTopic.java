package com.thesis.digital.mqtt.topic.outgoing;

import com.thesis.digital.mqtt.topic.MqttQosLevel;
import it.wldt.core.state.DigitalTwinStateProperty;

import java.util.function.Function;

public class PropertyOutgoingTopic<T> extends DigitalTwinOutgoingTopic<DigitalTwinStateProperty<T>> {
    public PropertyOutgoingTopic(String topic, MqttQosLevel qosLevel, Function<T, String> propertyValueToString) {
        super(topic, qosLevel, dtStateProperty -> propertyValueToString.apply(dtStateProperty.getValue()));
    }
}
