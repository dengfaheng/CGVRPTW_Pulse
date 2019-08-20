package com.dfh.algorithm;

import java.util.HashMap;
import java.util.Map;

import com.dfh.instance.*;

public class ESPPRC {
	
	public VrptwInstance vrptwInstance;
	private HashMap<Integer, ESPNode> espNodes;// Array of nodes
	private int nodesNr;
	private ESPFinalNode espFinalNode;		// The final node overrides the class node and is different because it stops the recursion
	private double capacity;
	private ESPNode espStartNode;
	public double[][] costs;
	public double[][] distances;
	
	public ESPPRC() {
		
	}
	// Class constructor
	public ESPPRC(VrptwInstance vrpIns) {
		this.vrptwInstance   = vrpIns;
		this.nodesNr         = vrpIns.getCustomersNr()+1;
		this.espNodes        = new HashMap<Integer, ESPNode>();
		this.espFinalNode    = new ESPFinalNode(vrpIns.getNodes().get(vrpIns.getDepot().getNumber()));
//		this.espStartNode    = new ESPNode(vrpIns.getNodes().get(vrpIns.getDepot().getNumber()));

		this.capacity        = vrpIns.getVehiclesCapacity();
		this.distances       = vrpIns.getDistances();
		this.costs           = new double[vrpIns.getCustomersNr()+1][vrpIns.getCustomersNr()+1];
		initESPPRCDate();
	}
	
	public void initESPPRCDate() {
		int arc = 0;
		//add esp nodes
		for(Node val : vrptwInstance.getNodes().values()) {
			ESPNode espno = new ESPNode(val);
			espno.visitedMT = new boolean[vrptwInstance.getParameters().numThreads];
			espNodes.put(espno.getNumber(), espno);
		}
		
		//add esp edges
		for(ESPNode espi : getEspNodes().values()) {
			for(ESPNode espj : getEspNodes().values()) {
				if( (espi.getNumber() != espj.getNumber()) && 
					(espi.getStartTw()+espi.getServiceDuration() + 
					vrptwInstance.getTravelTime(espi.getNumber(), espj.getNumber()) < espj.getEndTw()) ) {
					ESPEdge espe = new ESPEdge(arc, espi, espj);
					espe.dist = vrptwInstance.getTravelTime(espi.getNumber(), espj.getNumber());
					espe.load = espj.getDemand();
					espe.time = espi.getServiceDuration() + espe.dist;
					espe.cost = espe.dist;
					//cost will be update later ... 
					espi.outgoingEdges.add(espe);
					
					costs[espe.getStartNode().getNumber()][espe.getEndNode().getNumber()] = espe.dist;
					
					arc++;
				}
			}
		}
		this.espStartNode = espNodes.get(vrptwInstance.getDepot().getNumber());
		this.espStartNode.visitedMT = new boolean[vrptwInstance.getParameters().numThreads];
	}
	
	public int getNodesNr() {
		return nodesNr;
	}
	public void setNodesNr(int nodesNr) {
		this.nodesNr = nodesNr;
	}
	public HashMap<Integer, ESPNode> getEspNodes() {
		return espNodes;
	}
	public void setEspNodes(HashMap<Integer, ESPNode> espNodes) {
		this.espNodes = espNodes;
	}
	public ESPFinalNode getEspFinalNode() {
		return espFinalNode;
	}
	public void setEspFinalNode(ESPFinalNode espFinalNode) {
		this.espFinalNode = espFinalNode;
	}
	public double getCapacity() {
		return capacity;
	}
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}
	public ESPNode getEspStartNode() {
		return espStartNode;
	}
	public void setEspStartNode(ESPNode espStartNode) {
		this.espStartNode = espStartNode;
	}
	
	

}
