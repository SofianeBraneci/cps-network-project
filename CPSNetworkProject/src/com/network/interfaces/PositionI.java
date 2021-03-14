package com.network.interfaces;

/**
 * Interface for positions
 * @author Softwarkers
 *
 */
public interface PositionI {

	/** calculate distance between two positions
	 * @param other position
	 * @return distance
	 */
	double distance(PositionI other);
}
