package com.security.Security.JWT;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.Security.Config.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {//It extends OncePerRequestFilter – ensures one execution per request.

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    //This method is called automatically by Spring Security for every HTTP request when your filter is registered in the filter chain.
    @Override
    protected void doFilterInternal(HttpServletRequest request,//Contains request details: headers, parameters, body, method (GET/POST), etc.
                                    HttpServletResponse response,//Used to send response back if needed (e.g., error, 401).
                                    FilterChain filterChain) //Allows the request to pass to the next filter in the chain.
                                    		throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");//Looks for token like: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...

        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);//Extracting Token
            //In Below code we are extracting UserName from token. If token is tampered/expired, this fails.
            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Invalid JWT token");
            }
        }

        //If username is valid and not yet authenticated. In below code we are loading user from DB and match weather we took username from Token and from DB are same or not
        //SecurityContextHolder is Spring Security ka core class jo security-related information (like currently logged-in user) ko hold karta hai.
        //Purpose of SecurityContextHolder-->To store and retrieve authentication information during the entire lifecycle of a request (Thread-local scope).
        //SecurityContextHolder.getContext().getAuthentication() == null  Is condition se hum check kar rhe h ki kahi ye user ya koi or loggedin to nahi h kyo ki. agar ek baar bumne SecurityContextHolder me koi user set kardiya to wo ek request ke life span tak SecurityContextHolder me rhta h and hum use pure spring container me kahi bhi get kar skte h.  
       //ecurityContextHolder.getContext().getAuthentication() It returns the current user's Authentication object if present.
        // purpose explain below in easy language
        //SecurityContextHolder ->Hum yeh check karte hain ki agar JWT token se user ka naam mil gaya (token valid tha), lekin abhi tak Spring Security ke context me koi user setAuthentication() ke through set nahi hua, toh set karo.
        //Taaki duplicate set na ho aur already authenticated user ko baar-baar authenticate na karein.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);// Here we are loading user details from DB  UserDetails object has: username ,password, authorities (roles)

            if (jwtUtil.validateToken(token, userDetails.getUsername())) {//Calls a method in your JwtUtil class that: Parses the token, Checks: Signature is valid?, Token is not expired?, Token username matches DB username?
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());//This is Spring Security’s core Authentication object. You’re saying: "Yes, this user is valid. Here is his identity and roles."
                																								//Arguments of above method-> userDetails: the principal (who is logged in),null: credentials (password) — not needed since already authenticated, userDetails.getAuthorities(): list of roles (e.g., ROLE_USER)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));//Adds extra request info (like IP address, session ID) into token. Helps Spring internally for logging/auditing/etc. This line is optional but useful.

             // Final step: Set this user as authenticated
                SecurityContextHolder.getContext().setAuthentication(authToken);// most important line->You are telling Spring Security: This request is now authenticated. Here is the Authentication object with user identity and roles.
            }
        }

        filterChain.doFilter(request, response);//Passes control to the next filter in the chain. If all filters are done, controller gets the request.
    }
}
