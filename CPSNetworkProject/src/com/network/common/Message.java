package com.network.common;

import java.io.Serializable;

import com.network.interfaces.AddressI;

public class Message implements MessageI{
	private AddressI address;
	private Serializable content;
	private int hops;
	
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
		return hops == 0;
	}

	@Override
	public void decementHops() {
		hops--;
	}

}
