package com.rakesh.dataexplorer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.rakesh.dataexplorer.model.Column;
import com.rakesh.dataexplorer.model.ColumnData;
import com.rakesh.dataexplorer.model.Datatype;
import com.rakesh.dataexplorer.model.NumericColumn;
import com.rakesh.dataexplorer.model.SymbolicColumn;

public class DatatypeParser {
	
	private static Pattern integerPattern = Pattern.compile("-?[0-9]*"); 
	private static Pattern doublePattern = Pattern.compile("(-?)([0-9]*)(\\.[0-9]+)"); 
	
//	Pattern.matches("^[\\+\\-]{0,1}[0-9]+[\\.\\,]{1}[0-9]+$" // scientific numbers and commas
	
	public static Datatype getDatatype(String value) {
		Datatype datatype = Datatype.STRING;

		if (integerPattern.matcher(value).matches() || doublePattern.matcher(value).matches()) {
		   datatype = Datatype.NUMERIC;
		} 
		
		return datatype;
	}
	
	
	
	public static Datatype guessDatatype(List<String> sample) {
		Datatype columnDataType = Datatype.NUMERIC;
		
		for(String value : sample) {
			Datatype datatype = DatatypeParser.getDatatype(value);
			
			if(Datatype.STRING.equals(datatype)) {
				columnDataType = Datatype.STRING;
				break;
			}
		}
		
		return columnDataType;
	}
	
	
	public static Column createColumn(ColumnData columnData, Datatype datatype) {
		double[] numericData = null;
		String[] symbolicData = null;
		
		List<String> data = columnData.getData();
		
		Column column = null;
		
		switch(datatype) {
			case NUMERIC:
				column = new NumericColumn();
				numericData = new double[data.size()];
				((NumericColumn) column).setData(numericData);
				break;
			default:
				column = new SymbolicColumn();
				symbolicData = new String[data.size()];
				((SymbolicColumn) column).setData(symbolicData);
				break;
		}
		
		List<Integer> missingValueIndices = new ArrayList<Integer>();
		
		int index = 0;
		for (int i=0; i<data.size(); i++) {
			String point = data.get(i);
			boolean missingValue = StatisticUtils.isMissingValue(point);
			if(missingValue) {
				missingValueIndices.add(i);
			}
			
			switch(datatype) {
				case NUMERIC:
					double doubleValue = Double.POSITIVE_INFINITY;
					
					if(!missingValue) {
						try {
							doubleValue = Double.parseDouble(point);
						} catch (Exception e) {
							doubleValue = Double.NaN; // for the outside case of a non-numeric in a numeric column
						}	
					}
					
					numericData[index] = doubleValue;
					break;
				default:
					symbolicData[index] = point;
					break;
			}
			
			index++;
		}
		
		column.setId(columnData.getName());
		column.setName(columnData.getName());
		column.setDatatype(datatype);
		
		if(missingValueIndices.size() > 0) {
			int[] missingValueIndicesForColumn = new int[missingValueIndices.size()];
			for(int i=0; i<missingValueIndices.size(); i++) {
				missingValueIndicesForColumn[i] = missingValueIndices.get(i);
			}
			
			column.setMissingValueIndices(missingValueIndicesForColumn);
		}
		
		
		return column;
	}
}
