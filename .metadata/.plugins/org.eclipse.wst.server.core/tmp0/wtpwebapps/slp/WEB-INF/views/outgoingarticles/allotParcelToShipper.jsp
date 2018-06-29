<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- DataTables CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">
<link href="<c:url value="/assets/css/source/outgoingarticles/allotParcelToShipper.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.min.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Danh sách các lô hàng</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<div class="row">
	  <!-- Modal -->
	  <div class="modal fade" id="changeBatch" role="dialog">
	    <div class="modal-dialog">
	    
	      <!-- Modal content-->
	      <div class="modal-content">
	        <div class="modal-header">
	          <button type="button" class="close" data-dismiss="modal">&times;</button>
	          <h4 class="modal-title">Phân lô hàng cho shipper</h4>
	        </div>
	        <div class="modal-body">
				<table class="responstable">
					<thead>
						<tr>
							<th class="col-xs-1"></th>
							<th class="col-xs-3">Username</th>
							<th class="col-xs-3">Tên</th>
							<th class="col-xs-5">Địa chỉ</th>
						</tr>
					</thead>
					<tbody class="form-group" id="shipper-list">
					</tbody>
				</table>
	        </div>
	        <div class="modal-footer">
	        	<button type="button" class="btn btn-success" onclick="update()">Update</button>
	         	<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        </div>
	      </div>
	      
	    </div>
	  </div>
	  
	  
		<div class="col-lg-12">
		
			<div class="panel panel-default">
				<div class="panel-body">
					<div class="dataTable_wrapper">
						<table class="table table-striped table-bordered table-hover" id="dataTabels-outarticles">
							<thead>
								<tr>
									<th>ID</th>
									<th>Code</th>
									<th>Description</th>
									<th>CustomerCode</th>
									<th>Shippers</th>
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

<script>
var dataBatchs;
var shipperList;
var sessionIndex;
var table;

function allotParcel(index) {
	shipperList.forEach(function(value, index) {
		document.getElementById(index).checked = false;
	})
	sessionIndex = index;
	$.get(window.location.origin + "/slp/ship/getAllotParcel?batchCode="+dataBatchs[index].reqbat_Code, function(data) {
		data.forEach(function(shipper, index1) {
			shipperList.forEach(function(value, index2) {
				if(shipper === value.shp_Code) {
					document.getElementById(index2).checked = true;
					return;
				}
			})
		})
		
	});
	
}

function update() {
	var shippersHTML = Array.from($(".regular-checkbox"));

	var shippers = []
	shippersHTML.forEach(function(value, index) {
		if(value.checked) {
			shippers.push(shipperList[index].shp_Code);
		}
	});
	
	var data = {
		batchCode: dataBatchs[sessionIndex].reqbat_Code,
		shippers: shippers
	};
	
	$.ajax({ 
	    type:"POST", 
	    url:window.location.origin + "/slp/ship/update-allot-parcel",
	    data: JSON.stringify(data),
	    contentType: "application/json; charset=utf-8",
	    dataType: "json",
	    //Stringified Json Object
	    success: function(){
	    	$("#changeBatch").modal("hide");
	    	var temp = table.row(sessionIndex).data();
			temp.shippers = data.shippers.join(", ");
			table.row(sessionIndex).data(temp);
	    }
    })
	}


$(document).ready(function(){
	$.get(window.location.origin + "/slp/ship/getShipperList", function(data) {
		shipperList = data;
		data.forEach(function(value, index) {
			$("#shipper-list").append('<tr><td class = "col-xs-1"><div class="item-shipper"><input type="checkbox" id='+index+' class="regular-checkbox" /><label for='+index+'></label></td>'
			+'<td class = "name-shipper col-xs-3">'+value.shp_User_Name+'</td>'
			+'<td class = "shp_Code col-xs-3">'+value.shp_Code+'</td>'
			+'<td class = "shp_Code col-xs-5">'+value.shp_DepotAddress+'</td></tr>');
		})
		
	});
	$.get(window.location.origin + "/slp/outgoingarticles/parcel/getBatchList", function(data) {
		dataBatchs = data;
		
		for(var i = 0; i < data.length; ++i) {
			dataBatchs[i].change = '<span style = "color: green; margin:10px; cursor:pointer" class="glyphicon glyphicon-pencil" onclick="allotParcel(' + i + ')" data-toggle="modal" data-target="#changeBatch"></span>';
			dataBatchs[i].shippers = "";
		}
		if(data == "") {
			listBatch = [];
		} else {
			listBatch = data;
		}
		
		table = $('#dataTabels-outarticles').DataTable({
			 data: dataBatchs,
	       columns: [
	           { "data": "reqbat_ID" },
	           { "data": "reqbat_Code" },
	           { "data": "reqbat_Description" },
	           { "data": "reqbat_CustomerCode" },
	           { "data": "shippers" },
	           { "data": "change"}
	       ],
	       "bSort": false,
	       "fnInfoCallback": function( oSettings, iStart, iEnd, iMax, iTotal, sPre ) {
	    	   var indexData = oSettings.aiDisplay;
	    	   viewBatch(indexData, 0);
	    	}
	   });
	});
});

function viewBatch(arr, index) {
	if(index == arr.length) {
		return ;
	} else {
		$.get(window.location.origin + "/slp/ship/getAllotParcel?batchCode="+dataBatchs[arr[index]].reqbat_Code, function(data) {
			var temp = table.row(arr[index]).data();
			temp.shippers = data.join(", ");
			
			table.row(arr[index]).data(temp);
			viewBatch(arr, index + 1);
		});
	}
}
</script>