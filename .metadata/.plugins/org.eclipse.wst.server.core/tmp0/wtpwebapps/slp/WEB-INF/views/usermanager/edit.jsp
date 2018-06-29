<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style>

.tree li {
    list-style-type:none;
    margin:0;
    padding:10px 5px 0 5px;
    position:relative
}
.tree li::before, .tree li::after {
    content:'';
    left:-30px;
    position:absolute;
    right:auto
}
.tree li::before {
    border-left:1px solid #999;
    bottom:50px;
    height:100%;
    top:0;
    width:1px
}
.tree li::after {
    border-top:1px solid #999;
    height:20px;
    top:25px;
    width:25px
}
.tree li span {
    -moz-border-radius:5px;
    -webkit-border-radius:5px;


    display:inline-block;
    padding:3px 8px;
    text-decoration:none
}
.tree li.parent_li>span {
    cursor:pointer
}

.tree li:last-child::before {
    height:30px
}
.tree li.parent_li>span:hover, .tree li.parent_li>span:hover+ul li span {
    background:#eee;
    border:1px solid #94a0b4;
    color:#000
}
</style>	 
<div id="page-wrapper">
     <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Chỉnh sửa quyền truy cập chức năng
                </div>
        		<div class="panel-body">
        			<form:form action="${baseUrl}/usermanager/edit-user-function" method="POST" commandName="editUserFunction" role="form">
        			<form:hidden path="userName" name="userName"/>
        			<div class="tree form-group">
								<label>Chức năng</label>
					            <ul>
						               <c:forEach items="${listParentFunctionEdit}" var="lPFE">	
							                <li class="checkbox">
							                	<label><input type="checkbox" name="functions" <c:if test="${lPFE.FUNC_Selected eq 1}">checked</c:if> value="${lPFE.FUNC_Code}" /> <span>${lPFE.FUNC_Name}</span></label>
							                	<ul>
							                	<c:forEach items="${listChildrenFunctionEdit}" var="lCFE">
							                		<c:if test="${lCFE.FUNC_ParentId == lPFE.FUNC_Id}">
							                			<li>
															<label><input type="checkbox" name="functions" <c:if test="${lCFE.FUNC_Selected eq 1}">checked</c:if> value="${lCFE.FUNC_Code}" /> <span>${lCFE.FUNC_Name}</span></label>
														</li>									                	
								                    </c:if>
							                	</c:forEach>
							                	</ul>
							                </li>
						                </c:forEach>
					            </ul>
							</div>
							<button type="submit" class="btn btn-primary">Lưu</button>
                            <button type="reset" class="btn btn-primary cancel">Hủy</button>
						</form:form>
        		</div>
 			</div>
 		</div>
 	</div>
</div>
<!-- /#page-wrapper -->