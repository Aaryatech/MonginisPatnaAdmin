package com.ats.adminpanel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.model.ErrorMessage;
import com.ats.adminpanel.model.RawMaterial.RawMaterialUomList;
import com.ats.adminpanel.model.franchisee.SubCategory;
import com.ats.adminpanel.model.gatesale.GateSaleBillDetail;
import com.ats.adminpanel.model.gatesale.GateSaleBillHeader;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.Item;
import com.ats.adminpanel.model.item.MCategoryList;
import com.ats.adminpanel.model.taxrate.GetTaxRate;
import com.ats.adminpanel.model.taxrate.TaxRate;


//Inserting damaged qty/eaten cake by staff cakes into gate sale table
@Controller
public class GateSaleController {
	
	@RequestMapping(value = "/showAddGateSaleQty", method = RequestMethod.GET)
	public ModelAndView showAddGateSaleQty(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		try {
			
			  List<MCategoryList> mCategoryList = null;

			  CategoryListResponse categoryListResponse;

			
			model = new ModelAndView("gatesale/addgatesale");
			System.out.println("Add Tax Rate Request");

			RestTemplate restTemplate = new RestTemplate();
			// CategoryListResponse
			categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			mCategoryList = new ArrayList<MCategoryList>();
			mCategoryList = categoryListResponse.getmCategoryList();
			
			
			model.addObject("mCategoryList", mCategoryList);

			
		} catch (Exception e) {
			
			System.err.println("Exce in showAddGateSaleQty show " + e.getMessage());
			e.printStackTrace();
			
		}
		return model;
	}
	
	@RequestMapping(value = "/getItemBySubCat", method = RequestMethod.GET)
	public @ResponseBody  List<Item>  getItemBySubCat(@RequestParam(value = "subCatId", required = true) int subCatId) {
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		
		List<Item> itemList = new ArrayList<Item>();
		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			
			RestTemplate restTemplate = new RestTemplate();
			// CategoryListResponse
			
			map.add("subCatId", subCatId);
			itemList = restTemplate.postForObject(Constants.url + "getItemsBySubCatId",
					map,List.class);

		} catch (Exception e) {
			
			System.err.println("Exce in getSubCateListByCatId Ajax " + e.getMessage());
			e.printStackTrace();
			
		}
		return itemList;
	}
	
	//damagedItemList
	//addGateSaleItem
	
	
	@RequestMapping(value = "/addGateSaleItem", method = RequestMethod.POST)
	public String addGateSaleItem(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		try {
			
			  
			RestTemplate restTemplate = new RestTemplate();
			
			int catId=Integer.parseInt(request.getParameter("item_grp1"));
			
			int subCatId=Integer.parseInt(request.getParameter("item_grp2"));
			
			int itemId=Integer.parseInt(request.getParameter("item_name"));
			
			int damQty=Integer.parseInt(request.getParameter("dam_qty"));
			
			
			GateSaleBillHeader gateSaleHeader=new GateSaleBillHeader();
			
			List<GateSaleBillDetail> saleBillDetailList=new ArrayList<GateSaleBillDetail>();
			
			GateSaleBillDetail detail=new GateSaleBillDetail();
			
			gateSaleHeader.setApprovedDate(new Date());
			gateSaleHeader.setBillDate(new Date());
			gateSaleHeader.setCollectedDate(new Date());
			gateSaleHeader.setCustName("");
			gateSaleHeader.setInvoiceNo("");
			
			detail.setItemId(itemId);
			detail.setItemQty(damQty);
			
			saleBillDetailList.add(detail);
			
			gateSaleHeader.setGateSaleBillDetailList(saleBillDetailList);
			
			System.err.println("Detail List  " +gateSaleHeader.toString());
			
			ErrorMessage gateSaleBillRes=restTemplate.postForObject(Constants.url + "gatesale/insertGateSaleBill",gateSaleHeader, ErrorMessage.class);

			
		} catch (Exception e) {
			
			System.err.println("Exce in addGateSaleItem  " + e.getMessage());
			e.printStackTrace();
			
		}
		return "";
	}
	
}
