package com.dfh.instance;

public class Node {
	private int number;
	private double xCoordinate;
	private double yCoordinate;
	private double serviceDuration;     // duration that takes to dispatch the delivery    
	private double demand;                // demand of the pack that is expecting
	private int startTw;                // beginning of time window (earliest time for start of service),if any
	private int endTw;                  // end of time window (latest time for start of service), if any
	
	public Node() {
		xCoordinate          = 0;
		yCoordinate          = 0;
		serviceDuration      = 0;
		demand               = 0;
		startTw              = 0;
		endTw                = 0;
	}
	
	public Node(Node node) {
		this.number 			= node.number;
		this.xCoordinate 		= node.xCoordinate;
		this.yCoordinate 		= node.yCoordinate;
		this.serviceDuration 	= node.serviceDuration;
		this.demand 		    = node.demand;
		this.startTw 			= node.startTw;
		this.endTw 				= node.endTw;
	}

	/**
	 * This return a string with formated customer data
	 * @return
	 */
	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("\n");
		print.append("\n" + "--- Customer " + number + " -----------------------------------");
		print.append("\n" + "| x=" + xCoordinate + " y=" + yCoordinate);
		print.append("\n" + "| ServiceDuration=" + serviceDuration + " Demand=" + demand);
		print.append("\n" + "| StartTimeWindow=" + startTw + " EndTimeWindow=" + endTw);
		print.append("\n" + "--------------------------------------------------");
		return print.toString();
	}
	
    @Override
    public boolean equals(Object o) {
    	Node node = (Node)o;
        return this.getNumber() == node.getNumber();
    }
    
 
	/**
	 * @return the customernumber
	 */
	public int getNumber() {
		return this.number;
	}


	/**
	 * @param customernumber the customernumber to set
	 */
	public void setNumber(int customernumber) {
		this.number = customernumber;
	}


	/**
	 * @return the xcoordinate
	 */
	public double getXCoordinate() {
		return xCoordinate;
	}


	/**
	 * @param xcoordinate the xcoordinate to set
	 */
	public void setXCoordinate(double xcoordinate) {
		this.xCoordinate = xcoordinate;
	}


	/**
	 * @return the ycoordinate
	 */
	public double getYCoordinate() {
		return yCoordinate;
	}


	/**
	 * @param ycoordinate the ycoordinate to set
	 */
	public void setYCoordinate(double ycoordinate) {
		this.yCoordinate = ycoordinate;
	}


	/**
	 * @return the serviceduration
	 */
	public double getServiceDuration() {
		return serviceDuration;
	}


	/**
	 * @param serviceduration the serviceduration to set
	 */
	public void setServiceDuration(double serviceduration) {
		this.serviceDuration = serviceduration;
	}


	/**
	 * @return the demand
	 */
	public double getDemand() {
		return demand;
	}


	/**
	 * @param demand the demand to set
	 */
	public void setDemand(double demand) {
		this.demand = demand;
	}


	/**
	 * @return the startTW
	 */
	public int getStartTw() {
		return startTw;
	}


	/**
	 * @param startTW the startTW to set
	 */
	public void setStartTw(int startTW) {
		this.startTw = startTW;
	}


	/**
	 * @return the endTW
	 */
	public int getEndTw() {
		return endTw;
	}


	/**
	 * @param endTW the endTW to set
	 */
	public void setEndTw(int endTW) {
		this.endTw = endTW;
	}



}
