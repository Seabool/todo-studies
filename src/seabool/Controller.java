package seabool;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

public class Controller extends AbstractController implements Initializable {

    @FXML
    private TreeView<String> notesTreeView;
    @FXML
    private TreeView<String> filesTreeView;

    private TreeItem<String> rootItemInNotesTreeView;
    private TreeItem<String> rootItemInFilesTreeView;

    private final FilesHandler filesHandler = new FilesHandler();
    private XMLHandler xmlHandler;

    private Set<StudentClass> studentClasses = new TreeSet<>();

    private final Desktop desktop = Desktop.getDesktop();


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            xmlHandler = new XMLHandler();
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            //System.out.println("Problem with XML handler.");
            e.printStackTrace();
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
            try {
                xmlHandler.addToXML(className);
            } catch (TransformerException e) {
                System.out.println("Problem with adding class to XML file.");
            }
            updateNotesTreeView();
        }
    }

    public void addNoteOnClick() {
        String note = showPopupWindow("addNote.fxml");
        if(note != null){
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if(studentClass != null){
                studentClass.addNote(note);
                try {
                    xmlHandler.addNoteToClass(studentClass.getClassName(), note);
                } catch (TransformerException e) {
                    System.out.println("Problem with adding note to XML file.");
                }
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
                    TreeItem<String> noteItem = new TreeItem<>("• " + note);
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

    private StudentClass getClassByName(String className){
        for (StudentClass studentClass : studentClasses) {
            if (studentClass.equals(new StudentClass(className)))
                return studentClass;
        }
        return null;
    }

    public void classesTreeViewOnClick() {
        updateFilesTreeView();
    }

    private void updateFilesTreeView(){
        rootItemInFilesTreeView.getChildren().clear();
        if(getClassSelectedCell() != null){
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if(studentClass != null){
                if(studentClass.getClassDirectory() != null){
                    for (final File fileEntry : studentClass.getClassDirectory().listFiles()) {
                        TreeItem<String> noteItem = new TreeItem<>(fileEntry.getName());
                        rootItemInFilesTreeView.getChildren().add(noteItem);
                    }
                }
            }
        }
    }

    public void openFileOnClick(){
        File file = getFileFromFilesTreeViewBySelection();
        if(file != null){
            try {
                desktop.open(file);
            } catch (IOException e) {
                System.out.println("Problem with opening file.");
            }
        }
    }

    public void deleteFileOnClick() {
        File file = getFileFromFilesTreeViewBySelection();
        if(file != null){
            file.delete();
            updateFilesTreeView();
        }
    }

    private File getFileFromFilesTreeViewBySelection(){
        StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
        if(studentClass != null){
            File file = new File("Classes/" + studentClass.getClassName() + "/" + getFileSelectedCell().getValue());
            if(file.exists()) {
                return file;
            }
        }
        return null;
    }

    public void copyToClipboardOnClick() {
        if(getClassSelectedCell() != null){
            String textFromCell = getClassSelectedCell().getValue();
            if(!textFromCell.equals("")){
                StringSelection textToClipboard = new StringSelection(getClassSelectedCell().getValue());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(textToClipboard, null);
            }
        }
    }

    public void openFolderOnClick() {
        if(getClassSelectedCell() != null){
            StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());
            if(studentClass != null){
                if(studentClass.getClassDirectory() != null){
                    try {
                        desktop.open(studentClass.getClassDirectory());
                    } catch (IOException e) {
                        System.out.println("Problem with opening folder.");
                    }
                }
            }
        }
    }

    public void addFilesOnClick() {

        Path to;
        Path from;
        File selectedFile;

        FileChooser fc = new FileChooser();
        fc.setTitle("Copy a file");
        selectedFile = fc.showOpenDialog(null);

        if (selectedFile != null) {
            if(getClassSelectedCell() != null){
                StudentClass studentClass = getClassByName(getClassSelectedCell().getValue());

                from = Paths.get(selectedFile.toURI());
                to = Paths.get(studentClass.getClassDirectory().toString() + "/" + selectedFile.getName());
                try {
                    Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
                    updateFilesTreeView();
                } catch (IOException e) {
                    System.out.println("Problem with copying file.");
                }
            }
        }
    }
}
