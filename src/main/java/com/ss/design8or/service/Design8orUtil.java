package com.ss.design8or.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author ezerbo
 *
 */
public class Design8orUtil {

	private Design8orUtil() {}
	
	public static String formatRotationTime(LocalTime rotationTime) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a");
		return rotationTime.format(dtf);
	}
}
