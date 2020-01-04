package app.fx.controller;

import java.util.Collection;

import domain.model.EnderecoVO;
import domain.model.EstadoCivilVO;
import domain.model.Municipio;
import domain.model.UFVO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import service.ClienteService;
import service.MunicipioService;
import service.impl.DefaultClienteService;
import service.impl.DefaultMunicipioService;

public class ClienteNovoController {

	private MunicipioService sMUNICIPIO;
	private ClienteService service;

	@FXML
	private ComboBox<Municipio> cbMunicipio;

	@FXML
	private TextField tfSobrenome;

	@FXML
	private TextField tfNascimento;

	@FXML
	private ComboBox<?> cbDdd;

	@FXML
	private ComboBox<?> cbTipoEndereco;

	@FXML
	private TableView<?> tvCliente;

	@FXML
	private ComboBox<EnderecoVO> cbEndereco;

	@FXML
	private Button btSalvar;

	@FXML
	private TextField tfCep;

	@FXML
	private TextField tfNumero;

	@FXML
	private TextField tfContato;

	@FXML
	private ComboBox<EstadoCivilVO> cbEstadoCivil;

	@FXML
	private TableColumn<?, ?> tcValor;

	@FXML
	private ComboBox<?> cbBairro;

	@FXML
	private TableColumn<?, ?> tcContato;

	@FXML
	private TextField tfComplemento;

	@FXML
	private ComboBox<UFVO> cbUF;

	@FXML
	private TextField tfNome;

	@FXML
	private Button btCancelar;

	@FXML
	void listar(ActionEvent event) {

	}

	@FXML
	private void initialize() {
		service = new DefaultClienteService();
		sMUNICIPIO = new DefaultMunicipioService();

		cbEstadoCivil.getItems().addAll(EstadoCivilVO.values());
		cbEstadoCivil.getSelectionModel().select(EstadoCivilVO.ESCOLHA);

		cbUF.getItems().addAll(UFVO.values());
		cbUF.getSelectionModel().select(UFVO.ESCOLHA);

		cbEndereco.getItems().addAll(EnderecoVO.values());
		cbEndereco.getSelectionModel().select(EnderecoVO.ESCOLHA);

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

}
