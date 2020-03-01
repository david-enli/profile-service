package com.profile.config;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailUtil {
    static final String FROM = "E-commerce <david.enli@gmail.com>";

    // Replace recipient@example.com with a "To" address. If your account
    // is still in the sandbox, this address must be verified.
    static final String TO = "recipient@example.com";
    static final String SUBJECT = "Account registration successful";
    static final String TEXTBODY = "account registration successful, your user name is %s;";


    @Value("${amazon.aws.accessKey}")
    private String access_key_id;

    @Value("${amazon.aws.secretKey}")
    private String secret_access_key;
    public void sendEmail(String emailAddress, String username) {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials(access_key_id, secret_access_key);
        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard()
                            // Replace US_WEST_2 with the AWS Region you're using for
                            // Amazon SES.
                            .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                            .withRegion(Regions.US_WEST_2).build();
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(emailAddress))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withText(new Content()
                                            .withCharset("UTF-8").withData(String.format(TEXTBODY, username ))))
                            .withSubject(new Content()
                                    .withCharset("UTF-8").withData(SUBJECT)))
                    .withSource(FROM);

            client.sendEmail(request);
            System.out.println("Email sent!");
        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: "
                    + ex.getMessage());
        }
    }
}
