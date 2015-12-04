package kz.hts.ce.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import kz.hts.ce.config.PagesConfiguration;
import kz.hts.ce.controller.sale.CalculatorController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static kz.hts.ce.util.spring.SpringFxmlLoader.getPagesConfiguration;

@Controller
public class MenuController {

    @FXML
    private Button settings;
    @FXML
    private Button products;
    @FXML
    private Button createProduct;
    @FXML
    private Button sales;
    @FXML
    private Button receipts;

    @Autowired
    private MainController mainController;
    @Autowired
    private CalculatorController calculatorController;

    @FXML
    private void showContent(ActionEvent event) throws IOException {
        PagesConfiguration screens = getPagesConfiguration();
        Node node;
//        if (event.getSource() == createProduct) {
//            node = screens.createProducts();
//            mainController.getContentContainer().getChildren().setAll(node);
//            calculatorController.stopEventHandler(screens.getPrimaryStage().getScene());
//        } else
        if (event.getSource() == sales) {
            mainController.getContentContainer().getChildren().setAll(mainController.getSales());
            calculatorController.startEventHandler(screens.getPrimaryStage().getScene());
        } else if (event.getSource() == receipts){
            node = screens.receipts();
            mainController.getContentContainer().getChildren().setAll(node);
            calculatorController.stopEventHandler(screens.getPrimaryStage().getScene());
        } else if (event.getSource() == products) {
            node = screens.shopProducts();
            mainController.getContentContainer().getChildren().setAll(node);
            calculatorController.stopEventHandler(screens.getPrimaryStage().getScene());
        } else if (event.getSource() == settings) {
            node = screens.settings();
            mainController.getContentContainer().getChildren().setAll(node);
            calculatorController.stopEventHandler(screens.getPrimaryStage().getScene());
        }
    }
}
