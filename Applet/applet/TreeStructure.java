package applet;


import java.util.LinkedHashMap;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Myroslav Sypa
 */
public class TreeStructure {
    //input data
    private Vector<Vector<String>> data;
    private String xml;
    private Double max;
    private Vector<String> names = new Vector<String>();
    
    public TreeStructure(Vector<Vector<String>> data){
        this.data = data;
    }

    private Double[][] formatData(){
        //LinkedHashMap<String,Vector<Double>> result = new LinkedHashMap<String, Vector<Double>>();
        // Vector<Vector<String>> temp_data = this.data;
        Double[][] result = new Double[data.size()-1][data.size()-1];
        /*temp_data.remove(0);
            Iterator<Vector<String>> i = (Iterator<Vector<String>>)temp_data.iterator();
        while(i.hasNext()){
            Vector<String> vs = i.next();
            System.out.println(vs.toString());
            if(vs.size()>0) result.put((String)vs.get(0), convertStringsToDoubles(vs));
        }*/
        for(int i=0;i<data.size()-1;i++){
            names.add(((Vector<String>)data.get(0)).get(i+1));
            System.out.println();
            for(int j=0;j<data.size()-1;j++){
                result[i][j] = new Double(((Vector<String>)data.get(i+1)).get(j+1));
                System.out.print(result[i][j]+" ");
            }
        }
        System.out.println(">>>>>>>>"+names.toString());
        return result;
    }
    
    private Vector<Double> convertStringsToDoubles(Vector<String> data){
        Vector<Double> results = new Vector<Double>();
        for(int i=1;i<data.size();i++){
            results.add(new Double(data.get(i)));
            //if(data.get(i).equals("1")) break;
        }
        return results;
    }
    
    private Integer[] findMax(Double[][] data){
        /*max = new Double(0);
        String[] key = new String[2];
        for(int i =0;i< data_map.values().size(); i++){
            Vector<Double> tv = ((Vector<Double>) data_map.values().toArray()[i]);
            String current_key = (String) data_map.keySet().toArray()[i];
            for(int j=0;j<tv.size();j++)
            if(tv.get(j)>=max && tv.get(j)<1){ 
                max = tv.get(j);
                key[0] = current_key;
                key[1] = (String) data_map.keySet().toArray()[j];
            }
          */
        max = new Double(0);
        Integer[] key = new Integer[2];
         for(int i=0;i<data.length;i++){
            for(int j=0;j<data.length;j++){
                if(max<data[i][j] && i!=j){ 
                    max = data[i][j];
                    key[0] = i;
                    key[1] = j;
                }
            }
        }
        System.out.println(key[0]+"=="+key[1]);
        return key;

    }
    
    private LinkedHashMap<String,Integer> createBaseXMLAndCompoundIdMap(){ 
        LinkedHashMap<String, Integer> comp_id_id = new LinkedHashMap<String, Integer>();
        xml="";
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!--  An excerpt of an egocentric social network  --><graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">" +
                "<graph edgedefault=\"undirected\">\n<!-- data schema -->\n" +
                "<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\"/>\n"+
                "<!-- nodes -->\n";
        for(int i=0;i<names.size();i++){
        xml+= "<node id=\""+names.get(i)+"\">\n"+
            "<data key=\"name\">"+names.get(i)+"</data>\n"+
            "</node>\n";
        comp_id_id.put(names.get(i), i);
        }
        
        return comp_id_id;
    }
    
    
    public void build(){
        Double[][] data_map = formatData();
        LinkedHashMap<String,Integer> map = createBaseXMLAndCompoundIdMap();
        int id_iterator = data_map.length;
        String newname="";
        String name1 = "";
       String name2 ="";
        while(names.size()>2){
        System.out.println("*********************************************");
        Double[][] temp_map = data_map;
        
        
        Integer[] key = findMax(data_map);
        
        
       //combining closest compuonds 
        ///
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
        
        Double[] merged = new Double[matrix_without_keys.length];
        ii=0;
        for(int i=0;i<temp_map.length;i++){
            if(temp_map[key[0]][i]<1.0 && temp_map[key[1]][i]<1.0){
                    merged[ii] = temp_map[key[0]][i]>=temp_map[key[1]][i]?temp_map[key[0]][i]:temp_map[key[1]][i];
                    System.out.println(">>"+merged[ii]);
                    ii++;
                }
            
        }
        merged[merged.length-1] = 1.0;
        
        for(int i=0;i<matrix_without_keys.length;i++){
            matrix_without_keys[matrix_without_keys.length-1][i] = merged[i];
            matrix_without_keys[i][matrix_without_keys.length-1] = merged[i];
        }
        
        data_map = matrix_without_keys;
        
        
        for(int i=0;i<matrix_without_keys.length;i++){
            System.out.println();
           for(int j=0;j<matrix_without_keys.length;j++){
               System.out.print(matrix_without_keys[i][j]+" ");
           
          }
          }
        
       
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
       
       System.out.println("***********************************************");
       System.out.println(name1+":"+name2);
       id_iterator++;
        }
        
        xml+="<node id=\"*\">\n"+
            "<data key=\"name\">top</data>\n"+
            "</node>\n";    
        xml+="<edge source=\"*\" target=\""+names.get(0)+"\"></edge>\n";
        xml+="<edge source=\"*\" target=\""+names.get(1)+"\"></edge>\n";
        xml+="</graph></graphml>\n";
        
        System.out.println(xml);
    }

}
