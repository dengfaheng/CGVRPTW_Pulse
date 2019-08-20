package com.dfh.instance;

/**
 * Customer class stores information about one customer which implements the Vertex interface.
 * Stores the number of the customer, coordinates, service duration, capacity,
 */
public class Customer extends Node{
	private double arriveTime;          // time at which the car arrives to the customer
	private double waitingTime;         // time to wait until arriveTime equal start time window
	private double twViol;              // value of time window violation, 0 if none
	private double anglesToDepot;
	private double distanceTo;
	
	public Customer() {
		super();
		arriveTime           = 0;
		waitingTime          = 0;
		twViol               = 0;
		distanceTo           = 0;

	}
	
	public Customer(Node node) {
		super(node);
		arriveTime           = 0;
		waitingTime          = 0;
		twViol               = 0;
		distanceTo           = 0;
	}
	
	public Customer(Customer customer) {
		this.setNumber(customer.getNumber());
		this.setXCoordinate(customer.getXCoordinate()); 
		this.setYCoordinate(customer.getYCoordinate()); 
		this.setServiceDuration(customer.getServiceDuration());
		this.setDemand(customer.getDemand());
		this.setStartTw(customer.getStartTw());
		this.setEndTw(customer.getEndTw());
		this.arriveTime 		= new Double(customer.arriveTime);
		this.waitingTime 		= new Double(customer.waitingTime);
		this.twViol 			= new Double(customer.twViol);
		this.anglesToDepot   	= customer.anglesToDepot;
		this.distanceTo         = customer.distanceTo;
	}

	/**
	 * This return a string with formated customer data
	 * @return
	 */
	@Override
	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("\n");
		print.append("\n" + "--- Customer " + this.getNumber() + " -----------------------------------");
		print.append("\n" + "| x=" + this.getXCoordinate() + " y=" + this.getYCoordinate());
		print.append("\n" + "| ServiceDuration=" + this.getServiceDuration() + " Demand=" + this.getDemand());
		print.append("\n" + "| StartTimeWindow=" + this.getStartTw() + " EndTimeWindow=" + this.getEndTw());
		print.append("\n" + "| AnglesToDepots: " + anglesToDepot);
		print.append("\n" + "--------------------------------------------------");
		return print.toString();
		
	}
	
    @Override
    public boolean equals(Object o) {
    	Customer cust = (Customer)o;
        return this.getNumber() == cust.getNumber();
    }
    
    /**
	 * get the distanceTo
	 * @return distanceTo
	 */
	public double getDistanceTo() {
		return distanceTo;
	}
	
	/**
	 * set the distanceTo
	 * @param distanceTo
	 */
	public void setDistanceTo(double distanceTo) {
		this.distanceTo = distanceTo;
	}

	
	/**
	 * get the time at which the car arrives to the customer
	 * @return dispatchtime
	 */
	public double getArriveTime() {
		return arriveTime;
	}
	
	/**
	 * set the time at which the car arrives to the customer
	 * @param dispatchtime
	 */
	public void setArriveTime(double dispatchtime) {
		this.arriveTime = dispatchtime;
	}

	
	/**
	 * @return the anglestodepot
	 */
	public double getAnglesToDepot() {
		return anglesToDepot;
	}


	/**
	 * @param anglestodepot the anglestodepot to set
	 */
	public void setAnglesToDepot(double anglesToDepot) {
		this.anglesToDepot = anglesToDepot;
	}

	/**
	 * @return the waitingTime
	 */
	public double getWaitingTime() {
		return waitingTime;
	}

	/**
	 * @param waitingTime the waitingTime to set
	 */
	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

	/**
	 * @return the twViol
	 */
	public double getTwViol() {
		return twViol;
	}

	/**
	 * @param twViol the twViol to set
	 */
	public void setTwViol(double twViol) {
		this.twViol = twViol;
	}

}
