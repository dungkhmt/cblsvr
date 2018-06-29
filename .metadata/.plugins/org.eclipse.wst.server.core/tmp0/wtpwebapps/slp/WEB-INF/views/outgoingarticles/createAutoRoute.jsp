<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div id="page-wrapper">
   	<div class="row">
        <div class="col-lg-12">
            <h1 class="page-header">Lập tuyến giao hàng tự động</h1>
		</div>
		<div class="col-sm-4">
			<div class="form-group">
				<select class="form-control" id="lstBatch">
					<option>Chọn Batch</option>
					<c:forEach items="${lstreBatch}" var="reBatch">
						<option value="${reBatch.REQBAT_Code}"><c:out value="${reBatch.REQBAT_Description}"/></option>
					</c:forEach>
				</select>
			</div>
		</div>
		<button type="submit" class="btn btn-primary" id="create">Lập tuyến</button> <span id ="icon"></span>
 	</div>
 </div>
 
 <script>
 $(document).ready(function(){
	$("#create").click(function(){
		$("#icon").append("<h4>Creating....<img src=\"<c:url value='/assets/icon/loading.gif' />\" style=\"width:50px;height:50px;\"><h4>");
		
		var batch = $("#lstBatch").val();
		
		$.ajax({
			type : "POST",
			url : baseUrl+"/outgoingarticles/callServiceCreateRoute",
			data : batch,
			contentType: "application/text",
			success: function(){
				window.location = baseUrl+"/onlinestore";
			}
		})
	});
 });
 </script>
 