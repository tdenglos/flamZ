package org.dojo_analytics.api.controllers;

import java.sql.Connection;
import java.sql.SQLException;

import org.dojo_analytics.api.DataLayer;
import org.dojo_analytics.api.DbConnectionParamSet;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
public class DataLayerController {
	@CrossOrigin(origins = "*")
	 @RequestMapping(value = "/datalayer", method = RequestMethod.GET, produces="text/plain")
	    public String datalayer(@RequestParam(value="website") int websiteId, @RequestParam(value="page") String pageUrl) {
			
		 System.out.println("\nProcessing request for page " +pageUrl + " in website " +websiteId + ".");
		 
		 	// Set database connection parameters
		 	String dbDomain		= "//localhost";
			int dbPort			= 5432;
			String dbName 		= "dojo_DB";
		    String dbUser 		= "postgres";
		    String dbPassword 	= "pwd";
		    DbConnectionParamSet dbVars = new DbConnectionParamSet(dbDomain, dbPort, dbName, dbUser, dbPassword);
		    
		    // Create the connection using the above configuration
		    Connection db = dbVars.openConnection();  
		    
		    // Create response content
		    String result = new DataLayer(db, websiteId, pageUrl).content;
		    
		    // Close database connection
		    try {
				db.close();
				System.out.println("--- Connection closed with "+ dbVars.domain + ":" + dbVars.port + "/" + dbVars.dbName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
			}
			System.out.println("Request processed successfully.");
		    return result;

	    }
	
	
}
