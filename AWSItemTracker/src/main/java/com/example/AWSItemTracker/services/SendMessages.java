package com.example.AWSItemTracker.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.SesException;

public class SendMessages {

	private String sender;

	// The subject line for the email
	private String subject = "Weekly AWS Status Report";

	// The email body for recipients with non-HTML email clients
	private String bodyText = "Hello,\r\n" + "Please see the attached file for a weekly update.";

	// The HTML body of the email
	private String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
			+ "<p>Please see the attached file for a weekly update.</p>" + "</body>" + "</html>";
	
	private Region region;
	
	private Logger logger = LoggerFactory.getLogger(SendMessages.class);
	
	@Autowired
	public SendMessages(Environment env) {
		this.region = Region.of(env.getProperty("aws.region"));
		this.sender = env.getProperty("mail.sender");
	}
	
	public void sendReport(InputStream is, String emailAddress ) throws IOException {

		// Convert the InputStream to a byte[]
		byte[] fileContent = IOUtils.toByteArray(is);

		try {
			send(fileContent,emailAddress);
		} catch (MessagingException e) {
			logger.error("Could not send message to '" + emailAddress +"': " + e.getMessage());
		}
	}

	public void send(byte[] attachment, String emailAddress) throws MessagingException, IOException {

		MimeMessage message = null;
		Session session = Session.getDefaultInstance(new Properties());

		// Create a new MimeMessage object
		message = new MimeMessage(session);

		// Add subject, from and to lines
		message.setSubject(subject, "UTF-8");
		message.setFrom(new InternetAddress(sender));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));

		// Create a multipart/alternative child container
		MimeMultipart msgBody = new MimeMultipart("alternative");

		// Create a wrapper for the HTML and text parts
		MimeBodyPart wrap = new MimeBodyPart();

		// Define the text part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setContent(bodyText, "text/plain; charset=UTF-8");

		// Define the HTML part
		MimeBodyPart htmlPart = new MimeBodyPart();
		htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

		// Add the text and HTML parts to the child container
		msgBody.addBodyPart(textPart);
		msgBody.addBodyPart(htmlPart);

		// Add the child container to the wrapper object
		wrap.setContent(msgBody);

		// Create a multipart/mixed parent container
		MimeMultipart msg = new MimeMultipart("mixed");

		// Add the parent container to the message
		message.setContent(msg);

		// Add the multipart/alternative part to the message
		msg.addBodyPart(wrap);

		// Define the attachment
		MimeBodyPart att = new MimeBodyPart();
		DataSource fds = new ByteArrayDataSource(attachment, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		att.setDataHandler(new DataHandler(fds));

		String reportName = "WorkReport.xls";
		att.setFileName(reportName);

		// Add the attachment to the message
		msg.addBodyPart(att);

		// Send the email
		try {
			logger.info("Attempting to send an email through Amazon SES using the AWS SDK for Java...");

			SesClient client = SesClient.builder()
					.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
					.region(region)
					.build();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);

			ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

			byte[] arr = new byte[buf.remaining()];
			buf.get(arr);

			SdkBytes data = SdkBytes.fromByteArray(arr);

			RawMessage rawMessage = RawMessage.builder()
					.data(data)
					.build();

			SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
					.rawMessage(rawMessage)
					.build();

			client.sendRawEmail(rawEmailRequest);

		} catch (SesException e) {
			logger.error("Could not send email: " + e.awsErrorDetails().errorMessage());
			System.exit(1);
		}
		logger.info("Email sent with attachment");
	}

}
