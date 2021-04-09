package com.messagebus.client.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * message carry type
 */
public enum MessageCarryType {

    PRODUCE,
    CONSUME,
    REQUEST,
    RESPONSE,
    PUBLISH,
    SUBSCRIBE,
    BROADCAST,
    RPCREQUEST,
    RPCRESPONSE,
    PRODUCE_CONSUME,
    REQUEST_REQUEST,
    PUBLISH_SUBSCRIBE;

    private static final ConcurrentMap<String, MessageCarryType> lookups =
            new ConcurrentHashMap<String, MessageCarryType>(9);

    static {
        lookups.put("produce", PRODUCE);
        lookups.put("consume", CONSUME);
        lookups.put("request", REQUEST);
        lookups.put("response", RESPONSE);
        lookups.put("publish", PUBLISH);
        lookups.put("subscribe", SUBSCRIBE);
        lookups.put("broadcast", BROADCAST);
        lookups.put("rpcrequest", RPCREQUEST);
        lookups.put("rpcresponse", RPCRESPONSE);
        lookups.put("produce-consume", PRODUCE_CONSUME);
        lookups.put("request-response", REQUEST_REQUEST);
        lookups.put("publish-subscribe", PUBLISH_SUBSCRIBE);
    }


    public static MessageCarryType lookup(String strType) {
        MessageCarryType result = lookups.get(strType);

        if (result == null)
            throw new UnknownError("can not find the enum item of MessageCarryType which " +
                    " maped the key : " + strType);

        return result;
    }


    public String stringOf() {
        for (Map.Entry<String, MessageCarryType> entry : lookups.entrySet()) {
            if (entry.getValue().equals(this))
                return entry.getKey();
        }

        return "";
    }

}
