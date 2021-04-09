package com.messagebus.managesystem.config;

import java.util.concurrent.ConcurrentHashMap;


public class UtilCache {
	
	private static ConcurrentHashMap<String,Object> cache = new ConcurrentHashMap<String,Object>();
	
	public static ConcurrentHashMap<String,Object> findCache(){
		
		return cache;
		
	}

}
