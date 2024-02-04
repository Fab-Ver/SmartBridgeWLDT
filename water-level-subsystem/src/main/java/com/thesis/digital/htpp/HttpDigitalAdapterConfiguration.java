package com.thesis.digital.htpp;

import com.thesis.digital.htpp.exception.HttpDigitalAdapterConfigurationException;


public class HttpDigitalAdapterConfiguration {
    
    private final String host;
    private final Integer port;
    
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



}
