package com.ats.adminpanel.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.AccessControll;
import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.Orders;
import com.ats.adminpanel.model.SpCakeResponse;
import com.ats.adminpanel.model.SpecialCake;
import com.ats.adminpanel.model.RawMaterial.ItemDetail;
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.franchisee.CommonConf;
import com.ats.adminpanel.model.franchisee.FranchiseeAndMenuList;
import com.ats.adminpanel.model.franchisee.FranchiseeList;
import com.ats.adminpanel.model.franchisee.Menu;
import com.ats.adminpanel.model.item.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@Scope("session")
public class ManualOrderController {
	List<Orders> orderList = new ArrayList<Orders>();

	FranchiseeAndMenuList franchiseeAndMenuList;

	@RequestMapping(value = "/showManualOrder", method = RequestMethod.GET)
	public ModelAndView showManualOrder(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showManualOrder", "showManualOrder", "1", "0", "0", "0", newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {

			model = new ModelAndView("orders/manualOrder");
			try {
				RestTemplate restTemplate = new RestTemplate();
				franchiseeAndMenuList = restTemplate.getForObject(Constants.url + "getFranchiseeAndMenu",
						FranchiseeAndMenuList.class);
				orderList = new ArrayList<Orders>();
				System.out.println("Franchisee Response " + franchiseeAndMenuList.getAllFranchisee());

				model.addObject("allFranchiseeAndMenuList", franchiseeAndMenuList);

			} catch (Exception e) {
				System.out.println("Franchisee Controller Exception " + e.getMessage());
			}
		}
		return model;

	}

	// METHOD)-------------------------
	@RequestMapping(value = "/getMenuForOrder", method = RequestMethod.GET)
	public @ResponseBody List<Menu> findAllMenu(@RequestParam(value = "fr_id", required = true) int frId) {

		List<Menu> menuList = new ArrayList<Menu>();
		List<Menu> confMenuList = new ArrayList<Menu>();
		try {
			RestTemplate restTemplate = new RestTemplate();

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("frId", frId);
			// Calling Service to get Configured Menus
			Integer[] configuredMenuId = restTemplate.postForObject(Constants.url + "getConfiguredMenuId", map,
					Integer[].class);

			ArrayList<Integer> configuredMenuList = new ArrayList<Integer>(Arrays.asList(configuredMenuId));

			menuList = franchiseeAndMenuList.getAllMenu();

			for (Menu menu : menuList) {
				if (menu.getMainCatId() != 5 && menu.getMenuId() != 42) {
					for (int i = 0; i < configuredMenuList.size(); i++) {
						if (menu.getMenuId() == configuredMenuList.get(i)) {
							confMenuList.add(menu);
						}

					}
				}
			}
			System.out.println("configuredMenuList:" + confMenuList.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return confMenuList;
	}
	// ----------------------------------------END--------------------------------------------
	
	/* @RequestMapping(value = "/getItemsOfMenuId", method = RequestMethod.GET)
	 public @ResponseBody List<CommonConf> commonItemById(@RequestParam(value ="menuId", required = true) int menuId) {
	
	     RestTemplate restTemplate = new RestTemplate();
	  
	  List<Menu> menuList = franchiseeAndMenuList.getAllMenu();
	  Menu frMenu = new  Menu(); 
	  for (Menu menu : menuList) { 
		  if (menu.getMenuId() == menuId) { 
			  frMenu= menu; 
			  break; 
			  }
		  }
	  int selectedCatId = frMenu.getMainCatId();
	
	  List<SpecialCake> specialCakeList = new ArrayList<SpecialCake>();
	 
	  List<CommonConf> commonConfList = new ArrayList<CommonConf>();
	  
	  if (selectedCatId == 5) { 
		  SpCakeResponse spCakeResponse=restTemplate.getForObject(Constants.url + "showSpecialCakeList", SpCakeResponse.class);
	
	     specialCakeList = spCakeResponse.getSpecialCake();
	  
	  for (SpecialCake specialCake : specialCakeList) { 
		  CommonConf commonConf = new CommonConf(); 
		  commonConf.setId(specialCake.getSpId());
	      commonConf.setName(specialCake.getSpCode() + "-" + specialCake.getSpName());
	      commonConfList.add(commonConf);
	  }
		
	  } else {
		  
	  MultiValueMap<String, Object> map = new LinkedMultiValueMap<String,
	  Object>(); map.add("itemGrp1", selectedCatId);
	  
	  Item[] item = restTemplate.postForObject(Constants.url + "getItemsByCatId", map, Item[].class); 
	  ArrayList<Item> itemList = new  ArrayList<Item>(Arrays.asList(item)); 
	  
	  for (Item items : itemList) { 
		  CommonConf commonConf = new CommonConf();
	  commonConf.setId(items.getId()); 
	  commonConf.setName(items.getItemName());
	  commonConfList.add(commonConf); 
	  } 
	  }
	  
	  return commonConfList;
	 }*/
	
	@RequestMapping(value = "/getItemsOfMenuId", method = RequestMethod.GET)
	public @ResponseBody List<Orders> commonItemById(@RequestParam(value = "menuId", required = true) int menuId,
			@RequestParam(value = "frId", required = true) int frId) {

		System.out.println("menuId " + menuId+"FrId"+frId);

		RestTemplate restTemplate = new RestTemplate();
		orderList=new ArrayList<>();
		List<Menu> menuList = franchiseeAndMenuList.getAllMenu();
		Menu frMenu = new Menu();
		for (Menu menu : menuList) {
			if (menu.getMenuId() == menuId) {
				frMenu = menu;
				break;
			}
		}
		int selectedCatId = frMenu.getMainCatId();

		System.out.println("Finding Item List for Selected CatId=" + selectedCatId);

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		map.add("itemGrp1", selectedCatId);
		Item[] itemRes = restTemplate.postForObject(Constants.url + "getItemsByCatId", map, Item[].class);
		ArrayList<Item> itemList = new ArrayList<Item>(Arrays.asList(itemRes));
		System.out.println("Filter Item List " + itemList.toString());

		FranchiseeAndMenuList franchiseeListRes = restTemplate.getForObject(Constants.url + "getFranchiseeAndMenu",
				FranchiseeAndMenuList.class);
		System.out.println("franchiseeList" + franchiseeListRes.toString());
		FranchiseeList franchiseeList = null;

		for (int i = 0; i < franchiseeListRes.getAllFranchisee().size(); i++) {
			if (franchiseeListRes.getAllFranchisee().get(i).getFrId() == frId) {
				franchiseeList = franchiseeListRes.getAllFranchisee().get(i);
			}
		}
		System.err.println("franchiseeListes" +franchiseeList.toString());

		for (Item item : itemList) {

			Orders order = new Orders();

			if (franchiseeList.getFrRateCat() == 1) {
				order.setOrderRate(item.getItemRate1());
				order.setOrderMrp(item.getItemMrp1());
			} else if (franchiseeList.getFrRateCat() == 2) {
				order.setOrderRate(item.getItemRate2());
				order.setOrderMrp(item.getItemMrp2());
			} else {
				order.setOrderRate(item.getItemRate3());
				order.setOrderMrp(item.getItemMrp3());
			}
			int frGrnTwo = franchiseeList.getGrnTwo();
			System.err.println("frGrnTwo" + frGrnTwo + "item.getGrnTwo()" + item.getGrnTwo());
			if (item.getGrnTwo() == 1) {

				if (frGrnTwo == 1) {

					order.setGrnType(1);

				} else {

					order.setGrnType(0);
				}
			} // end of if

			else {
				if (item.getGrnTwo() == 2) {
					order.setGrnType(2);

				} else {
					order.setGrnType(0);
				}
			} // end of else
			if (menuId == 29 || menuId == 30 || menuId == 42 || menuId == 43 || menuId == 44 || menuId == 47) {

				order.setGrnType(3);

			}
			// for push grn
			if (menuId == 48) {

				order.setGrnType(4);
			}

			Date today = new Date();
			Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
			java.sql.Date sqlCurrDate = new java.sql.Date(today.getTime());
			java.sql.Date sqlTommDate = new java.sql.Date(tomorrow.getTime());

			order.setOrderId(0);
			order.setItemId(String.valueOf(item.getId()));
			order.setItemName(item.getItemName() + "--[" + franchiseeList.getFrCode() + "]");
			order.setFrId(frId);
			if (menuId == 84 || menuId == 87||menuId == 88||menuId == 89) {
				order.setDeliveryDate(sqlCurrDate);
			} else {
				order.setDeliveryDate(sqlTommDate);
			}
			order.setMinQty(item.getMinQty());
			order.setIsEdit(0);
			order.setEditQty(0);/// set order qty on submit
			order.setIsPositive(1);
			order.setMenuId(menuId);
			order.setOrderDate(sqlCurrDate);
			order.setOrderDatetime("" + sqlCurrDate);
			order.setUserId(0);
			order.setOrderQty(0);
			order.setOrderStatus(0);
			order.setOrderType(item.getItemGrp1());
			order.setOrderSubType(item.getItemGrp2());
			order.setProductionDate(sqlCurrDate);
			order.setRefId(item.getId());

			orderList.add(order);

		}
		System.out.println("------------------------orderList"+orderList.toString());

		return orderList;
	}

	@RequestMapping(value = "/insertItem", method = RequestMethod.GET)
	public @ResponseBody List<Orders> insertItem(HttpServletRequest request, HttpServletResponse response) {

		try {

			int itemId = Integer.parseInt(request.getParameter("itemId"));
			System.out.println("itemId" + itemId);

			int frId = Integer.parseInt(request.getParameter("frId"));
			System.out.println("frId" + frId);

			int menuId = Integer.parseInt(request.getParameter("menuId"));
			System.out.println("menuId" + menuId);

			int qty = Integer.parseInt(request.getParameter("qty"));
			System.out.println("qty" + qty);

			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("id", itemId);

			Item item = restTemplate.postForObject("" + Constants.url + "getItem", map, Item.class);
			System.out.println("ItemResponse" + item);

			map = new LinkedMultiValueMap<String, Object>();

			map.add("frId", frId);

			FranchiseeList franchiseeList = restTemplate.getForObject(Constants.url + "getFranchisee?frId={frId}",
					FranchiseeList.class, frId);
			System.out.println("franchiseeList" + franchiseeList.toString());

			Orders order = new Orders();

			if (franchiseeList.getFrRateCat() == 1) {
				order.setOrderRate(item.getItemRate1());
				order.setOrderMrp(item.getItemMrp1());
			} else if (franchiseeList.getFrRateCat() == 2) {
				order.setOrderRate(item.getItemRate2());
				order.setOrderMrp(item.getItemMrp2());
			} else {
				order.setOrderRate(item.getItemRate3());
				order.setOrderMrp(item.getItemMrp3());
			}
			int frGrnTwo = franchiseeList.getGrnTwo();
			System.err.println("frGrnTwo" + frGrnTwo + "item.getGrnTwo()" + item.getGrnTwo());
			if (item.getGrnTwo() == 1) {

				if (frGrnTwo == 1) {

					order.setGrnType(1);

				} else {

					order.setGrnType(0);
				}
			} // end of if

			else {
				if (item.getGrnTwo() == 2) {
					order.setGrnType(2);

				} else {
					order.setGrnType(0);
				}
			} // end of else
			if (menuId == 29 || menuId == 30 || menuId == 42 || menuId == 43 || menuId == 44 || menuId == 47) {

				order.setGrnType(3);

			}
			// for push grn
			if (menuId == 48) {

				order.setGrnType(4);
			}

			Date today = new Date();
			Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
			java.sql.Date sqlCurrDate = new java.sql.Date(today.getTime());
			java.sql.Date sqlTommDate = new java.sql.Date(tomorrow.getTime());

			order.setOrderId(0);
			order.setItemId(String.valueOf(itemId));
			order.setItemName(item.getItemName() + "--[" + franchiseeList.getFrCode() + "]");
			order.setFrId(frId);
			if (menuId == 84 || menuId == 87||menuId == 88||menuId == 89) {
				order.setDeliveryDate(sqlCurrDate);
			} else {
				order.setDeliveryDate(sqlTommDate);
			}
			order.setIsEdit(0);
			order.setEditQty(qty);
			order.setIsPositive(1);
			order.setMenuId(menuId);
			order.setOrderDate(sqlCurrDate);
			order.setOrderDatetime("" + sqlCurrDate);
			order.setUserId(0);
			order.setOrderQty(qty);
			order.setOrderStatus(0);
			order.setOrderType(item.getItemGrp1());
			order.setOrderSubType(item.getItemGrp2());
			order.setProductionDate(sqlCurrDate);
			order.setRefId(itemId);

			orderList.add(order);

			System.out.println("orderListinserted:" + orderList.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return orderList;
	}

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}
	@RequestMapping(value = "/deleteItems", method = RequestMethod.GET)
	public @ResponseBody List<Orders> deleteItemDetail(HttpServletRequest request, HttpServletResponse response) {
		ResponseEntity<String> orderListResponse = null;
		try {

			int index = Integer.parseInt(request.getParameter("key"));
			orderList.remove(index);

			System.out.println("OrderList :" + orderList.toString());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return orderList;
	}

	/*
	 * @RequestMapping(value = "/generateManualBill", method = RequestMethod.GET)
	 * public @ResponseBody List<Orders> generateManualBill(HttpServletRequest
	 * request, HttpServletResponse response) {
	 * 
	 * List<Orders> orderListResponse=new ArrayList<>(); try { RestTemplate
	 * restTemplate = new RestTemplate(); if(orderList!=null ||
	 * !orderList.isEmpty()) { orderListResponse =
	 * restTemplate.postForObject(Constants.url + "placeOrder",
	 * orderList,List.class); orderList=new ArrayList<Orders>();
	 * System.out.println("Place Order Response" + orderListResponse.toString()); }
	 * } catch (Exception e) { e.printStackTrace(); } return orderListResponse; }
	 */
	@RequestMapping(value = "/generateManualBill", method = RequestMethod.POST)
	public String generateManualBill(HttpServletRequest request, HttpServletResponse response) {

		List<Orders> orderListResponse = new ArrayList<>();
		List<Orders> orderListSave = new ArrayList<>();

		try {
			RestTemplate restTemplate = new RestTemplate();
			if (orderList != null || !orderList.isEmpty()) {

				for (int i = 0; i < orderList.size(); i++) {
					int qty = Integer.parseInt(request.getParameter("qty" + orderList.get(i).getItemId()));
					orderList.get(i).setEditQty(qty);
					orderList.get(i).setOrderQty(qty);
					if (qty > 0) {
						orderListSave.add(orderList.get(i));
					}
				}

				orderListResponse = restTemplate.postForObject(Constants.url + "placeOrder", orderListSave, List.class);
				orderList = new ArrayList<Orders>();
				System.out.println("Place Order Response" + orderListResponse.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/showManualOrder";
	}
}
