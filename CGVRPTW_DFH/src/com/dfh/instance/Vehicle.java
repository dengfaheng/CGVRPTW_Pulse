package com.dfh.instance;

public class Vehicle {
	private String type;
	private double range;
	private double capacity;
	private double duration;

	public Vehicle() {
		duration = Integer.MAX_VALUE;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the range
	 */
	public double getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(double range) {
		this.range = range;
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
	
	/**
	 * @return the duration
	 */
	public double getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(double duration) {
		this.duration = duration;
	}

}
