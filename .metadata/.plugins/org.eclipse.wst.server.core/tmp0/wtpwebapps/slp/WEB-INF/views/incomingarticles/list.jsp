<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- DataTables CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Danh sách mặt hàng đã nhập</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
	<div class="row">
		<div class="col-sm-2">
			<div class="form-group">
				<select class="form-control" id="inArtDate">
					<c:forEach items="${inArtDateList}" var="inArtDate">
						<option value="${inArtDate}">${inArtDate}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<!-- /.form-group .col-sm-3  -->
		<div class="col-sm-offset-9 col-sm-1">
			<button type="button" class="btn btn-primary addInArticles">Thêm</button>
		</div>
	</div>
	<!-- /.row -->
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-heading">Danh sách mặt hàng đã nhập ngày: <b id="dateInfo"></b></div>
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="dataTabels-inarticles">
							<thead>
								<tr>
									<th>Mặt hàng</th>
									<th>Số lượng</th>
									<th>Giá</th>
									<th>Nhà cung cấp</th>
									<th>Ngày nhập</th>
								</tr>
							</thead>
							<tfoot>
								<tr>
									<th colspan="5" style="text-align:right;">Tổng: </th>	
								</tr>
							</tfoot>
							<tbody>
								<c:forEach items="${inArtList}" var="inArt">
									<tr>
										<td><c:out value="${inArt.IA_ArticleCode }"/></td>
										<td><c:out value="${inArt.IA_Amount}"/></td>
										<td><c:out value="${inArt.IA_Price}"/></td>
										<td><c:out value="${inArt.IA_Supplier_Code }"/></td>
										<td><c:out value="${inArt.IA_Date }"/></td>	
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
					<!--/.dataTable_wrapper -->
				</div>
				<!-- /.panel-body -->
			</div>
			<!-- /.panel -->
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->
</div>
<!-- /#page-wrapper -->

<script>


$(document).ready(function(){
	
	$.fn.dataTable.ext.search.push(function(settings,data,dataIndex){
		var artDateSelected = $('#inArtDate').val();
		var indexCut = data[4].indexOf(" ");
		var date = data[4].substring(0,indexCut);
		if(date === artDateSelected){
			return true;
		}
		return false;
	});
	
	var table = $('#dataTabels-inarticles').DataTable( {
		"footerCallback": function ( row, data, start, end, display ) {
	    	var api = this.api(), data;
	 
	        // Remove the formatting to get integer data for summation
	        var intVal = function ( i ) {
	        	return typeof i === 'string' ? i.replace(/[\$,]/g, '')*1 : typeof i === 'number' ? i : 0;
	        };

	        var pageTotalPrice = api
    			.column(2, { page: 'current'} )
    			.data();
   			var pageTotalAmount = api
    			.column(1, { page: 'current'} )
    			.data();
    		var pageTotal = 0;
			for(var i=0; i<pageTotalPrice.length; i++){
				 pageTotal += pageTotalPrice[i]*pageTotalAmount[i];
			}	     
	        
	        var SpageTotal = "";
	        var tmp = ""+pageTotal;
	        var check=0;
	        for(var i=tmp.length-1;i>=0; i--){
	        	SpageTotal = tmp[i] + SpageTotal;
	        	check++;
	        	if(check==3){
	        		SpageTotal = "." + SpageTotal;
	        		check = 0;
	        	}
	        }
	        if(tmp.length % 3 == 0){
	        	SpageTotal = SpageTotal.substring(1);
	        }
	       	// Update footer
	       	$( api.column(2).footer() ).html("Tổng: "+SpageTotal+" VND");
	    }, 
	    "bInfo":false
	});
	
	$('.addInArticles').click(function(){
		window.location = '${baseUrl}' + "/incomingArticles/addArticles.html";
	});
	
	$('#dateInfo').text($('#inArtDate').val());
	
	$("#inArtDate").click(function(){
		$('#dateInfo').text($(this).val());
		table.draw();
	})
});
</script>