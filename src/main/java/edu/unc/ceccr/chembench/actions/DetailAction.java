package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.Dataset;
import edu.unc.ceccr.chembench.persistence.Prediction;
import edu.unc.ceccr.chembench.persistence.Predictor;
import edu.unc.ceccr.chembench.persistence.User;
import org.apache.struts2.interceptor.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;

public class DetailAction extends ActionSupport implements ServletRequestAware {
    protected long id;
    protected User user = User.getCurrentUser();
    protected HttpServletRequest request;

    private boolean isViewable(Object object) {
        if (object instanceof Dataset) {
            Dataset dataset = (Dataset) object;
            return dataset.isViewableBy(user);
        } else if (object instanceof Predictor) {
            Predictor predictor = (Predictor) object;
            return predictor.isViewableBy(user);
        } else if (object instanceof Prediction) {
            Prediction prediction = (Prediction) object;
            return prediction.isViewableBy(user);
        }
        return false;
    }

    protected String validateObject(Object object) {
        if (object == null) {
            return "notfound";
        }
        if (!isViewable(object)) {
            return "forbidden";
        }
        return SUCCESS;
    }

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
