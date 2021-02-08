package com.network.common;

import com.network.interfaces.AddressI;
import com.network.interfaces.NodeAddressI;

public class NodeAddress extends AbstractAddress implements NodeAddressI{

	public NodeAddress(String ip) {
		// TODO Auto-generated constructor stub
		super(ip);
	}

	@Override
	public boolean equals(AddressI a) {
		return this == a;
	}
	

}
