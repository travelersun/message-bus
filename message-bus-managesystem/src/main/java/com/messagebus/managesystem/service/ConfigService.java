package com.messagebus.managesystem.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.messagebus.common.Constants;
import com.messagebus.managesystem.common.MessagebusUtil;
import com.messagebus.managesystem.module.OperationResult;
import com.messagebus.managesystem.module.entity.Config;
import com.messagebus.managesystem.module.service.IConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by yanghua on 3/10/15.
 */
@RestController
@RequestMapping("/messagebusmanage/maintain/config")
public class ConfigService {

    public static final String module = ConfigService.class.getName();
    
    Logger logger = LoggerFactory.getLogger(ConfigService.class);
    
    @Autowired
    @Qualifier("configServiceImpl")
    IConfigService iConfigService; 
    
    @Autowired
    @Qualifier("messagebusUtil")
    MessagebusUtil messagebusUtil;

    @RequestMapping("createConfigItem")
    public  OperationResult createConfigItem(@RequestBody Map<String, ? extends Object> context) {
        String itemKey = (String) context.get("itemKey");
        String itemValue = (String) context.get("itemValue");
        String itemType = (String) context.get("type");
        
        Config item = new Config();
        item.setItemKey(itemKey);
        item.setItemValue(itemValue);
        item.setType(itemType);

        try {
            iConfigService.saveOrUpdate(item);
            messagebusUtil.publishEvent(Constants.PUBSUB_CONFIG_CHANNEL, itemValue, true);
            return OperationResult.buildSuccessResult();
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

    @RequestMapping("updateConfigItem")
    public  OperationResult updateConfigItem(@RequestBody Map<String, ? extends Object> context) {
        String configId = (String) context.get("configId");
        String itemKey = (String) context.get("itemKey");
        String itemValue = (String) context.get("itemValue");
        String type = (String) context.get("type");

        try {
            Config oldConfigItem = iConfigService.getOne(new QueryWrapper<Config>().eq("config_id", configId));
            oldConfigItem.setItemKey(itemKey);
            oldConfigItem.setItemValue(itemValue);
            oldConfigItem.setType(type);
            iConfigService.saveOrUpdate(oldConfigItem);
            
            messagebusUtil.publishEvent(Constants.PUBSUB_CONFIG_CHANNEL, itemValue, true);
            return OperationResult.buildSuccessResult();
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

    @RequestMapping("removeConfigItem")
    public  OperationResult removeConfigItem(@RequestBody Map<String, ? extends Object> context) {
        String configId = (String) context.get("configId");

        try {
        	
            boolean c = iConfigService.remove(new QueryWrapper<Config>().eq("config_id", configId));
            
            return OperationResult.buildSuccessResult(c);
        } catch (Exception e) {
        	logger.error(e.getMessage());
            return OperationResult.buildFailureResult(e.getMessage());
        }
    }

}
