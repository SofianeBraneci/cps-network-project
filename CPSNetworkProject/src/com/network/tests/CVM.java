package com.network.tests;


import com.network.components.register.RegisterComponent;
import com.network.components.terminalnode.TerminalNodeComponenet;
import com.network.connectors.TerminalNodeRegistrationConnector;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM{

	public CVM() throws Exception{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void deploy() throws Exception {
//		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[]{});
//		String terminalNodeUri = AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(), new Object[] {});
//		this.doPortConnection(terminalNodeUri, 
//				TerminalNodeComponent.TERMINAL_NODE_REGISTRATION_OUTBOUND_URI, 
//				RegisterComponent.REGISTER_INBOUND_PORT_URI,
//				RegisterTerminalNodeConnector.class.getCanonicalName());
//		super.deploy();
		
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[]{});
		String terminalNodeURI = AbstractComponent.createComponent(TerminalNodeComponenet.class.getCanonicalName(),	new Object[] {});
		doPortConnection(terminalNodeURI, 
				TerminalNodeComponenet.TERMINAL_NODE_REGISTRATION_OUTBOUND_PORT_URI, 
				RegisterComponent.REGISTER_INBOUND_PORT_URI, TerminalNodeRegistrationConnector.class.getCanonicalName());
		super.deploy();
	}

	public static void main(String[] args) throws Exception {
		CVM c = new CVM();
		c.startStandardLifeCycle(5000L);
		System.exit(0);
	}
}
