package com.messagebus.client.event.carry;

import com.google.common.eventbus.Subscribe;
import com.messagebus.client.ConfigManager;
import com.messagebus.client.MessageContext;
import com.messagebus.common.Constants;
import com.rabbitmq.tools.jsonrpc.JsonRpcClient;
import com.rabbitmq.tools.jsonrpc.JsonRpcException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by yanghua on 6/29/15.
 */
public class RpcRequestEventProcessor extends CommonEventProcessor {

    private static final Log logger = LogFactory.getLog(RpcRequestEventProcessor.class);

    public RpcRequestEventProcessor() {
    }

    @Subscribe
    public void onPermissionCheck(PermissionCheckEvent event) {
        super.exceptionCheck(event);
        logger.debug("=-=-=- event : onPermissionCheck =-=-=-");
        MessageContext       context       = event.getMessageContext();
        ConfigManager.Source source        = context.getSource();
        ConfigManager.Sink   sink          = context.getSink();
        boolean              hasPermission = true;
        /*
        boolean              hasPermission = false;
        hasPermission = context.getStream() != null
                && context.getStream().getSinkSecret().equals(sink.getSecret())
                && context.getStream().getSourceName().equals(source.getName());
		*/
        if (!hasPermission) {
            logger.error("[handle] can not produce message from queue [" + source.getName() +
                    "] to queue [" + sink.getName() + "]");
            event.getMessageContext().setThrowable(new RuntimeException("can not produce message from queue [" + source.getName() +
                    "] to queue [" + sink.getName() + "]"));
        }
    }

    @Subscribe
    public void onRpcRequest(RpcRequestEvent event) {
        super.exceptionCheck(event);
        logger.debug("=-=-=- event : onRpcRequest =-=-=-");
        MessageContext context = event.getMessageContext();
        JsonRpcClient  client  = null;
        try {
            client = new JsonRpcClient(context.getChannel(),
                    Constants.PROXY_EXCHANGE_NAME,
                    context.getSink().getRoutingKey(),
                    (int) context.getTimeout());
            Object[] params = null;
            if (context.getOtherParams().get("params") != null) {
                params = (Object[]) context.getOtherParams().get("params");
            }
            Object respObj = client.call(context.getOtherParams().get("methodName").toString(), params);
            context.getOtherParams().put("result", respObj);
        } catch (IOException e) {
            logger.error(e);
            event.getMessageContext().setThrowable(new RuntimeException(e));
        } catch (TimeoutException e) {
            context.setIsTimeout(true);
        } catch (JsonRpcException e) {
            logger.error(e);
            event.getMessageContext().setThrowable(new RuntimeException(e));
        } finally {
            try {
                if (client != null) client.close();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }

    @Override
    public void process(MessageContext msgContext) {
        throw new UnsupportedOperationException("this method should be implemented in consume event processor!");
    }

    public static class PermissionCheckEvent extends CarryEvent {
    }

    public static class RpcRequestEvent extends CarryEvent {
    }

}
