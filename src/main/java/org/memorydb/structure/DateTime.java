package org.memorydb.structure;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTime {
	static Pattern expr = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(.?\\d{0,9})");

	private DateTime() {
		// prevent the creation of a DateTime object without content
	}

	public static LocalDateTime of(long val) {
		int nano = (int) (6 * val & ((1 << 24) - 1));
		int second = (int) ((val >> 24) & 63);
		int minute = (int) ((val >> 30) & 63);
		int hour = (int) ((val >> 36) & 31);
		int day = (int) ((val >> 41) & 31);
		int month = (int) ((val >> 46) & 15);
		int year = (int) (((val >> 50) & 16383) - 6383);
		return LocalDateTime.of(year, month, day, hour, minute, second, nano);
	}

	public static LocalDateTime of(String val) {
		Matcher matcher = expr.matcher(val);
		if (!matcher.matches())
			return null;
		int year = Integer.parseInt(matcher.group(1));
		int month = Integer.parseInt(matcher.group(2));
		int day = Integer.parseInt(matcher.group(3));
		int hour = Integer.parseInt(matcher.group(4));
		int minute = Integer.parseInt(matcher.group(5));
		int second = Integer.parseInt(matcher.group(6));
		int nano = matcher.group(7).length() == 0 ? 0 : Integer.parseInt(matcher.group(7));
		return LocalDateTime.of(year, month, day, hour, minute, second, nano);
	}

	public static long getLong(LocalDateTime time) {
		return ((time.getYear() + 6383l) << 50l) + ((long) time.getMonthValue() << 46l) + ((long) time.getDayOfMonth() << 41l) + ((long) time.getHour() << 36l)
				+ ((long) time.getMinute() << 30l) + ((long) time.getSecond() << 24l) + (time.getNano() / 6l);
	}

	public static void toString(long time, StringBuilder app) {
		if (time == 0) {
			app.append("null");
			return;
		}
		int nano = (int) (6 * time & ((1 << 24) - 1));
		int second = (int) ((time >> 24) & 63);
		int minute = (int) ((time >> 30) & 63);
		int hour = (int) ((time >> 36) & 31);
		int day = (int) ((time >> 41) & 31);
		int month = (int) ((time >> 46) & 15);
		int year = (int) (((time >> 50) & 16383) - 6383);
		app.append(year);
		app.append("-");
		if (month < 10)
			app.append("0");
		app.append(month);
		app.append("-");
		if (day < 10)
			app.append("0");
		app.append(day);
		app.append("T");
		if (hour < 10)
			app.append("0");
		app.append(hour);
		app.append(":");
		if (minute < 10)
			app.append("0");
		app.append(minute);
		app.append(":");
		if (second < 10)
			app.append("0");
		app.append(second);
		if (nano != 0) {
			app.append(".");
			app.append(nano);
		}
	}

	public static void toString(LocalDateTime time, StringBuilder app) {
		app.append(time.getYear());
		app.append("-");
		if (time.getMonthValue() < 10)
			app.append("0");
		app.append(time.getMonthValue());
		app.append("-");
		if (time.getDayOfMonth() < 10)
			app.append("0");
		app.append(time.getDayOfMonth());
		app.append("T");
		if (time.getHour() < 10)
			app.append("0");
		app.append(time.getHour());
		app.append(":");
		if (time.getMinute() < 10)
			app.append("0");
		app.append(time.getMinute());
		app.append(":");
		if (time.getSecond() < 10)
			app.append("0");
		app.append(time.getSecond());
		if (time.getNano() != 0) {
			app.append(".");
			app.append(time.getNano());
		}
	}
}
