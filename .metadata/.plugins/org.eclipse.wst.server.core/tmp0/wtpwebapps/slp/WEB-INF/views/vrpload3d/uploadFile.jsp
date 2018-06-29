<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link href="<c:url value="/assets/libs/fileinput/css/fileinput.min.css" />" media="all" rel="stylesheet" type="text/css">

<div id="page-wrapper">
	<form:form action="${baseUrl}/vrp-load3d/computeSolution" method="POST" commandName="vrpInputFile" enctype="multipart/form-data" role="form">
		<div class="row">
			<div class="col-lg-12">
				<h1 class="page-header">Upload Data File</h1>
			</div>
			<div class="col-lg-12">
				<form:input id="input-file" path="vrpInputRequest" name="vrpInputRequest" type="file" class="file file-loading" data-allowed-file-extensions='["json"]'/>
			</div>
		</div>
	</form:form>
</div>

<script src="<c:url value="/assets/libs/fileinput/js/fileinput.min.js"/>"></script>
<script>
$(document).ready(function(){
	 $("#input-file").fileinput();
 });
</script>