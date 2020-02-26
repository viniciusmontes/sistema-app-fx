package app.fx.controller;

import java.util.Collection;
import java.util.Optional;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import domain.exception.BairroException;
import domain.exception.MunicipioException;
import domain.model.Bairro;
import domain.model.Municipio;
import domain.model.UFVO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import service.BairroService;
import service.MunicipioService;
import service.impl.DefaultBairroService;
import service.impl.DefaultMunicipioService;
import utils.Alerts;

@SuppressWarnings({ "deprecation", "unused" })
public class BairroController {

	private BairroService service;
	private MunicipioService sMUNICIPIO;

	@FXML
	private TextField tfNOME;

	@FXML
	private Button btOK;

	@FXML
	private Button btCANCELAR;

	@FXML
	private ComboBox<UFVO> cbUF;

	@FXML
	private ComboBox<Municipio> cbMUNICIPIO;

	@FXML
	private TableView<Bairro> tvBAIRRO;

	@FXML
	private TableColumn<Bairro, String> tcBAIRRO;

	@FXML
	private TableColumn<Bairro, String> tcMUNICIPIO;

	@FXML
	private TableColumn<Bairro, UFVO> tcUF;

	@FXML
	private void initialize() {
		service = new DefaultBairroService();
		sMUNICIPIO = new DefaultMunicipioService();

		cbUF.getItems().addAll(UFVO.values());
		cbUF.getSelectionModel().select(UFVO.ESCOLHA);

		cbMUNICIPIO.getSelectionModel();
		cbMUNICIPIO.setOnAction(this::listar);

		btOK.setOnAction(this::salvar); // Reference method JDK 8
		btCANCELAR.setOnAction(this::cancelar);

		tvBAIRRO.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tcBAIRRO.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNome()));
		tcMUNICIPIO.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getMunicipio().toString()));
		tcUF.setCellValueFactory(v -> new SimpleObjectProperty<UFVO>(v.getValue().getUf()));

	}

	private void listar(ActionEvent e) {
		Thread t = new Thread(() -> {
			Collection<Bairro> c;
			ObservableList<Bairro> ol;
			Municipio m;
			UFVO uf;

			try {
				uf = cbUF.getValue();
				m = cbMUNICIPIO.getValue();
				c = service.listar(m, uf);
				ol = tvBAIRRO.getItems();

				ol.clear();

				ol.addAll(c);

			} catch (Exception cause) {
				cause.printStackTrace();
			}
		});

		t.setPriority(Thread.MIN_PRIORITY);
		t.start();
	}

	@FXML
	private void listarMunicipios(ActionEvent e) {
		UFVO uf = cbUF.getValue();
		Collection<Municipio> municipios;
		ObservableList<Municipio> olMUNICIPIO;

		try {
			municipios = sMUNICIPIO.listar(uf);
			olMUNICIPIO = cbMUNICIPIO.getItems();

			olMUNICIPIO.clear();
			olMUNICIPIO.addAll(municipios);
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	private void listarBairro(ActionEvent e) {
		UFVO uf = cbUF.getValue();
		Municipio municipio = cbMUNICIPIO.getValue();
		Collection<Bairro> b;
		ObservableList<Bairro> olbairro;

		try {
			b = service.listar(municipio, uf);

			olbairro = tvBAIRRO.getItems();

			olbairro.clear();
			olbairro.addAll(b);

		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	private Bairro aalterar;

	@FXML
	private void alterar(ActionEvent e) {
		int total = tvBAIRRO.getSelectionModel().getSelectedItems().size();

		switch (total) {
		case 1:
			aalterar = tvBAIRRO.getSelectionModel().getSelectedItem();
			tfNOME.setText(aalterar.getNome());
			break;
		default:
			Alerts.showAlert("Erro", null, "Favor, selecione um bairro para alterar", AlertType.ERROR);

			break;
		}
	}

	@FXML
	private void apagar(ActionEvent e) {
		ObservableList<Bairro> items;
		int rows;

		items = tvBAIRRO.getSelectionModel().getSelectedItems();
		rows = items.size();

		switch (rows) {
		case 0:
			Alerts.showAlert("Erro", null, "Selecione ao menos um para apagar", AlertType.ERROR);
			break;
		default:

			Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
					"Tem certeza que deseja apagar o Bairro ?");

			if (result.get() == ButtonType.OK) {
				items.forEach(b -> {
					try {
						service.apagar(b);
					} catch (Exception cause) {
						cause.printStackTrace();
					}
				});
				listar(e);
			}
			break;
		}
	}

	@FXML
	private void salvar(ActionEvent e) {
		Bairro model;

		try {
			model = aalterar != null ? aalterar : new Bairro();
			aalterar = null;

			model.setUf(cbUF.getValue());
			model.setMunicipio(cbMUNICIPIO.getValue());
			model.setNome(tfNOME.getText());

			service.validar(model);
			service.salvar(model);
			listar(e);
			cancelar(e);
		} catch (BairroException cause) {
			cause.printStackTrace();
			Dialogs.create().title("Cadastro de bairros").message(cause.getMessage()).showWarning();
		}
	}

	private void cancelar(ActionEvent e) {
		tfNOME.clear();
		tfNOME.requestFocus();
	}
}
