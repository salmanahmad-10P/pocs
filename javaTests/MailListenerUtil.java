/*
 * Copyright 2005 Wavechain Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.wavechain.utilities;

import	org.apache.log4j.Logger;
import	java.io.BufferedReader;
import	java.io.IOException;
import	java.io.Reader;
import	org.apache.commons.net.pop3.POP3Client;
import	org.apache.commons.net.pop3.POP3MessageInfo;

public class MailListenerUtil {
	private static Logger log = Logger.getLogger("MailListenerUtil");
	private static final String TEST_ALL = "TEST_ALL";

	public static final String FROM = "from";
	public static final String SUBJECT = "subject";
	public static final String MAIL_SERVER_ID = "MAIL_SERVER_ID";
	public static final String MAIL_USER_ID = "MAIL_USER_ID";
	public static final String MAIL_PASSWORD = "MAIL_PASSWORD";

	private String userId = null;
	private String password = null;
	private String serverId = null;
        private POP3Client pop3;
        private Reader reader;
        private POP3MessageInfo[] messages;

	public MailListenerUtil(String serverId, String userId, String password) {
		this.serverId = serverId;
		this.userId = userId;
		this.password = password;

		if(StringUtil.isEmpty(serverId) || StringUtil.isEmpty(userId) || StringUtil.isEmpty(password)) {
			log.fatal("Please pass a valid serverId, userId and password");
			return;
		}

        	pop3 = new POP3Client();
        	// We want to timeout if a response takes longer than 60 seconds
        	pop3.setDefaultTimeout(60000);

        	try {
            		pop3.connect(serverId);
        	} catch (IOException e) {
            		log.fatal("Could not connect to server: "+serverId);
			return;
        	}
	}

	public void scanMessages() {
        	int message;
        	try {
            		if (!pop3.login(userId, password)) {
                		log.fatal("Could not login to server: "+serverId+"  Check password.");
                		pop3.disconnect();
				return;
            		}

            		messages = pop3.listMessages();

            		if (messages == null) {
                		log.fatal("Could not retrieve message list from mail server at : "+serverId);
                		pop3.disconnect();
				return;
            		}
            		else if (messages.length == 0) {
                		log.info("No messages at mail server : "+serverId);
                		pop3.logout();
                		pop3.disconnect();
				return;
            		}

            		for (message = 0; message < messages.length; message++) {
                		reader = pop3.retrieveMessageTop(messages[message].number, 0);
                		if (reader == null) {
                    			log.fatal("Could not retrieve message header from mail server at : "+serverId);
                    			pop3.disconnect();
					return;
                		}
                		printMessageInfo(new BufferedReader(reader), messages[message].number);
            		}

            		pop3.logout();
            		pop3.disconnect();
        	} catch (IOException e) {
            		e.printStackTrace();
		}
    	}

    	public static void printMessageInfo(BufferedReader reader, int id) throws IOException {
        	String line = null;
		String lower = null;
		String from = null;
		String subject = null;
        	while ((line = reader.readLine()) != null) {
            		lower = line.toLowerCase();
            		if (lower.startsWith(FROM))
                		from = line.substring(6).trim();
            		else if (lower.startsWith(SUBJECT))
                		subject = line.substring(9).trim();
        	}
        	log.info(Integer.toString(id) + " From: " + from + "  Subject: " + subject);
    	}
}
