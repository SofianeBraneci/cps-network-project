package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.NetworkAddressI;

/**
 * 
 * Class for network addresses
 * 
 * @author Softwarkers
 *
 */
public class NetworkAddress extends AbstractAddress implements NetworkAddressI {
	/**
	 * Initialize a network address
	 * @param ip IP address
	 */
	public NetworkAddress(String ip) {
		super(ip);
	}

	@Override
	public boolean equals(AddressI a) {
		return this.toString().equals(a.toString());
	}

	@Override
	public int hashCode() {
		return getIp().hashCode();
	}

	@Override
	public String toString() {
		return getIp();
	}

}
