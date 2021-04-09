package com.messagebus.interactor.pubsub.impl.zookeeper;


import com.messagebus.common.ExceptionHelper;
import com.messagebus.interactor.pubsub.IDataConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ZookeeperDataConverter implements IDataConverter {
    private static final Log logger = LogFactory.getLog(ZookeeperDataConverter.class);

    public ZookeeperDataConverter() {
    }

    public <T> byte[] serialize(Serializable obj) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        byte[] bytes;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            bytes = baos.toByteArray();
        } catch (IOException var13) {
            ExceptionHelper.logException(logger, var13, "serialize");
            throw new RuntimeException(var13);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }

                if (oos != null) {
                    oos.close();
                }
            } catch (IOException var12) {
            }

        }

        return bytes;
    }

    public <T> byte[] serialize(Serializable obj, Class<T> clazz) {
        return this.serialize(obj);
    }

    public <T> T[] deSerializeArray(byte[] originalData, Class<T[]> clazz) {
        Object obj = this.deSerialize(originalData);
        return (T[])((Object[])obj);
    }

    public <T> T deSerializeObject(byte[] originalData, Class<T> clazz) {
        if (originalData != null && originalData.length != 0) {
            if (clazz.equals(String.class)) {
                String tmp = new String(originalData, Charset.defaultCharset());
                return (T) tmp;
            } else {
                Object obj = this.deSerialize(originalData);
                return (T) obj;
            }
        } else {
            try {
                return clazz.newInstance();
            } catch (InstantiationException var4) {
                ExceptionHelper.logException(logger, var4, "");
                throw new RuntimeException(var4);
            } catch (IllegalAccessException var5) {
                ExceptionHelper.logException(logger, var5, "");
                throw new RuntimeException(var5);
            }
        }
    }

    private Object deSerialize(byte[] originalData) {
        if (originalData == null) {
            return null;
        } else {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;
            Object obj = null;

            try {
                bais = new ByteArrayInputStream(originalData);
                ois = new ObjectInputStream(bais);
                obj = null;

                try {
                    obj = ois.readObject();
                } catch (ClassNotFoundException var14) {
                    logger.error("[download] occurs a ClassNotFoundException : " + var14.getMessage());
                    throw new RuntimeException(var14);
                }
            } catch (IOException var15) {
                logger.error("occurs a IOException : " + var15.toString());
                throw new RuntimeException(var15.toString());
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }

                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException var13) {
                    logger.error("occurs a IOException : " + var13.toString());
                }

            }

            return obj;
        }
    }
}
