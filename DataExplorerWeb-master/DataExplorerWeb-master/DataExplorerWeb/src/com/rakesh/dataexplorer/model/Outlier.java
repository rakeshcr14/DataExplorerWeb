package com.rakesh.dataexplorer.model;

public class Outlier {

	private double lowerRange;
	private double upperRange;
	
	private int[] outlierIndices;

	public int[] getOutlierIndices() {
		return outlierIndices;
	}

	public void setOutlierIndices(int[] outlierIndices) {
		this.outlierIndices = outlierIndices;
	}
	
	public int getOutlierCount() {
		return outlierIndices.length;
	}

	public double getLowerRange() {
		return lowerRange;
	}

	public void setLowerRange(double lowerRange) {
		this.lowerRange = lowerRange;
	}

	public double getUpperRange() {
		return upperRange;
	}

	public void setUpperRange(double upperRange) {
		this.upperRange = upperRange;
	}
}
