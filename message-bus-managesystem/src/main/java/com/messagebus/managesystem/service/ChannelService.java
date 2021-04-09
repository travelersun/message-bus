package com.messagebus.managesystem.service;



import java.util.Map;

import com.messagebus.managesystem.common.Constants;

/**
 * Created by yanghua on 3/20/15.
 */

public class ChannelService {

    public static final String module = ChannelService.class.getName();
    /*
    public static Map<String, Object> filterPublisherList(Map<String, ? extends Object> context) {
        Map inputFields = (Map) context.get("inputFields");
        inputFields.put("available", "1");
        inputFields.put("isInner", "0");
        inputFields.put("type", "1");
        inputFields.put("auditTypeCode_op", "notEqual");
        inputFields.put("auditTypeCode", Constants.CODE_OF_AUDIT_TYPE_UNAUDIT);

        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            Map<String, Object> resultCtx = dispatcher.runSync("performFind", context);
            return resultCtx;
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }

    public static Map<String, Object> filterSubscribeList(DispatchContext ctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            Map<String, Object> resultCtx = dispatcher.runSync("performFind", context);
            return resultCtx;
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }
    }
   */
}
