package seabool;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PopupController extends AbstractController implements Initializable {

    @FXML
    private TextField contentField;
    @FXML
    private Button addButton;
    @FXML
    private Stage stage = null;

    private final HashMap<String, Object> result = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        addButton.setOnAction((event) -> {
            result.clear();
            result.put("className", contentField.getText());
            closeStage();
        });

    }

    public HashMap<String, Object> getResult() {
        return this.result;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void closeStage() {
        if (stage != null) {
            stage.close();
        }
    }

}