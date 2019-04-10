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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author shivam
 */
public class CustomerDriver extends Utils {
    
    int id;

    public CustomerDriver() {
        this.id = -1;
    }

    public CustomerDriver(int id) {
        this.id = id;
    }
    
    public TabPane node(){
        TabPane pane = new TabPane();
        pane.getTabs().add(create());
        pane.getTabs().add(new Tab("Customer", custviewGrid("All Customers", pane)));
        return pane;
    }
    
    public Tab create(){

        GridPane create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane add = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));
        addLabel(add, 0, 0, "New Customer", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(add, 0, 3, "Opening Date", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 6, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 6, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 2, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 4, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 5, "City", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 7, "Credit", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 7, "Debit", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 8, "Max Due", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 8, "after", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 5, 8, "days", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label label = new Label();
        label.setFont(new Font("Open Sans", 20));
        add(add, label, 3, 2, HPos.CENTER);
        add.setColumnSpan(label, 3);
        
        //Label
        Label ilabel = new Label();
        ilabel.setFont(new Font("Open Sans", 20));
        add(add, ilabel, 2, 2, HPos.LEFT);
        
        //Date
        DatePicker date = new DatePicker();
        date.setPrefSize(200, 25);
        listen(date, LocalDate.now());
        add(add, date, 2, 3, HPos.LEFT);

        //Name
        TextField name = new TextField();
        name.setText("CUSTOMER #");
        add(add, name, 2, 4, HPos.CENTER);
        add.setColumnSpan(name, 2);
                
        //City
        ComboBox citi = new ComboBox();
        citi.setPrefSize(200, 25);
        citi.focusedProperty().addListener((obs, oldValue, newValue) -> {
        if(newValue){
            citi.getSelectionModel().clearSelection();
            citi.setItems(FXCollections.observableArrayList(city));
            edit(citi);
        }
        else citi.setEditable(false);
        
        if(!newValue && oldValue)
            citi.setValue(citi.getItems().get(0));
        });
        listen(citi, city);
        add(add, citi, 2, 5, HPos.LEFT);
        
        ComboBox eid = new ComboBox();
        ComboBox ename = new ComboBox();
        addCIN(add, eid, ename, 2, 6, 5, 6, empid, emp, emp_map);

        //Credit
        TextField credit = new TextField();
        credit.setMaxSize(200, 25);
        credit.setText("0");
        add(add, credit, 2, 7, HPos.LEFT);
        
        //Debit
        TextField debit = new TextField();
        debit.setMaxSize(200, 25);
        debit.setText("0");
        add(add, debit, 5, 7, HPos.LEFT);
        
        //Debt
        TextField debt = new TextField();
        debt.setMaxSize(200, 25);
        debt.setText("1000");
        add(add, debt, 2, 8, HPos.LEFT);
        
        //Days
        TextField days = new TextField();
        days.setMaxSize(200, 25);
        days.setText("15");
        add(add, days, 5, 8, HPos.LEFT);

        if(id != -1){
            ilabel.setText(String.valueOf(id));
            Customer c = cust_map.get(id);
            date.setValue(c.getOpDate());
            name.setText(c.getName());
            citi.setValue(c.getCity());
            citi.setDisable(true);
            eid.setValue(c.getEmp().getId());
            ename.setValue(c.getEmp());
            eid.setDisable(true);
            ename.setDisable(true);
            credit.setText(c.getCredit().toString());
            debit.setText(c.getDebit().toString());
            debt.setText(c.getDue().toString());
            days.setText(c.getDays().toString());
        }
      
        //Button
        Button done = new Button("Create");
        done.setFont(new Font("Roboto", 20));
        
        done.setOnAction((func) -> {
            try{
                int cid = id;
                if(id == -1){
                    //empid < 100
                    //custid < 10000
                    int ceid = ((City)citi.getValue()).getId() * 100 + (int)eid.getValue();
                    cid = 1 + ceid*10000 + custid.stream().filter(K -> K/10000 == ceid)
                            .reduce(0, (a, b) -> Math.max(a%10000, b%10000));
                }
                
                //ALL Option Selected
                if(cid < 1010000) return;
                
                
            Customer c = new Customer(cid, 
                    name.getText(), (City)citi.getValue(), emp_map.get((int)eid.getValue()),
                    Double.valueOf(credit.getText()), Double.valueOf(debit.getText()), 
                    date.getValue(),
                    Double.valueOf(debt.getText()), Integer.valueOf(days.getText()));
            cust_map.putAndUpdate(cid, c);

            //
            ilabel.setText(String.valueOf(cid));
            Data.writeData();
            Data.setData();
            label.setText("Customer created successfully!");
            } catch (IOException | InterruptedException | NumberFormatException | ExecutionException ex) {
                Logger.getLogger(CustomerDriver.class.getName()).log(Level.SEVERE, null, ex);
                label.setText("Customer NOT created!");
            } catch(TimeoutException ex) {
                        label.setText("NO Internet!");
            } 
            fade(label);
            new Timeline(new KeyFrame(Duration.seconds(2), f-> MyBooks.sCus.fire())).play();
        });
        add(add, done, 5, 9, HPos.RIGHT);
        
        create.add(add, 1, 1);
        if(id == -1)
            return new Tab("Create", create);
        else
            return new Tab("Edit", create);
        
    }
    
    private GridPane custviewGrid(String label, TabPane tab){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "Employee ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        ComboBox eid = new ComboBox();
        ComboBox ename = new ComboBox();
        addCIN(venGrid, eid, ename, 2, 2, 5, 2, empid, emp, emp_map);
        
        TableView table = new TableView();
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            cust_map.forEach((K, V) -> {
                if(K == 0) return;
                if(eid.getValue() == null) return;
                else if(eid.getValue().equals(0))
                    table.getItems().add(new BillRow(K, V, V.getCity(), V.getEmp()));
                else{
                    if(V.getEmp().getId().equals(eid.getValue()))
                        table.getItems().add(new BillRow(K, V, V.getCity(), V.getEmp()));
                }
            });
        });
        add(venGrid, done, 5, 3, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Customer");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn amt = new TableColumn("City");
        amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("city"));
        TableColumn empl = new TableColumn("Employee");
        empl.setPrefWidth(150);
        empl.setCellValueFactory(new PropertyValueFactory<>("emp"));
        
        table.getColumns().setAll(id, cuz, amt, empl);
        venGrid.add(table, 0, 4);
        venGrid.setRowSpan(table, 5);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
 
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            BillRow r = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            CustomerDriver cd = new CustomerDriver(r.getId());
            Tab node = cd.create();
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
        });
        add(venGrid, edit, 0, 9, HPos.LEFT);

        Label lb = new Label();
        lb.setFont(new Font("Roboto", 18));
        add(venGrid, lb, 2, 9, HPos.LEFT);
        venGrid.setColumnSpan(lb, 5);

        Button export = new Button("Export");
        export.setFont(new Font("Roboto", 18));
        export.setOnAction((func) -> {
            List<Customer> map = new ArrayList<>();
            if(eid.getValue() == null)  return;
            if((int)eid.getValue() == 0)
                cust_map.values().forEach(K -> {
                    if(K.getId() != 0)
                    map.add(K);
                });
                
            else
                cust_map.values().forEach(K -> {
                    if(K.getId() == 0)  return;
                    if(K.getEmp().getId() == (int)eid.getValue()){
                        
                        map.add(K);
                    }
                });
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(emp_map.get((int)eid.getValue()).getName());
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Customer ID");
            row.createCell(1).setCellValue("Customer Name");
            row.createCell(2).setCellValue("Employee");
            int i = 1;
            for(Customer entry: map){
                    row = sheet.createRow(i++);
                    row.createCell(0).setCellValue(entry.getId());
                    row.createCell(1).setCellValue(entry.getName());
                    row.createCell(2).setCellValue(entry.getEmp().getName());
            }
            try {
                File file = new File(emp.get((int)eid.getValue()).getName()+"-Customer List.xls");
                file.createNewFile();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    workbook.write(fout);
                }
                lb.setText("Exported Successfully!");
            } catch (IOException ex) {
                lb.setText("Could NOT Export!");
                Logger.getLogger(CustomerDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
            fade(lb);

        });
        add(venGrid, export, 5, 9, HPos.LEFT);
        
        Button print = new Button("Print");
        print.setFont(new Font("Roboto", 18));
        print.setOnAction((func) -> {
            try {
                Print.printNode(table, print.getScene().getWindow());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(venGrid, print, 5, 9, HPos.RIGHT);
        grid.add(venGrid, 1, 1);
        return grid;

    }

}
