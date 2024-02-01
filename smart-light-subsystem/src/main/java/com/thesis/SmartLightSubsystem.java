package com.thesis;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONObject;

import com.thesis.digital.DemoDigitalAdapter;
import com.thesis.physical.mqtt.MqttPhysicalAdapter;
import com.thesis.physical.mqtt.MqttPhysicalAdapterConfiguration;
import com.thesis.physical.mqtt.exception.MqttPhysicalAdapterConfigurationException;

import it.wldt.core.engine.WldtEngine;

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
        MqttPhysicalAdapterConfiguration configuration = null;
        return new MqttPhysicalAdapter("smart-light-subsystem-mqtt-esp",configuration);
    }
}