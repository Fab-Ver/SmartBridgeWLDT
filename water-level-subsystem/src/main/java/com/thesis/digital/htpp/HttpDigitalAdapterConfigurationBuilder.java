package com.thesis.digital.htpp;

import java.util.List;

import com.thesis.digital.htpp.exception.HttpDigitalAdapterConfigurationException;

public class HttpDigitalAdapterConfigurationBuilder {

    private final HttpDigitalAdapterConfiguration configuration; 

    public HttpDigitalAdapterConfigurationBuilder(int port, String host) throws HttpDigitalAdapterConfigurationException {
        if(isValid(port) || !isValid(host))
            throw new HttpDigitalAdapterConfigurationException("Host cannot be empty string or null and Port must be a positive number");
        this.configuration = new HttpDigitalAdapterConfiguration(port, host);
    }

    public HttpDigitalAdapterConfigurationBuilder addActionRoute(String actionKey, String actionRoute) throws HttpDigitalAdapterConfigurationException{
        checkActionRoute(actionKey, actionRoute);
        this.configuration.addActionRoute(actionKey, actionRoute);
        return this;
    }

    public HttpDigitalAdapterConfigurationBuilder addPropertySSERoute(List<String> propertiesKeys, String sseRoute) throws HttpDigitalAdapterConfigurationException{
        checkPropertyRoute(propertiesKeys, sseRoute);
        this.configuration.addPropertySseRoute(propertiesKeys, sseRoute);
        return this;
    }

    
    public HttpDigitalAdapterConfiguration build() {
        return this.configuration;
    }
    
    private boolean isValid(String param){
        return param != null && !param.isEmpty();
    }
    
    private boolean isValid(int param){
        return param <= 0;
    }
    
    private void checkActionRoute(String actionKey, String actionRoute) throws HttpDigitalAdapterConfigurationException{
        if(!isValid(actionKey) || !isValid(actionRoute))
            throw new HttpDigitalAdapterConfigurationException("Action Key or Action Route cannot be empty or null");
        if(configuration.getActionRoutes().containsKey(actionKey))
            throw new HttpDigitalAdapterConfigurationException("Action Key already registered");
    }

    private void checkPropertyRoute(List<String> propertiesKeys, String sseRoute) throws HttpDigitalAdapterConfigurationException {
        if(!isValid(sseRoute))
            throw new HttpDigitalAdapterConfigurationException("SSE Route cannot be empty or null");
        if(configuration.getPropertiesSse().containsKey(sseRoute))
            throw new HttpDigitalAdapterConfigurationException("SSE Route already registered");
        if(propertiesKeys.isEmpty() || propertiesKeys == null)
            throw new HttpDigitalAdapterConfigurationException("Properties Keys List cannot be empty or null");    
    }
}
