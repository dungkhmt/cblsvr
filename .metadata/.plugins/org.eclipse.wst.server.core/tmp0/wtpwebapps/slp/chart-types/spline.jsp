<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Spline Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 17363);  map.put("label", "2005-06"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 28726);  map.put("label", "2006-07"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 35000);  map.put("label", "2007-08"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 25250);  map.put("label", "2008-09"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 19750);  map.put("label", "2009-10"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 18850);  map.put("label", "2010-11"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 26700);  map.put("label", "2011-12"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 16000);  map.put("label", "2012-13"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 19000);  map.put("label", "2013-14"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 18000);  map.put("label", "2014-15"); list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           theme: "theme2",
           animationEnabled: true,
           title: {
               text: "Analysis of Pepper Export - India"
           },
           axisX: {
               title: "Year"
           },
           axisY: {
               title: "In Tonnes"
           },

           data: [
           {
               type: "spline",
               dataPoints: <%out.print(dataPoints);%>
           }
           ]
       });

       chart.render();
   });
</script>