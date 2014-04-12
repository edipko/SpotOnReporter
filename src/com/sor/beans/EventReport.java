package com.sor.beans;

import java.util.ArrayList;

public class EventReport {

	private int id;
	private String name;
	private String severity;
	private String type;
	private String description;
	private Double lat;
	private Double lon;
	private String disposition;
	private ArrayList<String> filePaths;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the severity
	 */
	public String getSeverity() {
		return severity;
	}
	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the lat
	 */
	public Double getLat() {
		return lat;
	}
	/**
	 * @param lat the lat to set
	 */
	public void setLat(Double lat) {
		this.lat = lat;
	}
	/**
	 * @return the lon
	 */
	public Double getLon() {
		return lon;
	}
	/**
	 * @param lon the lon to set
	 */
	public void setLon(Double lon) {
		this.lon = lon;
	}
	/**
	 * @return the disposition
	 */
	public String getDisposition() {
		return disposition;
	}
	/**
	 * @param disposition the disposition to set
	 */
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}
	/**
	 * @return the filePaths
	 */
	public ArrayList<String> getFilePaths() {
		return filePaths;
	}
	/**
	 * @param filePaths the filePaths to set
	 */
	public void setFilePaths(ArrayList<String> filePaths) {
		this.filePaths = filePaths;
	}

	
	
}
