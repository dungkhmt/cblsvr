package localsearch.domainspecific.vehiclerouting.vrp.online.app.osar;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Statistics {

	public ArrayList<Double> getSequence(String fn) {
		System.out.println("getSequence " + fn);
		ArrayList<Double> s = new ArrayList<Double>();
		try {
			Scanner in = new Scanner(new File(fn));
			while (in.hasNext()) {
				int i = in.nextInt();
				double x = in.nextDouble();
				s.add(x);
				// System.out.println(i + "\t" + x + "\t" + s.size());
			}
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return s;
	}

	public void statistic(String dir, int nbPoints) {
		String[] fn = {
				"osar-W20-H20-minDT120-maxDT300-Horizon72000.ins1.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT120-maxDT300-Horizon72000.ins2.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT120-maxDT300-Horizon72000.ins3.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT120-maxDT300-Horizon72000.ins4.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT120-maxDT300-Horizon72000.ins5.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT300-maxDT900-Horizon72000.ins1.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT300-maxDT900-Horizon72000.ins2.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT300-maxDT900-Horizon72000.ins3.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT300-maxDT900-Horizon72000.ins4.txt-costovertime-reoptimize",
				"osar-W20-H20-minDT300-maxDT900-Horizon72000.ins5.txt-costovertime-reoptimize", };

		ArrayList<Double> f_one = new ArrayList<Double>();
		ArrayList<Double> f_false = new ArrayList<Double>();
		ArrayList<Double> f_true = new ArrayList<Double>();
		
		for (int k = 0; k < fn.length; k++) {
			ArrayList<Double> sfalse = getSequence(dir + fn[k] + "-false.txt");
			ArrayList<Double> strue = getSequence(dir + fn[k] + "-true.txt");
			ArrayList<Double> sone = getSequence(dir + fn[k] + "-false-one-one.txt");
			int sz = sfalse.size();
			if(sz < strue.size()) sz = strue.size();
			if(sz < sone.size()) sz = sone.size();
			
			ArrayList<Integer> x = new ArrayList<Integer>();
			while (sfalse.size() < sz) {
				sfalse.add(sfalse.get(sfalse.size() - 1));
			}
			while (strue.size() < sz) {
				strue.add(strue.get(strue.size() - 1));
			}
			while(sone.size() < sz)
				sone.add(sone.get(sone.size()-1));

			f_one.add(sone.get(sone.size()-1));
			f_false.add(sfalse.get(sfalse.size()-1));
			f_true.add(strue.get(strue.size()-1));

			System.out.println(sfalse.size() + ", "
					+ sfalse.get(sfalse.size() - 1));
			System.out.println(strue.size() + ", "
					+ strue.get(strue.size() - 1));
			for (int i = 0; i < strue.size(); i++)
				x.add(i * 10);

			int f = strue.size() / nbPoints;
			ArrayList<Double> r_sfalse = new ArrayList<Double>();
			ArrayList<Double> r_strue = new ArrayList<Double>();
			ArrayList<Double> r_sone = new ArrayList<Double>();
			ArrayList<Integer> r_x = new ArrayList<Integer>();
			int i = 0;
			while (i < strue.size()) {
				if (i % f == 0) {
					r_sfalse.add(sfalse.get(i));
					r_strue.add(strue.get(i));
					r_sone.add(sone.get(i));
					r_x.add(x.get(i));
				}
				i++;
			}
			r_sfalse.add(sfalse.get(sfalse.size() - 1));
			r_strue.add(strue.get(strue.size() - 1));
			r_sone.add(sone.get(sone.size()-1));
			r_x.add(x.get(x.size() - 1));
			try {
				PrintWriter out = new PrintWriter(dir + fn[k] + "-true-false.txt");
				out.println("time \t one-one \t insert-no-improve\t insert-improve");
				for (i = 0; i < r_x.size(); i++)
					out.println(r_x.get(i) + "\t" + r_sone.get(i) + "\t" + r_sfalse.get(i) + "\t"
							+ r_strue.get(i));
				out.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		try{
			PrintWriter compare = new PrintWriter(dir + "compare-3.txt");
			for(int i = 0; i < fn.length; i++){
				compare.println(fn[i] + "\t" + f_one.get(i) + "\t" + f_false.get(i) + "\t" + f_true.get(i));
			}
			compare.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dir = "C:\\DungPQ\\research\\projects\\opencbo\\output\\vrp\\osar\\";

		Statistics T = new Statistics();
		T.statistic(dir, 20);
	}

}
