package edu.unc.ceccr.utilities;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import edu.unc.ceccr.formbean.EmailToAllBean;
import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.User;

public class SendEmails {

	public static boolean isValidEmail(String email) {
		return (email.indexOf("@") > 0) && (email.indexOf(".") > 2);
	}

	public static void sendEmail(String address, String cc, String bcc, String subject, String message){
		try{
			Utility.writeToDebug("Sending an email...");
			
			//gonna exec sendmail, gonna pack it up nice

			String workingDir = Constants.CECCR_USER_BASE_PATH + "EMAILS/";
			File wdFile = new File(workingDir);
			wdFile.mkdirs();
			
			//create email file
			Date t = new Date();
			String fileName = t.toString().replace(" ", "_");
			fileName += ".txt";
			
			FileWriter fw = new FileWriter(new File(workingDir + fileName));
			fw.write("Subject: " + subject + "\n");
			fw.write("To: " + address + "\n");
			fw.write("From: " + "ceccr@listserv.unc.edu" + "\n\n");
			fw.write(message);

			fw.close();
			
			String execstr = "sendmail -t < " + fileName;
			Utility.writeToDebug("Running external program: " + execstr);
		    Process p = Runtime.getRuntime().exec(execstr, null, wdFile);
		    p.waitFor();
			
			/*
			Here's what we do:
			//Create the header of the mail text in this case the file is called "x.txt":
			From: research@unc.edu
			To: fishback@email.unc.edu
			test

			Send the mail by the following command line:
			$ sendmail -t < x.txt

			So to shell script this:
			cat > x.txt <<EOFmail
			Subject: Emerald /largefs file(s) will be removed in two days for $i
			To: $i@email.unc.edu
			From: research@unc.edu
			 Hey man!
			EOFmail

			 
			sendmail -t < x.txt
		*/
			
			

		
			Utility.writeToDebug("Email sent!");
		}catch(Exception ex){
			Utility.writeToDebug(ex);
		}	
		/*
		//The old way
		Properties props=System.getProperties();
		props.put(Constants.MAILHOST,Constants.MAILSERVER);
		javax.mail.Session session=javax.mail.Session.getInstance(props,null);
		Message message=new MimeMessage(session);
		message.setFrom(new InternetAddress(Constants.WEBSITEEMAIL));
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(userInfo.getEmail()));
		message.setSubject("Sorry,"+userInfo.getFirstName());
		String HtmlBody="message goes here";
		
		message.setContent(HtmlBody, "text/html");
		Transport.send(message);
		*/
		
	}
	
	public static void sendEmailToAdmins(String subject, String message){
		Iterator it=Constants.ADMINEMAIL_LIST.iterator();
		while(it.hasNext())
		{
			String adminAddress = (String)it.next();
			sendEmail(adminAddress, "", "", subject, message);
		}
	}
}