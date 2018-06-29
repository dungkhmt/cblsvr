<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script src="<c:url value="/assets/libs/chart/Chart.bundle.js"/>"></script>
<script src="<c:url value="/assets/libs/chart/Chart.bundle.min.js"/>"></script>
<script src="<c:url value="/assets/libs/chart/Chart.js"/>"></script>
<script src="<c:url value="/assets/libs/chart/Chart.min.js"/>"></script>

<div id="page-wrapper">
	<canvas id="myChart" height="600" width="600"></canvas>
</div>

<script>
var ctx = document.getElementById("myChart");	

var data = ${data};
console.log(JSON.stringify(data));
var myChart = new Chart(ctx, {
    type: 'line',
    data: {
        datasets: [{
           data: data,
           fill : false,
           borderColor:'#ff0000',
           backgroundColor:'#ff0000',
           label : "hello"
        }]
    },
    options: {
    	 scales: {
             xAxes: [{
                 type: 'linear',
                 position: 'bottom'
             }]
         },
         responsive : false,
         
    }
});
</script>
