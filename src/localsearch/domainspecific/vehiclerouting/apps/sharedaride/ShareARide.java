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
	
	ArrayList<Point> rejectPoints;
	ArrayList<Point> rejectPickup;
	ArrayList<Point> rejectDelivery;
	ArrayList<Integer> typeOfRejectPoint;
	HashMap<Point, Integer> earliestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	HashMap<Point, Integer> lastestAllowedArrivalTime;
	HashMap<Point,Point> pickup2DeliveryOfGood;
	HashMap<Point,Point> pickup2DeliveryOfPeople;
	
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
		
		rejectPoints = new ArrayList<Point>();
		rejectPickup = new ArrayList<Point>();
		rejectDelivery = new ArrayList<Point>();
		typeOfRejectPoint = new ArrayList<Integer>();
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
	pickup2DeliveryOfGood = new HashMap<Point, Point>();
	pickup2DeliveryOfPeople = new HashMap<Point, Point>();
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
	
	mgr.close();
}

public void insertPeople(Point pickup, Point delivery){
	double minDelta = Integer.MAX_VALUE;
	Point sel_p = null;
	for(int r = 1; r <= XR.getNbRoutes(); r++){
		for(Point p = XR.startPoint(r); p != XR.endPoint(r); p = XR.next(p)){
			int dv = S.evaluateAddOnePoint(pickup, p);
			if(dv > 0) continue;
			
			double dc = objective.evaluateAddOnePoint(pickup, p);
			if(dc < minDelta){
				minDelta = dc;
				sel_p = p;
			}
			
		}
	}
	
	mgr.performAddOnePoint(pickup, sel_p);
	mgr.performAddOnePoint(delivery, pickup);
	
}

public void insertParcel(Point pickup, Point delivery){
	double minDelta = Integer.MAX_VALUE;
	Point sel_p = null;
	for(int r = 1; r <= XR.getNbRoutes(); r++){
		for(Point p = XR.startPoint(r); p != XR.endPoint(r); p = XR.next(p)){
			for(Point q =XR.next(p); q != XR.endPoint(r); q = XR.next(q)){
			int dv = S.evaluateAddOnePoint(pickup, p);
			if(dv > 0) continue;
			
			double dc = objective.evaluateAddOnePoint(pickup, p);
			if(dc < minDelta){
				minDelta = dc;
				sel_p = p;
			}
			}
			
		}
	}
	
	mgr.performAddOnePoint(pickup, sel_p);
	mgr.performAddOnePoint(delivery, pickup);
	
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


public VarRoutesVR test(){
	System.out.println("greedyConstructiveSearch start, XR = " + XR.toString());
	int idxVehicle = 1;
	for(int i = 0; i < pickupPoints.size(); i++){
		Point pickup = pickupPoints.get(i);
		Point delivery = deliveryPoints.get(i);

		Point end = XR.endPoint(idxVehicle);
		Point pre_end = XR.prev(end);
		System.out.println("1 vio all: " + S.violations());
		mgr.performAddOnePoint(pickup, pre_end);
		mgr.performAddOnePoint(delivery, pickup);
		if(idxVehicle > XR.getNbRoutes()) idxVehicle = 1;
		/*if(i < 5) {
			System.out.println("Iter " + i+ ", XR = " + XR.toString() + ", S.violations = "+ S.violations()+", obj=" + objective.getValue());
		
		//for(Point q: XR.getAllPoints()){
			System.out.println("greedyConstructiveSearch, earliest arrival time at point " + pickup.ID + " = " + 
		eat.getEarliestArrivalTime(pickup) + ", earliest arrival time at point " + delivery.ID + " = " + 
					eat.getEarliestArrivalTime(delivery) + ", acc_dis[" + pickup.ID + "] = " + accDisInvr.getCostRight(pickup) +
					", acc_dis[" + delivery.ID+"] = " + accDisInvr.getCostRight(delivery)+
					"disF[" + pickup.ID + "]=" + accDisF.get(pickup).getValue());
			
		}*/
	}
	
	valueSolution = new LexMultiValues();
	valueSolution.add(S.violations());
	valueSolution.add(objective.getValue());
	System.out.println("vio = " + S.violations() + ", value = " + objective.getValue());
	return XR;
}
	
	public VarRoutesVR greedyConstructiveSearch(int typeInit){
		System.out.println("greedyConstructiveSearch start, XR = " + XR.toString());

		int ix = 0;
		if(typeInit == 1){
			for(int i = 0; i < pickupPoints.size(); i++){
				Point pickup = pickupPoints.get(i);
				Point delivery = deliveryPoints.get(i);
				ix++;
				//add the request to end route
				boolean added = false;
				int vio_cur = S.violations();
				for(int r = 1; r <= XR.getNbRoutes(); r++){
					Point end = XR.endPoint(r);
					Point pre_end = XR.prev(end);
	
					mgr.performAddOnePoint(pickup, pre_end);
					mgr.performAddOnePoint(delivery, pickup);
	
					if(S.violations() > vio_cur){
						mgr.performRemoveOnePoint(delivery);
						mgr.performRemoveOnePoint(pickup);
					}
					else{
						added = true;
						System.out.println("ix = " + ix + ", add = " + added);
						break;
					}
				}
				if(!added){
					rejectPoints.add(pickup);
					rejectPoints.add(delivery);
					rejectPickup.add(pickup);
					rejectDelivery.add(delivery);
					typeOfRejectPoint.add(type.get(i));
					System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
				}
			}
		}
		else if(typeInit == 2){
			ix = 0;
			//add the people request to end route
			for(Point pickup : pickup2DeliveryOfPeople.keySet()){
				Point delivery = pickup2DeliveryOfPeople.get(pickup);
				
				ix++;
				boolean added = false;
				int vio_cur = S.violations();
				for(int r = 1; r <= XR.getNbRoutes(); r++){
					Point end = XR.endPoint(r);
					Point pre_end = XR.prev(end);
	
					mgr.performAddOnePoint(pickup, pre_end);
					mgr.performAddOnePoint(delivery, pickup);
	
					if(S.violations() > vio_cur){
						mgr.performRemoveOnePoint(delivery);
						mgr.performRemoveOnePoint(pickup);
					}
					else{
						System.out.println("ix = " + ix + ", add = " + added);
						added = true;
						break;
					}
				}

				if(!added){
					rejectPoints.add(pickup);
					rejectPoints.add(delivery);
					rejectPickup.add(pickup);
					rejectDelivery.add(delivery);
				}
			}
			
			//add the parcel request to route
			Set<Point> peoplePickup = pickup2DeliveryOfPeople.keySet();
			for(Point pickup : pickup2DeliveryOfGood.keySet()){
				Point delivery = pickup2DeliveryOfGood.get(pickup);
				ix++;
				boolean added = false;
				int vio_cur = S.violations();
				for(int r = 1; r <= XR.getNbRoutes(); r++){
					for(Point v = XR.getStartingPointOfRoute(r); v!= XR.getTerminatingPointOfRoute(r); v = XR.next(v)){
						//if(!peoplePickup.contains(v)){
							mgr.performAddOnePoint(pickup, v);
							for(Point u = pickup; u != XR.getTerminatingPointOfRoute(r); u = XR.next(u)){
								//if(!peoplePickup.contains(u)){
									mgr.performAddOnePoint(delivery, u);
									if(S.violations() > vio_cur){
										mgr.performRemoveOnePoint(delivery);
									}
									else{
										added = true;
										System.out.println("ix = " + ix + ", add = " + added);
										break;
									}
								//}
							}
							if(added)
								break;
							else
								mgr.performRemoveOnePoint(pickup);
						//}	
					}
					if(added)
						break;
				}
				if(!added){
					rejectPoints.add(pickup);
					rejectPoints.add(delivery);
					rejectPickup.add(pickup);
					rejectDelivery.add(delivery);
				}
			}
		}
		
		else if(typeInit == 3){
			ix = 0;
			Set<Point> peoplePickup = pickup2DeliveryOfPeople.keySet();
			for(int i = 0; i < pickupPoints.size(); i++){
				Point pickup = pickupPoints.get(i);
				Point delivery = deliveryPoints.get(i);
				ix++;
				//add the request to end route
				boolean added = false;
				int vio_cur = S.violations();
				for(int r = 1; r <= XR.getNbRoutes(); r++){
					for(Point v = XR.getStartingPointOfRoute(r); v!= XR.getTerminatingPointOfRoute(r); v = XR.next(v)){
						//if(!peoplePickup.contains(v)){
							mgr.performAddOnePoint(pickup, v);
							for(Point u = pickup; u != XR.getTerminatingPointOfRoute(r); u = XR.next(u)){
								//if(!peoplePickup.contains(u)){
									mgr.performAddOnePoint(delivery, u);
									if(S.violations() > vio_cur){
										mgr.performRemoveOnePoint(delivery);
									}
									else{
										added = true;
										System.out.println("ix = " + ix + ", add = " + added);
										break;
									}
								//}
							}
							if(added)
								break;
							else
								mgr.performRemoveOnePoint(pickup);
						//}	
					}
					if(added)
						break;
				}
				if(!added){
					rejectPoints.add(pickup);
					rejectPoints.add(delivery);
					rejectPickup.add(pickup);
					rejectDelivery.add(delivery);
					typeOfRejectPoint.add(type.get(i));
					System.out.println("reject request: " + i + "reject size = " + rejectPickup.size());
				}
			}
		}
		
		valueSolution = new LexMultiValues();
		valueSolution.add(S.violations());
		valueSolution.add(objective.getValue());
		System.out.println("vio = " + S.violations() + ", value = " + objective.getValue());
		System.out.println("rejected reqs = " + rejectPickup.size());
		return XR;
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
    	Info info = new Info("data/vrpData/n4111r1000_1" + ".txt");
    	//sar.test();
    	for(int typeInit = 2; typeInit <= 3; typeInit++){	    	
	    	for(int S = 1; S <= 1; S++)
	    	{
	    		ShareARide sar = new ShareARide(info);
	        	sar.stateModel();
		    	sar.greedyConstructiveSearch(typeInit);
		    	PrintWriter out = new PrintWriter("output/N4111_R1000_D1_type" + typeInit + "_S" + S + ".txt");
	        	LexMultiValues v = sar.valueSolution;
	        	out.println("first violationss = " + v.get(0) + ", first obj = " + v.get(1));
	        	out.println("rejected reqs: " + sar.rejectPickup.size());
	        	out.println(sar.XR.toString());
	        	sar.search(1000, 1300, S);
	        	v = sar.valueSolution;
	        	out.println("new vio = " + v.get(0)+ ", new obj = " + v.get(1));
	        	out.println("rejected reqs: " + sar.rejectPickup.size());
	        	out.println(sar.XR.toString());
		    	out.close();
	    	}
    	}
    }
    
    
   
    
}

