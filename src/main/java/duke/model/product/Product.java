package duke.model.product;

import javafx.beans.property.StringProperty;

import java.util.Objects;

import static duke.commons.util.AppUtil.checkEmpty;
import static duke.commons.util.CollectionUtil.requireAllNonNull;

public class Product {

    public enum Status {
        ACTIVE,
        ARCHIVE
    }


    public static final String MESSAGE_CONSTRAINTS = "comProduct name can take any values, "
            + "and should not be blank";

    private String productName;
    private IngredientItemList ingredients;
    private Double ingredientCost;
    private Double retailPrice;
    private Status status;


    public Product() {
    }

    /** Constructor for Order parser.util*/
    public Product(String productName) {
        this.productName = productName;
    }

    //public StringProperty productNameProperty() {
    //    return productName;
    //}

    /**
     * Creates a Product.
     */
    public Product(String productName, String retailPrice, String ingredientCost) {
        requireAllNonNull(productName);
        checkEmpty(productName, MESSAGE_CONSTRAINTS);

        try {
            this.productName = productName;
            this.ingredientCost = Double.parseDouble(ingredientCost);
            this.retailPrice = Double.parseDouble(retailPrice);
            this.status = Status.ACTIVE;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Product.
     */
    public Product(String productName, String retailPrice, String ingredientCost, IngredientItemList ingredientItemList) {
        requireAllNonNull(productName);
        checkEmpty(productName, MESSAGE_CONSTRAINTS);

        try {
            this.productName = productName;
            this.ingredientCost = Double.parseDouble(ingredientCost);
            this.retailPrice = Double.parseDouble(retailPrice);
            this.status = Status.ACTIVE;
            this.ingredients = ingredientItemList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a Product.
     */
    public Product(String productName, Double retailPrice, Double ingredientCost,
                   IngredientItemList ingredientItemList, Product.Status status) {
        requireAllNonNull(productName);
        checkEmpty(productName, MESSAGE_CONSTRAINTS);

        try {
            this.productName = productName;
            this.ingredientCost = ingredientCost;
            this.retailPrice = retailPrice;
            this.status = status;
            this.ingredients = ingredientItemList;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /** Constructor for edit comProduct */
    public Product(String productName, Double retailPrice, Double ingredientCost, Product.Status status) {
        requireAllNonNull(productName);
        checkEmpty(productName, MESSAGE_CONSTRAINTS);

        try {
            this.productName = productName;
            this.ingredientCost = ingredientCost;
            this.retailPrice = retailPrice;
            this.status = status;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public double getIngredientCost() {
        return ingredientCost;
    }

    public void setIngredientCost(double ingredientCost) {
        this.ingredientCost = ingredientCost;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public IngredientItemList getIngredients() {
        return ingredients;
    }

    public void setIngredients(IngredientItemList ingredients) {
        this.ingredients = ingredients;
    }

    /*
        public List<Ingredient> getIngredients() {
            return this.ingredients;
        }

        /*
            public List<Ingredient> getIngredients() {
                return this.ingredients;
            }

            public void setIngredients(List<Ingredient> ingredients) {
                this.ingredients = ingredients;
            }
        */
    @Override
    public String toString() {
        return productName + ": " + retailPrice + "$" + ingredients.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        duke.model.product.Product product = (duke.model.product.Product) o;
        return productName.equals(product.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName);
    }

}

