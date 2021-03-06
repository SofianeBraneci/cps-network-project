package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;
/**
 * 
 * Class for node addresses
 * 
 * @author Softwarkers
 *
 */
public class NodeAddress extends AbstractAddress implements NodeAddressI {

	/**
	 * Initialise a node address
	 * @param ip IP address
	 */
	public NodeAddress(String ip) {
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
	public boolean equals(Object obj) {
		if(obj instanceof NodeAddress) {
			NodeAddressI o = (NodeAddressI) obj;
			return this.equals(o);
		}
		else if(obj instanceof NetworkAddress) {
			NetworkAddressI o = (NetworkAddressI) obj;
			return this.equals(o);
		}
		return false;
	}

	@Override
	public String toString() {
		return getIp();
	}

}
