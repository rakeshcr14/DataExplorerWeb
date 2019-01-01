package com.rakesh.dataexplorer.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.rakesh.dataexplorer.model.Column;
import com.rakesh.dataexplorer.model.ColumnData;
import com.rakesh.dataexplorer.model.Datatype;
import com.rakesh.dataexplorer.model.NumericColumn;
import com.rakesh.dataexplorer.model.Outlier;
import com.rakesh.dataexplorer.model.Partition;
import com.rakesh.dataexplorer.model.SymbolicColumn;
import com.rakesh.dataexplorer.util.CSVReader;
import com.rakesh.dataexplorer.util.DatatypeParser;
import com.rakesh.dataexplorer.util.StatisticUtils;

public class TestStatistics {

	
	public static void main(String[] args) {
		
		try{
			FileInputStream fis = new FileInputStream(new File("E:\\Chrome Downloads\\Final year project & seminar\\sample datasets\\New folder\\iris3.csv"));
			List<ColumnData> colData = CSVReader.readDataUsingScanner(fis);
			
			List<Column> rawColumns = new ArrayList<Column>();
			for(ColumnData col : colData){
				System.out.println(col.getName());
				List<String> data = col.getData();
				Datatype type = DatatypeParser.guessDatatype(data);
				Column rawCol = DatatypeParser.createColumn(col, type);
				rawColumns.add(rawCol);
				if(type == Datatype.NUMERIC){
					NumericColumn newColumn = (NumericColumn)rawCol;
					double[] numData = newColumn.getData();
					Outlier out = StatisticUtils.getOutliers(numData);
					double skewness = StatisticUtils.getSkewness(numData);
					System.out.println("Lower range: "+out.getLowerRange());
					System.out.println("Upper range: "+out.getUpperRange());
					System.out.println("Outlier count: "+out.getOutlierCount());
					System.out.println("Skewness: "+skewness);
					
					double score = StatisticUtils.getColumnQualityScoreForNumerics(rawCol, out.getOutlierIndices(), skewness);
					System.out.println("Quality score of the column is : "+score);
					System.out.println("--------------------------------------------------------------");
				} else if(type == Datatype.STRING){
					SymbolicColumn newColumn = (SymbolicColumn)rawCol;
					List<Partition> partitions = StatisticUtils.discretize(newColumn.getData());
					int[] partitionsLength = new int[partitions.size()];
					for(int i=0;i<partitions.size();i++){
						partitionsLength[i] = partitions.get(i).getCount();
					}
					boolean isImbalanced = StatisticUtils.isImbalanced(partitionsLength);
					System.out.println("Is imbalanced ? "+isImbalanced);
					
					double score = StatisticUtils.getColumnQualityScoreForSymbolics(rawCol, partitions, isImbalanced);
					System.out.println("Quality score of the column is : "+score);
					System.out.println("---------------------------------------------------------------");
				}
			}
			
			double[][] matrix = StatisticUtils.getPearsonCorrealtionMatrix(rawColumns);
			
			StatisticUtils.printArray(matrix);
	
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
}
