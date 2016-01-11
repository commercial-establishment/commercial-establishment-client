package kz.hts.ce.controller;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import kz.hts.ce.config.PagesConfiguration;
import kz.hts.ce.model.dto.ProductDto;
import kz.hts.ce.model.entity.Category;
import kz.hts.ce.model.entity.Employee;
import kz.hts.ce.model.entity.WarehouseProduct;
import kz.hts.ce.service.CategoryService;
import kz.hts.ce.service.WarehouseProductService;
import kz.hts.ce.util.spring.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static kz.hts.ce.util.JavaUtil.createProductDtoFromWarehouseProduct;
import static kz.hts.ce.util.javafx.JavaFxUtil.alert;
import static kz.hts.ce.util.javafx.JavaFxUtil.calculator;
import static kz.hts.ce.util.spring.SpringFxmlLoader.getPagesConfiguration;

@Controller
public class SalesController implements Initializable {

    private StringBuilder buttonState;
    private Button button;
    private boolean flag;
    private ObservableList<String> categoriesData = FXCollections.observableArrayList();
    private ObservableList<ProductDto> categoryProductsData = FXCollections.observableArrayList();
    private Map<String, Integer> barcodeMap;

    private List<ProductDto> productsDto = new ArrayList<>();
    private ObservableList<ProductDto> productsData = FXCollections.observableArrayList();

    @FXML
    private AnchorPane root;
    @FXML
    private SplitPane splitPane;
    @FXML
    private TextField txtDisplay;
    @FXML
    private TextField txtAdditionalDisplay;

    @FXML
    private TableView<ProductDto> productTable;
    @FXML
    private TableColumn<ProductDto, String> name;
    @FXML
    private TableColumn<ProductDto, Number> amount;
    @FXML
    private TableColumn<ProductDto, BigDecimal> price;
    @FXML
    private TableColumn<ProductDto, BigDecimal> totalPrice;
    @FXML
    private TextField priceResult;
    @FXML
    private TableColumn<ProductDto, String> nameFromCategory;
    @FXML
    private TableColumn<ProductDto, BigDecimal> priceFromCategory;
    @FXML
    private TableColumn<ProductDto, Number> residueFromCategory;
    @FXML
    private TableView<ProductDto> categoryProductsTable;
    @FXML
    private ListView<String> categories;

    @Autowired
    private WarehouseProductService warehouseProductService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpringUtil springUtil;

    private EventHandler<KeyEvent> eventHandler;
    private Map<String, List<WarehouseProduct>> productMap = new HashMap<>();
    private ProductDto tempProducts;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        DoubleProperty dividerPositionProperty = splitPane.getDividers().get(0).positionProperty();
//        double[] pos = splitPane.getDividerPositions();
//        dividerPositionProperty.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
//            splitPane.setDividerPositions(pos);
//        });
        button = new Button();
        buttonState = new StringBuilder("");
        eventHandler = event -> {
            buttonState.setLength(0);
            buttonState.append(event.getCode().toString());
            handleOnAnyButtonFromKeypad();
        };

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startEventHandler(root.getScene());
            }
        });

        categoriesData.clear();
        List<Category> categoriesFromDb = categoryService.findAll();
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoriesFromDb) {
            String categoryName = category.getName();
            categoryNames.add(categoryName);
        }

        categoriesData.addAll(categoryNames.stream().collect(Collectors.toList()));
        categories.setItems(categoriesData);

        Employee employee = springUtil.getEmployee();


        List<Category> categoriesFromDB = categoryService.findAll();
        for (Category category : categoriesFromDB) {
            List<WarehouseProduct> warehouseProducts = warehouseProductService.
                    findByCategoryIdAndShopId(category.getId(), employee.getShop().getId());
            productMap.put(category.getName(), warehouseProducts);
        }

        categoriesListener(productMap);
        addProductToTable();

    }

    public void startEventHandler(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, eventHandler);
//        scene.setOnKeyPressed(eventHandler);
    }

    public void addProductsToTable() {
        productsData.clear();
        BigDecimal priceResultBD = BigDecimal.ZERO;
        for (ProductDto productDto : productsDto) {
//            WarehouseProduct byProductBarcode = warehouseProductService.findByProductBarcode(productDto.getBarcode());
//            for (List<WarehouseProduct> warehouseProducts : productMap.values()) {
//                for (WarehouseProduct warehouseProduct : warehouseProducts) {
//                    if(productDto.getBarcode() == warehouseProduct.getProduct().getBarcode())
//                        if(productDto.getAmount() > warehouseProduct.getResidue()){
//                            productDto.setAmount(warehouseProduct.getResidue());
//                            warehouseProduct.setResidue(warehouseProduct.getResidue()-productDto.getAmount());
//                        }
//                }
//            }
            productsData.add(productDto);
            BigDecimal totalPrice = productDto.getTotalPrice();
            priceResultBD = priceResultBD.add(totalPrice);
        }
        priceResult.setText(String.valueOf(priceResultBD));

        name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        amount.setCellValueFactory(cellData -> cellData.getValue().amountProperty());
        price.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        totalPrice.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty());

        productTable.setItems(productsData);
    }

    public void setProductDtoToProductsDto(ProductDto productDto) {
        productsDto.add(productDto);
    }

    public void deleteSelectedProductFromTable() {
        if (productTable != null) {
            ProductDto productDto = productTable.getSelectionModel().getSelectedItem();
            BigDecimal priceResultBD = new BigDecimal(priceResult.getText());
            priceResultBD = priceResultBD.subtract(productDto.getTotalPrice());
            priceResult.setText(String.valueOf(priceResultBD));
            productsDto.remove(productDto);
            productsData.remove(productDto);
            productTable.setItems(productsData);
        }
    }

    public void deleteAllProductsFromTable() {
        ObservableList<ProductDto> productDto = productTable.getSelectionModel().getTableView().getItems();
        productDto.clear();
        priceResult.setText("0.00");
    }

    public void addProductInProductsDto(ProductDto productDto) {
        productsDto.add(productDto);
    }

    @FXML
    public void handleOnAnyButtonClicked(ActionEvent evt) {
        Button button = (Button) evt.getSource();
        final String buttonText = button.getText();
        if (buttonText.matches("^[0-9CE\\s[*+.]\\s]*$")) {
            calculator(buttonText, txtDisplay, txtAdditionalDisplay);
        }
    }

    public void handleOnAnyButtonFromKeypad() {
        button.setText(String.valueOf(buttonState));
        String buttonText = button.getText();
        if (buttonText.startsWith("NUMPAD")) {
            String[] splittedButtonText = buttonText.split("NUMPAD");
            buttonText = splittedButtonText[1];
        } else if (button.getText().equals("DECIMAL")) {
            buttonText = ".";
        } else if (button.getText().equals("MULTIPLY")) {
            buttonText = "*";
        } else if (button.getText().equals("BACK_SPACE")) {
            buttonText = "CE";
        }
        if (buttonText.matches("^[0-9CE\\s[*+.]\\s]*$")) {
            calculator(buttonText, txtDisplay, txtAdditionalDisplay);
        } else if (buttonText.matches("ENTER")) {
            findAndAddProductByBarcode();
        } else if (buttonText.matches("SUBTRACT")) {
            deleteSelectedProductFromTable();
        } else if (buttonText.matches("ADD")) {
            paymentPage();
        }
    }

    public void paymentPage() {
        if (!getPriceResult().getText().equals("")) {
            PagesConfiguration screens = getPagesConfiguration();
            Stage stage = new Stage();
            screens.setPrimaryStage(stage);
            screens.payment();
        } else alert(Alert.AlertType.WARNING, "Товар не выбран", null, "Извините, список товаров пуст.");
    }

    public void findAndAddProductByBarcode() {
        try {
            String barcode = txtDisplay.getText();
            WarehouseProduct warehouseProduct = warehouseProductService.findByProductBarcode(barcode);
            if (warehouseProduct != null) {
                if (txtAdditionalDisplay.getText().equals("") || txtAdditionalDisplay.getText().equals("*")) {
                    txtAdditionalDisplay.setText("*1");
                }
                String[] splittedAmount = txtAdditionalDisplay.getText().split("\\*");
                ProductDto productDto = createProductDtoFromWarehouseProduct(warehouseProduct, Integer.parseInt(splittedAmount[1]));
                setProductDtoToProductsDto(productDto);
                addProductsToTable();
            } else
                alert(Alert.AlertType.WARNING, "Товар не найден", null, "Товар с данным штрих-кодом отсутствует!");
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Неверный штрих-код", null, "Штрих-код введён неверно!");
        }
    }

    public StringBuilder getButtonState() {
        return buttonState;
    }

    public TextField getPriceResult() {
        return priceResult;
    }

    public ObservableList<ProductDto> getProductsData() {
        return productsData;
    }

    public List<ProductDto> getProductsDto() {
        return productsDto;
    }


    public void categoriesListener(Map<String, List<WarehouseProduct>> productMap) {
        categories.getSelectionModel().selectedItemProperty().addListener((ov, oldVal, newVal) -> {
//            if (flag) {
            categoryProductsData.clear();
            List<WarehouseProduct> warehouseProducts = null;
            for (Map.Entry<String, List<WarehouseProduct>> productMapEntry : productMap.entrySet()) {
                if (newVal.equals(productMapEntry.getKey())) {
                    warehouseProducts = productMapEntry.getValue();
                }
            }
            if (warehouseProducts != null) {
                for (WarehouseProduct warehouseProduct : warehouseProducts) {
                    ProductDto productDto = new ProductDto();
                    productDto.setBarcode(warehouseProduct.getProduct().getBarcode());
                    productDto.setId(warehouseProduct.getProduct().getId());
                    productDto.setName(warehouseProduct.getProduct().getName());
                    productDto.setPrice(warehouseProduct.getInitialPrice());
                    productDto.setAmount(warehouseProduct.getResidue());
                    productDto.setResidue(warehouseProduct.getResidue());
                    categoryProductsData.add(productDto);
                }
            }
            nameFromCategory.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
            priceFromCategory.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
            residueFromCategory.setCellValueFactory(cellData -> cellData.getValue().residueProperty());
            categoryProductsTable.setItems(categoryProductsData);
//            }
//            flag = true;
        });
    }

    public void addProductToTable() {
        categoryProductsTable.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                if (barcodeMap == null) {
                    barcodeMap = new HashMap<>();
                }

                ProductDto productDtoRow = categoryProductsTable.getSelectionModel().getSelectedItem();
                WarehouseProduct warehouseProduct = warehouseProductService.findByProductBarcode(productDtoRow.getBarcode());
                if (barcodeMap.containsKey(productDtoRow.getBarcode())) {
                    for (Map.Entry<String, Integer> barcodeAmount : barcodeMap.entrySet()) {
                        if (barcodeAmount.getKey().equals(productDtoRow.getBarcode())) {
                            if (barcodeAmount.getValue() < warehouseProduct.getResidue()) {
                                productDtoRow.setAmount(barcodeAmount.getValue() + 1);
                                barcodeAmount.setValue(barcodeAmount.getValue() + 1);
                            } else {
                                productDtoRow.setAmount(barcodeAmount.getValue());
                                barcodeAmount.setValue(barcodeAmount.getValue());
                                alert(Alert.AlertType.WARNING, "Внимание!", null, "Количество добавляемого товара превышает остаток");
                            }
                            Iterator<ProductDto> productDtoIterator = getProductsDto().iterator();
                            while (productDtoIterator.hasNext()) {
                                ProductDto productDto = productDtoIterator.next();
                                if (productDto.getBarcode().equals(productDtoRow.getBarcode()))
                                    productDtoIterator.remove();
                            }


                            addProductInProductsDto(productDtoRow);
                        }
                    }
                } else {
                    productDtoRow.setAmount(1);
                    barcodeMap.put(productDtoRow.getBarcode(), 1);
                    addProductInProductsDto(productDtoRow);
                }
                addProductsToTable();
//                    if (selectedItem.getResidue() != 0)
//                        selectedItem.setResidue(selectedItem.getResidue() - 1);
//                    else
//                        selectedItem.setResidue(selectedItem.getResidue());

            }
        });
    }


    @FXML
    public void refreshTable() {
        getCategoryProductsTable().getProperties().put(TableViewSkin.RECREATE, Boolean.TRUE);
    }

    public TableColumn<ProductDto, String> getNameFromCategory() {
        return nameFromCategory;
    }

    public void setNameFromCategory(TableColumn<ProductDto, String> nameFromCategory) {
        this.nameFromCategory = nameFromCategory;
    }

    public TableColumn<ProductDto, BigDecimal> getPriceFromCategory() {
        return priceFromCategory;
    }

    public void setPriceFromCategory(TableColumn<ProductDto, BigDecimal> priceFromCategory) {
        this.priceFromCategory = priceFromCategory;
    }

    public TableColumn<ProductDto, Number> getResidueFromCategory() {
        return residueFromCategory;
    }

    public void setResidueFromCategory(TableColumn<ProductDto, Number> residueFromCategory) {
        this.residueFromCategory = residueFromCategory;
    }

    public TableView<ProductDto> getCategoryProductsTable() {
        return categoryProductsTable;
    }

    public void setCategoryProductsTable(TableView<ProductDto> categoryProductsTable) {
        this.categoryProductsTable = categoryProductsTable;
    }

    @FXML
    public void exit() {
        getPagesConfiguration().sales().close();
    }

    @FXML
    public void changeMode() {
        getPagesConfiguration().sales().close();
        getPagesConfiguration().main();
    }

}

