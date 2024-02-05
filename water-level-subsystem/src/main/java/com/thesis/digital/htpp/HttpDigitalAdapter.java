package com.thesis.digital.htpp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.error.SimpleErrorPageHandler;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateEvent;
import it.wldt.core.state.DigitalTwinStateEventNotification;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;
import it.wldt.core.state.IDigitalTwinState;
import it.wldt.exception.EventBusException;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDigitalAdapter extends DigitalAdapter<HttpDigitalAdapterConfiguration>{

    private final static Logger logger = LoggerFactory.getLogger(HttpDigitalAdapter.class);

    /**
     * The reference to the Undertow server used by the Adapter
     */
    private Undertow server;

    private Map<ServerSentEventHandler, List<String>> sseHandlers = new HashMap<>();

    public HttpDigitalAdapter(String id, HttpDigitalAdapterConfiguration configuration) {
        super(id, configuration);
    }

    @Override
    protected void onStateChangePropertyCreated(DigitalTwinStateProperty digitalTwinStateProperty) {
        
    }

    @Override
    protected void onStateChangePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        JSONObject obj = new JSONObject();
        obj.put("platform", "wldt");
        obj.put(digitalTwinStateProperty.getKey(),digitalTwinStateProperty.getValue());

        sseHandlers.forEach((handler, keys) -> {
            if(keys.contains(digitalTwinStateProperty.getKey())){
                handler.getConnections().forEach(connection -> {
                    connection.send(obj.toString());
                });
            }
        });
    }

    @Override
    protected void onStateChangePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {

    }

    @Override
    protected void onStatePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        logger.info("HTTP Digital Adapter({}) received property update", this.getId());
    }

    @Override
    protected void onStatePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {

    }

    @Override
    protected void onStateChangeActionEnabled(DigitalTwinStateAction digitalTwinStateAction) {

    }

    @Override
    protected void onStateChangeActionUpdated(DigitalTwinStateAction digitalTwinStateAction) {
       
    }

    @Override
    protected void onStateChangeActionDisabled(DigitalTwinStateAction digitalTwinStateAction) {
        
    }

    @Override
    protected void onStateChangeEventRegistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        
    }

    @Override
    protected void onStateChangeEventRegistrationUpdated(DigitalTwinStateEvent digitalTwinStateEvent) {
        
    }

    @Override
    protected void onStateChangeEventUnregistered(DigitalTwinStateEvent digitalTwinStateEvent) {

    }

    @Override
    protected void onDigitalTwinStateEventNotificationReceived(DigitalTwinStateEventNotification digitalTwinStateEventNotification) {

    }

    @Override
    protected void onStateChangeRelationshipCreated(DigitalTwinStateRelationship digitalTwinStateRelationship) {
    
    }

    @Override
    protected void onStateChangeRelationshipInstanceCreated(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        
    }

    @Override
    protected void onStateChangeRelationshipDeleted(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        
    }

    @Override
    protected void onStateChangeRelationshipInstanceDeleted(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {

    }

    @Override
    public void onAdapterStart() {
        this.server = Undertow.builder()
            .addHttpListener(getConfiguration().getPort(), getConfiguration().getHost())
            .setHandler(getHandler())
            .build();
        server.start();
        logger.info("HTTP Digital Adapter Started");
        this.notifyDigitalAdapterBound();
    }

    @Override
    public void onAdapterStop() {
        this.server.stop();
    }

    @Override
    public void onDigitalTwinSync(IDigitalTwinState digitalTwinState) {
        try {
          //Retrieve the list of available events and observe all variations
          digitalTwinState.getEventList()
                  .map(eventList -> eventList.stream()
                          .map(DigitalTwinStateEvent::getKey)
                          .collect(Collectors.toList()))
                  .ifPresent(eventKeys -> {
                      try {
                          observeDigitalTwinEventsNotifications(eventKeys);
                      } catch (EventBusException e) {
                          e.printStackTrace();
                      }
                  });

        } catch (Exception e) {
          e.printStackTrace();
        }
    }

    @Override
    public void onDigitalTwinUnSync(IDigitalTwinState digitalTwinState) {

    }

    @Override
    public void onDigitalTwinCreate() {
    }

    @Override
    public void onDigitalTwinStart() {
    }

    @Override
    public void onDigitalTwinStop() {

    }

    @Override
    public void onDigitalTwinDestroy() {

    }

    private HttpHandler getHandler(){
        RoutingHandler routingHandler = new RoutingHandler();
        routingHandler.setFallbackHandler(new SimpleErrorPageHandler());
        routingHandler.setInvalidMethodHandler(new SimpleErrorPageHandler());

        /**
         * Add actions handlers
         */
        getConfiguration().getActionRoutes().forEach((key,route) -> {
            routingHandler.add(Methods.POST, route + "/" + key, exchange -> {
                exchange.getRequestReceiver().receiveFullBytes((e, requestBody) -> {
                    try {
                        publishDigitalActionWldtEvent(key, new String(requestBody));
                        exchange.setStatusCode(200);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Action Request received successfully");
                    } catch (EventBusException e1) {
                        e1.printStackTrace();
                    }
                    
                });
            });
        });

        /**
         * Add properties SSE handlers
         */
        getConfiguration().getPropertiesSse().forEach((route, keys) -> {
            ServerSentEventHandler sseHandler = new ServerSentEventHandler();
            routingHandler.add(Methods.GET, route, sseHandler);
            sseHandlers.put(sseHandler, keys);
        });
        return routingHandler;
    }
    
}
