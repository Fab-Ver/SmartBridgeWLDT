package com.thesis.digital.htpp;

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
}
