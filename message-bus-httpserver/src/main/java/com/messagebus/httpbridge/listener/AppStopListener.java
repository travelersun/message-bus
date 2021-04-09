package com.messagebus.httpbridge.listener;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.messagebus.client.MessagebusPool;
import com.messagebus.httpbridge.util.Constants;

@Component
public class AppStopListener implements ApplicationListener<ContextStoppedEvent> {
    @Override
    public void onApplicationEvent(ContextStoppedEvent contextStoppedEvent) {
        // 将 ApplicationContext 转化为 WebApplicationContext 
        WebApplicationContext webApplicationContext = 
            (WebApplicationContext)contextStoppedEvent.getApplicationContext();
        // 从 webApplicationContext 中获取  servletContext 
        ServletContext servletContext = webApplicationContext.getServletContext();
        
        MessagebusPool messagebusPool = (MessagebusPool)servletContext.getAttribute(Constants.KEY_OF_MESSAGEBUS_POOL_OBJ);
        if (messagebusPool != null) {
            messagebusPool.destroy();
        }
        
    }
}
