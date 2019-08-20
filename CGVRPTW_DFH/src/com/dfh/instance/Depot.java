package com.dfh.instance;

import java.util.ArrayList;

/**
 * Depot class stores information about one depot which implements the Vertex inferface.
 * It stores the number of the depot, it's capacity, coordinates, it's working time(time windows)
 * @author Banea George
 *
 */
public class Depot extends Node{
	private double capacity;                       // capacity that the depot can support

	public Depot() {
		super();
	}
	
	public Depot(Node node) {
		super(node);
	}
	
	/**
	 * Return the formated string of the depot
	 */
	@Override
	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("\n");
		print.append("\n" + "--- Depot " + this.getNumber() + " -------------------------------------");
		print.append("\n" + "| x=" + this.getXCoordinate() + " y=" + this.getYCoordinate());
		print.append("\n" + "| Capacity=" + capacity);
		print.append("\n" + "| StartTimeWindow=" + this.getStartTw() + " EndTimeWindow=" + this.getEndTw());
		print.append("\n" + "--------------------------------------------------");
		return print.toString();	
	}
	/**
	 * @return the capacity
	 */
	public double getCapacity() {
		return capacity;
	}
	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}



}
