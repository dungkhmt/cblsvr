<%@ page import="java.util.*" %>
<%@page import="com.google.gson.Gson"%>
<%@page import="com.google.gson.JsonObject"%>

<jsp:include page="../WEB-INF/pages/template.jsp">
	<jsp:param value="Stacked Column Chart" name="pageTitle" />
	<jsp:param value="<div id='chartContainer'/>" name="content" />
</jsp:include>

<%
Gson gsonObj = new Gson();
Map<Object,Object> map = null;
List<Map<Object,Object>> list = new ArrayList<Map<Object,Object>>();

map = new HashMap<Object,Object>(); map.put("y", 111338);  map.put("label", "USA"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 49088);  map.put("label", "Russia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 62200);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 90085);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 38600);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 48750);  map.put("label", "SA"); list.add(map);	
String dataPoints1 = gsonObj.toJson(list);

list = new ArrayList<Map<Object,Object>>();
map = new HashMap<Object,Object>(); map.put("y", 135305);  map.put("label", "USA"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 107922);  map.put("label", "Russia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 52300);  map.put("label", "China"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 3360);  map.put("label", "India"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 39900);  map.put("label", "Australia"); list.add(map);
map = new HashMap<Object,Object>(); map.put("y", 0);  map.put("label", "SA"); list.add(map);	
String dataPoints2 = gsonObj.toJson(list);
%>

<script type="text/javascript">

   $(function () {
       var chart = new CanvasJS.Chart("chartContainer", {
           animationEnabled: true,
           title: {
               text: "Coal Reserves of Countries"
           },
           axisY: {
               title: "Coal (bn tonnes)",
               valueFormatString: "#0.#,.",
           },
           data: [
           {
               type: "stackedColumn",
               legendText: "Anthracite & Bituminous",
               yValueFormatString: "#0.#,.",
               showInLegend: "true",
               dataPoints: <%out.print(dataPoints1);%>
           }, 
           {
               type: "stackedColumn",
               legendText: "SubBituminous & Lignite",
               showInLegend: "true",
               indexLabel: "#total bn",
               yValueFormatString: "#0.#,.",
               indexLabelFormatString: "#0.#,.",
               indexLabelPlacement: "outside",
               dataPoints: <%out.print(dataPoints2);%>
           }
           ]
       });
       chart.render();
   });
</script>