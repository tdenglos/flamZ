package org.dojo_analytics.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class CustomDimension {
	
	// INSTANCE VARIABLES
	
	int id;
	Page page;
	String name;
	String value;
	
	// CONSTRUCTORS
	
	public CustomDimension(Connection c, Page pPage, String pName, String pValue){
		this.page = pPage;
		this.name = pName;
		this.value = pValue;
		if (this.checkExistence(c)){
			this.setId(c);
		}
	}
	
	// Check if custom dimension is defined in the database for this page, based on page id and custom dimension name
	public boolean checkExistence(Connection c){		
	    Statement stmt = null;
	    int num = 0;
	    try {		    	        
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT COUNT(customdimension_id) FROM customdimensions WHERE ("
		    		+ "customdimensions.customdimension_name = \'" + this.name + "\' "
		    				+ "AND customdimensions.page_id = " + this.page.id + ");");
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
	
	// Set custom dimension id based on page id and custom dimension name
	public void setId(Connection c){
	    Statement stmt = null;
	    int num = 0;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT customdimension_id FROM customdimensions WHERE ("
		    		+ "customdimensions.page_id = "+ this.page.id + " AND "
		    				+ "customdimensions.customdimension_name = \'" + this.name + "\');");
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

	
}
