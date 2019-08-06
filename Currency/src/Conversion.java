import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

/** Conversion.jws
 *
 * Author: Greg Choice c9311718@uon.edu.au
 *
 * Created: 14/10/2018
 * Updated: 17/10/2018
 *
 * Description:
 * Conversion is a Java Web Service used to view and convert currencies
 *
 */
public class Conversion
{
    // Simulates a database of currency conversions
    private HashMap<String, HashMap<String, Double>> currencyDB;

    /** listRates()
     *
     * Lists all the available currency conversion rates
     *
     * @return String[], A list of all the available currency conversion rates
     */
    public String[] listRates()
    {
        // Load data file into memory
        loadCurrencies();

        // Consolidate list of conversions for each currency to one list
        ArrayList<String> rates = new ArrayList<>();
        for(String currency : currencyDB.keySet())
        {
            rates.addAll(Arrays.asList(listConversions(currency)));
        }

        // Convert to string array
        return rates.toArray(new String[0]);
    }

    /** rateOf()
     *
     * Shows the conversion rate of two currencies
     *
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @return double, The conversion rate of the two currencies
     */
    public double rateOf(String fromCurrencyCode, String toCurrencyCode)
    {
        // Load data file into memory
        loadCurrencies();

        // Check the currencies exist in the database
        if(currencyDB.containsKey(fromCurrencyCode) && currencyDB.get(fromCurrencyCode).containsKey(toCurrencyCode))
            return currencyDB.get(fromCurrencyCode).get(toCurrencyCode);
        else
            return -1.0;
    }

    /** convert()
     *
     * Converts an amount of currency from one form to another
     *
     * @param fromCurrencyCode - String, Currency to convert from
     * @param toCurrencyCode - String, Currency to convert to
     * @param amount - double, Amount of from currency to convert
     * @return double, The amount of to currency less service fee
     */
    public double convert(String fromCurrencyCode, String toCurrencyCode, double amount)
    {
        // Load data file into memory
        loadCurrencies();

        // Check the currencies exist in the database and are tradeable
        if(!currencyDB.get(fromCurrencyCode).isEmpty() && currencyDB.get(fromCurrencyCode).containsKey(toCurrencyCode))
        {
            return amount * currencyDB.get(fromCurrencyCode).get(toCurrencyCode) * 0.99;
        }
        else
            return -1.0;
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
        catch(IOException|ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
