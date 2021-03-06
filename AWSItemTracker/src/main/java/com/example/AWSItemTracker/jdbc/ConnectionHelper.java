package com.example.AWSItemTracker.jdbc;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ConnectionHelper {
	
	private static String url;
	private static ConnectionHelper instance;
	private static String username;
	private static String password;
	
	private static Logger logger = LoggerFactory.getLogger(ConnectionHelper.class);
	
	private ConnectionHelper(Environment env) {
		if(env!=null) {
			username = env.getProperty("database.username");
			password = env.getProperty("database.password");
			url = env.getProperty("database.url");
		} else {
			logger.error("No enviroment found! Uses default values...");
		}
    }
	
	public static Connection getConnection(Environment env) throws SQLException {
		if (instance == null) {
			instance = new ConnectionHelper(env);	
			logger.info("DB URL: "+url);
		}

		try {

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection c = DriverManager.getConnection(url, username, password);
			if (c==null) {
				logger.error("No connection found!");
			} else {
				logger.info("Got connection to database!");
			}
			
			return c;
		} catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			logger.error("Could not connect to the database[" + e.getMessage()+"], Stacktrace:");
			e.printStackTrace();
			
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
	
}
