import java.sql.Connection;
import java.sql.*;

public class Install {
    public static void main(String[] args) throws SQLException {
        DB.init(args);
        Connection dbConnection = DB.db;
        String sql = "RUNSCRIPT FROM 'Schema.sql'"; 
        Statement stmt = dbConnection.createStatement();
        stmt.execute(sql); 

        String seed = "RUNSCRIPT FROM 'seed.sql'"; 
        Statement stmt2 = dbConnection.createStatement();
        stmt2.execute(seed); 
        dbConnection.close();
    }
}
