package localsearch.domainspecific.vehiclerouting.apps.sharedaride;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.CPickupDeliveryOfGoodVR;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.CPickupDeliveryOfPeopleVR;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Constraint.ScaleConstraint;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyCrossExchangeMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyExchangeRequestWithPeriodTime;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOnePointMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOneRequestMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyOrOptMove1ExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyTwoPointsMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Neighborhood.GreedyTwoRequestMoveExplorerLimit;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search.VariableNeighborhoodSearch;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IConstraintVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.timewindows.CEarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.ConstraintViolationsVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyCrossExchangeMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOnePointMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyOrOptMove1Explorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.GreedyTwoPointsMoveExplorer;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;



public class ShareARideWithThePeriodTime{
	public static int PEOPLE =1 ;
	public static int GOOD = 0;
	int scale = 100000;
	ArrayList<Point> points;
	ArrayList<Point> pickupPoints;
	ArrayList<Point> deliveryPoints;
	ArrayList<Integer> type;
	ArrayList<Point> startPoints;
	ArrayList<Point> stopPoints;
	
	ArrayList<Point> rejectPoints;
	ArrayList<Point> rejectPickup;
	ArrayList<Point> rejectDelivery;
	HashMap<Point, Integer> earliestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	HashMap<Point, Integer> lastestAllowedArrivalTime;
	HashMap<Point,Point> pickup2DeliveryOfGood;
	HashMap<Point,Point> pickup2DeliveryOfPeople;
	HashMap<Point, Point> pickup2Delivery;
	private HashMap<Point, Double> scoreReq;
	Point badPick;
	int nVehicle;
	int nRequest;
	
	ArcWeightsManager awm;
	VRManager mgr;
	VarRoutesVR XR;
	ConstraintSystemVR S;
	IFunctionVR objective;
	public CEarliestArrivalTimeVR ceat;
	LexMultiValues valueSolution;
	EarliestArrivalTimeVR eat;
	CEarliestArrivalTimeVR cEarliest;
	
	AccumulatedWeightEdgesVR accDisInvr;
	HashMap<Point, IFunctionVR> accDisF;
	
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
	public ShareARideWithThePeriodTime(Info info)
	{
		this.nVehicle = info.nbVehicle;
		this.nRequest = info.nRequest;
		points = new ArrayList<Point>();
		earliestAllowedArrivalTime = new HashMap<Point, Integer>();
		serviceDuration = new HashMap<Point, Integer>();
		lastestAllowedArrivalTime = new HashMap<Point, Integer>();
		
		scoreReq = new HashMap<Point, Double>();
		pickupPoints = new ArrayList<Point>();
		deliveryPoints = new ArrayList<Point>();
		startPoints = new ArrayList<Point>();
		stopPoints = new ArrayList<Point>();
		type = new ArrayList<>();
		
		rejectPoints = new ArrayList<Point>();
		rejectPickup = new ArrayList<Point>();
		rejectDelivery = new ArrayList<Point>();
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
			
			scoreReq.put(pickup, 0.0);
		}
		awm = new ArcWeightsManager(points);
		for(Point px: points){
			for(Point py: points){
				awm.setWeight(px, py, info.cost[px.getID()][py.getID()]);
				
			}
		}
	}

public void stateModel() {
	pickup2DeliveryOfGood = new HashMap<Point, Point>();
	pickup2DeliveryOfPeople = new HashMap<Point, Point>();
	pickup2Delivery = new HashMap<Point, Point>();
	for(int i=0; i < nRequest; ++i)
	{
		Point pickup = pickupPoints.get(i);
		Point delivery = deliveryPoints.get(i);
		pickup2Delivery.put(pickup, delivery);
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
	}
	
	//IConstraintVR goodC = new CPickupDeliveryOfGoodVR(XR, pickup2DeliveryOfGood);
	//IConstraintVR peopleC = new CPickupDeliveryOfPeopleVR(XR, pickup2DeliveryOfPeople);
	//S.post(goodC);
	//S.post(peopleC);

	//time windows
	eat = new EarliestArrivalTimeVR(XR,awm,earliestAllowedArrivalTime,serviceDuration);
	cEarliest = new CEarliestArrivalTimeVR(eat, lastestAllowedArrivalTime);
	
	// new accumulated distance
	accDisInvr = new AccumulatedWeightEdgesVR(XR, awm);
	//function mapping a point to F calculate distance when route exchanged
	accDisF = new HashMap<Point, IFunctionVR>();
	for(Point p: XR.getAllPoints()){
		IFunctionVR f =new AccumulatedEdgeWeightsOnPathVR(accDisInvr, p);
		accDisF.put(p, f);
		
	}
	S.post(cEarliest);
	objective = new TotalCostVR(XR,awm);
	valueSolution = new LexMultiValues();
	valueSolution.add(S.violations());
	valueSolution.add(objective.getValue());
	
	mgr.close();
	System.out.println("startmodel: obj = " + objective.getValue());
}

//public void insertPeople(Point pickup, Point delivery){
//	double minDelta = Integer.MAX_VALUE;
//	Point sel_p = null;
//	for(int r = 1; r <= XR.getNbRoutes(); r++){
//		for(Point p = XR.startPoint(r); p != XR.endPoint(r); p = XR.next(p)){
//			int dv = S.evaluateAddOnePoint(pickup, p);
//			if(dv > 0) continue;
//			
//			double dc = objective.evaluateAddOnePoint(pickup, p);
//			if(dc < minDelta){
//				minDelta = dc;
//				sel_p = p;
//			}
//			
//		}
//	}
//	
//	mgr.performAddOnePoint(pickup, sel_p);
//	mgr.performAddOnePoint(delivery, pickup);
//	
//}
//
//public void insertParcel(Point pickup, Point delivery){
//	double minDelta = Integer.MAX_VALUE;
//	Point sel_p = null;
//	for(int r = 1; r <= XR.getNbRoutes(); r++){
//		for(Point p = XR.startPoint(r); p != XR.endPoint(r); p = XR.next(p)){
//			for(Point q =XR.next(p); q != XR.endPoint(r); q = XR.next(q)){
//			int dv = S.evaluateAddOnePoint(pickup, p);
//			if(dv > 0) continue;
//			
//			double dc = objective.evaluateAddOnePoint(pickup, p);
//			if(dc < minDelta){
//				minDelta = dc;
//				sel_p = p;
//			}
//			}
//			
//		}
//	}
//	
//	mgr.performAddOnePoint(pickup, sel_p);
//	mgr.performAddOnePoint(delivery, pickup);
//	
//}

ArrayList<ArrayList<INeighborhoodExplorer>>search1(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	NE.add(new GreedyExchangeRequestWithPeriodTime(XR, F, 0.9, (int)(XR.getNbRoutes()*0.1), pickupPoints, deliveryPoints,
			pickup2Delivery, earliestAllowedArrivalTime, scoreReq)); 
	//NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
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
	NE.add(new GreedyOnePointMoveExplorer(XR, F)); 
	NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
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
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.1 , 3, pickupPoints, deliveryPoints, scoreReq));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 3));
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyTwoRequestMoveExplorerLimit(XR, F, 1, pickupPoints, deliveryPoints));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 2, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 5 , pickupPoints, deliveryPoints));
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.3 , 3, pickupPoints, deliveryPoints, scoreReq));
	
	listNE.add(NE);
	
	NE = new ArrayList<>();
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 0.6 , 3, pickupPoints, deliveryPoints, scoreReq));
	NE.add(new GreedyCrossExchangeMoveExplorerLimit(XR, F, 6, pickupPoints, deliveryPoints));
	NE.add(new GreedyOrOptMove1ExplorerLimit(XR, F, 10 , pickupPoints, deliveryPoints));
	listNE.add(NE);

	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>> search9(LexMultiFunctions F)
{
	ArrayList<ArrayList<INeighborhoodExplorer>> listNE = new ArrayList<>();
	ArrayList<INeighborhoodExplorer> NE = new ArrayList<>();
	NE.add(new GreedyOneRequestMoveExplorerLimit(XR, F, 1, 1000, pickupPoints, deliveryPoints, scoreReq)); 
	//NE.add(new GreedyTwoPointsMoveExplorer(XR, F));
	listNE.add(NE);
	return listNE;
}

ArrayList<ArrayList<INeighborhoodExplorer>> search10(LexMultiFunctions F)
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

VarRoutesVR search(int maxIter, int timeLimit, int searchMethod, String outDir)
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
	case 9:
		listNE = search9(F);
		break;
	case 10:
		listNE = search10(F);
		break;
	}
	
	VariableNeighborhoodSearch vns = new VariableNeighborhoodSearch(mgr, F, listNE, pickupPoints, deliveryPoints);
	vns.search(maxIter, timeLimit, outDir);
	valueSolution =vns.getIncumbentValue();
	cntInteration = vns.getCurrentIteration();
	cntTimeRestart = vns.getCntRestart();
	return XR;
}
//public VarRoutesVR test(){
//	System.out.println("greedyConstructiveSearch start, XR = " + XR.toString());
//	int idxVehicle = 1;
//	for(int i = 0; i < pickupPoints.size(); i++){
//		Point pickup = pickupPoints.get(i);
//		Point delivery = deliveryPoints.get(i);
//
//		Point end = XR.endPoint(idxVehicle);
//		Point pre_end = XR.prev(end);
//		System.out.println("1 vio all: " + S.violations());
//		mgr.performAddOnePoint(pickup, pre_end);
//		mgr.performAddOnePoint(delivery, pickup);
//		if(idxVehicle > XR.getNbRoutes()) idxVehicle = 1;
//		/*if(i < 5) {
//			System.out.println("Iter " + i+ ", XR = " + XR.toString() + ", S.violations = "+ S.violations()+", obj=" + objective.getValue());
//		
//		//for(Point q: XR.getAllPoints()){
//			System.out.println("greedyConstructiveSearch, earliest arrival time at point " + pickup.ID + " = " + 
//		eat.getEarliestArrivalTime(pickup) + ", earliest arrival time at point " + delivery.ID + " = " + 
//					eat.getEarliestArrivalTime(delivery) + ", acc_dis[" + pickup.ID + "] = " + accDisInvr.getCostRight(pickup) +
//					", acc_dis[" + delivery.ID+"] = " + accDisInvr.getCostRight(delivery)+
//					"disF[" + pickup.ID + "]=" + accDisF.get(pickup).getValue());
//			
//		}*/
//	}
//	
//	valueSolution = new LexMultiValues();
//	valueSolution.add(S.violations());
//	valueSolution.add(objective.getValue());
//	System.out.println("vio = " + S.violations() + ", value = " + objective.getValue());
//	return XR;
//}
	public void addRequestIntoRoute(ArrayList<Point> ppList, ArrayList<Point> dpList) throws FileNotFoundException{
		Set<Point> pickPeoplePoints = pickup2DeliveryOfPeople.keySet();
		String outDir= "data/output/SARP-offline/logtest.txt";
		PrintWriter out1 = new PrintWriter(outDir);
		out1.println("cur obj = " + objective.getValue() + ", cur vio = " + S.violations());
		out1.println(XR.toString());
		out1.close();
		for(int i = 0; i < ppList.size(); i++){
			System.out.println("i = " + i);
			Point pickup = ppList.get(i);
			if(XR.route(pickup) != Constants.NULL_POINT)
				continue;
			Point delivery = dpList.get(i);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double cur_obj = Double.MAX_VALUE; 
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				for(Point v = XR.getStartingPointOfRoute(r); v!= XR.getTerminatingPointOfRoute(r); v = XR.next(v)){
					if(S.evaluateAddOnePoint(pickup, v) > 0 || pickPeoplePoints.contains(v)){
						continue;
					}
					
					if(pickPeoplePoints.contains(pickup) //this request is people request
						&& S.evaluateAddTwoPoints(pickup, v, delivery, v) == 0 //violation not change and obj min
						&& objective.evaluateAddTwoPoints(pickup, v, delivery, v) + objective.getValue() < cur_obj){
						cur_obj = objective.getValue() + objective.evaluateAddTwoPoints(pickup, v, delivery, v);
						pre_pick = v;
						pre_delivery = v;
					}
					else{
						for(Point u = v; u != XR.getTerminatingPointOfRoute(r); u = XR.next(u)){
							if(S.evaluateAddOnePoint(delivery, u) > 0 || pickPeoplePoints.contains(u)){
								continue;
							}
							if(S.evaluateAddTwoPoints(pickup, v, delivery, u) == 0 && objective.evaluateAddTwoPoints(pickup, v, delivery, u) + objective.getValue() < cur_obj){
								cur_obj = objective.getValue() + objective.evaluateAddTwoPoints(pickup, v, delivery, u);
								pre_pick = v;
								pre_delivery = u;
							}
						}
					}	
				}
			}
			if((pre_pick == null || pre_delivery == null) && !rejectPickup.contains(pickup)){
				rejectPoints.add(pickup);
				rejectPoints.add(delivery);
				rejectPickup.add(pickup);
				rejectDelivery.add(delivery);
				scoreReq.put(pickup, 0.0);
				//System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
			}
			else{
				double score = objective.evaluateAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				scoreReq.put(pickup, score);
			}
		}
		
		ArrayList<Point> rejectListTemp = new ArrayList<Point>(rejectPickup);
		for(int i = 0; i < rejectListTemp.size(); i++){
			if(XR.route(rejectListTemp.get(i)) != Constants.NULL_POINT){
				Point delivery = rejectDelivery.get(i);
				rejectPoints.remove(delivery);
				rejectDelivery.remove(i);
				rejectPickup.remove(i);
			}
		}

		updateBadRequest();
	}
	
	public VarRoutesVR greedyInitSolution(String outDir) throws FileNotFoundException{
		System.out.println("greedyInitSolution start");

		
		addRequestIntoRoute(pickupPoints, deliveryPoints);
		
		valueSolution = new LexMultiValues();
		valueSolution.add(S.violations());
		valueSolution.add(objective.getValue());
		return XR;
	}
    
	public void updateBadRequest(){
		double sc = 0;
		badPick = pickupPoints.get(0);
		for(Point p : scoreReq.keySet()){
			if(scoreReq.get(p) > sc ){
				sc = scoreReq.get(p);
				badPick = p;
			}
		}
	}
	public void reInsertRequest(double maxTime, String outDir) throws FileNotFoundException{
		System.out.println("ShareARide::reInsertReq======start");
		double t0 = System.currentTimeMillis();
		int nRemove = 0;
		int nLoop = 0;
		int nAdd = 0;
		PrintWriter output = new PrintWriter(new FileOutputStream(outDir, true));
		output.println("ShareARide::reInsertReq======start vio = " + S.violations() + ", obj = " + objective.getValue() 
			+ ", reject size = " + rejectPickup.size());
		output.close();
		double cur_obj = objective.getValue();
		int cur_vio = S.violations();
		while((System.currentTimeMillis() - t0)/1000 < maxTime){
			Point badDelivery;
			Set<Point> pickPeoplePoints = pickup2DeliveryOfPeople.keySet();
			if(pickPeoplePoints.contains(badPick))
				badDelivery = pickup2DeliveryOfPeople.get(badPick);
			else
				badDelivery = pickup2DeliveryOfGood.get(badPick);
			if(XR.route(badPick) != Constants.NULL_POINT){	
				mgr.performRemoveTwoPoints(badPick, badDelivery);
				rejectPickup.add(badPick);
				rejectDelivery.add(badDelivery);
				rejectPoints.add(badPick);
				rejectPoints.add(badDelivery);
				scoreReq.put(badPick, 0.0);
				nRemove++;
			}
			nLoop++;
			addRequestIntoRoute(rejectPickup, rejectDelivery);
			if((objective.getValue() < cur_obj && S.violations() <= cur_vio)
					|| (objective.getValue() == cur_obj && S.violations() < cur_vio)){
				nAdd++;
				cur_obj = objective.getValue();
				cur_vio = S.violations();
				PrintWriter out = new PrintWriter(new FileOutputStream(outDir, true));
				out.println("ShareARide::reInsertReq====== nLoop = " + nLoop + ", nRemove " + nRemove + ", nAdd = " + nAdd + ", vio = " + S.violations() + ", obj = " + objective.getValue() + ", reject size = " + rejectPickup.size());
				out.close();
			}
			System.out.println("nLoop = " + nLoop + ", nRemove = " + nRemove + ", nAdd = " + nAdd);
		}
		valueSolution = new LexMultiValues();
		valueSolution.add(S.violations());
		valueSolution.add(objective.getValue());
		System.out.println("vio = " + S.violations() + ", value = " + objective.getValue());
		System.out.println("rejected reqs = " + rejectPickup.size());
		PrintWriter out = new PrintWriter(new FileOutputStream(outDir, true));
		out.println("ShareARide::reInsertReq====== end loop = " + nLoop + ", nRemove " + nRemove + ", nAdd = " + nAdd 
				+ ", vio = " + S.violations() + ", obj = " + objective.getValue() + ", reject size = " 
				+ rejectPickup.size());
		out.close();
	}
    public static void main(String []args) throws FileNotFoundException
    {
    	String inData = "data/SARP-offline/n6167r200_1.txt";
    	
    	int timeLimit = 36000;
    	int nIter = 3000;
    	String description = "===comparison 10 searching types. search1 exchange two requests between two routes===";
    	
    	for(int i = 1; i <= 10; i++){
    		String outDir= "data/output/SARP-offline/N6167_R200_D1_" + "nIter" + nIter + "_time" + timeLimit + "_S" + i + ".txt";
    		Info info = new Info(inData);
			ShareARideWithThePeriodTime sar = new ShareARideWithThePeriodTime(info);
			double t0 = System.currentTimeMillis();
	    	
			sar.stateModel();
	    	PrintWriter out = new PrintWriter(outDir);
	    	out.println(description);
	    	out.println("statemodel: starting time = " + t0 + ", endtime = " + (System.currentTimeMillis() - t0));
	    	out.close();
	    	
	    	t0 = System.currentTimeMillis();
	    	sar.greedyInitSolution(outDir);
	    	LexMultiValues v = sar.valueSolution;
	    	PrintWriter out1 = new PrintWriter(new FileOutputStream(outDir, true));
	    	out1.println("======================================");
	    	out1.println("greedyInitSolution: starting time = " + t0 + ", ending time = " + (System.currentTimeMillis() - t0) 
	    			+ ", first violationss = " + v.get(0) + ", first obj = " + v.get(1) + ", rejected reqs: " + sar.rejectPickup.size());
	    	out1.close();
	    	
	    	t0 = System.currentTimeMillis();
	    	sar.reInsertRequest(timeLimit, outDir);
	    	PrintWriter out2 = new PrintWriter(new FileOutputStream(outDir, true));
	    	out2.println("======================================");
	    	out2.println("reInsertRequest: starting time = " + t0 + ", ending time = " + (System.currentTimeMillis() - t0) 
	    			+ ", violationss = " + v.get(0) + ", obj = " + v.get(1) + ", rejected reqs: " + sar.rejectPickup.size());
	    	out2.close();
	    	
	    	t0 = System.currentTimeMillis();
	    	sar.search(nIter, timeLimit/10, i, outDir);
	    	PrintWriter out3 = new PrintWriter(new FileOutputStream(outDir, true));
	    	LexMultiValues v1 = sar.valueSolution;
	    	out3.println("======================================");
	    	out3.println("search:: starting time = " + t0 + ", ending time = " + (System.currentTimeMillis() - t0) 
	    			+ ", The last violationss = " + v1.get(0) + ", obj = " + v1.get(1) + ", rejected reqs: " + sar.rejectPickup.size());
	    	out3.close();
    	}
    }
    
    
   
}

