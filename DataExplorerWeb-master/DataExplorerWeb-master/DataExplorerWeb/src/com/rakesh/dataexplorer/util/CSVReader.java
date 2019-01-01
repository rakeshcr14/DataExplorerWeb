package com.rakesh.dataexplorer.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.rakesh.dataexplorer.model.ColumnData;

public class CSVReader {
	
	public static List<ColumnData> readDataUsingScanner(InputStream instream) {
		List<ColumnData> columns = new ArrayList<ColumnData>(); 
		
		Scanner fileScanner = new Scanner(instream);
		
		try {
			if(fileScanner.hasNextLine()) {
				String headerLine = fileScanner.nextLine(); 
				Scanner headerScanner = new Scanner(headerLine);
				
				try {
					headerScanner.useDelimiter(",");
					
					while (headerScanner.hasNext()) {
					    String token = headerScanner.next();
					    columns.add(new ColumnData(token));
					}
				} finally {
					headerScanner.close();
				}
			}
			
			while(fileScanner.hasNextLine()) {
				String dataLine = fileScanner.nextLine();
				Scanner dataScanner = new Scanner(dataLine);
				
				try {
					dataScanner.useDelimiter(",");
					
					int i = 0;
					while (dataScanner.hasNext()) {
					    String token = dataScanner.next();
					    columns.get(i).addData(token);
					    i++;
					}
				} finally {
					dataScanner.close();
				}
			}
		} finally {
			fileScanner.close();
		}
		
		return columns;
	}
	
	public static List<ColumnData> readDataUsingBuffer(InputStream instream) throws Exception {
		List<ColumnData> columns = new ArrayList<ColumnData>();
		
		BufferedReader fileReader = null;
		
		try {
			fileReader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
			
			String headerLine = fileReader.readLine();
			String[] headerTokens = headerLine.split(",");
			for (String token : headerTokens) {
				columns.add(new ColumnData(token));
			}
			
			String dataLine = null;
			
			while ((dataLine = fileReader.readLine()) != null) {
                String[] tokens = dataLine.split(",");
               
                int i = 0;
                for(String token : tokens) {
                    columns.get(i).addData(token);
                    i++;
                }
            }
		} catch (Exception e) {
			throw new Exception("Error while reading data", e);
		} finally {
			instream.close();
			fileReader.close();
		}
        
        return columns;
	}

}
