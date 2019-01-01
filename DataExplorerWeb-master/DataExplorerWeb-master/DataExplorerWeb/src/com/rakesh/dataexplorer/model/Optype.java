package com.rakesh.dataexplorer.model;

/**
 * http://www.dummies.com/how-to/content/types-of-statistical-data-numerical-categorical-an.html
 */

public enum Optype {

    CATEGORICAL("categorical"), DISCRETE("discrete"), CONTINUOUS("continuous");
    
	private final String value;

    Optype(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}