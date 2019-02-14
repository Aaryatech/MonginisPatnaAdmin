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
	width:100%;
page-break-inside: auto !important 

} 
p  {
    color: black;
    font-family: arial;
    font-size: 60%;
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
<body onload="myFunction()">
  
<div align="center"> Order List &nbsp;&nbsp;&nbsp;&nbsp; </div>
	
	<table  align="center" border="1" cellspacing="0" cellpadding="1" 
		id="table_grid" class="table table-bordered">
		<thead>
			<tr class="bgpink">
				<th height="25">Sr.No.</th>
				<th>Franchisee Name</th>
				<th>Item Name</th>
				<th>Category</th>
				<th>Quantity</th>
				<th>DEL.Date</th>
				
			</tr>
		</thead>
		<tbody>
			
			<c:forEach items="${orderList}" var="report" varStatus="count">
				<tr>
					<td><c:out value="${count.index+1}" /></td>
					<td width="100"><c:out value="${report.frName}" /></td>
					<td width="150"><c:out value="${report.itemName}" /></td>
					<td width="200"><c:out value="${report.catName}" /></td>
					<td width="100"><c:out value="${report.orderQty}" /></td>
					<td width="100"><c:out value="${report.deliveryDate}" /></td>
					
				</tr>

			</c:forEach>
			
		</tbody>
	</table>


	<!-- END Main Content -->

</body>
</html>