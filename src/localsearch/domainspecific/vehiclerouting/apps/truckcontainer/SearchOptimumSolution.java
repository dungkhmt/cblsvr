package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.ShareARide;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Mooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Truck;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;

public class SearchOptimumSolution {
	TruckContainerSolver tcs;
	
	private int lower_removal;
	private int upper_removal;
	private double shaw1st = 0.5;
	private double shaw2nd = 0.2;
	private double shaw3rd = 0.1;
	
	public SearchOptimumSolution(TruckContainerSolver tcs){
		super();
		this.tcs = tcs;
		
		lower_removal = (int) 1*(tcs.nRequest)/100;
		upper_removal = (int) tcs.nRequest/2;
	}
	
	public void allRemoval(){
		System.out.println("all removal");
		tcs.mgr.performRemoveAllClientPoints();
		for(int i = 0; i < tcs.pickupPoints.size(); i++){
			Point pickup = tcs.pickupPoints.get(i);
			if(!tcs.rejectPickupPoints.contains(pickup)){
				tcs.rejectPickupPoints.add(pickup);
				tcs.rejectDeliveryPoints.add(tcs.pickup2Delivery.get(pickup));
			}
		}
		for(int k : tcs.group2marked.keySet())
			tcs.group2marked.put(k, 0);
	}
	
	public void routeRemoval(){
		Random r = new Random();
		int k = r.nextInt(tcs.XR.getNbRoutes()) + 1;
		System.out.println("routeRemoval: index of removed route = " + k);
		Point x = tcs.XR.getStartingPointOfRoute(k);
		Point next_x = tcs.XR.next(x);
		while(next_x != tcs.XR.getTerminatingPointOfRoute(k)){
			x = next_x;
			next_x = tcs.XR.next(x);
			tcs.mgr.performRemoveOnePoint(x);
			tcs.group2marked.put(tcs.point2Group.get(x), 0);
			if(tcs.point2Type.get(x) != tcs.START_MOOC
				&& tcs.point2Type.get(x) != tcs.END_MOOC){	
				if(tcs.pickup2Delivery.keySet().contains(x))
					tcs.rejectPickupPoints.add(x);
				else 
					tcs.rejectDeliveryPoints.add(x);
			}
		}
	}

	public void randomRequestRemoval(){
		Random r = new Random();
		int n = r.nextInt(tcs.pickupPoints.size()) + 1;
		System.out.println("randomReqRemoval:number of removed request = " + n);
		if(n >= tcs.pickupPoints.size()){
			tcs.mgr.performRemoveAllClientPoints();
			for(int i = 0; i < tcs.pickupPoints.size(); i++){
				Point pickup = tcs.pickupPoints.get(i);
				if(!tcs.rejectPickupPoints.contains(pickup)){
					tcs.rejectPickupPoints.add(pickup);
					tcs.rejectDeliveryPoints.add(tcs.pickup2Delivery.get(pickup));
				}
			}
			for(int k : tcs.group2marked.keySet())
				tcs.group2marked.put(k, 0);
		}
		else{
			int i = 0;
			while(i < n){
				if(tcs.rejectPickupPoints.size() == tcs.pickupPoints.size())
					break;
				int rand = r.nextInt(tcs.pickupPoints.size());
				Point pickup = tcs.pickupPoints.get(rand);
				
				if(tcs.XR.route(pickup) == Constants.NULL_POINT)
					continue;
				Point delivery = tcs.pickup2Delivery.get(tcs.pickupPoints.get(rand));
				tcs.mgr.performRemoveTwoPoints(pickup, delivery);
				tcs.rejectPickupPoints.add(pickup);
				tcs.rejectDeliveryPoints.add(delivery);
				tcs.group2marked.put(tcs.point2Group.get(pickup), 0);
				tcs.group2marked.put(tcs.point2Group.get(delivery), 0);
				i++;
			}
		}
	}
	
	public void shaw_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		System.out.println("Shaw removal : number of request removed = " + nRemove);
		
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1 = null;
		do{
			ipRemove = R.nextInt(tcs.pickupPoints.size());
			r1 = tcs.pickupPoints.get(ipRemove);	
		}while(tcs.rejectPickupPoints.contains(r1));
		
		Point dr1 = tcs.pickup2Delivery.get(r1);
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove && r1 != null && dr1 != null){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			int routeOfR1 = tcs.XR.route(r1);
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			double arrivalTimeR1 = tcs.eat.getEarliestArrivalTime(tcs.XR.prev(r1))+
					tcs.serviceDuration.get(tcs.XR.prev(r1))+
					tcs.awm.getDistance(tcs.XR.prev(r1), r1);
			
			double serviceTimeR1 = 1.0*tcs.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + tcs.serviceDuration.get(r1);
			
			double arrivalTimeDR1 = tcs.eat.getEarliestArrivalTime(tcs.XR.prev(dr1))+
					tcs.serviceDuration.get(tcs.XR.prev(dr1))+
					tcs.awm.getDistance(tcs.XR.prev(dr1), dr1);
			
			double serviceTimeDR1 = 1.0*tcs.earliestAllowedArrivalTime.get(dr1);
			serviceTimeDR1 = arrivalTimeDR1 > serviceTimeDR1 ? arrivalTimeDR1 : serviceTimeDR1;
			
			double depatureTimeDR1 = serviceTimeDR1 + tcs.serviceDuration.get(dr1);
			
			tcs.rejectPickupPoints.add(r1);
			tcs.rejectDeliveryPoints.add(dr1);
			
			tcs.mgr.performRemoveTwoPoints(r1, dr1);
			tcs.group2marked.put(tcs.point2Group.get(r1), 0);
			tcs.group2marked.put(tcs.point2Group.get(dr1), 0);
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=tcs.XR.getNbRoutes(); k++){
				Point x = tcs.XR.getStartingPointOfRoute(k);
				for(x = tcs.XR.next(x); x != tcs.XR.getTerminatingPointOfRoute(k); x = tcs.XR.next(x)){

					Point dX = tcs.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute arrival time of x and its delivery dX
					 */
					double arrivalTimeX = tcs.eat.getEarliestArrivalTime(tcs.XR.prev(x))+
							tcs.serviceDuration.get(tcs.XR.prev(x))+
							tcs.awm.getDistance(tcs.XR.prev(x), x);
					
					double serviceTimeX = 1.0*tcs.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + tcs.serviceDuration.get(x);
					
					double arrivalTimeDX =  tcs.eat.getEarliestArrivalTime(tcs.XR.prev(dX))+
							tcs.serviceDuration.get(tcs.XR.prev(dX))+
							tcs.awm.getDistance(tcs.XR.prev(dX), dX);
					
					double serviceTimeDX = 1.0*tcs.earliestAllowedArrivalTime.get(dX);
					serviceTimeDX = arrivalTimeDX > serviceTimeDX ? arrivalTimeDX : serviceTimeDX;
					
					double depatureTimeDX = serviceTimeDX + tcs.serviceDuration.get(dX);
					
					/*
					 * Compute related between r1 and x
					 */
					int lr1x;
					if(routeOfR1 == k){
						lr1x = 1;
					}else{
						lr1x = -1;
					}
					
					double related = shaw1st*(tcs.awm.getDistance(r1, x) + tcs.awm.getDistance(dX, dr1))+
							shaw2nd*(Math.abs(depatureTimeR1-depatureTimeX) + Math.abs(depatureTimeDX-depatureTimeDR1))+
							shaw3rd*lr1x;
					if(related < relatedMin){
						relatedMin = related;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			r1 = removedPickup;
			dr1 = removedDelivery;
		}
		
	}
	
	public void worst_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		System.out.println("worstRemoval: nRemove = " + nRemove);
		
		int inRemove = 0;
		while(inRemove++ != nRemove){
			if(tcs.rejectPickupPoints.size() == tcs.pickupPoints.size())
				break;
			double maxCost = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=tcs.XR.getNbRoutes(); k++){
				Point x = tcs.XR.getStartingPointOfRoute(k);
				for(x = tcs.XR.next(x); x != tcs.XR.getTerminatingPointOfRoute(k); x = tcs.XR.next(x)){
					
//					if(!removeAllowed.get(x))
//						continue;
					
					Point dX = tcs.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double cost = tcs.objective.evaluateRemoveTwoPoints(x, dX);
					if(cost > maxCost){
						maxCost = cost;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			if(removedDelivery == null || removedPickup == null)
				break;
			
			tcs.rejectPickupPoints.add(removedPickup);
			tcs.rejectDeliveryPoints.add(removedDelivery);
			tcs.group2marked.put(tcs.point2Group.get(removedPickup), 0);
			tcs.group2marked.put(tcs.point2Group.get(removedDelivery), 0);
//			nChosed.put(removedDelivery, nChosed.get(removedDelivery)+1);
//			nChosed.put(removedPickup, nChosed.get(removedPickup)+1);
			
			
			tcs.mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	
	public void greedyInsertion(){
		System.out.println("greedyInsertion");
		HashMap<Truck, Integer> truck2marked = new HashMap<Truck, Integer>();
		Truck[] trucks = tcs.input.getTrucks();
		for(int i = 0; i < trucks.length; i++)
			truck2marked.put(trucks[i], 0);
		for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
			if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) > 1){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
			}
		}
		
		for(int i = 0; i < tcs.rejectPickupPoints.size(); i++){
			Point pickup = tcs.rejectPickupPoints.get(i);
			int groupId = tcs.point2Group.get(pickup);
			
			if(tcs.XR.route(pickup) != Constants.NULL_POINT
				|| tcs.group2marked.get(groupId) == 1)
				continue;
			Point delivery = tcs.pickup2Delivery.get(pickup);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);

				if(truck2marked.get(truck) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
					if(tcs.S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
						if(tcs.S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
//						System.out.println("pick = " + pickup.getID()
//							+ ", p = " + p.getID()
//							+ ", del = " + delivery.getID()
//							+ ", q = " + q.getID());
//						System.out.println("s vio = " + S.evaluateAddTwoPoints(pickup, p, delivery, q));
//						System.out.println("arrTime p = " + DateTimeUtils.unixTimeStamp2DateTime((long)eat.getEarliestAllowedArrivalTime(p)));
//						System.out.println("travelTime p ->pick = " + eat.getTravelTime(p, pickup));
//						System.out.println("arrTime q = " + DateTimeUtils.unixTimeStamp2DateTime((long)eat.getEarliestAllowedArrivalTime(q)));
//						System.out.println("travelTime q ->del = " + eat.getTravelTime(p, delivery));
//						System.out.println("acc = " +  accContainerInvr.getSumWeights(p));
//						System.out.println("weight at pickup = " + accContainerInvr.getWeights(pickup));
						if(tcs.S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = tcs.objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							if( cost < best_objective){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
							}
						}
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				tcs.mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				Point st = tcs.XR.getStartingPointOfRoute(tcs.XR.route(pre_pick));
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
				tcs.rejectPickupPoints.remove(pickup);
				tcs.rejectDeliveryPoints.remove(delivery);
				tcs.group2marked.put(groupId, 1);
				i--;
			}
		}
	}
	
	public void greedyInsertionWithNoise(){
		System.out.println("greedyInsertionWithNoise");
		HashMap<Truck, Integer> truck2marked = new HashMap<Truck, Integer>();
		Truck[] trucks = tcs.input.getTrucks();
		for(int i = 0; i < trucks.length; i++)
			truck2marked.put(trucks[i], 0);
		for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
			if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) > 1){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
			}
		}
		
		for(int i = 0; i < tcs.rejectPickupPoints.size(); i++){
			Point pickup = tcs.rejectPickupPoints.get(i);
			int groupId = tcs.point2Group.get(pickup);
			
			if(tcs.XR.route(pickup) != Constants.NULL_POINT
				|| tcs.group2marked.get(groupId) == 1)
				continue;
			Point delivery = tcs.pickup2Delivery.get(pickup);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);

				if(truck2marked.get(truck) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
					if(tcs.S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
						if(tcs.S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						
						if(tcs.S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = tcs.objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							double ran = Math.random()*2-1;
							cost += TruckContainerSolver.MAX_TRAVELTIME*0.1*ran;
							if( cost < best_objective){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
							}
						}
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				tcs.mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				Point st = tcs.XR.getStartingPointOfRoute(tcs.XR.route(pre_pick));
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
				tcs.rejectPickupPoints.remove(pickup);
				tcs.rejectDeliveryPoints.remove(delivery);
				tcs.group2marked.put(groupId, 1);
				i--;
			}
		}
	}
	
	public void regret_n_insertion(int n){
		System.out.println("regret insertion n = " + n);
		HashMap<Truck, Integer> truck2marked = new HashMap<Truck, Integer>();
		Truck[] trucks = tcs.input.getTrucks();
		for(int i = 0; i < trucks.length; i++)
			truck2marked.put(trucks[i], 0);
		for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
			if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) > 1){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
			}
		}
		
		for(int i = 0; i < tcs.rejectPickupPoints.size(); i++){
			Point pickup = tcs.rejectPickupPoints.get(i);
			int groupId = tcs.point2Group.get(pickup);
			
			if(tcs.XR.route(pickup) != Constants.NULL_POINT
				|| tcs.group2marked.get(groupId) == 1)
				continue;
			Point delivery = tcs.pickup2Delivery.get(pickup);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double n_best_objective[] = new double[n];
			double best_regret_value = Double.MIN_VALUE;
			
			for(int it=0; it<n; it++){
				n_best_objective[it] = Double.MAX_VALUE;
			}

			for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);

				if(truck2marked.get(truck) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
					if(tcs.S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
						if(tcs.S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						
						if(tcs.S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = tcs.objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							for(int it=0; it<n; it++){
								if(n_best_objective[it] > cost){
									for(int it2 = n-1; it2 > it; it2--){
										n_best_objective[it2] = n_best_objective[it2-1];
									}
									n_best_objective[it] = cost;
									break;
								}
							}
							double regret_value = 0;
							for(int it=1; it<n; it++){
								regret_value += Math.abs(n_best_objective[it] - n_best_objective[0]);
							}
							if(regret_value > best_regret_value){
								best_regret_value = regret_value;
								pre_pick = p;
								pre_delivery = q;
							}
						}
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				tcs.mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				Point st = tcs.XR.getStartingPointOfRoute(tcs.XR.route(pre_pick));
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
				tcs.rejectPickupPoints.remove(pickup);
				tcs.rejectDeliveryPoints.remove(delivery);
				tcs.group2marked.put(groupId, 1);
				i--;
			}
		}
	}
	
	public void first_possible_insertion(){
		System.out.println("first_possible_insertion");
		HashMap<Truck, Integer> truck2marked = new HashMap<Truck, Integer>();
		Truck[] trucks = tcs.input.getTrucks();
		for(int i = 0; i < trucks.length; i++)
			truck2marked.put(trucks[i], 0);
		for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
			if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) > 1){
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);
				truck2marked.put(truck, 1);
			}
		}
		
		for(int i = 0; i < tcs.rejectPickupPoints.size(); i++){
			Point pickup = tcs.rejectPickupPoints.get(i);
			int groupId = tcs.point2Group.get(pickup);
			
			if(tcs.XR.route(pickup) != Constants.NULL_POINT
				|| tcs.group2marked.get(groupId) == 1)
				continue;
			Point delivery = tcs.pickup2Delivery.get(pickup);
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double best_objective = Double.MAX_VALUE;
			boolean finded = false;
			for(int r = 1; r <= tcs.XR.getNbRoutes(); r++){
				if(finded)
					break;
				Point st = tcs.XR.getStartingPointOfRoute(r);
				Truck truck = tcs.startPoint2Truck.get(st);

				if(truck2marked.get(truck) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
					if(finded)
						break;
					if(tcs.S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
						if(tcs.S.evaluateAddOnePoint(delivery, q) > 0)
							continue;
						if(tcs.S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
							double cost = tcs.objective.evaluateAddTwoPoints(pickup, p, delivery, q);
							if( cost < best_objective){
								tcs.mgr.performAddTwoPoints(pickup, p, delivery, q);
								truck2marked.put(truck, 1);
								tcs.rejectPickupPoints.remove(pickup);
								tcs.rejectDeliveryPoints.remove(delivery);
								tcs.group2marked.put(groupId, 1);
								finded = true;
								i--;
								break;
							}
						}
					}
				}
			}
		}
	}
}
