package com.ani.db.object;

import java.util.Date;

/**
 * Aggregate of several NetLog on APP_NAME or IP
 * 
 * @author Nicolas
 * 
 */
public class NetLogAggregate {
	private NetworkType type;
	private String value; // "192.1.23.56" OR "Facebook"
	private long size; // in bites
	private Date beginDate;
	private Date endDate;

	public NetLogAggregate(NetworkType type, String value, long size,
			Date beginDate, Date endDate) {
		this.type = type;
		this.value = value;
		this.size = size;
		this.beginDate = beginDate;
		this.endDate = endDate;
	}

	public NetworkType getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public long getSize() {
		return size;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

}
