package seabool;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

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

    public void removeFolderWithContent(Path path) throws IOException {
        if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path)) {
                for (Path entry : entries) {
                    removeFolderWithContent(entry);
                }
            }
        }
        Files.delete(path);
    }

}
