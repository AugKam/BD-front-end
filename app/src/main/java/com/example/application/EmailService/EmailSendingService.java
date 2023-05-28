package com.example.application.EmailService;

import android.view.View;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSendingService {
    public void sendEmail(String stringReceiverEmail, String randomCode){

        try {
            //Laiškų siuntimo servisui reikalingas el. pašto adresas ir jo slaptažodis.
            //stringSenderEmail reikia nurodyti el. pašto adresą, iš kurio siunčiami laiškai į naudotojo nurodyta el. paštą.
            String stringSenderEmail = "";
            //stringPasswordSenderEmail nurodyti el. pašto prisijungimo slaptažodį
            String stringPasswordSenderEmail = "";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("El. pašto patvirtinimas");
            mimeMessage.setText("Įveskite šį kodą programėleje " + randomCode);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
