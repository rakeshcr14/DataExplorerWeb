package com.rakesh.dataexplorer.model;

public class SymPartition extends Partition {
	
	private String category;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public int compareTo(Object o) {
		SymPartition partition = (SymPartition) o;
		return this.category.compareTo(partition.getCategory());
	}
}
