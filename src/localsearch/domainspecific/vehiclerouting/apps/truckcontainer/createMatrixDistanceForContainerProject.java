package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.google.gson.Gson;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ConfigParam;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Container;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotContainer;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotMooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotTruck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DistanceElement;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Mooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.MoocGroup;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Port;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ShipCompany;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Truck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Warehouse;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.WarehouseTransportRequest;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;

public class createMatrixDistanceForContainerProject {
	//int[][] matrix;
	int n = 21;//tong so logical point
	int[] mark;//danh dau cac diem cua cung 1 dia chi vat ly,
				//cac diem thuoc cung group chua chac da cung diem vat ly
	HashMap<Integer, Integer> group2group;//chia thanh cac group de tinh toan khoang cach
	HashMap<String, Integer> idx2value;//idx la cac diem co cung dia chi vat ly
							//khoang cach di/den cac diem co cung dia chi vat ly la giong nhau
	
	
//	ArrayList<Integer> depotTrucks1;
//	ArrayList<Integer> depotTrucks2;
//	ArrayList<Integer> depotContainers1;
//	ArrayList<Integer> depotContainers2;
//	ArrayList<Integer> depotContainers3;
//	ArrayList<Integer> depotTrailers1;
//	ArrayList<Integer> depotTrailers2;
//	
//	ArrayList<Integer> returnContainers1;
//	ArrayList<Integer> returnContainers2;
//	ArrayList<Integer> returnContainers3;
//	
//	ArrayList<Integer> warehouses1;
//	ArrayList<Integer> warehouses2;
//	ArrayList<Integer> warehouses3;
//	ArrayList<Integer> warehouses4;
//	ArrayList<Integer> warehouses5;
//	ArrayList<Integer> warehouses6;
//	ArrayList<Integer> ports;
//	ArrayList<Integer> ports2;
//	ArrayList<Integer> ports3;
//	int[][] matrix;
//	int n = 21;
//	int[] mark;
//	HashMap<Integer, Integer> group2group;
//	HashMap<String, Integer> idx2value;
//	
//	//1EE-1IL-1IE-1EL-2T-2M-2C
//	public createMatrixDistanceForContainerProject(){
//		depotTrucks1 = new ArrayList<Integer>();
//		depotTrucks2 = new ArrayList<Integer>();
//		depotTrucks1.add(1);
//		depotTrucks1.add(5);
//		depotTrucks2.add(11);
//		depotTrucks2.add(12);
//		
//		depotTrailers1 = new ArrayList<Integer>();
//		depotTrailers2 = new ArrayList<Integer>();
//		depotTrailers1.add(2);
//		depotTrailers1.add(6);
//		depotTrailers2.add(8);
//		depotTrailers2.add(9);
//		
//		depotContainers1 = new ArrayList<Integer>();
//		depotContainers2 = new ArrayList<Integer>();
//		depotContainers1.add(3);
//		depotContainers1.add(7);
//		depotContainers2.add(10);
//		depotContainers2.add(13);
//		
//		returnContainers1 = new ArrayList<Integer>();
//		returnContainers2 = new ArrayList<Integer>();
//		returnContainers3 = new ArrayList<Integer>();
//		returnContainers1.add(17);
//		returnContainers2.add(18);
//		returnContainers3.add(19);
//		
//		warehouses1 = new ArrayList<Integer>();
//		warehouses1.add(4);
//		//warehouses1.add(16);
//		warehouses2 = new ArrayList<Integer>();
//		warehouses2.add(14);
//		
//		warehouses3 = new ArrayList<Integer>();
//		warehouses3.add(16);
//		
//		warehouses4 = new ArrayList<Integer>();
//		warehouses4.add(20);
//		
//		ports = new ArrayList<Integer>();
//		ports.add(0);
//		
//		ports2 = new ArrayList<Integer>();
//		ports2.add(15);
//		
//		mark = new int[n];
//		group2group = new HashMap<Integer, Integer>();
//		
//		for(int i = 0; i < n; i++)
//			mark[i] = 0;
//		int t = 0;
//		for(int i = 0; i < depotTrucks1.size(); i++){
//			mark[depotTrucks1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrucks2.size(); i++){
//			mark[depotTrucks2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers1.size(); i++){
//			mark[depotTrailers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers2.size(); i++){
//			mark[depotTrailers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotContainers1.size(); i++){
//			mark[depotContainers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotContainers2.size(); i++){
//			mark[depotContainers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers1.size(); i++){
//			mark[returnContainers1.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers2.size(); i++){
//			mark[returnContainers2.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers3.size(); i++){
//			mark[returnContainers3.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < warehouses1.size(); i++){
//			mark[warehouses1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < warehouses2.size(); i++){
//			mark[warehouses2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < warehouses3.size(); i++){
//			mark[warehouses3.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		for(int i = 0; i < warehouses4.size(); i++){
//			mark[warehouses4.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		for(int i = 0; i < ports.size(); i++){
//			mark[ports.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < ports2.size(); i++){
//			mark[ports2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		idx2value = new HashMap<String, Integer>();
//	}
	
//	//1EE-1IL-1IE-2T-2M-2C
//	public createMatrixDistanceForContainerProject(){
//		depotTrucks1 = new ArrayList<Integer>();
//		depotTrucks2 = new ArrayList<Integer>();
//		depotTrucks1.add(1);
//		depotTrucks1.add(5);
//		depotTrucks2.add(11);
//		depotTrucks2.add(12);
//		
//		depotTrailers1 = new ArrayList<Integer>();
//		depotTrailers2 = new ArrayList<Integer>();
//		depotTrailers1.add(2);
//		depotTrailers1.add(6);
//		depotTrailers2.add(8);
//		depotTrailers2.add(9);
//		
//		depotContainers1 = new ArrayList<Integer>();
//		depotContainers2 = new ArrayList<Integer>();
//		depotContainers1.add(3);
//		depotContainers1.add(7);
//		depotContainers2.add(10);
//		depotContainers2.add(13);
//		
//		returnContainers1 = new ArrayList<Integer>();
//		returnContainers2 = new ArrayList<Integer>();
//		returnContainers3 = new ArrayList<Integer>();
//		returnContainers1.add(17);
//		returnContainers2.add(18);
//		returnContainers3.add(19);
//		
//		warehouses1 = new ArrayList<Integer>();
//		warehouses1.add(4);
//		//warehouses1.add(16);
//		warehouses2 = new ArrayList<Integer>();
//		warehouses2.add(14);
//		
//		warehouses3 = new ArrayList<Integer>();
//		warehouses3.add(16);
//		
//		ports = new ArrayList<Integer>();
//		ports.add(0);
//		
//		mark = new int[n];
//		group2group = new HashMap<Integer, Integer>();
//		
//		for(int i = 0; i < n; i++)
//			mark[i] = 0;
//		int t = 0;
//		for(int i = 0; i < depotTrucks1.size(); i++){
//			mark[depotTrucks1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrucks2.size(); i++){
//			mark[depotTrucks2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers1.size(); i++){
//			mark[depotTrailers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers2.size(); i++){
//			mark[depotTrailers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotContainers1.size(); i++){
//			mark[depotContainers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotContainers2.size(); i++){
//			mark[depotContainers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers1.size(); i++){
//			mark[returnContainers1.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers2.size(); i++){
//			mark[returnContainers2.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < returnContainers3.size(); i++){
//			mark[returnContainers3.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		
//		for(int i = 0; i < warehouses1.size(); i++){
//			mark[warehouses1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < warehouses2.size(); i++){
//			mark[warehouses2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < warehouses3.size(); i++){
//			mark[warehouses3.get(i)] = t;
//			group2group.put(t, 3);
//		}
//		t++;
//		for(int i = 0; i < ports.size(); i++){
//			mark[ports.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		idx2value = new HashMap<String, Integer>();
//	}
	
//	//1EE-1IL-2T-2M-2C
//	public createMatrixDistanceForContainerProject(){
//		depotTrucks1 = new ArrayList<Integer>();
//		depotTrucks2 = new ArrayList<Integer>();
//		depotTrucks1.add(1);
//		depotTrucks1.add(5);
//		depotTrucks2.add(11);
//		depotTrucks2.add(12);
//		
//		depotTrailers1 = new ArrayList<Integer>();
//		depotTrailers2 = new ArrayList<Integer>();
//		depotTrailers1.add(2);
//		depotTrailers1.add(6);
//		depotTrailers2.add(8);
//		depotTrailers2.add(9);
//		
//		depotContainers1 = new ArrayList<Integer>();
//		depotContainers2 = new ArrayList<Integer>();
//		depotContainers1.add(3);
//		depotContainers1.add(7);
//		depotContainers2.add(10);
//		depotContainers2.add(13);
//		
//		warehouses1 = new ArrayList<Integer>();
//		warehouses1.add(4);
//		//warehouses1.add(16);
//		warehouses2 = new ArrayList<Integer>();
//		warehouses2.add(14);
//		
//		ports = new ArrayList<Integer>();
//		ports.add(0);
//		
//		mark = new int[n];
//		group2group = new HashMap<Integer, Integer>();
//		
//		for(int i = 0; i < n; i++)
//			mark[i] = 0;
//		int t = 0;
//		for(int i = 0; i < depotTrucks1.size(); i++){
//			mark[depotTrucks1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrucks2.size(); i++){
//			mark[depotTrucks2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers1.size(); i++){
//			mark[depotTrailers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers2.size(); i++){
//			mark[depotTrailers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotContainers1.size(); i++){
//			mark[depotContainers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotContainers2.size(); i++){
//			mark[depotContainers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < warehouses1.size(); i++){
//			mark[warehouses1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < warehouses2.size(); i++){
//			mark[warehouses2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < ports.size(); i++){
//			mark[ports.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		idx2value = new HashMap<String, Integer>();
//	}
	
//	//3EE-3IL-2T-2M3C
//	public createMatrixDistanceForContainerProject(){
//		depotTrucks1 = new ArrayList<Integer>();
//		depotTrucks2 = new ArrayList<Integer>();
//		depotTrucks1.add(1);
//		depotTrucks1.add(5);
//		depotTrucks2.add(11);
//		depotTrucks2.add(12);
//		
//		depotTrailers1 = new ArrayList<Integer>();
//		depotTrailers2 = new ArrayList<Integer>();
//		depotTrailers1.add(2);
//		depotTrailers1.add(6);
//		depotTrailers2.add(8);
//		depotTrailers2.add(9);
//		
//		depotContainers1 = new ArrayList<Integer>();
//		depotContainers2 = new ArrayList<Integer>();
//		depotContainers3 = new ArrayList<Integer>();
//		depotContainers1.add(3);
//		depotContainers1.add(7);
//		depotContainers2.add(10);
//		depotContainers2.add(13);
//		depotContainers3.add(21);
//		depotContainers3.add(22);
//		
//		warehouses1 = new ArrayList<Integer>();
//		warehouses1.add(4);
//		//warehouses1.add(16);
//		warehouses2 = new ArrayList<Integer>();
//		warehouses2.add(14);
//		warehouses3 = new ArrayList<Integer>();
//		warehouses3.add(15);
//		warehouses4 = new ArrayList<Integer>();
//		warehouses4.add(17);
//		warehouses5 = new ArrayList<Integer>();
//		warehouses5.add(18);
//		warehouses6 = new ArrayList<Integer>();
//		warehouses6.add(20);
//		
//		ports = new ArrayList<Integer>();
//		ports.add(0);
//		ports2 = new ArrayList<Integer>();
//		ports2.add(16);
//		ports3 = new ArrayList<Integer>();
//		ports3.add(19);
//		//ports.add(15);
//		
//		mark = new int[n];
//		group2group = new HashMap<Integer, Integer>();
//		
//		for(int i = 0; i < n; i++)
//			mark[i] = 0;
//		int t = 0;
//		for(int i = 0; i < depotTrucks1.size(); i++){
//			mark[depotTrucks1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrucks2.size(); i++){
//			mark[depotTrucks2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers1.size(); i++){
//			mark[depotTrailers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotTrailers2.size(); i++){
//			mark[depotTrailers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < depotContainers1.size(); i++){
//			mark[depotContainers1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < depotContainers2.size(); i++){
//			mark[depotContainers2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < warehouses1.size(); i++){
//			mark[warehouses1.get(i)] = t;
//			group2group.put(t, 1);
//		}
//		t++;
//		for(int i = 0; i < warehouses2.size(); i++){
//			mark[warehouses2.get(i)] = t;
//			group2group.put(t, 2);
//		}
//		t++;
//		for(int i = 0; i < warehouses3.size(); i++)
//			mark[warehouses3.get(i)] = t;
//		t++;
//		for(int i = 0; i < warehouses4.size(); i++)
//			mark[warehouses4.get(i)] = t;
//		t++;
//		for(int i = 0; i < ports.size(); i++)
//			mark[ports.get(i)] = t;
//		t++;
//		for(int i = 0; i < ports2.size(); i++)
//			mark[ports2.get(i)] = t;
//		idx2value = new HashMap<String, Integer>();
//	}
//	//tao matrix cho n diem du lieu
//	public void createMatrix(){
//		matrix = new int[n][n];
//		for(int i = 0; i < n; i++)
//			for(int j = 0; j < n; j++)
//				matrix[i][j] = 0;
//		Random r = new Random();
//		for(int i = 0; i < n; i++){
//			for(int j = 0; j < n; j++){
//				if(mark[i] != mark[j]){
//					String str = mark[i] + "-" + mark[j];
//					if(idx2value.get(str) == null){
//						matrix[i][j] = r.nextInt(10) + 2;
//						idx2value.put(str, matrix[i][j]);
//					}
//					else
//						matrix[i][j] = idx2value.get(str);
//				}
//			}
//		}
//		try{
//			PrintWriter out = new PrintWriter(new File("E:/Project/smartlog/doc/documents/matrix.txt"));
//			out.println(n*(n-1));
//			for(int i = 0; i < n; i++){
//				for(int j = 0; j < n; j++){
//					if(i != j)
//						out.println(i + " " + j + " " + matrix[i][j]);
//				}
//			}
//			out.close();
//		}catch(Exception e){
//			
//		}
//		
//	}
//	
//	//tao matrix theo group, cac diem trong group se gan nhau,
//	//giua 2 group se xa nhau
//	public void createMatrixForTwoRoutes(){
//		matrix = new int[n][n];
//		for(int i = 0; i < n; i++)
//			for(int j = 0; j < n; j++)
//				matrix[i][j] = 0;
//		Random r = new Random();
//		for(int i = 0; i < n; i++){
//			for(int j = 0; j < n; j++){
//				if(mark[i] != mark[j]){
//					String str = mark[i] + "-" + mark[j];
//					if(idx2value.get(str) == null){
//						if(group2group.get(mark[i]) == group2group.get(mark[j]))
//							matrix[i][j] = r.nextInt(5) + 2;
//						else
//							matrix[i][j] = r.nextInt(20) + 20;
//						idx2value.put(str, matrix[i][j]);
//					}
//					else
//						matrix[i][j] = idx2value.get(str);
//				}
//			}
//		}
//		try{
//			PrintWriter out = new PrintWriter(new File("E:/Project/smartlog/doc/documents/matrix.txt"));
//			out.println(n*(n-1));
//			for(int i = 0; i < n; i++){
//				for(int j = 0; j < n; j++){
//					if(i != j)
//						out.println(i + " " + j + " " + matrix[i][j]);
//				}
//			}
//			out.close();
//		}catch(Exception e){
//			
//		}
//		
//	}
	
//	public static void main(String[] args){
//		createMatrixDistanceForContainerProject c = new createMatrixDistanceForContainerProject();
//		c.createMatrix();
//	}
}
