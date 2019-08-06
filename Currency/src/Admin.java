import authorisation.*;

import javax.xml.rpc.ServiceException;
import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/** Admin.jws
 *
 * Author: Greg Choice c9311718@uon.edu.au
 *
 * Created: 14/10/2018
 * Updated: 17/10/2018
 *
 * Description:
 * Admin is a Java Web Service used to add to and modify a currency conversion database
 *
 */
public class Admin
{
    // Simulates a database of currency conversions
    private HashMap<String, HashMap<String, Double>> currencyDB;

    /** addCurrency()
     *
     * Adds a new currency to the database
     *
     * @param sessionKey - String, Session key from the client
     * @param currencyCode - String, The currency to add to the database
     * @return boolean, Returns true if the currency was added, otherwise an invalid session key was submitted
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public boolean addCurrency(String sessionKey, String currencyCode) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Add currency
            currencyDB.put(currencyCode, new HashMap<>());

            // Save database from memory to file
            saveCurrencies();
            return true;
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** removeCurrency()
     *
     * Removes a specified currency from the database
     *
     * @param sessionKey - String, Session key from the client
     * @param currencyCode - String, The currency to remove
     * @return boolean, Returns true if the currency exists, false otherwise
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public boolean removeCurrency(String sessionKey, String currencyCode) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Check if the currency exists
            if(currencyDB.containsKey(currencyCode))
            {
                // Remove currency from reciprocal rates
                for(String rate : currencyDB.get(currencyCode).keySet())
                {
                    currencyDB.get(rate).remove(currencyCode);
                }

                // Remove currency from database
                currencyDB.remove(currencyCode);

                // Save database from memory to file
                saveCurrencies();
                return true;
            }
            else
                return false;
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** listCurrencies()
     *
     * Gets a list of all currencies in the database
     *
     * @param sessionKey - String, Session key from the client
     * @return String[], Returns a list of all existing currencies
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public String[] listCurrencies(String sessionKey) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            return currencyDB.keySet().toArray(new String[0]);
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** conversionsFor()
     *
     * Gets all the conversion rates for the specified currency
     *
     * @param sessionKey - String, Session key from the client
     * @param currencyCode - String, Currency to list conversions for
     * @return String[], A list of currency conversion rates for a specified currency
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public String[] conversionsFor(String sessionKey, String currencyCode) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Check that the currency exists in the database
            if(currencyDB.containsKey(currencyCode))
            {
                ArrayList<String> convert = new ArrayList<>(Arrays.asList(listConversions(currencyCode)));

                for(Entry<String, Double> conversion : currencyDB.get(currencyCode).entrySet())
                {
                    convert.add(String.format("%1$s-%2$s:%3$.4f", conversion.getKey(), currencyCode, 1/conversion.getValue()));
                }

                return convert.toArray(new String[0]);
            }
            else
                return new String[]{"Currency not in database."};

            /*if(currencyDB.containsKey(currencyCode))
                return listConversions(currencyCode);
            else
                return new String[]{"Currency not in database."};*/
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** addRate()
     *
     * Adds a conversion rate to an existing currency
     *
     * @param sessionKey - String, Session key from the client
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @param conversionRate - double, Conversion rate
     * @return boolean, Returns false if currencies don't exist, if conversion rate already exists or rate <= 0.0, true otherwise
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public boolean addRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double conversionRate) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Check if currencies don't exist, if conversion rate already exists or rate <= 0.0
            if(!currencyDB.containsKey(fromCurrencyCode) || !currencyDB.containsKey(toCurrencyCode) || currencyDB.get(fromCurrencyCode).containsKey(toCurrencyCode) || conversionRate <= 0.0)
            {
                return false;
            }
            else
            {
                // Add conversion rate
                enterRate(fromCurrencyCode, toCurrencyCode, conversionRate);
                return true;
            }
        }
        else
        {
            throw new SecurityException("Invalid session key");
        }
    }

    /** updateRate()
     *
     * Updates an existing conversion rate
     *
     * @param sessionKey - String, Session key from the client
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @param rate - double, Conversion rate
     * @return boolean, Returns false if currencies don't exist, conversion rate doesn't exist or rate <= 0.0, true otherwise
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public boolean updateRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode, double rate) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Check currencies exist, conversion rate exists and rate <= 0.0
            if(!currencyDB.containsKey(fromCurrencyCode) || !currencyDB.containsKey(toCurrencyCode) || !currencyDB.get(fromCurrencyCode).containsKey(toCurrencyCode) || rate <= 0.0)
            {
                return false;
            }
            else
            {
                // Upadate rates
                currencyDB.get(fromCurrencyCode).put(toCurrencyCode, rate);
                currencyDB.get(toCurrencyCode).put(fromCurrencyCode, 1 / rate);

                // Save database from memory to file
                saveCurrencies();
                return true;
            }
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** removeRate()
     *
     * Removes a specified conversion rate from the database
     *
     * @param sessionKey - String, Session key from the client
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @return boolean, Returns false if currencies don't exist or conversion rate doesn't exist, true otherwise
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public boolean removeRate(String sessionKey, String fromCurrencyCode, String toCurrencyCode) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Check currencies exist and rate exists
            if(!currencyDB.containsKey(fromCurrencyCode) || !currencyDB.containsKey(toCurrencyCode) || !currencyDB.get(fromCurrencyCode).containsKey(toCurrencyCode))
            {
                return false;
            }
            else
            {
                // Remove conversion rate
                currencyDB.get(fromCurrencyCode).remove(toCurrencyCode);
                currencyDB.get(toCurrencyCode).remove(fromCurrencyCode);

                // Save database from memory to file
                saveCurrencies();
                return true;
            }
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** listRates()
     *
     * @param sessionKey - String, Session key from the client
     * @return String[], List of all available conversion rates
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    public String[] listRates(String sessionKey) throws SecurityException
    {
        // Check session key
        if(authorise(sessionKey))
        {
            // Consolidate list of conversions for each currency to one list
            ArrayList<String> rates = new ArrayList<>();
            for(String currency : currencyDB.keySet())
            {
                rates.addAll(Arrays.asList(listConversions(currency)));
            }

            // Convert to string array
            return rates.toArray(new String[0]);
        }
        else
            throw new SecurityException("Invalid session key");
    }

    /** authorise()
     *
     * @param sessionKey - String, Session key from the client
     * @return boolean, Returns true if the session key is valid, false otherwise
     * @throws SecurityException, thrown when the session key cannot be verified
     */
    private boolean authorise(String sessionKey) throws SecurityException
    {
        try
        {
            // Instantiate the authorisation service
            AuthorisationService authorisationService = new AuthorisationServiceLocator();
            Authorisation_PortType authorisation = authorisationService.getAuthorisation();

            // Check session key
            if(authorisation.authorise(sessionKey))
            {
                // Load currency data file into memory
                loadCurrencies();
                return true;
            }
            else
            {
                return false;
            }
        }
        catch(RemoteException|ServiceException e)
        {
            throw new SecurityException("Couldn't reach authorisation service");
        }
    }

    /** loadCurrencies()
     *
     * Loads the currency data file into memory
     */
    private void loadCurrencies()
    {
        try(FileInputStream fileIn = new FileInputStream(System.getProperty( "catalina.base" )+"/webapps/currency/WEB-INF/currencyDB.ser");
            ObjectInputStream objIn = new ObjectInputStream(fileIn))
        {
            Object obj;

            // Check that the data file contains correct type
            if((obj = objIn.readObject()) instanceof HashMap)
                currencyDB = (HashMap)obj;
            else
                throw new ClassNotFoundException();
        }
        catch(IOException e)
        {
            // No data file found, create default currencies
            currencyDB = new HashMap<>();
            currencyDB.put("AUD", new HashMap<>());
            enterRate("AUD", "USD", 0.7);
            enterRate("AUD", "NZD", 1.09);
            enterRate("AUD", "GBP", 0.55);
            saveCurrencies();
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /** listConversions()
     *
     * Generates a list of available currency conversion rates
     *
     * @param currencyCode - String, Currency code supplied from the client
     * @return String[], An array of currency conversions or untradeable message
     */
    private String[] listConversions(String currencyCode)
    {
        // Check the currency can be traded
        if(!currencyDB.get(currencyCode).isEmpty())
        {
            // List all the conversion rates for the currency
            String[] list = new String[currencyDB.get(currencyCode).size()];
            int i = 0;
            for(Entry<String, Double> conversion : currencyDB.get(currencyCode).entrySet())
            {
                list[i] = String.format("%1$s-%2$s:%3$.4f", currencyCode, conversion.getKey(), conversion.getValue());
                i++;
            }
            return list;
        }
        else
            return new String[]{currencyCode + " cannot be traded."};
    }

    /** enterRate()
     *
     * Enters currency conversion rates and reciprocal rates into the database
     *
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @param rate - double, Conversion rate
     */
    private void enterRate(String fromCurrencyCode, String toCurrencyCode, double rate)
    {
        // Creates a new currency record for from currency (for default currencies only)
        if(!currencyDB.containsKey(fromCurrencyCode))
        {
            currencyDB.put(fromCurrencyCode, new HashMap<>());
        }
        // Creates a new currency record for to currency (for default currencies only)
        if(!currencyDB.containsKey(toCurrencyCode))
        {
            currencyDB.put(toCurrencyCode, new HashMap<>());
        }

        // Add the currency rates
        currencyDB.get(fromCurrencyCode).put(toCurrencyCode, rate);
        currencyDB.get(toCurrencyCode).put(fromCurrencyCode, 1/rate);

        // Saves currency database from memory to file
        saveCurrencies();
    }

    /** saveCurrencies()
     *
     * Saves database from memory to file
     */
    private void saveCurrencies()
    {
        // Write currencyDB to file
        try(FileOutputStream fileOut = new FileOutputStream(System.getProperty( "catalina.base" )+"/webapps/currency/WEB-INF/currencyDB.ser");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut))
        {
            objOut.writeObject(currencyDB);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
