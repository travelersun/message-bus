package com.messagebus.interactor.pubsub;

import java.io.Serializable;

public interface IDataConverter {
    <T> byte[] serialize(Serializable var1);

    <T> byte[] serialize(Serializable var1, Class<T> var2);

    <T> T deSerializeObject(byte[] var1, Class<T> var2);

    <T> T[] deSerializeArray(byte[] var1, Class<T[]> var2);
}