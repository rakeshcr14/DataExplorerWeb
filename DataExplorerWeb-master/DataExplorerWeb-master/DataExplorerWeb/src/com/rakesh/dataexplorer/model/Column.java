package com.rakesh.dataexplorer.model;

public abstract class Column {
	
	private String id;
	private String name;
	private Datatype datatype;
	private Optype optype;
	
	private int[] missingValueIndices;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Optype getOptype() {
		return optype;
	}

	public void setOptype(Optype optype) {
		this.optype = optype;
	}
	
	public int[] getMissingValueIndices() {
		return missingValueIndices;
	}

	public void setMissingValueIndices(int[] missingValueIndices) {
		this.missingValueIndices = missingValueIndices;
	}

	public abstract int getCount();
	
	public int getMissingValueCount() {
		if(missingValueIndices == null)
			return 0;
		else		
			return missingValueIndices.length;
	}
	
	@Override
	public boolean equals(Object object) {
		if((object != null) && (object instanceof Column)) {
			Column column = (Column) object;
			return this.id.equals(column.getId());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}
}