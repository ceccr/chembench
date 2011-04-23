package edu.unc.ceccr.utilities;

import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.unc.ceccr.global.Constants;

public class ParseConfigurationXML{

    public static void initializeConstants( String filePath){
    try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(filePath));
            doc.getDocumentElement ().normalize ();

            NodeList listOfAdmins = doc.getElementsByTagName("admin");
            for(int s=0; s<listOfAdmins.getLength() ; s++){
                Node adminNode = listOfAdmins.item(s);
                if(adminNode.getNodeType() == Node.ELEMENT_NODE){
                	String userName=getNestedNodeValue((Element)adminNode,"name");
                	if(userName.length()>0){
                        Constants.ADMIN_LIST.add(userName);
                	}
                    String email=getNestedNodeValue((Element)adminNode,"email");
                    if(email.length()>0&&SendEmails.isValidEmail(email)){
                    	Constants.ADMINEMAIL_LIST.add(email);
                    }
                }
            }
            
            
            NodeList listOfDescriptorAccessUsers = doc.getElementsByTagName("descriptorUser");
            for(int s=0; s<listOfDescriptorAccessUsers.getLength() ; s++){
                Node descriptorUserNode = listOfDescriptorAccessUsers.item(s);
                if(descriptorUserNode.getNodeType() == Node.ELEMENT_NODE){
                	NodeList userChildNodes = descriptorUserNode.getChildNodes();
                	String userName= (userChildNodes.item(0)).getNodeValue();
                	if(userName != null && userName.trim().length()>0){
                        Constants.DESCRIPTOR_DOWNLOAD_USERS_LIST.add(userName);
                	}
                }
            }
            
            Constants.WORKBENCH=getSingNodeValue(doc,"workbench");
            Constants.CECCR_DATABASE_NAME=getNestedNodeValue(getParentNode(doc,"database"),"databaseName");
            Constants.DATABASE_USERNAME=getNestedNodeValue(getParentNode(doc,"database"),"userName");
            Constants.CECCR_DATABASE_PASSWORD=getNestedNodeValue(getParentNode(doc,"database"),"password");
            Constants.DATABASE_URL=getNestedNodeValue(getParentNode(doc,"database"),"url");
            Constants.DATABASE_DRIVER=getNestedNodeValue(getParentNode(doc,"database"),"driver");
            Constants.WEBADDRESS=getNestedNodeValue(getParentNode(doc,"website"),"address");
            Constants.WEBSITEEMAIL=getNestedNodeValue(getParentNode(doc,"website"),"email");
            Constants.RECAPTCHA_PUBLICKEY=getNestedNodeValue(getParentNode(doc,"webService"),"publicKey");
            Constants.RECAPTCHA_PRIVATEKEY=getNestedNodeValue(getParentNode(doc,"webService"),"privateKey");
            
            Constants.CECCR_BASE_PATH=getSingNodeValue(doc,"systemBasePath");
            if(! Constants.CECCR_BASE_PATH.endsWith("/")){
            	Constants.CECCR_BASE_PATH += "/";
            }

            Constants.CECCR_USER_BASE_PATH=getSingNodeValue(doc,"userFilesPath");
            
            Constants.TOMCAT_PATH=getSingNodeValue(doc,"tomcatPath");
            if(! Constants.TOMCAT_PATH.endsWith("/")){
            	Constants.TOMCAT_PATH += "/";
            }
            
            Constants.EXECUTABLEFILE_PATH=getSingNodeValue(doc,"executablesPath");
            if(! Constants.EXECUTABLEFILE_PATH.endsWith("/")){
            	Constants.EXECUTABLEFILE_PATH += "/";
            }
            
            Constants.BUILD_DATE_FILE_PATH = Constants.TOMCAT_PATH + "webapps/ROOT/WEB-INF/buildDate.txt";
    		Constants.XML_FILE_PATH = Constants.CECCR_BASE_PATH +"xml-files/";
            Constants.isCustomized=true;
            
        }catch (SAXParseException err)
        {
        	Utility.writeToDebug("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        	Utility.writeToDebug(" " + err.getMessage ());

        }catch (SAXException e)
        {
        	Exception x = e.getException ();
        	Utility.writeToDebug(x);
            ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) 
        {
        	Utility.writeToDebug("Exception in ParseConfigurationXML.java");
        	t.printStackTrace ();
        }
    }
    
    
    public static String getSingNodeValue(Document doc,String item)
    {
    	try{
	    	String value=(doc.getElementsByTagName(item)).item(0).getFirstChild().getTextContent().trim();
	    	
	    	return value;
    	}
    	catch(Exception ex){
    		return "";
    	}
    }
    
    public static String getNestedNodeValue(Element parent,String childItem)
    {
    	try{
    		String value=(parent.getElementsByTagName(childItem).item(0)).getChildNodes().item(0).getNodeValue().trim();
    		return value;
    	}
    	catch(Exception ex){
    		return "";
    	}
    }
    
    public static Element getParentNode(Document doc,String item)
    {
    	Node node=doc.getElementsByTagName(item).item(0);
    	return (Element)node;
    }

}