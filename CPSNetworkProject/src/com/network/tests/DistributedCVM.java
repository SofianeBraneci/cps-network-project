package com.network.tests;

import com.network.common.NodeAddress;
import com.network.common.Position;
import com.network.components.accesspointnode.AccessPointComponent;
import com.network.components.register.RegisterComponent;
import com.network.components.routingnode.RoutingNodeComponent;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {

	protected static final String TERMINAL_JVM_URI = "terminal";
	protected static final String ROUTING_JVM_URI = "routing";
	protected static final String REGISTER_JVM_URI = "register";
	protected static final String ACCESSPOINT_JVM_URI = "accesspoint";
	
	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}
	
	@Override
	public void instantiateAndPublish() throws Exception {
		if(AbstractCVM.getThisJVMURI().equals(TERMINAL_JVM_URI)) {
//			AbstractComponent.createComponent(TerminalNodeComponent.class.getCanonicalName(),
//			new Object[] { new NodeAddress("192.168.25.6"), new Position(3, 2), 10220.0 });
		}else if(AbstractCVM.getThisJVMURI().equals(ROUTING_JVM_URI)) {
			AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
					new Object[] { new NodeAddress("192.168.25.5"), new Position(2, 3), 11.0 });
		}else if(AbstractCVM.getThisJVMURI().equals(REGISTER_JVM_URI)) {
			AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[] {});
		}else if(AbstractCVM.getThisJVMURI().equals(ACCESSPOINT_JVM_URI)) {
			AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
					new Object[] { new NodeAddress("192.168.25.1"), new Position(1, 2), 1120.0 });
			AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
					new Object[] { new NodeAddress("192.168.25.6"), new Position(1, 5), 1120.0 });
			AbstractComponent.createComponent(RoutingNodeComponent.class.getCanonicalName(),
					new Object[] { new NodeAddress("192.168.25.2"), new Position(3, 2), 11200.0 });
			AbstractComponent.createComponent(AccessPointComponent.class.getCanonicalName(),
					new Object[] { new NodeAddress("192.168.25.3"), new Position(12, 112), 11200.0 });
		}else {
			System.out.println("UNKNOWN JVM URI:" + AbstractCVM.getThisJVMURI());
		}
		super.instantiateAndPublish();
	}


	@Override
	public void interconnect() throws Exception {
		if(AbstractCVM.getThisJVMURI().equals(TERMINAL_JVM_URI)) {
			
		}else if(AbstractCVM.getThisJVMURI().equals(ROUTING_JVM_URI)) {
			
		}else if(AbstractCVM.getThisJVMURI().equals(REGISTER_JVM_URI)) {
			
		}else if(AbstractCVM.getThisJVMURI().equals(ACCESSPOINT_JVM_URI)) {
			
		}else {
			System.out.println("UNKNOWN JVM URI:" + AbstractCVM.getThisJVMURI());
		}
		super.interconnect();
	}

	public static void main(String[] args) {
		try {
			DistributedCVM dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(10000L);
			Thread.sleep(100000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}




}
