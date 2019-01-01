package com.rakesh.dataexplorer.model;

import java.util.ArrayList;
import java.util.List;

public class ColumnData {
	
	private String name;
	private List<String> data = new ArrayList<String>();

	public ColumnData (String name) {
		this.name = name;
	}
	
	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
	
	public void addData(String dataPoint) {
		this.data.add(dataPoint);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getDataCount(){
		if(data != null){
			return data.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if((object != null) && (object instanceof ColumnData)) {
			ColumnData column = (ColumnData) object;
			return this.name.equals(column.getName());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

}
