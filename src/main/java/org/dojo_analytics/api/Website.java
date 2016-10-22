package org.dojo_analytics.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Website {

	
	// INSTANCE VARIABLES
	
	int id;
	Account account;
	String name;
	ArrayList<Page> pages = new ArrayList<Page>();
	
	
	// CONSTRUCTORS
	
	public Website(Connection c, Account pAccount, String pName){
		this.account = pAccount;
		this.name = pName;
		if (this.checkExistence(c)) {
			this.setId(c);
		}
	}
	
	public Website(Connection c, int websiteId){
		this.id = websiteId;
	}
	
	
	// METHODS

	// Check if website exists in the database based on website name and account number
	public boolean checkExistence(Connection c){		
	    Statement stmt = null;
	    int num = 0;
	    try {		    	        
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT COUNT(website_id) FROM websites WHERE ("
		    		+ "websites.website_name = \'" + this.name + "\' "
		    				+ "AND websites.account_id = " + this.account.id + ");");
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
		    ResultSet rs = stmt.executeQuery("SELECT website_id FROM websites WHERE ("
		    		+ "websites.account_id = \'"+ this.account.id + "\' AND "
		    				+ "websites.website_name = \'" + name + "\');");
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

	// Add a page to the website
	public void addPage(Connection c, Page p){
	    Statement stmt = null;	    
	    try {	         	        
		    stmt = c.createStatement();    
		    if(!p.checkExistence(c)){
		    	stmt = c.createStatement();
			    String sql = "INSERT INTO pages (website_id, page_url) "
			               + "VALUES (" + this.id + ", \'" + p.url + "\');";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
			    p.setId(c);
			    System.out.println("Page " + p.url + " added to website " + this.name +" .");
		    } else {
		    	System.out.println("Warning: Page " + p.url + " was already present in database for this website.");
		    }
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	}
	
	// Get all page id's os this website in ArrayList<int>
	public ArrayList<Integer> getAllPageId(Connection c){
		ArrayList<Integer> pageIdList = new ArrayList<Integer>();
	    Statement stmt = null;	    
	    try {
		    stmt = c.createStatement();
		    ResultSet rs = stmt.executeQuery("SELECT page_id FROM pages WHERE "
		    		+ "pages.website_id = "+ this.id + ";");
		    while(rs.next()){
			    pageIdList.add(rs.getInt(1));
		    }
		    rs.close();
		    stmt.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }	
		return pageIdList;
	}
	
	// Clear all pages from this website
	public void clearPages(Connection c) {
		// 1. Delete information in customdimensions table for each page of this website
		ArrayList<Integer> list = this.getAllPageId(c);
		for (int i = 0; i < list.size(); i++) {
		    Statement stmt = null;	    
		    try {	         
		    	stmt = c.createStatement();
		    	String sql = "DELETE from customdimensions "
		    			+ "WHERE customdimensions.page_id = " + list.get(i) + ";";
			    stmt.executeUpdate(sql);
			    stmt.close();
			    c.commit();
		    } catch (Exception e) {
		        e.printStackTrace();
		        System.err.println(e.getClass().getName()+": "+e.getMessage());
		        System.exit(0);
		    }
		}		
		// 2. Delete information in website table for this website	    
		Statement stmt = null;
		try {	         
	    	stmt = c.createStatement();
	    	String sql = "DELETE from pages "
	    			+ "WHERE pages.website_id = " + this.id + ";";
		    stmt.executeUpdate(sql);
		    stmt.close();
		    c.commit();
		    System.out.println("All pages information deleted for website  " +  this.name +" .");
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }
	}

	
}
