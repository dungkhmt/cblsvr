/*
 * authors: PHAM Quang Dung (dungkhmt@gmail.com)
 * date: 11/09/2015
 */
package localsearch.domainspecific.vehiclerouting.vrp.search;

import java.util.*;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;

import localsearch.domainspecific.vehiclerouting.vrp.moves.IVRMove;

public class Neighborhood {
	private VRManager mgr;
	private ArrayList<IVRMove> moves;
	private Random R;
	public Neighborhood(VRManager mgr){
		this.mgr = mgr;
		moves = new ArrayList<IVRMove>();
		R = new Random();
	}
	public void add(IVRMove m){
		moves.add(m);
	}
	public void clear(){
		moves.clear();
	}
	public int size(){
		return moves.size();
	}
	public boolean hasMove(){
		return moves.size() > 0;
	}
	public IVRMove getAMove(){
		return moves.get(R.nextInt(moves.size()));
	}
}
