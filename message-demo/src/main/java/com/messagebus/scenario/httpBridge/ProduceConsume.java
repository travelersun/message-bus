package com.messagebus.scenario.httpBridge;


import com.messagebus.client.message.model.Message;
import com.messagebus.client.message.model.MessageFactory;
import com.messagebus.client.message.model.MessageJSONSerializer;
import com.messagebus.common.Constants;
import com.messagebus.scenario.util.PropertiesHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProduceConsume {

    private static final Log logger = LogFactory.getLog(ProduceConsume.class);

    private static String testQueue = "oa-consume";
    private static String secret = "8R4hpZ2t1pC4AOOw0KLc";
    private static String token = "8R4hpZ2t1pC4AOOw0KLc";

    public static void main(String[] args) {
        testProduceWithPost();

        // testProduceWithGet();

        testConsume("pull");
    }

    private static void testProduceWithPost() {
        String testUrlFormat = "http://%s:%s/messagebus/queues/%s/messages?secret=%s&apiType=produce&token=%s";
        // String testQueue = "appDataQueue";
        // String secret = "iojawdnaisdflknoiankjfdblaidcas";
        // String token = "iojawdnaisdflknoiankjfdblaidcas";

        String url = String.format(testUrlFormat,
                                   PropertiesHelper.getPropertyValue("messagebus.httpbridge.host"),
                                   Integer.parseInt(PropertiesHelper.getPropertyValue("messagebus.httpbridge.port")),
                                   testQueue,
                                   secret,
                                   token);

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;

        
        Message msg = MessageFactory.createMessage();
        msg.setReplyTo(testQueue);
        msg.setContentType("text/plain");

        msg.setContent("test".getBytes(Constants.CHARSET_OF_UTF8));

        List<Message> msgs = new ArrayList<Message>(1);
        msgs.add(msg);
        String msgs2json = MessageJSONSerializer.serializeMessages(msgs);
        
        try {
            HttpPost postRequest = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("messages", msgs2json));
            
            postRequest.setEntity(new UrlEncodedFormEntity(nvps));

            response = httpClient.execute(postRequest);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                System.out.println("response is : " + EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            logger.error("[syncHTTPGet] occurs a IOException : " + e.getMessage());
        }
    }

    private static void testProduceWithGet() {
        String testUrlFormat = "http://%s:%s/messagebus/queues/%s/messages?secret=%s&apiType=produce&token=%s&callback=callback";
        // String testQueue = "appDataQueue";
        // String secret = "iojawdnaisdflknoiankjfdblaidcas";
        // String token = "iojawdnaisdflknoiankjfdblaidcas";

        String url = String.format(testUrlFormat,
                                   PropertiesHelper.getPropertyValue("messagebus.httpbridge.host"),
                                   Integer.parseInt(PropertiesHelper.getPropertyValue("messagebus.httpbridge.port")),
                                   testQueue,
                                   secret,
                                   token);
        url += "&content=text&contentType=text/plain";

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;

        try {
            response = httpClient.execute(new HttpGet(url));

            HttpEntity entity = response.getEntity();
            if (entity != null) {
            	System.out.println("response is : " + EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            logger.error("[syncHTTPGet] occurs a IOException : " + e.getMessage());
        }
    }

    private static void testConsume(String mode) {
        String testUrlFormat = "http://%s:%s/messagebus/queues/messages?secret=%s&apiType=consume&mode=%s&num=1";
        String secret1 = "9LC68n4M3eejFuV2488a";

        String url = String.format(testUrlFormat,
                                   PropertiesHelper.getPropertyValue("messagebus.httpbridge.host"),
                                   Integer.parseInt(PropertiesHelper.getPropertyValue("messagebus.httpbridge.port")),
                                   secret1,
                                   mode);

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = null;

        try {
            response = httpClient.execute(new HttpGet(url));

            HttpEntity entity = response.getEntity();
            if (entity != null) {
            	System.out.println("response is : " + EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            logger.error("[syncHTTPGet] occurs a IOException : " + e.getMessage());
        }
    }

}
