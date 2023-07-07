package de.saxsys.mvvmfx.cdi.it;

import jakarta.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;

public class MyViewModel implements ViewModel {
	public static int instanceCounter = 0;
	
	@Inject
	private MyService myService;
	
	public MyViewModel() {
		instanceCounter++;
	}
	
}
