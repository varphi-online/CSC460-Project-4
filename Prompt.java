import java.sql.Date;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Prompt {
    private static final Scanner scanner = Prog4.getScanner();

    /**
     * get a string from user.
     * If current is blank or null, user MUST give a nonblank input.
     * Otherwise they can leave blank and current will be used.
     * 
     * @param label   description of what you want user to input.
     * @param current default value to use if input blank. use "" or null for no
     *                default
     * @return parsed String.
     */
    public static String string(String label, String current) {
        while (true) {
            System.out.printf("Enter %s %s: ", label,
                    current.isBlank() ? "" : "(leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
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

    /**
     * Get a string, allowing null result.
     * If user input is blank, then returns current (if not blank/null) or null.
     * 
     * @param label   description of what you want user to input.
     * @param current default value to use if input blank. use "" or null for no
     *                default
     * @return parsed String, or null value
     */
    public static String stringNullable(String label, String current) {
        System.out.printf("Enter %s %s: ",
                label,
                current == null || current.isBlank()
                        ? "(leave blank for null)"
                        : "(leave blank for default: " + current + ")");
        var input = scanner.nextLine().trim();
        if (input.isBlank()) {
            return current.isBlank() ? null : current;
        }
        return input;
    }

    /**
     * Get a string from a specific list of choices. if you want to allow blank then
     * include null in the list.
     * input is case insenstive.
     * 
     * @param label   description of what you want user to input.
     * @param choices list of valid inputs.
     * @return the entry from the input list that was selected by the user.
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

    /**
     * Get an integer from user.
     * If current is blank or null, user MUST give a valid nonblank input.
     * Otherwise they can leave blank and current will be used.
     * 
     * @param label   description of what you want user to input.
     * @param current default value to use if input blank. use null for no default.
     * @return parsed Integer
     */
    public static Integer integer(String label, Integer current) {
        while (true) {
            System.out.printf("Enter %s%s: ", label,
                    current == null
                            ? ""
                            : " (leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
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

    /**
     * Get an Integer, allowing null result.
     * If user input is blank, then returns current (either an integer or null).
     * 
     * @param label   description of what you want user to input.
     * @param current default value to use if input blank. use null for no default
     * @return parsed Integer, or null value
     */
    public static Integer integerNullable(String label, Integer current) {
        while (true) {
            System.out.printf("Enter %s%s: ", label,
                    current == null
                            ? " (leave blank for null)"
                            : " (leave blank for default: " + current + ")");
            var input = scanner.nextLine().trim();
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

    /**
     * Parses a date input of a specified format.
     * 
     * @param label   description of what the date is for
     * @param pattern date pattern to use. e.g. "MM-dd-YYYY"
     * @return parsed sql.Date object
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

    /**
     * Parses a timestamp input of a specified format.
     * 
     * @param label   description of what the date is for
     * @param pattern date pattern to use. e.g. "HH:mm" or "MM-dd-YYYY HH:mm"
     * @return parsed sql.Timestamp object
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
            try {
                return new Timestamp(format.parse(input).getTime());
            } catch (ParseException e) {
                System.out.println("Error: Invalid format.");
                continue;
            }
        }
    }

    /**
     * Parses a time input of a given format. Used for things like Intervals
     * 
     * @param label description of what the time is for
     * @param pattern format to use (e.g. HH:mm:ss or HH:mm)
     * @return parsed LocalTime object
     */
    public static LocalTime time(String label, String pattern) {
        var format = DateTimeFormatter.ofPattern(pattern);
        while (true) {
            System.out.printf("Enter %s (format: %s): ", label, pattern);
            var input = scanner.nextLine().trim();
            try {
                return LocalTime.parse(input, format);
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid format.");
            }
        }
    }

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
