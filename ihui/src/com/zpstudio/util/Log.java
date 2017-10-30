package com.zpstudio.util;

import org.apache.log4j.Logger;


public class Log {

	public static void i(String tag , String msg)
	{
		Logger.getLogger(tag).info(msg);
	}
	public static void i(String tag , String msg,Throwable t)
	{
		Logger.getLogger(tag).info(msg ,t);
	}
	public static void d(String tag , String msg)
	{
		Logger.getLogger(tag).debug(msg);
	}
	public static void d(String tag , String msg,Throwable t)
	{
		Logger.getLogger(tag).debug(msg,t);
	}
	
	public static void w(String tag , String msg)
	{
		Logger.getLogger(tag).warn(msg);
	}
	public static void w(String tag , String msg ,Throwable t)
	{
		Logger.getLogger(tag).warn(msg, t);
	}
	
	public static void e(String tag , String msg)
	{
		Logger.getLogger(tag).error(msg);
	}
	public static void e(String tag , String msg,Throwable t)
	{
		Logger.getLogger(tag).error(msg ,t);
	}
	public static void f(String tag , String msg)
	{
		Logger.getLogger(tag).fatal(msg);
	}
	public static void f(String tag , String msg,Throwable t)
	{
		Logger.getLogger(tag).fatal(msg,t);
	}
	public static void v(String tag , String msg)
	{
		Logger.getLogger(tag).info(msg);
	}
	public static void v(String tag , String msg,Throwable t)
	{
		Logger.getLogger(tag).info(msg,t);
	}
}
