package matching;

/**
 * Created by josec on 6/4/2016.
 */
import java.sql.*;

/**
 * Created by josec on 5/28/2016.
 */
public class BaseHandler {
    private static String DB_URL = "jdbc:mysql://adsconfiguration.cydtpwlenuws.us-east-1.rds.amazonaws.com:3306";
    private static String USER = "pruebas";
    private static String PASSWORD = "pruebas2016";
    protected Connection connection;

    protected void openConnection(){
        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
    }

    protected ResultSet executeQuery(String stQuery) throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery(stQuery);
    }

    protected void executeUpdate(String stQuery) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(stQuery);
    }
    protected void closeConnection() throws SQLException {
        connection.close();
    }
    protected boolean isStringNullOrEmpty(String st)
    {
        return (st==null || st=="");
    }

    public class CustomException extends Exception {
        public String message;

        public CustomException(String message){
            this.message = message;
        }

        @Override
        public String getMessage(){
            return message;
        }
    }
}
