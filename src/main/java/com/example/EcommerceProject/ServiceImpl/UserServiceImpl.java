package com.example.EcommerceProject.ServiceImpl;

import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.repository.UserRepos;
import com.example.EcommerceProject.service.UserService;
import com.example.EcommerceProject.util.AppConstant;
import com.example.EcommerceProject.util.Bucket;
import com.example.EcommerceProject.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepos userRepos;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    private CommonUtil commonUtil;

    @Autowired
    private FileServiceImpl fileService;

    @Override
    public AppUser save(AppUser user) {
        user.setRole("ROLE_USER");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        AppUser saveUsers=userRepos.save(user);
        return saveUsers;
    }

    @Override
    public AppUser getUserByEmail(String email) {
        return userRepos.findByEmail(email);
    }

    @Override
    public List<AppUser> getUserByRole(String role) {
        return userRepos.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(int id, boolean status) {
        Optional<AppUser> findByUser = userRepos.findById(id);

        if(findByUser.isPresent())
        {
            AppUser user=findByUser.get();
            user.setEnabled(status);
            userRepos.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void increaseFailedAttempt(AppUser user) {
        int attempt=user.getFailedAttempt()+1;
        user.setFailedAttempt(attempt);
        userRepos.save(user);
    }

    @Override
    public void UserAccountLock(AppUser user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepos.save(user);

    }

    @Override
    public Boolean UnlockAccountTimeExpire(AppUser user) {

        long lockTime= user.getLockTime().getClass().getModifiers();
        long unLockTime=lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime=System.currentTimeMillis();

        if(unLockTime<currentTime)
        {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepos.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        AppUser findByEmail=userRepos.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userRepos.save(findByEmail);
    }

    @Override
    public AppUser getUserByToken(String token) {

        return userRepos.findByResetToken(token);

    }

    @Override
    public AppUser updateUser(AppUser user) {
        return userRepos.save(user);
    }

    @Override
    public AppUser updateProfile(AppUser user, MultipartFile image) throws IOException {

        AppUser dbUser = userRepos.findById(user.getId()).orElse(null);



        if (dbUser != null) {
            // Update user details from the input form
            dbUser.setName(user.getName());
            dbUser.setNumber(user.getNumber());
            dbUser.setAddress(user.getAddress());
            dbUser.setCity(user.getCity());
            dbUser.setState(user.getState());
            dbUser.setPincode(user.getPincode());

            // Handle image upload
            if (!image.isEmpty()) {
//                String fileName = image.getOriginalFilename();

                String fileName=commonUtil.getUrl(image, Bucket.PROFILE.getId());

                // Save new filename to DB
                dbUser.setImageName(fileName);

                // Save image to folder
//                File saveFile = new ClassPathResource("static/image/UserProfile").getFile();
//                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + fileName);
//                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                fileService.uploadFileS3(image,Bucket.PROFILE.getId());
            }

            // Save updated user to DB
            dbUser = userRepos.save(dbUser);
        }

        return dbUser;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepos.existsByEmail(email);
    }

    @Override
    public AppUser saveAdmin(AppUser user) {
        user.setRole("ROLE_ADMIN");
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        String encodePassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        AppUser saveUsers=userRepos.save(user);
        return saveUsers;
    }

}
