package com.messagebus.client;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.messagebus.client.model.Node;
import com.messagebus.client.model.NodeView;
import com.messagebus.common.Constants;
import com.messagebus.common.GsonUtil;
import com.messagebus.interactor.pubsub.IPubsuberDataListener;
import com.messagebus.interactor.pubsub.PubsuberManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * the config manager
 */
public class ConfigManager {

    private static final Log logger = LogFactory.getLog(ConfigManager.class);
    
    private static final Gson   GSON              = new Gson();
    
    private static final String REVERSE_MESSAGE_ZK_PATH               = "/reverse/message/nodeView";

    private          Map<String, NodeView> secretNodeViewMap = new ConcurrentHashMap<String, NodeView>();
    
    private Map<String, Source> secretSourceMap = new ConcurrentHashMap<String, Source>();
    private Map<String, Source> nameSourceMap   = new ConcurrentHashMap<String, Source>();
    private Map<String, Sink>   secretSinkMap   = new ConcurrentHashMap<String, Sink>();
    private Map<String, Sink>   nameSinkMap     = new ConcurrentHashMap<String, Sink>();
    private Map<String, Stream> streamMap       = new ConcurrentHashMap<String, Stream>();

    private PubsuberManager pubsuberManager;
    private CuratorFramework openedZookeeper;
    private EventBus        componentEventBus;

    public ConfigManager(CuratorFramework zookeeper) {
        this.openedZookeeper = zookeeper;
        
      //source -> secret
        PathChildrenCache sourceSecretCache = new PathChildrenCache(zookeeper,
        		REVERSE_MESSAGE_ZK_PATH, false);
        sourceSecretCache.getListenable().addListener(new PathChildrenCacheListener() {
        	@Override
            public void childEvent(CuratorFramework curatorFramework,
                                   PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        		
        		PathChildrenCacheEvent.Type eventType = pathChildrenCacheEvent.getType();
                switch (eventType) {
                    case CONNECTION_SUSPENDED:
                    case CONNECTION_LOST:
                        logger.info("Connection error,waiting...");
                        break;
                    case CHILD_REMOVED:
                    	
                    	String path = pathChildrenCacheEvent.getData().getPath();
                    	onPathChildrenRemove(path);
                        logger.info("Child node removed : "+ path);
                        
                        break;
                    case CHILD_UPDATED:
                    	String path2 = pathChildrenCacheEvent.getData().getPath();
                    	onPathChildrenChanged(path2);
                        logger.info("Child node update : "+ path2);
                        
                        break;
                    case CHILD_ADDED:
                    	String path3 = pathChildrenCacheEvent.getData().getPath();
                    	
                        logger.info("Child node add : "+ path3);
                        
                        break;
                    default:
                    	
                        logger.info("Child node new added or updated : "+eventType);
                        
                }
            }
        });
        
        try {
			sourceSecretCache.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
			e.printStackTrace();
		}
        
    }

    public PubsuberManager getPubsuberManager() {
        return pubsuberManager;
    }

    public void setPubsuberManager(PubsuberManager pubsuberManager) {
        this.pubsuberManager = pubsuberManager;
    }

    public EventBus getComponentEventBus() {
        return componentEventBus;
    }

    public void setComponentEventBus(EventBus componentEventBus) {
        this.componentEventBus = componentEventBus;
    }

    public Map<String, NodeView> getSecretNodeViewMap() {
        return secretNodeViewMap;
    }


    public NodeView getNodeView(String secret) {
        if (Strings.isNullOrEmpty(secret)) {
            throw new NullPointerException("the secret can not be null or empty");
        }

        if (this.secretNodeViewMap.containsKey(secret)) {   //local cache
            return this.secretNodeViewMap.get(secret);
        } else {                                            //remote data then local cache
            //NodeView nodeViewObj = this.pubsuberManager.get(secret, NodeView.class);
            String nodeViewObjStr = getDataFromZK(REVERSE_MESSAGE_ZK_PATH +"/"+secret);
           
            NodeView nodeViewObj = GsonUtil.fromJson(nodeViewObjStr, NodeView.class); 
            
            this.secretNodeViewMap.put(secret, nodeViewObj);
            return nodeViewObj;
        }
    }
    
    private String getDataFromZK(String path) {
        try {
            logger.debug("path : " + path);
            return new String(openedZookeeper.getData().forPath(path));
        } catch (Exception e) {
            logger.error(e);
        }

        return "";
    }

    
    private void onPathChildrenChanged(String path) {
        logger.debug("received path change from zookeeper, key : " + path);
        String partPath = path.replace(REVERSE_MESSAGE_ZK_PATH + "/", "");

        if (getSecretNodeViewMap().containsKey(partPath)) {
            getSecretNodeViewMap().remove(partPath);
            getNodeView(partPath);
        }

    }
    
    private void onPathChildrenRemove(String path) {
        logger.debug("received path change from zookeeper, key : " + path);
        String partPath = path.replace(REVERSE_MESSAGE_ZK_PATH + "/", "");

        if (getSecretNodeViewMap().containsKey(partPath)) {
            getSecretNodeViewMap().remove(partPath);
        }

    }
    


    public class NodeViewChangedHandler implements IPubsuberDataListener {

        @Override
        public void onChannelDataChanged(String channel, Object data) {
            if (getSecretNodeViewMap().containsKey(channel)) {
                getSecretNodeViewMap().remove(channel);
                getNodeView(channel);
            }
        }

    }
    
    public Source getSourceBySecret(String secret) {
    	
        if (Strings.isNullOrEmpty(secret)) {
            throw new NullPointerException("the secret can not be null or empty");
        }
        
        Source s= new Source();
        
        NodeView n =getNodeView(secret);
        if(null == n) {
        	throw new NullPointerException("the secret is invalied");
        }
        
        Node soureNode = n.getCurrentQueue();
        
        s.setAppId(soureNode.getAppId());
        s.setBroadcastable(soureNode.getCanBroadcast());
        s.setName(soureNode.getName());
        s.setRoutingKey(soureNode.getRoutingKey());
        s.setSecret(soureNode.getSecret());
        s.setType(soureNode.getCommunicateType());

        if(null != n.getSubscribedNodes() && n.getSubscribedNodes().size() > 0){
            List<Sink> sinks = new ArrayList<Sink>();
            for(Node sub : n.getSubscribedNodes() ){
                Sink sink = new Sink();
                sink.setAppId(sub.getAppId());
                sink.setMsgBodySize(sub.getMsgBodySize());
                sink.setName(sub.getName());
                sink.setQueueName(sub.getValue());
                sink.setRoutingKey(sub.getRoutingKey());
                sink.setSecret(sub.getSecret());
                sink.setType(sub.getCommunicateType());
                sinks.add(sink);
            }
            s.setSinks(sinks);
        }

    	return s;
    }

    public Source getSourceByName(String name) {
    	return getSourceBySecret(name);
    }

    public Sink getSinkBySecret(String secret) {
    	if (Strings.isNullOrEmpty(secret)) {
            throw new NullPointerException("the secret can not be null or empty");
        }
        
    	Sink s= new Sink();
        
        NodeView n =getNodeView(secret);;
        if(null == n) {
        	throw new NullPointerException("the secret is invalied");
        }
        
        Node sn = n.getCurrentQueue();
        
        s.setAppId(sn.getAppId());
        //s.setAutoAck(sn.get);
        s.setMsgBodySize(sn.getMsgBodySize());
        s.setName(sn.getName());
        s.setQueueName(sn.getValue());
        s.setRoutingKey(sn.getRoutingKey());
        s.setSecret(sn.getSecret());
        s.setType(sn.getCommunicateType());
        
    	return s;
    }

    public Sink getSinkByName(String secret,String name) {
    	if (Strings.isNullOrEmpty(secret)) {
            throw new NullPointerException("the secret can not be null or empty");
        }
    	
    	if (Strings.isNullOrEmpty(name)) {
            throw new NullPointerException("the name can not be null or empty");
        }
        
    	Sink s= new Sink();
        
        NodeView n =getNodeView(secret);;
        if(null == n) {
        	throw new NullPointerException("the secret is invalied");
        }
        
        Node sn  = null;
        if(name.equals(n.getCurrentQueue().getName())) {
        	sn = n.getCurrentQueue();
        }else {
        	 sn = n.getRelatedQueueNameNodeMap().get(name);
        }
        
        
        
        s.setAppId(sn.getAppId());
        //s.setAutoAck(sn.get);
        s.setMsgBodySize(sn.getMsgBodySize());
        s.setName(sn.getName());
        s.setQueueName(sn.getValue());
        s.setRoutingKey(sn.getRoutingKey());
        s.setSecret(sn.getSecret());
        s.setType(sn.getCommunicateType());
        
    	return s;
    }

    public Stream getStreamByToken(String secret,String token) {
    	
    	
    	return null;
    }
    

    public static class Source {

        private String secret;
        private String name;
        private String type;
        private String appId;
        private String broadcastable;
        private String routingKey;
        private List<Sink> sinks;

        public Source() {
        }

        public List<Sink> getSinks() {
            return sinks;
        }

        public void setSinks(List<Sink> sinks) {
            this.sinks = sinks;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getBroadcastable() {
            return broadcastable;
        }

        public void setBroadcastable(String broadcastable) {
            this.broadcastable = broadcastable;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }
    }

    public static class Sink {
        private String secret;
        private String name;
        private String queueName;
        private String routingKey;
        private String type;
        private String appId;
        private String autoAck;
        private String msgBodySize;

        public Sink() {
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getRoutingKey() {
            return routingKey;
        }

        public void setRoutingKey(String routingKey) {
            this.routingKey = routingKey;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public boolean isAutoAck() {
            return autoAck == "1";
        }

        public void setAutoAck(String autoAck) {
            this.autoAck = autoAck;
        }

        public String getMsgBodySize() {
            return msgBodySize;
        }

        public void setMsgBodySize(String msgBodySize) {
            this.msgBodySize = msgBodySize;
        }
    }

    public static class Stream {
        private String sourceSecret;
        private String sourceName;
        private String sinkSecret;
        private String sinkName;
        private String token;

        public Stream() {
        }

        public String getSourceSecret() {
            return sourceSecret;
        }

        public void setSourceSecret(String sourceSecret) {
            this.sourceSecret = sourceSecret;
        }

        public String getSourceName() {
            return sourceName;
        }

        public void setSourceName(String sourceName) {
            this.sourceName = sourceName;
        }

        public String getSinkSecret() {
            return sinkSecret;
        }

        public void setSinkSecret(String sinkSecret) {
            this.sinkSecret = sinkSecret;
        }

        public String getSinkName() {
            return sinkName;
        }

        public void setSinkName(String sinkName) {
            this.sinkName = sinkName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
