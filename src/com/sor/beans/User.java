package com.sor.beans;

import com.google.gson.annotations.SerializedName;


public class User {

	@SerializedName("authenticated")
	private boolean authenticated;
	
	@SerializedName("username")
	private String username;
	
	@SerializedName("password")
	private String password;
	
	@SerializedName("projectID")
	private int projectID;
	
	@SerializedName("permissionLevel")
	private int permissionLevel;
	
	@SerializedName("orgID")
	private int orgID;
	
	@SerializedName("uuid")
	private String uuid;
	
	@SerializedName("projName")
	private String projName;

	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}

	/**
	 * @param authenticated the authenticated to set
	 */
	public void setAuthenticated(boolean authenticated) {
		this.authenticated = authenticated;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the projectID
	 */
	public int getProjectID() {
		return projectID;
	}

	/**
	 * @param projectID the projectID to set
	 */
	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	/**
	 * @return the permissionLevel
	 */
	public int getPermissionLevel() {
		return permissionLevel;
	}

	/**
	 * @param permissionLevel the permissionLevel to set
	 */
	public void setPermissionLevel(int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	/**
	 * @return the orgID
	 */
	public int getOrgID() {
		return orgID;
	}

	/**
	 * @param orgID the orgID to set
	 */
	public void setOrgID(int orgID) {
		this.orgID = orgID;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the projName
	 */
	public String getProjName() {
		return projName;
	}

	/**
	 * @param projName the projName to set
	 */
	public void setProjName(String projName) {
		this.projName = projName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	
	
}
