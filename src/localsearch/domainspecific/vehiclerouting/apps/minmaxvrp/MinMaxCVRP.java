package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyKPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyThreeOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove2Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove3Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove4Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove5Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove6Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove7Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoOptMove8Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.search.GenericLocalSearch;

public class MinMaxCVRP {
	// raw data input
	public int nbVehicles;
	public int nbClients;
	public int capacity;
	public int[] demand;
	public double[][] cost;
	public int depot;
	
	// object mapping
	public ArrayList<Point> startPoints;
	public ArrayList<Point> endPoints;
	public ArrayList<Point> clientPoints;
	public ArrayList<Point> allPoints;
	public NodeWeightsManager nwm;
	public ArcWeightsManager awm;
	
	
	// modelling
	public VRManager mgr;
	public VarRoutesVR XR;
	public IFunctionVR[] accDemand;
	public IFunctionVR[] distance;
	public ConstraintSystemVR CS;
	public IFunctionVR obj;
	public IFunctionVR totalDistance;
	public LexMultiFunctions F;
	
	// result
	public double best_obj;
	public double time_to_best;
	
	public void readData(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			
			nbVehicles = in.nextInt();
			int N = in.nextInt();
			nbClients = N-1;
			depot = in.nextInt();
			capacity = in.nextInt();
			demand = new int[N+1];
			for(int i = 0; i < N; i++){
				int id = in.nextInt();
				demand[id] = in.nextInt();
			}
			cost = new double[N+1][N+1];
			for(int i = 1; i <= N; i++){
				for(int j = 1; j <= N; j++){
					int I = in.nextInt();
					int J = in.nextInt();
					cost[I][J] = in.nextDouble();
				}
			}
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void mapping(){
		clientPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
	
		HashMap<Point, Integer> mPoint2ID = new HashMap<Point, Integer>();
		for(int k = 1; k <= nbVehicles; k++){
			Point s = new Point(depot);
			startPoints.add(s);
			allPoints.add(s);
			mPoint2ID.put(s, depot);
			
			Point t = new Point(depot);
			endPoints.add(t);
			allPoints.add(t);
			mPoint2ID.put(t, depot);
		}
		for(int i= 1; i <= nbClients+1; i++){
			if(i != depot){
				Point p = new Point(i);
				clientPoints.add(p);
				allPoints.add(p);
				mPoint2ID.put(p, p.ID);
			}
		}
		
		nwm = new NodeWeightsManager(allPoints);
		awm = new ArcWeightsManager(allPoints);
		for(Point p: clientPoints)
			nwm.setWeight(p, demand[mPoint2ID.get(p)]);
		for(Point p: startPoints)
			nwm.setWeight(p, 0);
		for(Point p: endPoints)
			nwm.setWeight(p, 0);
		
		for(Point p1: allPoints){
			int i = mPoint2ID.get(p1);
			for(Point p2: allPoints){
				int j = mPoint2ID.get(p2);
				awm.setWeight(p1, p2, cost[i][j]);
			}
		}
		
		
	}
	
	public void stateModel(){
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for(int i = 0; i < startPoints.size(); i++){
			Point s = startPoints.get(i);
			Point t = endPoints.get(i);
			XR.addRoute(s, t);
		}
		for(Point p: clientPoints)
			XR.addClientPoint(p);
		
		CS = new ConstraintSystemVR(mgr);
		AccumulatedWeightNodesVR awn = new AccumulatedWeightNodesVR(XR, nwm);
		AccumulatedWeightEdgesVR awe = new AccumulatedWeightEdgesVR(XR, awm);
		accDemand = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			accDemand[k-1] = new AccumulatedNodeWeightsOnPathVR(awn, XR.endPoint(k));
			CS.post(new Leq(accDemand[k-1], capacity));
		}
		
		distance = new IFunctionVR[XR.getNbRoutes()];
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			distance[k-1] = new AccumulatedEdgeWeightsOnPathVR(awe, XR.endPoint(k));
		}
		
		obj = new MaxVR(distance);
		
		//totalDistance = new TotalCostVR(XR, awm);
		
		F = new LexMultiFunctions();
		F.add(new ConstraintViolationsVR(CS));
		F.add(obj);
		//F.add(totalDistance);
		mgr.close();
	}
	
	public void search(int timeLimit) {
		HashSet<Point> mandatory = new HashSet<Point>();
		for(Point p: clientPoints) mandatory.add(p);
		
		ArrayList<INeighborhoodExplorer> NE = new ArrayList<INeighborhoodExplorer>();
		NE.add(new GreedyOnePointMoveExplorer(XR, F));
		NE.add(new GreedyCrossExchangeMoveExplorer(XR, F));
		
		
		NE.add(new GreedyOrOptMove1Explorer(XR, F));
		NE.add(new GreedyOrOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove1Explorer(XR, F));
		NE.add(new GreedyThreeOptMove2Explorer(XR, F));
		NE.add(new GreedyThreeOptMove3Explorer(XR, F));
		NE.add(new GreedyThreeOptMove4Explorer(XR, F));
		NE.add(new GreedyThreeOptMove5Explorer(XR, F));
		NE.add(new GreedyThreeOptMove6Explorer(XR, F));
		NE.add(new GreedyThreeOptMove7Explorer(XR, F));
		NE.add(new GreedyThreeOptMove8Explorer(XR, F));
		NE.add(new GreedyTwoOptMove1Explorer(XR, F));
		NE.add(new GreedyTwoOptMove2Explorer(XR, F));
		NE.add(new GreedyTwoOptMove3Explorer(XR, F));
		NE.add(new GreedyTwoOptMove4Explorer(XR, F));
		NE.add(new GreedyTwoOptMove5Explorer(XR, F));
		NE.add(new GreedyTwoOptMove6Explorer(XR, F));
		NE.add(new GreedyTwoOptMove7Explorer(XR, F));
		NE.add(new GreedyTwoOptMove8Explorer(XR, F));
		
		
		//NE.add(new GreedyKPointsMoveExplorer(XR, F, 2, mandatory));
		
		MMSearch se = new MMSearch(mgr);
		//GenericLocalSearch se = new GenericLocalSearch(mgr);
		se.setNeighborhoodExplorer(NE);
		se.setObjectiveFunction(F);
		se.setMaxStable(50);
		
		se.search(10000, timeLimit);
		print();

	}
	public void print(){
		System.out.println("capacity = " + capacity);
		for(int k = 1; k <= XR.getNbRoutes(); k++){
			System.out.println("Route[" + k + "] = " + XR.routeString(k) + ", accDemand = " + accDemand[k-1].getValue() + 
					", distance = " + distance[k-1].getValue());
		}
		System.out.println("obj = " + obj.getValue());
	}
	public void stdEuclide(String fni, String fno){
		try{
			Scanner in = new Scanner(new File(fni));
			PrintWriter out = new PrintWriter(fno);
			
			int nbVehicles = in.nextInt();
			int nbPoints = in.nextInt();
			int depot = in.nextInt();
			int capacity = in.nextInt();
			int[] demand = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				demand[id] = in.nextInt();
			}
			int[] x = new int[nbPoints+1];
			int[] y = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				x[id] = in.nextInt();
				y[id] = in.nextInt();
			}
			in.close();
			
			double[][] c = new double[nbPoints+1][nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				for(int j = 1; j <= nbPoints; j++){
					c[i][j] = Math.sqrt((x[i]-x[j])^2 + (y[i]-y[j])^2);
					double d = (x[i] - x[j])*(x[i] - x[j]) + (y[i] - y[j])*(y[i] - y[j]);
					double d2 = Math.sqrt(d);
					c[i][j] = d2;
					
					c[i][j] = Math.abs(x[i] - x[j]) + Math.abs(y[i] - y[j]);// manhatan distance
					
					/*
					if(i == 1 && j == 4){
						System.out.println("c[" + i + "," + j + "] = " + c[i][j] + ", x[" +i + "] = " + 
					x[i] + ", x[" + j + "] = " + x[j] + ", y[" + i + "] = " + y[i] + ", y[" + j + "] = " + 
								y[j] + ", d = " + d + ", d2 = " + d2);
					}
					*/
				}
			}
			out.println(nbVehicles + " " + nbPoints);
			out.println(depot + " " + capacity);
			for(int i = 1; i <= nbPoints; i++)
				out.println(i + " " + demand[i]);
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					out.println(i + " " + j + " " + c[i][j]);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void stdLowDiagnal(String fni, String fno){
		try{
			Scanner in = new Scanner(new File(fni));
			PrintWriter out = new PrintWriter(fno);
			
			int nbVehicles = in.nextInt();
			int nbPoints = in.nextInt();
			int depot = in.nextInt();
			int capacity = in.nextInt();
			int[] demand = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				demand[id] = in.nextInt();
			}
			
			
			
			double[][] c = new double[nbPoints+1][nbPoints+1];
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					c[i][j] = 0;
			
			int I = 2;
			int J = 1;
			for(int k = 1; k <= nbPoints*(nbPoints-1)/2; k++){
				c[I][J] = in.nextInt();
				if(I == J+1){
					I++;
					J = 1;
				}else{
					J++;
				}
			}
			for(int i = 1; i < nbPoints; i++){
				for(int j = i+1; j <= nbPoints; j++){
					c[i][j] = c[j][i];
				}
			}
			out.println(nbVehicles + " " + nbPoints);
			out.println(depot + " " + capacity);
			for(int i = 1; i <= nbPoints; i++)
				out.println(i + " " + demand[i]);
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					out.println(i + " " + j + " " + c[i][j]);
			out.close();
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void standardizeData(){
		MinMaxCVRP vrp  = new MinMaxCVRP();
		String[] fe = new String[]{
				"E-n101-k14.vrp",
				"E-n23-k3.vrp",
				"E-n33-k4.vrp",
				"E-n76-k14.vrp",
				"E-n76-k8.vrp",
				"E-n101-k8.vrp",
				"E-n30-k3.vrp",
				"E-n51-k5.vrp",
				"E-n76-k15.vrp",
				"E-n22-k4.vrp",
				"E-n30-k4.vrp",
				"E-n76-k10.vrp",
				"E-n76-k7.vrp"};
		String[] fl = new String[]{
			"E-n7-k2.vrp",
			"E-n13-k4.vrp",
			"E-n31-k7.vrp"
		};
		for(int i = 0; i < fe.length; i++){
			vrp.stdEuclide("data/MinMaxVRP/Christophides/std-euclide/" + fe[i],
					"data/MinMaxVRP/Christophides/std-all/" + fe[i]);
		}
		
		for(int i = 0; i < fl.length; i++){
			vrp.stdLowDiagnal("data/MinMaxVRP/Christophides/std-lower-diag/" + fl[i],
					"data/MinMaxVRP/Christophides/std-all/" + fl[i]);
		}
		

	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		MinMaxCVRP vrp = new MinMaxCVRP();
		
		//vrp.standardizeData();if(true) return;
		
		//vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n7-k2.vrp");
		//vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n22-k4.vrp");
		vrp.readData("data/MinMaxVRP/Christophides/std-all/E-n101-k14.vrp");
		vrp.mapping();
		vrp.stateModel();
		vrp.search(300);
	}

}
