package applet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Myroslav Sypa
 */
public class Compound {

      /**
     * <code>compound_id</code> - the id of the curent compond.
     */
    private String compound_id;
    /**
     * <code>image_path</code> - path to the image of the compound file 
     */
    private String image_path;
    /**
     * <code>value</code> - value of the compound 
     */
    private String value;

    
    public Compound(){
    }
    
    public Compound(String id, String path, String val){
        this.compound_id = id;
        this.image_path = path;
        this.value = val;
    }
    
    public String getCompound_id() {
        return compound_id;
    }

    public void setCompound_id(String compound_id) {
        this.compound_id = compound_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
            
    
    
    
}
