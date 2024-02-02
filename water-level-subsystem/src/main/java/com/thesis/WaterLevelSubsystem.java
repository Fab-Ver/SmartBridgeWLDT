package com.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.thesis.digital.DemoDigitalAdapter;

import it.wldt.adapter.mqtt.digital.MqttDigitalAdapter;
import it.wldt.adapter.mqtt.digital.MqttDigitalAdapterConfiguration;
import it.wldt.adapter.mqtt.digital.exception.MqttDigitalAdapterConfigurationException;
import it.wldt.adapter.mqtt.digital.topic.MqttQosLevel;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.physical.PhysicalAssetEvent;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.engine.WldtEngine;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.EventBusException;

public class WaterLevelSubsystem {
    public static void main(String[] args) {
        try{

            WldtEngine digitalTwinEngine = new WldtEngine(new WaterLevelSubsystemShadowingFunction("wls-shadowing-function"), "water-level-subsystem");

            digitalTwinEngine.addPhysicalAdapter(getMqttEspPhysicalAdapter());
            digitalTwinEngine.addDigitalAdapter(new DemoDigitalAdapter("test-digital-adapter"));
            digitalTwinEngine.addDigitalAdapter(getMqttEventDigitalAdapter());

            digitalTwinEngine.startLifeCycle();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static MqttPhysicalAdapter getMqttEspPhysicalAdapter() throws MqttException, MqttPhysicalAdapterConfigurationException{
    	MqttPhysicalAdapterConfiguration configuration = MqttPhysicalAdapterConfiguration
				.builder("test.mosquitto.org",1883,"water-level-subsystem-dt")
                .addPhysicalAssetPropertyAndTopic("water-distance", 0.0f, "subsystems/org.eclipse.ditto:water-level-subsystem/distance", distance -> {
                    JSONObject obj = new JSONObject(distance);
                    return Float.parseFloat(obj.get("distance").toString());
                })
                .addPhysicalAssetPropertyAndTopic("valve-angle", 0, "subsystems/org.eclipse.ditto:water-level-subsystem/angle", angle -> {
                    JSONObject obj = new JSONObject(angle);
                    return Integer.parseInt(obj.get("angle").toString());
                })
                .addPhysicalAssetPropertyAndTopic("green-led", true, "subsystems/org.eclipse.ditto:water-level-subsystem/green", green -> {
                    JSONObject obj = new JSONObject(green);
                    return Boolean.parseBoolean(obj.get("on").toString());
                })
                .addPhysicalAssetPropertyAndTopic("manual", false, "subsystems/org.eclipse.ditto:water-level-subsystem/manual", manual -> {
                    JSONObject obj = new JSONObject(manual);
                    return Boolean.parseBoolean(obj.get("manual").toString());
                })
                .addIncomingTopic(new DigitalTwinIncomingTopic("subsystems/org.eclipse.ditto:water-level-subsystem/red", msg -> {
                    JSONObject obj = new JSONObject(msg);
                    List<WldtEvent<?>> list = new ArrayList<>();
                    try {
                        list.add(new PhysicalAssetPropertyWldtEvent<>("red-led-on",Boolean.parseBoolean(obj.get("on").toString())));
                        list.add(new PhysicalAssetPropertyWldtEvent<>("red-led-blinking",Boolean.parseBoolean(obj.get("blinking").toString())));
                        return list;
                    } catch (EventBusException e) {
                        e.printStackTrace();
                    }
                    return null; 
                }), List.of(new PhysicalAssetProperty<Boolean>("red-led-on", false), new PhysicalAssetProperty<Boolean>("red-led-blinking", false)), Collections.emptyList())
                .addIncomingTopic(new DigitalTwinIncomingTopic("subsystems/org.eclipse.ditto:water-level-subsystem/status", msg -> {
                    JSONObject obj = new JSONObject(msg);
                    String status = obj.get("status").toString();
                    List<WldtEvent<?>> list = new ArrayList<>();
                    try {
                        list.add(new PhysicalAssetPropertyWldtEvent<>("status",status));
                        list.add(new PhysicalAssetEventWldtEvent<>("alarm",status.equals("ALARM")));
                        return list;
                    } catch (EventBusException e) {
                        e.printStackTrace();
                    }
                    return null;
                    }), List.of(new PhysicalAssetProperty<String>("status", "NORMAL")), List.of(new PhysicalAssetEvent("alarm","boolean")))
                .build();
        return new MqttPhysicalAdapter("water-level-subsystem-mqtt-esp",configuration);
    }

    private static MqttDigitalAdapter getMqttEventDigitalAdapter() throws MqttException, MqttDigitalAdapterConfigurationException{
        MqttDigitalAdapterConfiguration configuration = MqttDigitalAdapterConfiguration
				.builder("test.mosquitto.org", 1883,"water-level-subsystem-dt")
                .addEventNotificationTopic("alarm", "subsystems/org.eclipse.ditto:water-level-subsystem/alarm-event", MqttQosLevel.MQTT_QOS_0, event -> {
                    JSONObject obj = new JSONObject();
                    obj.put("alarm", event);
                    return obj.toString();
                })
                .build();
        return new MqttDigitalAdapter("water-level-subsystem-mqtt-event", configuration);
    }
}