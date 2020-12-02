package com.example.AWSItemTracker.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.example.AWSItemTracker.entities.WorkItem;

@Component
public class InjectWorkService {

	private Environment env;
	
	private static Logger logger = LoggerFactory.getLogger(InjectWorkService.class);
	
	public InjectWorkService(Environment env) {
		this.env = env;
	}
	
	// Inject a new submission
	public String modifySubmission(String id, String desc, String status) {

		Connection c = null;
		int rowCount= 0;

		try {

			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			if(c==null) {
				logger.error("No connection!!!");
				return "";
			}
			// Use prepared statements
			PreparedStatement ps = null;

			String query = "update Work set description = ?, status = ? where idwork = '" +id +"'";
			ps = c.prepareStatement(query);
			ps.setString(1, desc);
			ps.setString(2, status);
			ps.execute();
			return id;
		} catch (SQLException e) {
			logger.error("Could not update the dataabse: " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}

	// Inject a new submission
	public String injestNewSubmission(WorkItem item) {

		Connection c = null;
		int rowCount= 0;
		try {

			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			if(c==null) {
				logger.error("No connection!!!");
				return "";
			}
			
			// Use a prepared statement
			PreparedStatement ps = null;

			// Convert rev to int
			String name = item.getName();
			String guide = item.getGuide();
			String description = item.getDescription();
			String status = item.getStatus();

			// Generate the work item ID
			UUID uuid = UUID.randomUUID();
			String workId = uuid.toString();

			// Date conversion
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			String sDate1 = dtf.format(now);
			Date date1 = new SimpleDateFormat("yyyy/MM/dd").parse(sDate1);
			java.sql.Date sqlDate = new java.sql.Date( date1.getTime());

			// Inject an item into the system
			String insert = "INSERT INTO Work (idwork, username,date,description, guide, status, archive) VALUES(?,?, ?,?,?,?,?);";
			ps = c.prepareStatement(insert);
			ps.setString(1, workId);
			ps.setString(2, name);
			ps.setDate(3, sqlDate);
			ps.setString(4, description);
			ps.setString(5, guide );
			ps.setString(6, status );
			ps.setBoolean(7, false);
			ps.execute();
			return workId;

		} catch (SQLException | ParseException e) {
			logger.error("Could not insert new entety in the database: " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}

}
