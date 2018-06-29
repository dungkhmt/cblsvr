<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Step Area Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 1293820200000L);  map.put("y", 89.28); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1296498600000L);  map.put("y", 85.53); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1298917800000L);  map.put("y", 98.66); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1301596200000L);  map.put("y", 105.72); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1304188200000L);  map.put("y", 95.72); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1306866600000L);  map.put("y", 90.67); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1309458600000L);  map.put("y", 91.51); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1312137000000L);  map.put("y", 79.86); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1314815400000L);  map.put("y", 79.31); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1317407400000L);  map.put("y", 80.19); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1320085800000L);  map.put("y", 91.34); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1322677800000L);  map.put("y", 93.14); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1325356200000L);  map.put("y", 94.18); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1328034600000L);  map.put("y", 96.17); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1330540200000L);  map.put("y", 99.49); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1333218600000L);  map.put("y", 96.22); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1335810600000L);  map.put("y", 87.31); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1338489000000L);  map.put("y", 75.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1341081000000L);  map.put("y", 80.93); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1343759400000L);  map.put("y", 88.04); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1346437800000L);  map.put("y", 88.41); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1349029800000L);  map.put("y", 83.06); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1351708200000L);  map.put("y", 80.55); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1354300200000L);  map.put("y", 82.35); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1356978600000L);  map.put("y", 88.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1359657000000L);  map.put("y", 86.48); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1362076200000L);  map.put("y", 87.5); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {

           animationEnabled: true,
           zoomEnabled: true,
           title: {
               text: "Monthly Average Crude Oil Prices"
           },
           legend: {
               verticalAlign: "bottom",
               horizontalAlign: "center"
           },
           axisX: {
               valueFormatString: "MMM YY"
           },
           axisY: {
               includeZero: false,
               prefix: "$",
               title: "Inflation Adjusted Price",
               maximum: 110
           },
           data: [
           {
               type: "stepArea",
               markerSize: 0,
               toolTipContent: "{x} : <strong>${y} <strong>",
               xValueType: "dateTime",
               dataPoints: <%out.print(dataPoints1);%>
           }
           ]
       });
       chart.render();
   });
</script>