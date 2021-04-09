package com.messagebus.managesystem.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */

public class Node implements Serializable , Comparable<Node>{

    private static final long serialVersionUID = 1L;

    @TableId(value="NODE_ID")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long nodeId;

    @TableField("SECRET")
    private String secret;

    @TableField("NAME")
    private String name;

    @TableField("VALUE")
    private String value;

    @TableField("PARENT_ID")
    private String parentId;

    @TableField("TYPE")
    private String type;

    @TableField("ROUTER_TYPE")
    private String routerType;

    @TableField("ROUTING_KEY")
    private String routingKey;

    @TableField("AVAILABLE")
    private String available;

    @TableField("IS_INNER")
    private String isInner;

    @TableField("IS_VIRTUAL")
    private String isVirtual;

    @TableField("COMMUNICATE_TYPE")
    private String communicateType;

    @TableField("CREATOR")
    private String creator;

    @TableField("APP_ID")
    private String appId;

    @TableField("RATE_LIMIT")
    private String rateLimit;

    @TableField("THRESHOLD")
    private String threshold;

    @TableField("MSG_BODY_SIZE")
    private String msgBodySize;

    @TableField("TTL")
    private String ttl;

    @TableField("TTL_PER_MSG")
    private String ttlPerMsg;

    @TableField("AUDIT_TYPE_CODE")
    private String auditTypeCode;

    @TableField("CAN_BROADCAST")
    private String canBroadcast;

    @TableField("FROM_DATE")
    private Date fromDate;

    @TableField("THRU_DATE")
    private Date thruDate;

    @TableField("LAST_UPDATED_STAMP")
    private Date lastUpdatedStamp;

    @TableField("LAST_UPDATED_TX_STAMP")
    private Date lastUpdatedTxStamp;

    @TableField("CREATED_STAMP")
    private Date createdStamp;

    @TableField("CREATED_TX_STAMP")
    private Date createdTxStamp;

	@TableField(exist = false)
    private String appName;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getNodeId() {
		return nodeId;
	}

	public void setNodeId(Long nodeId) {
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
			return this.nodeId < o.getNodeId() ? -1 : 1;
		}
	}  
    


}
