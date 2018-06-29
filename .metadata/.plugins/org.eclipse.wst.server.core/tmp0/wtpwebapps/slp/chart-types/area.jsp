<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>
<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Area Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'></div>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>();	map.put("x", 1325356200000L); map.put("y", 2600); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1328034600000L); map.put("y", 3800); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1330540200000L); map.put("y", 4300); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1333218600000L); map.put("y", 2900); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1335810600000L); map.put("y", 4100); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1338489000000L); map.put("y", 4500); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1341081000000L); map.put("y", 8600); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1343759400000L); map.put("y", 6400); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1346437800000L); map.put("y", 5300); list.add(map);
map = new HashMap<Object,Object>();	map.put("x", 1349029800000L); map.put("y", 6000); list.add(map);

String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           theme: "theme2",
           animationEnabled: true,
           title: {
               text: "Monthly Downloads",
               fontSize: 25
           },
           axisX: {
               valueFormatString: "MMM",
               interval: 1,
               intervalType: "month"

           },
           axisY: {
               title: "Downloads"
           },

           data: [
           {
               type: "area",
               xValueType: "dateTime",
               dataPoints: <%out.print(dataPoints);%>
           }
           ]
       });

       chart.render();
   });
</script>