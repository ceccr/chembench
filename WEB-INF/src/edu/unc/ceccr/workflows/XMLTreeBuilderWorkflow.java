package edu.unc.ceccr.workflows;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import edu.unc.ceccr.utilities.Utility;

public class XMLTreeBuilderWorkflow {
	 private Vector<Vector<String>> data;
	    private String xml;
	    private String file_path;
	    private Double max;
	    private Vector<String> names = new Vector<String>();
	    private Double[][] data_map;
	    
	    public XMLTreeBuilderWorkflow(){
	    }
	    
	    public XMLTreeBuilderWorkflow(String  dataFile){
	    	this.file_path = dataFile;
	    }

	    public void setFilePath(String file_path){
	    	this.file_path = file_path;
	    }
	    
	    public void readFileToVector() throws Exception{
	    	this.data = Utility.readFileToVector("\t", this.file_path+".mat");
	    }
	    
	    public void formatData(){
	        Double[][] result = new Double[data.size()-1][data.size()-1];
	        for(int i=0;i<data.size()-1;i++){
	            names.add(((Vector<String>)data.get(0)).get(i+1));
	            for(int j=0;j<data.size()-1;j++){
	                result[i][j] = new Double(((Vector<String>)data.get(i+1)).get(j+1));
	            }
	        }
	        data_map = result; 
	    }
	    
	    public void formatData(Vector<String> names, Vector<Vector<Double>> data){
	    	Double[][] result = new Double[data.size()][data.size()];
	    	for(int i=0;i<data.size();i++){
	    		for(int j=0;j<data.size();j++){
	    			result[i][j] = new Double(data.get(i).get(j));
	    		}
	    	}
	    	this.names = names;
	    	data_map = result; 
	    }
	    
	 		    
   
	    private Integer[] findMax(Double[][] data){
	    	Integer[] key = new Integer[2];
	    	try{
	    	max = new Double(0);
	        for(int i=0;i<data.length;i++){
	            for(int j=0;j<data.length;j++){
	            	if(max<data[i][j] && i!=j){ 
	                    max = data[i][j];
	                    key[0] = i;
	                    key[1] = j;
	                }
	            }
	        }
	        }
	         catch(Exception e){
	        	 Utility.writeToMSDebug("-------"+e.getMessage());
	         }
	         
	        return key;

	    }
	    
	    private void createBaseXML(){ 
	        
	        xml="";
	        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	        		"<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">" +
	                "<graph edgedefault=\"undirected\">\n" +
	                "<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"+
	                "<!-- nodes -->\n";
	        xml+="<node id=\"****\">\n"+
            "<data key=\"name\">top</data>\n"+
            "</node>\n";  
	        for(int i=0;i<names.size();i++){
	        xml+= "<node id=\""+names.get(i)+"\">\n"+
	            "<data key=\"name\">"+names.get(i)+"</data>\n"+
	            "</node>\n";
	        
	        }
	        
	        
	    }
	    	    
	    private String buildXML(){
	        createBaseXML();
	        int id_iterator = data_map.length;
	        String newname="";
	        String name1 = "";
	       String name2 ="";
	        while(names.size()>2){
	        
	        Double[][] temp_map = data_map;
	        Integer[] key = findMax(data_map);
	       //combining closest compounds 
	        Double[][] matrix_without_keys = new Double[temp_map.length-1][temp_map.length-1];
	        int ii = 0;
	        
	        for(int i=0;i<data_map.length;i++){
	          int jj = 0;
	          for(int j=0;j<data_map.length;j++){
	            if(i!=key[0]&&i!=key[1]&&j!=key[0]&&j!=key[1]){
	                matrix_without_keys[ii][jj] = temp_map[i][j];
	                jj++;
	            }
	          }
	          if(i!=key[0]&&i!=key[1]) ii++;
	        }
	        //creating new record for similarity matrix
	        Double[] merged = new Double[matrix_without_keys.length];
	        ii=0;
	        for(int i=0;i<temp_map.length;i++){
	            if(temp_map[key[0]][i]<1.0 && temp_map[key[1]][i]<1.0){
	            	// searching max value between merging values
	                    merged[ii] = temp_map[key[0]][i]>=temp_map[key[1]][i]?temp_map[key[0]][i]:temp_map[key[1]][i];
	                    ii++;
	                }
	            
	        }
	        merged[merged.length-1] = 1.0;
	        
	        for(int i=0;i<matrix_without_keys.length;i++){
	            matrix_without_keys[matrix_without_keys.length-1][i] = merged[i];
	            matrix_without_keys[i][matrix_without_keys.length-1] = merged[i];
	        }
	        
	        data_map = matrix_without_keys;
	        
	        
	       /* for(int i=0;i<matrix_without_keys.length;i++){
	            System.out.println();
	           for(int j=0;j<matrix_without_keys.length;j++){
	               System.out.print(matrix_without_keys[i][j]+" ");
	           
	          }
	          }
	        */
	       
	        // removing keys from names vector - adding merged one
	       name1 = names.get(key[0]);
	       name2 = names.get(key[1]); 
	       newname = name1+name2;
	       names.remove(name1);
	       names.remove(name2);
	       names.add(newname);
	       
	        //// link two compound_id into the cluster
	        xml+="<node id=\""+newname+"\">\n"+
	            "<data key=\"name\"></data>\n"+
	            "</node>\n";    
	        xml+="<edge source=\""+newname+"\" target=\""+name1+"\"></edge>\n";
	        xml+="<edge source=\""+newname+"\" target=\""+name2+"\"></edge>\n";
	       
	       id_iterator++;
	        }
	        
	          
	        xml+="<edge source=\"****\" target=\""+names.get(0)+"\"></edge>\n";
	        xml+="<edge source=\"****\" target=\""+names.get(1)+"\"></edge>\n";
	        xml+="</graph></graphml>\n";
	        
	        return xml;
	    }
	    
	    public void writeXMLFile() throws IOException{
	    	String xml = buildXML();
	    	BufferedWriter out = new BufferedWriter(new FileWriter(this.file_path+".xml"));
	        out.write(xml);
	        out.close();
	    }
	    
}
