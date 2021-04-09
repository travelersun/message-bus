package com.messagebus.managesystem.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Strings;
import com.messagebus.common.GsonUtil;
import com.messagebus.interactor.pubsub.PubsuberManager;
import com.messagebus.managesystem.config.UtilCache;
import com.messagebus.managesystem.module.entity.Node;
import com.messagebus.managesystem.module.entity.NodeView;
import com.messagebus.managesystem.module.entity.Sink;
import com.messagebus.managesystem.module.service.INodeService;
import com.messagebus.managesystem.module.service.ISinkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanghua on 4/7/15.
 */
@Component
public class MessagebusUtil {

    public static final String module = MessagebusUtil.class.getName();
    
    Logger logger = LoggerFactory.getLogger(MessagebusUtil.class);
    
    @Autowired
    @Qualifier("sinkServiceImpl")
    ISinkService iSinkService;
    
    @Autowired
    @Qualifier("nodeServiceImpl")
    INodeService iNodeService;
    
    public NodeView buildNodeViewBySecret(Node aQueue, Map<String, Node> idNodeMapView) {
    	
    	Map<String,Object> queryParam = new HashMap<String,Object>();
		
		queryParam.put("flow_from", aQueue.getNodeId());
		queryParam.put("audit_type_code", Constants.CODE_OF_AUDIT_TYPE_SUCCESS);
		
        List<Sink> sinks = iSinkService.list(new QueryWrapper<Sink>().allEq(queryParam));
        List<String> sinkTokens = extractSinkTokens(sinks);
        Map<String, Node> relatedQueueNameNodeMap = buildRelatedNameNodeMapView(sinks, idNodeMapView);
        List<Node> subscribedNodes = filterSubscribeNodes(sinks, idNodeMapView);

        NodeView nodeView = new NodeView();
        nodeView.setSecret(aQueue.getSecret());
        nodeView.setCurrentQueue(aQueue);
        nodeView.setSinkTokens(sinkTokens);
        nodeView.setRelatedQueueNameNodeMap(relatedQueueNameNodeMap);
        nodeView.setSubscribeNodes(subscribedNodes);

        return nodeView;
    }

    public void publishAndCacheForNodeView(Node nodeEntity) {
        publishEvent(com.messagebus.common.Constants.PUBSUB_NODEVIEW_CHANNEL,
                                    nodeEntity.getSecret(),
                                    false);

        ConcurrentHashMap<String,Object> poolUtilCache = UtilCache.findCache();
        PubsuberManager pubsuberManager = (PubsuberManager) poolUtilCache.get(Constants.KEY_OF_MESSAGEBUS_PUBSUBER_MANAGER);

        Map<String, Node> idNodeViewMap = buildIdNodeMapView();
        Node node = nodeEntity;
        NodeView newNodeView = buildNodeViewBySecret(node,idNodeViewMap);
        pubsuberManager.set(com.messagebus.common.Constants.REVERSE_MESSAGE_ZK_PATH +"/"+nodeEntity.getSecret(), GsonUtil.toJsonWtihNullField(newNodeView).getBytes());
    }

    public Map<String, Node> buildIdNodeMapView() {
    	
    	Map<String,Object> queryParam = new HashMap<String,Object>();
		
		queryParam.put("type", "1");
		queryParam.put("available", "1");
		queryParam.put("audit_type_code", Constants.CODE_OF_AUDIT_TYPE_SUCCESS);
		
        List<Node> allPublicQueues = iNodeService.list(new QueryWrapper<Node>().allEq(queryParam));
        if (allPublicQueues == null || allPublicQueues.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Node> idNodeMap = new HashMap<>(allPublicQueues.size());
        for (Node node : allPublicQueues) {
            idNodeMap.put(node.getNodeId().toString(), node);
        }

        return idNodeMap;
    }


    public void publishEvent(String channel, String content, boolean setByHand) {
    	ConcurrentHashMap<String,Object> poolCache = UtilCache.findCache();
        PubsuberManager pubsuberManager = (PubsuberManager) poolCache.get(Constants.KEY_OF_MESSAGEBUS_PUBSUBER_MANAGER);
        if (setByHand) {
            pubsuberManager.set(channel, content.getBytes(Charset.defaultCharset()));
        }

        pubsuberManager.publish(channel, content.getBytes(Charset.defaultCharset()));
    }


    private static Map<String, Node> buildRelatedNameNodeMapView(List<Sink> sinks,
                                                                 Map<String, Node> idNodeViewMap) {
        Map<String, Node> relatedNameNodeMap = new HashMap<>(sinks.size());
        for (Sink aSink : sinks) {
            String flowTo = aSink.getFlowTo();
            Node flowToNode = idNodeViewMap.get(flowTo);
            relatedNameNodeMap.put(flowToNode.getName(), flowToNode);
        }

        return relatedNameNodeMap;
    }

    private static List<Node> filterSubscribeNodes(List<Sink> sinks,
                                                   Map<String, Node> idNodeViewMap) {
        List<Node> subscribeNodes = new ArrayList<>();
        for (Sink aSink : sinks) {
            String fromCommunicateType = aSink.getFromCommunicateType();
            String toCommunicateType = aSink.getToCommunicateType();

            if (Strings.isNullOrEmpty(fromCommunicateType)) continue;
            if (Strings.isNullOrEmpty(toCommunicateType)) continue;

            if (!fromCommunicateType.equals("publish") && !fromCommunicateType.equals("publish-subscribe"))
                continue;
            if (!toCommunicateType.equals("subscribe") && !toCommunicateType.equals("publish-subscribe")) continue;

            String subscribeNodeId = aSink.getFlowTo();
            subscribeNodes.add(idNodeViewMap.get(subscribeNodeId));
        }

        return subscribeNodes;
    }

   
    private static List<String> extractSinkTokens(List<Sink> sinks) {
        List<String> sinkTokens = new ArrayList<>(sinks.size());
        for (Sink aSink : sinks) {
            sinkTokens.add(aSink.getToken());
        }

        return sinkTokens;
    }


}
