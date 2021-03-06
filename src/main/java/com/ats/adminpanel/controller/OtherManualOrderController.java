package com.ats.adminpanel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
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
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.franchisee.CommonConf;
import com.ats.adminpanel.model.franchisee.FranchiseeAndMenuList;
import com.ats.adminpanel.model.franchisee.FranchiseeList;
import com.ats.adminpanel.model.franchisee.Menu;
import com.ats.adminpanel.model.item.Item;

@Controller
@Scope("session")
public class OtherManualOrderController {
	
	List<Orders> orderList=new ArrayList<Orders>();

	FranchiseeAndMenuList franchiseeAndMenuList;
	
	@RequestMapping(value = "/showManualOrderOther", method = RequestMethod.GET)
	public ModelAndView showManualOrderOther(HttpServletRequest request, HttpServletResponse response) {


		ModelAndView model = null;
		HttpSession session = request.getSession();

	/*	List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showManualOrderOther", "showManualOrderOther", "1", "0", "0", "0", newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("accessDenied");

		} else {*/

			model = new ModelAndView("orders/manualOrderOther");
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
		//}
		return model;

	}
	// METHOD)-------------------------
		@RequestMapping(value = "/getMenuForOrderOther", method = RequestMethod.GET)
		public @ResponseBody List<Menu> getMenuForOrderOther(@RequestParam(value = "fr_id", required = true) int frId) {

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
		@RequestMapping(value = "/getItemsOfMenuIdOther", method = RequestMethod.GET)
		public @ResponseBody List<CommonConf> getItemsOfMenuIdOther(@RequestParam(value = "menuId", required = true) int menuId) {

			System.out.println("menuId " + menuId);

			RestTemplate restTemplate = new RestTemplate();

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

			List<SpecialCake> specialCakeList = new ArrayList<SpecialCake>();

			List<CommonConf> commonConfList = new ArrayList<CommonConf>();

			if (selectedCatId == 5) {
				SpCakeResponse spCakeResponse = restTemplate.getForObject(Constants.url + "showSpecialCakeList",
						SpCakeResponse.class);
				System.out.println("SpCake Controller SpCakeList Response " + spCakeResponse.toString());

				specialCakeList = spCakeResponse.getSpecialCake();

				for (SpecialCake specialCake : specialCakeList) {
					CommonConf commonConf = new CommonConf();
					commonConf.setId(specialCake.getSpId());
					commonConf.setName(specialCake.getSpCode() + "-" + specialCake.getSpName());
					commonConfList.add(commonConf);
					System.out.println("spCommonConf" + commonConf.toString());
				}

				System.out.println("------------------------");
			} else {
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
				map.add("itemGrp1", selectedCatId);

				Item[] item = restTemplate.postForObject(Constants.url + "getItemsByCatId", map, Item[].class);
				ArrayList<Item> itemList = new ArrayList<Item>(Arrays.asList(item));
				System.out.println("Filter Item List " + itemList.toString());

				for (Item items : itemList) {
					CommonConf commonConf = new CommonConf();
					commonConf.setId(items.getId());
					commonConf.setName(items.getItemName());
					commonConfList.add(commonConf);
					System.out.println("itemCommonConf" + commonConf.toString());
				}
				System.out.println("------------------------");
			}

			return commonConfList;
		}
		@RequestMapping(value = "/insertItemOther", method = RequestMethod.GET)
		public @ResponseBody List<Orders> insertItemOther(HttpServletRequest request, HttpServletResponse response) {
			
			try {
			
			int itemId=Integer.parseInt(request.getParameter("itemId"));
			System.out.println("itemId"+itemId);
			
			int frId=Integer.parseInt(request.getParameter("frId"));
			System.out.println("frId"+frId);
			
			int menuId=Integer.parseInt(request.getParameter("menuId"));
			System.out.println("menuId"+menuId);
			
			int qty=Integer.parseInt(request.getParameter("qty"));
			System.out.println("qty"+qty);
			
			RestTemplate restTemplate = new RestTemplate();
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("id", itemId);
			
			Item item = restTemplate.postForObject("" + Constants.url + "getItem", map,Item.class);
			System.out.println("ItemResponse" + item);
			
			 map = new LinkedMultiValueMap<String, Object>();

			map.add("frId", frId);

			FranchiseeList franchiseeList = restTemplate.getForObject(Constants.url + "getFranchisee?frId={frId}",
					FranchiseeList.class, frId);
			System.out.println("franchiseeList" + franchiseeList.toString());

			Orders order=new Orders();

			if(franchiseeList.getFrRateCat()==1)
            {
    			order.setOrderRate(item.getItemRate1());
    			order.setOrderMrp(item.getItemMrp1());
            }
            else if(franchiseeList.getFrRateCat()==2)
            {
            	order.setOrderRate(item.getItemRate2());
    			order.setOrderMrp(item.getItemMrp2());
            }
            else {
            	order.setOrderRate(item.getItemRate3());
    			order.setOrderMrp(item.getItemMrp3());
            }
			int frGrnTwo=franchiseeList.getGrnTwo();
			System.err.println("frGrnTwo"+frGrnTwo+"item.getGrnTwo()"+item.getGrnTwo());
			if(item.getGrnTwo()==1) {
				
				if(frGrnTwo==1) {
				
				order.setGrnType(1);
				
				
				}else {
			
				order.setGrnType(0);
				}
			}//end of if
			
			else {	
				if(item.getGrnTwo()==2) {
				order.setGrnType(2);
				
				}
				else {
				order.setGrnType(0);
			}
			}// end of else
			if(menuId==29||menuId==30||menuId==42||menuId==43||	menuId==44||menuId==47) {
				
				order.setGrnType(3);
				
			}
			//for push grn 
			if(menuId==48) {
				
				order.setGrnType(4);
			}
			
			Date today = new Date();
			Date tomorrow = new Date(today.getTime() + (1000 * 60 * 60 * 24));
			java.sql.Date sqlCurrDate = new java.sql.Date(today.getTime()); 
			java.sql.Date sqlTommDate = new java.sql.Date(tomorrow.getTime()); 
			
			order.setOrderId(0);
			order.setItemId(String.valueOf(itemId));
			order.setItemName(item.getItemName()+"--["+franchiseeList.getFrCode()+"]");
			order.setFrId(frId);
			if (menuId == 84 || menuId == 87||menuId == 88||menuId == 89) {
			
				order.setDeliveryDate(sqlCurrDate);
			}else
			{
				order.setDeliveryDate(sqlTommDate);	
			}
			order.setIsEdit(0);
			order.setEditQty(qty);
			order.setIsPositive(1);
			order.setMenuId(menuId);
			order.setOrderDate(sqlCurrDate);
			order.setOrderDatetime(""+sqlCurrDate);
			order.setUserId(0);
			order.setOrderQty(qty);
			order.setOrderStatus(0);
			order.setOrderType(item.getItemGrp1());
			order.setOrderSubType(item.getItemGrp2());
			order.setProductionDate(sqlCurrDate);
			order.setRefId(itemId);

			orderList.add(order);
			
			System.out.println("orderListinserted:"+orderList.toString());
			
		
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return orderList;
		}
		@RequestMapping(value = "/deleteItemsOther", method = RequestMethod.GET)
		public @ResponseBody List<Orders> deleteItemsOther(HttpServletRequest request, HttpServletResponse response) {
			ResponseEntity<String> orderListResponse=null;
			try {
			     
				int index=Integer.parseInt(request.getParameter("key"));
				orderList.remove(index);
	
 			System.out.println("OrderList :"+orderList.toString());
			}
			catch (Exception e) {
				e.printStackTrace();
				
			}
			return orderList;
		}
		@RequestMapping(value = "/generateManualBillOther", method = RequestMethod.GET)
		public @ResponseBody List<Orders> generateManualBillOther(HttpServletRequest request, HttpServletResponse response) {
		
			List<Orders> orderListResponse=new ArrayList<>();
		try {
		       System.out.println("Place Order Response" + orderList.toString());

				RestTemplate restTemplate = new RestTemplate();
				if(orderList!=null || !orderList.isEmpty())
				{
					orderListResponse = restTemplate.postForObject(Constants.url + "placeOrder", orderList,List.class);
					orderList=new ArrayList<Orders>();
		       System.out.println("Place Order Response" + orderListResponse.toString());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return orderListResponse;
		}

}
