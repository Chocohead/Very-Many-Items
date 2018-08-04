package com.chocohead.nottmi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NotTMILog {
	public static final Logger log = LogManager.getLogger("Not TMI");

	public static void info(CharSequence text) {
		log.info(text);
	}

	public static void warn(CharSequence text) {
		log.warn(text);
	}
}