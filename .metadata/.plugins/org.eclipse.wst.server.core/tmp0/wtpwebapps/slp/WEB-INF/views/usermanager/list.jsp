<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Datatable CSS -->
<link href="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.css"/>" rel="stylesheet">

<!-- DataTables Responsive CSS -->
<link href="<c:url value="/assets/libs/datatables-responsive/css/dataTables.responsive.css" />" rel="stylesheet">

<!-- DataTables JavaScript -->
<script src="<c:url value="/assets/libs/datatables/media/js/jquery.dataTables.js"/>"></script>
<script src="<c:url value="/assets/libs/datatables-plugins/integration/bootstrap/3/dataTables.bootstrap.js"/>"></script>


<div id="page-wrapper">
    <div class="row">
    	<div class="col-lg-12 center">
            <h1 class="page-header">Chức năng quản lý user</h1>
            
        </div>
        <!-- /.col-lg-12 -->
    </div>
    <!-- /.row -->
    <div class ="row">
    	<div class ="col-sm-1">
    		<button type ="button" class = "btn btn-primary addUser"> Thêm</button>
    		
    	</div>
    
    </div>
    
    
    <div class ="row">
    	<div class ="col-lg-8">
    		<div class ="panel panel-default">
    		<div class ="panel-heading"> Danh sách các User đã đăng ký:</div>
    			<div class ="panel-body">
    			<div class ="dataTable_wrapper">
    				<table class="table table-striped table-bordered table-hover" id="dataTables-mNU">
    				<thead>
    					<tr>
    						<th> Danh sách User</th>
    						<th></th>
    					</tr>
    				</thead>
    				
    				<tbody>
    					<c:forEach items ="${usi}" var = "mNU">
    					<tr>
    						<td><c:out value = "${mNU.username}"/></td>
    						<td>
    							<button type="button" onclick="v_fViewDetail('${mNU.username}');" class="btn btn-info btn-xs" title="Edit">Chi tiết</button>
    						</td>
    					</tr>
    					</c:forEach>
    				</tbody>
    			</table>
    			</div>
    			<!-- /dataTable_wrapper -->
    			</div>
    			<!-- /panel-body -->
    	</div>
    	<!-- /panel panel-default -->
    	</div>
    	<!-- /col-lg-12 -->
	</div>
	<!-- /row -->
</div>
<!-- /#page-wrapper -->

<script>
$(document).ready(function(){
	var table = $('dataTables-mNU').dataTable();
	$('.addUser').click(function(){
		window.location = '${baseUrl}' + "/usermanager/add-a-newUser.html";
	});
	
});
function v_fViewDetail(user){
	console.log(user);
	var sViewDetailUrl = baseUrl + "/usermanager/user-function-detail/"+user+".html";
	window.location = sViewDetailUrl;
}

</script>

