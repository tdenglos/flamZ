package org.dojo_analytics.api.controllers;


import java.io.IOException;
import java.security.Principal;
import java.sql.Connection;
import javax.servlet.annotation.MultipartConfig;

import org.dojo_analytics.api.Account;
import org.dojo_analytics.api.DbConnectionParamSet;
import org.dojo_analytics.api.Website;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeleteWebsiteController {

    @RequestMapping(value = "/ws/deletewebsite")
    public JSONObject uploadFile(
           // @RequestParam(value="googleid") String googleId,
            @RequestParam(value="website") int websiteId,
            Principal principal
    		) throws IOException {
    
	// Set database connection parameters
 	String dbDomain		= "//localhost";
	int dbPort			= 5432;
	String dbName 		= "dojo_DB";
    String dbUser 		= "postgres";
    String dbPassword 	= "pwd";
    DbConnectionParamSet dbVars = new DbConnectionParamSet(dbDomain, dbPort, dbName, dbUser, dbPassword);		    
    // Create the connection using the above configuration
    Connection db = dbVars.openConnection();	
    
    // Create website instance
    String googleId = principal.getName();
    Account user = new Account(db,googleId);
    Website website = new Website(db, websiteId);
    
    // Add website to database
    user.deleteWebsite(db, website);

	// Return result
    JSONObject result = new JSONObject();
	result.put("result", "Website deleted successfully... Poor website... :'(");
	return result;
    }


}

