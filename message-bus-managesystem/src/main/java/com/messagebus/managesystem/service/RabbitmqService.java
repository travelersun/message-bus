package com.messagebus.managesystem.service;

import com.messagebus.client.MessageResponseTimeoutException;
import com.messagebus.client.Messagebus;
import com.messagebus.client.MessagebusPool;
import com.messagebus.client.message.model.Message;
import com.messagebus.client.message.model.MessageFactory;
import com.messagebus.managesystem.common.Constants;
import com.messagebus.managesystem.config.UtilCache;
import com.messagebus.managesystem.module.OperationResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by yanghua on 4/17/15.
 */

@Component
public class RabbitmqService {

    public static final String module = RabbitmqService.class.getName();
    
    Logger logger = LoggerFactory.getLogger(RabbitmqService.class);
    
    @Value("${messagebus.sink.serverInfoRequestResponse.token:masdjfqiowieqooeirfajhfihfweld}")
    String token ;
    
    @Value("${messagebus.queue.serverInfoRequest.secret:miuhqihusahdfuhaksjhfuiqweka}")
    String secret ;

    public  OperationResult getRabbitmqServerOverview(Map<String, ? extends Object> context) {
    	ConcurrentHashMap<String,Object> poolUtilCache = UtilCache.findCache();
        MessagebusPool pool = (MessagebusPool) poolUtilCache.get(Constants.KEY_OF_MESSAGEBUS_POOL);
        logger.info("is pool null : " + (pool == null), module);
       
        Messagebus client = pool.getResource();
        Message requestMsg = MessageFactory.createMessage();
        Message respMsg = null;
        String jsonStr;
        try {
            respMsg = client.request(secret, "serverInfoResponse", requestMsg, token, Constants.REQUEST_DEFAULT_TIMEOUT);
            jsonStr = new String(respMsg.getContent());
            logger.info(jsonStr, module);
            
            Map<String, Object> resultMap = new HashMap<String,Object>();
            resultMap.put("result", jsonStr);

            return OperationResult.buildSuccessResult(resultMap);
        } catch (MessageResponseTimeoutException e) {
        	logger.error("occured message response timeout exception", module);
            Map<String, Object> resultMap = new HashMap<String,Object>();
            resultMap.put("result", "");

            return OperationResult.buildFailureResult(e.getMessage(),resultMap);
        } catch (Exception e) {
        	logger.error(e.getMessage(), module);
            Map<String, Object> resultMap = new HashMap<String,Object>();
            resultMap.put("result", "");

            return OperationResult.buildFailureResult(e.getMessage(),resultMap);
        } finally {
            pool.returnResource(client);
        }
    }

}
