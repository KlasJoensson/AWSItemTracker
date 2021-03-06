package com.example.AWSItemTracker.jdbc;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.example.AWSItemTracker.entities.WorkItem;

@Component
public class RetrieveItems {

	private Environment env;
	
	private static Logger logger = LoggerFactory.getLogger(RetrieveItems.class);
	
	public RetrieveItems(Environment env) {
	}
	
	// Retrieves an item based on the ID
	public String flipItemArchive(String id ) {

		Connection c = null;
		String query = "";

		try {

			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			
			ResultSet rs = null;
			Statement s = c.createStatement();
			Statement scount = c.createStatement();

			// Use prepared statements
			PreparedStatement pstmt = null;
			PreparedStatement ps = null;

			// Specify the SQL statement to query data
			query = "update work set archive = ? where idwork ='" +id + "' ";

			PreparedStatement updateForm = c.prepareStatement(query);
			updateForm.setBoolean(1, true);
			updateForm.execute();

		} catch (SQLException e) {
			logger.error("Could not update item with id '" + id + "': " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}


	// Retrieves archive data from the MySQL database
	public String getArchiveData(String username) {

		Connection c = null;

		// Define a list in which work items are stored
		List<WorkItem> itemList = new ArrayList<WorkItem>();
		int rowCount = 0;
		String query = "";
		WorkItem item = null;

		try {
			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			
			ResultSet rs = null;
			Statement s = c.createStatement();
			Statement scount = c.createStatement();

			// Use prepared statements
			PreparedStatement pstmt = null;
			PreparedStatement ps = null;

			int arch = 1;

			// Specify the SQL statement to query data
			query = "Select idwork,username,date,description,guide,status FROM Work where username = '" +username +"' and archive = " +arch +"";
			pstmt = c.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// For each record, create a WorkItem object
				item = new WorkItem();

				// Populate the WorkItem object
				item.setId(rs.getString(1));
				item.setName(rs.getString(2));
				item.setDate(rs.getDate(3).toString().trim());
				item.setDescription(rs.getString(4));
				item.setGuide(rs.getString(5));
				item.setStatus(rs.getString(6));

				// Push the WorkItem object to the list
				itemList.add(item);
			}

			return convertToString(toXml(itemList));

		} catch (SQLException e) {
			logger.error("Could not retrive any items for user '" + username + "': " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}

	// Retrieves an item based on the ID
	public String getItemSQL(String id ) {

		Connection c = null;

		// Define a list in which all work items are stored
		String query = "";
		String status="" ;
		String description="";

		try {
			// Create a Connection object
			c = ConnectionHelper.getConnection(env);

			ResultSet rs = null;
			Statement s = c.createStatement();
			Statement scount = c.createStatement();

			// Use prepared statements
			PreparedStatement pstmt = null;
			PreparedStatement ps = null;

			//Specify the SQL statement to query data
			query = "Select description, status FROM Work where idwork ='" +id + "' ";
			pstmt = c.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				description = rs.getString(1);
				status = rs.getString(2);
			}
			return convertToString(toXmlItem(id,description,status));


		} catch (SQLException e) {
			logger.error("Could not retrive item with id '" + id + "' :" + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}

	// Get Items data from MySQL
	public List<WorkItem> getItemsDataSQLReport(String username) {

		Connection c = null;

		// Define a list in which all work items are stored
		List<WorkItem> itemList = new ArrayList<WorkItem>();
		int rowCount = 0;
		String query = "";
		WorkItem item = null;

		try {
			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			
			ResultSet rs = null;
			Statement s = c.createStatement();
			Statement scount = c.createStatement();

			// Use prepared statements
			PreparedStatement pstmt = null;
			PreparedStatement ps = null;

			int arch = 0;

			// Specify the SQL statement to query data
			query = "Select idwork,username,date,description,guide,status FROM Work where username = '" 
					+username +"' and archive = " + arch +"";
			pstmt = c.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				// For each record-- create a WorkItem instance
				item = new WorkItem();

				// Populate WorkItem with data from MySQL
				item.setId(rs.getString(1));
				item.setName(rs.getString(2));
				item.setDate(rs.getDate(3).toString().trim());
				item.setDescription(rs.getString(4));
				item.setGuide(rs.getString(5));
				item.setStatus(rs.getString(6));

				// Push the WorkItem Object to the list
				itemList.add(item);
			}
			return itemList;

		} catch (SQLException e) {
			logger.error("Could not get the items data for user '"+username+"': " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}


	// Get Items Data from MySQL
	public String getItemsDataSQL(String username) {

		Connection c = null;

		// Define a list in which all work items are stored
		List<WorkItem> itemList = new ArrayList<WorkItem>();
		int rowCount = 0;
		String query = "";
		WorkItem item = null;
		try {
			// Create a Connection object
			c = ConnectionHelper.getConnection(env);
			
			ResultSet rs = null;
			Statement s = c.createStatement();
			Statement scount = c.createStatement();

			// Use prepared statements
			PreparedStatement pstmt = null;
			PreparedStatement ps = null;

			int arch = 0;

			// Specify the SQL statement to query data
			query = "Select idwork,username,date,description,guide,status FROM Work where username = '" +username +"' and archive = " +arch +"";
			pstmt = c.prepareStatement(query);
			rs = pstmt.executeQuery();

			while (rs.next()) {

				// For each record-- create a WorkItem instance
				item = new WorkItem();

				//Populate WorkItem object with data
				item.setId(rs.getString(1));
				item.setName(rs.getString(2));
				item.setDate(rs.getDate(3).toString().trim());
				item.setDescription(rs.getString(4));
				item.setGuide(rs.getString(5));
				item.setStatus(rs.getString(6));

				// Push the WorkItem Object to the list
				itemList.add(item);
			}
			return convertToString(toXml(itemList));

		} catch (SQLException e) {
			logger.error("Could not get the items data for user '"+username+"': " + e.getMessage());
		} finally {
			ConnectionHelper.close(c);
		}
		return null;
	}

	// Convert Work item data retrieved from MySQL
	// into XML to pass back to the view
	private Document toXml(List<WorkItem> itemList) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// Start building the XML
			Element root = doc.createElement( "Items" );
			doc.appendChild( root );

			// Get the elements from the collection
			int custCount = itemList.size();

			// Iterate through the collection
			for ( int index=0; index < custCount; index++) {

				// Get the WorkItem object from the collection
				WorkItem myItem = itemList.get(index);

				Element item = doc.createElement( "Item" );
				root.appendChild( item );

				// Set Id
				Element id = doc.createElement( "Id" );
				id.appendChild( doc.createTextNode(myItem.getId() ) );
				item.appendChild( id );

				// Set Name
				Element name = doc.createElement( "Name" );
				name.appendChild( doc.createTextNode(myItem.getName() ) );
				item.appendChild( name );

				// Set Date
				Element date = doc.createElement( "Date" );
				date.appendChild( doc.createTextNode(myItem.getDate() ) );
				item.appendChild( date );

				// Set Description
				Element desc = doc.createElement( "Description" );
				desc.appendChild( doc.createTextNode(myItem.getDescription() ) );
				item.appendChild( desc );

				// Set Guide
				Element guide = doc.createElement( "Guide" );
				guide.appendChild( doc.createTextNode(myItem.getGuide() ) );
				item.appendChild( guide );

				// Set Status
				Element status = doc.createElement( "Status" );
				status.appendChild( doc.createTextNode(myItem.getStatus() ) );
				item.appendChild( status );
			}

			return doc;
		} catch(ParserConfigurationException e) {
			logger.error("Could not parse item(s): " + e.getMessage());
		}
		return null;
	}

	private String convertToString(Document xml) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xml);
			transformer.transform(source, result);
			return result.getWriter().toString();

		} catch(TransformerException ex) {
			logger.error("Could not convert xml to string: " + ex.getMessage());
		}
		return null;
	}


	// Convert Work item data retrieved from MySQL
	// into an XML schema to pass back to client
	private Document toXmlItem(String id2, String desc2, String status2) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.newDocument();

			// Start building the XML
			Element root = doc.createElement( "Items" );
			doc.appendChild( root );

			Element item = doc.createElement( "Item" );
			root.appendChild( item );

			// Set Id
			Element id = doc.createElement( "Id" );
			id.appendChild( doc.createTextNode(id2 ) );
			item.appendChild( id );

			// Set Description
			Element desc = doc.createElement( "Description" );
			desc.appendChild( doc.createTextNode(desc2 ) );
			item.appendChild( desc );

			// Set Status
			Element status = doc.createElement( "Status" );
			status.appendChild( doc.createTextNode(status2 ) );
			item.appendChild( status );

			return doc;

		} catch(ParserConfigurationException e) {
			logger.error("Could not parse XML: " + e.getMessage());
		}
		return null;
	}
}
