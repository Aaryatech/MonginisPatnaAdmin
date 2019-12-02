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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
import com.ats.adminpanel.model.HSNWiseReport;
import com.ats.adminpanel.model.ItemReport;
import com.ats.adminpanel.model.ItemReportDetail;
import com.ats.adminpanel.model.item.SubCategory;
import com.ats.adminpanel.report.model.CrNoteRegItem;
import com.ats.adminpanel.report.model.CrNoteRegSp;
import com.ats.adminpanel.report.model.CrNoteRegisterList;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Controller
@Scope("session")
public class ReportController {

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

	}

	@RequestMapping(value = "/showCRNoteRegisterDone", method = RequestMethod.GET)
	public ModelAndView showCRNoteRegisterDone(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/crNote_register_done");

		try {

			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);

			model.addObject("todaysDate", todaysDate);

		} catch (Exception e) {
			System.out.println("Exce in showRegCakeSpOrderReport " + e.getMessage());
			e.printStackTrace();
		}

		return model;
	}

	List<CrNoteRegItem> crNoteRegItemList = new ArrayList<>();
	// getCRNoteRegister Ajax

	@RequestMapping(value = "/getCRNoteRegisterDone", method = RequestMethod.GET)
	public @ResponseBody List<CrNoteRegItem> getCRNoteRegisterDone(HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException {

		String frIdString = "";

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

		RestTemplate restTemplate = new RestTemplate();

		System.out.println("inside getCRNoteRegister ajax call");

		String fromDate = request.getParameter("fromDate");
		String toDate = request.getParameter("toDate");
		try {

			/*
			 * frIdString = frIdString.substring(1, frIdString.length() - 1); frIdString =
			 * frIdString.replaceAll("\"", "");
			 * 
			 * List<String> franchIds = new ArrayList();
			 * 
			 * franchIds = Arrays.asList(frIdString); if (franchIds.contains("-1")) {
			 * map.add("frIdList", -1);
			 * 
			 * } else {
			 * 
			 * map.add("frIdList", frIdString); } System.err.println("frId string " +
			 * frIdString);
			 */
			map.add("fromDate", DateConvertor.convertToYMD(fromDate));

			map.add("toDate", DateConvertor.convertToYMD(toDate));

			CrNoteRegisterList crnArray = restTemplate.postForObject(Constants.url + "getCrNoteRegisterDone", map,
					CrNoteRegisterList.class);

			List<CrNoteRegSp> crnRegSpList = new ArrayList<>();

			crNoteRegItemList = crnArray.getCrNoteRegItemList();
			crnRegSpList = crnArray.getCrNoteRegSpList();

			for (int j = 0; j < crnRegSpList.size(); j++) {
				int flag = 0;

				for (int i = 0; i < crNoteRegItemList.size(); i++) {

					if (crNoteRegItemList.get(i).getCrnId() == crnRegSpList.get(j).getCrnId()
							&& crNoteRegItemList.get(i).getHsnCode().equals(crnRegSpList.get(j).getHsnCode())) {
						flag = 1;
						crNoteRegItemList.get(i)
								.setCrnQty(crNoteRegItemList.get(i).getCrnQty() + crnRegSpList.get(j).getCrnQty());

						crNoteRegItemList.get(i).setCrnTaxable(
								(crNoteRegItemList.get(i).getCrnTaxable() + crnRegSpList.get(j).getCrnTaxable()));

						crNoteRegItemList.get(i)
								.setCgstAmt((crNoteRegItemList.get(i).getCgstAmt() + crnRegSpList.get(j).getCgstAmt()));
						crNoteRegItemList.get(i)
								.setSgstAmt((crNoteRegItemList.get(i).getSgstAmt() + crnRegSpList.get(j).getSgstAmt()));
						crNoteRegItemList.get(i)
								.setCrnAmt((crNoteRegItemList.get(i).getCrnAmt() + crnRegSpList.get(j).getCrnAmt()));

					}

				}

				if (flag == 0) {

					System.err.println("New hsn code item found ");

					CrNoteRegItem regItem = new CrNoteRegItem();

					regItem.setCrnDate(crnRegSpList.get(j).getCrnDate());

					regItem.setBillDate(crnRegSpList.get(j).getBillDate());
					regItem.setCrndId(crnRegSpList.get(j).getCrndId());
					regItem.setCrnId(crnRegSpList.get(j).getCrnId());
					regItem.setCrnQty(crnRegSpList.get(j).getCrnQty());
					regItem.setCgstAmt(crnRegSpList.get(j).getCgstAmt());
					regItem.setCgstPer(crnRegSpList.get(j).getCgstPer());
					regItem.setFrGstNo(crnRegSpList.get(j).getFrGstNo());
					regItem.setFrName(crnRegSpList.get(j).getFrName());
					regItem.setCrnAmt(crnRegSpList.get(j).getCrnAmt());
					regItem.setHsnCode(crnRegSpList.get(j).getHsnCode());
					regItem.setInvoiceNo(crnRegSpList.get(j).getInvoiceNo());
					regItem.setSgstAmt(crnRegSpList.get(j).getSgstAmt());
					regItem.setSgstPer(crnRegSpList.get(j).getSgstPer());
					regItem.setCrnTaxable(crnRegSpList.get(j).getCrnTaxable());

					regItem.setFrCode(crnRegSpList.get(j).getFrCode());

					crNoteRegItemList.add(regItem);
				}
			}

			System.err.println("crNoteRegItemList combined  " + crNoteRegItemList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

		/*	rowData.add("Sr. No.");
			rowData.add("CRN No");
			rowData.add("CRN Date");
			rowData.add("Party Name");
			rowData.add("GST No");
			rowData.add("Tax Rate");
			rowData.add("Crn Qty");
			rowData.add("Taxable Amt");
			rowData.add("Cgst Amt");
			rowData.add("Sgst Amt");
			rowData.add("Crn Amt");
			rowData.add("Crn Total Amt");*/
			
			rowData.add("Sr. No.");
			rowData.add("GSTIN/UIN of Recipient");
			rowData.add("Receiver Name");
			rowData.add("Invoice/Advance Receipt Number");
			rowData.add("Invoice/Advance Receipt date");
			rowData.add("Note/Refund Voucher Number");
			rowData.add("Note/Refund Voucher date");
			rowData.add("Document Type");
			rowData.add("Place Of Supply");
			rowData.add("Note/Refund Voucher Value");
			rowData.add("Applicable % of Tax Rate");
			rowData.add("Rate");
			rowData.add("Taxable Value");
			rowData.add("Cess Amount");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			float crnQty = 0.0f;
			float crnTaxable = 0.0f;
			float cgstAmt = 0.0f;
			float sgstAmt = 0.0f;
			float crnAmt = 0.0f;float crnTotAmt = 0.0f;

			for (int i = 0; i < crNoteRegItemList.size(); i++) {
				float crnTotal = 0.0f;
				for (int j = 0; j < crNoteRegItemList.size(); j++) {
					if (crNoteRegItemList.get(i).getCrnId() == crNoteRegItemList.get(j).getCrnId()) {
						crnTotal = crnTotal + (roundUp(crNoteRegItemList.get(j).getCrnTaxable())+roundUp(crNoteRegItemList.get(j).getCgstAmt())+roundUp(crNoteRegItemList.get(j).getSgstAmt()));
					}
				}

				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				
				rowData.add("" + (i + 1));
				rowData.add("" + crNoteRegItemList.get(i).getFrGstNo());
				rowData.add("" + crNoteRegItemList.get(i).getFrName());
				rowData.add("");
				rowData.add("");
				rowData.add("" + crNoteRegItemList.get(i).getCrnId());
				rowData.add("");
				rowData.add("");
				rowData.add("Patna");
				rowData.add("" + roundUp(crnTotal));
				rowData.add("");
				rowData.add("" + (crNoteRegItemList.get(i).getCgstPer() + crNoteRegItemList.get(i).getSgstPer()));
				rowData.add(""+roundUp(crNoteRegItemList.get(i).getCrnTaxable()));
				rowData.add(""+0);
				
				/*	
				rowData.add("" + crNoteRegItemList.get(i).getCrnId());
				rowData.add("" + crNoteRegItemList.get(i).getCrnDate());

				rowData.add("" + crNoteRegItemList.get(i).getFrName());
				rowData.add("" + crNoteRegItemList.get(i).getFrGstNo());
				rowData.add("" + (crNoteRegItemList.get(i).getCgstPer() + crNoteRegItemList.get(i).getSgstPer()));
				rowData.add(""+crNoteRegItemList.get(i).getCrnQty());
				rowData.add(""+roundUp(crNoteRegItemList.get(i).getCrnTaxable()));
				rowData.add(""+roundUp(crNoteRegItemList.get(i).getCgstAmt()));
				rowData.add(""+roundUp(crNoteRegItemList.get(i).getSgstAmt()));
				rowData.add(roundUp(crNoteRegItemList.get(i).getCrnTaxable())+roundUp(crNoteRegItemList.get(i).getCgstAmt())+roundUp(crNoteRegItemList.get(i).getSgstAmt())+"");

				rowData.add("" + roundUp(crnTotal));*/

				crnQty = crnQty + crNoteRegItemList.get(i).getCrnQty();
				crnTaxable = crnTaxable + roundUp(crNoteRegItemList.get(i).getCrnTaxable());
				cgstAmt = cgstAmt + roundUp(crNoteRegItemList.get(i).getCgstAmt());
				sgstAmt = sgstAmt + roundUp(crNoteRegItemList.get(i).getSgstAmt());
				crnAmt = crnAmt + (roundUp(crNoteRegItemList.get(i).getCrnTaxable())+roundUp(crNoteRegItemList.get(i).getCgstAmt())+roundUp(crNoteRegItemList.get(i).getSgstAmt()));
				crnTotAmt=crnTotAmt+roundUp(crnTotal);
				
				
				
				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();
			rowData.add("Total");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add(""+roundUp(crnTotAmt));
			rowData.add("");
			rowData.add("");
			rowData.add(""+roundUp(crnTaxable));
			/*rowData.add(""+crnQty);
			rowData.add(""+roundUp(crnTaxable));
			rowData.add(""+roundUp(cgstAmt));
			rowData.add(""+roundUp(sgstAmt));
			rowData.add(""+roundUp(crnAmt));
			rowData.add(""+roundUp(crnTotAmt));*/

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "Credit Note Register Report");

		} catch (Exception e) {

			e.printStackTrace();

		}
		return crNoteRegItemList;
	}

	// getCRN Reg Pdf
	@RequestMapping(value = "/getCRNoteRegisterDonePdf/{fromdate}/{todate}", method = RequestMethod.GET)
	public void getCRNoteRegisterDonePdf(@PathVariable String fromdate, @PathVariable String todate,
			HttpServletRequest request, HttpServletResponse response) throws FileNotFoundException {

		Document document = new Document(PageSize.A4);
		document.setPageSize(PageSize.A4.rotate());
		// ByteArrayOutputStream out = new ByteArrayOutputStream();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("timegetCRNoteRegisterPdf PDF ==" + dateFormat.format(cal.getTime()));
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

		PdfPTable table = new PdfPTable(11);
		table.setHeaderRows(1);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 0.7f, 2.1f, 2.3f, 2.0f, 2.2f, 1.2f, 1.2f, 1.2f, 0.9f, 0.9f, 1.2f });
			Font headFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("Sr.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CRN No", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CRN Date", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Party Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("GST No", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Tax Rate", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Qty", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Taxable Amt", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CGST Amt", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("SGST Amt", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Amt", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			int index = 0;
			for (int j = 0; j < crNoteRegItemList.size(); j++) {

				index++;
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + crNoteRegItemList.get(j).getFrCode(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(crNoteRegItemList.get(j).getCrnDate()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(crNoteRegItemList.get(j).getFrName()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(String.valueOf(crNoteRegItemList.get(j).getFrGstNo()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(
						String.valueOf(
								roundUp(crNoteRegItemList.get(j).getCgstPer() + crNoteRegItemList.get(j).getSgstPer())),
						headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(
						new Phrase(String.valueOf(roundUp(crNoteRegItemList.get(j).getCrnQty())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(
						new Phrase(String.valueOf(roundUp(crNoteRegItemList.get(j).getCrnTaxable())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(
						new Phrase(String.valueOf(roundUp(crNoteRegItemList.get(j).getCgstAmt())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(
						new Phrase(String.valueOf(roundUp(crNoteRegItemList.get(j).getSgstAmt())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

				cell = new PdfPCell(
						new Phrase(String.valueOf(roundUp(crNoteRegItemList.get(j).getCrnAmt())), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(8);
				table.addCell(cell);

			}
			document.open();

			Paragraph heading = new Paragraph(
					"Credit Note Register Report \n From Date:" + fromdate + " To Date:" + todate);
			heading.setAlignment(Element.ALIGN_CENTER);
			document.add(heading);

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

	@RequestMapping(value = "/showHSNwiseReportBetDate", method = RequestMethod.GET)
	public ModelAndView showHSNwiseReportBetDate(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/hsnwiseReport");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);
			model.addObject("todaysDate", todaysDate);

		} catch (Exception e) {

			System.out.println("Exc in show   report hsn wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	List<HSNWiseReport> hsnListBill = null;

	@RequestMapping(value = "/getReportHSNwise", method = RequestMethod.GET)
	public @ResponseBody List<HSNWiseReport> getReportHSNwise(HttpServletRequest request,
			HttpServletResponse response) {
		String fromDate = "";
		String toDate = "";
		List<HSNWiseReport> hsnList = null;

		try {
			System.out.println("Inside get hsnList    ");
			hsnListBill = new ArrayList<>();
			hsnList = new ArrayList<>();
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			int type = Integer.parseInt(request.getParameter("type"));

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));

			if (type == 1 || type == 3) {
				ParameterizedTypeReference<List<HSNWiseReport>> typeRef = new ParameterizedTypeReference<List<HSNWiseReport>>() {
				};
				ResponseEntity<List<HSNWiseReport>> responseEntity = restTemplate
						.exchange(Constants.url + "getHsnBillReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				hsnListBill = responseEntity.getBody();
				System.out.println("HSN List-------------"+hsnListBill);
			}
			if (type == 2 || type == 3) {
				ParameterizedTypeReference<List<HSNWiseReport>> typeRef1 = new ParameterizedTypeReference<List<HSNWiseReport>>() {
				};
				ResponseEntity<List<HSNWiseReport>> responseEntity1 = restTemplate
						.exchange(Constants.url + "getHsnReport", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				hsnList = responseEntity1.getBody();

				System.out.println("hsn List Bill Wise " + hsnList.toString());
			}

			for (int i = 0; i < hsnList.size(); i++) {
				for (int j = 0; j < hsnListBill.size(); j++) {
					if (hsnList.get(i).getItemHsncd().equals(hsnListBill.get(j).getItemHsncd())) {
						hsnListBill.get(j)
								.setTaxableAmt(hsnListBill.get(j).getTaxableAmt() - hsnList.get(i).getTaxableAmt());
						hsnListBill.get(j).setGrnGvnQty(hsnList.get(i).getBillQty());

						hsnListBill.get(j).setCgstRs(hsnListBill.get(j).getCgstRs() - hsnList.get(i).getCgstRs());

						hsnListBill.get(j).setSgstRs(hsnListBill.get(j).getSgstRs() - hsnList.get(i).getSgstRs());

					}
					// hsnListBill.get(j).setGrnGvnQty(0);
				}
			}
			if (type == 2) {
				hsnListBill.addAll(hsnList);
			}
			System.out.println(hsnListBill.toString());
			System.out.println(hsnList.toString());

		} catch (

		Exception e) {
			System.out.println("get sale Report hsn Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr No");
		rowData.add("HSN Code");
		rowData.add("TAX %");
		rowData.add("MANUF");
		rowData.add("RET");
		rowData.add("TOTAL");
		rowData.add("Taxable Amount");
		rowData.add("CGST %");
		rowData.add("CGST Amount");
		rowData.add("SGST %");
		rowData.add("SGST Amount");
		rowData.add("Total");

		float taxableAmt = 0.0f;
		float cgstSum = 0.0f;
		float sgstSum = 0.0f;
		float igstSum = 0.0f;
		float totalTax = 0.0f;
		float grandTotal = 0.0f;

		expoExcel.setRowData(rowData);
		int srno = 1;
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < hsnListBill.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);
			rowData.add(hsnListBill.get(i).getItemHsncd());

			rowData.add(" " + roundUp(hsnListBill.get(i).getItemTax1() + hsnListBill.get(i).getItemTax2()));
			rowData.add(" " + hsnListBill.get(i).getBillQty());
			rowData.add(" " + hsnListBill.get(i).getGrnGvnQty());
			rowData.add(" " + (hsnListBill.get(i).getBillQty() - hsnListBill.get(i).getGrnGvnQty()));
			rowData.add("" + roundUp(hsnListBill.get(i).getTaxableAmt()));
			rowData.add(" " + roundUp(hsnListBill.get(i).getItemTax1()));
			rowData.add("" + roundUp(hsnListBill.get(i).getCgstRs()));
			rowData.add(" " + roundUp(hsnListBill.get(i).getItemTax2()));

			rowData.add("" + roundUp(hsnListBill.get(i).getSgstRs()));

			rowData.add(" " + roundUp(hsnListBill.get(i).getTaxableAmt() + hsnListBill.get(i).getCgstRs()
					+ hsnListBill.get(i).getSgstRs()));

			totalTax = totalTax + hsnListBill.get(i).getItemTax1() + hsnListBill.get(i).getItemTax2();
			taxableAmt = taxableAmt + hsnListBill.get(i).getTaxableAmt();
			cgstSum = cgstSum + hsnListBill.get(i).getCgstRs();
			sgstSum = sgstSum + hsnListBill.get(i).getSgstRs();
			grandTotal = grandTotal + hsnListBill.get(i).getTaxableAmt() + hsnListBill.get(i).getCgstRs()
					+ hsnListBill.get(i).getSgstRs();

			srno = srno + 1;

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		/*
		 * expoExcel = new ExportToExcel(); rowData = new ArrayList<String>();
		 * 
		 * rowData.add(""); rowData.add("Total"); rowData.add(""); rowData.add("");
		 * rowData.add(""); rowData.add("" + roundUp(totalTax)); rowData.add("" +
		 * roundUp(taxableAmt));
		 * 
		 * rowData.add(""); rowData.add("" + roundUp(cgstSum));
		 * 
		 * rowData.add(""); rowData.add("" + roundUp(sgstSum));
		 * 
		 * rowData.add("" + roundUp(grandTotal));
		 * 
		 * expoExcel.setRowData(rowData); exportToExcelList.add(expoExcel);
		 */

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "HSNWiseReport");

		return hsnListBill;
	}

	// getCRN Reg Pdf
	@RequestMapping(value = "/getHsnWisePdf/{fromdate}/{todate}", method = RequestMethod.GET)
	public void getHsnWisePdf(@PathVariable String fromdate, @PathVariable String todate, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException {

		Document document = new Document(PageSize.A4);
		document.setPageSize(PageSize.A4.rotate());
		// ByteArrayOutputStream out = new ByteArrayOutputStream();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("getHsnWisePdf PDF ==" + dateFormat.format(cal.getTime()));
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

		PdfPTable table = new PdfPTable(12);
		table.setHeaderRows(1);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 0.7f, 1.1f, 0.9f, 1.2f, 1.2f, 1.2f, 1.2f, 1.2f, 1.2f, 1.2f, 0.9f, 1.2f });
			Font headFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("Sr.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("HSN", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("TAX %", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("MANUF", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("RET", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("TOTAL", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("TAXABLE AMT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CGST %", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("CGST AMT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("SGST %", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("SGST AMT", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("TOTAL", headFont1)); // Varience title replaced with P2 Production
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			int index = 0;
			for (int j = 0; j < hsnListBill.size(); j++) {

				index++;
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + hsnListBill.get(j).getItemHsncd(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(
						"" + (hsnListBill.get(j).getItemTax1() + hsnListBill.get(j).getItemTax2()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + hsnListBill.get(j).getBillQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + hsnListBill.get(j).getGrnGvnQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase(
						"" + roundUp(hsnListBill.get(j).getBillQty() - hsnListBill.get(j).getGrnGvnQty()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getTaxableAmt()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getItemTax1()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getCgstRs()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getItemTax2()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getSgstRs()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + roundUp(hsnListBill.get(j).getSgstRs()
						+ hsnListBill.get(j).getTaxableAmt() + hsnListBill.get(j).getCgstRs()), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

			}
			document.open();

			Paragraph heading = new Paragraph(
					"HSN wise Summary Report \n From Date:" + fromdate + " To Date:" + todate);
			heading.setAlignment(Element.ALIGN_CENTER);
			document.add(heading);

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

	RestTemplate restTemplate = new RestTemplate();
	AllFrIdNameList allFrIdNameList = new AllFrIdNameList();

	@RequestMapping(value = "/showItemReportBetDate", method = RequestMethod.GET)
	public ModelAndView showItemReportBetDate(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/itemReport");

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);
			model.addObject("todaysDate", todaysDate);

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

		} catch (Exception e) {

			System.out.println("Exc in show   report hsn wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	List<ItemReport> itemReportList = null;

	@RequestMapping(value = "/getReportItemwise", method = RequestMethod.GET)
	public @ResponseBody List<ItemReport> getReportItemwise(HttpServletRequest request, HttpServletResponse response) {
		String fromDate = "";
		String toDate = "";

		try {
			System.out.println("Inside get hsnList    ");
			itemReportList = new ArrayList<>();

			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			int selectFr = Integer.parseInt(request.getParameter("selectFr"));

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("frId", selectFr);

			ParameterizedTypeReference<List<ItemReport>> typeRef1 = new ParameterizedTypeReference<List<ItemReport>>() {
			};
			ResponseEntity<List<ItemReport>> responseEntity1 = restTemplate.exchange(Constants.url + "getItemReport",
					HttpMethod.POST, new HttpEntity<>(map), typeRef1);

			itemReportList = responseEntity1.getBody();

			System.out.println("itemReportList" + itemReportList.toString());

		} catch (

		Exception e) {
			System.out.println("get sale Report hsn Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr No");
		rowData.add("Item Name");
		rowData.add("Order Qty");
		rowData.add("Bill Qty");

		float totalBillQty = 0;
		float totalOrderQty = 0;

		expoExcel.setRowData(rowData);
		int srno = 1;
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < itemReportList.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);
			rowData.add(itemReportList.get(i).getItemName());

			rowData.add(" " + roundUp(itemReportList.get(i).getOrderQty()));
			rowData.add("" + roundUp(itemReportList.get(i).getBillQty()));

			srno = srno + 1;

			totalBillQty = totalBillQty + itemReportList.get(i).getBillQty();
			totalOrderQty = totalOrderQty + itemReportList.get(i).getOrderQty();

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("Total");
		rowData.add("");
		rowData.add("" + roundUp(totalOrderQty));
		rowData.add(" " + roundUp(totalBillQty));

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "ItemReport");

		return itemReportList;
	}

	// getCRN Reg Pdf
	@RequestMapping(value = "/getItemReportPdf/{fromdate}/{todate}", method = RequestMethod.GET)
	public void getItemReportPdf(@PathVariable String fromdate, @PathVariable String todate, HttpServletRequest request,
			HttpServletResponse response) throws FileNotFoundException {

		Document document = new Document(PageSize.A4);
		document.setPageSize(PageSize.A4.rotate());
		// ByteArrayOutputStream out = new ByteArrayOutputStream();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("getHsnWisePdf PDF ==" + dateFormat.format(cal.getTime()));
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

		PdfPTable table = new PdfPTable(4);
		table.setHeaderRows(1);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 0.7f, 3.0f, 1.2f, 1.2f });
			Font headFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("Sr.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Order Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			int index = 0;
			float totalBillQty = 0;
			float totalOrderQty = 0;

			for (int j = 0; j < itemReportList.size(); j++) {

				index++;
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemReportList.get(j).getItemName(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemReportList.get(j).getOrderQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemReportList.get(j).getBillQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				totalBillQty = totalBillQty + itemReportList.get(j).getBillQty();
				totalOrderQty = totalOrderQty + itemReportList.get(j).getOrderQty();

			}

			PdfPCell cell;

			cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));

			cell = new PdfPCell(new Phrase(" ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(" Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("" + totalOrderQty, headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("" + totalBillQty, headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			document.open();

			Paragraph heading = new Paragraph(
					"NET SALES CODE TAX WISE SUMMERY Report \n From Date:" + fromdate + " To Date:" + todate);
			heading.setAlignment(Element.ALIGN_CENTER);
			document.add(heading);

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

	List<ItemReportDetail> itemDetailList = null;

	@RequestMapping(value = "/showItemDetailReportBetDate/{itemId}/{frId}/{fromdate}/{todate}", method = RequestMethod.GET)
	public ModelAndView showItemDetailReportBetDate(@PathVariable int itemId, @PathVariable int frId,
			@PathVariable String fromdate, @PathVariable String todate, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/itemDetailReport");

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", DateConvertor.convertToYMD(fromdate));
			map.add("toDate", DateConvertor.convertToYMD(todate));
			map.add("frId", frId);
			map.add("itemId", itemId);

			ItemReportDetail[] subArray = restTemplate.postForObject(Constants.url + "getItemDetailReport", map,
					ItemReportDetail[].class);
			itemDetailList = new ArrayList<>(Arrays.asList(subArray));

			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);
			model.addObject("todaysDate", todaysDate);
			model.addObject("itemDetailList", itemDetailList);

			System.out.println("itemDetailListitemDetailListitemDetailListitemDetailList" + itemDetailList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr No");
			rowData.add("Bill Date");
			rowData.add("Bill No");
			rowData.add("Item Name");
			rowData.add("Order Qty");
			rowData.add("Bill Qty");

			expoExcel.setRowData(rowData);

			float totalBillQty = 0;
			float totalOrderQty = 0;
			int srno = 1;
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < itemDetailList.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add("" + srno);

				rowData.add(itemDetailList.get(i).getBillDate());
				rowData.add(itemDetailList.get(i).getInvoiceNo());
				rowData.add(itemDetailList.get(i).getItemName());

				rowData.add(" " + roundUp(itemDetailList.get(i).getOrderQty()));
				rowData.add("" + roundUp(itemDetailList.get(i).getBillQty()));

				totalBillQty = totalBillQty + itemDetailList.get(i).getBillQty();
				totalOrderQty = totalOrderQty + itemDetailList.get(i).getOrderQty();

				srno = srno + 1;

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("Total");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("" + roundUp(totalOrderQty));
			rowData.add(" " + roundUp(totalBillQty));

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			HttpSession session = request.getSession();
			
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "ItemDetailReport");

		} catch (Exception e) {

			System.out.println("Exc in show   report hsn wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getItemDetailReportPdf", method = RequestMethod.GET)
	public void getItemDetailReportPdf(HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {

		Document document = new Document(PageSize.A4);
		document.setPageSize(PageSize.A4.rotate());
		// ByteArrayOutputStream out = new ByteArrayOutputStream();

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("getHsnWisePdf PDF ==" + dateFormat.format(cal.getTime()));
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

		PdfPTable table = new PdfPTable(6);
		table.setHeaderRows(1);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 0.7f, 1.2f, 1.2f, 1.2f, 1.2f, 1.2f });
			Font headFont = new Font(FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);
			Font f = new Font(FontFamily.TIMES_ROMAN, 10.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell;
			hcell = new PdfPCell(new Phrase("Sr.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Date", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill No.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Order Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Bill Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);
			table.addCell(hcell);

			float totalBillQty = 0;
			float totalOrderQty = 0;

			int index = 0;
			for (int j = 0; j < itemDetailList.size(); j++) {

				index++;
				PdfPCell cell;

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));

				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemDetailList.get(j).getBillDate(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemDetailList.get(j).getInvoiceNo(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemDetailList.get(j).getItemName(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemDetailList.get(j).getOrderQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + itemDetailList.get(j).getBillQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(1);
				table.addCell(cell);

				totalBillQty = totalBillQty + itemDetailList.get(j).getBillQty();
				totalOrderQty = totalOrderQty + itemDetailList.get(j).getOrderQty();

			}

			PdfPCell cell;

			cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));

			cell = new PdfPCell(new Phrase(" ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(" ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(" ", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase(" Total", headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("" + totalBillQty, headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(1);
			table.addCell(cell);

			cell = new PdfPCell(new Phrase("" + totalOrderQty, headFont));
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPaddingRight(1);
			table.addCell(cell);
			document.open();

			Paragraph heading = new Paragraph("Item SUMMERY Report \n  ");
			heading.setAlignment(Element.ALIGN_CENTER);
			document.add(heading);

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
