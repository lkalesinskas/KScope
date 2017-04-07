

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBConnector {
    
    public static final String            dbHost      = "localhost";
    public static final String            dbPort      = "3306";
    public static final String            dbUserName  = "root";
    public static final String            dbPassword  = "password";
    
    private static Map<String, Connection> connections = new HashMap<String, Connection>();
    
    public static Connection getConn(String dbName) throws SQLException {
    
        if (connections.get(dbName) == null || connections.get(dbName).isClosed()) {
            setupConnection(dbName);
        }
        
        return connections.get(dbName);
    }
    
    private static boolean setupConnection(String dbName) {
    
        try {
            Class.forName("com.mysql.jdbc.Driver");
            
            connections.put(dbName,
                    DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?noAccessToProcedureBodies=true",
                            dbUserName, dbPassword));
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
    
   
}
