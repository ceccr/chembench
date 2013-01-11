package edu.unc.ceccr.action.ViewPredictor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import edu.unc.ceccr.global.Constants;
import edu.unc.ceccr.persistence.HibernateUtil;
import edu.unc.ceccr.persistence.Predictor;
import edu.unc.ceccr.persistence.RandomForestGrove;
import edu.unc.ceccr.persistence.RandomForestTree;
import edu.unc.ceccr.utilities.PopulateDataObjects;
import edu.unc.ceccr.utilities.Utility;

public class RandomForestModelsPage extends ViewPredictorAction
{

    private List<RandomForestGrove> randomForestGroves;
    private List<RandomForestTree>  randomForestTrees;

    public String load() throws Exception
    {

        String result = getBasicParameters();
        if (!result.equals(SUCCESS))
            return result;

        getModelsPageParameters();

        Utility.writeToDebug("rf foldnum: " + currentFoldNumber);

        if (childPredictors.size() == 0) {
            loadTrees();
        }
        else {
            currentFoldNumber = ""
                    + (Integer.parseInt(currentFoldNumber) + 1);
            for (int i = 0; i < childPredictors.size(); i++) {
                foldNums.add("" + (i + 1));
                if (currentFoldNumber.equals("" + (i + 1))) {
                    String parentId = objectId;
                    objectId = "" + childPredictors.get(i).getId();
                    loadTrees();
                    objectId = parentId;
                }
            }
        }

        // get descriptor freqs from trees
        HashMap<String, Integer> descriptorFreqMap 
                                              = new HashMap<String, Integer>();
        if (randomForestTrees != null) {
            for (RandomForestTree t : randomForestTrees) {
                if (t.getDescriptorsUsed() != null
                        && !t.getDescriptorsUsed().equals("")) {
                    String[] descriptorArray = t.getDescriptorsUsed().split(
                            "\\s+");
                    for (int i = 0; i < descriptorArray.length; i++) {
                        if (descriptorFreqMap.get(descriptorArray[i]) == null){
                            descriptorFreqMap.put(descriptorArray[i], 1);
                        }
                        else {
                            // increment
                            descriptorFreqMap
                                    .put(descriptorArray[i],
                                            descriptorFreqMap
                                                 .get(descriptorArray[i]) + 1);
                        }
                    }
                }
            }
        }
        ArrayList<descriptorFrequency> descriptorFrequencies
                                        = new ArrayList<descriptorFrequency>();
        ArrayList<String> mapKeys = new ArrayList<String>(descriptorFreqMap
                .keySet());
        for (String k : mapKeys) {
            descriptorFrequency df = new descriptorFrequency();
            df.setDescriptor(k);
            df.setNumOccs(descriptorFreqMap.get(k));
            descriptorFrequencies.add(df);
        }

        Collections.sort(descriptorFrequencies,
                new Comparator<descriptorFrequency>()
                {
                    public int compare(descriptorFrequency df1,
                                       descriptorFrequency df2)
                    {
                        return (df1.getNumOccs() > df2.getNumOccs() ? -1 : 1);
                    }
                });
        if (descriptorFrequencies.size() >= 5) {
            // if there weren't at least 5 descriptors, don't even bother - no
            // summary needed
            mostFrequentDescriptors = "The 5 most frequent descriptors " +
            		                               "used in your trees were: ";
            for (int i = 0; i < 5; i++) {
                mostFrequentDescriptors += descriptorFrequencies.get(i)
                        .getDescriptor()
                        + " ("
                        + descriptorFrequencies.get(i).getNumOccs()
                        + " trees)";
                if (i < 4) {
                    mostFrequentDescriptors += ", ";
                }
            }
            mostFrequentDescriptors += ".";
        }

        Utility.writeToDebug("Done loading trees page for predictor id"
                + objectId);

        return result;
    }

    public String loadGrovesPage() throws Exception
    {
        String result = SUCCESS;
        getBasicParameters();
        getModelsPageParameters();

        if (childPredictors.size() == 0) {
            loadGroves();
        }
        else {
            loadGroveSets();
        }

        return result;
    }

    private String loadGroves() throws Exception
    {
        String result = SUCCESS;
        session = HibernateUtil.getSession();
        List<RandomForestGrove> rfGroves = PopulateDataObjects
                .getRandomForestGrovesByPredictorId(Long.parseLong(objectId),
                        session);
        randomForestGroves = new ArrayList<RandomForestGrove>();

        if (rfGroves != null) {
            for (RandomForestGrove rfg : rfGroves) {
                if (isYRandomPage.equals(Constants.YES)
                        && rfg.getIsYRandomModel().equals(Constants.YES)) {
                    randomForestGroves.add(rfg);
                }
                else if (isYRandomPage.equals(Constants.NO)
                        && rfg.getIsYRandomModel().equals(Constants.NO)) {
                    randomForestGroves.add(rfg);
                }
            }
        }
        session.close();
        return result;
    }

    private String loadTrees() throws Exception
    {
        String result = SUCCESS;
        Utility.writeToDebug("getting trees for " + objectId);
        session = HibernateUtil.getSession();
        List<RandomForestGrove> rfGroves = PopulateDataObjects
                .getRandomForestGrovesByPredictorId(Long.parseLong(objectId),
                        session);

        randomForestTrees = new ArrayList<RandomForestTree>();
        if (rfGroves != null) {
            for (RandomForestGrove rfg : rfGroves) {
                ArrayList<RandomForestTree> rfTrees 
                            = (ArrayList<RandomForestTree>) PopulateDataObjects
                                             .getRandomForestTreesByGroveId(
                                                         rfg.getId(), session);
                if (isYRandomPage.equals(Constants.YES)
                        && rfg.getIsYRandomModel().equals(Constants.YES)
                        && rfTrees != null) {
                    randomForestTrees.addAll(rfTrees);
                }
                else if (isYRandomPage.equals(Constants.NO)
                        && rfg.getIsYRandomModel().equals(Constants.NO)
                        && rfTrees != null) {
                    randomForestTrees.addAll(rfTrees);
                }
            }
        }
        for (RandomForestTree rfTree : randomForestTrees) {
            String splitNumber = rfTree.getTreeFileName();
            if (splitNumber.split("_").length > 2) {
                splitNumber = splitNumber.split("_")[3];
            }
            rfTree.setTreeFileName(splitNumber);
        }
        session.close();
        return result;
    }

    private String loadTreeSets() throws Exception
    {
        String result = SUCCESS;
        String parentPredictorId = objectId;

        for (Predictor childPredictor : childPredictors) {
            objectId = "" + childPredictor.getId();
            result = loadTrees();
            if (!result.equals(SUCCESS)) {
                return result;
            }
        }
        objectId = parentPredictorId;
        return result;
    }

    private String loadGroveSets() throws Exception
    {
        String result = SUCCESS;
        String parentPredictorId = objectId;

        for (Predictor childPredictor : childPredictors) {
            objectId = "" + childPredictor.getId();
            result = loadGroves();
            if (!result.equals(SUCCESS)) {
                return result;
            }
        }
        objectId = parentPredictorId;
        return result;
    }

    public List<RandomForestGrove> getRandomForestGroves()
    {
        return randomForestGroves;
    }

    public void
            setRandomForestGroves(List<RandomForestGrove> randomForestGroves)
    {
        this.randomForestGroves = randomForestGroves;
    }

    public List<RandomForestTree> getRandomForestTrees()
    {
        return randomForestTrees;
    }

    public void
            setRandomForestTrees(List<RandomForestTree> randomForestTrees)
    {
        this.randomForestTrees = randomForestTrees;
    }

}