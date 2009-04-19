package edu.unc.ceccr.global;

import java.util.Comparator;

import edu.unc.ceccr.persistence.ModelInterface;

public class CategoryKNNComparator implements Comparator {

	public int compare(Object one, Object two) {
		ModelInterface knnOutputOne = (ModelInterface)one;
		ModelInterface knnOutputTwo = (ModelInterface)two;
		
		Float knnOutputOneDouble = knnOutputOne.getNormalizedTestAcc();
		Float knnOutputTwoDouble = knnOutputTwo.getNormalizedTestAcc();
		//double difference = knnOutputOneDouble.doubleValue() - knnOutputTwoDouble.doubleValue();
		return (knnOutputOneDouble.compareTo(knnOutputTwoDouble));
	}

}