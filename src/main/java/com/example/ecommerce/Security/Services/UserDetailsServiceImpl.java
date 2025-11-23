package com.example.ecommerce.Security.Services;

import com.example.ecommerce.Model.User;
import com.example.ecommerce.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
                           //Calls DAO and encoder
@Service
public class UserDetailsServiceImpl implements UserDetailsService {  //Calls DAO and encoder type shit

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with given username!!!"));
        return UserDetailsImpl.build(user);     //Builds the container which stores user data
    }
}
