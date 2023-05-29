package tfip.akimori.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tfip.akimori.server.repositories.StoreRepository;

@Service
public class EmailSenderService {
    private static String websiteLink = "https://isms.up.railway.app/";
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private JwtService jwtSvc;
    @Autowired
    private StoreRepository storeRepo;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }

    // https://crontab.guru/
    // @Scheduled(cron = "0 0 * * * *", zone = "Asia/Singapore")
    // public void scheduledEmail() {
    // List<EmailSchedule> emailSchedules = userRepo.getAllSchedules(); // Retrieve
    // all user schedules

    // int currentHour = LocalTime.now().getHour();

    // for (EmailSchedule emailSchedule : emailSchedules) {
    // if (emailSchedule.getScheduledHour().equals(currentHour)) {
    // String toEmail = emailSchedule.getUserEmail();
    // String subject = "ISMS: Scheduled Email";
    // String body = "This is a scheduled email for user " + toEmail;
    // sendEmail(toEmail, subject, body);
    // }
    // }
    // }

    public void sendManagerInvite(String toEmail, String jwt, String storeID) throws MessagingException {
        // Generate confirmation link

        String inviterEmail = jwtSvc.extractUsername(jwt);
        String storeName = storeRepo.getStore(storeID).getStore_name();

        // Construct the HTML email body
        // String plainEmailBody = """
        // <p>This is an invitation by (%s) to manage an instrument store - %s</p>
        // <p>Click <a href=\"%s\">here</a> to join!</p>
        // """.formatted(inviterEmail, storeName, confirmationLink);
        String emailBody = formatInviteEmail(inviterEmail, storeName, websiteLink);
        System.out.println("SENDING EMAIL >>> " + emailBody);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Invitation to Manage Instrument Store");
        helper.setText(emailBody, true); // Set the email body as HTML

        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }

    public void sendWelcomeEmail(String toEmail, String givenname) throws MessagingException {
        // Generate confirmation link
        String websiteLink = "https://isms.up.railway.app/#/";

        // Construct the HTML email body
        String emailBody = formatWelcomeEmail(givenname, websiteLink);
        System.out.println("SENDING EMAIL >>> " + emailBody);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Invitation to Manage Instrument Store");
        helper.setText(emailBody, true); // Set the email body as HTML

        mailSender.send(message);
        System.out.println("Email sent to: " + toEmail);
    }

    // public void sendEmailWithAttachment(String toEmail, String subject, String
    // body, String attachment)
    // throws MessagingException {
    // MimeMessage mimeMsg = mailSender.createMimeMessage();
    // MimeMessageHelper mimeMsgHelper = new MimeMessageHelper(mimeMsg, true);
    // mimeMsgHelper.setFrom(fromEmail);
    // mimeMsgHelper.setTo(toEmail);
    // mimeMsgHelper.setSubject(subject);
    // mimeMsgHelper.setText(body);

    // FileSystemResource fsr = new FileSystemResource(new File(attachment));
    // mimeMsgHelper.addAttachment(fsr.getFilename(), fsr);

    // mailSender.send(mimeMsg);
    // System.out.println("Email sent to: " + toEmail);
    // }

    private String formatInviteEmail(String senderEmail, String storeName, String websiteLink) {
        return """
                <div align="center">
                    <div style="border: 1px solid black;
                                border-radius:10px;
                                padding:40px 20px"
                         align="center">
                        <div style="font-family:Roboto,RobotoDraft,Helvetica,Arial,sans-serif;
                                    border-bottom:thin solid #dadce0;
                                    color:rgba(0,0,0,0.87);
                                    line-height:32px;
                                    padding-bottom:24px;
                                    text-align:center;
                                    word-break:break-word">
                            <div style="font-size:24px">
                                Someone added you as inventory manager
                            </div>
                        </div>
                        <div style="font-family:Roboto-Regular,Helvetica,Arial,sans-serif;
                                    font-size:14px;
                                    color:rgba(0,0,0,0.87);
                                    line-height:20px;
                                    padding-top:20px;
                                    text-align:left">
                            <p>
                                %s added you as inventory manager of (%s) at <a href="%s">Instrument Store Management App</a><br>
                                If you don’t recognize this account, it’s likely your email address was added in error.
                            </p>
                        </div>
                    </div>
                </div>
                """
                .formatted(senderEmail, storeName, websiteLink);
    }

    private String formatWelcomeEmail(String givenname, String websiteLink) {
        return """
                <div align="center">
                    <div style="border: 1px solid black;
                                border-radius:10px;
                                padding:40px 20px"
                         align="center">
                        <div style="font-family:Roboto,RobotoDraft,Helvetica,Arial,sans-serif;
                                    border-bottom:thin solid #dadce0;
                                    color:rgba(0,0,0,0.87);
                                    line-height:32px;
                                    padding-bottom:24px;
                                    text-align:center;
                                    word-break:break-word">
                            <div style="font-size:24px">
                                Someone added you as inventory manager
                            </div>
                        </div>
                        <div style="font-family:Roboto-Regular,Helvetica,Arial,sans-serif;
                                    font-size:14px;
                                    color:rgba(0,0,0,0.87);
                                    line-height:20px;
                                    padding-top:20px;
                                    text-align:left">
                            <p>
                            Hello %s,

                            Thank you for registering with our <a href=\"%s\">platform</a>!
                            We are committed to providing you with a seamless and rewarding experience.
                            Should you have any questions or need assistance, our friendly support team is always here to help.

                            Once again, welcome aboard!

                            Best regards,
                            Wee Seng
                            </p>
                        </div>
                    </div>
                </div>
                """
                .formatted(givenname, websiteLink);
    }
}
