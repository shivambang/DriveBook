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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
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
public class PurchaseReport extends Utils{
    
    public TabPane node() {
        TabPane pane = new TabPane();
        GridPane view = viewGrid("Purchase Report", pane);
        
        pane.getTabs().add(new Tab("View", view));
        return pane;
    }
    
    private GridPane viewGrid(String label, TabPane pane){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "From", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "To", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 4, "Product ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //From Date
        DatePicker fdate = new DatePicker();
        fdate.setPrefSize(150, 25);
        listen(fdate, LocalDate.of(2000, 1, 1));
        add(venGrid, fdate, 2, 2, HPos.LEFT);

        //To Date
        DatePicker tdate = new DatePicker();
        tdate.setPrefSize(150, 25);
        listen(tdate, LocalDate.of(2999, 12, 31));
        add(venGrid, tdate, 5, 2, HPos.LEFT);

        ComboBox vid = new ComboBox();
        ComboBox vname = new ComboBox();
        addCIN(venGrid, vid, vname, 2, 3, 5, 3, venid, ven, ven_map);
        
        ComboBox pid = new ComboBox();
        ComboBox pname = new ComboBox();
        addCIN(venGrid, pid, pname, 2, 4, 5, 4, prodid, prod, prod_map);
        
        TableView table = new TableView();
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Bill> map = new HashMap<>();
            bill_map.forEach((K, V) -> {
                if(!V.boolType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.localDate().isBefore(fdate.getValue()) || V.localDate().isAfter(tdate.getValue()))
                    set.add(K);
            });

            if(vid.getValue() == null)  return;
            else if(vid.getValue().equals(0));
            else{
                map.forEach((K, V) -> {
                    if(!V.getVenId().equals(vid.getValue()))
                        set.add(K);
                });
            }
            set.forEach((value) -> { map.remove(value); } );
            table.getItems().clear();
            Map<Integer, Map<Integer, Double>> tab = new HashMap<>();
            map.forEach((K, V) -> {
                int id = K;
                Vendor c = V.ven();
                if(!tab.containsKey(c.getId()))
                    tab.put(c.getId(), new HashMap<>());
                for(Pair<Product, ArrayList<Double>> p: V.getProd()){
                    if(pid.getValue() == null)  return;
                    else if(pid.getValue().equals(0))
                        tab.get(c.getId()).merge(p.getFirst().getId(), p.getSecond().get(0), (V1, V2) -> V1 + V2);
                    else if(pid.getValue().equals(p.getFirst().getId()))
                        tab.get(c.getId()).merge(p.getFirst().getId(), p.getSecond().get(0), (V1, V2) -> V1 + V2);
                }
            });
            
            tab.forEach((K, V) -> {
                V.forEach((_K, _V) -> {table.getItems().add(new BillRow(K, ven_map.get(K), prod_map.get(_K), _V));});
            });
            pane.getTabs().add(new Tab("Report", view(table)));
            pane.getSelectionModel().select(1);
            pane.requestFocus();
        });
        add(venGrid, done, 5, 5, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Vendor");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        TableColumn prud = new TableColumn("Products");
        prud.setPrefWidth(300);
        prud.setCellValueFactory(new PropertyValueFactory<>("prod"));
        TableColumn amt = new TableColumn("Purchase");
        amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("sale"));
        
        table.getColumns().setAll(id, cuz, prud, amt);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

    private GridPane view(TableView table){
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "Report", new Font("Open Sans", 30), HPos.LEFT, 3);
        
        ScrollPane pane = new ScrollPane();
        pane.setContent(table);
        venGrid.add(pane, 0, 2);
        venGrid.setRowSpan(pane, GridPane.REMAINING);
        venGrid.setColumnSpan(pane, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;
        
    }
}
