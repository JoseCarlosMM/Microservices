package matching;

/**
 * Created by josec on 6/4/2016.
 */
import java.sql.*;

/**
 * Created by josec on 5/28/2016.
 */
public class BaseHandler {
    private static String DB_URL = "jdbc:mysql://52.90.64.203:9306?useUnicode=yes&characterEncoding=UTF-8";
    //private static String DB_URL = "jdbc:mysql://172.31.63.59:9306?useUnicode=yes&characterEncoding=UTF-8";
    private static String USER = "";
    private static String PASSWORD = "";
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
