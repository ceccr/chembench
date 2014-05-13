package edu.unc.ceccr.action;

//struts2

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class TooltipActions extends ActionSupport {
    /*
	 Tooltips are JSPs which may contain calculated values.
	 This class populates any variables that will be read by a tooltip JSP.
	 */

    private static final long serialVersionUID = 1L;
    //member variables, with getters and setters
    public String compoundId = "Ur Butt";
    public String compoundPredictedValue = "3";
    public String compoundObservedValue = "2";

    public String loadExternalValidationChartTooltip() throws Exception {
        //get the observed and predicted values of the compound
        ActionContext context = ActionContext.getContext();

        compoundId = ((String[]) context.getParameters().get("compoundId"))[0];
        compoundPredictedValue = ((String[]) context.getParameters().get("predictedValue"))[0];
        compoundObservedValue = ((String[]) context.getParameters().get("observedValue"))[0];

        return SUCCESS;
    }

    public String getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(String compoundId) {
        this.compoundId = compoundId;
    }

    public String getCompoundPredictedValue() {
        return compoundPredictedValue;
    }

    public void setCompoundPredictedValue(String compoundPredictedValue) {
        this.compoundPredictedValue = compoundPredictedValue;
    }

    public String getCompoundObservedValue() {
        return compoundObservedValue;
    }

    public void setCompoundObservedValue(String compoundObservedValue) {
        this.compoundObservedValue = compoundObservedValue;
    }
}