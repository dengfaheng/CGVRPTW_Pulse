package com.dfh.algorithm;

import com.dfh.instance.*;

import ilog.concert.*;
import ilog.cplex.*;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RestrictedMasterProblem {
	public IloCplex cplex;
	private IloObjective rmpObjective;
	private Map<Integer,IloRange> rowCustomersConstrains;
	private Map<Integer,Double> dualValues;
	private List<IloConversion> mipConversion;
	public VrptwInstance vrptwInstance;
	public double objectiveValue;
	private List<Path> paths;
	private double totalTravelTime;

	
	public RestrictedMasterProblem() {
		
	}
	
	public RestrictedMasterProblem(VrptwInstance vrpIns) {
		this.vrptwInstance =vrpIns;
		paths = new ArrayList<Path>();
		rowCustomersConstrains = new HashMap<Integer,IloRange>();
		dualValues = new HashMap<Integer,Double>(); //对偶变量
		mipConversion = new ArrayList<IloConversion>();
		totalTravelTime = 0;
		bulidRMPModel();
	}
	
	public void bulidRMPModel() {
		createModel();
		createDefaultRoutes();
		this.vrptwInstance.parameters.cplexConfigure(this);
	}
	
	////TODO: nothing need to do here
	
	public void createDefaultRoutes() {
		for (Customer customer : vrptwInstance.getCustomers()) {			
			Path newPath = new Path();
			newPath.setId(paths.size()+1);
			//初始单独为每个客户开一条路径
			newPath.getPath().add(vrptwInstance.getDepot().getNumber());
			newPath.getPath().add(customer.getNumber());
			newPath.getPath().add(vrptwInstance.getDepot().getNumber());
			newPath.calcTravalTime(vrptwInstance);
			
			addNewColumn(newPath);
			paths.add(newPath);
		}
		
	}
	public void createModel() {
		try{
			cplex = new IloCplex();
			rmpObjective = cplex.addMinimize();
			
			for (Customer customer : vrptwInstance.getCustomers()) {
				rowCustomersConstrains.put(customer.getNumber(), cplex.addRange(1, 1, "cust "+customer.getNumber()));
			}
			
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	public void addNewColumn(Path path) {
		try {
			IloColumn new_column = cplex.column(rmpObjective, path.getTravalTime());
			
			for (Customer customer : vrptwInstance.getCustomers()) {
				new_column = new_column.and(cplex.column(rowCustomersConstrains.get(customer.getNumber()),path.a(customer.getNumber())));
			}
			path.y = cplex.numVar(new_column, 0, 1, "y."+path.getId());
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	public void solveRelaxation() {
		try {
			//System.out.println(cplex.getModel());
			if (cplex.solve()) {
				saveDualValues();
				objectiveValue = cplex.getObjValue();
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	public void saveDualValues() {
		try {			
			for (Customer customer : vrptwInstance.getCustomers()) {
				dualValues.put(customer.getNumber(), cplex.getDual(rowCustomersConstrains.get(customer.getNumber())));
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	public void solveMIP() {
		try {
			convertToMIP();
			if (cplex.solve()) {
				displaySolution();
				//logger.writeLog(instance, cplex.getObjValue(), cplex.getBestObjValue());
			}
			else {
				System.out.println("Integer solution not found");
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	public void convertToMIP() {
		try {
			for (Path path : paths) {
				mipConversion.add(cplex.conversion(path.y, IloNumVarType.Bool)) ;
				cplex.add(mipConversion.get(mipConversion.size()-1));
			}
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	
	
	public Map<Integer,Double> getDualValues() {
		return this.dualValues;
	}
	
	public void displaySolution() {
		try {
			totalTravelTime = 0;
			System.out.println("\n" + "--- Solution >>> ------------------------------");
			for (Path path : paths) {
				if (cplex.getValue(path.y)>0.99999) {
					totalTravelTime += path.getTravalTime();
					System.out.print(path.toString());
				}
			}
			System.out.println("\n"+"Total TravelTime = "+totalTravelTime);
			System.out.println("\n" + "--- Solution <<< ------------------------------");
		}
		catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}
	
	public void writeSolution(String outPutFile, double totTime) {
	    try {    
	        File csv = new File(outPutFile); // CSV数据文件   
	        BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // 附加   
	        // 添加新的数据行   
	        bw.write(this.vrptwInstance.parameters.getInstanceName() + "," + this.totalTravelTime+","+totTime);    
	        bw.newLine();    
	        bw.close();
	      } catch (FileNotFoundException e) {    
	        // File对象的创建过程中的异常捕获   
	        e.printStackTrace();    
	      } catch (IOException e) {    
	        // BufferedWriter在关闭对象捕捉异常   
	        e.printStackTrace();    
	      }
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}
	
}