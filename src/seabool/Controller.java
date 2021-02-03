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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class Controller extends AbstractController implements Initializable {

    private final FilesHandler filesHandler = new FilesHandler();
    private final Desktop desktop = Desktop.getDesktop();
    @FXML
    private TreeView<String> notesTreeView;
    @FXML
    private TreeView<String> filesTreeView;
    private TreeItem<String> rootItemInNotesTreeView;
    private TreeItem<String> rootItemInFilesTreeView;
    private XMLHandler xmlHandler;
    private Set<StudentClass> studentClasses = new TreeSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            xmlHandler = new XMLHandler();
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            showPopupWindow("alert.fxml", "Problem with XML handler.");
        }

        rootItemInNotesTreeView = new TreeItem<>("Your classes");
        rootItemInNotesTreeView.setExpanded(true);
        notesTreeView.setRoot(rootItemInNotesTreeView);

        rootItemInFilesTreeView = new TreeItem<>("Your files");
        rootItemInFilesTreeView.setExpanded(true);
        filesTreeView.setRoot(rootItemInFilesTreeView);

        studentClasses = xmlHandler.initializeClasses();
        updateNotesTreeView();
    }

    private String showPopupWindow(String fxmlName, String alertText) {
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
            popupController.setLabel(alertText);
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
        String className = showPopupWindow("addClass.fxml", "Class name");
        if (className != null) {
            studentClasses.add(new StudentClass(className, filesHandler.createClassFolder(className)));
            try {
                xmlHandler.addToXML(className);
            } catch (TransformerException e) {
                showPopupWindow("alert.fxml", "Problem with adding class to XML file.");
            }
            updateNotesTreeView();
        }
    }

    public void addNoteOnClick() {
        String note = showPopupWindow("addNote.fxml", "Note");
        if (note != null) {
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if (studentClass != null) {
                studentClass.addNote(note);
                try {
                    xmlHandler.addNoteToClass(studentClass.getClassName(), note);
                } catch (TransformerException e) {
                    showPopupWindow("alert.fxml", "Problem with adding note to XML file.");
                }
                updateNotesTreeView();
            }
        }
    }

    private void updateNotesTreeView() {
        clearTreeView(rootItemInNotesTreeView);

        for (StudentClass studentClass : studentClasses) {
            TreeItem<String> classItem = new TreeItem<>(studentClass.getClassName());
            rootItemInNotesTreeView.getChildren().add(classItem);
            if (studentClass.getNotes().size() > 0) {
                for (String note : studentClass.getNotes()) {
                    TreeItem<String> noteItem = new TreeItem<>(note);
                    classItem.getChildren().add(noteItem);
                }
            }
        }
        notesTreeView.setRoot(rootItemInNotesTreeView);
    }

    private TreeItem<String> getClassSelectedCell() {
        return notesTreeView.getSelectionModel().getSelectedItem();
    }

    private TreeItem<String> getFileSelectedCell() {
        return filesTreeView.getSelectionModel().getSelectedItem();
    }

    private StudentClass getClassByName(String className) {
        for (StudentClass studentClass : studentClasses) {
            if (studentClass.equals(new StudentClass(className)))
                return studentClass;
        }
        return null;
    }

    public void classesTreeViewOnClick() {
        updateFilesTreeView();
    }

    private void updateFilesTreeView() {
        clearTreeView(rootItemInFilesTreeView);
        if (getClassSelectedCell() != null) {
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if (studentClass != null) {
                if (studentClass.getClassDirectory() != null) {
                    for (final File fileEntry : Objects.requireNonNull(studentClass.getClassDirectory().listFiles())) {
                        TreeItem<String> noteItem = new TreeItem<>(fileEntry.getName());
                        rootItemInFilesTreeView.getChildren().add(noteItem);
                    }
                }
            }
        }
    }

    public void openFileOnClick() {
        File file = getFileFromFilesTreeViewBySelection();
        if (file != null) {
            try {
                desktop.open(file);
            } catch (IOException e) {
                showPopupWindow("alert.fxml", "Problem with opening file.");
            }
        }
    }

    public void deleteFileOnClick() {
        if (filesHandler.deleteFile(getFileFromFilesTreeViewBySelection())) {
            updateFilesTreeView();
        }
    }

    private File getFileFromFilesTreeViewBySelection() {
        StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
        if (studentClass != null) {
            File file = new File("Classes/" + studentClass.getClassName() + "/" + getFileSelectedCell().getValue());
            if (file.exists()) {
                return file;
            }
        }
        return null;
    }

    public void copyToClipboardOnClick() {
        if (getClassSelectedCell() != null) {
            String textFromCell = getClassSelectedCell().getValue();
            if (!textFromCell.equals("")) {
                StringSelection textToClipboard = new StringSelection(getClassSelectedCell().getValue());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(textToClipboard, null);
            }
        }
    }

    public void openFolderOnClick() {
        if (getClassSelectedCell() != null) {
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if (studentClass != null && studentClass.getClassDirectory() != null) {
                try {
                    desktop.open(studentClass.getClassDirectory());
                } catch (IOException e) {
                    showPopupWindow("alert.fxml", "Problem with opening folder.");
                }
            }
        }
    }

    public void addFilesOnClick() {
        File selectedFile = filesHandler.getFileFromFileChooser();
        if (selectedFile != null && getClassSelectedCell() != null) {
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if (studentClass != null) {
                filesHandler.copyFile(selectedFile.toString(), studentClass.getClassDirectory().toString() + "/" + selectedFile.getName());
                updateFilesTreeView();
            }
        }
    }

    private void clearTreeView(TreeItem<String> root) {
        root.getChildren().clear();
    }
}
