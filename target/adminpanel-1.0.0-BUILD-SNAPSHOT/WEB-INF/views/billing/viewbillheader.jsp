<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
	table{
  width:100%;
  border:1px solid #ddd;
}</style>
<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>
	<c:url var="callGetBillListProcess" value="/getBillListProcess" />

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
						<i class="fa fa-file-o"></i>View Your Bills
					</h1>

				</div>
			</div> -->
			<!-- END Page Title -->

			<c:set var="isEdit" value="0">
			</c:set>
			<c:set var="isView" value="0">
			</c:set>
			<c:set var="isDelete" value="0">
			</c:set>
			
			<input type="hidden" id="modList" value="${sessionScope.newModuleList}">

			<c:forEach items="${sessionScope.newModuleList}" var="modules">
				<c:forEach items="${modules.subModuleJsonList}" var="subModule">

					<c:choose>
						<c:when test="${subModule.subModuleMapping eq 'showBillList'}">

							<c:choose>
								<c:when test="${subModule.editReject=='visible'}">
									<c:set var="isEdit" value="1">
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="isEdit" value="0">
									</c:set>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${subModule.view=='visible'}">
									<c:set var="isView" value="1">
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="isView" value="0">
									</c:set>
								</c:otherwise>
							</c:choose>


							<c:choose>
								<c:when test="${subModule.deleteRejectApprove=='visible'}">
									<c:set var="isDelete" value="1">
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set var="isDelete" value="0">
									</c:set>
								</c:otherwise>
							</c:choose>
						</c:when>
					</c:choose>
				</c:forEach>
			</c:forEach>
			
			<input type="hidden" id="isDelete" value="${isDelete}">
						<input type="hidden" id="isEdit" value="${isEdit}">
			

			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i> View Your Bills
							</h3>
							<div class="box-tool">
								<!-- <a href="">Back to List</a> <a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a> -->
							</div>
							<!-- <div class="box-tool">
								<a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a> <a data-action="close" href="#"><i
									class="fa fa-times"></i></a>
							</div> -->
						</div>


						<div class="box-content">
							<form class="form-horizontal" method="get" id="validation-form">
							<input type="hidden" class="form-control" id="transport_mode" name="transport_mode" value="By Road"/>
							<input type="hidden" class="form-control" name="vehicle_no" id="vehicle_no"	value="0"  />
							<input type="hidden" class="form-control" name="billnumber" id="billnumber"	value="0"  />
							<input type="hidden" class="form-control" name="issinglepdf" id="issinglepdf" 	value="0" />
							
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">From
										Date</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input class="form-control date-picker" id="dp1" size="16"
											value="${fromDate}" type="text" name="from_date" required />
									</div>




									<label class="col-sm-3 col-lg-2 control-label">To Date</label>
									<div class="col-sm-5 col-lg-3 controls">
										<input class="form-control date-picker" id="dp2" size="16"
											value="${toDate}" type="text" name="to_date" required />

									</div>

								</div>



								<div class="form-group">

									<label for="textfield2" class="col-xs-3 col-lg-2 control-label">Select
										Franchise </label>
									<div class="col-sm-9 col-lg-4 controls">

										<select class="form-control chosen" multiple="multiple"
											tabindex="6" name="fr_id" id="fr_id">

											<option value="-1">All</option>
											
											<c:forEach items="${allFrIdNameList}" var="allFrIdNameList"
												varStatus="count">
												<option value="${allFrIdNameList.frId}">${allFrIdNameList.frName}</option>

											</c:forEach>

										</select>
									</div>




									<label for="textfield2" class="col-xs-1 col-lg-1 control-label">
										OR </label> 
									<div class="col-sm-9 col-lg-3 controls">


										<select class="form-control chosen" tabindex="6"
											name="route_id" id="route_id">

											<option value="0">Select Route</option>
											<c:forEach items="${routeList}" var="route" varStatus="count">
												<option value="${route.routeId}"> ${route.routeName}</option>

											</c:forEach>

										</select>
									</div>
								<!-- </div>









								<div align="center" class="form-group">
									<div
										class="col-sm-25 col-sm-offset-3 col-lg-30 col-lg-offset-0"> -->
										<input type="button" class="btn btn-primary" value="Search"
											id="callSubmit" onclick="callSearch()">


									</div>
								





								<div align="center" id="loader" style="display: none">

									<span>
										<h4>
											<font color="#343690">Loading</font>
										</h4>
									</span> <span class="l-1"></span> <span class="l-2"></span> <span
										class="l-3"></span> <span class="l-4"></span> <span
										class="l-5"></span> <span class="l-6"></span>
								</div>

								<!-- </form>
 -->
								<!-- <tion="getBillListProcess" class="form-horizontal"
								method="post" id="validation-form"> -->
								<div class="box">
									<div class="box-title">
										<h3>
											<i class="fa fa-table"></i> Bill List Header
										</h3>
										<div class="box-tool">
											<a data-action="collapse" href="#"><i
												class="fa fa-chevron-up"></i></a>
											<!--<a data-action="close" href="#"><i class="fa fa-times"></i></a>-->
										</div>
									</div>

									<div class="box-content">

										<div class="clearfix"></div>
										<div id="table-scroll" class="table-scroll">

											<div id="faux-table" class="faux-table" aria="hidden">
												<!-- <table id="table2" class="table table-advance" border="1" >
													<thead>
														<tr class="bgpink">
															<th class="col-sm-1" align="left">Sr No</th>
															<th class="col-md-1" align="left">Inv No</th>
															<th class="col-md-1" align="left">Date</th>
															<th class="col-md-2" align="left">Franchise Name</th>
															<th class="col-md-2" align="left">Taxable Amt</th>
															<th class="col-md-2" align="left">Total tax</th>
															<th class="col-md-1" align="left">Total</th>
															<th class="col-md-1" align="left">Status</th>
															<th class="col-md-2" align="left">Action</th>
														</tr>
													</thead>
												</table> -->

											</div>
											<div class="table-wrap">

												<table id="table1" class="table table-advance" border="1" >
													<thead>
														<tr class="bgpink">
															<th class="col-sm-1"><input type="checkbox"
													onClick="selectBillNo(this)" /> All<br /></th>
															<th class="col-sm-1" align="left">Sr No</th>
															<th class="col-md-1" align="left">Inv No</th>
															<th class="col-md-1" align="left">Date</th>
															<th class="col-md-2" align="left">Franchise Name</th>
															<th class="col-md-1" align="left">Taxable Amt</th>
															<th class="col-md-1" align="left">Total tax</th>
															<th class="col-md-1" align="left">Total</th>
															<th class="col-md-1" align="left">Status</th>
															<th class="col-md-1" align="left">Action</th>
														</tr>
													</thead>
													<tbody>
														<!-- <div class="table-responsive" style="border: 0">
											<table width="100%" class="table table-advance" id="table1">
												<thead>
													<tr>
														<th class="col-sm-1" align="left">Sr No</th>
														<th class="col-md-1" align="left">Invoice No</th>
														<th class="col-md-1" align="left">Date</th>
														<th class="col-md-2" align="left">Franchise Name</th>
														<th class="col-md-1" align="left">Taxable Amt</th>
														<th class="col-md-1" align="left">Total tax</th>
														<th class="col-md-1" align="left">Total</th>
														<th class="col-md-1" align="left">Status</th>
														<th class="col-md-1" align="center">Action</th>
													</tr>
												</thead>
												<tbody> -->
														<c:forEach items="${billHeadersList}"
															var="billHeadersList" varStatus="count">

															<tr>
															<td class="col-sm-1"><input type="checkbox" name="select_to_print"
																id="${billHeadersList.billNo}"
																value="${billHeadersList.billNo}"/></td>
																<td class="col-sm-1"><c:out
																		value="${count.index+1}" /></td>
																<td class="col-md-1" align="left"><c:out
																		value="${billHeadersList.invoiceNo}" /></td>

																<td class="col-md-1" align="left"><c:out
																		value="${billHeadersList.billDate}" /></td>

																<td class="col-md-2" align="left"><c:out
																		value="${billHeadersList.frName}" /></td>
																<td class="col-md-1" style="text-align:right;"><c:out
																		value="${billHeadersList.taxableAmt}" /></td>
																<td class="col-md-1" style="text-align:right;"><c:out
																		value="${billHeadersList.totalTax}" /></td>
																<td style="text-align:right;"><c:out
																		value="${billHeadersList.grandTotal}" /></td>
																<td align="left"><c:out
																		value="${billHeadersList.status}" /></td>


																<c:choose>
																	<c:when test="${isEdit==1 and isDelete==1}">
																		<td align="left" class="col-md-2" >&nbsp;&nbsp;&nbsp;&nbsp;<a
																			href="${pageContext.request.contextPath}/updateBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='Update Bill'></abbr> <i
																				class='fa fa-edit  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/viewBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='View Bill'></abbr> <i
																				class='fa fa-info  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/deleteBill/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='Delete Bill'></abbr> <i
																				class='fa fa-trash-o  fa-lg'></i></a>&nbsp;
																				<a
																			href="#" onclick=""><abbr
																				title='Bill Pdf'></abbr> <i
																				class='fa fa-pdf-o  fa-lg'></i></a>
										&nbsp;&nbsp;<input type="button"  id="btn_submit_pdf" class="btn btn-primary" value="PDF" onclick="generateSinglePdf(${billHeadersList.billNo})" style="padding: 0px 4px;font-size: 14px;"/>
																				</td>

																	</c:when>

																	<c:when test="${isEdit==1 and isDelete==0}">
																		<td align="left" class="col-md-2" >&nbsp;&nbsp;&nbsp;&nbsp;<a
																			href="${pageContext.request.contextPath}/updateBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='Update Bill'></abbr> <i
																				class='fa fa-edit  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/viewBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='View Bill'></abbr> <i
																				class='fa fa-info  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/deleteBill/${billHeadersList.billNo}/${billHeadersList.frName}" class="disableClick"><abbr
																				title='Delete Bill'></abbr> <i
																				class='fa fa-trash-o  fa-lg'></i></a>
																				&nbsp;&nbsp;<input type="button"  id="btn_submit_pdf"  onclick="generateSinglePdf(${billHeadersList.billNo})" style="padding: 0px 4px;font-size: 14px;"
															class="btn btn-primary"
															value="PDF" />
																				</td>

																	</c:when>

																	<c:when test="${isEdit==0 and isDelete==1}">
																		<td align="left" class="col-md-2" >&nbsp;&nbsp;&nbsp;&nbsp;<a
																			href="${pageContext.request.contextPath}/updateBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}" class="disableClick"><abbr
																				title='Update Bill'></abbr> <i
																				class='fa fa-edit  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/viewBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='View Bill'></abbr> <i
																				class='fa fa-info  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/deleteBill/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='Delete Bill'></abbr> <i
																				class='fa fa-trash-o  fa-lg'></i></a>
																			&nbsp;&nbsp;<input type="button"  id="btn_submit_pdf" onclick="generateSinglePdf(${billHeadersList.billNo})"   style="padding: 0px 4px;font-size: 14px;"
															class="btn btn-primary"
															value="PDF" />	
																				</td>

																	</c:when>

																	<c:otherwise>

																		<td align="left" class="col-md-2" >&nbsp;&nbsp;&nbsp;&nbsp;<a
																			href="${pageContext.request.contextPath}/updateBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}" class="disableClick"><abbr
																				title='Update Bill'></abbr> <i
																				class='fa fa-edit  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/viewBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																				title='View Bill'></abbr> <i
																				class='fa fa-info  fa-lg'></i></a>&nbsp; <a
																			href="${pageContext.request.contextPath}/deleteBill/${billHeadersList.billNo}/${billHeadersList.frName}" class="disableClick"><abbr
																				title='Delete Bill'></abbr> <i
																				class='fa fa-trash-o  fa-lg'></i></a>
																		&nbsp;&nbsp;<input type="button"  id="btn_submit_pdf" onclick="generateSinglePdf(${billHeadersList.billNo})" style="padding: 0px 4px;font-size: 14px;"
															class="btn btn-primary"
															value="PDF" />		
																				</td>

																	</c:otherwise>
																</c:choose>



<%-- 
																<td align="left"><a
																	href="${pageContext.request.contextPath}/updateBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																		title='Update Bill'></abbr> <i
																		class='fa fa-edit  fa-lg'></i></a>&nbsp; <a
																	href="${pageContext.request.contextPath}/viewBillDetails/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																		title='View Bill'></abbr> <i class='fa fa-info  fa-lg'></i></a>&nbsp;

																	<a
																	href="${pageContext.request.contextPath}/deleteBill/${billHeadersList.billNo}/${billHeadersList.frName}"><abbr
																		title='Delete Bill'></abbr> <i
																		class='fa fa-trash-o  fa-lg'></i></a></td> --%>

																<!-- <td rowspan="1" align="left"> <input
																type="button" value="View"> <input type="button"
																value="Edit"> <input type="button"
																value="Cancel"></td>
 -->


																<!-- <td align="left"><label><input type="submit"
																	name="submit_button" id="submit_button"></label></td>  -->


															</tr>
														</c:forEach>

													</tbody>
												</table>
											</div>
										</div>

	<input type="button" id="btn_submit" class="btn btn-primary" onclick="submitBill()"	value="BillDetail" />
									</div>
							</form>
						</div>
					</div>
				</div>
			</div></div>
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
		$(document).ready(function() {
			$('#callSubmit2').submit(function() {
				alert("searching");
				$.ajax({
					type : "get",
					url : "/getBillListProcess", //this is my servlet
					/*   data: "input=" +$('#ip').val()+"&output="+$('#op').val(), */
					success : function(data) {
						alert("success");

					}

				});
			});

		});
	</script>
	<script type="text/javascript">
		function submitBill() {
			var form = document.getElementById("validation-form").target="_blank";
			var form = document.getElementById("validation-form");
			form.action = "${pageContext.request.contextPath}/getBillDetailForPrint";
			form.submit();
		}
		function submitBill1(selectedBills){
			var vehicleNo = document.getElementById("vehicle_no").value;
			var transportMode = document.getElementById("transport_mode").value;
			
			window.open("${pageContext.request.contextPath}/getBillDetailForPrint1/"+vehicleNo+'/'+transportMode+'/'+selectedBills);
		}
		$('#btn_submit')
				.click(
						function() {
							var form = document.getElementById("validation-form")
							form.action = "${pageContext.request.contextPath}/getBillDetailForPrint";
							form.submit();
						});	
		
	</script>
	<script type="text/javascript">
	/* 	$('#btn_submit_pdf')
				.click(
						function() {
							document.getElementById("validation-form").target = "_blank";

							var form = document.getElementById("validation-form");

							form.action = "${pageContext.request.contextPath}/getBillDetailForPrintPdf";
							form.submit();
						}); */
		function submitBillPdf() {
			document.getElementById("validation-form").target = "_blank";

			var form = document.getElementById("validation-form");

			form.action = "${pageContext.request.contextPath}/getBillDetailForPrintPdf";
			form.submit();
		}
		function generateSinglePdf(billNo) {
			
			document.getElementById("billnumber").value=billNo;
			document.getElementById("issinglepdf").value=1;
			document.getElementById("validation-form").target = "_blank";

			var form = document.getElementById("validation-form");

			form.action = "${pageContext.request.contextPath}/getBillDetailForPrintPdf";
			form.submit();
		}
	</script>
	<script type="text/javascript">
		function callSearch() {
		
			var isDelete=document.getElementById("isDelete").value;
			var isEdit=document.getElementById("isEdit").value;
			
			var array = [];
			var frIds = $("#fr_id").val();
			var fromDate = document.getElementById("dp1").value;
			var toDate = document.getElementById("dp2").value;

			var routeId = document.getElementById("route_id").value;

			$('#loader').show();

			$
					.getJSON(
							'${callGetBillListProcess}',
							{
								fr_id_list : JSON.stringify(frIds),
								from_date : fromDate,
								to_date : toDate,
								route_id : routeId,
								ajax : 'true'
							},
							function(data) {
								$('#table1 td').remove();
								$('#loader').hide();
								if (data == "") {
									alert("No Bill Found");
								}

								$
										.each(
												data,
												function(key, bill) {
													

													var tr = $('<tr></tr>');
													tr
													.append($(
															'<td class="col-sm-1"></td>')
															.html(
																	"<input type='checkbox' name='select_to_print' id="+bill.billNo+" value="+bill.billNo+" />"));

										
													tr
															.append($(
																	'<td class="col-sm-1"></td>')
																	.html(
																			key + 1));

													tr
															.append($(
																	'<td class="col-md-1"></td>')
																	.html(
																			bill.invoiceNo));

													tr
															.append($(
																	'<td class="col-md-1"></td>')
																	.html(
																			bill.billDate));

													tr
															.append($(
																	'<td class="col-md-2"></td>')
																	.html(
																			bill.frName));

													tr
															.append($(
																	'<td class="col-md-1" style="text-align:right;"></td>')
																	.html(
																			bill.taxableAmt));

													tr
															.append($(
																	'<td class="col-md-1" style="text-align:right;"></td>')
																	.html(
																			bill.totalTax));

													tr
															.append($(
																	'<td class="col-md-1" style="text-align:right;"></td>')
																	.html(
																			bill.grandTotal));

													if (bill.status == 1) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"Pending"));

													} else if (bill.status == 2) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"Received"));
													} else if (bill.status == 3) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"GVN Applied"));
													} else if (bill.status == 4) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"GVN Approved"));
													} else if (bill.status == 5) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"GRN Applied"));
													} else if (bill.status == 6) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"GRN Approved"));
													} else if (bill.status == 7) {
														tr
																.append($(
																		'<td class="col-md-1"></td>')
																		.html(
																				"Closed"));
													}

													
												 if(isDelete==1 && isEdit==1){
														// alert("in first");
														 tr
															.append($(
																	'<td class="col-md-2"></td>')
																	.html(
																			"&nbsp; <a href='${pageContext.request.contextPath}/updateBillDetails/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='Update Bill'></abbr><i class='fa fa-edit  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${pageContext.request.contextPath}/viewBillDetails/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='View Bill'></abbr><i class='fa fa-info  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${pageContext.request.contextPath}/deleteBill/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='Delete Bill'></abbr><i class='fa fa-trash-o  fa-lg'></i></a>&nbsp;&nbsp;<input type=button  style='padding: 0px 4px;font-size: 14px;' onclick=generateSinglePdf("+bill.billNo+")  id=btn_submit_pdf class='btn btn-primary' value=PDF />"));

														 
													 }else if(isDelete==1 && isEdit==0){
														 //alert("in second");
														 tr
															.append($(
																	'<td class="col-md-2"></td>')
																	.html(
																			"&nbsp; <a href=''javascript: void(0)'/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "class='disableClick''<abbr title='Update Bill'></abbr><i class='fa fa-edit  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${pageContext.request.contextPath}/viewBillDetails/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='View Bill'></abbr><i class='fa fa-info  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${pageContext.request.contextPath}/deleteBill/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='Delete Bill'></abbr><i class='fa fa-trash-o  fa-lg'></i></a>&nbsp;&nbsp;<input type=button  id=btn_submit_pdf  style='padding: 0px 4px;font-size: 14px;' onclick=generateSinglePdf("+bill.billNo+") class='btn btn-primary' value=PDF />"));

														 
													 }else if(isDelete==0 && isEdit==1){
														// alert("in third");
														 tr
															.append($(
																	'<td class="col-md-2"></td>')
																	.html(
																			"&nbsp; <a href='${pageContext.request.contextPath}/updateBillDetails/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='Update Bill'></abbr><i class='fa fa-edit  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='${pageContext.request.contextPath}/viewBillDetails/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='View Bill'></abbr><i class='fa fa-info  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href=''javascript: void(0)'"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "class='disableClick''<abbr title='Delete Bill'></abbr><i class='fa fa-trash-o  fa-lg'></i></a>&nbsp;&nbsp;<input type=button  id=btn_submit_pdf class='btn btn-primary' value=PDF onclick=generateSinglePdf("+ bill.billNo+") style='padding: 0px 4px;font-size: 14px;'/>"));

														 
													 }else{
														 
														 tr
															.append($(
																	'<td class="col-md-2"></td>')
																	.html(
																			"&nbsp; <a href='javascript: void(0)/"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "class='disableClick''<abbr title='Update Bill'></abbr><i class='fa fa-edit  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript: void(0)'"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "'<abbr title='View Bill'></abbr><i class='fa fa-info  fa-lg'></i></a>&nbsp;&nbsp;&nbsp;&nbsp;<a href='javascript: void(0)'"
																					+ bill.billNo
																					+ "/"
																					+ bill.frName
																					+ "class='disableClick''<abbr title='Delete Bill'></abbr><i class='fa fa-trash-o  fa-lg'></i></a>&nbsp;&nbsp;<input type=button  id=btn_submit_pdf class='btn btn-primary' value=PDF  onclick=generateSinglePdf("+ bill.billNo+") style='padding: 0px 4px;font-size: 14px;' />"));
													 }
													$('#table1 tbody').append(
															tr);
													

												})

							});

		}
	</script>
</body>
</html>