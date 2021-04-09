package com.messagebus.httpbridge.listener;

import javax.servlet.ServletContext;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.messagebus.client.MessagebusPool;
import com.messagebus.httpbridge.config.MessageBusConfig;
import com.messagebus.httpbridge.util.Constants;

@Component
public class AppInitListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 将 ApplicationContext 转化为 WebApplicationContext 
        WebApplicationContext webApplicationContext = 
            (WebApplicationContext)contextRefreshedEvent.getApplicationContext();
        
        MessageBusConfig messageBusConfig = webApplicationContext.getBean(MessageBusConfig.class);
        // 从 webApplicationContext 中获取  servletContext 
        ServletContext servletContext = webApplicationContext.getServletContext();
        
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(messageBusConfig.getMaxtotal());
        MessagebusPool messagebusPool = new MessagebusPool(messageBusConfig.getPubsuberHost(), poolConfig);
        
        // servletContext设置值
        servletContext.setAttribute(Constants.KEY_OF_MESSAGEBUS_POOL_OBJ, messagebusPool);
    }
}