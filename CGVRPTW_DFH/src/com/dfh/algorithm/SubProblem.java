package com.dfh.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dfh.instance.*;
import java.util.Comparator;

public class SubProblem {
	
	public double reducedCost;
	public Map<Integer,Double> rmpDualValues;
	public VrptwInstance vrptwInstance;
	private Path optPath;

	public SubProblem() {
		
	}
	
	public SubProblem(VrptwInstance vrptwInstance) {
		this.vrptwInstance = vrptwInstance;		
		
	}
	
	public void solve() throws InterruptedException {
		//simpleHeuristic();
		espprcPulseAlgo();
		
		
		
	}
	
	
	public void espprcPulseAlgo() throws InterruptedException {
		ESPPRC espprc = new ESPPRC(vrptwInstance);
		PulseAlgorithm pulseAlgo = new PulseAlgorithm(espprc);
		optPath = pulseAlgo.runPulseAlgo(rmpDualValues);
		reducedCost = optPath.getTravalTime();//借用一个变量转移reducedCost，不用重复计算
		optPath.calcTravalTime(vrptwInstance);
	}
	
	
	public void updateDualValues(Map<Integer,Double> dualValues) {
		this.rmpDualValues = dualValues;
	}
	
	public Path getPath() {
		return this.optPath;
	}
}

