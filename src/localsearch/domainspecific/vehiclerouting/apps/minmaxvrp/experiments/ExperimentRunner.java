package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp.experiments;

import java.io.PrintWriter;

public class ExperimentRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nbRuns = 20;
		
		String[] fn = new String[]{
				"E-n101-k14.vrp",
				"E-n23-k3.vrp",
				"E-n33-k4.vrp",
				"E-n76-k14.vrp",
				"E-n76-k8.vrp",
				"E-n101-k8.vrp",
				"E-n30-k3.vrp",
				"E-n51-k5.vrp",
				"E-n76-k15.vrp",
				"E-n22-k4.vrp",
				"E-n30-k4.vrp",
				"E-n76-k10.vrp",
				"E-n76-k7.vrp",
			"E-n7-k2.vrp",
			"E-n13-k4.vrp",
			"E-n31-k7.vrp"
		};

		String in_dir = "data/MinMaxVRP/Christophides/std-all/";
		String out_dir = "output/MinMaxVRP/";
		int timeLimit = 300;
		
		try{
			//PrintWriter out = new PrintWriter(outFile);
			
			for(int r = 1; r <= nbRuns; r++){
				for(int i = 0; i < fn.length; i++){
					String fi = in_dir + "" + fn[i];
					
					String fo = out_dir + "MinMaxCVRPMultiNeighborhoodsWithTotalCost-ins-" + 
					fn[i] + "-run-" + r + ".txt";
					MinMaxCVRPMultiNeighborhoodsWithTotalCost.run(fi, fo, timeLimit);
					
					
					fo = out_dir + "MinMaxCVRPMultiNeighborhoods-ins-" + 
							fn[i] + "-run-" + r + ".txt";
					MinMaxCVRPMultiNeighborhoods.run(fi, fo, timeLimit);
					
					fo = out_dir + "MinMaxCVRP2Neighborhoods-ins-" + 
									fn[i] + "-run-" + r + ".txt";
					MinMaxCVRP2Neighborhoods.run(fi, fo, timeLimit);
					
					
					fo = out_dir + "MinMaxCVRP2NeighborhoodsWithTotalCost-ins-" + 
							fn[i] + "-run-" + r + ".txt";
					MinMaxCVRP2NeighborhoodsWithTotalCost.run(fi, fo, timeLimit);
			
				}
				
			}
			//out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
