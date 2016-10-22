package org.dojo_analytics.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Account {
	
	// INSTANCE VARIABLES
	
	int id;
	String googleId;
	String email;
	ArrayList<Website> websites = new ArrayList<Website>();
	String pictureUrl;
	String name;
	String surname;
	
	
	// CONSTRUCTORS
	
	public Account(Connection c, String pGoogleId){
		this.googleId = pGoogleId;
		if (this.checkExistence(c)) { 
			this.setId(c);
		}
		this.email="not provided";
	}
	
	// METHODS
	
	// Check if account exists in the database based on Google ID
	public boolean checkExistence(Connection c){		
	    Statement stmt = null;
	    int num = 0;
	    try {		    	        
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT COUNT(google_id) FROM accounts WHERE accounts.google_id = \'"
		    		+ this.googleId 
		    		+ "\' ;");
		    rs.next();
		    num = rs.getInt(1);
		    rs.close();
		    stmt.close();
		    c.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }	       
		return (num > 0);
	}
	
	// Create account in the database
	public String create(Connection c){
	    Statement stmt = null;	 
	    String output = "";
	    try {		    	    
		    if(!this.checkExistence(c)){
		    	stmt = c.createStatement();
			    String sql = "INSERT INTO accounts (google_id, email) "
			               + "VALUES (\'" 
			               + this.googleId 
			               + "\', \'" + this.email + "\');";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
			    this.setId(c);
		    	output = "User " + this.googleId + " created in database with email " + this.email + ".";
		    } else {
		    	output = "User " + this.googleId + " already present in database.";
		    }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    System.out.println(output);
	    return (output);
	}
	
	// Set account id by searching in database, based on account Google ID
	public void setId(Connection c){
	    Statement stmt = null;
	    int num = 0;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT account_id FROM accounts WHERE "
		    		+ "accounts.google_id = \'"+ this.googleId + "\' ;");
		    rs.next();
		    num = rs.getInt(1);
		    rs.close();
		    stmt.close();
		    c.commit();
		    this.id = num;
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	}

	// Check if account is already registered as beta tester
	public boolean isBetatester(Connection c){		
	    Statement stmt = null;
	    int num = 0;
	    try {		    	        
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT COUNT(google_id) FROM betatesters WHERE betatesters.google_id = \'"
		    		+ this.googleId 
		    		+ "\' ;");
		    rs.next();
		    num = rs.getInt(1);
		    rs.close();
		    stmt.close();
		    c.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }	       
		return (num > 0);
	}
	
	
	// Add account googleid and contact name to beta testers list
	public String authorizeBetatesting(Connection c, String contact){
	    Statement stmt = null;	
	    String output = "";
	
	    try {	         		    
			if(!this.isBetatester(c)){	
				stmt = c.createStatement();
			    String sql = "INSERT INTO betatesters (google_id, contact) "
			               + "VALUES (" + this.googleId + ", \'" + contact + "\');";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
			    output = "Betatester " + contact + " created in database for account " + this.googleId + ".";
		    } else {
		    	output = "Betatester " + contact + " already present in database.";
		    }		         	      	      
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    System.out.println(output);
	    return output;
	}
	
	
	// Add a website to the account
	public String addWebsite(Connection c, Website pWebsite){
	    Statement stmt = null;	
	    String output = "";
	    try {	         		    
			if(!pWebsite.checkExistence(c)){	
		    	stmt = c.createStatement();
			    String sql = "INSERT INTO websites (account_id, website_name) "
			               + "VALUES (" + this.id + ", \'" + pWebsite.name + "\');";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
			    pWebsite.setId(c);
			    output = "Website " + pWebsite.name + " created in database for account " + this.id + ".";
		    } else {
		    	output = "Website " + pWebsite.name + " already present in database.";
		    }		         	      	      
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    System.out.println(output);
	    return output;
	}
	
	// Get all websites of the account >> Returns JSON with websites' IDs and names
	public JSONObject getWebsites(Connection c){
		JSONObject obj = new JSONObject();
		JSONArray listOfWebsites = new JSONArray();
		Statement stmt = null;
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT website_id, website_name "
		    		+ "FROM websites WHERE account_id = " + this.id + " ;");
		    while (rs.next()){
		    	JSONObject website = new JSONObject();
		    	website.put("id", rs.getString(1));
		    	website.put("name", rs.getString(2));
		    	listOfWebsites.add(website);
		    };		   
		    obj.put("listOfWebsites", listOfWebsites);
		    rs.close();
		    stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }		
	    return obj;	
	}
		
	// Delete a website attached to this account (WARNING = any authenticated user can do in for any website !)
	public void deleteWebsite(Connection c, Website pWebsite){
	    
		pWebsite.clearPages(c);
		
		Statement stmt = null;	    
	    try {	         		    
	    	stmt = c.createStatement();
		    String sql = "DELETE from websites "
		               + "WHERE website_id = \'" + pWebsite.id + "\';";
		    stmt.executeUpdate(sql);
		    stmt.close();
		    c.commit();
	    	System.out.println("Website " + pWebsite.name + " deleted in database for account " + this.id + ".");        	      	      
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	       // System.exit(0);
	    }	
	}
	
	// Remove all websites for the account
	public void clearWebsites(Connection c){
		
	}
	
}
