package com.network.interfaces;

public interface NetworkAddressI extends AddressI{
	@Override
	default boolean isNetworkAddress() {
		return true;
	}
	
	@Override
	default boolean isNodeAddress() {
		return false;
	}

}
