import currency.conversion.*;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.util.Scanner;

/** CurrencyClient.java
 *
 * Author: Greg Choice c9311718@uon.edu.au
 *
 * Created: 14/10/2018
 * Updated: 17/10/2018
 *
 * Description:
 * CurrencyClient is a user client application to view and convert currencies
 *
 */
public class CurrencyClient
{
    /** run()
     *
     */
    private void run()
    {
        System.out.println("Cheersy Change Conversion Client");
        System.out.println("================================");

        try
        {
            String[] command;

            // Instantiate Conversion service
            ConversionService conversionService = new ConversionServiceLocator();
            Conversion_PortType conversion = conversionService.getConversion();

            // Loop until user enters exit
            do
            {
                // Get command from console
                command = getCommand();

                // Check only one space between command segments
                boolean spaceCheck = true;
                for(String part : command)
                {
                    if(part.endsWith(" "))
                    {
                        System.out.println("Error: Too many spaces after " + part.trim());
                        spaceCheck = false;
                    }
                }
                if(!spaceCheck)
                    continue;

                // Verify and invoke service call from command
                switch(command[0])
                {
                    case "convert":
                        // Check the command has the right number of segments
                        if(command.length != 4)
                        {
                            System.out.println("Command format invalid. Usage: convert <AAA> <BBB> <amount>");
                            break;
                        }

                        double amount, exchange;

                        // Check currency code is uppercase and 3 characters long
                        if(isValidCode(command[1]))
                        {
                            if(isValidCode(command[2]))
                            {
                                try
                                {
                                    amount = Double.parseDouble(command[3]);
                                    exchange = conversion.convert(command[1], command[2], amount);

                                    // Check result of conversion
                                    if(exchange == -1.0)
                                    {
                                        System.out.println("Rate conversion not valid.");
                                    }
                                    else
                                        System.out.println(String.format("%1$.4f %2$s = %3$s %4$.4f", amount, command[1], command[2], exchange));
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Invalid amount specified.");
                                }
                            }
                            else
                                System.out.println("Invalid currency code. (" + command[2] + ")");
                        }
                        else
                            System.out.println("Invalid currency code. (" + command[1] + ")");
                        break;

                    case "rateOf":
                        // Check the command has the right number of segments
                        if(command.length != 4)
                        {
                            System.out.println("Command format invalid. Usage: rateOf <AAA> <BBB>");
                            break;
                        }

                        // Check currency code is uppercase and 3 characters long
                        if(isValidCode(command[1]))
                        {
                            if(isValidCode(command[2]))
                            {
                                System.out.println(conversion.rateOf(command[1], command[2]));
                            }
                            else
                                System.out.println("Invalid currency code. (" + command[2] + ")");
                        }
                        else
                            System.out.println("Invalid currency code. (" + command[1] + ")");
                        break;

                    case "listRates":
                        // Check the command has the right number of segments
                        if(command.length != 1)
                        {
                            System.out.println("Command format invalid. Usage: listRates");
                            break;
                        }
                        // List all conversion rates
                        for(String rate : conversion.listRates())
                        {
                            System.out.println(rate);
                        }
                        break;

                    case "exit":
                        // Check the command has the right number of segments
                        if(command.length != 1)
                        {
                            System.out.println("Command format invalid. Usage: exit");
                            command[0] = "";
                            break;
                        }
                        System.out.println("Goodbye!");
                        break;

                    default:
                        System.out.println("\nError: Invalid command.");
                }
            }
            while(!command[0].equals("exit"));
        }
        catch(ServiceException|RemoteException e)
        {
            System.out.println(e.getMessage());
        }
    }

    /** getCommand()
     *
     * Gets a user command from the console and splits it into word segments
     *
     * @return String[], An array of separated command segments
     */
    private String[] getCommand()
    {
        Scanner input = new Scanner(System.in);
        System.out.print("\nWhat would you like to do >> ");

        // Split command into word segments
        return input.nextLine().split("([ ](?![ ]))");
    }

    /** isValidCode()
     *
     * Checks that the currency code is in the correct format
     *
     * @param currencyCode - A currency code entered by the user
     * @return - boolean, Returns true if the code is valid, false otherwise
     */
    private boolean isValidCode(String currencyCode)
    {
        char[] chars = currencyCode.toCharArray();
        return chars.length == 3 && (Character.isUpperCase(chars[0]) || Character.isUpperCase(chars[0]) || Character.isUpperCase(chars[0]));
    }

    /** main()
     *
     * @param args - String[], No arguments required
     */
    public static void main(String[] args)
    {
        CurrencyClient client = new CurrencyClient();
        client.run();
    }
}
