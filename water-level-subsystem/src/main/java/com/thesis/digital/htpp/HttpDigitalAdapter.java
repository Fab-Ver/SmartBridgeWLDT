package com.thesis.digital.htpp;

import java.util.stream.Collectors;

import io.undertow.Undertow;
import io.undertow.util.Headers;
import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateEvent;
import it.wldt.core.state.DigitalTwinStateEventNotification;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;
import it.wldt.core.state.IDigitalTwinState;
import it.wldt.exception.EventBusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDigitalAdapter extends DigitalAdapter<HttpDigitalAdapterConfiguration>{

    private final static Logger logger = LoggerFactory.getLogger(HttpDigitalAdapter.class);

    /**
     * The reference to the Undertow server used by the Adapter
     */
    private Undertow server;

    public HttpDigitalAdapter(String id, HttpDigitalAdapterConfiguration configuration) {
        super(id, configuration);
    }

    @Override
    protected void onStateChangePropertyCreated(DigitalTwinStateProperty digitalTwinStateProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangePropertyCreated'");
    }

    @Override
    protected void onStateChangePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangePropertyUpdated'");
    }

    @Override
    protected void onStateChangePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangePropertyDeleted'");
    }

    @Override
    protected void onStatePropertyUpdated(DigitalTwinStateProperty digitalTwinStateProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStatePropertyUpdated'");
    }

    @Override
    protected void onStatePropertyDeleted(DigitalTwinStateProperty digitalTwinStateProperty) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStatePropertyDeleted'");
    }

    @Override
    protected void onStateChangeActionEnabled(DigitalTwinStateAction digitalTwinStateAction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeActionEnabled'");
    }

    @Override
    protected void onStateChangeActionUpdated(DigitalTwinStateAction digitalTwinStateAction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeActionUpdated'");
    }

    @Override
    protected void onStateChangeActionDisabled(DigitalTwinStateAction digitalTwinStateAction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeActionDisabled'");
    }

    @Override
    protected void onStateChangeEventRegistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeEventRegistered'");
    }

    @Override
    protected void onStateChangeEventRegistrationUpdated(DigitalTwinStateEvent digitalTwinStateEvent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeEventRegistrationUpdated'");
    }

    @Override
    protected void onStateChangeEventUnregistered(DigitalTwinStateEvent digitalTwinStateEvent) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeEventUnregistered'");
    }

    @Override
    protected void onDigitalTwinStateEventNotificationReceived(
            DigitalTwinStateEventNotification digitalTwinStateEventNotification) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onDigitalTwinStateEventNotificationReceived'");
    }

    @Override
    protected void onStateChangeRelationshipCreated(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeRelationshipCreated'");
    }

    @Override
    protected void onStateChangeRelationshipInstanceCreated(DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeRelationshipInstanceCreated'");
    }

    @Override
    protected void onStateChangeRelationshipDeleted(DigitalTwinStateRelationship digitalTwinStateRelationship) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeRelationshipDeleted'");
    }

    @Override
    protected void onStateChangeRelationshipInstanceDeleted(
            DigitalTwinStateRelationshipInstance digitalTwinStateRelationshipInstance) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onStateChangeRelationshipInstanceDeleted'");
    }

    @Override
    public void onAdapterStart() {
        this.server = Undertow.builder()
            .addHttpListener(getConfiguration().getPort(), getConfiguration().getHost())
            .setHandler(exchange -> {exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain"); exchange.getResponseSender().send("Hello Baeldung");})
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
    
}
