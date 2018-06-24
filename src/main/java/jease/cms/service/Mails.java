/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jease.cms.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;
import java.util.function.Supplier;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import jease.Names;
import jease.Registry;
import jfix.db4o.Database;

/**
 * Service for sending emails.
 * 
 * The mail-service can be configured via a parameter with key
 * "JEASE_SMTP_PROPERTIES" which should provide properties according to the
 * JavaMail-API. Username and password can be provided via "mail.smtp.user" and
 * "mail.smtp.password".
 * 
 * Example for using the GoogleMail-SMTP-Server:
 * 
 * <pre>
 * mail.smtp.host						smtp.googlemail.com
 * mail.smtp.auth						true
 * mail.smtp.port						465
 * mail.smtp.socketFactory.port			465
 * mail.smtp.socketFactory.class		javax.net.ssl.SSLSocketFactory
 * mail.smtp.socketFactory.fallback		false
 * mail.smtp.user						dummy@gmail.com
 * mail.smtp.password					topsecret
 * </pre>
 */
public class Mails {

	private static Supplier<Properties> propertySupplier = new Supplier<Properties>() {
		public Properties get() {
			try {
				String parameter = Registry
						.getParameter(Names.JEASE_SMTP_PROPERTIES);
				if (parameter != null) {
					Properties properties = new Properties();
					properties.load(new StringReader(parameter));
					return properties;
				}
			} catch (IOException e) {
				// pass
			}
			return null;
		}
	};

	/**
	 * Sends an email synchronously.
	 */
	public static void send(String sender, String recipients, String subject,
			String text) throws MessagingException {
		final Properties properties = Database.query(propertySupplier);
		if (properties != null) {
			Session session = Session.getInstance(properties,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(properties
									.getProperty("mail.smtp.user", ""),
									properties.getProperty(
											"mail.smtp.password", ""));
						}
					});
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setReplyTo(new InternetAddress[] { new InternetAddress(
					sender) });
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subject, "utf-8");
			message.setSentDate(new Date());
			message.setHeader("Content-Type", "text/plain; charset=\"utf-8\"");
			message.setHeader("Content-Transfer-Encoding", "quoted-printable");
			message.setText(text, "utf-8");
			Transport.send(message);
		}
	}

	/**
	 * Sends an email asynchronously.
	 */
	public static void dispatch(final String sender, final String recipients,
			final String subject, final String text) {
		new Thread() {
			public void run() {
				try {
					send(sender, recipients, subject, text);
				} catch (MessagingException e) {
					// pass
				}
			}
		}.start();
	}
}
