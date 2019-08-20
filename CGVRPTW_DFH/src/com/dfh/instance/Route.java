package com.dfh.instance;

import java.util.ArrayList;
import java.util.List;

import ilog.concert.IloNumVar;
import com.dfh.instance.VrptwInstance;



public class Route {
	private int index;				  // Number of the route
	private Cost cost;                // cost of the route
	private double load;			     // sum of all quantities
	private Depot depot;              // depot the route starts from
	private List<Customer> customers; //list of customers served in the route
	
	
	/**
	 * Constructor of the route
	 */
	public Route() {
		cost = new Cost();
		customers = new ArrayList<>();
		load = 0;
		
	}
	
	public Route(Route route) {
		this.index = new Integer(route.index);
		this.cost = new Cost(route.cost);
		this.depot = route.depot;
		this.load  = route.getLoad();
		this.customers = new ArrayList<>();
		for (int i = 0; i < route.customers.size(); ++i) {
			this.customers.add(new Customer(route.getCustomer(i)));
		}
	}
	
	
	/**
	 * this function calculates the cost of a route from scratch
	 * @param route
	 */
	public void evaluateRoute(VrptwInstance vrptwInstance) {
    	double totalTime = 0;
    	double waitingTime = 0;
    	double twViol = 0;
    	Customer customerK;
    	//System.out.println("route["+route.getIndex()+"].twv = "+route.getCost().getTwViol());
    	this.initializeTimes();
    	this.setLoad(0);
    	// do the math only if the route is not empty
		if(!this.getCustomers().isEmpty()){
						
	    	// sum distances between each node in the route
			for (int k = 0; k < this.getCustomersLength(); ++k){
				// get the actual customer
				customerK = this.getCustomer(k);
				// add travel time to the route
				if(k == 0){
					this.getCost().travelTime += vrptwInstance.getTravelTime(this.getDepotNr(), customerK.getNumber());
					totalTime += vrptwInstance.getTravelTime(this.getDepotNr(), customerK.getNumber());
				}else{
					this.getCost().travelTime += vrptwInstance.getTravelTime(this.getCustomerNr(k -1), customerK.getNumber());
					totalTime += vrptwInstance.getTravelTime(this.getCustomerNr(k -1), customerK.getNumber());
				} // end if else
				
				customerK.setArriveTime(totalTime);
				// add waiting time if any
				waitingTime = Math.max(0, customerK.getStartTw() - totalTime); //ritorna zero se il customer 锟� pronto a ricevere il pacco altrimenti ritorna il tempo di attesa quindi potrei andare da un altro customer
				// update customer timings information
				customerK.setWaitingTime(waitingTime);
				
				totalTime = Math.max(customerK.getStartTw(), totalTime);

				// add time window violation if any
				twViol = Math.max(0, totalTime - customerK.getEndTw());
				
				this.getCost().addTWViol(twViol);
				customerK.setTwViol(twViol);
				// add the service time to the total
				totalTime += customerK.getServiceDuration();
				// add service time to the route
				// add capacity to the route
				this.addLoad(customerK.getDemand());
				
			} // end for customers
			
			// add the distance to return to depot: from last node to depot
			totalTime += vrptwInstance.getTravelTime(this.getLastCustomerNr(), this.getDepotNr());
			this.getCost().travelTime += vrptwInstance.getTravelTime(this.getLastCustomerNr(), this.getDepotNr());
			// add the depot time window violation if any
			twViol = Math.max(0, totalTime - this.getDepot().getEndTw());
						
			this.getCost().addTWViol(twViol);
			
			// update route with timings of the depot
			this.getCost().setLoadViol(Math.max(0, this.getLoad() - vrptwInstance.getVehiclesCapacity()));
			
			// update total violation
			this.getCost().calculateTotal(waitingTime, twViol);;
			
		} // end if route not empty
		
    } // end method evaluate route
	
	@Override
    public boolean equals(Object o) {
    	Route route = (Route)o;
    	
    	if(route.getCustomersLength() == this.getCustomersLength()) {
            for(int i = 0; i < route.getCustomers().size(); i++) {
            	if(route.getCustomer(i).getNumber() != this.getCustomer(i).getNumber()) {
            		return false;
            	}
            }
    	}else {
    		return false;
    	}
    	
        return true;
    }
	
	

	
	public Customer getCustomer(int index) {
		return this.customers.get(index);
	}
	public void setDepot(Depot depot) {
		this.depot = depot;
	}
	
	public void removeCustomer(int index){
		this.customers.remove(index);
	}
	
	public int getDepotNr(){
		return this.depot.getNumber();
	}
	
	public Depot getDepot(){
		return this.depot;
	}
	
	public int getLastCustomerNr(){
		return getCustomerNr(customers.size() - 1);
	}
	
	public int getFirstCustomerNr(){
		return getCustomerNr(0);
	}
	
	public boolean isEmpty(){
		if(getCustomersLength() > 0)
			return false;
		else return true;
	}
	
	/**
	 * Get the customer index found at certain position in the customer list
	 * @param index
	 * @return
	 */
	public int getCustomerNr(int index){
		return this.customers.get(index).getNumber();
	}
	/**
	 * Prints the route
	 */
	public String toString() {
		if(this.customers.isEmpty()) {
			return "";
		}
		StringBuffer print = new StringBuffer();
		
		print.append("Route[" + String.format("%-3d", index) + ", " +String.format("%-3d", getCustomersLength()) + "] = ");
		print.append( this.depot.getNumber());
		for (int i = 0; i < this.customers.size(); ++i) {
			print.append(" -> " +String.format("%-3d", this.customers.get(i).getNumber()));
		}
		print.append( " -> " + String.format("%-3d",this.depot.getNumber()));
		print.append("\n");
		return print.toString();
	}
	
	
	public String routeCostToString() {
		if(this.customers.isEmpty()) {
			return "";
		}
		StringBuffer print = new StringBuffer();
		print.append("\n" + "--- Route[" + String.format("%-3d",index)+ ", "+ String.format("%-6.4f", cost.total)+ "] ------------------------------");
		print.append("\n" + "| Load         = " + String.format("%-6.4f",load) );
		print.append("\n" + "| TravelTime   = " + String.format("%-6.4f",cost.travelTime) );
		print.append("\n" + "| LoadViol     = " + String.format("%-6.4f",cost.loadViol) );
		print.append("\n" + "| TWViol       = " + String.format("%-6.4f",cost.twViol) );
		print.append("\n" + "-------------------------------------------------------");
		return print.toString();
	}
	
	
	/**
	 * @param customers list to set
	 */
	public void setCustomers(ArrayList<Customer> customers) {
		this.customers = customers;
	}
	
	/**
	 * Add a new customer to the route
	 * @param customer
	 */
	public void addCustomer(Customer customer) {
		this.customers.add(customer);
	}
	
	/**
	 * Add a new customer to the route on specific position
	 * @param node
	 */
	public void addCustomer(Customer customer, int index) {
		this.customers.add(index, customer);
	}
	
	/**
	 * Set the index to the route
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}


	/**
	 * @param cost the cost to set
	 */
	public void setCost(Cost cost) {
		this.cost = cost;
	}
	

	/**
	 * @return customers list
	 */
	public List<Customer> getCustomers() {
		return this.customers;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return the number of nodes in the route
	 */
	public int getCustomersLength() {
		return this.customers.size();
	}

	/**
	 * @return the cost
	 */
	public Cost getCost() {
		return this.cost;
	}


	public void initializeTimes() {
		cost.initialize();
	}
	
	public void addLoad(double load) {
		this.load += load;
		
	}
	
	/**
	 * @return the load
	 */
	public double getLoad() {
		return load;
	}

	/**
	 * @param load the load to set
	 */
	public void setLoad(double load) {
		this.load = load;
	}

	public int a(int cust) {
		
		return 0;
	}

	

	  
}
