package localsearch.domainspecific.vehiclerouting.apps.sharedaride.Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import localsearch.domainspecific.vehiclerouting.apps.sharedaride.ShareARide;
import localsearch.domainspecific.vehiclerouting.apps.sharedaride.SolutionShareARide;
import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.EarliestArrivalTimeVR;

public class ALNSwithSA {
	
	private VRManager mgr;
	private VarRoutesVR XR;
	private ConstraintSystemVR S;
	private IFunctionVR objective;
	private EarliestArrivalTimeVR eat;
	private ArcWeightsManager awm;
	
	private int nRemovalOperators=7;
	private int nInsertionOperators=2;
	
	//parameters
	private int lower_removal = (int) 2.5*(ShareARide.nRequest)/100;
	private int upper_removal = (int) 25*(ShareARide.nRequest)/100;
	private int sigma1 = 1;
	private int sigma2 = 3;
	private int sigma3 = 5;
	private double rp = 0.1;
	private int nw = 10;
	private double shaw1st = 0.5;
	private double shaw2nd = 0.2;
	private double shaw3rd = 0.1;
	private double temperature = 200;
	private double cooling_rate = 0.9995;
	//private double shaw4th = 0.2;
	
	
	public ALNSwithSA(VRManager mgr, IFunctionVR objective, ConstraintSystemVR S, EarliestArrivalTimeVR eat, ArcWeightsManager awm){
		this.mgr = mgr;
		this.objective = objective;
		this.XR = mgr.getVarRoutesVR();
		this.S = S;
		this.eat = eat;
		this.awm = awm;
	}
	
	public SolutionShareARide search(int maxIter, int timeLimit){
		//insertion operators selection probabilities
		double[] pti = new double[nInsertionOperators];
		//removal operators selection probabilities
		double[] ptd = new double[nRemovalOperators];
		
		//wi - number of times used during last iteration
		int[] wi = new int[nInsertionOperators];
		int[] wd = new int[nRemovalOperators];
		
		//pi_i - score of operator
		int[] si = new int[nInsertionOperators];
		int[] sd = new int[nRemovalOperators];
		
		
		//init probabilites
		for(int i=0; i<nInsertionOperators; i++){
			pti[i] = 1.0/nInsertionOperators;
			wi[i] = 1;
			si[i] = 0;
		}
		for(int i=0; i<nRemovalOperators; i++){
			ptd[i] = 1.0/nRemovalOperators;
			wd[i] = 1;
			sd[i] = 0;
		}
		
		int it = 0;
		
		double best_cost = objective.getValue();
		SolutionShareARide best_solution = new SolutionShareARide(XR, ShareARide.rejectPoints, ShareARide.rejectPickup, ShareARide.rejectDelivery, best_cost);
		ShareARide.LOGGER.log(Level.INFO, "start search best_solution: \n"+best_solution.toString());
		
		while(it++ < maxIter){
			
			double current_cost = objective.getValue();
			SolutionShareARide current_solution = new SolutionShareARide(XR, ShareARide.rejectPoints, ShareARide.rejectPickup, ShareARide.rejectDelivery, current_cost);
			ShareARide.LOGGER.log(Level.INFO, "Iter "+it+" current_solution: \n"+current_solution.toString());
			
			int i_selected_removal = get_operator(ptd);
			wd[i_selected_removal]++;
			/*
			 * Select remove operator
			 */
			//ShareARide.LOGGER.log(Level.INFO,"selected removal operator = "+i_selected_removal);
			switch(i_selected_removal){
			
				case 0: random_removal(); break;
				case 1: route_removal(); break;
				case 2: late_arrival_removal(); break;
				case 3: shaw_removal(); break;
				case 4: proximity_based_removal(); break;
				case 5: time_based_removal(); break;
				case 6: worst_removal(); break;
			}
			int i_selected_insertion = get_operator(pti);
			wi[i_selected_insertion]++;
			//ShareARide.LOGGER.log(Level.INFO,"selected insertion operator = "+i_selected_insertion);
			/*
			 * Select insertion operator
			 */
			switch(i_selected_insertion){
				
				case 0: greedy_insertion(); break;
				case 1: greedy_insertion_noise_function(); break;
			}
			double new_cost = objective.getValue();
			ShareARide.LOGGER.log(Level.INFO,"Iter "+it+" new_solution: \n"+XR.toString()+"cost = "+new_cost);
			
			
			/*
			 * if new solution has cost better than current solution
			 * 		update current solution =  new solution
			 * 		if new solution has best cost
			 * 			update best cost
			 */
			int new_nb_reject_points = ShareARide.rejectPickup.size();
			int current_nb_reject_points = current_solution.get_rejectPickupPoints().size();
			if( new_nb_reject_points < current_nb_reject_points
					|| (new_nb_reject_points == current_nb_reject_points && new_cost < current_cost)){
				
				int best_nb_reject_points = best_solution.get_rejectPickupPoints().size();
				
				if(new_nb_reject_points < best_nb_reject_points
						|| (new_nb_reject_points == best_nb_reject_points && new_cost < best_cost)){
					
					best_cost = new_cost;
					best_solution = new SolutionShareARide(XR, ShareARide.rejectPoints, ShareARide.rejectPickup, ShareARide.rejectDelivery, best_cost);
					
					ShareARide.LOGGER.log(Level.INFO,"Iter "+it+" find the best solution with number of reject points = "+best_solution.get_rejectPickupPoints().size()+"  cost = "+best_solution.get_cost());
					
					si[i_selected_insertion] += sigma1;
					sd[i_selected_removal] += sigma1;
					
				}else{
					si[i_selected_insertion] += sigma2;
					sd[i_selected_removal] += sigma2;
				}
			}
			/*
			 * if new solution has cost worst than current solution
			 * 		because XR is new solution
			 * 			copy current current solution to new solution if don't change solution
			 */
			else{
				si[i_selected_insertion] += sigma3;
				sd[i_selected_removal] += sigma3;
				
				double v = Math.exp(-(new_cost-current_cost)/temperature);
				double r = Math.random();
				if(r >= v){
					ShareARide.LOGGER.log(Level.INFO,"The cost did not improve and reverse solution back to current solution");
					current_solution.copy2XR(XR);
					ShareARide.rejectPoints = current_solution.get_rejectPoints();
					ShareARide.rejectPickup = current_solution.get_rejectPickupPoints();
					ShareARide.rejectDelivery = current_solution.get_rejectDeliveryPoints();
				}
			}
			
			temperature = cooling_rate*temperature;
			
			//update probabilities
			if(it % nw == 0){
				for(int i=0; i<nInsertionOperators; i++){
					pti[i] = pti[i]*(1-rp) + rp*si[i]/wi[i];
					wi[i] = 1;
					si[i] = 0;
				}
				
				for(int i=0; i<nRemovalOperators; i++){
					ptd[i] = ptd[i]*(1-rp) + rp*sd[i]/wd[i];
					wd[i] = 1;
					sd[i] = 0;
				}
			}
		}
		return best_solution;
	}
	
	private void random_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		ArrayList<Point> clientPoints = XR.getClientPoints();
		Collections.shuffle(clientPoints);
		
		int inRemove = 0 ;
		for(int i=0; i<clientPoints.size(); i++){
			if(inRemove == nRemove)
				break;
			Point pr1 = clientPoints.get(i);
			if(ShareARide.rejectPoints.contains(pr1))
				continue;
			//out.println("pr1 = "+pr1.getID());
			Point pr2 = ShareARide.pickup2Delivery.get(pr1);
			boolean pr2IsDelivery = true;
			if(pr2 == null){
				//out.println("pr2 null");
				pr2 = ShareARide.delivery2Pickup.get(pr1);
				pr2IsDelivery = false;
			}
			//System.out.println("pr2 = "+pr2.getID());
			/*if(S.evaluateRemoveTwoPoints(pr1,pr2) != 0){
				System.out.println("iter "+i+"  invalid   "+pr1.getID()+"  "+pr2.getID());
				continue;
			}*/
			//System.out.println("iter "+i+"  Remove "+pr1.getID()+"  "+pr2.getID());
			inRemove++;

			if(pr2IsDelivery){
				mgr.performRemoveTwoPoints(pr1, pr2);
			}else{
				mgr.performRemoveTwoPoints(pr2, pr1);
			}
		
			ShareARide.rejectPoints.add(pr1);
			ShareARide.rejectPoints.add(pr2);
			if(pr2IsDelivery){
				ShareARide.rejectDelivery.add(pr2);
				ShareARide.rejectPickup.add(pr1);
			}else{
				ShareARide.rejectDelivery.add(pr1);
				ShareARide.rejectPickup.add(pr2);
			}
		}
		if(inRemove == 0){
			ShareARide.LOGGER.log(Level.INFO,"Can't remove any client points");
		}
		//System.out.println("random_removal done");
	}
	
	private void route_removal(){
		int K = XR.getNbRoutes();
		Random R = new Random();
		int iRouteRemoval = R.nextInt(K)+1;
		ShareARide.LOGGER.log(Level.INFO,"index of route removed = "+iRouteRemoval);
		
		Point x = XR.getStartingPointOfRoute(iRouteRemoval);
		Point next_x = XR.next(x);
		while(next_x != XR.getTerminatingPointOfRoute(iRouteRemoval)){
			x = next_x;
			next_x = XR.next(x);
			ShareARide.rejectPoints.add(x);
			if(ShareARide.pickup2Delivery.containsKey(x)){
				ShareARide.rejectPickup.add(x);
			}else{
				ShareARide.rejectDelivery.add(x);
			}
			mgr.performRemoveOnePoint(x);
		}	
	}
	
	private void late_arrival_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int iRemove = 0;
		while(iRemove++ != nRemove){
			
			double deviationMax = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double arrivalTime = eat.getEarliestArrivalTime(XR.prev(x))+ 
							ShareARide.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					
					double serviceTime = 1.0*ShareARide.earliestAllowedArrivalTime.get(x);
					serviceTime = arrivalTime > serviceTime ? arrivalTime : serviceTime;
					
					double depatureTime = serviceTime + ShareARide.serviceDuration.get(x);
					
					double arrivalTimeD =  eat.getEarliestArrivalTime(XR.prev(dX))+ 
							ShareARide.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeD = 1.0*ShareARide.earliestAllowedArrivalTime.get(dX);
					serviceTime = arrivalTimeD > serviceTimeD ? arrivalTimeD : serviceTimeD;
					
					double depatureTimeD = serviceTimeD + ShareARide.serviceDuration.get(dX);
					
					double deviation = depatureTime - arrivalTime + depatureTimeD - arrivalTimeD;
					if(deviation > deviationMax){
						deviationMax = deviation;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			ShareARide.rejectPoints.add(removedDelivery);
			ShareARide.rejectPoints.add(removedPickup);
			ShareARide.rejectPickup.add(removedPickup);
			ShareARide.rejectDelivery.add(removedDelivery);
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	
	/*
	private void worst_distance_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int iRemove = 0;
		while(iRemove++ != nRemove){
			double distanceMax = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double distance = awm.getDistance(x, XR.prev(x)) + awm.getDistance(x, XR.next(x)) 
							+ awm.getDistance(dX, XR.prev(dX)) + awm.getDistance(dX, XR.next(dX));
					
					if(distance > distanceMax){
						distanceMax = distance;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			ShareARide.rejectPoints.add(removedDelivery);
			ShareARide.rejectPoints.add(removedPickup);
			ShareARide.rejectPickup.add(removedPickup);
			ShareARide.rejectDelivery.add(removedDelivery);
			
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	*/

	private void shaw_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(ShareARide.rejectPoints.contains(r1));
		
		Point dr1;
		boolean isPickup = ShareARide.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = ShareARide.pickup2Delivery.get(r1);
		}else{
			Point tmp = ShareARide.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			int routeOfR1 = XR.route(r1);
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			double arrivalTimeR1 = eat.getEarliestArrivalTime(XR.prev(r1))+
					ShareARide.serviceDuration.get(XR.prev(r1))+
					awm.getDistance(XR.prev(r1), r1);
			
			double serviceTimeR1 = 1.0*ShareARide.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + ShareARide.serviceDuration.get(r1);
			
			double arrivalTimeDR1 = eat.getEarliestArrivalTime(XR.prev(dr1))+
					ShareARide.serviceDuration.get(XR.prev(dr1))+
					awm.getDistance(XR.prev(dr1), dr1);
			
			double serviceTimeDR1 = 1.0*ShareARide.earliestAllowedArrivalTime.get(dr1);
			serviceTimeDR1 = arrivalTimeDR1 > serviceTimeDR1 ? arrivalTimeDR1 : serviceTimeDR1;
			
			double depatureTimeDR1 = serviceTimeDR1 + ShareARide.serviceDuration.get(dr1);
			
			ShareARide.rejectPoints.add(r1);
			ShareARide.rejectPoints.add(dr1);
			ShareARide.rejectPickup.add(r1);
			ShareARide.rejectDelivery.add(dr1);
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute arrival time of x and its delivery dX
					 */
					double arrivalTimeX =  eat.getEarliestArrivalTime(XR.prev(x))+
							ShareARide.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					double serviceTimeX = 1.0*ShareARide.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + ShareARide.serviceDuration.get(x);
					
					double arrivalTimeDX =  eat.getEarliestArrivalTime(XR.prev(dX))+
							ShareARide.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeDX = 1.0*ShareARide.earliestAllowedArrivalTime.get(dX);
					serviceTimeDX = arrivalTimeDX > serviceTimeDX ? arrivalTimeDX : serviceTimeDX;
					
					double depatureTimeDX = serviceTimeDX + ShareARide.serviceDuration.get(dX);
					
					/*
					 * Compute related between r1 and x
					 */
					int lr1x;
					if(routeOfR1 == k){
						lr1x = 1;
					}else{
						lr1x = -1;
					}
					
					double related = shaw1st*(awm.getDistance(r1, x) + awm.getDistance(dX, dr1))+
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
	
	private void proximity_based_removal(){
		
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(ShareARide.rejectPoints.contains(r1));
		
		Point dr1;
		boolean isPickup = ShareARide.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = ShareARide.pickup2Delivery.get(r1);
		}else{
			Point tmp = ShareARide.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			ShareARide.rejectPoints.add(r1);
			ShareARide.rejectPoints.add(dr1);
			ShareARide.rejectPickup.add(r1);
			ShareARide.rejectDelivery.add(dr1);
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute related between r1 and x
					 */
					
					double related = shaw1st*(awm.getDistance(r1, x) + awm.getDistance(dX, dr1));
					
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
	
	private void time_based_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int ipRemove;
		
		/*
		 * select randomly request r1 and its delivery dr1
		 */
		Point r1;
		do{
			ipRemove = R.nextInt(clientPoints.size());
			r1 = clientPoints.get(ipRemove);	
		}while(ShareARide.rejectPoints.contains(r1));
		
		Point dr1;
		boolean isPickup = ShareARide.pickup2Delivery.containsKey(r1);
		if(isPickup){
			dr1 = ShareARide.pickup2Delivery.get(r1);
		}else{
			Point tmp = ShareARide.delivery2Pickup.get(r1);
			dr1 = r1;
			r1 = tmp;
		}
		
		/*
		 * Remove request most related with r1
		 */
		int inRemove = 0;
		while(inRemove++ != nRemove){
			
			Point removedPickup = null;
			Point removedDelivery = null;
			double relatedMin =  Double.MAX_VALUE;
			
			/*
			 * Compute arrival time at request r1 and its delivery dr1
			 */
			double arrivalTimeR1 =  eat.getEarliestArrivalTime(XR.prev(r1))+
					ShareARide.serviceDuration.get(XR.prev(r1))+
					awm.getDistance(XR.prev(r1), r1);
			
			double serviceTimeR1 = 1.0*ShareARide.earliestAllowedArrivalTime.get(r1);
			serviceTimeR1 = arrivalTimeR1 > serviceTimeR1 ? arrivalTimeR1 : serviceTimeR1;
			
			double depatureTimeR1 = serviceTimeR1 + ShareARide.serviceDuration.get(r1);
			
			double arrivalTimeDR1 =  eat.getEarliestArrivalTime(XR.prev(dr1))+
					ShareARide.serviceDuration.get(XR.prev(dr1))+
					awm.getDistance(XR.prev(dr1), dr1);
			
			double serviceTimeDR1 = 1.0*ShareARide.earliestAllowedArrivalTime.get(dr1);
			serviceTimeDR1 = arrivalTimeDR1 > serviceTimeDR1 ? arrivalTimeDR1 : serviceTimeDR1;
			
			double depatureTimeDR1 = serviceTimeDR1 + ShareARide.serviceDuration.get(dr1);
			
			ShareARide.rejectPoints.add(r1);
			ShareARide.rejectPoints.add(dr1);
			ShareARide.rejectPickup.add(r1);
			ShareARide.rejectDelivery.add(dr1);
			
			mgr.performRemoveTwoPoints(r1, dr1);
			
			/*
			 * find the request is the most related with r1
			 */
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null)
						continue;
					
					/*
					 * Compute arrival time of x and its delivery dX
					 */
					double arrivalTimeX =  eat.getEarliestArrivalTime(XR.prev(x))+
							ShareARide.serviceDuration.get(XR.prev(x))+
							awm.getDistance(XR.prev(x), x);
					
					double serviceTimeX = 1.0*ShareARide.earliestAllowedArrivalTime.get(x);
					serviceTimeX = arrivalTimeX > serviceTimeX ? arrivalTimeX : serviceTimeX;
					
					double depatureTimeX = serviceTimeX + ShareARide.serviceDuration.get(x);
					
					double arrivalTimeDX =  eat.getEarliestArrivalTime(XR.prev(dX))+
							ShareARide.serviceDuration.get(XR.prev(dX))+
							awm.getDistance(XR.prev(dX), dX);
					
					double serviceTimeDX = 1.0*ShareARide.earliestAllowedArrivalTime.get(dX);
					serviceTimeDX = arrivalTimeDX > serviceTimeDX ? arrivalTimeDX : serviceTimeDX;
					
					double depatureTimeDX = serviceTimeDX + ShareARide.serviceDuration.get(dX);
					
					/*
					 * Compute related between r1 and x
					 */
					
					double related = shaw2nd*(Math.abs(depatureTimeR1-depatureTimeX) + Math.abs(depatureTimeDX-depatureTimeDR1));
					
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
	
	private void worst_removal(){
		Random R = new Random();
		int nRemove = R.nextInt(upper_removal-lower_removal+1) + lower_removal;
		
		ShareARide.LOGGER.log(Level.INFO,"number of request removed = "+nRemove);
		
		int inRemove = 0;
		while(inRemove++ != nRemove){
			
			double maxCost = Double.MIN_VALUE;
			Point removedPickup = null;
			Point removedDelivery = null;
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				Point x = XR.getStartingPointOfRoute(k);
				for(x = XR.next(x); x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
					
					Point dX = ShareARide.pickup2Delivery.get(x);
					if(dX == null){
						continue;
					}
					
					double cost = objective.evaluateRemoveTwoPoints(x, dX);
					if(cost > maxCost){
						maxCost = cost;
						removedPickup = x;
						removedDelivery = dX;
					}
				}
			}
			
			ShareARide.rejectPoints.add(removedDelivery);
			ShareARide.rejectPoints.add(removedPickup);
			ShareARide.rejectPickup.add(removedPickup);
			ShareARide.rejectDelivery.add(removedDelivery);
			
			mgr.performRemoveTwoPoints(removedPickup, removedDelivery);
		}
	}
	
 	private void greedy_insertion(){
		
		for(int i=0; i<ShareARide.rejectPickup.size(); i++){
			Point pickup = ShareARide.rejectPickup.get(i);
			Point delivery = ShareARide.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			boolean is_people = ShareARide.pickup2DeliveryOfPeople.containsKey(pickup);
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				for(Point p = XR.getStartingPointOfRoute(k); p != XR.getTerminatingPointOfRoute(k); p = XR.next(p)){
					//check constraint
					if(ShareARide.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;

					//if type of point is people
					if(is_people){
						if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
							//cost improve
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
							if( cost < best_objective){
								best_objective = cost;
								best_insertion_pickup = p;
								best_insertion_delivery = p;
							}
						}
					}
					//point is good
					else{
						for(Point q = p; q != XR.getTerminatingPointOfRoute(k); q = XR.next(q)){
							if(ShareARide.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
								continue;
							if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
								double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
								if(cost < best_objective){
									best_objective = cost;
									best_insertion_pickup = p;
									best_insertion_delivery = q;
								}
							}
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				ShareARide.rejectPickup.remove(pickup);
				ShareARide.rejectDelivery.remove(delivery);
				ShareARide.rejectPoints.remove(pickup);
				ShareARide.rejectPoints.remove(delivery);
				i--;
			}
		}
	}
	
 	private void greedy_insertion_noise_function(){
 		for(int i=0; i<ShareARide.rejectPickup.size(); i++){
			Point pickup = ShareARide.rejectPickup.get(i);
			Point delivery = ShareARide.pickup2Delivery.get(pickup);
			
			Point best_insertion_pickup = null;
			Point best_insertion_delivery = null;
			
			double best_objective = Double.MAX_VALUE;
			
			boolean is_people = ShareARide.pickup2DeliveryOfPeople.containsKey(pickup);
			
			for(int k=1; k<=XR.getNbRoutes(); k++){
				for(Point p = XR.getStartingPointOfRoute(k); p != XR.getTerminatingPointOfRoute(k); p = XR.next(p)){
					//check constraint
					if(ShareARide.pickup2DeliveryOfPeople.containsKey(p) || S.evaluateAddOnePoint(pickup, p) > 0)
						continue;
					
					//if type of point is people
					if(is_people){
						//check constraint
						if(S.evaluateAddTwoPoints(pickup, p, delivery, p) == 0){
							//cost improve
							double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, p);
							double r = Math.random()*2-1;
							cost += ShareARide.MAX_DISTANCE*0.1*r;
							if( cost < best_objective){
								best_objective = cost;
								best_insertion_pickup = p;
								best_insertion_delivery = p;
							}
						}
					}
					//point is good
					else{
						for(Point q = p; q != XR.getTerminatingPointOfRoute(k); q = XR.next(q)){
							if(ShareARide.pickup2DeliveryOfPeople.containsKey(q) || S.evaluateAddOnePoint(delivery, q) > 0)
								continue;
							
							if(S.evaluateAddTwoPoints(pickup, p, delivery, q) == 0){
								
								double cost = objective.evaluateAddTwoPoints(pickup, p, delivery, q);
								double r = Math.random()*2-1;
								cost += ShareARide.MAX_DISTANCE*0.1*r;
								if(cost < best_objective){
									best_objective = cost;
									best_insertion_pickup = p;
									best_insertion_delivery = q;
								}
							}
						}
					}
				}
			}
			
			if(best_insertion_pickup != null && best_insertion_delivery != null){
				mgr.performAddTwoPoints(pickup, best_insertion_pickup, delivery, best_insertion_delivery);
				ShareARide.rejectPickup.remove(pickup);
				ShareARide.rejectDelivery.remove(delivery);
				ShareARide.rejectPoints.remove(pickup);
				ShareARide.rejectPoints.remove(delivery);
				i--;
			}
		}
 	}
 	
	//roulette-wheel mechanism
 	private int get_operator(double[] p){
 		//String message = "probabilities input \n";
 		
 		int n = p.length;
		double[] s = new double[n];
		s[0] = 0+p[0];
		
		//String messagep = ("p = ["+p[0]+", ");
		//String messages = ("s = ["+s[0]+", ");
		
		for(int i=1; i<n; i++){
			//messagep += (p[i]+", ");
			s[i] = s[i-1]+p[i]; 
			//messages += (s[i]+", ");
		}
		//messagep += ("]");
		//messages += ("]");
		
		double r = s[n-1]*Math.random();
		//String messr = ("radom value = " + r);
		
		//message += (messagep +"\n" + messages + "\n" + messr);
		//ShareARide.LOGGER.log(Level.INFO,message);
		
		if(r>=0 && r <= s[0])
			return 0;
		
		for(int i=1; i<n; i++){
			if(r>=s[i-1] && r<=s[i])
				return i;
		}
		return -1;
	}
	
}
