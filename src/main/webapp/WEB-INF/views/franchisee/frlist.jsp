<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<jsp:include page="/WEB-INF/views/include/header.jsp"></jsp:include>
<style>

/* h1{
  font-size: 30px;
  color: #fff;
  text-transform: uppercase;
  font-weight: 300;
  text-align: center;
  margin-bottom: 15px;
} */
th:hover{
     cursor:pointer;
    background:#AAA;
    
}
table{
  width:100%;
  table-layout: fixed;
  border:1px solid #ddd;
}

/* th{
  padding: 20px 15px;
  text-align: left;
  font-weight: 500;
  font-size: 14px;
  text-shadow: 0 1px 0 #ffffff;
  color: #ddd;
  text-transform: uppercase;
} */
/* td{
  padding: 15px;
  text-align: left;
  vertical-align:middle;
  font-weight: 300;
  font-size: 12px;
  color: #ddd;
  border-bottom: solid 1px rgba(255,255,255,0.1);
 
}
 */

/* demo styles */
/* 
@import url(https://fonts.googleapis.com/css?family=Roboto:400,500,300,700);
body{
  
  font-family: 'Roboto', sans-serif;
} */
/* for custom scrollbar for webkit browser

::-webkit-scrollbar {
    width: 6px;
} 
::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
} 
::-webkit-scrollbar-thumb {
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3); 
}*/

</style>
<body>

	<jsp:include page="/WEB-INF/views/include/logout.jsp"></jsp:include>


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
						<i class="fa fa-file-o"></i>Franchisee Configuration
					</h1>
				</div>
			</div> -->
			<!-- END Page Title -->


			<c:set var="isEdit" value="0">
			</c:set>

			<c:forEach items="${sessionScope.newModuleList}" var="modules">
				<c:forEach items="${modules.subModuleJsonList}" var="subModule">

					<c:choose>
						<c:when
							test="${subModule.subModuleMapping eq 'configureFranchiseesList'}">

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



						</c:when>
					</c:choose>
				</c:forEach>
			</c:forEach>



			<!-- BEGIN Main Content -->
			<div class="row">
				<div class="col-md-12">
					<div class="row">
						<div class="col-md-12">
							<div class="box">


								<div class="row">
									<div class="col-md-12">
									
									<form id="frConfForm" method="post">

										<div class="box">
											<div class="box-title">
												<h3>
													<i class="fa fa-table"></i> Configured Franchisee List
												</h3>
												<div class="box-tool">
													<a data-action="collapse" href="#"><i
														class="fa fa-chevron-up"></i></a>
													<!--<a data-action="close" href="#"><i class="fa fa-times"></i></a>-->
												</div>
											</div>
                                          <div class="box-content">
												<jsp:include page="/WEB-INF/views/include/tableSearch.jsp"></jsp:include>


												<div class="clearfix"></div>
<input type="button" onClick="callDeleteAction()" value="Delete" class="btn btn-danger" />
<div class="clearfix"></div>
												<div id="table-scroll" class="table-scroll">

												<!-- 	<div id="faux-table" class="faux-table" aria="hidden">
														<table id="table2" class="table table-advance" border="1">
															<thead>
																<tr class="bgpink">
																	<th width="27" style="width: 28px" id="sr">#</th>
																	<th width="124" align="left" id="fr">Franchisee Name</th>
																	<th width="202" align="left" id="menu">Menu Title</th>
																	<th width="106" align="left" id="cat">Category Name</th>
																	<th width="126" align="left" id="time">Time</th>
																	<th width="66" align="left" id="type">Type</th>
																	<th width="66" align="left">Action</th>
																</tr>
															</thead>
														</table>

													</div> -->
													<div class="table-wrap">

														<table id="table1" class="table table-advance" border="1">
															<thead>
																<tr class="bgpink">
																	<th width="35" style="width: 28px"id="sr">#<input type="checkbox"
															onClick="selectFrConf(this)" /></th>
																	<th width="124" align="left"id="fr">Franchisee Name</th>
																	<th width="202" align="left"id="menu">Menu Title</th>
																	<th width="106" align="left"id="cat">Category Name</th>
																	<th width="126" align="left" id="time">Time</th>
																	<th width="66" align="left"id="type">Type</th>
																	<th width="66" align="left">Action</th>
																</tr>
															</thead>
															<tbody>
																<c:forEach items="${configureFrList}"
																	var="configureFrList" varStatus="count">


																	<tr>
																		<td><c:out value="${count.index+1}"></c:out>
																		<input type=checkbox name="settingIdList"   value="${configureFrList.settingId}">
																		</td>
																		<td align="left"><c:out
																				value="${configureFrList.frName}"></c:out> <!-- <img src="http://monginisaurangabad.com/admin/uploads/cakes/0L6KEg55AhP18.jpg" alt="" width="150" height="100" /> -->
																		</td>
																		<td align="left"><c:out
																				value="${configureFrList.menuTitle}  "></c:out></td>
																		<td align="left"><c:out
																				value="${configureFrList.catName}  "></c:out></td>
																		<%-- 	<td align="left"><c:out
																			value="${configureFrList.itemShow}"></c:out></td>
 --%>

																		<td align="left"><c:out
																				value="${configureFrList.fromTime} To ${configureFrList.toTime}"></c:out></td>

																		<c:choose>
																			<c:when test="${configureFrList.settingType==1}">
																				<td align="left"><c:out value="Daily"></c:out></td>
																			</c:when>
																			<c:when test="${configureFrList.settingType==2}">
																				<td align="left"><c:out value="Date"></c:out></td>
																			</c:when>
																			<c:when test="${configureFrList.settingType==3}">
																				<td align="left"><c:out value="Day"></c:out></td>
																			</c:when>
																		</c:choose>

																		<c:choose>
																			<c:when test="${isEdit==1}">

																				<td align="left"><a
																					href="updateFranchiseeConf/${configureFrList.settingId}"><span
																						class="glyphicon glyphicon-edit"></span></a>&nbsp;&nbsp;&nbsp;&nbsp;


																				
																			</c:when>
																			<c:otherwise>
																				<td align="left"><a
																					href="updateFranchiseeConf/${configureFrList.settingId}" class="disableClick"><span
																						class="glyphicon glyphicon-edit"></span></a>&nbsp;&nbsp;&nbsp;&nbsp;

																			</c:otherwise>
																		</c:choose>
																		<%-- <td align="left"><a
																			href="updateFranchiseeConf/${configureFrList.settingId}"><span
																				class="glyphicon glyphicon-edit"></span></a>&nbsp;&nbsp;&nbsp;&nbsp;
 --%>
																		<%-- <a
																		href="deleteSpecialCake/${configureFrList.settingId}"
																		onClick="return confirm('Are you sure want to delete this record');"><span
																			class="glyphicon glyphicon-remove"></span></a></td> --%>
																	</tr>

																</c:forEach>


															</tbody>

														</table>
													</div>
												</div>
<input type="button" onClick="callDeleteAction()" value="Delete" class="btn btn-danger" />

											</div>
											<%-- <div class="box-content">
						
												<div class="clearfix"></div>
												<div class="table-responsive" style="border: 0">
													<table width="100%" class="table table-advance" id="table1">
														<thead>
															<tr>
																<th width="17" style="width: 18px">#</th>
																<th width="364" align="left">Franchisee Name</th>
																<th width="282" align="left">Menu Title</th>
																<th width="218" align="left">Category Name</th>
																<!-- <th width="106" align="left">Items</th> -->
																<th width="66" align="left">Type</th>
																<th width="66" align="left">Action</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${configureFrList}"
																var="configureFrList" varStatus="count">


																<tr>
																	<td><c:out value="${count.index+1}"></c:out></td>
																	<td align="left"><c:out
																			value="${configureFrList.frName}"></c:out> <!-- <img src="http://monginisaurangabad.com/admin/uploads/cakes/0L6KEg55AhP18.jpg" alt="" width="150" height="100" /> -->
																	</td>
																	<td align="left"><c:out
																			value="${configureFrList.menuTitle}  "></c:out></td>
																	<td align="left"><c:out
																			value="${configureFrList.catName}  "></c:out></td>
																	<td align="left"><c:out
																			value="${configureFrList.itemShow}"></c:out></td>


																
																	


																	<c:choose>
																		<c:when test="${configureFrList.settingType==1}">
																			<td align="left"><c:out value="Daily"></c:out></td>
																		</c:when>
																		<c:when test="${configureFrList.settingType==2}">
																			<td align="left"><c:out value="Date"></c:out></td>
																		</c:when>
																		<c:when test="${configureFrList.settingType==3}">
																			<td align="left"><c:out value="Day"></c:out></td>
																		</c:when>
																	</c:choose>


																	<td align="left"><a
																		href="updateFranchiseeConf/${configureFrList.settingId}"><span
																			class="glyphicon glyphicon-edit"></span></a>&nbsp;&nbsp;&nbsp;&nbsp;

																		<a
																		href="deleteSpecialCake/${configureFrList.settingId}"
																		onClick="return confirm('Are you sure want to delete this record');"><span
																			class="glyphicon glyphicon-remove"></span></a></td>
																</tr>
																
													</c:forEach>
														</tbody>
													</table>
												</div>
												<!-- <div class="form-group">
													<div
														class="col-sm-9 col-sm-offset-3 col-lg-10 col-lg-offset-2">
														
														
													</div>
												</div> -->
												</form>
											</div> --%>
										</div>
										</form>
									</div>
									
								</div>

							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- END Main Content -->
			<footer>
				<p>2017 © MONGINIS.</p>
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
//'.tbl-content' consumed little space for vertical scrollbar, scrollbar width depend on browser/os/platfrom. Here calculate the scollbar width .
$(window).on("load resize ", function() {
  var scrollWidth = $('.tbl-content').width() - $('.tbl-content table').width();
  $('.tbl-header').css({'padding-right':scrollWidth});
}).resize();
</script>
<script>

function sortTable(f,n){
	var rows = $('#table1 tbody  tr').get();

	rows.sort(function(a, b) {

		var A = getVal(a);
		var B = getVal(b);

		if(A < B) {
			return -1*f;
		}
		if(A > B) {
			return 1*f;
		}
		return 0;
	});

	function getVal(elm){
		var v = $(elm).children('td').eq(n).text().toUpperCase();
		if($.isNumeric(v)){
			v = parseInt(v,10);
		}
		return v;
	}

	$.each(rows, function(index, row) {
		$('#table1').children('tbody').append(row);
	});
}
var f_sr = 1;
var f_fr = 1;
var l_menu = 1;
var l_cat= 1;
var l_time = 1;
$("#sr").click(function(){
    f_sr *= -1;
    var n = $(this).prevAll().length;
    sortTable(f_sr,n);
});
$("#fr").click(function(){
    f_fr *= -1;
    var n = $(this).prevAll().length;
    sortTable(f_fr,n);
});
$("#menu").click(function(){
   l_menu *= -1;
    var n = $(this).prevAll().length;
    sortTable(l_menu,n);
});
$("#cat").click(function(){
	   l_cat *= -1;
	    var n = $(this).prevAll().length;
	    sortTable(l_cat,n);
	});
$("#time").click(function(){
	   l_time *= -1;
	    var n = $(this).prevAll().length;
	    sortTable(l_time,n);
	});
</script>
<script>
function myFunction() {
  var input, filter, table, tr, td, i,td1,td2;
  input = document.getElementById("myInput");
  filter = input.value.toUpperCase();
  table = document.getElementById("table1");
  tr = table.getElementsByTagName("tr");
  for (i = 0; i < tr.length; i++) {
    td = tr[i].getElementsByTagName("td")[1];
    td1 = tr[i].getElementsByTagName("td")[2];
    td2 = tr[i].getElementsByTagName("td")[3];
    if (td||td1||td2) {
      if (td.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      } else  if (td1.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      }else   if (td2.innerHTML.toUpperCase().indexOf(filter) > -1) {
        tr[i].style.display = "";
      }else {
        tr[i].style.display = "none";
      }
    }    
   
    
  }
}

function selectFrConf(source) {
	checkboxes = document.getElementsByName('settingIdList');
	
	for (var i = 0, n = checkboxes.length; i < n; i++) {
		checkboxes[i].checked = source.checked;
	}
	
}

function callDeleteAction() {
	
	
	checkboxes = document.getElementsByName('settingIdList');
	var okay=false;
	for (var i = 0, n = checkboxes.length; i < n; i++) {
		if(checkboxes[i].checked ==true){
			okay=true;
		break;
	}
		
	}
	
	if(okay==true){
		
		var x=confirm("Are you sure to delete ")
		if(x==true){
	var form=document.getElementById("frConfForm");
	form.action=("deleteMultiFrConfig");
	form.submit();
	}
	}
	else{
		
		alert("Please select some configurations to delete");
	}
	
}
</script>
</body>

</html>