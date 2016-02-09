package edu.unc.ceccr.chembench.utilities;

import edu.unc.ceccr.chembench.global.Constants;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ParseConfigurationXML {
    private static final Logger logger = Logger.getLogger(ParseConfigurationXML.class.getName());

    public static void initializeConstants(String filePath) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList listOfAdmins = doc.getElementsByTagName("admin");
            logger.info("Admin(s) found: " + listOfAdmins.getLength());
            for (int s = 0; s < listOfAdmins.getLength(); s++) {
                Node adminNode = listOfAdmins.item(s);
                if (adminNode.getNodeType() == Node.ELEMENT_NODE) {
                    String userName = getNestedNodeValue((Element) adminNode, "name");
                    if (userName.length() > 0) {
                        Constants.ADMIN_LIST.add(userName);
                        logger.info("Added admin: " + userName);
                    }
                    String email = getNestedNodeValue((Element) adminNode, "email");
                    if (SendEmails.isValidEmail(email)) {
                        Constants.ADMINEMAIL_LIST.add(email);
                        logger.info("Added admin email: " + email);
                    }
                }
            }
            NodeList listOfDescriptorAccessUsers = doc.getElementsByTagName("descriptorUser");
            for (int s = 0; s < listOfDescriptorAccessUsers.getLength(); s++) {
                Node descriptorUserNode = listOfDescriptorAccessUsers.item(s);
                if (descriptorUserNode.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList userChildNodes = descriptorUserNode.getChildNodes();
                    String userName = (userChildNodes.item(0)).getNodeValue();
                    if (userName != null && userName.trim().length() > 0) {
                        Constants.DESCRIPTOR_DOWNLOAD_USERS_LIST.add(userName);
                    }
                }
            }

            Constants.WORKBENCH = getSingNodeValue(doc, "workbench");
            Constants.LSFJOBPATH = getSingNodeValue(doc, "lsfPath");
            Constants.USERWORKFLOWSPATH = getSingNodeValue(doc, "userWorkflow");
            Constants.INSTALLS_PATH = getSingNodeValue(doc, "installsPath");
            Constants.CECCR_DATABASE_NAME = getNestedNodeValue(getParentNode(doc, "database"), "databaseName");
            Constants.DATABASE_USERNAME = getNestedNodeValue(getParentNode(doc, "database"), "userName");
            Constants.CECCR_DATABASE_PASSWORD = getNestedNodeValue(getParentNode(doc, "database"), "password");
            Constants.DATABASE_URL = getNestedNodeValue(getParentNode(doc, "database"), "url");
            Constants.DATABASE_DRIVER = getNestedNodeValue(getParentNode(doc, "database"), "driver");
            Constants.WEBADDRESS = getNestedNodeValue(getParentNode(doc, "website"), "address");
            Constants.WEBSITEEMAIL = getNestedNodeValue(getParentNode(doc, "website"), "email");
            Constants.RECAPTCHA_PUBLICKEY = getNestedNodeValue(getParentNode(doc, "webService"), "publicKey");
            Constants.RECAPTCHA_PRIVATEKEY = getNestedNodeValue(getParentNode(doc, "webService"), "privateKey");
            Constants.MOLCONNZ_MODELING_DATFILE_PATH =
                    getNestedNodeValue(getParentNode(doc, "molconnz"), "modelingDatFilePath");
            Constants.MOLCONNZ_PREDICTION_DATFILE_PATH =
                    getNestedNodeValue(getParentNode(doc, "molconnz"), "predictionDatFilePath");
            Constants.MOLCONNZ_CSV_DATFILE_PATH = getNestedNodeValue(getParentNode(doc, "molconnz"), "csvDatFilePath");
            Constants.CDK_XMLFILE_PATH = getNestedNodeValue(getParentNode(doc, "cdk"), "xmlFilePath");
            Constants.RF_BUILD_MODEL_RSCRIPT =
                    getNestedNodeValue(getParentNode(doc, "randomForest"), "buildModelRScript");
            Constants.RF_PREDICT_RSCRIPT = getNestedNodeValue(getParentNode(doc, "randomForest"), "predictRScript");
            Constants.RF_DESCRIPTORS_USED_FILE =
                    getNestedNodeValue(getParentNode(doc, "randomForest"), "descriptorsUsedFile");
            Constants.KNN_OUTPUT_FILE = getNestedNodeValue(getParentNode(doc, "knn"), "outputFile");
            Constants.EXTERNAL_VALIDATION_OUTPUT_FILE =
                    getNestedNodeValue(getParentNode(doc, "other"), "externalValidationOutputFile");
            Constants.PRED_OUTPUT_FILE = getNestedNodeValue(getParentNode(doc, "other"), "predOutputFile");
            Constants.KNN_DEFAULT_FILENAME = getNestedNodeValue(getParentNode(doc, "knn"), "defaultFileName");
            Constants.KNN_CATEGORY_DEFAULT_FILENAME =
                    getNestedNodeValue(getParentNode(doc, "knn"), "defaultCategoryFileName");
            Constants.SE_DEFAULT_FILENAME = getNestedNodeValue(getParentNode(doc, "other"), "seDefaultFileName");
            Constants.DESCRIPTOR_ERROR_FILE = getNestedNodeValue(getParentNode(doc, "other"), "descriptorErrorFile");
            Constants.KNNPLUS_MODELS_FILENAME = getNestedNodeValue(getParentNode(doc, "knn"), "modelsFileName");
            Constants.EXTERNAL_SET_A_FILE = getNestedNodeValue(getParentNode(doc, "setFiles"), "externalA");
            Constants.EXTERNAL_SET_X_FILE = getNestedNodeValue(getParentNode(doc, "setFiles"), "externalX");
            Constants.MODELING_SET_A_FILE = getNestedNodeValue(getParentNode(doc, "setFiles"), "modelingA");
            Constants.MODELING_SET_X_FILE = getNestedNodeValue(getParentNode(doc, "setFiles"), "modelingX");
            Constants.IMAGE_FILEPATH = getNestedNodeValue(getParentNode(doc, "other"), "imgPath");
            Constants.CECCR_BASE_PATH = getSingNodeValue(doc, "systemBasePath");
            if (!Constants.CECCR_BASE_PATH.endsWith("/")) {
                Constants.CECCR_BASE_PATH += "/";
            }

            Constants.CECCR_USER_BASE_PATH = getSingNodeValue(doc, "userFilesPath");
            Constants.SCRIPTS_PATH = getSingNodeValue(doc, "scriptsPath");
            Constants.SYSTEMCONFIG_XML_PATH = getSingNodeValue(doc, "sysConfigPath");

            Constants.TOMCAT_PATH = getSingNodeValue(doc, "tomcatPath");
            if (!Constants.TOMCAT_PATH.endsWith("/")) {
                Constants.TOMCAT_PATH += "/";
            }

            Constants.EXECUTABLEFILE_PATH = getSingNodeValue(doc, "executablesPath");
            if (!Constants.EXECUTABLEFILE_PATH.endsWith("/")) {
                Constants.EXECUTABLEFILE_PATH += "/";
            }

            Constants.BUILD_DATE_FILE_PATH =
                    Constants.TOMCAT_PATH + getNestedNodeValue(getParentNode(doc, "other"), "buildDateFile");
            //FIXME:Does not exist! Uknown purpose
            Constants.XML_FILE_PATH = Constants.CECCR_BASE_PATH + "xml-files/";
            Constants.doneReadingConfigFile = true;
        } catch (SAXParseException err) {

            logger.error("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            logger.error(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            logger.error(x);
            ((x == null) ? e : x).printStackTrace();
        } catch (Throwable t) {
            String debugStr = ("Exception in ParseConfigurationXML.java:" + t.getMessage());
            FileAndDirOperations.writeStringToFile(debugStr, Constants.USERWORKFLOWSPATH + "javadebug.txt");
        }
    }

    public static String getSingNodeValue(Document doc, String item) {
        try {
            String value = (doc.getElementsByTagName(item)).item(0).getFirstChild().getTextContent().trim();
            return value;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getNestedNodeValue(Element parent, String childItem) {
        try {
            String value =
                    (parent.getElementsByTagName(childItem).item(0)).getChildNodes().item(0).getNodeValue().trim();
            return value;
        } catch (Exception ex) {
            return "";
        }
    }

    public static Element getParentNode(Document doc, String item) {
        Node node = doc.getElementsByTagName(item).item(0);
        return (Element) node;
    }

}
