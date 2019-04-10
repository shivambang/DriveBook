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
import java.util.Map;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 *
 * @author shivam
 */
public class PurchaseDues extends Utils{
    
    public TabPane node() {
        TabPane pane = new TabPane();
        GridPane view = viewGrid("Payment Dues");
        
        pane.getTabs().add(new Tab("View", view));
        return pane;
    }
    
    private GridPane viewGrid(String label){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "As Of", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        //addLabel(venGrid, 0, 4, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        //addLabel(venGrid, 3, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(150, 25);
        listen(date, LocalDate.of(2999, 12, 31));
        add(venGrid, date, 2, 2, HPos.LEFT);

        ComboBox cid = new ComboBox();
        ComboBox cname = new ComboBox();
        addCIN(venGrid, cid, cname, 2, 3, 5, 3, venid, ven, ven_map);
        
        TableView table = new TableView();
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            if(cid.getValue() == null) return;
            Map<Integer, Double> due = vdues(date.getValue(), (int)cid.getValue());
            table.getItems().clear();
            due.forEach((K, V) -> {
                table.getItems().add(new BillRow(K, ven_map.get(K), V));
            });
        });
        add(venGrid, done, 5, 4, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Vendor");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        TableColumn amt = new TableColumn("Dues");
        amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("due"));
        
        table.getColumns().setAll(id, cuz, amt);
        venGrid.add(table, 0, 5);
        venGrid.setRowSpan(table, GridPane.REMAINING);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

}
