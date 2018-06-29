<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<link rel="stylesheet" type="text/css" href="<c:url value="/assets/libs/inputDate/dist/css/bootstrap-datetimepicker.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/assets/libs/inputDate/dist/css/bootstrap-datetimepicker.min.css"/>"/>

<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/moment.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/inputDate/dist/js/bootstrap-datetimepicker.min.js" />"></script>
<script type="text/javascript" src="<c:url value="/assets/libs/bootstrap/dist/js/collapse.js" />"></script>

<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Nhập hàng</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<!-- /.row -->	
	<div class="row">
		<div class="panel panel-default">
			<div class="panel-heading">
				Thêm mặt hàng
			</div>
			<div class="panel-body">
				<form:form class="form-horizontal" role="form" commandName="articleFormAdd">
					<div class="form-group">
						<label for="articleCode" class="control-label col-sm-2">Mặt hàng</label>
						<div class="col-sm-6">
							<form:select path="articleCode" name="articleCode" class="form-control">
								<option value="">Chọn mặt hàng</option>
								<c:forEach items="${lstArticlesCat}" var="articlesCat">
									<option value="${articlesCat.ARCat_Code}">${articlesCat.ARCat_Name}</option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="form-group">
						<label for="amount" class="control-label col-sm-2">Số lượng</label>
						<div class="col-sm-6">
							<form:input path="amount" name="amount" type="text" class="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<label for="price" class="control-label col-sm-2">Giá</label>
						<div class="col-sm-6">
							<form:input path="price" name="price" type="text" class="form-control"/>
						</div>
					</div>
					<div class="form-group">
						<label for="sp_code" class="control-label col-sm-2">Nhà cung cấp</label>
						<div class="col-sm-6">
							<form:select path="sp_code" name="sp_code" class="form-control">
								<option value="">Chọn nhà cung cấp</option>
								<c:forEach items="${listSuppliers}" var="supplier">
									<option value="${supplier.supplier_Code}">${supplier.supplier_Name}</option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="form-group">
						<label for="date" class="control-label col-sm-2">Ngày nhập</label>
		                <div class="col-sm-6">
		                	<div class='input-group date' id='datetimepicker1'>
		                    <form:input path="date" name="date" type='text' class="form-control"	/>
		                    <span class="input-group-addon">
		                        <span class="glyphicon glyphicon-calendar"></span>
		                    </span>
		                </div>
		                </div>
		            </div>
					<div class="form-group">
						<div class="col-sm-offset-7 col-sm-1">
							<button type="button" class="btn btn-primary" onclick="v_fAddArticle();">Thêm</button>
						</div>
					</div>
			</form:form>
			</div>
			<!--/.panel-body  -->
		</div>
		<!-- /.panel -->
		<div class="panel panel-default">
			<div class="panel-heading">
				Kết quả
			</div>
			<div class="panel-body">
				<div class="table-responsive">
					<form:form action="${baseUrl}/incomingArticles/saveIncomingArticles" role="form" modelAttribute="lstArtForm" method="POST">
						<table class="table table-striped table-bordered table-hover" id="listArticles">
							<thead>
								<tr>
									<th>STT</th>
									<th>Mặt hàng</th>
									<th>Số lượng</th>
									<th>Giá</th>
									<th>Nhà cung cấp</th>
									<th>Ngày nhập</th>
									<th></th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
						<button type="submit" class="btn btn-primary" id="addListArticles">Lưu</button>
						<button type="reset" class="btn btn-primary cancel" >Hủy</button>
					</form:form>
				</div>
				<!-- /.table-reponsive -->
			</div>
			<!-- /.panel-body -->
		</div>
		<!-- /.panel -->
	</div>
	<!-- /.row -->
</div>
<!-- /#page-wrapper -->

 <script type="text/javascript">
$(document).ready(function () {
	$('#datetimepicker1').datetimepicker({
		format : "YYYY-MM-DD HH:mm"
	});
	$('.cancel').click(function(){
		window.location = '${baseUrl}/incomingArticles/list';
	})
});

var nArticle=0;
function v_fAddArticle(){
	var addedArticle = "";
	var articleCode = $("select#articleCode").find(":selected").val();
	var articleName = $("select#articleCode").find(":selected").text();
	var articleAmount = $("#amount").val();
	var articlePrice = $("#price").val();
	var articleSPCode = $("select#sp_code").find(":selected").val();
	var articleSPName = $("select#sp_code").find(":selected").text();
	var articleDate = $("#date").val();
	
	addedArticle += "<tr>";
	addedArticle += "<td>"+(nArticle+1)+"</td>";
	addedArticle += "<td>"+articleName+"<input name='lstIncomingArticles["+nArticle+"].articleCode' type='hidden' value='"+articleCode+"'/>"+"</td>";
	addedArticle += "<td>"+articleAmount+"<input name='lstIncomingArticles["+nArticle+"].amount' type='hidden' value='"+articleAmount+"'/>"+"</td>";
	addedArticle += "<td>"+articlePrice+"<input name='lstIncomingArticles["+nArticle+"].price' type='hidden' value='"+articlePrice+"'/>"+"</td>";
	addedArticle += "<td>"+articleSPName+"<input name='lstIncomingArticles["+nArticle+"].sp_code' type='hidden' value='"+articleSPCode+"'/>"+"</td>";
	addedArticle += "<td>"+articleDate+"<input name='lstIncomingArticles["+nArticle+"].date' type='hidden' value='"+articleDate+"'/>"+"</td>";
	addedArticle += "<td><button type='button' onclick='v_fClearArticle(this);' class='btn btn-warning btn-xs' title='Hủy' >Xóa</button></td>";
	addedArticle += "</tr>";
	
	$("table#listArticles tbody").append(addedArticle);
	nArticle++;
}

function v_fClearArticle(element){
	$(element).parents("tr").remove();
	nArticle--;
}
</script>
