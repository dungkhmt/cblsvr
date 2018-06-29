<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Column Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("labels", "Apple");	map.put("y",6); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Mango");	map.put("y",4); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Orange"); map.put("y",5); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Banana"); map.put("y",7); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Pineapple");	map.put("y",4);	list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Pears");	map.put("y",6);	list.add(map);	
map = new HashMap<Object,Object>(); map.put("labels", "Grapes"); map.put("y",7); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Lychee"); map.put("y",5); list.add(map);	
map = new HashMap<Object,Object>();	map.put("labels", "Jackfruit");	map.put("y",4);list.add(map);	

String dataPoints = gsonObj.toJson(list);
%>
<script type="text/javascript">
    $(function () {
        var chart = new CanvasJS.Chart("chartContainer", {
            theme: "theme2",
            animationEnabled: true,
            title: {
                text: "Column Chart in JSP using CanvasJS"
            },
            data: [{
                type: "column",
                dataPoints: <%out.print(dataPoints);%>
            }]
        });
        chart.render();
    });
</script>