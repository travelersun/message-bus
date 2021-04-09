//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.messagebus.interactor.pubsub.impl.redis;

import com.google.gson.Gson;
import com.messagebus.common.ExceptionHelper;
import com.messagebus.interactor.pubsub.IDataConverter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RedisDataConverter implements IDataConverter {
    private static final Log logger = LogFactory.getLog(RedisDataConverter.class);
    private static final Gson gson = new Gson();

    public RedisDataConverter() {
    }

    public <T> byte[] serialize(Serializable obj) {
        String tmp;
        if (obj instanceof String) {
            tmp = (String)obj;
        } else if (obj instanceof List) {
            tmp = gson.toJson(obj, List.class);
        } else {
            tmp = gson.toJson(obj);
        }

        System.out.println("serialize obj:"+tmp);

        return tmp.getBytes(Charset.defaultCharset());
    }

    public <T> byte[] serialize(Serializable obj, Class<T> clazz) {
        String tmp = gson.toJson(obj, clazz);
        System.out.println("serialize obj:"+tmp);
        return tmp.getBytes(Charset.defaultCharset());
    }

    public <T> T deSerializeObject(byte[] originalData, Class<T> clazz) {
        if (originalData == null || originalData.length == 0) {
            try {
                return clazz.newInstance();
            } catch (InstantiationException var4) {
                ExceptionHelper.logException(logger, var4, "");
            } catch (IllegalAccessException var5) {
                ExceptionHelper.logException(logger, var5, "");
            }
        }

        String jsonStr = new String(originalData, Charset.defaultCharset());



        System.out.println("deSerializeObject obj:"+clazz.getName()+jsonStr);

        if("java.lang.String".equals(clazz.getName())){
            return (T)jsonStr;
        }



        return gson.fromJson(jsonStr, clazz);
    }

    public <T> T[] deSerializeArray(byte[] originalData, Class<T[]> clazz) {
        String jsonStr = new String(originalData, Charset.defaultCharset());

        System.out.println("deSerializeArray obj:"+jsonStr);

        return (T[])gson.fromJson(jsonStr, clazz);
    }
}
