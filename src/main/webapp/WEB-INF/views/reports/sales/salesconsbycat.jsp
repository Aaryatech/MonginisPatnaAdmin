<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script src="${pageContext.request.contextPath}/resources/js/main.js"></script>
<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>

<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>

	<c:url var="getBillList" value="/getSaleReportRoyConsoByCat"></c:url>
	<c:url var="getAllCatByAjax" value="/getAllCatByAjax"></c:url>

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
					<i class="fa fa-file-o"></i>Item-wise Royalty Report
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
					<i class="fa fa-bars"></i>Item-wise Royalty Report
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

				<!-- <div class="col-sm-9 col-lg-5 controls">
 -->
				<div class="row">
					<div class="form-group">
						<label class="col-sm-3 col-lg-2 control-label">Select
							Route</label>
						<div class="col-sm-6 col-lg-4 controls">
							<select data-placeholder="Select Route"
								class="form-control chosen" name="selectRoute" id="selectRoute"
								onchange="disableFr()">
								<option value="0">Select Route</option>
								<c:forEach items="${routeList}" var="route" varStatus="count">
									<option value="${route.routeId}"><c:out
											value="${route.routeName}" />
									</option>

								</c:forEach>
							</select>

						</div>

						<label class="col-sm-3 col-lg-2 control-label"><b>OR</b>Select
							Franchisee</label>
						<div class="col-sm-6 col-lg-4">

							<select data-placeholder="Choose Franchisee"
								class="form-control chosen" multiple="multiple" tabindex="-1"
								id="selectFr" name="selectFr" onchange="disableRoute()">

								<option value="-1"><c:out value="All" /></option>

								<c:forEach items="${unSelectedFrList}" var="fr"
									varStatus="count">
									<option value="${fr.frId}"><c:out value="${fr.frName}" /></option>
								</c:forEach>
							</select>

						</div>
					</div>
				</div>

				<br>
				<div class="row">

					<div class="form-group">

						<div class="col-md-2">Select Category</div>
						<div class="col-md-4" style="text-align: left;">
							<select data-placeholder="Select Group"
								class="form-control chosen" name="item_grp1" tabindex="-1"
								id="item_grp1" data-rule-required="true"
								onchange="setCatOptions(this.value)" >
								

								<c:forEach items="${catList}" var="mCategoryList">
									<option value="${mCategoryList.catId}"><c:out
											value="${mCategoryList.catName}"></c:out></option>
								</c:forEach>


							</select>
						</div>

						<button class="btn btn-info" onclick="searchReport()">Search
							Report</button>
						<!-- <button class="btn search_btn"  onclick="showChart()">Graph</button> -->


						<button class="btn btn-primary" value="PDF" id="PDFButton"
							onclick="genPdf()" disabled="disabled">PDF</button>
						<%-- <a
							href="${pageContext.request.contextPath}/pdfForReport?url=showSaleRoyaltyByCatPdf"
							target="_blank">PDF</a>
 --%>
					</div>


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
						<i class="fa fa-list-alt"></i>Royalty Cons Report (r10)
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
											<th>Item Name</th>
											<th>Sale Qty</th>
											<th>Sale Value</th>
											<th>GRN Qty</th>
											<th>GRN Value</th>
											<th>GVN Qty</th>
											<th>GVN Value</th>
											<th>Net Qty</th>
											<th>Net Value</th>
											<th>Royalty %</th>
											<th>Royalty Amt</th>
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

					<div id="chart_div" style="width: 100%; height: 700px;"></div>
					<div id="PieChart_div" style="width: 100%; height: 700px;"></div>

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

				//document.getElementById('chart').style.display = "block";
				document.getElementById("PieChart_div").style = "display:none";
				document.getElementById("chart_div").style = "display:none";

				var selectedFr = $("#selectFr").val();
				var routeId = $("#selectRoute").val();
				var isGraph = 0;

				var selectedCat = $("#item_grp1").val();

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
									route_id : routeId,
									cat_id_list : JSON.stringify(selectedCat),
									is_graph : isGraph,
									ajax : 'true'

								},
								function(data) {

									$('#table_grid td').remove();
									$('#loader').hide();

									var royPer = ${royPer};
									//alert(royPer);

									if (data == "") {
										alert("No records found !!");
										document.getElementById("expExcel").disabled = true;
										document.getElementById("PDFButton").disabled = true;

									}

									$
											.each(
													data.categoryList,
													function(key, cat) {
														document
																.getElementById("expExcel").disabled = false;
														document
																.getElementById('range').style.display = 'block';
														document.getElementById("PDFButton").disabled = false;

														var tr = $('<tr></tr>');
														tr
																.append($(
																		'<td></td>')
																		.html(
																				cat.catName));
														//tr.append($('<td></td>').html(key+1));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));

														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));

														$('#table_grid tbody')
																.append(tr);

														var srNo = 0;
														var tBillQtyTotal=0;
														var tBillTaxableAmtTotal=0;
														var tGrnQtyTotal=0;
														var tGrnTaxableAmtTotal=0;
														var tGvnQtyTotal=0;
														var tGvnTaxableAmtTotal=0;
														var netQtyTotal=0;
														var netValueTotal=0; 
														var rAmtTotal=0;
														
														
														$
																.each(
																		data.salesReportRoyalty,
																		function(
																				key,
																				report) {

																			if (cat.catId == report.catId) {
																				//alert("Hi");
																				srNo = srNo + 1;
																				//var index = key + 1;
																				var tr = $('<tr></tr>');
																				//tr.append($('<td></td>').html(cat.catName));
																				tr
																						.append($(
																								'<td></td>')
																								.html(
																										srNo));
																				tr
																						.append($(
																								'<td></td>')
																								.html(
																										report.item_name));
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										report.tBillQty));
																				tBillQtyTotal=tBillQtyTotal+report.tBillQty;
																				
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										(report.tBillTaxableAmt)
																												.toFixed(2)));
																				tBillTaxableAmtTotal=tBillTaxableAmtTotal+report.tBillTaxableAmt;
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										report.tGrnQty));
																				tGrnQtyTotal=tGrnQtyTotal+report.tGrnQty;
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										report.tGrnTaxableAmt));
																				tGrnTaxableAmtTotal=tGrnTaxableAmtTotal+report.tGrnTaxableAmt;
																				
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										report.tGvnQty));
																				tGvnQtyTotal=tGvnQtyTotal+report.tGvnQty;
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										report.tGvnTaxableAmt));
																				tGvnTaxableAmtTotal=tGvnTaxableAmtTotal+report.tGvnTaxableAmt;
																				
																				var netQty = report.tBillQty
																						- (report.tGrnQty + report.tGvnQty);
																				netQtyTotal=netQtyTotal+netQty;
																				netQty = netQty
																						.toFixed(2);

																				var netValue = report.tBillTaxableAmt
																						- (report.tGrnTaxableAmt + report.tGvnTaxableAmt);
																				netValueTotal=netValueTotal+netValue;
																				
																				netValue = netValue
																						.toFixed();

																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										netQty));
																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										netValue));

																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										royPer));

																				rAmt = netValue
																						* royPer
																						/ 100;
																				rAmtTotal=rAmtTotal+rAmt;
																				rAmt = rAmt
																						.toFixed(2);

																				tr
																						.append($(
																								'<td style="text-align:right;"></td>')
																								.html(
																										rAmt));
																				
																				$(
																						'#table_grid tbody')
																						.append(
																								tr);

																			}//end of if

																		})
														 tBillQtyTotal=(tBillQtyTotal).toFixed(2);
														 tBillTaxableAmtTotal=(tBillTaxableAmtTotal).toFixed(2);
														 tGrnQtyTotal=(tGrnQtyTotal).toFixed(2);
														 tGrnTaxableAmtTotal=(tGrnTaxableAmtTotal).toFixed(2);
														 tGvnQtyTotal=(tGvnQtyTotal).toFixed(2);
														 tGvnTaxableAmtTotal=(tGvnTaxableAmtTotal).toFixed(2);
														 netQtyTotal=(netQtyTotal).toFixed(2);
														 netValueTotal=(netValueTotal).toFixed(2);
														 rAmtTotal=(rAmtTotal).toFixed(2); 
														 tr = $('<tr></tr>');
														tr
																.append($(
																		'<td></td>')
																		.html("Total"));
														tr
																.append($(
																		'<td></td>')
																		.html(""));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(""+tBillQtyTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(""+tBillTaxableAmtTotal));

														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(""+tGrnQtyTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(""+tGrnTaxableAmtTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(
																				""+tGvnQtyTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(
																				""+tGvnTaxableAmtTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(
																				""+netQtyTotal));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(
																				""+netValueTotal));
														tr
																.append($(
																		'<td></td>')
																		.html(
																				""));
														tr
																.append($(
																		'<td style="text-align:right;"></td>')
																		.html(
																				""+rAmtTotal));

														$('#table_grid tbody')
																.append(tr);

													})

								});

			}
		</script>


		<script type="text/javascript">
			function showChart() {

				$("#PieChart_div").empty();
				$("#chart_div").empty();
				//document.getElementById('chart').style.display = "block";
				document.getElementById("table_grid").style = "display:none";
				document.getElementById("PieChart_div").style = "block";
				document.getElementById("chart_div").style = "block";

				var selectedFr = $("#selectFr").val();
				var routeId = $("#selectRoute").val();
				var selectedCat = $("#item_grp1").val();
				var from_date = $("#fromDate").val();
				var to_date = $("#toDate").val();

				var isGraph = 1;
				$('#loader').show();

				$
						.getJSON(
								'${getBillList}',

								{
									fr_id_list : JSON.stringify(selectedFr),
									fromDate : from_date,
									toDate : to_date,
									route_id : routeId,
									cat_id_list : JSON.stringify(selectedCat),
									is_graph : isGraph,

									ajax : 'true'

								},
								function(data) {

									$('#loader').hide();

									if (data == "") {
										alert("No records found !!");

									}
									var i = 0;

									google.charts.load('current', {
										'packages' : [ 'corechart', 'bar' ]
									});
									google.charts.setOnLoadCallback(drawStuff);

									function drawStuff() {

										// alert("Inside DrawStuff");

										var chartDiv = document
												.getElementById('chart_div');
										document.getElementById("chart_div").style.border = "thin dotted red";

										var PiechartDiv = document
												.getElementById('PieChart_div');
										document.getElementById("PieChart_div").style.border = "thin dotted red";

										var dataTable = new google.visualization.DataTable();
										dataTable.addColumn('string',
												'Category'); // Implicit domain column.
										dataTable.addColumn('number', 'NetQty'); // Implicit data column.
										dataTable.addColumn('number',
												'NetValue');

										var piedataTable = new google.visualization.DataTable();
										piedataTable.addColumn('string',
												'Category'); // Implicit domain column.
										piedataTable.addColumn('number',
												'NetValue');

										$
												.each(
														data.categoryList,
														function(key, cat) {
															var netQty = 0;
															var netValue = 0;
															$
																	.each(
																			data.salesReportRoyalty,
																			function(
																					key,
																					report) {

																				if (cat.catId === report.catId) {
																					netQty = netQty
																							+ report.tBillQty
																							- (report.tGrnQty + report.tGvnQty);
																					//netQty=netQty.toFixed(2);
																					netValue = netValue
																							+ report.tBillTaxableAmt
																							- (report.tGrnTaxableAmt + report.tGvnTaxableAmt);
																					//netValue=netValue.toFixed(2);
																					var catName = report.cat_name;
																					//alert("CatName"+catName);

																					//alert("netValue"+netValue);
																					//alert("netQty"+netQty);

																					dataTable
																							.addRows([
																									[
																											catName,
																											netQty,
																											netValue ], ]);

																					piedataTable
																							.addRows([
																									[
																											catName,
																											netValue ], ]);
																				}
																			})

														})
										// Instantiate and draw the chart.

										var materialOptions = {

											width : 500,
											chart : {
												title : 'Date wise Tax Graph',
												subtitle : 'Total tax & Taxable Amount per day',

											},
											series : {
												0 : {
													axis : 'distance'
												}, // Bind series 0 to an axis named 'distance'.
												1 : {
													axis : 'brightness'
												}
											// Bind series 1 to an axis named 'brightness'.
											},
											axes : {
												y : {
													distance : {
														label : 'Total Tax'
													}, // Left y-axis.
													brightness : {
														side : 'right',
														label : 'Taxable Amount'
													}
												// Right y-axis.
												}
											}
										};

										function drawMaterialChart() {
											var materialChart = new google.charts.Bar(
													chartDiv);

											materialChart
													.draw(
															dataTable,
															google.charts.Bar
																	.convertOptions(materialOptions));

										}

										var chart = new google.visualization.ColumnChart(
												document
														.getElementById('chart_div'));

										var Piechart = new google.visualization.PieChart(
												document
														.getElementById('PieChart_div'));
										chart
												.draw(
														dataTable,
														{
															width : 1000,
															height : 600,
															title : 'Sales Summary Group By Month'
														});

										Piechart
												.draw(
														piedataTable,
														{
															width : 1000,
															height : 600,
															title : 'Sales Summary Group By Month',
															is3D : true
														});
										// drawMaterialChart();
									}
									;

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
		</script>


		<script type="text/javascript">
			function genPdf() {
				var from_date = $("#fromDate").val();
				var to_date = $("#toDate").val();
				var selectedFr = $("#selectFr").val();
				var routeId = $("#selectRoute").val();
				var isGraph = 0;
				var selectedCat = $("#item_grp1").val();

		/* 		pdfForReport?url= */
				window
						.open('pdf/getSaleReportRoyConsoByCatPdf/'
								+ from_date
								+ '/'
								+ to_date
								+ '/'
								+ selectedFr
								+ '/' + routeId + '/' + selectedCat);

			}
			function exportToExcel() {

				window.open("${pageContext.request.contextPath}/exportToExcel");
				document.getElementById("expExcel").disabled = true;
			}
		</script>
		<script type="text/javascript">
			function setCatOptions(catId) {
				if (catId == -1) {
					$.getJSON('${getAllCatByAjax}', {
						ajax : 'true'
					}, function(data) {
						var len = data.length;
						$('#item_grp1').find('option').remove().end()

						$("#item_grp1").append(
								$("<option ></option>").attr("value", -1).text(
										"Select All"));

						for (var i = 0; i < len; i++) {

							$("#item_grp1").append(
									$("<option selected></option>").attr(
											"value", data[i].catId).text(
											data[i].catName));
						}

						$("#item_grp1").trigger("chosen:updated");
					});
				}
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