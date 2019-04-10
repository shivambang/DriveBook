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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

/**
 *
 * @author shivam
 */
public class SaleBox implements Initializable {
    
    private Map<ComboBox, ArrayList> map;
    private SaleBox.SalesBill sb;

    
    @FXML
    public ComboBox emp_id, cust_id, emp_name, cust_name, sb_pid, sb_pname;
    public DatePicker sb_date;
    public GridPane sb_cust;
    
    @FXML
    private void sb_done(ActionEvent event){
        sb_cust.setDisable(true);
        
    }

        class SalesBill {

        ComboBox[] cb = new ComboBox[]{emp_id, emp_name, cust_id, cust_name/*, sb_pid, sb_pname*/};
        
        private void keyTyped(ComboBox box, String s){
            s = s.trim();
            ArrayList<Object> arr = new ArrayList<>();
            if(!"".equals(s)) {
                for(Object c: map.get(box)){
                    if(c.toString().toLowerCase().contains(s.toLowerCase()))
                        arr.add(c);
                }
            } else arr = map.get(box);
            box.setItems(FXCollections.observableArrayList(arr));
            box.setVisibleRowCount(5);

        }
        private void reset(){
            for(ComboBox box: cb)
                box.getSelectionModel().clearSelection();
            /*for(TextField text: field)
                text.setText("");
            for(Label text: label)
                text.setText("");*/
        }
        private void sbReset(){
            keyTyped(emp_id, "");
            keyTyped(emp_name, "");
            keyTyped(cust_id, "");
            keyTyped(cust_name, "");
            //keyTyped(sb_pid, "");
            //keyTyped(sb_pname, "");
        }
        private void set(ComboBox box){
            switch (box.getId()) {
                case "empid":
                    sbReset();
                    break;
                case "emp_name":
                    box.getSelectionModel().select(Data.emp_map.get((Integer)emp_id.getValue()));
                    break;
                case "custid":
                    box.getEditor().setText("");
                    break;
                case "cust_name":
                    box.getSelectionModel().select(cust_map.get((Integer)cust_id.getValue()));
                    break;
                /*case "sb_pid":
                    sbReset();
                    break;
                case "sb_pname":
                    box.getSelectionModel().select(prod_map.get((Integer)sb_pid.getValue()));
                    break;*/
                default:
                    break;
            }

        }
        private void setCust(int id){
            ArrayList<Customer> arr = new ArrayList<>();
            ArrayList<Integer> iarr = new ArrayList<>();
            for(Customer c: cust){
                if(true){
                    arr.add(c);
                    iarr.add(c.getId());
                }
            }
            cust_id.setItems(FXCollections.observableArrayList(iarr));
            cust_name.setItems(FXCollections.observableArrayList(arr));
        }
        private void setOther(ComboBox box, Object obj){
            if(box.getId().equals("empid")){
                int id = Integer.valueOf(obj.toString());
                emp_name.setValue(emp_map.get(id));
                if(emp_id.isFocused())
                    setCust(id);
            }
            else if(box.getId().equals("emp_name")){
                int id = ((Employee)obj).getId();
                emp_id.setValue(id);
                if(emp_name.isFocused())
                    setCust(id);
            }
            else if(box.getId().equals("custid")){
                int id = Integer.valueOf(obj.toString());
                Customer c = cust_map.get(id);
//                emp_id.setValue(c.getEmp().getId());
//                emp_name.setValue(c.getEmp());
                cust_name.setValue(c);
            }
            else if(box.getId().equals("cust_name")){
                Customer c = ((Customer)obj);
//                emp_id.setValue(c.getEmp().getId());
//                emp_name.setValue(c.getEmp());
                cust_id.setValue(c.getId());
            }
            /*else if(box.getId().equals("sb_pid")){
                int id = Integer.valueOf(obj.toString());
                sb_pname.setValue(prod.get(id));
                sb_pprice.setText(String.format("%.2f", prod.get(id).getPrice()));
            }
            else if(box.getId().equals("sb_pname")){
                Product p = (Product)obj;
                sb_pid.setValue(p.getId());
                sb_pprice.setText(String.format("%.2f", p.getPrice()));
            }*/
        }

        private void listen(ComboBox[] cb){
            for(ComboBox box: cb){
                box.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    if(newValue){
                        box.setEditable(true);
                        box.getEditor().positionCaret(box.getEditor().getText().length());
                        box.setVisibleRowCount(5);
                        box.show();
                        set(box);
                    }
                    else box.setEditable(false);

                    if(!newValue && oldValue)
                        box.setValue(box.getItems().get(0));
                });
                box.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                    if(newVal != null)
                        setOther(box, newVal);
                    
                });
                box.getEditor().setOnKeyPressed(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event){
                        if(event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER)
                            keyTyped(box, box.getEditor().getText());

                        else event.consume();
                        
                    }
                });
                box.getEditor().setOnKeyReleased(new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event){
                        
                        String newVal = "", oldVal = box.getEditor().getText().toUpperCase();
                        if(event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.SPACE){
                            newVal = (oldVal + event.getCharacter()).toUpperCase();
                            keyTyped(box, newVal);
                        }

                        else if(event.getCode() == KeyCode.BACK_SPACE){
                            if(oldVal.length() > 0)
                                newVal = oldVal.substring(0, oldVal.length() - 1);
                            keyTyped(box, newVal);
                        }

                        else if(event.getCode() == KeyCode.DOWN){
                            newVal = box.getEditor().getText().toUpperCase();
                        }

                        else if(event.getCode() == KeyCode.UP){
                            newVal = box.getEditor().getText().toUpperCase();
                        }

                        else
                            return;
                        box.getEditor().setText(newVal);
                        box.getEditor().positionCaret(box.getEditor().getText().length());
                        box.show();
                        box.setVisibleRowCount(5);
                        event.consume();
                    }
                });
            }
        }
        
        /*private void listen(TextField[] field){
            for(TextField text: field){
                text.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if(!newValue && oldValue){
                        if("".equals(text.getText()))
                            text.setText("0.00");
                        if(text.getId().equals("sb_tax")){
                            double total = Double.valueOf(sb_pqty.getText()) * Double.valueOf(sb_pprice.getText());
                            total += Double.valueOf(sb_freight.getText());
                            total -= total * Double.valueOf(sb_pdisc.getText()) / 100;
                            total -= Double.valueOf(sb_ldisc.getText()) * Double.valueOf(sb_pqty.getText()) * ((Product)(prod_map.get((int)sb_pid.getValue()))).getVol();
                            total += total * Double.valueOf(sb_tax.getText()) / 100;
                            sb_total.setText(String.format("%.2f", total));
                        }
                        else if(text.getId().equals("sb_tdisc")){
                            double total = 0;
                            for(Object obj: sb_table.getItems())
                                total += ((ProductRow)obj).getTotal();
                            total -= total * Double.valueOf(sb_tdisc.getText()) / 100;
                            sb_netAmt.setText(String.format("%.2f", total));
                        }
                            
                    }
                });
            }
        }*/
        
        SalesBill(){
            map = new HashMap<>();
            map.put(cust_name, cust);
            map.put(emp_name, emp);
            map.put(emp_id, new ArrayList<>(empid));
            map.put(cust_id, new ArrayList<>(custid));
            map.put(sb_pid, new ArrayList<>(prodid));
            map.put(sb_pname, prod);
            sb_date.setConverter(new StringConverter<LocalDate>() {
                private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

                @Override
                public String toString(LocalDate localDate) {
                    if(localDate==null)
                        return "";
                    return dateTimeFormatter.format(localDate);
                }

                @Override
                public LocalDate fromString(String dateString) {
                    if(dateString==null || dateString.trim().isEmpty())
                        return null;
                    return LocalDate.parse(dateString,dateTimeFormatter);
                }
            });
            listen(cb);
            //listen(field);
        }
        
        private void start(){
            sb_cust.setDisable(false);
            //sb_prod.setDisable(true);
            reset();
            sbReset();
            sb_date.setValue(LocalDate.now());
            sb_date.requestFocus();
        }
    }

    
        
    @Override
    public void initialize(URL url, ResourceBundle rb){
        sb = this.new SalesBill();
        sb.start();
        
    }
}
