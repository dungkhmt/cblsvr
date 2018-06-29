<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Area 100% Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 1.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 9.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 36.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 56.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 74.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 80.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 78.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 84.1); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 10.5); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 15.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 16.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 22.5); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 18.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 15.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 17.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 14.8); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 10.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 6.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 2.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 1.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 2.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 2.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 2.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 0.6); list.add(map);	
String dataPoints3 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 20.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 19.7); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 13); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 6.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 0.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 0.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 0.2); list.add(map);	
String dataPoints4 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 48.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 44.2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 27.7); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 8.5); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 0.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 0.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 0.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 0.2); list.add(map);	
String dataPoints5 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 2009);  map.put("y", 8.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2010);  map.put("y", 4.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2011);  map.put("y", 3.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2012);  map.put("y", 3.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2013);  map.put("y", 0.9); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2014);  map.put("y", 0.1); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2015);  map.put("y", 0.1); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 2016);  map.put("y", 0.1); list.add(map);	
String dataPoints6 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           animationEnabled: true,
           title: {
               text: "Global Mobile OS Market Share in Sales to End Users"
           },
           axisX: {
               title: "Year",
               valueFormatString: "####"
           },
           axisY: {
               title: "Share of Global Sales to the end user",
               titleFontSize: 16
           },
           toolTip: {
               shared: true,
               content: "<span style='\"'color: {color};'\"'>{name}</span> : {y}%"
           },
           data: [
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "Android",
               dataPoints: <%out.print(dataPoints1);%>
           },
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "iOS",
               dataPoints: <%out.print(dataPoints2);%>
           },
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "Microsoft",
               dataPoints: <%out.print(dataPoints3);%>
           },
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "RIM BlackBerry",
               dataPoints: <%out.print(dataPoints4);%>
           },
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "Symbian",
               dataPoints: <%out.print(dataPoints5);%>
           },
           {
               type: "stackedArea100",
               xValueFormatString: "####",
               showInLegend: true,
               name: "Others",
               dataPoints: <%out.print(dataPoints6);%>
           }
           ]
       });

       chart.render();
   });
</script>