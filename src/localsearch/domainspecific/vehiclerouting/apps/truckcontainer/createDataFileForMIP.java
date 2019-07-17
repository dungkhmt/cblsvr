package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class createDataFileForMIP {
	public int nTrucks;
	public ArrayList<Integer> depotTrucks;
	public ArrayList<Integer> terminalTrucks;
	public int nMoocs;
	public ArrayList<Integer> depotMoocs;
	public ArrayList<Integer> terminalMoocs;
	public ArrayList<Integer> depotContainers;
	public ArrayList<Integer> terminalContainers;
	public int nContainers;
	public ArrayList<Integer> intermediateTrucks;
	public ArrayList<Integer> intermediateMoocs;
	public ArrayList<Integer> intermediateContainers;
	public int nReturnedContainers;
	public ArrayList<Integer> returnDepotContainers;
	public ArrayList<Integer> whEE;
	public ArrayList<Integer> whIE;
	public ArrayList<Integer> whEL;
	public ArrayList<Integer> portEL;
	public ArrayList<Integer> whIL;
	public ArrayList<Integer> portIL;
	public int nEE;
	public int nEL;
	public int nIE;
	public int nIL;
	public int nLogicalPoints;
	public HashMap<Integer, Integer> ear;
	public HashMap<Integer, Integer> late;
	public HashMap<Integer, Integer> ser;
	public int[][] matrix;
	
	public int pickTruckTime;
	public int pickMoocTime;
	public int pickContTime;
	public int delTruckTime;
	public int delMoocTime;
	public int delContTime;
	
	public void init(){
		Random r = new Random();
		pickTruckTime = 0;
		pickMoocTime = 2;
		pickContTime = 3;
		delTruckTime = 0;
		delMoocTime = 2;
		delContTime = 3;
		nEE = 2;
		nEL = 1;
		nIE = 1;
		nIL = 2;
		nTrucks = (nEE + nEL + nIE + nIL)/3;
		nMoocs = nTrucks;
		nContainers = nEE;// + r.nextInt(3);
		nReturnedContainers = nIE;// + r.nextInt(3);
		nLogicalPoints = nEE + nContainers*2 + nEL*2 + nIE + nReturnedContainers + nIL*2
				+ nTrucks*2 + nMoocs*2;
		matrix = new int[nLogicalPoints][nLogicalPoints];
		for(int i = 0; i < nLogicalPoints; i++)
			for(int j = 0; j < nLogicalPoints; j++)
				matrix[i][j] = -1;
		
		
		intermediateTrucks = new ArrayList<Integer>();
		intermediateMoocs = new ArrayList<Integer>();
		intermediateContainers = new ArrayList<Integer>();
		ear = new HashMap<Integer, Integer>();
		late = new HashMap<Integer, Integer>();
		ser = new HashMap<Integer, Integer>();
		
		depotTrucks = new ArrayList<Integer>();
		terminalTrucks = new ArrayList<Integer>();
		int id = -1;
		for(int i = 0; i < nTrucks; i++){
			id++;
			depotTrucks.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, pickTruckTime);
			id++;
			terminalTrucks.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, delTruckTime);
		}
		depotMoocs = new ArrayList<Integer>();
		terminalMoocs = new ArrayList<Integer>();
		for(int i = 0; i < nMoocs; i++){
			id++;
			depotMoocs.add(id);
			intermediateTrucks.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, pickMoocTime);
			id++;
			terminalMoocs.add(id);
			intermediateTrucks.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, delMoocTime);
		}
		depotContainers = new ArrayList<Integer>();
		terminalContainers = new ArrayList<Integer>();
		for(int i = 0; i < nContainers; i++){
			id++;
			depotContainers.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, pickContTime);
			id++;
			terminalContainers.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, delContTime);
//			intermediateTrucks.add(id);
//			intermediateMoocs.add(id);
		}
		returnDepotContainers = new ArrayList<Integer>();
		for(int i = 0; i < nReturnedContainers; i++){
			id++;
			returnDepotContainers.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			ear.put(id, 0);
			late.put(id, 10000);
			ser.put(id, delContTime);
		}
		whEE = new ArrayList<Integer>();
		for(int i = 0; i < nEE; i++){
			id++;
			whEE.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			int truck2mooc = r.nextInt(20) + 5;
			int mooc2cont = r.nextInt(20) + 5;
			int cont2wh = r.nextInt(20) + 5;
			matrix[depotTrucks.get(i%2)][depotMoocs.get(i%2)] = truck2mooc;
			matrix[depotMoocs.get(i%2)][depotContainers.get(i)] = mooc2cont;
			matrix[depotContainers.get(i)][id] = cont2wh;
			int e = truck2mooc + ser.get(depotTrucks.get(i%2))
					+ mooc2cont + ser.get(depotMoocs.get(i%2))
					+ cont2wh + ser.get(depotContainers.get(i))
					- r.nextInt(10);
			int l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, delContTime);
		}
		whIE = new ArrayList<Integer>();
		for(int i = 0; i < nIE; i++){
			id++;
			whIE.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			int truck2mooc = r.nextInt(20) + 5;
			int mooc2wh = r.nextInt(20) + 5;
			int wh2cont = r.nextInt(20) + 5;
			int idx = (i + nEE)%2;
			matrix[depotTrucks.get(idx)][depotMoocs.get(idx)] = truck2mooc;
			matrix[depotMoocs.get(idx)][id] = mooc2wh;
			matrix[id][returnDepotContainers.get(i)] = wh2cont;
			int e = truck2mooc + ser.get(depotTrucks.get((i + nEE)%2))
					+ mooc2wh + ser.get(depotMoocs.get((i + nEE)%2))
					+ wh2cont + ser.get(returnDepotContainers.get(i))
					- r.nextInt(10);
			int l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, pickContTime);
		}
		whEL = new ArrayList<Integer>();
		portEL = new ArrayList<Integer>();
		for(int i = 0; i < nEL; i++){
			id++;
			whEL.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			int truck2mooc = r.nextInt(20) + 5;
			int mooc2wh = r.nextInt(20) + 5;
			int idx = (i + nEE + nIE)%2;
			matrix[depotTrucks.get(idx)][depotMoocs.get(idx)] = truck2mooc;
			matrix[depotMoocs.get(idx)][id] = mooc2wh;
			int e = truck2mooc + ser.get(depotTrucks.get(idx))
					+ mooc2wh + ser.get(depotMoocs.get(idx))
					- r.nextInt(10);
			int l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, pickContTime);
			
			id++;
			portEL.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			
			int wh2port = r.nextInt(20) + 5;
			matrix[id - 1][id] = wh2port;
			e = e + wh2port
					- r.nextInt(10);
			l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, delContTime);
		}
		whIL = new ArrayList<Integer>();
		portIL = new ArrayList<Integer>();
		for(int i = 0; i < nIL; i++){
			id++;
			portIL.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			int truck2mooc = r.nextInt(20) + 5;
			int mooc2port = r.nextInt(20) + 5;
			int idx = (i + nEE + nIE + nEL)%2;
			matrix[depotTrucks.get(idx)][depotMoocs.get(idx)] = truck2mooc;
			matrix[depotMoocs.get(idx)][id] = mooc2port;
			int e = truck2mooc + ser.get(depotTrucks.get(idx))
					+ mooc2port + ser.get(depotMoocs.get(idx))
					- r.nextInt(10);
			int l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, pickContTime);
			id++;
			whIL.add(id);
			intermediateTrucks.add(id);
			intermediateMoocs.add(id);
			intermediateContainers.add(id);
			int port2wh = r.nextInt(20) + 5;
			matrix[id - 1][id] = port2wh;
			e = e + port2wh
					- r.nextInt(10);
			l = e + r.nextInt(40) + 150;
			ear.put(id, e);
			late.put(id, l);
			ser.put(id, delContTime);
		}
		
		for(int i = 0; i < nLogicalPoints; i++){
			for(int j = 0; j < nLogicalPoints; j++){
				if(i == j)
					matrix[i][j] = 0;
				if(matrix[i][j] == -1){
					if(matrix[j][i] != -1)
						matrix[i][j] = matrix[j][i];
					else{
						int d = r.nextInt(20) + 5;
						matrix[i][j] = d;
						matrix[j][i] = d;
					}
				}
			}
		}
	}
	
	public void print2JsonFile(String fileName){
		try{
			Random r = new Random();
			PrintWriter f = new PrintWriter(new File(fileName));
			f.println("#nb trucks");
			f.println(nTrucks);
			f.println("#intermediate truck points");
			String str = "";
			for(int i = 0; i < intermediateTrucks.size(); i++)
				str += intermediateTrucks.get(i) + " ";
			f.println(str);
			f.println("#truck (depot,  terminus)");
			for(int i = 0; i < nTrucks; i++)
				f.println(depotTrucks.get(i) + " " + terminalTrucks.get(i));
			
			f.println("#nb trailers");
			f.println(nMoocs);
			f.println("#intermediate trailer points");
			str = "";
			for(int i = 0; i < intermediateMoocs.size(); i++)
				str += intermediateMoocs.get(i) + " ";
			f.println(str);
			f.println("#trailer (depot,  terminus)");
			for(int i = 0; i < nMoocs; i++)
				f.println(depotMoocs.get(i) + " " + terminalMoocs.get(i));
			
			f.println("#nb containers");
			f.println(nContainers);
			f.println("#intermediate Container points");
			str = "";
			for(int i = 0; i < intermediateContainers.size(); i++)
				str += intermediateContainers.get(i) + " ";
			f.println(str);
			f.println("#Container (depot,  terminus)");
			for(int i = 0; i < nContainers; i++)
				f.println(depotContainers.get(i) + " " + terminalContainers.get(i));
			
			f.println("#nb returned container depots");
			f.println(nReturnedContainers);
			f.println("#return-depot container");
			str = "";
			for(int i = 0; i < nReturnedContainers; i++)
				str += returnDepotContainers.get(i) + " ";
			f.println(str);
			
			f.println("#export empty");
			f.println(nEE);
			for(int i = 0; i < nEE; i++)
				f.println(whEE.get(i) + " " + r.nextInt(1));
			
			f.println("#import empty");
			f.println(nIE);
			for(int i = 0; i < nIE; i++)
				f.println(whIE.get(i));
			
			f.println("#export laden");
			f.println(nEL);
			for(int i = 0; i < nEL; i++)
				f.println(whEL.get(i) + " " + portEL.get(i) + " " + r.nextInt(1));
			
			f.println("#import laden");
			f.println(nIL);
			for(int i = 0; i < nIL; i++)
				f.println(portIL.get(i) + " " + whIL.get(i) + " " + r.nextInt(1));
			
			f.println("#nb Logical points");
			f.println(nLogicalPoints);
			
			f.println("#time window");
			for(int i = 0; i < nLogicalPoints; i++)
				f.println(i + " " + ear.get(i) + " " + late.get(i) + " " + ser.get(i));
			
			f.println("#distances");
			f.println(nLogicalPoints * (nLogicalPoints-1));
			for(int i = 0; i < nLogicalPoints; i++)
				for(int j = 0; j < nLogicalPoints; j++){
					if(i == j)
						continue;
					f.println(i + " " + j + " " + matrix[i][j]);
				}
			f.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void main(String args[]){
		createDataFileForMIP c = new createDataFileForMIP();
		c.init();
		c.print2JsonFile("data/truck-container/" + c.nEE + "EE-" + c.nIL + "IL-" + c.nIE + "IE-" + c.nEL + "EL-artificial.txt");
	}
	
}

