package com.example.AWSItemTracker.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHelper {
	
	private static String url;
	private static ConnectionHelper instance;

	private static Logger logger = LoggerFactory.getLogger(ConnectionHelper.class);
	
	private ConnectionHelper() {
    }
	
	public static Connection getConnection() throws SQLException {
		if (instance == null) {
			instance = new ConnectionHelper();	
			
		}

		try {

			Class.forName("com.mysql.jc.jdbc.Driver").newInstance();
			return DriverManager.getConnection(url, "root","root");
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Could not connect to the database: " + e.getMessage());
		}
		return null;
	}

	public static void close(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.error("Could not close connection: " + e.getMessage());
		}
	}

	public static void setDatabaseUrl(String dbUrl) {
		if(url != null) {
			logger.warn("Database url Changed!");
		}
		url = dbUrl;
	}
	
	public static boolean isUrlSet() {
		return url != null;
	}
	
}
