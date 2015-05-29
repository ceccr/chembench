package edu.unc.ceccr.chembench.interceptors;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class AuthenticationInterceptor extends AbstractInterceptor {

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();
        HttpServletRequest request = ServletActionContext.getRequest();
        if (session.get("user") == null) {
            String queryString = request.getQueryString();
            session.put("savedUrl", request.getRequestURI() + ((queryString == null) ? "" : "?" + queryString));
            return Action.LOGIN;
        }
        return invocation.invoke();
    }
}
