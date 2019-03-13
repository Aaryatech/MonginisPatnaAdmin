package com.ats.adminpanel.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.ats.adminpanel.commons.AccessControll;
import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.commons.DateConvertor;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.AllRoutesListResponse;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.Route;
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteId;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteIdResponse;
import com.ats.adminpanel.model.ggreports.GGReportByDateAndFr;
import com.ats.adminpanel.model.ggreports.GGReportGrpByFrId;
import com.ats.adminpanel.model.ggreports.GGReportGrpByItemId;
import com.ats.adminpanel.model.ggreports.GGReportGrpByMonthDate;
import com.ats.adminpanel.model.item.AllItemsListResponse;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.Item;
import com.ats.adminpanel.model.item.MCategoryList;
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
import com.itextpdf.text.pdf.codec.Base64.InputStream;

@Controller
@Scope("session")
public class GrnGvnReportController {

	public AllFrIdNameList allFrIdNameList = new AllFrIdNameList();

	public List<Route> routeList = new ArrayList<Route>();
	AllRoutesListResponse allRouteListResponse = new AllRoutesListResponse();

	AllFrIdNameList getFrNameId() {

		RestTemplate restTemplate = new RestTemplate();
		return allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName", AllFrIdNameList.class);
	}

	AllRoutesListResponse getAllRoute() {

		RestTemplate restTemplate = new RestTemplate();

		return allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
				AllRoutesListResponse.class);

	}

	// r1
	@RequestMapping(value = "/showGGReportDateWise", method = RequestMethod.GET)
	public ModelAndView showGGReportDateWise(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showGGReportDateWise", "showGGReportDateWise", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("reports/grnGvn/ggByDate");

			try {

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				String todaysDate = date.format(formatters);

				allFrIdNameList = getFrNameId();

				allRouteListResponse = getAllRoute();

				model.addObject("routeList", allRouteListResponse.getRoute());
				model.addObject("todaysDate", todaysDate);
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			} catch (Exception e) {
				System.out.println("Exce inshowGGReportDateWise " + e.getMessage());
				e.printStackTrace();
			}
		}

		return model;
	}
	// consume R1 web Service

	@RequestMapping(value = "/getGrnGvnByDatewise", method = RequestMethod.GET)
	@ResponseBody
	public List<GGReportByDateAndFr> getGrnGvnByDatewise(HttpServletRequest request, HttpServletResponse response) {

		List<GGReportByDateAndFr> grnGvnByDateList = new ArrayList<>();

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			String routeId = "0";
			String frIdString = "";

			System.out.println("inside getGrnGvnByDatewise ajax call");

			frIdString = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("from_date");
			String toDate = request.getParameter("to_date");
			routeId = request.getParameter("route_id");

			String isGrn = request.getParameter("is_grn");
			String grnType;
			if (isGrn.equalsIgnoreCase("2")) {

				System.err.println("Is Grn =2");
				grnType = "1" + "," + "0";

				map.add("isGrn", grnType);
			} else {
				System.err.println("Is Grn not =2");
				grnType = isGrn;
				map.add("isGrn", isGrn);

			}

			System.out.println("fromDate= " + fromDate);

			boolean isAllFrSelected = false;

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();
			franchIds = Arrays.asList(frIdString);

			System.out.println("fr Id ArrayList " + franchIds.toString());

			if (franchIds.contains("-1")) {
				isAllFrSelected = true;

			}

			if (!routeId.equalsIgnoreCase("0")) {

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
				frIdString = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + frIdString);

			} // end of if

			map = new LinkedMultiValueMap<String, Object>();
			if (isAllFrSelected) {

				System.out.println("Inside IF  is All fr Selected " + isAllFrSelected);

				map.add("frIdList", 0);
				// model.addObject("billHeadersList",billHeadersListForPrint);

			} else { // few franchisee selected

				System.out.println("Inside Else: Few Fr Selected ");
				map.add("frIdList", frIdString);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("isGrn", grnType);

			ParameterizedTypeReference<List<GGReportByDateAndFr>> typeRef = new ParameterizedTypeReference<List<GGReportByDateAndFr>>() {
			};
			ResponseEntity<List<GGReportByDateAndFr>> responseEntity = restTemplate
					.exchange(Constants.url + "getgGReportByDate", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnByDateList = responseEntity.getBody();

			System.err.println("List " + grnGvnByDateList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Date");
			rowData.add("Type");
			rowData.add("GrnGvn SrNo");
			rowData.add("Franchise Name");
			rowData.add("Req Qty");
			rowData.add("Req Value");
			rowData.add("Apr Qty");
			rowData.add("Apr Value");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			List<GGReportByDateAndFr> excelItems = grnGvnByDateList;
			for (int i = 0; i < excelItems.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));
				rowData.add("" + excelItems.get(i).getGrngvnDate());

				String type;
				if (excelItems.get(i).getIsGrn() == 1) {
					type = "GRN";

				} else {
					type = "GVN";
				}
				rowData.add(type);
				rowData.add(excelItems.get(i).getGrngvnSrno());
				rowData.add(excelItems.get(i).getFrName());
				rowData.add("" + excelItems.get(i).getReqQty());
				rowData.add("" + excelItems.get(i).getTotalAmt());
				rowData.add("" + excelItems.get(i).getAprQty());

				rowData.add("" + excelItems.get(i).getAprGrandTotal());

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "grnGvnReport");

		} catch (Exception e) {

			System.out.println("Ex in getting /getgGReportByDate List  Ajax call" + e.getMessage());
			e.printStackTrace();
		}

		return grnGvnByDateList;

	}
	// showGGreportByDate PDF

	@RequestMapping(value = "pdf/showGGreportByDate/{fDate}/{tDate}/{selectedFr}/{routeId}/{isGrn}", method = RequestMethod.GET)
	public ModelAndView showGGreportByDatePdf(@PathVariable String fDate, @PathVariable String tDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable int isGrn,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/grnGvn/pdf/r1");

		List<GGReportByDateAndFr> grnGvnByDateList = new ArrayList<>();

		System.err.println("Inside PDF mapping");

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
				map.add("frIdList", 0);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);

			}

			if (isGrn == 2) {
				System.err.println("Is Grn ==2");

				map.add("isGrn", "1" + "," + "0");
			} else {
				System.err.println("Is Grn  not Eq 2");

				map.add("isGrn", isGrn);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fDate));
			map.add("toDate", DateConvertor.convertToYMD(tDate));
			// map.add("isGrn", isGrn);

			ParameterizedTypeReference<List<GGReportByDateAndFr>> typeRef = new ParameterizedTypeReference<List<GGReportByDateAndFr>>() {
			};
			ResponseEntity<List<GGReportByDateAndFr>> responseEntity = restTemplate
					.exchange(Constants.url + "getgGReportByDate", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnByDateList = responseEntity.getBody();

			System.err.println("List---- " + grnGvnByDateList.toString());

		} catch (Exception e) {
			System.err.println("Exc in GRN PDF report 2");
			e.printStackTrace();
		}

		model.addObject("fromDate", fDate);

		model.addObject("toDate", tDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", grnGvnByDateList);

		return model;
	}

	// r2
	@RequestMapping(value = "/showGGReportGrpByFr", method = RequestMethod.GET)
	public ModelAndView showGGReportGrpByFr(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showGGReportGrpByFr", "showGGReportGrpByFr", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("reports/grnGvn/ggGrpByFr");

			try {

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				String todaysDate = date.format(formatters);

				allFrIdNameList = getFrNameId();

				allRouteListResponse = getAllRoute();

				model.addObject("routeList", allRouteListResponse.getRoute());
				model.addObject("todaysDate", todaysDate);
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			} catch (Exception e) {
				System.out.println("Exce inshowGGReportDateWise " + e.getMessage());
				e.printStackTrace();
			}
		}
		return model;
	}

	// consume r2 web service Ajax call
	@RequestMapping(value = "/getGrnGvnByGrpByFr", method = RequestMethod.GET)
	@ResponseBody
	public List<GGReportGrpByFrId> getGrnGvnByGrpByFr(HttpServletRequest request, HttpServletResponse response) {

		List<GGReportGrpByFrId> grnGvnGrpByFrList = new ArrayList<>();

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			String routeId = "0";
			String frIdString = "";

			System.out.println("inside getGrnGvnByDatewise ajax call");

			frIdString = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("from_date");
			String toDate = request.getParameter("to_date");
			routeId = request.getParameter("route_id");
			String isGrn = request.getParameter("is_grn");

			String grnType;
			if (isGrn.equalsIgnoreCase("2")) {

				System.err.println("Is Grn =2");
				grnType = "1" + "," + "0";

				map.add("isGrn", grnType);
			} else {
				System.err.println("Is Grn not =2");
				grnType = isGrn;
				map.add("isGrn", isGrn);

			}

			System.out.println("fromDate= " + fromDate);

			boolean isAllFrSelected = false;

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();
			franchIds = Arrays.asList(frIdString);

			System.out.println("fr Id ArrayList " + franchIds.toString());

			if (franchIds.contains("-1")) {
				isAllFrSelected = true;

			}

			if (!routeId.equalsIgnoreCase("0")) {

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
				frIdString = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + frIdString);

			} // end of if

			map = new LinkedMultiValueMap<String, Object>();
			if (isAllFrSelected) {

				System.out.println("Inside IF  is All fr Selected " + isAllFrSelected);

				map.add("frIdList", 0);
				// model.addObject("billHeadersList",billHeadersListForPrint);

			} else { // few franchisee selected

				System.out.println("Inside Else: Few Fr Selected ");
				map.add("frIdList", frIdString);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("isGrn", grnType);

			ParameterizedTypeReference<List<GGReportGrpByFrId>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByFrId>>() {
			};
			ResponseEntity<List<GGReportGrpByFrId>> responseEntity = restTemplate
					.exchange(Constants.url + "gGReportGrpByFrId", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByFrList = responseEntity.getBody();

			System.err.println("List " + grnGvnGrpByFrList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Type");
			rowData.add("Franchise Name");
			rowData.add("Req Qty");
			rowData.add("Req Value");
			rowData.add("Apr Qty");
			rowData.add("Apr Value");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			List<GGReportGrpByFrId> excelItems = grnGvnGrpByFrList;
			for (int i = 0; i < excelItems.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));

				String type;
				if (excelItems.get(i).getIsGrn() == 1) {
					type = "GRN";

				} else {
					type = "GVN";
				}
				rowData.add(type);
				rowData.add(excelItems.get(i).getFrName());
				rowData.add("" + excelItems.get(i).getReqQty());
				rowData.add("" + excelItems.get(i).getTotalAmt());
				rowData.add("" + excelItems.get(i).getAprQty());

				rowData.add("" + excelItems.get(i).getAprGrandTotal());

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "grnGvnReport");

		} catch (Exception e) {

			System.out.println("Ex in getting /getGrnGvnByGrpByFr List  Ajax call" + e.getMessage());
			e.printStackTrace();
		}

		return grnGvnGrpByFrList;

	}

	// r2 PDFshowGGreportGrpByFr

	@RequestMapping(value = "pdf/showGGreportGrpByFr/{fDate}/{tDate}/{selectedFr}/{routeId}/{isGrn}", method = RequestMethod.GET)
	public ModelAndView showSaleReportByDatePdf(@PathVariable String fDate, @PathVariable String tDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable int isGrn,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/grnGvn/pdf/r2");

		List<GGReportGrpByFrId> grnGvnGrpByFrList = new ArrayList<>();

		System.err.println("Inside PDF mapping");

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
				map.add("frIdList", 0);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);

			}

			if (isGrn == 2) {
				System.err.println("Is Grn ==2");

				map.add("isGrn", "1" + "," + "0");
			} else {
				System.err.println("Is Grn  not Eq 2");

				map.add("isGrn", isGrn);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fDate));
			map.add("toDate", DateConvertor.convertToYMD(tDate));
			// map.add("isGrn", isGrn);

			ParameterizedTypeReference<List<GGReportGrpByFrId>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByFrId>>() {
			};
			ResponseEntity<List<GGReportGrpByFrId>> responseEntity = restTemplate
					.exchange(Constants.url + "gGReportGrpByFrId", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByFrList = responseEntity.getBody();

			System.err.println("List---- " + grnGvnGrpByFrList.toString());

		} catch (Exception e) {
			System.err.println("Exc in GRN PDF report 2");
			e.printStackTrace();
		}

		model.addObject("fromDate", fDate);

		model.addObject("toDate", tDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", grnGvnGrpByFrList);

		return model;
	}

	// r3

	@RequestMapping(value = "/showGGReportGrpByDate", method = RequestMethod.GET)
	public ModelAndView showGGReportGrpByDate(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showGGReportGrpByDate", "showGGReportGrpByDate", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("reports/grnGvn/grnGvnGrpByDate");

			try {

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				String todaysDate = date.format(formatters);

				allFrIdNameList = getFrNameId();

				allRouteListResponse = getAllRoute();

				model.addObject("routeList", allRouteListResponse.getRoute());
				model.addObject("todaysDate", todaysDate);
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			} catch (Exception e) {
				System.out.println("Exce inshowGGReportDateWise " + e.getMessage());
				e.printStackTrace();
			}
		}
		return model;
	}

	// consume r3 web service ajax call
	//

	@RequestMapping(value = "/getGrnGvnByGrpByDate", method = RequestMethod.GET)
	@ResponseBody
	public List<GGReportGrpByMonthDate> getGrnGvnByGrpByDate(HttpServletRequest request, HttpServletResponse response) {

		List<GGReportGrpByMonthDate> grnGvnGrpByDateList = new ArrayList<>();

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			String routeId = "0";
			String frIdString = "";

			System.out.println("inside getGrnGvnByDatewise ajax call");

			frIdString = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("from_date");
			String toDate = request.getParameter("to_date");
			routeId = request.getParameter("route_id");
			String isGrn = request.getParameter("is_grn");

			String grnType;
			if (isGrn.equalsIgnoreCase("2")) {

				System.err.println("Is Grn =2");
				grnType = "1" + "," + "0";

				map.add("isGrn", grnType);
			} else {
				System.err.println("Is Grn not =2");
				grnType = isGrn;
				map.add("isGrn", isGrn);

			}

			System.out.println("fromDate= " + fromDate);

			boolean isAllFrSelected = false;

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();
			franchIds = Arrays.asList(frIdString);

			System.out.println("fr Id ArrayList " + franchIds.toString());

			if (franchIds.contains("-1")) {
				isAllFrSelected = true;

			}

			if (!routeId.equalsIgnoreCase("0")) {

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
				frIdString = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + frIdString);

			} // end of if

			map = new LinkedMultiValueMap<String, Object>();
			if (isAllFrSelected) {

				System.out.println("Inside IF  is All fr Selected " + isAllFrSelected);

				map.add("frIdList", 0);
				// model.addObject("billHeadersList",billHeadersListForPrint);

			} else { // few franchisee selected

				System.out.println("Inside Else: Few Fr Selected ");
				map.add("frIdList", frIdString);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("isGrn", grnType);
			map.add("frIdList", 0);

			ParameterizedTypeReference<List<GGReportGrpByMonthDate>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByMonthDate>>() {
			};
			ResponseEntity<List<GGReportGrpByMonthDate>> responseEntity = restTemplate
					.exchange(Constants.url + "getGGReportGrpByDate", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByDateList = responseEntity.getBody();

			System.err.println("List " + grnGvnGrpByDateList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Date");

			rowData.add("Type");
			rowData.add("Req Qty");
			rowData.add("Req Value");
			rowData.add("Apr Qty");
			rowData.add("Apr Value");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			List<GGReportGrpByMonthDate> excelItems = grnGvnGrpByDateList;
			for (int i = 0; i < excelItems.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));

				String type;
				if (excelItems.get(i).getIsGrn() == 1) {
					type = "GRN";

				} else {
					type = "GVN";
				}
				rowData.add(excelItems.get(i).getGrnGvnDate());
				rowData.add(type);
				rowData.add("" + excelItems.get(i).getReqQty());
				rowData.add("" + excelItems.get(i).getTotalAmt());
				rowData.add("" + excelItems.get(i).getAprQty());

				rowData.add("" + excelItems.get(i).getAprGrandTotal());

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "grnGvnReport");

		} catch (Exception e) {

			System.out.println("Ex in getting /getGrnGvnByGrpByDate List  Ajax call" + e.getMessage());
			e.printStackTrace();
		}

		return grnGvnGrpByDateList;

	}

	// showGGreportGrpByDate r3 PDF

	@RequestMapping(value = "pdf/showGGreportGrpByDate/{fDate}/{tDate}/{selectedFr}/{routeId}/{isGrn}", method = RequestMethod.GET)
	public ModelAndView showGGreportGrpByDatePdf(@PathVariable String fDate, @PathVariable String tDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable int isGrn,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/grnGvn/pdf/r3");

		List<GGReportGrpByMonthDate> grnGvnGrpByDateList = new ArrayList<>();

		System.err.println("Inside PDF mapping");

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
				map.add("frIdList", 0);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);

			}

			if (isGrn == 2) {
				System.err.println("Is Grn ==2");

				map.add("isGrn", "1" + "," + "0");
			} else {
				System.err.println("Is Grn  not Eq 2");

				map.add("isGrn", isGrn);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fDate));
			map.add("toDate", DateConvertor.convertToYMD(tDate));
			// map.add("isGrn", isGrn);
			map.add("frIdList", 0);

			ParameterizedTypeReference<List<GGReportGrpByMonthDate>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByMonthDate>>() {
			};
			ResponseEntity<List<GGReportGrpByMonthDate>> responseEntity = restTemplate
					.exchange(Constants.url + "getGGReportGrpByDate", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByDateList = responseEntity.getBody();

			System.err.println("List---- " + grnGvnGrpByDateList.toString());

		} catch (Exception e) {
			System.err.println("Exc in GRN PDF report 2");
			e.printStackTrace();
		}

		model.addObject("fromDate", fDate);

		model.addObject("toDate", tDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", grnGvnGrpByDateList);

		return model;
	}

	// r4

	@RequestMapping(value = "/showGGReportGrpByMonth", method = RequestMethod.GET)
	public ModelAndView showGGReportGrpByMonth(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showGGReportGrpByMonth", "showGGReportGrpByMonth", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("reports/grnGvn/gGGrpByMonth");

			try {

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				String todaysDate = date.format(formatters);

				allFrIdNameList = getFrNameId();

				allRouteListResponse = getAllRoute();

				model.addObject("routeList", allRouteListResponse.getRoute());
				model.addObject("todaysDate", todaysDate);
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			} catch (Exception e) {
				System.out.println("Exce inshowGGReportDateWise " + e.getMessage());
				e.printStackTrace();
			}
		}
		return model;
	}

	// r4 consume web service

	@RequestMapping(value = "/getGrnGvnByGrpByMonth", method = RequestMethod.GET)
	@ResponseBody
	public List<GGReportGrpByMonthDate> getGrnGvnByGrpByMonth(HttpServletRequest request,
			HttpServletResponse response) {

		List<GGReportGrpByMonthDate> grnGvnGrpByMonthList = new ArrayList<>();

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			String routeId = "0";
			String frIdString = "";

			System.out.println("inside getGrnGvnByDatewise ajax call");

			frIdString = request.getParameter("fr_id_list");
			String fromDate = request.getParameter("from_date");
			String toDate = request.getParameter("to_date");
			routeId = request.getParameter("route_id");
			String isGrn = request.getParameter("is_grn");

			String grnType;
			if (isGrn.equalsIgnoreCase("2")) {

				System.err.println("Is Grn =2");
				grnType = "1" + "," + "0";

				map.add("isGrn", grnType);
			} else {
				System.err.println("Is Grn not =2");
				grnType = isGrn;
				map.add("isGrn", isGrn);

			}

			System.out.println("fromDate= " + fromDate);

			boolean isAllFrSelected = false;

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();
			franchIds = Arrays.asList(frIdString);

			System.out.println("fr Id ArrayList " + franchIds.toString());

			if (franchIds.contains("-1")) {
				isAllFrSelected = true;

			}

			if (!routeId.equalsIgnoreCase("0")) {

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
				frIdString = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + frIdString);

			} // end of if

			map = new LinkedMultiValueMap<String, Object>();
			if (isAllFrSelected) {

				System.out.println("Inside IF  is All fr Selected " + isAllFrSelected);

				map.add("frIdList", 0);
				// model.addObject("billHeadersList",billHeadersListForPrint);

			} else { // few franchisee selected

				System.out.println("Inside Else: Few Fr Selected ");
				map.add("frIdList", frIdString);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("isGrn", grnType);
			map.add("frIdList", 0);

			ParameterizedTypeReference<List<GGReportGrpByMonthDate>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByMonthDate>>() {
			};
			ResponseEntity<List<GGReportGrpByMonthDate>> responseEntity = restTemplate
					.exchange(Constants.url + "getGGReportGrpByMonth", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByMonthList = responseEntity.getBody();

			System.err.println("List " + grnGvnGrpByMonthList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Month");

			rowData.add("Req Qty");
			rowData.add("Req Value");
			rowData.add("Apr Qty");
			rowData.add("Apr Value");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			List<GGReportGrpByMonthDate> excelItems = grnGvnGrpByMonthList;
			for (int i = 0; i < excelItems.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));

				rowData.add(excelItems.get(i).getMonth());
				rowData.add("" + excelItems.get(i).getReqQty());
				rowData.add("" + excelItems.get(i).getTotalAmt());
				rowData.add("" + excelItems.get(i).getAprQty());

				rowData.add("" + excelItems.get(i).getAprGrandTotal());

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "grnGvnReport");

		} catch (Exception e) {

			System.out.println("Ex in getting /getGrnGvnByGrpByDate List  Ajax call" + e.getMessage());
			e.printStackTrace();
		}

		return grnGvnGrpByMonthList;

	}

	// showGGreportGrpByMonth r4 PDF

	@RequestMapping(value = "pdf/showGGreportGrpByMonth/{fDate}/{tDate}/{selectedFr}/{routeId}/{isGrn}", method = RequestMethod.GET)
	public ModelAndView showGGreportGrpByMonthPdf(@PathVariable String fDate, @PathVariable String tDate,
			@PathVariable String selectedFr, @PathVariable String routeId, @PathVariable int isGrn,
			HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = new ModelAndView("reports/grnGvn/pdf/r4");

		List<GGReportGrpByMonthDate> grnGvnGrpByDateList = new ArrayList<>();

		System.err.println("Inside PDF mapping");

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
				map.add("frIdList", 0);

			} else {
				System.out.println("Inside else Few fr Selected ");

				map.add("frIdList", selectedFr);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fDate));
			map.add("toDate", DateConvertor.convertToYMD(tDate));

			if (isGrn == 2) {
				System.err.println("Is Grn ==2");

				map.add("isGrn", "1" + "," + "0");
			} else {
				System.err.println("Is Grn  not Eq 2");

				map.add("isGrn", isGrn);

			}
			// map.add("isGrn", isGrn);
			map.add("frIdList", 0);

			ParameterizedTypeReference<List<GGReportGrpByMonthDate>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByMonthDate>>() {
			};
			ResponseEntity<List<GGReportGrpByMonthDate>> responseEntity = restTemplate
					.exchange(Constants.url + "getGGReportGrpByMonth", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByDateList = responseEntity.getBody();

			System.err.println("List---- " + grnGvnGrpByDateList.toString());

		} catch (Exception e) {
			System.err.println("Exc in GRN PDF report 2");
			e.printStackTrace();
		}

		model.addObject("fromDate", fDate);

		model.addObject("toDate", tDate);
		model.addObject("FACTORYNAME", Constants.FACTORYNAME);
		model.addObject("FACTORYADDRESS", Constants.FACTORYADDRESS);
		model.addObject("report", grnGvnGrpByDateList);

		return model;
	}

	@RequestMapping(value = "/showGGReportGrpByItem", method = RequestMethod.GET)
	public ModelAndView showGGReportGrpByItem(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showGGReportGrpByItem", "showGGReportGrpByItem", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("reports/grnGvn/ggByItem");

			try {
				RestTemplate restTemplate = new RestTemplate();

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				String todaysDate = date.format(formatters);

				allFrIdNameList = getFrNameId();

				allRouteListResponse = getAllRoute();

				List<Item> itemList = new ArrayList<Item>();
				AllItemsListResponse itemListResponse = restTemplate.getForObject(Constants.url + "getAllItems",
						AllItemsListResponse.class);
				itemList = itemListResponse.getItems();

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList;
				categoryList = categoryListResponse.getmCategoryList();

				model.addObject("catList", categoryList);

				model.addObject("routeList", allRouteListResponse.getRoute());
				model.addObject("itemList", itemList);
				model.addObject("todaysDate", todaysDate);
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			} catch (Exception e) {
				System.out.println("Exce inshowGGReportDateWise " + e.getMessage());
				e.printStackTrace();
			}
		}

		return model;
	}

	List<GGReportGrpByItemId> grnGvnGrpByFrList = new ArrayList<>();

	@RequestMapping(value = "/getGrnGvnByGrpByItem", method = RequestMethod.GET)
	@ResponseBody
	public List<GGReportGrpByItemId> getGrnGvnByGrpByItem(HttpServletRequest request, HttpServletResponse response) {

		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			String frIdString = "";
			String itemIdString = "";

			System.out.println("inside getGrnGvnByDatewise ajax call");

			frIdString = request.getParameter("fr_id_list");
			itemIdString = request.getParameter("item_id_list");
			String fromDate = request.getParameter("from_date");
			String toDate = request.getParameter("to_date");

			String isGrn = request.getParameter("is_grn");

			String grnType;
			if (isGrn.equalsIgnoreCase("2")) {

				System.err.println("Is Grn =2");
				grnType = "1" + "," + "0";

				map.add("isGrn", grnType);
			} else {
				System.err.println("Is Grn not =2");
				grnType = isGrn;
				map.add("isGrn", isGrn);

			}

			System.out.println("fromDate= " + fromDate);

			boolean isAllFrSelected = false;

			frIdString = frIdString.substring(1, frIdString.length() - 1);
			frIdString = frIdString.replaceAll("\"", "");

			List<String> franchIds = new ArrayList();
			franchIds = Arrays.asList(frIdString);

			itemIdString = itemIdString.substring(1, itemIdString.length() - 1);
			itemIdString = itemIdString.replaceAll("\"", "");

			/*
			 * List<String> itemIds = new ArrayList(); itemIds =
			 * Arrays.asList(itemIdString);
			 * 
			 * System.out.println("fr Id ArrayList " + franchIds.toString());
			 * 
			 * System.out.println("Item Id ArrayList " + itemIds.toString());
			 */

			if (franchIds.contains("-1")) {
				isAllFrSelected = true;

			}

			if (itemIdString.contains("-1")) {

				CategoryListResponse categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);
				List<MCategoryList> categoryList;
				categoryList = categoryListResponse.getmCategoryList();

				StringBuilder sbForCatId = new StringBuilder();
				for (int i = 0; i < categoryList.size(); i++) {

					sbForCatId = sbForCatId.append(categoryList.get(i).getCatId().toString() + ",");

				}

				String sbForCatIdWise = sbForCatId.toString();
				itemIdString = sbForCatIdWise.substring(0, sbForCatIdWise.length() - 1);
				System.out.println("fr Id Route WISE = " + sbForCatIdWise);

			} // end of if

			map = new LinkedMultiValueMap<String, Object>();
			if (isAllFrSelected) {

				System.out.println("Inside IF  is All fr Selected " + isAllFrSelected);

				map.add("frIdList", -1);
				map.add("catIdList", -1);
				// model.addObject("billHeadersList",billHeadersListForPrint);

			} else { // few franchisee selected

				System.out.println("Inside Else: Few Fr Selected ");
				map.add("frIdList", frIdString);
				map.add("catIdList", itemIdString);

			}

			map.add("fromDate", DateConvertor.convertToYMD(fromDate));
			map.add("toDate", DateConvertor.convertToYMD(toDate));
			map.add("isGrn", grnType);

			ParameterizedTypeReference<List<GGReportGrpByItemId>> typeRef = new ParameterizedTypeReference<List<GGReportGrpByItemId>>() {
			};
			ResponseEntity<List<GGReportGrpByItemId>> responseEntity = restTemplate
					.exchange(Constants.url + "gGReportGrpByItemId", HttpMethod.POST, new HttpEntity<>(map), typeRef);

			grnGvnGrpByFrList = responseEntity.getBody();

			System.err.println("List " + grnGvnGrpByFrList.toString());

			List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

			ExportToExcel expoExcel = new ExportToExcel();
			List<String> rowData = new ArrayList<String>();

			rowData.add("Sr. No.");
			rowData.add("Type");
			rowData.add("Item Name");
			rowData.add("Req Qty");
			rowData.add("Req Value");
			rowData.add("Apr Qty");
			rowData.add("Apr Value");

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);
			List<GGReportGrpByItemId> excelItems = grnGvnGrpByFrList;
			for (int i = 0; i < excelItems.size(); i++) {
				expoExcel = new ExportToExcel();
				rowData = new ArrayList<String>();
				rowData.add("" + (i + 1));

				String type;
				if (excelItems.get(i).getIsGrn() == 1) {
					type = "GRN";

				} else {
					type = "GVN";
				}
				rowData.add(type);
				rowData.add(excelItems.get(i).getItemName());
				rowData.add("" + excelItems.get(i).getReqQty());
				rowData.add("" + excelItems.get(i).getTotalAmt());
				rowData.add("" + excelItems.get(i).getAprQty());

				rowData.add("" + excelItems.get(i).getAprGrandTotal());

				expoExcel.setRowData(rowData);
				exportToExcelList.add(expoExcel);

			}

			HttpSession session = request.getSession();
			session.setAttribute("exportExcelList", exportToExcelList);
			session.setAttribute("excelName", "grnGvnReport");

		} catch (Exception e) {

			System.out.println("Ex in getting /getGrnGvnByGrpByFr List  Ajax call" + e.getMessage());
			e.printStackTrace();
		}

		return grnGvnGrpByFrList;

	}

	@RequestMapping(value = "/showGrnGvnItemwiseReportPdf/{fromDate}/{toDate}", method = RequestMethod.GET)
	public void showGrnGvnItemwiseReportPdf(@PathVariable("fromDate") String fromDate,
			@PathVariable("toDate") String toDate, HttpServletRequest request, HttpServletResponse response)
			throws FileNotFoundException {
		BufferedOutputStream outStream = null;
		System.out.println("Inside Pdf showPOReportPdf");
		Document document = new Document(PageSize.A4);

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();

		System.out.println("time in Gen Bill PDF ==" + dateFormat.format(cal.getTime()));
		String FILE_PATH = Constants.REPORT_SAVE;
		File file = new File(FILE_PATH);

		PdfWriter writer = null;

		FileOutputStream out = new FileOutputStream(FILE_PATH);
		try {
			writer = PdfWriter.getInstance(document, out);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		PdfPTable table = new PdfPTable(7);
		try {
			System.out.println("Inside PDF Table try");
			table.setWidthPercentage(100);
			table.setWidths(new float[] { 2.4f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f, 3.2f });
			Font headFont = new Font(FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
			Font headFont1 = new Font(FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
			headFont1.setColor(BaseColor.WHITE);
			Font f = new Font(FontFamily.TIMES_ROMAN, 12.0f, Font.UNDERLINE, BaseColor.BLUE);

			PdfPCell hcell = new PdfPCell();
			hcell.setBackgroundColor(BaseColor.PINK);

			hcell.setPadding(3);
			hcell = new PdfPCell(new Phrase("Sr.No.", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Type", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Item Name", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Req Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Req Value ", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Apr Qty", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase("Apr Value", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);
			int index = 0;
			float totalReqQty = 0;
			float totalReqAmt = 0;
			float totalAprQty = 0;
			float totalAprValue = 0;
			for (GGReportGrpByItemId work : grnGvnGrpByFrList) {
				index++;
				PdfPCell cell;

				totalReqQty = totalReqQty + work.getReqQty();
				totalReqAmt = totalReqAmt + work.getTotalAmt();
				totalAprQty = totalAprQty + work.getAprQty();
				totalAprValue = totalAprValue + work.getAprGrandTotal();

				cell = new PdfPCell(new Phrase(String.valueOf(index), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPadding(3);
				cell.setPaddingRight(2);
				table.addCell(cell);

				String isGrnGvn;

				if (work.getIsGrn() == 1) {
					isGrnGvn = "GRN";

				} else if (work.getIsGrn() == 0) {
					isGrnGvn = "GVN";
				} else {
					isGrnGvn = "Cust Complaint";
				}

				cell = new PdfPCell(new Phrase("" + isGrnGvn, headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + work.getItemName(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + work.getReqQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + work.getTotalAmt(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + work.getAprQty(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

				cell = new PdfPCell(new Phrase("" + work.getAprGrandTotal(), headFont));
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				cell.setPaddingRight(2);
				cell.setPadding(3);
				table.addCell(cell);

			}

			hcell = new PdfPCell();

			hcell.setPadding(3);
			hcell = new PdfPCell(new Phrase("", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase("Total", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase(roundUp(totalReqQty) + "", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase(roundUp(totalReqAmt) + "", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			hcell = new PdfPCell(new Phrase(roundUp(totalAprQty) + "", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);
			hcell = new PdfPCell(new Phrase(roundUp(totalAprValue) + "", headFont1));
			hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
			hcell.setBackgroundColor(BaseColor.PINK);

			table.addCell(hcell);

			document.open();
			Paragraph name = new Paragraph("Monginis Patana\n", f);
			name.setAlignment(Element.ALIGN_CENTER);
			document.add(name);
			document.add(new Paragraph(" "));
			Paragraph company = new Paragraph("Itemwise Grn/Gvn Report\n", f);
			company.setAlignment(Element.ALIGN_CENTER);
			document.add(company);
			document.add(new Paragraph(" "));

			DateFormat DF = new SimpleDateFormat("dd-MM-yyyy");
			String reportDate = DF.format(new Date());

			document.add(table);

			int totalPages = writer.getPageNumber();

			System.out.println("Page no " + totalPages);

			document.close();

			if (file != null) {

				String mimeType = URLConnection.guessContentTypeFromName(file.getName());

				if (mimeType == null) {

					mimeType = "application/pdf";

				}

				response.setContentType(mimeType);

				response.addHeader("content-disposition", String.format("inline; filename=\"%s\"", file.getName()));

				response.setContentLength((int) file.length());

				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				try {
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				} catch (IOException e) {
					System.out.println("Excep in Opening a Pdf File");
					e.printStackTrace();
				}
			}

		} catch (DocumentException ex) {

			System.out.println("Pdf Generation Error: " + ex.getMessage());

			ex.printStackTrace();

		}

	}

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

}
