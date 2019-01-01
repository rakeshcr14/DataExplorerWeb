package com.rakesh.dataexplorer.model;

public class NumPartition extends Partition {

	private double upperLimit;
	private double lowerLimit;

	public double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@Override
	public int compareTo(Object o) {
		NumPartition partition = (NumPartition) o;
		return Double.compare(lowerLimit, partition.getLowerLimit());
	}
}
