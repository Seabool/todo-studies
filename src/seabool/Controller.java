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
import java.util.*;

public class Controller extends AbstractController implements Initializable {

    @FXML
    private TreeView<String> notesTreeView;
    @FXML
    private TreeView<String> filesTreeView;

    private TreeItem<String> rootItemInNotesTreeView;
    private final FilesHandler filesHandler = new FilesHandler();
    Set<StudentClass> studentClasses = new TreeSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rootItemInNotesTreeView = new TreeItem<>("Your classes");
        rootItemInNotesTreeView.setExpanded(true);
        notesTreeView.setRoot(rootItemInNotesTreeView);
    }

    private String showPopupWindow(String fxmlName) {
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
        String className = showPopupWindow("addClass.fxml");
        if(className != null){
            studentClasses.add(new StudentClass(className, filesHandler.createClassFolder(className)));
            updateNotesTreeView();
        }
    }

    public void addNoteOnClick() {
        String note = showPopupWindow("addNote.fxml");
        if(note != null){
            StudentClass studentClass = getClassByName(getSelectedCell().getValue());
            if(studentClass != null){
                studentClass.addNote(note);
                updateNotesTreeView();
            }
        }
    }

    private void updateNotesTreeView() {
        rootItemInNotesTreeView.getChildren().clear();

        for (StudentClass studentClass : studentClasses) {
            TreeItem<String> classItem = new TreeItem<>(studentClass.getClassName());
            rootItemInNotesTreeView.getChildren().add(classItem);
            if(studentClass.getNotes().size() > 0){
                for (String note : studentClass.getNotes()) {
                    TreeItem<String> noteItem = new TreeItem<>(note);
                    classItem.getChildren().add(noteItem);
                }
            }
        }
        notesTreeView.setRoot(rootItemInNotesTreeView);
    }

    private TreeItem<String> getSelectedCell() {
        return notesTreeView.getSelectionModel().getSelectedItem();
    }

    private StudentClass getClassByName(String className){
        for (StudentClass studentClass : studentClasses) {
            if (studentClass.equals(new StudentClass(className)))
                return studentClass;
        }
        return null;
    }

}
