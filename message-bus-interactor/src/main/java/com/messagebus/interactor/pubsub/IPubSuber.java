package com.messagebus.interactor.pubsub;


public interface IPubSuber {
	void open();

	void close();

	boolean isAlive();

	void watch(String[] var1, IPubSubListener var2);

	void publish(String var1, byte[] var2);

	byte[] get(String var1);

	void set(String var1, byte[] var2);

	boolean exists(String var1);

	void setHost(String var1);

	String getHost();

	void setPort(int var1);

	int getPort();
}
