<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<link href="<c:url value="/assets/libs/fileinput/css/fileinput.min.css" />" media="all" rel="stylesheet" type="text/css">
	
<div id="page-wrapper">
    <form:form action="${baseUrl}/containerdelivery/upload-file-pickupdelivery-orders" method="POST" commandName="formAdd" enctype="multipart/form-data" role="form">
    <div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">Upload danh sách hóa đơn</h1>
		</div>
		<div class="col-sm-4">
			<div class="form-group">
				<form:select path="batchCode" name="batchCode" class="form-control">
					<option>Chọn Batch</option>
					<c:forEach items="${listBatch}" var="reBatch">
						<option value="${reBatch.REQBAT_Code}"><c:out value="${reBatch.REQBAT_Code}"/></option>
					</c:forEach>
				</form:select>
			</div>
		</div>
		<div class="col-lg-12">
				<form:input id="input-file" path="ordersFile" name="ordersFile" type="file" class="file file-loading" data-allowed-file-extensions='["xlsx"]'></form:input>
		</div>
		<!-- /.col-lg-12 -->
    </div>
   	</form:form>
 </div>
 
<script src="<c:url value="/assets/libs/fileinput/js/fileinput.min.js"/>"></script>
<script>

$(document).ready(function(){
	 $("#input-file").fileinput();
 });
</script>