package duke.model;

import duke.commons.core.index.Index;
import duke.logic.command.order.SortOrderCommand;
import duke.logic.command.product.SortProductCommand;
import duke.model.commons.Item;
import duke.model.commons.Quantity;
import duke.model.inventory.Ingredient;
import duke.model.order.Order;
import duke.model.order.OrderComparator;
import duke.model.product.Product;
import duke.model.sale.Sale;
import duke.model.shortcut.Shortcut;
import javafx.collections.ObservableList;

import java.util.List;

import static duke.commons.util.CollectionUtil.requireAllNonNull;
import static java.util.Objects.requireNonNull;

/**
 * Wraps all data at the baking-home level.
 */
public class BakingHome implements ReadOnlyBakingHome {

    private final UniqueEntityList<Sale> sales;
    private final UniqueEntityList<Order> orders;
    private final UniqueEntityList<Product> products;
    private final UniqueEntityList<Item<Ingredient>> inventory;
    private final UniqueEntityList<Item<Ingredient>> shoppingList;
    private final UniqueEntityList<Shortcut> shortcuts;

    /**
     * Creates a BakingHome.
     */
    public BakingHome() {
        sales = new UniqueEntityList<>();
        orders = new UniqueEntityList<>();
        products = new UniqueEntityList<>();
        inventory = new UniqueEntityList<>();
        shoppingList = new UniqueEntityList<>();
        shortcuts = new UniqueEntityList<>();
    }

    /**
     * Creates BakingHome for {@code toBeCopied}.
     */
    public BakingHome(ReadOnlyBakingHome toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    /**
     * Resets the existing data of this {@code BakingHome} with {@code newData}.
     */
    public void resetData(ReadOnlyBakingHome newData) {
        requireNonNull(newData);

        setShortcuts(newData.getShortcutList());
        setInventory(newData.getInventoryList());
        setSale(newData.getSaleList());
        setProducts(newData.getProductList());
        setShoppingList(newData.getShoppingList());
        setOrders(newData.getOrderList());
    }

    //================Order operations================

    public void sortOrders(SortOrderCommand.SortCriteria criteria, boolean isReversed) {
        requireNonNull(criteria);

        orders.sort(new OrderComparator((criteria)), isReversed);

    }

    /**
     * Replaces the contents of the order list with {@code orders}.
     */
    public void setOrders(List<Order> orders) {
        this.orders.setAll(orders);
        for (Order order : orders) {
            order.listenToInventory(getInventoryList());
        }
    }

    /**
     * Returns true if an order with the same identity as {@code order} exists in {@code orders}.
     */
    public boolean hasOrder(Order order) {
        requireNonNull(order);
        return orders.contains(order);
    }

    /**
     * Adds an order to orders
     * The order must not already exist in orders.
     */
    public void addOrder(Order o) {
        orders.add(o);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setOrder(Order target, Order editedOrder) {
        requireNonNull(editedOrder);

        orders.set(target, editedOrder);
    }

    /**
     * Replaces the order at {@code Index} in the list with {@code editedOrder}.
     * {@code Index} must be a valid index
     * {@code target} must exist in orders
     */
    public void setOrder(Index index, Order order) {
        requireAllNonNull(index, order);

        orders.set(index, order);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeOrder(Order key) {
        orders.remove(key);
    }

    @Override
    public ObservableList<Order> getOrderList() {
        return orders.asUnmodifiableObservableList();
    }


    //================Sale operations================

    /**
     * Adds a sale to sales
     * The sale must not already exist in sales.
     */
    public void addSale(Sale s) {
        sales.add(s);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeSale(Sale key) {
        sales.remove(key);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setSale(Sale target, Sale editedSale) {
        requireNonNull(editedSale);

        sales.set(target, editedSale);
    }

    public void setSale(List<Sale> allSale) {
        this.sales.setAll(allSale);
    }

    /**
     * Replaces the sale at {@code Index} in the list with {@code editedSale}.
     * {@code Index} must be a valid index
     * {@code target} must exist in sales
     */
    public void setSale(Index index, Sale sale) {
        requireAllNonNull(index, sale);

        sales.set(index, sale);
    }

    @Override
    public ObservableList<Sale> getSaleList() {
        return sales.asUnmodifiableObservableList();
    }


    //============Product operations==============

    /**
     * Removes a given product from product List
     */
    public void removeProduct(Product product) {
        products.remove(product);
    }
    /**
     * Adds an product to products.
     * The order must not already exist in orders.
     */
    public void addProduct(Product product) {
        products.add(product);
    }

    public void setProduct(Product originalProduct, Product editedProduct) {
        requireNonNull(editedProduct);
        products.set(originalProduct, editedProduct);
    }

    /**
     * Replaces the contents of the product list with {@code products}.
     */
    public void setProducts(List<Product> products) {
        this.products.setAll(products);
    }

    /**
     * Sorts products.
     *
     * @param category   sorting category
     * @param isReversed true if sorted in decreasing order
     */
    public void sortProducts(SortProductCommand.Category category, boolean isReversed) {
        switch (category) {
        case NAME:
            products.sort((o1, o2) -> o2.getProductName().compareTo(o1.getProductName()),
                isReversed);
            break;
        case COST:
            products.sort((o1, o2) -> o2.getIngredientCost().compareTo(o1.getIngredientCost()),
                isReversed);
            break;
        case PRICE:
            products.sort((o1, o2) -> o2.getRetailPrice().compareTo(o1.getRetailPrice()),
                isReversed);
            break;
        case PROFIT:
            products.sort((o1, o2) -> o2.getProfit().compareTo(o1.getProfit()),
                isReversed);
            break;
        default:
            break;
        }


    }

    @Override
    public ObservableList<Product> getProductList() {
        return products.asUnmodifiableObservableList();
    }

    //============Inventory operations==============

    /**

     * Adds an ingredient to the inventory list.
     * @param toAdd The ingredient to be added to the inventory list
     */
    public void addInventory(Item<Ingredient> toAdd) {
        inventory.add(toAdd);
    }

    /**
     * Removes an ingredient from the inventory list.
     * @param toRemove The ingredient to be removed from the inventory list
     */
    public void removeInventory(Item<Ingredient> toRemove) {
        inventory.remove(toRemove);
    }

    /**
     * Replaces the ingredient toEdit in the inventory list with the edited ingredient.
     * @param toEdit  the ingredient that needs to be edited
     * @param edited the edited ingredient
     */
    public void setInventory(Item<Ingredient> toEdit, Item<Ingredient> edited) {
        requireAllNonNull(toEdit, edited);
        inventory.set(toEdit, edited);
    }

    public void setInventory(List<Item<Ingredient>> replacement) {
        requireNonNull(replacement);
        inventory.setAll(replacement);
    }

    public void clearInventory(List<Item<Ingredient>> emptyList) {
        inventory.setAll(emptyList);
    }

    public boolean deductIngredient(Ingredient ingredient, double amount) {
        boolean isDeducted = false;

        for (Item<Ingredient> item : inventory) {
            if (item.getItem().equals(ingredient)) {
                Double currentAmount = item.getQuantity().getNumber();

                if (currentAmount >= amount) {
                    Double newAmount = currentAmount - amount;

                    inventory.set(item, new Item<>(item.getItem(), new Quantity(newAmount)));

                    isDeducted = true;
                } else {
                    inventory.set(item, new Item<>(item.getItem(), new Quantity(0.0)));
                }
                break;
            }
        }
        return isDeducted;
    }

    @Override
    public ObservableList<Item<Ingredient>> getInventoryList() {
        return inventory.asUnmodifiableObservableList();
    }

    //============Shopping operations==============

    /**
     * Adds an ingredient to the shopping list.
     * @param toAdd The ingredient to be added to the shopping list.
     */
    public void addShoppingList(Item<Ingredient> toAdd) {
        shoppingList.add(toAdd);
    }

    /**
     * Removes an ingredient from the shopping list.
     * @param toRemove The ingredient to be removed from the shopping list.
     */
    public void removeShoppingList(Item<Ingredient> toRemove) {
        shoppingList.remove(toRemove);
    }

    /**
     * Replaces the ingredient toEdit in the shopping list with the edited ingredient.
     *
     * @param toEdit  the ingredient that needs to be edited
     * @param edited the edited ingredient
     */
    public void setShoppingList(Item<Ingredient> toEdit, Item<Ingredient> edited) {
        requireAllNonNull(toEdit, edited);
        shoppingList.set(toEdit, edited);
    }

    public void setShoppingList(List<Item<Ingredient>> replacement) {
        requireNonNull(replacement);
        shoppingList.setAll(replacement);
    }

    public void clearShoppingList(List<Item<Ingredient>> emptyList) {
        shoppingList.setAll(emptyList);
    }

    @Override
    public ObservableList<Item<Ingredient>> getShoppingList() {
        return shoppingList.asUnmodifiableObservableList();
    }


    //// shortcut-related operations

    /**
     * Adds {@code shortcut} to shortcut list.
     * If the shortcut already exists, it overrides the old shortcut.
     */
    public void setShortcut(Shortcut shortcut) {
        requireNonNull(shortcut);

        if (shortcuts.contains(shortcut)) {
            shortcuts.set(shortcut, shortcut);
        } else {
            shortcuts.add(shortcut);
        }
    }

    /**
     * Replaces the contents of the shortcut list with {@code shortcuts}.
     */
    public void setShortcuts(List<Shortcut> shortcuts) {
        this.shortcuts.setAll(shortcuts);
    }

    /**
     * Deletes the given {@code shortcut}.
     * The shortcut must exist in order list.
     */
    public void removeShortcut(Shortcut key) {
        requireNonNull(key);
        shortcuts.remove(key);
    }

    /**
     * Returns true if a shortcut with the same name as {@code order} exists in shortcut list.
     */
    public boolean hasShortcut(Shortcut shortcut) {
        requireNonNull(shortcut);
        return shortcuts.contains(shortcut);
    }

    @Override
    public List<Shortcut> getShortcutList() {
        return shortcuts.asUnmodifiableObservableList();
    }


    //// util methods

    @Override
    public String toString() {
        return orders.asUnmodifiableObservableList().size() + " orders";
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof BakingHome // instanceof handles nulls
                && orders.equals(((BakingHome) other).orders));
    }

    @Override
    public int hashCode() {
        return orders.hashCode();
    }

}
