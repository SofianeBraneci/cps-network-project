package com.network.common;

import com.network.interfaces.AddressI;
/**
 * 
 * Abstract class for addresses
 * 
 * @author Softwarkers
 *
 */
public abstract class AbstractAddress implements AddressI{
	/** IP address */
	private String ip;
	/**
	 * Initialize an address
	 * @param ip IP address
	 */
	public AbstractAddress(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the IP address
	 */
	public String getIp() {
		return ip;
	}
	
	@Override
	public int hashCode() {
		return ip.hashCode();
	}
	
	
	@Override
	public boolean equals(AddressI a) {
		return this.ip.equals(a.toString());
	}
}
