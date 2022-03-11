//$Id$
package com.zc.apihandlers.apiInputValidation;

public class APIInputValidationClass {
	public boolean ValidateAPIInput(String validateSource) {
		if(validateSource == null || validateSource.length() == 0 || validateSource.trim().isEmpty() || "\"\"".equals(validateSource)) {
			return true;
		}
		return false;
	}
}
