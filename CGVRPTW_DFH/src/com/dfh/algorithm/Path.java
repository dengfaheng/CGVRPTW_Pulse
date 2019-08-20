package com.dfh.algorithm;

import java.util.ArrayList;

import com.dfh.instance.Customer;
import com.dfh.instance.VrptwInstance;

import ilog.concert.IloNumVar;

public class Path {
	private ArrayList<Integer> path;
	private int id;
	private double travalTime;
	public IloNumVar y;
	
	public Path() {
		path       = new ArrayList<Integer>();
		id         = 0;
		travalTime = 0;
	}
	
	public Path(Path p) {
		path       = new ArrayList<Integer>();
		id         = p.getId();
		travalTime = p.getTravalTime();
		y          = p.y;
		path.addAll(p.getPath());
	}

	public int a(int customer) {
		return path.contains(customer)? 1 : 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getTravalTime() {
		return travalTime;
	}

	public void setTravalTime(double travalTime) {
		this.travalTime = travalTime;
	}

	public ArrayList<Integer> getPath() {
		return path;
	}

	public void setPath(ArrayList<Integer> path) {
		this.path = path;
	}

	public void calcTravalTime(VrptwInstance vrptwInstance) {
    	// do the math only if the route is not empty
		this.travalTime = 0;
		if(path.size() > 2){
			for(int i = 0; i < path.size()-1; i++) {
				this.travalTime+=vrptwInstance.getTravelTime(path.get(i), path.get(i+1));
			}
			//this.travalTime += vrptwInstance.getTravelTime(i, i+1)
		}
	}
	
	
	/**
	 * Prints the path
	 */
	public String toString() {
		if(path.size() <= 2) {
			return "";
		}
		StringBuffer print = new StringBuffer();
		
		print.append("Path[" + String.format("%-3d", id) + ", " +String.format("%-3d", path.size()-2) + "] = ");
		for (int i = 0; i < path.size()-1; ++i) {
			print.append(String.format("%-3d", path.get(i))+" -> ");
		}
		print.append(String.format("%-3d",path.get(path.size()-1)));
		print.append("\n");
		return print.toString();
	}

}
