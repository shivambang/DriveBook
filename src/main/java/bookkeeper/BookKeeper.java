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

import static bookkeeper.Data.bill_map;
import static bookkeeper.Data.city_map;
import static bookkeeper.Data.cust_map;
import static bookkeeper.Data.emp_map;
import static bookkeeper.Data.pay_map;
import static bookkeeper.Data.prod_map;
import static bookkeeper.Data.readData;
import static bookkeeper.Data.update;
import static bookkeeper.Data.updateData;
import static bookkeeper.Data.ven_map;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
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
        stage.setTitle("DriveBook");
        stage.show();
    }
    @FXML
    private Button submit;
    public TextField user;
    public PasswordField pass;
    public Label label;
    
    @FXML
    private void submit(ActionEvent event) throws IOException{
        Stage stage = (Stage)(submit.getScene().getWindow());
        if(user.getText().equalsIgnoreCase("admin") && pass.getText().equalsIgnoreCase("admin")) {
            try{
                fc = new FirebaseController();
                stage.hide();
                initData();
                (new MyBooks()).start(stage);

            } catch(IOException ex){
                label.setText("Could Not Connect!");
                FadeTransition fader = new FadeTransition(Duration.seconds(2.5), label);
                fader.setFromValue(1);
                fader.setToValue(0);
                new SequentialTransition(label, fader).play();
                System.out.println(ex);
            } catch (TimeoutException ex) {
                stage.close();
            }
        } else {
            label.setText("Try Again!");
            FadeTransition fader = new FadeTransition(Duration.seconds(2.5), label);
            fader.setFromValue(1);
            fader.setToValue(0);
            new SequentialTransition(label, fader).play();
        }
    }
    
    public void initData() throws TimeoutException  {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception{
                readData();
                updateProgress(0.2, 1);
                try{
                    update(Customer.class, cust_map);
                    updateProgress(0.3, 1);
                    update(City.class, city_map);
                    updateProgress(0.4, 1);
                    update(Employee.class, emp_map);
                    updateProgress(0.5, 1);
                    update(Product.class, prod_map);
                    updateProgress(0.6, 1);
                    update(Bill.class, bill_map);
                    updateProgress(0.7, 1);
                    update(Vendor.class, ven_map);
                    updateProgress(0.8, 1);
                    update(Payment.class, pay_map);
                    updateProgress(0.9, 1);
                }   catch(TimeoutException | InterruptedException | ExecutionException ex){
                    System.out.println(ex);
                    updateMessage("Update Failed! No Connection.");
                    cancel();
                    return null;
                }
                updateMessage("Finished");
                updateProgress(1, 1);
                return null;
            }
            @Override
            protected void running(){
                updateMessage("Updating... Please Wait");
            }
        };
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.titleProperty().bind(task.titleProperty());
        alert.contentTextProperty().bind(task.messageProperty());

        ProgressIndicator pIndicator = new ProgressIndicator();
        pIndicator.progressProperty().bind(task.progressProperty());
        alert.setGraphic(pIndicator);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.getDialogPane().lookupButton(ButtonType.OK)
                .disableProperty().bind(task.runningProperty());
        
        alert.getDialogPane().cursorProperty().bind(
                Bindings.when(task.runningProperty())
                    .then(Cursor.WAIT)
                    .otherwise(Cursor.DEFAULT)
        );
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
        alert.showAndWait();
        if(task.isCancelled()) throw new TimeoutException();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    
    }    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}