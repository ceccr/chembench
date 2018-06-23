package edu.unc.ceccr.chembench.actions;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.User;
import edu.unc.ceccr.chembench.persistence.UserRepository;
import edu.unc.ceccr.chembench.utilities.SendEmails;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class UserAction extends ActionSupport implements ServletRequestAware {

    private static final Logger logger = LoggerFactory.getLogger(UserAction.class);
    private static final Pattern NON_ALPHANUMERIC_REGEX = Pattern.compile("[^a-zA-Z0-9]");

    private User user = User.getCurrentUser();
    private String recaptchaPublicKey = Constants.RECAPTCHA_PUBLICKEY;
    private String outputMessage;
    private String newUserName;
    private String address;
    private String city;
    private String country;

    /* HELPER FUNCTIONS */
    private String email;
    private String firstName;
    private String lastName;

    /* END HELPER FUNCTIONS */

    /* DATA OBJECTS, GETTERS, AND SETTERS */
    private String organizationName;
    private String organizationType;
    private String organizationPosition;
    private String phoneNumber;
    private String stateOrProvince;
    private String zipCode;
    // deprecated, but some people think it's still important
    private String workBench;
    /* Variables used in password changes and user options */
    private String oldPassword;
    private String newPassword;
    private String showPublicDatasets;
    private String showPublicPredictors;
    private String viewDatasetCompoundsPerPage;
    private String viewPredictorModels;
    private String viewPredictionCompoundsPerPage;
    private String showAdvancedKnnModeling;
    private boolean userIsAdmin = false;

    private String recaptcha_challenge_field;
    private String recaptcha_response_field;

    private HttpServletRequest request;

    private UserRepository userRepository;

    @Autowired
    public UserAction(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String loadUserRegistration() throws Exception {
        String result = SUCCESS;
        organizationType = "Academia";
        return result;
    }

    public String loadEditProfilePage() throws Exception {
        String result = SUCCESS;
        // check that the user is logged in
        userIsAdmin = user.getIsAdmin().equals(Constants.YES);
        if (user.getUserName().equals("guest")) {
            addActionError("Error: You may not change the guest" + " profile settings.");
            return ERROR;
        }

        // user profile information
        address = user.getAddress();
        city = user.getCity();
        country = user.getCountry();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        organizationName = user.getOrgName();
        organizationType = user.getOrgType();
        organizationPosition = user.getOrgPosition();
        phoneNumber = user.getPhone();
        stateOrProvince = user.getState();
        zipCode = user.getZipCode();
        workBench = user.getWorkbench();

        // user options
        showPublicDatasets = user.getShowPublicDatasets();
        showPublicPredictors = user.getShowPublicPredictors();
        viewDatasetCompoundsPerPage = user.getViewDatasetCompoundsPerPage();
        viewPredictionCompoundsPerPage = user.getViewPredictionCompoundsPerPage();
        showAdvancedKnnModeling = user.getShowAdvancedKnnModeling();

        return result;
    }

    public String registerUser() throws Exception {
        String result = SUCCESS;
        // form validation
        // Validate that each required field has something in it.
        // this function will populate the errorMessages arraylist.
        validateUserInfo();

        if (!getActionErrors().isEmpty()) {
            result = ERROR;
        }

        if (newUserName.isEmpty()) {
            addActionError("Please enter a user name.");
            result = ERROR;
        }

        // Check whether the username already exists
        // (queries database)
        if (!newUserName.equals("") && userExists(newUserName)) {
            addActionError("The user name '" + newUserName + "' is already in use.");
            result = ERROR;
        } else if (NON_ALPHANUMERIC_REGEX.matcher(newUserName).find()) {
            addActionError("You have entered an invalid username. " +
                    "Usernames must be alphanumeric (no spaces, symbols, or special characters).");
            result = ERROR;
        }

        result = checkCaptcha(result);

        if (result.equals(ERROR)) {
            return result;
        }

        // make user
        user = new User();

        user.setUserName(newUserName);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setOrgName(organizationName);
        user.setOrgType(organizationType);
        user.setOrgPosition(organizationPosition);
        user.setPhone(phoneNumber);
        user.setAddress(address);
        user.setState(stateOrProvince);
        user.setCity(city);
        user.setCountry(country);
        user.setZipCode(zipCode);
        user.setWorkbench(workBench); // deprecated, but may come back
        user.setCreationTime(new Date());

        // options
        user.setShowPublicDatasets(Constants.SOME);
        user.setShowPublicPredictors(Constants.ALL);
        user.setViewDatasetCompoundsPerPage(Constants.TWENTYFIVE);
        user.setViewPredictionCompoundsPerPage(Constants.TWENTYFIVE);
        user.setShowAdvancedKnnModeling(Constants.NO);

        // rights
        user.setCanDownloadDescriptors(Constants.NO);
        user.setIsAdmin(Constants.NO);

        // password
        String password = Utility.randomPassword();
        user.setPassword(Utility.encrypt(password));

        user.setStatus("agree");
        userRepository.save(user);

        // user is auto-approved; email them a temp password

        outputMessage =
                "Your account has been created! " + "An email containing your password has been sent to " + email
                        + ". Please check your email and log in to Chembench. "
                        + "Note: Email delivery may be delayed up to 15 " + "minutes depending on email server load.";

        String HtmlBody =
                "Thank you for you interest in CECCR's Chembench." + " <br/>Your account has been approved.<br/>"
                        + "<br/> Your user name : <font color=red>" + user.getUserName() + "</font>"
                        + "<br/> Your temporary password : <font color=red>" + password + "</font>"
                        + "<br/> Please note that passwords are case "
                        + "sensitive.<br/> In order to change your password," + " log in to Chembench at"
                        + " <a href='http://chembench.mml.unc.edu'>" + "http://chembench.mml.unc.edu</a> and click the"
                        + " 'edit profile' link at the upper right." + "<br/>"
                        + "<br/>We hope that you find Chembench to be a "
                        + "useful tool. <br/>If you have any problems or "
                        + "suggestions for improvements, please contact us at" + " : " + Constants.WEBSITEEMAIL
                        + "<br/><br/>Thank you. <br/>The Chembench Team<br/>";

        SendEmails.sendEmail(user.getEmail(), "", "", "Chembench User Registration", HtmlBody);

        logger.debug("just registered! " + newUserName);
        logger.debug("In case email failed, temp password for user: " + user.getUserName() + " is: " + password);
        // if user != null, it will show a "You are logged in" message.
        user = null;
        return result;
    }

    class ReCaptchaResult {
        @SerializedName("success")
        private boolean success;
        @SerializedName("error-codes")
        private List<String> errors;

        public boolean isValid() {
            return success;
        }
    }

    private String checkCaptcha(String result) throws IOException {
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("secret", Constants.RECAPTCHA_PRIVATEKEY));
        params.add(new BasicNameValuePair("response", request.getParameter("g-recaptcha-response")));

        //Execute and get the response.
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://www.google.com/recaptcha/api/siteverify");
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        ReCaptchaResult captchaResult = new ReCaptchaResult();
        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                Reader reader = new InputStreamReader(instream, "UTF-8");
                captchaResult = new Gson().fromJson(reader, ReCaptchaResult.class);
            } finally {
                instream.close();
            }
        }

        if (!captchaResult.isValid()) {
            addActionError("Sorry, you didn't pass the CAPTCHA test. Try again.");
            result = ERROR;
        }
        return result;
    }
    /* End Variables used for user registration and updates */

    public String loadChangePassword() throws Exception {
        return SUCCESS;
    }

    public String changePassword() throws Exception {
        String result = SUCCESS;
        userIsAdmin = user.getIsAdmin().equals(Constants.YES);

        String realPasswordHash = user.getPassword();
        if (!(Utility.encrypt(oldPassword).equals(realPasswordHash))) {
            addActionError("You entered your old password incorrectly. Your password was not changed. Please try again.");
            return ERROR;
        }

        // Change user object to have new password
        logger.debug("Changing user password");
        user.setPassword(Utility.encrypt(newPassword));

        // Commit changes
        userRepository.save(user);
        addActionMessage("Password change successful!");
        return result;
    }

    public String loadUpdateUserInformation() throws Exception {
        String result = SUCCESS;
        ActionContext context = ActionContext.getContext();
        address = user.getAddress();
        city = user.getCity();
        country = user.getCountry();
        email = user.getEmail();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        organizationName = user.getOrgName();
        organizationType = user.getOrgType();
        organizationPosition = user.getOrgType();
        phoneNumber = user.getPhone();
        stateOrProvince = user.getState();
        zipCode = user.getZipCode();
        workBench = user.getWorkbench();
        return result;
    }

    public String updateUserInformation() throws Exception {
        String result = SUCCESS;

        // validate each field
        validateUserInfo();
        if (!getActionErrors().isEmpty()) {
            return ERROR;
        }

        // Change user object according to edited fields
        logger.debug("Updating user information");
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setOrgName(organizationName);
        user.setOrgType(organizationType);
        user.setOrgPosition(organizationPosition);
        user.setPhone(phoneNumber);
        user.setAddress(address);
        user.setState(stateOrProvince);
        user.setCity(city);
        user.setCountry(country);
        user.setZipCode(zipCode);
        // deprecated, but some people think it's still important
        user.setWorkbench(workBench);

        // Commit changes
        userRepository.save(user);
        addActionMessage("Your information has been updated!");

        return result;
    }

    public String loadUpdateUserOptions() throws Exception {
        String result = SUCCESS;
        // check that the user is logged in
        ActionContext context = ActionContext.getContext();
        showPublicDatasets = user.getShowPublicDatasets();
        showPublicPredictors = user.getShowPublicPredictors();
        viewDatasetCompoundsPerPage = user.getViewDatasetCompoundsPerPage();
        viewPredictionCompoundsPerPage = user.getViewPredictionCompoundsPerPage();
        showAdvancedKnnModeling = user.getShowAdvancedKnnModeling();

        return result;
    }

    public String updateUserOptions() throws Exception {
        String result = SUCCESS;

        // Change user object according to edited fields
        user.setShowPublicDatasets(showPublicDatasets);
        user.setShowPublicPredictors(showPublicPredictors);
        user.setViewDatasetCompoundsPerPage(viewDatasetCompoundsPerPage);
        user.setViewPredictionCompoundsPerPage(viewPredictionCompoundsPerPage);
        user.setShowAdvancedKnnModeling(showAdvancedKnnModeling);

        // Commit changes
        userRepository.save(user);
        addActionMessage("Your settings have been saved!");

        return result;
    }

    private boolean userExists(String userName) throws Exception {
        return userRepository.findByUserName(userName) != null;
    }

    public void validateUserInfo() {
        if (firstName.isEmpty()) {
            addActionError("Please enter your first name.");
        }
        if (lastName.isEmpty()) {
            addActionError("Please enter your last name.");
        }
        if (organizationName.isEmpty()) {
            addActionError("Please enter your organization name.");
        }
        if (organizationPosition.isEmpty()) {
            addActionError("Please enter your organization position.");
        }
        if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
            addActionError("Please enter a valid email address.");
        }
        if (city.isEmpty()) {
            addActionError("Please enter your city.");
        }
        if (country.isEmpty()) {
            addActionError("Please enter your country.");
        }
    }

    /* End Variables used in password changes and user options */

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /* Variables used for user registration and updates */
    public String getRecaptchaPublicKey() {
        return recaptchaPublicKey;
    }

    public void setRecaptchaPublicKey(String recaptchaPublicKey) {
        this.recaptchaPublicKey = recaptchaPublicKey;
    }

    public String getOutputMessage() {
        return outputMessage;
    }

    public void setOutputMessage(String outputMessage) {
        this.outputMessage = outputMessage;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getOrganizationPosition() {
        return organizationPosition;
    }

    public void setOrganizationPosition(String organizationPosition) {
        this.organizationPosition = organizationPosition;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStateOrProvince() {
        return stateOrProvince;
    }

    public void setStateOrProvince(String stateOrProvince) {
        this.stateOrProvince = stateOrProvince;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getWorkBench() {
        return workBench;
    }

    public void setWorkBench(String workBench) {
        this.workBench = workBench;
    }

    /* End Variables used for user registration and updates */

    /* Variables used in password changes and user options */
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getShowPublicDatasets() {
        return showPublicDatasets;
    }

    public void setShowPublicDatasets(String showPublicDatasets) {
        this.showPublicDatasets = showPublicDatasets;
    }

    public String getShowPublicPredictors() {
        return showPublicPredictors;
    }

    public void setShowPublicPredictors(String showPublicPredictors) {
        this.showPublicPredictors = showPublicPredictors;
    }

    public String getViewDatasetCompoundsPerPage() {
        return viewDatasetCompoundsPerPage;
    }

    public void setViewDatasetCompoundsPerPage(String viewDatasetCompoundsPerPage) {
        this.viewDatasetCompoundsPerPage = viewDatasetCompoundsPerPage;
    }

    public String getViewPredictorModels() {
        return viewPredictorModels;
    }

    public void setViewPredictorModels(String viewPredictorModels) {
        this.viewPredictorModels = viewPredictorModels;
    }

    public String getViewPredictionCompoundsPerPage() {
        return viewPredictionCompoundsPerPage;
    }

    public void setViewPredictionCompoundsPerPage(String viewPredictionCompoundsPerPage) {
        this.viewPredictionCompoundsPerPage = viewPredictionCompoundsPerPage;
    }

    public String getShowAdvancedKnnModeling() {
        return showAdvancedKnnModeling;
    }

    public void setShowAdvancedKnnModeling(String showAdvancedKnnModeling) {
        this.showAdvancedKnnModeling = showAdvancedKnnModeling;
    }

    public boolean isUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }
    /* End Variables used in password changes and user options */

    public void setRecaptcha_challenge_field(String recaptcha_challenge_field) {
        this.recaptcha_challenge_field = recaptcha_challenge_field;
    }

    public void setRecaptcha_response_field(String recaptcha_response_field) {
        this.recaptcha_response_field = recaptcha_response_field;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    /* END DATA OBJECTS, GETTERS, AND SETTERS */
}
