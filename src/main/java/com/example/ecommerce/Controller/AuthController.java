package com.example.ecommerce.Controller;

import com.example.ecommerce.Model.AppRoles;
import com.example.ecommerce.Model.Role;
import com.example.ecommerce.Model.User;
import com.example.ecommerce.Repository.RoleRepository;
import com.example.ecommerce.Repository.UserRepository;
import com.example.ecommerce.Security.Request.LoginRequestDTO;
import com.example.ecommerce.Security.Request.SignUpRequest;
import com.example.ecommerce.Security.Response.MessageResponse;
import com.example.ecommerce.Security.Response.UserInfoResponse;
import com.example.ecommerce.Security.Services.UserDetailsImpl;
import com.example.ecommerce.Security.jwt.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequestDTO){
        Authentication authentication;  //auth obj
        try{
            authentication = authenticationManager.authenticate(  //.authenticate will check the username and password with the obj provided, and then it will load auth obj with userdetails if correct
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()) //Usernamepasstokken is used to describe username pass
            );
        }catch(AuthenticationException e){
            Map<String , Object> map = new HashMap<>();
            map.put("message", "Invalid username or password");
            map.put("status", false);

            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);  //logs the user for current login request

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal(); //auth obj se data le liya

        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails); //generating cookie

        List<String> roles = userDetails.getAuthorities().stream()  //user can have multiple roles
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){

        //Checking for already existing account
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            MessageResponse messageRes = new MessageResponse("UserName already Exists!!!");
            return new ResponseEntity<>(messageRes.getMessage(), HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            MessageResponse messageRes = new MessageResponse("Email already Exists!!!");
            return new ResponseEntity<>(messageRes.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Saving User
        User user = new User(  //Creating user object for saving
                signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getEmail()
        );

        Set<String> strRoles = signUpRequest.getRole();; //Coming from User
        Set<Role> roles = new HashSet<>();  //Variable

        //If not provided roles then assign user
        if(strRoles == null){
             Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                     .orElseThrow(() -> new RuntimeException("User not found"));
             roles.add(userRole);
        }else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleName(AppRoles.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleName(AppRoles.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Seller not found"));
                        roles.add(sellerRole);
                        break;
                    default:
                        //If not admin or seller by default assign user
                        Role userRole = roleRepository.findByRoleName(AppRoles.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    //For Profile pages

    @GetMapping("/username")
    public String currentUsername(Authentication authentication){   //Auth ka object hamesha data store karke rkhta hai
                                                                    //as vo ContextHolder mai save rhta har request ke liye
        if(authentication != null){
            return authentication.getName();
        }
        return null;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()  //user can have multiple roles
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser(){
        ResponseCookie cookie = jwtUtils.getCleanCookie();
        return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,
                cookie.toString()).body(new MessageResponse("Successfully logged out!"));
    }

}
