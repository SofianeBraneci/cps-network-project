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
}
