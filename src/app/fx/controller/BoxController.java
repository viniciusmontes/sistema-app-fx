package app.fx.controller;

import domain.model.UFVO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class BoxController {

    @FXML
    private TextFlow text;

    @FXML
    private Text textData;


	@FXML
	private void Ok(ActionEvent ae) {

		Stage stage = (Stage) text.getScene().getWindow();
		stage.close();

	}

	public void setData(String nome, UFVO ufvo) {

		textData.setText("Municipio : " + nome + "  UF : " + ufvo.toString());

	}

}

