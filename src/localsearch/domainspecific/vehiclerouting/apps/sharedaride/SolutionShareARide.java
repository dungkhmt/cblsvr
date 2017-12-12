package localsearch.domainspecific.vehiclerouting.apps.sharedaride;

import java.util.ArrayList;

import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class SolutionShareARide {
	
	private ArrayList<ArrayList<Point>> _route;
	private ArrayList<Point> _rejectPoints;
	private ArrayList<Point> _rejectPickupPoints;
	private ArrayList<Point> _rejectDeliveryPoints;
	
	private double _cost;
	
	public SolutionShareARide(VarRoutesVR XR, ArrayList<Point> rejectPoints, ArrayList<Point> rejectPickupPoints, ArrayList<Point> rejectDeliveryPoints, double cost){
		
		_rejectPoints = new ArrayList<Point>();
		_rejectDeliveryPoints = new ArrayList<Point>();
		_rejectPickupPoints = new ArrayList<Point>();
		
		for(int i=0; i<rejectPoints.size(); i++){
			_rejectPoints.add(rejectPoints.get(i));
		}
		
		for(int i=0; i<rejectPickupPoints.size(); i++){
			_rejectPickupPoints.add(rejectPickupPoints.get(i));
			_rejectDeliveryPoints.add(rejectDeliveryPoints.get(i));
		}
		
		_route = new ArrayList<ArrayList<Point>>();
		
		int K = XR.getNbRoutes();
		
		for(int k=1; k<=K; k++){
			ArrayList<Point> route_k = new ArrayList<Point>();
			Point x = XR.getStartingPointOfRoute(k);
			for(; x != XR.getTerminatingPointOfRoute(k); x = XR.next(x)){
				route_k.add(x);
			}
			route_k.add(x);
			_route.add(route_k);
		}
		
		this._cost = cost;
	}
	
	public double getCost() {
		return _cost;
	}
	
	public void setCost(double cost) {
		this._cost = cost;
	}

	public ArrayList<ArrayList<Point>> getRoute() {
		return _route;
	}

	public void setRoute(ArrayList<ArrayList<Point>> route) {
		this._route = route;
	}

	public String toString(){
		String s = "";
		int K = _route.size();
		for(int k = 0; k < K; k++){
			s += "route[" + k + "] = ";
			ArrayList<Point> route_k = _route.get(k);
			int i=0;
			Point x;
			for(; i< route_k.size()-1; i++){
				x = route_k.get(i);
				s = s + x.getID() + " " + " -> ";
			}
			x = route_k.get(i);
			s = s + x.getID() + "\n";
		}
		
		String r = "rejectPoints = [";
		for(int i = 0; i<_rejectPickupPoints.size(); i++){
			r += (_rejectPickupPoints.get(i).getID()+", ");
		}
		r += "] \n";
		
		s += (r + "cost = "+_cost+"\n");
		
		return s;
	}
	
}
