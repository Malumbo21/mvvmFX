package de.saxsys.mvvmfx.examples.contacts.ui.about;

import jakarta.inject.Inject;

import de.saxsys.mvvmfx.ViewModel;
import javafx.application.HostServices;

public class AboutAuthorViewModel implements ViewModel {

	@Inject
	private HostServices hostServices;

	public void openBlog() {
		hostServices.showDocument("https://www.lestard.eu");
	}

	public void openTwitter() {
		hostServices.showDocument("https://twitter.com/manuel_mauky");
	}

}
