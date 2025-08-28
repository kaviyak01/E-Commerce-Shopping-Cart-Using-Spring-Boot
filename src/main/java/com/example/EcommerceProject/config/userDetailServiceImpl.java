package com.example.EcommerceProject.config;


import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.repository.UserRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepos userRepos;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user=userRepos.findByEmail(username);

        if(user==null)
        {
            throw new UsernameNotFoundException("user not found");
        }
        return new CustomUser(user);
    }
}
