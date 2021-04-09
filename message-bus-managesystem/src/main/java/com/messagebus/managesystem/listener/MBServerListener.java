package com.messagebus.managesystem.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.messagebus.client.MessagebusPool;
import com.messagebus.common.GsonUtil;
import com.messagebus.interactor.pubsub.PubsuberManager;
import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.common.MessagebusUtil;
import com.messagebus.managesystem.config.MessageBusConfig;
import com.messagebus.managesystem.config.UtilCache;
import com.messagebus.managesystem.module.entity.Config;
import com.messagebus.managesystem.module.entity.Node;
import com.messagebus.managesystem.module.entity.NodeView;
import com.messagebus.managesystem.module.service.IConfigService;
import com.messagebus.managesystem.module.service.INodeService;
import com.messagebus.managesystem.module.service.impl.ConfigServiceImpl;
import com.messagebus.managesystem.module.service.impl.NodeServiceImpl;
import com.messagebus.managesystem.service.bootstrap.MQDataInitializer;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanghua on 4/13/15.
 */
@Component
public class MBServerListener implements ApplicationListener<ContextRefreshedEvent> {

    public static String module = MBServerListener.class.getName();
    Logger logger = LoggerFactory.getLogger(MBServerListener.class);

    private MessagebusPool innerMessagebusPool;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) { 

    	// 将 ApplicationContext 转化为 WebApplicationContext 
        WebApplicationContext webApplicationContext = 
            (WebApplicationContext)contextRefreshedEvent.getApplicationContext();
        

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=bootstrap service (start)-=-=-=-=-=-=-=-=-=-=-=-=-=-", module);

        //start up bootstrap service
        logger.info("initializing log config file....", module);
        
        MessageBusConfig messageBusConfig = webApplicationContext.getBean(MessageBusConfig.class);
        
        logger.info("initializing mq server.... ", module);
        
        String mqHost = messageBusConfig.getMqHost();
        logger.info("message queue server host is : " + mqHost, module);
        MQDataInitializer mqManager = MQDataInitializer.getInstance(mqHost);
        List<Node> allNodes = findAllNode(webApplicationContext);
        logger.info("all nodes count : " + allNodes.size(), module);
        try {
            mqManager.initTopologyComponent(allNodes.toArray(new Node[allNodes.size()]));
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            throw new RuntimeException("init mq topology component error : " + e.getMessage());
        }
        
        IConfigService iConfigService = webApplicationContext.getBean("configServiceImpl",IConfigService.class);

        //refresh mq host config
        logger.info("refreshing config for mq host with new key : " + mqHost, module);
        try {
           
            List<Config> results = iConfigService.list(new QueryWrapper<Config>().eq("item_key", "messagebus.client.host"));
            
            Config oldConfigItem = results.get(0);
            oldConfigItem.setItemValue(mqHost);
            iConfigService.saveOrUpdate(oldConfigItem);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            throw new RuntimeException("refresh config with key 'messagebus.client.host' error " + e.getMessage());
        }

        //cache mete data
        logger.info("caching meta data to pubsuber.... ", module);
        String pubsuberHost = messageBusConfig.getPubsuberHost();
        int pubsuberPort = messageBusConfig.getPubsuberPort(); 
        logger.info("pubsuber host : " + pubsuberHost, module);
        logger.info("pubsuber port : " + pubsuberPort, module);
        PubsuberManager pubsuberManager = new PubsuberManager(pubsuberHost, pubsuberPort);

        //cache pubsuberManager
        ConcurrentHashMap<String, Object> banyanGlobalCache = UtilCache.findCache();
        banyanGlobalCache.put(Constants.KEY_OF_MESSAGEBUS_PUBSUBER_MANAGER, pubsuberManager);
        cacheMetaData(pubsuberManager, webApplicationContext, allNodes);

        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=bootstrap service (end)-=-=-=-=-=-=-=-=-=-=-=-=-=-", module);

        //broadcast start event
        logger.info("publishing server started event ...", module);
        pubsuberManager.publish(com.messagebus.common.Constants.PUBSUB_SERVER_STATE_CHANNEL,
                                com.messagebus.common.Constants.MESSAGEBUS_SERVER_EVENT_STARTED.getBytes(), true);

        logger.info("initial a inner messagebus client pool ...", module);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(10);
        innerMessagebusPool = new MessagebusPool(pubsuberHost,poolConfig);

        //start up daemon service
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=daemon service (start)-=-=-=-=-=-=-=-=-=-=-=-=-=-", module);
        Map<String, Object> ctx = new HashMap<String, Object>(2);
        ctx.put(com.messagebus.managesystem.common.Constants.KEY_OF_MESSAGEBUS_INNER_POOL, innerMessagebusPool
        		);
        ctx.put(com.messagebus.managesystem.common.Constants.KEY_OF_MESSAGEBUS_INNER_POOL, mqHost);
        com.messagebus.managesystem.service.daemon.ServiceLoader serviceLoader = com.messagebus.managesystem.service.daemon.ServiceLoader.getInstance(ctx);

        logger.info("initializing deamon service : rateWarningMonitorService ...", module);
        final List<Node> rateLimitedQueues = this.findAllRateLimitedQueues(webApplicationContext);
        /*
        IService rateWarningMonitorService = new RateWarningMonitorService(ctx);
        ((RateWarningMonitorService) rateWarningMonitorService).setCallback(new IServiceCallback() {
            @Override
            public void callback(Map<String, Object> map) {
                List<Object> remoteObjs = (List<Object>) map.get("queueInfoList");
                try {
                    for (GenericValue queue : rateLimitedQueues) {
                        String queueName = queue.getString("value");
                        for (Object queueInfoObj : remoteObjs) {
                            Map<String, Object> queueInfo = (Map) queueInfoObj;
                            if (queueInfo.get("name").equals(queueName)) {
                                Map<String, Object> msgStatsInfo = (Map) queueInfoObj;
                                Map<String, Object> publishDetailMap = (Map) msgStatsInfo.get("publish_details");
                                int benchmark = Integer.parseInt(queue.getString("rateLimit"));
                                int realRate = Integer.parseInt(publishDetailMap.get("rate").toString());
                                //log to rate limit
                                if (realRate > benchmark) {
                                    GenericValue nodeEntity = delegator.makeValue("Node");
                                    nodeEntity.setString("warningId", delegator.getNextSeqId("QueueRateWarning"));
                                    nodeEntity.setString("nodeId", queue.getString("nodeId"));
                                    nodeEntity.setString("rateLimit", queue.getString("rateLimit"));
                                    nodeEntity.setString("realRate", publishDetailMap.get("rate").toString());
                                    nodeEntity.set("fromDate", UtilDateTime.nowDate());
                                    delegator.create(nodeEntity);
                                }
                            }
                        }
                    }
                } catch (GenericEntityException e) {
                    logger.logError(e, module);
                }
            }
        });
		*/
        //serviceLoader.getScheduleCycleServiceMap().put("rateWarningMonitorService", rateWarningMonitorService);
        serviceLoader.launch();
        logger.info("=-=-=-=-=-=-=-=-=-=-=-=-=-=daemon service (end)-=-=-=-=-=-=-=-=-=-=-=-=-=-", module);
    }

    private List<Node> findAllNode(ApplicationContext ctx) {
        List<String> orderByList = new ArrayList<String>(1);
        orderByList.add("+parentId");
        
        INodeService iNodeService = (INodeService)ctx.getBean("nodeServiceImpl",INodeService.class);
        
        List<Node> nodeList = null;
        
        
        
        try {
           
            nodeList = iNodeService.list(new QueryWrapper<Node>().orderByAsc("parent_id"));
            
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return Collections.emptyList();
        }

        if (nodeList == null || nodeList.size() == 0) {
            return Collections.emptyList();
        }

        Iterator<Node> iterator = nodeList.iterator();
        List<Node> nodes = new ArrayList<>(nodeList.size());
        while (iterator.hasNext()) {
        	Node genericNode = iterator.next();
            Node node = new Node();
            node.setNodeId(genericNode.getNodeId());
            node.setSecret(genericNode.getSecret());
            node.setName(genericNode.getName());
            node.setValue(genericNode.getValue());
            node.setType(genericNode.getType());
            node.setParentId(genericNode.getParentId());
            node.setRoutingKey(genericNode.getRoutingKey() == null ? "" : genericNode.getRoutingKey());
            node.setRouterType(genericNode.getRouterType());
            node.setAppId(genericNode.getAppId());
            node.setAvailable(genericNode.getAvailable());
            node.setIsInner(genericNode.getIsInner());
            node.setIsVirtual(genericNode.getIsVirtual());
            node.setCanBroadcast(genericNode.getCanBroadcast());
            node.setCommunicateType(genericNode.getCommunicateType());
            node.setRateLimit(genericNode.getRateLimit());
            node.setThreshold(genericNode.getThreshold());
            node.setMsgBodySize(genericNode.getMsgBodySize());
            node.setTtl(genericNode.getTtl());
            node.setTtlPerMsg(genericNode.getTtlPerMsg());
            nodes.add(node);
        }

        return nodes;
    }

    private void cacheMetaData(PubsuberManager pubsuberManager, ApplicationContext ctx, List<Node> allNode) {
        //cache queue data
        Map<String, NodeView> secretNodeViewMap = this.buildMetaData(ctx);
        logger.info(" secret node view map num : " + secretNodeViewMap.size(), module);
        for (Map.Entry<String, NodeView> entry : secretNodeViewMap.entrySet()) {
            pubsuberManager.set(com.messagebus.common.Constants.REVERSE_MESSAGE_ZK_PATH +"/"+entry.getKey(),  GsonUtil.toJsonWtihNullField(entry.getValue()).getBytes());
        }

        //cache client config data
        Map<String, String> allClientConfigMap = this.findAllClientConfig(ctx);
        for (Map.Entry<String, String> entry : allClientConfigMap.entrySet()) {
            pubsuberManager.set(com.messagebus.common.Constants.PUBSUB_CONFIG_CHANNEL +"/"+entry.getKey(), entry.getValue());
        }

        //cache notification exchange node
        Node notificationNode = this.findNotificationExchangeNode(allNode);
        pubsuberManager.set(com.messagebus.common.Constants.PUBSUB_NOTIFICATION_EXCHANGE_CHANNEL,
                            notificationNode);
        pubsuberManager.publish(com.messagebus.common.Constants.PUBSUB_NOTIFICATION_EXCHANGE_CHANNEL,
                                "".getBytes(Charset.defaultCharset()));
    }

    private Map<String, NodeView> buildMetaData(ApplicationContext ctx) {
    	
    	MessagebusUtil messagebusUtil = ctx.getBean(MessagebusUtil.class);
    	
        Map<String, Node> idNodeMapView = messagebusUtil.buildIdNodeMapView();

        Map<String, NodeView> secretNodeViewMap = new HashMap<>(idNodeMapView.size());
        for (Node aQueue : idNodeMapView.values()) {
            NodeView nodeView = messagebusUtil.buildNodeViewBySecret(aQueue,idNodeMapView);

            secretNodeViewMap.put(aQueue.getSecret(), nodeView);
        }

        return secretNodeViewMap;
    }

    private List<Node> findAllRateLimitedQueues(ApplicationContext ctx) {
    
    	INodeService iNodeService = (INodeService)ctx.getBean("nodeServiceImpl",INodeService.class);
        
        QueryWrapper<Node> q = new QueryWrapper<Node>();
        
        Map<String,Object> queryParam = new HashMap<String,Object>();
		
		queryParam.put("type", "1");
		queryParam.put("available", "1");
		queryParam.put("audit_type_code", Constants.CODE_OF_AUDIT_TYPE_SUCCESS);
		

        try {
            
            List<Node> rateLimitedQueues = iNodeService.list(q.allEq(queryParam).isNotNull("rate_limit"));
            return rateLimitedQueues;
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return Collections.emptyList();
        }
    }

    private Map<String, String> findAllClientConfig(ApplicationContext ctx) {
       
    	IConfigService iConfigService = ctx.getBean("configServiceImpl",IConfigService.class);
        try {
            
            List<Config> clientConfigs = iConfigService.list(new QueryWrapper<Config>().eq("type", "client"));
            
            Map<String, String> clientConfigMap = new HashMap<>(clientConfigs.size());
            for (Config clientConfig : clientConfigs) {
                clientConfigMap.put(clientConfig.getItemKey(), clientConfig.getItemValue());
            }

            return clientConfigMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return Collections.emptyMap();
        }
    }

    private Node findNotificationExchangeNode(List<Node> allNodes) {
        for (Node node : allNodes) {
            if (node.getType().equals("0") && node.getValue().contains("exchange")
                && node.getValue().contains("notification"))
                return node;
        }

        return new Node();
    }

}