package seabool;

import java.io.File;
import java.util.ArrayList;

public class StudentClass implements Comparable<StudentClass> {

    private final ArrayList<String> classNotes = new ArrayList<>();
    private String className;
    private File classDirectory;

    public StudentClass(String className, File classDirectory) {
        this.className = className;
        this.classDirectory = classDirectory;
    }

    public StudentClass(String className) {
        this.className = className;
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public File getClassDirectory() {
        return classDirectory;
    }

    public void setClassDirectory(File classDirectory) {
        this.classDirectory = classDirectory;
    }

    public void addNote(String note) {
        classNotes.add(note);
    }

    public ArrayList<String> getNotes() {
        return classNotes;
    }

    @Override
    public int compareTo(StudentClass obj) {
        return className.compareTo(obj.getClassName());
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(getClass() != obj.getClass()){
            return false;
        }
        return className.equals(((StudentClass) obj).className);
    }
}
