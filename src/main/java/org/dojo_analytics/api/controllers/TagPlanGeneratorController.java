package org.dojo_analytics.api.controllers;


//import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.Principal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import javax.servlet.annotation.MultipartConfig;

import org.dojo_analytics.api.Account;
import org.dojo_analytics.api.DbConnectionParamSet;
import org.dojo_analytics.api.TagPlanGenerator;
import org.dojo_analytics.api.Website;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@MultipartConfig(fileSizeThreshold = 20971520)
public class TagPlanGeneratorController {

    @RequestMapping(value = "/ws/upload")
    public JSONObject uploadFile(
            @RequestParam("uploadedFile") MultipartFile uploadedFileRef
            ,@RequestParam(value="website") int websiteId
           // ,@RequestParam(value="googleid") String googleId
            ,Principal principal
          //  ,@RequestParam(value="website") String websiteName
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
    
    // Create website instance from Id
    String googleId = principal.getName();
    Account user = new Account(db,googleId);
   // Website website = new Website(db, user, websiteName);
    Website website = new Website(db, websiteId);
    
    // Get name of uploaded file.
    String fileName = uploadedFileRef.getOriginalFilename();

    // Path where the uploaded file will be stored.
    //String path = "D:/" + fileName;
    Path tempFilePath = Files.createTempFile(fileName, ".tmp");
    
    // This buffer will store the data read from 'uploadedFileRef'
    byte[] buffer = new byte[1000];

    // Now create the output file on the server.
    //File outputFile = new File(path);
    File outputFile = new File(tempFilePath.toString());

    FileInputStream reader = null;
    FileOutputStream writer = null;
    int totalBytes = 0;
    try {
        outputFile.createNewFile();

        // Create the input stream to uploaded file to read data from it.
        reader = (FileInputStream) uploadedFileRef.getInputStream();

        // Create writer for 'outputFile' to write data read from
        // 'uploadedFileRef'
        writer = new FileOutputStream(outputFile);

        // Iteratively read data from 'uploadedFileRef' and write to
        // 'outputFile';            
        int bytesRead = 0;
        while ((bytesRead = reader.read(buffer)) != -1) {
            writer.write(buffer);
            totalBytes += bytesRead;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }finally{
        try {
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    	try {
    		TagPlanGenerator tpgen = new TagPlanGenerator(db, website, tempFilePath.toString());
    	    tpgen.run(db);
    		List<String> lines = Arrays.asList();
			Files.write(tempFilePath, lines, Charset.defaultCharset(), StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject result = new JSONObject();
		result.put("result", "File uploaded successfully!");
		result.put("bytes", totalBytes);
    	return result;
    }


}

