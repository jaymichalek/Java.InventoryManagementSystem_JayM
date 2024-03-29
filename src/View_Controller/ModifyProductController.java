package View_Controller;

import Model.Inventory;
import Model.Part;
import Model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static Model.Inventory.*;

public class ModifyProductController implements Initializable {
    public TextField modifyProductIdText;
    public TextField modifyProductNameText;
    public TextField modifyProductInvText;
    public TextField modifyProductPriceText;
    public TextField modifyProductMaxText;
    public TextField modifyProductMinText;
    public TextField modifyProductSearchText;
    public TableView modifyProductTableViewTop;
    public TableColumn modifyProductTopPartIdCol;
    public TableColumn modifyProductTopPartNameCol;
    public TableColumn modifyProductTopInventoryCol;
    public TableColumn modifyProductTopPriceCol;
    public TableView modifyProductTableViewBottom;
    public TableColumn modifyProductBottomPartIdCol;
    public TableColumn modifyProductBottomPartNameCol;
    public TableColumn modifyProductBottomInventoryCol;
    public TableColumn modifyProductBottomPriceCol;

    Stage stage;
    Parent scene;
    private ObservableList<Part> chosenParts = FXCollections.observableArrayList();
    private ObservableList<Part> existingParts = FXCollections.observableArrayList();
    private int currentId;
    private double totalPartPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // DISPLAY ALL PARTS DATA ON TOP TABLE VIEW:

        modifyProductTopPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        modifyProductTopPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        modifyProductTopInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        modifyProductTopPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        modifyProductTableViewTop.setItems(getAllParts());

        // DISPLAY ASSOCIATED PARTS ON BOTTOM TABLE VIEW WHEN ADD BUTTON IS CLICKED:

        modifyProductBottomPartIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        modifyProductBottomPartNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        modifyProductBottomInventoryCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
        modifyProductBottomPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        modifyProductTableViewBottom.setItems(existingParts);

    }

    public void onActionModifyProductSearchButton(ActionEvent actionEvent) {

        String searchText = modifyProductSearchText.getText().trim().toUpperCase();

        lookupPart(searchText);
        if (getAllFilteredParts().size() == 0) {
            try {
                lookupPart(Integer.parseInt(searchText));
            } catch (NumberFormatException e) {
                // ignore exception due to parseInt()
            }
        }

        modifyProductTableViewTop.setItems(getAllFilteredParts());

        if (getAllFilteredParts().isEmpty()) {
            modifyProductTableViewTop.setItems(getAllParts());
        }

    }

    public void onActionModifyProductAddButton(ActionEvent actionEvent) {

        // CHECKS IF PART IS ALREADY IN ASSOCIATED PARTS LIST:

        boolean existsInList = false;

        Part associatedPart = (Part) modifyProductTableViewTop.getSelectionModel().getSelectedItem();

        if (existingParts.contains(associatedPart)) {
            existsInList = true;
        }

        if(associatedPart == null) {
            return;
        } else if (existsInList) {
            AlertMessage.errorInProduct(7);
        } else {
            existingParts.add(associatedPart);
        }

    }

    public void onActionModifyProductDeleteButton(ActionEvent actionEvent) {

        Alert alertDelete = new Alert(Alert.AlertType.CONFIRMATION);
        alertDelete.setTitle("Confirmation Required");
        alertDelete.setHeaderText("This part will be deleted as an associated part of the product.");
        alertDelete.setContentText("Do you wish to proceed?");

        Optional<ButtonType> result = alertDelete.showAndWait();
        if (result.get() == ButtonType.OK){
            Part associatedPart = (Part) modifyProductTableViewBottom.getSelectionModel().getSelectedItem();
            if(associatedPart == null) {
                return;
            } else {
                existingParts.remove(associatedPart);
                resetTotalPartPrice();
            }
        }

    }

    public void onActionModifyProductSaveButton(ActionEvent actionEvent) throws IOException {

        try {
            // GET TEXT FROM TEXT FIELDS:

            String name = modifyProductNameText.getText();
            int stock = Integer.parseInt(modifyProductInvText.getText());
            double price = Double.parseDouble(modifyProductPriceText.getText());
            int max = Integer.parseInt(modifyProductMaxText.getText());
            int min = Integer.parseInt(modifyProductMinText.getText());
            getPartsTotalAmt();

            if (min > max) {
                AlertMessage.errorInProduct(5);
            } else if (max < min) {
                AlertMessage.errorInProduct(6);
            } else if (existingParts.isEmpty()) {
                AlertMessage.errorInProduct(1);
            } else if (stock > max || stock < min) {
                AlertMessage.errorInProduct(8);
            } else if (price < totalPartPrice) {
                AlertMessage.errorInProduct(9);
            } else {

                Product oldProduct = Inventory.selectProduct(currentId);
                Inventory.deleteProduct(oldProduct);

                Product newProduct = new Product(currentId, name, price, stock, min, max);

                for (int i = 0; i < existingParts.size(); i++) {
                    newProduct.addAssociatedPart(existingParts.get(i));
                }
                for (int i = 0; i < existingParts.size(); i++) {
                    if (newProduct.getAllAssociatedParts().contains(existingParts.get(i))) {
                        continue;
                    } else {
                        newProduct.deleteAssociatedPart(existingParts.get(i));
                    }
                }
                Inventory.addProduct(newProduct);

                stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }
        } catch (NumberFormatException e) {
            AlertMessage.errorInProduct(3);
        }

        resetTotalPartPrice();

    }

    public void onActionModifyProductCancelButton(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Required");
        alert.setHeaderText("All progress will not be saved.");
        alert.setContentText("Do you wish to proceed?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            stage = (Stage)((Button)actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
            stage.setScene(new Scene(scene));
            stage.show();
        }

    }

    public void onMouseModifyPartSearch(MouseEvent mouseEvent) {

        getAllFilteredParts().clear();
        modifyProductTableViewTop.setItems(getAllParts());

    }

    public void getProduct(Product product) {

        currentId = product.getId();
        modifyProductNameText.setText(product.getName());
        modifyProductInvText.setText(String.valueOf(product.getStock()));
        modifyProductPriceText.setText(String.valueOf(product.getPrice()));
        modifyProductMaxText.setText(String.valueOf(product.getMax()));
        modifyProductMinText.setText(String.valueOf(product.getMin()));

        for(int i = 0; i < product.getAllAssociatedParts().size(); i++) {
            existingParts.add(product.getAllAssociatedParts().get(i));
        }

    }

    public void getPartsTotalAmt() {

        for (Part part : existingParts) {

            totalPartPrice = totalPartPrice + part.getPrice();
        }
        System.out.println(totalPartPrice);
    }

    private void resetTotalPartPrice() {

        totalPartPrice = 0;
    }


}
