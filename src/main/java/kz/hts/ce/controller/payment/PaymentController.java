package kz.hts.ce.controller.payment;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import kz.hts.ce.config.PagesConfiguration;
import kz.hts.ce.controller.ControllerException;
import kz.hts.ce.controller.sale.ProductsController;
import kz.hts.ce.model.dto.ProductDto;
import kz.hts.ce.model.entity.WarehouseProductHistory;
import kz.hts.ce.model.entity.WarehouseProduct;
import kz.hts.ce.service.EmployeeService;
import kz.hts.ce.service.WarehouseProductHistoryService;
import kz.hts.ce.service.WarehouseProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static kz.hts.ce.util.JavaFxUtil.alert;
import static kz.hts.ce.util.JavaUtil.multiplyIntegerAndBigDecimal;
import static kz.hts.ce.util.JavaUtil.stringToBigDecimal;
import static kz.hts.ce.util.spring.SpringFxmlLoader.getPagesConfiguration;
import static kz.hts.ce.util.spring.SpringUtil.getPrincipal;

@Controller
public class PaymentController implements Initializable {

    public static final String ZERO = "0.00";

    @FXML
    private TextField shortage;
    @FXML
    private TextField given;
    @FXML
    private TextField change;
    @FXML
    private TextField total;

    @Autowired
    private WarehouseProductService warehouseProductService;
    @Autowired
    private WarehouseProductHistoryService warehouseProductHistoryService;
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProductsController productsController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        total.setText(productsController.getPriceResult().getText());
        shortage.setText(total.getText());
        given.setText(ZERO);
        change.setText(ZERO);
        given.textProperty().addListener((observable, oldValue, newValue) -> {
            BigDecimal totalBD = stringToBigDecimal(total.getText());
            if (!newValue.equals("")) {
                BigDecimal newVal = stringToBigDecimal(newValue);
                if (newVal.compareTo(totalBD) == 1) {
                    BigDecimal changeBD = newVal.subtract(totalBD);
                    change.setText(String.valueOf(changeBD));
                    shortage.setText(ZERO);
                } else {
                    BigDecimal shortageBD = totalBD.subtract(newVal);
                    shortage.setText(String.valueOf(shortageBD));
                    change.setText(ZERO);
                }
            } else {
                given.setText("");
                shortage.setText(total.getText());
            }
        });
    }

    @FXML
    public void success() {
        try {
            if (shortage.getText().equals(ZERO)) {
                ObservableList<ProductDto> productsData = productsController.getProductsData();
                for (ProductDto productDto : productsData) {
                    long warehouseProductId = productDto.getId();
                    WarehouseProduct warehouseProduct = warehouseProductService.findById(warehouseProductId);

                    WarehouseProductHistory warehouseProductHistory = new WarehouseProductHistory();
                    warehouseProductHistory.setEmployee(employeeService.findByUsername(getPrincipal()));
                    warehouseProductHistory.setWarehouseProduct(warehouseProduct);
                    warehouseProductHistory.setVersion(warehouseProduct.getVersion());
                    warehouseProductHistory.setArrival(productDto.getAmount());
                    warehouseProductHistory.setDate(new Date());
                    warehouseProductHistory.setTotalPrice(multiplyIntegerAndBigDecimal(productDto.getAmount(), warehouseProduct.getPrice()));
                    warehouseProductHistoryService.save(warehouseProductHistory);

                    int productAmount = productDto.getAmount();
                    int residue = warehouseProduct.getResidue();
                    warehouseProduct.setResidue(residue - productAmount);

                    warehouseProduct.setVersion(warehouseProduct.getVersion() + 1);
                    warehouseProductService.save(warehouseProduct);
                }
                alert(Alert.AlertType.INFORMATION, "Товар успешно продан", null, "Сдача: " + change.getText() + " тенге");
                productsController.deleteAllProductsFromTable();
                productsController.getProductsDto().clear();
                PagesConfiguration screens = getPagesConfiguration();
                screens.payment().hide();
            } else {
                alert(Alert.AlertType.WARNING, "Недостаточно средств", null, "Недостаточно средств для оплаты товара");
            }
        } catch (RuntimeException e) {
            throw new ControllerException(e);
        }
    }

    @FXML
    public void print(ActionEvent event) {
    }

    @FXML
    public void cancel() {
        PagesConfiguration screens = getPagesConfiguration();
        screens.payment().hide();
    }

    public TextField getGiven() {
        return given;
    }
}
