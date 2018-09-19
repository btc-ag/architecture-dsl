package com.btc.arch.generator;

import java.text.MessageFormat;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.btc.arch.generator.Messages;

public class LoggerUtils {

	public static void logExtendedInfo(Logger logger, Level logLevel,
			String message, Exception e) {
		logger.log(
				logLevel,
				message
						+ MessageFormat.format(Messages.ArchDslGenerator_Cause,
								e));
	}

}
