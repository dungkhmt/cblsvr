package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class TruckRoute {
	private Truck truck;
	private int nbStops;
	private int travelTime;
	private RouteElement[] nodes;

	public TruckRoute(Truck truck, int nbStops,
			int travelTime,
			RouteElement[] nodes){
		super();
		this.truck = truck;
		this.nbStops = nbStops;
		this.travelTime = travelTime;
		this.nodes = nodes;
	}
	public TruckRoute() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Truck getTruck() {
		return truck;
	}
	public void setTruck(Truck truck) {
		this.truck = truck;
	}
	public int getNbStops() {
		return nbStops;
	}
	public void setNbStops(int nbStops) {
		this.nbStops = nbStops;
	}
	public int getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
	public RouteElement[] getNodes() {
		return nodes;
	}
	public void setNodes(RouteElement[] nodes) {
		this.nodes = nodes;
	}
	
}
