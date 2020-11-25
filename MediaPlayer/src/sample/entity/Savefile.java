package sample.entity;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class Savefile {
    @FXML
    TextField saveFileName;
    @FXML
    Button cancelSave;
    @FXML
    Button okSave;
    private String fileName=null;


    @FXML
    private void initialize(){
        okSave.disableProperty().bind(Bindings.isEmpty(saveFileName.textProperty()));
        okSave.setOnAction(event -> {
            fileName=saveFileName.getText();
            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
        cancelSave.setOnAction(event -> {

            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
    }
    public String getFileName(){
        if(fileName.equals(null)) return "null";
        else return fileName;
    }


}
