package com.dfh.parameters;

import com.dfh.algorithm.*;

import java.util.Random;


import ilog.concert.IloException;
import ilog.cplex.IloCplex;


public class Parameters {
	private String inputFileName;
	private String outputFileName;
	private String instanceName;
	private double precision;
	private int iterations;
	private int startClient;
	private int randomSeed;
	private String currDir;
	private Random random;
	private double alpha;
	private double beta;
	
	///////column generation
	public boolean abort;
	public double zero_reduced_cost;
	public double zero_reduced_cost_AbortColGen;

	///////espprc
	public int numThreads;		// Number of threads
	public int boundStep;		// Step size for the bounding procedure

	
	public Parameters() {
		currDir 			= System.getProperty("user.dir");
		//defult output
		outputFileName    	= currDir + "\\output\\solution.csv";
		inputFileName       = "input\\C101.TXT";
		//defult configure
		precision         	= 1E-4;
		iterations        	= 1;
		startClient       	= 0;
		randomSeed		  	= 10086;
		random = new Random(); 
		random.setSeed(randomSeed);
		alpha = 1;
		beta = 1;
		
		abort = false;
		zero_reduced_cost = -0.0001;
		zero_reduced_cost_AbortColGen = -0.005;
		
		numThreads  = 20;
		boundStep   = 4;

	}
	
	public void updateParameters(String[] args)
	{
		if(args.length % 2 == 0){
			for(int i = 0; i < args.length; i += 2){
				switch (args[i]) {
					case "-in":
						inputFileName= args[i+1];
						String []tS1 = args[i+1].split("\\\\");
						String []tS2 = tS1[tS1.length-1].split("\\.");
						instanceName = tS2[0];
						outputFileName = currDir + "\\output\\" + instanceName+"_solution.txt";
						break;
					case "-out":
						outputFileName = args[i+1];
						if(args[i+1].charAt(args[i+1].length()-1) != '\\') {
							outputFileName = outputFileName+"\\"+instanceName+"_solution.txt";
						}
						else {
							outputFileName = outputFileName+instanceName+"_solution.txt";
						}						
						break;
					case "-precision":
						precision = Double.parseDouble(args[i+1]);
						break;
					case "-iteration":
						iterations = Integer.parseInt(args[i+1]);
						break;
					case "-startClient":
						startClient = Integer.parseInt(args[i+1]);
						break;
					case "-randomSeed":
						randomSeed = Integer.parseInt(args[i+1]);
						this.random.setSeed(randomSeed);
						break;
					case "-alpha":
						alpha = Double.parseDouble(args[i+1]);
						break;
					case "-beta":
						beta = Double.parseDouble(args[i+1]);
						break;
					default: {
						System.out.println("Unknown type of argument: " + args[i]);
						System.exit(-1);
					}
				}
			}
		}else {
			System.out.println("Parameters are not in correct format");
			System.exit(-1);
		}
	}
	
	public String toString(){
		StringBuffer print = new StringBuffer();
		print.append("\n" + "--- Parameters: -------------------------------------");
		print.append("\n" + "| Input File Name       = " + inputFileName);
		print.append("\n" + "| Output File Name      = " + outputFileName);
		print.append("\n" + "| Precision             = " + precision);
		print.append("\n" + "| Iterations            = " + iterations);
		print.append("\n" + "| Start Client          = " + startClient);
		print.append("\n" + "| Random Seed           = " + randomSeed);
		print.append("\n" + "| Alpha                 = " + alpha);
		print.append("\n" + "| Beta                  = " + beta);
		print.append("\n" + "------------------------------------------------------");
		return print.toString();	
	}
	
	public void cplexConfigure(RestrictedMasterProblem masterproblem) {
		try {
			// branch and bound
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Strategy.NodeSelect, 1);
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Strategy.Branch,1);
			//masterproblem.cplex.setParam(IloCplex.Param.Preprocessing.Presolve, true);
			// display options
			masterproblem.cplex.setParam(IloCplex.Param.MIP.Display, 2);
			masterproblem.cplex.setParam(IloCplex.Param.Tune.Display, 1);
			masterproblem.cplex.setParam(IloCplex.Param.Simplex.Display, 0);
		}catch (IloException e) {
			System.err.println("Concert exception caught: " + e);
		}
	}



	/**
	 * @return the inputFileName
	 */
	public String getInputFileName() {
		return inputFileName;
	}
	
	/**
	 * @return the instanceName
	 */
	public String getInstanceName() {
		return instanceName;
	}

	/**
	 * @param inputFileName the inputFileName to set
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * @return the outputFileName
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * @param outputFileName the outputFileName to set
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	/**
	 * @return the iterations
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations the iterations to set
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	/**
	 * @return the startClient
	 */
	public int getStartClient() {
		return startClient;
	}

	/**
	 * @return the randomSeed
	 */
	public int getRandomSeed() {
		return randomSeed;
	}

	/**
	 * @param randomSeed the randomSeed to set
	 */
	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	/**
	 * @param startClient the startClient to set
	 */
	public void setStartClient(int startClient) {
		this.startClient = startClient;
	}

	public double getPrecision() {
		return precision;
	}

	public String getCurrDir() {
		return currDir;
	}

	public void setCurrDir(String currDir) {
		this.currDir = currDir;
	}
	
	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * @param random the random to set
	 */
	public void setRandom(Random random) {
		this.random = random;
	}
	
	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}
	
	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	
}
