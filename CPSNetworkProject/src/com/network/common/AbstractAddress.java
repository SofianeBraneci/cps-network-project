package com.network.common;

import com.network.interfaces.AddressI;

public abstract class AbstractAddress implements AddressI{
	private String ip;
	public AbstractAddress(String ip) {
		this.ip = ip;
	}


	public String getIp() {
		return ip;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return ip.hashCode();
	}
	
	
	@Override
	public boolean equals(AddressI a) {
		// TODO Auto-generated method stub
		return this.ip.equals(a.toString());
	}
}
