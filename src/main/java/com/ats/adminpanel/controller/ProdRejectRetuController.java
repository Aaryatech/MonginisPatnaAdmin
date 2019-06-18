package com.ats.adminpanel.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.ats.adminpanel.commons.DateConvertor;
import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.item.AllItemsListResponse;
import com.ats.adminpanel.model.item.CategoryListResponse;
import com.ats.adminpanel.model.item.Item;
import com.ats.adminpanel.model.item.MCategoryList;
import com.ats.adminpanel.model.production.GetOrderItemQty;
import com.ats.adminpanel.model.production.PostProdPlanHeader;
import com.ats.adminpanel.model.production.PostProductionDetail;
import com.ats.adminpanel.model.production.PostProductionHeader;
import com.ats.adminpanel.model.production.PostProductionPlanDetail;
import com.ats.adminpanel.model.production.UpdateOrderStatus;
import com.ats.adminpanel.model.stock.FinishedGoodStock;
import com.ats.adminpanel.model.stock.FinishedGoodStockDetail;

@Controller
public class ProdRejectRetuController {

	List<MCategoryList> filteredCatList;
	AllItemsListResponse allItemsListResponse = new AllItemsListResponse();
	List<FinishedGoodStockDetail> showFinStockDetail = new ArrayList<FinishedGoodStockDetail>();
	FinishedGoodStock showStockHeader;

	String todaysDate;

	@RequestMapping(value = "/showProductionHeaderDetail", method = RequestMethod.GET)
	public ModelAndView showProductionHeaderDetail(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showFinishedGoodStock", "showFinishedGoodStock", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == true) {

			model = new ModelAndView("stock/showProductionHeaderDetail");

		} else {
			model = new ModelAndView("stock/showProductionHeaderDetail");
			try {
				Constants.mainAct = 4;
				Constants.subAct = 40;

				RestTemplate restTemplate = new RestTemplate();

				CategoryListResponse allCategoryResponse = restTemplate.getForObject(Constants.url + "showAllCategory",
						CategoryListResponse.class);

				List<MCategoryList> catList = allCategoryResponse.getmCategoryList();

				filteredCatList = new ArrayList<MCategoryList>();
				System.out.println("catList :" + catList.toString());

				for (MCategoryList mCategory : catList) {
					if (mCategory.getCatId() != 5 && mCategory.getCatId() != 3) {
						filteredCatList.add(mCategory);

					}
				}

				model.addObject("catList", filteredCatList);

				ZoneId z = ZoneId.of("Asia/Calcutta");

				LocalDate date = LocalDate.now(z);
				DateTimeFormatter formatters = DateTimeFormatter.ofPattern("d-MM-uuuu");
				todaysDate = date.format(formatters);

				model.addObject("todaysDate", todaysDate);

			} catch (Exception e) {

				System.out.println("Exe in showing add Fin good Stock Page " + e.getMessage());
				e.printStackTrace();
			}
		}
		return model;

	}

	ArrayList<Item> itemList = new ArrayList<>();

	@RequestMapping(value = "/getItemBySubCatAndCatId", method = RequestMethod.GET)
	public @ResponseBody List<Item> getItemBySubCatAndCatId(HttpServletRequest request, HttpServletResponse response) {
		// Constants.mainAct =1;
		// Constants.subAct =4;

		try {
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			RestTemplate restTemplate = new RestTemplate();

			int catId = Integer.parseInt(request.getParameter("item_grp1"));
			int subCatId = Integer.parseInt(request.getParameter("item_grp2"));
			// CategoryListResponse

			map.add("subCatId", subCatId);
			Item[] itemsList = restTemplate.postForObject(Constants.url + "getItemsBySubCatId", map, Item[].class);

			itemList = new ArrayList<Item>(Arrays.asList(itemsList));

		} catch (Exception e) {

			System.err.println("Exce in getSubCateListByCatId Ajax " + e.getMessage());
			e.printStackTrace();

		}
		return itemList;
	}

	@RequestMapping(value = "/insertProductionHeaderDetail", method = RequestMethod.POST)
	public String insertProductionHeaderDetail(HttpServletRequest request, HttpServletResponse response) {

		RestTemplate restTemplate = new RestTemplate();
		System.err.println("global item list in /insertProductionHeaderDetail " + itemList.toString());
		try {

			int catId = Integer.parseInt(request.getParameter("item_grp1"));
			System.err.println("catId" + catId);
			String fromDate = request.getParameter("fromDate");
			System.err.println("fromDate" + fromDate);

			String status = request.getParameter("status");

			System.out.println("statusstatusstatusstatusstatus" + status);
			PostProdPlanHeader postProductionHeader = new PostProdPlanHeader();

			postProductionHeader.setTimeSlot(0);
			postProductionHeader.setItemGrp1(catId);
			postProductionHeader.setProductionDate(fromDate);
			postProductionHeader.setDelStatus(0);
			postProductionHeader.setIsBom(0);
			postProductionHeader.setIsMixing(0);
			postProductionHeader.setIsPlanned(1);
			postProductionHeader.setProductionBatch("0");
			postProductionHeader.setProductionStatus(5);
			postProductionHeader.setProductionHeaderId(0);

			List<PostProductionPlanDetail> postProductionDetailList = new ArrayList<>();
			PostProductionPlanDetail postProductionDetail;
			System.out.println("postProductionHeaderpostProductionHeaderpostProductionHeaderpostProductionHeader"
					+ itemList.size());
			for (int i = 0; i < itemList.size(); i++) {

				System.out.println(request.getParameter("qty1" + itemList.get(i).getId()));

				int t1 = Integer.parseInt(request.getParameter("qty1" + itemList.get(i).getId()));
				int t2 = Integer.parseInt(request.getParameter("qty2" + itemList.get(i).getId()));

				if (t1 > 0 || t2 > 0) {
					postProductionDetail = new PostProductionPlanDetail();

					postProductionDetail.setProductionDate(fromDate);
					postProductionDetail.setItemId(itemList.get(i).getId());
					postProductionDetail.setOpeningQty(0);
					postProductionDetail.setOrderQty(0);
					postProductionDetail.setProductionBatch("0");
					postProductionDetail.setRejectedQty(t1);
					postProductionDetail.setProductionQty(0);
					postProductionDetail.setPlanQty(0);
					postProductionDetail.setInt5(t2);
					postProductionDetail.setProductionDetailId(0);

					postProductionDetailList.add(postProductionDetail);
				}
			}

			postProductionHeader.setPostProductionPlanDetail(postProductionDetailList);
			System.out.println("postProductionHeader" + postProductionHeader.toString());

			Info info = restTemplate.postForObject(Constants.url + "postProductionPlanForRejRet", postProductionHeader,
					Info.class);
			System.out.println(info.toString());

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return "redirect:/showProductionHeaderDetail";

	}

}
