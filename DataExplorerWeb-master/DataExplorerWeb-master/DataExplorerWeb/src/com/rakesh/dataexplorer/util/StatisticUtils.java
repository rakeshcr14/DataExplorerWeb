package com.rakesh.dataexplorer.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.rakesh.dataexplorer.model.Column;
import com.rakesh.dataexplorer.model.NumericColumn;
import com.rakesh.dataexplorer.model.Outlier;
import com.rakesh.dataexplorer.model.Partition;
import com.rakesh.dataexplorer.model.SymPartition;
import com.rakesh.dataexplorer.model.SymbolicColumn;

public class StatisticUtils {
	
	
	/**
	 * IQR method to calculate the outliers for numeric fields
	 * 
	 * 
	 * 
	 * @param data
	 */
	public static Outlier getOutliers(double[] data){
		
		DescriptiveStatistics statistics = new DescriptiveStatistics(data);
		
		double q1 = statistics.getPercentile(25);
		double q3 = statistics.getPercentile(75);

		double iqr = q3 - q1;
		double lowerRange = q1 - 1.5 * iqr;
		double upperRange = q3 + 1.5 * iqr;

		/*
		 * Outliers are found using IQR method
		 * In this we find the first quartile(25th percentile) and third quartile(75th percentile)
		 * The InterQuatrileRange is calculated as iqr = q3-q1 then find the lower range and upper range of the data as 
		 * 		lowerRange = q1-1.5*iqr
		 * 		upperRange = q3+1.5*iqr
		 */
		List<Integer> outlierIndicesList = new ArrayList<Integer>();
		for (int index=0; index<data.length; index++) {
			double d = data[index];
			
			if(!isMissingValue(d)) {
				if (d < lowerRange || d > upperRange) {
					outlierIndicesList.add(index);
				}
			}
		}
		
		int[] outlierIndices = new int[outlierIndicesList.size()];
		for(int i=0; i<outlierIndicesList.size(); i++) {
			Integer outlierIndex = outlierIndicesList.get(i);
			outlierIndices[i] = outlierIndex.intValue();
		}

		Outlier out = new Outlier();
		out.setOutlierIndices(outlierIndices);
		out.setLowerRange(lowerRange);
		out.setUpperRange(upperRange);

		return out;
	}
	
	
	public static boolean isMissingValue(String point) {
		boolean missing = false;
		
		if(point == null || point.isEmpty()) {
			missing = true;
		}
		
		return missing;
	}
	
	public static boolean isMissingValue(double point) {
		boolean missing = false;
		
		if(point == Double.NaN || point == Double.POSITIVE_INFINITY) {
			missing = true;
		}
		
		return missing;
	}

	
	/**
	 * Method to calculate skewness for numeric fields
	 * 
	 * @param data
	 * @return
	 */
	public static double getSkewness(double[] data){
		
		int startIndex=0;
		int length = 0;
		if(data !=null){
			length = data.length;
		}
		
		double skew = Double.NaN;
		
		if(data.length>0){
			double sampleSize = length;
			double sum = 0.0;
			for (int i = startIndex; i < startIndex + length; i++) {
                sum += data[i];
            }
			
			double xbar = sum/sampleSize;
			
			double correction = 0;
            for (int i = startIndex; i < startIndex + length; i++) {
                correction += data[i] - xbar;
            }
            
            double mean = xbar + (correction/sampleSize);
            
            
            double accum = 0.0;
            double accum2 = 0.0;
            for (int i = startIndex; i < startIndex + length; i++) {
                final double d = data[i] - mean;
                accum  += d * d;
                accum2 += d;
            }
            double variance = (accum - (accum2 * accum2 / length)) / (length - 1);

            double accum3 = 0.0;
            for (int i = startIndex; i < startIndex + length; i++) {
                final double d = data[i] - mean;
                accum3 += d * d * d;
            }
            accum3 /= variance * Math.sqrt(variance);

            double n0 = length;

            skew = (n0 / ((n0 - 1) * (n0 - 2))) * accum3;
		}
		
		return skew;
	}
	
	
    public static double getSumOfAllElements(List<Double> population){
        double retValue = 0;
        
        if( population == null || population.size() == 0 ){
            return retValue;
        }
        
        for (int i = 0; i < population.size(); i++) {
            retValue+=population.get(i);
        }
        return retValue;
    }
    
    
    public static List<Double> multipyLists(List<Double> arr1, List<Double> arr2){
        List<Double> retValue = new ArrayList<Double>();

        if( arr1 == null && arr2== null ){
            return retValue;
        }
        
        for (int i = 0; i < arr1.size() ; i++) {
            retValue.add( arr1.get(i)*arr2.get(i));
        }
        if( arr2.size() > arr1.size() ){
            for (int i = arr1.size(); i < arr2.size() ; i++) {
                retValue.add( arr2.get(i));
            }
        }
        return retValue;
    }
    
    public static List<Double> squareList(List<Double> arr1){
        List<Double> retValue = new ArrayList<Double>();

        if( arr1 == null ){
            return retValue;
        }
        for (int i = 0; i < arr1.size(); i++) {
            retValue.add(arr1.get(i)*arr1.get(i));
        }
        return retValue;
    }
    
    
	public static double getCorrealtion(double[] x, double[] y){
	    List<Double> X = new ArrayList<Double>();
	    List<Double> Y = new ArrayList<Double>();

        for (int i = 0; i < x.length; i++) {
            X.add(new Double(x[i]));
        }

        for (int i = 0; i < y.length; i++) {
            Y.add(new Double(y[i]));
        }
        
        return getCorrealtion(X, Y);
	}
	
    /**
     * implementation of PearsonCorrlation
     * 
     * implementation is based on the below link
     * http://onlinestatbook.com/2/describing_bivariate_data/calculation.html
     */
    public static double getCorrealtion(List<Double> X, List<Double> Y){
        // calculate the mean of X & Y
        double meanX = StatisticUtils.getMean(X);
        double meanY = StatisticUtils.getMean(Y);
        
        // calculate x & y by subtracting each element from the meanX and meanY
        List<Double> x = new ArrayList<Double>(X.size());
        List<Double> y = new ArrayList<Double>(Y.size());
        
        for (Double double1 : X) { x.add(double1 - meanX); }
        for (Double double1 : Y) { y.add(double1 - meanY); }
        
        // calculation Product xy
        List<Double> xy = multipyLists(x, y);

        // calculate sumpr = sum of xy
        double sumpr = getSumOfAllElements(xy);

        // calculate sumsq1 = sum of square of all the elments of x
        double sumsq1 = getSumOfAllElements(squareList(x));
        
        // calculate sumsq2 = sum of square of all the elments of y
        double sumsq2 = getSumOfAllElements(squareList(y));
        
        double pearsonCorrelation = sumpr/(Math.sqrt(sumsq1*sumsq2));
        
        return pearsonCorrelation;
    }
    
	public static double getMean(List<Double> population) {
		Double sum = 0.0;
		long count = population.size();
		
		if(count == 0) {
			return Double.POSITIVE_INFINITY;
		}
		
		for(Double value : population) {
			sum += value;
		}
		
		return  sum/count;
	}
	
	
	public static List<String> getAllColumnNames(List<Column> columns){
	    List<String> columnNames = new ArrayList<String>();
	    
	    if(columns == null){
	        return null;
	    }
	    for(Column column : columns) {
	        columnNames.add(column.getName());
	    }
	    return columnNames;
	}
	
	public static Column getColumnById(List<Column> columns, String id) {
		Column selectedColumn = null;
		
		if(id == null || id.isEmpty() || columns == null || columns.isEmpty()) {
			return null;
		}
		
		for(Column column : columns) {
			if(id.equals(column.getId())) {
				selectedColumn = column;
				break;
			}
		}
		
		return selectedColumn;
	}
	
	
    public static double[][] getPearsonCorrealtionMatrix(List<Column> rawColumns) {
        List<String> headers = getAllColumnNames(rawColumns);

        int size = headers.size();

        double[][] corr = new double[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //TODO NumericColumn for now
                Column col1 = getColumnById(rawColumns, headers.get(i));
                Column col2 = getColumnById(rawColumns, headers.get(j));

                if( col1 instanceof NumericColumn && col2 instanceof NumericColumn){
                    
                    NumericColumn c1 = (NumericColumn)col1;
                    NumericColumn c2 = (NumericColumn)col2;
                    
                    
                    double[] data1 = c1.getData();
                    double[] data2 = c2.getData();
                    
                    double value = new PearsonsCorrelation().correlation(data1, data2);
                    if (value == Double.NaN) {
                        value = 0;
                    }
                    corr[i][j] = value;
                }else{
                    //TODO re-check for symbolic
                    System.err.println("Unsupported");
                }
                
            }
        }
        
        return corr;
    }
    
    
	/**
	 * Method to check if the field is imbalanced for symbolic fields
	 * 
	 * @param categoriesCount
	 * @return
	 */
	public static boolean isImbalanced(int[] categoriesCount){
		//Number of categories present in the data
		int noOfcategories = categoriesCount.length;
		int[] expected = new int[categoriesCount.length];
		
		int total = 0;
		double effectSize = 0;
		
		//Get the total count of all categories
		for(int i:categoriesCount){
			total += i;
		}
		
		//here we calculate the expected value per category
		//which is total count of all categories/ number of categories
		for(int i=0;i<expected.length;i++){
			expected[i] = total/noOfcategories;
		}
		
		//Calculate the chisquare and get the total effect size
		for(int i=0;i<categoriesCount.length;i++){
			/*
			 * Formula to calculate chisquare value is :( (observed-expected) the whole square )/expected
			 * 
			 * Using this chisquare value, get the total effect size as 
			 * 
			 * 
			 * Effect size = sqrt((totalcategories-1)*total count)
			 * 
			 */
			int diff = categoriesCount[i] - expected[i];
			double chiSquare = ((Math.pow(diff, 2))/expected[i]);
			
			double cVal = (noOfcategories-1)*total;
			double es = chiSquare/cVal;
			effectSize +=Math.sqrt(es);
		}
		
		if(effectSize >0.6d){
			return true;
		}
		
		return false;
	}
	
	
	public static List<Partition> discretize(String[] data) {
		List<Partition> partitions = new ArrayList<Partition>();
		
		Map<String, Integer> categoryFrequency = new HashMap<String, Integer>();
		
		for(String symbol : data) {
			if(categoryFrequency.containsKey(symbol)) {
				Integer frequency = categoryFrequency.get(symbol);
				categoryFrequency.put(symbol, frequency+1);
			} else {
				categoryFrequency.put(symbol, 1);
			}
		}
		
		Set<String> categories = categoryFrequency.keySet();
		for(String category : categories) {
			SymPartition partition = new SymPartition();
			partition.setCategory(category);
			partition.setCount(categoryFrequency.get(category));
			partition.setId(category);
			partition.setLabel(category);
			
			partitions.add(partition);
		}
		
		return partitions;
	}
	
	
	public static void printArray(double matrix[][]) {
	    for (double[] row : matrix) 
	        System.out.println(Arrays.toString(row));       
	}
	
	
	public static double getColumnQualityScoreForNumerics(Column column, int[] outlierIndices, double skewness) {
		double score=0.0d;
		int missingCount =0;
		double outlierPercent =0.0d;
		
		
		double threshold=column.getCount()/4;
		missingCount=column.getMissingValueCount();
		if(missingCount<=threshold){
			score=((column.getCount()-missingCount)/column.getCount())*100;
		}else{
			return score;
		}
		
		if(column instanceof NumericColumn) {
			NumericColumn numericCol=(NumericColumn) column;
			boolean hasDistinct=false;
			double[] data=numericCol.getData();
			
			double oldValue=data[0];
			for(double val:data){
				if(val != oldValue){
					hasDistinct=true;
					break;
				}
				oldValue = val;
			}
			
			if(!hasDistinct){
				return 0.0d;
			}
			int count=1;
			if(outlierIndices!=null){
				outlierPercent=100-((column.getCount()-(outlierIndices.length))/column.getCount()*100);
				count++;
			}else{
				outlierPercent=0.0d;
			}
			
			
			double skewnessQualityPercentage=100;
			if(skewness != 0){
				count++;
				if(skewness>0 && skewness<0.1){
					skewnessQualityPercentage=100;
				}else if(skewness>0.1 && skewness<1){
					skewnessQualityPercentage=100-skewness;
				}else  if(skewness>1 && skewness<100){
					skewnessQualityPercentage=Math.abs(skewness-100);
				}else{
					skewnessQualityPercentage=0;
					count--;
				}
			}
					
			
			score=(score+outlierPercent+skewnessQualityPercentage)/count;
		}

		
		return score;
	}
	
	
	public static double getColumnQualityScoreForSymbolics(Column column, List<Partition> part, boolean isImbalanced){
		
		double score = 0.0;
		if(column instanceof SymbolicColumn) {
			
			if(part != null){
				if(part.size()<=1){
					return 0.0;
				}
			}
			int count=2;
			double imbalanceQualityPercentage=100;
			if(isImbalanced) {
				imbalanceQualityPercentage=70;
			}
			
			score=(score+imbalanceQualityPercentage)/count;
			
		}
		
		return score;
	}
}
