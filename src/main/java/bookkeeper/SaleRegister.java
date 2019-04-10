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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author shivam
 */
public class SaleRegister extends Utils {
    
    int cid;    
    public TabPane node() {
        TabPane pane = new TabPane();
        GridPane view = viewGrid("All Bills", pane);
        GridPane payview = payviewGrid("All Payments", pane);
        pane.getTabs().add(new Tab("Bills", view));
        pane.getTabs().add(new Tab("Payments", payview));
        pane.getTabs().get(0).setClosable(false);
        pane.getTabs().get(1).setClosable(false);
        return pane;
    }
    
    private GridPane viewGrid(String label, TabPane tab){
        
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
        
        addLabel(venGrid, 0, 5, "Bill ID", new Font("Roboto", 18), HPos.RIGHT);
        TextField bid = new TextField();
        bid.setText("0");
        add(venGrid, bid, 2, 5, HPos.LEFT);
        
        TableView table = new TableView();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            List<Bill> map = custbills(fdate.getValue(), tdate.getValue(), venGrid);
            if(Integer.parseInt(bid.getText()) != 0){
                map = map.stream()
                        .filter(K -> K.getNo() == Integer.parseInt(bid.getText()))
                        .collect(Collectors.toList());
            }
            
            Employee e = null;
            LocalDate d = LocalDate.MIN;
            table.getItems().clear();
            for(Bill V: map) {
                String part;
                if(V.getType() == 1)
                    part = "BILL";
                else
                    part = "SLIP"; 
                table.getItems().add(new BillRow(V.getId(), V.getNo(), V.localDate(), part, V.getEmp(), V.getCust(), V.getCust().getCity(), V.getNetAmt()));
            }
            Tab node = new Tab("View", view(tab, table));
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
            tab.requestFocus();
        });
        add(venGrid, done, 5, 5, HPos.RIGHT);

        Label lb = new Label();
        lb.setFont(new Font("Roboto", 18));
        add(venGrid, lb, 0, 6, HPos.LEFT);
        venGrid.setColumnSpan(lb, 5);

        Button export = new Button("Export");
        export.setFont(new Font("Roboto", 18));
        export.setOnAction((func) -> {
            List<Bill> map = custbills(fdate.getValue(), tdate.getValue(), venGrid);
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(fdate.getValue().toString() + " to " + tdate.getValue().toString());
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Bill ID");
            row.createCell(1).setCellValue("Customer ID");
            row.createCell(2).setCellValue("Customer");
            row.createCell(3).setCellValue("Product ID");
            row.createCell(4).setCellValue("Product");
            row.createCell(5).setCellValue("Product Code");
            row.createCell(6).setCellValue("Total Price");
            int i = 1;
            for(Bill entry: map){
                for(Pair<Product, ArrayList<Double>> p: entry.getProd()){
                    row = sheet.createRow(i++);
                    row.createCell(0).setCellValue(entry.getId());
                    row.createCell(1).setCellValue(entry.getCust().getId());
                    row.createCell(2).setCellValue(entry.getCust().getName());
                    row.createCell(3).setCellValue(p.getFirst().getId());
                    row.createCell(4).setCellValue(p.getFirst().getName());
                    row.createCell(5).setCellValue(p.getFirst().getHSN());
                    row.createCell(6).setCellValue(p.getSecond().get(6)*(100/(100+p.getSecond().get(5))));
                }
            }
            try {
                File file = new File("Sales.xls");
                file.createNewFile();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    workbook.write(fout);
                }
                lb.setText("Exported Successfully!");
            } catch (IOException ex) {
                lb.setText("Could NOT Export!");
                Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
            fade(lb);
        });
        add(venGrid, export, 5, 6, HPos.RIGHT);
        
        TableColumn id = new TableColumn("Bill ID");
        //id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn no = new TableColumn("SNo");
        //id.setPrefWidth(75);
        no.setCellValueFactory(new PropertyValueFactory<>("no"));
        TableColumn dt = new TableColumn("Date");
        //dt.setPrefWidth(150);
        dt.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn pt = new TableColumn("Type");
        pt.setCellValueFactory(new PropertyValueFactory<>("part"));
        TableColumn em = new TableColumn("Employee");
        //em.setPrefWidth(150);
        em.setCellValueFactory(new PropertyValueFactory<>("emp"));
        TableColumn cuz = new TableColumn("Customer");
        //cuz.setPrefWidth(250);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn cty = new TableColumn("City");
        //cty.setPrefWidth(120);
        cty.setCellValueFactory(new PropertyValueFactory<>("city"));
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
        TableColumn amt = new TableColumn("Amount");
        //amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("amt"));
        
        table.getColumns().setAll(no, dt, pt, em, cuz, amt);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
    
    
    private GridPane payviewGrid(String label, TabPane tab){
        
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
        
        addLabel(venGrid, 0, 5, "Pay ID", new Font("Roboto", 18), HPos.RIGHT);
        TextField bid = new TextField();
        bid.setText("0");
        add(venGrid, bid, 2, 5, HPos.LEFT);
        
        TableView table = new TableView();
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Payment> map = new HashMap<>();
            pay_map.forEach((K, V) -> {
                if(V.isType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.localDate().isBefore(fdate.getValue()) || V.localDate().isAfter(tdate.getValue()))
                    set.add(K);
                if(Integer.parseInt(bid.getText()) != 0){
                    if(V.getNo() != Integer.parseInt(bid.getText()))
                        set.add(K);
                }

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
                                if(V.getEmp().getId() != (int)(((ComboBox)i).getValue()))
                                    set.add(K);
                            });
                        }
                    }

                    if(i.getId().equals("CID")){
                        if(((ComboBox)i).getValue() == null) cid = -1;
                        else if(((ComboBox)i).getValue().equals(0)) cid = 0;
                        else{
                            cid = (int)(((ComboBox)i).getValue());
                        }
                    }
                }
            });
            set.forEach((value) -> { map.remove(value); } );
            table.getItems().clear();
            map.forEach((K, V) -> {
                int id = K;
                for(Pair<Customer, Pair<Double, String>> p: V.getPay()){
                    if(cid == -1) return;
                    else if(cid == 0)
                        table.getItems().add(new BillRow(V.getId(), V.getNo(), V.localDate(), V.getEmp(), p.getFirst(), p.getSecond().getFirst(), p.getSecond().getSecond()));
                    else{
                        if(p.getFirst().getId().equals(cid))
                            table.getItems().add(new BillRow(V.getId(), V.getNo(), V.localDate(), V.getEmp(), p.getFirst(), p.getSecond().getFirst(), p.getSecond().getSecond()));
                    }
                        
                }
                if(cid == 0)
                    table.getItems().add(new BillRow(V.getId(), V.getNo(), V.localDate(), null, null, V.getNetAmt(), "Total"));
            });
            ArrayList<Double> sum = new ArrayList<>();
            table.getItems().forEach((K) -> {
                if(((BillRow)K).getEmp() == null)   return;
                sum.add(((BillRow)K).getAmt());
            });
            double s = 0.0;
            for(double i: sum)
                s += i;
            Tab node = new Tab("View", payview(tab, table, String.format("%.2f", s)));
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
            tab.requestFocus();
        });
        add(venGrid, done, 5, 5, HPos.RIGHT);

        TableColumn id = new TableColumn("Pay ID");
        //id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn no = new TableColumn("SNo");
        //id.setPrefWidth(75);
        no.setCellValueFactory(new PropertyValueFactory<>("no"));
        TableColumn date = new TableColumn("Date");
        //id.setPrefWidth(75);
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn em = new TableColumn("Employee");
        //em.setPrefWidth(250);
        em.setCellValueFactory(new PropertyValueFactory<>("emp"));
        TableColumn cuz = new TableColumn("Customer");
        //cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("cust"));
        TableColumn amt = new TableColumn("Amount");
        //amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn part = new TableColumn("Particulars");
        //part.setPrefWidth(250);
        part.setCellValueFactory(new PropertyValueFactory<>("part"));
        
        table.getColumns().setAll(no, date, em, cuz, amt, part);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
    

}
