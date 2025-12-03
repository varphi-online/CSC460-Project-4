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

import java.sql.*; // For access to the SQL interaction methods
import java.util.Calendar;
import java.util.Scanner;

public class Prog3 {
    final static int[] years = { 2012, 2016, 2020, 2024 };

    public static void main(String[] args) {
        // Setup
        final String oracleURL = // Magic lectura -> aloe access spell
                "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
        String username = null, // Oracle DBMS username
                password = null; // Oracle DBMS password
        if (args.length == 2) { // get username/password from cmd line args
            username = args[0];
            password = args[1];
        } else {
            System.out.println("\nUsage:  java JDBC <username> <password>\n"
                    + "    where <username> is your Oracle DBMS"
                    + " username,\n    and <password> is your Oracle"
                    + " password (not your system password).\n");
            System.exit(-1);
        }
        // load the (Oracle) JDBC driver by initializing its base
        // class, 'oracle.jdbc.OracleDriver'.
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("*** ClassNotFoundException:  "
                    + "Error loading Oracle JDBC driver.  \n"
                    + "\tPerhaps the driver is not on the Classpath?");
            System.exit(-1);
        }
        // make and return a database connection to the user's
        // Oracle database
        Connection dbconn = null;
        try {
            dbconn = DriverManager.getConnection(oracleURL, username, password);
        } catch (SQLException e) {
            System.err.println("*** SQLException:  "
                    + "Could not open JDBC connection.");
            System.err.println("\tMessage:   " + e.getMessage());
            System.err.println("\tSQLState:  " + e.getSQLState());
            System.err.println("\tErrorCode: " + e.getErrorCode());
            System.exit(-1);
        }

        // Repl
        System.out.print(">> ");
        try (Scanner main = new Scanner(System.in)) {
            repl: while (main.hasNextLine()) {
                String line = main.nextLine();
                String[] words = line.split(" ");
                try {
                    switch (words[0].toLowerCase()) {
                        case "help", "h":
                            System.out.println(
                                    """
                                            Program 3 Help:

                                                help -- Display this help message.
                                                year -- Display available years to use in queries.
                                                exit -- Exit the live repl and terminate the program.

                                                There are valid 4 queries you may enter to return information,
                                                any other queries types will be ignored.

                                                1. avgs         -- Display the average low and high temperatures for all available years.
                                                2. prec <year>  -- Display the top 10 highest precipitation amounts for the given year, if exists.
                                                3. diff         -- Display the pair of consec. dates across all data that has the largest diff in max temp.
                                                4. snow <amt>   -- Display the last 10 recorded dates where snowfall exceeded the given amount of inches""");
                            break;
                        case "clear":
                            System.out.print("\033[2J\033[H");
                            break;
                        case "-1", "exit":
                            break repl;
                        case "avgs":
                            yearlyAverages(dbconn);
                            break;
                        case "prec":
                            try {
                                if (words.length >= 2)
                                    precipitation(dbconn, Integer.parseInt(words[1]));
                                else
                                    System.out.println("prec query requires a year argument.");
                            } catch (NumberFormatException e) {
                                System.out.println("Year parameter is not an integer.");
                            }
                            break;
                        case "diff":
                            maxDiff(dbconn);
                            break;
                        case "snow":
                            try {
                                if (words.length >= 2)
                                    snowfall(dbconn, Double.parseDouble(words[1]));
                                else
                                    System.out.println("snow query requires an amount argument.");
                            } catch (NumberFormatException e) {
                                System.out.println("Amount parameter is not a number.");
                            }
                            break;
                        case "year":
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < years.length; i++) {
                                    sb.append(years[i]+"\n");
                            }
                            System.out.print(sb.toString());
                            break;
                        default:
                            ;
                    }
                } catch (SQLException e) {
                    System.err.println("*** SQLException:  " + "Could not fetch query results.");
                    System.err.println("\tMessage:   " + e.getMessage());
                    System.err.println("\tSQLState:  " + e.getSQLState());
                    System.err.println("\tErrorCode: " + e.getErrorCode());
                }
                System.out.print(">> ");
            }
        }

        try {
            dbconn.close();
        } catch (SQLException e) {
            System.out.printf("An error occured when closing the database connection.");
        }
    }

    static void yearlyAverages(Connection dbConnection) throws SQLException {
        System.out.println(" Year │ Average Max │ Average Min");
        System.out.println("──────┼─────────────┼─────────────");
        for (int year : years) {
            String sql = "SELECT AVG(max_temp) AS a_max, AVG(min_temp) AS a_min FROM fb" + year;
            try (Statement stmt = dbConnection.createStatement();
                    ResultSet result = stmt.executeQuery(sql)) {
                if (result.next()) {
                    System.out.printf(
                            " %d │ %.2f       │ %.2f%n", year, result.getDouble("a_max"), result.getDouble("a_min"));
                } else {
                    System.out.printf("%d | (no rows)%n", year);
                }
                stmt.close();
            }
        }
    }

    static void precipitation(Connection dbConnection, int year) throws SQLException {
        switch (year) {
            case 2012, 2016, 2020, 2024:
                String sql = "SELECT day, precipitation FROM fb" + year + " ORDER BY precipitation DESC NULLS LAST";
                try (Statement stmt = dbConnection.createStatement();
                        ResultSet result = stmt.executeQuery(sql)) {
                    Double last = null;
                    int count = 0;

                    System.out.println("  #  │ Date       │ Rainfall (in.)");
                    System.out.println("─────┼────────────┼────────────────");

                    while (result.next()) {
                        double precipitation = result.getDouble("precipitation");
                        Date day = result.getDate("day");
                        if (count < 10 || (last != null && Double.compare(precipitation, last) == 0)) {
                            System.out.printf(" %2d. │ %s │ %.2f%n", (count + 1), day, precipitation);
                            if (count != 10)
                                count++;
                            last = precipitation;
                        } else {
                            break;
                        }
                    }
                    stmt.close();
                }
                break;
            default:
                System.out.printf("No data is available for year: %d.%n", year);
                break;
        }
    }

    static void maxDiff(Connection dbConnection) throws SQLException {
        final String query = """
                WITH all_data AS (
                   SELECT day, max_temp FROM fb2012
                   UNION ALL
                   SELECT day, max_temp FROM fb2016
                   UNION ALL
                   SELECT day, max_temp FROM fb2020
                   UNION ALL
                   SELECT day, max_temp FROM fb2024
                )
                SELECT day, max_temp - LAG(max_temp, 1, 0) OVER (ORDER BY day) as diff FROM all_data ORDER BY diff DESC""";
        try (Statement stmt = dbConnection.createStatement();
                ResultSet result = stmt.executeQuery(query)) {
            Integer last = null;
            System.out.println("\nMaximum Maximum Temperature Difference\n");
            System.out.println(" From       │ To         │ Difference (°F)");
            System.out.println("────────────┼────────────┼─────────────────");
            while (result.next() && (last == null || result.getInt("diff") == last)) {
                int temp = result.getInt("diff");
                Date day = result.getDate("day");
                // https://stackoverflow.com/questions/912762/get-previous-day
                Calendar cal = Calendar.getInstance();
                cal.setTime(day);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date prior = new java.sql.Date(cal.getTimeInMillis());
                System.out.printf(" %s │ %s │ %d%n", prior, day, temp);
                last = temp;
            }
            stmt.close();
        }

        // String firstDay = "SELECT day, LEAD(max_temp, 1, 0) OVER (ORDER BY day) -
        // max_temp as diff FROM all_data ORDER BY diff ASC"
    }

    static void snowfall(Connection dbConnection, Double amt) throws SQLException {
        final String query = """
                WITH all_data AS (
                   SELECT day, snowfall FROM fb2012
                   UNION ALL
                   SELECT day, snowfall FROM fb2016
                   UNION ALL
                   SELECT day, snowfall FROM fb2020
                   UNION ALL
                   SELECT day, snowfall FROM fb2024
                )
                SELECT day, snowfall FROM all_data WHERE snowfall > ? ORDER BY day DESC""";

        if (Double.compare(0, amt) > 0) {
            System.out.printf("Please enter a positive amount of snowfall!%n",
                    amt);
            return;
        }

        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setDouble(1, amt);
            try (ResultSet result = stmt.executeQuery()) {
                Double last = null;
                int count = 0;

                if (!result.next()) {
                    System.out.printf("Our data shows that there has never been a day with >%f in. of snowfall!%n",
                            amt);
                    return;
                }

                System.out.println("  #  │ Date       │ Snowfall (in.)");
                System.out.println("─────┼────────────┼────────────────");

                do {
                    double snowfall = result.getDouble("snowfall");
                    Date day = result.getDate("day");
                    if (count < 10 || (last != null && Double.compare(snowfall, last) == 0)) {
                        System.out.printf(" %2d. │ %s │ %.2f%n", (count + 1), day, snowfall);
                        count++;
                        last = snowfall;
                    } else {
                        break;
                    }
                } while (result.next());
            }
            stmt.close();
        }
    }
}