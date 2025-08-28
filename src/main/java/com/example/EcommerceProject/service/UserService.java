package com.example.EcommerceProject.service;

import com.example.EcommerceProject.model.AppUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface UserService {

    public AppUser save(AppUser user);

    public AppUser getUserByEmail(String email);

  public List<AppUser> getUserByRole(String role);

   public Boolean updateAccountStatus(int id, boolean status);

   public void increaseFailedAttempt(AppUser user);

   public void UserAccountLock(AppUser user);

   public Boolean UnlockAccountTimeExpire(AppUser user);

   public void resetAttempt(int userId);

   public void updateUserResetToken(String email, String resetToken);

   public AppUser getUserByToken(String token);

   public AppUser updateUser(AppUser user);

   public AppUser updateProfile(AppUser user, MultipartFile image) throws IOException;

   public boolean existsByEmail(String email);

   public AppUser saveAdmin(AppUser user);


}
