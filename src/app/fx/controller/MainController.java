package app.fx.controller;

import java.net.URL;
import java.util.Optional;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.Alerts;

@SuppressWarnings("deprecation")
public class MainController {

	@FXML
	private BorderPane view;

	@FXML
	private MenuItem miSAIR;

	@FXML
	private void initialize() {
		miSAIR.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
	}

	@FXML
	public void abrirMunicipio(ActionEvent e) {
		try {
			abrir("/app/fx/view/MunicipioView.fxml");
		} catch (Exception cause) {
			Dialogs.create().title("Cadastro de Municípios").message(cause.getMessage()).showException(cause);
			cause.printStackTrace();
		}
	}

	@FXML
	public void abrirBairro(ActionEvent e) {
		try {
			abrir("../view/BairroView.fxml");
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	public void abrirLogradouro(ActionEvent e) {
		try {
			abrir("../view/LogradouroView.fxml");
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	public void abrirClienteListar(ActionEvent e) {
		try {
			abrir("../view/ClienteListarView.fxml");
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	public void abrirClienteNovo(ActionEvent e) {
		try {
			abrir("../view/ClienteNovoView.fxml");
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	public void abrir(String tela) throws Exception {
		URL location;
		FXMLLoader fxml;
		Parent v;

		location = getClass().getResource(tela);
		fxml = new FXMLLoader(location);
		v = fxml.load();

		view.setCenter(v);
	}

	@FXML
	public void fechar(Event e) {
		Stage s = (Stage) view.getScene().getWindow();

		Optional<ButtonType> result = Alerts.showConfirmation("Sistema de cadastro - Vinicius Montes 2020", "Tem certeza que deseja sair ?");

		if (result.get() == ButtonType.OK) {
			s.close();

		} else {
			e.consume();
		}
	}
}
