<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<style>
table {
	width: 100%;
	border: 1px solid #ddd;
}
</style>

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>
	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="findItemsByCategory" value="/getItemsByCatIdForFinGood"></c:url>
	<c:url var="getGroup2ByCatId" value="/getSubCateListByCatId" />

	<c:url var="getItemBySubCatAndCatId" value="/getItemBySubCatAndCatId"></c:url>
	<div class="container" id="main-container">

		<!-- BEGIN Sidebar -->
		<div id="sidebar" class="navbar-collapse collapse">

			<jsp:include page="/WEB-INF/views/include/navigation.jsp"></jsp:include>

			<div id="sidebar-collapse" class="visible-lg">
				<i class="fa fa-angle-double-left"></i>
			</div>
			<!-- END Sidebar Collapse Button -->
		</div>
		<!-- END Sidebar -->

		<!-- BEGIN Content -->
		<div id="main-content">
			<!-- BEGIN Page Title -->
			<!-- 	<div class="page-title">
				<div>
					<h1>
						<i class="fa fa-file-o"></i>Finished Good Stock Adjustment & Overview
					</h1>

				</div>
			</div> -->
			<!-- END Page Title -->



			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">


						<div class="box-content">
							<form class="form-horizontal" id="validation-form">



								<input type="hidden" id="selectedCatId" name="selectedCatId" />

							</form>
							<form action="insertProductionHeaderDetail" method="post"
								id="validation-form">

								<div class="box">
									<div class="box-title">
										<h3>
											<i class="fa fa-table"></i> Add Return And Rejection Qty

										</h3>
										<div class="box-tool">
											<a data-action="collapse" href="#"><i
												class="fa fa-chevron-up"></i></a>
											<!--<a data-action="close" href="#"><i class="fa fa-times"></i></a>-->
										</div>
									</div>

									<div class="box-content">






										<div class="form-group">



											<label class="col-sm-2 col-lg-1 control-label"
												style="color: green;"><b>Date:</b> </label>
											<div class="col-sm-6 col-lg-2 controls date_select">
												<input class="form-control date-picker" id="fromDate"
													name="fromDate" size="30" type="text" value="${todaysDate}" />
											</div>




											<label class="col-sm-2 col-lg-1 control-label">Category</label>
											<div class="col-sm-6 col-lg-2 controls">
												<select data-placeholder="Select Category"
													class="form-control chosen" name="item_grp1" tabindex="-1"
													id="item_grp1" data-rule-required="true">
													<option selected>Select Group 1</option>

													<c:forEach items="${catList}" var="mCategoryList">


														<option value="${mCategoryList.catId}"><c:out
																value="${mCategoryList.catName}"></c:out></option>
													</c:forEach>


												</select>
											</div>

											<label class="col-sm-2 col-lg-1 control-label">Sub
												Category</label>
											<div class="col-sm-6 col-lg-2 controls">
												<select data-placeholder="Select Sub Category"
													class="form-control chosen-select" name="item_grp2"
													id="item_grp2" onchange="getItemsForSubCat()" tabindex="-1"
													data-rule-required="true">
												</select>
											</div>


											<label class="col-sm-2 col-lg-1 control-label">Select
												Status</label>
											<div class="col-sm-6 col-lg-2 controls">
												<select data-placeholder="Select Category"
													class="form-control chosen" name="status" tabindex="-1"
													id="status" data-rule-required="true">


													<option value="0" Selected>All</option>
													<option value="1">Rejected</option>
													<option value="2">Return</option>

												</select>
											</div>




										</div>
										<br />
										<jsp:include page="/WEB-INF/views/include/tableSearch.jsp"></jsp:include>

										<div class="clearfix"></div>
										<div id="table-scroll" class="table-scroll">

											<div id="faux-table" class="faux-table" aria="hidden">
												<table id="table2" class="table table-advance" border="1">
													<thead>
														<tr class="bgpink">
															<th class="col-md-1" align="left">Sr No</th>
															<th class="col-md-2" align="left">Item Name</th>
															<th class="col-md-2">Rejected Qty</th>
															<th class="col-md-2">Return Qty</th>

														</tr>
													</thead>
												</table>

											</div>
											<div class="table-wrap">

												<table id="table1" class="table table-advance" border="1">
													<thead>
														<tr class="bgpink">
															<th class="col-md-1" align="left">Sr No</th>
															<th class="col-md-2" align="left">Item Name</th>
															<th class="col-md-2">Rejected Qty</th>
															<th class="col-md-2">Return Qty</th>
														</tr>
													</thead>

													<tbody>

														<c:forEach items="${itemsList}" var="item"
															varStatus="count">
															<tr>
																<td><c:out value="${count.index+1}"></c:out></td>
																<td><c:out value="${item.itemName}"></c:out></td>
																<td><input type=text class=form-control
																	id="qty1${item.id}" value="0" name="qty1${item.id}"></td>
																<td><input type=text class=form-control
																	id="qty2${item.id}" value="0" name="qty2${item.id}"></td>

															</tr>
														</c:forEach>

													</tbody>
												</table>
											</div>
										</div>

									</div>

									<div align="right" class="form-group">

										<div
											class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0">
											<input type="submit" class="btn btn-primary" value="Submit"
												id="submitBtn" disabled="disabled">


										</div>

									</div>
								</div>
							</form>
						</div>


					</div>
					<!-- </form> -->
				</div>

			</div>
		</div>


		<!-- END Main Content -->
		<footer>
			<p>2018 © MONGINIS.</p>
		</footer>

		<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
			class="fa fa-chevron-up"></i></a>
	</div>
	<!-- END Content -->
	</div>
	<!-- END Container -->

	<!--basic scripts-->
	<script
		src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<script>
		window.jQuery
				|| document
						.write('<script src="${pageContext.request.contextPath}/resources/assets/jquery/jquery-2.0.3.min.js"><\/script>')
	</script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/bootstrap/js/bootstrap.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/jquery-slimscroll/jquery.slimscroll.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/jquery-cookie/jquery.cookie.js"></script>

	<!--page specific plugin scripts-->
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.resize.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.pie.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.stack.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.crosshair.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/flot/jquery.flot.tooltip.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/assets/sparkline/jquery.sparkline.min.js"></script>


	<!--page specific plugin scripts-->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/jquery.validate.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-validation/dist/additional-methods.min.js"></script>



	<!--flaty scripts-->
	<script src="${pageContext.request.contextPath}/resources/js/flaty.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/flaty-demo-codes.js"></script>
	<!--page specific plugin scripts-->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/chosen-bootstrap/chosen.jquery.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/clockface/js/clockface.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/date.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/daterangepicker.js"></script>

	<script type="text/javascript">
		function getItemsForSubCat() {
			$('#table1 td').remove();
			var catId = $("#item_grp1").val();
			var subCatId = $("#item_grp2").val();
			var subCatName = $('#item_grp2 option:selected').text();
			if (subCatId == 0) {
				subCatId = catId;
			}
			alert("Items For Sub Category - " + subCatName);
			$('#loader').show();

			$
					.getJSON(
							'${getItemBySubCatAndCatId}',
							{

								item_grp1 : catId,
								item_grp2 : subCatId,
								ajax : 'true'

							},
							function(data) {

								$('#table1 td').remove();

								document.getElementById("submitBtn").disabled = false;

								$('#loader').hide();
								if (data == "") {
									alert("No records found !!");

									document.getElementById("submitBtn").disabled = true;
								}
								//alert(data.length);

								$
										.each(
												data,
												function(key, item) {

													var index = key + 1;

													var tr = $('<tr ></tr>');

													tr.append($('<td ></td>')
															.html(index));
													tr
															.append($(
																	'<td ></td>')
																	.html(
																			item.itemName));
													tr
															.append($('<td align=center ><input type=number  class=form-control style=height:26px;  id= qty1'+ item.id+' value='+0+' name=qty1'+item.id+'  required></td>'));
													tr
															.append($('<td align=center ><input type=number  class=form-control style=height:26px;  id= qty2'+ item.id+ ' value='+0+' name=qty2'+item.id+'  required></td>'));

													$('#table1 tbody').append(
															tr);
													$("#myInput").focus();

													/* 		
															var index = "<td>&nbsp;&nbsp;&nbsp;"
																	+ index
																	+ "</td>";

															var itemName = "<td>&nbsp;&nbsp;&nbsp;"
																	+ item.itemName
																	+ "</td>";
																	
													
													
														    	var qty1 = "<td align=center ><input type=number  class=form-control  id= qty1"+ item.itemId+ " value="+item.opT1+" name=qty1"+item.itemId+"  required></td>"; 
																
																var qty2 = "<td align=center ><input type=number  class=form-control  id= qty2"+ item.itemId+ " value="+item.opT2+" name=qty2"+item.itemId+"  required></td>";

																var qty3 = "<td align=center ><input type=number  class=form-control  id= qty3"+ item.itemId+ " value="+item.opT3+" name=qty3"+item.itemId+"  required></td>";
														    	
														var trclosed = "</tr>";

															$('#table1 tbody')
																	.append(tr);
															$('#table1 tbody')
																	.append(index);
															$('#table1 tbody')
																	.append(itemName);
															$('#table1 tbody')
																	.append(
																			qty1);
													
															$('#table1 tbody')
																	.append(qty2);
														
															$('#table1 tbody')
																	.append(
																			qty3);
															
															
															$('#table1 tbody')
																	.append(
																			trclosed);  */

												})

							});

		}
	</script>


	<!-- 
	<script type="text/javascript">

$('#dayEndButton').click(function(){
	
	var option= $("#selectStock").val();
if(option==1){
	alert("Day End ");
	$('#loader').show();

	$.getJSON('${finishedGoodDayEnd}',
			{
				ajax : 'true',
				
				
	
})
	$('#loader').hide();

));

}else{alert("Please Select Current Stock")}});

</script> -->

	<script>
		/* function showDiv(elem) {
			if (elem.value == 1) {
				document.getElementById('select_month_year').style = "display:none";
				document.getElementById('select_date').style = "display:none";

			} else if (elem.value == 2) {
				document.getElementById('select_month_year').style.display = "block";
				document.getElementById('select_date').style = "display:none";
			} else if (elem.value == 3) {
				document.getElementById('select_date').style.display = "block";
				document.getElementById('select_month_year').style = "display:none";
				
			}
			
		} */

		function exportToExcel() {

			window.open("${pageContext.request.contextPath}/exportToExcel");
			document.getElementById("expExcel").disabled = true;
		}
		function getPdf() {

			window
					.open('${pageContext.request.contextPath}/finishedGoodStockPdfFnction?url=pdf/finishedGoodStockPdf');

		}
	</script>

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$('#item_grp1')
									.change(
											function() {
												$
														.getJSON(
																'${getGroup2ByCatId}',
																{
																	catId : $(
																			this)
																			.val(),
																	ajax : 'true'
																},
																function(data) {
																	var html = '<option value="" selected >Select Group 2</option><option value="0"  >All</option>';

																	var len = data.length;
																	for (var i = 0; i < len; i++) {
																		html += '<option value="' + data[i].subCatId + '">'
																				+ data[i].subCatName
																				+ '</option>';
																	}
																	html += '</option>';
																	$(
																			'#item_grp2')
																			.html(
																					html);
																	$(
																			'#item_grp2')
																			.formcontrol(
																					'refresh');

																});
											});
						});
	</script>

</body>

</html>