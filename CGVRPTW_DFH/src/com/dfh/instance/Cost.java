package com.dfh.instance;

/**
 * This class stores information about cost of a route or a group of routes.
 * It has total which is the sum of travel, capacityViol, durationViol, twViol.
 */
public class Cost {
	public double total;			 // sum of all the costs
	public double travelTime;		 // sum of all distances travel time
	public double loadViol;		     // violation of the load
	public double twViol;            // violation of the time window
	
	
	// default constructor
	public Cost(){
		total             = 0;
		travelTime        = 0;	
		loadViol          = 0;
		twViol            = 0;
	}
	
	// constructor which clone the cost passed as parameter
	public Cost(Cost cost) {
		this.total             = new Double(cost.total);
		this.travelTime        = new Double(cost.travelTime);
		
		this.loadViol          = new Double(cost.loadViol);
		this.twViol            = new Double(cost.twViol);	
	}

	
	public String toString() {
		StringBuffer print = new StringBuffer();
		print.append("\n" + "--- Cost -------------------------------------");
		print.append("\n" + "| TotalTravelCost=" + travelTime + " TotalCostViol=" + total);
		print.append("\n" + "| LoadViol=" + loadViol +  " TWViol=" + twViol);
		print.append("\n" + "--------------------------------------------------" + "\n");
		return print.toString();
	}
	
	
	/**
	 * Set the total cost based on alpha, beta
	 * @param alpha
	 * @param beta
	 * @param gamma
	 */
	public void calculateTotal(double alpha, double beta) {
		total = travelTime + alpha * loadViol  + beta * twViol;
	}
	
	public void setLoadViol(double capacityviol) {
		this.loadViol = capacityviol;
	}
	

	
	public void addLoadViol(double capacityviol) {
		this.loadViol += capacityviol;
	}
	

	
	public void addTWViol(double TWviol) {
		this.twViol += TWviol;
	}
	
	/**
	 * Add cost to the total cost
	 * @param cost
	 */
	public void addTravel(double cost) {
		travelTime +=cost;
	}
	
	public void setTravelTime(double travelTime) {
		this.travelTime = travelTime;
	}

	/**
	 * @return the totalcostviol
	 */
	public double getTotal() {
		return total;
	}
	/**
	 * @return the capacityviol
	 */
	public double getLoadViol() {
		return loadViol;
	}


	/**
	 * @return the tWviol
	 */
	public double getTwViol() {
		return twViol;
	}

	public void initialize() {
		total             	= 0;
		travelTime        	= 0;
		loadViol			= 0;
		twViol       		= 0;
		
	}
	
	// check if a cost has violations
    public boolean checkFeasible() {
    	if (this.loadViol == 0  && this.twViol == 0) {
    		return true;
    	} else {
    		return false;
    	}
    }
	/**
	 * @return the travelTime
	 */
	public double getTravelTime() {
		return travelTime;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(double total) {
		this.total = total;
	}

	/**
	 * @param twViol the twViol to set
	 */
	public void setTwViol(double twViol) {
		this.twViol = twViol;
	}
}
