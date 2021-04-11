package com.network.tests;

import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.components.register.RegisterComponent;
import com.network.components.routingnode.RoutingNodeComponent;
import com.network.components.terminalnode.TerminalNodeComponent;
import com.network.components.accesspointnode.*;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMNetwork1 extends AbstractCVM {

	public CVMNetwork1() throws Exception {

	}

	@Override
	public void deploy() throws Exception {
		// this is just a simple network config
		// if you want to test other functionalities make sure to uncomment them in the
		// execute of each component
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[] {});
	
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.2"), new Position(3, 2), 11200.0 });
		

//		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
//				new Object[] { new NodeAddress("192.168.25.5"), new Position(2, 3), 11.0 });
//
//		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
//				new Object[] { new NodeAddress("192.168.25.6"), new Position(3, 2), 10220.0 });
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.6"), new Position(2, 3), 11.0 });
		
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.61"), new Position(3, 2), 10220.0 });

		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVMNetwork1 c = new CVMNetwork1();
			c.startStandardLifeCycle(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}