<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="getBillList" value="/getReportHSNwise"></c:url>



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
					<i class="fa fa-file-o"></i>NET SALES HSN CODE WISE SUMMERY Report
				</h1>
				<h4></h4>
			</div>
		</div>
		<!-- END Page Title -->

		<!-- BEGIN Breadcrumb -->
		<%-- <div id="breadcrumbs">
			<ul class="breadcrumb">
				<li><i class="fa fa-home"></i> <a
					href="${pageContext.request.contextPath}/home">Home</a> <span
					class="divider"><i class="fa fa-angle-right"></i></span></li>
				<li class="active">HSN Report</li>
			</ul>
		</div> --%>
		<!-- END Breadcrumb -->

		<!-- BEGIN Main Content -->
		<div class="box">
			<div class="box-title">
				<h3>
					<i class="fa fa-bars"></i>View HSN Wise Report
				</h3>

			</div>

			<div class="box-content">
				<div class="row">


					<div class="form-group">
						<label class="col-sm-3 col-lg-2	 control-label">From Date</label>
						<div class="col-sm-6 col-lg-4 controls date_select">
							<input class="form-control date-picker" id="fromDate"
								name="fromDate" size="30" type="text" value="${todaysDate}" />
						</div>

						<!-- </div>

					<div class="form-group  "> -->

						<label class="col-sm-3 col-lg-2	 control-label">To Date</label>
						<div class="col-sm-6 col-lg-4 controls date_select">
							<input class="form-control date-picker" id="toDate" name="toDate"
								size="30" type="text" value="${todaysDate}" />
						</div>
					</div>

				</div>
				<br>
				<div class="row">
					<div class="form-group">
						<label class="col-sm-3 col-lg-2	 control-label">Select
							Type</label>
						<div class="col-sm-10 col-lg-4  controls">

							<select data-placeholder="Select Type"
								class="form-control chosen" id="type" name="type" required>
								<option value="">Select Type</option>
								<option value="1">Bill wise</option>
								<option value="2">Credit Note Wise</option>
								<option value="3">Consolidated</option>
							</select>
						</div>



						<div class="col-md-4">
							<button class="btn btn-info" onclick="searchReport()">Search
								Report</button>
							<button class="btn btn-primary" value="PDF" id="PDFButton"
								onclick="genPdf()">PDF</button>
						</div>

						<!-- <div class="col-sm-9 col-lg-5 controls">
 -->
					</div>
				</div>
				<br>



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
			<div class="box-title">
				<h3>
					<i class="fa fa-list-alt"></i>HSN Report
				</h3>

			</div>

			<form id="submitBillForm"
				action="${pageContext.request.contextPath}/submitNewBill"
				method="post">
				<div class=" box-content">
					<div class="row">
						<div class="col-md-12 table-responsive">
							<table class="table table-bordered table-striped fill-head "
								style="width: 100%" id="table_grid">
								<thead style="background-color: #f3b5db;">
									<tr>
										<th>Sr.No.</th>
										<th>HSN</th>
										<th>TAX %</th>
										<th>MANUF</th>
										<th>RET</th>
										<th>TOTAL</th>
										<th>TAXABLE AMT</th>
										<th>CGST</th>
										<th>CGST AMT</th>
										<th>SGST</th>
										<th>SGST AMT</th>
										<th>Total</th>

									</tr>
								</thead>
								<tbody>

								</tbody>
							</table>
						</div>
						<div class="form-group" style="display: none;" id="range">



							<div class="col-sm-3  controls">
								<input type="button" id="expExcel" class="btn btn-primary"
									value="EXPORT TO Excel" onclick="exportToExcel();"
									disabled="disabled">
							</div>
						</div>
					</div>

				</div>
			</form>
		</div>
	</div>
	<!-- END Main Content -->

	<footer>
		<p>2019 © Monginis.</p>
	</footer>

	<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
		class="fa fa-chevron-up"></i></a>


	<script type="text/javascript">
		function searchReport() {
			//	var isValid = validate();

			var from_date = $("#fromDate").val();
			var to_date = $("#toDate").val();
			var type = $("#type").val();

			$('#loader').show();

			$.getJSON('${getBillList}',

			{

				fromDate : from_date,
				toDate : to_date,
				type : type,
				ajax : 'true'

			}, function(data) {

				$('#table_grid td').remove();
				$('#loader').hide();

				if (data == "") {
					alert("No records found !!");
					document.getElementById("expExcel").disabled = true;
				}

				var totalTaxableAmt = 0;
				var totalSgst = 0;
				var totalCgst = 0;
				var totalFinal = 0;

				$.each(data, function(key, report) {

					document.getElementById("expExcel").disabled = false;
					document.getElementById('range').style.display = 'block';
					var index = key + 1;
					//var tr = "<tr>";

					var tr = $('<tr></tr>');

					tr.append($('<td></td>').html(key + 1));

					tr.append($('<td></td>').html(report.itemHsncd));

					tr.append($('<td></td>').html(
							report.itemTax1 + report.itemTax2));

					tr.append($('<td  style="text-align:right;"></td>').html(
							report.billQty));

					tr.append($('<td  style="text-align:right;"></td>').html(
							report.grnGvnQty));
					tr.append($('<td  style="text-align:right;"></td>').html(
							report.billQty - report.grnGvnQty));

					totalTaxableAmt = totalTaxableAmt + report.taxableAmt;

					totalSgst = totalSgst + report.sgstRs;
					totalCgst = totalCgst + report.cgstRs;

					totalFinal = totalFinal + report.cgstRs + report.sgstRs
							+ report.taxableAmt;

					tr.append($('<td style="text-align:right;"></td>').html(
							report.taxableAmt.toFixed(2)));
					tr.append($('<td style="text-align:right;"></td>').html(
							report.itemTax1));
					tr.append($('<td style="text-align:right;"></td>').html(
							report.cgstRs.toFixed(2)));
					tr.append($('<td style="text-align:right;"></td>').html(
							report.itemTax2));
					tr.append($('<td style="text-align:right;"></td>').html(
							report.sgstRs.toFixed(2)));
					tr.append($('<td style="text-align:right;"></td>').html(
							(report.cgstRs + report.sgstRs + report.taxableAmt)
									.toFixed(2)));

					$('#table_grid tbody').append(tr);

				})

				var tr = $('<tr></tr>');

				tr.append($('<td></td>').html(""));
				tr.append($('<td></td>').html(""));
				tr.append($('<td></td>').html(""));
				tr.append($('<td></td>').html(""));
				tr.append($('<td></td>').html(""));
				tr.append($('<td style="font-weight:bold;"></td>')
						.html("Total"));
				tr.append($('<td style="text-align:right;"></td>').html(
						totalTaxableAmt.toFixed(2)));
				tr.append($('<td></td>').html(""));
				tr.append($('<td style="text-align:right;"></td>').html(
						totalCgst.toFixed(2)));
				tr.append($('<td></td>').html(""));
				tr.append($('<td style="text-align:right;"></td>').html(
						totalSgst.toFixed(2)));

				tr.append($('<td style="text-align:right;"></td>').html(
						totalFinal.toFixed(2)));

				$('#table_grid tbody').append(tr);

			});

		}
	</script>

	<script type="text/javascript">
		function validate() {

			var selectedFr = $("#selectFr").val();
			var selectedMenu = $("#selectMenu").val();
			var selectedRoute = $("#selectRoute").val();

			var isValid = true;

			if (selectedFr == "" || selectedFr == null) {

				if (selectedRoute == "" || selectedRoute == null) {
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
	</script>

	<script type="text/javascript">
		function updateTotal(orderId, rate) {

			var newQty = $("#billQty" + orderId).val();

			var total = parseFloat(newQty) * parseFloat(rate);

			$('#billTotal' + orderId).html(total);
		}
	</script>

	<script>
		$('.datepicker').datepicker({
			format : {
				/*
				 * Say our UI should display a week ahead,
				 * but textbox should store the actual date.
				 * This is useful if we need UI to select local dates,
				 * but store in UTC
				 */
				format : 'mm/dd/yyyy',
				startDate : '-3d'
			}
		});

		function genPdf() {
			var from_date = $("#fromDate").val();
			var to_date = $("#toDate").val();

			/* 	window
						.open('${pageContext.request.contextPath}/pdfForReport?url=pdf/showSaleReportByDatePdf/'
								+ from_date + '/' + to_date);
			 */
			window.open("${pageContext.request.contextPath}/getHsnWisePdf/"
					+ from_date + "/" + to_date);

		}
	</script>

	<script type="text/javascript">
		function disableFr() {

			//alert("Inside Disable Fr ");
			document.getElementById("selectFr").disabled = true;

		}

		function disableRoute() {

			//alert("Inside Disable route ");
			var x = document.getElementById("selectRoute")
			//alert(x.options.length);
			var i;
			for (i = 0; i < x; i++) {
				document.getElementById("selectRoute").options[i].disabled;
				//document.getElementById("pets").options[2].disabled = true;
			}
			//document.getElementById("selectRoute").disabled = true;

		}

		function exportToExcel() {

			window.open("${pageContext.request.contextPath}/exportToExcel");
			document.getElementById("expExcel").disabled = true;
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