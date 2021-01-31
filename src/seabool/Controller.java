package seabool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Controller extends AbstractController implements Initializable {

    @FXML
    private TreeView<String> notesTreeView;
    @FXML
    private TreeView<String> filesTreeView;

    private TreeItem<String> rootItemInNotesTreeView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootItemInNotesTreeView = new TreeItem<>("Your classes");
        rootItemInNotesTreeView.setExpanded(true);
        notesTreeView.setRoot(rootItemInNotesTreeView);
    }

    private HashMap<String, Object> showPopupWindow(String fxmlName) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("fxml/" + fxmlName));
        PopupController popupController = new PopupController();
        loader.setController(popupController);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            Stage popupStage = new Stage();
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

    public void addClassOnClick() {
        HashMap<String, Object> resultMap = showPopupWindow("addClass.fxml");
        TreeItem<String> item = new TreeItem<>(resultMap.get("content").toString());
        rootItemInNotesTreeView.getChildren().add(item);
        updateNotesTreeView();
    }

    public void addNoteOnClick() {
        HashMap<String, Object> resultMap = showPopupWindow("addNote.fxml");
        TreeItem<String> item = new TreeItem<>(resultMap.get("content").toString());
        getSelectedCell().getChildren().add(item);
        updateNotesTreeView();
    }

    private void updateNotesTreeView() {
        notesTreeView.setRoot(rootItemInNotesTreeView);
    }

    private TreeItem<String> getSelectedCell() {
        return notesTreeView.getSelectionModel().getSelectedItem();
    }

}
