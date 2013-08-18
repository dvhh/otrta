package com.nibdev.otrtav2.model.tools;

import java.util.Comparator;
import java.util.Locale;

public class LowerStringComparator implements Comparator<String> {
	@Override
	public int compare(String lhs, String rhs) {
		return lhs.toLowerCase(Locale.getDefault()).compareTo(rhs.toLowerCase(Locale.getDefault()));
	}
}
