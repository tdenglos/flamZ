package org.dojo_analytics.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataLayer {
	
	// INSTANCE VARIABLES
	
	public String content;
	
	// CONSTRUCTORS
	/*
	public DataLayer(Connection c, int pageId){
		String content=""; 
		content += "{";
	    Statement stmt = null;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT customdimension_name, customdimension_value "
		    		+ "FROM customdimensions WHERE customdimensions.page_id = "+ pageId + ";");
		    int i = 1;
		    while (rs.next()){
		    	if (i > 1){	content += ","; }
		    	content += "\n\t" + "\"" + rs.getString(1) + "\"" + " : " + "\"" + rs.getString(2) + "\"" ;
		    	i++;
		    };		   
		    rs.close();
		    stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }		
		content += "\n}";
		this.content = content;	
	}
	
	*/
	public DataLayer(Connection c, int websiteId, String PageUrl){
		JSONObject obj = new JSONObject();
		Statement stmt = null;
		this.content="dataLayer = [{\n";
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT customdimension_name, customdimension_value "
		    		+ "FROM websites, pages, customdimensions WHERE ("
		    		+ "customdimensions.page_id = pages.page_id "
		    		+ "AND pages.website_id  = " + websiteId + " AND "
		    		+ "pages.page_url = \'" + PageUrl + "\')"
		    				+ "GROUP BY customdimension_name, customdimension_value;");
		    int i = 0;
		    while (rs.next()){
		    	if(i>0){
		    		this.content += ",";
		    	}
			    this.content += "\t\'"+rs.getString(1) +"\' : \'"+ rs.getString(2)+"\'\n";
			    i++;
		    };		   
		    rs.close();
		    stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }		
		this.content += "}];";	
	}
	
	/*
	public DataLayer(Connection c, int websiteId, String PageUrl){
		String content=""; 
		content += "{";
	    Statement stmt = null;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT customdimension_name, customdimension_value "
		    		+ "FROM websites, pages, customdimensions WHERE ("
		    		+ "customdimensions.page_id = pages.page_id "
		    		+ "AND pages.website_id  = " + websiteId + " AND "
		    		+ "pages.page_url = \'" + PageUrl + "\')"
		    				+ "GROUP BY customdimension_name, customdimension_value;");
		    int i = 1;
		    while (rs.next()){
		    	if (i > 1){	content += ","; }
		    	content += "\n\t" + "\"" + rs.getString(1) + "\"" + " : " + "\"" + rs.getString(2) + "\"" ;
		    	i++;
		    };		   
		    rs.close();
		    stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }		
		content += "\n}";
		this.content = content;	
	}
	*/
}
