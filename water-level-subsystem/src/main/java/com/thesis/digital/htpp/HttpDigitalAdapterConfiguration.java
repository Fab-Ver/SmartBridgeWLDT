package com.thesis.digital.htpp;

import java.util.HashMap;
import java.util.Map;

import com.thesis.digital.htpp.exception.HttpDigitalAdapterConfigurationException;


public class HttpDigitalAdapterConfiguration {
    
    private final String host;
    private final int port;

    private final Map<String, String> actionRoutes = new HashMap<>();
    
    protected HttpDigitalAdapterConfiguration(int port, String host) {
        this.host = host;
        this.port = port;
    }

    public static HttpDigitalAdapterConfigurationBuilder builder(int port, String host) throws HttpDigitalAdapterConfigurationException{
        return new HttpDigitalAdapterConfigurationBuilder(port, host);
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    protected void addActionRoute(String actionKey, String actionRoute){
        this.actionRoutes.put(actionKey, actionRoute);
    }

    public Map<String,String> getActionRoutes() {
        return actionRoutes;
    }

}
