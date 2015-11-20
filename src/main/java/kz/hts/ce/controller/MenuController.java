package kz.hts.ce.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import kz.hts.ce.config.PagesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static kz.hts.ce.util.SpringFxmlLoader.getPagesConfiguration;

@Controller
public class MenuController {

    @FXML
    private Button createProduct;
    @FXML
    private Button sales;

    @Autowired
    private MainController mainController;

    @FXML
    private void show(ActionEvent event) throws IOException {
        PagesConfiguration screens = getPagesConfiguration();
        Node node;
        if (event.getSource() == createProduct) {
            node = screens.createProducts();
            mainController.getContentContainer().getChildren().setAll(node);
        } else if (event.getSource() == sales) {
            mainController.getContentContainer().getChildren().setAll(mainController.getSales());
        }
    }
}
