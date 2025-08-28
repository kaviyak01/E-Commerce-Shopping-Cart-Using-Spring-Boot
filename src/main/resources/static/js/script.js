$(function(){
    $("#userRegistration").validate({
        rules: {
            name: { required: true},
            number: {
                required: true,
                digits: true,
                minlength: 10,
                maxlength: 10
            },

            email: { required: true, email: true },
            address: { required: true },
            city: { required: true },
            state: { required: true },
            pincode: { required: true, digits: true },
            password: { required: true, minlength: 6 },
            confirmPassword: {
                required: true,
                equalTo: "[name='password']"
            },
            image: { required: true }

        },
        messages: {
            name: { required: "Name is required" },
            number: {
                required: "Mobile number is required",
                digits: "Enter digits only",
                minlength: "Mobile number must be 10 digits",
                maxlength: "Mobile number must be 10 digits"
            },

            email: { required: "Email is required", email: "Enter a valid email" },
            address: { required: "Address is required" },
            city: { required: "City is required" },
            state: { required: "State is required" },
            pincode: { required: "Pincode is required", digits: "Enter digits only" },
            password: { required: "Password is required", minlength: "Minimum 6 characters required" },
            confirmPassword: { required: "Please confirm password", equalTo: "Passwords do not match" },
            image:{required:"please upload your profile"}

        },
        errorElement: "div",
        errorPlacement: function(error, element) {
            error.addClass('text-danger');  // Bootstrap class for red text
            error.insertAfter(element);
        },
        submitHandler: function(form) {
            form.submit();
        }
    });
});
