package com.pms.constants;

public enum PMSLoginType {
	system("system"), 
	microsoft("microsoft"), 
	google("google");
	
	private String value;
	 
	PMSLoginType(String value) {
        this.value = value;
    }
 
    public String getValue() {
        return value;
    }
}
