package com.messagebus.managesystem.event;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yanghua on 4/7/15.
 */
public class SinkEvent {

    public static final String module = SinkEvent.class.getName();

    public static String auditSink(HttpServletRequest request, HttpServletResponse response) {
    	return "success";
    }

    public static String switchSink(HttpServletRequest request, HttpServletResponse response) {
    	return "success";
    }
}
