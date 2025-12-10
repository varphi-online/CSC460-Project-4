import java.util.Stack;

/*
 * Class: ProgramContext
 * Purpose: To serve as a global context container for the application, managing
 * user session data, navigation history, and UI feedback messages.
 *
 * Public Constants and Variables:
 * - userId: The unique identifier for the current user.
 * - type: The categorization of the current user (STAFF or MEMBER).
 * - breadcrumb: A Stack containing the history of visited page titles.
 * - statusMessage: A string conveying success, error, or warning info to the user.
 *
 * Constructors:
 * - None
 *
 * Methods:
 * - setUserId: Sets the global user ID.
 * - getUserId: Retrieves the global user ID.
 * - setType: Sets the global user type.
 * - getType: Retrieves the global user type.
 * - getBreadcrumb: Returns a formatted string of the navigation path.
 * - pushBreadcrumb: Adds a location to the navigation stack.
 * - popBreadcrumb: Removes the last location from the navigation stack.
 * - setStatusMessage: Sets the global feedback message.
 * - getStatusMessage: Retrieves the global feedback message.
 * - clearStatusMessage: Resets the feedback message to null.
 * - genericError: Handles exceptions by setting an error message and throwing a RuntimeException.
 * - successMessage: Sets a green success status message.
 * - failureMessage: Sets a red failure status message.
 * - warningMessage: Sets a yellow warning status message.
 */
public class ProgramContext {

    public enum UserType {
        STAFF,  // Represents a staff user
        MEMBER  // Represents a member user
    };

    public static Integer userId;   // Stores the currently logged-in user's ID
    public static UserType type;    // Stores the class of the current user
    public final static Stack<String> breadcrumb = new Stack<>();   // LIFO stack for navigation history
    public static String statusMessage; // Stores a temporary message to display to the user after an action

    /*
     * Class: Color
     * Author: <Your Name>
     * External Packages: None
     * Package: ProgramContext (Inner Enum)
     * Inheritance: Enum
     *
     * Purpose: Defines ANSI color codes for console output formatting.
     *
     * Public Constants and Variables:
     * - RED: ANSI code for red text.
     * - GREEN: ANSI code for green text.
     * - YELLOW: ANSI code for yellow text.
     * - code: The string representation of the ANSI escape sequence.
     *
     * Constructors:
     * - Color(String): Initializes the enum with specific ANSI code.
     *
     * Methods:
     * - toString: Returns the ANSI code string.
     */
    public enum Color {
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m");

        private final String code;

        /*
         * Method Name: Color
         * Purpose: Constructor for the Color enum.
         * Pre-conditions: code is a valid String.
         * Post-conditions: A Color enum instance is created with the specified code.
         * Return Value: None.
         * Parameters:
         * - code (Into): The ANSI escape sequence string.
         */
        Color(String code) {
            this.code = code;
        }

        /*
         * Method Name: toString
         * Purpose: Returns the string representation of the color code.
         * Pre-conditions: None.
         * Post-conditions: The ANSI code is returned.
         * Return Value: String - The ANSI escape sequence.
         * Parameters: None.
         */
        @Override
        public String toString() {
            return code;
        }
    }

    /*
     * Method Names: setUserId, getUserId, setType, getType
     * Purpose: Getter and Setter methods for global user state variables.
     * Pre-conditions: Input values for setters must be valid types.
     * Post-conditions: Global state variables are updated or returned.
     * Return Value: Integer (ID) or UserType (type) for getters; None for setters.
     * Parameters:
     * - uid (Into): The user ID to set.
     * - t (Into): The UserType to set.
     */
    public static void setUserId(Integer uid) {
        userId = uid;
    }

    public static Integer getUserId() {
        return userId;
    }

    public static void setType(UserType t) {
        type = t;
    }

    public static UserType getType() {
        return type;
    }

    /*
     * Method Name: getBreadcrumb
     * Purpose: Formats the current navigation stack into a readable string.
     * Pre-conditions: breadcrumb stack is initialized.
     * Post-conditions: Returns a string joining stack elements with " > ".
     * Return Value: String - The formatted navigation path.
     * Parameters: None.
     */
    public static String getBreadcrumb() {
        return String.join(" > ", breadcrumb);
    }

    /*
     * Method Name: pushBreadcrumb
     * Purpose: Adds a new navigation step to the history stack.
     * Pre-conditions: text is not null.
     * Post-conditions: text is pushed onto the breadcrumb stack if valid.
     * Return Value: None.
     * Parameters:
     * - text (Into): The name of the navigation location to add.
     */
    public static void pushBreadcrumb(String text) {
        if (text.length() > 0 || breadcrumb.size() > 0)
            breadcrumb.push(text);
    }

    /*
     * Method Name: popBreadcrumb
     * Purpose: Removes the most recent navigation step from the history.
     * Pre-conditions: breadcrumb stack is not empty.
     * Post-conditions: The top element is removed from the stack.
     * Return Value: String - The element removed from the stack.
     * Parameters: None.
     */
    public static String popBreadcrumb() {
        return breadcrumb.pop();
    }

    /*
     * Method Name: setStatusMessage
     * Purpose: Updates the global status message string.
     * Pre-conditions: None.
     * Post-conditions: statusMessage is updated with the provided text and optional color.
     * Return Value: None.
     * Parameters:
     * - msg (Into): The text content of the message.
     * - color (Into): The Color enum to format the message (optional, overloaded).
     */
    public static void setStatusMessage(String msg) {
        statusMessage = "🅘 " + msg;
    }

    public static void setStatusMessage(String msg, Color color) {
        String colorCode = (color != null) ? color.toString() : "";
        statusMessage = colorCode + "🅘 " + msg + "\u001B[0m";
    }

    /*
     * Method Name: getStatusMessage
     * Purpose: Retrieves the current status message.
     * Pre-conditions: None.
     * Post-conditions: The message is returned.
     * Return Value: String - The current status message.
     * Parameters: None.
     */
    public static String getStatusMessage() {
        return statusMessage;
    }

    /*
     * Method Name: clearStatusMessage
     * Purpose: Clears the global status message.
     * Pre-conditions: None.
     * Post-conditions: statusMessage is set to null.
     * Return Value: None.
     * Parameters: None.
     */
    public static void clearStatusMessage() {
        statusMessage = null;
    }

    /*
     * Method Name: genericError
     * Purpose: Sets a failure message and throws a runtime exception.
     * Pre-conditions: Exception e is not null.
     * Post-conditions: statusMessage is set to red and RuntimeException is thrown.
     * Return Value: None.
     * Parameters:
     * - e (Into): The original exception that occurred.
     */
    public static void genericError(Exception e) {
        failureMessage("An error occurred: " + e.getMessage());
        throw new RuntimeException(e.getMessage());
    }

    /*
     * Method Name: successMessage
     * Purpose: Sets a green status message indicating success.
     * Pre-conditions: None.
     * Post-conditions: statusMessage is updated with green formatting.
     * Return Value: None.
     * Parameters:
     * - msg (Into): The success message text.
     */
    public static void successMessage(String msg) {
        setStatusMessage(msg, ProgramContext.Color.GREEN);
    }

    /*
     * Method Name: failureMessage
     * Purpose: Sets a red status message indicating failure.
     * Pre-conditions: None.
     * Post-conditions: statusMessage is updated with red formatting.
     * Return Value: None.
     * Parameters:
     * - msg (Into): The failure message text.
     */
    public static void failureMessage(String msg) {
        setStatusMessage(msg, ProgramContext.Color.RED);
    }

    /*
     * Method Name: warningMessage
     * Purpose: Sets a yellow status message indicating a warning.
     * Pre-conditions: None.
     * Post-conditions: statusMessage is updated with yellow formatting.
     * Return Value: None.
     * Parameters:
     * - msg (Into): The warning message text.
     */
    public static void warningMessage(String msg) {
        setStatusMessage(msg, ProgramContext.Color.YELLOW);
    }
}