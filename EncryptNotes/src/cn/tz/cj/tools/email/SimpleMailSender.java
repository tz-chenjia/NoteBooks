package cn.tz.cj.tools.email;

import cn.tz.cj.tools.EncryptUtils;
import cn.tz.cj.tools.GlobalExceptionHandling;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;

public class SimpleMailSender {

    public static Properties getMailProperties() throws IOException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        Properties properties = new Properties();
        BufferedReader bufferedReader = null;
        bufferedReader = new BufferedReader(new InputStreamReader(SimpleMailSender.class.getResourceAsStream("email.properties")));
        properties.load(bufferedReader);
        properties.put("mail.password", EncryptUtils.d(properties.getProperty("mail.password"), SimpleMailSender.class.getName()));
        return properties;
    }

    /**
     * @param to
     * @param subject
     * @param content
     * @param files
     * @return
     */
    public static boolean sendMail(String to, String subject, String content, Map<String, File> files) {
        if (to != null) {
            try {
                Properties prop = getMailProperties();
                String user = prop.getProperty("mail.user");
                String password = prop.getProperty("mail.password");
                MailAuthenticator auth = new MailAuthenticator(user, password);
                Session session = Session.getInstance(prop, auth);

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(user));
                message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(to));
                message.setSubject(subject);

                MimeMultipart mimeMultipart = new MimeMultipart("mixed");
                message.setContent(mimeMultipart);
                for (Map.Entry<String, File> e : files.entrySet()) {
                    String fileName = e.getKey();
                    File file = e.getValue();
                    MimeBodyPart attch = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(file);
                    DataHandler dataHandler = new DataHandler(fileDataSource);
                    attch.setDataHandler(dataHandler);
                    attch.setFileName(fileName);
                    mimeMultipart.addBodyPart(attch);
                }
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                mimeMultipart.addBodyPart(mimeBodyPart);
                MimeMultipart bodyMultipart = new MimeMultipart("related");
                mimeBodyPart.setContent(bodyMultipart);
                MimeBodyPart htmlPart = new MimeBodyPart();
                bodyMultipart.addBodyPart(htmlPart);
                htmlPart.setContent(content, "text/html;charset=utf-8");
                Transport.send(message);
            }catch (Throwable e){
                GlobalExceptionHandling.exceptionHanding(e);
            }
            return true;
        } else {
            return false;
        }
    }
}
