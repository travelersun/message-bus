package com.messagebus.managesystem.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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

public class SinkAuditHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("SINK_AUDIT_ID")
    private String sinkAuditId;

    @TableField("SINK_ID")
    private String sinkId;

    @TableField("AUDIT_TYPE_CODE")
    private String auditTypeCode;

    @TableField("AUDITOR")
    private String auditor;

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

	public String getSinkAuditId() {
		return sinkAuditId;
	}

	public void setSinkAuditId(String sinkAuditId) {
		this.sinkAuditId = sinkAuditId;
	}

	public String getSinkId() {
		return sinkId;
	}

	public void setSinkId(String sinkId) {
		this.sinkId = sinkId;
	}

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
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
    
    


}
