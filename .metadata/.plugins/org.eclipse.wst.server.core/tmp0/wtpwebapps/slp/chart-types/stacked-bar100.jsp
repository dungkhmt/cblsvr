<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Bar 100% Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 374);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 320);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 300);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 400);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 220);  map.put("label", "Larry"); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 250);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 280);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 275);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 321);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 320);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 280);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 420);  map.put("label", "Larry"); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 350);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 374);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 400);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Larry"); list.add(map);	
String dataPoints3 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 250);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 250);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 250);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 275);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 320);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 220);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 100);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 420);  map.put("label", "Larry"); list.add(map);	
String dataPoints4 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 150);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 30);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 45);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 74);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 64);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 40);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 50);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 40);  map.put("label", "Larry"); list.add(map);	
String dataPoints5 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 150);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 170);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 150);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 174);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 160);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 100);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 80);  map.put("label", "Larry"); list.add(map);	
String dataPoints6 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 160);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 170);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 50);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 174);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 104);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 300);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 420);  map.put("label", "Larry"); list.add(map);	
String dataPoints7 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 80);  map.put("label", "George"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 150);  map.put("label", "Alex"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 50);  map.put("label", "Mike"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 74);  map.put("label", "Jake"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 40);  map.put("label", "Shah"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Joe"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 100);  map.put("label", "Fin"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 120);  map.put("label", "Larry"); list.add(map);	
String dataPoints8 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           theme: 'theme2',
           title: {
               text: "Time Spent in Holiday Season"
           },
           animationEnabled: true,
           axisY: {
               title: "percent"
           },
           legend: {
               horizontalAlign: 'center',
               verticalAlign: 'bottom'
           },
           toolTip: {
               shared: true,
               content: "<span style='\"'color: {color};'\"'>{name}</span> : {y}min"
           },
           data: [
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "With Friends",
               dataPoints: <%out.print(dataPoints1);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Eating Out",
               dataPoints: <%out.print(dataPoints2);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Reading",
               dataPoints: <%out.print(dataPoints3);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Shopping",
               dataPoints: <%out.print(dataPoints4);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Fitness",
               dataPoints: <%out.print(dataPoints5);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Travel",
               dataPoints: <%out.print(dataPoints6);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Internet",
               dataPoints: <%out.print(dataPoints7);%>
           },
           {
               type: "stackedBar100",
               showInLegend: true,
               name: "Hobbies",
               dataPoints: <%out.print(dataPoints8);%>
           }
           ]
       });
       chart.render();
   });
</script>