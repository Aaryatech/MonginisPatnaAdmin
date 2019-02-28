/*package com.ats.adminpanel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.AllRoutesListResponse;
import com.ats.adminpanel.model.ExportToExcel;
import com.ats.adminpanel.model.RegularSpCkOrder;
import com.ats.adminpanel.model.RegularSpCkOrdersResponse;
import com.ats.adminpanel.model.Route;
import com.ats.adminpanel.model.SpCakeOrdersBeanResponse;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteId;
import com.ats.adminpanel.model.franchisee.FrNameIdByRouteIdResponse;
import com.ats.adminpanel.model.franchisee.FranchiseeList;
import com.ats.adminpanel.model.franchisee.Menu;


@Controller
@Scope("session")
public class OrdController {

	
	public List<Menu> menuList;
	RegularSpCkOrdersResponse regOrderListResponse;
	public List<FranchiseeList> franchiseeList = new ArrayList<FranchiseeList>();

	boolean isDelete = false;
	public String frIds = null;
	public String prodDate = null;
	public int routeId = 0;
	public String menuId = null;
	@RequestMapping(value = "/regularSpCkOrderProcessd", method = RequestMethod.GET)
	public @ResponseBody List<RegularSpCkOrder> regularSpCkOrderProcessd(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView model = null;
		System.out.println("/inside search reg sp cake order process  ");
		List<RegularSpCkOrder> regularSpCkOrderList = new ArrayList<RegularSpCkOrder>();

		model = new ModelAndView("orders/regularsporders");

		// model.addObject("menuList", franchiseeAndMenuList.getAllMenu());
		model.addObject("isDelete", 0);

		try {

			menuId = request.getParameter("menu_id");

			frIds = request.getParameter("fr_id");
			System.out.println("frIds:" + frIds);
			routeId = Integer.parseInt(request.getParameter("selectRoute"));
			System.out.println("routeId:" + routeId);
			prodDate = request.getParameter("prod_date");
			System.out.println("prodDate:" + prodDate);
			String strFrId = "";
			String strMenuId = "";

			List<String> frIdList = new ArrayList<>();
			if (frIds != null) {
				frIds = frIds.substring(1, frIds.length() - 1);
				frIds = frIds.replaceAll("\"", "");
				System.out.println("frIds  New =" + frIds);

				frIdList = (List) Arrays.asList(frIds);

				StringBuilder sb = new StringBuilder();

				
			}

			List<String> menuIdList = new ArrayList<>();
			if (menuId != null) {

				menuId = menuId.substring(1, menuId.length() - 1);
				menuId = menuId.replaceAll("\"", "");
				System.out.println("frIds  New =" + menuId);
				menuIdList = (List) Arrays.asList(menuId);

				StringBuilder sb = new StringBuilder();

				
			}

			System.out.println("menu array is=" + strMenuId);

			System.out.println("frid array is=" + strFrId);
			RestTemplate restTemplate = new RestTemplate();

			List<FranchiseeList> selectedFrList = new ArrayList<>();
			List<Menu> selectedMenuList = new ArrayList<>();
			List<Menu> remMenuList = new ArrayList<Menu>();
			remMenuList = menuList;

			List<FranchiseeList> remFrList = new ArrayList<FranchiseeList>();
			
			AllRoutesListResponse allRouteListResponse = restTemplate.getForObject(Constants.url + "showRouteList",
					AllRoutesListResponse.class);

			List<Route> routeList = new ArrayList<Route>();

			routeList = allRouteListResponse.getRoute();
			model.addObject("routeList", routeList);
			model.addObject("routeId", routeId);
			model.addObject("todayDate", prodDate);
			model.addObject("frIdList", selectedFrList);
			model.addObject("franchiseeList", remFrList);

			model.addObject("menuIdList", selectedMenuList);
			model.addObject("menuList", remMenuList);

			if (routeId != 0) {

				MultiValueMap<String, Object> mvm = new LinkedMultiValueMap<String, Object>();

				mvm.add("routeId", routeId);

				FrNameIdByRouteIdResponse frNameId = restTemplate.postForObject(Constants.url + "getFrNameIdByRouteId",
						mvm, FrNameIdByRouteIdResponse.class);

				List<FrNameIdByRouteId> frNameIdByRouteIdList = frNameId.getFrNameIdByRouteIds();

				System.out.println("route wise franchisee " + frNameIdByRouteIdList.toString());

				StringBuilder sbForRouteFrId = new StringBuilder();
				for (int i = 0; i < frNameIdByRouteIdList.size(); i++) {

					sbForRouteFrId = sbForRouteFrId.append(frNameIdByRouteIdList.get(i).getFrId().toString() + ",");

				}

				String strFrIdRouteWise = sbForRouteFrId.toString();
				strFrId = strFrIdRouteWise.substring(0, strFrIdRouteWise.length() - 1);
				System.out.println("fr Id Route WISE = " + strFrId);

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();//

				map.add("frId", frIds);
				map.add("prodDate", prodDate);
				map.add("menuId", menuId);

				RestTemplate restTemp = new RestTemplate();

				regOrderListResponse = restTemp.postForObject(Constants.url + "getRegSpCkOrderList", map,
						RegularSpCkOrdersResponse.class);

				regularSpCkOrderList = new ArrayList<RegularSpCkOrder>();
				regularSpCkOrderList = regOrderListResponse.getRegularSpCkOrdersList();

				System.out.println("order list count is" + regularSpCkOrderList.size());
				model.addObject("regularSpCkOrderList", regularSpCkOrderList);

				if (menuId.toString().equals("-1")) {
					System.out.println("all menu selected");
					model.addObject("menuIdList", menuList);
				}
			} else if (frIds.toString().equals("0")) {
				System.out.println("all fr selected");
				model.addObject("frIdList", franchiseeList);
				if (menuId.toString().equals("-1")) {
					System.out.println("all menu selected");
					model.addObject("menuIdList", menuList);
				}
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				map.add("menuId", menuId);
				map.add("prodDate", prodDate);

				RestTemplate restTemplate1 = new RestTemplate();

				regOrderListResponse = restTemplate1.postForObject(Constants.url + "getAllFrRegSpCakeOrders", map,
						RegularSpCkOrdersResponse.class);

				regularSpCkOrderList = new ArrayList<RegularSpCkOrder>();
				regularSpCkOrderList = regOrderListResponse.getRegularSpCkOrdersList();

				System.out.println("order list count is" + regularSpCkOrderList.toString());
				model.addObject("regularSpCkOrderList", regularSpCkOrderList);

			} // end of if
			else {

				System.out.println("few fr selected");

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();//

				map.add("frId", frIds);
				map.add("prodDate", prodDate);
				map.add("menuId", menuId);
				RestTemplate restTemp = new RestTemplate();

				model.addObject("menuIdList", menuList);
				regOrderListResponse = restTemp.postForObject(Constants.url + "getRegSpCkOrderList", map,
						RegularSpCkOrdersResponse.class);

				regularSpCkOrderList = new ArrayList<RegularSpCkOrder>();
				regularSpCkOrderList = regOrderListResponse.getRegularSpCkOrdersList();

				System.out.println("order list count is" + regularSpCkOrderList.size());
				model.addObject("regularSpCkOrderList", regularSpCkOrderList);

			} // end of else
				// model.addObject("menuId", 0);
		} catch (Exception e) {
			System.out.println("exception in order display" + e.getMessage());
		}

		List<ExportToExcel> exportToExcelList = new ArrayList<ExportToExcel>();

		ExportToExcel expoExcel = new ExportToExcel();
		List<String> rowData = new ArrayList<String>();

		rowData.add("Franchisee Name");

		rowData.add("Item Id");
		rowData.add("Item Name");

		rowData.add("Mrp");
		rowData.add("Rate");

		rowData.add("Quantity");

		rowData.add("Sub Total");

		expoExcel.setRowData(rowData);
		exportToExcelList.add(expoExcel);
		for (int i = 0; i < regOrderListResponse.getRegularSpCkOrdersList().size(); i++) {
			expoExcel = new ExportToExcel();
			rowData = new ArrayList<String>();

			rowData.add(regOrderListResponse.getRegularSpCkOrdersList().get(i).getFrName());

			rowData.add("" + regOrderListResponse.getRegularSpCkOrdersList().get(i).getId());

			rowData.add(regOrderListResponse.getRegularSpCkOrdersList().get(i).getItemName());

			rowData.add("" + regOrderListResponse.getRegularSpCkOrdersList().get(i).getMrp());
			rowData.add("" + regOrderListResponse.getRegularSpCkOrdersList().get(i).getRate());
			rowData.add("" + regOrderListResponse.getRegularSpCkOrdersList().get(i).getQty());
			rowData.add("" + regOrderListResponse.getRegularSpCkOrdersList().get(i).getRspSubTotal());

			expoExcel.setRowData(rowData);
			exportToExcelList.add(expoExcel);

		}

		HttpSession session = request.getSession();
		session.setAttribute("exportExcelList", exportToExcelList);
		session.setAttribute("excelName", "RegSpCakeOrders");
		System.err.println("res Ajx " + regularSpCkOrderList.toString());
		return regularSpCkOrderList;
	}
}
*/