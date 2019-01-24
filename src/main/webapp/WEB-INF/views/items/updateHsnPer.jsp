<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<style>
.buttonload {
    background-color: white; /* Green background */
    border: none; /* Remove borders */
    color: #ec268f; /* White text */
    padding: 12px 20px; /* Some padding */
    font-size: 15px; /* Set a font-size */
    display:none;
}

/* Add a right margin to each icon */
.fa {
    margin-left: -12px;
    margin-right: 8px;
}
</style>
<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>
	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>
 <c:url var="getItemsByCatId" value="/getItemByIdUpdateHsn"></c:url>
  <c:url var="getGroup2ByCatId" value="/getGroup2ByCatId"></c:url>
 
   <c:url var="itemsBysubCatId" value="/itemsBysubCatId"></c:url>
 
 
 

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
						<i class="fa fa-file-o"></i> Update Items
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
								<c:when test="${subModule.subModuleMapping eq 'addItem'}">

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
								<i class="fa fa-bars"></i> Update Items HSN And Tax%
							</h3>
							<div class="box-tool">

								<a href="${pageContext.request.contextPath}/itemList">Back
									to List</a> <a data-action="collapse" href="#"><i
									class="fa fa-chevron-up"></i></a>


							</div>
							
						</div>


						<div class="box-content">
							<form action="${pageContext.request.contextPath}/updateHsnAndTaxPerc" class="form-horizontal"
								method="post" id="validation-form" enctype="multipart/form-data">


                          <div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Category</label>
									<div class="col-sm-9 col-lg-3 controls">
									<select name="cat_id" id="cat_id" class="form-control" placeholder="Select Category" onchange="catChange(this.value)">
											<option value="">Select Category</option>
										 <c:forEach items="${mCategoryList}" var="mCategoryList">
										            	  <option value="${mCategoryList.catId}"><c:out value="${mCategoryList.catName}"></c:out></option>
										</c:forEach> 
												
								</select>	
									</div>
									<!-- <button class="buttonload" id="loader">
                                   <i class="fa fa-spinner fa-spin"></i>Loading
                                   </button> -->
								</div>
								
								 <div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Sub Category</label>
									<div class="col-sm-9 col-lg-3 controls">
									<select name="sub_cat_id" id="sub_cat_id" class="form-control" placeholder="Select SubCategory" onchange="subCatChange(this.value)">
											<option value="-1">Select SubCatId</option>
										<%--  <c:forEach items="${mCategoryList}" var="mCategoryList">
										            	  <option value="${mCategoryList.catId}"><c:out value="${mCategoryList.catName}"></c:out></option>
										</c:forEach> 
												 --%>
								</select>	
									</div>
									<button class="buttonload" id="loader">
                                   <i class="fa fa-spinner fa-spin"></i>Loading
                                   </button>
								</div>
								
                              <div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Items</label>
									<div class="col-sm-9 col-lg-10 controls">
									<select name="items[]" id="items" multiple="multiple"       data-rule-required="true" class="form-control chosen" multiplaceholder="Select Item">
										<%-- <c:forEach items="${itemsList}" var="item">
												<option value="${item.id}"><c:out value="${item.itemName}"></c:out></option>
										</c:forEach> --%>
								   </select>	
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label" for="hsn_code">Hsn Code</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="text" name="hsn_code" id="hsn_code"
											placeholder="Hsn Code" class="form-control"
											data-rule-required="true" />
									</div>
								</div>

								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">IGST %</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="text" name="item_tax3" id="item_tax3"
											placeholder="IGST" class="form-control"
											data-rule-required="true" data-rule-number="true" value="0.0"
											onchange="calTotalGst()" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">CGST %</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="text" name="item_tax2" id="item_tax2"
											placeholder="CGST" class="form-control"
											data-rule-required="true" data-rule-number="true" value="0.0"
											onchange="calTotalGst()" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">SGST %</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="text" name="item_tax1" id="item_tax1"
											placeholder="SGST" class="form-control"
											data-rule-required="true" data-rule-number="true" value="0.0" />
									</div>
								</div>


								<div class="form-group">
									<label class="col-sm-3 col-lg-2 control-label">Total
										GST Applicable %</label>
									<div class="col-sm-9 col-lg-10 controls">
										<input type="text" name="total_gst_appli" id="total_gst_appli"
											placeholder="Total GST Applicable" class="form-control"
											data-rule-required="true" data-rule-number="true" disabled />
									</div>
								</div>

								<div class="form-group">
									<div class="col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2">
										<c:choose>

											<c:when test="${isAdd==1}">

												<input type="submit" class="btn btn-primary" value="Submit">

											</c:when>

											<c:otherwise>
												<input type="submit" disabled="disabled"
													class="btn btn-primary" value="Submit">

											</c:otherwise>
										</c:choose>

										<button type="button" class="btn">Cancel</button>
									</div>
								</div>
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



	<script>
		function calTotalGst() {
			var igst = parseFloat($("#item_tax3").val());
			var cgst = parseFloat($("#item_tax2").val());
			var sgst = parseFloat(igst - cgst);
			var totGst = parseFloat(cgst + sgst);

			document.getElementById("item_tax1").setAttribute('value', sgst);

			document.getElementById("total_gst_appli").setAttribute('value',
					totGst);
		}
	</script>
<script type="text/javascript">

			function subCatChange(subCatId) {
				$('#loader').show();
				$.getJSON('${itemsBysubCatId}', {
					subCatId : subCatId,
					ajax : 'true'
				}, function(data) {
					var html = '';
					var html = '<option value="-1">ALL</option>';

					$('#loader').hide();
					var len = data.length;
					for ( var i = 0; i < len; i++) {
						html += '<option value="' + data[i].id + '">'
								+ data[i].itemName + '</option>';
					}
					html += '</option>';
					$('#items').html(html);
					   $("#items").trigger("chosen:updated");

				});
			}
			 
			
			function catChange(cat_id) {
				$('#loader').show();
				$.getJSON('${getGroup2ByCatId}', {
					catId : cat_id,
					ajax : 'true'
				}, function(data) {
					var html = '';
					$('#loader').hide();
					var len = data.length;
					for ( var i = 0; i < len; i++) {
						html += '<option value="' + data[i].subCatId + '">'
								+ data[i].subCatName + '</option>';
					}
					html += '</option>';
					$('#sub_cat_id').html(html);
					   $("#sub_cat_id").trigger("chosen:updated");

				});
			}
			</script>
			<script type="text/javascript">
			$(document).ready(function() { // if all label selected set all items selected
				
			$('#items').change(
					function () {
						 var selected=$('#items').val();
						 var subCatId=$('#sub_cat_id').val();
				
			        if(selected==-1){
						$.getJSON('${itemsBysubCatId}', {
							subCatId : subCatId,
							ajax : 'true'
						}, function(data) {
							var html = '<option value="">Items</option>';
						
							var len = data.length;
							
							$('#items')
						    .find('option')
						    .remove()
						    .end()
						
							for ( var i = 0; i < len; i++) {
			    
			                   $("#items").append(
			                           $("<option selected></option>").attr(
			                               "value", data[i].id).text(data[i].itemName)
			                       );
							}
					
							   $("#items").trigger("chosen:updated");
						});
			  }
			});
			});

			</script>
			
</body>
</html>