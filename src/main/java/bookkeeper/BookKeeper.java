/* 
 * The MIT License
 *
 * Copyright 2018 Shivam Bang.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package bookkeeper;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author shivam
 */
public class BookKeeper extends Application implements Initializable {
    
    protected static FirebaseController fc;
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/bookkeeper.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/css/mainStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    @FXML
    private Button submit;
    public TextField user;
    public PasswordField pass;
    public Label label;
    
    @FXML
    private void submit(ActionEvent event) throws IOException{
        if(user.getText().equalsIgnoreCase("admin") && pass.getText().equalsIgnoreCase("admin")) {
            try{
                fc = new FirebaseController();
                label.setText("Success!");
                Stage stage = (Stage)(submit.getScene().getWindow());
                stage.hide();
                Parent root = FXMLLoader.load(getClass().getResource("/fxml/myBooks.fxml"));
                Scene scene = new Scene(root);
                scene.getStylesheets().add(this.getClass().getResource("/css/myBooks.css").toExternalForm());
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }
            catch(Exception ex){
                label.setText("Could Not Connect!");
                FadeTransition fader = new FadeTransition(Duration.seconds(2.5), label);
                fader.setFromValue(1);
                fader.setToValue(0);
                new SequentialTransition(label, fader).play();
                System.out.println(ex);
            }
        } else {
            label.setText("Try Again!");
            FadeTransition fader = new FadeTransition(Duration.seconds(2.5), label);
            fader.setFromValue(1);
            fader.setToValue(0);
            new SequentialTransition(label, fader).play();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    
    }    

    public static void main(String[] args) {
        launch(args);
    }
    
}
