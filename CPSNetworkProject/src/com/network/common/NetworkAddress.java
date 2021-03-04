package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.NetworkAddressI;

public class NetworkAddress extends AbstractAddress implements NetworkAddressI {

	public NetworkAddress(String ip) {
		super(ip);
	}

	@Override
	public boolean equals(AddressI a) {
		return this == a;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return getIp().hashCode();
	}
	
}
