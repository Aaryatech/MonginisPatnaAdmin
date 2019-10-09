<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>
	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="getGroup2ByCatId" value="/getSubCateListByCatId" />
	<c:url var="getItemBySubCat" value="/getItemBySubCat" />

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
			<div class="page-title">
				<div>
					<h1>
						<i class="fa fa-file-o"></i> Damaged Item
					</h1>

					<c:set var="isEdit" value="0">
					</c:set>
					<c:set var="isView" value="0">
					</c:set>
					<c:set var="isAdd" value="0">
					</c:set>

					<c:forEach items="${sessionScope.newModuleList}" var="modules">
						<c:forEach items="${modules.subModuleJsonList}" var="subModule">

							<c:choose>
								<c:when test="${subModule.subModuleMapping eq 'showAddGateSaleQty'}">

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
										<c:when test="${subModule.addApproveConfig=='visible'}">
											<c:set var="isAdd" value="1">
											</c:set>
										</c:when>
										<c:otherwise>
											<c:set var="isAdd" value="0">
											</c:set>
										</c:otherwise>
									</c:choose>
								</c:when>
							</c:choose>
						</c:forEach>
					</c:forEach>

				</div>
			</div>
			<!-- END Page Title -->

			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="box">
						<div class="box-title">
							<h3>
								<i class="fa fa-bars"></i> Add Damaged Items 
							</h3>
							<div class="box-tool">

								<a href="${pageContext.request.contextPath}/damagedItemList">Back
									to List</a> <a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a>


							</div>
							<!-- <div class="box-tool">
								<a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a> <a data-action="close" href="#"><i
									class="fa fa-times"></i></a>
							</div> -->
						</div>


						<div class="box-content">
							<form action="${pageContext.request.contextPath}/addGateSaleItem" class="form-horizontal"
								method="post" id="validation-form">

								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Group1</label>
									<div class="col-sm-9 col-lg-10 controls">
										<select data-placeholder="Select Group"
											class="form-control chosen" name="item_grp1" tabindex="-1"
											id="item_grp1" data-rule-required="true">
											<option selected>Select Group 1</option>

											<c:forEach items="${mCategoryList}" var="mCategoryList">


												<option value="${mCategoryList.catId}"><c:out value="${mCategoryList.catName}"></c:out></option>
											</c:forEach>

										</select>
									</div>
									<!-- </div>


								<div class="form-group"> -->
									<label class="col-sm-3 col-lg-2 control-label">Group2</label>
									<div class="col-sm-9 col-lg-10 controls">
										<select data-placeholder="Select Group"
											class="form-control chosen-select" name="item_grp2"
											tabindex="-1" id="item_grp2" data-rule-required="true">

										</select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Item
										</label>
									<div class="col-sm-9 col-lg-10 controls">
										<select data-placeholder="Select Item"
											class="form-control chosen-select" name="item_name"
											tabindex="-1" id="item_name" data-rule-required="true">

										</select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Damaged Quantity
									</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="number" name="dam_qty" id="dam_qty" min="1"
											placeholder="Damaged Quantity" class="form-control"
											data-rule-required="true" data-rule-number="true"/>
									</div>
								</div>
								

								<div class="form-group">
									
									<label class="col-sm-3 col-lg-2 control-label">Submit</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="submit" class="btn btn-primary" value="Submit">

									</div>
								</div>



								<%-- <div class="clearfix"></div>
								<div id="table-scroll" class="table-scroll">

									<div id="faux-table" class="faux-table" aria="hidden">
										<table id="table2" class="main-table">
											<thead>
												<tr class="bgpink">
												<th width="140" style="width: 30px" align="left">No</th>
													<th width="138" align="left">Sub Category</th>
													<th width="120" align="left">HSN Code</th>
													<th width="100" align="left">UOM</th>
													<th width="60" align="left">CGST</th>
													<th width="80" align="left">SGST</th>
													<th width="100" align="left">IGST</th>
													<th width="120" align="left">Action</th>
												</tr>
											</thead>
										</table>

									</div>
									<div class="table-wrap">

										<table id="table1" class="table table-advance">
											<thead>
												<tr class="bgpink">
													<th width="140" style="width: 30px" align="left">No</th>
													<th width="138" align="left">Sub Category</th>
													<th width="120" align="left">HSN Code</th>
													<th width="100" align="left">UOM</th>
													<th width="60" align="left">CGST</th>
													<th width="80" align="left">SGST</th>
													<th width="100" align="left">IGST</th>
													<th width="120" align="left">Action</th>
												</tr>
											</thead>
											<!-- 
								<div class="table-responsive" style="border: 0">
									<table width="100%" class="table table-advance" id="table1">
										<thead>
											<tr>

												<th width="140" style="width: 30px" align="left">Sr No</th>
												<th width="138" align="left">SF Name</th>
												<th width="120" align="left">SF Type</th>
												<th width="60" align="left">SF Weight</th>

												<th width="80" align="left">Min Level Qty</th>
												<th width="100" align="left">Reorder Level Qty</th>

												<th width="120" align="left">Action</th>
													<th width="140" align="left">GST %</th>

											</tr>

										</thead> -->

											<tbody>

												<c:forEach items="${taxRateList}" var="taxRate"
													varStatus="count">

													<tr>
														<td><c:out value="${count.index+1}" /></td>

														<td align="left"><c:out value="${taxRate.subCatName}" /></td>


														<td align="left"><c:out value="${taxRate.hsnCode}" /></td>

														<td align="center"><c:out value="${taxRate.uom}" /></td>

														<td align="center"><c:out
																value="${taxRate.cgstPer}" /></td>

														<td align="center"><c:out
																value="${taxRate.sgstPer}" /></td>

														<td align="center"><c:out
																value="${taxRate.igstPer}" /></td>

														<td> <a
															href="${pageContext.request.contextPath}/deleteTaxRate/${taxRate.taxRateId}"
															onClick="return confirm('Are you sure want to delete this record');"><span
																class="glyphicon glyphicon-trash"></span></a></td>


													</tr>
												</c:forEach>

											</tbody>
										</table>
									</div>
								</div> --%>
							</form>
						</div>
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
	function calcIgst() {
		var cgstPer = $("#cgst_per").val();
		var sgstPer = $("#sgst_per").val();
		var igstPer=parseFloat(cgstPer)+parseFloat(sgstPer);
		document.getElementById("igst_per").value=igstPer;

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
																	var html = '<option value="" selected >Select Group 2</option>';

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



	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$('#item_grp2')
									.change(
											function() {
												$
														.getJSON(
																'${getItemBySubCat}',
																{
																	subCatId : $(
																			this)
																			.val(),
																	ajax : 'true'
																},
																function(data) {
																	var html = '<option value="" selected >Select Item</option>';

																	var len = data.length;
																	for (var i = 0; i < len; i++) {
																		html += '<option value="' + data[i].id + '">'
																				+ data[i].itemName
																				+ '</option>';
																	}
																	html += '</option>';
																	$(
																			'#item_name')
																			.html(
																					html);
																	$(
																			'#item_name')
																			.formcontrol(
																					'refresh');

																});
											});
						});
	</script>

</body>
</html>