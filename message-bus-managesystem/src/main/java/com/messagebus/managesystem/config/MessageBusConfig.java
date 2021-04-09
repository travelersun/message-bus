package com.messagebus.managesystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageBusConfig {
	
	@Value("${messagebus.pubsuberHost:127.0.0.1}")
    String pubsuberHost;
    
    @Value("${messagebus.pubsuberPort:6379}")
    int pubsuberPort;
    
    @Value("${messagebus.messagebuspool.maxtotal:200}")
    int maxtotal;
    
    @Value("${messagebus.mq.host:127.0.0.1}")
    String mqHost;
    
    

	public String getMqHost() {
		return mqHost;
	}

	public void setMqHost(String mqHost) {
		this.mqHost = mqHost;
	}

	public String getPubsuberHost() {
		return pubsuberHost;
	}

	public void setPubsuberHost(String pubsuberHost) {
		this.pubsuberHost = pubsuberHost;
	}

	public int getPubsuberPort() {
		return pubsuberPort;
	}

	public void setPubsuberPort(int pubsuberPort) {
		this.pubsuberPort = pubsuberPort;
	}

	public int getMaxtotal() {
		return maxtotal;
	}

	public void setMaxtotal(int maxtotal) {
		this.maxtotal = maxtotal;
	}
    
    

}