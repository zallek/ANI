package com.ani.db.entity;

import java.io.Serializable;

public class NetLog implements Serializable {
	private static final long serialVersionUID = -8796224245268857368L;

	private long timeStamp;
	private String appName;
	private String destination;
	private int messageSize;

	public NetLog() {
		this.timeStamp = System.currentTimeMillis();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long time) {
		this.timeStamp = time;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String application) {
		this.appName = application;
	}

	public int getMessageSize() {
		return messageSize;
	}

	public void setMessageSize(int weight) {
		this.messageSize = weight;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

}
