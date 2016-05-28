package localsearch.domainspecific.vehiclerouting.vrp.online.invariants;

import java.util.ArrayList;
import java.util.HashSet;

import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;
import localsearch.domainspecific.vehiclerouting.vrp.online.VarRoutesVROnline;


public class OAccumulatedWeightNodes extends AccumulatedWeightNodesVR implements
		OInvariantVR {
	protected VarRoutesVROnline XRO;
	private static final int scaleSz = 1000;
	private int maxSz = 0;
	protected ArrayList<Point> points;

	public OAccumulatedWeightNodes(VarRoutesVROnline XR, NodeWeightsManager nodeWeights){
		super(XR,nodeWeights);
		XRO = XR;

		//TODO
		System.out.println(name() + "::constructor, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		System.exit(-1);
	}
	public void scaleUp(){
		//TODO
		System.out.println(name() + "::scaleUp, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		System.exit(-1);
		
	}
	public void addPoint(Point p){
		//TODO
		System.out.println(name() + "::addPoint, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		System.exit(-1);
		
	}
	public String name(){
		return "OAccumulatedWeightNodes";
	}
	@Override
	public void updateWhenReachingTimePoint(int t) {
		// TODO Auto-generated method stub
		System.out.println(name() + "::updateWhenReachingTimePoint, NOT IMPLEMENTED YET!!!!!!  --> exit(-1)");
		System.exit(-1);
		 
	}

}
