package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.utils.ScannerInput;
import gurobi.*;

final class SubSetGeneratorTest40ft{
	private Set<Integer> S;
	public ArrayList<Integer> map;
	private int n;
	private ArrayList<Integer> X;
	public HashSet<HashSet<Integer>> subSet;
	
	public SubSetGeneratorTest40ft(Set<Integer> S){
		this.S = S;
		map = new ArrayList<Integer>();
		X = new ArrayList<Integer>();
		subSet = new HashSet<HashSet<Integer>>();
		
		this.n = S.size();
		
		for(int e : S){
			map.add(e);
			X.add(0);
		}
		
		
	}
	
	public void solution(){
		HashSet<Integer> s = new HashSet<Integer>();
		for(int i = 0; i < X.size(); i++){
			if(X.get(i) == 1)
				s.add(map.get(i));
		}
		subSet.add(s);
	}
	
	public void TRY(int k){
		for(int v = 0; v < 2; v++){
			X.set(k, v);
			if(k == n - 1)
				solution();
			else
				TRY(k+1);
		}
	}
	
	public void generate(){
		TRY(0);
	}
}

public class MIPModelTest40ft{
	Set<Integer> logicalPoints;
	
	Set<Integer> intermediateTruckPoints;
	Set<Integer> intermediateTrailerPoints;
	Set<Integer> intermediateContainerPoints;
	Set<Integer> truckPoints;
	Set<Integer> trailerPoints;
	Set<Integer> containerPoints;
	
	
	int nbTrucks;
	int nbTrailers;
	int nbContainers;
	int nbReturnedContainers;
	int nbExportEmpty;
	int nbExportLaden;
	int nbImportEmpty;
	int nbImportLaden;
	int nbLogicalPoints;
	
	ArrayList<Integer> depotTrucks;
	ArrayList<Integer> terminateTrucks;
	int[] depotTrailers;
	int[] terminateTrailers;
	int[] depotContainers;
	//int[] terminateContainers;
	int[] returnedContainerDepots;
	int[] warehouseEE;
	int[] EEbreakRomooc;
	int[] warehouseIE;
	int[] warehouseEL;
	int[] portEL;
	int[] ELbreakRomooc;
	int[] warehouseIL;
	int[] portIL;
	int[] ILbreakRomooc;
	
	int[] early;
	int[] late;
	int[] ser;
	
	int[][] distances;
	
	HashSet<ArrayList<Integer>> arcTrucks;
	HashMap<Integer, HashSet<Integer>> inArcTruck;
	HashMap<Integer, HashSet<Integer>> outArcTruck;
	
	ArrayList<GRBVar> X;
	HashMap<String, GRBVar> arc2X;
	
//	ArrayList<GRBVar> varTL;
//	ArrayList<GRBVar> varCL;
	
	HashMap<Integer, GRBVar> point2varTL;
	HashMap<Integer, GRBVar> point2varCL;
	
	HashMap<Integer, GRBVar> point2varPlus;
	HashMap<Integer, GRBVar> point2varMinus;
	
	ArrayList<GRBVar> varEECL;
	HashMap<Integer, GRBVar> point2varEECL;
	ArrayList<GRBVar> varIECL;
	HashMap<Integer, GRBVar> point2varIECL;
	
	ArrayList<GRBVar> waitingTime;
	HashMap<Integer, GRBVar> point2WaitingTime;
	
	ArrayList<GRBVar> arrivalTime;
	ArrayList<GRBVar> departureTime;
	HashMap<Integer, GRBVar> point2ArrivalTime;
	HashMap<Integer, GRBVar> point2DepartureTime;
	
	HashMap<Integer, Integer> trailerAt;
	HashMap<Integer, Integer> containerAt;
	HashMap<Integer, Integer> eeContainerAt;
	HashMap<Integer, Integer> ieContainerAt;
	HashMap<Integer, Integer> typeOfContainerAt;
	HashMap<Integer, Integer> plusOrminusContainerAt;
	
	GRBVar y0;
	GRBVar y1;
	
	int a = 100;
	int M = 100000;
	
	
	public MIPModelTest40ft(){
	}
	
	public void readData(String fn){
		logicalPoints = new HashSet<Integer>();
		try{
			Scanner sc = new Scanner(new File(fn));
			while(sc.hasNextLine()){
				System.out.println(sc.nextLine());
				nbTrucks = Integer.parseInt(sc.nextLine());
			
				sc.nextLine();
				intermediateTruckPoints = new HashSet<Integer>();
				truckPoints = new HashSet<Integer>();
				
				String[] intermediate = sc.nextLine().split(" ");
				for(int i = 0; i < intermediate.length; i++){
					int p = Integer.parseInt(intermediate[i]);
					intermediateTruckPoints.add(p);
					truckPoints.add(p);
					logicalPoints.add(p);
				}
				
				sc.nextLine();
				depotTrucks = new ArrayList<Integer>();
				terminateTrucks = new ArrayList<Integer>();
				for(int i = 0; i < nbTrucks; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					depotTrucks.add(p);
					logicalPoints.add(p);
					truckPoints.add(p);
					p = Integer.parseInt(str[1]);
					terminateTrucks.add(p);
					logicalPoints.add(p);
					truckPoints.add(p);
				}
				
				trailerPoints = new HashSet<Integer>();
				intermediateTrailerPoints = new HashSet<Integer>();
				
				sc.nextLine();
				nbTrailers = Integer.parseInt(sc.nextLine());
			
				sc.nextLine();
				
				intermediate = sc.nextLine().split(" ");
				for(int i = 0; i < intermediate.length; i++){
					int p = Integer.parseInt(intermediate[i]);
					intermediateTrailerPoints.add(p);
					trailerPoints.add(p);
					logicalPoints.add(p);
				}
				
				depotTrailers = new int[nbTrailers];
				terminateTrailers = new int[nbTrailers];
				sc.nextLine();
				for(int i = 0; i < nbTrailers; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					depotTrailers[i] = p;
					logicalPoints.add(p);
					trailerPoints.add(p);
					p = Integer.parseInt(str[1]);
					terminateTrailers[i] = p;
					logicalPoints.add(p);
					trailerPoints.add(p);
				}
				
				containerPoints = new HashSet<Integer>();
				intermediateContainerPoints = new HashSet<Integer>();
				typeOfContainerAt = new HashMap<Integer, Integer>();
				
				sc.nextLine();
				nbContainers = Integer.parseInt(sc.nextLine());
			
				sc.nextLine();
				
				intermediate = sc.nextLine().split(" ");
				for(int i = 0; i < intermediate.length; i++){
					int p = Integer.parseInt(intermediate[i]);
					intermediateContainerPoints.add(p);
					containerPoints.add(p);
					logicalPoints.add(p);
				}
				
				depotContainers = new int[nbContainers];
				//terminateContainers = new int[nbContainers];
				sc.nextLine();
				for(int i = 0; i < nbContainers; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					//int p = Integer.parseInt(sc.nextLine());
					depotContainers[i] = p;
					logicalPoints.add(p);
					containerPoints.add(p);
					int type = Integer.parseInt(str[1]);
					typeOfContainerAt.put(p, type);
					
//						p = Integer.parseInt(str[1]);
//						terminateContainers[i] = p;
//						logicalPoints.add(p);
//						containerPoints.add(p);
				}
				
				//read returned containers
				sc.nextLine();
				nbReturnedContainers = Integer.parseInt(sc.nextLine());
				
				sc.nextLine();
				
				returnedContainerDepots = new int[nbReturnedContainers];
				for(int i = 0; i < nbReturnedContainers; i++){
					intermediate = sc.nextLine().split(" ");
					int p = Integer.parseInt(intermediate[0]);
					returnedContainerDepots[i] = p;
					intermediateContainerPoints.add(p);
					containerPoints.add(p);
					logicalPoints.add(p);
					int type = Integer.parseInt(intermediate[1]);
					typeOfContainerAt.put(p, type);
				}
				
				//read export empty
				sc.nextLine();
				nbExportEmpty = Integer.parseInt(sc.nextLine());
				
				warehouseEE = new int[nbExportEmpty];
				EEbreakRomooc = new int[nbExportEmpty];
				for(int i = 0; i < nbExportEmpty; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					warehouseEE[i] = p;
					logicalPoints.add(p);
					EEbreakRomooc[i] = Integer.parseInt(str[1]);
					int type = Integer.parseInt(str[2]);
					typeOfContainerAt.put(p, type);
				}
				
				sc.nextLine();
				nbImportEmpty = Integer.parseInt(sc.nextLine());
				
				warehouseIE = new int[nbImportEmpty];
				for(int i = 0; i < nbImportEmpty; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					warehouseIE[i] = p;
					logicalPoints.add(p);
					int type = Integer.parseInt(str[1]);
					typeOfContainerAt.put(p, type);
				}
				
				sc.nextLine();
				nbExportLaden = Integer.parseInt(sc.nextLine());
				
				warehouseEL = new int[nbExportLaden];
				portEL = new int[nbExportLaden];
				ELbreakRomooc = new int[nbExportLaden];
				for(int i = 0; i < nbExportLaden; i++){
					String[] str = sc.nextLine().split(" ");
					
					int type = Integer.parseInt(str[3]);
					
					int p = Integer.parseInt(str[0]);
					warehouseEL[i] = p;
					logicalPoints.add(p);
					typeOfContainerAt.put(p, type);
					p = Integer.parseInt(str[1]);
					portEL[i] = p;
					logicalPoints.add(p);
					ELbreakRomooc[i] = Integer.parseInt(str[2]);
					typeOfContainerAt.put(p, type);
				}
				
				sc.nextLine();
				nbImportLaden = Integer.parseInt(sc.nextLine());
				
				warehouseIL = new int[nbImportLaden];
				portIL = new int[nbImportLaden];
				ILbreakRomooc = new int[nbImportLaden];
				for(int i = 0; i < nbImportLaden; i++){
					String[] str = sc.nextLine().split(" ");
					int type = Integer.parseInt(str[3]);
					int p = Integer.parseInt(str[0]);
					portIL[i] = p;
					logicalPoints.add(p);
					typeOfContainerAt.put(p, type);
					p = Integer.parseInt(str[1]);
					warehouseIL[i] = p;
					logicalPoints.add(p);
					ILbreakRomooc[i] = Integer.parseInt(str[2]);
					typeOfContainerAt.put(p, type);
				}
				
				sc.nextLine();
				nbLogicalPoints = Integer.parseInt(sc.nextLine());
				
				sc.nextLine();
				
				early = new int[nbLogicalPoints];
				late = new int[nbLogicalPoints];
				ser = new int[nbLogicalPoints];
				for(int i = 0; i < nbLogicalPoints; i++){
					String[] str = sc.nextLine().split(" ");
					int p = Integer.parseInt(str[0]);
					early[p] = Integer.parseInt(str[1]);
					late[p] = Integer.parseInt(str[2]);
					ser[p] = Integer.parseInt(str[3]);
				}
				
				sc.nextLine();
				
				distances = new int[nbLogicalPoints][nbLogicalPoints];
				for(int i = 0; i < nbLogicalPoints; i++)
					for(int j = 0; j < nbLogicalPoints; j++)
						distances[i][j] = 0;
				int nLines = Integer.parseInt(sc.nextLine());
				for(int i = 0; i < nLines; i++){
					String[] str = sc.nextLine().split(" ");
					int f = Integer.parseInt(str[0]);
					int t = Integer.parseInt(str[1]);
					distances[f][t] = Integer.parseInt(str[2]);
				}
			}
			sc.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		createArcTrucks();
	}
	
	public void createArcTrucks(){
		arcTrucks = new HashSet<ArrayList<Integer>>();
		inArcTruck = new HashMap<Integer, HashSet<Integer>>();
		outArcTruck = new HashMap<Integer, HashSet<Integer>>();
		
		for(int u : intermediateTruckPoints)
			for(int v : intermediateTruckPoints)
				if(u != v){
					ArrayList<Integer> arc = new ArrayList<Integer>();
					arc.add(u);
					arc.add(v);
					arcTrucks.add(arc);
				}
		for(int i = 0; i < nbTrucks; i++){
			int d = depotTrucks.get(i);
			int t = terminateTrucks.get(i);
			for(int v : intermediateTruckPoints){
				ArrayList<Integer> arc = new ArrayList<Integer>();
				arc.add(d);
				arc.add(v);
				arcTrucks.add(arc);
				arc = new ArrayList<Integer>();
				arc.add(v);
				arc.add(t);
				arcTrucks.add(arc);
			}
			ArrayList<Integer> arc = new ArrayList<Integer>();
			arc.add(d);
			arc.add(t);
			arcTrucks.add(arc);
		}
		
		for(int v : truckPoints){
			inArcTruck.put(v, new HashSet<Integer>());
			outArcTruck.put(v, new HashSet<Integer>());
		}

		for(ArrayList<Integer> arc : arcTrucks){
			HashSet<Integer> outKey = outArcTruck.get(arc.get(0));
			outKey.add(arc.get(1));
			outArcTruck.put(arc.get(0), outKey);
			HashSet<Integer> inVal = inArcTruck.get(arc.get(1));
			inVal.add(arc.get(0));
			inArcTruck.put(arc.get(1), inVal);
		}
		
		
//		System.out.println("arcTrucks:");
//		for(ArrayList<Integer> arc : arcTrucks)
//			System.out.println(arc.get(0) + "-" + arc.get(1));
//		System.out.println("inArcTruck:");
//		for(int key : inArcTruck.keySet())
//			System.out.println(key + "-" + inArcTruck.get(key).toString());
//		System.out.println("outArcTruck:");
//		for(int key : outArcTruck.keySet())
//			System.out.println(key + "-" + outArcTruck.get(key).toString());
	}
	
	public void defineFlowVariableOfTrucks(GRBModel model){
		X = new ArrayList<GRBVar>();
		arc2X = new HashMap<String, GRBVar>();
		
//		System.out.println("=====defineFlowVariableOfTrucks=====");
		for(int k = 0; k < nbTrucks; k++){
			for(ArrayList<Integer> arc : arcTrucks){
				int key = arc.get(0);
				int value = arc.get(1);
				if(depotTrucks.contains(key) && key != depotTrucks.get(k))
					continue;
				if(terminateTrucks.contains(value) && value != terminateTrucks.get(k))
					continue;
				try {
					GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "X(" + k + "," + key + "," + value + ")");
					X.add(var);
					String s = k + "-" + key + "-" + value;
					arc2X.put(s, var);
//					System.out.println(s);
				} catch (GRBException e) {
					System.out.println("Error code: " + e.getErrorCode() + ". " +
				            e.getMessage());
				}
			}
		}
	}
	
	public void defineVariableLoadTrailer(GRBModel model){
		point2varTL = new HashMap<Integer, GRBVar>();
		
		for(int v : truckPoints){
			try{
				GRBVar var = model.addVar(0.0, nbTrailers*a + nbTrailers * (nbTrailers + 1) / 2, 0.0, GRB.INTEGER, "TL(" + v + ")");
				point2varTL.put(v, var);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}
	
	public void defineVariableLoadContainer(GRBModel model){
		point2varCL = new HashMap<Integer, GRBVar>();
		
		int maxContainerLoad = nbContainers + nbReturnedContainers;
		for(int v : truckPoints){
			try{
				GRBVar var = model.addVar(0.0, maxContainerLoad, 0.0, GRB.INTEGER, "CL(" + v + ")");
				point2varCL.put(v, var);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}

	public void defineVariableLoadExportEmptyContainer(GRBModel model){
		varEECL = new ArrayList<GRBVar>();
		point2varEECL = new HashMap<Integer, GRBVar>();
		
		int maxContainerLoad = nbContainers;
		for(int v : truckPoints){
			try{
				GRBVar var = model.addVar(0.0, maxContainerLoad, 0.0, GRB.INTEGER, "EECL(" + v + ")");
				varEECL.add(var);
				point2varEECL.put(v, var);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}
	
	public void defineVariableLoadImportEmptyContainer(GRBModel model){
		varIECL = new ArrayList<GRBVar>();
		point2varIECL = new HashMap<Integer, GRBVar>();
		
		int maxContainerLoad = nbReturnedContainers;
		for(int v : truckPoints){
			try{
				GRBVar var = model.addVar(0.0, maxContainerLoad, 0.0, GRB.INTEGER, "IECL(" + v + ")");
				varIECL.add(var);
				point2varIECL.put(v, var);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}
	
	public void defineMaxWaitingTimeVariables(GRBModel model){
		waitingTime = new ArrayList<GRBVar>();
		point2WaitingTime = new HashMap<Integer, GRBVar>();
		
		int maxTime = 10000;
		for(int v : truckPoints){
			try{
				GRBVar var = model.addVar(0.0, maxTime, 0.0, GRB.INTEGER, "waitTime(" + v + ")");
				waitingTime.add(var);
				point2WaitingTime.put(v, var);
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}
	
	public void defineArrivalDepartureTimeVariables(GRBModel model){
		arrivalTime = new ArrayList<GRBVar>();
		departureTime = new ArrayList<GRBVar>();
		point2ArrivalTime = new HashMap<Integer, GRBVar>();
		point2DepartureTime = new HashMap<Integer, GRBVar>();
		
		int maxTime = 10000;
		for(int v : truckPoints){
			try{
				GRBVar var1 = model.addVar(0.0, maxTime, 0.0, GRB.INTEGER, "arrTime(" + v + ")");
				arrivalTime.add(var1);
				point2ArrivalTime.put(v, var1);
				
				GRBVar var2 = model.addVar(0.0, maxTime, 0.0, GRB.INTEGER, "depTime(" + v + ")");
				departureTime.add(var2);
				point2DepartureTime.put(v, var2);
				
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}
	}
	
	public void defineWeightExportEmptyContainer(){
		eeContainerAt = new HashMap<Integer, Integer>();
		for(int v : truckPoints)
			eeContainerAt.put(v, 0);
		for(int i = 0; i < nbContainers; i++){
			int d = depotContainers[i];
			eeContainerAt.put(d, 1);
		}
		for(int i = 0; i < nbExportEmpty; i++){
			int w = warehouseEE[i];
			eeContainerAt.put(w, -1);
		}
		
//		for(int key : eeContainerAt.keySet())
//			System.out.println("nb EE container at " + key + " is " + eeContainerAt.get(key));
	}
	
	public void defineWeightImportEmptyContainer(){
		ieContainerAt = new HashMap<Integer, Integer>();
		for(int v : truckPoints)
			ieContainerAt.put(v, 0);
		for(int v : returnedContainerDepots)
			ieContainerAt.put(v, -1);
		for(int i = 0; i < nbImportEmpty; i++){
			int w = warehouseIE[i];
			ieContainerAt.put(w, 1);
		}
		
//		for(int key : ieContainerAt.keySet())
//			System.out.println("nb IIIEEEE container at " + key + " is " + ieContainerAt.get(key));
	}
	
	public void defineWeightTrailer(){
		trailerAt = new HashMap<Integer, Integer>();
		for(int v : truckPoints)
			trailerAt.put(v, 0);
		
		for(int i = 0; i < nbTrailers; i++){
		   int d = depotTrailers[i];
		   trailerAt.put(d, a + i + 1);
		   int t = terminateTrailers[i];
		   trailerAt.put(t, -a - i - 1);
		}
		for(int i = 0; i < nbExportEmpty; i++){
			if(EEbreakRomooc[i] == 1){
				int w = warehouseEE[i];
				trailerAt.put(w, -nbTrailers);
			}
		}
		
		for(int i = 0; i < nbExportLaden; i++){
			if(ELbreakRomooc[i] == 1){
				int p = portEL[i];
				trailerAt.put(p, -nbTrailers);
			}
		}
		
		for(int i = 0; i < nbImportLaden; i++){
			if(ILbreakRomooc[i] == 1){
				int w = warehouseIL[i];
				trailerAt.put(w, -nbTrailers);
			}
		}
		
//		for(int key : trailerAt.keySet())
//			System.out.println("nb container at " + key + " is " + trailerAt.get(key));
		
	}
	
	public void defineWeightContainer(){
		containerAt = new HashMap<Integer, Integer>();
		for(int v : truckPoints)
			containerAt.put(v, 0);
		for(int i = 0; i < nbContainers; i++){
			int d = depotContainers[i];
			containerAt.put(d, 1);
			if(typeOfContainerAt.get(d) != null && typeOfContainerAt.get(d) == 2)
				containerAt.put(d, 2);
		}
		for(int i = 0; i < nbExportEmpty; i++){
			int w = warehouseEE[i];
			containerAt.put(w, -1);
			if(typeOfContainerAt.get(w) != null && typeOfContainerAt.get(w) == 2)
				containerAt.put(w, -2);
		}
		for(int i = 0; i < nbImportEmpty; i++){
			int w = warehouseIE[i];
			containerAt.put(w, 1);
			if(typeOfContainerAt.get(w) != null && typeOfContainerAt.get(w) == 2)
				containerAt.put(w, 2);
		}
		for(int v : returnedContainerDepots){
			containerAt.put(v, -1);
			if(typeOfContainerAt.get(v) != null && typeOfContainerAt.get(v) == 2)
				containerAt.put(v, -2);
		}
		for(int i = 0; i < nbExportLaden; i++){
			int w = warehouseEL[i];
			int p = portEL[i];
			containerAt.put(w, 1);
			if(typeOfContainerAt.get(w) != null && typeOfContainerAt.get(w) == 2)
				containerAt.put(w, 2);
			
			containerAt.put(p, -1);
			if(typeOfContainerAt.get(p) != null && typeOfContainerAt.get(p) == 2)
				containerAt.put(p, -2);
		}
		for(int i = 0; i < nbImportLaden; i++){
			int w = warehouseIL[i];
			int p = portIL[i];
			containerAt.put(w, -1);
			if(typeOfContainerAt.get(w) != null && typeOfContainerAt.get(w) == 2)
				containerAt.put(w, -2);
			
			containerAt.put(p, 1);
			if(typeOfContainerAt.get(p) != null && typeOfContainerAt.get(p) == 2)
				containerAt.put(p, 2);
		}
//		for(int key : containerAt.keySet())
//			System.out.println("nb container at " + key + " is " + containerAt.get(key));
	}
	
	public void defineBinaryVariables(GRBModel model){
		try{
			y0 = model.addVar(0.0, 1, 0.0, GRB.BINARY, "y0");			
			y1 = model.addVar(0.0, 1, 0.0, GRB.BINARY, "y1");
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineVariables(GRBModel model){
		defineFlowVariableOfTrucks(model);
		defineVariableLoadTrailer(model);
		defineVariableLoadContainer(model);
		defineVariableLoadExportEmptyContainer(model);
		defineVariableLoadImportEmptyContainer(model);
		defineArrivalDepartureTimeVariables(model);
		defineMaxWaitingTimeVariables(model);
		defineWeightContainer();
		defineWeightTrailer();
		defineWeightExportEmptyContainer();
		defineWeightImportEmptyContainer();
		defineBinaryVariables(model);
	}
	
	public void stateMode(){
		try {
			GRBEnv env   = new GRBEnv();
			GRBModel model = new GRBModel(env);
			
			// Create variables
			GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x");
			GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y");
			GRBVar z = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z");
			
			// Set objective: maximize x + y + 2 z
			GRBLinExpr expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(1.0, y); expr.addTerm(2.0, z);
			model.setObjective(expr, GRB.MAXIMIZE);
			
			// Add constraint: x + 2 y + 3 z <= 4
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(2.0, y); expr.addTerm(3.0, z);
			model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");
			
			// Add constraint: x + y >= 1
			expr = new GRBLinExpr();
			expr.addTerm(1.0, x); expr.addTerm(1.0, y);
			model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");
			
			// Optimize model
			model.optimize();
			
			System.out.println(x.get(GRB.StringAttr.VarName)
					+ " " + x.get(GRB.DoubleAttr.X));
			System.out.println(y.get(GRB.StringAttr.VarName)
					+ " " + y.get(GRB.DoubleAttr.X));
			System.out.println(z.get(GRB.StringAttr.VarName)
					+ " " + z.get(GRB.DoubleAttr.X));
			
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			
			// Dispose of model and environment
			model.dispose();
			env.dispose();
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineExportLadenPointSameRouteConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbExportLaden; i++){
				for(int q = 0; q < nbTrucks; q++){
					int p = portEL[i];
					int w = warehouseEL[i];
					GRBLinExpr expr1 = new GRBLinExpr();
					for(int v : inArcTruck.get(w)){
						String s = q + "-" + v + "-" + w;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(1, x);
					}
					
					for(int v : inArcTruck.get(p)){
						String s = q + "-" + v + "-" + p;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(-1, x);
					}
					model.addConstr(expr1, GRB.EQUAL, 0.0, "ELSameRoute1(" + i + "," + q + "," + w + "," + p + ")");
					
//					GRBLinExpr expr2 = new GRBLinExpr();
//					for(int v : inArcTruck.get(w)){
//						String s = q + "-" + v + "-" + w;
//						if(arc2X.get(s) == null)
//							continue;
//						GRBVar x = arc2X.get(s);
//						expr2.addTerm(-1, x);
//					}
//					
//					for(int v : inArcTruck.get(p)){
//						String s = q + "-" + v + "-" + p;
//						if(arc2X.get(s) == null)
//							continue;
//						GRBVar x = arc2X.get(s);
//						expr2.addTerm(1, x);
//					}
//					model.addConstr(expr2, GRB.LESS_EQUAL, 0.0, "ELSameRoute2(" + i + "," + q + "," + w + "," + p + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	
	public void defineImportLadenPointSameRouteConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbImportLaden; i++){
				for(int q = 0; q < nbTrucks; q++){
					int p = portIL[i];
					int w = warehouseIL[i];
					GRBLinExpr expr1 = new GRBLinExpr();
					for(int v : inArcTruck.get(w)){
						String s = q + "-" + v + "-" + w;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(1, x);
					}
					
					for(int v : inArcTruck.get(p)){
						String s = q + "-" + v + "-" + p;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(-1, x);
					}
					model.addConstr(expr1, GRB.EQUAL, 0.0, "ILSameRoute1(" + i + "," + q + "," + p + "," + w + ")");
					
//					GRBLinExpr expr2 = new GRBLinExpr();
//					for(int v : inArcTruck.get(w)){
//						String s = q + "-" + v + "-" + w;
//						if(arc2X.get(s) == null)
//							continue;
//						GRBVar x = arc2X.get(s);
//						expr2.addTerm(-1, x);
//					}
//					
//					for(int v : inArcTruck.get(p)){
//						String s = q + "-" + v + "-" + p;
//						if(arc2X.get(s) == null)
//							continue;
//						GRBVar x = arc2X.get(s);
//						expr2.addTerm(1, x);
//					}
//					model.addConstr(expr2, GRB.LESS_EQUAL, 0.0, "ILSameRoute2(" + i + "," + q + "," + p + "," + w + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineExportLadenServedConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbExportLaden; i++){
				int p = portEL[i];
				int w = warehouseEL[i];
				GRBLinExpr expr1 = new GRBLinExpr();
				for(int q = 0; q < nbTrucks; q++){
					for(int v : inArcTruck.get(w)){
						String s = q + "-" + v + "-" + w;
						if(arc2X.get(s) != null){
							GRBVar x = arc2X.get(s);
							expr1.addTerm(1, x);
						}
					}
				}
				
				model.addConstr(expr1, GRB.GREATER_EQUAL, 1, "ELServed(" + i + "," + w + "," + p + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineImportLadenServedConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbImportLaden; i++){
				int p = portIL[i];
				int w = warehouseIL[i];
				GRBLinExpr expr1 = new GRBLinExpr();
				for(int q = 0; q < nbTrucks; q++){
					for(int v : inArcTruck.get(p)){
						String s = q + "-" + v + "-" + p;
						if(arc2X.get(s) != null){
							GRBVar x = arc2X.get(s);
							expr1.addTerm(1, x);
						}
					}
				}
				
				model.addConstr(expr1, GRB.GREATER_EQUAL, 1, "ILServed(" + i + "," + p + "," + w + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void flowBalanceConstraint(GRBModel model){
		try{
			for(int k = 0; k < nbTrucks; k++){
				for(int v : intermediateTruckPoints){
					GRBLinExpr expr1 = new GRBLinExpr();
					for(ArrayList<Integer> arc : arcTrucks){
						int au = arc.get(0);
						int av = arc.get(1);
						String s = k + "-" + au + "-" + av;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						if(inArcTruck.get(v).contains(au))
							expr1.addTerm(1, x);
						if(outArcTruck.get(v).contains(av))
							expr1.addTerm(-1, x);
					}
					model.addConstr(expr1, GRB.EQUAL, 0.0, "Balance1(" + k + "," + v + ")");
					
//					GRBLinExpr expr2 = new GRBLinExpr();
//					for(ArrayList<Integer> arc : arcTrucks){
//						int au = arc.get(0);
//						int av = arc.get(1);
//						String s = k + "-" + au + "-" + av;
//						if(arc2X.get(s) == null)
//							continue;
//						GRBVar x = arc2X.get(s);
//						if(inArcTruck.get(v).contains(au))
//							expr2.addTerm(-1, x);
//						if(outArcTruck.get(v).contains(av))
//							expr2.addTerm(1, x);
//					}
//					model.addConstr(expr2, GRB.LESS_EQUAL, 0.0, "Balance2(" + k + "," + v + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public HashSet<ArrayList<Integer>> getArcInduced(HashSet<Integer> S, HashSet<ArrayList<Integer>> arcTrucks){
		HashSet<ArrayList<Integer>> A = new HashSet<ArrayList<Integer>>();
		for(ArrayList<Integer> arc : arcTrucks){
			int au = arc.get(0);
			int av = arc.get(1);
			if(S.contains(au) && S.contains(av)){
				ArrayList<Integer> ar = new ArrayList<Integer>();
				ar.add(au);
				ar.add(av);
				A.add(ar);
			}
		}
		return A;
	}
	
	public boolean checkExist(HashSet<ArrayList<Integer>> A, ArrayList<Integer> arc){
		for(ArrayList<Integer> e : A){
			if(e.get(0) == arc.get(0) && e.get(1) == arc.get(1))
				return true;
		}
		return false;
	}
	
	public void SubtourEliminationConstraint(GRBModel model){
		SubSetGenerator ssgen = new SubSetGenerator(intermediateTruckPoints);
		ssgen.generate();
		int t = 0;
		try{
			for(HashSet<Integer> s : ssgen.subSet){
				t++;
				HashSet<ArrayList<Integer>> A = getArcInduced(s, arcTrucks);
				int m = A.size() - 1;
				if(m <= 0)
					continue;
				for(int k = 0; k < nbTrucks; k++){
					GRBLinExpr expr = new GRBLinExpr();
					for(ArrayList<Integer> arc : arcTrucks){
						int au = arc.get(0);
						int av = arc.get(1);
						String str = k + "-" + au + "-" + av;
						if(arc2X.get(str) == null)
							continue;
						GRBVar x = arc2X.get(str);
						if(checkExist(A, arc))
							expr.addTerm(1, x);
						else
							expr.addTerm(0, x);
					}
					model.addConstr(expr, GRB.LESS_EQUAL, m, "SubTour(" + k +  "," + t + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineExportEmptyConstraintFlow(GRBModel model){
		try{
			for(int i = 0; i < nbExportEmpty; i++){
				int d = warehouseEE[i];
				GRBLinExpr expr = new GRBLinExpr();
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					for(int k = 0; k < nbTrucks; k++){
						if(av == d){
							String s = k + "-" + au + "-" + av;
							if(arc2X.get(s) == null)
								continue;
							GRBVar x = arc2X.get(s);
							expr.addTerm(1, x);
						}
					}
				}
				model.addConstr(expr, GRB.GREATER_EQUAL, 1, "EEflow(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineImportEmptyConstraintFlow(GRBModel model){
		try{
			for(int i = 0; i < nbImportEmpty; i++){
				int d = warehouseIE[i];
				GRBLinExpr expr = new GRBLinExpr();
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					for(int k = 0; k < nbTrucks; k++){
						if(av == d){
							String s = k + "-" + au + "-" + av;
							if(arc2X.get(s) == null)
								continue;
							GRBVar x = arc2X.get(s);
							expr.addTerm(1, x);
						}
					}
				}
				model.addConstr(expr, GRB.GREATER_EQUAL, 1, "IEflow(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineContainerCarriedByTrailer(GRBModel model){
		try{
			for(int v : truckPoints){
				GRBLinExpr expr = new GRBLinExpr();
				GRBVar xc = point2varCL.get(v);
				expr.addTerm(1, xc);
				GRBVar xt = point2varTL.get(v);
				expr.addTerm(-1, xt);
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "ContainerCarriedByTrailer(" + v + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void DefineExportEmptyContainerLoadConstraint(GRBModel model){
		try{
			for(int p = 0; p < nbTrucks; p++){
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					GRBLinExpr expr1 = new GRBLinExpr();
					String s = p + "-" + au + "-" + av;
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					expr1.addTerm(M, x);
				    GRBVar xu = point2varEECL.get(au);
				    GRBVar xv = point2varEECL.get(av);
				    expr1.addTerm(1, xu);
				    expr1.addTerm(-1, xv);
				    model.addConstr(expr1, GRB.LESS_EQUAL, M - eeContainerAt.get(au), "EELoadConst1(" + p + "," + au + "," + av + ")");
	
				    GRBLinExpr expr2 = new GRBLinExpr();
	
				    expr2.addTerm(-1, xu);
				    expr2.addTerm(1, xv);
				    expr2.addTerm(M, x);
				    
				    model.addConstr(expr2, GRB.LESS_EQUAL, M + eeContainerAt.get(au), "EELoadConst2(" + p + "," + au + "," + av + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
		    
	public void DefineImportEmptyContainerLoadConstraint(GRBModel model){
		try{
			for(int p = 0; p < nbTrucks; p++){
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					GRBLinExpr expr1 = new GRBLinExpr();
					String s = p + "-" + au + "-" + av;
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					expr1.addTerm(M, x);
				    GRBVar xu = point2varIECL.get(au);
				    GRBVar xv = point2varIECL.get(av);
				    expr1.addTerm(1, xu);
				    expr1.addTerm(-1, xv);
				    model.addConstr(expr1, GRB.LESS_EQUAL, M - ieContainerAt.get(au), "IELoadConst1(" + p + "," + au + "," + av + ")");
	
				    GRBLinExpr expr2 = new GRBLinExpr();
	
				    expr2.addTerm(-1, xu);
				    expr2.addTerm(1, xv);
				    expr2.addTerm(M, x);
				    
				    model.addConstr(expr2, GRB.LESS_EQUAL, M + ieContainerAt.get(au), "IELoadConst2(" + p + "," + au + "," + av + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void DefineAccumulatedTrailerLoadConstraint(GRBModel model){
		try{
			for(int p = 0; p < nbTrucks; p++){
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					GRBLinExpr expr1 = new GRBLinExpr();
					String s = p + "-" + au + "-" + av;
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					expr1.addTerm(M, x);
				    GRBVar xu = point2varTL.get(au);
				    GRBVar xv = point2varTL.get(av);
				    expr1.addTerm(1, xu);
				    expr1.addTerm(-1, xv);
				    model.addConstr(expr1, GRB.LESS_EQUAL, M - trailerAt.get(au), "TrailerAccumulateLoadConst1(" + p + "," + au + "," + av + ")");
	
				    GRBLinExpr expr2 = new GRBLinExpr();
	
				    expr2.addTerm(-1, xu);
				    expr2.addTerm(1, xv);
				    expr2.addTerm(M, x);
				    
				    model.addConstr(expr2, GRB.LESS_EQUAL, M + trailerAt.get(au), "TrailerAccumulateLoadConst2(" + p + "," + au + "," + av + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void DefineAccumulatedContainerLoadConstraint(GRBModel model){
		try{
			for(int p = 0; p < nbTrucks; p++){
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					GRBLinExpr expr1 = new GRBLinExpr();
					String s = p + "-" + au + "-" + av;
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					expr1.addTerm(M, x);
				    GRBVar xu = point2varCL.get(au);
				    GRBVar xv = point2varCL.get(av);
				    expr1.addTerm(1, xu);
				    expr1.addTerm(-1, xv);
				    model.addConstr(expr1, GRB.LESS_EQUAL, M - containerAt.get(au), "ContainerAccumulateLoadConst1(" + p + "," + au + "," + av + ")");
	
				    GRBLinExpr expr2 = new GRBLinExpr();
	
				    expr2.addTerm(-1, xu);
				    expr2.addTerm(1, xv);
				    expr2.addTerm(M, x);
				    
				    model.addConstr(expr2, GRB.LESS_EQUAL, M + containerAt.get(au), "ContainerAccumulateLoadConst2(" + p + "," + au + "," + av + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineDepartureTimeAtPointConstraint(GRBModel model){
		try{	
			for(int p : truckPoints){
				GRBVar at = point2ArrivalTime.get(p);
				GRBVar dt = point2DepartureTime.get(p);
				GRBVar wt = point2WaitingTime.get(p);
				
				GRBLinExpr expr1 = new GRBLinExpr();
				expr1.addTerm(1, at);
				expr1.addTerm(-1, dt);
				expr1.addTerm(1, wt);
				model.addConstr(expr1, GRB.EQUAL, - ser[p], "DepartureTime1(" + p + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, at);
//				expr2.addTerm(1, dt);
//				expr2.addTerm(-1, wt);
//				model.addConstr(expr2, GRB.LESS_EQUAL, ser[p], "DepartureTime2(" + p + ")");
				
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineArrivalDepartureTimeAlongArcConstraint(GRBModel model){
		try{
			for(int p = 0; p < nbTrucks; p++){
				for(ArrayList<Integer> arc : arcTrucks){
					int au = arc.get(0);
					int av = arc.get(1);
					String s = p + "-" + au + "-" + av;
					if(arc2X.get(s) == null)
						continue;
					int tij = distances[au][av];
					GRBLinExpr expr1 = new GRBLinExpr();
					GRBVar xu = point2DepartureTime.get(au);
					GRBVar xv = point2ArrivalTime.get(av);
					GRBVar x = arc2X.get(s);
					expr1.addTerm(1, xu);
					expr1.addTerm(-1, xv);
					expr1.addTerm(M, x);
					model.addConstr(expr1, GRB.LESS_EQUAL, M - tij, "ArrDepTimeLongArc1(" + p + "," + au + "," + av + ")");
					
					GRBLinExpr expr2 = new GRBLinExpr();
					expr2.addTerm(-1, xu);
					expr2.addTerm(1, xv);
					expr2.addTerm(M, x);
					model.addConstr(expr2, GRB.LESS_EQUAL, M + tij, "ArrDepTimeLongArc2(" + p + "," + au + "," + av + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void DefineArrivalTimeAtPointsConstraints(GRBModel model){
		try{	
			for(int v : truckPoints){
				GRBLinExpr expr = new GRBLinExpr();
				GRBVar atp = point2ArrivalTime.get(v);
				expr.addTerm(1, atp);
				model.addConstr(expr, GRB.LESS_EQUAL, late[v], "ArrivalTime1(" + v + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineWaitingTimeAtPointsConstraints(GRBModel model){
		try{	
			for(int v : truckPoints){
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar wt = point2WaitingTime.get(v);
				expr1.addTerm(-1, wt);
				model.addConstr(expr1, GRB.LESS_EQUAL, 0, "WaitingTime1(" + v + ")");
				
				GRBVar at = point2ArrivalTime.get(v);
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(-1, at);
				expr2.addTerm(-1, wt);
				model.addConstr(expr2, GRB.LESS_EQUAL, -early[v], "WaitingTime2(" + v + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineArrivalLessThanDepartureTimeAtPointConstraint(GRBModel model){
		try{	
			for(int p : truckPoints){
				GRBVar at = point2ArrivalTime.get(p);
				GRBVar dt = point2DepartureTime.get(p);
				
				GRBLinExpr expr1 = new GRBLinExpr();
				expr1.addTerm(1, at);
				expr1.addTerm(-1, dt);
				model.addConstr(expr1, GRB.LESS_EQUAL, 0, "ArrivalLessThanDepartureTime1(" + p + ")");
				
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void definePickupDeliveryOrderExportLadenConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbExportLaden; i++){
				int p = portEL[i];
				int w = warehouseEL[i];
				GRBLinExpr expr = new GRBLinExpr();
				GRBVar atp = point2ArrivalTime.get(p);
				GRBVar atw = point2ArrivalTime.get(w);
				expr.addTerm(-1, atp);
				expr.addTerm(1, atw);
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "PDOrderEL(" + i + ")");
			}
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void definePickupDeliveryOrderImportLadenConstraint(GRBModel model){
		try{
			for(int i = 0; i < nbImportLaden; i++){
				int p = portIL[i];
				int w = warehouseIL[i];
				GRBLinExpr expr = new GRBLinExpr();
				GRBVar atp = point2ArrivalTime.get(p);
				GRBVar atw = point2ArrivalTime.get(w);
				expr.addTerm(1, atp);
				expr.addTerm(-1, atw);
				model.addConstr(expr, GRB.LESS_EQUAL, 0, "PDOrderIL(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadTrailerAtDepotConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = depotTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varTL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadTrailerAtDepot1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadTrailerAtDepot2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadContainerAtDepotConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = depotTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varCL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadContainerAtDepot1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadContainerAtDepot2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadEEContainerAtDepotConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = depotTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varEECL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadEEContainerAtDepot1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadEEContainerAtDepot2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadIEContainerAtDepotConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = depotTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varIECL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadIEContainerAtDepot1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadIEContainerAtDepot2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadTrailerAtTerminusConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = terminateTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varTL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadTrailerAtTerminus1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadTrailerAtTerminus2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadContainerAtTerminusConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = terminateTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varCL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadContainerAtTerminus1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadContainerAtTerminus2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadIEContainerAtTerminusConstraint(GRBModel model){		
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = terminateTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varIECL.get(d);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.EQUAL, 0, "LoadIEContainerAtTerminus1(" + i + ")");
				
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadIEContainerAtTerminus2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineLoadEEContainerAtTerminusConstraint(GRBModel model){		
			try{
				for(int i = 0; i < nbTrucks; i++){
					int d = terminateTrucks.get(i);
					GRBLinExpr expr1 = new GRBLinExpr();
					GRBVar x = point2varEECL.get(d);
					expr1.addTerm(1, x);
					model.addConstr(expr1, GRB.EQUAL, 0, "LoadEEContainerAtTerminus1(" + i + ")");
					
	//				GRBLinExpr expr2 = new GRBLinExpr();
	//				expr2.addTerm(-1, x);
	//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadEEContainerAtTerminus2(" + i + ")");
				}
			} catch (GRBException e) {
				System.out.println("Error code: " + e.getErrorCode() + ". " +
			            e.getMessage());
			}
		}

	public void defineTrailerInTerminusOutDepotConstraints(GRBModel model){
		try{
			for(int k = 0; k < nbTrucks; k++){
				for(int i = 0; i < depotTrailers.length; i++){
					int d = depotTrailers[i];
					int t = terminateTrailers[i];
					GRBLinExpr expr1 = new GRBLinExpr();
					for(int v : outArcTruck.get(d)){
						String s = k + "-" + d + "-" + v;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(-1, x);
					}
					
					for(int v : inArcTruck.get(t)){
						String s = k + "-" + v + "-" + t;
						if(arc2X.get(s) == null)
							continue;
						GRBVar x = arc2X.get(s);
						expr1.addTerm(1, x);
					}
					
					model.addConstr(expr1, GRB.LESS_EQUAL, 0.0, "TrailerDepotTerminus(" + k + "," + i + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineATandDTatDepotTerminusConstraints(GRBModel model){
		try{
			if(nbTrailers <= 1)
				return;
			for(int i = 0; i < depotTrailers.length; i++){
				for(int j= 0; j < depotTrailers.length; j++){
					if(i == j)
						continue;
					int d1 = depotTrailers[i];
					int t1 = terminateTrailers[i];
					int d2 = depotTrailers[j];
					int t2 = terminateTrailers[j];
					
					GRBVar DTd1 = point2DepartureTime.get(d1);
					GRBVar DTd2 = point2DepartureTime.get(d2);
					GRBVar DTt1 = point2DepartureTime.get(t1);
					GRBVar DTt2 = point2DepartureTime.get(t2);
					
					GRBLinExpr expr1 = new GRBLinExpr();
					if(DTd1.get(GRB.DoubleAttr.X) < DTd2.get(GRB.DoubleAttr.X)){
						expr1.addTerm(1, DTt1);
						expr1.addTerm(-1, DTt2);
					}
					model.addConstr(expr1, GRB.LESS_EQUAL, 0.0, "DTatDepotTerminus(" + d1 + "," + d2 + ")");
				}
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineTrailerAtTContraints(GRBModel model){
		try{
			for(int i = 0; i < nbTrucks; i++){
				int d = terminateTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varTL.get(d);
				expr1.addTerm(1, x);
				model.addGenConstrIndicator(y0, 1, expr1, GRB.EQUAL, 0.0, "TrailerAtT1(" + i + ")");
				model.addGenConstrIndicator(y1, 1, expr1, GRB.GREATER_EQUAL, a - nbTrailers + 1, "TrailerAtT2(" + i + ")");
				model.addGenConstrIndicator(y1, 1, expr1, GRB.LESS_EQUAL, a, "TrailerAtT2(" + i + ")");
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(1, y0);
				expr2.addTerm(1, y1);
				model.addConstr(expr2, GRB.EQUAL, 1, "BinaryEqual");
//				GRBLinExpr expr2 = new GRBLinExpr();
//				expr2.addTerm(-1, x);
//				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "LoadTrailerAtTerminus2(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineTrailerAtTContraints2(GRBModel model){
		try{
			GRBLinExpr expr5 = new GRBLinExpr();
			expr5.addTerm(1, y0);
			expr5.addTerm(1, y1);
			model.addConstr(expr5, GRB.EQUAL, 1, "BinaryEqual");
			
			for(int i = 0; i < nbTrucks; i++){
				int d = terminateTrucks.get(i);
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varTL.get(d);
				expr1.addTerm(1, x);
				expr1.addTerm(-M, y0);
				model.addConstr(expr1, GRB.GREATER_EQUAL, -M, "TrailerAtTerminus1(" + i + ")");
				
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(1, x);
				expr2.addTerm(M, y0);
				model.addConstr(expr2, GRB.LESS_EQUAL, M, "TrailerAtTerminus2(" + i + ")");
				
				GRBLinExpr expr3 = new GRBLinExpr();
				expr3.addTerm(1, x);
				expr3.addTerm(-M, y1);
				model.addConstr(expr3, GRB.GREATER_EQUAL, a - nbTrailers + 1 - M, "TrailerAtTerminus3(" + i + ")");
				
				GRBLinExpr expr4 = new GRBLinExpr();
				expr4.addTerm(1, x);
				expr4.addTerm(M, y1);
				model.addConstr(expr4, GRB.LESS_EQUAL, a + M, "TrailerAtTerminus4(" + i + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineTrailerLoadConstraints(GRBModel model){
		try{
			for(int v : truckPoints){
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varTL.get(v);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.LESS_EQUAL, a + nbTrailers, "TrailerLoadConst1(" + v + ")");
				
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(-1, x);
				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "TrailerLoadConst2(" + v + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineContainerLoadConstraints(GRBModel model){
		try{
			for(int v : truckPoints){
				GRBLinExpr expr1 = new GRBLinExpr();
				GRBVar x = point2varCL.get(v);
				expr1.addTerm(1, x);
				model.addConstr(expr1, GRB.LESS_EQUAL, 2, "ContainerLoadConst1(" + v + ")");
				
				GRBLinExpr expr2 = new GRBLinExpr();
				expr2.addTerm(-1, x);
				model.addConstr(expr2, GRB.LESS_EQUAL, 0, "ContainerLoadConst2(" + v + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public void defineStartOneTimeAtDepotConstraints(GRBModel model){
		try{
			for(int k = 0; k < nbTrucks; k++){
				int d = depotTrucks.get(k);
				GRBLinExpr expr1 = new GRBLinExpr();
				for(int v : outArcTruck.get(d)){
					String s = k + "-" + d + "-" + v;
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					expr1.addTerm(1, x);
				}
				model.addConstr(expr1, GRB.LESS_EQUAL, 1, "StartOneTimeAtDepot(" + k + ")");
			}
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	
	public void defineConstraints(GRBModel model){
		flowBalanceConstraint(model);
		SubtourEliminationConstraint(model);
		
		DefineExportEmptyContainerLoadConstraint(model);
		DefineImportEmptyContainerLoadConstraint(model);
		
		DefineAccumulatedTrailerLoadConstraint(model);
		DefineAccumulatedContainerLoadConstraint(model);
		
		defineContainerCarriedByTrailer(model);
		
		//defineTrailerInTerminusOutDepotConstraints(model);
		//defineATandDTatDepotTerminusConstraints(model);
		//defineTrailerAtTContraints(model);
		defineTrailerAtTContraints2(model);
		defineStartOneTimeAtDepotConstraints(model);
		
		defineContainerLoadConstraints(model);
		defineTrailerLoadConstraints(model);
		
		defineLoadTrailerAtDepotConstraint(model);
		defineLoadContainerAtDepotConstraint(model);
		defineLoadEEContainerAtDepotConstraint(model);
		defineLoadIEContainerAtDepotConstraint(model);
		
		//defineLoadTrailerAtTerminusConstraint(model);
		defineLoadContainerAtTerminusConstraint(model);
		defineLoadIEContainerAtTerminusConstraint(model);
		defineLoadEEContainerAtTerminusConstraint(model);
		
		defineExportEmptyConstraintFlow(model);
		defineImportEmptyConstraintFlow(model);
		
		defineArrivalDepartureTimeAlongArcConstraint(model);
		DefineArrivalTimeAtPointsConstraints(model);
		defineDepartureTimeAtPointConstraint(model);
		defineWaitingTimeAtPointsConstraints(model);
		
		//defineArrivalLessThanDepartureTimeAtPointConstraint(model);
		
		defineExportLadenPointSameRouteConstraint(model);
		defineImportLadenPointSameRouteConstraint(model);
		
		defineExportLadenServedConstraint(model);
		defineImportLadenServedConstraint(model);
		
		definePickupDeliveryOrderExportLadenConstraint(model);
		definePickupDeliveryOrderImportLadenConstraint(model);
	}
	
	public void defineObjective(GRBModel model){
		try{
			GRBLinExpr expr = new GRBLinExpr();
			
			for(ArrayList<Integer> arc : arcTrucks){
				int au = arc.get(0);
				int av = arc.get(1);
				for(int k = 0; k < nbTrucks; k++){
					String s = k + "-" + au + "-" + av; 
					if(arc2X.get(s) == null)
						continue;
					GRBVar x = arc2X.get(s);
					int d = distances[au][av];
					expr.addTerm(d, x);
				}
			}
			model.setObjective(expr, GRB.MINIMIZE);
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
	}
	
	public String getNextPoint(int x){
		try{
			for(int i = 0; i < X.size(); i++){
				if(X.get(i).get(GRB.DoubleAttr.X) == 1){
					String[] s = X.get(i).get(GRB.StringAttr.VarName).split(",");
					int st = Integer.parseInt(s[1]);
					if(st == x)
						return s[2].substring(0, s[2].length()-1);
				}
			}
			return null;
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
			return null;
		}
	}
	
	public void getResult(GRBModel model, String outputFile){
		try{
			double t = System.currentTimeMillis();
			model.optimize();
			
			System.out.println("Optimize done!");
			
			//System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));
			
			model.update();
			
			for(int i = 0; i < X.size(); i++){
				System.out.println(X.get(i).get(GRB.StringAttr.VarName) + " "
						+ X.get(i).get(GRB.DoubleAttr.X));
			}
			
			PrintWriter fw = new PrintWriter(new File(outputFile));
			
			String d = "";
			for(int i = 0; i < nbTrucks; i++){
				int s = depotTrucks.get(i);
				String str = "route[" + i + "] = " + s + " -> ";
				
				while( s != terminateTrucks.get(i)){
					String nextS = getNextPoint(s);
					if(nextS == null)
						break;
					d += "p1 = " + s + ", p2 = " + nextS 
							+ ", cost = " + distances[s][Integer.parseInt(nextS)] + "\n";					
					str += nextS + " -> ";
					s = Integer.parseInt(nextS);
				}
				System.out.println(str);
				fw.println(str);
			}
			
			double runTime = (System.currentTimeMillis() - t)/1000;
			System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal)
					+ ", run time = " + runTime);
			fw.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal)
					+ ", run time = " + runTime);
			System.out.println(d);
			fw.println(d);
			fw.close();
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
		String dir = "data/truck-container/";
		String dataFile = dir + "random_big_data-4reqs-MIP-test40ft-2.txt";
		String outputFile = dir + "random_big_data-4reqs-MIP-test40ft-result-2.txt";
		MIPModelTest40ft m = new MIPModelTest40ft();

		
		try{
			GRBEnv env   = new GRBEnv();
			env.set("logFile", "truckContainer.log");
			env.start();
			GRBModel model = new GRBModel(env);
			
			m.readData(dataFile);
			
			m.defineVariables(model);
			
			m.defineConstraints(model);
			
			m.defineObjective(model);
			
			System.out.println("Define done!");
			
			m.getResult(model, outputFile);
			
			model.dispose();
			env.dispose();
			
		} catch (GRBException e) {
			System.out.println("Error code: " + e.getErrorCode() + ". " +
		            e.getMessage());
		}
		
	}
}
