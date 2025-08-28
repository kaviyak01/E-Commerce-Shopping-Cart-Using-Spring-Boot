package com.example.EcommerceProject.Controller;

import com.example.EcommerceProject.ServiceImpl.*;
import com.example.EcommerceProject.model.*;
import com.example.EcommerceProject.util.Bucket;
import com.example.EcommerceProject.util.CommonUtil;
import com.example.EcommerceProject.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Binding;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Controller
public class HomeController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private final CommonUtil commonUtil;

    @Autowired
    private FileServiceImpl fileService;


    public HomeController(CommonUtil commonUtil) {
        this.commonUtil = commonUtil;
    }
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private PasswordEncoder PasswordEncoder;

    @ModelAttribute
    public void getUserDetails(Principal p,Model m)
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
    public String index(Model m){

        List<category> category=categoryServiceImpl.getIsActiveCategory().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getId(), c1.getId()))

                .limit(6)
                .collect(Collectors.toList());
        List<Product> products = productService.getIsActiveProduct("").stream()
                .sorted((p1, p2) -> Integer.compare(p2.getId(), p1.getId()))
                .limit(6)
                .collect(Collectors.toList());

        m.addAttribute("products",products);
        m.addAttribute("category",category);
        return "index";

    }
    @GetMapping("/signin")
    public String login()
    {
        return "login";
    }
    @GetMapping("/register")
    public String register()
    {
        return "register";
    }
    @GetMapping("/product")
    public String product(Model m,
                          @RequestParam(value = "category", defaultValue = "") String category,
                          @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                          @RequestParam(value = "pagesize", defaultValue = "9") int pagesize) {

        if (pagesize <= 0) pagesize = 9;

        List<com.example.EcommerceProject.model.category> categories = categoryServiceImpl.getIsActiveCategory();
        Page<Product> page = productService.getAllActiveProductPagination(pageNo, pagesize, category);

        m.addAttribute("Product", page); // ✅ Pass full Page<Product>
        m.addAttribute("categories", categories);
        m.addAttribute("ParamValue", category);
        m.addAttribute("pageNo", pageNo);

        m.addAttribute("pageSize",pagesize);
        m.addAttribute("currentPage", pageNo + 1);
        m.addAttribute("first",page.isFirst());
        m.addAttribute("last",page.isLast());


        return "product";
    }

    @GetMapping("/viewproduct/{id}")
    public String viewproduct(Model m, @PathVariable int id)
    {
        Product product=productService.getProductById(id);
        m.addAttribute("product",product);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute AppUser user, @RequestParam( "image") MultipartFile file, HttpSession session, BindingResult result) throws IOException {

        if (result.hasErrors()) {
            System.out.println("Binding Errors: " + result.getAllErrors());
            session.setAttribute("errorMsg", "Form submission error: " + result.getAllErrors());
            return "redirect:/register";
        }

        boolean existsuser=userService.existsByEmail(user.getEmail());

        if(existsuser)
        {
            session.setAttribute("errorMsg","The Email already exists");
        }

        else {
//            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

            String imageName=commonUtil.getUrl(file,Bucket.PROFILE.getId());
            user.setImageName(imageName);

            AppUser saveUser = userService.save(user);


            if (!ObjectUtils.isEmpty(saveUser)) {
                if (!file.isEmpty()) {

//                    File saveFile = new ClassPathResource("static/image").getFile();
//
//                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "UserProfile" + File.separator + file.getOriginalFilename());
//
//                    System.out.println(path);
//
//
//                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                    fileService.uploadFileS3(file, Bucket.PROFILE.getId());
                }
                session.setAttribute("succMsg", "Registered successfully");
            } else {
                session.setAttribute("errorMsg", "Something went Wrong..! may be a internal error");
            }
        }
        return "redirect:/register";
    }


//    forget password

    @GetMapping("/forgetPassword")
    public String forgetPassword()
    {
        return "ForgetPassword.html";
    }

    @PostMapping("/forgetPassword")
    public String processForgetPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        AppUser userEmail=userService.getUserByEmail(email);

        if(ObjectUtils.isEmpty(userEmail))
        {
            session.setAttribute("errorMsg","The Email is invalid");
        }
        else
        {

            String resetToken=UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

//            Generate url=http://localhost:8080/resetPasword?token=fjakljfaosjfwoehgoajflkasjglka

            String url=commonUtil.generateUrl(request)+"/resetPassword?token="+resetToken;
            Boolean sendMail= commonUtil.sendMail(url,email);
            if(sendMail)
            {
                session.setAttribute("succMsg","Please check your email.. Password Reset link sent");
            }
            else {
                session.setAttribute("errorMsg","Something went wrong on server !! Email not Send");
            }
        }
        return "redirect:/forgetPassword";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam String token,Model m)
    {

        AppUser userByToken=userService.getUserByToken(token);
        if(userByToken==null)
        {
             m.addAttribute("msg","Your link is invalid or Expired..");
             return "/error";
        }
        m.addAttribute("token",token);
        return "resetPassword.html";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String token,Model m,@RequestParam String password)
    {

        AppUser userByToken=userService.getUserByToken(token);
        if(userByToken==null)
        {
            m.addAttribute("msg","Your link is invalid or Expired..");
            return "/error";
        }
        else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            m.addAttribute("msg","Password Changed Successfully");
            return "/error";
        }
    }

    @GetMapping("/addCart")
    public String viewCart(@RequestParam int pid,@RequestParam int uid,HttpSession session){

        cart saveCart=cartService.saveCart(pid,uid);

        if(ObjectUtils.isEmpty(saveCart))
        {
            session.setAttribute("errorMsg","Product add to cart is failed");
        }
        else
        {
            
            session.setAttribute("succMsg","Product is successfully added to cart");
        }

        return "redirect:/viewproduct/"+ pid;
    }

    @GetMapping("/cart")
    public String loadCart(Principal p,Model m)
    {
        AppUser user=getLoggedInUserDetails(p);
        List<cart> carts=cartService.getCartByUser(user.getId());
        m.addAttribute("cart",carts);
        if(carts.size()>0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";

    }

    private AppUser getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        AppUser user=userService.getUserByEmail(email);
        return user;
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy,@RequestParam int cid)
    {
        cartService.updateCartQuantity(sy,cid);
        return "redirect:/cart";
    }

    @GetMapping("/order")
    public String order(Principal p,Model m)
    {

        AppUser user=getLoggedInUserDetails(p);
        List<cart> carts=cartService.getCartByUser(user.getId());
        m.addAttribute("cart",carts);
        if(carts.size()>0) {
            Double totalPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice()+250+100;
            m.addAttribute("totalPrice", totalPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }


    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request,Principal p)
    {

        AppUser user=getLoggedInUserDetails(p);
        orderService.saveProduct(user.getId(),request);
        return "/user/succMessage";
    }

    @GetMapping("/user-orders")
    public String viewUserOrders(Model m,Principal p)
    {

        AppUser user=getLoggedInUserDetails(p);
        List<ProductOrder> orders=orderService.viewOrders(user.getId());
        m.addAttribute("orders",orders);
        return "/user/MyOrders";
    }

    @GetMapping("/update-status")
    public String updateStatus(@RequestParam int oid,@RequestParam int st,HttpSession session)
    {
        OrderStatus[] values= OrderStatus.values();

        String status=null;
        for(OrderStatus os: values)
        {
            if(os.getId()==(st))
            {
                status=os.getName();
            }
        }
        ProductOrder updateStatus=orderService.updateStatusByid(oid,status);

        commonUtil.sendMailforOrderStatus(updateStatus,status);

        if(!ObjectUtils.isEmpty(updateStatus))
        {
            session.setAttribute("succMsg","Status Updated");
        }
        else
        {
            session.setAttribute("errorMsg","Somthing went wrong in server");
        }
        return "redirect:/user-orders";
    }

    @GetMapping("/profile")
    public String ViewProfile()
    {
        return "/profile";
    }

    @PostMapping("UpdateProfile")
    public String updateProfile(@ModelAttribute AppUser user,@RequestParam MultipartFile image,HttpSession session) throws IOException {
        AppUser User=userService.updateProfile(user,image);

        if(ObjectUtils.isEmpty(User))
        {
            session.setAttribute("errorMsg","The Profile is not updated");
        }
        else
        {
            fileService.uploadFileS3(image,Bucket.PROFILE.getId());
            session.setAttribute("succMsg","The Profile is updated");
        }
        return "redirect:/profile";
    }

    @PostMapping("/updatePassword")
     public String updatePassword(Principal p,@RequestParam String currentPassword,String newPassword,HttpSession session)
    {
        AppUser user=getLoggedInUserDetails(p);
        Boolean matches=PasswordEncoder.matches(currentPassword,user.getPassword());

        if(matches)
        {
            String encode=PasswordEncoder.encode(newPassword);
            user.setPassword(encode);
            userService.updateUser(user);
            session.setAttribute("succMsg","Password is updated");
        }
        else {
            session.setAttribute("errorMsg","Your Current Password is incorrect");
        }
        return "redirect:/profile";
    }

    @GetMapping("/search")
    public String getSearchProduct(@RequestParam String ch,Model m)
    {
        List<Product> products=productService.getSearchProduct(ch);
        m.addAttribute("Product",products);
        List<category> categories=categoryServiceImpl.getIsActiveCategory();
        m.addAttribute("categories",categories);
        return "product";
    }
}
