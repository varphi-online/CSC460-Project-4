/*
 * JDBC.java -- A simple example of how to use Java and JDBC to
 * connect to an Oracle user's schema.
 *
 * At the time of this writing, the version of Oracle is 11.2g, and
 * the Oracle JDBC driver can be found at
 *   /usr/lib/oracle/19.8/client64/lib/ojdbc8.jar
 * on the lectura system in the UofA CS dept.
 * (Yes, 19.8, not 11.2.  It's the correct jar file but in a strange location.)
 *
 * To compile and execute this program on lectura:
 *
 *   Add the Oracle JDBC driver to your CLASSPATH environment variable:
 *
 *         export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:${CLASSPATH}
 *
 *     (or whatever shell variable set-up you need to perform to add the
 *     JAR file to your Java CLASSPATH)
 *
 *   Compile this file:
 *
 *         javac JDBC.java
 *
 *   Finally, run the program:
 *
 *         java JDBC <oracle username> <oracle password>
 *
 * Author:  L. McCann (2008-11-19; updated 2015-10-28 and 2021-10-19)
 */

import java.sql.*;
import java.util.*;

public class DB {
    public static Connection db = null;

    public static void init(String[] args) {

        final String oracleURL = "jdbc:h2:./my_local_db;MODE=Oracle";
        String username = "sa",
                password = "";

        /*
         * final String oracleURL = // Magic lectura -> aloe access spell
         * "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
         * String username = null, // Oracle DBMS username
         * password = null; // Oracle DBMS password
         * if (args.length == 2) { // get username/password from cmd line args
         * username = args[0];
         * password = args[1];
         * 
         * } else {
         * System.out.println("\nUsage:  java JDBC <username> <password>\n"
         * + "    where <username> is your Oracle DBMS"
         * + " username,\n    and <password> is your Oracle"
         * + " password (not your system password).\n");
         * System.exit(-1);
         * }
         */

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

    public static PreparedStatement prepared(String query){
        try {
            return db.prepareStatement(query);
        } catch (SQLException e) {
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
        }
        return null;
    }

    public static void safeExecute(Runnable action) {
    try {
        action.run();
    } catch (Exception e) {
        ProgramContext.genericError(e);
    }
    }

    public static void executeUpdate(String sql, Object... params) throws SQLException {
    var stmt = DB.prepared(sql);
    for (int i = 0; i < params.length; i++) {
        stmt.setObject(i + 1, params[i]);
    }
    stmt.executeUpdate();
    }

    public static ResultSet executeQuery(String sql, Object... params) throws SQLException {
    var stmt = DB.prepared(sql);
    for (int i = 0; i < params.length; i++) {
        stmt.setObject(i + 1, params[i]);
    }
    var rs = stmt.executeQuery();
    rs.next();
    return rs;
    }

    public static void printQuery(String sql, Object... params) throws SQLException {
    var stmt = DB.prepared(sql);
    for (int i = 0; i < params.length; i++) {
        stmt.setObject(i + 1, params[i]);
    }
    System.out.println(DB.tabularize(stmt.executeQuery()));
    }

 public static String tabularize(ResultSet rs) {
     StringBuilder sb = new StringBuilder();
     
     try {
            if(!rs.isBeforeFirst()) throw new RuntimeException("No data.");
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
            // Assuming ProgramContext is defined elsewhere in your project
            ProgramContext.setStatusMessage("An error occurred: " + e.getMessage(), ProgramContext.Color.RED);
            return null;
        }

        return sb.toString();
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static Boolean exists(String sql, Object... params) throws SQLException {
        var stmt = DB.prepared(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        var rs = stmt.executeQuery();
        return rs.isBeforeFirst();
    }
}