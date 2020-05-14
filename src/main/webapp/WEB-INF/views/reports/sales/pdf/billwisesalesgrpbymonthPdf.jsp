<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Sales Report Billwise PDF</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->


<style type="text/css">
table {
	border-collapse: collapse;
	font-size: 10;
	width: 100%;
	page-break-inside: auto !important
}

p {
	color: black;
	font-family: arial;
	font-size: 60%;
	margin-top: 0;
	padding: 0;
}

h6 {
	color: black;
	font-family: arial;
	font-size: 80%;
}

th {
	background-color: #EA3291;
	color: white;
}
</style>

</head>
<body onload="myFunction()">
	<h3 align="center">${FACTORYNAME}</h3>
	<p align="center">${FACTORYADDRESS}</p>

	<div align="center">
		<h5>Sales Report (Group by Month Wise) &nbsp;&nbsp;&nbsp;&nbsp;
			From &nbsp; ${fromDate} &nbsp;To &nbsp; ${toDate}</h5>
	</div>
	<div align="center">
		<h5>Franchisee : &nbsp;&nbsp;&nbsp;&nbsp; ${fr}</h5>
	</div>
	<table align="center" border="1" cellspacing="0" cellpadding="1"
		id="table_grid" class="table table-bordered">
		<thead>
			<!-- <tr class="bgpink">
				<th height="25">Sr.No.</th>
				<th>Month</th>
				<th>Taxable Value</th>
				<th>Tax Value</th>
				<th>Grand Total</th>
				<th>GRN Taxable Value</th>
				<th>GRN Tax Value</th>
				<th>GRN Grand Total</th>
				<th>GVN Taxable Value</th>
				<th>GVN Tax Value</th>
				<th>GVN Grand Total</th>
				<th>NET Taxable Total</th>
				<th>NET Tax Total</th>
				<th>NET Grand Total</th>
			</tr> -->

			<tr class="bgpink">
				<th height="25">Sr.No.</th>
				<th>Month</th>
				<th>Grand Total</th>
				<th>GRN Grand Total</th>
				<th>GVN Grand Total</th>
				<th>NET Grand Total</th>
				<th>Contribution %</th>
			</tr>
		</thead>
		<tbody>


			<c:set var="totalNetValForContri" value="${0}" />
			<c:forEach items="${report}" var="report" varStatus="count">
				<c:set var="totalNetValForContri"
					value="${totalNetValForContri + report.netGrandTotal}" />
			</c:forEach>



			<c:set var="totalGrandTotal" value="${0}" />
			<c:set var="totalTax" value="${0 }" />
			<c:set var="totalTaxableAmt" value="${0 }" />

			<c:set var="totalGrnGrandTotal" value="${0 }" />
			<c:set var="totalGrnTaxableAmt" value="${0}" />
			<c:set var="totalGrnTax" value="${0 }" />

			<c:set var="totalGvnGrandTotal" value="${0 }" />
			<c:set var="totalGvnTax" value="${0}" />
			<c:set var="totalGvnTaxableAmt" value="${0 }" />


			<c:set var="totalNetGrandTotal" value="${0 }" />
			<c:set var="totalNetTax" value="${0}" />
			<c:set var="totalNetTaxableAmt" value="${0 }" />

			<c:set var="totalContri" value="${0 }" />


			<c:forEach items="${report}" var="report" varStatus="count">
				<tr>

					<c:set var="totalTaxableAmt"
						value="${totalTaxableAmt + report.taxableAmt}" />
					<c:set var="totalTax" value="${totalTax + report.totalTax}" />
					<c:set var="totalGrandTotal"
						value="${totalGrandTotal + report.grandTotal}" />


					<c:set var="totalGrnGrandTotal"
						value="${totalGrnGrandTotal + report.grnGrandTotal}" />
					<c:set var="totalGrnTaxableAmt"
						value="${totalGrnTaxableAmt + report.grnTaxableAmt}" />
					<c:set var="totalGrnTax"
						value="${totalGrnTax + report.grnTotalTax}" />



					<c:set var="totalGvnGrandTotal"
						value="${totalGvnGrandTotal + report.gvnGrandTotal}" />
					<c:set var="totalGvnTaxableAmt"
						value="${totalGvnTaxableAmt + report.gvnTaxableAmt}" />
					<c:set var="totalGvnTax"
						value="${totalGvnTax + report.gvnTotalTax}" />


					<c:set var="totalNetGrandTotal"
						value="${totalNetGrandTotal + report.netGrandTotal}" />
					<c:set var="totalNetTax"
						value="${totalNetTax + report.netTotalTax}" />
					<c:set var="totalNetTaxableAmt"
						value="${totalNetTaxableAmt + report.netTaxableAmt}" />


					<c:set var="contri"
						value="${(report.netGrandTotal*100)/totalNetValForContri}" />

					<c:set var="totalContri" value="${totalContri+ contri}" />


					<td width="10"><c:out value="${count.index+1}" /></td>
					<td width="100"><c:out value="${report.month}" /></td>

					<%-- <td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.taxableAmt}" /></td>

					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.totalTax}" /></td> --%>
					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.grandTotal}" /></td>

					<%-- 					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.grnTaxableAmt}" /></td>

					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.grnTotalTax}" /></td> --%>


					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.grnGrandTotal}" /></td>


					<%-- 					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.gvnTaxableAmt}" /></td>

					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.gvnTotalTax}" /></td> --%>


					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.gvnGrandTotal}" /></td>





					<%-- 					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.netTaxableAmt}" /></td>

					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.netTotalTax}" /></td> --%>


					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2"
							value="${report.netGrandTotal}" /></td>

					<td width="100" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2" value="${contri}" /></td>



				</tr>

			</c:forEach>
			<tr>



				<td colspan='2'><b>Total</b></td>


				<%-- 				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalTaxableAmt}" /></b></td>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalTax}" /></b></td> --%>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGrandTotal}" /></b></td>



				<%-- 				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGrnTaxableAmt}" /></b></td>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGrnTax}" /></b></td> --%>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGrnGrandTotal}" /></b></td>



				<%-- 				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGvnTaxableAmt}" /></b></td>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGvnTax}" /></b></td> --%>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalGvnGrandTotal}" /></b></td>



				<%-- 				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalNetTaxableAmt}" /></b></td>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalNetTax}" /></b></td> --%>
				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalNetGrandTotal}" /></b></td>

				<td width="100" align="right"><b><fmt:formatNumber
							type="number" maxFractionDigits="2" minFractionDigits="2"
							value="${totalContri}" /></b></td>



			</tr>
		</tbody>
	</table>


	<!-- END Main Content -->

</body>
</html>