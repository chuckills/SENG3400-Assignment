import java.io.*;
import java.util.HashMap;

/** Authorisation.jws
 *
 * Author: Greg Choice c9311718@uon.edu.au
 *
 * Created: 14/10/2018
 * Updated: 17/10/2018
 *
 * Description:
 * Authorisation is a Java Web Service that verifies a user session key
 *
 */
public class Authorisation
{
    // Simulates a database of session keys
    private HashMap<String, String> keyDB;

    /** authorise()
     *
     * Checks that the key database contains the supplied session key
     *
     * @param key - String, Session key provided by the client
     * @return String, Returns true if the key is valid, false otherwise
     */
    public boolean authorise(String key)
    {
        // Load key data file
        loadKeys();

        // Check for the existence of valid key
        return keyDB.containsKey(key);
    }

    /** loadKeys()
     *
     * Loads the key database from file into memory
     */
    private void loadKeys()
    {
        try(FileInputStream fileIn = new FileInputStream(System.getProperty("catalina.base") + "/webapps/identity/WEB-INF/keyDB.ser");
            ObjectInputStream objIn = new ObjectInputStream(fileIn))
        {
            Object obj;

            // Check that data file contains correct type
            if((obj = objIn.readObject()) instanceof HashMap)
                keyDB = (HashMap)obj;
            else
                throw new ClassNotFoundException();
        }
        catch(IOException|ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
}