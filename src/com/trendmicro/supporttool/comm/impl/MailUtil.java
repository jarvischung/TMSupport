package com.trendmicro.supporttool.comm.impl;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import android.util.Log;

public class MailUtil {
	private final static String TAG = "MailUtil";
	private String mailAddress = "";
	private String supportMailAddress = "";
	private String mailServerAddress = "smtp.gmail.com";
	private int mailServerPort = 587;
	private boolean validate = true;

	private String username = "twmobile.sp";
	private String password = "19830823";

	MailDataObject mailDataObj;

	private Session session;

	public MailUtil() {
		//mailDataObj = new MailDataObject();
		
		//For test
		sendToServer(getZipFileList());
	}

	public Properties getProperties() {
		Properties props = new Properties();
		props.put("mail.smtp.auth", this.validate ? "true" : "false");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", this.mailServerAddress);
		props.put("mail.smtp.port", this.mailServerPort);
		
		/*props.setProperty("mail.imap.host", "imap.gmail.com");
		props.setProperty("mail.imap.port", "993");
		props.setProperty("mail.imap.connectiontimeout", "5000");
		props.setProperty("mail.imap.timeout", "5000");*/
		
		return props;
	}

	public boolean sendToServer(File fileList[]) {
		try {
			//Auth account
			session = Session.getInstance(getProperties(),
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(username, password);
						}
				});
			
			//URLName urlName = new URLName("imap://@gmail.com:MYPASSWORD@imap.gmail.com");
			//Store store = session.getStore(urlName);
			
			//File fileList[] = getZipFileList();
			Message mailMessage = new MimeMessage(session);
			mailMessage.setFrom(new InternetAddress("twmobile.sp@gmail.com"));
			mailMessage.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("twmobile.sp@gmail.com"));
			mailMessage.setSubject("Support");
			//mailMessage.setText("Dear,\n\nThis is a support email and attached related file.");
			
			MimeMultipart messageBodyPart = new MimeMultipart("mixed");
			MimeBodyPart text = new MimeBodyPart();
			text.setText("Dear,\n\nThis is a support email and attached related file.");
			messageBodyPart.addBodyPart(text);
			
			for(int i=0;i<fileList.length;i++){
				MimeBodyPart attachPartFile = createAttachment(
						fileList[i].getPath());
				messageBodyPart.addBodyPart(attachPartFile);
			}
			mailMessage.setContent(messageBodyPart);
			
			mailMessage.saveChanges();
			Transport.send(mailMessage);
			deleteZipFile(fileList);
			Log.d(TAG, "Mail send.....");
			
			return true;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private File[] getZipFileList(){
		File file = new File("/data/data/com.trendmicro.supporttool/files/zip/");
		File tmp[] = file.listFiles();
		
		for(int i=0;i<tmp.length;i++){
			Log.d(TAG, "Zip file list:" + tmp[i].getPath());
		}
		return tmp;
	}
	
	/*private File[] getCrashZipFileList(){
		File file = new File("/data/data/com.trendmicro.supporttool/files/zip/crash/");
		File tmp[] = file.listFiles();
		
		for(int i=0;i<tmp.length;i++){
			Log.d(TAG, "Zip file list:" + tmp[i].getPath());
		}
		return tmp;
	}*/
	
	private void deleteZipFile(File[] file){
		for(int i=0;i<file.length;i++){
			file[i].delete();
		}
	}

	private static MimeBodyPart createAttachment(String filename){
		MimeBodyPart attachPart = new MimeBodyPart();
		
		try {
			FileDataSource fds = new FileDataSource(filename);
			attachPart.setDataHandler(new DataHandler(fds));
			attachPart.setFileName(fds.getName());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return attachPart;
	}

}
