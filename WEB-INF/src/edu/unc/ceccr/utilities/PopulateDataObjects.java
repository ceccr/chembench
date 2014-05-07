package edu.unc.ceccr.utilities;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.CompoundPredictions;
import edu.unc.ceccr.persistence.DataSet;
import edu.unc.ceccr.persistence.ExternalValidation;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Job;
import edu.unc.ceccr.persistence.JobStats;
import edu.unc.ceccr.persistence.KnnModel;
import edu.unc.ceccr.persistence.KnnParameters;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.KnnPlusParameters;
import edu.unc.ceccr.persistence.Prediction;
import edu.unc.ceccr.persistence.PredictionValue;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestParameters;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.persistence.SoftwareLink;
import edu.unc.ceccr.persistence.SvmModel;
import edu.unc.ceccr.persistence.SvmParameters;
import edu.unc.ceccr.persistence.User;
import edu.unc.ceccr.workflows.datasets.DatasetFileOperations;

public class PopulateDataObjects {
    private static Logger logger = Logger.getLogger(PopulateDataObjects.class
            .getName());

    // Every time we need to get an object or set of objects from the database
    // we do it from here.

    @SuppressWarnings("rawtypes")
    public static ArrayList populateClass(Class<?> c, Session session) {
        // gets all of any one object from the database, returns it as a list
        ArrayList list = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            list = (ArrayList) session.createCriteria(c).list();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList populateClassInChunks(Class<?> c,
                                                  int chunkSize,
                                                  int chunkIndex,
                                                  Session session) {
        // gets a bunch of any one object from the database, returns it as a
        // list
        ArrayList list = null;
        Transaction tx = null;
        logger.info("PopulateClassInChunks called with chunkSize "
                + chunkSize + " and chunkIndex " + chunkIndex);
        logger.info("maxResults " + chunkSize + " firstResult: "
                + (chunkSize * chunkIndex));
        try {
            tx = session.beginTransaction();
            list = (ArrayList) session.createCriteria(c).setFirstResult(
                    chunkSize * chunkIndex).setMaxResults(chunkSize).list();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        if (list == null || list.isEmpty()) {
            list = null;
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList
    getUserData(String userName, Class<?> c, Session s) throws ClassNotFoundException,
            SQLException {
        // gets any data for which there is an associated username.
        // e.g.: datasets, predictors, predictions, jobs, users
        logger.info("looking for " + c.getName() + " of user " + userName);
        ArrayList list = null;
        Transaction tx = null;
        try {
            tx = s.beginTransaction();
            list = (ArrayList) s.createCriteria(c).add(
                    Restrictions.eq("userName", userName)).list();
            tx.commit();
        } catch (RuntimeException e) {
            logger.error(e);
        }
        logger.info("found " + list.size() + " " + c.getName()
                + " objects for user name " + userName);
        return list;
    }

    public static PredictionValue
    getFirstPredictionValueByPredictionIdAndPredictorId(Long predictionId,
                                                        Long predictorId,
                                                        Session session) throws Exception {
        PredictionValue predictionValue = null;
        try {
            session.beginTransaction();
            predictionValue = (PredictionValue) session.createCriteria(
                    PredictionValue.class).add(
                    Restrictions.eq("predictionId", predictionId)).add(
                    Restrictions.eq("predictorId", predictorId)).setMaxResults(
                    1).uniqueResult();
        } catch (Exception ex) {
            logger.error(ex);
        }

        if (predictionValue != null) {
            int numTotalModels = getPredictorById(
                    predictionValue.getPredictorId(), session)
                    .getNumTestModels();
            predictionValue.setNumTotalModels(numTotalModels);
        }
        return predictionValue;
    }

    public static List<PredictionValue>
    getPredictionValuesByPredictionIdAndPredictorId(Long predictionId,
                                                    Long predictorId,
                                                    Session session) throws Exception {
        ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
        try {
            session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(
                    PredictionValue.class).add(
                    Restrictions.eq("predictionId", predictionId)).add(
                    Restrictions.eq("predictorId", predictorId)).list()
                    .iterator();
            while (tempIter.hasNext()) {
                predictionValues.add((PredictionValue) tempIter.next());
            }

        } catch (Exception ex) {
            logger.error(ex);
        }

        for (PredictionValue pv : predictionValues) {
            Predictor p = getPredictorById(pv.getPredictorId(), session);
            int numTotalModels = 0;
            if (p.getChildType() != null
                    && p.getChildType().equals(Constants.NFOLD)) {
                numTotalModels = p.getNumTotalModels();
            } else {
                numTotalModels = p.getNumTestModels();
            }
            pv.setNumTotalModels(numTotalModels);
        }
        return predictionValues;
    }

    public static List<PredictionValue>
    getPredictionValuesByPredictionId(Long predictionId,
                                      Session session) throws Exception {
        ArrayList<PredictionValue> predictionValues = new ArrayList<PredictionValue>();
        Prediction prediction = getPredictionById(predictionId, session);
        String[] predictorIds = prediction.getPredictorIds().split("\\s+");

        for (String predictorId : predictorIds) {
            try {
                session.beginTransaction();
                ArrayList<PredictionValue> predictorPredictionValues = new ArrayList<PredictionValue>();
                Iterator<?> tempIter = session.createCriteria(
                        PredictionValue.class).add(
                        Restrictions.eq("predictionId", predictionId)).add(
                        Restrictions.eq("predictorId", Long
                                .parseLong(predictorId))
                ).list().iterator();

                while (tempIter.hasNext()) {
                    predictorPredictionValues.add((PredictionValue) tempIter
                            .next());
                }

                for (PredictionValue pv : predictorPredictionValues) {
                    Predictor p = getPredictorById(Long
                            .parseLong(predictorId), session);
                    int numTotalModels = 0;
                    if (p.getChildType() != null
                            && p.getChildType().equals(Constants.NFOLD)) {
                        numTotalModels = p.getNumTotalModels();
                    } else {
                        numTotalModels = p.getNumTestModels();
                    }
                    pv.setNumTotalModels(numTotalModels);
                }

                predictionValues.addAll(predictorPredictionValues);

            } catch (Exception ex) {
                logger.error(ex);
            }
        }

        return predictionValues;
    }

    public static ArrayList<CompoundPredictions>
    populateCompoundPredictionValues(Long datasetId,
                                     Long predictionId,
                                     Session session) throws Exception {
        DataSet dataset = getDataSetById(datasetId, session);

        // get compounds from SDF
        String datasetDir = "";
        datasetDir = Constants.CECCR_USER_BASE_PATH + dataset.getUserName()
                + "/DATASETS/" + dataset.getName() + "/";

        ArrayList<String> compounds = null;

        if (dataset.getXFile() != null && !dataset.getXFile().isEmpty()) {
            compounds = DatasetFileOperations.getXCompoundNames(datasetDir
                    + dataset.getXFile());
            logger.info("" + compounds.size() + " compounds found in X file.");
        } else {
            compounds = DatasetFileOperations.getSDFCompoundNames(datasetDir
                    + dataset.getSdfFile());
            logger.info("" + compounds.size() + " compounds found in SDF.");
        }

        logger.info("getting from db");
        ArrayList<PredictionValue> predictorPredictionValues = (ArrayList<PredictionValue>) PopulateDataObjects
                .getPredictionValuesByPredictionId(predictionId, session);
        logger.info("done getting from db");

        // sort the by predictor ID
        logger.info("Sorting");
        Collections.sort(predictorPredictionValues,
                new Comparator<PredictionValue>() {
                    public int
                    compare(PredictionValue p1, PredictionValue p2) {
                        return p1.getPredictorId().compareTo(
                                p2.getPredictorId());
                    }
                }
        );
        logger.info("Done sorting");

        logger.info("building hashmap");
        HashMap<String, ArrayList<PredictionValue>> predictionValueMap = new HashMap<String,
                ArrayList<PredictionValue>>();
        for (PredictionValue pv : predictorPredictionValues) {
            ArrayList<PredictionValue> compoundPredValues = predictionValueMap
                    .get(pv.getCompoundName());
            if (compoundPredValues == null) {
                compoundPredValues = new ArrayList<PredictionValue>();
            }
            compoundPredValues.add(pv);
            predictionValueMap.put(pv.getCompoundName(), compoundPredValues);
        }
        logger.info("done building hashmap");

        ArrayList<CompoundPredictions> compoundPredictionValues = new ArrayList<CompoundPredictions>();
        // get prediction values for each compound
        for (int i = 0; i < compounds.size(); i++) {
            CompoundPredictions cp = new CompoundPredictions();
            cp.setCompound(compounds.get(i));

            // get the prediction values for this compound
            cp.setPredictionValues(predictionValueMap.get(cp.getCompound()));

            // round them to a reasonable number of significant figures
            if (cp.getPredictionValues() != null) {
                for (PredictionValue pv : cp.getPredictionValues()) {
                    int sigfigs = Constants.REPORTED_SIGNIFICANT_FIGURES;
                    if (pv.getPredictedValue() != null) {
                        String predictedValue = DecimalFormat.getInstance()
                                .format(pv.getPredictedValue()).replaceAll(
                                        ",", "");
                        pv.setPredictedValue(Float.parseFloat(Utility
                                .roundSignificantFigures(predictedValue,
                                        sigfigs)));
                    }
                    if (pv.getStandardDeviation() != null) {
                        String stddev = DecimalFormat.getInstance().format(
                                pv.getStandardDeviation())
                                .replaceAll(",", "");
                        pv.setStandardDeviation(Float.parseFloat(Utility
                                .roundSignificantFigures(stddev, sigfigs)));
                    }
                }
            }
            compoundPredictionValues.add(cp);
        }
        return compoundPredictionValues;
    }

    public static List<DataSet>
    populateDatasetsForPrediction(String userName,
                                  boolean isAllUserIncludes,
                                  Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        List<DataSet> usersDataSet = new ArrayList<DataSet>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                // get both modeling AND prediction datasets, since modeling
                // datasets are possible to predict as well.
                Iterator<?> tempIter = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)).add(
                                Restrictions.or(Restrictions.eq("modelType",
                                        Constants.PREDICTION), Restrictions.or(
                                        Restrictions.eq("modelType",
                                                Constants.CONTINUOUS),
                                        Restrictions.eq("modelType",
                                                Constants.CATEGORY)
                                ))
                        )
                        .addOrder(Order.asc("name")).list().iterator();

                while (tempIter.hasNext()) {
                    dataSets.add((DataSet) tempIter.next());
                }
                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).add(
                                Restrictions.eq("jobCompleted", Constants.YES))
                        .add(Restrictions.or(Restrictions.eq("modelType",
                                Constants.PREDICTION), Restrictions.or(
                                Restrictions.eq("modelType",
                                        Constants.CONTINUOUS), Restrictions.eq(
                                        "modelType", Constants.CATEGORY)
                        )))
                        .addOrder(Order.asc("name")).list().iterator();
                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }
            } else {
                Iterator<?> tempIter = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).add(
                                Restrictions.eq("jobCompleted", Constants.YES))
                        .add(Restrictions.or(Restrictions.eq("modelType",
                                Constants.PREDICTION), Restrictions.or(
                                Restrictions.eq("modelType",
                                        Constants.CONTINUOUS), Restrictions.eq(
                                        "modelType", Constants.CATEGORY)
                        )))
                        .addOrder(Order.asc("name")).list().iterator();
                while (tempIter.hasNext()) {
                    dataSets.add((DataSet) tempIter.next());
                }

            }
            tx.commit();
            if (usersDataSet != null) {
                dataSets.addAll(usersDataSet);
            }

        } catch (Exception ex) {
            logger.error(ex);
        }

        return dataSets;
    }

    public static List<DataSet>
    populateDataset(String userName,
                    String modelType,
                    boolean isAllUserIncludes,
                    Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {
        // returns a list of datasets.
        // Used to populate the dropdowns on the Modeling and Dataset pages.

        List<DataSet> dataSets = new ArrayList<DataSet>();
        List<DataSet> usersDataSet = new ArrayList<DataSet>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                Iterator<?> tempIter1 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)).add(
                                Restrictions.eq("modelType", modelType))
                        .addOrder(Order.desc("name")).list().iterator();

                while (tempIter1.hasNext()) {
                    dataSets.add((DataSet) tempIter1.next());
                }

                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).add(
                                Restrictions.eq("jobCompleted", Constants.YES))
                        .add(Restrictions.eq("modelType", modelType)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }

            } else {
                Iterator<?> tempIter1 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).add(
                                Restrictions.eq("modelType", modelType))
                        .addOrder(Order.desc("name")).list().iterator();

                while (tempIter1.hasNext()) {
                    dataSets.add((DataSet) tempIter1.next());
                }
            }
            tx.commit();
            if (usersDataSet != null) {
                dataSets.addAll(usersDataSet);
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        Collections.reverse(dataSets);
        return dataSets;
    }

    public static List<String>
    populateDatasetNames(String userName,
                         boolean isAllUserIncludes,
                         Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        // returns a list of strings. Used in form validation, to make sure a
        // user doesn't reuse an existing name.

        List<DataSet> allUserDataSets = new ArrayList<DataSet>();
        List<DataSet> usersDataSet = new ArrayList<DataSet>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                Iterator<?> tempIter1 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter1.hasNext()) {
                    allUserDataSets.add((DataSet) tempIter1.next());
                }
                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }
            } else {
                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        List<String> datasetNames = new ArrayList<String>();
        try {
            if (allUserDataSets != null) {
                Iterator<DataSet> i = allUserDataSets.iterator();
                while (i.hasNext()) {
                    DataSet di = i.next();
                    datasetNames.add(di.getName()/* + " (public)" */);
                }
            }

            if (usersDataSet != null) {
                Iterator<DataSet> j = usersDataSet.iterator();
                while (j.hasNext()) {
                    DataSet dj = j.next();
                    datasetNames.add(dj.getName()/* + " (private)" */);
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        Collections.reverse(datasetNames);
        return datasetNames;
    }

    public static List<String>
    populatePredictorNames(String userName,
                           boolean isAllUserIncludes,
                           Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        // returns a list of strings. Used in form validation, to make sure a
        // user doesn't reuse an existing name.

        List<Predictor> userPredictors = new ArrayList<Predictor>();
        List<Predictor> allUserPredictors = new ArrayList<Predictor>();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                Iterator<?> tempIter1 = session.createCriteria(
                        Predictor.class).add(
                        Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)
                ).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter1.hasNext()) {
                    allUserPredictors.add((Predictor) tempIter1.next());

                }

                Iterator<?> tempIter2 = session.createCriteria(
                        Predictor.class).add(
                        Restrictions.eq("userName", userName)).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    userPredictors.add((Predictor) tempIter2.next());
                }

            } else {
                Iterator<?> tempIter2 = session.createCriteria(
                        Predictor.class).add(
                        Restrictions.eq("userName", userName)).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    userPredictors.add((Predictor) tempIter2.next());
                }

            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        List<String> predictorNames = new ArrayList<String>();
        try {
            if (userPredictors != null) {
                Iterator<Predictor> i = userPredictors.iterator();
                while (i.hasNext()) {
                    Predictor pi = i.next();
                    predictorNames.add(pi.getName());
                }
            }

            if (allUserPredictors != null) {
                Iterator<Predictor> j = allUserPredictors.iterator();
                while (j.hasNext()) {
                    Predictor pj = j.next();
                    predictorNames.add(pj.getName());
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        Collections.reverse(predictorNames);
        return predictorNames;
    }

    public static List<String>
    populatePredictionNames(String userName,
                            boolean isAllUserIncludes,
                            Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        // returns a list of strings. Used in form validation, to make sure a
        // user doesn't reuse an existing name.

        List<Prediction> userPredictions = new ArrayList<Prediction>();
        List<Prediction> allUserPredictions = new ArrayList<Prediction>();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                Iterator<?> tempIter1 = session.createCriteria(
                        Prediction.class).add(
                        Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)
                ).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter1.hasNext()) {
                    allUserPredictions.add((Prediction) tempIter1.next());
                }

                Iterator<?> tempIter2 = session.createCriteria(
                        Prediction.class).add(
                        Restrictions.eq("userName", userName)).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    userPredictions.add((Prediction) tempIter2.next());
                }
            } else {

                Iterator<?> tempIter2 = session.createCriteria(
                        Prediction.class).add(
                        Restrictions.eq("userName", userName)).addOrder(
                        Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    userPredictions.add((Prediction) tempIter2.next());
                }
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        List<String> predictionNames = new ArrayList<String>();
        try {
            if (userPredictions != null) {
                Iterator<Prediction> i = userPredictions.iterator();
                while (i.hasNext()) {
                    Prediction pi = i.next();
                    predictionNames.add(pi.getName());
                }
            }

            if (allUserPredictions != null) {
                Iterator<Prediction> j = allUserPredictions.iterator();
                while (j.hasNext()) {
                    Prediction pj = j.next();
                    predictionNames.add(pj.getName());
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        Collections.reverse(predictionNames);
        return predictionNames;
    }

    public static List<Predictor>
    populatePredictors(String userName,
                       boolean includePublic,
                       boolean onlyCompleted,
                       Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        List<Predictor> predictors = new ArrayList<Predictor>();
        List<Predictor> privatePredictors = new ArrayList<Predictor>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (onlyCompleted) {
                if (userName.equals("ALLOFTHEM")) { // silly kludge, remove
                    // when done
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions.eq("jobCompleted", Constants.YES))
                            .add(Restrictions.ne("predictorType",
                                    Constants.HIDDEN)).addOrder(
                                    Order.desc("name")).list().iterator();

                    while (tempIter.hasNext()) {
                        privatePredictors.add((Predictor) tempIter.next());
                    }
                } else {
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions.eq("userName", userName)).add(
                            Restrictions.eq("jobCompleted", Constants.YES))
                            .add(Restrictions.ne("predictorType",
                                    Constants.HIDDEN)).addOrder(
                                    Order.desc("name")).list().iterator();

                    while (tempIter.hasNext()) {
                        privatePredictors.add((Predictor) tempIter.next());
                    }
                }
            } else {
                Iterator<?> tempIter = session
                        .createCriteria(Predictor.class).add(
                                Restrictions.eq("userName", userName)).add(
                                Restrictions.ne("predictorType",
                                        Constants.HIDDEN)
                        ).list().iterator();

                while (tempIter.hasNext()) {
                    privatePredictors.add((Predictor) tempIter.next());
                }
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        predictors.addAll(privatePredictors);

        // adme
        if (includePublic) {
            List<Predictor> admePredictors = new ArrayList<Predictor>();
            session = HibernateUtil.getSession();
            tx = null;
            try {
                tx = session.beginTransaction();
                if (onlyCompleted) {
                    Iterator<?> tempIter = session
                            .createCriteria(Predictor.class)
                            .add(Restrictions.eq("userName", Constants.ALL_USERS_USERNAME))
                            .add(Restrictions.eq("predictorType", Constants.ADME))
                            .add(Restrictions.eq("jobCompleted", Constants.YES))
                            .addOrder(Order.desc("name")).list().iterator();

                    while (tempIter.hasNext()) {
                        admePredictors.add((Predictor) tempIter.next());
                    }
                } else {
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions.eq("predictorType", Constants.ADME))
                            .list().iterator();

                    while (tempIter.hasNext()) {
                        admePredictors.add((Predictor) tempIter.next());
                    }
                }

                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }
            predictors.addAll(admePredictors);
        }

        // tox
        if (includePublic) {
            List<Predictor> toxicityPredictors = new ArrayList<Predictor>();
            session = HibernateUtil.getSession();
            tx = null;
            try {
                tx = session.beginTransaction();
                if (onlyCompleted) {
                    Iterator<?> tempIter = session
                            .createCriteria(Predictor.class)
                            .add(Restrictions.eq("predictorType",
                                    Constants.TOXICITY))
                            .add(Restrictions.eq("jobCompleted", Constants.YES))
                            .addOrder(Order.desc("name")).list().iterator();

                    while (tempIter.hasNext()) {
                        toxicityPredictors.add((Predictor) tempIter.next());

                    }
                } else {
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions
                                    .eq("predictorType", Constants.TOXICITY)
                    )
                            .list().iterator();

                    while (tempIter.hasNext()) {
                        toxicityPredictors.add((Predictor) tempIter.next());
                    }
                }
                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }
            predictors.addAll(toxicityPredictors);
        }

        // drugdiscovery
        if (includePublic) {
            List<Predictor> drugDiscoveryPredictors = new ArrayList<Predictor>();
            session = HibernateUtil.getSession();
            tx = null;
            try {
                tx = session.beginTransaction();
                if (onlyCompleted) {
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions.eq("predictorType",
                                    Constants.DRUGDISCOVERY)
                    ).add(
                            Restrictions.eq("jobCompleted", Constants.YES))
                            .addOrder(Order.desc("name")).list().iterator();

                    while (tempIter.hasNext()) {
                        drugDiscoveryPredictors.add((Predictor) tempIter
                                .next());
                    }
                } else {
                    Iterator<?> tempIter = session.createCriteria(
                            Predictor.class).add(
                            Restrictions.eq("predictorType",
                                    Constants.DRUGDISCOVERY)
                    ).list()
                            .iterator();

                    while (tempIter.hasNext()) {
                        drugDiscoveryPredictors.add((Predictor) tempIter
                                .next());
                    }
                }
                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }
            predictors.addAll(drugDiscoveryPredictors);
        }

        for (int i = 0; i < predictors.size(); i++) {
            if (predictors.get(i).getDatasetId() != null
                    && getDataSetById(predictors.get(i).getDatasetId(),
                    session) != null) {
                predictors.get(i).setDatasetDisplay(
                        PopulateDataObjects.getDataSetById(
                                predictors.get(i).getDatasetId(), session)
                                .getName()
                );
            }
        }

        Collections.reverse(predictors);
        return predictors;
    }

    public static List<Prediction> populatePredictions(String userName,
                                                       boolean onlySaved,
                                                       Session session) {

        List<Prediction> predictions = new ArrayList<Prediction>();
        try {

            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterator<?> tempIter = session.createCriteria(
                        Prediction.class).add(
                        Restrictions.eq("jobCompleted", Constants.YES)).add(
                        Restrictions.or(Restrictions.eq("userName", userName),
                                Restrictions.eq("userName",
                                        Constants.ALL_USERS_USERNAME)
                        )
                )
                        .addOrder(Order.desc("name")).list().iterator();

                while (tempIter.hasNext()) {
                    predictions.add((Prediction) tempIter.next());
                }

                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }

            if (predictions != null) {
                for (Prediction p : predictions) {
                    String predictorNames = "";
                    String[] predictorIds = p.getPredictorIds().split("\\s+");
                    for (int i = 0; i < predictorIds.length; i++) {
                        predictorNames += getPredictorById(
                                Long.parseLong(predictorIds[i]), session)
                                .getName()
                                + " ";
                    }
                    p.setPredictorNames(predictorNames);
                    if (p.getDatasetId() != null
                            && getDataSetById(p.getDatasetId(), session) != null) {
                        p.setDatasetDisplay(getDataSetById(p.getDatasetId(),
                                session).getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }

        Collections.reverse(predictions);
        return predictions;
    }

    public static List<String>
    populateDatasetUploadedDescriptorTypes(String userName,
                                           boolean isAllUserIncludes,
                                           Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        // returns a list of strings. Used in form validation, to make sure a
        // user doesn't reuse an existing name.

        ArrayList<DataSet> allUserDataSets = new ArrayList<DataSet>();
        List<DataSet> usersDataSet = new ArrayList<DataSet>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {

                Iterator<?> tempIter1 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName",
                                Constants.ALL_USERS_USERNAME)).addOrder(
                                Order.desc("name")).list().iterator();
                while (tempIter1.hasNext()) {
                    allUserDataSets.add((DataSet) tempIter1.next());
                }

                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }
            } else {

                Iterator<?> tempIter2 = session.createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName)).addOrder(
                                Order.desc("name")).list().iterator();

                while (tempIter2.hasNext()) {
                    usersDataSet.add((DataSet) tempIter2.next());
                }

            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        ArrayList<String> datasetdescriptorsNames = new ArrayList<String>();
        try {
            if (allUserDataSets != null) {
                Iterator<DataSet> i = allUserDataSets.iterator();
                while (i.hasNext()) {
                    DataSet di = i.next();
                    if (di != null
                            && di.getAvailableDescriptors() != null
                            && di.getAvailableDescriptors().contains(
                            Constants.UPLOADED)
                            && di.getUploadedDescriptorType() != null
                            && !di.getUploadedDescriptorType().isEmpty()
                            && !datasetdescriptorsNames.contains(di
                            .getUploadedDescriptorType())) {
                        datasetdescriptorsNames.add(di
                                .getUploadedDescriptorType());
                    }
                }
            }

            if (usersDataSet != null) {
                Iterator<DataSet> j = usersDataSet.iterator();
                while (j.hasNext()) {
                    DataSet dj = j.next();
                    if (dj != null
                            && dj.getAvailableDescriptors() != null
                            && dj.getAvailableDescriptors().contains(
                            Constants.UPLOADED)
                            && dj.getUploadedDescriptorType() != null
                            && !dj.getUploadedDescriptorType().isEmpty()
                            && !datasetdescriptorsNames.contains(dj
                            .getUploadedDescriptorType())) {
                        datasetdescriptorsNames.add(dj
                                .getUploadedDescriptorType());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        return datasetdescriptorsNames;
    }

    public static List<DataSet>
    populateDatasetNamesForUploadedPredicors(String userName,
                                             String descriptorTypeName,
                                             boolean isAllUserIncludes,
                                             Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {

        List<DataSet> usersDataSet = new ArrayList<DataSet>();
        List<DataSet> allUserDataSets = new ArrayList<DataSet>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            if (isAllUserIncludes) {
                if (descriptorTypeName == null
                        || descriptorTypeName.trim().isEmpty()) {

		    /* Iterator<?> i1 = session.createCriteria(DataSet.class)
                            .add(Restrictions.eq("userName",
                                    Constants.ALL_USERS_USERNAME))
                            .add(Restrictions.or(Restrictions.eq("modelType",
                                    Constants.PREDICTION), Restrictions.or(
                                    Restrictions.eq("modelType",
                                            Constants.CONTINUOUS), Restrictions
                                            .eq("modelType",
                                                    Constants.CATEGORY))))
						    .addOrder(Order.desc("name")).list().iterator();*/
                    Iterator<?> i1 = session.createCriteria(DataSet.class)
                            .add(Restrictions.eq("userName",
                                    Constants.ALL_USERS_USERNAME))
                            .add(Restrictions.and(
                                    Restrictions.eq("uploadedDescriptorType", ""),
                                    Restrictions.eq("availableDescriptors", Constants.UPLOADED)))
                            .addOrder(
                                    Order.desc("name")).list().iterator();
                    while (i1.hasNext()) {
                        allUserDataSets.add((DataSet) i1.next());
                    }
                } else {
                    Iterator<?> i2 = session.createCriteria(DataSet.class)
                            .add(Restrictions.eq("userName",
                                    Constants.ALL_USERS_USERNAME)).add(
                                    Restrictions.eq("uploadedDescriptorType",
                                            descriptorTypeName)
                            ).addOrder(
                                    Order.desc("name")).list().iterator();
                    while (i2.hasNext()) {
                        allUserDataSets.add((DataSet) i2.next());
                    }
                }
            }

            if (descriptorTypeName == null
                    || descriptorTypeName.trim().isEmpty()) {
                /*Iterator<?> i3 = session.createCriteria(DataSet.class).add(
                        Restrictions.eq("userName", userName)).add(
                        Restrictions.or(Restrictions.eq("modelType",
                                Constants.PREDICTION), Restrictions.or(
                                Restrictions.eq("modelType",
                                        Constants.CONTINUOUS), Restrictions.eq(
                                        "modelType", Constants.CATEGORY))))
					.addOrder(Order.desc("name")).list().iterator();*/
                Iterator<?> i3 = session
                        .createCriteria(DataSet.class)
                        .add(Restrictions.eq("userName", userName))
                        .add(Restrictions.and(
                                Restrictions.eq("uploadedDescriptorType", ""),
                                Restrictions.eq("availableDescriptors", Constants.UPLOADED)))
                        .addOrder(Order.desc("name")).list().iterator();
                while (i3.hasNext()) {
                    usersDataSet.add((DataSet) i3.next());
                }
            } else {
                Iterator<?> i4 = session.createCriteria(DataSet.class).add(
                        Restrictions.eq("userName", userName)).add(
                        Restrictions.eq("uploadedDescriptorType",
                                descriptorTypeName)
                ).addOrder(
                        Order.desc("name")).list().iterator();
                while (i4.hasNext()) {
                    usersDataSet.add((DataSet) i4.next());
                }

            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        if (allUserDataSets != null) {
            usersDataSet.addAll(allUserDataSets);
        }
        return usersDataSet;
    }

    public static Job
    getJobById(Long jobId, Session session) throws ClassNotFoundException,
            SQLException {
        Job job = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            job = (Job) session.createCriteria(Job.class).add(
                    Restrictions.eq("id", jobId)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return job;
    }

    public static Job getJobByNameAndUsername(String name,
                                              String userName,
                                              Session session) {
        Job job = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            job = (Job) session.createCriteria(Job.class).add(
                    Restrictions.eq("jobName", name)).add(
                    Restrictions.eq("userName", userName)).uniqueResult();

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return job;
    }

    public static String
    getSdfFileForDataset(String datasetName,
                         String userName,
                         Session session) throws ClassNotFoundException,
            SQLException {
        DataSet dataset = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            dataset = (DataSet) session.createCriteria(DataSet.class).add(
                    Restrictions.eq("name", datasetName)).add(
                    Restrictions.eq("userName", userName)).uniqueResult();

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return dataset.getSdfFile();
    }

    public static DataSet
    getDataSetByName(String datasetName,
                     String userName,
                     Session session) throws ClassNotFoundException,
            SQLException {
        DataSet dataset = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            dataset = (DataSet) session.createCriteria(DataSet.class).add(
                    Restrictions.eq("name", datasetName)).add(
                    Restrictions.eq("userName", userName)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return dataset;
    }

    public static DataSet
    getDataSetById(Long id, Session session) throws ClassNotFoundException,
            SQLException {
        DataSet dataset = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            dataset = (DataSet) session.createCriteria(DataSet.class).add(
                    Restrictions.eq("id", id)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return dataset;
    }

    public static Predictor
    getPredictorById(Long predictorId, Session session) throws ClassNotFoundException,
            SQLException {
        Predictor predictor = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            predictor = (Predictor) session.createCriteria(Predictor.class)
                    .add(Restrictions.eq("id", predictorId)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        if (predictor != null && predictor.getDatasetId() != null) {
            if (getDataSetById(predictor.getDatasetId(), session) != null) {
                predictor.setDatasetDisplay(PopulateDataObjects
                        .getDataSetById(predictor.getDatasetId(), session)
                        .getName());
            }
        }
        return predictor;
    }

    public static Prediction
    getPredictionById(Long predictionId, Session session) throws Exception {
        Prediction prediction = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            prediction = (Prediction) session
                    .createCriteria(Prediction.class).add(
                            Restrictions.eq("id", predictionId)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        String predictorNames = "";
        String[] predictorIds = prediction.getPredictorIds().split("\\s+");
        for (int i = 0; i < predictorIds.length; i++) {
            Long predictorId = Long.parseLong(predictorIds[i]);
            Predictor p = getPredictorById(predictorId, session);
            if (p != null) {
                predictorNames += (p.getName() + " ");
            } else {
                logger.warn(String.format(
                        "Expected predictor %d for prediction %d " +
                                "does not exist", predictorId, predictionId
                ));
            }
        }
        prediction.setPredictorNames(predictorNames);
        prediction.setDatabase(prediction.getDatabase());

        if (prediction.getDatasetId() != null
                && getDataSetById(prediction.getDatasetId(), session) != null) {
            prediction.setDatasetDisplay(getDataSetById(
                    prediction.getDatasetId(), session).getName());
        }

        return prediction;
    }

    public static Prediction
    getPredictionByName(String jobName,
                        String userName,
                        Session session) throws Exception {
        Prediction prediction = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            prediction = (Prediction) session
                    .createCriteria(Prediction.class).add(
                            Restrictions.eq("userName", userName)).add(
                            Restrictions.eq("name", jobName)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        String predictorNames = "";
        String[] predictorIds = prediction.getPredictorIds().split("\\s+");
        for (int i = 0; i < predictorIds.length; i++) {
            predictorNames += getPredictorById(
                    Long.parseLong(predictorIds[i]), session).getName()
                    + " ";
        }
        prediction.setPredictorNames(predictorNames);
        prediction.setDatabase(prediction.getDatabase());

        if (prediction.getDatasetId() != null
                && getDataSetById(prediction.getDatasetId(), session) != null) {
            prediction.setDatasetDisplay(getDataSetById(
                    prediction.getDatasetId(), session).getName());
        }

        return prediction;
    }

    public static User getUserByUserName(String userName, Session session) {
        User user = null;

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            user = (User) session.createCriteria(User.class).add(
                    Restrictions.eq("userName", userName)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return user;
    }

    public static ArrayList<User> getAllUsers(Session session) {
        ArrayList<User> users = new ArrayList<User>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIterator = session.createCriteria(User.class)
                    .list().iterator();
            while (tempIterator.hasNext()) {

                users.add((User) tempIterator.next());
            }
            // users = (ArrayList<User>) session.createCriteria(User.class)
            // .list();
            tx.commit();

            Collections.sort(users, new Comparator<User>() {
                public int compare(User u1, User u2) {
                    return u1.getUserName().toLowerCase().compareTo(
                            u2.getUserName().toLowerCase());
                }
            });
        } catch (Exception e) {
            logger.error(e);
        }
        return users;
    }

    public static List<User> getUsers(Session session) {
        ArrayList<User> users = new ArrayList<User>();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIterator = session.createCriteria(User.class)
                    .list().iterator();
            while (tempIterator.hasNext()) {

                users.add((User) tempIterator.next());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return users;
    }

    public static List<JobStats> getJobStats(Session session) {
        List<JobStats> jobStats = new ArrayList<JobStats>();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(JobStats.class)
                    .list().iterator();
            while (tempIter.hasNext()) {
                jobStats.add((JobStats) tempIter.next());
            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return jobStats;
    }

    public static List<JobStats> getJobStatsByUserName(Session session,
                                                       String username) {
        List<JobStats> jobStats = new ArrayList<JobStats>();

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(JobStats.class)
                    .add(Restrictions.eq("userName", username)).list()
                    .iterator();
            while (tempIter.hasNext()) {
                jobStats.add((JobStats) tempIter.next());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return jobStats;
    }

    public static List<RandomForestGrove>
    getRandomForestGrovesByPredictorId(Long predictorId,
                                       Session session) throws Exception {

        List<RandomForestGrove> groves = new ArrayList<RandomForestGrove>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(
                    RandomForestGrove.class).add(
                    Restrictions.eq("predictorId", predictorId)).list()
                    .iterator();
            while (tempIter.hasNext()) {
                groves.add((RandomForestGrove) tempIter.next());
            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return groves;
    }

    public static List<RandomForestTree>
    getRandomForestTreesByGroveId(Long groveId, Session session) throws Exception {

        List<RandomForestTree> trees = new ArrayList<RandomForestTree>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(
                    RandomForestTree.class).add(
                    Restrictions.eq("randomForestGroveId", groveId)).list()
                    .iterator();

            while (tempIter.hasNext()) {
                trees.add((RandomForestTree) tempIter.next());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return trees;
    }

    public static List<KnnPlusModel>
    getKnnPlusModelsByPredictorId(Long predictorId, Session session) throws ClassNotFoundException,
            SQLException {
        List<KnnPlusModel> models = new ArrayList<KnnPlusModel>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(KnnPlusModel.class)
                    .add(Restrictions.eq("predictorId", predictorId)).list()
                    .iterator();
            while (tempIter.hasNext()) {
                models.add((KnnPlusModel) tempIter.next());
            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return models;
    }

    public static List<SvmModel>
    getSvmModelsByPredictorId(Long predictorId, Session session) throws ClassNotFoundException,
            SQLException {
        // logger.error("getting models for predictorId: " +
        // predictorId);

        List<SvmModel> models = new ArrayList<SvmModel>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(SvmModel.class)
                    .add(Restrictions.eq("predictorId", predictorId)).list()
                    .iterator();
            while (tempIter.hasNext()) {

                models.add((SvmModel) tempIter.next());
            }

            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return models;
    }

    public static List<KnnModel>
    getModelsByPredictorId(Long predictorId, Session session) throws ClassNotFoundException,
            SQLException {
        // logger.error("getting models for predictorId: " +
        // predictorId);
        Predictor predictor = getPredictorById(predictorId, session);

        List<KnnModel> models = new ArrayList<KnnModel>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(KnnModel.class)
                    .add(Restrictions.eq("predictorId", predictorId)).list()
                    .iterator();

            while (tempIter.hasNext()) {

                models.add((KnnModel) tempIter.next());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        // if the model type is continuous,
        // sort models in decreasing order by r^2 value
        // (used when displaying top 10 models on view predictor page)
        if (predictor.getActivityType().equals(Constants.CONTINUOUS)
                && models != null && models.size() > 1) {
            Collections.sort(models, new Comparator<KnnModel>() {
                public int compare(KnnModel o1, KnnModel o2) {
                    return (o2.getRSquared() > o1.getRSquared() ? 1 : -1);
                }
            });
        }
        return models;
    }

    public static Predictor
    getPredictorByName(String selectedPredictorName,
                       String user,
                       Session session) throws ClassNotFoundException,
            SQLException {
        Predictor predictor = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            predictor = (Predictor) session.createCriteria(Predictor.class)
                    .add(Restrictions.eq("name", selectedPredictorName)).add(
                            Restrictions.eq("userName", user)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        try {

            if (predictor != null && predictor.getDatasetId() != null) {
                if (getDataSetById(predictor.getDatasetId(), session) != null) {
                    predictor
                            .setDatasetDisplay(PopulateDataObjects
                                    .getDataSetById(predictor.getDatasetId(),
                                            session).getName());
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return predictor;
    }

    public static ArrayList<Predictor>
    getChildPredictors(Predictor predictor, Session session) {
        ArrayList<Predictor> childPredictors = new ArrayList<Predictor>();

        String[] childPredictorIds;
        if (predictor.getChildIds() != null
                && !predictor.getChildIds().trim().equals("")) {
            // get external validation from each child predictor
            childPredictorIds = predictor.getChildIds().split("\\s+");
        } else {
            return childPredictors;
        }

        Transaction tx = null;
        try {
            for (String childPredictorId : childPredictorIds) {
                if (childPredictorId != null) {
                    tx = session.beginTransaction();
                    Predictor childPredictor = (Predictor) session
                            .createCriteria(Predictor.class).add(
                                    Restrictions.eq("id", Long
                                            .parseLong(childPredictorId))
                            )
                            .uniqueResult();
                    tx.commit();
                    childPredictors.add(childPredictor);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return childPredictors;
    }

    public static List<ExternalValidation>
    getExternalValidationValues(Long predictorId, Session session) {

        List<ExternalValidation> externalValValues = new ArrayList<ExternalValidation>();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Iterator<?> tempIter = session.createCriteria(
                    ExternalValidation.class).add(
                    Restrictions.eq("predictorId", predictorId)).addOrder(
                    Order.asc("predictedValue")).list().iterator();

            while (tempIter.hasNext()) {

                externalValValues.add((ExternalValidation) tempIter.next());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }

        return externalValValues;
    }

    public static List<String> populateTaskNames(String userName,
                                                 boolean justRunning,
                                                 Session session) {

        List<String> taskNames = new ArrayList<String>();
        List<Job> tasks = new ArrayList<Job>();
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterator<?> tempIter = session.createCriteria(Job.class).add(
                        Restrictions.eq("userName", userName)).list()
                        .iterator();

                while (tempIter.hasNext()) {
                    tasks.add((Job) tempIter.next());
                }
                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }

        } catch (Exception e) {
            logger.error(e);
        }
        try {
            if (tasks != null) {
                Iterator<Job> i = tasks.iterator();
                while (i.hasNext()) {
                    Job ti = i.next();
                    if (!justRunning) {
                        taskNames.add(ti.getJobName());
                    } else if (!ti.getStatus().equals(Constants.QUEUED)) {
                        taskNames.add(ti.getJobName());
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        }

        return taskNames;
    }

    public static List<Job> populateTasks(String userName,
                                          boolean justRunning,
                                          Session session) {

        ArrayList<Job> tasks = new ArrayList<Job>();
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                if (justRunning) {
                    Iterator<?> tempIterator = session.createCriteria(
                            Job.class).add(
                            Restrictions.eq("userName", userName)).add(
                            Restrictions.eq("state", Constants.QUEUED)).list()
                            .iterator();

                    while (tempIterator.hasNext()) {
                        tasks.add((Job) tempIterator.next());
                    }
                } else {
                    Iterator<?> tempIterator = session.createCriteria(
                            Job.class).add(
                            Restrictions.eq("userName", userName)).list()
                            .iterator();

                    while (tempIterator.hasNext()) {
                        tasks.add((Job) tempIterator.next());
                    }
                }

                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return tasks;
    }

    public static Job
    getTaskById(Long id, Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {
        Job task = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            task = (Job) session.createCriteria(Job.class).add(
                    Restrictions.eq("id", id)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return task;
    }

    public static List<SoftwareLink> populateSoftwareLinks(Session session) {

        List<SoftwareLink> softwareLinks = new ArrayList<SoftwareLink>();
        try {
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Iterator<?> tempIter = session.createCriteria(
                        SoftwareLink.class).list().iterator();
                while (tempIter.hasNext()) {
                    softwareLinks.add((SoftwareLink) tempIter.next());
                }

                tx.commit();
            } catch (Exception e) {
                logger.error(e);
            }

        } catch (Exception e) {
            logger.error(e);
        }

        Collections.sort(softwareLinks, new Comparator<SoftwareLink>() {
            public int compare(SoftwareLink sl1, SoftwareLink sl2) {
                return sl1.getName().toLowerCase().compareTo(
                        sl2.getName().toLowerCase());
            }
        });
        return softwareLinks;
    }

    public static SoftwareLink
    getSoftwareLinkById(Long id, Session session) throws HibernateException,
            ClassNotFoundException,
            SQLException {
        SoftwareLink sl = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            sl = (SoftwareLink) session.createCriteria(SoftwareLink.class)
                    .add(Restrictions.eq("id", id)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return sl;
    }

    public static RandomForestParameters
    getRandomForestParametersById(Long id, Session session) throws Exception {
        RandomForestParameters params = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            params = (RandomForestParameters) session.createCriteria(
                    RandomForestParameters.class)
                    .add(Restrictions.eq("id", id)).uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return params;
    }

    public static KnnParameters
    getKnnParametersById(Long id, Session session) throws Exception {
        KnnParameters params = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            params = (KnnParameters) session.createCriteria(
                    KnnParameters.class).add(Restrictions.eq("id", id))
                    .uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return params;
    }

    public static KnnPlusParameters
    getKnnPlusParametersById(Long id, Session session) throws Exception {
        KnnPlusParameters params = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            params = (KnnPlusParameters) session.createCriteria(
                    KnnPlusParameters.class).add(Restrictions.eq("id", id))
                    .uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return params;
    }

    public static SvmParameters
    getSvmParametersById(Long id, Session session) throws Exception {
        SvmParameters params = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            params = (SvmParameters) session.createCriteria(
                    SvmParameters.class).add(Restrictions.eq("id", id))
                    .uniqueResult();
            tx.commit();
        } catch (Exception e) {
            logger.error(e);
        }
        return params;
    }

}
