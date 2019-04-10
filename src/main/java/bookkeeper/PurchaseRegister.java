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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author shivam
 */
public class PurchaseRegister extends Utils {
        
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
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 5);
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
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Bill> map = venbills(fdate.getValue(), tdate.getValue(), (Integer)id.getValue());
            table.getItems().clear();
            map.forEach((K, V) -> {
                Bill b = V;
                Vendor c = b.getVen();
                for(Pair<Product, ArrayList<Double>> p: b.getProd()){
                    table.getItems().add(new BillRow(K, V.getLocalDate(), c, p.getFirst(), p.getSecond().get(1), p.getSecond().get(0), p.getSecond().get(6)));
                }
                
                double amt = b.getNetAmt();
                table.getItems().add(new BillRow(K, V.getLocalDate(), new Vendor(-1, "TOTAL"), null, null, null, amt));
            });
            Tab node = new Tab("View", view(tab, table));
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
            tab.requestFocus();
        });
        add(venGrid, done, 5, 4, HPos.RIGHT);
        
        Label lb = new Label();
        lb.setFont(new Font("Roboto", 18));
        add(venGrid, lb, 0, 5, HPos.LEFT);
        venGrid.setColumnSpan(lb, 5);

        Button export = new Button("Export");
        export.setFont(new Font("Roboto", 18));
        export.setOnAction((func) -> {
            Map<Integer, Bill> map = venbills(fdate.getValue(), tdate.getValue(), (Integer)id.getValue());
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(fdate.getValue().toString() + " to " + tdate.getValue().toString());
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Bill ID");
            row.createCell(1).setCellValue("Vendor ID");
            row.createCell(2).setCellValue("Vendor");
            row.createCell(3).setCellValue("Product ID");
            row.createCell(4).setCellValue("Product");
            row.createCell(5).setCellValue("Product Code");
            row.createCell(6).setCellValue("Total Price");
            int i = 1;
            for(Map.Entry<Integer, Bill> entry: map.entrySet()){
                for(Pair<Product, ArrayList<Double>> p: entry.getValue().getProd()){
                    row = sheet.createRow(i++);
                    row.createCell(0).setCellValue(entry.getKey());
                    row.createCell(1).setCellValue(entry.getValue().getVen().getId());
                    row.createCell(2).setCellValue(entry.getValue().getVen().getName());
                    row.createCell(3).setCellValue(p.getFirst().getId());
                    row.createCell(4).setCellValue(p.getFirst().getName());
                    row.createCell(5).setCellValue(p.getFirst().getHSN());
                    row.createCell(6).setCellValue(p.getSecond().get(6)*(100/(100+p.getSecond().get(5))));
                }
            }
            try {
                File file = new File("Purchase.xls");
                file.createNewFile();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    workbook.write(fout);
                }
                lb.setText("Exported Successfully!");
            } catch (IOException ex) {
                lb.setText("Could NOT Export!");
                Logger.getLogger(PurchaseRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
            fade(lb);
        });
        add(venGrid, export, 5, 5, HPos.RIGHT);

        TableColumn vid = new TableColumn("Bill ID");
        //vid.setPrefWidth(75);
        vid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn dt = new TableColumn("Date");
        //dt.setPrefWidth(150);
        dt.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn cuz = new TableColumn("Vendor");
        //cuz.setPrefWidth(250);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        TableColumn prud = new TableColumn("Products");
        //prud.setPrefWidth(200);
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
        TableColumn rate = new TableColumn("Price");
        //rate.setPrefWidth(100);
        rate.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn qty = new TableColumn("Quantity");
        //qty.setPrefWidth(100);
        qty.setCellValueFactory(new PropertyValueFactory<>("rqty"));
        TableColumn amt = new TableColumn("Amount");
        //amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("amt"));
        
        table.getColumns().setAll(vid, dt, cuz, prud, rate, qty, amt);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }

    private GridPane payviewGrid(String label, TabPane tab){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 5);
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
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            Map<Integer, Payment> map = new HashMap<>();
            pay_map.forEach((K, V) -> {
                if(!V.isType())
                    map.put(K, V);
            });
            Set<Integer> set = new HashSet<>();
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(fdate.getValue()) || V.getLocalDate().isAfter(tdate.getValue()))
                    set.add(K);
            });
            set.forEach((value) -> { map.remove(value); } );
            table.getItems().clear();
            map.forEach((K, V) -> {
                if(id.getValue() == null) return;
                else if(id.getValue().equals(0))
                    table.getItems().add(new BillRow(V.getId(), V.getVen(), V.getNetAmt(), V.getPart()));
                else{
                    if(V.getVen().getId().equals(id.getValue()))
                        table.getItems().add(new BillRow(V.getId(), V.getVen(), V.getNetAmt(), V.getPart()));
                }
                        
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
        add(venGrid, done, 5, 4, HPos.RIGHT);

        TableColumn pid = new TableColumn("Pay ID");
        pid.setPrefWidth(75);
        pid.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Vendor");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("ven"));
        TableColumn amt = new TableColumn("Amount");
        amt.setPrefWidth(250);
        amt.setCellValueFactory(new PropertyValueFactory<>("amt"));
        TableColumn part = new TableColumn("Particulars");
        part.setPrefWidth(250);
        part.setCellValueFactory(new PropertyValueFactory<>("part"));
        
        table.getColumns().setAll(pid, cuz, amt, part);
        
        grid.add(venGrid, 1, 1);
        return grid;

    }
}
