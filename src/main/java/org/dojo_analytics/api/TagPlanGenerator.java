package org.dojo_analytics.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TagPlanGenerator {
	
	// INSTANCE VARIABLES
	
	String filePath;
	Website website;
	
	// CONSTRUCTORS
	
	public TagPlanGenerator(Connection c, Website website, String path){
		this.filePath = path;
		this.website = website;
		//this.run(c);
	}
	
	// METHODS
	
	public void run(Connection c){
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(new File(this.filePath));
	        Workbook workbook;
			workbook = new XSSFWorkbook(inputStream);
	        Sheet firstSheet = workbook.getSheetAt(0);	        
	        int nbCols = firstSheet.getRow(0).getLastCellNum();
	        Iterator<Row> iterator = firstSheet.iterator();        
	        ArrayList<String> headers = new ArrayList<String>();
	        this.website.clearPages(c);
	        while (iterator.hasNext()) {
	            Row nextRow = iterator.next();
	            if (nextRow.getRowNum() == 0){
	            	Iterator<Cell> cellIterator = nextRow.cellIterator();
	            	for (int i = 0 ; i < nbCols ; i++){
		            	Cell cell = cellIterator.next();
		            	if(i>0){
		            		headers.add(cell.getStringCellValue());
		            	}
	            	}
	            }else{
	            	// Create page with url = current row's first cell value
	            	Page page = new Page(c, this.website, nextRow.getCell(0).getStringCellValue());
	            	this.website.addPage(c, page);
	            	// Create custom dimension for each cell in this row
	            	Iterator<Cell> cellIterator = nextRow.cellIterator();
	            	for (int i = 0 ; i < nbCols ; i++){
		            	Cell cell = cellIterator.next();
		            	if(i>0){
		            		String cdimName = headers.get(i-1);
			                switch (cell.getCellType()) {
			                case Cell.CELL_TYPE_STRING:
			                	//System.out.println("String");
			                    page.addCustomDimension(c, new CustomDimension(c, page, cdimName, cell.getStringCellValue()));
			                    break;
			                case Cell.CELL_TYPE_BOOLEAN:
			                	//System.out.println("Bool");
			                	page.addCustomDimension(c, new CustomDimension(c, page, cdimName, String.valueOf(cell.getBooleanCellValue())));
			                    break;
			                case Cell.CELL_TYPE_NUMERIC:
			                	//System.out.println("Numeric");
			                	page.addCustomDimension(c, new CustomDimension(c, page, cdimName, String.valueOf(cell.getNumericCellValue())));
			                    break;
			                }
		            	}		            	
	            	}  
	            } 
	            inputStream.close();
	        }	
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}	
}
