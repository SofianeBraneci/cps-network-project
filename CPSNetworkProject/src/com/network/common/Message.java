package com.network.common;

import java.io.Serializable;

import com.network.interfaces.AddressI;
import com.network.interfaces.MessageI;
/**
 * 
 * Class for a message
 * 
 * @author Softwarkers
 * 
 */
public class Message implements MessageI{
	/**
	 * Destination address
	 */
	private AddressI address;
	/**
	 * Message content
	 */
	private Serializable content;
	/**
	 * Message remaining hops
	 */
	private int hops;
	
	/**
	 * Create and initialize a message
	 * @param address destination address
	 * @param content message content
	 * @param hops message initial hops
	 */
	public Message(AddressI address, Serializable content, int hops) {
		this.address = address;
		this.content = content;
		this.hops = hops;
		
	}

	@Override
	public AddressI getAddress() {
		return address;
	}

	@Override
	public Serializable getContent() {
		return content;
	}

	@Override
	public boolean stillAlive() {
		return hops > 0;
	}

	@Override
	public void decrementHops() {
		hops--;
	}

}
