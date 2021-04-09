package com.messagebus.managesystem.service;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.messagebus.managesystem.module.entity.App;
import com.messagebus.managesystem.module.service.IAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.messagebus.managesystem.module.OperationResult;
import com.messagebus.managesystem.module.entity.Node;
import com.messagebus.managesystem.module.service.INodeService;

/**
 * Created by yanghua on 3/10/15.
 */
@RestController
@RequestMapping("/messagebusmanage/node")
public class NodeService {

    public static final String module        = NodeService.class.getName();
    public static final String resourceError = "BanyanUiLabels";
    Logger logger = LoggerFactory.getLogger(NodeService.class);
    
    @Autowired
    @Qualifier("nodeServiceImpl")
    INodeService iNodeService;

    @Autowired
    @Qualifier("appServiceImpl")
    IAppService iAppService;

    @RequestMapping("updateNode")
    public  OperationResult updateNode(@RequestBody Map<String, ? extends Object> context) {
        String nodeName = (String) context.get("name");
        String nodeId = (String) context.get("nodeId");

        try {
            Node node = iNodeService.getOne(new QueryWrapper<Node>().eq("node_id", nodeId));
            String oldName = node.getName();
            String oldVal = node.getValue();
            String oldRoutingKey = node.getRoutingKey();

            node.setName(nodeName);
            node.setValue(oldVal.replace(oldName, nodeName));
            node.setRoutingKey(oldRoutingKey.replace(oldName, nodeName));
            iNodeService.saveOrUpdate(node);
            
            
            Map<String, Object> result = new HashMap<String,Object>();
            result.put("nodeId", nodeId);

            return OperationResult.buildSuccessResult(result);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

    @RequestMapping("deleteNode")
    public  OperationResult deleteNode(@RequestBody Map<String, ? extends Object> context) {
        String nodeId = (String) context.get("nodeId");

       
        iNodeService.remove(new QueryWrapper<Node>().eq("node_id", nodeId));

        return OperationResult.buildSuccessResult();
    }
    
    @RequestMapping("list")
    public OperationResult list(@RequestBody Map<String, ? extends Object> context) {
    	
        try {

            int limit = Integer.parseInt(context.get("limit").toString());

            int offset = Integer.parseInt(context.get("offset").toString());
        	String appId = (String) context.get("appId");
            String auditTypeCode = (String) context.get("auditTypeCode");
            String communicateType = (String) context.get("communicateType");
            String creator = (String) context.get("creator");
            String isVirtual = (String) context.get("isVirtual");

            String type = (String) context.get("type");

            String queueName = (String) context.get("name");

            String dateBegin = (String) context.get("dateBegin");
            String dateEnd = (String) context.get("dateEnd");

            String sortBy = (String) context.get("sortBy");

            String sortOrder = (String) context.get("sortOrder");

        	PageHelper.startPage(offset, limit);

            QueryWrapper<Node> query = new QueryWrapper<Node>();
            Map<String,Object> param = new HashMap<String,Object>();
            if(StringUtils.hasText(type)){
                param.put("type", type);
            }
            param.put("app_id", appId);
            param.put("audit_type_code", auditTypeCode);
            param.put("communicate_type", communicateType);
            param.put("creator", creator);
            param.put("is_virtual", isVirtual);
            query.allEq(param,false);
            if(StringUtils.hasText(queueName)){
                query.like("name",queueName);
            }
            if(StringUtils.hasText(dateBegin)){
                query.gt("created_stamp",dateBegin);
            }

            if(StringUtils.hasText(dateEnd)){
                query.lt("created_stamp",dateEnd);
            }
            if(StringUtils.hasText(sortBy)){
                query.orderBy(true,"ascending".equals(sortOrder),sortBy);
            }

            List<Node> queues = iNodeService.list(query);

            for (Node n : queues){
                if(StringUtils.hasText(n.getAppId())){
                    App app = iAppService.getOne(new QueryWrapper<App>().eq("app_id",n.getAppId()));
                    n.setAppName(app.getName());
                }
                // n.setSecret(null);
            }
            
            PageInfo<Node>  pageInfo= new PageInfo<>(queues);
            
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
