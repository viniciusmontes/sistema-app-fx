package app.fx.controller;

import java.util.Collection;
import java.util.Optional;

import org.controlsfx.dialog.Dialogs;

import domain.exception.LogradouroException;
import domain.model.Bairro;
import domain.model.Logradouro;
import domain.model.Municipio;
import domain.model.UFVO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import service.BairroService;
import service.LogradouroService;
import service.MunicipioService;
import service.impl.DefaultBairroService;
import service.impl.DefaultLogradouroService;
import service.impl.DefaultMunicipioService;
import utils.Alerts;

@SuppressWarnings("deprecation")
public class LogradouroController {

	private LogradouroService service;
	private BairroService sBAIRRO;
	private MunicipioService sMUNICIPIO;

	@FXML
	private Button btOK;

	@FXML
	private ComboBox<Municipio> cbMunicipio;

	@FXML
	private TableColumn<Logradouro, UFVO> tcUF;

	@FXML
	private TableColumn<Logradouro, String> tcLogradouro;

	@FXML
	private TableView<Logradouro> tvLOGRADOURO;

	@FXML
	private ComboBox<Bairro> cbBairro;

	@FXML
	private TableColumn<Logradouro, String> tcMunicipio;

	@FXML
	private TableColumn<Logradouro, String> tcBairro;

	@FXML
	private ComboBox<UFVO> cbUF;

	@FXML
	private Button btCancelar;

	@FXML
	private TextField tfNome;

	@FXML
	private void initialize() {

		service = new DefaultLogradouroService();
		sMUNICIPIO = new DefaultMunicipioService();
		sBAIRRO = new DefaultBairroService();

		cbUF.getItems().addAll(UFVO.values());
		cbUF.getSelectionModel().select(UFVO.ESCOLHA);

		cbMunicipio.getSelectionModel();
		cbMunicipio.setOnAction(this::listarBairro);
		cbBairro.getSelectionModel();
		cbBairro.setOnAction(this::listar);

		btOK.setOnAction(this::salvar); // Reference method JDK 8
		btCancelar.setOnAction(this::cancelar);

		tvLOGRADOURO.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tcUF.setCellValueFactory(v -> new SimpleObjectProperty<UFVO>(v.getValue().getUf()));
		tcMunicipio.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getMunicipio().toString()));
		tcBairro.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getBairro().toString()));
		tcLogradouro.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getNome()));

	}

	private void listar(ActionEvent e) {
		Thread t = new Thread(() -> {
			UFVO uf;
			Municipio m;
			Bairro b;
			Collection<Logradouro> c;
			ObservableList<Logradouro> ol;

			try {
				uf = cbUF.getValue();
				m = cbMunicipio.getValue();
				b = cbBairro.getValue();
				c = service.listar(b, m, uf);

				ol = tvLOGRADOURO.getItems();

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
			olMUNICIPIO = cbMunicipio.getItems();

			olMUNICIPIO.clear();
			olMUNICIPIO.addAll(municipios);
		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	private void listarBairro(ActionEvent e) {
		UFVO uf = cbUF.getValue();
		Municipio municipio = cbMunicipio.getValue();
		Collection<Bairro> b;
		ObservableList<Bairro> olbairro;

		try {
			b = sBAIRRO.listar(municipio, uf);
			olbairro = cbBairro.getItems();

			olbairro.clear();
			olbairro.addAll(b);

		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	@FXML
	private void listarLogradouro(ActionEvent e) {
		UFVO uf = cbUF.getValue();
		Municipio municipio = cbMunicipio.getValue();
		Bairro bairro = cbBairro.getValue();
		Collection<Logradouro> l;
		ObservableList<Logradouro> olLogradouro;

		try {
			l = service.listar(bairro, municipio, uf);

			olLogradouro = tvLOGRADOURO.getItems();

			olLogradouro.clear();
			olLogradouro.addAll(l);

		} catch (Exception cause) {
			cause.printStackTrace();
		}
	}

	private Logradouro aalterar;

	@FXML
	private void salvar(ActionEvent e) {
		Logradouro model;

		try {
			model = aalterar != null ? aalterar : new Logradouro();
			aalterar = null;

			model.setUf(cbUF.getValue());
			model.setMunicipio(cbMunicipio.getValue());
			model.setBairro(cbBairro.getValue());
			model.setNome(tfNome.getText());

			service.validar(model);
			service.salvar(model);
			listar(e);
			cancelar(e);
			Alerts.showAlert("Confirmação", "Tudo certo...", "Cadastrado com sucesso!", AlertType.CONFIRMATION);
		} catch (LogradouroException cause) {
			cause.printStackTrace();
			Dialogs.create().title("Cadastro de municípios").message(cause.getMessage()).showWarning();
		}
	}

	@FXML
	private void apagar(ActionEvent e) {
		ObservableList<Logradouro> items;
		int rows;

		items = tvLOGRADOURO.getSelectionModel().getSelectedItems();
		rows = items.size();

		switch (rows) {
		case 0:
			Alerts.showAlert("Erro", null, "Selecione ao menos um para apagar", AlertType.ERROR);
			break;
		default:

			Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
					"Tem certeza que deseja apagar o Logradouro ?");

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

	private void cancelar(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
