package seabool;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FilesHandler {
    private final String mainFolderName = "Classes";

    public File createClassFolder(String className) {
        File classFolder = new File(mainFolderName + "/" + className);
        if (!classFolder.exists()) {
            classFolder.mkdirs();
        }
        return classFolder;
    }

    public boolean deleteFile(File file) {
        if (file != null) {
            return file.delete();
        }
        return false;
    }

    public void copyFile(String from, String to) {
        Path toPath = Paths.get(to);
        Path fromPath = Paths.get(from);

        try {
            Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFileFromFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Copy a file");
        return fileChooser.showOpenDialog(null);
    }

}
