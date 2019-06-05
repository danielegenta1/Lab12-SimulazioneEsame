/**
 * Sample Skeleton for 'Crimes.fxml' Controller Class
 */

package it.polito.tdp.crimes;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class CrimesController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="boxAnno"
    private ComboBox<Integer> boxAnno; // Value injected by FXMLLoader

    @FXML // fx:id="boxMese"
    private ComboBox<Integer> boxMese; // Value injected by FXMLLoader

    @FXML // fx:id="boxGiorno"
    private ComboBox<Integer> boxGiorno; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaReteCittadina"
    private Button btnCreaReteCittadina; // Value injected by FXMLLoader

    @FXML // fx:id="btnSimula"
    private Button btnSimula; // Value injected by FXMLLoader

    @FXML // fx:id="txtN"
    private TextField txtN; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML
    void doCreaReteCittadina(ActionEvent event)
    {
    	if (boxAnno.getSelectionModel().getSelectedIndex() != -1)
    	{
	    	int annoSelezionato = boxAnno.getSelectionModel().getSelectedItem();
	    	txtResult.clear();
	    	txtResult.appendText(model.handleCreaReteCittadina(annoSelezionato));
    	}
    	else
    	{
    		txtResult.clear();
    		txtResult.appendText("Devi prima selezionare un anno.\n");
    	}
    }

    @FXML
    void doSimula(ActionEvent event) 
    {
    	try
    	{
    		int n = Integer.parseInt(txtN.getText());
    		if (n >= 1 && n <= 10)
    		{
    			int anno = boxAnno.getSelectionModel().getSelectedItem();
    			int mese = boxMese.getSelectionModel().getSelectedItem();
    			int giorno = boxGiorno.getSelectionModel().getSelectedItem();
    			model.handleSimula(n, giorno, mese, anno);
    		}
    		else
    			//TODO
    		{}
    	}
    	catch (NumberFormatException e)
    	{
    		//TODO 
    	}
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxMese != null : "fx:id=\"boxMese\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert boxGiorno != null : "fx:id=\"boxGiorno\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnCreaReteCittadina != null : "fx:id=\"btnCreaReteCittadina\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Crimes.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Crimes.fxml'.";

    }
    
    public void setModel(Model model) 
    {
    	this.model = model;
    	
    	//load boxAnno
    	boxAnno.getItems().addAll(model.loadYears());
    	
    	//punto 2
    	boxMese.getItems().addAll(model.loadMonths());
    	boxGiorno.getItems().addAll(model.loadDays());
    }
}
