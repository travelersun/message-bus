package com.messagebus.interactor.pubsub;

import java.util.Map;

public interface IPubSubListener {
	void onChange(String var1, byte[] var2, Map<String, Object> var3);
}
