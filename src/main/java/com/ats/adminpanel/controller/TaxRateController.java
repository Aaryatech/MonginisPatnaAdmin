package com.ats.adminpanel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.ats.adminpanel.model.RawMaterial.Info;
import com.ats.adminpanel.model.RawMaterial.RawMaterialUomList;
import com.ats.adminpanel.model.franchisee.SubCategory;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.MCategoryList;
import com.ats.adminpanel.model.taxrate.GetTaxRate;
import com.ats.adminpanel.model.taxrate.TaxRate;
import com.sun.org.apache.bcel.internal.util.SyntheticRepository;
@Controller
public class TaxRateController {

	
	@RequestMapping(value = "/showAddTaxRate", method = RequestMethod.GET)
	public ModelAndView showAddTaxRate(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		try {
			
			  List<MCategoryList> mCategoryList = null;

			  CategoryListResponse categoryListResponse;

			
			model = new ModelAndView("taxrate/addtaxrate");
			System.out.println("Add Tax Rate Request");

			RestTemplate restTemplate = new RestTemplate();
			// CategoryListResponse
			categoryListResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
					CategoryListResponse.class);
			mCategoryList = new ArrayList<MCategoryList>();
			mCategoryList = categoryListResponse.getmCategoryList();
			
			
			RawMaterialUomList rawMaterialUomList=restTemplate.getForObject(Constants.url + "/rawMaterial/getRmUomList",RawMaterialUomList.class);
			
			model.addObject("rmUomList", rawMaterialUomList.getRawMaterialUom());
			
			model.addObject("mCategoryList", mCategoryList);
			
			GetTaxRate[] getTaxRateList=restTemplate.getForObject(Constants.url + "getTaxRateList",GetTaxRate[].class);

			List<GetTaxRate> taxRateList=new ArrayList<>(Arrays.asList(getTaxRateList));
			
			model.addObject("taxRateList", taxRateList);
			
			System.err.println("Taxt Rate List  " +taxRateList.toString());
			
			
		} catch (Exception e) {
			
			System.err.println("Exce in showAddTaxRate show " + e.getMessage());
			e.printStackTrace();
			
		}
		return model;
	}
	
	@RequestMapping(value = "/getSubCateListByCatId", method = RequestMethod.GET)
	public @ResponseBody  List<SubCategory>  getSubCateListByCatId(@RequestParam(value = "catId", required = true) int catId) {
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		
		List<SubCategory> subCatList = new ArrayList<SubCategory>();
		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			
			RestTemplate restTemplate = new RestTemplate();
			// CategoryListResponse
			
			map.add("catId", catId);
			subCatList = restTemplate.postForObject(Constants.url + "getSubCateListByCatId",
					map,List.class);
			

		} catch (Exception e) {
			
			System.err.println("Exce in getSubCateListByCatId Ajax " + e.getMessage());
			e.printStackTrace();
			
		}
		return subCatList;
	}
	
	
	//taxRateList
	
	// submit
	@RequestMapping(value = "/addTaxRateForSubCatId", method = RequestMethod.POST)
	public String addTaxRateForSubCatId(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView model = null;
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		try {
			
			System.out.println("Add addTaxRateForSubCatId Insert Request");

			RestTemplate restTemplate = new RestTemplate();
			
			int catId=Integer.parseInt(request.getParameter("item_grp1"));
			
			int subCatId=Integer.parseInt(request.getParameter("item_grp2"));
			
			String hsnCode=request.getParameter("hsn_code");
			
			int uomId=Integer.parseInt(request.getParameter("uom"));
			
			float cgstPer=Float.parseFloat(request.getParameter("cgst_per"));
			
			float sgstPer=Float.parseFloat(request.getParameter("sgst_per"));
			
			float igstPer=Float.parseFloat(request.getParameter("igst_per"));
			
			
			TaxRate taxRate=new TaxRate();
			
			taxRate.setCatId(catId);
			taxRate.setCgstPer(cgstPer);
			taxRate.setDelStatus(0);
			taxRate.setHsnCode(hsnCode);
			taxRate.setIgstPer(igstPer);
			taxRate.setSgstPer(sgstPer);
			taxRate.setSubCatId(subCatId);
			taxRate.setUom(uomId);
			
			TaxRate taxRateRespnse=restTemplate.postForObject(Constants.url + "/saveTaxRate",taxRate, TaxRate.class);
			System.err.println("taxRateRespnse  "+taxRateRespnse.toString());
			
		} catch (Exception e) {
			
			System.err.println("Exce in addTaxRateForSubCatId show " + e.getMessage());
			e.printStackTrace();
			
		}
		return "redirect:/showAddTaxRate";
	}
	
	
	
	
	@RequestMapping(value = "/deleteTaxRate/{taxRateId}", method = RequestMethod.GET)
	public String deleteTaxRate(HttpServletRequest request, HttpServletResponse response,@PathVariable("taxRateId")int taxRateId) {
		ModelAndView model = null;
	//	Constants.mainAct =1;
		//Constants.subAct =4;
		try {
			
			System.out.println("Add addTaxRateForSubCatId Insert Request");

			RestTemplate restTemplate = new RestTemplate();
			
			
			TaxRate taxRate=new TaxRate();
			
			taxRate.setDelStatus(1);
			taxRate.setTaxRateId(taxRateId);
			
			TaxRate taxRateRespnse=restTemplate.postForObject(Constants.url + "/saveTaxRate",taxRate, TaxRate.class);
			System.err.println("taxRateRespnse  "+taxRateRespnse.toString());
			
		} catch (Exception e) {
			
			System.err.println("Exce in deleteTaxRate delete " + e.getMessage());
			e.printStackTrace();
			
		}
		return "redirect:/showAddTaxRate";
	}
	
	
	
}
