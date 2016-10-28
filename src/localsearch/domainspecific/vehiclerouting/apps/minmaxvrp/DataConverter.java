package localsearch.domainspecific.vehiclerouting.apps.minmaxvrp;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class DataConverter {
	public void standardizeDataChristophides(){
		String[] fe = new String[]{
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
				"E-n76-k7.vrp"};
		String[] fl = new String[]{
			"E-n7-k2.vrp",
			"E-n13-k4.vrp",
			"E-n31-k7.vrp"
		};
		for(int i = 0; i < fe.length; i++){
			stdEuclide("data/MinMaxVRP/Christophides/std-euclide/" + fe[i],
					"data/MinMaxVRP/Christophides/std-all-round-euclide-distance/" + fe[i]);
		}
		
		for(int i = 0; i < fl.length; i++){
			stdLowDiagnal("data/MinMaxVRP/Christophides/std-lower-diag/" + fl[i],
					"data/MinMaxVRP/Christophides/std-all-round-euclide-distance/" + fl[i]);
		}
		

	}
	public void stdEuclide(String fni, String fno){
		try{
			Scanner in = new Scanner(new File(fni));
			PrintWriter out = new PrintWriter(fno);
			
			int nbVehicles = in.nextInt();
			int nbPoints = in.nextInt();
			int depot = in.nextInt();
			int capacity = in.nextInt();
			int[] demand = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				demand[id] = in.nextInt();
			}
			int[] x = new int[nbPoints+1];
			int[] y = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				x[id] = in.nextInt();
				y[id] = in.nextInt();
			}
			in.close();
			
			double[][] c = new double[nbPoints+1][nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				for(int j = 1; j <= nbPoints; j++){
					c[i][j] = Math.sqrt((x[i]-x[j])^2 + (y[i]-y[j])^2);
					double d = (x[i] - x[j])*(x[i] - x[j]) + (y[i] - y[j])*(y[i] - y[j]);
					double d2 = Math.sqrt(d);
					//c[i][j] = d2;
					c[i][j] = (int)Math.round(d2);
					
					//c[i][j] = Math.abs(x[i] - x[j]) + Math.abs(y[i] - y[j]);// manhatan distance
					//c[i][j] = (int)Math.round(c[i][j]);
					
					/*
					if(i == 1 && j == 4){
						System.out.println("c[" + i + "," + j + "] = " + c[i][j] + ", x[" +i + "] = " + 
					x[i] + ", x[" + j + "] = " + x[j] + ", y[" + i + "] = " + y[i] + ", y[" + j + "] = " + 
								y[j] + ", d = " + d + ", d2 = " + d2);
					}
					*/
				}
			}
			out.println(nbVehicles + " " + nbPoints);
			out.println(depot + " " + capacity);
			for(int i = 1; i <= nbPoints; i++)
				out.println(i + " " + demand[i]);
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					out.println(i + " " + j + " " + c[i][j]);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void stdLowDiagnal(String fni, String fno){
		try{
			Scanner in = new Scanner(new File(fni));
			PrintWriter out = new PrintWriter(fno);
			
			int nbVehicles = in.nextInt();
			int nbPoints = in.nextInt();
			int depot = in.nextInt();
			int capacity = in.nextInt();
			int[] demand = new int[nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				int id = in.nextInt();
				demand[id] = in.nextInt();
			}
			
			
			
			double[][] c = new double[nbPoints+1][nbPoints+1];
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					c[i][j] = 0;
			
			int I = 2;
			int J = 1;
			for(int k = 1; k <= nbPoints*(nbPoints-1)/2; k++){
				c[I][J] = in.nextInt();
				if(I == J+1){
					I++;
					J = 1;
				}else{
					J++;
				}
			}
			for(int i = 1; i < nbPoints; i++){
				for(int j = i+1; j <= nbPoints; j++){
					c[i][j] = c[j][i];
				}
			}
			out.println(nbVehicles + " " + nbPoints);
			out.println(depot + " " + capacity);
			for(int i = 1; i <= nbPoints; i++)
				out.println(i + " " + demand[i]);
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					out.println(i + " " + j + " " + c[i][j]);
			out.close();
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void convertKelly(String fni, String fno, int nbVehicles){
		try{
			Scanner in = new Scanner(new File(fni));
			PrintWriter out = new PrintWriter(fno);
			
			int nbClients = in.nextInt();
			int depot = nbClients + 1;
			int capacity = in.nextInt();
			int tmp1 = in.nextInt();
			int tmp2 = in.nextInt();
			double tmp3 = in.nextDouble();
			double x_depot = in.nextDouble();
			double y_depot = in.nextDouble();
			int demand_depot = in.nextInt();
			int nbPoints = nbClients + 1;
			
			int[] demand = new int[nbPoints+1];
			double[] x = new double[nbPoints+1];
			double[] y = new double[nbPoints+1];

			for(int i = 1; i <= nbClients; i++){
				x[i] = in.nextDouble();
				y[i] = in.nextDouble();
				System.out.println(i + ", fni =  " + fni + "\t  " + x[i] + " " + y[i]);
				demand[i] = in.nextInt();
			}
			
			// depot
			x[depot] = x_depot;
			y[depot] = y_depot;
			demand[depot] = demand_depot;
			
			in.close();
			
			double[][] c = new double[nbPoints+1][nbPoints+1];
			for(int i = 1; i <= nbPoints; i++){
				for(int j = 1; j <= nbPoints; j++){
					c[i][j] = Math.sqrt((x[i]-x[j])*(x[i]-x[j]) + (y[i]-y[j])*(y[i]-y[j]));
				}
			}
			
			// write to file
			out.println(nbVehicles + " " + nbPoints);
			out.println(depot + " " + capacity);
			for(int i = 1; i <= nbPoints; i++)
				out.println(i + " " + demand[i]);
			for(int i = 1; i <= nbPoints; i++)
				for(int j = 1; j <= nbPoints; j++)
					out.println(i + " " + j + " " + c[i][j]);
			out.close();
			
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(-1);
		}
	}
	public void standardizeDataKelly(){
		String[] fn = {
			"kelly01.txt",
			"kelly02.txt",
			"kelly03.txt",
			"kelly04.txt",
			"kelly05.txt",
			"kelly06.txt",
			"kelly07.txt",
			"kelly08.txt",
			"kelly09.txt",
			"kelly10.txt",
			"kelly11.txt",
			"kelly12.txt",
			"kelly13.txt",
			"kelly14.txt",
			"kelly15.txt",
			"kelly16.txt",
			"kelly17.txt",
			"kelly18.txt",
			"kelly19.txt",
			"kelly20.txt",
		};
		
		int[] nbVehicles = new int[]{
			10,
			10,
			10,
			12,
			5,
			8,
			9,
			11,
			14,
			16,
			18,
			19,
			27,
			30,
			34,
			38,
			22,
			28,
			33,
			41,
		};
		
		String dir = "data/MinMaxVRP/Kelly/";
		
		for(int i = 0; i < fn.length; i++){
			String fni = dir + "/original/" + fn[i];
			String fno = dir + "/std_all/" + fn[i];
			convertKelly(fni, fno, nbVehicles[i]);
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataConverter DC = new DataConverter();
		//DC.standardizeDataChristophides();
		DC.standardizeDataKelly();
	}

}
