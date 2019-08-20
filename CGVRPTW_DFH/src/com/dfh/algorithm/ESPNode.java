package com.dfh.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import com.dfh.instance.*;


public class ESPNode extends Node{
	
	public boolean firstTime;              // boolean that indicates if the node is visited for first time
	public  boolean visited;			    // Binary indicator for detecting cycles in the bounding stage
	public  boolean[] visitedMT;		    // Binary indicator for detecting cycles one for each thread
	public  double bestCost;		        // Best cost found for node at each iteration of the bounding stage
	public ArrayList<ESPEdge> outgoingEdges;// Array of edges
	public HashMap<Integer, Double> boundsMatrix;// Bounds matrix
	
	public ESPNode() {
		super();
		visited    = false;
		firstTime  = true;
		bestCost   = Integer.MAX_VALUE;
		outgoingEdges             = new ArrayList<ESPEdge>();
		boundsMatrix = new HashMap<Integer, Double>();
	}
	
	public ESPNode(Node node) {
		super(node);
		visited    = false;
		firstTime  = true;
		bestCost   = Integer.MAX_VALUE;
		outgoingEdges             = new ArrayList<ESPEdge>();
		boundsMatrix = new HashMap<Integer, Double>();
	}
}
