package com.rakesh.dataexplorer.model;

public enum Datatype {
    STRING("string"), NUMERIC("numeric");
    
	private final String value;

    Datatype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}
