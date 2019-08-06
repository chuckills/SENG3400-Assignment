import java.io.*;
import java.security.SecureRandom;
import java.util.*;

/** Login.jws
 *
 * Author: Greg Choice c9311718@uon.edu.au
 *
 * Created: 14/10/2018
 * Updated: 17/10/2018
 *
 * Description:
 * Login is a Java Web Service that verifies user login details
 *
 */
public class Login
{
    // Simulates a database of session keys
    private HashMap<String, String> keyDB;

    /** login()
     *
     * Validates a user login and gets a unique session key
     *
     * @param username - String, username sent from the client application
     * @param password - String, password sent from the client application
     * @return String, A unique session key if details are verified otherwise INVALID
     */
    public String login(String username, String password)
    {
        // Load keys from file
        loadKeys();

        // Check username and password match
        if(username.equals("hayden") && password.equals("1234") || username.equals("josh") && password.equals("4321"))
        {
            // Get a new session key
            String sessionKey = generateKey(username);

            // Add to user to login to database
            addUserLogin(sessionKey, username);

            return sessionKey;
        }
        else
            return "INVALID";
    }

    /** logout()
     *
     * Invalidates a session key association to a user
     *
     * @param key - String, Session key to invalidate
     * @return String, Returns true if a session key was invalidated, false otherwise
     */
    public boolean logout(String key)
    {
        // Load keys from file
        loadKeys();

        // Remove key record from database
        if(keyDB.remove(key) != null)
        {
            // Save database
            saveKeys();
            return true;
        }
        return false;
    }

    /** generateKey()
     *
     * Generates a random session key
     *
     * @param username - String, username sent from the client
     * @return String, User session key
     */
    private String generateKey(String username)
    {
        final String valid = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rand = new SecureRandom();
        StringBuilder sessionKey = new StringBuilder();

        int sum = 0;

        // Calculate sum of username characters
        for(char c : username.toCharArray())
        {
            sum += c;
        }

        // Generate random 5 character key
        for(int i = 0; i < 5; i++)
        {
            // Generate a random number between 0 and 62
            int randIndex = (rand.nextInt(valid.length())+sum)%62;

            // Select a character from the valid characters
            sessionKey.append(valid.charAt(randIndex));
        }
        return sessionKey.toString();
    }

    /** addUserLogin()
     *
     * Adds a new session key to the database for a user
     *
     * @param sessionKey - String, User's session key
     * @param username - String, Username from the client
     */
    private void addUserLogin(String sessionKey, String username)
    {
        // Check for existing user key to replace
        if(keyDB.containsValue(username))
        {
            // Remove existing key
            for(String userKey : keyDB.keySet())
            {
                if(keyDB.get(userKey).equals(username))
                {
                    keyDB.remove(userKey);
                    break;
                }
            }
        }

        // Add key and user to the user list
        keyDB.put(sessionKey, username);

        // Save database
        saveKeys();
    }

    /** saveKeys()
     *
     * Saves the key database from memory to file
     */
    private void saveKeys()
    {
        // Write keyDB to file
        try(FileOutputStream fileOut = new FileOutputStream(System.getProperty("catalina.base") + "/webapps/identity/WEB-INF/keyDB.ser");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut))
        {
            objOut.writeObject(keyDB);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
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
        catch(IOException e)
        {
            keyDB = new HashMap<>();
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
