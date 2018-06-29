<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:url value="/outgoingarticles/addAOrder.html" var="url"/>
<a href="<c:out value='${url}'/>">...</a>
<link href="<c:url value="/assets/libs/bootstrap-datepicker/css/bootstrap-datepicker.css" />" rel="stylesheet" type="text/css" media="all" />
<script src="<c:url value="/assets/libs/bootstrap-datepicker/js/bootstrap-datepicker.js"/>"></script>
<link href="<c:url value="/assets/libs/bootstrap-timepicker/css/bootstrap-timepicker.css" />" rel="stylesheet" type="text/css" media="all" />
<script src="<c:url value="/assets/libs/bootstrap-timepicker/js/bootstrap-timepicker.js"/>"></script>
<div id="page-wrapper">
	<div class="row">
		<div class="col-lg-12">
			<h1 class="page-header">Thêm mới hóa đơn</h1>
		</div>
		<!-- /.col-lg-12 -->
	</div>
	<form:form action="${baseUrl}/outgoingarticles/save-a-order.html" method="POST" commandName="orderFormAdd" role="form" class="form-horizontal">
	<div class="row">
		<div class="panel panel-default">
			
			<div class="panel-heading">
					Thêm mới hóa đơn khách hàng
			</div>
			
			
		<div class="panel-body">
					<div class="form-group">
						<label class="control-label col-lg-2">Số điện thoại</label>
						<div class="col-lg-6">
						<form:input path="orderClientCode" class="form-control" name="orderClientCode" placeholder="Number Phone"></form:input>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-2">Tên Khách Hàng</label>
						<div class="col-lg-6">
						<form:input path="orderClientName" class="form-control" name="orderClientName" placeholder="Client Name"></form:input>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-2">Địa chỉ</label>
						<div class="col-lg-6">
						<form:input path="orderAdress" class="form-control" name="orderAdress" placeholder="Adress"></form:input>
						</div>
					</div>
					<div class="form-group">
						<label class="control-label col-sm-2">Ngày giao hàng</label>
						<div class="col-lg-2">
						<form:input  path="orderDate" class="form-control datepicker" name="orderDate" placeholder="Date" ></form:input>
						</div>
						<label class="control-label col-lg-1">Từ:</label>
						<div class="col-lg-2">
						<form:input  path="orderTimeEarly" class="form-control timepicker" name="orderTimeEarly" placeholder="TimeEarly" ></form:input>
						</div>
						<label class="control-label col-sm-1">Đến:</label>
						<div class="col-lg-2">
						<form:input  path="orderTimeLate" class="form-control timepicker" name="orderTimeLate" placeholder="TimeLate" ></form:input>
						</div>
					</div>
		</div>
		</div>
		<div class="panel panel-default">
			<div class="panel-heading">
					Thêm các mặt hàng
			</div>
			<div class="panel-body">
				<div class="form-group ">
					<label class="control-label col-sm-2" for="categoryArticle">Chọn mặt hàng</label>
					<div class="col-sm-2">
						<select class="form-control categoryArticle" name="categoryArticle" >
							<option value="">Chọn mặt hàng</option>
							<c:forEach items="${listArticleCategory}" var="article">
                                     	<option value="${article.ARCat_Code}">${article.ARCat_Name}</option>
                            </c:forEach>
                    		
                    		
                    	</select>
                    </div>
				</div>	
				
				<div class="form-group ">
					<label class="control-label col-sm-2 articleAmount">Số lượng</label>
					<div class="col-sm-2">
						<input class="form-control" name="amount" placeholder="" ></input>
                    </div>
				</div>	
				
				<div class="form-group ">
					<label class="control-label col-sm-2">Giá</label>
					<div class="col-sm-2">
						<input class="form-control articlePrice" name="price" placeholder="" ></input>
                    </div>
				</div>
				
				<button type="button" class="btn btn-primary btn-xs" onclick="v_fAddOrderArticle();">Thêm</button>
			</div>
		
		<div class="panel-body">
			<div class="panel panel-default">
				<div class="panel-heading">
	                  Thành viên
	            </div>
	            <div class="panel-body">
	            	<div class="table-responsive">
	            		<table class="table table-striped table-bordered table-hover" id="listOrderArticles">
	            			<thead>
	            			<tr>
	                        	<th>Mặt hàng</th>
	                            <th>Số lượng</th>
	                            <th>Giá</th>
	                            <th>Xóa</th>
	                        </tr>
	                        </thead>
	                        <tbody>
	                        	<tr class="no-records-found">
	                            	<td colspan="6" align="center">Chưa có mặt hàng</td>
	                            </tr>
	                        </tbody>
	            		</table>
	            	</div>
	            </div>
			</div>
		</div>
		</div>
		<button type="submit" class="btn btn-primary" id="addANewPaper">Lưu</button>
        <button type="reset" class="btn btn-primary cancel">Hủy</button>
	</div>
	</form:form>
</div>
<!-- Javascript -->
<script>
	$(function() {
        $( ".datepicker" ).datepicker();
        
    });
	$('.timepicker').timepicker();
</script>
<script type="text/javascript">
function v_fAddOrderArticle(){
	var categoryArticle= $("select.categoryArticle").find(":selected").val();;
	var articleAmount= document.getElementsByName("amount")[0].value;
	var articlePrice=document.getElementsByName("price")[0].value;
	var sAddedArticle="";
	if(categoryArticle != "" && articleAmount != "" && articlePrice!=""){
		// Remove no records found column
		$("table#listOrderArticles tbody tr.no-records-found").remove();
		//make orderArticleRecord
		sAddedArticle+="<tr>";
		sAddedArticle+="<td><span>"+categoryArticle+"</span><input name='orderArticles' type='hidden' value='"+categoryArticle+ " "+articleAmount+" "+articlePrice+" "+"'/></td>";
		sAddedArticle+="<td><span>"+articleAmount+"</span></td>";
		sAddedArticle+="<td><span>"+articlePrice+"</span></td>";
		sAddedArticle 	+= "<td><button type='button' onclick='v_fClearOrderArticle(this);' class='btn btn-warning btn-xs' title='Xóa hàng này' >Xóa</button></td>";
		sAddedArticle+="</tr>";
		//push orderArticle in table
		$("table#listOrderArticles tbody").append(sAddedArticle);
	}
}	

function v_fClearOrderArticle(the_oElement){
	$(the_oElement).parents("tr").remove();
}
</script>

