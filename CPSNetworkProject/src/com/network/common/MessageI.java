package com.network.common;

import java.io.Serializable;

import com.network.interfaces.AddressI;

public interface MessageI {
	AddressI getAddress();
	Serializable  getContent();
	boolean stillAlive();
	void decementHops();
	
}
