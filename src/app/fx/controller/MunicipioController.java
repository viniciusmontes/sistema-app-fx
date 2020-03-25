package app.fx.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import org.controlsfx.dialog.Dialogs;

import domain.exception.MunicipioException;
import domain.model.Municipio;
import domain.model.UFVO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import service.MunicipioService;
import service.impl.DefaultMunicipioService;
import utils.Alerts;

@SuppressWarnings("deprecation")
public class MunicipioController {

	private MunicipioService service;

	@FXML
	private TextField tfNOME;

	@FXML
	private TextField tfFILTER;

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
	private static ObservableList<Municipio> listItens = FXCollections.observableArrayList();

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

		tfFILTER.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!tfFILTER.getText().toLowerCase().equals("")) {

					tvMUNICIPIOS.setItems(findItems());
				} else {
					tvMUNICIPIOS.setItems(listItens);

				}

			}
		});

		initListerners();

	}

	private void initListerners() {
		tvMUNICIPIOS.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					FXMLLoader Loader = new FXMLLoader();
					Loader.setLocation(getClass().getResource("/app/fx/view/BoxView.fxml"));

					try {
						Loader.load();
					} catch (IOException ex) {
						ex.printStackTrace();

					}

					BoxController alertBoxController = Loader.getController();
					alertBoxController.setData("" + tvMUNICIPIOS.getSelectionModel().getSelectedItem().getNome(),
							tvMUNICIPIOS.getSelectionModel().getSelectedItem().getUf());
					Parent p = Loader.getRoot();
					Stage stage = new Stage();
					stage.setScene(new Scene(p));
					stage.show();

				}
			}

		});

	}

	@FXML
	private ObservableList<Municipio> findItems() {
		ObservableList<Municipio> itensEncontrados = FXCollections.observableArrayList();
		for (Municipio itens : listItens) {
			if (itens.getNome().contains(tfFILTER.getText())) {
				itensEncontrados.add(itens);
			}
		}
		return itensEncontrados;
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
			Alerts.showAlert("Erro", null, "Selecione ao menos um municipio para apagar", AlertType.ERROR);
			break;
		default:
			Optional<ButtonType> result = Alerts.showConfirmation("Confirmação",
					"Tem certeza que deseja apagar o Municipio ?");

			if (result.get() == ButtonType.OK) {
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