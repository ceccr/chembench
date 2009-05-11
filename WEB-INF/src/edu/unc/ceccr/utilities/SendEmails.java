package edu.unc.ceccr.utilities;

import java.util.Date;
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

	public static void sendEmail(User userInfo,EmailToAllBean  formBean)throws Exception
	{
		Properties props=System.getProperties();
		props.put(Constants.MAILHOST,Constants.MAILSERVER);
		javax.mail.Session session=javax.mail.Session.getInstance(props,null);
		Message message=new MimeMessage(session);
		message.setFrom(new InternetAddress(Constants.WEBSITEEMAIL));
		message.addRecipient(Message.RecipientType.TO,new InternetAddress(userInfo.getEmail()));
		
		if(formBean.getCc()!=null&&isValidEmail(formBean.getCc()))
		{message.addRecipient(Message.RecipientType.CC, new InternetAddress(formBean.getCc()));}
		if(formBean.getBcc()!=null&&isValidEmail(formBean.getBcc()))
		{message.addRecipient(Message.RecipientType.BCC,new InternetAddress(formBean.getBcc()));}
		
		message.setSubject(formBean.getSubject());
		String HtmlBody="Hi,"+userInfo.getFirstName()+",<br/>"+ formBean.getContent()+
		"<br/><br/><br/><br/>"+ new Date();
		
		message.setContent(HtmlBody, "text/html");
		Transport.send(message);
		
	}
	
}