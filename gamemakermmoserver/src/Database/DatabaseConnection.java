/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import Constants.ServerConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author marcb
 */
public class DatabaseConnection {
    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(ServerConstants.DB_URL, ServerConstants.DB_USER, ServerConstants.DB_PASS);
            System.out.println("Connected to the database");
        } catch (SQLException e) {
            System.out.println("Couldn't connect to the database");
        }
        return connection;
    }
}
