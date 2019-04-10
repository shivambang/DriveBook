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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

/**
 *
 * @author shivam
 */
public class SaleBill extends Utils {
    
    Node node;
    static int id, no;
    
    public SaleBill(){
        this.id = -1;
    }
    
    public SaleBill(int id){
        this.id = id;
    }
    public Tab node() {
        //GridPane grid = createGrid(true, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane create = createGrid("Sale Bill");
        //grid.add(create, 1, 1);
        if(id == -1)
            return new Tab("Create", create);
        else
            return new Tab("Edit", create);
    }
    
    
    private GridPane createGrid(String label){
        
        GridPane grid = createGrid(false, 3, 4, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 5);
        GridPane empGrid = createGrid(false, 6, 4, 16.5, 5.5, 22, 16.5, 5.5, 34, 25, 25, 25, 25);
        int bid;
        if(id == -1){
            if(billid.size() > 0)
                bid = billid.get(billid.size() - 1) + 1;
            else
                bid = 1;
        } else {
            if(bill_map.get(id).getType() == 2)
                empGrid.setDisable(true);
            bid = id;
        }
        no = 0;
        
        addLabel(empGrid, 0, 0, "SNo", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 0, "Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 1, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 1, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 2, "Customer ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label l = new Label();
        l.setFont(new Font("Roboto", 18));
        add(empGrid, l, 2, 0, HPos.LEFT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(150, 25);
        listen(date, LocalDate.now());
        if(id != -1)
            date.setValue(bill_map.get(id).getLocalDate());
        add(empGrid, date, 5, 0, HPos.LEFT);
        
        addIN(empGrid, 2, 1, 5, 1, 2, 2, 5, 2, 1);
        if(id != -1){
            set(empGrid);
            no = bill_map.get(id).getNo();
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
                            for(Map.Entry<Integer, Bill> V: bill_map.entrySet()){
                                if(V.getValue().getType() == 1 && V.getValue().getEmp().getId().equals(newValue)){
                                    if(V.getValue().getLocalDate().getMonth().equals(date.getValue().getMonth())
                                            && V.getValue().getLocalDate().getYear() == date.getValue().getYear())
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
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            ProdBox.type = 1;
            ProdBox.no = no;
            ProdBox.bid = bid;
            ProdBox.vid = -1;
            empGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null);
                    else if(i.getId().equals("EID"))
                        ProdBox.eid = (int)(((ComboBox)i).getValue());
                    else if(i.getId().equals("CID"))
                        ProdBox.cid = (int)(((ComboBox)i).getValue());
                }
            });
            
            if(ProdBox.cid == 0) return;
            if(ProdBox.eid == 0) return;
            
            Map<Integer, Double> due = dues(date.getValue().minusDays(cust_map.get(ProdBox.cid).getDays()), ProdBox.cid, true);
            if(due.containsKey(ProdBox.cid)){
                if(cust_map.get(ProdBox.cid).getDebit() - cust_map.get(ProdBox.cid).getCredit() + 
                        due.get(ProdBox.cid) > cust_map.get(ProdBox.cid).getDue()){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer #" + ProdBox.cid + " has exceeded his max due!\nDo you wish to Continue?", 
                                            ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if(alert.getResult() == ButtonType.NO)  return;
                }
            }
            
            //Label
            Label lbl = new Label();
            lbl.setFont(new Font("Open Sans", 20));
            lbl.setText(emp_map.get(ProdBox.eid).getName() + "'s   " + cust_map.get(ProdBox.cid).getName() + "   #" + ProdBox.cid);

            //Label
            Label dlbl = new Label();
            dlbl.setFont(new Font("Open Sans", 20));
            dlbl.setText(date.getValue().toString());
            
            ProdBox.date = date.getValue();
            venGrid.getChildren().removeAll(venGrid.getChildren());
            addLabel(venGrid, 0, 0, String.format("Sale Bill #%d", no), new Font("Open Sans", 30), HPos.LEFT, 5);
            add(venGrid, lbl, 0, 1, HPos.LEFT);
            venGrid.setColumnSpan(lbl, GridPane.REMAINING);
            add(venGrid, dlbl, 5, 1, HPos.RIGHT);

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
        add(empGrid, done, 5, 3, HPos.RIGHT);
        
        venGrid.add(empGrid, 0, 1);
        venGrid.setRowSpan(empGrid, 4);
        venGrid.setColumnSpan(empGrid, GridPane.REMAINING);
        createSlip(venGrid);
        grid.add(venGrid, 1, 1);

        return grid;
    }
    
    public void createSlip(GridPane venGrid){
        addLabel(venGrid, 0, 5, "Sale Slip", new Font("Open Sans", 30), HPos.LEFT, 5);
        GridPane empGrid = createGrid(false, 6, 4, 16.5, 5.5, 22, 16.5, 5.5, 34, 25, 25, 25, 25);
        int bid;
        if(id == -1){
            if(billid.size() > 0)
                bid = billid.get(billid.size() - 1) + 1;
            else
                bid = 1;
        } else {
            if(bill_map.get(id).getType() == 1)
                empGrid.setDisable(true);
            bid = id;
        }
        no = 0;
        
        addLabel(empGrid, 0, 0, "SNo", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 0, "Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 1, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 1, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 0, 2, "Customer ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(empGrid, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label l = new Label();
        l.setFont(new Font("Roboto", 18));
        add(empGrid, l, 2, 0, HPos.LEFT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(150, 25);
        listen(date, LocalDate.now());
        if(id != -1)
            date.setValue(bill_map.get(id).getLocalDate());
        add(empGrid, date, 5, 0, HPos.LEFT);
        
        addIN(empGrid, 2, 1, 5, 1, 2, 2, 5, 2, 1);
        if(id != -1){
            set(empGrid);
            no = bill_map.get(id).getNo();
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
                            for(Map.Entry<Integer, Bill> V: bill_map.entrySet()){
                                if(V.getValue().getType() == 2 && V.getValue().getEmp().getId().equals(newValue)){
                                    if(V.getValue().getLocalDate().getMonth().equals(date.getValue().getMonth())
                                            && V.getValue().getLocalDate().getYear() == date.getValue().getYear())
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
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            ProdBox.type = 2;
            ProdBox.no = no;
            ProdBox.bid = bid;
            ProdBox.vid = -1;
            empGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null);
                    else if(i.getId().equals("EID"))
                        ProdBox.eid = (int)(((ComboBox)i).getValue());
                    else if(i.getId().equals("CID"))
                        ProdBox.cid = (int)(((ComboBox)i).getValue());
                }
            });
            
            if(ProdBox.cid == 0) return;
            if(ProdBox.eid == 0) return;
            
            Map<Integer, Double> due = dues(date.getValue().minusDays(cust_map.get(ProdBox.cid).getDays()), ProdBox.cid, true);
            if(due.containsKey(ProdBox.cid)){
                if(cust_map.get(ProdBox.cid).getDebit() - cust_map.get(ProdBox.cid).getCredit() + 
                        due.get(ProdBox.cid) > cust_map.get(ProdBox.cid).getDue()){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer #" + ProdBox.cid + " has exceeded his max due!\nDo you wish to Continue?", 
                                            ButtonType.YES, ButtonType.NO);
                    alert.showAndWait();

                    if(alert.getResult() == ButtonType.NO)  return;
                }
            }
            
            //Label
            Label lbl = new Label();
            lbl.setFont(new Font("Open Sans", 20));
            lbl.setText(emp_map.get(ProdBox.eid).getName() + "'s   " + cust_map.get(ProdBox.cid).getName() + "   #" + ProdBox.cid);

            //Label
            Label dlbl = new Label();
            dlbl.setFont(new Font("Open Sans", 20));
            dlbl.setText(date.getValue().toString());
            
            ProdBox.date = date.getValue();
            venGrid.getChildren().removeAll(venGrid.getChildren());
            addLabel(venGrid, 0, 0, String.format("Sale Slip #%d", no), new Font("Open Sans", 30), HPos.LEFT, 5);
            add(venGrid, lbl, 0, 1, HPos.LEFT);
            venGrid.setColumnSpan(lbl, GridPane.REMAINING);
            add(venGrid, dlbl, 5, 1, HPos.RIGHT);
            
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
        add(empGrid, done, 5, 3, HPos.RIGHT);
        
        venGrid.add(empGrid, 0, 6);
        venGrid.setRowSpan(empGrid, 4);
        venGrid.setColumnSpan(empGrid, GridPane.REMAINING);
        
    }
    
    private void set(GridPane empGrid){
            ArrayList<Customer> arr = new ArrayList<>();
            ArrayList<Integer> iarr = new ArrayList<>();
            cust_map.forEach((K, V) -> {
                if(K == 0)  return;
                if(V.getEmp().getId().equals(bill_map.get(id).getEmp().getId())){
                    iarr.add(V.getId());
                    arr.add(V);
                }
            });
            empGrid.getChildren().forEach((i) -> {
                if(i.getClass().equals(ComboBox.class)){
                    if(i.getId() == null);
                    else if(i.getId().equals("EID")){
                        ((ComboBox) i).setValue(bill_map.get(id).getEmp().getId());
                        ((ComboBox) i).setDisable(true);
                    }
                    else if(i.getId().equals("ENAME"))
                        ((ComboBox) i).setDisable(true);
                    else if(i.getId().equals("CID")){
                        ((ComboBox) i).setValue(bill_map.get(id).getCust().getId());
                        ((ComboBox) i).setItems(FXCollections.observableArrayList(iarr));
                    }
                    else if(i.getId().equals("CNAME"))
                        ((ComboBox) i).setItems(FXCollections.observableArrayList(arr));
                }
            });
    }
}
