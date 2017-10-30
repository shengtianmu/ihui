package com.zpstudio.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author lxbccsu
 *日期比较差值不包括起始日期,包括最后日期
 */
public class DateTimeCalculate{
	private static final String FORMAT_DIFFYEARS = "%s年前";
	private static final String FORMAT_DIFFMONTHS = "%s月前";
	private static final String FORMAT_DIFFDAYS = "%s天前";
	private static final String FORMAT_DIFFHOURS = "%s小时前";
	private static final String FORMAT_DIFFMINUTES = "%s分钟前";
	private static final String FORMAT_DIFFSECONDS = "%s秒钟前";
	private long differenceOfYears = 0 ;//年份差值
	private long differenceOfMonths = 0 ;//月份差值
	private long differenceOfDays = 0 ;//天数差值
	private long differenceOfHours = 0 ;//小时数差值
	private long differenceOfMinutes = 0 ;//分钟数差值
	private long differenceOfSeconds = 0 ;//秒数差值
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static DateTimeCalculate fromThenOn(String startdate)
	{
		return calculate(startdate , dateFormat.format(new Date()));
	}
	public static DateTimeCalculate calculate(String startdate, String endDate){
		try {
			return calculate(dateFormat.parse(startdate),dateFormat.parse(endDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 计算差值,注意 endDate > startDate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static DateTimeCalculate calculate(Date startDate, Date endDate){
		if(startDate.after(endDate)) {
			endDate = new Date(startDate.getTime());
		}
		System.out.println("开始日：" + dateFormat.format(startDate) + ", 结束日: "+ dateFormat.format(endDate));
		DateTimeCalculate dataCalculate = new DateTimeCalculate();
		
		Calendar firstDay = Calendar.getInstance();
		Calendar lastDay = Calendar.getInstance();
		firstDay.setTime(startDate);
		lastDay.setTime(endDate);
		
		//算出天数总差值
		long allSeconds = (((lastDay.getTimeInMillis()) - (firstDay.getTimeInMillis()))/1000);
		long allDays 	= allSeconds/(24*60*60);
		long remainTime = allSeconds%(24*60*60);
		
		
		Calendar loopEndDay = calculateLoopEndOfDate(firstDay,lastDay);
		System.out.println("循环终止日期 : " + dateFormat.format(loopEndDay.getTime()));
		
		int month = firstDay.get(Calendar.MONTH);
		while(firstDay.before(loopEndDay)){
			firstDay.add(Calendar.DAY_OF_MONTH, 1);
			allDays--;
			if(month != firstDay.get(Calendar.MONTH)){
				month = firstDay.get(Calendar.MONTH);
				dataCalculate.setDifferenceOfMonths(dataCalculate.getDifferenceOfMonths()+1);
			}
		}
		long months = dataCalculate.getDifferenceOfMonths();
		dataCalculate.setDifferenceOfYears(months/12);
		dataCalculate.setDifferenceOfMonths(months%12);
		dataCalculate.setDifferenceOfDays(allDays);
		dataCalculate.setDifferenceOfHours(remainTime/(60*60));
		dataCalculate.setDifferenceOfMinutes((remainTime%(60*60))/60);
		dataCalculate.setDifferenceOfSeconds((remainTime%(60*60))%60);
		
		return dataCalculate;
		
	}

	/**
	 * 计算循环终止日期
	 * 例如:开始日：2011/03/17    结束日 2012/02/13 ,循环终止日期 2012/01/17;
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private static Calendar calculateLoopEndOfDate(Calendar startDate, Calendar endDate) {
		int year = endDate.get(Calendar.YEAR);
		int month = endDate.get(Calendar.MONTH);
		int day = startDate.get(Calendar.DAY_OF_MONTH);
		int maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,month,1));
		
		if(year > startDate.get(Calendar.YEAR)){
			if(month == Calendar.JANUARY){
				year -= 1;
				month = Calendar.DECEMBER;
			}else{
				if(day > maxDaysInMonth){
					month -= 1;
					endDate.set(year, month, 1);
					day = getMaxDaysOfMonth(new GregorianCalendar(year,month,1));
				}else{
					if(day > endDate.get(Calendar.DAY_OF_MONTH)){
						month -= 1;
						endDate.set(year, month, 1);
						maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,month,1));;
						if(day > maxDaysInMonth){
							day = maxDaysInMonth;
						}
					}
				}
			}
		}else{
			if(day > maxDaysInMonth){
				month -= 1;
				endDate.set(year, month, 1);
				day = getMaxDaysOfMonth(new GregorianCalendar(year,month,1));
			}else{
				if(day > endDate.get(Calendar.DAY_OF_MONTH)){
					month -= 1;
					endDate.set(year, month, 1);
					maxDaysInMonth = getMaxDaysOfMonth(new GregorianCalendar(year,month,1));
					if(day > maxDaysInMonth){
						day = maxDaysInMonth;
					}
				}
			}
		}
		
		return new GregorianCalendar(year, month, day);
	}

	/**
	 * 获取一月最大天数,考虑年份是否为润年
	 * (对API中的 getMaximum(int field)不了解, date.getMaximum(Calendar.DAY_OF_MONTH)却不是月份的最大天数)
	 * @param date
	 * @return
	 */
	private static int getMaxDaysOfMonth(GregorianCalendar date) {
		int month = date.get(Calendar.MONTH);
		int maxDays  = 0;
		switch(month){
			case Calendar.JANUARY:
			case Calendar.MARCH:
			case Calendar.MAY:
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.OCTOBER:
			case Calendar.DECEMBER:
			maxDays = 31;
			break;
			case Calendar.APRIL:
			case Calendar.JUNE:
			case Calendar.SEPTEMBER:
			case Calendar.NOVEMBER:
			maxDays = 30;
			break;
			case Calendar.FEBRUARY:
			if(date.isLeapYear(date.get(Calendar.YEAR))){
				maxDays = 29;
			}else{
				maxDays = 28;
			}
			break;
		}
		return maxDays;
	}

	/**
	 * @return the differenceOfYears
	 */
	public long getDifferenceOfYears() {
		return differenceOfYears;
	}

	/**
	 * @param differenceOfYears the differenceOfYears to set
	 */
	public void setDifferenceOfYears(long differenceOfYears) {
		this.differenceOfYears = differenceOfYears;
	}

	public long getDifferenceOfMonths() {
		return differenceOfMonths;
	}

	public void setDifferenceOfMonths(long differenceOfmonths) {
		this.differenceOfMonths = differenceOfmonths;
	}

	public long getDifferenceOfDays() {
		return differenceOfDays;
	}

	public void setDifferenceOfDays(long differenceOfDays) {
		this.differenceOfDays = differenceOfDays;
	}

	/**
	 * @return the differenceOfHours
	 */
	public long getDifferenceOfHours() {
		return differenceOfHours;
	}

	/**
	 * @param differenceOfHours the differenceOfHours to set
	 */
	public void setDifferenceOfHours(long differenceOfHours) {
		this.differenceOfHours = differenceOfHours;
	}

	/**
	 * @return the differenceOfMinutes
	 */
	public long getDifferenceOfMinutes() {
		return differenceOfMinutes;
	}

	/**
	 * @param differenceOfMinutes the differenceOfMinutes to set
	 */
	public void setDifferenceOfMinutes(long differenceOfMinutes) {
		this.differenceOfMinutes = differenceOfMinutes;
	}

	/**
	 * @return the differenceOfSeconds
	 */
	public long getDifferenceOfSeconds() {
		return differenceOfSeconds;
	}

	/**
	 * @param differenceOfSeconds the differenceOfSeconds to set
	 */
	public void setDifferenceOfSeconds(long differenceOfSeconds) {
		this.differenceOfSeconds = differenceOfSeconds;
	}
	
	public String getDiffrence()
	{
		if(getDifferenceOfYears()>0)
		{
			return String.format(FORMAT_DIFFYEARS, getDifferenceOfYears());
		}
		else if(getDifferenceOfMonths()>0)
		{
			return String.format(FORMAT_DIFFMONTHS, getDifferenceOfMonths());
		}
		else if(getDifferenceOfDays()>0)
		{
			return String.format(FORMAT_DIFFDAYS, getDifferenceOfDays());
		}
		else if(getDifferenceOfHours()>0)
		{
			return String.format(FORMAT_DIFFHOURS, getDifferenceOfHours());
		}
		else if(getDifferenceOfMinutes()>0)
		{
			return String.format(FORMAT_DIFFMINUTES, getDifferenceOfMinutes());
		}
		else
		{
			return String.format(FORMAT_DIFFSECONDS, getDifferenceOfSeconds());
		}
		
	}
	
}
