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
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 *
 * @author shivam
 */
public class PurchaseBill extends Utils{
    
    Node node;
    int pid;
    public PurchaseBill(){
        pid = -1;
    }
    
    public PurchaseBill(int id){
        this.pid = id;
    }
    
    public Tab node() {
        //GridPane grid = createGrid(true, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane create = createGrid("Purchase Billing");
        //grid.add(create, 1, 1);
        if(pid == -1)
            return new Tab("Create", create);
        else
            return new Tab("Edit", create);
    }
    
    
    private GridPane createGrid(String label){
        
        GridPane grid = createGrid(false, 3, 4, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 5);
   
        int bid;
        if(pid == -1){
            if(billid.size() > 0)
                bid = billid.get(billid.size() - 1) + 1;
            else
                bid = 1;
        } else bid = pid;
     
        GridPane empGrid = createGrid(false, 6, 3, 16.5, 5.5, 22, 16.5, 5.5, 34, 35, 35, 30);
        addLabel(empGrid, 0, 0, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 2, 0, String.valueOf(bid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(empGrid, 3, 0, "Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 1, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 1, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(150, 25);
        listen(date, LocalDate.now());
        add(empGrid, date, 5, 0, HPos.LEFT);

        ComboBox id = new ComboBox();
        ComboBox name = new ComboBox();
        addCIN(empGrid, id, name, 2, 1, 5, 1, venid, ven, ven_map);
        if(pid != -1){
            date.setValue(pay_map.get(pid).getDate());
            id.setValue(pid);
        }
     
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            ProdBox.bid = bid;
            ProdBox.cid = -1;
            if(id.getValue() == null) return;
            else
                ProdBox.vid = (int)id.getValue();
            
            if(ProdBox.vid == 0) return;
            
            //Label
            Label lbl = new Label();
            lbl.setFont(new Font("Open Sans", 20));
            lbl.setText(ven_map.get(ProdBox.vid).getName() + "   #" + ProdBox.vid);
            add(venGrid, lbl, 0, 1, HPos.LEFT);
            venGrid.setColumnSpan(lbl, GridPane.REMAINING);

            //Label
            Label dlbl = new Label();
            dlbl.setFont(new Font("Open Sans", 20));
            dlbl.setText(date.getValue().toString());
            add(venGrid, dlbl, 5, 1, HPos.RIGHT);
            
            ProdBox.date = date.getValue();
            venGrid.getChildren().remove(empGrid);

            try {
                this.node = FXMLLoader.load(getClass().getResource("/fxml/prodBox.fxml"));
            } catch (IOException ex) {
                Logger.getLogger(SaleBill.class.getName()).log(Level.SEVERE, null, ex);
            }

            venGrid.add(node, 0, 2);
            venGrid.setRowSpan(node, GridPane.REMAINING);
            venGrid.setColumnSpan(node, GridPane.REMAINING);
            venGrid.requestFocus();
        });
        add(empGrid, done, 5, 2, HPos.RIGHT);
        
        venGrid.add(empGrid, 0, 2);
        venGrid.setRowSpan(empGrid, 4);
        venGrid.setColumnSpan(empGrid, GridPane.REMAINING);
        grid.add(venGrid, 1, 1);

        return grid;
    }
    
}
