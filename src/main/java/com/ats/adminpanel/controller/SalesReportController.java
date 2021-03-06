package com.ats.adminpanel.controller;

import java.awt.Dimension;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.zefer.pd4ml.PD4Constants;
import org.zefer.pd4ml.PD4ML;
import org.zefer.pd4ml.PD4PageMark;

import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.AllFrIdName;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.AllRoutesListResponse;
import com.ats.adminpanel.model.DispatchReport;
import com.ats.adminpanel.model.DispatchReportList;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.FranchiseForDispatch;
import com.ats.adminpanel.model.PDispatchReport;
import com.ats.adminpanel.model.PDispatchReportList;
import com.ats.adminpanel.model.POrder;
import com.ats.adminpanel.model.Route;
import com.ats.adminpanel.model.Tax1Report;
import com.ats.adminpanel.model.Tax2Report;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteId;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteIdResponse;
import com.ats.adminpanel.model.franchisee.Menu;
import com.ats.adminpanel.model.franchisee.SubCategory;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.FrItemStockConfigureList;
import com.ats.adminpanel.model.item.Item;
import com.ats.adminpanel.model.item.MCategoryList;
import com.ats.adminpanel.model.salesreport.RoyaltyListBean;
import com.ats.adminpanel.model.salesreport.SalesReportBillwise;
import com.ats.adminpanel.model.salesreport.SalesReportBillwiseAllFr;
import com.ats.adminpanel.model.salesreport.SalesReportDateMonth;
import com.ats.adminpanel.model.salesreport.SalesReportItemwise;
import com.ats.adminpanel.model.salesreport.SalesReportRoyalty;
import com.ats.adminpanel.model.salesreport.SalesReportRoyaltyFr;
import com.ats.adminpanel.model.salesreport.SalesRoyaltyConsByCat;
import com.ats.adminpanel.model.salesvaluereport.SalesReturnValueDaoList;

@Controller
@Scope("session")
public class SalesReportController {
	List<MCategoryList> mCategoryList;
	List<String> frList = new ArrayList<>();
	AllFrIdNameList allFrIdNameList = new AllFrIdNameList();
	List<SalesReportBillwise> saleListForPdf;// it is Static
	String todaysDate;
	List<SalesReportRoyalty> royaltyListForPdf;
	List<SalesRoyaltyConsByCat> royaltyConsByCatListForPdf;

	List<SalesReportBillwiseAllFr> staticSaleByAllFr;

	List<SalesReportRoyaltyFr> royaltyFrList;

	List<SalesReportRoyaltyFr> staticRoyaltyFrList;

	List<SalesReportItemwise> staticSaleListItemWise;

	RoyaltyListBean staticRoyaltyBean = new RoyaltyListBean();

	float getRoyPer() {

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

		RestTemplate restTemplate = new RestTemplate();

		String settingKey = new String();

		settingKey = "roy_percentage";

		map.add("settingKeyList", settingKey);

		FrItemStockConfigureList settingList = restTemplate.postForObject(Constants.url + "getDeptSettingValue", map,
				FrItemStockConfigureList.class);

		float royPer = settingList.getFrItemStockConfigure().get(0).getSettingValue();

		return royPer;
	}

	@RequestMapping(value = "/showSaleReports", method = RequestMethod.GET)
	public ModelAndView showSaleReporPage(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/viewSalesReports");

		return model;

	}

	@RequestMapping(value = "/showSaleReportByDate", method = RequestMethod.GET)
	public ModelAndView showSaleReportByDate(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/billwisesalesbydate");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);
			CategoryListResponse categoryListResponse;

			categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);

			mCategoryList = categoryListResponse.getmCategoryList();

			model.addObject("mCategoryList", mCategoryList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	// ajax for billwise sale by date if all cate selected
	@RequestMapping(value = "/getAllCatByAjax", method = RequestMethod.GET)
	@ResponseBody
	public List<MCategoryList> getAllCatByAjax(HttpServletRequest request, HttpServletResponse response) {
		System.err.println("CAT - " + mCategoryList);
		return mCategoryList;

	}

	@RequestMapping(value = "/showTaxReport", method = RequestMethod.GET)
	public ModelAndView showTaxReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/tax/tax1Report");
		// Constants.mainAct =2;
		// Constants.subAct =20;
		List<Tax1Report> taxReportList = null;
		String fromDate = "";
		String toDate = "";
		try {

			RestTemplate restTemplate = new RestTemplate();

			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");

			if (fromDate == null && toDate == null) {
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				fromDate = formatter.format(date);
				toDate = formatter.format(date);
			}
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<Tax1Report>> typeRef = new ParameterizedTypeReference<List<Tax1Report>>() {
			};
			ResponseEntity<List<Tax1Report>> responseEntity = restTemplate.exchange(Constants.url + "getTax1Report",
					HttpMethod.POST, new HttpEntity<>(map), typeRef);

			taxReportList = responseEntity.getBody();
			model.addObject("taxReportList", taxReportList);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);

			// exportToExcel
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			/*
			 * rowData.add("Sr.No."); rowData.add("Invoice No"); rowData.add("Bill No");
			 * rowData.add("Bill Date"); rowData.add("Franchisee Name");
			 * rowData.add("Franchisee Gst No");
			 * 
			 * rowData.add("CGST %"); rowData.add("SGST %");
			 * 
			 * rowData.add("CGST Amt"); rowData.add("SGST Amt"); rowData.add("Taxable Amt");
			 * rowData.add("Total Tax"); rowData.add("Grand Total");
			 * rowData.add("Bill Total");
			 */

			rowData.add("Sr.No.");
			rowData.add("GSTIN/UIN of Recipient");
			rowData.add("Receiver Name");
			rowData.add("Invoice No");
			rowData.add("Invoice Date");
			rowData.add("Invoice Value");
			rowData.add("Place of Supply");
			rowData.add("Reverse Charge");

			rowData.add("Applicable % of Tax Rate");
			rowData.add("Invoice Type");
			rowData.add("E Commerce GSTIN");
			rowData.add("Rate");
			rowData.add("Taxable Value");
			rowData.add("Cess Amount");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			float cgstAmt = 0.0f;
			float sgstAmt = 0.0f;
			float taxableAmt = 0.0f;
			float taxAmt = 0.0f;
			float grandAmt = 0.0f;
			float billAmt = 0.0f;
			for (int i = 0; i < taxReportList.size(); i++) {
				float finalTotal = 0;
				for (int j = 0; j < taxReportList.size(); j++) {

					if (taxReportList.get(j).getBillNo() == taxReportList.get(i).getBillNo()) {
						finalTotal = finalTotal + (roundUp(taxReportList.get(j).getTaxableAmt())
								+ roundUp(taxReportList.get(j).getCgstAmt())
								+ roundUp(taxReportList.get(j).getSgstAmt()));
					}
				}
				cgstAmt = cgstAmt + roundUp(taxReportList.get(i).getCgstAmt());
				sgstAmt = sgstAmt + roundUp(taxReportList.get(i).getSgstAmt());
				taxableAmt = taxableAmt + roundUp(taxReportList.get(i).getTaxableAmt());
				taxAmt = taxAmt
						+ (roundUp(taxReportList.get(i).getCgstAmt()) + roundUp(taxReportList.get(i).getSgstAmt()));
				grandAmt = grandAmt + (roundUp(taxReportList.get(i).getTaxableAmt())
						+ roundUp(taxReportList.get(i).getCgstAmt()) + roundUp(taxReportList.get(i).getSgstAmt()));
				billAmt = billAmt + roundUp(finalTotal);

				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add((i + 1) + "");
				rowData.add("" + taxReportList.get(i).getFrGstNo());
				rowData.add("" + taxReportList.get(i).getFrName());
				// rowData.add("" + taxReportList.get(i).getBillNo());
				rowData.add("" + taxReportList.get(i).getInvoiceNo());
				rowData.add("" + taxReportList.get(i).getBillDate());

				// rowData.add((roundUp(taxReportList.get(i).getTaxableAmt())+roundUp(taxReportList.get(i).getCgstAmt())+roundUp(taxReportList.get(i).getSgstAmt()))+"");
				// //Grand Total
				rowData.add("" + taxReportList.get(i).getGrandTotal());
				rowData.add("Patna"); // Place Of Supply
				rowData.add("N"); // Reverse Charge
				rowData.add(""); // Applicable % of Tax Rate
				rowData.add("Regular");
				rowData.add(""); // E Commerce GSTIN

				rowData.add("" + (taxReportList.get(i).getCgstPer() + taxReportList.get(i).getSgstPer()));
				rowData.add("" + roundUp(taxReportList.get(i).getTaxableAmt()));
				rowData.add(""); // Cess Amt

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
			rowData.add("" + roundUp(grandAmt));

			/*
			 * rowData.add(""); rowData.add("Total");
			 * 
			 * rowData.add(""+roundUp(cgstAmt)); rowData.add(""+roundUp(sgstAmt));
			 * rowData.add(""+roundUp(taxableAmt)); rowData.add(""+roundUp(taxAmt));
			 * rowData.add(""+roundUp(grandAmt));
			 */

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "Tax_Repot1");

		} catch (Exception e) {
			System.out.println("Exc in Tax Report" + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/showTax2Report", method = RequestMethod.GET)
	public ModelAndView showTax2Report(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/tax/tax2Report");
		// Constants.mainAct =2;
		// Constants.subAct =20;
		List<Tax2Report> taxReportList = null;
		String fromDate = "";
		String toDate = "";
		try {

			RestTemplate restTemplate = new RestTemplate();

			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");

			if (fromDate == null && toDate == null) {
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				fromDate = formatter.format(date);
				toDate = formatter.format(date);
			}
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<Tax2Report>> typeRef = new ParameterizedTypeReference<List<Tax2Report>>() {
			};
			ResponseEntity<List<Tax2Report>> responseEntity = restTemplate.exchange(Constants.url + "getTax2Report",
					HttpMethod.POST, new HttpEntity<>(map), typeRef);

			taxReportList = responseEntity.getBody();
			model.addObject("taxReportList", taxReportList);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);

			// exportToExcel
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr.No.");
			rowData.add("Invoice No");
			rowData.add("Bill No");
			rowData.add("Bill Date");
			rowData.add("Party Name");
			rowData.add("GSTIN");

			rowData.add("Sell @ 28%");
			rowData.add("Sell @ 18%");
			rowData.add("Sell @ 12%");
			rowData.add("Sell @ 5%");
			rowData.add("Sell @ 0%");
			rowData.add("Taxable Value");
			rowData.add("SGST @ 14%");
			rowData.add("CGST @ 14%");
			rowData.add("SGST @ 9%");
			rowData.add("CGST @ 9%");
			rowData.add("SGST @ 6%");
			rowData.add("CGST @ 6%");
			rowData.add("SGST @ 2.5%");
			rowData.add("CGST @ 2.5%");
			rowData.add("SGST @ 0%");
			rowData.add("CGST @ 0%");
			rowData.add("SGST Value");
			rowData.add("CGST Value");
			rowData.add("GROSS BILL");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			for (int i = 0; i < taxReportList.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add((i + 1) + "");
				rowData.add("" + taxReportList.get(i).getInvoiceNo());
				rowData.add("" + taxReportList.get(i).getBillNo());
				rowData.add("" + taxReportList.get(i).getBillDate());

				rowData.add("" + taxReportList.get(i).getFrName());
				rowData.add("" + taxReportList.get(i).getFrGstNo());

				rowData.add("" + taxReportList.get(i).getTaxableAmtTwentyEight());
				rowData.add("" + taxReportList.get(i).getTaxableAmtEighteen());
				rowData.add("" + taxReportList.get(i).getTaxableAmtTwelve());
				rowData.add("" + taxReportList.get(i).getTaxableAmtFive());
				rowData.add("" + taxReportList.get(i).getTaxableAmtZero());
				float taxableAmt = Math.round((taxReportList.get(i).getTaxableAmtTwentyEight()
						+ taxReportList.get(i).getTaxableAmtEighteen() + taxReportList.get(i).getTaxableAmtTwelve()
						+ taxReportList.get(i).getTaxableAmtFive() + taxReportList.get(i).getTaxableAmtZero()));
				rowData.add("" + taxableAmt);
				rowData.add("" + taxReportList.get(i).getSgstAmtTwentyEight());
				rowData.add("" + taxReportList.get(i).getCgstAmtTwentyEight());
				rowData.add("" + taxReportList.get(i).getSgstAmtEighteen());
				rowData.add("" + taxReportList.get(i).getCgstAmtEighteen());
				rowData.add("" + taxReportList.get(i).getSgstAmtTwelve());
				rowData.add("" + taxReportList.get(i).getCgstAmtTwelve());
				rowData.add("" + taxReportList.get(i).getSgstAmtFive());
				rowData.add("" + taxReportList.get(i).getCgstAmtFive());
				rowData.add("" + taxReportList.get(i).getSgstAmtZero());
				rowData.add("" + taxReportList.get(i).getCgstAmtZero());
				float sgstSum = Math.round((taxReportList.get(i).getSgstAmtTwentyEight()
						+ taxReportList.get(i).getSgstAmtEighteen() + taxReportList.get(i).getSgstAmtTwelve()
						+ taxReportList.get(i).getSgstAmtFive() + taxReportList.get(i).getSgstAmtZero()));
				float cgstSum = Math.round((taxReportList.get(i).getCgstAmtTwentyEight()
						+ taxReportList.get(i).getCgstAmtEighteen() + taxReportList.get(i).getCgstAmtTwelve()
						+ taxReportList.get(i).getCgstAmtFive() + taxReportList.get(i).getCgstAmtZero()));
				float grossSell = Math.round((taxableAmt + sgstSum + cgstSum));
				rowData.add("" + sgstSum);
				rowData.add("" + cgstSum);
				rowData.add("" + grossSell);
				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "Tax_Repot2");

		} catch (Exception e) {
			System.out.println("Exc in Tax Report" + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleBillwise", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportBillwise> saleReportBillWise(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportBillwise> saleList = new ArrayList<>();
		String fromDate = "";
		String toDate = "";

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			String selectedCat = request.getParameter("cat_id_list");
			List<String> catIdList = new ArrayList<>();
			selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
			selectedCat = selectedCat.replaceAll("\"", "");
			catIdList = Arrays.asList(selectedCat);
			System.err.println("cat Id List " + catIdList.toString());

			boolean isAllFrSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseAllFrSelected", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				saleListForPdf = saleList;
				System.out.println("sales List Bill Wise " + saleList.toString());

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwise", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				saleListForPdf = saleList;
				System.out.println("sales List Bill Wise " + saleList.toString());

			}
		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr");
		rowData.add("Invoice No");
		rowData.add("Invoice Date");

		rowData.add("Franchise");
		rowData.add("City");
		rowData.add("GST No");
		rowData.add("Taxable Amt");
		rowData.add("CGST Amt");
		rowData.add("SGST Amt");

		rowData.add("IGST Amt");
		rowData.add("Total Tax Amt");
		rowData.add("Total");

		expoExcel.setRowData(rowData);
		int srno = 1;
		exportToExcelList.add(expoExcel);
		float taxableAmt = 0.0f;
		float cgstSum = 0.0f;
		float sgstSum = 0.0f;
		float igstSum = 0.0f;
		float totalTax = 0.0f;
		float grandTotal = 0.0f;

		for (int i = 0; i < saleList.size(); i++) {

			taxableAmt = taxableAmt + saleList.get(i).getTaxableAmt();
			cgstSum = cgstSum + saleList.get(i).getCgstSum();
			sgstSum = sgstSum + saleList.get(i).getSgstSum();
			igstSum = igstSum + saleList.get(i).getIgstSum();
			totalTax = totalTax + saleList.get(i).getTotalTax();
			grandTotal = grandTotal + saleList.get(i).getGrandTotal();

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);
			rowData.add(saleList.get(i).getInvoiceNo());
			rowData.add(saleList.get(i).getBillDate());

			rowData.add(saleList.get(i).getFrName());

			rowData.add(saleList.get(i).getFrCity());
			rowData.add(saleList.get(i).getFrGstNo());
			rowData.add("" + roundUp(saleList.get(i).getTaxableAmt()));
			rowData.add("" + roundUp(saleList.get(i).getCgstSum()));
			rowData.add("" + roundUp(saleList.get(i).getSgstSum()));

			rowData.add("" + roundUp(saleList.get(i).getIgstSum()));
			rowData.add("" + roundUp(saleList.get(i).getTotalTax()));
			rowData.add("" + roundUp(saleList.get(i).getGrandTotal()));
			srno = srno + 1;

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}
		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("");
		rowData.add("Total");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("");
		rowData.add("" +

				roundUp(taxableAmt));
		rowData.add("" + roundUp(cgstSum));
		rowData.add("" + roundUp(sgstSum));
		rowData.add("" + roundUp(igstSum));
		rowData.add("" + roundUp(totalTax));
		rowData.add("" + roundUp(grandTotal));

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "SaleBillWiseDate");
		session.setAttribute("reportNameNew", "Bill-wise Report");
		session.setAttribute("searchByNew", "From Date: " + fromDate + "  To Date: " + toDate + " ");
		session.setAttribute("mergeUpto1", "$A$1:$L$1");
		session.setAttribute("mergeUpto2", "$A$2:$L$2");

		return saleList;
	}

	@RequestMapping(value = "pdf/showSaleReportByDatePdf/{fDate}/{tDate}/{selectedFr}/{routeId}/{selectedCat}/", method = RequestMethod.GET)
	public ModelAndView showSaleReportByDatePdf(@PathVariable String fDate, @PathVariable String tDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable String selectedCat,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/billwisesalesbydatePdf");

		List<SalesReportBillwise> saleList = new ArrayList<>();

		boolean isAllFrSelected = false;
		try {

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.equalsIgnoreCase("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");
				map.add("catIdList", selectedCat);
				map.add("fromDate", fDate);
				map.add("toDate", tDate);
				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseAllFrSelected", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				saleListForPdf = saleList;
				System.out.println("sales List Bill Wise " + saleList.toString());

			} else {
				System.out.println("Inside else Few fr Selected ");
				map.add("catIdList", selectedCat);

				map.add("frIdList", selectedFr);
				map.add("fromDate", fDate);
				map.add("toDate", tDate);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwise", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				saleListForPdf = saleList;
				System.out.println("sales List Bill Wise " + saleList.toString());

			}

		} catch (

		Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		model.addObject("fromDate", fDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("toDate", tDate);

		model.addObject("report", saleList);

		return model;
	}

	// report 2
	@RequestMapping(value = "/showSaleReportByFr", method = RequestMethod.GET)
	public ModelAndView showSaleReportByFr(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/billwisesalebyfr");
		System.out.println("inside showSaleReportByFr ");
		// Constants.mainAct =2;
		// Constants.subAct =20;
		HttpSession session = request.getSession();

		System.out.println("session Id in show Page  " + session.getId());

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

			CategoryListResponse categoryListResponse;

			categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);

			mCategoryList = categoryListResponse.getmCategoryList();

			model.addObject("mCategoryList", mCategoryList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleBillwiseByFr", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportBillwise> getSaleBillwiseByFr(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportBillwise> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			String selectedCat = request.getParameter("cat_id_list");
			List<String> catIdList = new ArrayList<>();
			selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
			selectedCat = selectedCat.replaceAll("\"", "");
			catIdList = Arrays.asList(selectedCat);

			boolean isAllFrSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseByFrAllFr", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				String name = "Sachin";

				HttpSession session = request.getSession();

				System.out.println("session Id  In Ajax Call " + session.getId());
				session.setAttribute("pdfData", saleList);

				session.setAttribute("name", name);
				saleListForPdf = saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseByFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				String name = "Sachin";

				HttpSession session = request.getSession();

				System.out.println("session Id  In Ajax Call " + session.getId());
				session.setAttribute("pdfData", saleList);

				session.setAttribute("name", name);
				saleListForPdf = saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			}
		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("SR NO");
		rowData.add("Franchise Name");
		rowData.add("Franchise City");
		rowData.add("Franchise GST Number");
		rowData.add("Taxable Amount");
		rowData.add("SGST Amount");
		rowData.add("CGST Amount");
		rowData.add("IGST Amount");
		rowData.add("Total Tax");
		rowData.add("Grand Total");

		int SrNo = 1;

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < saleList.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + SrNo);

			rowData.add(saleList.get(i).getFrName());

			rowData.add(saleList.get(i).getFrCity());
			rowData.add(saleList.get(i).getFrGstNo());
			rowData.add("" + saleList.get(i).getTaxableAmt());
			rowData.add("" + saleList.get(i).getSgstSum());
			rowData.add("" + saleList.get(i).getCgstSum());
			rowData.add("" + saleList.get(i).getIgstSum());
			rowData.add("" + saleList.get(i).getTotalTax());
			rowData.add("" + saleList.get(i).getGrandTotal());

			SrNo = SrNo + 1;

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "SaleBillWiseFr");
		return saleList;
	}

	@RequestMapping(value = "pdf/showSaleBillwiseByFrPdf/{fromDate}/{toDate}/{selectedFr}/{routeId}/{selectedCat}", method = RequestMethod.GET)
	public ModelAndView showSaleBillwiseByFrPdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable String selectedCat,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/pdf/billwisesalebyfrPdf");

		List<SalesReportBillwise> saleList = new ArrayList<>();
		boolean isAllFrSelected = false;

		try {

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.equalsIgnoreCase("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseByFrAllFr", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				String name = "Sachin";

				HttpSession session = request.getSession();

				System.out.println("session Id  In Ajax Call " + session.getId());
				session.setAttribute("pdfData", saleList);

				session.setAttribute("name", name);
				saleListForPdf = saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			} else {

				System.out.println("Inside else Few fr Selected mgg" + selectedFr + "ui");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catIdList", selectedCat);

				ParameterizedTypeReference<List<SalesReportBillwise>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwise>>() {
				};
				ResponseEntity<List<SalesReportBillwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportBillwiseByFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				String name = "Sachin";

				HttpSession session = request.getSession();

				System.out.println("session Id  In Ajax Call " + session.getId());
				session.setAttribute("pdfData", saleList);

				session.setAttribute("name", name);
				saleListForPdf = saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			}

			model.addObject("fromDate", fromDate);

			model.addObject("toDate", toDate);
			model.addObject("FACTORYNAME", Constants.FACTORYNAME);
			model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);

			model.addObject("report", saleListForPdf);
		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise1 " + e.getMessage());
			e.printStackTrace();

		}

		return model;
	}

	// report 3
	@RequestMapping(value = "/showSaleReportGrpByDate", method = RequestMethod.GET)
	public ModelAndView showSaleReportGrpByDate(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/billwisesalesgrpbydate");
		System.out.println("inside showSaleReportGrpByDate ");
		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			String todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getFrListForDatewiseReport", method = RequestMethod.GET)
	@ResponseBody
	public List<AllFrIdName> getFrListForDatewiseReport(HttpServletRequest request, HttpServletResponse response) {

		return allFrIdNameList.getFrIdNamesList();
	}

	@RequestMapping(value = "/getSaleBillwiseGrpByDate", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportDateMonth> getSaleBillwiseGrpByDate(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportDateMonth> saleList = new ArrayList<>();
		String fromDate = "";
		String toDate = "";
		String frDisplay = "";

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside else Few fr Selected ");

			map.add("frIdList", selectedFr);
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			System.out.println(map);
			ParameterizedTypeReference<List<SalesReportDateMonth>> typeRef = new ParameterizedTypeReference<List<SalesReportDateMonth>>() {
			};
			ResponseEntity<List<SalesReportDateMonth>> responseEntity = restTemplate
					.exchange(Constants.url + "getDatewiseReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			for (int i = 0; i < saleList.size(); i++) {

				float netGrandTotal = (saleList.get(i).getGrandTotal()
						- (saleList.get(i).getGrnGrandTotal() + saleList.get(i).getGvnGrandTotal()));
				float netTaxableAmt = (saleList.get(i).getTaxableAmt()
						- (saleList.get(i).getGrnTaxableAmt() + saleList.get(i).getGvnTaxableAmt()));

				float netTotalTax = (saleList.get(i).getTotalTax()
						- (saleList.get(i).getGrnTotalTax() + saleList.get(i).getGvnTotalTax()));
				saleList.get(i).setNetGrandTotal(netGrandTotal);
				saleList.get(i).setNetTaxableAmt(netTaxableAmt);
				saleList.get(i).setNetTotalTax(netTotalTax);
			}

			System.out.println("sales List Bill Wise " + saleList.toString());

			List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

			Set<String> set = new HashSet<>();

			if (allFrIdNameList.getFrIdNamesList() != null) {

				for (int j = 0; j < frIds.size(); j++) {

					for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

						if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
							set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
							break;
						}

					}

				}
			}
			frDisplay = String.join(", ", set);

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("SR NO");

		rowData.add("Bill Date");
		// rowData.add("Taxable Amount");
		// rowData.add("Tax Amount");
		rowData.add("Grand Total");

		// rowData.add("GRN Taxable Amount");
		// rowData.add("GRN Tax Amount");
		rowData.add("GRN Grand Total");

		// rowData.add("GVN Taxable Amount");
		// rowData.add("GVN Tax Amount");
		rowData.add("GVN Grand Total");

		// rowData.add("Net Taxable Amount");
		// rowData.add("Net Tax Amount");
		rowData.add("Net Grand Total");

		float totalGrandTotal = 0f;
		float totalTax = 0f;
		float totalTaxableAmt = 0f;

		float totalGrnGrandTotal = 0f;
		float totalGrnTaxableAmt = 0f;
		float totalGrnTax = 0f;

		float totalGvnGrandTotal = 0f;
		float totalGvnTax = 0f;
		float totalGvnTaxableAmt = 0f;

		float totalNetGrandTotal = 0f;
		float totalNetTax = 0f;
		float totalNetTaxableAmt = 0f;

		int srno = 1;
		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < saleList.size(); i++) {

			totalGrnGrandTotal = totalGrnGrandTotal + saleList.get(i).getGrnGrandTotal();
			totalGrnTaxableAmt = totalGrnTaxableAmt + saleList.get(i).getGrnTaxableAmt();
			totalGrnTax = totalGrnTax + saleList.get(i).getGrnTotalTax();

			totalGrandTotal = totalGrandTotal + saleList.get(i).getGrandTotal();
			totalTax = totalTax + saleList.get(i).getTotalTax();
			totalTaxableAmt = totalTaxableAmt + saleList.get(i).getTaxableAmt();

			totalGvnGrandTotal = totalGvnGrandTotal + saleList.get(i).getGvnGrandTotal();
			totalGvnTax = totalGvnTax + saleList.get(i).getGvnTotalTax();
			totalGvnTaxableAmt = totalGvnTaxableAmt + saleList.get(i).getGvnTaxableAmt();

			totalNetGrandTotal = totalNetGrandTotal + saleList.get(i).getNetGrandTotal();
			totalNetTax = totalNetTax + saleList.get(i).getNetTotalTax();
			totalNetTaxableAmt = totalNetTaxableAmt + saleList.get(i).getNetTaxableAmt();

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);

			rowData.add(saleList.get(i).getBillDate());
			// rowData.add("" + saleList.get(i).getTaxableAmt());
			// rowData.add("" + saleList.get(i).getTotalTax());
			rowData.add("" + saleList.get(i).getGrandTotal());

			// rowData.add("" + saleList.get(i).getGrnTaxableAmt());
			// rowData.add("" + saleList.get(i).getGrnTotalTax());
			rowData.add("" + saleList.get(i).getGrnGrandTotal());

			// rowData.add("" + saleList.get(i).getGvnTaxableAmt());
			// rowData.add("" + saleList.get(i).getGvnTotalTax());
			rowData.add("" + saleList.get(i).getGvnGrandTotal());

			// rowData.add("" + saleList.get(i).getNetTaxableAmt());
			// rowData.add("" + saleList.get(i).getNetTotalTax());
			rowData.add("" + saleList.get(i).getNetGrandTotal());

			srno = srno + 1;
			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("Total");

		rowData.add("");

		// rowData.add("" + roundUp(totalTax));
		// rowData.add("" + roundUp(totalTaxableAmt));
		rowData.add("" + roundUp(totalGrandTotal));

		// rowData.add("" + roundUp(totalGrnTaxableAmt));
		// rowData.add("" + roundUp(totalGrnTax));
		rowData.add("" + roundUp(totalGrnGrandTotal));

		// rowData.add("" + roundUp(totalGvnTaxableAmt));
		// rowData.add("" + roundUp(totalGvnTax));
		rowData.add("" + roundUp(totalGvnGrandTotal));

		// rowData.add("" + roundUp(totalNetTaxableAmt));
		// rowData.add("" + roundUp(totalNetTax));
		rowData.add("" + roundUp(totalNetGrandTotal));

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "SaleDateWise");
		session.setAttribute("reportNameNew", "Date Wise Sale Report Between " + fromDate + " To " + toDate);
		// session.setAttribute("searchByNew", "From Date: " + fromDate + " To Date: " +
		// toDate);
		session.setAttribute("searchByNew", "Franchisee : " + frDisplay);

		session.setAttribute("mergeUpto1", "$A$1:$F$1");
		session.setAttribute("mergeUpto2", "$A$2:$F$2");

		return saleList;
	}

	@RequestMapping(value = "pdf/showSaleBillwiseGrpByDatePdf/{fromDate}/{toDate}/{selectedFr}/{routeId}", method = RequestMethod.GET)
	public ModelAndView showSaleBillwiseGrpByDate(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/billwisesalesgrpbydatePdf");

		boolean isAllFrSelected = false;

		List<SalesReportDateMonth> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Bill Wise");

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

			Set<String> set = new HashSet<>();

			if (allFrIdNameList.getFrIdNamesList() != null) {

				for (int j = 0; j < frIds.size(); j++) {

					for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

						if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
							set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
							break;
						}

					}

				}
			}
			String string = String.join(", ", set);
			model.addObject("fr", string);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside else Few fr Selected ");

			map.add("frIdList", selectedFr);
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SalesReportDateMonth>> typeRef = new ParameterizedTypeReference<List<SalesReportDateMonth>>() {
			};
			ResponseEntity<List<SalesReportDateMonth>> responseEntity = restTemplate
					.exchange(Constants.url + "getDatewiseReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			for (int i = 0; i < saleList.size(); i++) {

				float netGrandTotal = (saleList.get(i).getGrandTotal()
						- (saleList.get(i).getGrnGrandTotal() + saleList.get(i).getGvnGrandTotal()));
				float netTaxableAmt = (saleList.get(i).getTaxableAmt()
						- (saleList.get(i).getGrnTaxableAmt() + saleList.get(i).getGvnTaxableAmt()));

				float netTotalTax = (saleList.get(i).getTotalTax()
						- (saleList.get(i).getGrnTotalTax() + saleList.get(i).getGvnTotalTax()));
				saleList.get(i).setNetGrandTotal(netGrandTotal);
				saleList.get(i).setNetTaxableAmt(netTaxableAmt);
				saleList.get(i).setNetTotalTax(netTotalTax);
			}

			System.out.println("sales List Bill Wise " + saleList.toString());

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", saleList);
		return model;
	}

	// getSaleReportBillwiseByMonth

	@RequestMapping(value = "/showSaleReportByMonth", method = RequestMethod.GET)
	public ModelAndView showSaleReportByMonth(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/billwisesalebymonth");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	List<SalesReportDateMonth> saleList = new ArrayList<>();

	@RequestMapping(value = "/getSaleBillwiseGrpByMonth", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportDateMonth> getSaleBillwiseGrpByMonth(HttpServletRequest request,
			HttpServletResponse response) {

		// List<SalesReportDateMonth> saleList = new ArrayList<>();
		String fromDate = "";
		String toDate = "";
		String frDisplay = "";

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside else Few fr Selected ");

			map.add("frIdList", selectedFr);
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			saleList = new ArrayList<>();
			saleList.clear();

			ParameterizedTypeReference<List<SalesReportDateMonth>> typeRef = new ParameterizedTypeReference<List<SalesReportDateMonth>>() {
			};
			ResponseEntity<List<SalesReportDateMonth>> responseEntity = restTemplate
					.exchange(Constants.url + "getMonthwiseReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			for (int i = 0; i < saleList.size(); i++) {
				if (saleList != null) {

					float netGrandTotal = (saleList.get(i).getGrandTotal()
							- (saleList.get(i).getGrnGrandTotal() + saleList.get(i).getGvnGrandTotal()));
					float netTaxableAmt = (saleList.get(i).getTaxableAmt()
							- (saleList.get(i).getGrnTaxableAmt() + saleList.get(i).getGvnTaxableAmt()));

					float netTotalTax = (saleList.get(i).getTotalTax()
							- (saleList.get(i).getGrnTotalTax() + saleList.get(i).getGvnTotalTax()));
					saleList.get(i).setNetGrandTotal(netGrandTotal);
					saleList.get(i).setNetTaxableAmt(netTaxableAmt);
					saleList.get(i).setNetTotalTax(netTotalTax);
				}
			}

			System.out.println("sales List Bill Wise " + saleList.toString());

			List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

			Set<String> set = new HashSet<>();

			if (allFrIdNameList.getFrIdNamesList() != null) {

				for (int j = 0; j < frIds.size(); j++) {

					for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

						if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
							set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
							break;
						}

					}

				}
			}
			frDisplay = String.join(", ", set);

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("SR NO");

		rowData.add("Month");
		// rowData.add("Taxable Amount");
		// rowData.add("Tax Amount");
		rowData.add("Grand Total");

		// rowData.add("GRN Taxable Amount");
		// rowData.add("GRN Tax Amount");
		rowData.add("GRN Grand Total");

		// rowData.add("GVN Taxable Amount");
		// rowData.add("GVN Tax Amount");
		rowData.add("GVN Grand Total");

		// rowData.add("Net Taxable Amount");
		// rowData.add("Net Tax Amount");
		rowData.add("Net Grand Total");
		rowData.add("Contribution %");

		rowData.add("Return % GRN");
		rowData.add("Return % GVN");
		rowData.add("Return % SUM");

		float totalNetValForContri = 0f;
		for (int i = 0; i < saleList.size(); i++) {
			totalNetValForContri = totalNetValForContri + saleList.get(i).getNetGrandTotal();
		}

		float totalGrandTotal = 0f;
		float totalTax = 0f;
		float totalTaxableAmt = 0f;

		float totalGrnGrandTotal = 0f;
		float totalGrnTaxableAmt = 0f;
		float totalGrnTax = 0f;

		float totalGvnGrandTotal = 0f;
		float totalGvnTax = 0f;
		float totalGvnTaxableAmt = 0f;

		float totalNetGrandTotal = 0f;
		float totalNetTax = 0f;
		float totalNetTaxableAmt = 0f;

		float totalContri = 0f;

		int srno = 1;
		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < saleList.size(); i++) {

			totalGrnGrandTotal = totalGrnGrandTotal + saleList.get(i).getGrnGrandTotal();
			totalGrnTaxableAmt = totalGrnTaxableAmt + saleList.get(i).getGrnTaxableAmt();
			totalGrnTax = totalGrnTax + saleList.get(i).getGrnTotalTax();

			totalGrandTotal = totalGrandTotal + saleList.get(i).getGrandTotal();
			totalTax = totalTax + saleList.get(i).getTotalTax();
			totalTaxableAmt = totalTaxableAmt + saleList.get(i).getTaxableAmt();

			totalGvnGrandTotal = totalGvnGrandTotal + saleList.get(i).getGvnGrandTotal();
			totalGvnTax = totalGvnTax + saleList.get(i).getGvnTotalTax();
			totalGvnTaxableAmt = totalGvnTaxableAmt + saleList.get(i).getGvnTaxableAmt();

			totalNetGrandTotal = totalNetGrandTotal + saleList.get(i).getNetGrandTotal();
			totalNetTax = totalNetTax + saleList.get(i).getNetTotalTax();
			totalNetTaxableAmt = totalNetTaxableAmt + saleList.get(i).getNetTaxableAmt();

			float contri = (saleList.get(i).getNetGrandTotal() * 100) / totalNetValForContri;
			totalContri = totalContri + contri;

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);

			rowData.add(saleList.get(i).getMonth());
			// rowData.add("" + saleList.get(i).getTaxableAmt());
			// rowData.add("" + saleList.get(i).getTotalTax());
			rowData.add("" + saleList.get(i).getGrandTotal());

			// rowData.add("" + saleList.get(i).getGrnTaxableAmt());
			// rowData.add("" + saleList.get(i).getGrnTotalTax());
			rowData.add("" + saleList.get(i).getGrnGrandTotal());

			// rowData.add("" + saleList.get(i).getGvnTaxableAmt());
			// rowData.add("" + saleList.get(i).getGvnTotalTax());
			rowData.add("" + saleList.get(i).getGvnGrandTotal());

			// rowData.add("" + saleList.get(i).getNetTaxableAmt());
			// rowData.add("" + saleList.get(i).getNetTotalTax());
			rowData.add("" + saleList.get(i).getNetGrandTotal());

			rowData.add("" + contri);

			float grnRet = 0;
			float gvnRet = 0;
			float sumRet = 0;

			if (saleList.get(i).getGrandTotal() > 0) {
				grnRet = (saleList.get(i).getGrnGrandTotal() * 100) / saleList.get(i).getGrandTotal();
				gvnRet = (saleList.get(i).getGvnGrandTotal() * 100) / saleList.get(i).getGrandTotal();
				sumRet = ((saleList.get(i).getGrnGrandTotal() + saleList.get(i).getGvnGrandTotal()) * 100)
						/ saleList.get(i).getGrandTotal();
			}

			rowData.add("" + grnRet);
			rowData.add("" + gvnRet);
			rowData.add("" + sumRet);

			srno = srno + 1;
			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("Total");

		rowData.add("");

		// rowData.add("" + roundUp(totalTax));
		// rowData.add("" + roundUp(totalTaxableAmt));
		rowData.add("" + roundUp(totalGrandTotal));

		// rowData.add("" + roundUp(totalGrnTaxableAmt));
		// rowData.add("" + roundUp(totalGrnTax));
		rowData.add("" + roundUp(totalGrnGrandTotal));

		// rowData.add("" + roundUp(totalGvnTaxableAmt));
		// rowData.add("" + roundUp(totalGvnTax));
		rowData.add("" + roundUp(totalGvnGrandTotal));

		// rowData.add("" + roundUp(totalNetTaxableAmt));
		// rowData.add("" + roundUp(totalNetTax));
		rowData.add("" + roundUp(totalNetGrandTotal));

		rowData.add("" + roundUp(totalContri));

		float grnRet = 0;
		float gvnRet = 0;
		float sumRet = 0;

		if (totalGrandTotal > 0) {
			grnRet = (totalGrnGrandTotal * 100) / totalGrandTotal;
			gvnRet = (totalGvnGrandTotal * 100) / totalGrandTotal;
			sumRet = ((totalGrnGrandTotal + totalGvnGrandTotal) * 100) / totalGrandTotal;
		}

		rowData.add("" + grnRet);
		rowData.add("" + gvnRet);
		rowData.add("" + sumRet);

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "MonthWiseSaleReport");
		session.setAttribute("reportNameNew", "Month Wise Sale Report Between " + fromDate + " To " + toDate);
		// session.setAttribute("searchByNew", "From Date: " + fromDate + " To Date: " +
		// toDate + " ");
		session.setAttribute("searchByNew", "Franchisee : " + frDisplay);
		session.setAttribute("mergeUpto1", "$A$1:$J$1");
		session.setAttribute("mergeUpto2", "$A$2:$J$2");

		return saleList;
	}

	@RequestMapping(value = "/getMonthWiseSaleForChart", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportDateMonth> getMonthWiseSaleForChart(HttpServletRequest request,
			HttpServletResponse response) {
		return saleList;
	}

	@RequestMapping(value = "pdf/showSaleBillwiseGrpByMonthPdf/{fromDate}/{toDate}/{selectedFr}/{routeId}", method = RequestMethod.GET)
	public ModelAndView showSaleBillwiseGrpByMonthPdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/billwisesalesgrpbymonthPdf");

		boolean isAllFrSelected = false;

		List<SalesReportDateMonth> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Bill Wise");

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

			Set<String> set = new HashSet<>();

			if (allFrIdNameList.getFrIdNamesList() != null) {

				for (int j = 0; j < frIds.size(); j++) {

					for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

						if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
							set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
							break;
						}

					}

				}
			}
			String string = String.join(", ", set);
			model.addObject("fr", string);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside else Few fr Selected ");

			map.add("frIdList", selectedFr);
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SalesReportDateMonth>> typeRef = new ParameterizedTypeReference<List<SalesReportDateMonth>>() {
			};
			ResponseEntity<List<SalesReportDateMonth>> responseEntity = restTemplate
					.exchange(Constants.url + "getMonthwiseReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			for (int i = 0; i < saleList.size(); i++) {

				float netGrandTotal = (saleList.get(i).getGrandTotal()
						- (saleList.get(i).getGrnGrandTotal() + saleList.get(i).getGvnGrandTotal()));
				float netTaxableAmt = (saleList.get(i).getTaxableAmt()
						- (saleList.get(i).getGrnTaxableAmt() + saleList.get(i).getGvnTaxableAmt()));

				float netTotalTax = (saleList.get(i).getTotalTax()
						- (saleList.get(i).getGrnTotalTax() + saleList.get(i).getGvnTotalTax()));
				saleList.get(i).setNetGrandTotal(netGrandTotal);
				saleList.get(i).setNetTaxableAmt(netTaxableAmt);
				saleList.get(i).setNetTotalTax(netTotalTax);
			}

			System.out.println("sales List Bill Wise " + saleList.toString());

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", saleList);
		return model;
	}

	// *******************************************************************//
	// Royalty Sale

	@RequestMapping(value = "/showSaleRoyaltyByCat", method = RequestMethod.GET)
	public ModelAndView showSaleRoyaltyByCat(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/salesroyaltybycat");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);
			model.addObject("royPer", getRoyPer());

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleRoyaltyByCat", method = RequestMethod.GET)
	public @ResponseBody RoyaltyListBean getSaleRoyaltyByCat(HttpServletRequest request, HttpServletResponse response) {

		List<SalesReportBillwise> saleList = new ArrayList<>();
		List<SalesReportRoyalty> royaltyList = new ArrayList<>();
		RoyaltyListBean royaltyBean = new RoyaltyListBean();

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			boolean isAllFrSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				// web serviece: getSalesReportRoyaltyAllFr

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyalty>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyalty>>() {
				};
				ResponseEntity<List<SalesReportRoyalty>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyAllFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyList = responseEntity.getBody();
				royaltyListForPdf = new ArrayList<>();

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyalty>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyalty>>() {
				};
				ResponseEntity<List<SalesReportRoyalty>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyalty", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyList = responseEntity.getBody();
				royaltyListForPdf = new ArrayList<>();
			}

			royaltyListForPdf = royaltyList;

			System.out.println("royaltyList List Bill Wise " + saleList.toString());

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;
			categoryList = categoryListResponse.getmCategoryList();
			// allFrIdNameList = new AllFrIdNameList();
			System.out.println("Category list  " + categoryList);

			royaltyBean.setCategoryList(categoryList);
			// royaltyBean.setSalesReportRoyalty(royaltyList);
			staticRoyaltyBean = royaltyBean;

		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by cat " + e.getMessage());
			e.printStackTrace();

		}
		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr.No.");
		rowData.add("Category Name");
		rowData.add("Item Name");
		rowData.add("Sale Qty");
		rowData.add("Sale Value");

		rowData.add("GRN Qty");
		rowData.add("GRN Value");
		rowData.add("GVN Qty");
		rowData.add("GVN Value");

		rowData.add("Net Qty");
		rowData.add("Net Value");
		rowData.add("Royalty %");
		rowData.add("Royalty Amt");
		expoExcel.setRowData(rowData);
		float royPer = getRoyPer();
		exportToExcelList.add(expoExcel);
		if (!royaltyBean.getSalesReportRoyalty().isEmpty()) {
			for (int i = 0; i < royaltyList.size(); i++) {
				int index = 1;
				index = index + i;
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add("" + index);
				rowData.add("" + royaltyList.get(i).getCat_name());

				rowData.add("" + royaltyList.get(i).getItem_name());

				rowData.add("" + roundUp(royaltyList.get(i).gettBillQty()));
				rowData.add("" + roundUp(royaltyList.get(i).gettBillTaxableAmt()));

				rowData.add("" + roundUp(royaltyList.get(i).gettGrnQty()));

				rowData.add("" + roundUp(royaltyList.get(i).gettGrnTaxableAmt()));
				rowData.add("" + roundUp(royaltyList.get(i).gettGvnQty()));
				rowData.add("" + roundUp(royaltyList.get(i).gettGvnTaxableAmt()));

				float netQty = royaltyList.get(i).gettBillQty()
						- (royaltyList.get(i).gettGrnQty() + royaltyList.get(i).gettGvnQty());

				float netValue = royaltyList.get(i).gettBillTaxableAmt()
						- (royaltyList.get(i).gettGrnTaxableAmt() + royaltyList.get(i).gettGvnTaxableAmt());

				float rAmt = netValue * royPer / 100;

				rowData.add("" + roundUp(netQty));
				rowData.add("" + roundUp(netValue));
				rowData.add("" + roundUp(royPer));
				rowData.add("" + roundUp(rAmt));

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}
		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "RoyaltyByCatList");

		return royaltyBean;

	}

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@RequestMapping(value = "pdf/showSaleRoyaltyByCatPdf/{fromDate}/{toDate}/{selectedFr}/{routeId}", method = RequestMethod.GET)
	public ModelAndView showSaleBil(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/salesroyaltybycatPdf");
		List<SalesReportBillwise> saleList = new ArrayList<>();
		List<SalesReportRoyalty> royaltyList = new ArrayList<>();
		RoyaltyListBean royaltyBean = new RoyaltyListBean();
		boolean isAllFrSelected = false;

		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			float royPer = getRoyPer();

			if (!routeId.equalsIgnoreCase("0")) {

				map = new LinkedMultiValueMap<String, Object>();
				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.equalsIgnoreCase("-1")) {
				isAllFrSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");
				// getSalesReportRoyaltyAllFr
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyalty>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyalty>>() {
				};
				ResponseEntity<List<SalesReportRoyalty>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyAllFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyList = responseEntity.getBody();

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyalty>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyalty>>() {
				};
				ResponseEntity<List<SalesReportRoyalty>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyalty", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyList = responseEntity.getBody();

			}
			royaltyListForPdf = new ArrayList<>();

			royaltyListForPdf = royaltyList;

			System.out.println("royaltyList List Bill Wise " + saleList.toString());

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;
			categoryList = categoryListResponse.getmCategoryList();
			// allFrIdNameList = new AllFrIdNameList();
			System.out.println("Category list  " + categoryList);

			royaltyBean.setCategoryList(categoryList);
			// royaltyBean.setSalesReportRoyalty(royaltyList);
			staticRoyaltyBean = royaltyBean;
			model.addObject("royPer", royPer);

		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by cat " + e.getMessage());
			e.printStackTrace();

		}

		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("catList", staticRoyaltyBean.getCategoryList());
		model.addObject("royaltyList", staticRoyaltyBean.getSalesReportRoyalty());
		return model;
	}

// royalty FR wise

	@RequestMapping(value = "/showSaleRoyaltyByFr", method = RequestMethod.GET)
	public ModelAndView showSaleRoyaltyByFr(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/salesroyaltybyfr");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());
			model.addObject("royPer", getRoyPer());
			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleRoyaltyByFr", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportRoyaltyFr> getSaleRoyaltyByFr(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			System.out.println("Inside get Sale royalty by fr");
			String selectedFr = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");
			int type = Integer.parseInt(request.getParameter("type"));

			boolean isAllFrSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected /getSaleRoyaltyByFr :getSalesReportRoyaltyFrAllFr");
				// Web Service :getSalesReportRoyaltyFrAllFr
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("type", type);
				ParameterizedTypeReference<List<SalesReportRoyaltyFr>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyaltyFr>>() {
				};
				ResponseEntity<List<SalesReportRoyaltyFr>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyFrAllFr", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				royaltyFrList = new ArrayList<>();
				royaltyFrList = responseEntity.getBody();

			} else {
				System.out.println("Inside else Few fr Selected /getSaleRoyaltyByFr :getSalesReportRoyaltyFr");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("type", type);
				ParameterizedTypeReference<List<SalesReportRoyaltyFr>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyaltyFr>>() {
				};
				ResponseEntity<List<SalesReportRoyaltyFr>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyFrList = new ArrayList<>();
				royaltyFrList = responseEntity.getBody();
			}
			// royaltyListForPdf=new ArrayList<>();

			// royaltyListForPdf=royaltyList;
			staticRoyaltyFrList = new ArrayList<>();

			staticRoyaltyFrList = royaltyFrList;
			System.out.println("royalty List List royaltyFr List " + royaltyFrList.toString());

			// allFrIdNameList = new AllFrIdNameList();

		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by cat " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();
		float royPer = getRoyPer();
		rowData.add("Sr No");
		rowData.add("Franchise Name");
		rowData.add("Franchise City");
		rowData.add("Sales Value");
		rowData.add("GRN Value");
		rowData.add("GVN Value");
		rowData.add("Net Value");
		rowData.add("Royalty %");
		rowData.add("Royalty Amount");

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < royaltyFrList.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + i + 1);
			rowData.add(royaltyFrList.get(i).getFrName());
			rowData.add(royaltyFrList.get(i).getFrCity());
			rowData.add("" + royaltyFrList.get(i).gettBillTaxableAmt());
			rowData.add("" + royaltyFrList.get(i).gettGrnTaxableAmt());
			rowData.add("" + royaltyFrList.get(i).gettGvnTaxableAmt());
			rowData.add("" + (royaltyFrList.get(i).gettBillTaxableAmt()
					- (royaltyFrList.get(i).gettGvnTaxableAmt() + royaltyFrList.get(i).gettGrnTaxableAmt())));
			rowData.add("" + royPer);
			float netValue = royaltyFrList.get(i).gettBillTaxableAmt()
					- (royaltyFrList.get(i).gettGrnTaxableAmt() + royaltyFrList.get(i).gettGvnTaxableAmt());

			float rAmt = netValue * royPer / 100;
			rowData.add("" + rAmt);

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "RoyaltyFrList");

		return royaltyFrList;

	}

// royalty fr pdf is not done

// done pdf
	@RequestMapping(value = "pdf/showSaleRoyaltyByFrPdf/{fromDate}/{toDate}/{selectedFr}/{routeId}", method = RequestMethod.GET)
	public ModelAndView showSaleRoyaltyByFrPdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/salesroyaltybyfrPdf");
		boolean isAllFrSelected = false;

		try {
			System.out.println("Inside get Sale royalty by fr");

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.equalsIgnoreCase("-1")) {
				isAllFrSelected = true;
			}
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyaltyFr>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyaltyFr>>() {
				};
				ResponseEntity<List<SalesReportRoyaltyFr>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyFrAllFr", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				royaltyFrList = new ArrayList<>();
				royaltyFrList = responseEntity.getBody();

				// getSalesReportRoyaltyFrAllFr

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportRoyaltyFr>> typeRef = new ParameterizedTypeReference<List<SalesReportRoyaltyFr>>() {
				};
				ResponseEntity<List<SalesReportRoyaltyFr>> responseEntity = restTemplate.exchange(
						Constants.url + "getSalesReportRoyaltyFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				royaltyFrList = new ArrayList<>();
				royaltyFrList = responseEntity.getBody();

			}
			// royaltyListForPdf=new ArrayList<>();

			// royaltyListForPdf=royaltyList;
			staticRoyaltyFrList = new ArrayList<>();

			staticRoyaltyFrList = royaltyFrList;
			System.out.println("royalty List List royaltyFr List " + royaltyFrList.toString());

			// allFrIdNameList = new AllFrIdNameList();

		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by Fr " + e.getMessage());
			e.printStackTrace();

		}
		model.addObject("royPer",

				getRoyPer());

		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", staticRoyaltyFrList);

		return model;
	}

	// report no 8
	@RequestMapping(value = "/showSaleReportItemwise", method = RequestMethod.GET)
	public ModelAndView showSaleReportItemwise(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/salesreportitemwise");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();
			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);

			mCategoryList = categoryListResponse.getmCategoryList();
			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());
			model.addObject("catList", mCategoryList);
			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleReportItemwise", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportItemwise> getSaleReportItemwise(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportItemwise> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Item  Wise");
			String selectedFr = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			int catId = Integer.parseInt(request.getParameter("catId"));

			boolean isAllFrSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			frList = new ArrayList<>();
			frList = Arrays.asList(selectedFr);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;

				// No frIds for Filter:it us based on Item Selection :
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catId", catId);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);
				map.add("catId", catId);

				ParameterizedTypeReference<List<SalesReportItemwise>> typeRef = new ParameterizedTypeReference<List<SalesReportItemwise>>() {
				};
				ResponseEntity<List<SalesReportItemwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportItemwise", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				staticSaleListItemWise = saleList;
				// saleListForPdf=saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			}
		} catch (Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr No");

		rowData.add("Item Name");
		rowData.add("Item Hsn Code");
		rowData.add("Item Tax Rate");
		rowData.add("Item Qty");
		rowData.add("Taxable Amount");
		rowData.add("CGST Amount");
		rowData.add("SGST Amount");
		rowData.add("IGST Amount");
		rowData.add("Total Tax Amount");
		rowData.add("Grand Total");

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		int srno = 1;
		for (int i = 0; i < saleList.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("" + srno);

			rowData.add("" + saleList.get(i).getItemName());
			rowData.add("" + saleList.get(i).getItemHsncd());
			rowData.add("" + (saleList.get(i).getItemTax1() + saleList.get(i).getItemTax2()));
			rowData.add("" + roundUp(saleList.get(i).getBillQtySum()));
			rowData.add("" + roundUp(saleList.get(i).getTaxableAmtSum()));
			rowData.add("" + roundUp(saleList.get(i).getCgstRsSum()));
			rowData.add("" + roundUp(saleList.get(i).getSgstRsSum()));
			rowData.add("" + roundUp(saleList.get(i).getIgstRsSum()));
			rowData.add("" + roundUp(saleList.get(i).getSgstRsSum() + saleList.get(i).getCgstRsSum()));
			rowData.add("" + roundUp(saleList.get(i).getSgstRsSum() + saleList.get(i).getCgstRsSum()
					+ saleList.get(i).getTaxableAmtSum()));

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "SaleReportItemWise");
		return saleList;

	}

	// pdf for r8 to be done
	// pdf for r8
	@RequestMapping(value = "pdf/showSaleReportItemwisePdf/{fromDate}/{toDate}/{selectedFr}/{routeId}", method = RequestMethod.GET)
	public ModelAndView showSaleReportItemwisePdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, HttpServletRequest request,
			HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/salesreportitemwisePdf");

		List<SalesReportItemwise> saleList = new ArrayList<>();
		boolean isAllFrSelected = false;

		try {
			System.out.println("Inside get Sale Item  Wise");

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.equalsIgnoreCase("-1")) {
				isAllFrSelected = true;
			}
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			if (isAllFrSelected) {

				System.out.println("Inside If all fr Selected ");

				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);
				map.add("fromDate", fromDate);
				map.add("toDate", toDate);

				ParameterizedTypeReference<List<SalesReportItemwise>> typeRef = new ParameterizedTypeReference<List<SalesReportItemwise>>() {
				};
				ResponseEntity<List<SalesReportItemwise>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleReportItemwise", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				saleList = responseEntity.getBody();
				saleListForPdf = new ArrayList<>();
				staticSaleListItemWise = saleList;
				// saleListForPdf=saleList;

				System.out.println("sales List Bill Wise " + saleList.toString());

			}
		} catch (Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", staticSaleListItemWise);

		return model;
	}

	// report 7
	@RequestMapping(value = "/showSaleReportBillwiseAllFr", method = RequestMethod.GET)
	public ModelAndView showSaleReportBillwiseAllFr(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/salesreportbillallfr");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			List<AllFrIdName> selectedFrListAll = new ArrayList();
			List<Menu> selectedMenuList = new ArrayList<Menu>();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	@RequestMapping(value = "/getSaleReportBillwiseAllFr", method = RequestMethod.GET)
	public @ResponseBody List<SalesReportBillwiseAllFr> getSaleReportBillwiseAllFr(HttpServletRequest request,
			HttpServletResponse response) {

		List<SalesReportBillwiseAllFr> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Item  Wise");
			// String selectedFr = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("fromDate");
			String toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");

			String selectedFr;

			// boolean isAllFrSelected = false;
			// selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			// selectedFr = selectedFr.replaceAll("\"", "");

			// frList = new ArrayList<>();
			// frList = Arrays.asList(selectedFr);

			/*
			 * if (!routeId.equalsIgnoreCase("0")) {
			 * 
			 * MultiValueMap<String, Object> map = new LinkedMultiValueMap<String,
			 * Object>();
			 * 
			 * RestTemplate restTemplate = new RestTemplate();
			 * 
			 * map.add("routeId", routeId);
			 * 
			 * FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url
			 * + "getFrNameIdByRouteId", map, FrNameIdByRouteIdResponse.class);
			 * 
			 * List<FrNameIdByRouteId> frNameIdByRouteIdList =
			 * frNameId.getFrNameIdByRouteIds();
			 * 
			 * System.out.println("route wise franchisee " +
			 * frNameIdByRouteIdList.toString());
			 * 
			 * StringBuilder sbForRouteFrId = new StringBuilder(); for (int i = 0; i <
			 * frNameIdByRouteIdList.size(); i++) {
			 * 
			 * sbForRouteFrId =
			 * sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() +
			 * ",");
			 * 
			 * }
			 * 
			 * String strFrIdRouteWise = sbForRouteFrId.toString(); selectedFr =
			 * strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			 * System.out.println("fr Id Route WISE = " + selectedFr);
			 * 
			 * } // end of if
			 * 
			 * if (frList.contains("-1")) { //isAllFrSelected = true; }
			 */

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SalesReportBillwiseAllFr>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwiseAllFr>>() {
			};
			ResponseEntity<List<SalesReportBillwiseAllFr>> responseEntity = restTemplate.exchange(
					Constants.url + "getSaleReportBillwiseAllFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();
			staticSaleByAllFr = new ArrayList<>();
			staticSaleByAllFr = saleList;
			// saleListForPdf=saleList;

			Map<Integer, SalesReportBillwiseAllFr> LHM = new LinkedHashMap<Integer, SalesReportBillwiseAllFr>();

			for (int i = 0; i < saleList.size(); i++) {
				if (LHM.containsKey(saleList.get(i).getBillNo()) == false) {
					LHM.put(saleList.get(i).getBillNo(), saleList.get(i));
				}

			}

			List<SalesReportBillwiseAllFr> frList = new ArrayList<>();

			for (Entry<Integer, SalesReportBillwiseAllFr> entry : LHM.entrySet()) {
				frList.add(entry.getValue());
			}

			float billAmt = 0;

			for (int i = 0; i < frList.size(); i++) {

				for (int j = 0; j < saleList.size(); j++) {

					if (frList.get(i).getBillNo() == saleList.get(j).getBillNo())
						billAmt = billAmt + saleList.get(j).getCgstRsSum() + saleList.get(j).getSgstRsSum()
								+ saleList.get(j).getTaxableAmtSum();
				}
				for (int k = 0; k < saleList.size(); k++) {
					if (frList.get(i).getBillNo() == saleList.get(k).getBillNo()) {
						saleList.get(k).setBillTotalAmt(billAmt);

					}

				}

			}

		} catch (Exception e) {
			System.out.println("get sale Report Bill Wise all Fr " + e.getMessage());
			e.printStackTrace();

		}

		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();
		rowData.add("Sr No.");
		rowData.add("Bill No");
		rowData.add("Invoice No");
		rowData.add("Bill Date");

		rowData.add("Franchisee Name");
		rowData.add("Franchisee City");
		rowData.add("Franchisee Gst No");
		rowData.add("Item Name");
		rowData.add("Item Hsn Code");
		rowData.add("SGST %");
		rowData.add("CGST %");
		rowData.add("IGST %");
		rowData.add("SGST sum");
		rowData.add("CGST sum");
		rowData.add("IGST sum");
		rowData.add("Taxable Amt");
		rowData.add("Bill Amt");

		/*
		 * float taxableAmtSum; float sgstRsSum; float cgstRsSum; float igstRsSum;
		 */
		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < saleList.size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();
			rowData.add("" + (i + 1));
			rowData.add("" + saleList.get(i).getBillNo());
			rowData.add(saleList.get(i).getInvoiceNo());
			rowData.add(saleList.get(i).getBillDate());

			rowData.add(saleList.get(i).getFrName());

			rowData.add(saleList.get(i).getFrCity());
			rowData.add(saleList.get(i).getFrGstNo());
			rowData.add("" + saleList.get(i).getItemName());
			rowData.add("" + saleList.get(i).getItemHsncd());
			rowData.add("" + saleList.get(i).getItemTax1());

			rowData.add("" + saleList.get(i).getItemTax2());
			rowData.add("" + saleList.get(i).getItemTax3());
			rowData.add("" + saleList.get(i).getSgstRsSum());
			rowData.add("" + saleList.get(i).getCgstRsSum());

			rowData.add("" + saleList.get(i).getIgstRsSum());
			rowData.add("" + saleList.get(i).getTaxableAmtSum());
			rowData.add("" + saleList.get(i).getBillTotalAmt());

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "BillWiseAllFr");
		return saleList;
	}
	// pdf to be done

	// pdf report 7
	@RequestMapping(value = "pdf/showSaleReportBillwiseAllFrPdf/{fromDate}/{toDate}", method = RequestMethod.GET)
	public ModelAndView showSaleReportBillwiseAllFrPdf(@PathVariable String fromDate, @PathVariable String toDate,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/sales/pdf/salereportbillallfrPdf");

		List<SalesReportBillwiseAllFr> saleList = new ArrayList<>();

		try {
			System.out.println("Inside get Sale Item  Wise");

			String selectedFr;

			// boolean isAllFrSelected = false;
			// selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			// selectedFr = selectedFr.replaceAll("\"", "");

			// frList = new ArrayList<>();
			// frList = Arrays.asList(selectedFr);

			/*
			 * if (!routeId.equalsIgnoreCase("0")) {
			 * 
			 * MultiValueMap<String, Object> map = new LinkedMultiValueMap<String,
			 * Object>();
			 * 
			 * RestTemplate restTemplate = new RestTemplate();
			 * 
			 * map.add("routeId", routeId);
			 * 
			 * FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url
			 * + "getFrNameIdByRouteId", map, FrNameIdByRouteIdResponse.class);
			 * 
			 * List<FrNameIdByRouteId> frNameIdByRouteIdList =
			 * frNameId.getFrNameIdByRouteIds();
			 * 
			 * System.out.println("route wise franchisee " +
			 * frNameIdByRouteIdList.toString());
			 * 
			 * StringBuilder sbForRouteFrId = new StringBuilder(); for (int i = 0; i <
			 * frNameIdByRouteIdList.size(); i++) {
			 * 
			 * sbForRouteFrId =
			 * sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() +
			 * ",");
			 * 
			 * }
			 * 
			 * String strFrIdRouteWise = sbForRouteFrId.toString(); selectedFr =
			 * strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			 * System.out.println("fr Id Route WISE = " + selectedFr);
			 * 
			 * } // end of if
			 * 
			 * if (frList.contains("-1")) { //isAllFrSelected = true; }
			 */

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside else Few fr Selected ");

			// map.add("frIdList", selectedFr);
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SalesReportBillwiseAllFr>> typeRef = new ParameterizedTypeReference<List<SalesReportBillwiseAllFr>>() {
			};
			ResponseEntity<List<SalesReportBillwiseAllFr>> responseEntity = restTemplate.exchange(
					Constants.url + "getSaleReportBillwiseAllFr", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();
			staticSaleByAllFr = new ArrayList<>();
			staticSaleByAllFr = saleList;
			// saleListForPdf=saleList;

			System.out.println("sales List Bill Wise all fr  " + saleList.toString());

		} catch (Exception e) {
			System.out.println("get sale Report Bill Wise all Fr " + e.getMessage());
			e.printStackTrace();

		}
		model.addObject("fromDate", fromDate);

		model.addObject("toDate", toDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", staticSaleByAllFr);

		return model;
	}

	// report no 10 conso by category report

	List<MCategoryList> categoryListAjax;

	@RequestMapping(value = "/getAllCatAjax", method = RequestMethod.GET)
	public @ResponseBody List<MCategoryList> getAllCatAjax(HttpServletRequest request, HttpServletResponse response) {
		return categoryListAjax;
	}

	@RequestMapping(value = "/showSaleReportRoyConsoByCat", method = RequestMethod.GET)
	public ModelAndView showSaleReportRoyConsoByCat(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		model = new ModelAndView("reports/sales/salesconsbycat");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			// List<MCategoryList> categoryList;
			categoryListAjax = categoryListResponse.getmCategoryList();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("catList", categoryListAjax);

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);
			model.addObject("royPer", getRoyPer());

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}
		return model;

	}

	// ----------------------------Show Dispatch Item
	// List-----------------------------
	@RequestMapping(value = "/showDispatchItemReport", method = RequestMethod.GET)
	public ModelAndView showDispatchItemReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/dispatchReport");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;
			categoryList = categoryListResponse.getmCategoryList();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("catList", categoryList);

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());
			model.addObject("FACTORYNAME", Constants.FACTORYNAME);
			model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	// ---------------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/showPDispatchItemReport", method = RequestMethod.GET)
	public ModelAndView showPDispatchItemReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/pDispatchReport");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			// get Routes

			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();

			// end get Routes

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;
			categoryList = categoryListResponse.getmCategoryList();

			System.out.println(" Fr " + allFrIdNameList.getFrIdNamesList());

			model.addObject("catList", categoryList);

			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			model.addObject("routeList", routeList);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}

	// ---------------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/getDispatchReportByRoute", method = RequestMethod.GET)
	public @ResponseBody DispatchReportList getDispatchReportByRoute(HttpServletRequest request,
			HttpServletResponse response) {

		List<DispatchReport> dispatchReportList = new ArrayList<DispatchReport>();
		DispatchReportList dispatchReports = new DispatchReportList();
		try {
			System.out.println("Inside get Dispatch Report");
			String billDate = request.getParameter("bill_date");
			String routeId = request.getParameter("route_id");
			String selectedCat = request.getParameter("cat_id_list");

			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				selectedCat = selectedCat.replaceAll("\"", "");
				System.out.println("selectedCat" + selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			map.add("routeId", routeId);

			FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId", map,
					FrNameIdByRouteIdResponse.class);

			List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();
				// List<Integer> cateList=new ArrayList<>();
				StringBuilder cateList = new StringBuilder();

				for (MCategoryList mCategoryList : categoryList) {
					// cateList.add(mCategoryList.getCatId());
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
				}

				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				System.out.println("cateList" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());
				System.out.println("billDate" + billDate.toString());

				map.add("categories", selectedCat);
				map.add("billDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<DispatchReport>> typeRef = new ParameterizedTypeReference<List<DispatchReport>>() {
				};

				ResponseEntity<List<DispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);
				System.out.println("Items:" + responseEntity1.toString());
				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));
				System.out.println("subCatAList:" + subCatAList.toString());
				dispatchReports.setDispatchReportList(dispatchReportList);
				dispatchReports.setFrList(frNameIdByRouteIdList);
				dispatchReports.setItemList(responseEntity1.getBody());
				dispatchReports.setSubCatList(subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("billDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<DispatchReport>> typeRef = new ParameterizedTypeReference<List<DispatchReport>>() {
				};

				ResponseEntity<List<DispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);
				System.out.println("Items:" + responseEntity.toString());

				dispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				dispatchReports.setDispatchReportList(dispatchReportList);
				dispatchReports.setFrList(frNameIdByRouteIdList);
				dispatchReports.setItemList(responseEntity1.getBody());
				dispatchReports.setSubCatList(responseEntity2.getBody());
			}

		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}

		return dispatchReports;

	}

	List<PDispatchReport> pDispatchReportList = null;

	@RequestMapping(value = "/getPDispatchReportByRoute", method = RequestMethod.GET)
	public @ResponseBody PDispatchReportList getPDispatchReportByRoute(HttpServletRequest request,
			HttpServletResponse response) {

		List<PDispatchReport> dispatchReportList = new ArrayList<PDispatchReport>();
		PDispatchReportList dispatchReports = new PDispatchReportList();
		try {
			System.out.println("Inside get Dispatch Report");
			String billDate = request.getParameter("bill_date");
			String routeId = request.getParameter("route_id");
			String selectedCat = request.getParameter("cat_id_list");

			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				selectedCat = selectedCat.replaceAll("\"", "");
				System.out.println("selectedCat" + selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			map.add("routeId", routeId);

			FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId", map,
					FrNameIdByRouteIdResponse.class);

			List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();
				// List<Integer> cateList=new ArrayList<>();
				StringBuilder cateList = new StringBuilder();

				for (MCategoryList mCategoryList : categoryList) {
					// cateList.add(mCategoryList.getCatId());
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
				}

				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				System.out.println("cateList" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());
				System.out.println("productionDate" + billDate.toString());

				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				pDispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);
				System.out.println("Items:" + responseEntity1.toString());
				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));
				System.out.println("subCatAList:" + subCatAList.toString());
				dispatchReports.setDispatchReportList(dispatchReportList);
				dispatchReports.setFrList(frNameIdByRouteIdList);
				dispatchReports.setItemList(responseEntity1.getBody());
				dispatchReports.setSubCatList(subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);
				System.out.println("Items:" + responseEntity.toString());

				dispatchReportList = responseEntity.getBody();
				pDispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				dispatchReports.setDispatchReportList(dispatchReportList);
				dispatchReports.setFrList(frNameIdByRouteIdList);
				dispatchReports.setItemList(responseEntity1.getBody());
				dispatchReports.setSubCatList(responseEntity2.getBody());
			}

		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}

		return dispatchReports;

	}

	@RequestMapping(value = "/submitEditedQty", method = RequestMethod.POST)
	public String submitEditedQty(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/pDispatchReport");
		try {
			System.err.println("#####################pDispatchReportList##########################"
					+ pDispatchReportList.toString());

			if (!pDispatchReportList.isEmpty()) {
				List<POrder> orderList = new ArrayList<>();

				for (int i = 0; i < pDispatchReportList.size(); i++) {
					POrder order = null;
					try {
						int editedQty = Integer.parseInt(request.getParameter("itemQty"
								+ pDispatchReportList.get(i).getFrId() + "" + pDispatchReportList.get(i).getItemId()
								+ "" + pDispatchReportList.get(i).getOrderId()));
						order = new POrder();
						order.setOrderId(pDispatchReportList.get(i).getOrderId());
						order.setEditQty(editedQty);
					} catch (Exception e) {
						System.err.println("orderQty:" + pDispatchReportList.get(i).getFrId() + ""
								+ pDispatchReportList.get(i).getItemId() + ""
								+ pDispatchReportList.get(i).getOrderId());
						e.printStackTrace();
					}
					if (order != null) {
						orderList.add(order);
					}
				}
				RestTemplate restTemplate = new RestTemplate();
				List<POrder> orderLists = restTemplate.postForObject(Constants.url + "updateEditedQty", orderList,
						List.class);
				System.err.println("orderLists" + orderLists.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/showPDispatchItemReport";
	}

	@RequestMapping(value = "/getFranchiseByRoute", method = RequestMethod.GET)
	public @ResponseBody List<FranchiseForDispatch> getFranchiseByRoute(
			@RequestParam(value = "routeId", required = true) int routeId) {
		RestTemplate restTemplate = new RestTemplate();

		List<FranchiseForDispatch> frNameIdByRouteIdList = null;
		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			System.err.println(routeId);
			map.add("routeId", routeId);

			FranchiseForDispatch[] frNameId = restTemplate.postForObject(Constants.url + "getFranchiseForDispatch", map,
					FranchiseForDispatch[].class);

			frNameIdByRouteIdList = new ArrayList<>(Arrays.asList(frNameId));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return frNameIdByRouteIdList;
	}

	@RequestMapping(value = "pdf/getDispatchReportPdf/{billDate}/{routeId}/{selectedCat}", method = RequestMethod.GET)
	public ModelAndView getSaleReportRoyConsoByCat(@PathVariable String billDate, @PathVariable String routeId,
			@PathVariable String selectedCat, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/dispatchReportPdf");
		RestTemplate restTemplate = new RestTemplate();

		List<DispatchReport> dispatchReportList = new ArrayList<DispatchReport>();
		DispatchReportList dispatchReports = new DispatchReportList();
		try {

			System.out.println("Inside get Dispatch Report");
			// String billDate = request.getParameter("bill_date");
			// String routeId = request.getParameter("route_id");
			// String selectedCat=request.getParameter("cat_id_list");
			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();
			String routeName = "def";
			for (int i = 0; i < routeList.size(); i++) {

				if (routeList.get(i).getRouteId() == Integer.parseInt(routeId)) {
					routeName = routeList.get(i).getRouteName();
					break;

				}
			}
			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				// selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				// selectedCat = selectedCat.replaceAll("\"", "");
				// System.out.println("selectedCat"+selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("routeId", routeId);

			FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId", map,
					FrNameIdByRouteIdResponse.class);

			List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();

				StringBuilder cateList = new StringBuilder();
				// List<String> cateList = new ArrayList<>();
				for (MCategoryList mCategoryList : categoryList) {
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
					// cateList.add("" + mCategoryList.getCatId());
				}
				System.err.println(cateList);
				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				map.add("categories", selectedCat);
				map.add("billDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<DispatchReport>> typeRef = new ParameterizedTypeReference<List<DispatchReport>>() {
				};

				ResponseEntity<List<DispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));

				/*
				 * if(!dispatchReportList.isEmpty()&&!responseEntity1.getBody().isEmpty()&&!
				 * frNameIdByRouteIdList.isEmpty()) { for(int
				 * j=0;j<responseEntity1.getBody().size();j++) { for(int
				 * i=0;i<frNameIdByRouteIdList.size();i++) { boolean flag=false; for(int
				 * k=0;k<dispatchReportList.size();k++) {
				 * if(dispatchReportList.get(k).getFrId()==frNameIdByRouteIdList.get(i).getFrId(
				 * ) &&
				 * dispatchReportList.get(k).getItemId()==responseEntity1.getBody().get(j).getId
				 * ()) { flag=true; break;
				 * 
				 * } } if(flag==false) { DispatchReport dispachReport=new DispatchReport();
				 * dispachReport.setBillDetailNo(0);
				 * dispachReport.setFrId(frNameIdByRouteIdList.get(i).getFrId());
				 * dispachReport.setItemId(responseEntity1.getBody().get(j).getId());
				 * dispachReport.setBillQty(0); dispatchReportList.add(dispachReport); } } } }
				 */
				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("billDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<DispatchReport>> typeRef = new ParameterizedTypeReference<List<DispatchReport>>() {
				};

				ResponseEntity<List<DispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.out.println("############################dispatchReportList######################## = "
						+ dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				/*
				 * if(!dispatchReportList.isEmpty()&&!responseEntity1.getBody().isEmpty()&&!
				 * frNameIdByRouteIdList.isEmpty()) { for(int
				 * j=0;j<responseEntity1.getBody().size();j++) { for(int
				 * i=0;i<frNameIdByRouteIdList.size();i++) { boolean flag=false; for(int
				 * k=0;k<dispatchReportList.size();k++) {
				 * if(dispatchReportList.get(k).getFrId()==frNameIdByRouteIdList.get(i).getFrId(
				 * ) &&
				 * dispatchReportList.get(k).getItemId()==responseEntity1.getBody().get(j).getId
				 * ()) { flag=true; break;
				 * 
				 * } } if(flag==false) { DispatchReport dispachReport=new DispatchReport();
				 * dispachReport.setBillDetailNo(0);
				 * dispachReport.setFrId(frNameIdByRouteIdList.get(i).getFrId());
				 * dispachReport.setItemId(responseEntity1.getBody().get(j).getId());
				 * dispachReport.setBillQty(0); dispatchReportList.add(dispachReport); } } } }
				 */

				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", responseEntity2.getBody());
			}
			model.addObject("routeName", routeName);

		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}
		return model;

	}

	@RequestMapping(value = "pdf/getPDispatchReportPdf/{billDate}/{routeId}/{selectedCat}", method = RequestMethod.GET)
	public ModelAndView getPDispatchReportPdf(@PathVariable String billDate, @PathVariable String routeId,
			@PathVariable String selectedCat, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/dispatchPReportPdf");
		RestTemplate restTemplate = new RestTemplate();

		List<PDispatchReport> dispatchReportList = new ArrayList<PDispatchReport>();
		PDispatchReportList dispatchReports = new PDispatchReportList();
		try {
			String convertedDate = "";
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormat.parse(billDate));
				cal.add(Calendar.DATE, 1);
				convertedDate = dateFormat.format(cal.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Inside get Dispatch Report");
			// String billDate = request.getParameter("bill_date");
			// String routeId = request.getParameter("route_id");
			// String selectedCat=request.getParameter("cat_id_list");
			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();
			String routeName = "def";
			for (int i = 0; i < routeList.size(); i++) {

				if (routeList.get(i).getRouteId() == Integer.parseInt(routeId)) {
					routeName = routeList.get(i).getRouteName();
					break;

				}
			}
			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				// selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				// selectedCat = selectedCat.replaceAll("\"", "");
				// System.out.println("selectedCat"+selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("routeId", routeId);

			FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId", map,
					FrNameIdByRouteIdResponse.class);

			List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();

				StringBuilder cateList = new StringBuilder();
				// List<String> cateList = new ArrayList<>();
				for (MCategoryList mCategoryList : categoryList) {
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
					// cateList.add("" + mCategoryList.getCatId());
				}
				System.err.println(cateList);
				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));

				/*
				 * if(!dispatchReportList.isEmpty()&&!responseEntity1.getBody().isEmpty()&&!
				 * frNameIdByRouteIdList.isEmpty()) { for(int
				 * j=0;j<responseEntity1.getBody().size();j++) { for(int
				 * i=0;i<frNameIdByRouteIdList.size();i++) { boolean flag=false; for(int
				 * k=0;k<dispatchReportList.size();k++) {
				 * if(dispatchReportList.get(k).getFrId()==frNameIdByRouteIdList.get(i).getFrId(
				 * ) &&
				 * dispatchReportList.get(k).getItemId()==responseEntity1.getBody().get(j).getId
				 * ()) { flag=true; break;
				 * 
				 * } } if(flag==false) { DispatchReport dispachReport=new DispatchReport();
				 * dispachReport.setBillDetailNo(0);
				 * dispachReport.setFrId(frNameIdByRouteIdList.get(i).getFrId());
				 * dispachReport.setItemId(responseEntity1.getBody().get(j).getId());
				 * dispachReport.setBillQty(0); dispatchReportList.add(dispachReport); } } } }
				 */
				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.out.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", responseEntity2.getBody());
			}
			model.addObject("routeName", routeName);
			model.addObject("convertedDate", convertedDate);
		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}
		return model;

	}

	@RequestMapping(value = "pdf/getDispatchPReportPdfForBill/{billDate}/{routeId}/{selectedCat}/{frId}", method = RequestMethod.GET)
	public ModelAndView getDispatchPReportPdfForBill(@PathVariable String billDate, @PathVariable String routeId,
			@PathVariable String selectedCat, @PathVariable int frId, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/dispatchReportPPdfBill");/* dispatchReportPPdfBill */
		RestTemplate restTemplate = new RestTemplate();

		List<PDispatchReport> dispatchReportList = new ArrayList<PDispatchReport>();
		PDispatchReportList dispatchReports = new PDispatchReportList();
		try {
			String convertedDate = "";
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormat.parse(billDate));
				cal.add(Calendar.DATE, 1);
				convertedDate = dateFormat.format(cal.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Inside get Dispatch Report");
			// String billDate = request.getParameter("bill_date");
			// String routeId = request.getParameter("route_id");
			// String selectedCat=request.getParameter("cat_id_list");
			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();
			String routeName = "def";
			for (int i = 0; i < routeList.size(); i++) {

				if (routeList.get(i).getRouteId() == Integer.parseInt(routeId)) {
					routeName = routeList.get(i).getRouteName();
					break;

				}
			}
			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				// selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				// selectedCat = selectedCat.replaceAll("\"", "");
				// System.out.println("selectedCat"+selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("routeId", routeId);

			FranchiseForDispatch[] frNameId = restTemplate.postForObject(Constants.url + "getFranchiseForDispatch", map,
					FranchiseForDispatch[].class);

			List<FranchiseForDispatch> frNameIdByRouteIdList = new ArrayList<>(Arrays.asList(frNameId));

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();

				StringBuilder cateList = new StringBuilder();
				// List<String> cateList = new ArrayList<>();
				for (MCategoryList mCategoryList : categoryList) {
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
					// cateList.add("" + mCategoryList.getCatId());
				}
				System.err.println(cateList);
				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.err.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));

				/*
				 * if(!dispatchReportList.isEmpty()&&!responseEntity1.getBody().isEmpty()&&!
				 * frNameIdByRouteIdList.isEmpty()) { for(int
				 * j=0;j<responseEntity1.getBody().size();j++) { for(int
				 * i=0;i<frNameIdByRouteIdList.size();i++) { boolean flag=false; for(int
				 * k=0;k<dispatchReportList.size();k++) {
				 * if(dispatchReportList.get(k).getFrId()==frNameIdByRouteIdList.get(i).getFrId(
				 * ) &&
				 * dispatchReportList.get(k).getItemId()==responseEntity1.getBody().get(j).getId
				 * ()) { flag=true; break;
				 * 
				 * } } if(flag==false) { DispatchReport dispachReport=new DispatchReport();
				 * dispachReport.setBillDetailNo(0);
				 * dispachReport.setFrId(frNameIdByRouteIdList.get(i).getFrId());
				 * dispachReport.setItemId(responseEntity1.getBody().get(j).getId());
				 * dispachReport.setBillQty(0); dispatchReportList.add(dispachReport); } } } }
				 */
				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.err.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", responseEntity2.getBody());
			}
			model.addObject("routeName", routeName);
			model.addObject("frId", frId);
			model.addObject("convertedDate", convertedDate);
		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}
		return model;

	}

	@RequestMapping(value = "pdf/getDispatchPReportPdfForDispatch/{billDate}/{routeId}/{selectedCat}/{frId}", method = RequestMethod.GET)
	public ModelAndView getDispatchPReportPdfForDispatch(@PathVariable String billDate, @PathVariable String routeId,
			@PathVariable String selectedCat, @PathVariable int frId, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/dispatchMini");/* dispatchReportPPdfBill */
		RestTemplate restTemplate = new RestTemplate();

		List<PDispatchReport> dispatchReportList = new ArrayList<PDispatchReport>();
		PDispatchReportList dispatchReports = new PDispatchReportList();
		try {
			String convertedDate = "";
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				Calendar cal = Calendar.getInstance();
				cal.setTime(dateFormat.parse(billDate));
				cal.add(Calendar.DATE, 1);
				convertedDate = dateFormat.format(cal.getTime());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Inside get Dispatch Report");
			// String billDate = request.getParameter("bill_date");
			// String routeId = request.getParameter("route_id");
			// String selectedCat=request.getParameter("cat_id_list");
			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();
			String routeName = "def";
			for (int i = 0; i < routeList.size(); i++) {

				if (routeList.get(i).getRouteId() == Integer.parseInt(routeId)) {
					routeName = routeList.get(i).getRouteName();
					break;

				}
			}
			boolean isAllCatSelected = false;
			String selectedFr = null;

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				// selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				// selectedCat = selectedCat.replaceAll("\"", "");
				// System.out.println("selectedCat"+selectedCat.toString());
			}
			List<String> catList = new ArrayList<>();
			catList = Arrays.asList(selectedCat);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map.add("routeId", routeId);

			FranchiseForDispatch[] frNameId = restTemplate.postForObject(Constants.url + "getFranchiseForDispatch", map,
					FranchiseForDispatch[].class);

			List<FranchiseForDispatch> frNameIdByRouteIdList = new ArrayList<>(Arrays.asList(frNameId));

			System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

			StringBuilder sbForRouteFrId = new StringBuilder();
			for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

				sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId() + ",");

			}

			String strFrIdRouteWise = sbForRouteFrId.toString();
			selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
			System.out.println("fr Id Route WISE = " + selectedFr);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			map = new LinkedMultiValueMap<String, Object>();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}

			if (isAllCatSelected) {
				map = new LinkedMultiValueMap<String, Object>();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList = categoryListResponse.getmCategoryList();

				StringBuilder cateList = new StringBuilder();
				// List<String> cateList = new ArrayList<>();
				for (MCategoryList mCategoryList : categoryList) {
					cateList = cateList.append(mCategoryList.getCatId().toString() + ",");
					// cateList.add("" + mCategoryList.getCatId());
				}
				System.err.println(cateList);
				String catlist = cateList.toString();
				selectedCat = catlist.substring(0, catlist.length() - 1);
				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.err.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				SubCategory[] subCatList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				ArrayList<SubCategory> subCatAList = new ArrayList<SubCategory>(Arrays.asList(subCatList));

				/*
				 * if(!dispatchReportList.isEmpty()&&!responseEntity1.getBody().isEmpty()&&!
				 * frNameIdByRouteIdList.isEmpty()) { for(int
				 * j=0;j<responseEntity1.getBody().size();j++) { for(int
				 * i=0;i<frNameIdByRouteIdList.size();i++) { boolean flag=false; for(int
				 * k=0;k<dispatchReportList.size();k++) {
				 * if(dispatchReportList.get(k).getFrId()==frNameIdByRouteIdList.get(i).getFrId(
				 * ) &&
				 * dispatchReportList.get(k).getItemId()==responseEntity1.getBody().get(j).getId
				 * ()) { flag=true; break;
				 * 
				 * } } if(flag==false) { DispatchReport dispachReport=new DispatchReport();
				 * dispachReport.setBillDetailNo(0);
				 * dispachReport.setFrId(frNameIdByRouteIdList.get(i).getFrId());
				 * dispachReport.setItemId(responseEntity1.getBody().get(j).getId());
				 * dispachReport.setBillQty(0); dispatchReportList.add(dispachReport); } } } }
				 */
				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", subCatAList);

			} else {
				System.out.println("selectedCat" + selectedCat.toString());
				System.out.println("selectedFr" + selectedFr.toString());

				map.add("categories", selectedCat);
				map.add("productionDate", billDate);
				map.add("frId", selectedFr);

				ParameterizedTypeReference<List<PDispatchReport>> typeRef = new ParameterizedTypeReference<List<PDispatchReport>>() {
				};

				ResponseEntity<List<PDispatchReport>> responseEntity = restTemplate.exchange(
						Constants.url + "getPDispatchItemReport", HttpMethod.POST, new HttpEntity<>(map), typeRef);

				dispatchReportList = responseEntity.getBody();
				System.err.println("dispatchReportList = " + dispatchReportList.toString());

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catIdList", selectedCat);
				ParameterizedTypeReference<List<Item>> typeRef1 = new ParameterizedTypeReference<List<Item>>() {
				};

				ResponseEntity<List<Item>> responseEntity1 = restTemplate.exchange(
						Constants.url + "getItemsByCatIdForDisp", HttpMethod.POST, new HttpEntity<>(map), typeRef1);

				map = new LinkedMultiValueMap<String, Object>();
				map.add("catId", selectedCat);
				ParameterizedTypeReference<List<SubCategory>> typeRef2 = new ParameterizedTypeReference<List<SubCategory>>() {
				};

				ResponseEntity<List<SubCategory>> responseEntity2 = restTemplate
						.exchange(Constants.url + "getSubCatList", HttpMethod.POST, new HttpEntity<>(map), typeRef2);

				model.addObject("dispatchReportList", dispatchReportList);
				model.addObject("frList", frNameIdByRouteIdList);
				model.addObject("itemList", responseEntity1.getBody());
				model.addObject("subCatList", responseEntity2.getBody());
			}
			model.addObject("routeName", routeName);
			model.addObject("frId", frId);
			model.addObject("convertedDate", convertedDate);
		} catch (Exception e) {
			System.out.println("get Dispatch Report Exception: " + e.getMessage());
			e.printStackTrace();

		}
		return model;

	}

// --------------------------------------------------------------------------------------------------
	@RequestMapping(value = "/getSaleReportRoyConsoByCat", method = RequestMethod.GET)
	public @ResponseBody RoyaltyListBean getSaleReportRoyConsoByCat(HttpServletRequest request,
			HttpServletResponse response) {
		String fromDate = "";
		String toDate = "";
		List<SalesReportBillwise> saleList = new ArrayList<>();
		List<SalesRoyaltyConsByCat> royaltyList = new ArrayList<>();
		RoyaltyListBean royaltyBean = new RoyaltyListBean();
		int getBy = 1;
		int type = 1;
		List<String> catList = new ArrayList<>();
		String frDisplay = "";

		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			String routeId = request.getParameter("route_id");
			getBy = Integer.parseInt(request.getParameter("getBy"));
			type = Integer.parseInt(request.getParameter("type"));
			int isGraph = Integer.parseInt(request.getParameter("is_graph"));

			String selectedCat = request.getParameter("cat_id_list");

			boolean isAllFrSelected = false;
			boolean isAllCatSelected = false;
			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			// frList = new ArrayList<>();
			// frList = Arrays.asList(selectedFr);

			frList = Stream.of(selectedFr.split(",")).collect(Collectors.toList());

			selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
			selectedCat = selectedCat.replaceAll("\"", "");

			catList.clear();
			catList = Stream.of(selectedCat.split(",")).collect(Collectors.toList());

			// List<String> catList = new ArrayList<>();
			// catList = Arrays.asList(selectedCat);

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (frList.contains("-1")) {
				isAllFrSelected = true;
			}

			if (catList.contains("-1")) {
				isAllCatSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			// new code

			// few fr Selected

			map.add("catIdList", selectedCat);

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			map.add("frIdList", selectedFr);
			map.add("getBy", getBy);
			map.add("type", type);

			ParameterizedTypeReference<List<SalesRoyaltyConsByCat>> typeRef = new ParameterizedTypeReference<List<SalesRoyaltyConsByCat>>() {
			};

			ResponseEntity<List<SalesRoyaltyConsByCat>> responseEntity = restTemplate.exchange(
					Constants.url + "getSaleRoyConsoByCatReportData", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			royaltyList = responseEntity.getBody();
			royaltyConsByCatListForPdf = new ArrayList<>();

			royaltyConsByCatListForPdf = royaltyList;

			System.out.println("royaltyList List Bill Wise " + royaltyList.toString());

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;

			categoryList = categoryListResponse.getmCategoryList();
			// allFrIdNameList = new AllFrIdNameList();
			System.out.println("Category list  " + categoryList);
			List<MCategoryList> tempList = new ArrayList<>();

			// royaltyBean.setCategoryList(categoryList);
			Map<Integer, String> catNameId = new HashMap<Integer, String>();

			for (int i = 0; i < categoryList.size(); i++) {

				for (int j = 0; j < royaltyList.size(); j++) {

					if (categoryList.get(i).getCatId() == royaltyList.get(j).getCatId()) {
						catNameId.put(categoryList.get(i).getCatId(), categoryList.get(i).getCatName());

						if (!tempList.contains(categoryList.get(i))) {

							tempList.add(categoryList.get(i));

						}
					}

				}
			}
			System.out.println("temp list " + categoryList.toString() + "size of t List " + categoryList.size());
			royaltyBean.setCategoryList(tempList);
			royaltyBean.setSalesReportRoyalty(royaltyList);
			staticRoyaltyBean = royaltyBean;

			List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt).collect(Collectors.toList());

			Set<String> set = new HashSet<>();

			if (allFrIdNameList.getFrIdNamesList() != null) {

				for (int j = 0; j < frIds.size(); j++) {

					for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

						if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
							set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
							break;
						}

					}

				}
			}
			frDisplay = String.join(", ", set);

		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by cat " + e.getMessage());
			e.printStackTrace();

		}
		// exportToExcel
		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr.No.");
		rowData.add("Category Name");
		rowData.add("Item Name");
		rowData.add("Sale Qty");
		rowData.add("Sale Value");

		rowData.add("GRN Qty");
		rowData.add("GRN Value");
		rowData.add("GVN Qty");
		rowData.add("GVN Value");

		rowData.add("Net Qty");
		rowData.add("Net Value");

		rowData.add("Royalty %");
		rowData.add("Royalty Amt");

		rowData.add("Return % GRN");
		rowData.add("Return % GVN");
		rowData.add("Return % SUM");

		rowData.add("Contribution %");

		float royPer = getRoyPer();
		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		float saleQty = 0.0f;
		float saleValue = 0.0f;
		float grnQty = 0.0f;
		float grnValue = 0.0f;
		float gvnQty = 0.0f;
		float gvnValue = 0.0f;
		float netQtyTotal = 0.0f;
		float netValueTotal = 0.0f;
		float rAmtTotal = 0.0f;
		float contriTotal = 0.0f;

		float netValTotalForContri = 0.0f;

		if (!royaltyBean.getSalesReportRoyalty().isEmpty()) {

			for (int i = 0; i < royaltyList.size(); i++) {

				float billVal = 0, grnQty1 = 0, grnVal = 0, gvnQty1 = 0, gvnVal = 0;

				if (getBy == 1) {
					billVal = royaltyList.get(i).getTaxableAmt();

					if (type == 1) {
						grnVal = royaltyList.get(i).getGrnTaxableAmt();
						gvnVal = royaltyList.get(i).getGvnTaxableAmt();

					} else {
						grnVal = royaltyList.get(i).getCrnGrnTaxableAmt();
						gvnVal = royaltyList.get(i).getCrnGvnTaxableAmt();

					}
				} else {
					billVal = royaltyList.get(i).getGrandTotal();

					if (type == 1) {
						grnVal = royaltyList.get(i).getGrnGrandTotal();
						gvnVal = royaltyList.get(i).getGvnGrandTotal();

					} else {
						grnVal = royaltyList.get(i).getCrnGrnGrandTotal();
						gvnVal = royaltyList.get(i).getCrnGvnGrandTotal();

					}

				}

				float netValue = billVal - (grnVal + gvnVal);

				netValTotalForContri = netValTotalForContri + netValue;

			}

			for (int c = 0; c < catList.size(); c++) {

				int sr = 1;

				float saleQtyCat = 0.0f;
				float saleValueCat = 0.0f;
				float grnQtyCat = 0.0f;
				float grnValueCat = 0.0f;
				float gvnQtyCat = 0.0f;
				float gvnValueCat = 0.0f;
				float netQtyTotalCat = 0.0f;
				float netValueTotalCat = 0.0f;
				float rAmtTotalCat = 0.0f;

				for (int i = 0; i < royaltyList.size(); i++) {

					if (catList.get(c).equalsIgnoreCase(String.valueOf(royaltyList.get(i).getCatId()))) {

						int index = 1;

						float billVal = 0, grnQty1 = 0, grnVal = 0, gvnQty1 = 0, gvnVal = 0;

						if (getBy == 1) {
							billVal = royaltyList.get(i).getTaxableAmt();

							if (type == 1) {
								grnVal = royaltyList.get(i).getGrnTaxableAmt();
								gvnVal = royaltyList.get(i).getGvnTaxableAmt();

							} else {
								grnVal = royaltyList.get(i).getCrnGrnTaxableAmt();
								gvnVal = royaltyList.get(i).getCrnGvnTaxableAmt();

							}
						} else {
							billVal = royaltyList.get(i).getGrandTotal();

							if (type == 1) {
								grnVal = royaltyList.get(i).getGrnGrandTotal();
								gvnVal = royaltyList.get(i).getGvnGrandTotal();

							} else {
								grnVal = royaltyList.get(i).getCrnGrnGrandTotal();
								gvnVal = royaltyList.get(i).getCrnGvnGrandTotal();

							}

						}

						if (type == 1) {
							grnQty1 = royaltyList.get(i).getGrnQty();
							gvnQty1 = royaltyList.get(i).getGvnQty();
						} else {
							grnQty1 = royaltyList.get(i).getCrnGrnQty();
							gvnQty1 = royaltyList.get(i).getCrnGvnQty();
						}

						if (billVal > 0 || grnVal > 0 || gvnVal > 0) {

							expoExcel = new ExportToExcel();
							rowData = new ArrayList<String>();

							index = index + i;

							rowData.add("" + sr);
							sr = sr + 1;
							rowData.add("" + royaltyList.get(i).getCat_name());

							rowData.add("" + royaltyList.get(i).getItem_name());

							rowData.add("" + roundUp(royaltyList.get(i).getBillQty()));

							rowData.add("" + roundUp(billVal));

							rowData.add("" + roundUp(grnQty1));

							rowData.add("" + roundUp(grnVal));
							rowData.add("" + roundUp(gvnQty1));
							rowData.add("" + roundUp(gvnVal));

							float netQty = royaltyList.get(i).getBillQty() - (grnQty1 + gvnQty1);

							float netValue = billVal - (grnVal + gvnVal);
							// float royPer = getRoyPer();

							float rAmt = netValue * royPer / 100;

							rowData.add("" + roundUp(netQty));
							rowData.add("" + roundUp(netValue));
							rowData.add("" + roundUp(royPer));
							rowData.add("" + roundUp(rAmt));

							float grnRet =0;
							float gvnRet=0;
							float sumRet=0;
							if(billVal>0)
							{	
							 grnRet = (grnVal * 100) / billVal;
							}
							if(billVal>0)
							{	
							 gvnRet = (gvnVal * 100) / billVal;
							}
							if((billVal)>0)
							{	
							 sumRet = ((grnVal + gvnVal) * 100) / billVal;
							}

						
							rowData.add("" + roundUp(grnRet));
							rowData.add("" + roundUp(gvnRet));
							rowData.add("" + roundUp(sumRet));

							float contri = (netValue * 100) / netValTotalForContri;
							contriTotal = contriTotal + contri;

							rowData.add("" + roundUp(contri));

							saleQtyCat = saleQtyCat + royaltyList.get(i).getBillQty();
							saleValueCat = saleValueCat + billVal;
							grnQtyCat = grnQtyCat + grnQty1;
							grnValueCat = grnValueCat + grnVal;
							gvnQtyCat = gvnQtyCat + gvnQty1;
							gvnValueCat = gvnValueCat + gvnVal;
							netQtyTotalCat = netQtyTotalCat + netQty;
							netValueTotalCat = netValueTotalCat + netValue;

							rAmtTotalCat = rAmtTotalCat + rAmt;

							saleQty = saleQty + royaltyList.get(i).getBillQty();
							saleValue = saleValue + billVal;
							grnQty = grnQty + grnQty1;
							grnValue = grnValue + grnVal;
							gvnQty = gvnQty + gvnQty1;
							gvnValue = gvnValue + gvnVal;
							netQtyTotal = netQtyTotal + netQty;
							netValueTotal = netValueTotal + netValue;

							rAmtTotal = rAmtTotal + rAmt;

							expoExcel.setRowData(rowData);
							exportToExcelList.add(expoExcel);

						}

					}

				}

				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add("");
				rowData.add("Total");
				rowData.add("");

				rowData.add("" + roundUp(saleQtyCat));
				rowData.add("" + roundUp(saleValueCat));
				rowData.add("" + roundUp(grnQtyCat));
				rowData.add("" + roundUp(grnValueCat));
				rowData.add("" + roundUp(gvnQtyCat));
				rowData.add("" + roundUp(gvnValueCat));
				rowData.add("" + roundUp(netQtyTotalCat));
				rowData.add("" + roundUp(netValueTotalCat));
				rowData.add("");
				rowData.add("" + roundUp(rAmtTotalCat));
				float grnRet=0;
				float gvnRet=0;
				float sumRet=0;
				if(grnValueCat>0)
				{	
				 grnRet = (grnValueCat * 100) / saleValueCat;
				}
				if(gvnValueCat>0)
				{
					 gvnRet = (gvnValueCat * 100) / saleValueCat;	
				}
				if(gvnValueCat>0)
				{
					 sumRet = ((grnValueCat + gvnValueCat) * 100) / saleValueCat;
				}
				
				

				rowData.add("" + roundUp(grnRet));
				rowData.add("" + roundUp(gvnRet));
				rowData.add("" + roundUp(sumRet));

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

//			for (int i = 0; i < royaltyList.size(); i++) {
//
//				int index = 1;
//
//				float billVal = 0, grnQty1 = 0, grnVal = 0, gvnQty1 = 0, gvnVal = 0;
//
//				if (getBy == 1) {
//					billVal = royaltyList.get(i).getTaxableAmt();
//
//					if (type == 1) {
//						grnVal = royaltyList.get(i).getGrnTaxableAmt();
//						gvnVal = royaltyList.get(i).getGvnTaxableAmt();
//
//					} else {
//						grnVal = royaltyList.get(i).getCrnGrnTaxableAmt();
//						gvnVal = royaltyList.get(i).getCrnGvnTaxableAmt();
//
//					}
//				} else {
//					billVal = royaltyList.get(i).getGrandTotal();
//
//					if (type == 1) {
//						grnVal = royaltyList.get(i).getGrnGrandTotal();
//						gvnVal = royaltyList.get(i).getGvnGrandTotal();
//
//					} else {
//						grnVal = royaltyList.get(i).getCrnGrnGrandTotal();
//						gvnVal = royaltyList.get(i).getCrnGvnGrandTotal();
//
//					}
//
//				}
//
//				if (type == 1) {
//					grnQty1 = royaltyList.get(i).getGrnQty();
//					gvnQty1 = royaltyList.get(i).getGvnQty();
//				} else {
//					grnQty1 = royaltyList.get(i).getCrnGrnQty();
//					gvnQty1 = royaltyList.get(i).getCrnGvnQty();
//				}
//
//				if (billVal > 0 || grnVal > 0 || gvnVal > 0) {
//
//					expoExcel = new ExportToExcel();
//					rowData = new ArrayList<String>();
//
//					index = index + i;
//
//					rowData.add("" + sr);
//					sr = sr + 1;
//					rowData.add("" + royaltyList.get(i).getCat_name());
//
//					rowData.add("" + royaltyList.get(i).getItem_name());
//
//					rowData.add("" + roundUp(royaltyList.get(i).getBillQty()));
//
//					rowData.add("" + roundUp(billVal));
//
//					rowData.add("" + roundUp(grnQty1));
//
//					rowData.add("" + roundUp(grnVal));
//					rowData.add("" + roundUp(gvnQty1));
//					rowData.add("" + roundUp(gvnVal));
//
//					float netQty = royaltyList.get(i).getBillQty() - (grnQty1 + gvnQty1);
//
//					float netValue = billVal - (grnVal + gvnVal);
//					// float royPer = getRoyPer();
//
//					float rAmt = netValue * royPer / 100;
//
//					rowData.add("" + roundUp(netQty));
//					rowData.add("" + roundUp(netValue));
//					rowData.add("" + roundUp(royPer));
//					rowData.add("" + roundUp(rAmt));
//
//					saleQty = saleQty + royaltyList.get(i).getBillQty();
//					saleValue = saleValue + billVal;
//					grnQty = grnQty + grnQty1;
//					grnValue = grnValue + grnVal;
//					gvnQty = gvnQty + gvnQty1;
//					gvnValue = gvnValue + gvnVal;
//					netQtyTotal = netQtyTotal + netQty;
//					netValueTotal = netValueTotal + netValue;
//
//					rAmtTotal = rAmtTotal + rAmt;
//
//					expoExcel.setRowData(rowData);
//					exportToExcelList.add(expoExcel);
//
//				}
//
//			}
		}

		expoExcel = new ExportToExcel();
		rowData = new ArrayList<String>();

		rowData.add("");
		rowData.add("Total");
		rowData.add("");

		rowData.add("" + roundUp(saleQty));
		rowData.add("" + roundUp(saleValue));
		rowData.add("" + roundUp(grnQty));
		rowData.add("" + roundUp(grnValue));
		rowData.add("" + roundUp(gvnQty));
		rowData.add("" + roundUp(gvnValue));
		rowData.add("" + roundUp(netQtyTotal));
		rowData.add("" + roundUp(netValueTotal));
		rowData.add("");
		rowData.add("" + roundUp(rAmtTotal));

		float grnRet = (grnValue * 100) / saleValue;
		float gvnRet = (gvnValue * 100) / saleValue;
		float sumRet = ((grnValue + gvnValue) * 100) / saleValue;

		rowData.add("" + roundUp(grnRet));
		rowData.add("" + roundUp(gvnRet));
		rowData.add("" + roundUp(sumRet));

		rowData.add("" + roundUp(contriTotal));

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);

		String searchBy = "", searchType = "";
		if (getBy == 1) {
			searchBy = "Taxable Amount";
		} else {
			searchBy = "Grand Total";
		}

		if (type == 1) {
			searchType = "GRN";
		} else {
			searchType = "CRN";
		}

		System.out.println("exportToExcelList" + exportToExcelList);
		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "RoyaltyConsolidatedCatList");
		session.setAttribute("reportNameNew", "View Sales Royalty by Category  " + " From Date: " + fromDate
				+ "  To Date: " + toDate + "   And Search By: " + searchBy + "  and  " + searchType);
		session.setAttribute("searchByNew", "Franchisee: " + frDisplay);

		session.setAttribute("mergeUpto1", "$A$1:$Q$1");
		session.setAttribute("mergeUpto2", "$A$2:$Q$2");

		return royaltyBean;

	}

	@RequestMapping(value = "pdf/getSaleReportRoyConsoByCatPdf/{fromDate}/{toDate}/{selectedFr}/{routeId}/{selectedCat}/{isGraph}/{getBy}/{type}", method = RequestMethod.GET)
	public ModelAndView getSaleReportRoyConsoByCat(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable String selectedCat,
			@PathVariable int isGraph, @PathVariable int getBy, @PathVariable int type, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/pdf/salesconsbycatPdf");

		List<SalesReportBillwise> saleList = new ArrayList<>();
		List<SalesRoyaltyConsByCat> royaltyList = new ArrayList<>();
		RoyaltyListBean royaltyBean = new RoyaltyListBean();
		boolean isAllFrSelected = false;
		boolean isAllCatSelected = false;
		try {
			System.out.println("Inside get Sale Bill Wise");

			if (!routeId.equalsIgnoreCase("0")) {

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				RestTemplate restTemplate = new RestTemplate();

				map.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						map, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				selectedFr = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + selectedFr);

			} // end of if

			if (selectedFr.contains("-1")) {
				isAllFrSelected = true;
				model.addObject("fr", "All Franchisee");
			} else {

				List<Integer> frIds = Stream.of(selectedFr.split(",")).map(Integer::parseInt)
						.collect(Collectors.toList());

				Set<String> set = new HashSet<>();

				if (allFrIdNameList.getFrIdNamesList() != null) {

					for (int j = 0; j < frIds.size(); j++) {

						for (int i = 0; i < allFrIdNameList.getFrIdNamesList().size(); i++) {

							if (allFrIdNameList.getFrIdNamesList().get(i).getFrId() == frIds.get(j)) {
								set.add(allFrIdNameList.getFrIdNamesList().get(i).getFrName());
								break;
							}

						}

					}
				}
				String string = String.join(", ", set);
				model.addObject("fr", string);
			}

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			}

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			// few fr Selected

			map.add("catIdList", selectedCat);

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			map.add("frIdList", selectedFr);
			map.add("getBy", getBy);
			map.add("type", type);
			if (isGraph == 0) {
				ParameterizedTypeReference<List<SalesRoyaltyConsByCat>> typeRef = new ParameterizedTypeReference<List<SalesRoyaltyConsByCat>>() {
				};

				ResponseEntity<List<SalesRoyaltyConsByCat>> responseEntity = restTemplate.exchange(
						Constants.url + "getSaleRoyConsoByCatReportData", HttpMethod.POST, new HttpEntity<>(map),
						typeRef);

				royaltyList = responseEntity.getBody();
				royaltyConsByCatListForPdf = new ArrayList<>();

				royaltyConsByCatListForPdf = royaltyList;
			}

			System.out.println("royaltyList List Bill Wise " + royaltyList.toString());

			CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			List<MCategoryList> categoryList;

			categoryList = categoryListResponse.getmCategoryList();
			// allFrIdNameList = new AllFrIdNameList();
			System.out.println("Category list  " + categoryList);
			List<MCategoryList> tempList = new ArrayList<>();

			// royaltyBean.setCategoryList(categoryList);
			Map<Integer, String> catNameId = new HashMap<Integer, String>();

			for (int i = 0; i < categoryList.size(); i++) {

				for (int j = 0; j < royaltyList.size(); j++) {

					if (categoryList.get(i).getCatId() == royaltyList.get(j).getCatId()) {
						catNameId.put(categoryList.get(i).getCatId(), categoryList.get(i).getCatName());

						if (!tempList.contains(categoryList.get(i))) {

							tempList.add(categoryList.get(i));

						}
					}

				}

				// }

				System.out.println("temp list " + tempList.toString() + "size of t List " + tempList.size());
				royaltyBean.setCategoryList(tempList);
				royaltyBean.setSalesReportRoyalty(royaltyList);
				model.addObject("royaltyList", royaltyBean);
				model.addObject("fromDate", fromDate);
				model.addObject("toDate", toDate);
				model.addObject("FACTORYNAME", Constants.FACTORYNAME);
				model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
				model.addObject("royPer", getRoyPer());

				model.addObject("getBy", getBy);
				model.addObject("type", type);

			}
		} catch (

		Exception e) {
			System.out.println("get sale Report royaltyList by cat " + e.getMessage());
			e.printStackTrace();

		}

		return model;

	}

	// pdf function

	private Dimension format = PD4Constants.A4;
	private boolean landscapeValue = false;
	private int topValue = 8;
	private int leftValue = 0;
	private int rightValue = 0;
	private int bottomValue = 8;
	private String unitsValue = "m";
	private String proxyHost = "";
	private int proxyPort = 0;

	private int userSpaceWidth = 750;
	private static int BUFFER_SIZE = 1024;

	@RequestMapping(value = "/pdfForReport", method = RequestMethod.GET)
	public void showPDF(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Inside PDf For Report URL ");
		String url = request.getParameter("url");
		System.out.println("URL " + url);

		File f = new File("/home/devour/apache-tomcat-9.0.12/webapps/uploads/report.pdf");
		// File f = new File("/home/lenovo/Desktop/grngvn.pdf");

		try {
			runConverter(Constants.ReportURL + url, f, request, response);
			// runConverter("www.google.com", f,request,response);

		} catch (IOException e) {

			System.out.println("Pdf conversion exception " + e.getMessage());
		}

		// get absolute path of the application
		ServletContext context = request.getSession().getServletContext();
		String appPath = context.getRealPath("");
		String filePath = "/home/devour/apache-tomcat-9.0.12/webapps/uploads/report.pdf";

		// String filePath = "/home/lenovo/Desktop/grngvn.pdf";

		// construct the complete absolute path of the file
		String fullPath = appPath + filePath;
		File downloadFile = new File(filePath);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(downloadFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/pdf";
			}
			System.out.println("MIME type: " + mimeType);

			String headerKey = "Content-Disposition";

			// response.addHeader("Content-Disposition", "attachment;filename=report.pdf");
			response.setContentType("application/pdf");

			OutputStream outStream;

			outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runConverter(String urlstring, File output, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (urlstring.length() > 0) {
			if (!urlstring.startsWith("http://") && !urlstring.startsWith("file:")) {
				urlstring = "http://" + urlstring;
			}
			System.out.println("PDF URL " + urlstring);
			java.io.FileOutputStream fos = new java.io.FileOutputStream(output);

			PD4ML pd4ml = new PD4ML();

			try {

				Dimension landscapeA4 = pd4ml.changePageOrientation(PD4Constants.A4);
				pd4ml.setPageSize(landscapeA4);
				pd4ml.enableSmartTableBreaks(true);
				PD4PageMark footer = new PD4PageMark();

				footer.setPageNumberTemplate("Page $[page] of $[total]");
				footer.setPageNumberAlignment(PD4PageMark.RIGHT_ALIGN);
				footer.setFontSize(10);
				footer.setAreaHeight(20);

				pd4ml.setPageFooter(footer);

			} catch (Exception e) {
				System.out.println("Pdf conversion method excep " + e.getMessage());
			}

			if (unitsValue.equals("mm")) {
				pd4ml.setPageInsetsMM(new Insets(topValue, leftValue, bottomValue, rightValue));
			} else {
				pd4ml.setPageInsets(new Insets(topValue, leftValue, bottomValue, rightValue));
			}

			pd4ml.setHtmlWidth(userSpaceWidth);

			pd4ml.render(urlstring, fos);
		}
	}

	@RequestMapping(value = "/pdfForDisReport", method = RequestMethod.GET)
	public void showPDF1(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("Inside PDf For Report URL ");
		String url = request.getParameter("url");
		System.out.println("URL " + url);

		// File f = new
		// File("/home/devour/apache-tomcat-9.0.12/webapps/uploads/report.pdf");
		// File f = new File("/opt/apache-tomcat-8.5.6/webapps/uploads/report.pdf");
		File f = new File("/home/devour/apache-tomcat-9.0.12/webapps/uploads/report2.pdf");
		// File f = new File("E:\\bill.pdf");
		try {
			runConverter1(Constants.ReportURL + url, f, request, response);
			// runConverter("www.google.com", f,request,response);

		} catch (IOException e) {

			System.out.println("Pdf conversion exception " + e.getMessage());
		}

		// get absolute path of the application
		ServletContext context = request.getSession().getServletContext();
		String appPath = context.getRealPath("");
		// String filePath =
		// "/home/devour/apache-tomcat-9.0.12/webapps/uploads/report.pdf";
		String filePath = "/home/devour/apache-tomcat-9.0.12/webapps/uploads/report2.pdf";
		// String filePath = "E:\\bill.pdf";

		// construct the complete absolute path of the file
		String fullPath = appPath + filePath;
		File downloadFile = new File(filePath);
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(downloadFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/pdf";
			}
			System.out.println("MIME type: " + mimeType);

			String headerKey = "Content-Disposition";

			// response.addHeader("Content-Disposition", "attachment;filename=report.pdf");
			response.setContentType("application/pdf");

			OutputStream outStream;

			outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}

			inputStream.close();
			outStream.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void runConverter1(String urlstring, File output, HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		if (urlstring.length() > 0) {
			if (!urlstring.startsWith("http://") && !urlstring.startsWith("file:")) {
				urlstring = "http://" + urlstring;
			}
			System.out.println("PDF URL " + urlstring);
			java.io.FileOutputStream fos = new java.io.FileOutputStream(output);

			PD4ML pd4ml = new PD4ML();
			pd4ml.enableSmartTableBreaks(true);
			try {

				try {
					pd4ml.setPageSize(landscapeValue ? pd4ml.changePageOrientation(format) : format);
				} catch (Exception e) {
					e.printStackTrace();
				}

				/*
				 * Dimension landscapeA4 = pd4ml.changePageOrientation(PD4Constants.A4);
				 * pd4ml.setPageSize(landscapeA4);
				 */
				PD4PageMark footer = new PD4PageMark();

				footer.setPageNumberTemplate("Page $[page] of $[total]");
				footer.setPageNumberAlignment(PD4PageMark.RIGHT_ALIGN);
				footer.setFontSize(10);
				footer.setAreaHeight(20);

				pd4ml.setPageFooter(footer);

			} catch (Exception e) {
				System.out.println("Pdf conversion method excep " + e.getMessage());
			}

			if (unitsValue.equals("mm")) {
				pd4ml.setPageInsetsMM(new Insets(topValue, leftValue, bottomValue, rightValue));
			} else {
				pd4ml.setPageInsets(new Insets(topValue, leftValue, bottomValue, rightValue));
			}

			pd4ml.setHtmlWidth(userSpaceWidth);

			pd4ml.render(urlstring, fos);
		}
	}

	@RequestMapping(value = "/showHsnWiseSummaryReport", method = RequestMethod.GET)
	public ModelAndView showHsnWiseSummaryReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		model = new ModelAndView("reports/hsnwiseReport");

		// Constants.mainAct =2;
		// Constants.subAct =20;

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);
			model.addObject("todaysDate", todaysDate);

		} catch (Exception e) {

			System.out.println("Exc in show   report hsn wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}
	
	
	ArrayList<SubCategory> subCatList=new ArrayList<>();
	List<SalesReturnValueDaoList> salesReturnValueReportList =new ArrayList<>();

	@RequestMapping(value = "/showMonthlySalesPercentageReport", method = RequestMethod.GET)
	public ModelAndView showMonthlySalesPercentageReport(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		String fromYear = "", toYear = "";

		model = new ModelAndView("reports/sales/monthwisesalespercentage");
		RestTemplate restTemplate = new RestTemplate();

		try {
			String year = request.getParameter("year");

			if (year != "") {
				String[] yrs = year.split("-"); // returns an array with the 2 parts

				MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();

				map.add("fromYear", yrs[0]);
				map.add("toYear", yrs[1]);

				fromYear = yrs[0];
				toYear = yrs[1];

				SalesReturnValueDaoList[] salesReturnValueReportListRes = restTemplate.postForObject(
						Constants.url + "getSalesReturnValueReport", map, SalesReturnValueDaoList[].class);

				salesReturnValueReportList = new ArrayList<SalesReturnValueDaoList>(
						Arrays.asList(salesReturnValueReportListRes));

				SubCategory[] subCategoryList = restTemplate.getForObject(Constants.url + "getAllSubCatList",
						SubCategory[].class);

				subCatList.clear();
				subCatList = new ArrayList<SubCategory>(Arrays.asList(subCategoryList));

				LinkedHashMap<Integer, SalesReturnValueDaoList> salesReturnValueReport = new LinkedHashMap<>();

				for (int i = 0; i < salesReturnValueReportList.size(); i++) {
					salesReturnValueReport.put(i, salesReturnValueReportList.get(i));
					float totBillAmt = 0;
					float totGrnValue = 0;
					float totGvnValue = 0;
					for (int k = 0; k < salesReturnValueReportList.get(i).getSalesReturnQtyValueList().size(); k++) {

						float temp = salesReturnValueReportList.get(i).getSalesReturnQtyValueList().get(k)
								.getGrandTotal()
								- (salesReturnValueReportList.get(i).getSalesReturnQtyValueList().get(k).getGrnQty()
										+ salesReturnValueReportList.get(i).getSalesReturnQtyValueList().get(k)
												.getGvnQty());

						totBillAmt = totBillAmt + temp;
						totGrnValue = totGrnValue
								+ salesReturnValueReportList.get(i).getSalesReturnQtyValueList().get(k).getGrnQty();
						totGvnValue = totGvnValue
								+ salesReturnValueReportList.get(i).getSalesReturnQtyValueList().get(k).getGvnQty();
					}
					salesReturnValueReportList.get(i).setTotBillAmt(totBillAmt);
					salesReturnValueReportList.get(i).setTotGrnQty(totGrnValue);
					salesReturnValueReportList.get(i).setTotGvnQty(totGvnValue);
				}

				System.out.println("LIst===========" + salesReturnValueReportList.toString());

				model.addObject("salesReturnValueReport", salesReturnValueReport);
				model.addObject("subCatList", subCatList);

				// exportToExcel

				List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

				ExportToExcel expoExcel = new ExportToExcel();
				List<String> rowData = new ArrayList<String>();
				rowData.add("Sr.");
				rowData.add("Group Name");
				for (int i = 0; i < salesReturnValueReport.size(); i++) {
					rowData.add(salesReturnValueReport.get(i).getMonth() + " Total");
					rowData.add(salesReturnValueReport.get(i).getMonth() + "%");
				}
				rowData.add("Total");
				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

				for (int k = 0; k < subCatList.size(); k++) {

					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();
					rowData.add("" + (k + 1));
					rowData.add("" + subCatList.get(k).getSubCatName());

					float horizontalTotal = 0.0f;

					for (int i = 0; i < salesReturnValueReport.size(); i++) {
						for (int j = 0; j < salesReturnValueReport.get(i).getSalesReturnQtyValueList().size(); j++) {

							if (salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
									.getSubCatId() == subCatList.get(k).getSubCatId()) {

								rowData.add("" + roundUp(salesReturnValueReport.get(i).getSalesReturnQtyValueList()
										.get(j).getGrandTotal()
										- (salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j).getGvnQty()
												+ salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
														.getGrnQty())));

								horizontalTotal = horizontalTotal
										+ salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
												.getGrandTotal()
										- (salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j).getGvnQty()
												+ salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
														.getGrnQty());

								if (salesReturnValueReport.get(i).getTotBillAmt() > 0) {
									rowData.add("" + roundUp(((salesReturnValueReport.get(i)
											.getSalesReturnQtyValueList().get(j).getGrandTotal()
											- (salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
													.getGvnQty()
													+ salesReturnValueReport.get(i).getSalesReturnQtyValueList().get(j)
															.getGrnQty()))
											* 100) / salesReturnValueReport.get(i).getTotBillAmt()));
								} else {
									rowData.add("0.00");
								}
							}
						}
					}
					
					rowData.add("" + roundUp(horizontalTotal));
					
					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
				}

				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("");
				rowData.add("Total");
				for (int i = 0; i < salesReturnValueReport.size(); i++) {
					rowData.add("" + salesReturnValueReport.get(i).getTotBillAmt());
					if(salesReturnValueReport.get(i).getTotBillAmt()>0) {
						rowData.add("100.00");
					}else {
						rowData.add("0.00");	
					}
					
				}

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

				System.err.println("exportToExcelList" + exportToExcelList.toString());
				// HttpSession session = request.getSession();
				// session.setAttribute("exportExcelList", exportToExcelList);
				// session.setAttribute("excelName", "MonthlySalesPerContrReport");

				HttpSession session = request.getSession();
				session.setAttribute("exportExcelListNew", exportToExcelList);
				session.setAttribute("excelNameNew", "SubCategoryWise_Contribution_Report");
				session.setAttribute("reportNameNew", "Sub Category Wise Contribution Report");
				session.setAttribute("searchByNew", "Year : " + fromYear + " To " + toYear + " ");
				session.setAttribute("mergeUpto1", "$A$1:$K$1");
				session.setAttribute("mergeUpto2", "$A$2:$K$2");

			}
		} catch (Exception e) {

		}
		// }
		return model;

	}
	
	
	
	@RequestMapping(value = "/getAllSubCatListForGraph", method = RequestMethod.GET)
	public @ResponseBody List<SubCategory> getAllSubCatListForGraph(HttpServletRequest request,
			HttpServletResponse response) {
		
		return subCatList;
	}
	
	
	@RequestMapping(value = "/getSubCatContriReportForGraph", method = RequestMethod.GET)
	public @ResponseBody List<SalesReturnValueDaoList>   getSubCatContriReportForGraph(HttpServletRequest request,
			HttpServletResponse response) {
		
		return salesReturnValueReportList;
	}
	
	
	
}
