package localsearch.domainspecific.vehiclerouting.vrp.invariants;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.IDistanceManager;
import localsearch.domainspecific.vehiclerouting.vrp.InvariantVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class RelatedPointBuckets implements InvariantVR {

	protected VarRoutesVR XR;
	protected VRManager mgr;
	HashMap<Point, Double> eat;
	HashMap<Point, Integer> latestAllowedArrivalTime;
	HashMap<Point, Integer> serviceDuration;
	private HashMap<Integer, ArrayList<Point>> bks;
	private int delta;
	public int nbBuckets;
	
	public RelatedPointBuckets(VarRoutesVR XR, HashMap<Point, Double> eat, HashMap<Point, Integer> latestAllowedArrivalTime, HashMap<Point, Integer> serviceDuration, int delta){
		this.eat = eat;
		this.latestAllowedArrivalTime = latestAllowedArrivalTime;
		this.serviceDuration = serviceDuration;
		this.XR = XR;
		this.mgr = XR.getVRManager();
		this.delta = delta;
		bks = new HashMap<Integer, ArrayList<Point>>();
		this.nbBuckets = 86400/delta;
		this.mgr.post(this);
	}
	
	@Override
	public VRManager getVRManager() {
		// TODO Auto-generated method stub
		return mgr;
	}
	
	public HashMap<Integer, ArrayList<Point>> getBuckets(){
		return this.bks;
	}
	
	public void setBuckets(HashMap<Integer, ArrayList<Point>> bks) {
		this.bks = bks;
	}
	public ArrayList<Point> getBucketWithIndex(int idx){
		return this.bks.get(idx);
	}
	
	public int getDelta(){
		return delta;
	}

	@Override
	public void initPropagation() {
		// TODO Auto-generated method stub
		
		ArrayList<Integer> bucketID = new ArrayList<Integer>();
		for(int i = 0; i < nbBuckets; i++){
			ArrayList<Point> b0 = new ArrayList<Point>();
			ArrayList<Point> temp = XR.getStartingPoints();
			for(int j = 0; j < temp.size(); j++)
				b0.add(temp.get(j));
			bks.put(i, b0);
			bucketID.add(i);
		}
		
		ArrayList<Point> startingpoint = XR.getStartingPoints();
		for(int i = 0; i < startingpoint.size(); i++){
			ArrayList<Integer> bkIds = new ArrayList<Integer>(bucketID);
			startingpoint.get(i).setBucketIDs(bkIds);
		}
	}
	/***
	 * Update bucket of some points on route k
	 * Point x is the last point on route k in which it was not affected by moving.
	 */
	private void propagate(Point x){
		int k = XR.route(x);
		for(Point p = x; p != XR.getTerminatingPointOfRoute(k); p = XR.next(p)){
			//get bucket list include point p
			ArrayList<Integer> BucketIDsOfPointP = p.getBucketIDs();
			//delete point p in the old bucket
			for(int i = 0; i < BucketIDsOfPointP.size(); i++){
				int bkId = BucketIDsOfPointP.get(i);
				ArrayList<Point> bucket = bks.get(bkId);
				bucket.remove(p);
				bks.put(bkId, bucket);
			}
			BucketIDsOfPointP.clear();
			
			//update new bucket
			double eatX = eat.get(p);
			int stIdx = (int)eatX/delta;
			int endIdx = stIdx;
			//double flexTime = latestAllowedArrivalTime.get(XR.next(p)) - eat.get(XR.next(p));
			//endIdx = (int)(eatX + serviceDuration.get(p) + flexTime)/delta;
			endIdx = latestAllowedArrivalTime.get(XR.next(p))/delta + 1;
			if(endIdx > 86400/delta - 1)
				endIdx = 86400/delta - 1;
			for(int i = stIdx; i <= endIdx; i++){	
				ArrayList<Point> bk = bks.get(i);
				bk.add(p);
				bks.put(i, bk);
				BucketIDsOfPointP.add(i);
			}
			//p.setBucketIDs(BucketIDsOfPointP);
		}
	}

	@Override
	public void propagateOnePointMove(Point x, Point y) {
		// TODO Auto-generated method stub
		Point preX = XR.oldPrev(x);
		propagate(preX);
		propagate(y);
	}

	@Override
	public void propagateTwoPointsMove(Point x, Point y) {
		// TODO Auto-generated method stub
		Point preY = XR.oldPrev(y);
		int k = XR.route(x);
		propagate(preY);
	}

	@Override
	public void propagateTwoOptMove1(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove2(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove3(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove4(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove5(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove6(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove7(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoOptMove8(Point x, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateOrOptMove1(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateOrOptMove2(Point x1, Point x2, Point y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove1(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove2(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove3(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove4(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove5(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove6(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove7(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreeOptMove8(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateCrossExchangeMove(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateTwoPointsMove(Point x1, Point x2, Point y1, Point y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateThreePointsMove(Point x1, Point x2, Point x3, Point y1, Point y2, Point y3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateFourPointsMove(Point x1, Point x2, Point x3, Point x4, Point y1, Point y2, Point y3,
			Point y4) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateKPointsMove(ArrayList<Point> x, ArrayList<Point> y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void propagateAddOnePoint(Point x, Point y) {
		// TODO Auto-generated method stub
		propagate(x);

	}

	@Override
	public void propagateRemoveOnePoint(Point x) {
		// TODO Auto-generated method stub
		ArrayList<Integer> bkOfX = x.getBucketIDs();
		//delete point p in the old bucket
		for(int i = 0; i < bkOfX.size(); i++){
			int bkId = bkOfX.get(i);
			ArrayList<Point> bucket = bks.get(bkId);
			bucket.remove(x);
			bks.put(bkId, bucket);
		}
		bkOfX.clear();
		x.setBucketIDs(bkOfX);
		propagate(XR.oldPrev(x));
	}

	@Override
	public void propagateAddTwoPoints(Point x1, Point y1, Point x2, Point y2) {
		// TODO Auto-generated method stub
		propagate(x1);
	}

	@Override
	public void propagateRemoveTwoPoints(Point x1, Point x2) {
		// TODO Auto-generated method stub
		ArrayList<Integer> bkOfX1 = x1.getBucketIDs();
		//delete point p in the old bucket
		for(int i = 0; i < bkOfX1.size(); i++){
			int bkId = bkOfX1.get(i);
			ArrayList<Point> bucket = bks.get(bkId);
			bucket.remove(x1);
			bks.put(bkId, bucket);
		}
		bkOfX1.clear();
		x1.setBucketIDs(bkOfX1);
		ArrayList<Integer> bkOfX2 = x2.getBucketIDs();
		//delete point p in the old bucket
		for(int i = 0; i < bkOfX2.size(); i++){
			int bkId = bkOfX2.get(i);
			ArrayList<Point> bucket = bks.get(bkId);
			bucket.remove(x2);
			bks.put(bkId, bucket);
		}
		bkOfX2.clear();
		x2.setBucketIDs(bkOfX2);
		propagate(XR.oldPrev(x1));
	}

	@Override
	public void propagateAddRemovePoints(Point x, Point y, Point z) {
		// TODO Auto-generated method stub

	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "RelatedPointBuckets";
	}

}
