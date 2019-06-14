package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ConfigParam;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Container;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotContainer;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotMooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DepotTruck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.DistanceElement;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportContainerRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Mooc;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Port;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Truck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Warehouse;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.WarehouseContainerTransportRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.WarehouseTransportRequest;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;
import localsearch.domainspecific.vehiclerouting.vrp.utils.ScannerInput;

public class DataAnalysis {
	TruckContainerSolver solver;
	ContainerTruckMoocInput input;
	int a = 0;
	int b;
	
	int nbEE;
	int nbEL;
	int nbIE;
	int nbIL;
	
	int nbLogicalPoints;
	int nbRequests;
	int nbTrucks;
	int nbMoocs;
	int nbContainers;
	int nbDepotTrucks;
	int nbDepotMoocs;
	int nbDepotContainers;
	int nbWarehouses;
	int nbPorts;
	long beginTime;
	long minEarliestTime;
	long maxLatestTime;
	int duration;//thoi gian giua max latest va min earliest
	int rangeTime;//thoi gian giua earliest va latest
	int[][] matrix;
	HashMap<String, Integer> mCode2idx;
	
	ArrayList<String> codes;
	ArrayList<String> locationCodes;
	
	private ExportEmptyRequests[] exEmptyRequests;
	private ExportLadenRequests[] exLadenRequests;
	private ImportEmptyRequests[] imEmptyRequests;
	private ImportLadenRequests[] imLadenRequests;
	
	private DepotContainer[] depotContainers;
	private DepotMooc[] depotMoocs;
	private DepotTruck[] depotTrucks;
	private Warehouse[] warehouses;
	private Truck[] trucks;
	private Mooc[] moocs;
	private Port[] ports;
	
	private Container[] containers;
	private ArrayList<Container> temp_containers;
	
	DistanceElement[] distance;

	private ConfigParam params;
	
	public void initParams(){
		nbEE = 100;
		nbEL = 100;
		nbIE = 100;
		nbIL = 100;
		
		nbLogicalPoints = 1400;
		nbRequests = nbEE + nbEL + nbIE + nbIL;
		nbTrucks = nbRequests;
		nbMoocs = nbTrucks;
		nbContainers = nbRequests;
		nbDepotTrucks = (int)(nbTrucks / 10);
		nbDepotMoocs = (int)(nbMoocs / 10);
		nbDepotContainers = (int)(nbContainers / 10);
		nbWarehouses = nbRequests/10;
		nbPorts = (nbEL + nbIL)/10;
		
		beginTime = 1560250800;
		minEarliestTime = 1560272400;
		maxLatestTime = 1560531599;
		duration = (int)(maxLatestTime - minEarliestTime);
		input = new ContainerTruckMoocInput();
	}
	
	//tao code cho cac entities (location code trung voi code) 
	//va tao ma tran thoi gian
	public void createCodesAndTravelTimeMatrix(){
		Random r = new Random();
		distance = new DistanceElement[nbLogicalPoints*(nbLogicalPoints-1)];
		codes = new ArrayList<String>();
		mCode2idx = new HashMap<String, Integer>();
		matrix = new int[nbLogicalPoints][nbLogicalPoints]; 
		for(int i = 0; i < nbLogicalPoints; i++){
			String s = "P-" + i;
			codes.add(s);
			mCode2idx.put(s, i);
		}
		for(int i = 0; i < nbLogicalPoints; i++)
			for(int j = 0; j < nbLogicalPoints; j++)
				matrix[i][j] = 0;
		int idx = 0;
		for(int i = 0; i < nbLogicalPoints; i++){
			for(int j = 0; j < nbLogicalPoints; j++){
				if(i == j)
					continue;
				DistanceElement e = new DistanceElement();
				double d = r.nextInt(40000) + 1000;
				double t = (d * 3600) / 30000;
				e.setSrcCode(codes.get(i));
				e.setDestCode(codes.get(j));
				e.setDistance(d);
				e.setTravelTime(t);
				matrix[i][j] = (int)t;
				distance[idx] = e;
				idx++;
			}
		}
		input.setDistance(distance);
	}
	
	public void createDepotTrucks(){
		Random r = new Random();
		depotTrucks = new DepotTruck[nbDepotTrucks];
		for(int i = 0; i < nbDepotTrucks; i++){
			DepotTruck dp = new DepotTruck();
			int idx = r.nextInt(codes.size());
			dp.setCode(codes.get(idx));
			dp.setLocationCode(codes.get(idx));
			codes.remove(idx);
			depotTrucks[i] = dp;
		}
		input.setDepotTrucks(depotTrucks);
		
	}
	
	public void createDepotMoocs(){
		Random r = new Random();
		depotMoocs = new DepotMooc[nbDepotMoocs];
		for(int i = 0; i < nbDepotMoocs; i++){
			DepotMooc dp = new DepotMooc();
			int idx = r.nextInt(codes.size());
			dp.setCode(codes.get(idx));
			dp.setLocationCode(codes.get(idx));
			codes.remove(idx);
			depotMoocs[i] = dp;
		}
		input.setDepotMoocs(depotMoocs);
	}
	
	public void createDepotContainers(){
		Random r = new Random();
		depotContainers = new DepotContainer[nbDepotContainers];
		for(int i = 0; i < nbDepotContainers; i++){
			DepotContainer dp = new DepotContainer();
			int idx = r.nextInt(codes.size());
			dp.setCode(codes.get(idx));
			dp.setLocationCode(codes.get(idx));
			codes.remove(idx);
			depotContainers[i] = dp;
		}
		input.setDepotContainers(depotContainers);
	}
	
	public void createTrucks(){
		Random r = new Random();
		trucks = new Truck[nbTrucks];
		for(int i = 0; i < nbTrucks; i++){
			Truck truck = new Truck();
			int idx = r.nextInt(codes.size());
			truck.setCode(codes.get(idx));
			codes.remove(idx);
			
			idx = r.nextInt(depotTrucks.length);
			truck.setDepotTruckCode(depotTrucks[idx].getCode());
			truck.setDepotTruckLocationCode(depotTrucks[idx].getLocationCode());
			
			idx = r.nextInt(depotTrucks.length);
			String[] returnDepotCodes = new String[1];
			returnDepotCodes[0] = depotTrucks[idx].getCode();
			truck.setReturnDepotCodes(returnDepotCodes);
			truck.setStartWorkingTime(
					DateTimeUtils.unixTimeStamp2DateTime(beginTime));
			trucks[i] = truck;
		}
		input.setTrucks(trucks);
	}
	
	public void createMoocs(){
		Random r = new Random();
		moocs = new Mooc[nbMoocs];
		for(int i = 0; i < nbMoocs; i++){
			Mooc mooc = new Mooc();
			int idx = r.nextInt(codes.size());
			mooc.setCode(codes.get(idx));
			codes.remove(idx);
			
			idx = r.nextInt(depotMoocs.length);
			mooc.setDepotMoocCode(depotMoocs[idx].getCode());
			mooc.setDepotMoocLocationCode(depotMoocs[idx].getLocationCode());
			
			idx = r.nextInt(depotMoocs.length);
			String[] returnDepotCodes = new String[1];
			returnDepotCodes[0] = depotMoocs[idx].getCode();
			mooc.setReturnDepotCodes(returnDepotCodes);
			moocs[i] = mooc;
		}
		input.setMoocs(moocs);
	}
	
	public void createContainers(){
		Random r = new Random();
		containers = new Container[nbContainers];
		temp_containers = new ArrayList<Container>();
		for(int i = 0; i < nbContainers; i++){
			Container container = new Container();
			int idx = r.nextInt(codes.size());
			container.setCode(codes.get(idx));
			codes.remove(idx);
			
			idx = r.nextInt(depotContainers.length);
			container.setDepotContainerCode(depotContainers[idx].getCode());

			idx = r.nextInt(depotContainers.length);
			String[] returnDepotCodes = new String[1];
			returnDepotCodes[0] = depotContainers[idx].getCode();
			container.setReturnDepotCodes(returnDepotCodes);
			containers[i] = container;
			temp_containers.add(container);
		}
		input.setContainers(containers);
	}
	
	public void createWarehouses(){
		Random r = new Random();
		warehouses = new Warehouse[nbWarehouses];
		for(int i = 0; i < nbWarehouses; i++){
			Warehouse warehouse = new Warehouse();
			int idx = r.nextInt(codes.size());
			warehouse.setCode(codes.get(idx));
			warehouse.setLocationCode(codes.get(idx));
			codes.remove(idx);
			warehouses[i] = warehouse;
		}
		input.setWarehouses(warehouses);
	}
	
	public void createPorts(){
		Random r = new Random();
		ports = new Port[nbPorts];
		for(int i = 0; i < nbPorts; i++){
			Port port = new Port();
			int idx = r.nextInt(codes.size());
			port.setCode(codes.get(idx));
			port.setLocationCode(codes.get(idx));
			codes.remove(idx);
			ports[i] = port;
		}
		input.setPorts(ports);
	}
	
	public void createExEmptyRequests(){
		Random r = new Random();
		exEmptyRequests = new ExportEmptyRequests[nbEE];
		for(int i = 0; i < nbEE; i++){
			ExportEmptyRequests e = new ExportEmptyRequests();
			int idx = r.nextInt(temp_containers.size());
			e.setContainerCode(temp_containers.get(idx).getCode());
			e.setDepotContainerCode(temp_containers.get(idx).getDepotContainerCode());
			temp_containers.remove(idx);
			
			idx = r.nextInt(warehouses.length);
			e.setWareHouseCode(warehouses[idx].getCode());
			
			long ear = r.nextInt(duration) + minEarliestTime;
			e.setEarlyDateTimePickupAtDepot(DateTimeUtils.unixTimeStamp2DateTime(ear));
			long late = ear + rangeTime;
			e.setLateDateTimePickupAtDepot(DateTimeUtils.unixTimeStamp2DateTime(late));
			ear += getTravelTime(e.getDepotContainerCode(),
					e.getWareHouseCode());
			e.setEarlyDateTimeLoadAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(ear));
			late = ear + rangeTime;
			e.setLateDateTimeLoadAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(late));
			
			exEmptyRequests[i] = e;
		}
		input.setExEmptyRequests(exEmptyRequests);
	}
	
	public void createExLadenRequests(){
		Random r = new Random();
		exLadenRequests = new ExportLadenRequests[nbEL];
		for(int i = 0; i < nbEL; i++){
			ExportLadenRequests e = new ExportLadenRequests();
			int idx = r.nextInt(temp_containers.size());
			e.setContainerCode(temp_containers.get(idx).getCode());
			temp_containers.remove(idx);
			
			idx = r.nextInt(ports.length);
			e.setPortCode(ports[idx].getCode());
			idx = r.nextInt(warehouses.length);
			e.setWareHouseCode(warehouses[idx].getCode());
			
			long ear = r.nextInt(duration) + minEarliestTime;
			e.setEarlyDateTimeAttachAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(ear));
			long late = ear + rangeTime 
					+ (long)getTravelTime(e.getWareHouseCode(),
					e.getPortCode());
			e.setLateDateTimeUnloadAtPort(DateTimeUtils
					.unixTimeStamp2DateTime(late));
			
			exLadenRequests[i] = e;
		}
		input.setExLadenRequests(exLadenRequests);
	}
	
	public void createImEmptyRequests(){
		Random r = new Random();
		imEmptyRequests = new ImportEmptyRequests[nbIE];
		for(int i = 0; i < nbIE; i++){
			ImportEmptyRequests e = new ImportEmptyRequests();
			int idx = r.nextInt(temp_containers.size());
			e.setContainerCode(temp_containers.get(idx).getCode());
			e.setDepotContainerCode(temp_containers.get(idx).getDepotContainerCode());
			temp_containers.remove(idx);
			
			idx = r.nextInt(warehouses.length);
			e.setWareHouseCode(warehouses[idx].getCode());
			
			long ear = r.nextInt(duration) + minEarliestTime;
			e.setEarlyDateTimeAttachAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(ear));
			long late = ear + rangeTime 
					+ (long)getTravelTime(e.getWareHouseCode(),
					e.getDepotContainerCode());
			e.setLateDateTimeReturnEmptyAtDepot(DateTimeUtils
					.unixTimeStamp2DateTime(late));
			imEmptyRequests[i] = e;
		}
		input.setImEmptyRequests(imEmptyRequests);
	}
	
	public void createImLadenRequests(){
		Random r = new Random();
		imLadenRequests = new ImportLadenRequests[nbIL];
		for(int i = 0; i < nbIL; i++){
			ImportLadenRequests e = new ImportLadenRequests();
			int idx = r.nextInt(temp_containers.size());
			e.setContainerCode(temp_containers.get(idx).getCode());
			temp_containers.remove(idx);
			
			idx = r.nextInt(warehouses.length);
			e.setWareHouseCode(warehouses[idx].getCode());
			
			idx = r.nextInt(ports.length);
			e.setPortCode(ports[idx].getCode());
			
			long ear = r.nextInt(duration) + minEarliestTime;
			e.setEarlyDateTimePickupAtPort(DateTimeUtils.unixTimeStamp2DateTime(ear));
			long late = ear + rangeTime;
			e.setLateDateTimePickupAtPort(DateTimeUtils.unixTimeStamp2DateTime(late));
			
			ear += getTravelTime(e.getPortCode(),
					e.getWareHouseCode());
			e.setEarlyDateTimeUnloadAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(ear));
			
			late = ear + rangeTime;
			e.setLateDateTimeUnloadAtWarehouse(DateTimeUtils
					.unixTimeStamp2DateTime(late));
			imLadenRequests[i] = e;
		}
		input.setImLadenRequests(imLadenRequests);
	}
	
	public void createConfigParams(){
		params = new ConfigParam();
		params.setCutMoocDuration(900);
		params.setLinkEmptyContainerDuration(900);
		params.setLinkLoadedContainerDuration(900);
		params.setLinkMoocDuration(900);
		params.setUnlinkEmptyContainerDuration(900);
		params.setUnlinkLoadedContainerDuration(900);
		input.setParams(params);
	}
	
	public double getTravelTime(String src, String dest){
		return matrix[mCode2idx.get(src)][mCode2idx.get(dest)];
	}
	/***
	 * tao du lieu 1-1: 1 truck, 1 mooc phuc vu 1 requests
	 */
	public void createJsonFile(String fileName){
		initParams();
		createCodesAndTravelTimeMatrix();
		
		createDepotTrucks();
		createTrucks();
		createDepotMoocs();
		createMoocs();
		createDepotContainers();
		createContainers();
		createWarehouses();
		createPorts();
		
		createExEmptyRequests();
		createExLadenRequests();
		createImEmptyRequests();
		createImLadenRequests();
		
		createConfigParams();
		
		try{
			Gson gson = new Gson();
			FileWriter fo = new FileWriter(fileName);
			gson.toJson(input, fo);
			fo.close();
		}catch(Exception e){
			
		}
	}
	public DataAnalysis(TruckContainerSolver solver){
		super();
		this.solver = solver;
		this.input = solver.input;
	}
	public DataAnalysis(){
		solver = new TruckContainerSolver();
	}
	
	public void fixError(){
		//fix depot container null
		for(int j = 0; j < solver.input.getContainers().length; j++){
			Container container = solver.input.getContainers()[j];
			if(container.isImportedContainer() == false){
				DepotContainer depotCont = solver.mCode2DepotContainer.get(container.getDepotContainerCode());
				if(depotCont == null){
					Random r = new Random();
					int idx = r.nextInt(solver.input.getDepotContainers().length);
					container.setDepotContainerCode(solver.input.getDepotContainers()[idx].getCode());
				}
			}
		}
		
		//fix time window
		fixTimeWindow();
	}
	
	public Truck getNearestTruck(String locationCode){
		int minTime = Integer.MAX_VALUE;
		Truck truck = null;
		for(int i = 0; i < input.getTrucks().length; i++){
			int d = solver.getTravelTime(input.getTrucks()[i].getDepotTruckLocationCode(),
					locationCode);
			if(d < minTime){
				minTime = d;
				truck = input.getTrucks()[i];
			}
		}
		return truck;
	}
	
	public Mooc getNearestMooc(String locationCode){
		int minTime = Integer.MAX_VALUE;
		Mooc mooc = null;
		for(int i = 0; i < input.getMoocs().length; i++){
			int d = solver.getTravelTime(input.getMoocs()[i].getDepotMoocLocationCode(),
					locationCode);
			if(d < minTime){
				minTime = d;
				mooc = input.getMoocs()[i];
			}
		}
		return mooc;
	}
	
	public int getMinTravelTimeTruckMooc(String locationCode){
		Truck truck = getNearestTruck(locationCode);
		Mooc mooc = getNearestMooc(locationCode);
		if(truck != null && mooc != null){
			return solver.getTravelTime(truck.getDepotTruckLocationCode(),
					mooc.getDepotMoocLocationCode())
					+ solver.getTravelTime(mooc.getDepotMoocLocationCode(),
					locationCode);
		}
		return 0;
	}
	
	/***
	 * e < l
	 */
	public void fixTimeWindow(){
		if(input.getExRequests() != null){
			for(int i = 0; i < input.getExRequests().length; i++){
				ExportContainerTruckMoocRequest R = input.getExRequests()[i];
				if(R.getContainerRequest() != null){
					for(int j = 0; j< R.getContainerRequest().length; j++){
						ExportContainerRequest r = R.getContainerRequest()[j];
						int et = 0;
						int lt = 0;
						DepotContainer depotcont = solver.mCode2DepotContainer.get(r.getDepotContainerCode());
						int tv = getMinTravelTimeTruckMooc(depotcont.getLocationCode());
						if(r.getEarlyDateTimePickupAtDepot() != null){
							et = (int)DateTimeUtils.dateTime2Int(
									r.getEarlyDateTimePickupAtDepot());
						}
						if(r.getLateDateTimePickupAtDepot() != null){
							lt = (int)DateTimeUtils.dateTime2Int(
									r.getLateDateTimePickupAtDepot());
						}
						if(et >= lt){
							
							lt = et + 25200;
						}
						if(tv >= lt){
							lt = tv + 25200;
						}
						r.setLateDateTimePickupAtDepot(DateTimeUtils.unixTimeStamp2DateTime(lt));
						
						Warehouse wh = solver.mCode2Warehouse.get(r.getPickupWarehouses()[0].getWareHouseCode());
						Port port = solver.mCode2Port.get(r.getPortCode());
						tv += solver.getTravelTime(depotcont.getLocationCode(),wh.getLocationCode());
						tv += solver.getTravelTime(wh.getLocationCode(), port.getLocationCode());
						if(r.getEarlyDateTimeUnloadAtPort() != null){
							et = (int)DateTimeUtils.dateTime2Int(
									r.getEarlyDateTimeUnloadAtPort());
						}
						if(r.getLateDateTimeUnloadAtPort() != null){
							lt = (int)DateTimeUtils.dateTime2Int(
									r.getLateDateTimeUnloadAtPort());
						}
						if(et >= lt){
							
							lt = et + 25200;
						}
						if(tv >= lt){
							lt = tv + 25200;
						}
						r.setLateDateTimeUnloadAtPort(DateTimeUtils.unixTimeStamp2DateTime(lt));
					}
				}
			}
		}
		
		if(input.getImRequests() != null){
			for(int i = 0; i < input.getImRequests().length; i++){
				ImportContainerTruckMoocRequest R = input.getImRequests()[i];
				if(R.getContainerRequest() != null){
					for(int j = 0; j < R.getContainerRequest().length; j++){
						ImportContainerRequest r = R.getContainerRequest()[j];
						int et = 0;
						int lt = 0;
						Port port = solver.mCode2Port.get(r.getPortCode());
						
						int tv = getMinTravelTimeTruckMooc(port.getLocationCode());
						if(r.getEarlyDateTimePickupAtPort() != null){
							et = (int)DateTimeUtils.dateTime2Int(
									r.getEarlyDateTimePickupAtPort());
						}
						if(r.getLateDateTimePickupAtPort() != null){
							lt = (int)DateTimeUtils.dateTime2Int(
									r.getLateDateTimePickupAtPort());
						}
						if(et >= lt){
							
							lt = et + 25200;
						}
						if(tv >= lt){
							lt = tv + 25200;
						}
						r.setLateDateTimePickupAtPort(DateTimeUtils.unixTimeStamp2DateTime(lt));
						
						Warehouse wh = solver.mCode2Warehouse.get(r.getDeliveryWarehouses()[0].getWareHouseCode());
						DepotContainer depotcont = solver.mCode2DepotContainer.get(r.getDepotContainerCode()[0]);
						tv += solver.getTravelTime(port.getLocationCode(),wh.getLocationCode());
						tv += solver.getTravelTime(wh.getLocationCode(), depotcont.getLocationCode());
						if(r.getEarlyDateTimeDeliveryAtDepot() != null){
							et = (int)DateTimeUtils.dateTime2Int(
									r.getEarlyDateTimeDeliveryAtDepot());
						}
						if(r.getLateDateTimeDeliveryAtDepot() != null){
							lt = (int)DateTimeUtils.dateTime2Int(
									r.getLateDateTimeDeliveryAtDepot());
						}
						if(et >= lt){
							
							lt = et + 25200;
						}
						if(tv >= lt){
							lt = tv + 25200;
						}
						r.setLateDateTimeDeliveryAtDepot(DateTimeUtils.unixTimeStamp2DateTime(lt));
					}
				}
			}
		}
		
		if(input.getImEmptyRequests() != null){
			for(int i = 0; i < input.getImEmptyRequests().length; i++){
				ImportEmptyRequests r = input.getImEmptyRequests()[i];
				int et = 0;
				int lt = 0;
				
				Warehouse wh = solver.mCode2Warehouse.get(r.getWareHouseCode());
				int tv = getMinTravelTimeTruckMooc(wh.getLocationCode());
				
				DepotContainer depotcont = solver.mCode2DepotContainer.get(r.getDepotContainerCode());
				tv += solver.getTravelTime(wh.getLocationCode(), depotcont.getLocationCode());
				if(r.getEarlyDateTimeAttachAtWarehouse() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimeAttachAtWarehouse());
				}
				if(r.getLateDateTimeReturnEmptyAtDepot() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimeReturnEmptyAtDepot());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimeReturnEmptyAtDepot(DateTimeUtils.unixTimeStamp2DateTime(lt));
			}
		}
		if(input.getImLadenRequests() != null){
			for(int i= 0; i < input.getImLadenRequests().length; i++){
				ImportLadenRequests r = input.getImLadenRequests()[i];
				int et = 0;
				int lt = 0;
				Port port = solver.mCode2Port.get(r.getPortCode());
				
				int tv = getMinTravelTimeTruckMooc(port.getLocationCode());
				if(r.getEarlyDateTimePickupAtPort() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimePickupAtPort());
				}
				if(r.getLateDateTimePickupAtPort() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimePickupAtPort());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimePickupAtPort(DateTimeUtils.unixTimeStamp2DateTime(lt));
				Warehouse wh = solver.mCode2Warehouse.get(r.getWareHouseCode());
				tv += solver.getTravelTime(port.getLocationCode(),wh.getLocationCode());
				if(r.getEarlyDateTimeUnloadAtWarehouse() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimeUnloadAtWarehouse());
				}
				if(r.getLateDateTimeUnloadAtWarehouse() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimeUnloadAtWarehouse());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimeUnloadAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(lt));
				
			}
		}
		
		if(input.getExEmptyRequests() != null){
			for(int i = 0; i < input.getExEmptyRequests().length; i++){
				ExportEmptyRequests r = input.getExEmptyRequests()[i];
				int et = 0;
				int lt = 0;
				DepotContainer depotcont = solver.mCode2DepotContainer.get(r.getDepotContainerCode());
				int tv = getMinTravelTimeTruckMooc(depotcont.getLocationCode());
				if(r.getEarlyDateTimePickupAtDepot() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimePickupAtDepot());
				}
				if(r.getLateDateTimePickupAtDepot() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimePickupAtDepot());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimePickupAtDepot(DateTimeUtils.unixTimeStamp2DateTime(lt));
				
				Warehouse wh = solver.mCode2Warehouse.get(r.getWareHouseCode());
				tv += solver.getTravelTime(depotcont.getLocationCode(), wh.getLocationCode());
				if(r.getEarlyDateTimeLoadAtWarehouse() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimeLoadAtWarehouse());
				}
				if(r.getLateDateTimeLoadAtWarehouse() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimeLoadAtWarehouse());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimeLoadAtWarehouse(DateTimeUtils.unixTimeStamp2DateTime(lt));
			}
		}
		if(input.getExLadenRequests() != null){
			for(int i = 0; i < input.getExLadenRequests().length; i++){
				ExportLadenRequests r = input.getExLadenRequests()[i];
				int et = 0;
				int lt = 0;
				Warehouse wh = solver.mCode2Warehouse.get(r.getWareHouseCode());
				Port port = solver.mCode2Port.get(r.getPortCode());
				int tv = getMinTravelTimeTruckMooc(wh.getLocationCode());
				tv += solver.getTravelTime(wh.getLocationCode(), port.getLocationCode());
				if(r.getEarlyDateTimeAttachAtWarehouse() != null){
					et = (int)DateTimeUtils.dateTime2Int(
							r.getEarlyDateTimeAttachAtWarehouse());
				}
				if(r.getLateDateTimeUnloadAtPort() != null){
					lt = (int)DateTimeUtils.dateTime2Int(
							r.getLateDateTimeUnloadAtPort());
				}
				if(et >= lt){
					
					lt = et + 25200;
				}
				if(tv >= lt){
					lt = tv + 25200;
				}
				r.setLateDateTimeUnloadAtPort(DateTimeUtils.unixTimeStamp2DateTime(lt));
			}
		}
	}
	
	public void updateJsonFile(String dataFileName){
		try{
			Gson gson = new Gson();
			File fo = new File(dataFileName);
			FileWriter fw = new FileWriter(fo);
			gson.toJson(solver.input, fw);
			fw.close();
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static JSONObject deepMerge(JSONObject source, JSONObject target) {
		try{
			for (String key: JSONObject.getNames(source)) {
			        Object value = source.get(key);
			        if (!target.has(key)) {
			            // new value for "key":
			            target.put(key, value);
			        } else {
			            // existing value for "key" - recursively deep merge:
			            if (value instanceof JSONObject) {
			                JSONObject valueJson = (JSONObject)value;
			                deepMerge(valueJson, target.getJSONObject(key));
			            } else {
			                //target.put(key, value);
			            	if (value instanceof JSONArray) {
				            	JSONArray srcArray = (JSONArray)value;
				            	JSONArray targetArray = (JSONArray)target.get(key);
				            	for(int i = 0; i < srcArray.length(); i++){
				            		boolean ex = false;
				            		for(int j = 0; j < targetArray.length(); j++){
				            			String t = targetArray.get(j).toString();
				            			String s = srcArray.get(i).toString();
				            			if(s.equals(t)){
				            				ex = true;
				            				break;
				            			}
				            		}
				            		if(!ex)
				            			targetArray.put(srcArray.get(i));
				            	}
				            	target.put(key, (Object)targetArray);
			            	}
			            	else
			            		target.put(key, value);
			                
			            }
			        }
			}
		}catch(Exception e){
			System.out.println(e);
		}
	    return target;
	}
	
	public ArrayList<String> getDayList(){
		ArrayList<String> days = new ArrayList<String>();
		days.add("1802");
		days.add("1902");
		days.add("2102");
		days.add("2202");
		days.add("2502");
		days.add("2602");
		days.add("2702");
		return days;
	}
	public void mergeFile(){
		try{
			ArrayList<String> days = getDayList();
			Gson gson = new Gson();
			String dataFileName = "data/truck-container/input_1802.json";
			solver.readData(dataFileName);
		    String bString = gson.toJson(solver.input);
			JSONObject b = new JSONObject(bString);
			for(int i = 1; i <days.size(); i++){
				System.out.println("day " + days.get(i));
				dataFileName = "data/truck-container/input_" + days.get(i) + ".json";
				TruckContainerSolver s = new TruckContainerSolver();
				s.readData(dataFileName);
				String aString = gson.toJson(s.input);
				JSONObject a = new JSONObject(aString);
				deepMerge(a, b);
			}
		
		
			dataFileName = "data/truck-container/generated_input.json";
			File fo = new File(dataFileName);
			FileWriter fw = new FileWriter(fo);
			fw.write(b.toString());
			fw.close();
			System.out.print("Done");
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void updateData(){
		String dataFileName = "data/truck-container/input_1802.json";
		solver = new TruckContainerSolver();
		solver.readData(dataFileName);
		
		fixError();
		updateJsonFile(dataFileName);
	}

	public static void main(String[] args){
		DataAnalysis da = new DataAnalysis();
		//chinh sua lai file du lieu
		//da.updateData();
		
		//merge cac filde du lieu thanh 1 file de test
		//da.mergeFile();
		
		//tao file du lieu lon de test
		String fileName = "data/truck-container/random_big_data.json";
		da.createJsonFile(fileName);
	}
}
