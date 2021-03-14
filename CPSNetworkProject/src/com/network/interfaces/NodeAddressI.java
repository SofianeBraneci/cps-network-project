package com.network.interfaces;

/**
 * Interface for node addresses
 * @author Softwarkers
 *
 */
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
