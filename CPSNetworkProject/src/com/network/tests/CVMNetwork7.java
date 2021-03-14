package com.network.tests;

import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.components.register.RegisterComponent;
import com.network.components.routingnode.RoutingNodeComponent;
import com.network.components.terminalnode.TerminalNodeComponent;
import com.network.components.accesspointnode.*;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMNetwork7 extends AbstractCVM {

	public CVMNetwork7() throws Exception {

	}

	@Override
	public void deploy() throws Exception {

		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.1"), new Position(1, 2), 1120.0 });
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.2"), new Position(3, 2), 11200.0 });
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.3"), new Position(12, 112), 11200.0 });
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.5"), new Position(2, 3), 11.0 });
//		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
//				new Object[] { new NodeAddress("192.168.25.6"), new Position(3, 2), 10220.0 });
//		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVMNetwork7 c = new CVMNetwork7();
			c.startStandardLifeCycle(50000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}