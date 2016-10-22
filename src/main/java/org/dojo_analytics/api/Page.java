package org.dojo_analytics.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Page {

	// INSTANCE VARIABLES	
	int id;
	String url;
	Website website;
	
	// CONSTRUCTORS
	public Page(Connection c, Website website, String pUrl){
		this.website = website;
		this.url = pUrl;
		if (this.checkExistence(c)) {
			this.setId(c);
		}
	}
	
	// METHODS
	
    // Add a custom dimension to a page
    public void addCustomDimension(Connection c, CustomDimension cdim){
	    Statement stmt = null;	    
	    try {	         
	    	stmt = c.createStatement();
		    if(!cdim.checkExistence(c)){
		    	String sql = "INSERT INTO customdimensions (page_id, customdimension_name, customdimension_value) "
			               + "VALUES (" + this.id + ", \'" + cdim.name + "\', \'" + cdim.value + "\');";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
			    cdim.setId(c);
			    System.out.println("Custom Dimension " + cdim.name + " added to page " +  this.url +" .");
		    }else{
		    	System.out.println("Custom Dimension " + cdim.name + " already defined for page " +  this.url +" .");
		    }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	}
    
	// Check if page exists in the database based on page url and website id
	public boolean checkExistence(Connection c){		
	    Statement stmt = null;
	    int num = 0;
	    try {		    	        
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT COUNT(page_id) FROM pages WHERE ("
		    		+ "pages.page_url = \'" + this.url + "\' "
		    				+ "AND pages.website_id = " + this.website.id + ");");
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
	
	// Set website id based on user account and website name
	public void setId(Connection c){
	    Statement stmt = null;
	    int num = 0;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT page_id FROM pages WHERE ("
		    		+ "pages.website_id = \'"+ this.website.id + "\' AND "
		    				+ "pages.page_url = \'" + url + "\');");
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
/*
	// Get DATA LAYER of the page   
	public DataLayer getDataLayer(Connection c){
		DataLayer datalayer = new DataLayer(c, this.id);
		return datalayer;
	} 
	*/

}
