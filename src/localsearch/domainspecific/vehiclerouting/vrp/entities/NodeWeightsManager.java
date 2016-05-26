package localsearch.domainspecific.vehiclerouting.vrp.entities;

import java.util.*;

public class NodeWeightsManager {
	private ArrayList<Point> points;
	private double[] weights;
	private HashMap<Point, Integer> map;
	public NodeWeightsManager(ArrayList<Point> points){
		this.points = points;
		map = new HashMap<Point, Integer>();
		for(int i = 0; i < points.size(); i++)
			map.put(points.get(i), i);
		weights = new double[points.size()];
	}
	public double getWeight(Point p){
		return weights[map.get(p)];
	}
	public void setWeight(Point p, double w){
		weights[map.get(p)] = w;
	}
	public ArrayList<Point> getPoints(){
		return this.points;
	}
	public String name(){
		return "NodeWeightManager";
	}
	public void print(){
		for(int i = 0; i < points.size(); i++){
			System.out.println(name() + "::NodeWeightManager::print, point " + points.get(i).ID);
		}
	}
}
