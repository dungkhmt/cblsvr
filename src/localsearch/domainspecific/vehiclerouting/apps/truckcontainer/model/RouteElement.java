package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class RouteElement {

	private String locationCode;
	private String action;
	
	private int arrivalTime;
	private int departureTime;
	private int travelTime;
	
	
	public RouteElement(String locationCode, String action,
			int arrivalTime, int departureTime, int travelTime){
		super();
		this.locationCode = locationCode;
		this.action = action;
		this.arrivalTime = arrivalTime;
		this.departureTime = departureTime;
		this.travelTime = travelTime;
	}
	public RouteElement() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getLocationCode(){
		return this.locationCode;
	}
	public void setLocationCode(String locationCode){
		this.locationCode = locationCode;
	}
	public String getAction(){
		return this.action;
	}
	public void setAction(String action){
		this.action = action;
	}
	public int getArrivalTime(){
		return this.arrivalTime;
	}
	public void setArrivalTime(int arrivalTime){
		this.arrivalTime = arrivalTime;
	}
	public int getDepartureTime(){
		return this.departureTime;
	}
	public void setDepartureTime(int departureTime){
		this.departureTime = departureTime;
	}
	public int getTravelTime() {
		return travelTime;
	}
	public void setTravelTime(int travelTime) {
		this.travelTime = travelTime;
	}
}

