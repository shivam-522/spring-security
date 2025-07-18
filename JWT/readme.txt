First we need to hit this request with user name and password
POST http://localhost:8080/authenticate
body-->{
    "username" : "shivam",
    "password":"shivam123"
}

These same userName and password are store in DB also so, Password is encrypted. So once we hit this request our user name and password wwill match with db userName and password. If valid than we got token in response for further request we need to hit request by placing this token in authheader.

Second request that is secured endpoint.

GET http://localhost:8080/secure/home

In Bearer token we need to place generated token "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzaGl2YW0iLCJpYXQiOjE3NTE1NjI3MjgsImV4cCI6MTc1MTU5ODcyOH0.ULA9doEAJ3-cuqt-nmOF8B2mM_kIRgXF5XOyeV2gQXY"



Client ➝ /authenticate (POST)
       ➝ SecurityConfig permits this
       ➝ AuthenticationManager.verify() → UserDetailsService → DB
       ➝ JWT Token created → returned to client

Client ➝ /secure/home (GET + Bearer Token)
       ➝ SecurityFilterChain blocks unauthenticated requests
       ➝ JwtFilter executes BEFORE UsernamePasswordAuthFilter
           ➝ Extract token from header
           ➝ Validate token & extract username
           ➝ Load user from DB
           ➝ Create Spring Authentication token
           ➝ Set SecurityContext
       ➝ Request proceeds to controller