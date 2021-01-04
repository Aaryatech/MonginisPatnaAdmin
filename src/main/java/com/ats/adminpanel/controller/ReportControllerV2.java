package com.ats.adminpanel.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.commons.DateConvertor;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.reportv2.SalesReport;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@Scope("session")
public class ReportControllerV2 {

	
	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public AllFrIdNameList allFrIdNameList = new AllFrIdNameList();

	@RequestMapping(value = "/getSalesReportV2", method = RequestMethod.GET)
	public ModelAndView getSalesReportV2(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/v2/fr_sales_report");

		try {

			RestTemplate restTemplate = new RestTemplate();

			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);

			allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

		
			model.addObject("todaysDate", todaysDate);
			model.addObject("allFrIdNameList", allFrIdNameList.getFrIdNamesList());

		} catch (Exception e) {
			System.out.println("Exce in showRegCakeSpOrderReport " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}
	
	
	List<SalesReport> saleReportList;
	String fromDatePdf = "";
	String toDatePdf = "";

	@RequestMapping(value = "/getSalesReport", method = RequestMethod.GET)
	public @ResponseBody List<SalesReport> callGetRegCakeAsSp(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {

		String frIdString = "";
		String fromDate = "";
		String toDate = "";

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

		RestTemplate restTemplate = new RestTemplate();

		System.out.println("inside getSalesReport ajax call");

		frIdString = request.getParameter("fr_id_list");
		fromDate = request.getParameter("fromDate");
		toDate = request.getParameter("toDate");
		
		fromDatePdf = request.getParameter("fromDate");
		toDatePdf = request.getParameter("toDate");
		
		try {

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();

			franchIds = Arrays.asList(frIdString);
			if (franchIds.contains("-1")) {
				map.add("frIdList", -1);

			} else {

				map.add("frIdList", frIdString);
			}
			System.err.println("frId string " + frIdString);
			map.add("fromDate", DateConvertor.convertToYMD(fromDate));

			map.add("toDate", DateConvertor.convertToYMD(toDate));

			SalesReport[] saleRepArray = restTemplate.postForObject(Constants.url + "getSalesReportV2", map,
					SalesReport[].class);
			saleReportList = new ArrayList<>(Arrays.asList(saleRepArray));

			System.err.println("saleReportList " + saleReportList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Party Code");
			rowData.add("Party Name");
			rowData.add("Sales");
			rowData.add("G.V.N");
			rowData.add("NET Value");
			rowData.add("G.R.N");
			rowData.add("NET Value");
			rowData.add("In Lakh");
			rowData.add("Return %");

			float salesValue = 0;
			float gvn = 0.0f;
			float gvnNetValue = 0.0f;
			float grn = 0.0f;
			float grnNetValue = 0.0f;
			float inLaskTotal = 0.0f;
			float returnTotal = 0.0f;

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < saleReportList.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));
				rowData.add("" + saleReportList.get(i).getFrCode());
				rowData.add("" + saleReportList.get(i).getFrName());
				rowData.add("" + roundUp(saleReportList.get(i).getSaleValue()));
				rowData.add("" + roundUp(saleReportList.get(i).getGvnValue()));
				float netVal1 = (saleReportList.get(i).getSaleValue()) - (saleReportList.get(i).getGvnValue());
				float netVal2 = (netVal1) - (saleReportList.get(i).getGrnValue());
				float inLac = (netVal2) / 100000;
				float retPer = 0.0f;
				if (saleReportList.get(i).getSaleValue() > 0) {
					retPer = ((saleReportList.get(i).getGrnValue()) / (saleReportList.get(i).getSaleValue() / 100));
				}
				rowData.add("" + roundUp(netVal1));
				rowData.add("" + roundUp(saleReportList.get(i).getGrnValue()));
				rowData.add("" + roundUp(netVal2));
				rowData.add("" + roundUp(inLac));
				rowData.add("" + roundUp(retPer));

				salesValue = salesValue + saleReportList.get(i).getSaleValue();
				gvn = gvn + saleReportList.get(i).getGvnValue();
				gvnNetValue = gvnNetValue + netVal1;
				grn = grn + saleReportList.get(i).getGrnValue();
				grnNetValue = grnNetValue + netVal2;
				inLaskTotal = inLaskTotal + inLac;
				
				//returnTotal = returnTotal + retPer;

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("");
			rowData.add("Total");
			rowData.add("");

			rowData.add("" + roundUp(salesValue));
			rowData.add("" + roundUp(gvn));
			rowData.add("" + roundUp(gvnNetValue));
			rowData.add("" + roundUp(grn));
			rowData.add("" + roundUp(grnNetValue));
			rowData.add("" + roundUp(inLaskTotal));
			
			returnTotal=(grn*100)/salesValue;
			rowData.add("" + roundUp(returnTotal));

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelListNew", exportToExcelList);
			session.setAttribute("excelNameNew", "SalesReport");
			session.setAttribute("reportNameNew", "Sales Report By Franchise");
			session.setAttribute("searchByNew", "From Date: " + fromDate + "  To Date: " + toDate + " ");
			session.setAttribute("mergeUpto1", "$A$1:$L$1");
			session.setAttribute("mergeUpto2", "$A$2:$L$2");

		} catch (Exception e) {

			e.printStackTrace();

		}
		return saleReportList;
	}

	// postProductionPlanDetaillist
	@RequestMapping(value = "/getSalesReportPdf", method = RequestMethod.GET)
	public void showProdByOrderPdf(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		

		String fromDate = "";
		String toDate = "";

		fromDate = request.getParameter("fromDate");
		toDate = request.getParameter("toDate");
		
		System.err.println("DATE - "+fromDate+"                    "+toDate);

		Document document = new Document(PageSize.A4);
		document.setPageSize(PageSize.A4.rotate());
		// ByteArrayOutputStream out = new ByteArrayOutputStream();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String timeStamp = dateFormat.format(cal.getTime());
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);

		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		PdfPTable table = new PdfPTable(9);
		try {
			table.setHeaderRows(1);
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 0.7f, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f });
			Font headFont = new Font(FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("Sr.No.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Party Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Sales", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("GVN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("NET Value", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("GRN", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("NET Value", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("IN Lacs", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Return%", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);
			float totalSaleValue = 0.0f;
			float totalGvnValue = 0.0f;
			float totalNetVal1 = 0.0f;
			float totalGrnValue = 0.0f;
			float totalNetVal2 = 0.0f;
			float totalInLac = 0.0f;
			float totalRetPer = 0.0f;
			int index = 0;
			for (int j = 0; j < saleReportList.size(); j++) {

				index++;
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				float netVal1 = (saleReportList.get(j).getSaleValue()) - (saleReportList.get(j).getGvnValue());
				float netVal2 = (netVal1) - (saleReportList.get(j).getGrnValue());
				float inLac = (netVal2) / 100000;
				float retPer = 0.0f;
				if (saleReportList.get(j).getSaleValue() > 0) {
					retPer = ((saleReportList.get(j).getGrnValue()) / (saleReportList.get(j).getSaleValue() / 100));
				}

				totalSaleValue = totalSaleValue + saleReportList.get(j).getSaleValue();
				totalGvnValue = totalGvnValue + saleReportList.get(j).getGvnValue();
				totalNetVal1 = totalNetVal1 + netVal1;
				totalGrnValue = totalGrnValue + saleReportList.get(j).getGrnValue();
				totalNetVal2 = totalNetVal2 + netVal2;
				totalInLac = totalInLac + inLac;
				//totalRetPer = totalRetPer + retPer;

				cell = new PdfPCell(new Phrase(String.valueOf(saleReportList.get(j).getFrName()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(roundUp(saleReportList.get(j).getSaleValue()) + "", headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(roundUp(saleReportList.get(j).getGvnValue()) + "", headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(roundUp(netVal1)), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(roundUp(saleReportList.get(j).getGrnValue())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(roundUp(netVal2)), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(roundUp(inLac)), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(roundUp(retPer)), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

			}

			PdfPCell cell;

			cell = new PdfPCell(new Phrase("Total", headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("", headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			double d = Double.parseDouble("" + totalSaleValue);
			String strTotalSaleValue = String.format("%.2f", d);

			cell = new PdfPCell(new Phrase("" + strTotalSaleValue, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			double d1 = Double.parseDouble("" + totalGvnValue);
			String strTotalGvnValue = String.format("%.2f", d1);

			cell = new PdfPCell(new Phrase("" + strTotalGvnValue, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			double d2 = Double.parseDouble("" + totalNetVal1);
			String strTotalNetVal1 = String.format("%.2f", d2);

			cell = new PdfPCell(new Phrase("" + strTotalNetVal1, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			double d3 = Double.parseDouble("" + totalGrnValue);
			String strTotalGrnValue = String.format("%.2f", d3);

			cell = new PdfPCell(new Phrase("" + strTotalGrnValue, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			double d4 = Double.parseDouble("" + totalNetVal2);
			String strTotalNetVal2 = String.format("%.2f", d4);

			cell = new PdfPCell(new Phrase("" + strTotalNetVal2, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			double d5 = Double.parseDouble("" + totalInLac);
			String strTotalInLac = String.format("%.2f", d5);

			cell = new PdfPCell(new Phrase("" + strTotalInLac, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);
			
			totalRetPer=(totalGrnValue*100)/totalSaleValue;

			double d6 = Double.parseDouble("" + totalRetPer);
			String strTotalRetPer = String.format("%.2f", d6);

			cell = new PdfPCell(new Phrase("" + strTotalRetPer, headFont1));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell(cell);

			document.open();

			Paragraph heading = new Paragraph("Sales Report by Franchisee");
			heading.setAlignment(Element.ALIGN_CENTER);
			document.add(heading);
			
			Paragraph heading2 = new Paragraph("Date : "+fromDatePdf+" to "+toDatePdf);
			heading2.setAlignment(Element.ALIGN_CENTER);
			document.add(heading2);

			DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
			String reportDate = DF.format(new Date());

			document.add(new Paragraph("\n"));

			document.add(table);

			document.close();

			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				// response.setHeader("Content-Disposition", String.format("attachment;
				// filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}

			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error: Prod From Orders" + ex.getMessage());

			ex.printStackTrace();

		}
	}
	
	
	
}
