package com.thesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.thesis.digital.DemoDigitalAdapter;

import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapter;
import it.wldt.adapter.mqtt.physical.MqttPhysicalAdapterConfiguration;
import it.wldt.adapter.mqtt.physical.exception.MqttPhysicalAdapterConfigurationException;
import it.wldt.adapter.mqtt.physical.topic.incoming.DigitalTwinIncomingTopic;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.core.engine.WldtEngine;
import it.wldt.core.event.WldtEvent;
import it.wldt.exception.EventBusException;

public class SmartLightSubsystem {
    public static void main(String[] args) {
        try{

            WldtEngine digitalTwinEngine = new WldtEngine(new SmartLightShadowingFunction("sls-shadowing-function"), "smart-light-subsystem");

            digitalTwinEngine.addPhysicalAdapter(getMqttEspPhysicalAdapter());
            digitalTwinEngine.addDigitalAdapter(new DemoDigitalAdapter("test-digital-adapter"));

            digitalTwinEngine.startLifeCycle();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private static MqttPhysicalAdapter getMqttEspPhysicalAdapter() throws MqttException, MqttPhysicalAdapterConfigurationException{
        MqttPhysicalAdapterConfiguration configuration = MqttPhysicalAdapterConfiguration
			.builder("test.mosquitto.org", 1883,"smart-light-subsystem-dt")
            .addPhysicalAssetPropertyAndTopic("dark", false, "subsystems/org.eclipse.ditto:smart-light-subsystem/dark", dark -> {
                JSONObject obj = new JSONObject(dark);
                return Boolean.parseBoolean(obj.get("dark").toString());
            })
            .addPhysicalAssetPropertyAndTopic("detected", false, "subsystems/org.eclipse.ditto:smart-light-subsystem/detected", detected -> {
                JSONObject obj = new JSONObject(detected);
                return Boolean.parseBoolean(obj.get("detected").toString());
            })
            .addIncomingTopic(new DigitalTwinIncomingTopic("subsystems/org.eclipse.ditto:smart-light-subsystem/light", msg -> {
                JSONObject obj = new JSONObject(msg);
                List<WldtEvent<?>> list = new ArrayList<>();
                try {
                    	list.add(new PhysicalAssetPropertyWldtEvent<>("smart-light-on",Boolean.parseBoolean(obj.get("on").toString())));
                        list.add(new PhysicalAssetPropertyWldtEvent<>("smart-light-waiting",Boolean.parseBoolean(obj.get("waiting").toString())));
                        return list;
                } catch (EventBusException e) {
                    e.printStackTrace();
                }
                return null; 
            }), List.of(new PhysicalAssetProperty<Boolean>("smart-light-on", false), new PhysicalAssetProperty<Boolean>("smart-light-waiting", false)), Collections.emptyList())
            .addPhysicalAssetPropertyAndTopic("status", "SYS_ON", "subsystems/org.eclipse.ditto:water-level-subsystem/alarm-event", alarm -> {
                JSONObject obj = new JSONObject(alarm);
                Boolean wls_status = Boolean.parseBoolean(obj.get("alarm").toString());
                return wls_status ? "SYS_OFF" : "SYS_ON";
            })
            .build();
        return new MqttPhysicalAdapter("smart-light-subsystem-mqtt-esp",configuration);
    }
}