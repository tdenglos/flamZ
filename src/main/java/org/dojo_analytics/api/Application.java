
package org.dojo_analytics.api;

import java.security.Principal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
//import java.sql.SQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.CompositeFilter;

@SpringBootApplication
@RestController
@EnableOAuth2Client
@EnableAuthorizationServer
@Order(6)
public class Application extends WebSecurityConfigurerAdapter{

	
	static Account user;
	static Website website = null;
	static Page page = null;
	static CustomDimension cdim = null;
	
	// Create a configuration set for communication with the database
	static String dbDomain		= "//localhost";
	static int dbPort			= 5432;
	static String dbName 		= "dojo_DB";
    static String dbUser 		= "postgres";
    static String dbPassword 	= "pwd";
    static DbConnectionParamSet dbVars = new DbConnectionParamSet(dbDomain, dbPort, dbName, dbUser, dbPassword);
    
    // Create the connection using the above configuration
    static Connection db = dbVars.openConnection(); 
	
    
    //SECURITY
    
	@Autowired
	OAuth2ClientContext oauth2ClientContext;
    
	@RequestMapping({ "/user", "/me" })
	public Map<String, String> user(Principal principal) {
		
		Account user = new Account(db, principal.getName()); // replace with Account (Connection, Principal) ?
		
		Map<String, String> map = new LinkedHashMap<>();
		
		map.put("googleid", user.googleId);
		map.put("id", String.valueOf(user.id));
		map.put("email", "");
				//((UserDetails)principal));
		map.put("picture", "");
		map.put("name", "");
		map.put("surname", "");
		map.put("betatester", String.valueOf(user.isBetatester(db)));
		
		return map;   		
	}	
	//public Principal user(Principal principal){
	//	return principal;
	//}
    
	
	
	
	@RequestMapping("/unauthenticated")
	public String unauthenticated() {
	  return "redirect:/?error=true";
	}
   
	//add-on to make datalayers accessible
	@Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/datalayer/**");
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.antMatcher("/**")
			.authorizeRequests()
				.antMatchers("/", "/login**", "/webjars/**", "/index", "/ws/datalayer", "/test.html").permitAll()
				.antMatchers("/private/**", "/ws/website", "/ws/addwebsite", "/ws/upload", "/ws/deletewebsite", "/ws/principal").hasRole("USER")
				.antMatchers("/ws/addaccount").hasRole("USER")
				.anyRequest().authenticated().
			and().exceptionHandling()
				.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
			.and().logout()
				.logoutSuccessUrl("/").permitAll()
				.and().csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
			.and()
				.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class)
 //       	.csrf().disable()
				;
		// @formatter:on
	}
    
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
			// @formatter:on
		}
	}
	
	
    
	public static void main(String args[]) {
		    
	    // Reset all tables
		
		dbVars.resetAllTables(db);
		/*    
	    // Story 1 - Create user account for user tdenglos@gmail.com and for doudours@gmail.com
	    
	    System.out.println("\n--- Story 1 - Create user account for user tdenglos@gmail.com and for doudours@gmail.com");
	    user = new Account(db, "tdenglos@gmail.com");
	    user.create(db);
	    user.create(db); // testing duplicate
	    user = new Account(db, "doudours@gmail.com");
	    user.create(db);
	    
	    // Story 2 - Create website tdenglos.com for user tdenglos@gmail.com and website doudours.com for doudours@gmail.com
	    
	    System.out.println("\n--- Story 2 - Create website tdenglos.com for user tdenglos@gmail.com and website doudours.com for doudours@gmail.com");
	    user = new Account(db, "tdenglos@gmail.com");
	    website = new Website(db, user, "tdenglos.com");
	    user.addWebsite(db, website);
	    user.addWebsite(db, website); // testing duplicate
	    user = new Account(db, "doudours@gmail.com");
	    website = new Website(db, user, "doudours.com");
	    user.addWebsite(db, website);

	    // Story 3 - Add page index.html and page product.html to doudours.com
	    
	    System.out.println("\n--- Story 3 - Add page index.html and page product.html to doudours.html");
	    user = new Account(db, "doudours@gmail.com");
	    website = new Website(db, user, "doudours.com");
	    page = new Page(db, website, "index.html");
	    website.addPage(db, page);
	    website.addPage(db, page); // testing duplicate
	    page = new Page(db, website, "product.html");
	    website.addPage(db, page);
	    
	    // Story 4 - Add 3 custom dimensions to index.html and retrieve DATA LAYER
	    
	    System.out.println("\n--- Story 4 - Add 3 custom dimensions to index.html and retrieve DATA LAYER");
	    page = new Page(db, website, "index.html");
	    cdim = new CustomDimension(db, page, "pageType", "Home");
	    page.addCustomDimension(db, cdim);
	    cdim = new CustomDimension(db, page, "productRange", "none");
	    page.addCustomDimension(db, cdim);
	    cdim = new CustomDimension(db, page, "visitorGender", "unknown");
	    page.addCustomDimension(db, cdim);
	    cdim = new CustomDimension(db, page, "visitorGender", "Female"); // testing duplicate
	    page.addCustomDimension(db, cdim); // testing duplicate (should not replace)
	    System.out.println(page.getDataLayer(db).content);
	    
	    // Story 5 - Add 3 custom dimensions to product.html and retrieve DATA LAYER
	    
	    System.out.println("\n--- Story 5 - Add 3 custom dimensions to product.html and retrieve DATA LAYER");
	    page = new Page(db, website, "product.html");
	    cdim = new CustomDimension(db, page, "pageType", "Product");
	    page.addCustomDimension(db, cdim);
	    cdim = new CustomDimension(db, page, "productRange", "Car");
	    page.addCustomDimension(db, cdim);
	    cdim = new CustomDimension(db, page, "visitorGender", "Male");
	    page.addCustomDimension(db, cdim);
	    System.out.println(page.getDataLayer(db).content);
	    System.out.println(new DataLayer(db, 2,"index.html").content);
	    
	    // Close connection with the database

	    try {
			db.close();
			System.out.println("--- Connection closed with "+ dbVars.domain + ":" + dbVars.port + "/" + dbVars.dbName);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    
	    // Story 6 - For user tdenglos@gmail.com, automatically create tag plan from Excel file for website tdenglos.com
	    System.out.println("\n--- Story 6 - For user tdenglos@gmail.com, automatically create tag plan from Excel file for website tdenglos.com");
	    user = new Account(db, "tdenglos@gmail.com");
	    user.create(db);	
    	website = new Website(db, user, "tdenglos.com");
	    user.addWebsite(db, website);
	    website = new Website(db, user, "thomasdenglos.com");
	    user.addWebsite(db, website);
	    TagPlanGenerator tpgen = new TagPlanGenerator(db, website, "/Users/thomas/Desktop/tagplan.xlsx");
	    tpgen.run(db);
    	tpgen.run(db); // testing re-write on existing website tagplan
	*/    
	    
	    Account user = new Account(db, "108593832187939818258");
	    user.authorizeBetatesting(db, "Thomas Denglos");
	    user = new Account(db, "114504894336086278354");
	    user.authorizeBetatesting(db, "thomas.denglos");
	    
		
		// Launch server
	    SpringApplication.run(Application.class, args);

	}
	
	
	@Bean
	public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(filter);
		registration.setOrder(-100);
		return registration;
	}

/*	@Bean
	@ConfigurationProperties("github")
	public ClientResources github() {
		return new ClientResources();
	}

	@Bean
	@ConfigurationProperties("facebook")
	public ClientResources facebook() {
		return new ClientResources();
	}
	*/
	@Bean
	@ConfigurationProperties("google")
	public ClientResources google() {
		return new ClientResources();
	}

	private Filter ssoFilter() {
		CompositeFilter filter = new CompositeFilter();
		List<Filter> filters = new ArrayList<>();
		//filters.add(ssoFilter(facebook(), "/login/facebook"));
		//filters.add(ssoFilter(github(), "/login/github"));
		filters.add(ssoFilter(google(), "/login/google"));
		filter.setFilters(filters);
		return filter;
	}

	private Filter ssoFilter(ClientResources client, String path) {
		OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationFilter = new OAuth2ClientAuthenticationProcessingFilter(
				path);
		OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
		oAuth2ClientAuthenticationFilter.setRestTemplate(oAuth2RestTemplate);
		UserInfoTokenServices tokenServices = new UserInfoTokenServices(client.getResource().getUserInfoUri(),
				client.getClient().getClientId());
		tokenServices.setRestTemplate(oAuth2RestTemplate);
		oAuth2ClientAuthenticationFilter.setTokenServices(tokenServices);
		return oAuth2ClientAuthenticationFilter;
	}
	
}

class ClientResources {

	@NestedConfigurationProperty
	private AuthorizationCodeResourceDetails client = new AuthorizationCodeResourceDetails();

	@NestedConfigurationProperty
	private ResourceServerProperties resource = new ResourceServerProperties();

	public AuthorizationCodeResourceDetails getClient() {
		return client;
	}

	public ResourceServerProperties getResource() {
		return resource;
	}
}

