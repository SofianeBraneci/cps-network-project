package com.network.tests;

import com.network.components.register.RegisterComponent;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVMTestRegister extends AbstractCVM {

	public CVMTestRegister() throws Exception {

	}

	@Override
	public void deploy() throws Exception {
		
		AbstractComponent.createComponent(RegisterComponent.class.getCanonicalName(), new Object[] {});
	
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVMTestRegister c = new CVMTestRegister();
			c.startStandardLifeCycle(5000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}