<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="getBillList" value="/getSubCatReport"></c:url>
	<c:url var="getAllCatByAjax" value="/getAllCatByAjax"></c:url>
	<c:url var="getGroup2ByCatId" value="/getSubCatByCatIdForReport"></c:url>

	<c:url var="getSubCatReportForGraph" value="/getSubCatReportForGraph"></c:url>




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
					<i class="fa fa-file-o"></i> Sub Category-wise Report
				</h1>
				<h4></h4>
			</div>
		</div>
		<!-- END Page Title -->

		<!-- BEGIN Breadcrumb -->
		<div id="breadcrumbs">
			<ul class="breadcrumb">
				<li><i class="fa fa-home"></i> <a
					href="${pageContext.request.contextPath}/home">Home</a> <span
					class="divider"><i class="fa fa-angle-right"></i></span></li>
				<li class="active">Bill Report</li>
			</ul>
		</div>
		<!-- END Breadcrumb -->

		<!-- BEGIN Main Content -->
		<div class="box">
			<div class="box-title">
				<h3>
					<i class="fa fa-bars"></i>Sub Category-wise Report
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

						<label class="col-sm-3 col-lg-2	 control-label">To Date</label>
						<div class="col-sm-6 col-lg-4 controls date_select">
							<input class="form-control date-picker" id="toDate" name="toDate"
								size="30" type="text" value="${todaysDate}" />
						</div>
					</div>
				</div>
				<br> <br>
				<div class="row">

					<div class="col-md-6" style="text-align: center;">
						<button class="btn btn-info" onclick="searchReport()">Search
							Report</button>
						<button class="btn btn-primary" value="PDF" id="PDFButton"
							onclick="genPdf()">PDF</button>
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
					<i class="fa fa-list-alt"></i>Sub Category-wise Report
				</h3>
			</div>
			<div class=" box-content">
				<div class="row">
					<div class="col-md-12 table-responsive">
						<table class="table table-bordered table-striped fill-head "
							style="width: 100%" id="table_grid">
							<thead style="background-color: #f3b5db;">
								<tr>
									<th>Sr.No.</th>
									<th>Sub Category Name</th>
									<th>Sold Qty</th>
									<th>Sold Amt</th>
									<th>Var Qty</th>
									<th>Var Amt</th>
									<th>Ret Qty</th>
									<th>Ret Amt</th>
									<th>Net Qty</th>
									<th>Net Amt</th>
									<th>Ret Amt %</th>
									<th>Contribution %</th>
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

		</div>

		<div class="box">

			<div class=" box-content">
				<div class="row" style="display: inline-block; width: 100%;">
					<div
						style="float: none !important; width: 100% !important; margin: 0 0 15px 0;">
						<div id="pieChart"></div>
					</div>

					<div class="clr"></div>

				</div>
			</div>




		</div>

	</div>
	<!-- END Main Content -->

	<footer>
		<p>2017 © Monginis.</p>
	</footer>

	<a id="btn-scrollup" class="btn btn-circle btn-lg" href="#"><i
		class="fa fa-chevron-up"></i></a>



	<script type="text/javascript">
		function searchReport() {
			//	var isValid = validate();
			var from_date = $("#fromDate").val();
			var to_date = $("#toDate").val();
			$('#loader').show();

			$.getJSON('${getBillList}',

			{

				fromDate : from_date,
				toDate : to_date,

				ajax : 'true'

			}, function(data) {

				//alert(data);

				$('#table_grid td').remove();
				$('#loader').hide();

				if (data == "") {
					alert("No records found !!");
					document.getElementById("expExcel").disabled = true;
				}

				drawGraph();

				var netTotalForContri = 0;
				$.each(data, function(key, report) {
					netTotalForContri = netTotalForContri + report.netAmt;
				})

				var totalSoldQty = 0;
				var totalSoldAmt = 0;
				var totalVarQty = 0;
				var totalVarAmt = 0;
				var totalRetQty = 0;
				var totalRetAmt = 0;
				var totalNetQty = 0;
				var totalNetAmt = 0;
				var retAmtPer = 0;
				var contriTotal = 0;

				$.each(data, function(key, report) {

					totalSoldQty = totalSoldQty + report.soldQty;
					totalSoldAmt = totalSoldAmt + report.soldAmt;
					totalVarQty = totalVarQty + report.varQty;
					totalVarAmt = totalVarAmt + report.varAmt;
					totalRetQty = totalRetQty + report.retQty;
					totalRetAmt = totalRetAmt + report.retAmt;
					totalNetQty = totalNetQty + report.netQty;
					totalNetAmt = totalNetAmt + report.netAmt;

					if (!isNaN(report.retAmtPer)) {
						retAmtPer = retAmtPer + report.retAmtPer;
					}

					document.getElementById("expExcel").disabled = false;
					document.getElementById('range').style.display = 'block';
					var index = key + 1;
					//var tr = "<tr>";

					var tr = $('<tr></tr>');

					tr.append($('<td></td>').html(key + 1));

					tr.append($('<td></td>').html(report.subCatName));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.soldQty.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.soldAmt.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.varQty.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.varAmt.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.retQty.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.retAmt.toFixed(2))));

					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.netQty.toFixed(2))));
					tr.append($('<td style="text-align:right;"></td>').html(
							addCommas(report.netAmt.toFixed(2))));

					if (isNaN(report.retAmtPer)) {
						tr.append($('<td style="text-align:right;"></td>')
								.html("0.00%"));
					} else {
						tr.append($('<td style="text-align:right;"></td>')
								.html(
										addCommas(report.retAmtPer.toFixed(2))
												+ "%"));
					}

					var contri = (report.netAmt * 100) / netTotalForContri;
					contriTotal = contriTotal + contri;

					tr.append($('<td style="text-align:right;"></td>').html(
							contri.toFixed(2)));

					$('#table_grid tbody').append(tr);

				})

				var tr = $('<tr></tr>');

				tr.append($('<td  ></td>').html(" "));

				tr.append($('<td style="font-weight:bold;"></td>')
						.html("Total"));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalSoldQty.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalSoldAmt.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalVarQty.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalVarAmt.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalRetQty.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalRetAmt.toFixed(2))));

				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalNetQty.toFixed(2))));
				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(totalNetAmt.toFixed(2))));

				tr.append($('<td style="text-align:right;"></td>').html(
						addCommas(retAmtPer.toFixed(2)) + "%"));

				tr.append($('<td style="text-align:right;"></td>').html(
						contriTotal.toFixed(2)));

				$('#table_grid tbody').append(tr);

			});

		}
	</script>


	<script>
		function addCommas(x) {

			x = String(x).toString();
			var afterPoint = '';
			if (x.indexOf('.') > 0)
				afterPoint = x.substring(x.indexOf('.'), x.length);
			x = Math.floor(x);
			x = x.toString();
			var lastThree = x.substring(x.length - 3);
			var otherNumbers = x.substring(0, x.length - 3);
			if (otherNumbers != '')
				lastThree = ',' + lastThree;
			return otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ",")
					+ lastThree + afterPoint;
		}
	</script>



	<script type="text/javascript">
		function drawGraph() {

			google.charts.load("current", {
				packages : [ "corechart" ]
			});
			google.charts.setOnLoadCallback(drawPieChart1);

			var type = $
			{
				type
			}
			;

		}
	</script>

	<script type="text/javascript">
		function drawPieChart1() {

			var chart;
			var datag = '';
			var a = "";
			var dataSale = [];
			var Header = [ 'Sub Category', 'Amount', 'ID' ];
			dataSale.push(Header);

			$.getJSON('${getSubCatReportForGraph}', {
				ajax : 'true'
			}, function(chartsdata) {
				//alert("---" + JSON.stringify(chartsdata));

				var len = chartsdata.length;
				datag = datag + '[';
				$.each(chartsdata, function(key, chartsdata) {
					var temp = [];

					var netAmt = parseFloat(chartsdata.netAmt);
					if (netAmt < 0) {
						netAmt = 0;
					}

					temp
							.push(chartsdata.subCatName + " ("
									+ (parseFloat(netAmt).toFixed(2)) + ")",
									(parseFloat(netAmt)),
									parseInt(chartsdata.subCatId));

					dataSale.push(temp);

				});

				//console.log(dataSale);
				var data1 = google.visualization.arrayToDataTable(dataSale);

				var options = {
					width : 900,
					height : 700,
					title : ' ',
					pieHole : 0.4,
					backgroundColor : 'transparent',
					pieSliceText : 'none',
					sliceVisibilityThreshold : 0,
					legend : {
						position : 'none',
						labeledValueText : 'both',
						textStyle : {
							color : 'red',
							fontSize : 10
						}
					},
					is3D : true,
				};
				//  alert(222);
				chart = new google.visualization.PieChart(document
						.getElementById('pieChart'));

				function selectQtyHandler() {
					// alert("hii");
					var selectedItem = chart.getSelection()[0];
					if (selectedItem) {
						// alert("hii selectedItem");
						i = selectedItem.row, 0;
					}
				}

				google.visualization.events.addListener(chart, 'select',
						selectQtyHandler);
				chart.draw(data1, options);

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
			var fromDate = $("#fromDate").val();
			var toDate = $("#toDate").val();

			window
					.open('${pageContext.request.contextPath}/pdfForReport?url=pdf/showSaleReportBySubCatPdf/'
							+ fromDate + '/' + toDate);

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

			window.open("${pageContext.request.contextPath}/exportToExcelNew");
			document.getElementById("expExcel").disabled = true;
		}
	</script>

	<!-- CHARTS -->

	<script type="text/javascript"
		src="https://www.gstatic.com/charts/loader.js"></script>

	<!-- ------ -->

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