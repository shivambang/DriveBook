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
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
public class SaleDues extends Utils{
    
    public TabPane node() {
        TabPane pane = new TabPane();
        GridPane view = viewGrid(pane);
        
        pane.getTabs().add(new Tab("View", view));
        return pane;
    }
    
    private GridPane viewGrid(TabPane tpane){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "Payment Dues", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "As Of", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        //addLabel(venGrid, 0, 4, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        //addLabel(venGrid, 3, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(150, 25);
        listen(date, LocalDate.of(2999, 12, 31));
        add(venGrid, date, 2, 2, HPos.LEFT);

        //Label
        Label lbl = new Label();
        lbl.setFont(new Font("Roboto", 18));
        add(venGrid, lbl, 3, 2, HPos.RIGHT);
        venGrid.setColumnSpan(lbl, GridPane.REMAINING);
        
        ComboBox eid = new ComboBox();
        ComboBox ename = new ComboBox();
        addCIN(venGrid, eid, ename, 2, 3, 5, 3, empid, emp, emp_map);

        //ComboBox cid = new ComboBox();
        //ComboBox cname = new ComboBox();
        //addCIN(venGrid, cid, cname, 2, 3, 5, 3, custid, cust, cust_map);
        
        TableView table = new TableView();
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            if(eid.getValue() == null) return;
            ArrayList<Double> sum = new ArrayList<>();
            /*Map<Integer, ArrayList<Double>> due = dues(date.getValue(), (int)eid.getValue(), 0);
            table.getItems().clear();
            due.forEach((K, V) -> {
                table.getItems().add(new BillRow(K, cust_map.get(K), cust_map.get(K).getCity(), 
                        V.get(0), V.get(1), V.get(2), V.get(3), V.get(4)));
                V.forEach((k) -> { sum.add(k); });
            });*/
            Set<Integer> cset = new HashSet<>();
            cust_map.keySet().forEach((K) -> {cset.add(K);});
            cset.remove(0);
            if(!eid.getValue().equals(0))
            cust_map.forEach((K, V) -> {
                if(K != 0 && !V.getEmpId().equals((int)eid.getValue()))
                    cset.remove(K);
            });
            table.getItems().clear();
            Map<Integer, Double> dr = dr(date.getValue(), (int)eid.getValue(), false);
            Map<Integer, Double> cr = cr(date.getValue(), (int)eid.getValue(), false);
            cset.forEach((K) -> {
                Double due = cust_map.get(K).getDebit() - cust_map.get(K).getCredit() + 
                        (dr.get(K) == null ? 0.0 : dr.get(K)) - (cr.get(K) == null ? 0.0 : cr.get(K));
                table.getItems().add(new BillRow(K, cust_map.get(K), cust_map.get(K).city(),
                    cust_map.get(K).getDebit() - cust_map.get(K).getCredit(), cr.get(K), dr.get(K), due, null));
                sum.add(due);
            });
            Double s = 0.0;
            s = sum.stream().map((d) -> d).reduce(s, (accumulator, _item) -> accumulator + _item);
            
            //New Tab
            dueTab(tpane, (int)eid.getValue(), date.getValue(), s, table);
            //
            
        });
        add(venGrid, done, 5, 4, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(50);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Customer");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn cty = new TableColumn("City");
        cty.setPrefWidth(120);
        cty.setCellValueFactory(new PropertyValueFactory<>("city"));
        //14
        TableColumn d1 = new TableColumn("Opening");
        d1.setPrefWidth(100);
        d1.setCellValueFactory(new PropertyValueFactory<>("due1"));
        //Less
        TableColumn d2 = new TableColumn("Credit");
        d2.setPrefWidth(100);
        d2.setCellValueFactory(new PropertyValueFactory<>("due2"));
        TableColumn d3 = new TableColumn("Debit");
        d3.setPrefWidth(100);
        d3.setCellValueFactory(new PropertyValueFactory<>("due3"));
        TableColumn d4 = new TableColumn("Closing");
        d4.setPrefWidth(100);
        d4.setCellValueFactory(new PropertyValueFactory<>("due4"));
        TableColumn d5 = new TableColumn("90 or More");
        d5.setPrefWidth(150);
        d5.setCellValueFactory(new PropertyValueFactory<>("due5"));
        
        table.getColumns().setAll(id, cuz, d1, d2, d3, d4);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
    
    private void dueTab(TabPane tpane, Integer eid, LocalDate date, double s, TableView table){
        GridPane gp = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venG = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venG.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venG, 0, 0, "Dues for "+emp_map.get(eid).getName(), new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venG, 0, 1, "As Of "+date, new Font("Roboto", 18), HPos.LEFT);
        addLabel(venG, 3, 1, "Total Dues: "+String.format("%.2f", s), new Font("Roboto", 18), HPos.RIGHT, 3);
        venG.add(table, 0, 2);
        venG.setRowSpan(table, 7);
        venG.setColumnSpan(table, GridPane.REMAINING);
                Button print = new Button("Print");
        print.setFont(new Font("Roboto", 18));
        print.setOnAction((func) -> {
            try {
                Print.printLandNode(table, print.getScene().getWindow());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(venG, print, 5, 9, HPos.RIGHT);
        gp.add(venG, 1, 1);
        Tab node = new Tab("Dues", gp);
        tpane.getTabs().add(node);
        tpane.getSelectionModel().select(node);

    }
}
