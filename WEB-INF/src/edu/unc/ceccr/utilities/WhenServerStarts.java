package edu.unc.ceccr.utilities;

public class WhenServerStarts {

    private boolean hasBeenCalledBefore = false;

    public WhenServerStarts() {

    }

    public void OnStartServer() {
        // calls all the functions that need calling on server start.

        // Return if called already
        if (hasBeenCalledBefore) {
            return;
        } else {
            hasBeenCalledBefore = true;
        }

    }
    /*
    private void loadConfigFile()
    {

    }

    private void testDatabaseConnection()
    {

    }

    private void loadJobListsAndStartThreads()
    {

    }
    */

}