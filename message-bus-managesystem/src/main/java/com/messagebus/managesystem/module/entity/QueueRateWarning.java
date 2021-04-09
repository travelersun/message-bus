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

public class QueueRateWarning implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("WARNING_ID")
    private String warningId;

    @TableField("NODE_ID")
    private String nodeId;

    @TableField("RATE_LIMIT")
    private String rateLimit;

    @TableField("REAL_RATE")
    private String realRate;

    @TableField("FROM_DATE")
    private Date fromDate;

    @TableField("LAST_UPDATED_STAMP")
    private Date lastUpdatedStamp;

    @TableField("LAST_UPDATED_TX_STAMP")
    private Date lastUpdatedTxStamp;

    @TableField("CREATED_STAMP")
    private Date createdStamp;

    @TableField("CREATED_TX_STAMP")
    private Date createdTxStamp;

	public String getWarningId() {
		return warningId;
	}

	public void setWarningId(String warningId) {
		this.warningId = warningId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getRateLimit() {
		return rateLimit;
	}

	public void setRateLimit(String rateLimit) {
		this.rateLimit = rateLimit;
	}

	public String getRealRate() {
		return realRate;
	}

	public void setRealRate(String realRate) {
		this.realRate = realRate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
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
