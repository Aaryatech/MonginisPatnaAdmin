package com.ats.adminpanel.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.AllFrIdName;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.MCategoryList;
import com.ats.adminpanel.model.item.SubCategory;
import com.ats.adminpanel.model.salesreport2.SubCatFrReport;
import com.ats.adminpanel.model.salesreport2.SubCatFrReportList;
import com.ats.adminpanel.model.salesreport2.SubCatReport;
	

@Controller
@Scope("session")
public class SalesReportController2 {

	AllFrIdNameList allFrIdNameList = new AllFrIdNameList();
	String todaysDate;
	List<MCategoryList> mCategoryList;
	
	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	@RequestMapping(value = "/showSaleReportBySubCategory", method = RequestMethod.GET)
	public ModelAndView showSaleReportBySubCategory(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		model = new ModelAndView("reports/sales/saleRepBySubCatOnly");

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			model.addObject("todaysDate", todaysDate);

		} catch (Exception e) {

			System.out.println("Exc in show sales report bill wise  " + e.getMessage());
			e.printStackTrace();
		}

		return model;

	}
	
	
	@RequestMapping(value = "/getSubCatReport", method = RequestMethod.GET)
	public @ResponseBody List<SubCatReport> getSubCatReport(HttpServletRequest request, HttpServletResponse response) {

		List<SubCatReport> saleList = new ArrayList<>();
		String fromDate = "";
		String toDate = "";
		try {

			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SubCatReport>> typeRef = new ParameterizedTypeReference<List<SubCatReport>>() {
			};
			ResponseEntity<List<SubCatReport>> responseEntity = restTemplate
					.exchange(Constants.url + "getSubCatReportApi", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			saleList = responseEntity.getBody();

			for (int i = 0; i < saleList.size(); i++) {

				float netQty = saleList.get(i).getSoldQty()
						- (saleList.get(i).getVarQty() + saleList.get(i).getRetQty());
				float netAmt = saleList.get(i).getSoldAmt()
						- (saleList.get(i).getVarAmt() + saleList.get(i).getRetAmt());
				float retAmtPer = (((saleList.get(i).getVarAmt() + saleList.get(i).getRetAmt()) * 100)
						/ saleList.get(i).getSoldAmt());

				saleList.get(i).setNetQty(netQty);
				saleList.get(i).setNetAmt(netAmt);
				saleList.get(i).setRetAmtPer(retAmtPer);
			}

			// exportToExcel
			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr");
			rowData.add("Sub Category Name");
			rowData.add("Pur Qty");
			rowData.add("Pur Amt");

			rowData.add("Var Qty");
			rowData.add("Var Amt");

			rowData.add("Ret Qty");
			rowData.add("Ret Amt");

			rowData.add("Net Qty");
			rowData.add("Net Amt");

			rowData.add("Ret Per Amt%");

			expoExcel.setRowData(rowData);
			int srno = 1;
			exportToExcelList.add(expoExcel);

			float totalSoldQty = 0;
			float totalSoldAmt = 0;
			float totalVarQty = 0;
			float totalVarAmt = 0;
			float totalRetQty = 0;
			float totalRetAmt = 0;
			float totalNetQty = 0;
			float totalNetAmt = 0;
			float retAmtPer = 0;

			for (int i = 0; i < saleList.size(); i++) {

				totalSoldQty = totalSoldQty + saleList.get(i).getSoldQty();
				totalSoldAmt = totalSoldAmt + saleList.get(i).getSoldAmt();
				totalVarQty = totalVarQty + saleList.get(i).getVarQty();
				totalVarAmt = totalVarAmt + saleList.get(i).getVarAmt();
				totalRetQty = totalRetQty + saleList.get(i).getRetQty();
				totalRetAmt = totalRetAmt + saleList.get(i).getRetAmt();
				totalNetQty = totalNetQty + saleList.get(i).getNetQty();
				totalNetAmt = totalNetAmt + saleList.get(i).getNetAmt();
				retAmtPer = retAmtPer + saleList.get(i).getRetAmtPer();

				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();

				rowData.add("" + srno);
				rowData.add(saleList.get(i).getSubCatName());

				rowData.add("" + roundUp(saleList.get(i).getSoldQty()));
				rowData.add("" + roundUp(saleList.get(i).getSoldAmt()));
				rowData.add("" + roundUp(saleList.get(i).getVarQty()));
				rowData.add("" + roundUp(saleList.get(i).getVarAmt()));
				rowData.add("" + roundUp(saleList.get(i).getRetQty()));

				rowData.add("" + roundUp(saleList.get(i).getRetAmt()));
				rowData.add("" + roundUp(saleList.get(i).getNetQty()));
				rowData.add("" + roundUp(saleList.get(i).getNetAmt()));
				rowData.add("" + roundUp(saleList.get(i).getRetAmtPer())+"%");

				srno = srno + 1;

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();
			rowData.add(" ");
			rowData.add("Total");

			rowData.add("" + roundUp(totalSoldQty));
			rowData.add("" + roundUp(totalSoldAmt));
			rowData.add("" + roundUp(totalVarQty));
			rowData.add("" + roundUp(totalVarAmt));
			rowData.add("" + roundUp(totalRetQty));
			rowData.add("" + roundUp(totalRetAmt));

			rowData.add("" + roundUp(totalNetQty));
			rowData.add("" + roundUp(totalNetAmt));
			rowData.add("" + roundUp(retAmtPer)+"%");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelListNew", exportToExcelList);
			session.setAttribute("excelNameNew", "SubCategoryWise_Summary_Report");
			session.setAttribute("reportNameNew", "Sub Category Wise Summary Report");
			session.setAttribute("searchByNew", "From Date: " + fromDate + "  To Date: " + toDate + " ");
			session.setAttribute("mergeUpto1", "$A$1:$K$1");
			session.setAttribute("mergeUpto2", "$A$2:$K$2");
		} catch (Exception e) {
			// TODO: handle exception
		}

		return saleList;
	}
	
	
	@RequestMapping(value = "pdf/showSaleReportBySubCatPdf/{fromDate}/{toDate}  ", method = RequestMethod.GET)
	public ModelAndView showSaleReportBySubCatPdf(@PathVariable String fromDate, @PathVariable String toDate,
			HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/pdf/saleRepBySubCatOnlyPdf");

		List<SubCatReport> subCatReportList = new ArrayList<>();

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);

			ParameterizedTypeReference<List<SubCatReport>> typeRef = new ParameterizedTypeReference<List<SubCatReport>>() {
			};
			ResponseEntity<List<SubCatReport>> responseEntity = restTemplate
					.exchange(Constants.url + "getSubCatReportApi", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			subCatReportList = responseEntity.getBody();

			for (int i = 0; i < subCatReportList.size(); i++) {

				float netQty = subCatReportList.get(i).getSoldQty()
						- (subCatReportList.get(i).getVarQty() + subCatReportList.get(i).getRetQty());
				float netAmt = subCatReportList.get(i).getSoldAmt()
						- (subCatReportList.get(i).getVarAmt() + subCatReportList.get(i).getRetAmt());
				float retAmtPer = (((subCatReportList.get(i).getVarAmt() + subCatReportList.get(i).getRetAmt()) * 100)
						/ subCatReportList.get(i).getSoldAmt());

				subCatReportList.get(i).setNetQty(netQty);
				subCatReportList.get(i).setNetAmt(netAmt);
				subCatReportList.get(i).setRetAmtPer(retAmtPer);
			}

			model.addObject("subCatReportList", subCatReportList);
			model.addObject("FACTORYNAME", Constants.FACTORYNAME);
			model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		return model;
	}
	
	
	@RequestMapping(value = "/showSaleReportBySubCatAndFr", method = RequestMethod.GET)
	public ModelAndView showSaleReportBySubCatAndFr(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		model = new ModelAndView("reports/sales/saleRepBySubCat");

		try {
			ZoneId z = ZoneId.of("Asia/Calcutta");

			LocalDate date = LocalDate.now(z);
			DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
			todaysDate = date.format(formatters);

			RestTemplate restTemplate = new RestTemplate();

			allFrIdNameList = new AllFrIdNameList();
			try {

				allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			} catch (Exception e) {
				System.out.println("Exception in getAllFrIdName" + e.getMessage());
				e.printStackTrace();

			}
			model.addObject("todaysDate", todaysDate);
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

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
	
	@RequestMapping(value = "/getSaleReportBySubCatAndFr", method = RequestMethod.GET)
	public @ResponseBody SubCatFrReportList getSaleReportBySubCatAndFr(HttpServletRequest request,
			HttpServletResponse response) {

		SubCatFrReportList subCatFrReportListData = new SubCatFrReportList();
		
		String fr="";

		List<SubCatFrReport> subCatFrReportList = new ArrayList<>();
		List<AllFrIdName> frListFinal = new ArrayList<>();

		String fromDate = "";
		String toDate = "";
		try {
			System.out.println("Inside get Sale Bill Wise");
			String selectedFr = request.getParameter("fr_id_list");
			String selectedSubCatIdList = request.getParameter("subCat_id_list");
			fromDate = request.getParameter("fromDate");
			toDate = request.getParameter("toDate");

			System.out.println("selectedFrBefore------------------" + selectedFr);

			selectedFr = selectedFr.substring(1, selectedFr.length() - 1);
			selectedFr = selectedFr.replaceAll("\"", "");

			selectedSubCatIdList = selectedSubCatIdList.substring(1, selectedSubCatIdList.length() - 1);
			selectedSubCatIdList = selectedSubCatIdList.replaceAll("\"", "");

			System.out.println("selectedFrAfter------------------" + selectedFr);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			System.out.println("Inside If all fr Selected ");

			map.add("fromDate", fromDate);
			map.add("toDate", toDate);
			map.add("frIdList", selectedFr);
			map.add("subCatIdList", selectedSubCatIdList);
			System.out.println(map);
			ParameterizedTypeReference<List<SubCatFrReport>> typeRef = new ParameterizedTypeReference<List<SubCatFrReport>>() {
			};
			ResponseEntity<List<SubCatFrReport>> responseEntity = restTemplate
					.exchange(Constants.url + "getSubCatFrReportApi", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			subCatFrReportList = responseEntity.getBody();

			for (int i = 0; i < subCatFrReportList.size(); i++) {

				float netQty = subCatFrReportList.get(i).getSoldQty()
						- (subCatFrReportList.get(i).getVarQty() + subCatFrReportList.get(i).getRetQty());
				float netAmt = subCatFrReportList.get(i).getSoldAmt()
						- (subCatFrReportList.get(i).getVarAmt() + subCatFrReportList.get(i).getRetAmt());
				float retAmtPer = (((subCatFrReportList.get(i).getVarAmt() + subCatFrReportList.get(i).getRetAmt())
						* 100) / subCatFrReportList.get(i).getSoldAmt());

				subCatFrReportList.get(i).setNetQty(netQty);
				subCatFrReportList.get(i).setNetAmt(netAmt);
				subCatFrReportList.get(i).setRetAmtPer(retAmtPer);
			}

			allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			TreeSet<Integer> frIdListFinal = new TreeSet<Integer>();
			for (int j = 0; j < subCatFrReportList.size(); j++) {
				frIdListFinal.add(subCatFrReportList.get(j).getFrId());
			}
			//for (int frId : frIdListFinal) {
				for (int j = 0; j < allFrIdNameList.getFrIdNamesList().size(); j++) {
					/*if (allFrIdNameList.getFrIdNamesList().get(j).getFrId() == frId) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}*/
					if (frIdListFinal.contains(allFrIdNameList.getFrIdNamesList().get(j).getFrId())) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}
				}
			//}

			subCatFrReportListData.setFrList(frListFinal);
			subCatFrReportListData.setSubCatFrReportList(subCatFrReportList);

			System.out.println("subCatFrReportList*********************************************"
					+ subCatFrReportListData.toString());
			
			
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
			fr = String.join(", ", set);


		} catch (Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Sr");
		rowData.add("Sub Category Name");
		rowData.add("Sold Qty");
		rowData.add("Sold Amt");
		rowData.add("Var Qty");
		rowData.add("Var Amt");
		rowData.add("Ret Qty");
		rowData.add("Ret Amt");
		rowData.add("Net Qty");
		rowData.add("Net Amt");
		rowData.add("Ret Per Amt");

		expoExcel.setRowData(rowData);
		int srno = 1;
		exportToExcelList.add(expoExcel);

		for (int j = 0; j < frListFinal.size(); j++) {

			float totalSoldQty = 0;
			float totalSoldAmt = 0;
			float totalVarQty = 0;
			float totalVarAmt = 0;
			float totalRetQty = 0;
			float totalRetAmt = 0;
			float totalNetQty = 0;
			float totalNetAmt = 0;
			float retAmtPer = 0;

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add("");
			rowData.add("" + frListFinal.get(j).getFrName());
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");
			rowData.add("");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

			for (int i = 0; i < subCatFrReportList.size(); i++) {

				if (frListFinal.get(j).getFrId() == subCatFrReportList.get(i).getFrId()) {

					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();

					totalSoldQty = totalSoldQty + subCatFrReportList.get(i).getSoldQty();
					totalSoldAmt = totalSoldAmt + subCatFrReportList.get(i).getSoldAmt();
					totalVarQty = totalVarQty + subCatFrReportList.get(i).getVarQty();
					totalVarAmt = totalVarAmt + subCatFrReportList.get(i).getVarAmt();
					totalRetQty = totalRetQty + subCatFrReportList.get(i).getRetQty();
					totalRetAmt = totalRetAmt + subCatFrReportList.get(i).getRetAmt();
					totalNetQty = totalNetQty + subCatFrReportList.get(i).getNetQty();
					totalNetAmt = totalNetAmt + subCatFrReportList.get(i).getNetQty();
					retAmtPer = retAmtPer + subCatFrReportList.get(i).getRetAmtPer();

					expoExcel = new ExportToExcel();
					rowData = new ArrayList<String>();

					rowData.add("" + srno);
					rowData.add(subCatFrReportList.get(i).getSubCatName());

					rowData.add("" + roundUp(subCatFrReportList.get(i).getSoldQty()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getSoldQty()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getVarQty()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getVarAmt()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getRetQty()));

					rowData.add("" + roundUp(subCatFrReportList.get(i).getRetAmt()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getNetQty()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getNetQty()));
					rowData.add("" + roundUp(subCatFrReportList.get(i).getRetAmtPer()));

					srno = srno + 1;

					expoExcel.setRowData(rowData);
					exportToExcelList.add(expoExcel);
				}
			}

			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();
			rowData.add(" ");
			rowData.add("Total");

			rowData.add("" + roundUp(totalSoldQty));
			rowData.add("" + roundUp(totalSoldAmt));
			rowData.add("" + roundUp(totalVarQty));
			rowData.add("" + roundUp(totalVarAmt));
			rowData.add("" + roundUp(totalRetQty));
			rowData.add("" + roundUp(totalRetAmt));

			rowData.add("" + roundUp(totalNetQty));
			rowData.add("" + roundUp(totalNetAmt));
			rowData.add("" + roundUp(retAmtPer));

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelListNew", exportToExcelList);
		session.setAttribute("excelNameNew", "Franchise_Wise_SubCategory_Wise_Summary_Report");
		session.setAttribute("reportNameNew", "Franchisee Wise Sub Category Wise Summary Report Between "+fromDate+" To "+toDate);
		session.setAttribute("searchByNew", "Franchisee : " + fr);
		session.setAttribute("mergeUpto1", "$A$1:$K$1");
		session.setAttribute("mergeUpto2", "$A$2:$K$2");

		return subCatFrReportListData;
	}

	@RequestMapping(value = "pdf/showSummeryFrAndSubCatPdf/{fromDate}/{toDate}/{selectedFr}/{selectedSubCatIdList} ", method = RequestMethod.GET)
	public ModelAndView showSummeryFrAndSubCatPdf(@PathVariable String fromDate, @PathVariable String toDate,
			@PathVariable String selectedFr, @PathVariable String selectedSubCatIdList, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = new ModelAndView("reports/sales/pdf/saleRepBySubCatPdf");

		SubCatFrReportList subCatFrReportListData = new SubCatFrReportList();

		List<SubCatFrReport> subCatFrReportList = new ArrayList<>();
		List<AllFrIdName> frListFinal = new ArrayList<>();

		try {
  
			  
			 
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();

			  
			map.add("fromDate", fromDate);
			map.add("toDate", toDate);
			map.add("frIdList", selectedFr);
			map.add("subCatIdList", selectedSubCatIdList);

			System.out.println(map);
			ParameterizedTypeReference<List<SubCatFrReport>> typeRef = new ParameterizedTypeReference<List<SubCatFrReport>>() {
			};
			ResponseEntity<List<SubCatFrReport>> responseEntity = restTemplate
					.exchange(Constants.url + "getSubCatFrReportApi", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			subCatFrReportList = responseEntity.getBody();

			for (int i = 0; i < subCatFrReportList.size(); i++) {

				float netQty = subCatFrReportList.get(i).getSoldQty()
						- (subCatFrReportList.get(i).getVarQty() + subCatFrReportList.get(i).getRetQty());
				float netAmt = subCatFrReportList.get(i).getSoldAmt()
						- (subCatFrReportList.get(i).getVarAmt() + subCatFrReportList.get(i).getRetAmt());
				float retAmtPer = (((subCatFrReportList.get(i).getVarAmt() + subCatFrReportList.get(i).getRetAmt())
						* 100) / subCatFrReportList.get(i).getSoldAmt());

				subCatFrReportList.get(i).setNetQty(netQty);
				subCatFrReportList.get(i).setNetAmt(netAmt);
				subCatFrReportList.get(i).setRetAmtPer(retAmtPer);
			}

			allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);

			TreeSet<Integer> frIdListFinal = new TreeSet<Integer>();
			for (int j = 0; j < subCatFrReportList.size(); j++) {
				frIdListFinal.add(subCatFrReportList.get(j).getFrId());
			}
			//for (int frId : frIdListFinal) {
				for (int j = 0; j < allFrIdNameList.getFrIdNamesList().size(); j++) {
					/*if (allFrIdNameList.getFrIdNamesList().get(j).getFrId() == frId) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}*/
					
					if (frIdListFinal.contains(allFrIdNameList.getFrIdNamesList().get(j).getFrId())) {
						frListFinal.add(allFrIdNameList.getFrIdNamesList().get(j));

					}
				}
			//}

			subCatFrReportListData.setFrList(frListFinal);
			subCatFrReportListData.setSubCatFrReportList(subCatFrReportList);

			System.out.println(frListFinal.toString());
			System.out.println(subCatFrReportList.toString());

			model.addObject("subCatFrReportListData", subCatFrReportListData);
			model.addObject("frList", frListFinal);
			model.addObject("subCatFrReportList", subCatFrReportList);
			model.addObject("FACTORYNAME", Constants.FACTORYNAME);
			model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
			model.addObject("fromDate", fromDate);
			model.addObject("toDate", toDate);
			
			
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

			

		} catch (

		Exception e) {
			System.out.println("get sale Report Bill Wise " + e.getMessage());
			e.printStackTrace();

		}

		return model;
	}
	
	
	@RequestMapping(value = "/getSubCatByCatIdForReport", method = RequestMethod.GET)
	public @ResponseBody List<SubCategory> getSubCatByCatIdForReport(HttpServletRequest request,
			HttpServletResponse response) {

		List<SubCategory> subCatList = new ArrayList<SubCategory>();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String selectedCat = request.getParameter("catId");
			boolean isAllCatSelected = false;

			System.out.println(
					"System.out.println(selectedCat);System.out.println(selectedCat);System.out.println(selectedCat);"
							+ selectedCat);

			if (selectedCat.contains("-1")) {
				isAllCatSelected = true;
			} else {
				selectedCat = selectedCat.substring(1, selectedCat.length() - 1);
				selectedCat = selectedCat.replaceAll("\"", "");
			}

			System.out.println(selectedCat);
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("catId", selectedCat);
			map.add("isAllCatSelected", isAllCatSelected);

			subCatList = restTemplate.postForObject(Constants.url + "getSubCatListByCatIdInForDisp", map, List.class);
			System.out.println(subCatList.toString());

		} catch (Exception e) {

		}

		return subCatList;
	}
	
	@RequestMapping(value = "/getFrListofAllFrForFrSummery", method = RequestMethod.GET)
	@ResponseBody
	public List<AllFrIdName> getFrListofAllFrForFrSummery(HttpServletRequest request, HttpServletResponse response) {

		return allFrIdNameList.getFrIdNamesList();
	}
	
	
	
}
