package com.network.interfaces;

public interface NodeAddressI extends AddressI{
	@Override
	default boolean isNetworkAddress() {
		return false;
	}
	
	@Override
	default boolean isNodeAddress() {
		return true;
	}

}
