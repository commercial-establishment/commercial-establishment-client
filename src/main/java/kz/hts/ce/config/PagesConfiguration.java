package kz.hts.ce.config;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import kz.hts.ce.controller.*;
import kz.hts.ce.util.JsonUtil;
import kz.hts.ce.util.SpringFxmlLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static kz.hts.ce.util.SpringFxmlLoader.showStage;

@Lazy
@Configuration
public class PagesConfiguration {

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @PostConstruct
    public void initialize() {
        boolean fileExists = jsonUtil().checkJsonFile();
        if (!fileExists) {
            jsonUtil().create(10, 30);
        } else {
            jsonUtil().fillFields();
        }
    }

    @Bean
    public Stage main() {
        showStage(primaryStage, "/view/main.fxml");
        return primaryStage;
    }

    @Bean
    public Stage login() {
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        showStage(primaryStage, "/view/login.fxml");
        return primaryStage;
    }

    @Bean
    @Scope("prototype")
    public Stage addProduct() {
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        showStage(primaryStage, "/view/add-product.fxml");
        return primaryStage;
    }

    @Bean
    @Scope("prototype")
    public Stage payment() {
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setResizable(false);
        showStage(primaryStage, "/view/payment.fxml");
        return primaryStage;
    }

    @Bean
    @Scope("prototype")
    public Node createProducts() throws IOException {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        return (Node) springFxmlLoader.load("/view/create-product-provider.fxml");
    }

    @Bean
    @Scope("prototype")
    public Node receipts() throws IOException {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        return (Node) springFxmlLoader.load("/view/receipts.fxml");
    }

    @Bean
    @Scope("prototype")
    public Node addReceipt() throws IOException {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        return (Node) springFxmlLoader.load("/view/add-receipt.fxml");
    }

    @Bean
    @Scope("prototype")
    public Node shopProducts() throws IOException {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        primaryStage.getScene().getStylesheets().add("style.css");
        return (AnchorPane) springFxmlLoader.load("/view/shop-products.fxml");
    }

    @Bean
    public Node settings() {
        SpringFxmlLoader springFxmlLoader = new SpringFxmlLoader();
        return (AnchorPane) springFxmlLoader.load("/view/settings.fxml");
    }

    @Bean
    public LoginController loginController() {
        return new LoginController();
    }

    @Bean
    public MainController mainController() {
        return new MainController();
    }

    @Bean
    public CalculatorController calculatorController() {
        return new CalculatorController();
    }

    @Bean
    public PaymentController paymentController() {
        return new PaymentController();
    }

    @Bean
    public ProductsController productsController() {
        return new ProductsController();
    }

    @Bean
    public AddProductController addProductController() {
        return new AddProductController();
    }

    @Bean
    public ProductCategoryController productCategoryController() {
        return new ProductCategoryController();
    }

    @Bean
    public CreateProductController createProductController() {
        return new CreateProductController();
    }

    @Bean
    public ShopProductsController shopProductsController() {
        return new ShopProductsController();
    }

    @Bean
    public AddReceiptController addReceiptController() {
        return new AddReceiptController();
    }

    @Bean
    public JsonUtil jsonUtil() {
        return new JsonUtil();
    }
}
