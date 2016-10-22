package org.dojo_analytics.api.controllers;

import java.security.Principal;
import java.sql.Connection;
import java.sql.SQLException;

import org.dojo_analytics.api.Account;
import org.dojo_analytics.api.DbConnectionParamSet;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebsiteController {
		
	@CrossOrigin(origins = "http://localhost:8888")
	@RequestMapping(value = "/ws/website", method = RequestMethod.GET)
	    public JSONObject datalayer(
	    		//@RequestParam(value="googleid") String googleId,
	    		Principal principal) {
			
		 //System.out.println("\nProcessing request for user " + googleId + ".");
		 
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
		    String googleId = principal.getName();
		    Account currentUser = new Account(db, googleId);
		    JSONObject result = currentUser.getWebsites(db);
		    
		    // Close database connection
		    try {
				db.close();
				System.out.println("--- Connection closed with "+ dbVars.domain + ":" + dbVars.port + "/" + dbVars.dbName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println(e);
			}
		    
		    // Return response
			 System.out.println("Request processed successfully.");
		    return result;

	    }
	
	
}
