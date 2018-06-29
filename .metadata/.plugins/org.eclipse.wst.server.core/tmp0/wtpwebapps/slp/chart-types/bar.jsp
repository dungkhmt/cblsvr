<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>


<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Bar Chart" name="pageTitle"/>
	<jsp:param value="<div id='chartContainer'/>" name="content"/> 	
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>();map.put("label", "France"); map.put("y",243); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Great Britain"); map.put("y",273); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Soviet Union"); map.put("y",525); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "USA"); map.put("y",1118); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>();map.put("label", "France"); map.put("y",272); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Great Britain"); map.put("y",299); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Soviet Union"); map.put("y",419); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "USA"); map.put("y",896); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>();map.put("label", "France"); map.put("y",307); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Great Britain"); map.put("y",301); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "Soviet Union"); map.put("y",392); list.add(map);	
map = new HashMap<Object,Object>();	map.put("label", "USA"); map.put("y",788); list.add(map);	
String dataPoints3 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           title: {
               text: "Olympic Medals of all Times (till 2016 Olympics)"
           },
           subtitles: [
               {
                   text: "Click on Legends to Enable/Disable Data Series"
               }
           ],
           animationEnabled: true,
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
           },
           axisY: {
               title: "Medals"
           },
           toolTip: {
               shared: true,
               content: function (e) {
                   var str = '';
                   var total = 0;
                   var str3;
                   var str2;
                   for (var i = 0; i < e.entries.length; i++) {
                       var str1 = "<span style= 'color:" + e.entries[i].dataSeries.color + "'> " + e.entries[i].dataSeries.name + "</span>: <strong>" + e.entries[i].dataPoint.y + "</strong> <br/>";
                       total = e.entries[i].dataPoint.y + total;
                       str = str.concat(str1);
                   }
                   str2 = "<span style = 'color:DodgerBlue; '><strong>" + e.entries[0].dataPoint.label + "</strong></span><br/>";
                   str3 = "<span style = 'color:Tomato '>Total: </span><strong>" + total + "</strong><br/>";

                   return (str2.concat(str)).concat(str3);
               }

           },
           data: [
           {
               type: "bar",
               showInLegend: true,
               name: "Gold",
               color: "gold",
               dataPoints: <%out.print(dataPoints1);%>
           },
           {
               type: "bar",
               showInLegend: true,
               name: "Silver",
               color: "silver",
               dataPoints: <%out.print(dataPoints2);%>
           },
           {
               type: "bar",
               showInLegend: true,
               name: "Bronze",
               color: "#A57164",
               dataPoints: <%out.print(dataPoints3);%>
           }

           ]
       });

       chart.render();
   });
</script>