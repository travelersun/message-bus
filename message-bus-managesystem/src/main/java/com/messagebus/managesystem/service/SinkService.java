package com.messagebus.managesystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.messagebus.common.RandomHelper;
import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.common.MessagebusUtil;
import com.messagebus.managesystem.module.OperationResult;
import com.messagebus.managesystem.module.entity.App;
import com.messagebus.managesystem.module.entity.Node;
import com.messagebus.managesystem.module.entity.Sink;
import com.messagebus.managesystem.module.service.INodeService;
import com.messagebus.managesystem.module.service.ISinkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanghua on 3/12/15.
 */
@RestController
@RequestMapping("/messagebusmanage/sink")
public class SinkService {

    public static final String module = SinkService.class.getName();
    
    Logger logger = LoggerFactory.getLogger(SinkService.class);
    
    @Autowired
    @Qualifier("sinkServiceImpl")
    ISinkService iSinkService;
    
    @Autowired
    @Qualifier("nodeServiceImpl")
    INodeService iNodeService;
    
    @Autowired
    MessagebusUtil messagebusUtil;

    @RequestMapping("createSink")
    public  OperationResult createSink(@RequestBody Map<String, ? extends Object> context) {
        
        try {
            //check flow from is audited
            String flowFrom = context.get("flowFrom") + "";
            
            Node fromNode = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", flowFrom));
            
            boolean isFromNodeAudited = fromNode.getAuditTypeCode() != null
                && fromNode.getAuditTypeCode().equals(Constants.CODE_OF_AUDIT_TYPE_SUCCESS);

            if (!isFromNodeAudited) {
            	logger.error("the from node with id : " + flowFrom + " is unaudited", module);
                return OperationResult.buildFailureResult("the from node with name : " + fromNode.getName() + " is unaudited");
            }

            //check flow to is audited
            String flowTo = context.get("flowTo") + "";
            
            Node toNode = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", flowTo));
            
            boolean isToNodeAudited = toNode.getAuditTypeCode() != null
                && toNode.getAuditTypeCode().equals(Constants.CODE_OF_AUDIT_TYPE_SUCCESS);

            if (!isToNodeAudited) {
            	logger.error("the to node with id : " + flowTo + " is unaudited", module);
                return OperationResult.buildFailureResult("the to node with name : " + toNode.getName() + " is unaudited");
            }

            //check flow to is not a virtual node
            boolean isToNodeVirtual = toNode.getIsVirtual() != null && toNode.getIsVirtual().equals("1");
            if (isToNodeVirtual) {
            	logger.error(" the to node with id : " + flowTo + " can not be a virtual node", module);
                return OperationResult.buildFailureResult(" the to node with name : " + toNode.getName() + " can not be a virtual node ");
            }

            //check flow from and to is under one communicate-exchange
            String fromCmutType = fromNode.getCommunicateType();
            String toCmutType = toNode.getCommunicateType();
            boolean isSameCommunicateType = judgeSameKindOfCommunicateType(fromCmutType, toCmutType);
            if (!isSameCommunicateType) {
            	logger.error("the from node with id : " + flowTo + " and the to node with id : " + flowTo
                                   + " is not the same communicate type", module);
                return OperationResult.buildFailureResult("can not communicate between two different type!");
            }

            //check the sink exists
            
            Map<String,Object> params = new HashMap<String,Object>();
            
            params.put("flow_from", flowFrom);
            params.put("flow_to", flowTo);
            
            int count = iSinkService.count(new QueryWrapper<Sink>().allEq(params));
            
            if (count > 0) {
            	logger.error("there is a sink flow from : " + flowFrom + " and flow to : " + flowTo, module);
                return OperationResult.buildFailureResult("there is a sink flow from : " + flowFrom + " and flow to : " + flowTo);
            }

            String creator = "";

            Sink sink = new Sink();
            
            sink.setFlowFrom(flowFrom);
            sink.setFromCommunicateType(fromNode.getCommunicateType());
            sink.setFlowTo(flowTo);
            sink.setToCommunicateType(toNode.getCommunicateType());
            sink.setAuditTypeCode(Constants.CODE_OF_AUDIT_TYPE_UNAUDIT);
            sink.setCreator(creator);
            
            iSinkService.saveOrUpdate(sink);
            
            Node flowFromQ = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", flowFrom));

            messagebusUtil.publishAndCacheForNodeView(flowFromQ);
        } catch (Exception e) {
        	logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }

    @RequestMapping("auditSink")
    public  OperationResult auditSink(@RequestBody Map<String, ? extends Object> context) {
        String sinkId = (String) context.get("sinkId");

        try {
            
            Sink sinkInfo = iSinkService.getOne(new QueryWrapper<Sink>().eq("sink_id", sinkId));
            
            sinkInfo.setAuditTypeCode(Constants.CODE_OF_AUDIT_TYPE_SUCCESS);
            sinkInfo.setToken(RandomHelper.randomNumberAndCharacter(20));
            sinkInfo.setEnable("1");
            iSinkService.saveOrUpdate(sinkInfo);

            Node flowFromQ = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", sinkInfo.getFlowFrom()));

            messagebusUtil.publishAndCacheForNodeView(flowFromQ);
        } catch (Exception e) {
        	logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }

        return OperationResult.buildSuccessResult();
    }

    @RequestMapping("removeSink")
    public  OperationResult removeSink(@RequestBody Map<String, ? extends Object> context) {
        String sinkId = context.get("sinkId") + "";

        try {
           
            Sink sinkInfo = iSinkService.getOne(new QueryWrapper<Sink>().eq("sink_id", sinkId));
            
            iSinkService.remove(new QueryWrapper<Sink>().eq("sink_id", sinkId));
            
            Node flowFromQ = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", sinkInfo.getFlowFrom()));
            
            
            messagebusUtil.publishAndCacheForNodeView(flowFromQ);

            return OperationResult.buildSuccessResult();
        } catch (Exception e) {
        	logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult("remove sink item with sink id : " + sinkId);
        }
    }

    @RequestMapping("switchSink")
    public  OperationResult switchSink(@RequestBody Map<String, ? extends Object> context) {
        String sinkId = (String) context.get("sinkId");

        try {
           
            Sink sinkInfo = iSinkService.getOne(new QueryWrapper<Sink>().eq("sink_id", sinkId));
            
            String isEnable = sinkInfo.getEnable();
            sinkInfo.setEnable("0".equals(isEnable) ? "1" : "0");
            iSinkService.saveOrUpdate(sinkInfo);
            Node flowFromQ = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", sinkInfo.getFlowFrom()));
            
            messagebusUtil.publishAndCacheForNodeView(flowFromQ);

            return OperationResult.buildSuccessResult();
        } catch (Exception e) {
        	logger.error(e.getMessage(), module);
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

    private static boolean judgeSameKindOfCommunicateType(String fromCommunicateType, String toCommunicateType) {
        int fromCmutTypeCode = getCommunicateTypeCode(fromCommunicateType);
        int toCmutTypeCode = getCommunicateTypeCode(toCommunicateType);

        if (fromCmutTypeCode == -1 || toCmutTypeCode == -1) return false;

        return fromCmutTypeCode == toCmutTypeCode;
    }

    private static int getCommunicateTypeCode(String communicateType) {
        switch (communicateType) {
            case "produce":
            case "consume":
            case "produce-consume":
                return 0;

            case "publish":
            case "subscribe":
            case "publish-subscribe":
                return 1;

            case "request":
            case "response":
            case "request-response":
                return 2;

            case "rpcrequest":
            case "rpcresponse":
            case "rpcrequest-rpcresponse":
                return 3;

        }

        return -1;
    }

    @RequestMapping("list")
    public OperationResult list(@RequestBody Map<String, ? extends Object> context) {

        try {

            int limit = Integer.parseInt(context.get("limit").toString());

            int offset = Integer.parseInt(context.get("offset").toString());

            Object flowFrom = context.get("flowFrom");
            Object flowTo = context.get("flowTo");

            String enable = (String) context.get("enable");
            String auditTypeCode = (String) context.get("auditTypeCode");


            PageHelper.startPage(offset, limit);

            QueryWrapper<Sink> query = new QueryWrapper<Sink>();
            Map<String,Object> param = new HashMap<String,Object>();
            if(flowFrom != null){
                param.put("flow_from", flowFrom.toString());
            }
            if(flowTo != null){
                param.put("flow_to", flowTo.toString());
            }
            param.put("audit_type_code", auditTypeCode);
            param.put("enable", enable);

            query.allEq(param,false);


            List<Sink> sinks = iSinkService.list(query);

            for (Sink s : sinks){
                if(StringUtils.hasText(s.getFlowFrom())){
                    Node n1 = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id",s.getFlowFrom()));
                    s.setFlowFromName(n1.getName());
                }

                if(StringUtils.hasText(s.getFlowTo())){
                    Node n2 = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id",s.getFlowTo()));
                    s.setFlowToName(n2.getName());
                }
            }


            PageInfo<Sink> pageInfo= new PageInfo<Sink>(sinks);

            PageHelper.clearPage();

            return OperationResult.buildSuccessResult(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage(), module);
            Map<String, Object> result = new HashMap<String,Object>();
            result.put("results", Collections.emptyList());
            return OperationResult.buildFailureResult(e.getMessage(), result);
        }
    }
}
