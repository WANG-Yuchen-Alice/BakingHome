package duke.ui;

import com.jfoenix.controls.JFXButton;
import duke.commons.core.LogsCenter;
import duke.logic.Logic;
import duke.logic.command.CommandResult;
import duke.logic.command.exceptions.CommandException;
import duke.logic.parser.exceptions.ParseException;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private TextField userInput;

    //Main page
    @FXML
    private Label currentPage;
    @FXML
    private AnchorPane pagePane;

    //Sidebar
    @FXML
    private JFXButton recipeButton;
    @FXML
    private JFXButton orderButton;
    @FXML
    private JFXButton inventoryButton;
    @FXML
    private JFXButton salesButton;

    /**
     * Creates the Main Window.
     *
     * @param primaryStage The stage to display MainWindow on.
     * @param logic        Logic component
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        this.primaryStage = primaryStage;
        this.logic = logic;

        setUpKeyEvent();

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
        salePage = new SalePage();
        inventoryPage = new InventoryPage(logic.getFilteredInventoryList());
        setAllPageAnchor(orderPage.getRoot(), productPage.getRoot(), salePage.getRoot(), inventoryPage.getRoot());
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
            showPage(commandResult.getDisplayedPage());
            showMessagePopUp(commandResult.getFeedbackToUser());
        } catch (CommandException | ParseException | IllegalArgumentException e) {
            showErrorPopUp(e.getMessage());
        }
        userInput.clear();
    }

    /**
     * Sets UP key to show previous input, and sets DOWN key to the next input.
     */
    @FXML
    private void setUpKeyEvent() {
        userInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.UP)) {
                    if (historyIndex > 0) {
                        historyIndex--;
                        userInput.setText(inputHistory.get(historyIndex));
                        userInput.setFocusTraversable(false);
                    }
                }
                if(event.getCode().equals(KeyCode.DOWN)) {
                    if (historyIndex < (inputHistory.size() - 1)) {
                        historyIndex++;
                        userInput.setText(inputHistory.get(historyIndex));
                        userInput.setFocusTraversable(false);
                    }
                }
            }
        });
    }

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
    private void handleShowSale() {
        showSalesPage();
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

    private void showPage(CommandResult.DisplayedPage toDisplay) {
        switch (toDisplay) {
            case SALE:
                showSalesPage();
                break;
            case ORDER:
                showOrderPage();
                break;
            case PRODUCT:
                showProductPage();
                break;
            case INVENTORY:
                showInventoryPage();
                break;
            default:
                break;
        }
    }

    private void showOrderPage() {

        pagePane.getChildren().clear();
        pagePane.getChildren().add(orderPage.getRoot());

        recipeButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.RAISED);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        salesButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Orders");
    }

    private void showProductPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(productPage.getRoot());

        recipeButton.setButtonType(JFXButton.ButtonType.RAISED);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        salesButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Products");
    }

    private void showInventoryPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(inventoryPage.getRoot());

        recipeButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.RAISED);
        salesButton.setButtonType(JFXButton.ButtonType.FLAT);

        currentPage.setText("Inventory");
    }

    private void showSalesPage() {
        pagePane.getChildren().clear();
        pagePane.getChildren().add(salePage.getRoot());

        recipeButton.setButtonType(JFXButton.ButtonType.FLAT);
        orderButton.setButtonType(JFXButton.ButtonType.FLAT);
        inventoryButton.setButtonType(JFXButton.ButtonType.FLAT);
        salesButton.setButtonType(JFXButton.ButtonType.RAISED);

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
