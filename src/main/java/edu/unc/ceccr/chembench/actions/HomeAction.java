package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.ActiveUser;
import edu.unc.ceccr.chembench.utilities.FileAndDirOperations;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.List;

public class HomeAction extends ActionSupport implements ServletResponseAware, ServletRequestAware {
    private static final Logger logger = Logger.getLogger(HomeAction.class.getName());
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobStatsRepository jobStatsRepository;
    protected HttpServletResponse servletResponse;
    //loads home page
    int visitors;
    int userStats;
    int jobStats;
    String cpuStats;
    String activeUsers;
    int runningJobs;
    String loginFailed = Constants.NO;
    User user;
    String username;
    String password;
    String showStatistics = Constants.YES;
    private List<String> errorStrings = Lists.newArrayList();
    private HttpServletRequest request;
    private String ipAddress;
    private String savedUrl = (String) ActionContext.getContext().getSession().get("savedUrl");


    public HomeAction(UserRepository userRepository, JobRepository jobRepository,
                      JobStatsRepository jobStatsRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobStatsRepository = jobStatsRepository;
    }

    @Override
    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public String loadPage() {
        try {
            //check if user is logged in
            ActionContext context = ActionContext.getContext();
            user = User.getCurrentUser();

            //populate each string for the statistics section
            int numJobs = jobRepository.findAll().size();
            List<User> users = userRepository.findAll();
            List<JobStats> jobStatList = jobStatsRepository.findAll();

            // cumulative visitors to the site
            int counter = 0;
            File counterFile = new File(Constants.CECCR_USER_BASE_PATH + "counter.txt");
            if (counterFile.exists()) {
                String counterStr = FileAndDirOperations.readFileIntoString(counterFile.getAbsolutePath()).trim();
                counter = Integer.parseInt(counterStr);
                FileAndDirOperations.writeStringToFile("" + (counter + 1), counterFile.getAbsolutePath());
            }
            visitors = counter;

            // number of registered users
            userStats = users.size();

            // finished jobs
            int numFinishedJobs = jobStatList.size();
            jobStats = numFinishedJobs;

            // CPU statistics
            int computeHours = 0;
            String computeYearsStr = "";
            long timeDiffs = 0;

            for (JobStats js : jobStatList) {
                if (js.getTimeFinished() != null && js.getTimeStarted() != null) {
                    timeDiffs += js.getTimeFinished().getTime() - js.getTimeCreated().getTime();
                }
            }
            computeHours = Math.round(timeDiffs / 1000 / 60 / 60);
            float computeHoursf = computeHours;
            float computeYears = computeHoursf / (float) (24.0 * 365.0);
            computeYearsStr = Utility.floatToString(computeYears);
            Utility.roundSignificantFigures(computeYearsStr, 4);
            cpuStats = computeYearsStr;

            // current users
            activeUsers = ActiveUser.getActiveSessions();

            // current number of jobs
            runningJobs = numJobs;

            ipAddress = getServletRequest().getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            ipAddress = ipAddress.replaceAll("\\.", "");
        } catch (Exception ex) {
            logger.error("", ex);
            showStatistics = "NO";
        }
        return SUCCESS;
    }

    public List<String> getErrorStrings() {
        return errorStrings;
    }

    public void setErrorStrings(List<String> errorStrings) {
        this.errorStrings = errorStrings;
    }

    public String execute() throws Exception {
        //log the user in
        String result = SUCCESS;

        //check username and password
        ActionContext context = ActionContext.getContext();

        if (context.getParameters().get("username") != null) {
            username = ((String[]) context.getParameters().get("username"))[0];
        }
        User user;

        if (context.getParameters().get("ip") != null) {

            String ip = ((String[]) context.getParameters().get("ip"))[0];
            long time = System.currentTimeMillis();
            String new_username = "guest" + ip + "_" + time;

            user = new User();

            user.setUserName(new_username);
            user.setEmail("ceccr@email.unc.edu");
            user.setFirstName("Guest");
            user.setLastName("Guest");
            user.setOrgName("Guest");
            user.setOrgType("Academia");
            user.setOrgPosition("Guest");
            user.setPhone("Guest");
            user.setAddress("Guest");
            user.setState("Guest");
            user.setCity("Guest");
            user.setCountry("Guest");
            user.setZipCode("Guest");

            //options
            user.setShowPublicDatasets(Constants.SOME);
            user.setShowPublicPredictors(Constants.ALL);
            user.setViewDatasetCompoundsPerPage(Constants.TWENTYFIVE);
            user.setViewPredictionCompoundsPerPage(Constants.TWENTYFIVE);
            user.setShowAdvancedKnnModeling(Constants.NO);

            //rights
            user.setCanDownloadDescriptors(Constants.NO);
            user.setIsAdmin(Constants.NO);

            //password
            String password = "";
            user.setPassword(Utility.encrypt(password));

            user.setStatus("agree");

            //commit user to DB
            userRepository.save(user);

            new Thread(new Runnable() {

                @Override
                public void run() {

                    deleteOldGuests();

                }
            }).start();

            context.getSession().put("user", user);
            context.getSession().put("userType", "guest");
            Cookie ckie = new Cookie("login", "true");
            servletResponse.addCookie(ckie);

            logger.debug("Logged in guest:: " + user.getUserName());

        } else {
            user = userRepository.findByUserName(username);

            if (user != null) {
                // allow admins to bypass password login if they have already
                // logged in first
                boolean adminBypassPassword = false;
                User currentUser = User.getCurrentUser();
                if (currentUser != null) {
                    String currentUserName = currentUser.getUserName();
                    if (currentUser.getIsAdmin().equals(Constants.YES)) {
                        logger.warn(String.format("Administrator bypassed password check: " + "ADMIN=%s, NEWUSER=%s",
                                currentUserName, username));
                        adminBypassPassword = true;
                    } else {
                        logger.warn(String.format("Attempt made by non-admin user %s to " + "impersonate other user %s",
                                currentUserName, username));
                    }
                }

                String realPasswordHash = user.getPassword();
                if ((adminBypassPassword) || (password != null && Utility.encrypt(password).equals(realPasswordHash))) {
                    context.getSession().put("user", user);
                    Cookie ckie = new Cookie("login", "true");
                    servletResponse.addCookie(ckie);
                    user.setLastLogintime(new Date());
                    userRepository.save(user);
                    logger.debug("Logged in " + user.getUserName());
                } else {
                    loginFailed = Constants.YES;
                }
            } else {
                loginFailed = Constants.YES;
            }
        }

        if (savedUrl != null && !savedUrl.isEmpty()) {
            return "returnToSaved";
        } else {
            return loadPage();
        }
    }

    public String logout() throws Exception {
        ActionContext context = ActionContext.getContext();
        user = User.getCurrentUser();
        if (user != null) {
            logger.debug("Logged out: " + user.getUserName());

        if (user.getUserName().contains("guest") && context.getSession().get("userType") != null && ((String) context
                .getSession().get("userType")).equals("guest")) {
            (new DeleteAction()).deleteUser(user.getUserName());
        }
        context.getSession().remove("user");
        context.getSession().clear();

            Cookie ckie = new Cookie("login", "false");
            servletResponse.addCookie(ckie);
        }

        return loadPage();
    }

    synchronized public void deleteOldGuests() {
        List<String> dirs = FileAndDirOperations.getGuestDirNames(new File(Constants.CECCR_USER_BASE_PATH));
        long currentTime = System.currentTimeMillis();
        for (String dir : dirs) {
            long guestTime = new Long(dir.substring(dir.lastIndexOf('_') + 1)).longValue();
            if (!dir.trim().isEmpty() && currentTime - guestTime > Constants.GUEST_DATA_EXPIRATION_TIME) {
                FileAndDirOperations.deleteDir(new File(Constants.CECCR_USER_BASE_PATH + dir));
                logger.debug("DELETING OLD GUEST DATA:" + dir);
            }
        }
    }


    public int getVisitors() {
        return visitors;
    }

    public void setVisitors(int visitors) {
        this.visitors = visitors;
    }

    public int getUserStats() {
        return userStats;
    }

    public void setUserStats(int userStats) {
        this.userStats = userStats;
    }

    public int getJobStats() {
        return jobStats;
    }

    public void setJobStats(int jobStats) {
        this.jobStats = jobStats;
    }

    public String getCpuStats() {
        return cpuStats;
    }

    public void setCpuStats(String cpuStats) {
        this.cpuStats = cpuStats;
    }

    public String getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(String activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getRunningJobs() {
        return runningJobs;
    }

    public void setRunningJobs(int runningJobs) {
        this.runningJobs = runningJobs;
    }

    public String getShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(String showStatistics) {
        this.showStatistics = showStatistics;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginFailed() {
        return loginFailed;
    }

    public void setLoginFailed(String loginFailed) {
        this.loginFailed = loginFailed;
    }

    public HttpServletRequest getServletRequest() {
        return this.request;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getSavedUrl() {
        return savedUrl;
    }
}
