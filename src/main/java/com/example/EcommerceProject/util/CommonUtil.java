package com.example.EcommerceProject.util;

import com.example.EcommerceProject.ServiceImpl.UserServiceImpl;
import com.example.EcommerceProject.model.AppUser;
import com.example.EcommerceProject.model.ProductOrder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;



@Component
public class CommonUtil {

    @Autowired
    private UserServiceImpl userService;

    private final JavaMailSender mailSender;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;

    @Value("${aws.s3.bucket.product}")
    private String productBucket;

    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;

    @Autowired
    public CommonUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public Boolean sendMail(String url, String recipientEmail) {
        try {
            System.out.println("Sending email to: " + recipientEmail);
            System.out.println("Reset URL: " + url);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("kaviyausha72@gmail.com", "Shopping Cart");
            helper.setTo(recipientEmail);
            helper.setSubject("Password Reset");

            String htmlContent = "html>" +
            "<body>" +
                    "<h2>Password Reset Request</h2>" +
                    "<p>Click below to reset your password:</p>" +
                    "<p><a href=\"" + url + "\">Reset Password</a></p>" +
                    "<p>If you didn’t request this, please ignore this email.</p>" +
                    "<p>Regards,<br>Shopping Cart Team</p>" +
                    "</body>" +
                    "</html>";
            helper.setText(htmlContent, true);
            mailSender.send(message);

            System.out.println("Email send triggered successfully!");
            return true;

        } catch (Exception e) {
            System.out.println("Error while sending email:");
            e.printStackTrace();
            return false;
        }
    }


    public String generateUrl(HttpServletRequest request) {
        return request.getRequestURL().toString().replace(request.getServletPath(), "");
    }

    String msg=null;
    public Boolean sendMailforOrderStatus(ProductOrder order, String Status) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);


            msg="<p> Hello [[name]]</p>"+
                    "<p> Thank you , your order is [[orderStatus]]"+
                    "<p> Product Details</p>"+
                    "<p> Product Name: [[productName]]"+
                    "<p> Category: [[categoryName]]"+
                    "<p> Quantity: [[quantity]]"+
                    "<p> Price: [[price]]"+
                    "<p> Payment Type: [[paymentType]]";

            helper.setFrom("kaviyausha72@gmail.com", "Shopping Cart");
            helper.setTo(order.getOrderAddress().getEmail());
            helper.setSubject("Product Order Status");

            OrderStatus[] values= OrderStatus.values();

            msg=msg.replace("[[orderStatus]]",Status);
            msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
            msg=msg.replace("[[productName]]",order.getProduct().getTitle());
            msg=msg.replace("[[categoryName]]",order.getProduct().getCategory());
            msg = msg.replace("[[quantity]]", Integer.toString(order.getQuantity()));
            msg = msg.replace("[[price]]", Double.toString(order.getQuantity() * order.getPrice()));

            msg=msg.replace("[[paymentType]]",order.getPaymentType());


            helper.setText(msg, true);
            mailSender.send(message);

            return true;

        } catch (Exception e) {
            System.out.println("Error while sending email:");
            e.printStackTrace();
            return false;
        }
    }

    public AppUser getLoggedInUserDetails(Principal p) {
        String email=p.getName();
        AppUser user=userService.getUserByEmail(email);
        return user;
    }

    public String getUrl(MultipartFile file,int BucketType)
    {

        String bucketName=null;
        if(BucketType==1)
        {
            bucketName=categoryBucket;
        }
        else if(BucketType==2)
        {
            bucketName=productBucket;
        }
        else {
            bucketName=profileBucket;
        }

//      My url  https://ecommerce-project-category.s3.eu-north-1.amazonaws.com/cloth.jpg
        String imagename = file != null ? file.getOriginalFilename() : "default.jpg";
        String url="https://"+bucketName+".s3.eu-north-1.amazonaws.com/"+imagename;
        return url;
    }
}
