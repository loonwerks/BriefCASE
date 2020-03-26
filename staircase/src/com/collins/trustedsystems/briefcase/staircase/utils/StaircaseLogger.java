package com.collins.trustedsystems.briefcase.staircase.utils;

public class StaircaseLogger {
	private static StringBuilder log = new StringBuilder();

	public static void logWarning(String message) {
		log.append("[WARNING]: ");
		log.append(message);
		log.append(System.lineSeparator());
	}

	public static void logError(String message) {
		log.append("[ERROR]: ");
		log.append(message);
		log.append(System.lineSeparator());
	}

	public static String getLog() {
		return log.toString();
	}

	@Override
	public String toString() {
		return log.toString();
	}
}
