import java.sql.*;
import java.util.*;

/*
 * Class:        DB
 *
 * Purpose:      This class serves as a static wrapper for JDBC database interactions.
 *               It manages a single shared connection object and provides utility
 *               methods to execute SQL queries, updates, and format ResultSets 
 *               into human-readable strings.
 *
 * Public Constants/Variables:
 *      Connection db   - The active connection object to the database.
 *
 * Constructors: 
 *  None.
 *
 * Methods:
 *      init(String[])          - Initializes the database connection.
 *      execute(String)         - Executes a raw SQL query.
 *      prepared(String)        - Creates a PreparedStatement.
 *      prepared(String, boolean) - Creates a PreparedStatement with key generation options.
 *      safeExecute(Runnable)   - Runs a task safely, catching generic exceptions.
 *      executeUpdate(String, Object...) - Executes a DML update with parameters.
 *      executeQuery(String, Object...)  - Executes a SELECT query with parameters.
 *      printQuery(String, Object...)    - Executes and prints a query result.
 *      uniqueId(String, String)         - Generates a unique ID for a table/column.
 *      tabularize(ResultSet)   - Converts a ResultSet into a formatted string table.
 *      padRight(String, int)   - Helper to pad strings for table formatting.
 *      exists(String, Object...) - Checks if a query returns any rows.
 */
public class DB {
    public static Connection db = null; // The static connection instance used for all database operations

    /*
     * Method Name:     init
     * Purpose:         Initializes the JDBC connection to the database (specifically H2 
     *                  in Oracle mode) using the provided driver.
     * Pre-conditions:  The H2 driver must be available in the classpath.
     * Post-conditions: The 'db' static variable is initialized, or the program exits on error.
     * Return:          None
     * Parameters:      
     *      args (in) - Command line arguments (currently unused in this specific implementation).
     */
    public static void init(String[] args) {

        final String oracleURL = "jdbc:h2:./my_local_db;MODE=Oracle";
        String username = "sa",
                password = "";

        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            // Class.forName("oracle.jdbc.OracleDriver");
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        }
        // make and return a database connection to the user's
        // Oracle database
        try {
            db = DriverManager.getConnection(oracleURL, username, password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }
    }

    /*
     * Method Name:     execute
     * Purpose:         Executes a raw SQL string using a simple Statement.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: A ResultSet is returned if successful.
     * Return:          ResultSet - The result of the query, or null on error.
     * Parameters:      
     *      query (in) - The raw SQL string to execute.
     */
    public static ResultSet execute(String query) {
        try {
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery(query);
            return result;
        } catch (SQLException e) {
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
        }
        return null;
    }

    /*
     * Method Name:     prepared
     * Purpose:         Creates a PreparedStatement for the given SQL query.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: A PreparedStatement is created and returned.
     * Return:          PreparedStatement - The prepared statement object, or null on error.
     * Parameters:      
     *      query (in) - The SQL query string containing placeholders.
     */
    public static PreparedStatement prepared(String query) {
        try {
            return db.prepareStatement(query);
        } catch (SQLException e) {
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
        }
        return null;
    }

    /*
     * Method Name:     prepared
     * Purpose:         Creates a PreparedStatement with an option to return generated keys.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: A PreparedStatement is created with the specified flag.
     * Return:          PreparedStatement - The prepared statement, or null on error.
     * Parameters:      
     *      query (in)               - The SQL query string.
     *      returnGeneratedKeys (in) - Flag indicating if auto-generated keys should be returned.
     */
    public static PreparedStatement prepared(String query, boolean returnGeneratedKeys) {
        try {
            if (returnGeneratedKeys) {
                return db.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            } else {
                return db.prepareStatement(query);
            }
        } catch (SQLException e) {
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
        }
        return null;
    }

    /*
     * Method Name:     safeExecute
     * Purpose:         Executes a Runnable action within a try-catch block to handle generic errors.
     * Pre-conditions:  None specific.
     * Post-conditions: The action is run, or an error is logged to ProgramContext.
     * Return:          None
     * Parameters:      
     *      action (in) - The Runnable code block to execute.
     */
    public static void safeExecute(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            ProgramContext.genericError(e);
        }
    }

    /*
     * Method Name:     executeUpdate
     * Purpose:         Executes a DML update (INSERT, UPDATE, DELETE) using prepared parameters.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: The database is updated based on the SQL.
     * Return:          None
     * Parameters:      
     *      sql (in)    - The SQL statement with placeholders.
     *      params (in) - Variable arguments to fill the SQL placeholders.
     */
    public static void executeUpdate(String sql, Object... params) throws SQLException {
        var stmt = DB.prepared(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        stmt.executeUpdate();
    }

    /*
     * Method Name:     executeQuery
     * Purpose:         Executes a SELECT query using prepared parameters.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: A ResultSet containing the data is returned.
     * Return:          ResultSet - The results of the query.
     * Parameters:      
     *      sql (in)    - The SQL SELECT statement with placeholders.
     *      params (in) - Variable arguments to fill the SQL placeholders.
     */
    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
        var stmt = DB.prepared(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        var rs = stmt.executeQuery();
        return rs;
    }

    /*
     * Method Name:     printQuery
     * Purpose:         Executes a query and prints the resulting data in a tabular format to StdOut.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: The query results are printed to the console.
     * Return:          None
     * Parameters:      
     *      sql (in)    - The SQL statement with placeholders.
     *      params (in) - Variable arguments to fill the SQL placeholders.
     */
    public static void printQuery(String sql, Object... params) throws SQLException {
        var stmt = DB.prepared(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        System.out.println(DB.tabularize(stmt.executeQuery()));
    }

    /*
     * Method Name:     uniqueId
     * Purpose:         Calculates the next available unique ID for a given table and column.
     * Pre-conditions:  The table and column must exist in the database.
     * Post-conditions: Returns the MAX(id) + 1.
     * Return:          Integer - The next unique identifier.
     * Parameters:      
     *      tableName (in)  - The name of the table to check.
     *      columnName (in) - The name of the ID column.
     */
    public static Integer uniqueId(String tableName, String columnName) throws SQLException {
        String sql = String.format(
                "SELECT COALESCE(MAX(%s), 0) + 1 AS next_id FROM %s",
                columnName, tableName);

        try (var stmt = db.createStatement();
                var rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1); // next_id
            } else {
                return 1;
            }
        }
    }

    /*
     * Method Name:     tabularize
     * Purpose:         Converts the contents of a ResultSet into a formatted ASCII table string.
     * Pre-conditions:  The ResultSet must be open and contain metadata.
     * Post-conditions: The ResultSet is iterated; a formatted string is generated.
     * Return:          String - The ASCII table representation of the data.
     * Parameters:      
     *      rs (in)     - The ResultSet to process.
     */
    public static String tabularize(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();

        try {
            if (!rs.isBeforeFirst())
                throw new RuntimeException("No data.");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            int[] colWidths = new int[columnCount];
            String[] headers = new String[columnCount];
            List<String[]> rows = new ArrayList<>();

            for (int i = 0; i < columnCount; i++) {
                headers[i] = rsmd.getColumnLabel(i + 1);
                colWidths[i] = headers[i].length();
            }

            while (rs.next()) {
                String[] rowData = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    Object obj = rs.getObject(i + 1);
                    String val = (obj == null) ? "NULL" : obj.toString();
                    rowData[i] = val;

                    if (val.length() > colWidths[i]) {
                        colWidths[i] = val.length();
                    }
                }
                rows.add(rowData);
            }

            String TL = "╭"; String TM = "┬"; String TR = "╮";
            String ML = "├"; String MM = "┼"; String MR = "┤";
            String BL = "╰"; String BM = "┴"; String BR = "╯";
            String V  = "│"; String H  = "─";

            sb.append(TL);
            for (int i = 0; i < columnCount; i++) {
                sb.append(H.repeat(colWidths[i] + 2));
                sb.append(i == columnCount - 1 ? TR : TM);
            }
            sb.append("\n");

            sb.append(V);
            for (int i = 0; i < columnCount; i++) {
                sb.append(" ").append(padRight(headers[i], colWidths[i])).append(" ").append(V);
            }
            sb.append("\n");

            sb.append(ML);
            for (int i = 0; i < columnCount; i++) {
                sb.append(H.repeat(colWidths[i] + 2));
                sb.append(i == columnCount - 1 ? MR : MM);
            }
            sb.append("\n");

            for (String[] row : rows) {
                sb.append(V);
                for (int i = 0; i < columnCount; i++) {
                    sb.append(" ").append(padRight(row[i], colWidths[i])).append(" ").append(V);
                }
                sb.append("\n");
            }

            sb.append(BL);
            for (int i = 0; i < columnCount; i++) {
                sb.append(H.repeat(colWidths[i] + 2));
                sb.append(i == columnCount - 1 ? BR : BM);
            }

        } catch (SQLException e) {
            // I think we should let the error propagate since it can indicate whether there
            // were no entries
            throw e;
            // ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(),
            // ProgramContext.Color.RED);
        }

        return sb.toString();
    }

    /*
     * Method Name:     padRight
     * Purpose:         Helper method to pad a string with spaces on the right to a specific length.
     * Pre-conditions:  None.
     * Post-conditions: Returns a new string of length 'n'.
     * Return:          String - The padded string.
     * Parameters:      
     *      s (in) - The input string.
     *      n (in) - The total width to pad to.
     */
    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    /*
     * Method Name:     exists
     * Purpose:         Checks if a specific query returns any rows.
     * Pre-conditions:  The 'db' connection must be active.
     * Post-conditions: Returns true if rows exist, false otherwise.
     * Return:          Boolean - True if data is found, false otherwise.
     * Parameters:      
     *      sql (in)    - The SQL query to execute.
     *      params (in) - Variable arguments to fill the SQL placeholders.
     */
    public static Boolean exists(String sql, Object... params) throws SQLException {
        var stmt = DB.prepared(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        var rs = stmt.executeQuery();
        return rs.isBeforeFirst();
    }
}