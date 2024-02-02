package com.thesis.digital.mqtt.topic.outgoing;

import java.util.function.Function;

public interface MqttPublishDigitalFunction<T> extends Function<T, String> {
}
