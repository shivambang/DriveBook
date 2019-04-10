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
public class SaleReport extends Utils{
    
    public TabPane node() {
        TabPane pane = new TabPane();
        GridPane view = viewGrid("Sales Report", pane);
        
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
        addLabel(venGrid, 0, 3, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 4, "Customer ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 5, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 5, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 6, "Product ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 6, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
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
        
        addIN(venGrid, 2, 3, 5, 3, 2, 4, 5, 4, 1);
        ComboBox vid = new ComboBox();
        ComboBox vname = new ComboBox();
        ComboBox pid = new ComboBox();
        ComboBox pname = new ComboBox();
        
        
        addPIN(venGrid, vid, vname, pid, pname, 2, 5, 5, 5, 2, 6, 5, 6, 1);
        
        TableView table = new TableView();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Bill> map = new HashMap<>();
            bill_map.forEach((K, V) -> {
                if(V.boolType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.localDate().isBefore(fdate.getValue()) || V.localDate().isAfter(tdate.getValue()))
                    set.add(K);
            });
            venGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null)
                        return;
                    if(i.getId().equals("EID")){
                        if(((ComboBox)i).getValue() == null);
                        else if(((ComboBox)i).getValue().equals(0));
                        else{
                            map.forEach((K, V) -> {
                                if(V.getEmpId() != (int)(((ComboBox)i).getValue()))
                                    set.add(K);
                            });
                        }
                    }

                    if(i.getId().equals("CID")){
                        if(((ComboBox)i).getValue() == null);
                        else if(((ComboBox)i).getValue().equals(0));
                        else{
                            map.forEach((K, V) -> {
                                if(V.getCustId() != (int)(((ComboBox)i).getValue()))
                                    set.add(K);
                            });
                        }
                    }
                        //MyBooks.cid = (int)(((ComboBox)i).getValue());
                }
            });
            set.forEach((value) -> { map.remove(value); } );
            table.getItems().clear();
            Map<Integer, Pair<Double, Double>> sale = new HashMap<>();
            //F: Vol
            //S: Amt
            map.forEach((K, V) -> {
                int id = K;
                Bill b = V;
                for(Pair<Product, ArrayList<Double>> p: b.getProd()){
                    if(pid.getValue() == null);
                    else if(vid.getValue().equals(0)){
                        sale.merge(p.getFirst().getId(), new Pair(p.getSecond().get(0), p.getSecond().get(6)),
                                (V1, V2) -> {
                                    return new Pair(V1.getFirst()+V2.getFirst(), V1.getSecond()+V2.getSecond());
                                });
                    }
                    else if(vid.getValue().equals(p.getFirst().getVenId())){
                        if(pid.getValue().equals(0) || pid.getValue().equals(p.getFirst().getId())){
                        sale.merge(p.getFirst().getId(), new Pair(p.getSecond().get(0), p.getSecond().get(6)),
                                (V1, V2) -> {
                                    return new Pair(V1.getFirst()+V2.getFirst(), V1.getSecond()+V2.getSecond());
                                });
                        }
                    }
                }
            });
            ArrayList<Double> arr = new ArrayList<>();
            sale.forEach((K, V) -> {
                    table.getItems().add(new BillRow(K, prod_map.get(K), prod_map.get(K).getVol(), V.getFirst(),
                            prod_map.get(K).getVol()*V.getFirst(), V.getSecond()));
                    arr.add(prod_map.get(K).getVol()*V.getFirst());
            });
            Double vsale = arr.stream().reduce(0.0, (a, b)->a+b);
            Pair<Double, Double> psale = sale.values().stream().reduce(new Pair(0.0, 0.0), 
                    (a, b) -> {return new Pair(a.getFirst()+b.getFirst(), a.getSecond()+b.getSecond());});
            pane.getTabs().add(new Tab("Report", view(table, psale.getSecond(), psale.getFirst(), vsale)));
            pane.getSelectionModel().select(1);
            pane.requestFocus();
        });
        add(venGrid, done, 5, 7, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        //id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn prud = new TableColumn("Product");
        //prud.setPrefWidth(300);
        prud.setCellValueFactory(new PropertyValueFactory<>("prod"));
        TableColumn sz = new TableColumn("Size");
        //sz.setPrefWidth(150);
        sz.setCellValueFactory(new PropertyValueFactory<>("iqty"));
        TableColumn amt = new TableColumn("Quantity");
        //amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("sale"));
        TableColumn vol = new TableColumn("Volume");
        //vol.setPrefWidth(150);
        vol.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn pr = new TableColumn("Amount");
        //vol.setPrefWidth(150);
        pr.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        table.getColumns().setAll(id, prud, sz, amt, vol, pr);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

    private GridPane view(TableView table, Double psum, Double qsum, Double vsum){
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "Report", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 1, String.format("Total Quantity: %.2f" , qsum)
                +String.format("    Volume: %.2f" , vsum)
                +String.format("    Amount: %.2f" , psum), new Font("Open Sans", 18), HPos.LEFT, GridPane.REMAINING);
        venGrid.add(table, 0, 2);
        venGrid.setRowSpan(table, GridPane.REMAINING);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;
        
    }
}
