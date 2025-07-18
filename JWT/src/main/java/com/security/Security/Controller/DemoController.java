package com.security.Security.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.security.Security.Config.CustomUserDetailsService;
import com.security.Security.JWT.AuthRequest;
import com.security.Security.JWT.AuthResponse;
import com.security.Security.JWT.JwtUtil;



@RestController
public class DemoController {
	
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {//Request ke body me username aur password aate hain in JSON format, Ye JSON Spring automatically convert kar deta hai Java object AuthRequest me.
        // 1. Authenticate username/password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));//Ye line internally check karti hai: 1. Kya username aur password database me valid hai? 2. Agar galat hai to ye line hi Exception throw kar degi (like BadCredentialsException
        		//Ye behind the scenes UserDetailsService use karta hai user details validate karne ke liye.

        // 2. Load user from DB by user name here we are getting everything role etc. Because we need to pass this user to generate JWT token.
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // 3. Generate JWT token
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // 4. Return token
        return ResponseEntity.ok(new AuthResponse(token));
    }
    
	
	
	
	
	@GetMapping("/public/welcome")
    public String welcome() {
        return "Welcome, Guest!";
    }

    @GetMapping("/secure/home")
    public String home() {
        return "Welcome to Secure Page!";
    }
}
