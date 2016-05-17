package edu.unc.ceccr.chembench.interceptors;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.User;

public class AdminInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {
        User user = User.getCurrentUser();
        if (!user.getIsAdmin().equals(Constants.YES)) {
            return "forbidden";
        }
        return actionInvocation.invoke();
    }
}
