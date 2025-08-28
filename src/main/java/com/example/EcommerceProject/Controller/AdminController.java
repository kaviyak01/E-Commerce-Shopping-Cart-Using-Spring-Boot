package com.example.EcommerceProject.Controller;

import com.example.EcommerceProject.ServiceImpl.*;
import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.model.Product;
import com.example.EcommerceProject.model.ProductOrder;
import com.example.EcommerceProject.model.category;
import com.example.EcommerceProject.util.Bucket;
import com.example.EcommerceProject.util.CommonUtil;
import com.example.EcommerceProject.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private CartServiceImpl cartService;

    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    @Lazy
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    @Autowired
    private FileServiceImpl fileService;


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
    public String adminIndex()
    {
        return "admin/index";
    }
    @GetMapping("/addproduct")
    public String addproduct(Model m)
    {
        List<category> categories=categoryServiceImpl.getAllCategory();
        m.addAttribute("categories",categories);
        return "admin/addproduct";
    }
    @GetMapping("/category")
    public String category(Model m, @RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                           @RequestParam(value = "pagesize", defaultValue = "9") int pagesize)
    {
//        model.addAttribute("categorys",categoryServiceImpl.getAllCategory());


        Page<category> page=categoryServiceImpl.getAllCategory(pageNo,pagesize);
        m.addAttribute("categorys", page);
        m.addAttribute("pageNo", pageNo);
        m.addAttribute("pageSize",pagesize);
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("currentPage", pageNo + 1);
        m.addAttribute("first",page.isFirst());
        m.addAttribute("last",page.isLast());

        return "admin/category";
    }

    @PostMapping("/savecategory")
    public String savecategory(@ModelAttribute category category,
                               @RequestParam("file") MultipartFile file,
                               HttpSession session) {
        try {


            String imagename=commonUtil.getUrl(file, Bucket.CATEGORY.getId());
            category.setImageName(imagename);

            Boolean exitCategory = categoryServiceImpl.existCategory(category.getName());
            if (exitCategory) {
                session.setAttribute("errorMsg", "Category Name already exists");
            } else {
                category saved = categoryServiceImpl.save(category);
                if (ObjectUtils.isEmpty(saved)) {
                    session.setAttribute("errorMsg", "Not saved! Internal server error");
                } else {

//                    File saveFile=new ClassPathResource("static/image").getFile();
//
//                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator+ "category" + File.separator+ file.getOriginalFilename());
//                    Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

                    fileService.uploadFileS3(file,1);
                    session.setAttribute("succMsg", "Saved successfully");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMsg", "Something went wrong: " + e.getMessage());
        }
        return "redirect:/admin/category";
    }
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id,HttpSession session)
    {
        Boolean delete=categoryServiceImpl.deleteCategory(id);
        if(delete)
        {
            session.setAttribute("succMsg","category Deleted successfully");
        }
        session.setAttribute("errorMsg","something went wrong on server");
        return "redirect:/admin/category";
    }

    @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable int id,Model m)
    {
        m.addAttribute("category",categoryServiceImpl.editCategoryById(id));
        return "/admin/EditCategory";
    }
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
        category oldcategory=categoryServiceImpl.editCategoryById(category.getId());
//        String imageName=file.isEmpty()? oldcategory.getImageName() : file.getOriginalFilename();

        String imageName=commonUtil.getUrl(file, Bucket.CATEGORY.getId());

        if(!ObjectUtils.isEmpty(oldcategory))
        {
            oldcategory.setName(category.getName());
            oldcategory.setIsActive(category.getIsActive());
            oldcategory.setImageName(imageName);
        }

        category updateCategory=categoryServiceImpl.save(oldcategory);

        if(!ObjectUtils.isEmpty(updateCategory))
        {
            fileService.uploadFileS3(file,1);
            session.setAttribute("succMsg","Category is updated successfully");
        }
        else
        {
            session.setAttribute("errorMsg","Something went wrong");
        }
        return "redirect:/admin/category";
    }

    @PostMapping("/saveProduct")
    public String saveProudct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session) throws IOException {
//        String imagename = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        String imagename=commonUtil.getUrl(image,Bucket.PRODUCT.getId());
        product.setImageName(imagename);

        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

       Product saveproduct= productService.save(product);


       if(!ObjectUtils.isEmpty(saveproduct))
       {

//           File saveFile=new ClassPathResource("static/image").getFile();
//
//           Path path = Paths.get(saveFile.getAbsolutePath() + File.separator+ "Product" + File.separator+ image.getOriginalFilename());
//
//           System.out.println(path);
//
//
//           Files.copy(image.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

           fileService.uploadFileS3(image,Bucket.PRODUCT.getId());

           session.setAttribute("succMsg","Product added successfully");
       }
       else
       {
           session.setAttribute("errorMsg","Something went Wrong..! may be a internal error");
       }
        return "redirect:/admin/addproduct";
    }


    @GetMapping("/ViewProduct")
    public String viewProducts(@RequestParam(value = "ch", required = false, defaultValue = "") String ch,Model m,@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(value = "pagesize", defaultValue = "2") int pagesize) {
//        model.addAttribute("Products", productService.getAllProduct());
        Page<Product> page=null;
        if(ch!=null && ch.length()>0) {
            page= productService.getSearchProductPagination(ch,pageNo,pagesize);
        }
        else
        {
            page=productService.getAllProductPagination(pageNo,pagesize);
        }


        m.addAttribute("Products",page.getContent());

        m.addAttribute("pageNo", pageNo);
        m.addAttribute("pageSize",pagesize);
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("currentPage", pageNo + 1);
        m.addAttribute("first",page.isFirst());
        m.addAttribute("last",page.isLast());

        return "admin/ViewProduct";

    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            session.setAttribute("succMsg", "Product Deleted Successfully");
        } else {
            session.setAttribute("errorMsg", "Something went wrong..!");
        }
        return "redirect:/admin/ViewProduct";  // ✅ Redirect must match the method below
    }

    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id,Model model)
    {
        model.addAttribute("Product",productService.getProductById(id));
        model.addAttribute("categories",categoryServiceImpl.editCategoryById(id));
        return "/admin/editProduct";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
        Product oldproduct=productService.getProductById(product.getId());
//        String imageName=file.isEmpty()? oldproduct.getImageName() : file.getOriginalFilename();

        String imageName=commonUtil.getUrl(file,Bucket.PRODUCT.getId());

        if(!ObjectUtils.isEmpty(oldproduct))
        {
            oldproduct.setTitle(product.getTitle());
            oldproduct.setDescription(product.getDescription());
            oldproduct.setCategory(product.getCategory());
            oldproduct.setPrice(product.getPrice());
            oldproduct.setStock(product.getStock());
            oldproduct.setImageName(imageName);
            oldproduct.setDiscount(product.getDiscount());

            //price=100 and discount=5 percent
            //then discountPrice=  100*(5/100)=5
            // discountPrice=100-5=95

            double discount=product.getPrice()*(product.getDiscount()/100.0);
            double discountPrice= product.getPrice()-discount;
            oldproduct.setDiscountPrice(discountPrice);

            oldproduct.setIsActive(product.getIsActive());
        }
        if(product.getDiscount()<0 || product.getDiscount()>100) {
            session.setAttribute("errorMsg", "The Discount value is between 0 and 100");
        }
        else {
            Product updateProduct = productService.save(oldproduct);

            if (!ObjectUtils.isEmpty(updateProduct)) {
                if (!file.isEmpty())
                    try {

//                        File saveFile = new ClassPathResource("static/image").getFile();
//
//                        Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product" + File.separator + file.getOriginalFilename());
//
//                        System.out.println(path);
//
//
//                        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                        fileService.uploadFileS3(file,Bucket.PRODUCT.getId());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                session.setAttribute("succMsg", "Category is updated successfully");
            } else {
                session.setAttribute("errorMsg", "Something went wrong");
            }
        }
        return "redirect:/admin/editProduct/"+ product.getId();
    }

    @GetMapping("/users")
    public String viewUsers(Model m,@RequestParam int type)
    {
        List<AppUser> users=null;
        if(type==1)
        {
            users=userService.getUserByRole("ROLE_USER");
        }
        else
        {
            users=userService.getUserByRole("ROLE_ADMIN");
        }
        m.addAttribute("userType",type);
        m.addAttribute("users",users);
        return "admin/users";
    }

    @GetMapping("/updateStatus")
    public String getUserStatus(@RequestParam boolean status,@RequestParam int id,HttpSession session,@RequestParam int type)
    {
        Boolean f=userService.updateAccountStatus(id,status);
        if(f)
        {
            session.setAttribute("succMsg","Status is updated Successfully");
        }
        else {
            session.setAttribute("errorMsg","Somthing went wrong in the Server");
        }
        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/allOrders")
    public String getAllOrders(Model m,@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(value = "pagesize", defaultValue = "2") int pagesize)
    {
        Page<ProductOrder> page=orderService.getAllOrdersPagination(pageNo,pagesize);
        m.addAttribute("orders",page.getContent());
        m.addAttribute("srch",false);

        m.addAttribute("pageNo", pageNo);
        m.addAttribute("pageSize",pagesize);
        m.addAttribute("totalElements",page.getTotalElements());
        m.addAttribute("totalPages",page.getTotalPages());
        m.addAttribute("currentPage", pageNo + 1);
        m.addAttribute("first",page.isFirst());
        m.addAttribute("last",page.isLast());
        return "/admin/Orders";
    }

    @PostMapping("/update-status")
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
        ProductOrder updated=orderService.updateStatusByid(oid,status);

        commonUtil.sendMailforOrderStatus(updated,status);

        if(!ObjectUtils.isEmpty(updated))
        {
            session.setAttribute("succMsg","Status Updated");
        }
        else
        {
            session.setAttribute("errorMsg","Somthing went wrong in server");
        }
        return "redirect:/admin/allOrders";
    }

    @GetMapping("/search-order")
    public String SeacrchOrder(@RequestParam String oid,HttpSession session,Model m,@RequestParam(value = "pageNo", defaultValue = "0") int pageNo,
                               @RequestParam(value = "pagesize", defaultValue = "2") int pagesize)
    {

        ProductOrder order= orderService.getSearchOrder(oid.trim());

        if(oid!=null && oid.length()>0) {
            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "The orderId is invalid");
                m.addAttribute("order", null);
            } else {
                m.addAttribute("order", order);
            }
            m.addAttribute("srch", true);
        }
        else{
            Page<ProductOrder> page=orderService.getAllOrdersPagination(pageNo,pagesize);
            m.addAttribute("orders",page.getContent());
            m.addAttribute("srch",false);

            m.addAttribute("pageNo", pageNo);
            m.addAttribute("pageSize",pagesize);
            m.addAttribute("totalElements",page.getTotalElements());
            m.addAttribute("totalPages",page.getTotalPages());
            m.addAttribute("currentPage", pageNo + 1);
            m.addAttribute("first",page.isFirst());
            m.addAttribute("last",page.isLast());
        }

        return "/admin/Orders";

    }

    @GetMapping("/addAdmin")
    public String addAdmin()
    {
        return "admin/add_admin";
    }

    @PostMapping("/saveUser")
    public String saveAdmin(@ModelAttribute AppUser user, @RequestParam("image") MultipartFile file, HttpSession session) throws IOException {
//        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

        String imageName=commonUtil.getUrl(file,Bucket.PROFILE.getId());
        user.setImageName(imageName);

        AppUser saveUser=userService.saveAdmin(user);


        if(!ObjectUtils.isEmpty(saveUser))
        {
            if(!file.isEmpty()) {

//                File saveFile = new ClassPathResource("static/image").getFile();
//
//                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "UserProfile" + File.separator + file.getOriginalFilename());
//
//                System.out.println(path);
//
//
//                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                fileService.uploadFileS3(file,Bucket.PROFILE.getId());
            }
            session.setAttribute("succMsg","Registered successfully");
        }
        else
        {
            session.setAttribute("errorMsg","Something went Wrong..! may be a internal error");
        }
        return "redirect:/admin/addAdmin";
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
        AppUser user=commonUtil.getLoggedInUserDetails(p);
        Boolean matches=passwordEncoder.matches(currentPassword,user.getPassword());

        if(matches)
        {
            String encode=passwordEncoder.encode(newPassword);
            user.setPassword(encode);
            userService.updateUser(user);
            session.setAttribute("succMsg","Password is updated");
        }
        else {
            session.setAttribute("errorMsg","Your Current Password is incorrect");
        }
        return "redirect:/profile";
    }


}
