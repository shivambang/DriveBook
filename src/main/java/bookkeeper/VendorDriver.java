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
import static bookkeeper.Utils.add;
import static bookkeeper.Utils.addLabel;
import static bookkeeper.Utils.createGrid;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 * @author shivam
 */
public class VendorDriver extends Utils {
    
    int id;

    public VendorDriver() {
        this.id = -1;
    }
    public VendorDriver(int id) {
        this.id = id;
    }
    
    
    public TabPane node(){
        TabPane pane = new TabPane();
        pane.getTabs().add(new Tab("Create", create()));
        pane.getTabs().add(new Tab("Edit", venviewGrid("Edit", pane)));
        return pane;
    }

    public GridPane create(){
        int cid;
        if(id == -1){
            if(venid.size() > 0)
                cid = venid.get(venid.size() - 1) + 1;
            else
                cid = 1;
        } else cid = id;

        GridPane create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane add = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));
        addLabel(add, 0, 0, "New Vendor", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(add, 0, 3, "Opening Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 2, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 2, 2, String.valueOf(cid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(add, 0, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 5, "Credit", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 5, "Debit", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label label = new Label();
        label.setFont(new Font("Open Sans", 20));
        add(add, label, 3, 2, HPos.CENTER);
        add.setColumnSpan(label, 3);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(200, 25);
        listen(date, LocalDate.now());
        add(add, date, 2, 3, HPos.LEFT);
        
        //Name
        TextField name = new TextField();
        name.setText("VENDOR #".concat(String.valueOf(ven.get(ven.size() - 1).getId() + 1)));
        add(add, name, 2, 4, HPos.CENTER);
        add.setColumnSpan(name, 2);
        
        //Credit
        TextField credit = new TextField();
        credit.setMaxSize(200, 25);
        credit.setText("0");
        add(add, credit, 2, 5, HPos.LEFT);
        
        //Debit
        TextField debit = new TextField();
        debit.setMaxSize(200, 25);
        debit.setText("0");
        add(add, debit, 5, 5, HPos.LEFT);
        
        if(id != -1){
            name.setText(ven_map.get(cid).getName());
            debit.setText(String.format("%.2f", ven_map.get(cid).getDebit()));
            credit.setText(String.format("%.2f", ven_map.get(cid).getCredit()));
        }
        //Button
        Button done = new Button("Create");
        done.setFont(new Font("Roboto", 18));
        
        done.setOnAction((func) -> {
            try{
            Vendor v = new Vendor(cid, date.getValue(),
                    name.getText(), Double.valueOf(credit.getText()), Double.valueOf(debit.getText()));
            ven_map.putAndUpdate(cid, v);
            
            //

            Data.writeData();
            Data.setData();
            label.setText("Vendor created successfully!");
            } catch(TimeoutException ex) {
                label.setText("NO Internet!");
            } catch (Exception ex) {
                Logger.getLogger(CustomerDriver.class.getName()).log(Level.SEVERE, null, ex);
                label.setText("Vendor NOT created!");
            }
            fade(label);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.pVen.fire())).play();
        });
        add(add, done, 5, 7, HPos.RIGHT);
        
        create.add(add, 1, 1);
        return create;
    }
    
    private GridPane venviewGrid(String label, TabPane tab){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        
        TableView table = new TableView();
        //Button
        Button done = new Button("Update");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            ven_map.forEach((K, V) -> {
                if(K == 0)  return;
                table.getItems().add(new BillRow(K, V, V.getDebit()));
                
            });
        });
        add(venGrid, done, 5, 0, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Vendor");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        
        table.getColumns().setAll(id, cuz);
        venGrid.add(table, 0, 1);
        venGrid.setRowSpan(table, 7);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
 
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            BillRow r = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            VendorDriver cd = new VendorDriver(r.getId());
            Tab node = new Tab("Edit", cd.create());
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
        });
        add(venGrid, edit, 0, 9, HPos.LEFT);

        grid.add(venGrid, 1, 1);
        return grid;

    }

}
