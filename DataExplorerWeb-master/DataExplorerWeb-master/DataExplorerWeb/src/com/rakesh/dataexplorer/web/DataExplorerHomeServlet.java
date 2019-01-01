package com.rakesh.dataexplorer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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

/**
 * Servlet implementation class DataExplorerHomeServlet
 */
@WebServlet("/DataExplorerHomeServlet")
public class DataExplorerHomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private boolean isMultipart;
	   private String filePath;
	   private int maxFileSize = 3000000 * 1024;
	   private int maxMemSize = 40000 * 1024;
	   private File file ;

    /**
     * Default constructor. 
     */
    public DataExplorerHomeServlet() {
        // TODO Auto-generated constructor stub
    }
    
    public void init(){
    	filePath = getServletContext().getInitParameter("file-upload");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().append("Served at: ").append(request.getContextPath());
        	PrintWriter out = response.getWriter();
           
            
            // Check that we have a file upload request
            isMultipart = ServletFileUpload.isMultipartContent(request);
            response.setContentType("text/html");
         
            if( !isMultipart ) {
               out.println("<html>");
               out.println("<head>");
               out.println("<title>Servlet upload</title>");  
               out.println("</head>");
               out.println("<body>");
               out.println("<p>No file uploaded</p>"); 
               out.println("</body>");
               out.println("</html>");
               return;
            }
        
            DiskFileItemFactory factory = new DiskFileItemFactory();
         
            // maximum size that will be stored in memory
            factory.setSizeThreshold(maxMemSize);
         
            // Location to save data that is larger than maxMemSize.
            factory.setRepository(new File("c:\\temp"));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
         
            // maximum file size to be uploaded.
            upload.setSizeMax( maxFileSize );
            FileInputStream fis = null;

            try { 
               // Parse the request to get file items.
               List fileItems = upload.parseRequest(request);
      	
               // Process the uploaded file items
               Iterator i = fileItems.iterator();

               out.println("<html>");
               out.println("<head>");
               out.println("<title>Servlet upload</title>");  
               out.println("</head>");
               out.println("<body>");
         
               while ( i.hasNext () ) {
                  FileItem fi = (FileItem)i.next();
                  if ( !fi.isFormField () ) {
                     // Get the uploaded file parameters
                     String fieldName = fi.getFieldName();
                     String fileName = fi.getName();
                     String contentType = fi.getContentType();
                     boolean isInMemory = fi.isInMemory();
                     long sizeInBytes = fi.getSize();
                  
                     // Write the file
                     if( fileName.lastIndexOf("\\") >= 0 ) {
                        file = new File( filePath + fileName.substring( fileName.lastIndexOf("\\"))) ;
                     } else {
                        file = new File( filePath + fileName.substring(fileName.lastIndexOf("\\")+1)) ;
                     }
                     fi.write( file ) ;
                     
                     out.println("Uploaded Filename: " + fileName + "<br>");
                     fis = new FileInputStream(file);
                     List<ColumnData> colData = CSVReader.readDataUsingScanner(fis);
                     
                     List<Column> rawColumns = new ArrayList<Column>();
         			for(ColumnData col : colData){
         				System.out.println(col.getName());
         				out.println("<h3>Column Name : "+col.getName()+"</h3><br>");
         				List<String> data = col.getData();
         				Datatype type = DatatypeParser.guessDatatype(data);
         				Column rawCol = DatatypeParser.createColumn(col, type);
         				rawColumns.add(rawCol);
         				if(type == Datatype.NUMERIC){
         					out.println("Column type : Numeric <br>");
         					NumericColumn newColumn = (NumericColumn)rawCol;
         					double[] numData = newColumn.getData();
         					Outlier outlier = StatisticUtils.getOutliers(numData);
         					double skewness = StatisticUtils.getSkewness(numData);
         					out.println("Outlier Data : <br>");
         					System.out.println("Lower range: "+outlier.getLowerRange());
         					out.println("Lower Range : "+outlier.getLowerRange() + "\n" +" Upper Range : "+outlier.getUpperRange()+" \n "+" Outlier Count : "+outlier.getOutlierCount()+"<br>");
         					System.out.println("Upper range: "+outlier.getUpperRange());
         					System.out.println("Outlier count: "+outlier.getOutlierCount());
         					System.out.println("Skewness: "+skewness);
         					out.println("Skewness value : "+skewness +"<br>");
         					if(skewness<0){
         						out.println("Data is negatively skewed for this field <br>");
         					} else {
         						out.println("Data is positively skewed for this field <br>");
         					}
         					
         					double score = StatisticUtils.getColumnQualityScoreForNumerics(rawCol, outlier.getOutlierIndices(), skewness);
         					System.out.println("Quality score of the column is : "+score);
         					out.println("<h2> Predictive power of the field is : "+score+" </h2> <br>");
         					System.out.println("--------------------------------------------------------------");
         					out.println("---------------------------------------------------------------------- <br>");
         				} else if(type == Datatype.STRING){
         					out.println("Column type : Symbolic <br>");
         					SymbolicColumn newColumn = (SymbolicColumn)rawCol;
         					List<Partition> partitions = StatisticUtils.discretize(newColumn.getData());
         					int[] partitionsLength = new int[partitions.size()];
         					for(int n=0;n<partitions.size();n++){
         						partitionsLength[n] = partitions.get(n).getCount();
         					}
         					boolean isImbalanced = StatisticUtils.isImbalanced(partitionsLength);
         					System.out.println("Is imbalanced ? "+isImbalanced);
         					out.println("Is imbalanced ? : "+isImbalanced+" <br>");
         					
         					double score = StatisticUtils.getColumnQualityScoreForSymbolics(rawCol, partitions, isImbalanced);
         					System.out.println("Quality score of the column is : "+score);
         					out.println("<h2> Predictive power of the field is : "+score+" </h2> <br>");
         					System.out.println("---------------------------------------------------------------");
         					out.println("---------------------------------------------------------------------- <br>");
         				}
         			}
         			
         			double[][] matrix = StatisticUtils.getPearsonCorrealtionMatrix(rawColumns);
         			
         			StatisticUtils.printArray(matrix);
                  }
               }
               out.println("</body>");
               out.println("</html>");
               } catch(Exception ex) {
            	   ex.printStackTrace();
                  System.out.println(ex);
               }
            
            
         finally {            
            out.close();
            if(fis!=null){
            	fis.close();
            }
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
