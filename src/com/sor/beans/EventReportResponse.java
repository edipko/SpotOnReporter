package com.sor.beans;

import com.google.gson.annotations.SerializedName;

public class EventReportResponse {

	@SerializedName("status")
	private String status;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
}
