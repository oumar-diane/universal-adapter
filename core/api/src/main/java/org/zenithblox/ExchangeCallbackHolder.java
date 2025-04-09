package org.zenithblox;

import org.zenithblox.util.KeyValuePair;

/**
 * the callback that will be called when an {@link Exchange} comes in, the tracer should implement this interface to be able to plug into zwangine container
 * **/
public interface ExchangeCallbackHolder {
    void supplyExchange(KeyValuePair<String , Exchange> exchangeEntry);
}
