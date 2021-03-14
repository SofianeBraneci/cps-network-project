package com.network.tests;

import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.components.register.RegisterComponent;
import com.network.components.routingnode.RoutingNodeComponent;
import com.network.components.terminalnode.TerminalNodeComponent;
import com.network.components.accesspointnode.*;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMSendingMessage3 extends AbstractCVM {

	public CVMSendingMessage3() throws Exception {

	}

	@Override
	public void deploy() throws Exception {
		// test message sending, just make sure to uncomment the
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[] {});
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.1"), new Position(3, 2), 11200.0 });
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.3"), new Position(12, 112), 11200.0 });
		AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.6"), new Position(3, 2), 100.0 });
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.7"), new Position(5, 2), 10220.0 });
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.8"), new Position(3, 7), 1020.0 });
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.10"), new Position(10, 2), 10220.0 });
		AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.9"), new Position(4, 2), 100.0 });
		AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
				new Object[] { new NodeAddress("192.168.25.12"), new Position(6, 2), 1020.0 });
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVMSendingMessage3 c = new CVMSendingMessage3();
			c.startStandardLifeCycle(5000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}