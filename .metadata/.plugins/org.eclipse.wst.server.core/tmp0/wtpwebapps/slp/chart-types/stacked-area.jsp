<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Area Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>


<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("x", 1363458600000L);  map.put("y", 2.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363545000000L);  map.put("y", 0.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363631400000L);  map.put("y", 0.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363717800000L);  map.put("y", 1.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363804200000L);  map.put("y", 1.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363890600000L);  map.put("y", 1.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363977000000L);  map.put("y", 2.6); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 1363458600000L);  map.put("y", 3.3); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363545000000L);  map.put("y", 1.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363631400000L);  map.put("y", 2.1); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363717800000L);  map.put("y", 1.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363804200000L);  map.put("y", 1.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363890600000L);  map.put("y", 1.7); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363977000000L);  map.put("y", 4.6); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 1363458600000L);  map.put("y", 2.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363545000000L);  map.put("y", 2); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363631400000L);  map.put("y", 2.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363717800000L);  map.put("y", 1.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363804200000L);  map.put("y", 1.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363890600000L);  map.put("y", 1.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363977000000L);  map.put("y", 1.6); list.add(map);	
String dataPoints3 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("x", 1363458600000L);  map.put("y", 0.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363545000000L);  map.put("y", 7); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363631400000L);  map.put("y", 6.8); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363717800000L);  map.put("y", 4.6); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363804200000L);  map.put("y", 6.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363890600000L);  map.put("y", 7.4); list.add(map);
map = new HashMap<Object,Object>(); map.put("x", 1363977000000L);  map.put("y", 1.6); list.add(map);	
String dataPoints4 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "Productivity by Day"
           },
           subtitles: [
               {
                   text: "Click on Legends to Enable/Disable Data Series"
               }
           ],
           animationEnabled: true,
           axisX: {
               valueFormatString: "DDD",
           },

           legend: {
               verticalAlign: "bottom",
               horizontalAlign: "center"
           },
           toolTip: {
               content: function (e) {
                   var weekday = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
                   var str1 = weekday[e.entries[0].dataPoint.x.getDay()] + "<br/>  <span style =' color:" + e.entries[0].dataSeries.color + "';>" + e.entries[0].dataSeries.name + "</span>: <strong>" + e.entries[0].dataPoint.y + " hrs</strong> <br/>";
                   return str1
               }
           },

           data: [
             {
                 type: "stackedArea",
                 name: "Very Distracting",
                 showInLegend: true,
                 legendMarkerType: "square",
                 color: "rgba(211,19,14,.8)",
                 markerSize: 0,
                 xValueType: "dateTime",
                 dataPoints: <%out.print(dataPoints1);%>
             },
             {
                 type: "stackedArea",
                 name: "Distracting",
                 showInLegend: true,
                 legendMarkerType: "square",
                 markerSize: 0,
                 color: "rgba(95,53,87,.8)",
                 xValueType: "dateTime",
                 dataPoints: <%out.print(dataPoints2);%>
             },

           {
               type: "stackedArea",
               name: "Productive",
               showInLegend: true,
               legendMarkerType: "square",
               markerSize: 0,
               color: "rgba(60,84,151,.9)",
               xValueType: "dateTime",
               dataPoints: <%out.print(dataPoints3);%>
           },
           {
               type: "stackedArea",
               name: "Very Productive",
               showInLegend: true,
               legendMarkerType: "square",
               markerSize: 0,
               color: "rgba(22,115,211,.9)",
               xValueType: "dateTime",
               dataPoints: <%out.print(dataPoints4);%>
           }
           ]
             ,
           legend: {
               cursor: "pointer",
               itemclick: function (e) {
                   if (typeof (e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
                       e.dataSeries.visible = false;
                   }
                   else {
                       e.dataSeries.visible = true;
                   }
                   chart.render();
               }
           }
       });

       chart.render();
   });
</script>