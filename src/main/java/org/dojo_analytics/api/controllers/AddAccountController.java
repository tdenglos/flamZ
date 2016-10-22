package org.dojo_analytics.api.controllers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
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
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

@RestController
public class AddAccountController {

    @RequestMapping(value = "/ws/addaccount")
    public JSONObject uploadFile(
            //@RequestParam(value="googleid") String googleId,
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
    
    // Create user instance
    String googleId = principal.getName();
    Account user = new Account(db,googleId);
    
    
    // HERE : GET REQUEST TO GOOGLE TO GET MORE INFORMATION
    /*
    try {
		URL url = new URL("https://www.googleapis.com/plus/v1/people/"+principal.getName());
		BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
		String strTemp = "";
		while (null != (strTemp = br.readLine())) {
			System.out.println(strTemp);
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
    */
    /*
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);   
    Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(
             "Oauth2").build();
    Userinfoplus userinfo = oauth2.userinfo().get().execute();
    userinfo.toPrettyString();
    */
    
    
    String result="";
    
    // Add user to database if recognized as an expected beta tester
    if (user.isBetatester(db)){
    	result = user.create(db);
    }else{
    	result = "You are not registered as a beta tester. Please contact us to quickly get an early access.";
    }
    
	// Return result
    JSONObject response = new JSONObject();
	response.put("result", result);
	return response;
    }


}

