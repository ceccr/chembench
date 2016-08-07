package edu.unc.ceccr.chembench.actions;

import com.opensymphony.xwork2.ActionSupport;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.RunExternalProgram;
import edu.unc.ceccr.chembench.utilities.Utility;
import edu.unc.ceccr.chembench.workflows.calculations.PredictorEvaluation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminAction extends ActionSupport {

    private static final Logger logger = LoggerFactory.getLogger(AdminAction.class);
    private final UserRepository userRepository;
    private final DatasetRepository datasetRepository;
    private final PredictorRepository predictorRepository;
    private final ExternalValidationRepository externalValidationRepository;

    private final KnnPlusModelRepository knnPlusModelRepository;
    private final SvmModelRepository svmModelRepository;
    private final RandomForestGroveRepository randomForestGroveRepository;
    private final RandomForestTreeRepository randomForestTreeRepository;

    private final RandomForestParametersRepository randomForestParametersRepository;
    private final SvmParametersRepository svmParametersRepository;
    private final KnnPlusParametersRepository knnPlusParametersRepository;

    private List<User> users;
    private String userName;
    private boolean isAdmin;
    private boolean canDownloadDescriptors;
    private long datasetId;
    private long predictorId;
    private String predictorType;

    @Autowired
    public AdminAction(UserRepository userRepository, DatasetRepository datasetRepository,
                       PredictorRepository predictorRepository, PredictionRepository predictionRepository,
                       PredictionValueRepository predictionValueRepository,
                       ExternalValidationRepository externalValidationRepository,
                       KnnPlusModelRepository knnPlusModelRepository, SvmModelRepository svmModelRepository,
                       RandomForestGroveRepository randomForestGroveRepository,
                       RandomForestTreeRepository randomForestTreeRepository,
                       RandomForestParametersRepository randomForestParametersRepository,
                       SvmParametersRepository svmParametersRepository,
                       KnnPlusParametersRepository knnPlusParametersRepository) {
        this.userRepository = userRepository;
        this.datasetRepository = datasetRepository;
        this.predictorRepository = predictorRepository;
        this.externalValidationRepository = externalValidationRepository;
        this.knnPlusModelRepository = knnPlusModelRepository;
        this.svmModelRepository = svmModelRepository;
        this.randomForestGroveRepository = randomForestGroveRepository;
        this.randomForestTreeRepository = randomForestTreeRepository;
        this.randomForestParametersRepository = randomForestParametersRepository;
        this.svmParametersRepository = svmParametersRepository;
        this.knnPlusParametersRepository = knnPlusParametersRepository;
    }

    public String execute() {
        users = userRepository.findAll();
        return SUCCESS;
    }

    public String changeUserFlags() {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            return "badrequest";
        }
        user.setIsAdmin(Utility.booleanToString(isAdmin));
        user.setCanDownloadDescriptors(Utility.booleanToString(canDownloadDescriptors));
        userRepository.save(user);
        return SUCCESS;
    }

    /**
     * Method responsible for converting predictor from private to public use
     *
     * @return
     */
    public String makePredictorPublic() {
        Predictor predictor = predictorRepository.findOne(predictorId);
        if (predictor == null) {
            return ERROR;
        }
        if (predictor.getParentId() != null) {
            Predictor parentPredictor = predictorRepository.findOne(predictor.getParentId());
            if (parentPredictor == null) {
                return ERROR;
            }
            predictor = parentPredictor;
        }

        // idiot proof if someone will try to make public predictor public again.
        if (predictor.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
            return SUCCESS;
        }

        //prevent duplication of names
        if (predictorRepository.findByNameAndUserName(predictor.getName(), Constants.ALL_USERS_USERNAME) != null) {
            addActionError("There is already a public predictor with the same name as this one.");
            return ERROR;
        }

        Dataset dataset = datasetRepository.findOne(predictor.getDatasetId());
        if (dataset == null) {
            return ERROR;
        } else if (!dataset.getUserName().trim().equals(Constants.ALL_USERS_USERNAME)) {
            this.datasetId = dataset.getId();
            String datasetResult = makeDatasetPublic();
            if (datasetResult.equals(ERROR)) {
                return ERROR;
            }
        }

        String allUserPredictorDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME +
                "/PREDICTORS/";
        String predictorUserName = predictor.getUserName();
        String userPredictorDir = Constants.CECCR_USER_BASE_PATH + predictorUserName + "/PREDICTORS/" + predictor.getName();

        //copy files to all users folder
        logger.debug("Start copying files from '" + userPredictorDir + "' to '" + allUserPredictorDir + "'");
        new File(allUserPredictorDir).mkdirs();
        String cmd = "cp -r " + userPredictorDir + " " + allUserPredictorDir;
        RunExternalProgram.runCommand(cmd, "");

        //starting database records cloning process
        Long oldPredictorId = predictor.getId();
        Long newPredictorId = null;
        //duplicating predictor record
        logger.debug("------DB: Duplicating predictor record for predictor: " + predictor.getName());
        predictor.setId(null);
        predictor.setUserName(Constants.ALL_USERS_USERNAME);
        predictor.setPredictorType(predictorType);
        predictor.setDatasetId(dataset.getId());
        Predictor newPredictor = predictorRepository.save(predictor);
        newPredictorId = predictor.getId();
        newPredictor = copyPredictorRelatedObjects(oldPredictorId, newPredictorId);

        //duplicating child predictors
        List<Predictor> children = predictorRepository.findByParentId(oldPredictorId);
        if (!children.isEmpty()) {
            List<Long> childIds = new ArrayList<>();
            for (Predictor child : children) {
                Long oldChildId = child.getId();
                child.setId(null);
                child.setUserName(Constants.ALL_USERS_USERNAME);
                child.setPredictorType("Hidden");
                child.setDatasetId(dataset.getId());
                child.setParentId(newPredictorId);
                Predictor newChild = predictorRepository.save(child);
                childIds.add(newChild.getId());
                copyPredictorRelatedObjects(oldChildId, newChild.getId());
            }
            newPredictor.setChildIds(Utility.SPACE_JOINER.join(childIds));
            predictorRepository.save(newPredictor);
            logger.debug("--------New child predictor IDs=" + newPredictor.getChildIds());
        }
        return SUCCESS;
    }

    private Predictor copyPredictorRelatedObjects(Long oldId, Long newId) {
        //taking care of external validation table
        logger.debug("------//taking care of external validation table");

        List<ExternalValidation> extValidation = externalValidationRepository.findByPredictorId(oldId);
        for (ExternalValidation exVal : extValidation) {
            exVal.setExternalValId(null);
            exVal.setPredictorId(newId);
            externalValidationRepository.save(exVal);
        }

        //taking care of knnPlusModel table
        logger.debug("------//taking care of knnPlusModel table");
        List<KnnPlusModel> knnPlusModels = knnPlusModelRepository.findByPredictorId(oldId);
        for (KnnPlusModel knnPlusModel : knnPlusModels) {
            knnPlusModel.setId(null);
            knnPlusModel.setPredictorId(newId);
            knnPlusModelRepository.save(knnPlusModel);
        }

        //taking care of SVM table
        logger.debug("------//taking care of SVM table");
        List<SvmModel> svmModels = svmModelRepository.findByPredictorId(oldId);
        for (SvmModel svmModel : svmModels) {
            svmModel.setId(null);
            svmModel.setPredictorId(newId);
            svmModelRepository.save(svmModel);
        }

        //taking care of RandomForest table
        logger.debug("------//taking care of RandomForest table");
        List<RandomForestGrove> groves = randomForestGroveRepository.findByPredictorId(oldId);
        for (RandomForestGrove grove : groves) {
            Long oldGroveId = grove.getId();
            grove.setId(null);
            grove.setPredictorId(newId);
            RandomForestGrove newGrove = randomForestGroveRepository.save(grove);
            List<RandomForestTree> trees = randomForestTreeRepository.findByRandomForestGroveId(oldGroveId);
            for (RandomForestTree tree : trees) {
                tree.setId(null);
                tree.setRandomForestGroveId(newGrove.getId());
                randomForestTreeRepository.save(tree);
            }
        }

        //taking care of modeling parameters
        Predictor oldPredictor = predictorRepository.findOne(oldId);
        Predictor newPredictor = predictorRepository.findOne(newId);
        logger.debug("------//taking care of modeling parameters");
        if (oldPredictor.getModelMethod().startsWith(Constants.RANDOMFOREST)) {
            logger.debug("------//RANDOMFOREST");
            RandomForestParameters randomForestParameters =
                    randomForestParametersRepository.findOne(oldPredictor.getModelingParametersId());
            randomForestParameters.setId(null);
            randomForestParameters = randomForestParametersRepository.save(randomForestParameters);
            newPredictor.setModelingParametersId(randomForestParameters.getId());
        } else if (oldPredictor.getModelMethod().equals(Constants.KNNGA) || oldPredictor.getModelMethod()
                .equals(Constants.KNNSA)) {
            logger.debug("------//KNN+");
            KnnPlusParameters knnPlusParameters =
                    knnPlusParametersRepository.findOne(oldPredictor.getModelingParametersId());
            knnPlusParameters.setId(null);
            knnPlusParameters = knnPlusParametersRepository.save(knnPlusParameters);
            newPredictor.setModelingParametersId(knnPlusParameters.getId());
        } else if (oldPredictor.getModelMethod().equals(Constants.SVM)) {
            logger.debug("------//SVM");
            SvmParameters svmParameters = svmParametersRepository.findOne(oldPredictor.getModelingParametersId());
            svmParameters.setId(null);
            svmParameters = svmParametersRepository.save(svmParameters);
            newPredictor.setModelingParametersId(svmParameters.getId());
        }
        logger.debug("--------Old predictor ID=" + oldId + " -> new one = " + newId);
        return predictorRepository.save(newPredictor);
    }

    public String makeDatasetPublic() {
        Dataset dataset = datasetRepository.findOne(datasetId);
        if (dataset == null) {
            return ERROR;
        }

        // idiot proof if someone will try to make public dataset public again.
        if (dataset.getUserName().equals(Constants.ALL_USERS_USERNAME)) {
            return SUCCESS;
        }

        //prevent duplication of names
        if (datasetRepository.findByNameAndUserName(dataset.getName(), Constants.ALL_USERS_USERNAME) != null) {
            addActionError("There is already been a public dataset with the same name as this one.");
            return ERROR;
        }

        String allUserDatasetDir = Constants.CECCR_USER_BASE_PATH + Constants.ALL_USERS_USERNAME + "/DATASETS/";
        String userDatasetDir = Constants.CECCR_USER_BASE_PATH + dataset.getUserName() + "/DATASETS/" + dataset.getName();

        //copy files to all users folder
        logger.debug("Start copying files from '" + userDatasetDir + "' to '" + allUserDatasetDir + "'");
        new File(allUserDatasetDir).mkdirs();
        String cmd = "cp -r " + userDatasetDir + " " + allUserDatasetDir;
        RunExternalProgram.runCommand(cmd, "");

        //starting database records cloning process

        //duplicating dataset record
        logger.debug("------DB: Duplicating dataset record for dataset: " + dataset.getName());
        dataset.setId(null);
        dataset.setUserName(Constants.ALL_USERS_USERNAME);
        datasetRepository.save(dataset);
        return SUCCESS;
    }

    @Transactional
    public String regenerateRSquaredOrCCR() {
        Predictor p = predictorRepository.findOne(predictorId);
        if (p == null) {
            return "notfound";
        }

        logger.debug("Regenerating R2 or CCR for predictor, id: {}" + p.getId());
        PredictorEvaluation.addRSquaredAndCCRToPredictor(p);
        predictorRepository.save(p);
        return SUCCESS;
    }

    @Transactional
    public String regenerateChildren() {
        Predictor parent = predictorRepository.findOne(predictorId);
        if (parent == null) {
            return "notfound";
        } else if (!parent.getChildType().equals(Constants.NFOLD)) {
            return "badrequest";
        }
        logger.info("Regenerating children for predictor, id: {}", parent.getId());

        List<Long> childIds =
                Utility.stringListToLongList(Utility.WHITESPACE_SPLITTER.splitToList(parent.getChildIds()));
        List<Long> newChildIds = new ArrayList<>();
        for (int i = 0; i < childIds.size(); i++) {
            Long originalChildId = childIds.get(i);
            Long newChildId = originalChildId; // just an initializing value
            Predictor child = predictorRepository.findOne(originalChildId);
            if (child != null && !child.getParentId().equals(parent.getId())) {
                /* XXX If there's an existing predictor referenced by a parent as a child predictor, but that supposed
                child predictor *doesn't* refer back to that parent, then we have to assume that it's completely
                unrelated. If we tried to regenerate the child, we'd clobber that unrelated predictor because we'd
                overwrite its id.

                We could try to create a new child predictor with a generated id, but then we run into the problem
                that there's no way to know what the correct external validation values are (do they belong to the
                expected child, or to this unrelated predictor)? So there's no point in going down that route. */
                throw new RuntimeException(String.format("Stopping regeneration because unrelated predictor with same "
                                + "child id exists: %d should be a child to parent %d but its parent is %d", child
                                .getId(),
                        parent.getId(), child.getParentId()));
            } else if (child == null) {
                logger.info("Expected child (id: {}) of parent (id: {}) doesn't exist, regenerating it",
                        originalChildId, parent.getId());
                // Use the parent as a template for the regenerated child predictor,
                // changing properties as necessary
                Predictor newChild = Utility.clone(parent);
                newChild.setId(null);
                newChild.setName(String.format("%s_fold_%d_of_%d", parent.getName(), (i + 1), childIds.size()));
                newChild.setParentId(parent.getId());
                newChild.setChildIds(null);
                newChild.setChildType(null);
                newChild.setPredictorType(Constants.HIDDEN);
                newChild.setExternalPredictionAccuracy(null);
                newChild.setExternalPredictionAccuracyAvg(null);
                newChild = predictorRepository.save(newChild);
                newChildId = newChild.getId(); // this will be the new id we need to point to in parent.childIds

                updateRelatedObjects(newChild, originalChildId, parent.getId());
                logger.info("Regeneration of child {} successful, new id is {}", originalChildId, newChildId);
            }
            newChildIds.add(newChildId);
        }
        parent.setChildIds(Utility.SPACE_JOINER.join(newChildIds));
        predictorRepository.save(parent);
        logger.info("Regeneration of parent predictor {} complete, new childIds: {}", parent.getId(),
                parent.getChildIds());
        return SUCCESS;
    }

    /**
     * Update related predictor objects (external validation and models) to point to a new predictor id.
     * <p>
     * Parameters don't have to be updated since predictors point to those rather than vice-versa.
     * <p>
     * Note: newChild *must* have been persisted already; otherwise its id will either be null (breaking old records
     * forever) or the same as the parent predictor it was cloned from (clobbering anything that pointed to the parent
     * predictor).
     *
     * @param newChild        the newly regenerated child predictor
     * @param originalChildId the id that the original child predictor had
     * @param parentId        the id of the parent predictor
     * @throws IllegalArgumentException if newChild is invalid
     */
    private void updateRelatedObjects(Predictor newChild, Long originalChildId, Long parentId) {
        Long newChildId = newChild.getId();
        if (newChildId == null || newChildId.equals(parentId)) {
            throw new IllegalArgumentException("newChild has an invalid id (has it been persisted?)");
        }

        switch (newChild.getModelMethod()) {
            case Constants.RANDOMFOREST:
            case Constants.RANDOMFOREST_R:
                for (RandomForestGrove grove : randomForestGroveRepository.findByPredictorId(originalChildId)) {
                    grove.setPredictorId(newChildId);
                    randomForestGroveRepository.save(grove);
                }
                break;
            case Constants.SVM:
                for (SvmModel svmModel : svmModelRepository.findByPredictorId(originalChildId)) {
                    svmModel.setPredictorId(newChildId);
                    svmModelRepository.save(svmModel);
                }
                break;
            case Constants.KNNGA:
            case Constants.KNNSA:
                for (KnnPlusModel knnPlusModel : knnPlusModelRepository.findByPredictorId(originalChildId)) {
                    knnPlusModel.setPredictorId(newChildId);
                    knnPlusModelRepository.save(knnPlusModel);
                }
                break;
            default:
                throw new IllegalArgumentException(
                        "newChild has unrecognized or unsupported model method: " + newChild.getModelMethod());
        }

        // XXX ExternalValidation objects *must* be updated before calling addRSquaredAndCCRToPredictor()
        for (ExternalValidation ev : externalValidationRepository.findByPredictorId(originalChildId)) {
            ev.setPredictorId(newChildId);
            externalValidationRepository.save(ev);
        }
        PredictorEvaluation.addRSquaredAndCCRToPredictor(newChild);

    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getCanDownloadDescriptors() {
        return canDownloadDescriptors;
    }

    public void setCanDownloadDescriptors(boolean canDownloadDescriptors) {
        this.canDownloadDescriptors = canDownloadDescriptors;
    }

    public long getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(long datasetId) {
        this.datasetId = datasetId;
    }

    public long getPredictorId() {
        return predictorId;
    }

    public void setPredictorId(long predictorId) {
        this.predictorId = predictorId;
    }

    public String getPredictorType() {
        return predictorType;
    }

    public void setPredictorType(String predictorType) {
        this.predictorType = predictorType;
    }
}
