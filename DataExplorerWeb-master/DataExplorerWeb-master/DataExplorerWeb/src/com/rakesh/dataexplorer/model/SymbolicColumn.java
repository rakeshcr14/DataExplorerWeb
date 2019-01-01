package com.rakesh.dataexplorer.model;

public class SymbolicColumn extends Column {
	public SymbolicColumn() {
		super();
	}

	private String[] data;

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		return data.length;
	}
	
	public String getElementAt(int index) {
		return data[index];
	}
}
