package com.messagebus.managesystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.messagebus.common.RandomHelper;
import com.messagebus.managesystem.config.MessageBusConfig;
import com.messagebus.managesystem.module.service.INodeService;

import java.lang.Exception;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.messagebus.managesystem.service.bootstrap.MQDataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.common.MessagebusUtil;
import com.messagebus.managesystem.module.OperationResult;
import com.messagebus.managesystem.module.entity.Node;

/**
 * Created by yanghua on 3/20/15.
 */
@RestController
@RequestMapping("/messagebusmanage/queue")
public class QueueService {

    public static final String module        = QueueService.class.getName();
    public static final String resourceError = "BanyanUiLabels";
    
    Logger logger = LoggerFactory.getLogger(QueueService.class);

    private static final String proconRoutingKeyPrefix  = "queue.proxy.message.procon.";
    private static final String reqrespRoutingKeyPrefix = "queue.proxy.message.reqresp.";
    private static final String pubsubRoutingKeyPrefix  = "queue.proxy.message.pubsub.";
    private static final String rpcRoutingKeyPrefix     = "queue.proxy.message.rpc";

    private static final String proconParentId  = "4";
    private static final String pubsubParentId  = "5";
    private static final String reqrespParentId = "6";
    private static final String rpcParentId     = "7";
    
    @Autowired
    @Qualifier("nodeServiceImpl")
    INodeService iNodeService;
    
    @Autowired
    MessagebusUtil messagebusUtil;

    @Autowired
    MessageBusConfig messageBusConfig;

    @RequestMapping("createQueue")
    public OperationResult createQueue(@RequestBody Map<String, ? extends Object> context) {
       
    	logger.info("param : name is null : " + (context.get("name") == null), module);

        String nodeName = (String) context.get("name");
        String carryTypeValue = (String) context.get("communicateType");

        //check exists node name
        try {
            long count  = iNodeService.count(new QueryWrapper<Node>().eq("name", nodeName));
            
            if (count > 0) {
                return OperationResult.buildFailureResult("the node with name : " + nodeName + " is exists ");
            }
        } catch (Exception e) {
            return OperationResult.buildFailureResult(e.getMessage());
        }

        String nodeValue;
        String parentId;
        String isVirtual = "0";

        switch (carryTypeValue) {
            case "produce":
            case "consume":
            case "produce-consume":
                nodeValue = proconRoutingKeyPrefix + nodeName;
                parentId = proconParentId;
                break;


            case "publish":
            case "subscribe":
            case "publish-subscribe":
                nodeValue = pubsubRoutingKeyPrefix + nodeName;
                parentId = pubsubParentId;
                break;

            case "request":
            case "response":
            case "request-response":
                nodeValue = reqrespRoutingKeyPrefix + nodeName;
                parentId = reqrespParentId;
                break;

            case "rpcrequest":
            case "rpcresponse":
            case "rpcrequest-rpcresponse":
                nodeValue = rpcRoutingKeyPrefix + nodeName;
                parentId = rpcParentId;
                break;

            default:
                logger.error("unknown carry type value : " + carryTypeValue, module);
                return OperationResult.buildFailureResult("unknown carry type value : " + carryTypeValue);
        }

        switch (carryTypeValue) {
            case "produce":
            case "publish":
            case "rpcrequest":
                isVirtual = "1";
                break;

            case "consume":
            case "subscribe":
            case "request":
            case "response":
            case "rpcresponse":
            case "produce-consume":
            case "publish-subscribe":
            case "request-response":
                isVirtual = "0";
                break;
        }

       
        Node node = new Node();

        
        node.setSecret(RandomHelper.randomNumberAndCharacter(20));
        node.setName(nodeName);
        node.setValue(nodeValue);
        node.setAppId(context.get("appId").toString());
        node.setParentId(parentId);
        node.setType("1");
        node.setRouterType("");
        node.setRoutingKey(nodeValue.replace("queue", "routingkey"));
        node.setAvailable("1");
        node.setIsInner("0");
        node.setIsVirtual(isVirtual);
        node.setCommunicateType(carryTypeValue);
        node.setCreator(""); //todo
        node.setAuditTypeCode(Constants.CODE_OF_AUDIT_TYPE_UNAUDIT);
        node.setRateLimit((String) context.get("rateLimit"));
        node.setThreshold((String) context.get("threshold"));
        node.setMsgBodySize((String) context.get("msgBodySize"));
        node.setTtl((String) context.get("ttl"));
        node.setTtlPerMsg((String) context.get("ttlPerMsg"));
        node.setCanBroadcast((String) context.get("canBroadcast"));
        //node.setDescription("description", context.get("description"));
        logger.info("the compress field is : " + context.get("compress"), module);
        //node.setCompress("compress", context.get("compress"));

        try {
            iNodeService.saveOrUpdate(node);
            messagebusUtil.publishAndCacheForNodeView(node);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }

    @RequestMapping("updateQueue")
    public OperationResult updateQueue(@RequestBody Map<String, ? extends Object> context) {
        String nodeId = (String) context.get("nodeId");
        String nodeName = (String) context.get("name");
        String available = (String) context.get("available");
        String canBroadcast = (String) context.get("canBroadcast");
        String description = (String) context.get("description");

        try {
            //check exists node name
            
            long count  = iNodeService.count(new QueryWrapper<Node>().eq("name", nodeName).eq("node_id", nodeId));
            
            if (count > 0) {
                return OperationResult.buildFailureResult("the node with name : " + nodeName + " is exists ");
            }

            Node queue = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", nodeId));
            
            String oldName = queue.getName();
            String oldVal = queue.getValue();
            String oldRoutingKey = queue.getRoutingKey();

            queue.setName(nodeName);
            queue.setValue(oldVal.replace(oldName, nodeName));
            queue.setRoutingKey(oldRoutingKey.replace(oldName, nodeName));
            queue.setAvailable(available);
            queue.setCanBroadcast(canBroadcast);
            //queue.setDescription(description);

            iNodeService.saveOrUpdate(queue);

            messagebusUtil.publishAndCacheForNodeView(queue);

            return OperationResult.buildSuccessResult();
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

    @RequestMapping("auditQueue")
    public OperationResult auditQueue(@RequestBody Map<String, ? extends Object> context) {
        String queueId = (String) context.get("queueId");
        try {
            Node queueInfo = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", queueId));
            queueInfo.setAuditTypeCode(Constants.CODE_OF_AUDIT_TYPE_SUCCESS);
            queueInfo.setSecret(RandomHelper.randomNumberAndCharacter(20));
            iNodeService.saveOrUpdate(queueInfo);

            // MQ initTopologyComponent
            if(!"1".equals(queueInfo.getIsVirtual())){
                List<Node> nodeList = iNodeService.list(new QueryWrapper<Node>().eq("type", "0").orderByAsc("parent_id"));
                nodeList.add(queueInfo);
                for (Node node : nodeList){
                    node.setRoutingKey(node.getRoutingKey() == null ? "" : node.getRoutingKey());
                }
                MQDataInitializer.getInstance(messageBusConfig.getMqHost()).initTopologyComponent(nodeList.toArray(new Node[nodeList.size()]));
            }

            messagebusUtil.publishAndCacheForNodeView(queueInfo);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }
/*
    public static Map<String, Object> getQueueRateWarningsById(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            Map<String, Object> resultCtx = dispatcher.runSync("performFind", context);
            return resultCtx;
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public static Map<String, Object> getAvailableFlowToQueues(DispatchContext ctx, Map<String, Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Delegator delegator = ctx.getDelegator();
        String flowFromId = (String) context.get("flowFromId");

        if (flowFromId != null && !flowFromId.isEmpty()) {
            //if there is a flowFromId key , it will be failed when exec performFind
            context.remove("flowFromId");
        }

        GenericValue queue = null;
        try {
            queue = delegator.findOne("Node", UtilMisc.toMap("nodeId", flowFromId), false);
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        String communicateType = queue.getString("communicateType");

        String flowToCommTypeSubStr = "";
        switch (communicateType) {
            case "produce":
            case "produce-consume":
                flowToCommTypeSubStr = "consume";
                break;

            case "publish":
            case "publish-subscribe":
                flowToCommTypeSubStr = "subscribe";
                break;


            case "request":
            case "request-response":
                flowToCommTypeSubStr = "response";
                break;

            case "rpcrequest":
                flowToCommTypeSubStr = "rpcresponse";
                break;

            default:
                Debug.logError("unknown communicate type : " +
                                   communicateType + "the flow from id is : " + flowFromId, module);
        }

        context.put("entityName", "Node");

        Map<String, Object> inputFields = new HashMap<>();

        inputFields.put("appId_op", "notEqual");
        inputFields.put("appId", flowFromId);
        inputFields.put("appId_ic", "Y");

        inputFields.put("communicateType_op", "contains");
        inputFields.put("communicateType", flowToCommTypeSubStr);
        inputFields.put("communicateType_ic", "Y");
        context.put("inputFields", inputFields);

        context.put("noConditionFind", "N");

        try {
            Map<String, Object> resultCtx = dispatcher.runSync("performFindList", context);
            return resultCtx;
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }
*/
    @RequestMapping("testSoapService")
    public OperationResult testSoapService(@RequestBody Map<String, ? extends Object> context) {
        try {
           
            List<Node> queues = iNodeService.list(new QueryWrapper<Node>().eq("type", "1"));
            
            Map<String, Object> result = new HashMap<String,Object>();
            result.put("results", queues);
            return OperationResult.buildSuccessResult(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            Map<String, Object> result = new HashMap<String,Object>();
            result.put("results", Collections.emptyList());
            return OperationResult.buildFailureResult(e.getMessage(), result);
        }
    }

}
