package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {

    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();
    public static ObservableList<Part> lookupPart = FXCollections.observableArrayList();
    public static ObservableList<Product> lookupProduct = FXCollections.observableArrayList();

    public static void addPart(Part newPart) {
        allParts.add(newPart);
    }

    public static void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    public static Part lookupPart(int partId) {

        if (!(Inventory.getAllFilteredParts().isEmpty())) {
            Inventory.getAllFilteredParts().clear();
        }

        for (Part part : Inventory.getAllParts()) {
            if (part.getId() == partId) {
                Inventory.getAllFilteredParts().add(part);
            }
        }

        return null;

    }

    public static Product lookupProduct(int productId) {

        if (!(Inventory.getAllFilteredProducts().isEmpty())) {
            Inventory.getAllFilteredProducts().clear();
        }

        for (Product product : Inventory.getAllProducts()) {
            if (product.getId() == productId) {
                Inventory.getAllFilteredProducts().add(product);
            }
        }

        return null;
    }

    public static ObservableList<Part> lookupPart(String partName) {

        if (!(Inventory.getAllFilteredParts().isEmpty())) {
            Inventory.getAllFilteredParts().clear();
        }

        for (Part part : Inventory.getAllParts()) {
            if (part.getName().contains(partName.toUpperCase())) {
                Inventory.getAllFilteredParts().add(part);
            }
        }

        if (Inventory.getAllFilteredParts().isEmpty()) {
            return Inventory.getAllParts();
        } else {
            return Inventory.getAllFilteredParts();
        }

    }

    public static ObservableList<Product> lookupProduct(String productName) {

        if (!(Inventory.getAllFilteredProducts().isEmpty())) {
            Inventory.getAllFilteredProducts().clear();
        }

        for (Product product: Inventory.getAllProducts()) {
            if (product.getName().contains(productName.toUpperCase())) {
                Inventory.getAllFilteredProducts().add(product);
            }
        }

        if (Inventory.getAllFilteredProducts().isEmpty()) {
            return getAllProducts();
        } else {
            return Inventory.getAllFilteredProducts();
        }

    }

    public static Part selectPart(int id) {
        for (Part part : Inventory.getAllParts()) {
            if (part.getId() == id) {
                return part;
            }
        }
        return null;
    }

    public static Product selectProduct(int id) {
        for (Product product : Inventory.getAllProducts()) {
            if(product.getId() == id) {
                return product;
            }
        }
        return null;
    }

    // FIXME: WILL PROBABLY NOT IMPLEMENT:
    public static void updatePart(int index, Part selectedPart) {

        int counter = -1;
        for (Part part : Inventory.getAllParts()) {
            counter++;
            if(part.getId() == index) {
                Inventory.getAllParts().set(counter, selectedPart);
                return;
            }
        }

    }

    // FIXME: WILL PROBABLY NOT IMPLEMENT:
    public static void updateProduct(int index, Product newProduct) {

        int counter = -1;
        for (Product product : Inventory.getAllProducts()) {
            counter++;
            if(product.getId() == index) {
                Inventory.getAllProducts().set(counter, newProduct);
                return;
            }
        }

    }

    public static boolean deletePart(Part selectedPart) {

        for(Part part : Inventory.getAllParts()) {
            if (part == selectedPart) {
                return Inventory.getAllParts().remove(part);
            }
        }
        return false;
    }

    public static boolean deleteProduct(Product selectedProduct) {

        for(Product product : Inventory.getAllProducts()) {
            if (product == selectedProduct) {
                return Inventory.getAllProducts().remove(product);
            }
        }
        return false;
    }

    public static ObservableList<Part> getAllParts() {

        return allParts;
    }

    public static ObservableList<Product> getAllProducts() {

        return allProducts;
    }

    public static ObservableList<Part> getAllFilteredParts() {

        return lookupPart;
    }

    public static ObservableList<Product> getAllFilteredProducts() {

        return lookupProduct;
    }

}
