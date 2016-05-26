
/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date:27/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.CBLSVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.ValueRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.LexMultiValues;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;
import localsearch.domainspecific.vehiclerouting.vrp.neighborhoodexploration.INeighborhoodExplorer;

public class GenericLocalSearch implements ISearch {

	private VRManager mgr;
	private VarRoutesVR XR;
	private LexMultiValues bestValue;
	private ValueRoutesVR bestSolution;
	private int currentIter;
	private LexMultiFunctions F;
	private ArrayList<INeighborhoodExplorer> neighborhoodExplorer;
	private int maxStable;
	private int nic;
	private HashMap<INeighborhoodExplorer, Integer> mN2ID;
	
	private Random R = new Random();
	public GenericLocalSearch(VRManager mgr, LexMultiFunctions F, ArrayList<INeighborhoodExplorer> neighborhoodExplorer){
		this.mgr = mgr;
		this.XR = mgr.getVarRoutesVR();
		this.F = F;
		this.neighborhoodExplorer = neighborhoodExplorer;
		this.maxStable = 100;
		mN2ID = new HashMap<INeighborhoodExplorer, Integer>();
		
		for(int i = 0; i < neighborhoodExplorer.size(); i++){
			INeighborhoodExplorer in = neighborhoodExplorer.get(i);
			mN2ID.put(in, i);
		}
		
		
	}
	
	private void updateBest() {
		bestValue.set(F.getValues());
		bestSolution.store();
	}

	public void setMaxStable(int maxStable){
		this.maxStable = maxStable;
	}
	private void restart(){
		System.out.println(name() + "::restart............");
		XR.setRandom();
		if(F.getValues().lt(bestValue)){
			updateBest();
		}
		nic = 0;
	}
	 
	public void search(int maxIter, int timeLimit){
		bestSolution = new ValueRoutesVR(XR);
		currentIter = 0;
		XR.setRandom();
		nic = 0;
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(F.getValues());
		double t0 = System.currentTimeMillis();
		System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
		//System.exit(-1);
		System.out.println(XR.toString());
		while (currentIter < maxIter) {
			double t = System.currentTimeMillis() - t0;
			if (t  > timeLimit)
				break;
			N.clear();
			
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(F.size(), CBLSVR.MAX_INT);
			
			
			for(INeighborhoodExplorer NI: neighborhoodExplorer){
				NI.exploreNeighborhood(N, bestEval);
			}
			
			if (N.hasMove()) {
				IVRMove m = N.getAMove();
				m.move();
				
				System.out.println(name() + "::search, step " + currentIter + ", F = " + F.getValues().toString() + ", best = " + bestValue.toString());
				if(F.getValues().lt(bestValue)){
					updateBest();
				}else{
					nic++;
					if(nic > maxStable){
						restart();
					}
				}
			} else {
				System.out.println(name()
						+ "::search --> no move available, break");
				break;
			}
			// System.out.println(obj.toString());

			currentIter++;

		}

		XR.setValue(bestSolution);

		System.out.println("Best = " + F.getValues().toString());
		System.out.println("bestValues = " + bestValue.toString());
		
	}
	public void perturb(int nbSteps){
		for(int k = 1; k <= nbSteps; k++){
			ArrayList<Point> P = XR.collectClientPointsOnRoutes();
			if(P.size() >= 2){
				for(int i = 1; i <= 10; i++){
					Point x = P.get(R.nextInt(P.size()));
					Point y = P.get(R.nextInt(P.size()));
					
					if(x != y){
						mgr.performOnePointMove(x, y);
						break;
					}
					
				}
			}
		}
	}
	public void searchImprove(int maxIter, int timeLimit){
		bestSolution = new ValueRoutesVR(XR);
		currentIter = 0;
		//XR.setRandom();
		nic = 0;
		Neighborhood N = new Neighborhood(mgr);
		bestValue = new LexMultiValues(F.getValues());
		updateBest();
		double t0 = System.currentTimeMillis();
		System.out.println(name() + "::search, init bestValue = " + bestValue.toString());
		//System.exit(-1);
		System.out.println(XR.toString());
		while (currentIter < maxIter) {
			double t = System.currentTimeMillis() - t0;
			if (t  > timeLimit)
				break;
			N.clear();
			
			LexMultiValues bestEval = new LexMultiValues();
			bestEval.fill(F.size(), CBLSVR.MAX_INT);
			
			
			for(INeighborhoodExplorer NI: neighborhoodExplorer){
				NI.exploreNeighborhood(N, bestEval);
			}
			
			if (N.hasMove()) {
				IVRMove m = N.getAMove();
				m.move();
				
				System.out.println(name() + "::search, step " + currentIter + ", F = " + F.getValues().toString() + ", best = " + bestValue.toString());
				if(F.getValues().lt(bestValue)){
					updateBest();
				}else{
					nic++;
					if(nic > maxStable){
						//restart();
						nic = 0;
						perturb(20);
					}
				}
			} else {
				System.out.println(name()
						+ "::search --> no move available, break");
				break;
			}
			// System.out.println(obj.toString());

			currentIter++;

		}

		XR.setValue(bestSolution);

		System.out.println("Best = " + F.getValues().toString());
		System.out.println("bestValues = " + bestValue.toString());
		
	}
	
	public String name(){
		return "GenericLocalSearch";
	}
	
	public LexMultiValues getIncumbentValue() {
		// TODO Auto-generated method stubValueRoutesVR
		return bestValue;
	}

	
	public int getCurrentIteration() {
		// TODO Auto-generated method stub
		return currentIter;
	}

	
	public ValueRoutesVR getIncumbent() {
		// TODO Auto-generated method stub
		return bestSolution;
	}

}
