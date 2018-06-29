<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


	
<div id="page-wrapper">
    
    <div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">Chọn lô hàng lập tuyến</h1>
		</div>
		<div class="main col-sm-4">
			<div class="form-group">
				<select id="batchcode" name="batchCode" class="form-control">
					<option>Chọn lô</option>
					<c:forEach items="${listBatch}" var="reBatch">
						<option value="${reBatch.REQBAT_Code}"><c:out value="${reBatch.REQBAT_Code}"/></option>
					</c:forEach>
				<select>
			</div>
			<button type="button" class="btn btn-primary active" title="Tạo route cho lô này" onclick="onclick2()">Tạo tuyến</button>
		</div>
		<!-- /.col-lg-12 -->
    </div>
   	
 </div>
 
<script>
function onclick2(){
	$(".main").append("<h4>Creating....<img src=\"<c:url value='/assets/icon/loading.gif' />\" style=\"width:50px;height:50px;\"><h4>");
	$( ".main" ).append( "" );
	
	var select=$("#batchcode option:selected").val();
	console.log(select);
	//alert("x");
	if(select !=null) 
	$.ajax({ 
   		type:"POST", 
    	url:"${baseUrl}/containerdelivery/get-route-auto",
    	data: select,
    	contentType: "application/json; charset=utf-8",
    	dataType: "json",
    	//Stringified Json Object
    	success: function(response){
    		if(response==true){
    			window.location = '${baseUrl}/containerdelivery'
    		}
    	}	
	});
}
</script>