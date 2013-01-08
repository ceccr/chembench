package edu.unc.ceccr.utilities;

public class WhenServerStarts {
    
    private boolean hasBeenCalledBefore = false;
    
    public WhenServerStarts(){
        
    }
    
    public void OnStartServer(){
        //calls all the functions that need calling on server start. 
        
        //Return if called already
        if(hasBeenCalledBefore){
            return;
        }
        else{
            hasBeenCalledBefore = true;
        }
        
        
    }
    
    private void loadConfigFile(){
    	// TODO method stub
    }
    
    private void testDatabaseConnection(){
    	// TODO method stub
    }

    private void loadJobListsAndStartThreads(){
    	// TODO method stub
    }
    
}