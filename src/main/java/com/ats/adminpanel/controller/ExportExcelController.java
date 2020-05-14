package com.ats.adminpanel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ats.adminpanel.model.ExportToExcel;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddress;
 
import com.ats.adminpanel.model.ExportToExcel;

 

@Controller
public class ExportExcelController {

	/*
	 * <dependency> <groupId>org.apache.poi</groupId>
	 * <artifactId>poi-ooxml</artifactId> <version>3.13</version> </dependency>
	 */

	List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();
	List<ExportToExcel> exportToExcelListDummy = new ArrayList<ExportToExcel>();
	List<ExportToExcel> exportToExcelListNew = new ArrayList<ExportToExcel>();
	List<ExportToExcel> exportToExcelList1 = new ArrayList<ExportToExcel>();
	List<ExportToExcel> exportToExcelList2 = new ArrayList<ExportToExcel>();
	List<ExportToExcel> exportToExcelTally = new ArrayList<ExportToExcel>();
	
	@RequestMapping(value = "/exportToExcel", method = RequestMethod.GET)
	@ResponseBody
	public void downloadSpreadsheet(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelList = (List) session.getAttribute("exportExcelList");
			System.out.println("Excel List :" + exportToExcelList.toString());

			String excelName = (String) session.getAttribute("excelName");
			wb = createWorkbook();
			autoSizeColumns(wb, 0);
			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportExcelList");
		System.out.println("Session List" + session.getAttribute("exportExcelList"));
	}

	@RequestMapping(value = "/exportToExcelDummy", method = RequestMethod.GET)
	@ResponseBody
	public void exportToExcelDummy(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelListDummy = (List) session.getAttribute("exportExcelListDummy");
			System.out.println("Excel List :" + exportToExcelList.toString());

			String excelName = (String) session.getAttribute("excelName");
			wb = createWorkbookDummy();
			autoSizeColumns(wb, 0);
			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportToExcelListDummy");
		System.out.println("Session List" + session.getAttribute("exportToExcelListDummy"));
	}

	private XSSFWorkbook createWorkbook() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("LIST");
		sheet.createFreezePane(0, 1);
		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */

		for (int rowIndex = 0; rowIndex < exportToExcelList.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex);
			row.setHeight((short) 700);
			for (int j = 0; j < exportToExcelList.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);
				  cell.setCellValue(exportToExcelList.get(rowIndex).getRowData().get(j));
				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelList.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		        		 XSSFCellStyle cellStyle = wb.createCellStyle();
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 
		                 // checking valid float using parseInt() method 
		                double value=Double.parseDouble(exportToExcelList.get(rowIndex).getRowData().get(j)); 
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelList.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
			}
			if (rowIndex == 0)
				row.setRowStyle(createHeaderStyle(wb));
		}
		return wb;
	}

	private XSSFWorkbook createWorkbookDummy() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("LIST");
		sheet.createFreezePane(0, 1);
		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */

		for (int rowIndex = 0; rowIndex < exportToExcelListDummy.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex);
			row.setHeight((short) 700);
			for (int j = 0; j < exportToExcelListDummy.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelListDummy.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		        		 XSSFCellStyle cellStyle = wb.createCellStyle();
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 
		                 // checking valid float using parseInt() method 
		                double value=Double.parseDouble(exportToExcelListDummy.get(rowIndex).getRowData().get(j)); 
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelListDummy.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
			}
			if (rowIndex == 0)
				row.setRowStyle(createHeaderStyle(wb));
		}
		return wb;
	}

	private XSSFWorkbook createWorkbook1() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");

		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */

		for (int rowIndex = 0; rowIndex < exportToExcelList1.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex);
			for (int j = 0; j < exportToExcelList1.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelList1.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		        		 XSSFCellStyle cellStyle = wb.createCellStyle();
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 
		                 // checking valid float using parseInt() method 
		                double value=Double.parseDouble(exportToExcelList1.get(rowIndex).getRowData().get(j)); 
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelList1.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
			}
			if (rowIndex == 0)
				row.setRowStyle(createHeaderStyle(wb));
		}
		return wb;
	}

	/*
	 * private void writeHeaders(XSSFWorkbook workbook, XSSFSheet sheet) { XSSFRow
	 * header = sheet.createRow(0); XSSFCell headerCell = header.createCell(0);
	 * headerCell.setCellValue("Cities to visit");
	 * headerCell.setCellStyle(createHeaderStyle(workbook));
	 * 
	 * }
	 */
	public void autoSizeColumns(Workbook workbook, int index) {
		int numberOfSheets = workbook.getNumberOfSheets();
		for (int i = 0; i < numberOfSheets; i++) {
			Sheet sheet = workbook.getSheetAt(i);
			if (sheet.getPhysicalNumberOfRows() > 0) {
				Row row = sheet.getRow(index);
				row.setHeight((short) 700);

				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					int columnIndex = cell.getColumnIndex();
					sheet.autoSizeColumn(columnIndex);
				}
			}
		}
	}

	private XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(247, 161, 103)));

		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setDataFormat(1);

		Font font = workbook.createFont();
		font.setFontName("Arial");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setBold(true);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		return style;
	}

	@RequestMapping(value = "/exportToExcelNew", method = RequestMethod.GET)
	@ResponseBody
	public void exportToExcelNew(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelListNew = (List) session.getAttribute("exportExcelListNew");
			System.out.println("Excel List :" + exportToExcelListNew.toString());

			String excelName = (String) session.getAttribute("excelNameNew");
			String reportName = (String) session.getAttribute("reportNameNew");
			String searchBy = (String) session.getAttribute("searchByNew");
			String mergeUpto1 = (String) session.getAttribute("mergeUpto1");
			String mergeUpto2 = (String) session.getAttribute("mergeUpto2");
			wb = createWorkbookNew(reportName, searchBy, mergeUpto1, mergeUpto2);
			autoSizeColumns(wb, 2);
			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportExcelListNew");
		System.out.println("Session List" + session.getAttribute("exportExcelListNew"));
	}
	
	
	@RequestMapping(value = "/exportToExcelNewFilter", method = RequestMethod.GET)
	@ResponseBody
	public void exportToExcelNewFilter(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelListNew = (List) session.getAttribute("exportExcelListNew");
			System.out.println("Excel List :" + exportToExcelListNew.toString());

			String excelName = (String) session.getAttribute("excelNameNew");
			String reportName = (String) session.getAttribute("reportNameNew");
			String searchBy = (String) session.getAttribute("searchByNew");
			String filter = (String) session.getAttribute("searchByFilter");
			String mergeUpto1 = (String) session.getAttribute("mergeUpto1");
			String mergeUpto2 = (String) session.getAttribute("mergeUpto2");
			String mergeUpto3 = (String) session.getAttribute("mergeUpto3");
			wb = createWorkbookNewWithFilter(reportName, searchBy, filter ,mergeUpto1, mergeUpto2,mergeUpto3);
			autoSizeColumns(wb, 3);
			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportExcelListNew");
		System.out.println("Session List" + session.getAttribute("exportExcelListNew"));
	}
	
	 
	private XSSFWorkbook createWorkbookNew(String reportName, String searchBy, String mergeUpto1, String mergeUpto2)
			throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");
		sheet.createFreezePane(0, 3);

		CellStyle style = wb.createCellStyle();
		// style.setFillForegroundColor(IndexedColors.PINK.getIndex());"$A$1:$L$1"
		// style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(25);
		titleRow.setRowStyle(style);
		Cell titleCell = titleRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		titleCell.setCellValue("" + reportName);
		titleCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto1));

		Row searchByRow = sheet.createRow(1);
		searchByRow.setHeightInPoints(25);
		searchByRow.setRowStyle(style);
		Cell searchByCell = searchByRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		searchByCell.setCellValue("" + searchBy);
		searchByCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		// titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto2));
		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */
		 XSSFCellStyle cellStyle = wb.createCellStyle();

		for (int rowIndex = 0; rowIndex < exportToExcelListNew.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex + 2);
			for (int j = 0; j < exportToExcelListNew.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelListNew.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		                 // checking valid float using parseInt() method
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 

		                double value=Double.parseDouble(exportToExcelListNew.get(rowIndex).getRowData().get(j)); 
		                
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		                
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelListNew.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
				

				// if((rowIndex+1)==1)
				// cell.setCellStyle(createHeaderStyleHeaderFont(wb,242, 242, 242,1));
				if ((rowIndex + 3) == 3)
					cell.setCellStyle(createHeaderStyleNew(wb));

			}
			// if((rowIndex+1)==1)
			// row.setRowStyle(createHeaderStyleNew(wb));
		}
		return wb;
	}
	
	
	private XSSFWorkbook createWorkbookNewWithFilter(String reportName, String searchBy, String searchByFilter ,String mergeUpto1, String mergeUpto2, String mergeUpto3)
			throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");
		sheet.createFreezePane(0, 4);

		CellStyle style = wb.createCellStyle();
		// style.setFillForegroundColor(IndexedColors.PINK.getIndex());"$A$1:$L$1"
		// style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(CellStyle.ALIGN_CENTER);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(25);
		titleRow.setRowStyle(style);
		Cell titleCell = titleRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		titleCell.setCellValue("" + reportName);
		titleCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto1));

		Row searchByRow = sheet.createRow(1);
		searchByRow.setHeightInPoints(25);
		searchByRow.setRowStyle(style);
		Cell searchByCell = searchByRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		searchByCell.setCellValue("Search By.." + searchBy);
		searchByCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		// titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto2));
		
		Row searchByFilterRow = sheet.createRow(2);
		searchByFilterRow.setHeightInPoints(25);
		searchByFilterRow.setRowStyle(style);
		Cell searchByFilterCell = searchByFilterRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		searchByFilterCell.setCellValue("Search By.." + searchByFilter);
		searchByFilterCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		// titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto3));
		
		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */
		 XSSFCellStyle cellStyle = wb.createCellStyle();

		for (int rowIndex = 0; rowIndex < exportToExcelListNew.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex + 2);
			for (int j = 0; j < exportToExcelListNew.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelListNew.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		                 // checking valid float using parseInt() method
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 

		                double value=Double.parseDouble(exportToExcelListNew.get(rowIndex).getRowData().get(j)); 
		                
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		                
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelListNew.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
				

				// if((rowIndex+1)==1)
				// cell.setCellStyle(createHeaderStyleHeaderFont(wb,242, 242, 242,1));
				if ((rowIndex + 2) == 2)
					cell.setCellStyle(createHeaderStyleNew(wb));

			}
			// if((rowIndex+1)==1)
			// row.setRowStyle(createHeaderStyleNew(wb));
		}
		return wb;
	}
	
	
	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}
	private XSSFCellStyle createHeaderStyleNew(XSSFWorkbook workbook) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(247, 161, 103)));

		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setDataFormat(1);

		Font font = workbook.createFont();
		font.setFontName("Times New Roman");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setBold(true);
		font.setColor(HSSFColor.WHITE.index);
		style.setFont(font);

		return style;
	}

	private XSSFCellStyle createHeaderStyleHeaderFont(XSSFWorkbook workbook, int r, int g, int b, int align) {
		XSSFCellStyle style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setFillForegroundColor(new XSSFColor(new java.awt.Color(r, g, b)));

		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		if (align == 0)
			style.setAlignment(CellStyle.ALIGN_CENTER);
		if (align == 1)
			style.setAlignment(CellStyle.ALIGN_LEFT);

		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setDataFormat(1);

		Font font = workbook.createFont();
		font.setFontName("Times New Roman");
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setBold(true);
		font.setColor(HSSFColor.BLACK.index);
		style.setFont(font);

		return style;
	}

	@RequestMapping(value = "/exportToExcel1", method = RequestMethod.GET)

	public void exportToExcel1(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelList1 = (List) session.getAttribute("exportExcelList1");
			System.out.println("Excel List :" + exportToExcelList1.toString());

			String excelName1 = (String) session.getAttribute("excelName");
			wb = createWorkbook1();

			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName1 + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportExcelList1");
		System.out.println("Session List" + session.getAttribute("exportExcelList1"));
	}

	@RequestMapping(value = "/exportToExcel2", method = RequestMethod.GET)

	public void exportToExcel2(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelList2 = (List) session.getAttribute("exportExcelList2");
			System.out.println("Excel List2 :" + exportToExcelList2.toString());

			String excelName2 = (String) session.getAttribute("excelName2");
			wb = createWorkbook2();

			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName2 + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportExcelList2");
		System.out.println("Session List" + session.getAttribute("exportExcelList2"));
	}

	private XSSFWorkbook createWorkbook2() throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");

		for (int rowIndex = 0; rowIndex < exportToExcelList2.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex);
			for (int j = 0; j < exportToExcelList2.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				
				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelList2.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		        		 XSSFCellStyle cellStyle = wb.createCellStyle();
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 

		                 // checking valid float using parseInt() method 
		                double value=Double.parseDouble(exportToExcelList2.get(rowIndex).getRowData().get(j)); 
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelList2.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
			}
			if (rowIndex == 0)
				row.setRowStyle(createHeaderStyle(wb));
		}
		return wb;
	}
	
	
	@RequestMapping(value = "/exportToExcelTally", method = RequestMethod.GET)
	@ResponseBody
	public void exportToExcelTally(HttpServletResponse response, HttpServletRequest request) throws Exception {
		XSSFWorkbook wb = null;
		HttpSession session = request.getSession();
		try {

			exportToExcelTally = (List) session.getAttribute("exportToExcelTally"); 

			String excelName = (String) session.getAttribute("excelNameNewTally");
			String reportName = (String) session.getAttribute("reportNameNewTally");
			String searchBy = (String) session.getAttribute("searchByNewTally");
			String mergeUpto1 = (String) session.getAttribute("mergeUpto1Tally");
			String mergeUpto2 = (String) session.getAttribute("mergeUpto2Tally");
			wb = createWorkbookNewTally(reportName, searchBy, mergeUpto1, mergeUpto2);
			autoSizeColumns(wb, 2);
			response.setContentType("application/vnd.ms-excel");
			String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			response.setHeader("Content-disposition", "attachment; filename=" + excelName + "-" + date + ".xlsx");
			wb.write(response.getOutputStream());

		} catch (IOException ioe) {
			throw new RuntimeException("Error writing spreadsheet to output stream");
		} finally {
			if (wb != null) {
				wb.close();
			}
		}
		session.removeAttribute("exportToExcelTally");
		System.out.println("Session List" + session.getAttribute("exportToExcelTally"));
	}
	 
	private XSSFWorkbook createWorkbookNewTally(String reportName, String searchBy, String mergeUpto1, String mergeUpto2)
			throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");
		sheet.createFreezePane(0, 3);

		CellStyle style = wb.createCellStyle(); 
		style.setAlignment(CellStyle.ALIGN_CENTER);

		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(25);
		titleRow.setRowStyle(style);
		Cell titleCell = titleRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		titleCell.setCellValue("" + reportName);
		titleCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto1));

		Row searchByRow = sheet.createRow(1);
		searchByRow.setHeightInPoints(25);
		searchByRow.setRowStyle(style);
		Cell searchByCell = searchByRow.createCell(0);

		// titleCell.setAlignment(CellStyle.ALIGN_CENTER);

		searchByCell.setCellValue("Search By.." + searchBy);
		searchByCell.setCellStyle(createHeaderStyleHeaderFont(wb, 255, 243, 235, 0));
		// titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergeUpto2));
		/*
		 * writeHeaders(wb, sheet); writeHeaders(wb, sheet); writeHeaders(wb, sheet);
		 */
		 XSSFCellStyle cellStyle = wb.createCellStyle();

		for (int rowIndex = 0; rowIndex < exportToExcelTally.size(); rowIndex++) {
			XSSFRow row = sheet.createRow(rowIndex + 2);
			for (int j = 0; j < exportToExcelTally.get(rowIndex).getRowData().size(); j++) {

				XSSFCell cell = row.createCell(j);

				try 
		        { 
		            // checking valid integer using parseInt() method 
		           int value=Integer.parseInt(exportToExcelTally.get(rowIndex).getRowData().get(j)); 
		            cell.setCellValue(value);
		        }  
		        catch (NumberFormatException e)  
		        { 
		        	 try
		             { 
		                 // checking valid float using parseInt() method
		        		 XSSFDataFormat xssfDataFormat = wb.createDataFormat(); 

		                double value=Double.parseDouble(exportToExcelTally.get(rowIndex).getRowData().get(j)); 
		                
		                cellStyle.setDataFormat(xssfDataFormat.getFormat("#,##0.00"));
		                cell.setCellStyle(cellStyle);
		                cell.setCellValue(value);
		                
		             }  
		             catch (NumberFormatException e1) 
		             { 
		            	 cell.setCellValue(exportToExcelTally.get(rowIndex).getRowData().get(j));
		             } 
		               
		        } 
				

				// if((rowIndex+1)==1)
				// cell.setCellStyle(createHeaderStyleHeaderFont(wb,242, 242, 242,1));
				if ((rowIndex + 2) == 2)
					cell.setCellStyle(createHeaderStyleNew(wb));

			}
			// if((rowIndex+1)==1)
			// row.setRowStyle(createHeaderStyleNew(wb));
		}
		return wb;
	}
}
