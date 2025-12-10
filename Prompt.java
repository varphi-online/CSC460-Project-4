import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
 * Class Name: Prompt
 *
 * Purpose: 
 * This class serves as a utility support class to handle console-based user input.
 * It provides methods to prompt the user for various data types (String, Integer, 
 * Date, Timestamp, Boolean, etc.), validating the input formats and handling 
 * default values.
 * 
 * Class Constants and Variables:
 * ExitFormError : RuntimeException - An exception thrown when the user types "exit" to cancel an operation.
 * 
 * Constructors:
 * None.
 * 
 * Implemented Class Methods:
 * string(String, String)
 * stringNullable(String, String)
 * choice(String, String[])
 * integer(String, Integer)
 * integerNullable(String, Integer)
 * date(String, String, Date)
 * timestamp(String, String, Timestamp)
 * time(String, String)
 * bool(String, Boolean)
 */
public class Prompt {
    private static final Scanner scanner = Prog4.getScanner(); // Scanner instance retrieved from the main program for input reading.
    public static final RuntimeException ExitFormError = new RuntimeException("Cancelled."); // Exception instance used to signal a user cancellation.

    /*
     * Method Name: string
     * Purpose: Prompts the user for a string input. If a default (current) value is provided,
     *          the user may leave the input blank to use the default. If no default is provided,
     *          non-blank input is enforced.
     * Pre-conditions: System.in is available via the scanner.
     * Post-conditions: Input is consumed from the scanner. Throws ExitFormError if "exit" is typed.
     * Return Value: String - The validated user input or the default value.
     * Parameters:
     *     label (in)   - Description of the required input to display to the user.
     *     current (in) - The default string value (use "" or null for no default).
     */
    public static String string(String label, String current) {
        while (true) {
            System.out.printf("Enter %s %s: ", label,
                    current == null || current.isBlank() ? "" : "(leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            if (input.isBlank()) {
                if (current == null || current.isBlank()) {
                    System.out.println("Error: Must give a non-blank input.");
                    continue;
                }
                return current;
            }
            return input;
        }
    }

    /*
     * Method Name: stringNullable
     * Purpose: Prompts the user for a string input, allowing a null result.
     *          If the input is blank, returns the default value or null.
     * Pre-conditions: System.in is available via the scanner.
     * Post-conditions: Input is consumed from the scanner. Throws ExitFormError if "exit" is typed.
     * Return Value: String - The user input, the default value, or null.
     * Parameters:
     *     label (in)   - Description of the required input to display to the user.
     *     current (in) - The default string value (use "" or null for no default).
     */
    public static String stringNullable(String label, String current) {
        System.out.printf("Enter %s %s: ",
                label,
                current == null || current.isBlank()
                        ? "(leave blank for null)"
                        : "(leave blank for default: " + current + ")");
        var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
        if (input.isBlank()) {
            return current == null || current.isBlank() ? null : current;
        }
        return input;
    }

    /*
     * Method Name: choice
     * Purpose: Prompts the user to select a string from a specific list of valid choices.
     *          Input is case-insensitive.
     * Pre-conditions: The choices array must not be empty.
     * Post-conditions: Input is consumed. Loops until valid input is received.
     * Return Value: String - The string from the choices array matching the user's input.
     * Parameters:
     *     label (in)   - Description of the required input.
     *     choices (in) - Array of valid string options (can include null).
     */
    public static String choice(String label, String[] choices) {
        var originalList = Arrays.asList(choices);
        boolean allowNull = originalList.contains(null);
        var lowercaseList = new ArrayList<>(originalList);
        lowercaseList.replaceAll(str -> str == null ? null : str.toLowerCase());

        var choiceList = new ArrayList<>(originalList);
        if (allowNull) {
            choiceList.remove(choiceList.indexOf(null));
        }
        while (true) {
            System.out.printf("Enter %s (Options: %s)%s: ",
                    label,
                    choiceList.toString(),
                    allowNull ? " (leave blank for none)" : "");
            var input = scanner.nextLine().trim().toLowerCase();
            if (input.isBlank() && allowNull) {
                return null;
            }
            if (lowercaseList.contains(input)) {
                return originalList.get(lowercaseList.indexOf(input));
            }
            System.out.println("Error: Invalid input.");
        }
    }

    /*
     * Method Name: integer
     * Purpose: Prompts the user for an integer input. Enforces valid integer format
     *          and requires input if no default is provided.
     * Pre-conditions: System.in is available.
     * Post-conditions: Input is consumed. Loops on NumberFormatException. Throws ExitFormError if "exit" is typed.
     * Return Value: Integer - The parsed integer or default value.
     * Parameters:
     *     label (in)   - Description of the required input.
     *     current (in) - Default integer value (use null for no default).
     */
    public static Integer integer(String label, Integer current) {
        while (true) {
            System.out.printf("Enter %s%s: ", label,
                    current == null
                            ? ""
                            : " (leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            if (input.isBlank() && current != null) {
                return current;
            }
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid format.");
            }
        }
    }

    /*
     * Method Name: integerNullable
     * Purpose: Prompts the user for an integer input, allowing a null result if the input is blank.
     * Pre-conditions: System.in is available.
     * Post-conditions: Input is consumed. Loops on NumberFormatException. Throws ExitFormError if "exit" is typed.
     * Return Value: Integer - The parsed integer, default value, or null.
     * Parameters:
     *     label (in)   - Description of the required input.
     *     current (in) - Default integer value (use null for no default).
     */
    public static Integer integerNullable(String label, Integer current) {
        while (true) {
            System.out.printf("Enter %s%s: ", label,
                    current == null
                            ? " (leave blank for null)"
                            : " (leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            if (input.isBlank()) {
                return current;
            }
            try {
                return Integer.valueOf(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid format.");
            }
        }
    }

    /*
     * Method Name: date
     * Purpose: Prompts the user for a date string matching a specific pattern and converts it to java.sql.Date.
     * Pre-conditions: Pattern must be a valid SimpleDateFormat string.
     * Post-conditions: Input is consumed. Loops on ParseException. Throws ExitFormError if "exit" is typed.
     * Return Value: Date - The parsed java.sql.Date object.
     * Parameters:
     *     label (in)   - Description of the date.
     *     pattern (in) - The date format pattern (e.g., "MM-dd-YYYY").
     *     current (in) - Default Date value.
     */
    public static Date date(String label, String pattern, Date current) {
        var format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        while (true) {
            System.out.printf("Enter %s (format: %s)%s: ",
                    label,
                    pattern,
                    current == null ? "" : " (current: " + current + ")");
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            try {
                if (input.isBlank() && current != null) {
                    return current;
                }
                return new Date(format.parse(input).getTime());
            } catch (ParseException e) {
                System.out.println("Error: Invalid format.");
                continue;
            }
        }
    }

    /*
     * Method Name: timestamp
     * Purpose: Prompts the user for a timestamp string matching a specific pattern and converts it to java.sql.Timestamp.
     * Pre-conditions: Pattern must be a valid SimpleDateFormat string.
     * Post-conditions: Input is consumed. Loops on ParseException. Throws ExitFormError if "exit" is typed.
     * Return Value: Timestamp - The parsed java.sql.Timestamp object.
     * Parameters:
     *     label (in)   - Description of the timestamp.
     *     pattern (in) - The format pattern (e.g., "HH:mm").
     *     current (in) - Default Timestamp value.
     */
    public static Timestamp timestamp(String label, String pattern, Timestamp current) {
        var format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        while (true) {
            System.out.printf("Enter %s (format: %s)%s: ",
                    label,
                    pattern,
                    current == null ? "" : " (current: " + current + ")");
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            try {
                return new Timestamp(format.parse(input).getTime());
            } catch (ParseException e) {
                System.out.println("Error: Invalid format.");
                continue;
            }
        }
    }

    /*
     * Method Name: time
     * Purpose: Prompts the user for a time string matching a specific pattern and converts it to java.time.LocalTime.
     * Pre-conditions: Pattern must be a valid DateTimeFormatter pattern.
     * Post-conditions: Input is consumed. Loops on DateTimeParseException. Throws ExitFormError if "exit" is typed.
     * Return Value: LocalTime - The parsed LocalTime object.
     * Parameters:
     *     label (in)   - Description of the time.
     *     pattern (in) - The format pattern (e.g., "HH:mm:ss").
     */
    public static LocalTime time(String label, String pattern) {
        var format = DateTimeFormatter.ofPattern(pattern);
        while (true) {
            System.out.printf("Enter %s (format: %s): ", label, pattern);
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) throw ExitFormError;
            try {
                return LocalTime.parse(input, format);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format.");
            }
        }
    }

    /*
     * Method Name: bool
     * Purpose: Prompts the user for a yes/no response and converts it to a Boolean.
     * Pre-conditions: System.in is available.
     * Post-conditions: Input is consumed. Loops until "yes", "no", or blank (if default exists) is entered.
     * Return Value: Boolean - True for "yes", False for "no", or the default value.
     * Parameters:
     *     label (in)   - Description of the boolean toggle.
     *     current (in) - Default Boolean value.
     */
    public static Boolean bool(String label, Boolean current) {
        while (true) {
            System.out.printf("%s (yes/no)%s: ",
                    label,
                    current == null
                            ? ""
                            : " (current: " + (current ? "yes" : "no") + ")");
            var input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty() && current != null)
                return current;
            if (input.equals("yes"))
                return true;
            if (input.equals("no"))
                return false;
            System.out.println("Error: Invalid input.");
        }
    }
}