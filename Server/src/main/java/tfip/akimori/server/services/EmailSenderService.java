package tfip.akimori.server.services;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import tfip.akimori.server.models.EmailSchedule;
import tfip.akimori.server.repositories.UserRepository;

@Service
public class EmailSenderService {

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtService jwtSvc;

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
    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Singapore")
    public void scheduledEmail() {
        List<EmailSchedule> emailSchedules = userRepo.getAllSchedules(); // Retrieve all user schedules

        int currentHour = LocalTime.now().getHour();

        for (EmailSchedule emailSchedule : emailSchedules) {
            if (emailSchedule.getScheduledHour().equals(currentHour)) {
                String toEmail = emailSchedule.getUserEmail();
                String subject = "ISMS: Scheduled Email";
                String body = "This is a scheduled email for user " + toEmail;
                sendEmail(toEmail, subject, body);
            }
        }
    }

    public void sendManagerInvite(String toEmail, String jwt, String storeName) throws MessagingException {
        // Generate confirmation link
        String confirmationLink = "https://mp-server-production.up.railway.app/#/";
        // TODO: need to store this request somehow

        String inviterEmail = jwtSvc.extractUsername(jwt);
        // Construct the HTML email body
        String emailBody = """
                <p>This is an invitation by (%s) to manage an instrument store - %s</p>
                <p>Click <a href=\"%s\">here</a> to join!</p>
                """.formatted(inviterEmail, storeName, confirmationLink);

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
}
