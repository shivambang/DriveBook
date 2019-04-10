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

import static bookkeeper.Data.bill_map;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Window;

/**
 *
 * @author shivam
 */
public class Print {
    
    public Print(int id, Window window) throws IOException{
        Bill bill = bill_map.get(id);
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/print.fxml"));
        GridPane node = fxml.load();
        ((Label) fxml.getNamespace().get("customer")).setText(bill.getCust().getName());
        ((Label) fxml.getNamespace().get("no")).setText(bill.getId().toString());
        ((Label) fxml.getNamespace().get("date")).setText(bill.getDate().format(DateTimeFormatter.ofPattern("dd MMM, yyyy")));
        ((Label) fxml.getNamespace().get("due")).setText(String.format(" %.2f", bill.getNetAmt()));
        
        //Table
        TableView table = (TableView) fxml.getNamespace().get("table");
        TableColumn sno = (TableColumn) fxml.getNamespace().get("sno");
        TableColumn item = (TableColumn) fxml.getNamespace().get("item");
        TableColumn qty = (TableColumn) fxml.getNamespace().get("qty");
        TableColumn price = (TableColumn) fxml.getNamespace().get("price");
        TableColumn tprice = (TableColumn) fxml.getNamespace().get("tprice");
        TableColumn disc = (TableColumn) fxml.getNamespace().get("disc");
        TableColumn frt = (TableColumn) fxml.getNamespace().get("frt");
        TableColumn tax = (TableColumn) fxml.getNamespace().get("tax");
        TableColumn amt = (TableColumn) fxml.getNamespace().get("amt");
        sno.setCellValueFactory(new PropertyValueFactory("id"));
        item.setCellValueFactory(new PropertyValueFactory("name"));
        qty.setCellValueFactory(new PropertyValueFactory("qty"));
        price.setCellValueFactory(new PropertyValueFactory("price"));
        tprice.setCellValueFactory(new PropertyValueFactory("netprice"));
        disc.setCellValueFactory(new PropertyValueFactory("disc"));
        frt.setCellValueFactory(new PropertyValueFactory("freight"));
        tax.setCellValueFactory(new PropertyValueFactory("gst"));
        amt.setCellValueFactory(new PropertyValueFactory("total"));
        
        double total = bill.getNetAmt() * 100 / (100 - bill.getDisc());
        ((Label) fxml.getNamespace().get("total")).setText(String.format(" %.2f", total));
        ((Label) fxml.getNamespace().get("netdisc")).setText(String.format(" %.2f", total - bill.getNetAmt()));
        ((Label) fxml.getNamespace().get("net")).setText(String.format(" %.2f", bill.getNetAmt()));
        
        int i = 1;
        for(Pair<Product, ArrayList<Double>> p: bill.getProd()){
            double netprice = p.getSecond().get(0)*p.getSecond().get(1);
            double tdisc = netprice - p.getSecond().get(6) * 100 / (100 + p.getSecond().get(5)) - p.getSecond().get(4);
            tdisc = tdisc > 0 ? tdisc : 0;
            table.getItems().add(new ProductRow(i++, p.getFirst().getName(), 
                    p.getSecond().get(0), p.getSecond().get(1), netprice, tdisc, 
                    p.getSecond().get(4), p.getSecond().get(5), p.getSecond().get(6)));
        }
        
        try {
            printNode(node, window, table);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            Logger.getLogger(Print.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void printNode(final Region node, Window window, TableView table) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout
            = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterAttributes attr = printer.getPrinterAttributes();
        PrinterJob job = PrinterJob.createPrinterJob();
        double scaleX
            = pageLayout.getPrintableWidth() / node.getPrefWidth();
        double scaleY
            = pageLayout.getPrintableHeight() / node.getPrefHeight();
        Scale scale = new Scale(scaleX, scaleY);
        node.getTransforms().add(scale);
        
        ObservableList list = FXCollections.observableArrayList(table.getItems());
        table.getItems().clear();
        int i = 0;
        if (job != null && job.showPrintDialog(window)) {
            while(i < list.size()){
                table.getItems().add(list.get(i++));
                if(i % 25 == 0 || i == list.size()){
                    boolean success = job.printPage(pageLayout, node);
                    if(success){
                        table.getItems().clear();
                    }
                }
            }
            job.endJob();
        }
        node.getTransforms().remove(scale);
    }    
    public static void printNode(final TableView node, Window window) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout
            = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterAttributes attr = printer.getPrinterAttributes();
        PrinterJob job = PrinterJob.createPrinterJob();
        GridPane pane = Utils.createGrid(false, 1, 2, 100, 5, 95);
        pane.setPadding(new Insets(10, 10, 10, 10));
        pane.setPrefSize(595, 845);
        pane.add(node, 0, 1);
        
        ObservableList list = FXCollections.observableArrayList(node.getItems());
        node.getItems().clear();
        int i = 0;
        if (job != null && job.showPrintDialog(window)) {
            while(i < list.size()){
                node.getItems().add(list.get(i++));
                if(i % 30 == 0 || i == list.size()){
                    boolean success = job.printPage(pageLayout, pane);
                    if(success){
                        node.getItems().clear();
                    }
                }
            }
            job.endJob();
        }
    }    
    public static void printLandNode(final TableView node, Window window) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout
            = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterAttributes attr = printer.getPrinterAttributes();
        PrinterJob job = PrinterJob.createPrinterJob();
        GridPane pane = Utils.createGrid(false, 1, 2, 100, 5, 95);
        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setPrefSize(845, 595);
        pane.add(node, 0, 1);
        
        ObservableList list = FXCollections.observableArrayList(node.getItems());
        node.getItems().clear();
        int i = 0;
        if (job != null && job.showPrintDialog(window)) {
            while(i < list.size()){
                node.getItems().add(list.get(i++));
                if(i % 21 == 0 || i == list.size()){
                    boolean success = job.printPage(pageLayout, pane);
                    if(success){
                        node.getItems().clear();
                    }
                }
            }
            job.endJob();
        }
    }    
}
