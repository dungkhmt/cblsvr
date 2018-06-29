<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Pie Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 72.48);  map.put("legendText", "Google");  map.put("label", "Google"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 10.39);  map.put("legendText", "Bing");  map.put("label", "Bing"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 7.78);  map.put("legendText", "Yahoo!");  map.put("label", "Yahoo!"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 7.14);  map.put("legendText", "Baidu");  map.put("label", "Baidu"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 0.22);  map.put("legendText", "Ask");  map.put("label", "Ask"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 0.15);  map.put("legendText", "AOL");  map.put("label", "AOL"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 1.84);  map.put("legendText", "Others");  map.put("label", "Others");list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">
   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "Desktop Search Engine Market Share, Jul-2016"
           },
           animationEnabled: true,
           legend: {
               verticalAlign: "center",
               horizontalAlign: "left",
               fontSize: 20,
               fontFamily: "Helvetica"
           },
           theme: "theme2",
           data: [
           {
               type: "pie",
               indexLabelFontFamily: "Garamond",
               indexLabelFontSize: 20,
               indexLabel: "{label} {y}%",
               startAngle: -20,
               showInLegend: true,
               toolTipContent: "{legendText} {y}%",
               dataPoints: <%out.print(dataPoints);%>              
           }
           ]
       });
       chart.render();
   });
</script>