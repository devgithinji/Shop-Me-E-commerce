package com.densoft.shopmeAdmin.security;

import com.densoft.shopmeAdmin.user.UserRepository;
import com.densoft.shopmecommon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return new CustomUserDetails(optionalUser.get());
        }
        throw new UsernameNotFoundException("Could not find user with email: " + email);
    }
}
