package com.dfh.main;

import com.dfh.algorithm.*;

import com.dfh.timer.*;
import com.dfh.instance.VrptwInstance;
import com.dfh.parameters.Parameters;

public class CGMainRun {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		Parameters parm = new Parameters();
		parm.updateParameters(args);

		VrptwInstance vrptwIns = new VrptwInstance(parm);
		vrptwIns.readInstanceFromFile(parm.getInputFileName());
		
		System.out.println("<<<<<<<<<<< Column Generation For VRPTW, instance : "+parm.getInstanceName() +" >>>>>>>>>>>");
		System.out.println(parm.toString());

		ColumnGeneration colGenMain = new ColumnGeneration(vrptwIns);
		
		double st = System.currentTimeMillis();
		colGenMain.runColumnGeneration();
		double et = System.currentTimeMillis();
		colGenMain.rstMasterProblem.writeSolution("output\\solution.csv", (et-st)/1000);
		
		System.out.println("Time >>> "+(et-st)/1000 + " s");

	}

}
