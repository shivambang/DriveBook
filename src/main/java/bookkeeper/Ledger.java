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
import javafx.scene.control.Hyperlink;
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
public class Ledger extends Utils {
    public TabPane node(){
        TabPane pane = new TabPane();
        pane.getTabs().add(new Tab("Debtor", debitGrid()));
        pane.getTabs().add(new Tab("Creditor", creditGrid()));
        return pane;
    }
    private GridPane creditGrid(){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "Creditor", new Font("Open Sans", 30), HPos.LEFT, 5);
        addLabel(venGrid, 0, 2, "From", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "To", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
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
        addCIN(venGrid, id, name, 2, 3, 5, 3, venid, ven, ven_map);
        
        TableView table = new TableView();
        TableColumn dt = new TableColumn("Date");
        dt.setPrefWidth(100);
        dt.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn pid = new TableColumn("ID");
        pid.setPrefWidth(75);
        pid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Vendor");
        cuz.setPrefWidth(250);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        TableColumn deb = new TableColumn("Debit");
        deb.setPrefWidth(120);
        deb.setCellValueFactory(new PropertyValueFactory<>("iqty"));
        TableColumn cred = new TableColumn("Credit");
        cred.setPrefWidth(120);
        cred.setCellValueFactory(new PropertyValueFactory<>("rqty"));
        TableColumn bala = new TableColumn("Balance");
        bala.setPrefWidth(150);
        bala.setCellValueFactory(new PropertyValueFactory<>("due"));
        TableColumn par = new TableColumn("Particulars");
        par.setPrefWidth(250);
        par.setCellValueFactory(new PropertyValueFactory<>("part"));
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            ArrayList<BillRow> arr = new ArrayList();
            Map<Integer, Payment> map = new HashMap<>();
            pay_map.forEach((K, V) -> {
                if(!V.isType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.getDate().isBefore(fdate.getValue()) || V.getDate().isAfter(tdate.getValue()))
                    set.add(K);
            });
            set.forEach((value) -> { map.remove(value); } );
            table.getItems().clear();
            if(id.getValue() == null) return;
            else if((id.getValue().equals(0)));
            else{
                Vendor V = ven_map.get((int)id.getValue());
                arr.add(new BillRow(V.getId(), V.getDate(), V, null, V.getCredit(), String.format("Opening Credit")));
                arr.add(new BillRow(V.getId(), V.getDate(), V, V.getDebit(), null, String.format("Opening Debit")));
            }
            map.forEach((K, V) -> {
                if(id.getValue() == null) return;
                else if(id.getValue().equals(0))
                    arr.add(new BillRow(V.getVen().getId(), V.getDate(), V.getVen(), null, V.getNetAmt(), String.format("Pay #%d: ", K)+V.getPart()));
                else{
                    if(V.getVen().getId().equals(id.getValue()))
                        arr.add(new BillRow(V.getVen().getId(), V.getDate(), V.getVen(), null, V.getNetAmt(), String.format("Pay #%d: ", K)+V.getPart()));
                }
                        
            });
            
            Map<Integer, Bill> bmap = new HashMap<>();
            bill_map.forEach((K, V) -> {
                if(!V.isType())
                    bmap.put(K, V);
            });
            Set<Integer> bset = new HashSet<>();
            bmap.forEach((K, V) -> {
                if(V.getDate().isBefore(fdate.getValue()) || V.getDate().isAfter(tdate.getValue()))
                    bset.add(K);
            });
            bset.forEach((value) -> { bmap.remove(value); } );
            bmap.forEach((K, V) -> {
                if(id.getValue() == null) return;
                else if(id.getValue().equals(0))
                    arr.add(new BillRow(V.getVen().getId(), V.getDate(), V.getVen(), V.getNetAmt(), null, String.format("Bill #%d", K)));
                else{
                    if(V.getVen().getId().equals(id.getValue()))
                        arr.add(new BillRow(V.getVen().getId(), V.getDate(), V.getVen(), V.getNetAmt(), null, String.format("Bill #%d", K)));
                }
                        
            });

            Map<Integer, Double> bal = new HashMap<>();
            ven_map.forEach((K, V) -> { bal.put(K, 0.0); });
            arr.forEach((value) -> {
                if(value.getIqty() == null)
                    bal.merge(value.getVen().getId(), value.getRqty(), (V1, V2) -> V1 - V2);
                else
                    bal.merge(value.getVen().getId(), value.getIqty(), (V1, V2) -> V1 + V2);
                value.setDue(bal.get(value.getVen().getId()));
            });
            table.getColumns().removeAll(table.getColumns());
            if(id.getValue() == null);
            else if(id.getValue().equals(0))
                table.getColumns().setAll(dt, pid, cuz, deb, cred, par, bala);
            else
                table.getColumns().setAll(dt, par, deb, cred, bala);
            table.getItems().addAll(arr);

        });
        add(venGrid, done, 5, 4, HPos.RIGHT);

        venGrid.add(table, 0, 5);
        venGrid.setRowSpan(table, 5);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
    private GridPane debitGrid(){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "Debtor", new Font("Open Sans", 30), HPos.LEFT, 5);
        addLabel(venGrid, 0, 2, "From", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "To", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 0, 3, "Customer ID", new Font("Roboto", 18), HPos.RIGHT);
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
        addCIN(venGrid, id, name, 2, 3, 5, 3, custid, cust, cust_map);
        
        TableView table = new TableView();
        TableColumn dt = new TableColumn("Date");
        dt.setPrefWidth(100);
        dt.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn pid = new TableColumn("ID");
        pid.setPrefWidth(75);
        pid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Customer");
        cuz.setPrefWidth(250);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn deb = new TableColumn("Debit");
        deb.setPrefWidth(120);
        deb.setCellValueFactory(new PropertyValueFactory<>("iqty"));
        TableColumn cred = new TableColumn("Credit");
        cred.setPrefWidth(120);
        cred.setCellValueFactory(new PropertyValueFactory<>("rqty"));
        TableColumn par = new TableColumn("Particulars");
        par.setPrefWidth(250);
        par.setCellValueFactory(new PropertyValueFactory<>("link"));
        /*par.setCellFactory((f) -> {
            return new TableCell<Hyperlink, Hyperlink>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty){
                    super.updateItem(item, empty);
                    if(empty)
                        setGraphic(null);
                    else
                        setGraphic(item);
                }
            };
        });*/
        TableColumn bala = new TableColumn("Balance");
        bala.setPrefWidth(150);
        bala.setCellValueFactory(new PropertyValueFactory<>("due"));
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            ArrayList<BillRow> arr = new ArrayList<>();
            Map<Integer, Payment> map = new HashMap<>();
            pay_map.forEach((K, V) -> {
                if(V.isType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.getDate().isBefore(fdate.getValue()) || V.getDate().isAfter(tdate.getValue()))
                    set.add(K);
            });

            set.forEach((value) -> { map.remove(value); } );
            
            table.getItems().clear();
            if(id.getValue() == null) return;
            else if((id.getValue().equals(0)));
            else{
                arr.add(new BillRow((int)id.getValue(), cust_map.get((int)id.getValue()).getOpDate(), cust_map.get((int)id.getValue()), 
                        null, cust_map.get((int)id.getValue()).getCredit(), new Hyperlink("Opening Credit")));
                arr.add(new BillRow((int)id.getValue(), cust_map.get((int)id.getValue()).getOpDate(), cust_map.get((int)id.getValue()), 
                        cust_map.get((int)id.getValue()).getDebit(), null, new Hyperlink("Opening Debit")));
            }
            map.forEach((K, V) -> {
                for(Pair<Customer, Pair<Double, String>> p: V.getPay()){
                    Hyperlink txt = new Hyperlink(String.format("Pay #%d: ", V.getNo())+p.getSecond().getSecond());
                    txt.setId("text");
                    if(id.getValue().equals(0))
                        arr.add(new BillRow(p.getFirst().getId(), V.getDate(), p.getFirst(), null, p.getSecond().getFirst(), txt));
                    else{
                        if(p.getFirst().getId().equals(id.getValue()))
                            arr.add(new BillRow(p.getFirst().getId(), V.getDate(), p.getFirst(), null, p.getSecond().getFirst(), txt));
                    }
                        
                }
            });
            Map<Integer, Bill> bmap = new HashMap<>();
            bill_map.forEach((K, V) -> {
                if(V.isType())
                    bmap.put(K, V);
            });
            Set<Integer> bset = new HashSet<>();
            bmap.forEach((K, V) -> {
                if(V.getDate().isBefore(fdate.getValue()) || V.getDate().isAfter(tdate.getValue()))
                    bset.add(K);
            });
            bset.forEach((value) -> { bmap.remove(value); } );
            bmap.forEach((K, V) -> {
                if(id.getValue() == null) return;
                Hyperlink txt;
                if(V.getType() == 1)
                    txt = new Hyperlink(String.format("Bill #%d", V.getNo()));
                else
                    txt = new Hyperlink(String.format("Slip #%d", V.getNo()));
                txt.setOnAction((f) -> { billWin(V); });
                txt.setId("link");
                if(id.getValue().equals(0))
                    arr.add(new BillRow(V.getCust().getId(), V.getDate(), V.getCust(), V.getNetAmt(), null, txt));
                else{
                    if(V.getCust().getId().equals(id.getValue()))
                        arr.add(new BillRow(V.getCust().getId(), V.getDate(), V.getCust(), V.getNetAmt(), null, txt));
                }
                        
            });
            /*arr.sort((BillRow b1, BillRow b2) -> {
                if(b1.getDate().isBefore(b2.getDate()))
                    return -1;
                else //if(b1.getDate().isAfter(b2.getDate()))
                    return 1;
                else{
                    if(b1.getIqty() == null)
                        return 1;
                    else return -1;
                }
            });*/
            Map<Integer, Double> bal = new HashMap<>();
            cust_map.forEach((K, V) -> { bal.put(K, 0.0); });
            arr.forEach((value) -> {
                if(value.getIqty() == null)
                    bal.merge(value.getCust().getId(), value.getRqty(), (V1, V2) -> V1 + V2);
                else
                    bal.merge(value.getCust().getId(), value.getIqty(), (V1, V2) -> V1 - V2);
                value.setDue(bal.get(value.getCust().getId()));
            });
            table.getColumns().removeAll(table.getColumns());
            if(id.getValue() == null);
            else if(id.getValue().equals(0))
                table.getColumns().setAll(dt, pid, cuz, deb, cred, par, bala);
            else
                table.getColumns().setAll(dt, par, deb, cred, bala);
            table.getItems().addAll(arr);
        });
        add(venGrid, done, 5, 4, HPos.RIGHT);

        table.getColumns().setAll(dt, pid, cuz, deb, cred, par, bala);
        venGrid.add(table, 0, 5);
        venGrid.setRowSpan(table, 5);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

}
