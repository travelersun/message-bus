package com.messagebus.managesystem.listener;

import com.google.common.base.Strings;
import com.messagebus.client.MessagebusPool;
import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.config.MessageBusConfig;
import com.messagebus.managesystem.config.UtilCache;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yanghua on 3/22/15.
 */
@Component
public class MBClientPoolListener implements ApplicationListener<ContextRefreshedEvent> {

    public static String module = MBClientPoolListener.class.getName();
    Logger logger = LoggerFactory.getLogger(MBClientPoolListener.class);
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) { 
    	
    	// 将 ApplicationContext 转化为 WebApplicationContext 
        WebApplicationContext webApplicationContext = 
            (WebApplicationContext)contextRefreshedEvent.getApplicationContext();
        
        MessageBusConfig messageBusConfig = webApplicationContext.getBean(MessageBusConfig.class);
    	
    	logger.info("initing messagebus pool ...", module);
        String pubsuberHost = messageBusConfig.getPubsuberHost();

        if (Strings.isNullOrEmpty(pubsuberHost)) {
            logger.error("missing config item : messagebus.pubsuberHost in config/MessagebusConfig.properties", module);
        }

        int pubsuberPort = messageBusConfig.getPubsuberPort();

        int maxTotal = messageBusConfig.getMaxtotal();

        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        MessagebusPool messagebusPool = new MessagebusPool(pubsuberHost,poolConfig);

        //set instance to cache
        ConcurrentHashMap<String,Object> poolCache = UtilCache.findCache();
        poolCache.put(Constants.KEY_OF_MESSAGEBUS_POOL, messagebusPool);
    }


}
