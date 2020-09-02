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
<title>Dispatch Report Pdf</title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

<style type="text/css">

#container {
    column-count:3;
   
}

#tbl {
    
}
</style>

<style type="text/css">
 table {
	border-collapse: collapse;
	font-size: 10;
	width:100%;
page-break-inside: auto !important 

} 
p  {
    color: black;
    font-family: arial;
    font-size: 50%;
	margin-top: 0;
	padding: 0;

}
h6  {
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
<body onload="directPrint()">
<!-- <h5 align="center">MONGINIS &nbsp;&nbsp; -->
<!-- <p align="center">Patna,Bihar</p>
 -->
<c:forEach items="${frList}" var="fr" varStatus="count">
			<c:if test="${fr.frId==frId}">
			<b>${fr.frName}</b>
			</c:if>
			</c:forEach></h5>
	<div align="center"> <h5>		
Production Start Date &nbsp; ${billDate} &nbsp;&nbsp; Dispatch Sheet &nbsp;&nbsp; Route ${routeName} &nbsp;&nbsp;Dispatch Date: ${convertedDate}</h5></div>

<div id="container"  >
<table  align="center" border="1" cellspacing="0" cellpadding="1" 
		id="table_grid" class="table table-bordered">
		<thead>
			<tr class="bgpink">
				<th height="25">Sr</th>
				<th>Item Name</th>
			<c:forEach items="${frList}" var="fr" varStatus="count">
			<c:if test="${fr.frId==frId}">
			<th style="font-size: 12px">Disp Qty</th>
			<th>Sent</th>
			</c:if>
			</c:forEach>
			<!-- <th style="font-size: 12px">Rate</th>
				<th>Total</th> -->
			</tr>
		</thead>
		<tbody>
				<c:set var="allTotal" value="0" />

			<c:forEach items="${subCatList}" var="subCat" varStatus="count">
			<c:set var="flag" value="0" />
			<%-- 	<tr>
                    <td bgcolor="lightgray"><c:out value="" /></td>
					<td bgcolor="lightgray"><b>${subCat.subCatName}</b></td>
					<c:forEach items="${frList}" var="fr" varStatus="count">
						<c:if test="${fr.frId==frId}">
			        <td bgcolor="lightgray"><c:out value="" /></td>
			         <td bgcolor="lightgray"><c:out value="" /></td>
			        </c:if>
			         </c:forEach>
					 <td bgcolor="lightgray"><c:out value="" /></td>
				</tr> --%>
				<c:set var="srNo" value="1"></c:set>
				<c:forEach items="${itemList}" var="item" varStatus="count">
				<c:set var="total" value="0" />
                <c:set var="frTotal" value="0" />
					<c:choose>
						<c:when test="${item.itemGrp2==subCat.subCatId}">

							<%-- <tr>
								<td width="100" ><c:out value="${srNo}" /></td>
								<c:set var="srNo" value="${srNo+1}"></c:set>
								<td width="200" ><c:out value="${item.itemName}" /></td> --%>
								<c:set var="editQty" value="0"></c:set>
								<c:forEach items="${frList}" var="fr" varStatus="count">
									<c:if test="${fr.frId==frId}">
							<%-- 	<c:set var="editQty" value="0"></c:set> --%>
								<c:forEach items="${dispatchReportList}" var="report" varStatus="count">
								<c:choose>
								<c:when test="${report.itemId==item.id}">
								<c:choose>
								<c:when test="${report.frId==fr.frId}">
							
								<c:set var="editQty" value="${report.editQty}"></c:set>
								 <c:if test="${fr.frRateCat==1}">
								 <c:set var="total" value="${report.editQty*item.itemRate1}"></c:set>
							     </c:if>
							   <c:if test="${fr.frRateCat==2}">
							    <c:set var="total" value="${report.editQty*item.itemRate2}"></c:set>
							    </c:if>
							     <c:if test="${fr.frRateCat==3}">
							    <c:set var="total" value="${report.editQty*item.itemRate3}"></c:set>
							    </c:if> 
								</c:when>
								</c:choose>
								</c:when>
								
								</c:choose>
								<c:set var="frTotal" value="${report.editQty+frTotal}" />
								</c:forEach>
									<%-- <td width="60px" align="right">${editQty}</td> --%>
									<%-- 	<td width="60px" align="right">${item.itemRate1}</td> --%>
									</c:if>
								</c:forEach>
								<c:if test="${editQty>0}">
								<c:if test="${flag==0}">
								<tr>
                    <td bgcolor="lightgray"><c:out value="" /></td>
					<td bgcolor="lightgray"><b>${subCat.subCatName}</b></td>
					<td bgcolor="lightgray"><c:out value="" />
					<c:forEach items="${frList}" var="fr" varStatus="count">
						<c:if test="${fr.frId==frId}">
			       <%--  <td bgcolor="lightgray"><c:out value="" /></td>
			         <td bgcolor="lightgray"><c:out value="" /></td>
			         --%></c:if>
			         </c:forEach>
					 <td bgcolor="lightgray"><c:out value="" /></td>
				</tr> <c:set var="flag" value="1" /></c:if>
								 <tr>
								<td width="30px" style="text-align:center;"><c:out value="${srNo}" /></td>
								<c:set var="srNo" value="${srNo+1}"></c:set>
								<td width="200px" ><c:out value="${item.itemName}" /></td>
								<td width="50px" style="text-align:center;">${editQty}</td> 
								<td width="30px" align="right"></td> 
								<%-- <td width="60px" align="right">${item.itemRate1}</td>
							 	<td width="60px" align="right"><fmt:formatNumber type="number"
							maxFractionDigits="2" minFractionDigits="2" value="${total}" /></td>		 --%>
							 	<c:set var="allTotal" value="${allTotal+total}"></c:set>
							</tr>
							</c:if>
						</c:when>
					</c:choose>


				</c:forEach>
	       

			</c:forEach>
		<%-- 
			<c:forEach items="${subCatList}" var="subCat" varStatus="count">
				<tr>
                    <td bgcolor="#fc67ca"><c:out value="" /></td>
					<td bgcolor="#fc67ca"><b>${subCat.subCatName}</b></td>
					  <c:set var="totalItems" value="0" />
				<c:forEach items="${frList}" var="fr" varStatus="count">
				<c:if test="${fr.frId==frId}">
				  <c:set var="itemTotal" value="0" />
				<c:forEach items="${dispatchReportList}" var="report" varStatus="count">
					<c:choose>
								<c:when test="${report.subCatId==subCat.subCatId}">
				<c:choose>
								<c:when test="${report.frId==fr.frId}">
										<c:set var="itemTotal" value="${report.editQty+itemTotal}" />
								</c:when>
				</c:choose>
			     </c:when>
			     </c:choose>
			    </c:forEach>
			    	  <c:set var="totalItems" value="${itemTotal+totalItems}" />
			    	  
			            <td align="right" bgcolor="#fc67ca"><fmt:formatNumber type="number"
							maxFractionDigits="2" value="${itemTotal}" /></td>
					</c:if>
			     </c:forEach>
			     <c:if test="${itemTotal>0}">
			     <tr>
                    <td bgcolor="#fc67ca"><c:out value="" /></td>
					<td bgcolor="#fc67ca"><b>${subCat.subCatName}</b></td>
					<td align="right" bgcolor="#fc67ca"><fmt:formatNumber type="number"
							maxFractionDigits="2" value="${itemTotal}" /></td> 
			     <td  align="right" bgcolor="#fc67ca"></td>
				<td  align="right" bgcolor="#fc67ca"><b><fmt:formatNumber type="number"
							maxFractionDigits="2" value="" /></b></td>
			</tr>
			</c:if>
			</c:forEach>
			
			<tr>

				<td colspan='2'><b>Total</b></td>
			
				
				<c:forEach items="${frList}" var="fr" varStatus="count">
				<c:if test="${fr.frId==frId}">
				  <c:set var="itemTotal" value="0" />
				<c:forEach items="${dispatchReportList}" var="report" varStatus="count">
				
				<c:choose>
								<c:when test="${report.frId==fr.frId}">
										<c:set var="itemTotal" value="${report.editQty+itemTotal}" />
								</c:when>
				</c:choose>
			     
			         </c:forEach>
			            <td align="right"><b><fmt:formatNumber type="number"
							maxFractionDigits="2" value="${itemTotal}" /></b></td>
				</c:if>
			         </c:forEach>
			         <td  align="right"></td>
				<td  align="right"><b><fmt:formatNumber type="number"
							maxFractionDigits="2" value="${allTotal}" /></b></td>
			</tr>  --%>
		</tbody>
	</table>
</div>

	<!-- END Main Content -->

</body>

	<script>
	 function directPrint()
	{
		 
		window.print();
	//	window.close();
	} 
	
	</script>
</body>
</html>