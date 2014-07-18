package edu.unc.ceccr.action.ViewPredictor;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.KnnPlusModel;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import org.apache.log4j.Logger;

import java.util.*;

public class KnnPlusModelsPage extends ViewPredictorAction {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(KnnPlusModelsPage.class
            .getName());
    private List<KnnPlusModel> knnPlusModels;

    public String load() throws Exception {
        try {
            String result = getBasicParameters();
            if (!result.equals(SUCCESS)) {
                return result;
            }

            getModelsPageParameters();

            if (childPredictors.size() == 0) {
                result = loadModels();
            } else {
                currentFoldNumber = ""
                        + (Integer.parseInt(currentFoldNumber) + 1);
                for (int i = 0; i < childPredictors.size(); i++) {
                    foldNums.add("" + (i + 1));
                    if (currentFoldNumber.equals("" + (i + 1))) {
                        String parentId = objectId;
                        objectId = "" + childPredictors.get(i).getId();
                        result = loadModels();
                        objectId = parentId;
                    }
                }
            }

            // get descriptor freqs from models
            HashMap<String, Integer> descriptorFreqMap = new HashMap<String, Integer>();
            if (knnPlusModels != null) {
                for (KnnPlusModel m : knnPlusModels) {
                    if (m.getDimsNames() != null
                            && !m.getDimsNames().equals("")) {
                        String[] descriptorArray = m.getDimsNames().split(
                                "\\s+");
                        for (int i = 0; i < descriptorArray.length; i++) {
                            if (descriptorFreqMap.get(descriptorArray[i]) == null) {
                                descriptorFreqMap.put(descriptorArray[i], 1);
                            } else {
                                // increment
                                descriptorFreqMap.put(descriptorArray[i],
                                        descriptorFreqMap
                                                .get(descriptorArray[i]) + 1
                                );
                            }
                        }
                    }
                }
            }

            List<descriptorFrequency> descriptorFrequencies = new ArrayList<descriptorFrequency>();
            List<String> mapKeys = new ArrayList<String>(
                    descriptorFreqMap.keySet());
            for (String k : mapKeys) {
                descriptorFrequency df = new descriptorFrequency();
                df.setDescriptor(k);
                df.setNumOccs(descriptorFreqMap.get(k));
                descriptorFrequencies.add(df);
            }

            Collections.sort(descriptorFrequencies,
                    new Comparator<descriptorFrequency>() {
                        public int compare(descriptorFrequency df1,
                                           descriptorFrequency df2) {
                            if (df1.getNumOccs() == df2.getNumOccs()) {
                                return 0;
                            } else if (df1.getNumOccs() > df2.getNumOccs()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    }
            );
            if (descriptorFrequencies.size() >= 5) {
                // if there weren't at least 5 descriptors, don't even bother
                // - no summary needed
                mostFrequentDescriptors = "The 5 most frequent descriptors "
                        + "                             used in your models were: ";
                for (int i = 0; i < 5; i++) {
                    mostFrequentDescriptors += descriptorFrequencies.get(i)
                            .getDescriptor()
                            + " ("
                            + descriptorFrequencies.get(i).getNumOccs()
                            + " models)";
                    if (i < 4) {
                        mostFrequentDescriptors += ", ";
                    }
                }
                mostFrequentDescriptors += ".";
            }

            return result;
        } catch (Exception ex) {
            logger.error(ex);
            errorStrings.add(ex.getMessage());
            return ERROR;
        }
    }

    private String loadModels() {
        String result = SUCCESS;
        try {
            knnPlusModels = new ArrayList<KnnPlusModel>();
            session = HibernateUtil.getSession();
            List<KnnPlusModel> temp = PopulateDataObjects
                    .getKnnPlusModelsByPredictorId(Long.parseLong(objectId),
                            session);
            session.close();
            if (temp != null) {
                Iterator<KnnPlusModel> it = temp.iterator();
                while (it.hasNext()) {
                    KnnPlusModel m = it.next();
                    if (m.getIsYRandomModel().equals(Constants.NO)
                            && isYRandomPage.equals(Constants.NO)) {
                        knnPlusModels.add(m);
                    } else if (m.getIsYRandomModel().equals(Constants.YES)
                            && isYRandomPage.equals(Constants.YES)) {
                        knnPlusModels.add(m);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            errorStrings.add(ex.getMessage());
            return ERROR;
        }
        return result;
    }

    @SuppressWarnings("unused")
    private String loadModelSets() {
        String result = SUCCESS;
        for (Predictor childPredictor : childPredictors) {
            objectId = "" + childPredictor.getId();
            result = loadModels();
            if (!result.equals(SUCCESS)) {
                return result;
            }
        }
        return result;
    }

    public List<KnnPlusModel> getKnnPlusModels() {
        return knnPlusModels;
    }

    public void setKnnPlusModels(List<KnnPlusModel> knnPlusModels) {
        this.knnPlusModels = knnPlusModels;
    }

}
