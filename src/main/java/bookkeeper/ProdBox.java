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
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

/**
 *
 * @author shivam
 */
public class ProdBox extends Utils implements Initializable {
    
    TextField[] field;
    ComboBox[] cb;
    Label[] label;
    public static int cid, eid, vid, bid, no, type;
    public static LocalDate date;
    private ArrayList<Integer> id;
    private ArrayList<Product> p;
    

    @FXML
    public ComboBox sb_pid, sb_pname;
    public TextField sb_pprice, sb_pqty, sb_ldisc, sb_pdisc, sb_freight, sb_tax, sb_tdisc;
    public Label sb_clbl, sb_total, sb_netAmt, sb_created;
    public TableView sb_table;
    public TableColumn sb_tid, sb_tname, sb_tqty, sb_tvol, sb_tnetvol, sb_tprice, sb_ttotal, sb_tldisc, sb_tpdisc, sb_tfrt, sb_ttax;

    public void ProdBox() {
        this.label = new Label[]{sb_total, sb_created};
        this.cb = new ComboBox[]{sb_pid, sb_pname};
        this.field = new TextField[]{sb_pqty, sb_pprice, sb_ldisc, sb_pdisc, sb_tax, sb_freight, sb_tdisc};
    }

    @FXML
    private void sb_save(ActionEvent event){
        
        int id = Integer.valueOf(sb_pid.getValue().toString());
        String name = prod_map.get((int)sb_pid.getValue()).toString();
        double qty = Double.valueOf(sb_pqty.getText());
        double vol = prod_map.get((int)sb_pid.getValue()).getVol();
        double price = Double.valueOf(sb_pprice.getText());
        double tot = Double.valueOf(sb_total.getText());
        double ldisc = Double.valueOf(sb_ldisc.getText());
        double pdisc = Double.valueOf(sb_pdisc.getText());
        double frt = Double.valueOf(sb_freight.getText());
        double tax = Double.valueOf(sb_tax.getText());
        sb_table.getItems().add(new ProductRow(id, name, qty, vol, price, ldisc, pdisc, frt, tax, tot));
        tot -= tot * Double.valueOf(sb_tdisc.getText()) / 100;
        double total = (sb_netAmt.getText().equals("") ? 0 : Double.valueOf(sb_netAmt.getText())) + tot;
        sb_netAmt.setText(String.format("%d", Math.round(total)));
        reset(cb, new TextField[]{sb_pqty, sb_pprice, sb_ldisc, sb_pdisc, sb_tax, sb_freight}, label);
        sb_pid.requestFocus();
    }
    
    @FXML
    private void sb_edit(ActionEvent event){
        ProductRow row = (ProductRow)sb_table.getItems().get(sb_table.getSelectionModel().getFocusedIndex());
        sb_pid.getSelectionModel().select(row.getId());
        sb_pname.getSelectionModel().select(prod_map.get(row.getId()));
        sb_pqty.setText(String.valueOf(row.getQty()));
        sb_pprice.setText(String.valueOf(row.getPrice()));
        sb_total.setText(String.valueOf(row.getTotal()));
        sb_ldisc.setText(String.valueOf(row.getLdisc()));
        sb_pdisc.setText(String.valueOf(row.getPdisc()));
        sb_freight.setText(String.valueOf(row.getFreight()));
        sb_tax.setText(String.valueOf(row.getGst()));
        sb_table.getItems().remove(sb_table.getSelectionModel().getFocusedIndex());
        double tot = Double.valueOf(sb_total.getText());
        tot -= tot * Double.valueOf(sb_tdisc.getText()) / 100;
        double total = (sb_netAmt.getText().equals("") ? 0 : Double.valueOf(sb_netAmt.getText())) - tot;
        sb_netAmt.setText(String.format("%d", Math.round(total)));
        sb_pid.requestFocus();
    }
    
    @FXML
    private void sb_delete(ActionEvent event){
        ProductRow row = (ProductRow)sb_table.getItems().get(sb_table.getSelectionModel().getFocusedIndex());
        double tot = Double.valueOf(row.getTotal());
        tot -= tot * Double.valueOf(sb_tdisc.getText()) / 100;
        double total = (sb_netAmt.getText().equals("") ? 0 : Double.valueOf(sb_netAmt.getText())) - tot;
        sb_netAmt.setText(String.format("%d", Math.round(total)));
        sb_table.getItems().remove(sb_table.getSelectionModel().getFocusedIndex());
    }
     
    @FXML
    private void sb_create(ActionEvent event){
            ArrayList<Pair<Product, ArrayList<Double>>> prod = new ArrayList<>();
            double netVol = 0;
        //try{    
            for(Object obj: sb_table.getItems()){
                ProductRow row = (ProductRow) obj;
                Product p = prod_map.get(row.getId());
                ArrayList<Double> arr = new ArrayList<>();
                arr.add(row.getQty());
                arr.add(Double.valueOf(row.getPrice()));
                arr.add(row.getLDisc());
                arr.add(row.getPDisc());
                arr.add(row.getFreight());
                arr.add(row.getTax());
                arr.add(Double.valueOf(row.getTotal()));
                prod.add(new Pair(p, arr));
                netVol += row.getQty() * row.getVol();
            }
        //} catch(Exception ex){
        //    sb_created.setText(String.format("Bill NOT created!"));
        //    ex.printStackTrace();
        //}
            double disc = Double.valueOf(sb_tdisc.getText());
            double net = Double.valueOf(sb_netAmt.getText());
            int id = bid;

        if(cid != -1){
        try{
            if(bill_map.containsKey(bid)){
                Bill b = bill_map.get(bid);
                for(Pair <Product, ArrayList<Double>> p: b.getProd())
                    prod_map.get(p.getFirst().getId()).setStock(p.getSecond().get(0));
            }
            bill_map.putAndUpdate(id, new Bill(id, no, type, date, emp_map.get(eid), cust_map.get(cid), prod, disc, net));
            for(Pair <Product, ArrayList<Double>> p: prod)
                prod_map.get(p.getFirst().getId()).setStock(-p.getSecond().get(0));
            Data.writeData();
            Data.setData();
            sb_created.setText(String.format("Bill created successfully! SNo #%d", no));
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bill#" + id + " created successfully!\nDo you want to Print?", 
                                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            
            if(alert.getResult() == ButtonType.YES){
                new Print(id, sb_table.getScene().getWindow());
            }

            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.sBill.fire())).play();
        }
        catch(Exception ex){
            sb_created.setText(String.format("Bill NOT created!"));
            ex.printStackTrace();
        }
        }
        
        else if(vid != -1){
        try{
            if(bill_map.containsKey(bid)){
                Bill b = bill_map.get(bid);
                b.ven().setDebit(-b.getNetAmt());
                for(Pair <Product, ArrayList<Double>> p: b.getProd())
                    prod_map.get(p.getFirst().getId()).setStock(-p.getSecond().get(0));
            }
            bill_map.put(id, new Bill(id, date, ven_map.get(vid), prod, disc, net));
            ven_map.get(vid).setDebit(net);
            for(Pair <Product, ArrayList<Double>> p: prod)
                prod_map.get(p.getFirst().getId()).setStock(p.getSecond().get(0));
            Data.writeData();
            Data.setData();
            sb_created.setText(String.format("Bill created successfully! ID #%d", id));
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.pBill.fire())).play();
        }
        catch(Exception ex){
            sb_created.setText(String.format("Bill NOT created!"));
            ex.printStackTrace();
        }
            
        }
        
        else{
            sb_created.setText(String.format("Bill NOT created!"));
        }
        fade(sb_created);
    }

    private void setTable(){
        sb_tid.setCellValueFactory(new PropertyValueFactory<>("id"));
        sb_tname.setCellValueFactory(new PropertyValueFactory<>("name"));
        sb_tqty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        sb_tvol.setCellValueFactory(new PropertyValueFactory<>("vol"));
        sb_tnetvol.setCellValueFactory(new PropertyValueFactory<>("netvol"));
        sb_tprice.setCellValueFactory(new PropertyValueFactory<>("price"));
        sb_ttotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        sb_tldisc.setCellValueFactory(new PropertyValueFactory<>("ldisc"));
        sb_tpdisc.setCellValueFactory(new PropertyValueFactory<>("pdisc"));
        sb_tfrt.setCellValueFactory(new PropertyValueFactory<>("freight"));
        sb_ttax.setCellValueFactory(new PropertyValueFactory<>("tax"));
    }
    
    private void putValuesInTable(){
        Bill b = bill_map.get(bid);
        for(Pair<Product, ArrayList<Double>> p: b.getProd())
            sb_table.getItems().add(new ProductRow(p.getFirst().getId(), p.getFirst().getName(), 
                    p.getSecond().get(0), p.getFirst().getVol(), 
                    p.getSecond().get(1), p.getSecond().get(6)));
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb){
        ProdBox();
        setTable();
        listen(field);
        sb_tdisc.setText("0.0");
        if(vid != -1){
            id = new ArrayList<>();
            p = new ArrayList<>();
            prodid.forEach((x) -> {
                if(x == 0);
                else if(prod_map.get(x).getVenId().equals(vid)){ 
                    id.add(x);
                    p.add(prod_map.get(x));
                }
            });
            listen(sb_pid, id);
            listen(sb_pname, p);
        }
        else{
            id = new ArrayList<>(prodid);
            p = new ArrayList<>(prod);
            listen(sb_pid, id);
            listen(sb_pname, p);
        }
        
        if(bill_map.containsKey(bid))
            putValuesInTable();
    }
    
    private void listen(TextField[] field){
        for(TextField text: field){
            text.focusedProperty().addListener((obs, oldValue, newValue) -> {
                if(!newValue && oldValue){
                    if("".equals(text.getText()))
                        text.setText("0.00");
                    if(text.getId().equals("sb_tax")){
                        double total = Double.valueOf(sb_pqty.getText()) * Double.valueOf(sb_pprice.getText());
                        total -= total * Double.valueOf(sb_pdisc.getText()) / 100;
                        total -= Double.valueOf(sb_ldisc.getText()) * Double.valueOf(sb_pqty.getText()) * ((Product)(prod_map.get((int)sb_pid.getValue()))).getVol();
                        total += Double.valueOf(sb_freight.getText());
                        total += total * Double.valueOf(sb_tax.getText()) / 100;
                        sb_total.setText(String.format("%d", Math.round(total)));
                    }
                    else if(text.getId().equals("sb_tdisc")){
                        double total = 0;
                        for(Object obj: sb_table.getItems())
                            total += Double.valueOf(((ProductRow)obj).getTotal());
                        total -= total * Double.valueOf(sb_tdisc.getText()) / 100;
                        sb_netAmt.setText(String.format("%d", Math.round(total)));
                    }

                }
            });
        }
        
        //Box Listeners
        sb_pid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                sb_pid.getSelectionModel().clearSelection();
                sb_pid.setItems(FXCollections.observableArrayList(id));
                edit(sb_pid);
            }
            else sb_pid.setEditable(false);
            
            if(!newValue && oldValue){
                sb_pid.setValue(sb_pid.getItems().get(0));
                if(cid != -1)
                    sb_pprice.setText(String.format("%.2f", ((Product)sb_pname.getValue()).getSprice()));
                else
                    sb_pprice.setText(String.format("%.2f", ((Product)sb_pname.getValue()).getPprice()));
                sb_tax.setText(String.format("%.2f", prod_map.get((int)sb_pid.getValue()).getTax()));
            }
            if(newValue && !oldValue) sb_pid.getEditor().clear();

        });
        sb_pid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                sb_pname.setValue(prod_map.get((int)newValue));
        });
        
        
        //PName
        sb_pname.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(sb_pname);
                sb_pname.setItems(FXCollections.observableArrayList(p));
                sb_pname.getSelectionModel().select(prod_map.get((int)sb_pid.getValue()));
                sb_pname.setValue(prod_map.get((int)sb_pid.getValue()));
            }
            else sb_pname.setEditable(false);
            
            if(!newValue && oldValue){
                sb_pname.setValue(sb_pname.getItems().get(0));
                if(cid != -1)
                    sb_pprice.setText(String.format("%.2f", ((Product)sb_pname.getValue()).getSprice()));
                else
                    sb_pprice.setText(String.format("%.2f", ((Product)sb_pname.getValue()).getPprice()));
                sb_tax.setText(String.format("%.2f", ((Product)sb_pname.getValue()).getTax()));
            }
        });
        sb_pname.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                sb_pid.setValue(((Product)newValue).getId());
        });

    }

}
