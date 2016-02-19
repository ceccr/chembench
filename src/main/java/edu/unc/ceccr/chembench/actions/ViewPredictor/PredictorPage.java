package edu.unc.ceccr.chembench.actions.ViewPredictor;

import com.opensymphony.xwork2.ActionContext;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.PredictorRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class PredictorPage extends ViewPredictorAction {
    private static final Logger logger = Logger.getLogger(PredictorPage.class.getName());
    private final PredictorRepository predictorRepository;
    private String predictorDescription = "";
    private String predictorReference = "";
    private String editable = "";

    @Autowired
    public PredictorPage(PredictorRepository predictorRepository) {
        this.predictorRepository = predictorRepository;
    }

    public String load() throws Exception {
        String result = getBasicParameters();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        if (context.getParameters().get("editable") != null) {
            if (user.getIsAdmin().equals(Constants.YES) || user.getUserName().equals(selectedPredictor.getUserName())) {
                editable = "YES";
            }
        } else {
            editable = "NO";
        }
        predictorDescription = selectedPredictor.getDescription();
        predictorReference = selectedPredictor.getPaperReference();

        // the predictor has now been viewed. Update DB accordingly.
        if (!selectedPredictor.getHasBeenViewed().equals(Constants.YES)) {
            selectedPredictor.setHasBeenViewed(Constants.YES);
            predictorRepository.save(selectedPredictor);
        }
        return result;
    }

    public String update() throws Exception {
        // check that the user is logged in
        context = ActionContext.getContext();

        if (context != null && context.getParameters().get("objectId") != null) {
            // get predictorId id
            objectId = ((String[]) context.getParameters().get("objectId"))[0];
            String[] predictorIdAsStringArray = new String[1];
            predictorIdAsStringArray[0] = objectId;
            context.getParameters().put("id", predictorIdAsStringArray);
            predictorDescription = ((String[]) context.getParameters().get("predictorDescription"))[0];
            predictorReference = ((String[]) context.getParameters().get("predictorReference"))[0];
            predictorRepository.findOne(Long.parseLong(objectId));
            selectedPredictor.setDescription(predictorDescription);
            selectedPredictor.setPaperReference(predictorReference);
            predictorRepository.save(selectedPredictor);
        }
        return load();
    }

    // getters and setters

    public String getPredictorDescription() {
        return predictorDescription;
    }

    public void setPredictorDescription(String predictorDescription) {
        this.predictorDescription = predictorDescription;
    }

    public String getPredictorReference() {
        return predictorReference;
    }

    public void setPredictorReference(String predictorReference) {
        this.predictorReference = predictorReference;
    }

    public String getEditable() {
        return editable;
    }

    public void setEditable(String editable) {
        this.editable = editable;
    }

    // end getters and setters

}
