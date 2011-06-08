package edu.unc.ceccr.calculations;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.visualization.XMLTreeBuilder;


public class TanimotoDistanceMeasure extends DistanceMeasure{

	public TanimotoDistanceMeasure(String userName, String datasetName, String sdfName) throws Exception {
		super(userName, datasetName, sdfName);
		readData();
	}
	
    public TanimotoDistanceMeasure() {
	}

	@Override
public void performMatrixCreation() throws IOException{
		Vector<Vector<String>> res = new Vector<Vector<String>>();
                distance_matrix = new Vector<Vector<Double>>();
		
		for(int k=0;k<bit_matrix.size();k++){
			Vector<String> row = new Vector<String>();
                        Vector<Double> row_d = new Vector<Double>();
			for(int i=0;i<bit_matrix.size();i++){
				double sumA = 0.0;
				double sumB = 0.0;
				double sumC = 0.0;
				for(int j=0;j<bit_matrix.get(i).size();j++){
					//double original = new Double(data.get(k).get(j)).doubleValue();
					//double competitor = new Double(data.get(i).get(j)).doubleValue();
					Double original = bit_matrix.get(k).get(j);
					Double competitor = bit_matrix.get(i).get(j);
					sumA+=original;
					sumB+=competitor;
					if(original.equals(1.0) && competitor.equals(1.0)) sumC+=1.0;
				}
				double div = (sumA+sumB-sumC);
				Double similarity = 0.0;
				if(div==0.0) similarity=0.0;
				else similarity= sumC/div;
				row.add(similarity.toString());			
                                row_d.add(similarity);
			}
                       res.add(row);
                        distance_matrix.add(row_d);
		}
		
	writeMATFile(res,"tan");
	//performXMLCreation("tan");
                
             
			
}
	
	public void performXMLCreation() throws IOException{
		XMLTreeBuilder xml_builder = new XMLTreeBuilder();
     	xml_builder.setFilePath(file_path+"_tan");
     	xml_builder.formatData(new Vector<String>(Arrays.asList(names)), distance_matrix);
     	xml_builder.writeXMLFile();
    }
	
	

}
