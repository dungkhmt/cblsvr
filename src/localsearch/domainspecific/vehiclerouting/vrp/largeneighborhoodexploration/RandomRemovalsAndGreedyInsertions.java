package localsearch.domainspecific.vehiclerouting.vrp.largeneighborhoodexploration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.Constants;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.AddOnePoint;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.moves.KPointsMove;
import localsearch.domainspecific.vehiclerouting.vrp.search.ISearch;
import localsearch.domainspecific.vehiclerouting.vrp.search.Neighborhood;

public class RandomRemovalsAndGreedyInsertions  implements ILargeNeighborhoodExplorer {

	private VRManager mgr;
	private VarRoutesVR XR;
	private ISearch search;
	private LexMultiFunctions F;
	private int nbIters = 1;
	private HashMap<Point, Point> pickup2delivery;
	private HashMap<Point, Point> delivery2pickup;
	private ArrayList<Point> pickupPeoplePoint;
	private HashMap<Point, Point> pickup2deliveryOfPeople;
	private HashMap<Point, Point> pickup2deliveryOfParcels;
	
	public RandomRemovalsAndGreedyInsertions(VarRoutesVR XR, LexMultiFunctions F, int nbIters, 
							HashMap<Point, Point> pickup2delivery, HashMap<Point, Point> delivery2pickup,
							HashMap<Point, Point> pickup2deliveryOfPeople, HashMap<Point, Point> pickup2deliveryOfParcels) {
		this.XR = XR;
		this.F = F;
		this.mgr = XR.getVRManager();
		this.nbIters = nbIters;
		this.pickup2delivery = pickup2delivery;
		this.delivery2pickup = delivery2pickup;
		this.pickup2deliveryOfPeople = pickup2deliveryOfPeople;
		this.pickup2deliveryOfParcels = pickup2deliveryOfParcels;
	}
	
	public void randomRemoval(Neighborhood N){
		int k = 0;
		ArrayList<Point> clientPoints = XR.getClientPoints();
		int n = clientPoints.size();
		Random r = new Random();
		
		ArrayList<Point> xRemoval = new ArrayList<Point>();
		ArrayList<Point> yRemoval = new ArrayList<Point>();
		for(int i = 0; i < nbIters; i++)
			yRemoval.add(CBLSVR.NULL_POINT);
		while(k < nbIters){
			int idx = r.nextInt(n-1);
			Point pr1 = clientPoints.get(idx);
			Point pr2 = null;
			pr2 = pickup2delivery.get(pr1);
			if(pr2 == null)
				pr2 = delivery2pickup.get(pr1);
			if(XR.checkPerformRemoveTwoPoints(pr1, pr2)){
				XR.performRemoveTwoPoints(pr1, pr2);
				k++;
				System.out.println("RandomRremoval:: remove request: " + k);
			}
			
		}
	}
	
	public void greedyInsertion2(Neighborhood N){
		int ix = 0;
		LexMultiValues bestEval = new LexMultiValues();
		bestEval.fill(F.size(), 0);
		
		Set<Point> pickPeoplePoints = pickup2deliveryOfPeople.keySet();
		Set<Point> pickupPoints = pickup2delivery.keySet();
		for(Point p : pickup2delivery.keySet()){
			if(XR.route(p) != Constants.NULL_POINT)
				continue;
			Point d = pickup2delivery.get(p);
			ix++;
			//add the request to route
			Point pre_pick = null;
			Point pre_delivery = null;
			double cur_obj = Double.MAX_VALUE; 
			for(int r = 1; r <= XR.getNbRoutes(); r++){
				for(Point v = XR.getStartingPointOfRoute(r); v!= XR.getTerminatingPointOfRoute(r); v = XR.next(v)){
					if(pickPeoplePoints.contains(v)){
						continue;
					}
					
					if(pickPeoplePoints.contains(p) //this request is people request
						&& F.evaluateAddTwoPoints(p, v, d, v).lt(bestEval)){
						bestEval.set(F.evaluateAddTwoPoints(p, v, d, v));
						pre_pick = v;
						pre_delivery = v;
					}
					else{
						for(Point u = v; u != XR.getTerminatingPointOfRoute(r); u = XR.next(u)){
							if(pickPeoplePoints.contains(u)){
								continue;
							}
							else{
								if(F.evaluateAddTwoPoints(p, v, d, u).lt(bestEval)){
									bestEval.set(F.evaluateAddTwoPoints(p, v, d, u));
									pre_pick = v;
									pre_delivery = u;
								}
							}
						}
					}
				}
			}
			if(pre_pick != null && pre_delivery != null){
				mgr.performAddTwoPoints(p, pre_pick, d, pre_delivery);
			}
			
			System.out.println("i = " + ix);
		}
	}
	
	public void greedyInsertion(Neighborhood N){
		LexMultiValues bestEval = new LexMultiValues();
		bestEval.fill(F.size(), Double.MAX_VALUE);
		int k = 0;
		int i = 0;
		int j = 0;
		System.out.println(XR.toString());
		System.out.println(bestEval.toString());
		for (Point x1 : XR.getClientPoints()) {
			j = 0;
			Point x2 = pickup2delivery.get(x1);
			if(x2 == null)
				x2 = delivery2pickup.get(x1);
			Point y1 = null;
			Point y2 = null;
			for (Point p : XR.getAllPoints()) {
				if (XR.checkPerformAddOnePoint(x1, p)) {
					for(Point q : XR.getAllPoints()){
						if(XR.checkPerformAddTwoPoints(x1, p, x2, q)){
							LexMultiValues eval = F.evaluateAddTwoPoints(x1, p, x2, q);
							if (eval.lt(bestEval)) {
								y1 = p;
								y2 = q;
								bestEval.set(eval);
							}
						}
						System.out.println("j = " + j);
					}					
				}
				j++;
				System.out.println("i = " + i + ", j = " + j);
			}
			i++;
			if(y1 != null && y2 != null){
				XR.performAddTwoPoints(x1, y1, x2, y2);
				System.out.println("GreedyInsertion:: insert request: " + k);
				k++;
			}
		}
	}
	public void exploreLargeNeighborhood(Neighborhood N) {
		// TODO Auto-generated method stub
		randomRemoval(N);
		greedyInsertion(N);
	}

	public String name(){
		return "RandomRemovalsAndGreedyInsertions";
	}
	public void performMove(IVRMove m) {
		// TODO Auto-generated method stub

	}
}