package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.TruckContainerSolution;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;

public class SearchOptimumByLocalSearch {
	SolverWithLocalSearch tcs;
	
	public double temperature = 200;
	public double cooling_rate = 0.9995;
	
	public HashMap<Integer, Integer> opt2nbUsed;
	public HashMap<Integer, Integer> opt2eff;
	
	private boolean isOpt;
	private double best_objective;
	private int best_nbTrucks;
	private int best_nbReject;
	
	public int sigma1 = 5;
	public int sigma2 = 3;
	public int sigma3 = -1;
	public double rp = 0.1;
	public int nw = 1;
	
	public SearchOptimumByLocalSearch(SolverWithLocalSearch tcs){
		super();
		this.tcs = tcs;
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
	
	//roulette-wheel mechanism
 	private int get_operator(double[] p){
 		//String message = "probabilities input \n";
 		
 		int n = p.length;
		double[] s = new double[n];
		s[0] = 0+p[0];

		
		for(int i=1; i<n; i++)
			s[i] = s[i-1]+p[i]; 
		
		double r = s[n-1]*Math.random();
		
		if(r>=0 && r <= s[0])
			return 0;
		
		for(int i=1; i<n; i++){
			if(r>s[i-1] && r<=s[i])
				return i;
		}
		return -1;
	}
	
	public void explore(int maxIters, int maxTime, int maxStable, String outputfile){
		int it = 0;
		double t0 = System.currentTimeMillis()/1000;
		try{
			FileOutputStream write = new FileOutputStream(outputfile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("nRequests = " + tcs.nRequest + ", time limit = " + maxTime + ", nbIters = " + maxIters);
			fo.println("iter=====opt=====time=====cost=====nbReject=====nbTrucks=====vio");
			fo.println("0 -1 " + " " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) + " " + tcs.objective.getValue() 
					+ " " + tcs.getNbRejectedRequests() + " " + tcs.getNbUsedTrucks() + " " + tcs.S.violations());
			fo.close();
		}
		catch(Exception e){
			
		}
		int nbOpt = 5;
		opt2nbUsed = new HashMap<Integer, Integer>();
		opt2eff = new HashMap<Integer, Integer>();
		for(int i = 0; i < nbOpt; i++){
			opt2nbUsed.put(i, 0);
			opt2eff.put(i, 0);
		}
		
		System.out.println("current rejectsize = " + tcs.getNbRejectedRequests()
				+ ", current nbTrucks = " + tcs.getNbUsedTrucks()
				+ ", current cos = " + tcs.objective.getValue()
				+ ", current vio = " + tcs.S.violations());
		
		double[] pti = new double[nbOpt];
		int[] wi = new int[nbOpt];
		int[] si = new int[nbOpt];
		
		
		//init probabilites
		for(int i=0; i<nbOpt; i++){
			pti[i] = 1.0/nbOpt;
			wi[i] = 1;
			si[i] = 0;
		}
		
		int cnt = 0;
		TruckContainerSolution best_solution = new TruckContainerSolution(tcs.XR, tcs.rejectPickupPoints,
				tcs.rejectDeliveryPoints, tcs.objective.getValue(), tcs.getNbUsedTrucks(), tcs.getNbRejectedRequests(),
				tcs.point2Group, tcs.group2marked);
		
		while(it++ < maxIters && System.currentTimeMillis()/1000 - t0 < maxTime){
			if(cnt > maxStable){
				allRemoval();
				greedyInsertion();
				cnt = 0;
				int nR = tcs.getNbRejectedRequests();
				int nT = tcs.getNbUsedTrucks();
//				if(nR < best_solution.get_nbReject()
//					|| (nR == best_solution.get_nbReject() && tcs.objective.getValue() < best_solution.get_cost()))
				if(nR < best_solution.get_nbReject()
					|| (nR == best_solution.get_nbReject() && nT < best_solution.get_nbTrucks())
					|| (nR == best_solution.get_nbReject() && nT == best_solution.get_nbTrucks() 
					&& tcs.objective.getValue() < best_solution.get_cost())){
					best_solution = new TruckContainerSolution(tcs.XR, tcs.rejectPickupPoints,
					tcs.rejectDeliveryPoints, tcs.objective.getValue(), tcs.getNbUsedTrucks(), tcs.getNbRejectedRequests(),
					tcs.point2Group, tcs.group2marked);
					try{
						FileOutputStream write = new FileOutputStream(outputfile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(it + " -1 " + " " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) + " " + tcs.objective.getValue()
								+ " " + tcs.getNbRejectedRequests() + " " + tcs.getNbUsedTrucks() + " " + tcs.S.violations());
						fo.close();
					}
					catch(Exception e){
						
					}
				}
			}
			best_objective = tcs.objective.getValue();
			best_nbTrucks = tcs.getNbUsedTrucks();
			best_nbReject = tcs.getNbRejectedRequests();
			tcs.removeAllMoocFromRoutes();
			
			System.out.println(it + "/" + maxIters + ", best Truck = " + best_nbTrucks + ", best obj = " + best_objective + ", vio = "+ tcs.S.violations());
			isOpt = false;
			//int op = r.nextInt(nbOpt);
			int op = get_operator(pti);
			wi[op]++;
			switch(op){
				case 0: exploreForOneRequestMoveWithGreedyInsertion(); break;
				case 1: exploreForTwoRequestExchangeRoute(); break;
				case 2: exploreForTwoOptMove5(); break;
				case 3: exploreForCrossExchangeMove(); break;
				case 4: exploreForTwoRequestExchangeBestRoute();
			}
			tcs.insertMoocForAllRoutes();
			int nu;
			if(isOpt){
				int nR = tcs.getNbRejectedRequests();
				int nT = tcs.getNbUsedTrucks();
				double new_cost = tcs.objective.getValue();
//				if(nR < best_solution.get_nbReject()
//					|| (nR == best_solution.get_nbReject() && new_cost < best_solution.get_cost())){
				if(nR < best_solution.get_nbReject()
					|| (nR == best_solution.get_nbReject() && nT < best_solution.get_nbTrucks())
					|| (nR == best_solution.get_nbReject() && nT < best_solution.get_nbTrucks() 
					&& new_cost < best_solution.get_cost())){
					best_solution = new TruckContainerSolution(tcs.XR, tcs.rejectPickupPoints,
						tcs.rejectDeliveryPoints, tcs.objective.getValue(), tcs.getNbUsedTrucks(), tcs.getNbRejectedRequests(),
						tcs.point2Group, tcs.group2marked);
					si[op] += sigma1;
					nu = opt2eff.get(op) + 1;
					opt2eff.put(op, nu);
					try{
						FileOutputStream write = new FileOutputStream(outputfile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(it + " " + op + " " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000) + " " + tcs.objective.getValue()
								+ " " + tcs.getNbRejectedRequests() + " " + tcs.getNbUsedTrucks() + " " + tcs.S.violations());
						fo.close();
					}
					catch(Exception e){
						
					}
				}
				else
					si[op] += sigma2;
				cnt = 0;
			}
			else{
				si[op] += sigma3;
				cnt++;
			}
			nu = opt2nbUsed.get(op) + 1;
			opt2nbUsed.put(op, nu);
			
			if(it % nw == 0){
				for(int i=0; i<nbOpt; i++){
					pti[i] = Math.max(0.01, pti[i]*(1-rp) + rp*si[i]/wi[i]);
				}
			}
			greedyInsertion();
		}
		best_solution.copy2XR(tcs.XR);
		tcs.group2marked = best_solution.get_group2marked();
		tcs.point2Group = best_solution.get_point2Group();
		tcs.rejectPickupPoints = best_solution.get_rejectPickupPoints();
		tcs.rejectDeliveryPoints = best_solution.get_rejectDeliveryPoints();
		try{
			FileOutputStream write = new FileOutputStream(outputfile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println("lastIter====lastTime");
			fo.println(it + " " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()/1000));
			fo.println("nu1====nu2====nu3=====nu4");
			fo.println(opt2nbUsed.get(0) + " " + opt2nbUsed.get(1) + " " + opt2nbUsed.get(2) + " " + opt2nbUsed.get(3));
			fo.println("u1=======u2========u3=======u4");
			fo.println(opt2eff.get(0) + " " + opt2eff.get(1) + " " + opt2eff.get(2) + " " + opt2eff.get(3));
			fo.println("pt1=======pt2========pt3=======pt4");
			fo.println(pti[0] + " " + pti[1] + " " + pti[2] + " " + pti[3]);
			fo.close();
		}
		catch(Exception e){
			
		}
	}
	
	private void exploreForTwoRequestExchangeRoute(){
		if(tcs.getNbUsedTrucks() == 1)
			return;
		Random ran = new Random();
		Point pickup1 = null;
		int it = 0;
		int n = tcs.pickupPoints.size();
		while(it++ < n){
			int i = ran.nextInt(n);
			pickup1 = tcs.pickupPoints.get(i);
			if(tcs.XR.route(pickup1) != Constants.NULL_POINT)
				break;
			pickup1 = null;
		}
		if(pickup1 == null)
			return;
		int r1 = tcs.XR.route(pickup1);
		Point pickup2 = null;
		it = 0;
		while(it++ < n){
			int i = ran.nextInt(n);
			pickup2 = tcs.pickupPoints.get(i);
			int r2 = tcs.XR.route(pickup2);
			if(r2 != Constants.NULL_POINT
				&& r2 != r1)
				break;
			pickup2 = null;
		}
		if(pickup1 != null && pickup2 != null)
			twoRequestExchangeRoute(pickup1, pickup2);
		
	}
	
	private void exploreForTwoRequestExchangeBestRoute(){
		if(tcs.getNbUsedTrucks() == 1)
			return;
		Random ran = new Random();
		Point pickup1 = null;
		int it = 0;
		int n = tcs.pickupPoints.size();
		while(it++ < n){
			int i = ran.nextInt(n);
			pickup1 = tcs.pickupPoints.get(i);
			if(tcs.XR.route(pickup1) != Constants.NULL_POINT)
				break;
			pickup1 = null;
		}
		if(pickup1 == null)
			return;
		int r1 = tcs.XR.route(pickup1);
		Point pickup2 = null;
		it = 0;
		while(it++ < n){
			int i = ran.nextInt(n);
			pickup2 = tcs.pickupPoints.get(i);
			int r2 = tcs.XR.route(pickup2);
			if(r2 != Constants.NULL_POINT
				&& r2 != r1)
				break;
			pickup2 = null;
		}
		if(pickup1 != null && pickup2 != null){
			if(tcs.XR.route(pickup1) == Constants.NULL_POINT || tcs.XR.route(pickup2) == Constants.NULL_POINT)
				System.out.println("sad");
			twoRequestExchangeBestRoute(pickup1, pickup2);
		}
		
	}
	
	//1-2-3-4
	//5-6-7-8
	//=> 1-2-7-8
	//=> 5-6-3-4
	private void exploreForTwoOptMove5(){
		if(tcs.getNbUsedTrucks() == 1)
			return;
		Random ran = new Random();
		
		Point p1 = null;
		int it = 0;
		int n = tcs.deliveryPoints.size();
		while(it++ < n){
			int i = ran.nextInt(n);
			p1 = tcs.deliveryPoints.get(i);
			if(tcs.XR.route(p1) != Constants.NULL_POINT
				&& tcs.accContainerInvr.getSumWeights(p1) == 0)
				break;
			p1 = null;
		}
		if(p1 == null)
			return;
		int r1 = tcs.XR.route(p1);
		Point p2 = null;
		it = 0;
		while(it++ < n){
			int i = ran.nextInt(n);
			p2 = tcs.deliveryPoints.get(i);
			int r2 = tcs.XR.route(p2);
			if(r2 != Constants.NULL_POINT
				&& r2 != r1
				&& tcs.accContainerInvr.getSumWeights(p2) == 0)
				break;
			p2 = null;
		}
		if(p1 != null && p2 != null)
			TwoOptMove5(p1, p2);
	}
	
	//1-2-3-4
	//5-6-7-8
	//=> 1-6-7-4
	//=> 5-2-3-8
	private void exploreForCrossExchangeMove(){
		if(tcs.getNbUsedTrucks() == 1)
			return;
		Random ran = new Random();
		ArrayList<Point> consideredPoints = new ArrayList<Point>();
		consideredPoints.addAll(tcs.deliveryPoints);
		consideredPoints.addAll(tcs.startPoints);
		
		Point x1 = null;
		int it = 0;
		int n = consideredPoints.size();
		while(it++ < n){
			int i = ran.nextInt(n);
			x1 = consideredPoints.get(i);
			if(tcs.XR.route(x1) != Constants.NULL_POINT
				&& tcs.accContainerInvr.getSumWeights(x1) == 0)
				break;
			x1 = null;
		}
		if(x1 == null)
			return;
		int r1 = tcs.XR.route(x1);
		Point y1 = null;
		for(Point p = tcs.XR.next(x1); p != tcs.XR.getTerminatingPointOfRoute(r1); p = tcs.XR.next(p)){
			if(tcs.accContainerInvr.getSumWeights(p) == 0){
				y1 = p;
				break;
			}
		}
		
		Point x2 = null;
		int r2 = -1;
		it = 0;
		while(it++ < n){
			int i = ran.nextInt(n);
			x2 = consideredPoints.get(i);
			r2 = tcs.XR.route(x2);
			if(r2 != Constants.NULL_POINT
				&& r2 != r1
				&& tcs.accContainerInvr.getSumWeights(x2) == 0)
				break;
			x2 = null;
		}
		if(x2 == null)
			return;
		Point y2 = null;
		for(Point p = tcs.XR.next(x2); p != tcs.XR.getTerminatingPointOfRoute(r2); p = tcs.XR.next(p)){
			if(tcs.accContainerInvr.getSumWeights(p) == 0){
				y2 = p;
				break;
			}
		}

		if(x1 != null && x2 != null && y1 != null && y2 != null)
			crossExchangeMove(x1, y1, x2, y2);
	}
	
	private void exploreForOneRequestMoveWithGreedyInsertion(){
		Random ran = new Random();
		Point pickup = null;
		
		while(pickup == null){
			int r = ran.nextInt(tcs.XR.getNbRoutes()) + 1;
			if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 2)
				continue;
			
			
		}
		
		int it = 0;
		int n = tcs.pickupPoints.size();
		while(pickup == null){
			int i = ran.nextInt(n);
			 pickup = tcs.pickupPoints.get(i);
			 if(tcs.XR.route(pickup) != Constants.NULL_POINT)
				break;
			 pickup = null;
			 if(it++ > n)
				 break;
		}
		if(pickup == null)
			return;
		int r = -1;
		int rp = tcs.XR.route(pickup);
		int groupPick = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(rp));
		it = 0;
		n = tcs.XR.getNbRoutes();
		while(r == -1){
			r = ran.nextInt(tcs.XR.getNbRoutes()) + 1;
			int groupId = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(r));
			if(tcs.group2marked.get(groupId) == 0
				|| (tcs.group2marked.get(groupId) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) > 1)
				|| (tcs.group2marked.get(groupId) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1
				&& groupId == groupPick && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(rp)) <= 3))
				break;
			
			r = -1;
			if(it++ > n)
				break;
		}
		if(pickup != null && r != -1)
			oneRequestMoveWithGreedyInsertion(pickup, r);
	}
	private void oneRequestMoveWithGreedyInsertion(Point pickup, int r){
		System.out.println("oneRequestMoveWithGreedyInsertion");
		
		boolean isRemoveRoute = false;
		int groupId = tcs.point2Group.get(pickup);
		Point delivery = tcs.pickup2Delivery.get(pickup);
		int ridx = -1;

		ridx = tcs.XR.route(pickup);
		Point start = tcs.XR.getStartingPointOfRoute(r);
		Point pre_pick = tcs.XR.prev(pickup);
		Point pre_delivery = tcs.XR.prev(delivery);
		if(pre_delivery == pickup)
			pre_delivery = pre_pick;
		tcs.mgr.performRemoveTwoPoints(pickup, delivery);

		if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(ridx)) <= 1){
			int groupTruck = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(ridx));
			tcs.group2marked.put(groupTruck, 0);
			isRemoveRoute = true;
		}
		Point best_pre_pick = null;
		Point best_pre_delivery = null;
		Point st = tcs.XR.getStartingPointOfRoute(r);
		for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
			for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
				tcs.mgr.performAddTwoPoints(pickup, p, delivery, q);
				tcs.insertMoocForAllRoutes();
				if(tcs.S.violations() == 0){
					double new_cost = tcs.objective.getValue();						
					int nT = tcs.getNbUsedTrucks();
					int nR = tcs.getNbRejectedRequests();
//					if(nR < best_nbReject
//						|| (nR == best_nbReject && new_cost < best_objective)){
					if(nR < best_nbReject
						|| (nR == best_nbReject && nT < best_nbTrucks)
						|| (nR == best_nbReject && nT == best_nbTrucks && new_cost < best_objective)){
						best_objective = new_cost;
						best_nbReject = nR;
						best_nbTrucks = nT;
						best_pre_pick = p;
						best_pre_delivery = q;
					}
				}
//				temperature = cooling_rate*temperature;
				tcs.removeAllMoocFromRoutes();
				tcs.mgr.performRemoveTwoPoints(pickup, delivery);
			}
		}
		if(best_pre_pick != null && best_pre_delivery != null){
			tcs.mgr.performAddTwoPoints(pickup, best_pre_pick, delivery, best_pre_delivery);

			int groupTruck = tcs.point2Group.get(st);
			tcs.group2marked.put(groupTruck, 1);
			tcs.group2marked.put(groupId, 1);
			if(isRemoveRoute){
				int groupOldTruck = tcs.point2Group.get(pre_pick);
				tcs.group2marked.put(groupOldTruck, 0);
			}
			isOpt = true;
		}
		else{
			tcs.mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
			int groupTruck = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(ridx));
			tcs.group2marked.put(groupTruck, 1);
		}
	}
	
	private void twoRequestExchangeRoute(Point pickup1, Point pickup2){
		System.out.println("twoRequestExchangeRoute");
		
		Point delivery1 = tcs.pickup2Delivery.get(pickup1);
		Point delivery2 = tcs.pickup2Delivery.get(pickup2);
		int r1 = tcs.XR.route(pickup1);
		int r2 = tcs.XR.route(pickup2);
		Point pre_pick1 = tcs.XR.prev(pickup1);
		Point pre_delivery1 = tcs.XR.prev(delivery1);
		if(pre_delivery1 == pickup1)
			pre_delivery1 = pre_pick1;
		
		Point pre_pick2 = tcs.XR.prev(pickup2);
		Point pre_delivery2 = tcs.XR.prev(delivery2);
		if(pre_delivery2 == pickup2)
			pre_delivery2 = pre_pick2;

		tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
		tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
		
		tcs.mgr.performAddTwoPoints(pickup2, pre_pick1, delivery2, pre_delivery1);
		tcs.mgr.performAddTwoPoints(pickup1, pre_pick2, delivery1, pre_delivery2);
		
		tcs.insertMoocForAllRoutes();
		if(tcs.S.violations() == 0){
			double new_cost = tcs.objective.getValue();
			int nT = tcs.getNbUsedTrucks();
			int nR = tcs.getNbRejectedRequests();
//			if(nR < best_nbReject
//				|| (nR == best_nbReject && new_cost < best_objective)){
			if(nR < best_nbReject
				|| (nR == best_nbReject && nT < best_nbTrucks)
				|| (nR == best_nbReject && nT == best_nbTrucks && new_cost < best_objective)){
				tcs.removeAllMoocFromRoutes();
				isOpt = true;
				return;
			}
		}
		tcs.removeAllMoocFromRoutes();
		tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
		tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
		tcs.mgr.performAddTwoPoints(pickup1, pre_pick1, delivery1, pre_delivery1);
		tcs.mgr.performAddTwoPoints(pickup2, pre_pick2, delivery2, pre_delivery2);
		
//		Point best_pre_pick1 = null;
//		Point best_pre_delivery1 = null;
//		Point best_pre_pick2 = null;
//		Point best_pre_delivery2 = null;
//		Point st1 = tcs.XR.getStartingPointOfRoute(r1);
//		Point st2 = tcs.XR.getStartingPointOfRoute(r2);
//		for(Point p1 = st1; p1 != tcs.XR.getTerminatingPointOfRoute(r1); p1 = tcs.XR.next(p1)){
//			for(Point q1 = p1; q1 != tcs.XR.getTerminatingPointOfRoute(r1); q1 = tcs.XR.next(q1)){
//				for(Point p2 = st2; p2 != tcs.XR.getTerminatingPointOfRoute(r2); p2 = tcs.XR.next(p2)){
//					for(Point q2 = p2; q2 != tcs.XR.getTerminatingPointOfRoute(r2); q2 = tcs.XR.next(q2)){
////						System.out.println(q2.getID());
//						tcs.mgr.performAddTwoPoints(pickup2, p1, delivery2, q1);
//						tcs.insertMoocForAllRoutes();
//						if(tcs.S.violations() == 0){
//							tcs.removeAllMoocFromRoutes();
//							tcs.mgr.performAddTwoPoints(pickup1, p2, delivery1, q2);	
//							tcs.insertMoocToRoutes(r2);
//							if(tcs.S.violations() == 0){
//								double new_cost = tcs.objective.getValue();
//								int nT = tcs.getNbUsedTrucks();
//								int nR = tcs.getNbRejectedRequests();
////								if(nR < best_nbReject
////									|| (nR == best_nbReject && new_cost < best_objective)){
//								if(nR < best_nbReject
//									|| (nR == best_nbReject && nT < best_nbTrucks)
//									|| (nR == best_nbReject && nT == best_nbTrucks && new_cost < best_objective)){
//									best_objective = new_cost;
//									best_nbReject = nR;
//									best_nbTrucks = nT;
//									best_pre_pick1 = p1;
//									best_pre_delivery1 = q1;
//									best_pre_pick2 = p2;
//									best_pre_delivery2 = q2;
//								}
//							}
//							tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
//							tcs.removeAllMoocFromRoutes();
//						}
//						tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
//						tcs.removeAllMoocFromRoutes();
//						
//					}
//				}
//			}
//		}
//
//		if(best_pre_pick1 != null){
//			tcs.mgr.performAddTwoPoints(pickup2, best_pre_pick1, delivery2, best_pre_delivery1);
//			tcs.mgr.performAddTwoPoints(pickup1, best_pre_pick2, delivery1, best_pre_delivery2);
//			isOpt = true;
//		}
//		else{
//			tcs.mgr.performAddTwoPoints(pickup1, pre_pick1, delivery1, pre_delivery1);
//			tcs.mgr.performAddTwoPoints(pickup2, pre_pick2, delivery2, pre_delivery2);
//		}
	}
	
	private void twoRequestExchangeBestRoute(Point pickup1, Point pickup2){
		System.out.println("twoRequestExchangeBestRoute");
		
		Point delivery1 = tcs.pickup2Delivery.get(pickup1);
		Point delivery2 = tcs.pickup2Delivery.get(pickup2);
		int r1 = tcs.XR.route(pickup1);
		int r2 = tcs.XR.route(pickup2);
		Point curr_pick1 = tcs.XR.prev(pickup1);
		Point curr_delivery1 = tcs.XR.prev(delivery1);
		if(curr_delivery1 == pickup1)
			curr_delivery1 = curr_pick1;
		
		Point curr_pick2 = tcs.XR.prev(pickup2);
		Point curr_delivery2 = tcs.XR.prev(delivery2);
		if(curr_delivery2 == pickup2)
			curr_delivery2 = curr_pick2;
		
		Point pre_pick1 = null;
		Point pre_delivery1 = null;
		
		Point pre_pick2 = null;
		Point pre_delivery2 = null;

		tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
		tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
		
		double best_cost = Double.MAX_VALUE;
		for(Point p = tcs.XR.getStartingPointOfRoute(r1); p != tcs.XR.getTerminatingPointOfRoute(r1);
				p = tcs.XR.next(p)){
			for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r1); q = tcs.XR.next(q)){
//				if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//					System.out.println("f");
				tcs.mgr.performAddTwoPoints(pickup2, p, delivery2, q);
				tcs.insertMoocToRoutes(r1);
				if(tcs.S.violations() == 0){
					double new_cost = tcs.objective.getValue();
//					if(nR < best_nbReject
//						|| (nR == best_nbReject && new_cost < best_objective)){
					if(new_cost < best_cost){
						best_cost = new_cost;
						pre_pick2 = p;
						pre_delivery2 = q;
					}
				}
				tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
				tcs.removeMoocOnRoutes(r1);
			}
		}
		
		best_cost = Double.MAX_VALUE;
		for(Point p = tcs.XR.getStartingPointOfRoute(r2); p != tcs.XR.getTerminatingPointOfRoute(r2);
				p = tcs.XR.next(p)){
			for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r2); q = tcs.XR.next(q)){
//				if(pickup.getID() == 1271 && p.getID() == 121 && q.getID() == 1222)
//					System.out.println("f");
				tcs.mgr.performAddTwoPoints(pickup1, p, delivery1, q);
				tcs.insertMoocToRoutes(r2);
				if(tcs.S.violations() == 0){
					double new_cost = tcs.objective.getValue();
//					if(nR < best_nbReject
//						|| (nR == best_nbReject && new_cost < best_objective)){
					if(new_cost < best_cost){
						best_cost = new_cost;
						pre_pick1 = p;
						pre_delivery1 = q;
					}
				}
				tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
				tcs.removeMoocOnRoutes(r2);
			}
		}
		if(pre_pick1 != null && pre_pick2 != null){
			tcs.mgr.performAddTwoPoints(pickup1, pre_pick1, delivery1, pre_delivery1);
			tcs.mgr.performAddTwoPoints(pickup2, pre_pick2, delivery2, pre_delivery2);
			isOpt = true;
		}
		else{
//			tcs.mgr.performRemoveTwoPoints(pickup1, delivery1);
//			tcs.mgr.performRemoveTwoPoints(pickup2, delivery2);
			tcs.mgr.performAddTwoPoints(pickup1, curr_pick1, delivery1, curr_delivery1);
			tcs.mgr.performAddTwoPoints(pickup2, curr_pick2, delivery2, curr_delivery2);
		}
	}
	
	public void TwoOptMove5(Point pickup1, Point pickup2){
		System.out.println("TwoOptMove5");
		
		int r1 = tcs.XR.route(pickup1);
		int r2 = tcs.XR.route(pickup2);
		tcs.mgr.performTwoOptMove5(pickup1, pickup2);
		tcs.insertMoocForAllRoutes();
		if(tcs.S.violations() == 0){
			int new_nbTrucks = tcs.getNbUsedTrucks();
			int nR = tcs.getNbRejectedRequests();
//			if(nR < best_nbReject
//				|| (nR == best_nbReject && tcs.objective.getValue() < best_objective)){
			if(nR < best_nbReject
				|| (nR == best_nbReject && new_nbTrucks < best_nbTrucks)
				|| (nR == best_nbReject && new_nbTrucks == best_nbTrucks && tcs.objective.getValue() < best_objective)){
				tcs.removeAllMoocFromRoutes();
				isOpt = true;
				return;
			}
		}
		tcs.removeAllMoocFromRoutes();
		tcs.mgr.performTwoOptMove5(pickup1, pickup2);
		
	}
	
	public void crossExchangeMove(Point x1, Point y1, Point x2, Point y2){
		System.out.println("crossExchangeMove");
		
		int r1 = tcs.XR.route(x1);
		int r2 = tcs.XR.route(x2);
		tcs.mgr.performCrossExchangeMove(x1, y1, x2, y2);
		tcs.insertMoocForAllRoutes();
		if(tcs.S.violations() > 0){
			int new_nbTrucks = tcs.getNbUsedTrucks();
			int nR = tcs.getNbRejectedRequests();
//			if(nR < best_nbReject
//				|| (nR == best_nbReject && tcs.objective.getValue() < best_objective)){
			if(nR < best_nbReject
				|| (nR == best_nbReject && new_nbTrucks < best_nbTrucks)
				|| (nR == best_nbReject && new_nbTrucks == best_nbTrucks && tcs.objective.getValue() < best_objective)){
				tcs.removeAllMoocFromRoutes();
				isOpt = true;

				if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r1)) <= 1){
					int groupTruck = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(r1));
					tcs.group2marked.put(groupTruck, 0);
				}
				if(tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r2)) <= 1){
					int groupTruck = tcs.point2Group.get(tcs.XR.getStartingPointOfRoute(r2));
					tcs.group2marked.put(groupTruck, 0);
				}
				return;
			}
		}
		tcs.removeAllMoocFromRoutes();
		tcs.mgr.performCrossExchangeMove(x1, y2, x2, y1);
	}
	
	public void greedyInsertion(){
		System.out.println("greedyInsertion");
		tcs.removeAllMoocFromRoutes();
		
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
				
				int groupTruck = tcs.point2Group.get(st);
				if(tcs.group2marked.get(groupTruck) == 1 && tcs.XR.index(tcs.XR.getTerminatingPointOfRoute(r)) <= 1)
					continue;
				
				for(Point p = st; p != tcs.XR.getTerminatingPointOfRoute(r); p = tcs.XR.next(p)){
					for(Point q = p; q != tcs.XR.getTerminatingPointOfRoute(r); q = tcs.XR.next(q)){
//						if((tcs.XR.prev(p)!= null && tcs.XR.prev(p).getID() % 2 == 1
//								&& p.getID() % 2 == 1
//								&& pickup.getID() % 2 == 1)
//								|| (tcs.XR.next(p) != null && tcs.XR.next(p).getID() % 2 == 1
//								&& p.getID() % 2 == 1
//								&& pickup.getID() % 2 == 1))
//								System.out.println("bug");
						tcs.mgr.performAddTwoPoints(pickup, p, delivery, q);
						tcs.insertMoocToRoutes(r);
						if(tcs.S.violations() == 0){
							double cost = tcs.objective.getValue();
							if( cost < best_objective){
								best_objective = cost;
								pre_pick = p;
								pre_delivery = q;
							}
						}
						tcs.mgr.performRemoveTwoPoints(pickup, delivery);
						tcs.removeMoocOnRoutes(r);
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				tcs.mgr.performAddTwoPoints(pickup, pre_pick, delivery, pre_delivery);
				Point st = tcs.XR.getStartingPointOfRoute(tcs.XR.route(pre_pick));
				tcs.rejectPickupPoints.remove(pickup);
				tcs.rejectDeliveryPoints.remove(delivery);
				int groupTruck = tcs.point2Group.get(st);
				tcs.group2marked.put(groupTruck, 1);
				tcs.group2marked.put(groupId, 1);
				i--;
			}
		}
		tcs.insertMoocForAllRoutes();
	}
}
