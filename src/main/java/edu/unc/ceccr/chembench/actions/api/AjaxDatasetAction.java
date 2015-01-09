package edu.unc.ceccr.chembench.actions.api;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.persistence.Dataset;

public class AjaxDatasetAction extends ActionSupport {
    private Dataset dataset;
    private long id;

    public String ajaxGetDataset() {
        dataset = Dataset.get(id);
        if (dataset == null) {
            return "notfound";
        }
        return SUCCESS;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
