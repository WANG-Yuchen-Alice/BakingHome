package duke.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import duke.commons.core.LogsCenter;
import duke.commons.core.index.Index;
import duke.logic.Logic;
import duke.logic.command.CommandResult;
import duke.logic.command.exceptions.CommandException;
import duke.logic.command.product.ShowProductCommandResult;
import duke.logic.parser.commons.AutoCompleter;
import duke.logic.parser.exceptions.ParseException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The Main Window.
 * Provides the basic application layout containing a popup bar, a text field, a side bar,
 * and space where different pages can be displayed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private final Logger logger = LogsCenter.getLogger(getClass());
    private Stage primaryStage;

    private Logic logic;

    private OrderPage orderPage;
    private ProductPage productPage;
    private SalePage salePage;
    private InventoryPage inventoryPage;
    private ShoppingPage shoppingPage;

    private List<String> inputHistory = new ArrayList<>();
    private int historyIndex = inputHistory.size();

    //Popup box
    @FXML
    private HBox popUp;
    @FXML
    private Label popUpLabel;
    @FXML
    private JFXButton popUpButton;
    @FXML
    private JFXTextField userInput;

    //Main page
    @FXML
    private Label currentPage;
    @FXML
    private AnchorPane pagePane;

    //Sidebar
    @FXML
    private JFXButton productButton;
    @FXML
    private JFXButton orderButton;
    @FXML
    private JFXButton inventoryButton;
    @FXML
    private JFXButton shoppingButton;
    @FXML
    private JFXButton saleButton;


    /**
     * Creates the Main Window.
     *
     * @param primaryStage The stage to display MainWindow on.
     * @param logic        Logic component
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);
        logger.info("Initializing MainWindow");
        this.primaryStage = primaryStage;
        this.logic = logic;

        this.popUp.setVisible(false);

        this.userInput.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                autocomplete();
                event.consume();
            }
        });
    }

    /**
     * Returns the stage that MainWindow is displayed on.
     */
    Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Creates Order, Product and Sales pages that fill up the placeholder window.
     */
    void fillInnerParts() {
        orderPage = new OrderPage(logic.getFilteredOrderList());
        productPage = new ProductPage(logic.getFilteredProductList());
        salePage = new SalePage(logic.getFilteredSaleList());
        inventoryPage = new InventoryPage(logic.getFilteredInventoryList());
        shoppingPage = new ShoppingPage(logic.getFilteredShoppingList());
        setAllPageAnchor(orderPage.getRoot(), productPage.getRoot(), salePage.getRoot(),
                inventoryPage.getRoot(), shoppingPage.getRoot());
        logger.fine("All pages filled in MainWindow");
    }

    void show() {
        primaryStage.show();
    }


    @FXML
    private void handleUserInput() {
        popUp.setVisible(false);
        String input = userInput.getText();

        inputHistory.add(input);
        historyIndex = inputHistory.size();

        try {
            CommandResult commandResult = logic.execute(input);
            showPage(commandResult);

            showMessagePopUp(commandResult.getFeedbackToUser());
        } catch (CommandException | ParseException | IllegalArgumentException e) {
            showErrorPopUp(e.getMessage());
        }
        userInput.clear();
    }

    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
        case UP:
            keyEvent.consume();
            if (historyIndex > 0) {
                loadPreviousText();
            }
            break;
        case DOWN:
            keyEvent.consume();
            if (historyIndex < (inputHistory.size() - 1)) {
                loadNextText();
            }
            break;
        case TAB:
            keyEvent.consume();
            autocomplete();
            break;
        case ESCAPE:
            keyEvent.consume();
            popUp.setVisible(false);
            break;
        default:
            // let JavaFx handle the keypress
            break;
        }
    }

    private void loadPreviousText() {
        historyIndex--;
        loadText();
    }

    private void loadNextText() {
        historyIndex++;
        loadText();
    }

    private void loadText() {
        String history = inputHistory.get(historyIndex);
        userInput.setText(history);
        userInput.setFocusTraversable(false);
        userInput.positionCaret(history.length());
    }

    //@@author liujiajun
    private void autocomplete() {
        if (logic.isAutoCompletable(new AutoCompleter.UserInputState(
                userInput.getText(),
                userInput.getCaretPosition()
        ))) {
            AutoCompleter.UserInputState newState = logic.complete();
            this.userInput.setText(newState.userInputString);
            this.userInput.positionCaret(newState.caretPosition);
        }
    }
    //@@author

    /**
     * Hides the pop up bar.
     */
    @FXML
    private void handleOk() {
        popUp.setVisible(false);
    }

    @FXML
    private void handleShowRecipe() {
        showProductPage();
    }

    @FXML
    private void handleShowOrder() {
        showOrderPage();
    }

    @FXML
    private void handleShowInventory() {
        showInventoryPage();
    }

    @FXML
    private void handleShowShopping() {
        showShoppingPage();
    }

    @FXML
    private void handleShowSale() {
        showSalePage();
    }


    /**
     * Shows the pop up bar displaying a {@code message}.
     */
    private void showMessagePopUp(String message) {
        popUpLabel.setText(message);
        popUpButton.getStyleClass().clear();
        popUpButton.getStyleClass().add("message-popup");
        popUp.getStyleClass().clear();
        popUp.getStyleClass().add("message-popup");
        popUp.setVisible(true);
    }

    /**
     * Shows the pop up bar displaying a {@code errorMessage} signifying an error.
     */
    private void showErrorPopUp(String errorMessage) {
        popUpLabel.setText(errorMessage);
        popUpButton.getStyleClass().clear();
        popUpButton.getStyleClass().add("error-popup");
        popUp.getStyleClass().clear();
        popUp.getStyleClass().add("error-popup");
        popUp.setVisible(true);
    }

    private void showPage(CommandResult commandResult) {
        CommandResult.DisplayedPage toDisplay = commandResult.getDisplayedPage();
        switch (toDisplay) {
        case SALE:
            showSalePage();
            break;
        case ORDER:
            showOrderPage();
            break;
        case PRODUCT:
            if (commandResult instanceof ShowProductCommandResult) {
                showProductDetail(((ShowProductCommandResult) commandResult).getIndex());
            } else {
                showProductList();
            }
            break;
        case INVENTORY:
            showInventoryPage();
            break;
        case SHOPPING:
            showShoppingPage();
            break;
        default:
            break;
        }
    }

    private void showOrderPage() {

        pagePane.getChildren().clear();
        pagePane.getChildren().add(orderPage.getRoot());

        productButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.RAISED);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        shoppingButton.setButtonType(JFXButton.ButtonType.FLAT);
        saleButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Orders");
    }

    private void showProductPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(productPage.getRoot());

        productButton.setButtonType(JFXButton.ButtonType.RAISED);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        shoppingButton.setButtonType(JFXButton.ButtonType.FLAT);
        saleButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Products");
    }

    private void showProductList() {
        showProductPage();
        productPage.showProductList();
    }

    private void showProductDetail(Index productIndex) {
        showProductPage();
        productPage.showProductDetail(productIndex);
    }

    private void showInventoryPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(inventoryPage.getRoot());

        productButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.RAISED);
        shoppingButton.setButtonType(JFXButton.ButtonType.FLAT);
        saleButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Inventory");
    }

    private void showShoppingPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(shoppingPage.getRoot());

        productButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        shoppingButton.setButtonType(JFXButton.ButtonType.RAISED);
        saleButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Shopping List");
    }

    private void showSalePage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(salePage.getRoot());

        productButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        shoppingButton.setButtonType(JFXButton.ButtonType.FLAT);
        saleButton.setButtonType(JFXButton.ButtonType.RAISED);

        currentPage.setText("Sales");
    }

    void disableInput() {
        userInput.setDisable(true);
    }

    private void setAllPageAnchor(AnchorPane... pages) {
        for (AnchorPane page : pages) {
            AnchorPane.setLeftAnchor(page, 0.0);
            AnchorPane.setRightAnchor(page, 0.0);
            AnchorPane.setTopAnchor(page, 0.0);
            AnchorPane.setBottomAnchor(page, 4.0);
        }
    }
}
