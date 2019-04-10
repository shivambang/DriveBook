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
import java.util.Map;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
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
public class CustomerPayment extends Utils {
    static int eid, pid, id, no;
    static LocalDate paydate;

    public CustomerPayment() {
        id = -1;
    }
    
    public CustomerPayment(int id) {
        this.id = id;
    }
    
    
    public Tab node(){
        GridPane create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane add = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));
        addLabel(add, 0, 0, "Payment", new Font("Open Sans", 30), HPos.LEFT, 3);
        if(id == -1){
            if(payid.size() > 0)
                pid = payid.get(payid.size() - 1) + 1;
            else
                pid = 1;
        } else pid = id;
        no = 0;
        
        GridPane empGrid = createGrid(false, 6, 3, 16.5, 5.5, 22, 16.5, 5.5, 34, 35, 35, 30);
        addLabel(empGrid, 0, 0, "SNo", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 0, "Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 1, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 1, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label l = new Label();
        l.setFont(new Font("Roboto", 18));
        add(empGrid, l, 2, 0, HPos.LEFT);

        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(200, 25);
        listen(date, LocalDate.now());
        if(id != -1)
            date.setValue(pay_map.get(id).localDate());
        add(empGrid, date, 5, 0, HPos.LEFT);
        
        addEIN(empGrid, 2, 1, 5, 1);
        if(id != -1){
            empGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null);
                    else if(i.getId().equals("EID")){
                        ((ComboBox) i).setValue(pay_map.get(id).getEmp().getId());
                        ((ComboBox) i).setDisable(true);
                    }
                    else if(i.getId().equals("ENAME"))
                        ((ComboBox) i).setDisable(true);
                }
            });
            no = pay_map.get(id).getNo();
            l.setText(String.valueOf(no));
        }
        empGrid.getChildren().forEach((i) -> {
            if(i.getClass().equals(ComboBox.class)){
                if(i.getId() == null);
                else if(i.getId().equals("EID")){
                    ((ComboBox) i).getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                        if(newValue == null);
                        else if(newValue.equals(0));
                        else{
                            int counter = 1;
                            for(Map.Entry<Integer, Payment> V: pay_map.entrySet()){
                                if(V.getValue().getEmp().getId().equals(newValue)){
                                    if(V.getValue().localDate().getMonth().equals(date.getValue().getMonth())
                                            && V.getValue().localDate().getYear() == date.getValue().getYear())
                                        counter++;
                                }
                            }
                            l.setText(String.valueOf(counter));
                            no = counter;
                        }
                    });
                }
            }
        });

        //Button
        Button ok = new Button("Done");
        ok.setFont(new Font("Roboto", 18));
        ok.setOnAction((func) -> {
            empGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null);
                    else if(i.getId().equals("EID"))
                        eid = (int)(((ComboBox)i).getValue());
                }
            });
            paydate = date.getValue();
            try{
                if(eid != 0){
                    adcust(add);
                    add.getChildren().remove(empGrid);
                }
            } catch(Exception ex){
                
            }
        });
        add(empGrid, ok, 5, 2, HPos.RIGHT);
        add.add(empGrid, 0, 2);
        add.setRowSpan(empGrid, 3);
        add.setColumnSpan(empGrid, GridPane.REMAINING);
        empGrid.setDisable(false);
        
        create.add(add, 1, 1);
        return new Tab("Create", create);
    }
    
    private void adcust(GridPane add){

        //Label
        Label label = new Label();
        label.setFont(new Font("Open Sans", 20));
        add(add, label, 0, 1, HPos.LEFT);
        add.setColumnSpan(label, 3);

        addLabel(add, 0, 2, "Customer ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 3, "Amount", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 3, "Particulars", new Font("Roboto", 18), HPos.RIGHT);
        
        ArrayList<Integer> iarr = new ArrayList<>();
        ArrayList<Customer> arr = new ArrayList<>();
        cust_map.forEach((K, V) -> {
            if(K == 0) return;
            if(V.getEmp().getId().equals(eid)){
                iarr.add(K);
                arr.add(V);
            }
        });
        ComboBox cid = new ComboBox();
        ComboBox cname = new ComboBox();
        addCIN(add, cid, cname, 2, 2, 5, 2, iarr, arr, cust_map);
        cid.requestFocus();
        
        //Amount
        TextField amt = new TextField();
        amt.setMaxSize(200, 25);
        amt.setText("0");
        add(add, amt, 2, 3, HPos.LEFT);
        
        //Particul
        TextField part = new TextField();
        part.setMaxSize(200, 25);
        part.setText("CASH");
        add(add, part, 5, 3, HPos.LEFT);

        TableView table = new TableView();

        //Button
        Button save = new Button("Save");
        save.setFont(new Font("Roboto", 18));
        save.setOnAction((func) -> {
            if(cid.getValue() == null);
            else if(cid.getValue().equals(0));
            else{
                table.getItems().add(new BillRow((int)cid.getValue(), cust_map.get((int)cid.getValue()), Double.valueOf(amt.getText()), part.getText()));
            }
            cid.requestFocus();
        });
        add(add, save, 5, 4, HPos.RIGHT);
        
        TableColumn cuid = new TableColumn("ID");
        cuid.setPrefWidth(75);
        cuid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Customer");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn py = new TableColumn("Amount");
        py.setPrefWidth(200);
        py.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn par = new TableColumn("Particulars");
        par.setPrefWidth(250);
        par.setCellValueFactory(new PropertyValueFactory<>("part"));

        table.getColumns().setAll(cuid, cuz, py, par);
        ScrollPane scroll = new ScrollPane();
        scroll.setContent(table);
        add.add(scroll, 0, 5);
        add.setRowSpan(scroll, 4);
        add.setColumnSpan(scroll, GridPane.REMAINING);
        
        if(id != -1){
            for(Pair<Customer, Pair<Double, String>> p: pay_map.get(id).getPay()){
                table.getItems().add(new BillRow(p.getFirst().getId(), p.getFirst(), p.getSecond().getFirst(), p.getSecond().getSecond()));
            }
        }
        
        //Button
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        
        edit.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getSelectedIndex());
            cid.setValue(row.getId());
            cname.setValue(row.getCust());
            amt.setText(row.getAmt().toString());
            part.setText(row.getPart());
            table.getItems().remove(table.getSelectionModel().getFocusedIndex());
        });
        add(add, edit, 0, 9, HPos.LEFT);
        
        
        //Delete
        Button del = new Button("Delete");
        del.setFont(new Font("Roboto", 18));
        
        del.setOnAction((func) -> {
            table.getItems().remove(table.getSelectionModel().getSelectedIndex());
        });
        add(add, del, 1, 9, HPos.LEFT);
        add.setColumnSpan(del, 2);
        
        //Button
        Button done = new Button("Create");
        done.setFont(new Font("Roboto", 20));
        
        done.setOnAction((func) -> {
            ArrayList<Pair<Customer, Pair<Double, String>>> list = new ArrayList<>();
            Double net = 0.0;
            table.getItems().forEach((r) -> {
                BillRow row = (BillRow) r;
                list.add(new Pair(row.getCust(), new Pair(row.getAmt(), row.getPart())));
            });
            for(Pair<Customer, Pair<Double, String>> p: list)
                net += p.getSecond().getFirst();
            
            try {
                pay_map.putAndUpdate(pid, new Payment(pid, no, paydate, emp_map.get(eid), list, net));
                Data.writeData();
                Data.setData();
                label.setText("Payment Created Successfully!");
            } catch(TimeoutException ex) {
                label.setText("NO Internet!");
            } catch (Exception ex) {
                label.setText("Payment NOT Created!");
                Logger.getLogger(CustomerPayment.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            fade(label);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.sPay.fire())).play();
        });
        add(add, done, 5, 9, HPos.RIGHT);

    }
}
