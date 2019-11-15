package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.google.gson.Gson;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.TruckMoocContainerOutputJson;
import localsearch.domainspecific.vehiclerouting.vrp.utils.DateTimeUtils;

public class TestAPI {
	public TestAPI(){
		
	}
	
	public static void main(String[] args){
		
//		String jsonInFileName = "E:/Project/cblsvr/truck-container/cblsvr/data/truck-container/" + "newreqs.json";
//		String outputfile = "E:/Project/cblsvr/truck-container/cblsvr/data/truck-container/" + "-result.json";
		String jsonInFileName = "input.json";//args[0] + ".json";
		String outputfile = "result.json";//args[0] + "-result.json";
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("--input"))
				jsonInFileName = args[i+1];
			else if(args[i].equals("--output"))
				outputfile = args[i+1];
		}
		System.out.println("input file = " + jsonInFileName + ", output file = " + outputfile);
		
		ContainerTruckMoocInput input = null;
		try{
			Gson g = new Gson();
			BufferedReader in = new BufferedReader(new FileReader(jsonInFileName));
			input = g.fromJson(in, ContainerTruckMoocInput.class);
			TruckMoocContainerOutputJson solution;
			
			TruckContainerSolver solver = new TruckContainerSolver();
			solver.readData(jsonInFileName);
			solver.init();
			solver.stateModel();
			
			double t = System.currentTimeMillis();
			try{
				FileOutputStream write = new FileOutputStream(outputfile);
				PrintWriter fo = new PrintWriter(write);
				fo.println("Starting time = " + DateTimeUtils.unixTimeStamp2DateTime(System.currentTimeMillis()) 
						+ ", total reqs = " + solver.nRequest
						+ ", total truck = " + solver.nVehicle);
				
				fo.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			String initType = "firstPossibleInitFPI";
			
			switch(initType){
				//case "greedyInit" : solver.greedyInitSolution2(); break;
				case "greedyInitWithAcceptanceBPI": solver.greedyInitSolutionWithAcceptanceBPI(); break;
				case "firstPossibleInitFPI": solver.firstPossibleInitFPI(); break;
				case "firstPossibleInitFPIUS": solver.firstPossibleInitFPIUS(); break;
				case "bestPossibleInitBPIUS": solver.bestPossibleInitBPIUS(); break;
				case "heuristicFPIUS": solver.heuristicFPIUS(outputfile); break;
				case "heuristicBPIUS": solver.heuristicBPIUS(outputfile); break;
				case "oneRequest2oneRoute": solver.insertOneReq2oneTruck(); break;
			}			
	
			solver.timeLimit = 1800000;
			solver.nIter = 10000;
			
			solver.nRemovalOperators = 8;
			solver.nInsertionOperators = 8;
			
			solver.lower_removal = (int) 0.05*(solver.nRequest)/100;
			solver.upper_removal = (int) 0.2*(solver.nRequest)/100;
			solver.sigma1 = 3;
			solver.sigma2 = 1;
			solver.sigma3 = 3;
			
			solver.rp = 0.1;
			solver.nw = 1;
			solver.shaw1st = 0.5;
			solver.shaw2nd = 0.2;
			solver.shaw3rd = 0.1;
	
			solver.temperature = 200;
			solver.cooling_rate = 0.9995;
			solver.nTabu = 5;
	
			solver.initParamsForALNS();
			solver.adaptiveSearchOperators(outputfile);
			solver.printSolution(outputfile, t);
			solution = solver.createFormatedSolution();
			
			String out = g.toJson(solution);
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputfile));
		    writer.write(out);
		     
		    writer.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
}
