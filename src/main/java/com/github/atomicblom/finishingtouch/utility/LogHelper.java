package com.github.atomicblom.finishingtouch.utility;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class LogHelper
{
	private static Logger modLog = null;

	public static void all(Object object)
	{
		modLog.log(Level.ALL, object);
	}

	public static void debug(Object object)
	{
		modLog.log(Level.DEBUG, object);
	}
	public static void debug(String format, Object... parameters)
	{
		modLog.log(Level.DEBUG, format, parameters);
	}

	public static void error(Object object)
	{
		modLog.log(Level.ERROR, object);
	}
	public static void error(String format, Object... parameters)
	{
		modLog.log(Level.ERROR, format, parameters);
	}

	public static void fatal(Object object)
	{
		modLog.log(Level.FATAL, object);
	}

	public static void info(Object object)
	{
		modLog.log(Level.INFO, object);
	}

	public static void info(String format, Object... parameters) {
		modLog.log(Level.INFO, format, parameters);
	}

	public static void off(Object object)
	{
		modLog.log(Level.OFF, object);
	}

	public static void trace(Object object)
	{
		modLog.log(Level.TRACE, object);
	}

	public static void trace(String format, Object... parameters)
	{
		modLog.log(Level.TRACE, format, parameters);
	}

	public static void warn(Object object)
	{
		modLog.log(Level.WARN, object);
	}

	public static void setLog(Logger modLog)
	{

		LogHelper.modLog = modLog;
	}
}