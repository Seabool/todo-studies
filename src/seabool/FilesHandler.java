package seabool;

import java.io.File;

public class FilesHandler {
    private final String mainFolderName = "Classes";

    public File createClassFolder(String className) {
        File classFolder = new File(mainFolderName + "/" + className);
        if (!classFolder.exists()) {
            classFolder.mkdirs();
        }
        return classFolder;
    }


}
