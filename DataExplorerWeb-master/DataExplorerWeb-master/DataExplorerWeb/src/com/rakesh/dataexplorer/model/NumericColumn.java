package com.rakesh.dataexplorer.model;

public class NumericColumn extends Column {

	public NumericColumn() {
		super();
	}

	private double[] data;

	public double[] getData() {
		return data;
	}

	public void setData(double[] data) {
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data.length;
	}
	
	public double getElementAt(int index) {
		return data[index];
	}
}
