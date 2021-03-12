package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.NetworkAddressI;
import com.network.interfaces.NodeAddressI;

public class NodeAddress extends AbstractAddress implements NodeAddressI {

	public NodeAddress(String ip) {
		// TODO Auto-generated constructor stub
		super(ip);
	}

	@Override
	public boolean equals(AddressI a) {
		return this.toString().equals(a.toString());
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getIp().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return getIp();
	}

}
