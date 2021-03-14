package com.network.interfaces;
/**
 * Addresses interface
 * @author Softwarkers
 *
 */
public interface AddressI {
	/**
	 * Check if is a node address
	 * @return True if is a node address, False else
	 */
	boolean isNodeAddress();
	/**
	 * Check if is a network address
	 * @return True if is a network address, False else
	 */
	boolean isNetworkAddress();
	boolean equals(AddressI a);
}
