package de.saxsys.mvvmfx.examples.contacts.ui.toolbar;

import de.saxsys.mvvmfx.Scope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.examples.contacts.ui.scopes.ContactDialogScope;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;

public class ToolbarViewModel implements ViewModel {

	@Inject
	private Instance<ContactDialogScope> scopeInstance;

	public List<Scope> getScopesForAddDialog() {
		return Collections.singletonList(scopeInstance.get());
	}
}
