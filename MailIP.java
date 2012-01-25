/**
		Mail my IP
	Copyright © Marco Sero
*/

import javax.mail.*;
import java.util.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.*;
import java.net.*;

public class MailIP {
	
	final static String HOST = "smtp.hostname.com"; 	// SMTP ADDRESS
	final static String FROM = "mail@hostname.com"; 	// MAIL SENDER
	final static String TO = "mailto@gmail.com"; 		// MAIL RECEIVER
	final static String USER = "username"; 				// USERNAME
	final static String PASS = "password"; 				// PASSWORD
	final static int REFRESH_MINUTES = 60;				// REFRESH EVERY _ MINUTES
	final static boolean SSL = true;					// SSL ENABLED
	
	final static boolean DEBUG = false;					// DEBUG ENABLED
	
	
	public static void sendMail(String newIP) {
		try {
			//initialize the StringBuffer object within the try/catch loop
			StringBuffer sb = new StringBuffer( );

			//Get system properties
			Properties props = System.getProperties( );

			//Setup mail server
			props.put("mail.smtp.host", HOST);
			props.put("mail.debug", "" + DEBUG);
			props.put("mail.smtp.auth","true");

			//Get session
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(DEBUG);
			session.setPasswordAuthentication(new URLName("smtp", HOST, 25, "INBOX", USER, PASS), new javax.mail.PasswordAuthentication(USER, PASS));

			//Define message
			MimeMessage msg = new MimeMessage(session);
			//Set the from address
			msg.setFrom(new InternetAddress(FROM));
			//Set the to address
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
			//Set the subject
			msg.setSubject("Mail my IP");
			//Set the text content for body
			sb.append("Your public IP is changed!\n\n");
			sb.append("This is your new public IP: " + newIP + "\n\n");
			msg.setText(sb.toString( ));  
			
			//Send message
			Transport tr = session.getTransport("smtps");
			if(!SSL)
				tr = session.getTransport("smtp");
				
			tr.connect(HOST, USER, PASS);
			msg.saveChanges(); // don't forget this
			tr.sendMessage(msg, msg.getAllRecipients());
			tr.close();
		}
		catch (MessagingException err) {
			err.printStackTrace();
		}
	}

	public static void main(String[] args) {
			
		boolean fileExist = true;
		boolean done = false;
		
		while(!done) {	
			try {			
				try {
					URL url = new java.net.URL("http://automation.whatismyip.com/n09230945.asp");
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					InputStream inStream = conn.getInputStream();
					InputStreamReader isr = new InputStreamReader(inStream);
					BufferedReader br = new BufferedReader(isr);
				
					String newIP = br.readLine();				
				
					if(fileExist) {
						BufferedReader in = new BufferedReader(new FileReader("MailIP.txt"));
						if(!newIP.equals(in.readLine())) {
							// delete old ip
							File oldFile = new File("MailIP.txt");
							oldFile.delete();
							// write new ip
							PrintWriter out = new PrintWriter(new FileWriter("MailIP.txt"), true);
							out.println(newIP);
							fileExist = true;
							sendMail(newIP);
							System.out.println("\n\nYour public IP is changed! The new is: " + newIP + "\n");
							System.out.println("I sent a mail to " + TO + " to notify the change!\n\n");
						}
					}
					else {
						// write ip
						PrintWriter out = new PrintWriter(new FileWriter("MailIP.txt"), true);
						out.println(newIP);
						fileExist = true;
						sendMail(newIP);
						System.out.println("\n\nYour public IP is changed! The new is: " + newIP + "\n");
						System.out.println("I sent a mail to " + TO + " to notify the change!\n\n");
					}

					Thread.sleep(REFRESH_MINUTES * 1000);
				
					} catch(FileNotFoundException err) {
						fileExist = false;
					} catch(IOException err) {
						err.printStackTrace();
						// wait to reconnect
						System.out.println("\n\nMaybe there was an error on your internet connection. Please wait while reconnecting...");
						Thread.sleep(30000);
					}
				} catch(InterruptedException err) {
					err.printStackTrace();
					done = true;	
					System.out.println("\n\n\nTHERE WAS AN ERROR!\nProgram will end!\n");
				}

			}
		}
	}