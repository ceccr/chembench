package edu.unc.ceccr.chembench.actions;

import com.google.common.collect.Lists;
import edu.unc.ceccr.chembench.global.Constants;
import edu.unc.ceccr.chembench.persistence.*;
import edu.unc.ceccr.chembench.utilities.PopulateDataObjects;
import edu.unc.ceccr.chembench.utilities.Utility;
import org.apache.log4j.Logger;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PredictionDetailAction extends DetailAction {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(PredictionDetailAction.class.getName());
    List<CompoundPredictions> compoundPredictionValues = Lists.newArrayList();
    private Prediction prediction;
    private List<Predictor> predictors; //put these in order by predictorId
    private Dataset dataset; //dataset used in prediction
    private String currentPageNumber;
    private String orderBy;
    private String sortDirection;
    private ArrayList<String> pageNums;
    private Float cutoff;

    public String loadPredictionsSection() throws Exception {

        String result = checkBasicParams();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        if (context.getParameters().get("orderBy") != null) {
            orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
            logger.debug("orderBy = " + orderBy);
        } else {
            orderBy = "compoundId";
        }

        if (context.getParameters().get("cutoff") != null) {
            cutoff = Float.parseFloat(((String[]) context.getParameters().get("cutoff"))[0]);
        } else {
            cutoff = Float.parseFloat("0");
        }

        if (context.getParameters().get("currentPageNumber") != null) {
            currentPageNumber = ((String[]) context.getParameters().get("currentPageNumber"))[0];
        } else {
            currentPageNumber = "1";
        }
        if (context.getParameters().get("id") != null) {
            objectId = ((String[]) context.getParameters().get("id"))[0];
        }
        if (context.getParameters().get("sortDirection") != null) {
            sortDirection = ((String[]) context.getParameters().get("sortDirection"))[0];
        } else {
            sortDirection = "asc";
        }

        //get prediction
        logger.debug("prediction id: " + objectId);
        session = HibernateUtil.getSession();
        prediction = PopulateDataObjects.getPredictionById(Long.parseLong(objectId), session);
        if (prediction == null || (!prediction.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user.getUserName()
                .equals(prediction.getUserName()))) {
            logger.debug("No prediction was found in the DB with provided ID.");
            super.errorStrings.add("Invalid prediction ID supplied.");
            result = ERROR;
            session.close();
            return result;
        }
        prediction.setDatasetDisplay(PopulateDataObjects.getDataSetById(prediction.getDatasetId(), session).getName());

        //get predictors for this prediction. Order them by predictor ID, increasing.
        predictors = Lists.newArrayList();
        String[] predictorIds = prediction.getPredictorIds().split("\\s+");
        for (int i = 0; i < predictorIds.length; i++) {
            predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIds[i]), session));
        }
        Collections.sort(predictors, new Comparator<Predictor>() {
            public int compare(Predictor p1, Predictor p2) {
                return p1.getId().compareTo(p2.getId());
            }
        });

        //get dataset
        dataset = PopulateDataObjects.getDataSetById(prediction.getDatasetId(), session);

        //define which compounds will appear on page
        int pagenum, limit, offset;
        if (user.getViewPredictionCompoundsPerPage().equals(Constants.ALL)) {
            pagenum = 0;
            limit = 99999999;
            offset = pagenum * limit;
        } else {
            pagenum = Integer.parseInt(currentPageNumber) - 1;
            limit = Integer.parseInt(user.getViewPredictionCompoundsPerPage()); //compounds per page to display
            offset = pagenum * limit; //which compoundid to start on
        }

        //get prediction values
        compoundPredictionValues = PopulateDataObjects
                .populateCompoundPredictionValues(prediction.getDatasetId(), Long.parseLong(objectId), session);

        //sort the compoundPrediction array
        logger.debug("Sorting compound predictions");
        if (orderBy == null || orderBy.equals("") || orderBy.equals("compoundId")) {
            //sort by compoundId
            Collections.sort(compoundPredictionValues, new Comparator<CompoundPredictions>() {
                public int compare(CompoundPredictions o1, CompoundPredictions o2) {
                    return Utility.naturalSortCompare(o1.getCompound(), o2.getCompound());
                }
            });
        } else if (orderBy.equals("zScore")) {
            logger.debug("Sorting by zScore");
            // noop

            for (CompoundPredictions cp : compoundPredictionValues) {
                logger.debug(cp.getPredictionValues().size());
                for (PredictionValue pv : cp.getPredictionValues()) {
                    logger.debug(pv.getZScore());
                }
            }
            /*
            Collections.sort(compoundPredictionValues, new Comparator<CompoundPredictions>() {
                public int compare(CompoundPredictions o1, CompoundPredictions o2) {
                    return Utility.naturalSortCompare(o1.getZScore(), o2.getZScore());
                }
            });
            */
        } else {
            //check if orderBy equals one of the predictor names,
            //and order by those values if so.

            for (int i = 0; i < predictors.size(); i++) {
                if (predictors.get(i).getName().equals(orderBy)) {
                    //tell each sub-object what its sortBy index is
                    for (CompoundPredictions c : compoundPredictionValues) {
                        c.setSortByIndex(i);
                    }
                    Collections.sort(compoundPredictionValues, new Comparator<CompoundPredictions>() {
                        public int compare(CompoundPredictions o1, CompoundPredictions o2) {
                            float f1;
                            float f2;

                            if (o1.getPredictionValues().get(o1.getSortByIndex()).getPredictedValue() == null) {
                                return -1;
                            } else {
                                f1 = o1.getPredictionValues().get(o1.getSortByIndex()).getPredictedValue();
                            }
                            if (o2.getPredictionValues().get(o2.getSortByIndex()).getPredictedValue() == null) {
                                return 1;
                            } else {
                                f2 = o2.getPredictionValues().get(o2.getSortByIndex()).getPredictedValue();
                            }
                            return (f2 < f1 ? 1 : -1);
                        }
                    });
                }
            }

        }

        if (sortDirection != null && sortDirection.equals("desc")) {
            Collections.reverse(compoundPredictionValues);
        }
        logger.debug("Done sorting compound predictions");

        //displays the page numbers at the top
        pageNums = Lists.newArrayList();
        int j = 1;
        for (int i = 0; i < compoundPredictionValues.size(); i += limit) {
            String page = Integer.toString(j);
            pageNums.add(page);
            j++;
        }

        //pick out the ones to be displayed on the page based on offset and limit
        int compoundNum = 0;
        for (int i = 0; i < compoundPredictionValues.size(); i++) {
            if (compoundNum < offset || compoundNum >= (offset + limit)) {
                //don't display this compound
                compoundPredictionValues.remove(i);
                i--;
            } else {
                //leave it in the array
            }
            compoundNum++;
        }
        return result;
    }

    public String loadWarningsSection() throws Exception {
        String result = checkBasicParams();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        session = HibernateUtil.getSession();

        if (context == null) {
            logger.debug("No ActionContext available");
        } else {
            user = (User) context.getSession().get("user");

            if (user == null) {
                logger.debug("No user is logged in.");
                result = LOGIN;
                return result;
            }

            if (context.getParameters().get("predictionId") != null) {
                objectId = ((String[]) context.getParameters().get("predictionId"))[0];
            }
            //get prediction
            logger.debug("[ext_compounds] dataset id: " + objectId);
            prediction = PopulateDataObjects.getPredictionById(Long.parseLong(objectId), session);
            if (prediction == null || (!prediction.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user
                    .getUserName().equals(prediction.getUserName()))) {
                logger.debug("No prediction was found in the DB with provided ID.");
                super.errorStrings.add("Invalid prediction ID supplied.");
                result = ERROR;
                session.close();
                return result;
            }
        }
        return result;
    }

    public String load() throws Exception {

        String result = checkBasicParams();
        if (!result.equals(SUCCESS)) {
            return result;
        }

        if (context.getParameters().get("orderBy") != null) {
            orderBy = ((String[]) context.getParameters().get("orderBy"))[0];
        }

        String pagenumstr = null;
        if (context.getParameters().get("pagenum") != null) {
            pagenumstr = ((String[]) context.getParameters().get("pagenum"))[0]; //how many to skip (pagination)
        }


        if (context.getParameters().get("predictionId") != null) {
            objectId = ((String[]) context.getParameters().get("predictionId"))[0];
        }

        currentPageNumber = "1";
        if (pagenumstr != null) {
            currentPageNumber = pagenumstr;
        }

        logger.debug("prediction id: " + objectId);
        session = HibernateUtil.getSession();
        prediction = PopulateDataObjects.getPredictionById(Long.parseLong(objectId), session);

        if (prediction == null || (!prediction.getUserName().equals(Constants.ALL_USERS_USERNAME) && !user.getUserName()
                .equals(prediction.getUserName()))) {
            logger.debug("No prediction was found in the DB with provided ID.");
            super.errorStrings.add("Invalid prediction ID supplied.");
            result = ERROR;
            session.close();
            return result;
        }


        prediction.setDatasetDisplay(PopulateDataObjects.getDataSetById(prediction.getDatasetId(), session).getName());

        //get predictors for this prediction
        predictors = Lists.newArrayList();
        String[] predictorIds = prediction.getPredictorIds().split("\\s+");
        for (int i = 0; i < predictorIds.length; i++) {
            predictors.add(PopulateDataObjects.getPredictorById(Long.parseLong(predictorIds[i]), session));
        }

        //get dataset
        dataset = PopulateDataObjects.getDataSetById(prediction.getDatasetId(), session);

        //the prediction has now been viewed. Update DB accordingly.
        if (!prediction.getHasBeenViewed().equals(Constants.YES)) {
            prediction.setHasBeenViewed(Constants.YES);
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(prediction);
                tx.commit();
            } catch (RuntimeException e) {
                if (tx != null) {
                    tx.rollback();
                }
                logger.error(e);
            }
        }

        session.close();

        //log the results
        if (result.equals(SUCCESS)) {
            logger.debug("Forwarding user " + user.getUserName() + " to viewPrediction page.");
        } else {
            logger.warn("Cannot load page.");
        }

        return result;
    }

    public Prediction getPrediction() {
        return prediction;
    }

    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }

    public List<Predictor> getPredictors() {
        return predictors;
    }

    public void setPredictors(List<Predictor> predictors) {
        this.predictors = predictors;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public List<CompoundPredictions> getCompoundPredictionValues() {
        return compoundPredictionValues;
    }

    public void setCompoundPredictionValues(List<CompoundPredictions> compoundPredictionValues) {
        this.compoundPredictionValues = compoundPredictionValues;
    }

    public String getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(String currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public Float getCutoff() {
        return cutoff;
    }

    public void setCutoff(Float cutoff) {
        this.cutoff = cutoff;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public ArrayList<String> getPageNums() {
        return pageNums;
    }

    public void setPageNums(ArrayList<String> pageNums) {
        this.pageNums = pageNums;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}
