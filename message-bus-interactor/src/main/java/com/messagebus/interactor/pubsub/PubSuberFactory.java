package com.messagebus.interactor.pubsub;


import java.util.ServiceLoader;
import com.messagebus.interactor.pubsub.impl.zookeeper.LongLiveZookeeper;
import com.messagebus.interactor.pubsub.impl.zookeeper.ZookeeperDataConverter;

public class PubSuberFactory {
	public static IPubSuber createPubSuber() {
		//ServiceLoader<IPubSuber> pubSuberServiceLoader = ServiceLoader.load(IPubSuber.class);
		//return (IPubSuber) pubSuberServiceLoader.iterator().next();
		return new LongLiveZookeeper();
	}

	public static IDataConverter createConverter() {
		//ServiceLoader<IDataConverter> converterServiceLoader = ServiceLoader.load(IDataConverter.class);
		//return (IDataConverter) converterServiceLoader.iterator().next();
		return new ZookeeperDataConverter();
	}
}
