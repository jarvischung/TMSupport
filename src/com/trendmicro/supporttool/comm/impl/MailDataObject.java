package com.trendmicro.supporttool.comm.impl;

import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class MailDataObject {
	private String mailAddress = "";
	private String supportMailAddress = "";
	private String mailServerAddress = "smtp.gmail.com";
	private int mailServerPort = 587;
	private boolean validate = true;
	
	private String username = "test";
	private String password = "test";

	public MailDataObject() {
	}

	public void setMailAddress(String address) {
		this.mailAddress = address;
	}

	public String getMailAddress() {
		return this.mailAddress;
	}

	public void setMailServerAddress(String serverAddress) {
		this.mailServerAddress = serverAddress;
	}

	public String getMailServerAddress() {
		return this.mailServerAddress;
	}

	public void setMailServerPort(int port) {
		this.mailServerPort = port;
	}

	public int getMailServerPort() {
		return this.mailServerPort;
	}
}
