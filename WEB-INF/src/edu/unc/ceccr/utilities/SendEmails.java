package edu.unc.ceccr.utilities;

import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.User;

public class SendEmails {

    public static boolean isValidEmail(String email) {
        return (email.indexOf("@") > 0) && (email.indexOf(".") > 2);
    }
    
    public static void sendJobCompletedEmail(Job j) throws Exception{
        Session s = HibernateUtil.getSession();
        User user = PopulateDataObjects.getUserByUserName(j.getUserName(), s);
        String subject = "Chembench Job Completed: " + j.getJobName();
        String message = user.getFirstName() + ","
            +"<br /> Your " + j.getJobType().toLowerCase() + " job, '" + j.getJobName() + "', is finished." 
            +"<br /> Please log in to check the results!";
        
        sendEmail(user.getEmail(), "", "", subject, message);
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
            fw.write("From: " + Constants.WEBSITEEMAIL + "\n");
            fw.write("MIME-Version: 1.0\n");
            fw.write("Content-Type: " + "text/html" + "\n\n");

            fw.write(message);
            
            fw.close();
            
            String execstr = "sendmail.sh " + workingDir + fileName;
            RunExternalProgram.runCommand(execstr, workingDir);
        
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