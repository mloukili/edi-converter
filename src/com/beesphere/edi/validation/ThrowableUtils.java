package com.beesphere.edi.validation;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtils {
	public static String toString (Throwable th) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		th.printStackTrace(pw);
		return sw.toString ();
	}
}
