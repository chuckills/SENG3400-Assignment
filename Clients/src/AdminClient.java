import identity.login.*;
import currency.admin.*;

import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import java.io.Console;
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
public class AdminClient
{
    /** run()
     *
     * @param user - String, Username from the command line
     */
    private void run(String user)
    {
        try
        {
            // Instantiate the Login service
            LoginService loginService = new LoginServiceLocator();
            Login_PortType login = loginService.getLogin();

            // Instantiate console
            Console console = System.console();
            if (console == null)
            {
                System.out.println("Couldn't get Console instance");
                System.exit(0);
            }

            System.out.println("Cheersy Change Admin Client");
            System.out.println("===========================\n");

            // 3 tries at correct password
            for(int i = 0; i < 3; i++)
            {
                String sessionKey;

                // Enter masked password
                if(i == 0)
                    sessionKey = login.login(user, new String(console.readPassword("Enter password >> ")));
                else
                {
                    System.out.print("Enter username >> ");
                    user = console.readLine();
                    sessionKey = login.login(user, new String(console.readPassword("Enter password >> ")));
                }

                if(sessionKey.equals("INVALID"))
                    // Unsuccessful login attempt
                    System.out.println("Error: Login incorrect - " + (2-i) + " attempts left.\n");
                else
                {
                    // Successful password attempt
                    System.out.println("Welcome, " + user);
                    String[] command;

                    // Instantiate Admin service
                    AdminService adminService = new AdminServiceLocator();
                    Admin_PortType admin = adminService.getAdmin();

                    // Loop until logout command entered
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
                            case "addCurrency":
                                // Check the command has the right number of segments
                                if(command.length != 2)
                                {
                                    System.out.println("Command format invalid. Usage: addCurrency <AAA>");
                                    break;
                                }
                                // Check currency code is uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    // Check result of adding currency
                                    if(admin.addCurrency(sessionKey, command[1]))
                                        System.out.println("Currency " + command[1] + " added.");
                                    else
                                        System.out.println("Currency not added. Currency already exists.");
                                }
                                else
                                    System.out.println("Invalid currency code. (" + command[1] + ")");
                                break;

                            case "removeCurrency":
                                // Check the command has the right number of segments
                                if(command.length != 2)
                                {
                                    System.out.println("Command format invalid. Usage: removeCurrency <AAA>");
                                    break;
                                }
                                // Check currency code is uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    // Check result of removing currency
                                    if(admin.removeCurrency(sessionKey, command[1]))
                                        System.out.println("Currency " + command[1] + " removed.");
                                    else
                                        System.out.println("Currency not removed. Currency not in list.");
                                }
                                else
                                    System.out.println("Invalid currency code. (" + command[1] + ")");
                                break;

                            case "listCurrencies":
                                // Check the command has the right number of segments
                                if(command.length != 1)
                                {
                                    System.out.println("Command format invalid. Usage: listCurrencies");
                                    break;
                                }
                                // List all the currencies
                                for(String currency : admin.listCurrencies(sessionKey))
                                {
                                    System.out.println(currency);
                                }
                                break;

                            case "conversionsFor":
                                // Check the command has the right number of segments
                                if(command.length != 2)
                                {
                                    System.out.println("Command format invalid. Usage: conversionsFor <AAA>");
                                    break;
                                }
                                // Check currency code is uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    // List all the rates for the specified currency
                                    for(String conversion : admin.conversionsFor(sessionKey, command[1]))
                                    {
                                        System.out.println(conversion);
                                    }
                                }
                                else
                                    System.out.println("Invalid currency code. (" + command[1] + ")");
                                break;

                            case "addRate":
                                // Check the command has the right number of segments
                                if(command.length != 4)
                                {
                                    System.out.println("Command format invalid. Usage: addRate <AAA> <BBB> <rate>");
                                    break;
                                }
                                // Check currency codes are uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    if(isValidCode(command[2]))
                                    {
                                        try
                                        {
                                            double rate = Double.parseDouble(command[3]);

                                            // Check the result of adding the rate
                                            if(admin.addRate(sessionKey, command[1], command[2], rate))
                                                System.out.println("Rate " + command[1] + "-" + command[2] + ":" + rate + " added.");
                                            else
                                            {
                                                System.out.print("Rate not added. ");
                                                if(rate <= 0.0)
                                                    System.out.println("Conversion rate must be positive.");
                                                else
                                                    System.out.println("Currency not in list.");
                                            }
                                        }
                                        catch(NumberFormatException e)
                                        {
                                            System.out.println("Conversion rate invalid.");
                                        }
                                    }
                                    else
                                        System.out.println("Invalid currency code. (" + command[2] + ")");
                                }
                                else
                                    System.out.println("Invalid currency code. (" + command[1] + ")");
                                break;

                            case "updateRate":
                                // Check the command has the right number of segments
                                if(command.length != 4)
                                {
                                    System.out.println("Command format invalid. Usage: updateRate <AAA> <BBB> <rate>");
                                    break;
                                }
                                // Check currency codes are uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    if(isValidCode(command[2]))
                                    {
                                        try
                                        {
                                            double rate = Double.parseDouble(command[3]);

                                            // Check result of updating the rate
                                            if(admin.updateRate(sessionKey, command[1], command[2], rate))
                                                System.out.println("Rate " + command[1] + "-" + command[2] + ":" + rate + " updated.");
                                            else
                                            {
                                                System.out.print("Rate not updated. ");
                                                if(rate <= 0.0)
                                                    System.out.println("Conversion rate must be positive.");
                                                else
                                                    System.out.println("Currency rate not defined.");
                                            }
                                        }
                                        catch(NumberFormatException e)
                                        {
                                            System.out.println("Conversion rate invalid.");
                                        }
                                    }
                                    System.out.println("Invalid currency code. (" + command[2] + ")");
                                }
                                else
                                    System.out.println("Invalid currency code. (" + command[1] + ")");
                                break;

                            case "removeRate":
                                // Check the command has the right number of segments
                                if(command.length != 3)
                                {
                                    System.out.println("Command format invalid. Usage: removeRate <AAA> <BBB>");
                                    break;
                                }
                                // Check currency codes are uppercase and 3 characters long
                                if(isValidCode(command[1]))
                                {
                                    if(isValidCode(command[2]))
                                    {
                                        // Check the result of removing the rate
                                        if(admin.removeRate(sessionKey, command[1], command[2]))
                                            System.out.println("Rate " + command[1] + "-" + command[2] + " removed.");
                                        else
                                            System.out.print("Currency rate not defined.");
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
                                // List all the rates in the database
                                for(String rate : admin.listRates(sessionKey))
                                {
                                    System.out.println(rate);
                                }
                                break;

                            case "logout":
                                // Check the command has the right number of segments
                                if(command.length != 1)
                                {
                                    System.out.println("Command format invalid. Usage: logout");
                                    command[0] = "";
                                    break;
                                }
                                System.out.print("Goodbye " + user + "!");
                                break;

                            default:
                                System.out.println("\nError: Invalid command.");
                        }
                    }
                    while(!command[0].equalsIgnoreCase("logout"));
                    break;
                }
            }
        }
        catch(RemoteException|ServiceException e)
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
     * @param args - String[], Username only
     */
    public static void main(String[] args)
    {
        if(args.length == 1)
        {
            AdminClient admin = new AdminClient();
            admin.run(args[0]);
        }
        else
            System.out.println("Error: Usage: java AdminClient <username>");
    }
}
