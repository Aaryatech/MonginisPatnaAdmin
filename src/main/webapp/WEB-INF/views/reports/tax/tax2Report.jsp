<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="getBillList" value="/getSaleBillwise"></c:url>

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

		<!-- END Page Title -->

		<!-- END Breadcrumb -->

		<!-- BEGIN Main Content -->
		<div class="box">
			<div class="box-title">
				<h3>
					<i class="fa fa-bars"></i>Tax2 Report
				</h3>

			</div>

			<div class="box-content">
				<form action="${pageContext.request.contextPath}/showTax2Report"
					class="form-horizontal" method="get" id="validation-form">
					<div class="row">


						<div class="form-group">
							<label class="col-sm-3 col-lg-2	 control-label">From Date</label>
							<div class="col-sm-6 col-lg-2 controls date_select">
								<input class="form-control date-picker" id="fromDate"
									name="fromDate" size="30" type="text" value="${fromDate}" />
							</div>

							<!-- </div>

					<div class="form-group  "> -->

							<label class="col-sm-3 col-lg-2	 control-label">To Date</label>
							<div class="col-sm-6 col-lg-2 controls date_select">
								<input class="form-control date-picker" id="toDate"
									name="toDate" size="30" type="text" value="${toDate}" />
							</div>
							<!-- </div>

				</div>


				<div class="row">
					<div class="col-md-12" style="text-align: center;"> -->
							<input type="submit" class="btn btn-info" value="Search" />
						</div>
					</div>
				</form>


				<div align="center" id="loader" style="display: none">

					<span>
						<h4>
							<font color="#343690">Loading</font>
						</h4>
					</span> <span class="l-1"></span> <span class="l-2"></span> <span
						class="l-3"></span> <span class="l-4"></span> <span class="l-5"></span>
					<span class="l-6"></span>
				</div>

			</div>
		</div>


		<div class="box">


			<div class=" box-content">
				<div class="row">
					<div  style="overflow-x: scroll;">
						<table class="table table-bordered table-striped fill-head "
							id="table_grid">
							<thead style="background-color: #f3b5db;">
								<tr>
									<th>Sr.No.</th>
									<th>Invoice No</th>
									<th>Bill No.</th>
									<th>Bill_Date</th>
									<th>Party Name</th>
									<th>GSTIN</th>
									<th>Sell @ 28%</th>
									<th>Sell @ 18%</th>
									<th>Sell @ 12%</th>
									<th>Sell @ 5%
									</th>
									<th>Sell @ 0%
									</th>
									<th>Taxable Value
									</th>
									<th>SGST @ 14%
									</th>
									<th>CGST @ 14%
									</th>
									<th>SGST @ 9%
									</th>
									<th>CGST @ 9%
									</th>
									<th>SGST @ 6%
									</th>
									<th>CGST @ 6%</th>
									<th>SGST @ 2.5%</th>
									<th>CGST @ 2.5%</th>
									<th>SGST @ 0%</th>
									<th>CGST @ 0%</th>
									<th>SGST Value</th>
									<th>CGST Value</th>
									<th>GROSS BILL</th>

								</tr>
							</thead>
							<tbody>
								<c:forEach items="${taxReportList}" var="taxList"
									varStatus="count">
									<tr>
										<td><c:out value="${count.index+1}" /></td>
										<td><c:out value="${taxList.invoiceNo}" /></td>
										<td><c:out value="${taxList.billNo}" /></td>
										<td><c:out value="${taxList.billDate}" /></td>
										<td><c:out value="${taxList.frName}" /></td>
										<td><c:out value="${taxList.frGstNo}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.taxableAmtTwentyEight}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.taxableAmtEighteen}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.taxableAmtTwelve}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.taxableAmtFive}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.taxableAmtZero}" /></td>
										<c:set var="taxableAmt"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${(taxList.taxableAmtZero)+(taxList.taxableAmtFive)+(taxList.taxableAmtTwelve)+(taxList.taxableAmtEighteen)+(taxList.taxableAmtTwentyEight)}" /></c:set>
										<c:set var="sgstAmt"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${(taxList.sgstAmtTwentyEight)+(taxList.sgstAmtEighteen)+(taxList.sgstAmtTwelve)+(taxList.sgstAmtFive)+(taxList.sgstAmtZero)}" /></c:set>
										<c:set var="cgstAmt"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${(taxList.cgstAmtTwentyEight)+(taxList.cgstAmtEighteen)+(taxList.cgstAmtTwelve)+(taxList.cgstAmtFive)+(taxList.cgstAmtZero)}" /></c:set>
										<c:set var="total"><fmt:formatNumber type="number" minFractionDigits="2" maxFractionDigits="2" value="${((taxList.taxableAmtZero)+(taxList.taxableAmtFive)+(taxList.taxableAmtTwelve)+(taxList.taxableAmtEighteen)+(taxList.taxableAmtTwentyEight))+((taxList.sgstAmtTwentyEight)+(taxList.sgstAmtEighteen)+(taxList.sgstAmtTwelve)+(taxList.sgstAmtFive)+(taxList.sgstAmtZero))+((taxList.cgstAmtTwentyEight)+(taxList.cgstAmtEighteen)+(taxList.cgstAmtTwelve)+(taxList.cgstAmtFive)+(taxList.cgstAmtZero))}" /></c:set>
												
										<td style="text-align: right;"><c:out
												value="${taxableAmt}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.sgstAmtTwentyEight}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.cgstAmtTwentyEight}" /></td>		
	                                    <td style="text-align: right;"><c:out
												value="${taxList.sgstAmtEighteen}" /></td>
										  <td style="text-align: right;"><c:out
												value="${taxList.cgstAmtEighteen}" /></td>		
									   <td style="text-align: right;"><c:out
												value="${taxList.sgstAmtTwelve}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.cgstAmtTwelve}" /></td>		
										<td style="text-align: right;"><c:out
												value="${taxList.sgstAmtFive}" /></td>
											<td style="text-align: right;"><c:out
												value="${taxList.cgstAmtFive}" /></td>		
										<td style="text-align: right;"><c:out
												value="${taxList.sgstAmtZero}" /></td>
										<td style="text-align: right;"><c:out
												value="${taxList.cgstAmtZero}" /></td>
												<td style="text-align: right;"><c:out
												value="${sgstAmt}" /></td>
												<td style="text-align: right;"><c:out
												value="${cgstAmt}" /></td>
												<td style="text-align: right;"> ${total} </td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<br>
					<div class="form-group" id="range">



						<div class="col-sm-3  controls">
							<input type="button" id="expExcel" class="btn btn-primary"
								value="EXPORT TO Excel" onclick="exportToExcel();">
						</div>
					</div>
				</div>

			</div>

		</div>
	</div>
	<!-- END Main Content -->

	<footer>
		<p>2018 © Monginis.</p>
	</footer>

	<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
		class="fa fa-chevron-up"></i></a>


	<!-- 	<script type="text/javascript">
		function searchReport() {
		//	var isValid = validate();

				var selectedFr = $("#selectFr").val();
				var routeId=$("#selectRoute").val();
				
				var from_date = $("#fromDate").val();
				var to_date = $("#toDate").val();

				$('#loader').show();

				$
						.getJSON(
								'${getBillList}',

								{
									fr_id_list : JSON.stringify(selectedFr),
									fromDate : from_date,
									toDate : to_date,
									route_id:routeId,
									ajax : 'true'

								},
								function(data) {
									
								
									$('#table_grid td').remove();
									$('#loader').hide();

									if (data == "") {
										alert("No records found !!");
										  document.getElementById("expExcel").disabled=true;
									}

									$
											.each(
													data,
													function(key, report) {
														
														  document.getElementById("expExcel").disabled=false;
															document.getElementById('range').style.display = 'block';
														var index = key + 1;
														//var tr = "<tr>";
														
														
														var tr = $('<tr></tr>');

													  	tr.append($('<td></td>').html(key+1));

													  	tr.append($('<td></td>').html(report.invoiceNo));

													  	tr.append($('<td></td>').html(report.billDate));

													  	tr.append($('<td></td>').html(report.frName));
													  	
													  	
													  	tr.append($('<td></td>').html(report.frCity));

													  	tr.append($('<td></td>').html(report.frGstNo));

													  	tr.append($('<td></td>').html(report.taxableAmt));
													  	
													  	if(report.isSameState==1){
														  	tr.append($('<td></td>').html(report.cgstSum));
														  	tr.append($('<td></td>').html(report.sgstSum));
														  	tr.append($('<td></td>').html(0));
														}
														else{
															tr.append($('<td></td>').html(0));
														  	tr.append($('<td></td>').html(0));
														  	tr.append($('<td></td>').html(report.igstSum));
														}
														tr.append($('<td></td>').html(report.roundOff));
														var total;
														
														if(report.isSameState==1){
															 total=parseFloat(report.taxableAmt)+parseFloat(report.cgstSum+report.sgstSum);
														}
														else{
															
															 total=report.taxableAmt+report.igstSum;
														}

													  	tr.append($('<td></td>').html(total));

														$('#table_grid tbody')
																.append(
																		tr);
														

													})

								});

			
		}
	</script> -->

	<!-- 	<script type="text/javascript">
		function validate() {

			var selectedFr = $("#selectFr").val();
			var selectedMenu = $("#selectMenu").val();
			var selectedRoute = $("#selectRoute").val();


			var isValid = true;

			if (selectedFr == "" || selectedFr == null  ) {
 
				if(selectedRoute=="" || selectedRoute ==null ) {
					alert("Please Select atleast one ");
					isValid = false;
				}
				//alert("Please select Franchise/Route");
 
			} else if (selectedMenu == "" || selectedMenu == null) {

				isValid = false;
				alert("Please select Menu");

			}
			return isValid;

		}
	</script> -->

	<!-- <script type="text/javascript">
		function updateTotal(orderId, rate) {
			
			var newQty = $("#billQty" + orderId).val();

			var total = parseFloat(newQty) * parseFloat(rate);


			 $('#billTotal'+orderId).html(total);
		}
	</script> -->

	<script>
$('.datepicker').datepicker({
    format: {
        /*
         * Say our UI should display a week ahead,
         * but textbox should store the actual date.
         * This is useful if we need UI to select local dates,
         * but store in UTC
         */
    	 format: 'mm/dd/yyyy',
    	    startDate: '-3d'
    }
});

/* function genPdf()
{
	var from_date = $("#fromDate").val();
	var to_date = $("#toDate").val();
	var selectedFr = $("#selectFr").val();
	var routeId=$("#selectRoute").val();

   window.open('${pageContext.request.contextPath}/pdfForReport?url=pdf/showSaleReportByDatePdf/'+from_date+'/'+to_date+'/'+selectedFr+'/'+routeId+'/');

	//window.open("${pageContext.request.contextPath}/pdfForReport?url=showSaleReportByDatePdf/"+from_date+"/"+to_date);
	
	} */


</script>

	<script type="text/javascript">
/* 
function disableFr(){

	//alert("Inside Disable Fr ");
document.getElementById("selectFr").disabled = true;

}

function disableRoute(){

	//alert("Inside Disable route ");
	var x=document.getElementById("selectRoute")
	//alert(x.options.length);
	var i;
	for(i=0;i<x;i++){
		document.getElementById("selectRoute").options[i].disabled;
		 //document.getElementById("pets").options[2].disabled = true;
	}
//document.getElementById("selectRoute").disabled = true;

} */

function exportToExcel()
{
	 
	window.open("${pageContext.request.contextPath}/exportToExcel");
			document.getElementById("expExcel").disabled=true;
}

</script>

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
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/chosen-bootstrap/chosen.jquery.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-inputmask/bootstrap-inputmask.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-tags-input/jquery.tagsinput.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/jquery-pwstrength/jquery.pwstrength.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-fileupload/bootstrap-fileupload.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-duallistbox/duallistbox/bootstrap-duallistbox.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/dropzone/downloads/dropzone.min.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-timepicker/js/bootstrap-timepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/clockface/js/clockface.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-colorpicker/js/bootstrap-colorpicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-datepicker/js/bootstrap-datepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/date.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-daterangepicker/daterangepicker.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-switch/static/js/bootstrap-switch.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-wysihtml5/wysihtml5-0.3.0.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/bootstrap-wysihtml5/bootstrap-wysihtml5.js"></script>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/assets/ckeditor/ckeditor.js"></script>

	<!--flaty scripts-->
	<script src="${pageContext.request.contextPath}/resources/js/flaty.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/flaty-demo-codes.js"></script>
</body>
</html>