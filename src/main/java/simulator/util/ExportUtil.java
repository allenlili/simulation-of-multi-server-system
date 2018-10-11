package simulator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportUtil {
	Map<String, CellStyle> styles;
	private String outputFile;
    Sheet sheet = null;
    Workbook workbook = null; 
    
    public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
    
	public ExportUtil(){
		
	}

    public void createWorkBook(){
		FileOutputStream out = null;
	    try {
	    	workbook = new XSSFWorkbook();
	    	styles = createStyles(workbook);
	        out = new FileOutputStream(outputFile);
	        workbook.write(out);
	        out.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    public void createSheet(String sheetName){
		FileOutputStream out = null;
	    try {
	    	sheet = workbook.createSheet(sheetName);
	        out = new FileOutputStream(outputFile);
	        workbook.write(out);
	        out.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
	public void createTitle(String sheetName, int firstRow, int lastRow, int firstColumn, int lastColumn, String title){
		FileOutputStream out = null;
	    try {
	    	Sheet sheet = workbook.getSheet(sheetName);
			Row row = sheet.createRow(firstRow);
			Cell titleCell = row.createCell(firstColumn);
			titleCell.setCellValue(title);
        	titleCell.setCellStyle(styles.get("title"));
            sheet.addMergedRegion(new CellRangeAddress(
            		firstRow,    //first row (0-based)
            		lastRow,     //last row  (0-based)
            		firstColumn, //first column (0-based)
            		lastColumn   //last column  (0-based)
            ));
	        out = new FileOutputStream(outputFile);
	        workbook.write(out);
	        out.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	    	try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createBody(List<String[]> results, int xMax, int yMax) {
		String[][] newStrArray = results.toArray(new String[][]{});
		Row row = sheet.createRow(1);
		Cell cell;
    	for (int i = 0; i < yMax; i++) {
    		cell = row.createCell(i+1);
    		cell.setCellValue("r"+(i+1));
    		cell.setCellStyle(styles.get("cell"));
		}
	    try {
	    	for (int i = 0; i < newStrArray.length; i++) {
				String[] strings = newStrArray[i];
				row = sheet.createRow(i+2);
				for(int j = 0; j < newStrArray[i].length + 1; j++){
					if (j == 0) {
						cell = row.createCell(0);
						cell.setCellValue("n="+(i+1));
						cell.setCellStyle(styles.get("cell"));
					}else{
						row.createCell(j).setCellValue(Double.valueOf(strings[j-1]));
					}
				}
			}
	        FileOutputStream out;       
	        out = new FileOutputStream(outputFile);
	        workbook.write(out);
	        out.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public void createBody(List<String[]> results, String[] xArg, String[] yArg) {
		String[][] newStrArray = results.toArray(new String[][]{});
		Row row = sheet.createRow(1);
		Cell cell;
    	for (int i = 0; i < yArg.length; i++) {
    		cell = row.createCell(i+1);
    		cell.setCellValue(yArg[i]);
    		cell.setCellStyle(styles.get("cell"));
		}
	    try {
	    	for (int i = 0; i < newStrArray.length; i++) {
				String[] strings = newStrArray[i];
				row = sheet.createRow(i+2);
				for(int j = 0; j < newStrArray[i].length + 1; j++){
					if (j == 0) {
						cell = row.createCell(0);
						cell.setCellValue(xArg[i]);
						cell.setCellStyle(styles.get("cell"));
					}else{
						row.createCell(j).setCellValue(Double.valueOf(strings[j-1]));
					}
				}
			}
	        FileOutputStream out;       
	        out = new FileOutputStream(outputFile);
	        workbook.write(out);
	        out.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    private static Map<String, CellStyle> createStyles(Workbook wb){
         Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
         CellStyle style;
         Font titleFont = wb.createFont();
         titleFont.setFontHeightInPoints((short)20);
         titleFont.setColor(IndexedColors.DARK_BLUE.getIndex());
         style = wb.createCellStyle();
         style.setAlignment(CellStyle.ALIGN_CENTER);
         style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
         style.setFont(titleFont);
         styles.put("title", style);
         
         style = wb.createCellStyle();
         style.setAlignment(CellStyle.ALIGN_LEFT);
         style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
         styles.put("cell", style);
         return styles;
    }
    
    public void createFolder(String filePath, String folderName){
    	File folder = new File(filePath+folderName);
		if (!folder.exists()) {
			folder.mkdir();
		}
    }
    
	public void export(List<List<String[]>> results, String filePath, int numDataPoints) throws Exception{
		List<String[]> replications = null;
		String[] dataPoints = null;
		for (int i = 0; i < results.size(); i++) {
			replications = results.get(i);
			createFolder(filePath, "trace n="+(i+1));
			for (int j = 0; j < replications.size(); j++) {
				dataPoints = replications.get(j);
				File file = new File(filePath+"trace n="+(i+1)+File.separator+"trace"+(j+1));
				file.createNewFile();
				PrintWriter out = new PrintWriter(file);
				for (int k = 0; k < dataPoints.length; k++) {
					String dataPoint = dataPoints[k];
					if (k < numDataPoints) {
						out.println(dataPoint);
					}else{
						break;
					}
					out.flush();
				}
				out.close();
			}
		}
	}

	
}
