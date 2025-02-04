package de.saxsys.mvvmfx.examples.contacts.ui.toolbar;

import jakarta.inject.Inject;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.examples.contacts.ui.addcontact.AddContactDialogView;
import de.saxsys.mvvmfx.examples.contacts.ui.addcontact.AddContactDialogViewModel;
import de.saxsys.mvvmfx.examples.contacts.util.DialogHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ToolbarView implements FxmlView<ToolbarViewModel> {

	@FXML
	public Button addNewContactButton;

	@InjectViewModel
	private ToolbarViewModel viewModel;

	@Inject
	private Stage primaryStage;

	public void initialize() {
		AwesomeDude.setIcon(addNewContactButton, AwesomeIcon.PLUS);
	}

	@FXML
	public void addNewContact() {
		ViewTuple<AddContactDialogView, AddContactDialogViewModel> load = FluentViewLoader
				.fxmlView(AddContactDialogView.class)
				.providedScopes(viewModel.getScopesForAddDialog())
				.load();
		Parent view = load.getView();
		Stage showDialog = DialogHelper.showDialog(view, primaryStage, "/contacts.css");
		load.getCodeBehind().setDisplayingStage(showDialog);
	}

}
