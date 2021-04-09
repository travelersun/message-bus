package com.messagebus.managesystem.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */

public class Sink implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SINK_ID")
	@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
    private Long sinkId;

    @TableField("TOKEN")
    private String token;

    @TableField("FLOW_FROM")
    private String flowFrom;

	@TableField(exist = false)
	private String flowFromName;

    @TableField("FROM_COMMUNICATE_TYPE")
    private String fromCommunicateType;

    @TableField("FLOW_TO")
    private String flowTo;

	@TableField(exist = false)
	private String flowToName;

    @TableField("TO_COMMUNICATE_TYPE")
    private String toCommunicateType;

    @TableField("ENABLE")
    private String enable;

    @TableField("CREATOR")
    private String creator;

    @TableField("AUDIT_TYPE_CODE")
    private String auditTypeCode;

    @TableField("LAST_UPDATED_STAMP")
    private Date lastUpdatedStamp;

    @TableField("LAST_UPDATED_TX_STAMP")
    private Date lastUpdatedTxStamp;

    @TableField("CREATED_STAMP")
    private Date createdStamp;

    @TableField("CREATED_TX_STAMP")
    private Date createdTxStamp;

	public String getFlowFromName() {
		return flowFromName;
	}

	public void setFlowFromName(String flowFromName) {
		this.flowFromName = flowFromName;
	}

	public String getFlowToName() {
		return flowToName;
	}

	public void setFlowToName(String flowToName) {
		this.flowToName = flowToName;
	}

	public Long getSinkId() {
		return sinkId;
	}

	public void setSinkId(Long sinkId) {
		this.sinkId = sinkId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getFlowFrom() {
		return flowFrom;
	}

	public void setFlowFrom(String flowFrom) {
		this.flowFrom = flowFrom;
	}

	public String getFromCommunicateType() {
		return fromCommunicateType;
	}

	public void setFromCommunicateType(String fromCommunicateType) {
		this.fromCommunicateType = fromCommunicateType;
	}

	public String getFlowTo() {
		return flowTo;
	}

	public void setFlowTo(String flowTo) {
		this.flowTo = flowTo;
	}

	public String getToCommunicateType() {
		return toCommunicateType;
	}

	public void setToCommunicateType(String toCommunicateType) {
		this.toCommunicateType = toCommunicateType;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
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
    
    


}
