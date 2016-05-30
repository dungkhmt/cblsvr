package localsearch.domainspecific.vehiclerouting.apps.sharedaride;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.CPickupDeliveryOfGoodVR;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.CPickupDeliveryOfPeopleVR;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.ScaleConstraint;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyCrossExchangeMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOnePointMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOneRequestMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOrOptMove1ExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyTwoPointsMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyTwoRequestMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search.VariableNeighborhoodSearch;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows.CEarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;



public class ShareARide{
	public static int PEOPLE =1 ;
	public static int GOOD = 0;
	int scale = 100000;
	ArrayList<Point> points;
	ArrayList<Point> pickupPoints;
	ArrayList<Point> deliveryPoints;
	ArrayList<Integer> type;
	ArrayList<Point> startPoints;
	ArrayList<Point> stopPoints;
	
	HashMap<Point, Integer> earliestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	HashMap<Point, Integer> lastestAllowedArrivalTime;
	ArcWeightsManager awm;
	
	
	int nVehicle;
	int nRequest;
	
	VRManager mgr;
	VarRoutesVR XR;
	ConstraintSystemVR S;
	IFunctionVR objective;
	public CEarliestArrivalTimeVR ceat;
	LexMultiValues valueSolution;
	
	int cntTimeRestart;
	int cntInteration;
	public int calVioNow()
	{
		int vio = 0;
		int nr = XR.getNbRoutes();
		for(int k=1;k<=nr;++k)
		{
			Point s = XR.startPoint(k);
			int t = earliestAllowedArrivalTime.get(s) + serviceDuration.get(s);
			while(s != XR.endPoint(k))
			{
				Point ns = XR.next(s);
				int tt = (int)(t + awm.getDistance(s, ns));
				int q = Math.max(tt, earliestAllowedArrivalTime.get(ns));
				if( q > lastestAllowedArrivalTime.get(ns))
					vio += q - lastestAllowedArrivalTime.get(ns);
				t = q + serviceDuration.get(ns);
				s = ns;
			}
		}
		return vio;
	}
	public ShareARide(Info info)
	{
		this.nVehicle = info.nbVehicle;
		this.nRequest = info.nRequest;
		points = new ArrayList<Point>();
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		pickupPoints = new ArrayList<Point>();
		deliveryPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		stopPoints = new ArrayList<Point>();
		type = new ArrayList<>();
		for(int i=1; i <= info.nbVehicle; ++i)
		{
			int startPointId = i+info.nRequest*2;
			Point sp = new Point(startPointId,info.p[startPointId].getX(),info.p[startPointId].getY());
			int stopPointId = i+info.nRequest*2+info.nbVehicle;
			Point tp = new Point(stopPointId,info.p[stopPointId].getX(),info.p[stopPointId].getY());
			points.add(sp);
			points.add(tp);
			startPoints.add(sp);
			stopPoints.add(tp);
			
			earliestAllowedArrivalTime.put(sp,(int)( info.earliest[startPointId]));
			serviceDuration.put(sp, (int)(info.serviceTime[startPointId]));
			lastestAllowedArrivalTime.put(sp,(int)( info.lastest[startPointId]));
			
			earliestAllowedArrivalTime.put(tp,(int)( info.earliest[stopPointId]));
			serviceDuration.put(tp, (int)(info.serviceTime[stopPointId]));
			lastestAllowedArrivalTime.put(tp,(int)( info.lastest[stopPointId]));
		}
		
		
	
		for(int i=0;i<info.nRequest; ++i)
		{
			Point pickup = new Point(i*2+1,info.p[i*2+1].getX(),info.p[i*2+1].getY());
			Point delivery = new Point(2*i+2,info.p[2*i+2].getX(),info.p[2*i+2].getY());

			points.add(pickup);
			points.add(delivery);
			
			pickupPoints.add(pickup);
			deliveryPoints.add(delivery);
			
			earliestAllowedArrivalTime.put(pickup,(int)( info.earliest[2*i+1]));
			serviceDuration.put(pickup, (int)(info.serviceTime[2*i+1]));
			lastestAllowedArrivalTime.put(pickup,(int)( info.lastest[2*i+1]));
			
			earliestAllowedArrivalTime.put(delivery,(int)( info.earliest[2*i+2]));
			serviceDuration.put(delivery, (int)(info.serviceTime[2*i+2]));
			lastestAllowedArrivalTime.put(delivery,(int)( info.lastest[2*i+2]));
			
			type.add(info.type[i*2+1]);
		}
		awm = new ArcWeightsManager(points);
		for(Point px: points){
			for(Point py: points){
				awm.setWeight(px, py, info.cost[px.getID()][py.getID()]);
				
			}
		}
	}

public void stateModel() {
	HashMap<Point,Point> pickup2DeliveryOfGood = new HashMap<Point, Point>();
	HashMap<Point,Point> pickup2DeliveryOfPeople = new HashMap<Point, Point>();
	for(int i=0; i < nRequest; ++i)
	{
		Point pickup = pickupPoints.get(i);
		Point delivery = deliveryPoints.get(i);
		if(type.get(i)==PEOPLE)
		{
			pickup2DeliveryOfPeople.put(pickup, delivery);
		}
		else{
			pickup2DeliveryOfGood.put(pickup, delivery);
		}
	}
	mgr = new VRManager();
	XR = new VarRoutesVR(mgr);
	S = new ConstraintSystemVR(mgr);
	for(int i=0;i<nVehicle;++i)
		XR.addRoute(startPoints.get(i), stopPoints.get(i));
	for(int i=0;i<nRequest; ++i)
	{
		Point pickup = pickupPoints.get(i);
		Point delivery = deliveryPoints.get(i);
		XR.addClientPoint(pickup);
		XR.addClientPoint(delivery);
		/*
		IFunctionVR indexP = new IndexOnRoute(XR, pickup);
		IFunctionVR indexD = new IndexOnRoute(XR,delivery);
		IFunctionVR routeP = new RouteIndex(XR,pickup);
		IFunctionVR routeD = new RouteIndex(XR,delivery);
		IConstraintVR pBeforeD =  new LeqFunctionFunction(indexP, indexD);
		S.post(new ScaleConstraint(pBeforeD,scale));
		IConstraintVR pAndDInSameRoute = new EquFunctionFunction(routeP,routeD);
		S.post(new ScaleConstraint(pAndDInSameRoute, scale));
		if(type.get(i)==PEOPLE)
		{
			IFunctionVR disPAndDIndex =  new MinusFunctionFunctionVR(indexD, indexP);
			IConstraintVR dIsNextOfP = new EquFunctionConstantVR(disPAndDIndex,1); 
			//= new LeqFunctionConstant(disPAndDIndex, M);
			S.post(new ScaleConstraint(dIsNextOfP,scale));
		}
		*/
	}
	
	IConstraintVR goodC = new CPickupDeliveryOfGoodVR(XR, pickup2DeliveryOfGood);
	IConstraintVR peopleC = new CPickupDeliveryOfPeopleVR(XR, pickup2DeliveryOfPeople);
	S.post(new ScaleConstraint(goodC, scale));
	S.post(new ScaleConstraint(peopleC, scale));

	EarliestArrivalTimeVR eat = new EarliestArrivalTimeVR(XR,awm,earliestAllowedArrivalTime,serviceDuration);
	CEarliestArrivalTimeVR cEarliest = new CEarliestArrivalTimeVR(eat, lastestAllowedArrivalTime);
	S.post(cEarliest);
	objective = new TotalCostVR(XR,awm);
	mgr.close();
}



ArrayList<ArrayList<INeighborhoodExplorer>>search1(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorer(XR, F)); 
	NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
	listNE.add(NE);
	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>>search2(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>>listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	NE.add(new GreedyCrossExchangeMoveExplorer(XR, F));
	listNE.add(NE);
	return listNE;
}


ArrayList<ArrayList<INeighborhoodExplorer>>search3(LexMultiFunctions F)
{
	System.out.println("search 3");
	ArrayList<ArrayList<INeighborhoodExplorer>>listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	NE.add(new GreedyOrOptMove1Explorer(XR, F));
	listNE.add(NE);
	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>>search4(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorer(XR, F));  
	NE.add(new GreedyTwoPointsMoveExplorer(XR, F));		
	listNE.add(NE);
	NE = new ArrayList<>();
	NE.add(new GreedyOrOptMove1Explorer(XR, F));
	listNE.add(NE);
	
	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>>search5(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorerLimit(XR, F, 4));  // 2
	NE.add(new GreedyTwoPointsMoveExplorerLimit(XR, F,4));		//2
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 2));
	listNE.add(NE);
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorer(XR, F));  // 2
	NE.add(new GreedyTwoPointsMoveExplorer(XR, F));		//2
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 5));
	
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOrOptMove1Explorer(XR, F));
	listNE.add(NE);
	
	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>>search6(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorerLimit(XR, F, 4));  
	NE.add(new GreedyTwoPointsMoveExplorerLimit(XR, F, 4));	
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 2));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorer(XR, F));  // 2
	NE.add(new GreedyTwoPointsMoveExplorer(XR, F));		//2
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 5));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOrOptMove1Explorer(XR, F));
	listNE.add(NE);

	NE = new ArrayList<>();
    NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 2, pickupPoints, deliveryPoints));
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 4));
	listNE.add(NE);
	return listNE;
}
ArrayList<ArrayList<INeighborhoodExplorer>>search7(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	
	NE = new ArrayList<>();

	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 1));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F,0.25, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 3));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F,1, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 2, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 5 , pickupPoints, deliveryPoints));
	
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 2, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 6, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 10 , pickupPoints, deliveryPoints));
	listNE.add(NE);

	return listNE;
}
ArrayList<ArrayList<INeighborhoodExplorer>>search8(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorerLimit(XR, F, 4));  
	NE.add(new GreedyTwoPointsMoveExplorerLimit(XR, F, 4));	
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 2));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOnePointMoveExplorerLimit(XR, F, 7)); 
	NE.add(new GreedyTwoPointsMoveExplorerLimit(XR, F, 7));		
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F,0.25, pickupPoints, deliveryPoints));
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.1 , 3, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 3));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F, 1, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 2, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 5 , pickupPoints, deliveryPoints));
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.3 , 3, pickupPoints, deliveryPoints));
	
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.6 , 3, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 6, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 10 , pickupPoints, deliveryPoints));
	listNE.add(NE);

	return listNE;
}

    VarRoutesVR search(int maxIter, int timeLimit, int searchMethod)
    {
    	LexMultiFunctions F;
		F = new LexMultiFunctions();
		F.add(new ConstraintViolationsVR(S));
		F.add(objective);
		ArrayList<ArrayList<INeighborhoodExplorer>> listNE = null;;
		switch(searchMethod)
		{
		case 1:
			listNE = search1(F);
			break;
		case 2:
			listNE  = search2(F);
			break;
		case 3:
			listNE = search3(F); 
			break;
		case 4:
			listNE = search4(F); 
			break;
		case 5:
			listNE = search5(F); 
			break;
		case 6:
			listNE = search6(F); 
			break;
		case 7:
			listNE = search7(F);
			break;
		case 8:
			listNE = search8(F);
			break;
		}
		
		VariableNeighborhoodSearch vns = new VariableNeighborhoodSearch(mgr, F, listNE, pickupPoints, deliveryPoints);
		vns.search(maxIter, timeLimit);
		valueSolution =vns.getIncumbentValue();
		cntInteration = vns.getCurrentIteration();
		cntTimeRestart = vns.getCntRestart();
		return XR;
    }
    
    public static void main(String []args) throws FileNotFoundException
    {
    	int S = 7;
    	for(int data = 1; data <= 10; ++data)
    	{
			Info info = new Info("data/sharedaride/n100r10_"+data+".txt");
	    	
	    	PrintWriter out = new PrintWriter(new File("out/S"+S+"/N100_R10_D"+data+"_S"+S+".txt"));
	    	for(int turn = 0; turn < 10; ++turn)
	    	{
	    		ShareARide sar = new ShareARide(info);
	        	sar.stateModel();
	        	sar.search(20000, 300000, S);
	        	LexMultiValues v = sar.valueSolution;
	        	out.println(v.get(0)+"  "+v.get(1)*50/3600.0);
	        	out.flush();
	    	}
	    	out.close();
    	}
    }
    
    
   
    
}

