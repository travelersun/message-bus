package com.messagebus.client;

import com.google.common.eventbus.EventBus;
import com.messagebus.common.ExceptionHelper;
import com.rabbitmq.client.Connection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yanghua on 3/5/15.
 */
class MessagebusFactory implements PooledObjectFactory<Messagebus> {

    private static final Log logger = LogFactory.getLog(MessagebusFactory.class.getName());

    private ConfigManager configManager;
    private Connection    connection;
    private EventBus      componentEventBus;

    private final Method openMethod;
    private final Method closeMethod;

    public MessagebusFactory(ConfigManager configManager,
                             Connection connection,
                             EventBus componentEventBus) {
        this.configManager = configManager;
        this.connection = connection;
        this.componentEventBus = componentEventBus;

        try {
            openMethod = Messagebus.class.getSuperclass().getDeclaredMethod("open");
            openMethod.setAccessible(true);
            closeMethod = Messagebus.class.getSuperclass().getDeclaredMethod("close");
            closeMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PooledObject<Messagebus> makeObject() throws Exception {
        Constructor<Messagebus> privateCtor = Messagebus.class.getDeclaredConstructor();
        privateCtor.setAccessible(true);
        Messagebus client = privateCtor.newInstance();
        privateCtor.setAccessible(false);

        Class<?> superClient = Messagebus.class.getSuperclass();

        //set private field
        Field configManagerField = superClient.getDeclaredField("configManager");
        configManagerField.setAccessible(true);
        configManagerField.set(client, this.configManager);
        configManagerField.setAccessible(false);

        Field connectionField = superClient.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(client, this.connection);
        connectionField.setAccessible(false);

        Field componentEventBus = superClient.getDeclaredField("componentEventBus");
        componentEventBus.setAccessible(true);
        componentEventBus.set(client, this.componentEventBus);
        componentEventBus.setAccessible(false);

        openMethod.invoke(client);

        return new DefaultPooledObject<Messagebus>(client);
    }

    @Override
    public void destroyObject(PooledObject<Messagebus> pooledObject) throws Exception {
        Messagebus client = pooledObject.getObject();
        if (client != null) {
            if (client.isOpen()) {
                closeMethod.invoke(client);
            }
        }
    }

    @Override
    public boolean validateObject(PooledObject<Messagebus> pooledObject) {
        Messagebus client = pooledObject.getObject();
        if (client != null && !client.isOpen()) {
            try {
                openMethod.invoke(client);
            } catch (IllegalAccessException e) {
                ExceptionHelper.logException(logger, e, "validateObject");
            } catch (InvocationTargetException e) {
                ExceptionHelper.logException(logger, e, "validateObject");
            }
        }

        return client != null && client.isOpen();
    }

    @Override
    public void activateObject(PooledObject<Messagebus> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<Messagebus> pooledObject) throws Exception {

    }
}
