package localsearch.domainspecific.vehiclerouting.algorithms.tsp;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class BranchAndBoundTSP {
	private boolean VERBOSE = false;
	private int N;
	private double[][] c;
	private int[] x;
	private double f;
	private double f_best;
	private int[] x_best;
	private boolean[] visited;
	private double cmin;
	private int maxTimeMiliseconds;
	private double t0;
	private boolean sureOptimal = false;
	
	private void solution(){
		if(f + c[x[N-1]][x[0]] < f_best){
			f_best = f + c[x[N-1]][x[0]];
			for(int i = 0; i < N; i++)
				x_best[i] = x[i];
			if(VERBOSE)
			System.out.println("update best " + f_best + ", time " + (System.currentTimeMillis() - t0)*0.001);
		}
	}
	public String name(){
		return "BranchAndBoundTSP";
	}
	private int[] getSortedCandidates(int k){
		ArrayList<Integer> cand = new ArrayList<Integer>();
		for(int v = 0; v < N; v++) if(!visited[v]) cand.add(v);
		int[] a = new int[cand.size()];
		for(int i = 0; i < cand.size(); i++) a[i] = cand.get(i);
		for(int i = 0; i < a.length-1; i++)
			for(int j = i+1; j < a.length; j++)
				if(c[x[k-1]][a[i]] > c[x[k-1]][a[j]]){
					int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
				}
		return a;
	}
	private void TRY(int k){
		if(System.currentTimeMillis() - t0 > maxTimeMiliseconds){
			System.out.println(name() + "solve, time expired!!");
			sureOptimal = false;
			return;
		}
		int[] cand = getSortedCandidates(k);
		for(int j = 0; j < cand.length; j++){
			int v = cand[j];
			if(!visited[v]){
				x[k] = v;
				visited[v] = true;
				f += c[x[k-1]][x[k]];
				if(k == N-1){
					solution();
				}else{
					double g = cmin*(N-k) + f;
					if(g < f_best) TRY(k+1);
				}
				f -= c[x[k-1]][x[k]];
				visited[v] = false;
			}
		}
	}
	private void computeCMIN(){
		cmin = Integer.MAX_VALUE;
		for(int i = 0; i < N; i++)
			for(int j = 0; j < N; j++)
				if(i != j && cmin > c[i][j]) cmin = c[i][j];
	}
	public double getBestObjective(){
		return f_best;
	}
	public void setData(double[][] c){
		this.c = c;
		this.N = c.length;
		
	}
	public int[] solve(int maxTimeMiliseconds){
		this.maxTimeMiliseconds = maxTimeMiliseconds;
		t0 = System.currentTimeMillis();
		computeCMIN();
		x = new int[N];
		x_best = new int[N];
		visited = new boolean[N];
		for(int i = 0; i < N; i++) visited[i] = false;
		
		f = 0;
		f_best = Integer.MAX_VALUE;
		x[0] = 0;
		visited[0] = true;
		sureOptimal = true;
		TRY(1);
		if(VERBOSE)
		System.out.println(name() + "::solve finished, time = " + (System.currentTimeMillis() - t0)*0.001);
		
		return x_best;
	}
	public boolean sureOptimal(){
		return sureOptimal;
	}
	public void loadDataFromFile(String filename){
		try{
			Scanner in = new Scanner(new File(filename));
			N = in.nextInt();
			c = new double[N][N];
			for(int i = 0; i < N; i++)
				for(int j = 0; j < N; j++)
					c[i][j] = in.nextDouble();
			in.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void setVerbose(boolean VERBOSE){
		this.VERBOSE = VERBOSE;
	}
	public void genData(String filename, int N, int maxW){
		try{
			Random R = new Random();
			PrintWriter out = new PrintWriter(filename);
			out.println(N);
			for(int i = 0; i < N; i++){
				for(int j = 0; j < N; j++){
					int w = R.nextInt(maxW) + 1;
					if(i == j) w = 0;
					out.print(w + " ");
				}
				out.println();
			}
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] c = {
				{0,1,3,2,4},
				{5,0,5,6,3},
				{1,2,0,6,4},
				{1,2,3,9,0},
				{5,3,2,9,0}
		};
		BranchAndBoundTSP solver = new BranchAndBoundTSP();
		//solver.genData("data/tsp/tsp-20.inp", 20, 30);
		solver.loadDataFromFile("data/tsp/tsp-20.inp");
		//solver.setData(c);
		solver.setVerbose(true);
		int[] s = solver.solve(100000);
		for(int i = 0; i < s.length; i++)
			System.out.print(s[i] + " ");
		
	}

}
