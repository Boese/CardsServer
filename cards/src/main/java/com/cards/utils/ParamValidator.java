package com.cards.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class ParamValidator {
 
	private Pattern EmailPattern;
	private Pattern UsernamePattern;
	private Matcher matcher;
 
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static final String USERNAME_PATTERN = "^[a-zA-Z][a-z0-9_-]{2,15}$";
 
	public ParamValidator() {
		EmailPattern = Pattern.compile(EMAIL_PATTERN);
		UsernamePattern = Pattern.compile(USERNAME_PATTERN);
	}
	public boolean validate(final String hex, String type) {
		if(type.equalsIgnoreCase("email"))
			matcher = EmailPattern.matcher(hex);
		if(type.equalsIgnoreCase("username"))
			matcher = UsernamePattern.matcher(hex);
		return matcher.matches();
	}
}
