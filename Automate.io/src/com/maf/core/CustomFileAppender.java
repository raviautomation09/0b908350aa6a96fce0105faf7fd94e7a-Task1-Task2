package com.maf.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;

/**
 * =======================================================================
 * 
 * @Author Ravindra Kumar - H124795
 * ==============================================
 * October 05, 2015 
 * Description : Log4j custom file appender
 * =======================================================================
 */

public class CustomFileAppender extends FileAppender {
	@Override
	public void setFile(String fileName) {
		if (fileName.indexOf("%timestamp") >= 0) {
			Date d = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
			fileName = fileName.replaceAll("%timestamp", format.format(d));
		}
		super.setFile(fileName);
	}
}