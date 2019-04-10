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

import static bookkeeper.Data.*;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 * @author shivam
 */
public class VendorPayment extends Utils {
    static int id, vid;
    static LocalDate paydate;

    public VendorPayment() {
        this.vid = -1;
    }
    
    public VendorPayment(int vid) {
        this.vid = vid;
    }
    
    public Tab node(){
        GridPane create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane add = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));
        addLabel(add, 0, 0, "Payment", new Font("Open Sans", 30), HPos.LEFT, 3);
        
        int pid;
        if(vid == -1){
            if(payid.size() > 0)
                pid = payid.get(payid.size() - 1) + 1;
            else
                pid = 1;
        } else pid = vid;
        
        GridPane empGrid = createGrid(false, 6, 3, 16.5, 5.5, 22, 16.5, 5.5, 34, 35, 35, 30);
        addLabel(empGrid, 0, 0, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 2, 0, String.valueOf(pid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(empGrid, 3, 0, "Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 1, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 1, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 2, "Amount", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 2, "Particulars", new Font("Roboto", 18), HPos.RIGHT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(200, 25);
        listen(date, LocalDate.now());
        add(empGrid, date, 5, 0, HPos.LEFT);
        
        ComboBox id = new ComboBox();
        ComboBox name = new ComboBox();
        addCIN(empGrid, id, name, 2, 1, 5, 1, venid, ven, ven_map);

        //Amount
        TextField amt = new TextField();
        amt.setMaxSize(200, 25);
        amt.setText("0");
        add(empGrid, amt, 2, 2, HPos.LEFT);
        
        //Particul
        TextField part = new TextField();
        part.setMaxSize(200, 25);
        part.setText("CASH");
        add(empGrid, part, 5, 2, HPos.LEFT);
        if(vid != -1){
            date.setValue(pay_map.get(vid).getDate());
            id.setValue(vid);
            name.setValue(pay_map.get(vid).getVen());
            amt.setText(pay_map.get(vid).getNetAmt().toString());
        }
        
        //Button
        Button ok = new Button("Done");
        ok.setFont(new Font("Roboto", 18));
        ok.setOnAction((func) -> {
            if(id.getValue() == null);
            else if(id.getValue().equals(0));
            else
                
            try {
                pay_map.putAndUpdate(pid, new Payment(pid, date.getValue(), ven_map.get((int)id.getValue()), Double.valueOf(amt.getText()), part.getText()));
                Data.writeData();
                Data.setData();
                //label.setText("Payment Created Successfully!");
            } catch(TimeoutException ex) {
                        //label.setText("NO Internet!");
            } catch (Exception ex) {
                        Logger.getLogger(ProductDriver.class.getName()).log(Level.SEVERE, null, ex);
                        //label.setText("Product NOT created!");
            }
            
            //fade(label);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.pPay.fire())).play();

        });
        add(empGrid, ok, 5, 3, HPos.RIGHT);
        add.add(empGrid, 0, 2);
        add.setRowSpan(empGrid, 3);
        add.setColumnSpan(empGrid, GridPane.REMAINING);
        empGrid.setDisable(false);
        
        create.add(add, 1, 1);
        return new Tab("Create", create);
    }
}
