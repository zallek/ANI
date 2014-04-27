package com.ani.db.object;

import com.ani.utils.StaticANI;

public class Traffic {
	private String appName;
	private float ascendingTraffic;
	private float descendingTraffic;

	public Traffic(String appName, float ascendingTraffic,
			float descendingTraffic) {
		super();
		this.appName = appName;
		this.ascendingTraffic = ascendingTraffic;
		this.descendingTraffic = descendingTraffic;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public float getAscendingTraffic() {
		return ascendingTraffic;
	}

	public void setAscendingTraffic(int ascendingTraffic) {
		this.ascendingTraffic = ascendingTraffic;
	}

	public float getDescendingTraffic() {
		return descendingTraffic;
	}

	public void setDescendingTraffic(int descendingTraffic) {
		this.descendingTraffic = descendingTraffic;
	}

	@Override
	public String toString() {
		return "Traffic [appName=" + appName + ", ascendingTraffic="
				+ ascendingTraffic + ", descendingTraffic=" + descendingTraffic
				+ "]";
	}

	public void calculateRate() {
		ascendingTraffic = getCurrentUploadTraffic();
		descendingTraffic = getCurrentDownloadTraffic();
	}

	public void calculateRate(long interval) {
		ascendingTraffic = getCurrentUploadTraffic(interval);
		descendingTraffic = getCurrentDownloadTraffic(interval);
	}

	/**
	 * 
	 * @param interval
	 *            : time interval
	 * @return : int : download rate in the wished interval
	 */
	public float getCurrentDownloadTraffic(long interval) {// TODO change to
															// service context
		/*
		 * return
		 * ((ANIApplication.getApplicationInstance().getDataBaseInterface()
		 * .getDownloadRate(interval, System.currentTimeMillis(),
		 * appName)*1000)/interval);
		 */
		return 0;
	}

	/**
	 * 
	 * @return : int : download rate in the base interval
	 */
	public float getCurrentDownloadTraffic() {
		return getCurrentDownloadTraffic(StaticANI.baseInterval);
	}

	/**
	 * 
	 * @param interval
	 *            : time interval
	 * @return : int : upload rate in the wished interval
	 */
	public float getCurrentUploadTraffic() {// TODO change to service context
		return getCurrentUploadTraffic(StaticANI.baseInterval);
	}

	/**
	 * 
	 * @param interval
	 *            : time interval
	 * @return : int : upload rate in the wished interval
	 */
	public float getCurrentUploadTraffic(long interval) {// TODO change to
															// service context
		/*
		 * return
		 * (ANIApplication.getApplicationInstance().getDataBaseInterface()
		 * .getUploadRate(interval, System.currentTimeMillis(),
		 * appName)*1000/interval);
		 */
		return 0;
	}
}
