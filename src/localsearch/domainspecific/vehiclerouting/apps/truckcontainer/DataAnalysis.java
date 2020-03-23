package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
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
	
	public DataAnalysis(){
		
	}
	
	public void readNewOutputForSummarise_ALNS(){
		int[] nbReq = new int[]{8};//, 70, 100, 150, 200};
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newOutput/summarise-ALNS.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Iteration Inst nbReq nbRejectReq cost time");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		for(int k = 0; k < 10; k++){
			for(int i = 0; i < 5; i++){
				for(int j = 0; j < nbReq.length; j++){
					
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputALNSfileTxt = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".txt";
					//String outputALNSfileJson = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".json";
					
					ScannerInput sc = new ScannerInput(outputALNSfileTxt);
					
					String str = "";
					boolean is = false;
					ArrayList<Integer> iterList = new ArrayList<Integer>();
					ArrayList<Long> timeList = new ArrayList<Long>();
					ArrayList<Double> costList = new ArrayList<Double>();
					ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
					ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
					
					int bestNbTruck = 1000000;
					double bestCost = 10000000;
					int bestNbReject = 100000;
					long bestTime = -1;
					while(str != null){
						str = sc.readLine();
						if(str.contains("iter"))
							is = true;
						if(str.contains("route"))
							break;
						if(!is)
							continue;
						else{
							if(str.contains("iter"))
								continue;
							str = str.trim().replaceAll("\\s+", " ");
							String[] brLine = str.split(" ");
							iterList.add(Integer.parseInt(brLine[0]));
							timeList.add(Long.parseLong(brLine[3]));
							costList.add(Double.parseDouble(brLine[4]));
							int nbR = Integer.parseInt(brLine[5]);
							nbRejectList.add(nbR);
							if(bestNbReject > nbR)
								bestNbReject = nbR;
							nbTruckList.add(Integer.parseInt(brLine[6]));
						}						
					}
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
						}
					}
					
					try{
						FileOutputStream write = new FileOutputStream(summariseFile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(k + " " + i + " " + nbReq[j] + " "
							+ bestNbReject + " " + bestCost + " " + bestTime);
						fo.close();
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
			
		}
	}
	
	public void readNewOutputForSummarise_HASBPIUS(){
		int[] nbReq = new int[]{8, 70, 100, 150, 200};
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newOutput/summarise-HASBPIUS.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Iteration Inst nbReq nbRejectReq cost time");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		for(int k = 0; k < 6; k++){
			if(k==3)
				continue;
			for(int i = 0; i < 5; i++){
				for(int j = 0; j < nbReq.length; j++){
					
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputHASfileTxt = dir + "output/newOutput/It-" + k +"-HASBPIUS-" + fileName + ".txt";
					//String outputALNSfileJson = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".json";
					
					ScannerInput sc = new ScannerInput(outputHASfileTxt);
					
					String str = "";
					ArrayList<Integer> iterList = new ArrayList<Integer>();
					ArrayList<Long> timeList = new ArrayList<Long>();
					ArrayList<Double> costList = new ArrayList<Double>();
					ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
					ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
					
					int bestNbTruck = 1000000;
					double bestCost = 10000000;
					int bestNbReject = 100000;
					long bestTime = -1;
					
					str = sc.readLine();
					while(str != null){
						str = sc.readLine();
						if(str.contains("route"))
							break;
						str = str.trim().replaceAll("\\s+", " ");
						String[] brLine = str.split(" ");
						iterList.add(Integer.parseInt(brLine[0]));
						timeList.add(Long.parseLong(brLine[1]));
						costList.add(Double.parseDouble(brLine[2]));
						int nbR = Integer.parseInt(brLine[3]);
						nbRejectList.add(nbR);
						if(bestNbReject > nbR)
							bestNbReject = nbR;
						nbTruckList.add(Integer.parseInt(brLine[4]));						
					}
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
						}
					}
					
					try{
						FileOutputStream write = new FileOutputStream(summariseFile, true);
						PrintWriter fo = new PrintWriter(write);
						fo.println(k + " " + i + " " + nbReq[j] + " "
							+ bestNbReject + " " + bestCost + " " + bestTime);
						fo.close();
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
			
		}
	}
	
	public void readHeuristicCostForSummarise_ALNS_HASBPIUS(){
		int[] nbReq = new int[]{8, 70, 100, 150, 200};
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newOutput/summarise-ALNS-HASBPIUS.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Ins & min_cost_HAS & max_cost_HAS & avg_cost_HAS & std_HAS & avg_time_HAS & min_cost_ALNS & max_cost_ALNS & avg_cost_ALNS & std_ALNS & avg_time_ALNS & nuy1 \\\\");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		for(int j = 0; j < nbReq.length; j++){
			for(int i = 0; i < 5; i++){
				double min_cost_A = Double.MAX_VALUE;
				double max_cost_A = -1;
				double avg_cost_A = 0;
				double avg_time_A = 0;
				double std_A = 0;
				
				double min_cost_H = Double.MAX_VALUE;
				double max_cost_H = -1;
				double avg_cost_H = 0;
				double avg_time_H = 0;
				double std_H = 0;
				
				double nuy1;
				
				ArrayList<Double> cost_A = new ArrayList<Double>();
				ArrayList<Double> cost_H = new ArrayList<Double>();
				
				for(int k = 0; k < 10; k++){
					
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputALNSfileTxt = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".txt";
					String outputHASfileTxt = dir + "output/newOutput/It-" + k +"-HASBPIUS-" + fileName + ".txt";
					
					ScannerInput sc = new ScannerInput(outputALNSfileTxt);
					
					String str = "";
					boolean is = false;
					ArrayList<Integer> iterList = new ArrayList<Integer>();
					ArrayList<Long> timeList = new ArrayList<Long>();
					ArrayList<Double> costList = new ArrayList<Double>();
					ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
					ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
					
					int bestNbTruck = 1000000;
					double bestCost = 10000000;
					int bestNbReject = 100000;
					long bestTime = -1;
					while(str != null){
						str = sc.readLine();
						if(str.contains("iter"))
							is = true;
						if(str.contains("route"))
							break;
						if(!is)
							continue;
						else{
							if(str.contains("iter"))
								continue;
							str = str.trim().replaceAll("\\s+", " ");
							String[] brLine = str.split(" ");
							iterList.add(Integer.parseInt(brLine[0]));
							timeList.add(Long.parseLong(brLine[3]));
							costList.add(Double.parseDouble(brLine[4]));
							int nbR = Integer.parseInt(brLine[5]);
							nbRejectList.add(nbR);
							if(bestNbReject > nbR)
								bestNbReject = nbR;
							nbTruckList.add(Integer.parseInt(brLine[6]));
						}						
					}
					sc.close();
					
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
						}
					}
					
					if(min_cost_A > bestCost)
						min_cost_A = bestCost;
					if(max_cost_A < bestCost)
						max_cost_A = bestCost;
					avg_cost_A += bestCost;
					avg_time_A += bestTime;
					cost_A.add(bestCost);
					
					sc = new ScannerInput(outputHASfileTxt);
					
					str = "";
					iterList = new ArrayList<Integer>();
					timeList = new ArrayList<Long>();
					costList = new ArrayList<Double>();
					nbRejectList = new ArrayList<Integer>();
					nbTruckList = new ArrayList<Integer>();
					
					bestNbTruck = 1000000;
					bestCost = 10000000;
					bestNbReject = 100000;
					bestTime = -1;
					
					str = sc.readLine();
					while(str != null){
						str = sc.readLine();
						if(str.contains("route"))
							break;
						str = str.trim().replaceAll("\\s+", " ");
						String[] brLine = str.split(" ");
						iterList.add(Integer.parseInt(brLine[0]));
						timeList.add(Long.parseLong(brLine[1]));
						costList.add(Double.parseDouble(brLine[2]));
						int nbR = Integer.parseInt(brLine[3]);
						nbRejectList.add(nbR);
						if(bestNbReject > nbR)
							bestNbReject = nbR;
						nbTruckList.add(Integer.parseInt(brLine[4]));						
					}
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
						}
					}
					
					if(min_cost_H > bestCost)
						min_cost_H = bestCost;
					if(max_cost_H < bestCost)
						max_cost_H = bestCost;
					avg_cost_H += bestCost;
					avg_time_H += bestTime;
					cost_H.add(bestCost);
				}
				
				std_A = calculateSTDEV(cost_A);
				std_H = calculateSTDEV(cost_H);
				avg_cost_A = avg_cost_A / 10;
				avg_cost_H = avg_cost_H / 10;
				avg_time_A = avg_time_A / 10;
				avg_time_H = avg_time_H / 10;
				nuy1 = Math.round((avg_cost_A - avg_cost_H)*10000/avg_cost_H); 
				try{
					FileOutputStream write = new FileOutputStream(summariseFile, true);
					PrintWriter fo = new PrintWriter(write);
					fo.println("\\textit{N}-" + nbReq[j] + "-" + i + " & " + Math.round(min_cost_H/1000) + " & "
						+ Math.round(max_cost_H/1000) + " & " + Math.round(avg_cost_H/1000) + " & " + Math.round(std_H/1000) + " & "
						+ Math.round(avg_time_H*100)/100 + " & " + Math.round(min_cost_A/1000) + " & " + Math.round(max_cost_A/1000) + " & "
						+ Math.round(avg_cost_A/1000) + " & " + Math.round(std_A/1000) + " & " + Math.round(avg_time_A*100)/100 + " & "
						+ nuy1/100 + " \\\\");
					fo.println("\\hline");
					fo.close();
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}
	}
	
	public void readHeuristicCostandRejectForSummarise_ALNS_HASBPIUS(){
		int[] nbReq = new int[]{20, 70, 100, 150, 200};
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newOutput/summarise-ALNS-HASBPIUS-reject-truck-cost.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Ins & avg_reject_HAS & avg_truck_HAS & avg_cost_HAS & std_HAS & avg_time_HAS & avg_reject_ALNS & avg_truck_ALNS & avg_cost_ALNS & std_ALNS & avg_time_ALNS & nuy1 \\\\");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		for(int j = 0; j < nbReq.length; j++){
			for(int i = 0; i < 5; i++){
				double min_cost_A = Double.MAX_VALUE;
				double max_cost_A = -1;
				double avg_cost_A = 0;
				double avg_time_A = 0;
				double avg_truck_A = 0;
				double avg_reject_A = 0;
				double std_A = 0;
				
				double min_cost_H = Double.MAX_VALUE;
				double max_cost_H = -1;
				double avg_cost_H = 0;
				double avg_time_H = 0;
				double avg_truck_H = 0;
				double avg_reject_H = 0;
				double std_H = 0;
				
				double nuy1;
				
				ArrayList<Double> cost_A = new ArrayList<Double>();
				ArrayList<Double> cost_H = new ArrayList<Double>();
				
				for(int k = 0; k < 10; k++){
					
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputALNSfileTxt = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".txt";
					String outputHASfileTxt = dir + "output/newOutput/It-" + k +"-HASBPIUS-" + fileName + ".txt";
					
					ScannerInput sc = new ScannerInput(outputALNSfileTxt);
					
					String str = "";
					boolean is = false;
					ArrayList<Integer> iterList = new ArrayList<Integer>();
					ArrayList<Long> timeList = new ArrayList<Long>();
					ArrayList<Double> costList = new ArrayList<Double>();
					ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
					ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
					
					int bestNbTruck = 1000000;
					double bestCost = 10000000;
					int bestNbReject = 100000;
					long bestTime = -1;
					while(str != null){
						str = sc.readLine();
						if(str.contains("iter"))
							is = true;
						if(str.contains("route"))
							break;
						if(!is)
							continue;
						else{
							if(str.contains("iter"))
								continue;
							str = str.trim().replaceAll("\\s+", " ");
							String[] brLine = str.split(" ");
							iterList.add(Integer.parseInt(brLine[0]));
							timeList.add(Long.parseLong(brLine[3]));
							costList.add(Double.parseDouble(brLine[4]));
							int nbR = Integer.parseInt(brLine[5]);
							nbRejectList.add(nbR);
							if(bestNbReject > nbR)
								bestNbReject = nbR;
							nbTruckList.add(Integer.parseInt(brLine[6]));
						}						
					}
					sc.close();
					
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
						}
					}
					
					if(min_cost_A > bestCost)
						min_cost_A = bestCost;
					if(max_cost_A < bestCost)
						max_cost_A = bestCost;
					avg_cost_A += bestCost;
					avg_time_A += bestTime;
					avg_reject_A += bestNbReject;
					avg_truck_A += bestNbTruck;
					cost_A.add(bestCost);
					
					sc = new ScannerInput(outputHASfileTxt);
					
					str = "";
					iterList = new ArrayList<Integer>();
					timeList = new ArrayList<Long>();
					costList = new ArrayList<Double>();
					nbRejectList = new ArrayList<Integer>();
					nbTruckList = new ArrayList<Integer>();
					
					bestNbTruck = 1000000;
					bestCost = 10000000;
					bestNbReject = 100000;
					bestTime = -1;
					
					str = sc.readLine();
					while(str != null){
						str = sc.readLine();
						if(str.contains("route"))
							break;
						str = str.trim().replaceAll("\\s+", " ");
						String[] brLine = str.split(" ");
						iterList.add(Integer.parseInt(brLine[0]));
						timeList.add(Long.parseLong(brLine[1]));
						costList.add(Double.parseDouble(brLine[2]));
						int nbR = Integer.parseInt(brLine[3]);
						nbRejectList.add(nbR);
						if(bestNbReject > nbR)
							bestNbReject = nbR;
						nbTruckList.add(Integer.parseInt(brLine[4]));						
					}
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r) - timeList.get(0);
							}
						}
					}
					
					if(min_cost_H > bestCost)
						min_cost_H = bestCost;
					if(max_cost_H < bestCost)
						max_cost_H = bestCost;
					avg_cost_H += bestCost;
					avg_time_H += bestTime;
					avg_reject_H += bestNbReject;
					avg_truck_H += bestNbTruck;
					cost_H.add(bestCost);
				}
				
				std_A = calculateSTDEV(cost_A);
				std_H = calculateSTDEV(cost_H);
				avg_cost_A = avg_cost_A / 10;
				avg_cost_H = avg_cost_H / 10;
				avg_time_A = avg_time_A / 10;
				//avg_time_A = Math.round(avg_time_A*100)/100;
				avg_time_H = avg_time_H / 10;
				avg_reject_A = avg_reject_A / 10;
				avg_truck_A = avg_truck_A / 10;
				avg_reject_H = avg_reject_H / 10;
				avg_truck_H = avg_truck_H / 10;
				
				nuy1 = Math.round((avg_cost_A - avg_cost_H)*10000/avg_cost_H); 
				try{
					FileOutputStream write = new FileOutputStream(summariseFile, true);
					PrintWriter fo = new PrintWriter(write);
					fo.println("\\textit{N}-" + nbReq[j] + "-" + i 
						+ " & " + Math.round(avg_cost_H/1000) + " & " + Math.round(std_H/1000) 
						+ " & " + avg_time_H + " & " + avg_reject_H + " & " + avg_truck_H 
						+ " & " + Math.round(avg_cost_A/1000) + " & " + Math.round(std_A/1000) 
						+ " & " + avg_time_A + " & " + avg_reject_A + " & " + avg_truck_A + " & "
						+ nuy1/100 + " \\\\");
					fo.println("\\hline");
					fo.close();
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}
	}
	
	public void readNewTuningForSummarise_ALNS(){
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/summarise-ALNS-tuning.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("i1 i2 i3 i4 i5 avg_cost");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		double[] lower_removal_list = new double[]{0.05, 0.1};
		double[] upper_removal_list = new double[]{0.2, 0.25};
		int[] sigma1_list = new int[]{3, 5, 10};
		int[] sigma2_list = new int[]{1, 0, -1};
		int[] sigma3_list = new int[]{-3, -5, -10};
		
		int nIters = 10;
		for(int i1 = 0; i1 < lower_removal_list.length; i1++){
			for(int i2 = 0; i2 < upper_removal_list.length; i2++){
				for(int i3 = 0; i3 < sigma1_list.length; i3++){
					for(int i4 = 0; i4 < sigma2_list.length; i4++){
						for(int i5 = 0; i5 < sigma3_list.length; i5++){
							double avg_cost = 0;
							for(int k = 1; k <= nIters; k++){
								String fileName = "random-20reqs-RealLoc-0";
								
								String outputALNSfileTxt = dir + "output/newTuning-" + k + "/ALNS-" + i1 + "-" + i2 + "-" + i3 + "-" + i4 + "-" + i5 + ".txt";
								//String outputALNSfileJson = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".json";
								
								try
								{
									File temp = new File(outputALNSfileTxt);
									if(!temp.exists()){
										System.out.println("k = " + k + ", i1 = " + i1 + ", i2 = " + i2 + ", i3 = " + i3 + ", i4 = " + i4 + ", i5 =" + i5);
										continue;
									}
								} catch (Exception e){
									e.printStackTrace();
								}
								
								ScannerInput sc = new ScannerInput(outputALNSfileTxt);
								
								String str = "";
								boolean is = false;
								ArrayList<Integer> iterList = new ArrayList<Integer>();
								ArrayList<Long> timeList = new ArrayList<Long>();
								ArrayList<Double> costList = new ArrayList<Double>();
								ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
								ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
								
								int bestNbTruck = 1000000;
								double bestCost = 10000000;
								int bestNbReject = 100000;
								long bestTime = -1;
								
								while(str != null){
									str = sc.readLine();
									if(str.contains("iter"))
										is = true;
									if(str.contains("route"))
										break;
									if(!is)
										continue;
									else{
										if(str.contains("iter"))
											continue;
										str = str.trim().replaceAll("\\s+", " ");
										String[] brLine = str.split(" ");
										iterList.add(Integer.parseInt(brLine[0]));
										timeList.add(Long.parseLong(brLine[3]));
										costList.add(Double.parseDouble(brLine[4]));
										int nbR = Integer.parseInt(brLine[5]);
										nbRejectList.add(nbR);
										if(bestNbReject > nbR)
											bestNbReject = nbR;
										nbTruckList.add(Integer.parseInt(brLine[6]));
									}						
								}
								for(int r = 0; r < nbRejectList.size(); r++){
									if(nbRejectList.get(r) == bestNbReject){
										if(costList.get(r) < bestCost){
											bestCost = costList.get(r);
											bestNbTruck = nbTruckList.get(r);
											bestTime = timeList.get(r) - timeList.get(0);
										}
										else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
											bestCost = costList.get(r);
											bestNbTruck = nbTruckList.get(r);
											bestTime = timeList.get(r) - timeList.get(0);
										}
									}
								}
								
								avg_cost += bestCost;
							}
							
							avg_cost = avg_cost / nIters;
							try{
								FileOutputStream write = new FileOutputStream(summariseFile, true);
								PrintWriter fo = new PrintWriter(write);
								fo.println(lower_removal_list[i1] + " & " + upper_removal_list[i2] + " & " + sigma1_list[i3] + " & " + sigma2_list[i4] + " & " + sigma3_list[i5] + " & "
									+ Math.round(avg_cost) + " \\\\");
								fo.close();
							}catch(Exception e){
								System.out.println(e);
							}
						}
					}
				}
			}
		}
	}
	
	public void readNewBPIForSummarise(){
		String dir = "data/truck-container/";
		String summariseBPIandBPIUSFile = dir + "output/newBPIandBPIUS/summarise-BPIandBPIUS.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseBPIandBPIUSFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Ins & min_cost_BPI & max_cost_BPI & avg_cost_BPI & stv_BPI & avg_time_BPI & min_cost_BPIUS & max_cost_BPIUS & avg_cost_BPIUS & stv_BPIUS & avg_time_BPIUS & nuy1 \\\\");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		int[] nbReq = new int[]{20, 70, 100, 150, 200};
		int nbIters = 10;
		
		for(int j = 0; j < nbReq.length; j++){
			for(int i = 0; i < 5; i++){
				double avg_cost_BPI = 0;
				double avg_time_BPI = 0;
				double avg_cost_BPIUS = 0;
				double avg_time_BPIUS = 0;
				double max_cost_BPI = -1;
				double min_cost_BPI = Integer.MAX_VALUE;
				double stdev_BPI = 0;
				double max_cost_BPIUS = -1;
				double min_cost_BPIUS = Integer.MAX_VALUE;
				double stdev_BPIUS = 0;
				ArrayList<Double> cost_BPI = new ArrayList<Double>();
				ArrayList<Double> cost_BPIUS = new ArrayList<Double>();
				
				for(int it = 0; it < nbIters; it++){
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputBPIfileTxt = dir + "output/newBPIandBPIUS/It-" + it + "-BPI-" + fileName + ".txt";
					String outputBPIUSfileTxt = dir + "output/newBPIandBPIUS/It-" + it + "-BPIUS-" + fileName + ".txt";
					
					ScannerInput sc = new ScannerInput(outputBPIfileTxt);
					
					String str = "";
					while(str != null){
						str = sc.readLine();
						if(str.contains("end time")){
							String[] brLine = str.split(",");
							String[] cost = brLine[3].split("=");
							String[] time = brLine[4].split("=");
							double cost_int = Double.parseDouble(cost[1]);
							avg_cost_BPI += cost_int;
							avg_time_BPI += Double.parseDouble(time[1]);
							if(cost_int < min_cost_BPI)
								min_cost_BPI = cost_int;
							if(cost_int > max_cost_BPI)
								max_cost_BPI = cost_int;
							cost_BPI.add(cost_int);
							break;
						}						
					}
					sc.close();
					
					sc = new ScannerInput(outputBPIUSfileTxt);
					
					str = "";
					while(str != null){
						str = sc.readLine();
						if(str.contains("end time")){
							String[] brLine = str.split(",");
							String[] cost = brLine[3].split("=");
							String[] time = brLine[4].split("=");
							double cost_int = Double.parseDouble(cost[1]);
							avg_cost_BPIUS += cost_int;
							avg_time_BPIUS += Double.parseDouble(time[1]);
							
							if(cost_int < min_cost_BPIUS)
								min_cost_BPIUS = cost_int;
							if(cost_int > max_cost_BPIUS)
								max_cost_BPIUS = cost_int;
							cost_BPIUS.add(cost_int);
							
							break;
						}						
					}
					sc.close();
				}
				avg_cost_BPI = avg_cost_BPI/nbIters;
				avg_time_BPI = avg_time_BPI/nbIters;
				avg_time_BPI = Math.round(avg_time_BPI * 100);
				avg_time_BPI = avg_time_BPI/100;
				
				avg_cost_BPIUS = avg_cost_BPIUS/nbIters;
				avg_time_BPIUS = avg_time_BPIUS/nbIters;
				avg_time_BPIUS = Math.round(avg_time_BPIUS * 100);
				avg_time_BPIUS = avg_time_BPIUS/100;
				
				double nuy1 = Math.round((avg_cost_BPIUS - avg_cost_BPI)*10000/avg_cost_BPI);
				double nuy2 = Math.round((avg_time_BPIUS - avg_time_BPI)*10000/avg_time_BPI);
				
				stdev_BPI = calculateSTDEV(cost_BPI);
				stdev_BPIUS = calculateSTDEV(cost_BPIUS);
				
				try{
					FileOutputStream write = new FileOutputStream(summariseBPIandBPIUSFile, true);
					PrintWriter fo = new PrintWriter(write);
					fo.println("\\textit{N}-" + nbReq[j] + "-" + i + " & " + Math.round(min_cost_BPI/1000) + " & "
						+ Math.round(max_cost_BPI/1000) + " & " + Math.round(avg_cost_BPI/1000) + " & " + Math.round(stdev_BPI/1000) + " & "
						+ avg_time_BPI + " & " + Math.round(min_cost_BPIUS/1000) + " & " + Math.round(max_cost_BPIUS/1000) + " & "
						+ Math.round(avg_cost_BPIUS/1000) + " & " + Math.round(stdev_BPIUS/1000) + " & " + avg_time_BPIUS + " & "
						+ nuy2/100 + " \\\\");
					fo.println("\\hline");
					fo.close();
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}
	}
	
	public double calculateSTDEV(ArrayList<Double> arr){
		double sum = 0;
		for(int i = 0; i < arr.size(); i++)
			sum += arr.get(i);
		double mean = sum/arr.size();
		
        double dv = 0;
        for(int i = 0; i < arr.size(); i++){
            double dm = arr.get(i) - mean;
            dv += dm * dm;
        }
        return Math.sqrt(dv / arr.size());
	}
	
	public void readNewInsertionOperatorsForSummarise(){
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newEfficientOfOperators/summarise-INS-operators.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Iters cost cost cost ...");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		int nbIters = 10;
		String fileName = "random-20reqs-RealLoc-0";
		String dataFileName = dir + "input/" + fileName + ".txt";
		String s = "";
		
		for(int i = 0; i < 8; i++) {		
			s += "x" + i + " = [";
			
			ArrayList<Double> cost_INS = new ArrayList<Double>();
			
			for(int k = 0; k < nbIters; k++){								
				String outputINSfileTxt = dir + "output/newEfficientOfOperators/It-" + k +"-INS-" + i + "-" + fileName + ".txt";
				
				ScannerInput sc = new ScannerInput(outputINSfileTxt);
				
				String str = "";
				while(str != null){
					str = sc.readLine();
					if(str.contains("end time")){
						String[] brLine = str.split(",");
						String[] cost = brLine[3].split("=");
						double cost_int = Double.parseDouble(cost[1]);
						cost_INS.add(cost_int);
						break;
					}	
				}
				sc.close();
			}
			
			for(int k = 0; k < cost_INS.size(); k++){
				if(k != cost_INS.size() - 1)
					s += cost_INS.get(k) + "; ";
				else
					s += cost_INS.get(k) + "];" + "\n";
			}
		}

		try{
			FileOutputStream write = new FileOutputStream(summariseFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(s);
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void readNewRemovalOperatorsForSummarise(){
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newEfficientOfOperators/summarise-RM-operators.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("Iters cost cost cost ...");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		
		int nbIters = 10;
		String fileName = "random-20reqs-RealLoc-0";
		String s = "";
		
		for(int i = 0; i < 8; i++) {
			s += "x" + i + " = [";
			
			ArrayList<Double> cost_REM = new ArrayList<Double>();
			
			for(int k = 0; k < nbIters; k++){								
				String outputREMfileTxt = dir + "output/newEfficientOfOperators/It-" + k +"-REM-" + i + "-" + fileName + ".txt";
				
				ScannerInput sc = new ScannerInput(outputREMfileTxt);
				
				String str = "";
				while(str != null){
					str = sc.readLine();
					if(str.contains("end time")){
						String[] brLine = str.split(",");
						String[] cost = brLine[3].split("=");
						double cost_int = Double.parseDouble(cost[1]);
						cost_REM.add(cost_int);
						break;
					}	
				}
				sc.close();
			}
			
			for(int k = 0; k < cost_REM.size(); k++){
				if(k != cost_REM.size() - 1)
					s += cost_REM.get(k) + "; ";
				else
					s += cost_REM.get(k) + "];" + "\n";
			}
		}

		try{
			FileOutputStream write = new FileOutputStream(summariseFile, true);
			PrintWriter fo = new PrintWriter(write);
			fo.println(s);
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public void readNewOutputForReducingRate_ALNS(){
		int[] nbReq = new int[]{200};//, 70, 100, 150, 200};
		String dir = "data/truck-container/";
		String summariseFile = dir + "output/newOutput/summarise-reducing-rate-" + nbReq[0] + "requests-ALNS.txt";
		
		try{
			FileOutputStream write = new FileOutputStream(summariseFile);
			PrintWriter fo = new PrintWriter(write);
			fo.println("");
			
			fo.close();
		}catch(Exception e){
			System.out.println(e);
		}
		String s = "";
		String lg = "";
		for(int j = 0; j < nbReq.length; j++){
			for(int i = 0; i < 5; i++){
				for(int k = 0; k < 10; k++){
					s += "x" + i + k + " = [";
				
					String fileName = "random-" + nbReq[j] + "reqs-RealLoc-" + i;
					
					String outputALNSfileTxt = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".txt";
					//String outputALNSfileJson = dir + "output/newOutput/It-" + k +"-ALNS-" + fileName + ".json";
					
					ScannerInput sc = new ScannerInput(outputALNSfileTxt);
					
					String str = "";
					boolean is = false;
					ArrayList<Integer> iterList = new ArrayList<Integer>();
					ArrayList<Long> timeList = new ArrayList<Long>();
					ArrayList<Double> costList = new ArrayList<Double>();
					ArrayList<Integer> nbRejectList = new ArrayList<Integer>();
					ArrayList<Integer> nbTruckList = new ArrayList<Integer>();
					
					int bestNbTruck = 1000000;
					double bestCost = 10000000;
					int bestNbReject = 100000;
					long bestTime = -1;
					while(str != null){
						str = sc.readLine();
						if(str.contains("iter"))
							is = true;
						if(str.contains("route"))
							break;
						if(!is)
							continue;
						else{
							if(str.contains("iter"))
								continue;
							str = str.trim().replaceAll("\\s+", " ");
							String[] brLine = str.split(" ");
							iterList.add(Integer.parseInt(brLine[0]));
							timeList.add(Long.parseLong(brLine[3]));
							costList.add(Double.parseDouble(brLine[4]));
							int nbR = Integer.parseInt(brLine[5]);
							nbRejectList.add(nbR);
							if(bestNbReject > nbR)
								bestNbReject = nbR;
							nbTruckList.add(Integer.parseInt(brLine[6]));
						}						
					}
					for(int r = 0; r < nbRejectList.size(); r++){
						if(nbRejectList.get(r) == bestNbReject){
							if(costList.get(r) < bestCost){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
							else if(costList.get(r) == bestCost && nbTruckList.get(r) < bestNbTruck){
								bestCost = costList.get(r);
								bestNbTruck = nbTruckList.get(r);
								bestTime = timeList.get(r);
							}
						}
					}
					
					
					for(int r = 0; r < iterList.size(); r++){
						if(costList.get(r) == bestCost && nbRejectList.get(r) == bestNbReject && nbTruckList.get(r) == bestNbTruck){
							s += iterList.get(r) + " ";
							break;
						}
						s += iterList.get(r) + " ";
					}
					s += "10000];" + "\n";
					
					s += "y" + i + k + " = [";
					
					for(int r = 0; r < iterList.size(); r++){
						if(costList.get(r) == bestCost && nbRejectList.get(r) == bestNbReject && nbTruckList.get(r) == bestNbTruck){
							s += (costList.get(0) - costList.get(r)) * 100 /costList.get(0) + " ";
							break;
						}
						s += (costList.get(0) - costList.get(r)) * 100 /costList.get(0) + " ";
					}
					
					s += (costList.get(0) - bestCost)*100/costList.get(0) + "];" + "\n";
					s += "h" + i + k + " = plot(x" + i + k + ", y" + i + k + ", 'k-o', 'linewidth',2);" + "\n";
					s += "hold on" + "\n";
					lg += "h" + i + k;
				}
			}
		
		
			s += "grid on" + "\n"; 
			s += "set(gca,'FontSize',20)" + "\n";
			s += "xlabel('iterations','fontsize',24);" + "\n";
			s += "ylabel('%','fontsize',24);" + "\n";
			s += "title('N-" + nbReq[j] + "-x','fontsize',24);" + "\n";
			
			try{
				FileOutputStream write = new FileOutputStream(summariseFile, true);
				PrintWriter fo = new PrintWriter(write);
				fo.println(s);
				fo.close();
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}

	public static void main(String[] args){
		DataAnalysis da = new DataAnalysis();
		//da.readHeuristicCostForSummarise_ALNS_HASBPIUS();
		//da.readHeuristicCostandRejectForSummarise_ALNS_HASBPIUS();
		//da.readNewOutputForSummarise_ALNS();
		//da.readNewTuningForSummarise_ALNS();
		//da.readNewBPIForSummarise();
		//da.readNewRemovalOperatorsForSummarise();
		da.readNewOutputForReducingRate_ALNS();
	}
}
