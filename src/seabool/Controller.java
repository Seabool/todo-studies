package seabool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller extends AbstractController implements Initializable {

    @FXML private TreeView<String> notesTreeView;
    @FXML private TreeView<String> filesTreeView;
    @FXML private Button addClassButton;
    @FXML private Button addNoteButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        TreeItem<String> rootItem = new TreeItem<>("Your classes");

        rootItem.setExpanded(true);

        notesTreeView.setRoot(rootItem);

        addClassButton.setOnAction((event) -> {
            HashMap<String, Object> resultMap = showPopupWindow("addClass.fxml");
            TreeItem<String> item = new TreeItem<>(resultMap.get("className").toString());
            rootItem.getChildren().add(item);
            notesTreeView.setRoot(rootItem);
        });

        addNoteButton.setOnAction((event) -> {
            HashMap<String, Object> resultMap = showPopupWindow("addNote.fxml");
            TreeItem<String> item = new TreeItem<>(resultMap.get("className").toString());
            getLeadSelect().getChildren().add(item);
            notesTreeView.setRoot(rootItem);
        });

    }

    private final Node rootIcon = new ImageView(
            new Image(getClass().getResourceAsStream("iconClass.png"))
    );

    private HashMap<String, Object> showPopupWindow(String fxmlName) {
        HashMap<String, Object> resultMap = new HashMap<>();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlName));
        // initializing the controller
        PopupController popupController = new PopupController();
        loader.setController(popupController);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            // this is the popup stage
            Stage popupStage = new Stage();
            // Giving the popup controller access to the popup stage (to allow the controller to close the stage)
            popupController.setStage(popupStage);
            if (this.main != null) {
                popupStage.initOwner(main.getPrimaryStage());
            }
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return popupController.getResult();
    }

    private TreeItem<String> getLeadSelect() {
        return notesTreeView.getSelectionModel().getSelectedItem();
    }

    public void notesTreeViewOnClick(MouseEvent mouseEvent) {
        System.out.println(getLeadSelect());
    }
}
