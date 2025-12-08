import java.util.Stack;

public class ProgramContext {
    // Global state stored in static gulp
    public enum UserType {
        STAFF, MEMBER
    };

    public static Integer userId;
    public static UserType type;
    public final static Stack<String> breadcrumb = new Stack<>();
    public static String statusMessage; // one time message that prints after an action happens so a user know something
                                        // happened

    public enum Color {
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m");

        private final String code;

        Color(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return code;
        }
    }

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

    public static String getBreadcrumb() {
        return String.join(" > ", breadcrumb);
    }

    public static void pushBreadcrumb(String text) {
        if (text.length() > 0 || breadcrumb.size() > 0)
            breadcrumb.push(text);
    }

    public static String popBreadcrumb() {
        return breadcrumb.pop();
    }

    public static void setStatusMessage(String msg) {
        statusMessage = "🅘 " + msg;
    }

    public static void setStatusMessage(String msg, Color color) {
        String colorCode = (color != null) ? color.toString() : "";
        statusMessage = colorCode + "🅘 " + msg + "\u001B[0m";
    }

    public static String getStatusMessage() {
        return statusMessage;
    }

    public static void clearStatusMessage() {
        statusMessage = null;
    }

    public static void genericError(Exception e){
        setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
        throw new RuntimeException(e.getMessage());
    }
}
