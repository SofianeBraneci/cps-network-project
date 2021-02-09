package com.network.tests;


import com.network.components.register.RegisterComponent;
import com.network.components.terminalnode.TerminalNodeComponent;
import com.network.connectors.RegisterTerminalNodeConnector;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM{

	public CVM() throws Exception{
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void deploy() throws Exception {
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[]{});
		String terminalNodeUri = AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(), new Object[] {});
		this.doPortConnection(terminalNodeUri, 
				TerminalNodeComponent.TERMINAL_NODE_INBOUND_URI, 
				RegisterComponent.REGISTER_PORT_URI,
				RegisterTerminalNodeConnector.class.getCanonicalName());
	}

	public static void main(String[] args) throws Exception {
		CVM c = new CVM();
		c.startStandardLifeCycle(5000L);
		System.exit(0);
	}
}
