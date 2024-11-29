package cpsc4620;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

// import cpsc4620.DBConnector;

public class TestDB {
	private static Connection conn;

    public static void main(String[] args) {
        try {
            conn = DBConnector.make_connection();
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            System.out.println("Error creating connection: " + e.getMessage());
        } catch (IOException e){
            System.out.println("Error creating connection: " + e.getMessage());
        }
        try {
            if (conn != null) {
                conn.close(); // Properly close the connection
                System.out.println("Connection closed successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        } 
    }
}
