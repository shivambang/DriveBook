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
public class Stock extends Utils {
    
    public TabPane node(){
        TabPane pane = new TabPane();
        GridPane reg = regGrid("Stock Register");
        GridPane view = viewGrid("Stock");
        pane.getTabs().add(new Tab("View", view));
        pane.getTabs().add(new Tab("Register", reg));
        return pane;
    }
    
    private GridPane regGrid(String label){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "From", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "To", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Product ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
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
        
        ComboBox id = new ComboBox();
        ComboBox name = new ComboBox();
        addCIN(venGrid, id, name, 2, 3, 5, 3, prodid, prod, prod_map);
        
        TableView table = new TableView();
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Bill> map = new HashMap<>(bill_map);
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(fdate.getValue()) || V.getLocalDate().isAfter(tdate.getValue()))
                    set.add(K);
            });

            set.forEach((value) -> { map.remove(value); } );
            if(id.getValue() == null);
            else if(id.getValue().equals(0)){
                map.forEach((K, V) -> {
                    for(Pair<Product, ArrayList<Double>> p: V.getProd()){
                        if(V.isType())
                            table.getItems().add(new BillRow(K, p.getFirst(), p.getSecond().get(0), null));
                        else
                            table.getItems().add(new BillRow(K, p.getFirst(), null, p.getSecond().get(0)));
                    }
                });
                
            }
            else{
                map.forEach((K, V) -> {
                    for(Pair<Product, ArrayList<Double>> p: V.getProd()){
                        if(p.getFirst().getId().equals(id.getValue())){
                            if(V.isType())
                                table.getItems().add(new BillRow(K, p.getFirst(), p.getSecond().get(0), null));
                            else
                                table.getItems().add(new BillRow(K, p.getFirst(), null, p.getSecond().get(0)));
                        }
                    }
                });
            }
            
        });
        add(venGrid, done, 5, 4, HPos.RIGHT);

        TableColumn pid = new TableColumn("Bill ID");
        pid.setPrefWidth(90);
        pid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn prud = new TableColumn("Product");
        prud.setPrefWidth(400);
        prud.setCellValueFactory(new PropertyValueFactory<>("prod"));
        /*prud.setCellFactory((col) -> {
            ListView<Product> listView = new ListView<>();
            listView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
                public void updateItem(Product p, boolean empty) {
                    super.updateItem(p, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(p.getName());
                    }
                }
            });
            return new TableCell<Product, List<Product>>() {
                @Override
                public void updateItem(List<Product> prod, boolean empty) {
                    super.updateItem(prod, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        listView.getItems().setAll(prod);
                        listView.setPrefHeight(100);
                        setGraphic(listView);
                    }
                }
            };
        });*/
        TableColumn issd = new TableColumn("Issued");
        issd.setPrefWidth(150);
        issd.setCellValueFactory(new PropertyValueFactory<>("iqty"));
        TableColumn recd = new TableColumn("Recieved");
        recd.setPrefWidth(150);
        recd.setCellValueFactory(new PropertyValueFactory<>("rqty"));
        
        table.getColumns().setAll(pid, prud, issd, recd);
        venGrid.add(table, 0, 5);
        venGrid.setHalignment(table, HPos.CENTER);
        venGrid.setRowSpan(table, GridPane.REMAINING);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
    
    private GridPane viewGrid(String label){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "Product ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
                
        ComboBox id = new ComboBox();
        ComboBox name = new ComboBox();
        addCIN(venGrid, id, name, 2, 2, 5, 2, prodid, prod, prod_map);
        
        TableView table = new TableView();
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            if(id.getValue() == null);
            else if(id.getValue().equals(0)){
                prod_map.forEach((K, V) -> {
                    if(K.equals(0)) return;
                    table.getItems().add(new ProductRow(K, V.getName(), V.getStock()));
                });
                
            }
            else{
                prod_map.forEach((K, V) -> {
                    if(K.equals(0)) return;
                    if(K.equals(id.getValue())){
                        table.getItems().add(new ProductRow(K, V.getName(), V.getStock()));
                    }
                });
            }
            
        });
        add(venGrid, done, 5, 3, HPos.RIGHT);

        TableColumn pid = new TableColumn("ID");
        pid.setPrefWidth(90);
        pid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn prud = new TableColumn("Product");
        prud.setPrefWidth(400);
        prud.setCellValueFactory(new PropertyValueFactory<>("name"));
        /*prud.setCellFactory((col) -> {
            ListView<Product> listView = new ListView<>();
            listView.setCellFactory(lv -> new ListCell<Product>() {
            @Override
                public void updateItem(Product p, boolean empty) {
                    super.updateItem(p, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(p.getName());
                    }
                }
            });
            return new TableCell<Product, List<Product>>() {
                @Override
                public void updateItem(List<Product> prod, boolean empty) {
                    super.updateItem(prod, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        listView.getItems().setAll(prod);
                        listView.setPrefHeight(100);
                        setGraphic(listView);
                    }
                }
            };
        });*/
        TableColumn stk = new TableColumn("Stock");
        stk.setPrefWidth(100);
        stk.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        table.getColumns().setAll(pid, prud, stk);
        venGrid.add(table, 0, 4);
        venGrid.setHalignment(table, HPos.CENTER);
        venGrid.setRowSpan(table, GridPane.REMAINING);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

}
