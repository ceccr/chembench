package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.User;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;

public class DetailAction extends ActionSupport implements ServletRequestAware {
    protected long id;
    protected User user = User.getCurrentUser();
    protected HttpServletRequest request;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }
}
