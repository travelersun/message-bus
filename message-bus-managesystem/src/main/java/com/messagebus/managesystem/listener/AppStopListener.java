package com.messagebus.managesystem.listener;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.messagebus.client.MessagebusPool;
import com.messagebus.interactor.pubsub.PubsuberManager;
import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.config.UtilCache;

@Component
public class AppStopListener implements ApplicationListener<ContextStoppedEvent> {
	
	Logger logger = LoggerFactory.getLogger(AppStopListener.class);
	
    @Override
    public void onApplicationEvent(ContextStoppedEvent contextStoppedEvent) {
        // 将 ApplicationContext 转化为 WebApplicationContext 
        WebApplicationContext webApplicationContext = 
            (WebApplicationContext)contextStoppedEvent.getApplicationContext();
        
        logger.info("destroying messagebus pool...");
        MessagebusPool messagebusPool = (MessagebusPool)UtilCache.findCache().get(Constants.KEY_OF_MESSAGEBUS_POOL);
        if (messagebusPool != null) {
            messagebusPool.destroy();
        }
        
        MessagebusPool innerMessagebusPool = (MessagebusPool)UtilCache.findCache().get(Constants.KEY_OF_MESSAGEBUS_INNER_POOL);
        
        logger.info("messagebus server is stopping...");
        if (innerMessagebusPool != null) innerMessagebusPool.destroy();
        
        PubsuberManager pubsuberManager = (PubsuberManager) UtilCache.findCache().get(Constants.KEY_OF_MESSAGEBUS_PUBSUBER_MANAGER);

        logger.info("publishing server started event ...");
        pubsuberManager.publish(com.messagebus.common.Constants.PUBSUB_SERVER_STATE_CHANNEL,
                                com.messagebus.common.Constants.MESSAGEBUS_SERVER_EVENT_STOPPED.getBytes(), true);

        UtilCache.findCache().clear();
        logger.info("messagebus server stopped.");
        
    }
}