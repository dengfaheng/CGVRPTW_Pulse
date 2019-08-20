package com.dfh.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.dfh.instance.Customer;
import com.dfh.instance.Route;

public class PulseAlgorithm {
	public ESPPRC espprcIns;
	
	private double primalBound;		// Primal bound updated through the execution of the algorithm
	private double naiveDualBound;	// Naive dual bound 
	private double overallBestCost;	// Overall best cost found at each iteration of the bounding stage
	private double timeIncumbent;		// Time incumbent for the bounding stage
	private int timeStep;
	
	//algo setting
	private int threadNr;
	private Thread[] threads;		// Threads
	
	
	public PulseAlgorithm(){
		
	}
	
	public PulseAlgorithm(ESPPRC espprc){
		espprcIns            = espprc;
		primalBound          = 0;
		naiveDualBound       = Integer.MAX_VALUE;
		overallBestCost      = 0;
		timeStep             = espprc.vrptwInstance.getParameters().boundStep;
		this.threadNr        = espprc.vrptwInstance.getParameters().numThreads;
		this.threads         = new Thread[threadNr];
		
		for(int i = 0; i <threadNr;++i ) {
			this.threads[i] = new Thread();
		}
		
	}
	
	//更新ESPPRC中的cost，该问题求出最终的结果是reduced cost
	public void updateESPPRCCost(Map<Integer,Double> rmpDualValues) {
		for(int cust:rmpDualValues.keySet()) {
			for(ESPEdge ogEdge : espprcIns.getEspNodes().get(cust).outgoingEdges) {
				ogEdge.cost = ogEdge.dist - rmpDualValues.get(cust);
				espprcIns.costs[ogEdge.getStartNode().getNumber()][ogEdge.getEndNode().getNumber()] = ogEdge.cost;
			}
		}
	}
	
	public Path runPulseAlgo(Map<Integer,Double> rmpDualValues) throws InterruptedException {
		//first update
		updateESPPRCCost(rmpDualValues);
		espprcIns.getEspFinalNode().resetFinalSol();
		
		this.timeIncumbent   = espprcIns.vrptwInstance.getDepot().getEndTw();// Capture the depot upper time window
		this.timeIncumbent  += timeStep;
		this.timeIncumbent  -= this.timeIncumbent % timeStep;
		
		//System.out.println("this.timeIncumbent = "+this.timeIncumbent);
		
		//BOUNDING PROCEDURE
		boundingScheme();
		
		// Run pulse
		this.timeIncumbent+=this.timeStep; 				// Set time incumbent to the last value solved
		this.primalBound=0;										// Reset the primal bound
		
		pulseMT(espprcIns.getEspStartNode(), 0, 0, 0, new ArrayList<Integer>(),0,0); 	// Run the pulse procedure on the source node
	
		// Print results
		/*
		System.out.println("************ESPPRC OPTIMAL SOLUTION *****************\n");
		System.out.println("Optimal cost: "+espprcIns.getEspFinalNode().pathCost);
		System.out.println("Optimal time: "+espprcIns.getEspFinalNode().pathTime);
		System.out.println("Optimal Load: "+espprcIns.getEspFinalNode().pathLoad);
		System.out.println();
		System.out.println("Optimal path: ");
		System.out.println(espprcIns.getEspFinalNode().path);
		System.out.println("******************ESPPRC END************************\n");
		*/
		Path optPath = new Path();
		optPath.getPath().addAll(espprcIns.getEspFinalNode().path);
		optPath.setTravalTime(espprcIns.getEspFinalNode().pathCost);

		return optPath;
	}
	
	public void boundingScheme() {
		//////////////////////////////////////////////////BOUNDING PROCEDURE //////////////////////////////////////////////////////////////////////////
		calNaiveDualBound();								    	// Calculate a naive lower bound
		int lowerTimeLimit = 100; 											// Lower time (resource) limit to stop the bounding procedure. For 100-series we used 50 and for 200-series we used 100;
		int timeIndex=0;											// Index to store the bounds
		
		while(timeIncumbent >= lowerTimeLimit){					      // Check the termination condition
			timeIndex=(int) Math.ceil(timeIncumbent/timeStep);		// Calculate the current index
			
			for(ESPNode espno : this.espprcIns.getEspNodes().values()) {
				pulseBound(espno, 0, timeIncumbent, 0 , new ArrayList<Integer>(), espno,0); 	// Solve an ESPPRC for all nodes given the time incumbent 
				espno.boundsMatrix.put(timeIndex, espno.bestCost);
				//System.out.println("boundsMatrix["+espno.getNumber()+"]["+timeIndex+"] = "+espno.bestCost);
			}

			this.overallBestCost = this.primalBound;					// Store the best cost found over all the nodes
			timeIncumbent   -= timeStep;						// Update the time incumbent
		}
		////////////////////////////////////////////////END OF BOUNDING PROCEDURE //////////////////////////////////////////////////////////////////////////		
	}
	
	//This method finds the best arc regarding the cost/time ratio
	public void  calNaiveDualBound() {
		this.naiveDualBound = Integer.MAX_VALUE;
		for(ESPNode espno : this.espprcIns.getEspNodes().values()) {
			for(ESPEdge ogEdge : espno.outgoingEdges) {
				if((ogEdge.time != 0) &&
				   (ogEdge.cost / ogEdge.time <= this.naiveDualBound)) {
					this.naiveDualBound = ogEdge.cost / ogEdge.time;
				}
			}
		}
		//System.out.println("naiveDualBound = "+naiveDualBound);
	}
	
	
	/**
	 * Pulse function for the bounding stage
	 * @param pLoad current path load
	 * @param pTime current path time
	 * @param pCost current path cost
	 * @param path current path
	 * @param root current root node
	 * @param pDist current path distance
	 */
	@SuppressWarnings("unchecked")
	public void pulseBound(ESPNode node, double pLoad, double pTime, double pCost, ArrayList<Integer> path, ESPNode root, double pDist) {

		if(node.firstTime == true){
			node.firstTime=false;
			Collections.sort(node.outgoingEdges);
		}
		
		// If the node is reached before the lower time window wait until the beginning of the time window
		if(pTime<node.getStartTw()){
			pTime=node.getStartTw();
		}

		// Try to prune pulses with the pruning strategies: cycles, infeasibility, bounds, and rollback
		if(!node.visited && 
		    pTime <= node.getEndTw() && 
		    (pCost+calcBoundPhaseI(node, pTime,root)) < root.bestCost && !rollback(node, path,pCost,pTime)){
			// If the pulse is not pruned add it to the path
			node.visited = true;
			path.add(node.getNumber());
			// Propagate the pulse through all the outgoing arcs
			for (ESPEdge espe : node.outgoingEdges) {
				double newPLoad = 0;
				double newPTime = 0;
				double newPCost = 0;
				double newPDist = 0;
				ESPNode arcHead = espe.getEndNode();
				
				// Update all path attributes
				newPTime = (pTime + espe.time);
				newPCost = (pCost + espe.cost);
				newPLoad = (pLoad + espe.load);
				newPDist = (pDist + espe.dist);
				
				// Check feasibility and propagate pulse
				if (newPTime <= arcHead.getEndTw() && 
					newPLoad <= espprcIns.vrptwInstance.getVehiclesCapacity() /*&& 
					//这一行判断感觉非必须 TODO
					newPTime <= espprcIns.vrptwInstance.getDepot().getEndTw()*/) {
				    // If the head of the arc is the final node, pulse the final node	
					//递归出口
					if(arcHead.getNumber() == espprcIns.getEspFinalNode().getNumber()){
						pulseBoundFinal(newPLoad, newPTime, newPCost, path, root ,newPDist);	
					}
					else{
						pulseBound(arcHead, newPLoad, newPTime, newPCost, path, root ,newPDist);
					}
				}

			}
			// Remove the explored node from the path
			path.remove((path.size() - 1));
			node.visited = false;
		}
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////7
	


	/** Multithread pulse function 
	 * @param PLoad current load
	 * @param PTime current time
	 * @param PCost current cost
	 * @param path current partial path
	 * @param PDist current distance
	 * @param thread current thread 
	 * @throws InterruptedException
	 */
	@SuppressWarnings("unchecked")
	public void pulseMT(ESPNode node, double pLoad, double pTime, double pCost, ArrayList<Integer> path, double pDist, int thread) throws InterruptedException {

		// If the node is visited for the first time, sort the outgoing arcs array 
		if(node.firstTime==true){
			node.firstTime=false;
			Collections.sort(node.outgoingEdges);
		}
		
		// If the node is reached before the lower time window wait until the beginning of the time window
		if(pTime < node.getStartTw()){
			pTime = node.getStartTw();
		}
		// Try to prune pulses with the pruning strategies
		if((!node.visitedMT[thread] && 
			(pCost+CalcBoundPhaseII(node, pTime))< primalBound && 
			!rollback(node, path,pCost,pTime))){
			
			//System.out.println("get in prune pulses");
			
			// If the pulse is not pruned add it to the path
			node.visitedMT[thread] = true;
			path.add(node.getNumber());	
			// Propagate the pulse through all the outgoing arcs
			for(ESPEdge espe : node.outgoingEdges){
				double newPLoad = 0;
				double newPTime = 0;
				double newPCost = 0;
				double newPDist = 0;
				ESPNode arcHead = espe.getEndNode();

				// Update all path attributes
				newPTime=(pTime+espe.time);
				newPCost=(pCost+espe.cost);
				newPLoad=(pLoad+espe.load);
				newPDist=(pDist+espe.dist);
				
				//System.out.println("newPTime ="+newPTime+" newPLoad = "+newPLoad);
				
				// Check feasibility and propagate pulse
				if( newPTime <= arcHead.getEndTw() && 
					newPLoad <= espprcIns.getCapacity() /*&& 
					//这一判断感觉也多余 TODO
					newPTime <= espprcIns.getEspFinalNode().getEndTw()*/){
					
					//System.out.println("get in");
					
					// If the head of the arc is the final node, pulse the final node
					//递归出口
					if (arcHead.getNumber() == espprcIns.getEspFinalNode().getNumber()) {
						pulseMTFinal(newPLoad,newPTime,newPCost, path, newPDist,thread);
					}else{
						// If not in the start node continue the exploration on the current thread 
						if(node.getNumber() != espprcIns.getEspStartNode().getNumber()){
							pulseMT(arcHead, newPLoad,newPTime,newPCost, path, newPDist, thread);	
						}
						// If standing in the start node, wait for the next available thread to trigger the exploration
						else {
							boolean stopLooking = false;
							for (int j = 1; j < this.threads.length; j++) {
								if(!this.threads[j].isAlive()){
									this.threads[j] = new Thread(new PulseTask(arcHead, newPLoad, newPTime, newPCost, path, newPDist, j));
									this.threads[j].start();
									stopLooking = true;
									//j = 1000;
									break;
								}
							}
							if (!stopLooking) {
								this.threads[1].join();
								this.threads[1] = new Thread(new PulseTask(arcHead, newPLoad, newPTime, newPCost, path, newPDist, 1));
								this.threads[1].start();
							}
						}
					}
				}
				
			}
			// Wait for all active threads to finish	
			if(node.getNumber() == espprcIns.getEspStartNode().getNumber()){
				for (int k = 1; k < this.threads.length; k++) {
					this.threads[k].join();
				}
			}
			// Remove the explored node from the path
			path.remove((path.size()-1));
			node.visitedMT[thread] = false ;
		}
	}
	
	public class PulseTask implements Runnable{
		private double pLoad;
		private double pTime;
		private double pCost;
		private ArrayList<Integer> path;
		private double pDist;
		private int thread;
		private ESPNode head;
		
		public PulseTask(ESPNode head, double pLoad, double pTime, double pCost, ArrayList<Integer> path, double pDist, int thread) {
			this.pLoad  = pLoad;
			this.pTime  = pTime;
			this.pCost  = pCost;
			this.path   = new ArrayList<Integer>();
			this.pDist  = pDist;
			this.thread = thread;
			this.head   = head;
			
			this.path.addAll(path);
		}
		@Override
		public void run() {
			try {
				pulseMT(head,pLoad, pTime, pCost, path, pDist, thread);
			} catch (InterruptedException e) {
				// 
				e.printStackTrace();
			}
		}

	}


	/** Rollback pruning strategy
	 * @param path current partial path
	 * @param pCost current cost
	 * @param pTime current time
	 * @return
	 */
	private boolean rollback(ESPNode node, ArrayList<Integer> path, double pCost, double pTime) {
		// Can't use the strategy for the start node
		if(path.size()<=1){
			return false;
		}
		else{
			// Calculate the cost for the rollback pruning strategy 
			int prevNode = (int) path.get(path.size()-1);
			int directNode = (int) path.get(path.size()-2);
			double directCost = pCost-espprcIns.costs[prevNode][node.getNumber()]-espprcIns.costs[directNode][prevNode]+espprcIns.costs[directNode][node.getNumber()];			
			
			if(directCost <= pCost ){
				return true;
			}
		}
	
		return false;
	}


	/** This method calculates a lower bound given a time consumption at a given node
	* @param time current time
	* @param root current root node
	* @return
	*/
	private double calcBoundPhaseI(ESPNode node, double time, ESPNode root) {
		double bound=0;
		// If the time consumed is less than the last time incumbent solved and the node id is larger than the current root node being explored it means that there is no lower bound available and we must use the naive bound 
		//do not understand TODO
		if(time<this.timeIncumbent+this.timeStep && node.getNumber()>=root.getNumber()){
			bound=((this.timeIncumbent+this.timeStep-time)*this.naiveDualBound+this.overallBestCost);
		}
		else {
		// Else use the available bound	
			int Index=((int) Math.floor(time/this.timeStep)); 
			bound=node.boundsMatrix.get(Index);
		}
		return bound;
	}
	
	
	/** This method calculates a lower bound given a time consumption at a given node
	* @param Time current time
	* @return
	*/
	private double CalcBoundPhaseII(ESPNode node, double time) {
		double bound=0;
		//If the time consumed is less than the current time incumbent it means that there is no lower bound available and we must use the naive bound 
		if(time<this.timeIncumbent){
			bound=(this.timeIncumbent-time)*this.naiveDualBound+this.overallBestCost;	
		}
		else {
			// Else use the available bound	
			int Index=((int) Math.floor(time/this.timeStep));			
			bound=node.boundsMatrix.get(Index);
		
		}
		return bound;
	}
	
	
	
	/* Override for the bounding procedure
	 */
	public void pulseBoundFinal(double PLoad, double PTime, double PCost, ArrayList<Integer> path, ESPNode root, double PDist) {
		// If the path is feasible update values for the bounding matrix and primal bound
		if (PLoad <= espprcIns.getCapacity() && (PTime) <= espprcIns.getEspFinalNode().getEndTw()) {
			if ((PCost) < root.bestCost) {
				root.bestCost = (PCost);
				if (PCost < primalBound) {
					primalBound = PCost;
				}
			}
		}
	}
	
	/* Final node for the pulse procedure
	 */
	public void pulseMTFinal(double pLoad, double pTime, double pCost, ArrayList<Integer> path, double pDist, int thread) {
		//System.out.println("final: pLoad = "+pLoad+" pTime = "+pTime+" pCost = "+pCost);
		
		// If the path is feasible and better than the best known solution update the best known solution and primal bound	
		if (pLoad <= espprcIns.getCapacity() && (pTime) <= espprcIns.getEspFinalNode().getEndTw()) {
			if (pCost <= primalBound) {
				primalBound = pCost;
				espprcIns.getEspFinalNode().pathTime = pTime;
				espprcIns.getEspFinalNode().pathCost = pCost;
				espprcIns.getEspFinalNode().pathLoad = pLoad;
				espprcIns.getEspFinalNode().pathDist = pDist;
				//espprcIns.getEspFinalNode().path.clear();
				//espprcIns.getEspFinalNode().path.addAll(path);
				ArrayList<Integer> finalPath = new ArrayList<Integer>(path);
				finalPath.add(espprcIns.getEspFinalNode().getNumber());
				espprcIns.getEspFinalNode().path = finalPath;
				//espprcIns.getEspFinalNode().path.add(espprcIns.getEspFinalNode().getNumber());
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////7
}
