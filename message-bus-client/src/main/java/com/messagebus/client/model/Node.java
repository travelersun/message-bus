package com.messagebus.client.model;

import java.io.Serializable;
import java.util.Date;


public class Node implements Serializable , Comparable<Node>{

    private static final long serialVersionUID = 1L;

    
    private String nodeId;

    
    private String secret;

    
    private String name;

    
    private String value;

    
    private String parentId;

    
    private String type;

    
    private String routerType;

    
    private String routingKey;

    
    private String available;

    
    private String isInner;

    
    private String isVirtual;

    
    private String communicateType;

    
    private String creator;

    
    private String appId;

    
    private String rateLimit;

    
    private String threshold;

    
    private String msgBodySize;

    
    private String ttl;

    
    private String ttlPerMsg;

    
    private String auditTypeCode;

    
    private String canBroadcast;

    
    private Date fromDate;

    
    private Date thruDate;

    
    private Date lastUpdatedStamp;

    
    private Date lastUpdatedTxStamp;

    
    private Date createdStamp;

    
    private Date createdTxStamp;

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRouterType() {
		return routerType;
	}

	public void setRouterType(String routerType) {
		this.routerType = routerType;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getIsInner() {
		return isInner;
	}

	public void setIsInner(String isInner) {
		this.isInner = isInner;
	}

	public String getIsVirtual() {
		return isVirtual;
	}

	public void setIsVirtual(String isVirtual) {
		this.isVirtual = isVirtual;
	}

	public String getCommunicateType() {
		return communicateType;
	}

	public void setCommunicateType(String communicateType) {
		this.communicateType = communicateType;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(String rateLimit) {
		this.rateLimit = rateLimit;
	}

	public String getThreshold() {
		return threshold;
	}

	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}

	public String getMsgBodySize() {
		return msgBodySize;
	}

	public void setMsgBodySize(String msgBodySize) {
		this.msgBodySize = msgBodySize;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	public String getTtlPerMsg() {
		return ttlPerMsg;
	}

	public void setTtlPerMsg(String ttlPerMsg) {
		this.ttlPerMsg = ttlPerMsg;
	}

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	public String getCanBroadcast() {
		return canBroadcast;
	}

	public void setCanBroadcast(String canBroadcast) {
		this.canBroadcast = canBroadcast;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getThruDate() {
		return thruDate;
	}

	public void setThruDate(Date thruDate) {
		this.thruDate = thruDate;
	}

	public Date getLastUpdatedStamp() {
		return lastUpdatedStamp;
	}

	public void setLastUpdatedStamp(Date lastUpdatedStamp) {
		this.lastUpdatedStamp = lastUpdatedStamp;
	}

	public Date getLastUpdatedTxStamp() {
		return lastUpdatedTxStamp;
	}

	public void setLastUpdatedTxStamp(Date lastUpdatedTxStamp) {
		this.lastUpdatedTxStamp = lastUpdatedTxStamp;
	}

	public Date getCreatedStamp() {
		return createdStamp;
	}

	public void setCreatedStamp(Date createdStamp) {
		this.createdStamp = createdStamp;
	}

	public Date getCreatedTxStamp() {
		return createdTxStamp;
	}

	public void setCreatedTxStamp(Date createdTxStamp) {
		this.createdTxStamp = createdTxStamp;
	}
	
	public int compareTo(Node o) {
		if (o == null) {
			return -1;
		} else if (this.nodeId.equals(o.getNodeId())) {
			return 0;
		} else {
			return Integer.parseInt(this.nodeId) < Integer.parseInt(o.getNodeId()) ? -1 : 1;
		}
	}  
}