package com.network.interfaces;

/**
 * Interface for network addresses
 * @author Softwarkers
 *
 */
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
