<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>


<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Bubble Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 78.1);  map.put("y", 2);  map.put("z", 306.77);  map.put("name", "US");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 68.5);  map.put("y", 2.15);  map.put("z", 237.414);  map.put("name", "Indonesia");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 72.5);  map.put("y", 1.86);  map.put("z", 193.24);  map.put("name", "Brazil");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 76.5);  map.put("y", 2.36);  map.put("z", 112.24);  map.put("name", "Mexico");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 50.9);  map.put("y", 5.56);  map.put("z", 154.48);  map.put("name", "Nigeria");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 68.6);  map.put("y", 1.54);  map.put("z", 141.91);  map.put("name", "Russia");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 82.9);  map.put("y", 1.37);  map.put("z", 127.55);  map.put("name", "Japan");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 79.8);  map.put("y", 1.36);  map.put("z", 81.9);  map.put("name", "Australia");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 72.7);  map.put("y", 2.78);  map.put("z", 79.71);  map.put("name", "Egypt");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 80.1);  map.put("y", 1.94);  map.put("z", 61.81);  map.put("name", "UK");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 55.8);  map.put("y", 4.76);  map.put("z", 39.24);  map.put("name", "Kenya");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 81.5);  map.put("y", 1.93);  map.put("z", 21.95);  map.put("name", "Australia");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 68.1);  map.put("y", 4.77);  map.put("z", 31.09);  map.put("name", "Iraq");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 47.9);  map.put("y", 6.42);  map.put("z", 33.42);  map.put("name", "Afganistan");list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 50.3);  map.put("y", 5.58);  map.put("z", 18.55);  map.put("name", "Angola"); list.add(map);	
String dataPoints = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           zoomEnabled: true,
           animationEnabled: true,
           title: {
               text: "Fertility Rate Vs Life Expectancy in different countries - 2009"
           },
           subtitles: [
               {
                   text: "Try Zooming and Panning"
               }
           ],
           axisX: {
               title: "Life Expectancy",
               maximum: 85
           },
           axisY: {
               title: "Fertility Rate"

           },

           legend: {
               verticalAlign: "bottom",
               horizontalAlign: "left"

           },
           data: [
           {
               type: "bubble",
               legendText: "Size of Bubble Represents Population",
               showInLegend: true,
               legendMarkerType: "circle",
               legendMarkerColor: "grey",
               toolTipContent: "<span style='\"'color: {color};'\"'><strong>{name}</strong></span><br/><strong>Life Exp</strong> {x} yrs<br/> <strong>Fertility Rate</strong> {y}<br/> <strong>Population</strong> {z}mn",

               dataPoints: <%out.print(dataPoints);%>
           }
           ]
       });

       chart.render();
   });
</script>