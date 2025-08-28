package com.example.EcommerceProject.Controller;

import com.example.EcommerceProject.ServiceImpl.CartServiceImpl;
import com.example.EcommerceProject.ServiceImpl.CategoryServiceImpl;
import com.example.EcommerceProject.ServiceImpl.UserServiceImpl;
import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.model.category;
import com.example.EcommerceProject.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CartServiceImpl cartService;

//    @Autowired
//    private CommonUtil commonUtil;

    @ModelAttribute
    public void getUserDetails(Principal p, Model m)
    {
        if(p!=null)
        {
            String email=p.getName();
            AppUser user=userService.getUserByEmail(email);
            m.addAttribute("user",user);
            int countCart=cartService.countCart(user.getId());
            m.addAttribute("countCart",countCart);
        }
        List<category> categories=categoryServiceImpl.getIsActiveCategory();
        m.addAttribute("categories",categories);
    }


    @GetMapping("/")
    public String userHome()
    {
        return "user/home";
    }
}
