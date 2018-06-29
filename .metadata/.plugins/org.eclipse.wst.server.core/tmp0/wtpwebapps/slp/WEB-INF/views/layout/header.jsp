<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
	<c:set var="url" value="${requestScope['javax.servlet.forward.request_uri']}" ></c:set>
	<c:set var="sUrl" value="${fn:substringAfter(url, 'slp')}"></c:set>
	<c:forEach items="${functionPermissionList}" var="fPL">
    	<c:if test="${fPL.FUNC_Url == sUrl}">
    		<c:choose>
    			<c:when test="${fPL.FUNC_HasChildren==1 }">
    				<c:set var="idparent" value="${fPL.FUNC_Id}"></c:set>	
    			</c:when>
    			<c:otherwise>
    				<c:set var="idparent" value="${fPL.FUNC_ParentId}"></c:set>	
    			</c:otherwise>
    		</c:choose>
    		
	    </c:if>
    </c:forEach>
    <c:if test="${empty idparent}">
    	<c:set var="sUrl" value="${fn:substringAfter(sUrl, '/')}"></c:set>
    	<c:set var="sUrl" value="${fn:substringBefore(sUrl, '/')}"></c:set>
    	<c:set var="sUrl" value="/${sUrl}" ></c:set>
    	<c:forEach items="${functionPermissionList}" var="fPL">
    		<c:set var="fsUrl" value="${fn:substringAfter(fPL.FUNC_Url, '/')}"></c:set>
    		<c:set var="fsUrl" value="${fn:substringBefore(fsUrl, '/')}"></c:set>
    		<c:set var="fsUrl" value="/${fsUrl}" ></c:set>
    		<c:if test="${fsUrl == sUrl}">
    			<c:choose>
    				<c:when test="${fPL.FUNC_HasChildren==1 }">
    					<c:set var="idparent" value="${fPL.FUNC_Id}"></c:set>	
    				</c:when>
    				<c:otherwise>
	    				<c:set var="idparent" value="${fPL.FUNC_ParentId}"></c:set>	
    				</c:otherwise>
    			</c:choose>	
	    	</c:if>
    	</c:forEach>
    </c:if>
    <c:forEach items="${functionPermissionList}" var="fPL">
    	<c:if test="${fPL.FUNC_Id == idparent}">
    		<c:set var="nameModule" value ="${fPL.FUNC_Name}"></c:set>
	    </c:if>
    </c:forEach>
	<div class="navbar-header">
		<a href="<c:url value="/"/>"><span class="navbar-brand">${nameModule}</span></a>
	</div>
	
	<ul class="nav navbar-top-links navbar-right">
		<li class="dropdown">
			<a class="drpdown-toggle" data-toggle="dropdown" href="#">
				${currentUser.username}
				<i class="fa fa-user fa-fw"></i>
				<i class="fa fa-caret-down"></i>
			</a>
			<ul class="dropdown-menu dropdown-user">
				<li><a href="<c:url value="/j_spring_security_logout" />"><i class="fa fa-sign-out fa-fw"> Đăng xuất</i></a></li>
			</ul>
		</li>
	</ul>
	<c:set var="url" value="${requestScope['javax.servlet.forward.request_uri']}" ></c:set>
	<c:set var="sUrl" value="${fn:substringAfter(url, 'slp')}"></c:set>
	<c:forEach items="${functionPermissionList}" var="fPL">
    	<c:if test="${fPL.FUNC_Url == sUrl}">
    		<c:choose>
    			<c:when test="${fPL.FUNC_HasChildren==1 }">
    				<c:set var="idparent" value="${fPL.FUNC_Id}"></c:set>	
    			</c:when>
    			<c:otherwise>
    				<c:set var="idparent" value="${fPL.FUNC_ParentId}"></c:set>	
    			</c:otherwise>
    		</c:choose>
    		
	    </c:if>
    </c:forEach>
	<div class="navbar-default sidebar" id="sidebar" role="navigation">
		<div class="sidebar-nav navbar-collapse">
		<ul	class="nav" id="side-menu">
			<c:forEach items="${functionChildrenPermissionList}" var="fCPL">
            		<c:if test="${fCPL.FUNC_ParentId == idparent}">	
		                <li>
		                	<a class="${fCPL.FUNC_SelectedClass}" href="<c:url value="${baseUrl}${fCPL.FUNC_Url}"/>"><i class="${fCPL.FUNC_TitleClass}"></i> ${fCPL.FUNC_Name} </a>
		                	
		                </li>
	                </c:if>
                </c:forEach>
        </ul>
		</div>
	</div>
</nav>