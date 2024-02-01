package com.thesis;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.thesis.digital.DemoDigitalAdapter;
import com.thesis.physical.mqtt.MqttPhysicalAdapter;
import com.thesis.physical.mqtt.MqttPhysicalAdapterConfiguration;
import com.thesis.physical.mqtt.exception.MqttPhysicalAdapterConfigurationException;

import it.wldt.core.engine.WldtEngine;

public class WaterLevelSubsystem {
    public static void main(String[] args) {
        try{

            WldtEngine digitalTwinEngine = new WldtEngine(new WaterLevelSubsystemShadowingFunction("test-shadowing-function"), "water-level-subsystem");

            digitalTwinEngine.addPhysicalAdapter(getWaterLevelSubsystemMqttEspPhysicalAdapter());
            digitalTwinEngine.addDigitalAdapter(new DemoDigitalAdapter("test-digital-adapter"));

            digitalTwinEngine.startLifeCycle();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static MqttPhysicalAdapter getWaterLevelSubsystemMqttEspPhysicalAdapter() throws MqttException, MqttPhysicalAdapterConfigurationException{
        MqttPhysicalAdapterConfiguration configuration = 
        MqttPhysicalAdapterConfiguration.builder("test.mosquitto.org",1883,"water-level-subsystem-dt")
                                        .addPhysicalAssetPropertyAndTopic("status", "NORMAL", "subsystems/org.eclipse.ditto:water-level-subsystem/status", status -> {
                                            JSONObject obj = new JSONObject(status);
                                            return obj.get("status").toString();
                                        })
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
                                        .build();
        
        return new MqttPhysicalAdapter("water-level-subsystem-mqtt-esp",configuration);
    }
}