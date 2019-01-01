package com.rakesh.dataexplorer.model;

public abstract class Partition implements Comparable {

	private String id;
	private String label;
	
	private int count;
	
	public Partition() {
		// do nothing
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void increment() {
		count++;
	}

	@Override
	public abstract int compareTo(Object o);
}
