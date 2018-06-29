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
			<h1 class="page-header">Danh sách hóa đơn đã nhận</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<button type="button" class="btn btn-primary addOutOrder pull-right">Thêm</button>
		
	<!-- /.row -->
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-heading">Danh các hóa đơn chưa được giao: <b id=batchCodeInfo></b></div>
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="dataTabels-outarticles">
							<thead>
								<tr>
									<th>Mã Hóa Đơn</th>
									<th>Mã khách hàng</th>
									<th>Ngày giao</th>
									<th>Thời gian giao</th>
									<th>Địa điểm giao</th>
									<th>Giá</th>
									<th>Lô</th>
								</tr>
							</thead>
							<tfoot>
								<tr>
									<th colspan="7" style="text-align:right">Tổng: </th>
								</tr>
							</tfoot>
							<tbody>
								<c:forEach items="${outArtList}" var="outArt">
									<tr>
										<td><c:out value="${outArt.o_Code}"/></td>
										<td><c:out value="${outArt.o_ClientCode}"/></td>
										<td><c:out value="${outArt.o_DueDate}"/></td>
										<td><c:out value="${outArt.o_TimeEarly}-${outArt.o_TimeLate}"/></td>
										<td><c:out value="${outArt.o_DeliveryAddress }"/></td>	
										<td><c:out value=""/>${outArt.o_Price }</td>
										<td><c:out value=""/>${outArt.o_BatchCode }</td>
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
	var table = $('#dataTabels-outarticles').DataTable({
		"footerCallback": function ( row, data, start, end, display ) {
	    	var api = this.api(), data;
	 
	        // Remove the formatting to get integer data for summation
	        var intVal = function ( i ) {
	        	return typeof i === 'string' ? i.replace(/[\$,]/g, '')*1 : typeof i === 'number' ? i : 0;
	        };

	           
			var pageTotal = api
    		.column(5, { page: 'current'} )
    		.data()
    		.reduce(function(a,b){
    			return intVal(a)+intVal(b);
    		});	      
			
			console.log("page total price "+pageTotal);
	        var SpageTotal = "";
	        var tmp = ""+pageTotal;
	        var indexDot = tmp.indexOf(".");
	        var subFixTmp="";
	        if(indexDot != -1){
	        	subFixTmp = tmp.substring(indexDot+1);
	        	tmp = tmp.substring(0,indexDot);
	        }
	        
	        console.log("tmp: "+tmp);
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
	        if(parseInt(subFixTmp)!=0){
	        	SpageTotal += subFixTmp;
	        }
	       	// Update footer
	       	$( api.column(5).footer() ).html("Tổng: "+SpageTotal+" VND");
	    },
	    "order": [[2, 'desc']],
	    "bSort":true,
	    "columnDefs":[{
	    	"targets":[6],
	    	"visible":false
	    }]
	});
	
	$('.addOutOrder').click(function(){
		window.location = '${baseUrl}' + "/outgoingarticles/add-an-order.html";
	});
	
	$('#batchCodeInfo').text($('#sel-batchCode').val());
	
});
</script>