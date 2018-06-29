<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Step Line Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 1325356200000L);  map.put("y", 8.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1328034600000L);  map.put("y", 8.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1330540200000L);  map.put("y", 8.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1333218600000L);  map.put("y", 8.1); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1335810600000L);  map.put("y", 8.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1338489000000L);  map.put("y", 8.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1341081000000L);  map.put("y", 8.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1343759400000L);  map.put("y", 8.1); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1346437800000L);  map.put("y", 7.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1349029800000L);  map.put("y", 7.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1351708200000L);  map.put("y", 7.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1354300200000L);  map.put("y", 7.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1356978600000L);  map.put("y", 7.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1359657000000L);  map.put("y", 7.7); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1362076200000L);  map.put("y", 7.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1364754600000L);  map.put("y", 7.5); list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "US Unemployement Rate"
           },
           animationEnabled: true,
           axisX: {
               valueFormatString: "MMM YY"
           },
           axisY: {
               includeZero: false,
               interval: .25,
               valueFormatString: "#.00'%'"
           },

           data: [
           {
               type: "stepLine",
               toolTipContent: "{x}: {y}%",
               markerSize: 5,
               xValueType: "dateTime",
               dataPoints: <%out.print(dataPoints);%>
           }

           ]
       });

       chart.render();
   });
</script>