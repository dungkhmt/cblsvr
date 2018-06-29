<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- DataTables CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- Selectize CSS -->
<link href="<c:url value="/assets/libs/selectize/css/selectize.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>

<!-- Selectize JavaScript -->
<script src="<c:url value="/assets/libs/selectize/js/selectize.min.js"/>"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Danh sách hóa đơn đã nhận</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>	
	<!-- /.row -->
	<div class="row">
		<div class="col-lg-12">
			<div class="panel panel-default">
				<div class="panel-heading">Danh các hóa đơn chưa được giao: <b id=batchCodeInfo></b></div>
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="dataTabels-order">
							<thead>
								<tr>
									<th>Mã Hóa Đơn</th>
									<th>Khách hàng</th>
									<th>Ngày đặt hàng</th>
									<th>Ngày giao</th>
									<th>Địa điểm giao</th>
									<th>Lô</th>
									<th>Trạng thái</th>
									<th></th>
								</tr>
							</thead>
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
<style>
	td{
        font-size: 15px;
	}
	
	.hide{
		display: none;
	}
	
</style>


<script>
var table;
var data;
function save(index) {
	$.post(window.location.origin + "/slp/outgoingarticles/order/save-orderstatus",
	        {
				"O_Code": data[index].o_Code,
				"O_Status_Code": $("#selectize_orderstatus"+index).val()
	        },
	        function(){
	        	data[index].o_Status_Code = $("#selectize_orderstatus"+index).val();
	        	$(".selectize_orderstatus" + index).addClass("hide");
	        });
}

function reset(index) {
	$("#selectize_orderstatus" + index)[0].selectize.setValue(data[index].o_Status_Code);
	$(".selectize_orderstatus" + index).addClass("hide");
}

$(document).ready(function(){
	$.get(window.location.origin + "/slp/outgoingarticles/order/getorderstatus", function(dataOrder) {
		data = dataOrder;
		var $wrapper = $('#page-wrapper');
		$.get(window.location.origin + "/slp/outgoingarticles/order/getstatus", function(listStatus) {
			for(var i = 0; i < data.length; ++i) {
				data[i].O_Time = data[i].o_TimeEarly + "-" + data[i].o_TimeLate;
				data[i].status = '<div class="selectize_orderstatus'+i+' hide">'
					+'<i class="fa fa-check" aria-hidden="true" style="color:green; cursor:pointer; font-size: 20px" onClick="save('+i+')"></i>'
					+ '<i class="fa fa-times" style="color:gray; cursor:pointer;font-size: 20px" onClick="reset('+i+')" aria-hidden="true"></i>'
					+'</div>';
				data[i].selectize_status = '<input style="width:200px" id="selectize_orderstatus'+i+'">';
			}
			
			table = $('#dataTabels-order').DataTable({
			 	data: data,
				columns: [
				    { "data": "o_Code" },
				    { "data": "c_Name" },
				    { "data": "o_OrderDate" },
				    { "data": "o_DueDate" },
				    { "data": "o_DeliveryAddress" },
				    { "data": 'reqbat_Description' },
				    { "data": "selectize_status" },
				    { "data": "status" }
				],
				"columnDefs": [
								{ "width": "12%", "targets": 0 },
								{ "width": "15%", "targets": 1 },
				               { "width": "12%", "targets": 3 },
				               { "width": "12%", "targets": 2 }
				             ],
		       "bSort": false,
		       "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
		    	   var indexData = oSettings.aiDisplay;
			    	   for(var i = iStart - 1; i < iEnd; ++i) {
			   			var $selectize = $("#selectize_orderstatus"+indexData[i]).selectize({
			   				options: listStatus,
			   				maxItems: 1,
			   				valueField: 'ost_Code',
			   				labelField: 'ost_Name',
			   				searchField: ['ost_Name']
			   			});
			   			$selectize[0].selectize.setValue(data[indexData[i]].o_Status_Code);
			   		}
			    	   $('select.selectized,input.selectized', $wrapper).each(function(index) {
			    			var local = indexData[index + iStart - 1];
			    		   var update = function(e) {
			    				if(data[local].o_Status_Code != $("#selectize_orderstatus" + local)[0].selectize.items[0]) {
			    					$(".selectize_orderstatus" + local).removeClass("hide");
			    				} else {
			    					
			    					$(".selectize_orderstatus" + local).addClass("hide");
			    				}
			    			};

			    			$(this).on('change', update);
			    		});
		    	  }
		   });
		})
	});
});
</script>