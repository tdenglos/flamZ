package org.dojo_analytics.api;

import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Connection;


public class DbConnectionParamSet {

	// INSTANCE VARIABLES
	
	public String domain;
	public int port;
	public String dbName;
    String user;
    String password;
	
	// CONSTRUCTORS
    
    public DbConnectionParamSet(String pDomain, int pPort, String pDbName, String pUser, String pPassword){
    	this.domain = pDomain;
    	this.port = pPort;		
    	this.dbName = pDbName;
    	this.user = pUser;
    	this.password = pPassword;
    }
	
    // METHODS
    
    // Open a connection with the database
    public Connection openConnection(){
		Connection c = null;
	    try {	
	    	Class.forName("org.postgresql.Driver");
 	        c = DriverManager.getConnection("jdbc:postgresql:" 
 	        		+ this.domain +":" 
 	        		+ this.port 
 	        		+ "/" 
 	        		+ this.dbName, this.user, this.password);
	        c.setAutoCommit(false);
	        System.out.println("--- Connection created with " + this.domain + ":" + this.port + "/" + this.dbName);
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    return c;
    }
 
    // RESET FUNCTIONS
    
    // Reset all tables
    public void resetAllTables(Connection c){
	    this.resetCustomDimensionsTable(c);
		this.resetPagesTable(c);
		this.resetWebsitesTable(c);
	    this.resetAccountsTable(c);
    }
    
    // Empty websites tables and restart website_id sequence    
    public void resetWebsitesTable(Connection c){
	    Statement stmt = null;	    
	    try {	        
		    stmt = c.createStatement();		    
		    String sql = "TRUNCATE TABLE websites;";
		    stmt.executeUpdate(sql);		    
		    sql = "ALTER SEQUENCE websites_website_id_seq RESTART WITH 1;";
		    stmt.executeUpdate(sql);		    
		    stmt.close();
		    c.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    System.out.println("Websites table reset (all rows deleted and website_id sequence restarted with 1).");
	}
    
    // Empty accounts tables and restart website_id sequence
    public void resetAccountsTable(Connection c){
	    Statement stmt = null;
	    try {
		    stmt = c.createStatement();
		    String sql = "TRUNCATE TABLE accounts;";
		    stmt.executeUpdate(sql);
		    sql = "ALTER SEQUENCE accounts_account_id_seq RESTART WITH 1;";
		    stmt.executeUpdate(sql);
		    stmt.close();
		    c.commit();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	    System.out.println("Accounts table reset (all rows deleted and website_id sequence restarted with 1).");
	}
    
    // Empty pages table and restart page_id sequence
     public void resetPagesTable(Connection c){
 	    Statement stmt = null;
 	    try {
 		    stmt = c.createStatement();
 		    String sql = "TRUNCATE TABLE pages;";
 		    stmt.executeUpdate(sql);
 		    sql = "ALTER SEQUENCE pages_page_id_seq RESTART WITH 1;";
 		    stmt.executeUpdate(sql);
 		    stmt.close();
 		    c.commit();
 	    } catch (Exception e) {
 	        e.printStackTrace();
 	        System.err.println(e.getClass().getName()+": "+e.getMessage());
 	        System.exit(0);
 	    }
 	    System.out.println("Pages table reset (all rows deleted and page_id sequence restarted with 1).");
 	}
     
    // Empty customDimensions table and restart customDimension_id sequence
     public void resetCustomDimensionsTable(Connection c){
 	    Statement stmt = null;
 	    try {
 		    stmt = c.createStatement();
 		    String sql = "TRUNCATE TABLE customdimensions;";
 		    stmt.executeUpdate(sql);
 		    sql = "ALTER SEQUENCE customdimensions_customdimension_id_seq RESTART WITH 1;";
 		    stmt.executeUpdate(sql);
 		    stmt.close();
 		    c.commit();
 	    } catch (Exception e) {
 	        e.printStackTrace();
 	        System.err.println(e.getClass().getName()+": "+e.getMessage());
 	        System.exit(0);
 	    }
 	    System.out.println("Customdimensions table reset (all rows deleted and customdimension_id sequence restarted with 1).");
 	}    

    
}
