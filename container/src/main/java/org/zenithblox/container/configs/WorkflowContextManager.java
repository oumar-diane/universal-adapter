package org.zenithblox.container.configs;

import lombok.extern.slf4j.Slf4j;
import org.zenithblox.Exchange;
import org.zenithblox.ExchangeCallbackHolder;
import org.zenithblox.ZwangineContext;
import org.zenithblox.impl.DefaultZwangineContext;
import org.zenithblox.util.KeyValuePair;

@Slf4j
public class WorkflowContextManager {

    private static ZwangineContext zwangineContext;

    private WorkflowContextManager() {}

    public static ZwangineContext getContext() throws Exception {
        if(zwangineContext == null) {
            zwangineContext= new DefaultZwangineContext();
            zwangineContext.setTracing(true);
            zwangineContext.supplyCallback(new DefaultExchangeCallbackHolder());
            zwangineContext.start();
        }
        return zwangineContext;
    }

    private static class DefaultExchangeCallbackHolder implements ExchangeCallbackHolder{

        @Override
        public void supplyExchange(KeyValuePair<String, Exchange> exchangeEntry) {
            System.out.println("Supplying exchange " + exchangeEntry.getLeft());
        }
    }
}
