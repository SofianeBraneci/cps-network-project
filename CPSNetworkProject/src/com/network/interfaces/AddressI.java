package com.network.interfaces;

public interface AddressI {
	boolean isNodeAddress();
	boolean isNetworkAddress();
	boolean equals(AddressI a);
}
