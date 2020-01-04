package app.fx.controller;

import java.util.Collection;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import service.MunicipioService;
import service.impl.DefaultMunicipioService;
import domain.exception.MunicipioException;
import domain.model.Municipio;
import domain.model.UFVO;

@SuppressWarnings("deprecation")
public class MunicipioController {

	private MunicipioService service;

	@FXML
	private TextField tfNOME;

	@FXML
	private ComboBox<UFVO> cbUF;

	@FXML
	private TableView<Municipio> tvMUNICIPIOS;

	@FXML
	private TableColumn<Municipio, String> tcMUNICIPIO;

	@FXML
	private TableColumn<Municipio, UFVO> tcUF;

	@FXML
	private Button btOK;

	@FXML
	private Button btCANCELAR;

	@FXML
	private void initialize() {
		service = new DefaultMunicipioService();

		cbUF.getItems().addAll(UFVO.values());
		cbUF.getSelectionModel().select(UFVO.ESCOLHA);
		cbUF.setOnAction(this::listar);

		// btOK.setOnAction(e -> this.salvar(e));
		btOK.setOnAction(this::salvar); // Reference method JDK 8
		btCANCELAR.setOnAction(this::cancelar);

		tvMUNICIPIOS.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tcMUNICIPIO.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNome()));
		tcUF.setCellValueFactory(v -> new SimpleObjectProperty<UFVO>(v.getValue().getUf()));
	}

	@FXML
	private void selecionarTodos(ActionEvent e) {
		tvMUNICIPIOS.getSelectionModel().selectAll();
	}

	private Municipio aalterar;

	@FXML
	private void alterar(ActionEvent e) {
		int total = tvMUNICIPIOS.getSelectionModel().getSelectedItems().size();

		switch (total) {
		case 1:
			aalterar = tvMUNICIPIOS.getSelectionModel().getSelectedItem();
			tfNOME.setText(aalterar.getNome());
			break;
		default:
			Dialogs.create().title("[IMPACTA]").message("Favor, selecione somente um para alterar").showInformation();
			break;
		}
	}

	@FXML
	private void apagar(ActionEvent e) {
		ObservableList<Municipio> items;
		int rows;

		items = tvMUNICIPIOS.getSelectionModel().getSelectedItems();
		rows = items.size();

		switch (rows) {
		case 0:
			Dialogs.create().title("[IMPACTA]").message("Favor, selecione ao menos 1 município para apagar!")
					.showInformation();
			break;
		default:
			Action a = Dialogs.create().title("[IMPACTA]").message("Confirma apagar?").showConfirm();

			if (a == Dialog.ACTION_YES) {
				items.forEach(m -> {
					try {
						service.apagar(m);
					} catch (Exception cause) {
						cause.printStackTrace();
					}
				});
				listar(e);
			}
			break;
		}
	}

	private void listar(ActionEvent e) {
		Thread t = new Thread(() -> {
			Collection<Municipio> c;
			ObservableList<Municipio> ol;
			UFVO uf;

			try {
				uf = cbUF.getValue();
				c = service.listar(uf);

				ol = tvMUNICIPIOS.getItems();

				ol.clear();
				ol.addAll(c);
			} catch (Exception cause) {
				cause.printStackTrace();
			}
		});

		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	private void cancelar(ActionEvent e) {
		tfNOME.clear();
		tfNOME.requestFocus();
	}

	private void salvar(ActionEvent e) {
		Municipio model;

		try {
			model = aalterar != null ? aalterar : new Municipio();
			aalterar = null;

			model.setNome(tfNOME.getText());
			model.setUf(cbUF.getValue());

			service.validar(model);
			service.salvar(model);
			listar(e);
			cancelar(e);
		} catch (MunicipioException cause) {
			cause.printStackTrace();
			Dialogs.create().title("Cadastro de municípios").message(cause.getMessage()).showWarning();
		}
	}
}
