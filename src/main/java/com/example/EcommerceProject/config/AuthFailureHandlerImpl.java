package com.example.EcommerceProject.config;

import com.example.EcommerceProject.ServiceImpl.UserServiceImpl;
import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.repository.UserRepos;
import com.example.EcommerceProject.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepos userRepo;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String email=request.getParameter("username");

        AppUser user=userRepo.findByEmail(email);
        if(user!=null){

        if(user.isEnabled())
        {
            if(user.isAccountNonLocked())
            {
                if(user.getFailedAttempt()< AppConstant.ATTEMPT_TIME)
                {
                    userService.increaseFailedAttempt(user);
                }
                else{
                    userService.UserAccountLock(user);
                    exception =new LockedException("Your account is locked !! failed attempt 3");
                }
            }
            else {

                if(userService.UnlockAccountTimeExpire(user))
                {
                    exception =new LockedException("Your account is Unlocked !! Please try to login");
                }

                exception=new LockedException("your account is locked !! Please try after sometimes");
            }
        }
        else {
            exception=new LockedException("your acccount is inactive");
        }}
        else{
            exception=new LockedException("your email and password is incorrect");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
