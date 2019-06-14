package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

import java.util.ArrayList;
import java.util.HashMap;

import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;

public class TruckContainerSolution {
	private ArrayList<ArrayList<Point>> _route;
	private ArrayList<Point> _rejectPickupPoints;
	private ArrayList<Point> _rejectDeliveryPoints;

	private double _cost;
	
	public TruckContainerSolution(VarRoutesVR XR, ArrayList<Point> rejectPickupPoints, 
			ArrayList<Point> rejectDeliveryPoints, double cost){
		this._rejectPickupPoints = new ArrayList<Point>();
		this._rejectDeliveryPoints = new ArrayList<Point>();
		
		for(int i=0; i<rejectPickupPoints.size(); i++){
			_rejectPickupPoints.add(rejectPickupPoints.get(i));
		}
		
		for(int i=0; i<rejectDeliveryPoints.size(); i++){
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
	
	public void copy2XR(VarRoutesVR XR){
		int K = XR.getNbRoutes();
		VRManager mgr = XR.getVRManager();
		mgr.performRemoveAllClientPoints();
		
		for(int k=1; k<=K; k++){
			ArrayList<Point> route_k = _route.get(k-1);
			for(int i=0; i<route_k.size()-2; i++){
				mgr.performAddOnePoint(route_k.get(i+1),route_k.get(i));
			}
		}
	}

	public ArrayList<ArrayList<Point>> get_route() {
		return _route;
	}

	public void set_route(ArrayList<ArrayList<Point>> _route) {
		this._route = _route;
	}

	public ArrayList<Point> get_rejectPickupPoints() {
		return _rejectPickupPoints;
	}

	public void set_rejectPickupPoints(ArrayList<Point> _rejectPickupPoints) {
		this._rejectPickupPoints = _rejectPickupPoints;
	}

	public ArrayList<Point> get_rejectDeliveryPoints() {
		return _rejectDeliveryPoints;
	}

	public void set_rejectDeliveryPoints(ArrayList<Point> _rejectDeliveryPoints) {
		this._rejectDeliveryPoints = _rejectDeliveryPoints;
	}

	public double get_cost() {
		return _cost;
	}

	public void set_cost(double _cost) {
		this._cost = _cost;
	}
}
