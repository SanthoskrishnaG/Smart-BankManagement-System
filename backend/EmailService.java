package backend;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    // IMPORTANT: Users must replace these with their own Gmail and App Password to
    // test in real life.
    private static final String SENDER_EMAIL = "santhoskrishnag37@gmail.com";
    private static final String SENDER_PASSWORD = "ffan rzle ssst sxpm";

    public static boolean sendOTP(String recipientEmail, String otp) {
        // Setup Mail Server Properties for Gmail
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Authenticate
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Global Trust Bank - Verification Code");

            String htmlContent = "<h2>Welcome to Global Trust Bank!</h2>"
                    + "<p>Your account verification code is: <strong>" + otp + "</strong></p>"
                    + "<p>Please enter this code in the application to finalize your registration.</p>"
                    + "<p>Do not share this code with anyone.</p>";

            message.setContent(htmlContent, "text/html");

            // Send the email
            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
            return false;
        }
    }
}
