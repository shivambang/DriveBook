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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
public class ProductDriver extends Utils {
    public TabPane node(int id){
        TabPane pane = new TabPane();
        pane.getTabs().add(new Tab("Create", create(id, pane)));
        return pane;
    }

    private GridPane create(int id, TabPane pane){
        GridPane create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane add = createGrid(false, 6, 9, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));

        addLabel(add, 0, 0, "New Product", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(add, 0, 5, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 5, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 2, "ID", new Font("Roboto", 18), HPos.RIGHT);
        //addLabel(add, 2, 2, String.valueOf(pid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(add, 0, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 4, "HSN", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 4, "Tax %", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 6, "Volume", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 7, "Purchase Price", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 3, 7, "Sale Price", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 0, 8, "Opening Stock", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label label = new Label();
        label.setFont(new Font("Open Sans", 20));
        add(add, label, 3, 2, HPos.CENTER);
        add.setColumnSpan(label, 3);

        //ID
        Label ilabel = new Label();
        ilabel.setFont(new Font("Roboto", 18));
        add(add, ilabel, 2, 2, HPos.LEFT);
        
        //Name
        TextField name = new TextField();
        name.setText("Product #");
        add(add, name, 2, 3, HPos.CENTER);
        add.setColumnSpan(name, 2);
        
        //HSN
        TextField hsn = new TextField();
        hsn.setText("000000");
        add(add, hsn, 2, 4, HPos.LEFT);

        //Tax
        TextField tax = new TextField();
        tax.setText("0");
        add(add, tax, 5, 4, HPos.LEFT);
        
        ComboBox vid = new ComboBox();
        ComboBox vname = new ComboBox();
        addCIN(add, vid, vname, 2, 5, 5, 5, venid, ven, ven_map);

        //Volume
        TextField vol = new TextField();
        vol.setMaxSize(200, 25);
        vol.setText("1");
        add(add, vol, 2, 6, HPos.LEFT);

        //Price
        TextField pprice = new TextField();
        pprice.setMaxSize(200, 25);
        pprice.setText("0");
        add(add, pprice, 2, 7, HPos.LEFT);
        
        //Price
        TextField sprice = new TextField();
        sprice.setMaxSize(200, 25);
        sprice.setText("0");
        add(add, sprice, 5, 7, HPos.LEFT);

        //TextField
        TextField stock = new TextField();
        stock.setMaxSize(200, 25);
        stock.setText("0");
        add(add, stock, 2, 8, HPos.LEFT);

        if(id != -1) {
            ilabel.setText(String.valueOf(id));
            name.setText(prod_map.get(id).getName());
            hsn.setText(prod_map.get(id).getHSN());
            vid.setValue(prod_map.get(id).getVendor().getId());
            vid.setDisable(true);
            vname.setDisable(true);
            pprice.setText(prod_map.get(id).getPprice().toString());
            sprice.setText(prod_map.get(id).getSprice().toString());
            vol.setText(prod_map.get(id).getVol().toString());
            tax.setText(prod_map.get(id).getTax().toString());
            stock.setText(String.format("%.2f", prod_map.get(id).getStock()));
        } else viewGrid(pane);
        //Button
        Button done = new Button("Create");
        done.setFont(new Font("Roboto", 20));
        
        done.setOnAction((func) -> {
                if(vid.getValue() == null);
                else if(vid.getValue().equals(0))   return;
                else{
                    try{
                    int pid = id;
                    //products < 1000
                    if(id == -1){
                        int vpid = (int)vid.getValue();
                        pid = 1 + vpid*1000 + prodid.stream().filter(K -> K/1000 == vpid).reduce(0, (a, b) -> Math.max(a%1000, b%1000));
                    }
                    if(pid < 1000)  return;
                    Product p = new Product(pid, hsn.getText(), name.getText(), 
                            Double.valueOf(vol.getText()),  Double.valueOf(tax.getText()),
                            Double.valueOf(pprice.getText()), Double.valueOf(sprice.getText()), Double.valueOf(stock.getText()),ven_map.get((int)vid.getValue()));
                    prod_map.putAndUpdate(pid, p);
                    ilabel.setText(String.valueOf(pid));
                    Data.writeData();
                    Data.setData();
                    label.setText("Product created successfully!");
                    } catch(TimeoutException ex) {
                        label.setText("NO Internet!");
                    } catch (Exception ex) {
                        Logger.getLogger(ProductDriver.class.getName()).log(Level.SEVERE, null, ex);
                        label.setText("Product NOT created!");
                    }
                }
            fade(label);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.mPro.fire())).play();
        });
        add(add, done, 5, 8, HPos.RIGHT);
        
        create.add(add, 1, 1);
        return create;
    }
    
    private void viewGrid(TabPane tab){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "View Products", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 2, "Vendor ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(venGrid, 3, 2, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        ComboBox eid = new ComboBox();
        ComboBox ename = new ComboBox();
        addCIN(venGrid, eid, ename, 2, 2, 5, 2, venid, ven, ven_map);
        
        TableView table = new TableView();

        Label lb = new Label();
        lb.setFont(new Font("Roboto", 18));
        add(venGrid, lb, 2, 9, HPos.LEFT);
        venGrid.setColumnSpan(lb, 5);
        
        //Button
        Button done = new Button("Done");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            prod_map.forEach((K, V) -> {
                if(K == 0) return;
                if(eid.getValue() == null) return;
                else if(eid.getValue().equals(0))
                    table.getItems().add(new ProductRow(K, V.getName(), V.getSprice()));
                else{
                    if(V.getVendor().getId().equals(eid.getValue()))
                        table.getItems().add(new ProductRow(K, V.getName(), V.getSprice()));
                }
            });
        });
        add(venGrid, done, 5, 3, HPos.RIGHT);

        Button export = new Button("Export");
        export.setFont(new Font("Roboto", 18));
        export.setOnAction((func) -> {
            List<Product> map = new ArrayList<>();
            if(eid.getValue() == null)  return;
            if((int)eid.getValue() == 0)
                prod_map.values().forEach(K -> {
                    if(K.getId() != 0)
                    map.add(K);
                });
                
            else
                prod_map.values().forEach(K -> {
                    if(K.getId() == 0)  return;
                    if(K.getVendor().getId() == (int)eid.getValue()){
                        
                        map.add(K);
                    }
                });
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet(emp_map.get((int)eid.getValue()).getName());
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Product ID");
            row.createCell(1).setCellValue("Product Name");
            row.createCell(2).setCellValue("Vendor");
            int i = 1;
            for(Product entry: map){
                    row = sheet.createRow(i++);
                    row.createCell(0).setCellValue(entry.getId());
                    row.createCell(1).setCellValue(entry.getName());
                    row.createCell(2).setCellValue(entry.getVendor().getName());
            }
            try {
                File file = new File(ven.get((int)eid.getValue()).getName()+"-Product List.xls");
                file.createNewFile();
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    workbook.write(fout);
                }
                lb.setText("Exported Successfully!");
            } catch (IOException ex) {
                lb.setText("Could NOT Export!");
                Logger.getLogger(ProductDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
            fade(lb);
        });
        add(venGrid, export, 5, 9, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn("Product");
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn amt = new TableColumn("Price");
        amt.setPrefWidth(150);
        amt.setCellValueFactory(new PropertyValueFactory<>("stock"));
        
        table.getColumns().setAll(id, cuz, amt);
        venGrid.add(table, 0, 4);
        venGrid.setRowSpan(table, 5);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            ProductRow r = (ProductRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Tab node = new Tab("Edit", create(r.getId(), tab));
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
        });
        add(venGrid, edit, 0, 9, HPos.LEFT);
        grid.add(venGrid, 1, 1);
        tab.getTabs().add(new Tab("View", grid));

    }
    
}
