package org.dojo_analytics.api.controllers;

import java.security.Principal;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PrincipalController {
		
	@CrossOrigin(origins = "http://localhost:8888")
	@RequestMapping(value = "/ws/principal", method = RequestMethod.GET)
	    public Principal principal(Principal principal) {
	
		    return  principal;

	    }
	
	
}
