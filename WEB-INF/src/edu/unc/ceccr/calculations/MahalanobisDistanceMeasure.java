package edu.unc.ceccr.calculations;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import edu.unc.ceccr.utilities.Utility;
import edu.unc.ceccr.workflows.visualization.XMLTreeBuilder;


public class MahalanobisDistanceMeasure extends DistanceMeasure{

	
	public MahalanobisDistanceMeasure(String userName, String datasetName, String sdfName)
			throws Exception {
		super(userName, datasetName, sdfName);
		readData();
	}
	
	
	public MahalanobisDistanceMeasure() {
		
	}

	private double mean(double[] vector){
            double res = 0.0;
            for(int i=0;i<vector.length;i++)
                res += vector[i];
            return res/vector.length;
        }
        
        private Double[] centerVector(double[] vector){
            Double mean = mean(vector);
            Double[] result = new Double[vector.length];
            for(int i=0;i<vector.length;i++)
                result[i] = vector[i]-mean;
            return result;
        }
        
        private double[][] multiplyMatrix(double[][] m1, double[][] m2){
                      int m1rows = m1.length;
                    int m1cols = m1[0].length;
                    int m2rows = m2.length;
                    int m2cols = m2[0].length;
                    if (m1cols != m2rows)
                      throw new IllegalArgumentException("matrices don't match: " + m1cols + " != " + m2rows);
                    double[][] result = new double[m1rows][m2cols];

                    // multiply
                    for (int i=0; i<m1rows; i++)
                      for (int j=0; j<m2cols; j++)
                        for (int k=0; k<m1cols; k++)
                        result[i][j] += m1[i][k] * m2[k][j];
                    return result;
          
        }

        private double multiplyVectors(double[] v1, double[] v2){
            return v1[0]*v2[0]+v1[1]*v2[1];                    
        }
        
        private double[][] calculateCovarianceMatrix(double[][] m1, double[][] m2){
                 double[][] result = multiplyMatrix(m1, m2);
                 int m1rows = result.length; 
                 int m1cols = result[0].length;
                 for(int i =0;i<m1rows;i++)
                        for(int j=0;j<m1cols;j++)
                            result[i][j] = result[i][j]/m1rows;
                    
                    return result;
        }
        
        private double[][] transpose(double[][] matrix){
            double[][] result = new double[matrix[0].length][matrix.length];
            for(int i = 0; i< matrix.length;i++){
                for(int j=0;j<matrix[0].length;j++)
                {
                    result[j][i] = matrix[i][j];
                }
            }
            return result;
        }
        
        private double[] multiplyVectorOnMatrix(double[] v1, double[][] mat){
            double[] rv = new double[v1.length];    
            
            for(int i =0;i<v1.length;i++){
                for(int j=0;j<mat.length;j++){
                       rv[i] += v1[i] * mat[j][i];
                }
                }
            return rv;
        }
        
          private double[][] invert(double a[][]) {
              double[][] res = new double[2][2];
              double det = 1/(a[0][0]*a[1][1]- a[0][1]*a[1][0]);
              res[0][0] = a[1][1]*det;
              res[0][1] = -a[0][1]*det;
              res[1][0] = -a[1][0]*det;
              res[1][1] = a[0][0]*det;
              return res;
  }

private double[] convertVectorToDouble(Vector<Double> v){
      double[] res = new double[v.size()];
      for(int i=0;i<v.size();i++){
      res[i] = v.get(i);
      }
      return res;
  }
    @Override
public void performMatrixCreation() throws IOException{
    	Vector<Vector<Double>> res_d = new Vector<Vector<Double>>();
		double min = 1000000000;
                for(int k=0;k<bit_matrix.size();k++){
                    Double similarity = new Double(0);
			Vector<Double> row = new Vector<Double>();
			for(int i=0;i<bit_matrix.size();i++){
                          if(i!=k){  double[][] two_vectro_mat = new double[2][bit_matrix.get(0).size()];
                            two_vectro_mat[0] = /*centerVector(*/convertVectorToDouble(bit_matrix.get(k))/*)*/;
                            two_vectro_mat[1] = /*centerVector(*/convertVectorToDouble(bit_matrix.get(i))/*)*/;
                            double[][] cov_mat = calculateCovarianceMatrix(two_vectro_mat, transpose(two_vectro_mat));
                            double[][] inv_mat = invert(cov_mat);
                            double[] d_vec = multiplyVectorOnMatrix(new double[]{mean(two_vectro_mat[0]),mean(two_vectro_mat[1])},inv_mat);
                            similarity = new Double(multiplyVectors(d_vec,new double[]{mean(two_vectro_mat[0]),mean(two_vectro_mat[1])}));
                          }
                            
                                if(min>similarity && similarity>0.0) min = similarity;
                          if(similarity.equals(Double.NaN)){
                              similarity = new Double(0.0);
                          }
                                row.add(similarity);			
			}
			res_d.add(row);
		}
                
                
		writeMATFile(normalize(res_d,min),"mah");
                //writeMATFile(res_d,"mah");
		//performXMLCreation("mah");
}
	 
    private Vector<Vector<String>> normalize(Vector<Vector<Double>> inv, double min){
      Vector<Vector<String>> res = new Vector<Vector<String>>();
      distance_matrix = new Vector<Vector<Double>>();
        for(int i=0;i<inv.size();i++){
            Vector<String> row = new Vector<String>();
            Vector<Double> row_d = new Vector<Double>();
        	for(int j=0;j<inv.get(i).size();j++){
                    double temp = 1.0;
                    if(i!=j){
			if(inv.get(i).get(j)!=0.0){
                             temp = min/inv.get(i).get(j);
                        }
                        else{ 
                          temp = 1.0;  
                        }
                    }
                                row.add(new Double(temp).toString());			
                                row_d.add(temp);
			}
            res.add(row);
            distance_matrix.add(row_d);
		}
      return res;
    }
    
    public void performXMLCreation() throws IOException{
    	XMLTreeBuilder xml_builder = new XMLTreeBuilder();
     	xml_builder.setFilePath(file_path+"_mah");
     	xml_builder.formatData(new Vector<String>(Arrays.asList(names)), distance_matrix);
     	xml_builder.writeXMLFile();
    }

}
