package com.messagebus.common;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanghua on 4/1/15.
 */
public class GsonUtil {
	
	private static Gson filterNullGson;
    private static Gson nullableGson;
    static {
        nullableGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .create();
        filterNullGson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                .create();
    }

    /**
     * 根据对象返回json   不过滤空值字段
     */
    public static String toJsonWtihNullField(Object obj){
        return nullableGson.toJson(obj);
    }

    /**
     * 根据对象返回json  过滤空值字段
     */
    public static String toJsonFilterNullField(Object obj){
        return filterNullGson.toJson(obj);
    }

    /**
     * 将json转化为对应的实体对象
     * new TypeToken<HashMap<String, Object>>(){}.getType()
     */
    public static <T>  T fromJson(String json, Type type){
        return nullableGson.fromJson(json, type);
    }

    /**
     * 将对象值赋值给目标对象
     * @param source 源对象
     * @param <T> 目标对象类型
     * @return 目标对象实例
     */
    public static <T> T convert(Object source, Class<T> clz){
        String json = GsonUtil.toJsonFilterNullField(source);
        return GsonUtil.fromJson(json, clz);
    }

    /**
     * convert json-object string to map
     *
     * @param jsonObjStr the string representation of json-object
     * @return the map object
     */
    public static Map jsonStrToMap(String jsonObjStr) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonObjStr, Map.class);
    }

    public static List jsonStrToList(String jsonObjStr) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Object.class, new NaturalDeserializer());
        Gson gson = gsonBuilder.create();

        return gson.fromJson(jsonObjStr, List.class);
    }

    /**
     * inner static class : implement JsonDeserializer interface
     * which overrides the default implementation
     */
    private static class NaturalDeserializer implements JsonDeserializer<Object> {

        @Override
        public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement.isJsonNull()) return null;
            else if (jsonElement.isJsonPrimitive()) return handlePrimitive(jsonElement.getAsJsonPrimitive());
            else if (jsonElement.isJsonArray()) return handleArray(jsonElement.getAsJsonArray(), context);
            else return handleObject(jsonElement.getAsJsonObject(), context);
        }

        private Object handlePrimitive(JsonPrimitive json) {
            if (json.isBoolean())
                return json.getAsBoolean();
            else if (json.isString())
                return json.getAsString();
            else {
                BigDecimal bigDec = json.getAsBigDecimal();
                // Find out if it is an int type
                try {
                    bigDec.toBigIntegerExact();
                    try {
                        return bigDec.intValueExact();
                    } catch (ArithmeticException e) {
                    }
                    return bigDec.longValue();
                } catch (ArithmeticException e) {
                }
                // Just return it as a double
                return bigDec.doubleValue();
            }
        }

        private Object handleArray(JsonArray json, JsonDeserializationContext context) {
            Object[] array = new Object[json.size()];
            for (int i = 0; i < array.length; i++)
                array[i] = context.deserialize(json.get(i), Object.class);
            return array;
        }

        private Object handleObject(JsonObject json, JsonDeserializationContext context) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet())
                map.put(entry.getKey(), context.deserialize(entry.getValue(), Object.class));
            return map;
        }
    }

}
