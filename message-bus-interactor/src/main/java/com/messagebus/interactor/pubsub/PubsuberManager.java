package com.messagebus.interactor.pubsub;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PubsuberManager {
	private static final Log logger = LogFactory.getLog(PubsuberManager.class);
	private Map<String, ChannelListenerEntry> registry;
	private IPubSuber pubsuber = PubSuberFactory.createPubSuber();
	private IDataConverter pubsuberDataConverter;
	private boolean dataFetcherInited = false;

	public PubsuberManager(String pubsuberHost, int pubsuberPort) {
		this.pubsuber.setHost(pubsuberHost);
		this.pubsuber.setPort(pubsuberPort);
		this.pubsuber.open();
		this.pubsuberDataConverter = PubSuberFactory.createConverter();
		boolean isAlive = this.pubsuber.isAlive();
		if (isAlive) {
			this.registry = new ConcurrentHashMap();
			List<String> channels = new ArrayList();
			channels.add("/event/config");
			channels.add("/event/serverState");
			channels.add("/event/nodeView");
			channels.add("/event/notification");
			this.watchPubSuber(channels);
		}
	}

	public boolean isPubsuberAlive() {
		return this.pubsuber.isAlive();
	}
	
	public synchronized void set(String key, byte[] data) {
		this.pubsuber.set(key, data);
	}
	
	public synchronized byte[] get(String key) {
		byte[] originalData = this.pubsuber.get(key);
		return originalData;
	}

	public synchronized void set(String key, Serializable data) {
		byte[] serializedData = this.pubsuberDataConverter.serialize(data);
		this.pubsuber.set(key, serializedData);
	}

	public synchronized void set(String key, Serializable data, Class<?> clazz) {
		byte[] serializedData = this.pubsuberDataConverter.serialize(data, clazz);
		this.pubsuber.set(key, serializedData);
	}

	public synchronized <T> T get(String key, Class<T> clazz) {
		byte[] originalData = this.pubsuber.get(key);
		return this.pubsuberDataConverter.deSerializeObject(originalData, clazz);
	}

	public synchronized boolean exists(String key) {
		return this.pubsuber.exists(key);
	}

	public synchronized void publish(String channel, byte[] data) {
		this.pubsuber.publish(channel, data);
	}

	public synchronized void publish(String channel, byte[] data, boolean setByHand) {
		if (setByHand) {
			this.pubsuber.set(channel, data);
		}

		this.pubsuber.publish(channel, data);
	}

	public void registerWithChannel(String clientId, IPubsuberDataListener onChanged, String channel) {
		ChannelListenerEntry entry;
		if (!this.registry.containsKey(clientId)) {
			entry = new ChannelListenerEntry();
			entry.setOnChanged(onChanged);
			List<String> channels = new ArrayList();
			channels.add(channel);
			entry.setChannels(channels);
			this.registry.put(clientId, entry);
		} else {
			entry = (ChannelListenerEntry) this.registry.get(clientId);
			entry.getChannels().add(channel);
		}

	}

	public void registerWithMultiChannels(String clientId, IPubsuberDataListener onChanged, String[] channels) {
		String[] arr$ = channels;
		int len$ = channels.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			String channel = arr$[i$];
			this.registerWithChannel(clientId, onChanged, channel);
		}

	}

	public void removeRegister(String appId) {
		this.registry.remove(appId);
		if (this.registry.size() == 0) {
			this.pubsuber.close();
		}

	}

	public void destroy() {
		if (this.pubsuber != null && this.pubsuber.isAlive()) {
			this.pubsuber.close();
		}

	}

	public void watchPubSuber(List<String> channels) {
        this.pubsuber.watch((String[])channels.toArray(new String[channels.size()]), new IPubSubListener() {
            public void onChange(String channel, byte[] data, Map<String, Object> params) {
                Iterator i$ = PubsuberManager.this.registry.values().iterator();

                while(i$.hasNext()) {
                    PubsuberManager.ChannelListenerEntry entry = (PubsuberManager.ChannelListenerEntry)i$.next();
                    if (entry.getChannels().contains(channel)) {
                        entry.getOnChanged().onChannelDataChanged(channel, new String(data, Charset.defaultCharset()));
                    }
                }

            }
        });
    }
	
	private static final class ChannelListenerEntry {
        private List<String> channels;
        private IPubsuberDataListener onChanged;

        public ChannelListenerEntry() {
        }

        public List<String> getChannels() {
            return this.channels;
        }

        public void setChannels(List<String> channels) {
            this.channels = channels;
        }

        public IPubsuberDataListener getOnChanged() {
            return this.onChanged;
        }

        public void setOnChanged(IPubsuberDataListener onChanged) {
            this.onChanged = onChanged;
        }
    }
}