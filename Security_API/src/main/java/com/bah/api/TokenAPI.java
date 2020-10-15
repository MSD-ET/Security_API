package com.bah.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bah.domain.Customer;
import com.bah.domain.CustomerFactory;
import com.bah.domain.Token;
import com.bah.util.JWTHelper;
import com.bah.util.JWTUtil;

@RestController
@RequestMapping("/token")
public class TokenAPI {
	String dataApiHost = "localhost:8080";
	
	//private static Key key = AuthFilter.key;	
	public static Token appUserToken;

	
	
	@GetMapping
	public String getAll() {
		return "jwt-fake-token-asdfasdfasfa".toString();
	}
	
	@PostMapping	
	public ResponseEntity<?> createTokenForCustomer(@RequestBody Customer customer) {
		System.out.println("Create Token for Customer: " + customer);
		
		
		String username = customer.getName();
		String password = customer.getPassword();
		
		if (username != null && username.length() > 0 && password != null && password.length() > 0 && checkPassword(username, password)) {
			Token token = createToken(username);
			ResponseEntity<?> response = ResponseEntity.ok(token);
			return response;			
		}
		
		return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
	}
	
	private boolean checkPassword(String username, String password) {
		
		if(username.equals("ApiClientApp") && password.equals("secret")) {
			return true;
		}		
		Customer cust = getCustomerByNameFromCustomerAPI(username);
				
		if(cust != null && cust.getName().equals(username) && cust.getPassword().equals(password)) {
			return true;				
		}		
		return false;
		
		

	}
	
	public static Token getAppUserToken() {
		if(appUserToken == null || appUserToken.getToken() == null || appUserToken.getToken().length() == 0) {
			appUserToken = createToken("ApiClientApp");
		}
		return appUserToken;
	}
	
    private static Token createToken(String username) {
    	String scopes = "com.api.customer.r";    	// special case for application user
    	if( username.equalsIgnoreCase("ApiClientApp")) {
    		scopes = "com.bah.auth.apis";
    	}
    	Token token = JWTHelper.createToken(scopes);
    
    	
    	return token;
    }
    
    
	private Customer getCustomerByNameFromCustomerAPI(String username) {
		String apiHost = System.getenv("API_HOST");
			
// added lines 92,97 and 99
		System.out.println("getCustomerByNameFromCustomerAPI");
		try {
			if(apiHost ==null) { apiHost= this.dataApiHost;}
			
			URL url = new URL("http://" + apiHost + "/api/customers/byname/" + username);


			//url = new URL("http://localhost:8080/api/customers/byname/" + username);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			Token token = getAppUserToken();
			conn.setRequestProperty("authorization", "Bearer " + token.getToken());

			System.out.println("Connection out get response code");
			
			if (conn.getResponseCode() != 200) {
				return null;
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				String output = "";
				String out = "";
				while ((out = br.readLine()) != null) {
					output += out;
				}
				conn.disconnect();
				Customer customer = CustomerFactory.getCustomer(output);
				System.out.println("Get customer by name: " + customer);
				return customer;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;

		} catch (java.io.IOException e) {
			e.printStackTrace();
			return null;
		}

	}  	

}    

