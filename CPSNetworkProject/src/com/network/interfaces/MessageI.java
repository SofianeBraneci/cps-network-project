package com.network.interfaces;

import java.io.Serializable;

/**
 * Interface for messages
 * @author Softwarkers
 *
 */
public interface MessageI {
	/**
	 * @return the message destination address
	 */
	AddressI getAddress();
	/**
	 * @return the message content
	 */
	Serializable getContent();
	/**
	 * @return True if the message is still alive, False else
	 */
	boolean stillAlive();
	/**
	 * Decrement the number of hops
	 */
	void decrementHops();

}
