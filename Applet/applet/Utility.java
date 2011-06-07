package applet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Vector;

/**
 *
 * @author Myroslav Sypa
 */
public class Utility {
    
     public static Vector<Vector<String>> readTable(String parameterData){
          
           Vector<Vector<String>> result = new Vector<Vector<String>>();
           if(parameterData==null || !parameterData.isEmpty()) throw new NullPointerException();
               
          String[] array = parameterData.split("~");
          for(int i=0;i<array.length;i++){
              Vector<String> temp = new Vector<String>();
              for(int j=0;j<array[i].split(",").length;j++){
                    temp.add(array[i].split(",")[j]);
              }
              result.add(temp);
          }
           
          return result;
                        
    }
           
public static Vector<Vector<String>> readFile(String parameterData) throws IOException{
    Vector<Vector<String>> result = null;	
    try{
            String line;
	result = new Vector<Vector<String>>();
	URL url = null;
	String url_ = parameterData;
	    
                url = new URL(url_);
            
                    InputStream in = url.openStream();
                    BufferedReader bf = new BufferedReader(new InputStreamReader(in));
                    while((line = bf.readLine()) != null){
                        //strBuff.append(line + "\n");
                        Vector<String> temp = new Vector<String>();
                        for (int j = 0; j < line.split("\t").length; j++) {
                            temp.add(line.split("\t")[j]);
                            }
                        result.add(temp);

                    }
            }
        catch(Exception ex){         
        	Utility.writeToDebug(ex);
            System.out.println("readFile returning null");
            return null;
        }
    
	//txtArea.append(strBuff.toString());
           
    
	return result;  
    
    }

        
public static String readXMLFile(String parameterData) throws IOException{
	String line;
	String result = new String();
	URL url = null;
	
	    String url_ = parameterData;
	    url = new URL(url_);
	
    try{
	InputStream in = url.openStream();
	BufferedReader bf = new BufferedReader(new InputStreamReader(in));
	
	while((line = bf.readLine()) != null){
	    //strBuff.append(line + "\n");
            
            result+=line;
                
	}
    }
      catch(Exception ex){   
    	  Utility.writeToDebug(ex);
            System.out.println("readXMLFile returning null");
            return null;
            }
	//txtArea.append(strBuff.toString());
           
    
	return result;  
    
    }
}
