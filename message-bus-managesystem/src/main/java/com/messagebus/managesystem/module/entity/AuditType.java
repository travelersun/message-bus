package com.messagebus.managesystem.module.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Maya
 * @since 2021-02-21
 */

public class AuditType implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("AUDIT_TYPE_CODE")
    private String auditTypeCode;

    @TableField("AUDIT_TYPE_NAME")
    private String auditTypeName;

    @TableField("LAST_UPDATED_STAMP")
    private Date lastUpdatedStamp;

    @TableField("LAST_UPDATED_TX_STAMP")
    private Date lastUpdatedTxStamp;

    @TableField("CREATED_STAMP")
    private Date createdStamp;

    @TableField("CREATED_TX_STAMP")
    private Date createdTxStamp;

	public String getAuditTypeCode() {
		return auditTypeCode;
	}

	public void setAuditTypeCode(String auditTypeCode) {
		this.auditTypeCode = auditTypeCode;
	}

	public String getAuditTypeName() {
		return auditTypeName;
	}

	public void setAuditTypeName(String auditTypeName) {
		this.auditTypeName = auditTypeName;
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
