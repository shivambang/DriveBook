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
import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 *
 * @author shivam
 */
public class Utils {
    
    public void listen(DatePicker date, LocalDate d){
        date.setPromptText("DD/MM/YYYY");
        DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");
        date.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(!newValue && oldValue)
                if(date.getEditor().getText() == null)
                    date.setValue(d);
                else if(date.getEditor().getText().equals(""))
                    date.setValue(d);
                else
                    date.setValue(LocalDate.parse(date.getEditor().getText(), dateTimeFormatter));
        });
        date.setConverter(new StringConverter<LocalDate>() {

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
    }
    
    public <T> void listen(ComboBox box, ArrayList<T> items){
        box.setCellFactory(cf -> {
            ListCell<T> cell = new ListCell<T>() {
                @Override
                protected void updateItem(T item, boolean empty){
                    super.updateItem(item, empty);
                    setText(empty ? null : item.toString());
                }
            };
            cell.setOnMouseReleased(r -> {
                if(!cell.isEmpty()){
                    keyTyped(box, cell.getItem().toString(), items);
                }
            });
            return cell;
        });
        box.getEditor().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event){
                String newVal = "", oldVal = box.getEditor().getText().toUpperCase();
                if(event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER)
                    keyTyped(box, oldVal, items);
                
                else if(event.getCode() == KeyCode.BACK_SPACE){
                    if(oldVal.length() > 0)
                        newVal = oldVal.substring(0, oldVal.length() - 1);
                    keyTyped(box, newVal, items);
                    box.getEditor().setText(newVal);
                    box.getEditor().positionCaret(box.getEditor().getText().length());
                    box.setVisibleRowCount(5);
                    box.show();
                    event.consume();
                }

                else event.consume();

            }
        });
        box.getEditor().setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event){

                String newVal = "", oldVal = box.getEditor().getText().toUpperCase();
                if(event.getCode().isLetterKey() || event.getCode().isDigitKey() || event.getCode() == KeyCode.SPACE){
                    newVal = (oldVal + event.getCharacter()).toUpperCase();
                    keyTyped(box, newVal, items);
                }

                else if(event.getCode() == KeyCode.BACK_SPACE){
                    if(oldVal.length() > 0)
                        newVal = oldVal.substring(0, oldVal.length() - 1);
                    keyTyped(box, newVal, items);
                }

                else if(event.getCode() == KeyCode.DOWN || 
                        event.getCode() == KeyCode.UP || 
                        event.getCode() == KeyCode.RIGHT)
                    newVal = box.getEditor().getText();
                
                else
                    return;
                box.getEditor().setText(newVal);
                box.getEditor().positionCaret(box.getEditor().getText().length());
                box.setVisibleRowCount(5);
                box.show();
                event.consume();
            }
        });

    }
        
    private <T> void keyTyped(ComboBox box, String s, ArrayList<T> items){
        s = s.trim();
        ArrayList<T> arr = new ArrayList<>();
        if(!"".equals(s)) {
            for(T c: items){
                if(c.toString().toLowerCase().contains(s.toLowerCase()))
                    arr.add(c);
            }
        } else arr = new ArrayList<>(items);
        box.setItems(FXCollections.observableArrayList(arr));
        box.setVisibleRowCount(5);

    }

    public void edit(ComboBox box){
        box.setEditable(true);
        box.getEditor().positionCaret(box.getEditor().getText().length());
        box.setVisibleRowCount(5);
        box.show();
    }

    public void reset(ComboBox[] cb, TextField[] field, Label[] label){
            for(ComboBox box: cb)
                box.getSelectionModel().clearSelection();
            for(TextField text: field)
                text.setText("");
            for(Label text: label)
                text.setText("");
        }
    
    public static GridPane createGrid(boolean bool, int col, int row, double...width){
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(bool);
        ColumnConstraints cc; RowConstraints rc;
        int k = 1;
        for(double w: width){
            if(k++ > col){
                rc = new RowConstraints();
                rc.setMinHeight(w);
                rc.setPercentHeight(w);
                grid.getRowConstraints().add(rc);
            }
            else{
                cc = new ColumnConstraints();
                cc.setMinWidth(w);
                cc.setPercentWidth(w);
                grid.getColumnConstraints().add(cc);
            }
        }
        return grid;
    }

    public static void addLabel(GridPane grid, int c, int r, String text, Font font, HPos hPos, int cSpan){
        Label label = new Label(text);
        label.setFont(font);
        grid.add(label, c, r);
        grid.setHalignment(label, hPos);
        grid.setValignment(label, VPos.CENTER);
        grid.setColumnSpan(label, cSpan);
    }

    
    public static void addLabel(GridPane grid, int c, int r, String text, Font font, HPos hPos){
        Label label = new Label(text);
        label.setFont(font);
        grid.add(label, c, r);
        grid.setHalignment(label, hPos);
        grid.setValignment(label, VPos.CENTER);
    }
    
    public static void add(GridPane grid, Node node, int c, int r, HPos hPos){
        if(node.getClass().equals(TextField.class))
            textUpper((TextField) node);
        grid.add(node, c, r);
        grid.setHalignment(node, hPos);
        grid.setValignment(node, VPos.CENTER);
    }

    public static void textUpper(TextField field){
        field.setTextFormatter(new TextFormatter<>((text) -> {
            text.setText(text.getText().toUpperCase());
            return text;
        }));
    }
    
    public void addIN(GridPane venGrid, int eic, int eir, int enc, int enr, int cic, int cir, int cnc, int cnr, int cSpan){

        //Employee ID
        ComboBox eid = new ComboBox();
        eid.setPrefSize(150, 25);
        eid.setId("EID");
        listen(eid, empid);
        add(venGrid, eid, eic, eir, HPos.LEFT);
        
        //Employee Name
        ComboBox ename = new ComboBox();
        ename.setMaxSize(350, 25);
        ename.setId("ENAME");
        listen(ename, emp);
        ename.setItems(FXCollections.observableArrayList(emp));
        add(venGrid, ename, enc, enr, HPos.LEFT);
        venGrid.setColumnSpan(ename, cSpan);

        //Customer ID
        ComboBox cid = new ComboBox();
        cid.setPrefSize(150, 25);
        cid.setId("CID");
        cid.setItems(FXCollections.observableArrayList(custid));
        listen(cid, custid);
        add(venGrid, cid, cic, cir, HPos.LEFT);
        
        //Customer Name
        ComboBox cname = new ComboBox();
        cname.setMaxSize(350, 25);
        cname.setId("CNAME");
        cname.setItems(FXCollections.observableArrayList(cust));
        listen(cname, cust);
        add(venGrid, cname, cnc, cnr, HPos.LEFT);
        venGrid.setColumnSpan(cname, cSpan);
        
        //Listeners
        //EID
        eid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                eid.getSelectionModel().clearSelection();
                eid.setItems(FXCollections.observableArrayList(empid));
                edit(eid);
            }
            else eid.setEditable(false);
            
            if(!newValue && oldValue){ if(eid.getValue() == null) eid.setValue(eid.getItems().get(0));
                if(eid.getValue().equals(0)){
                    cid.setItems(FXCollections.observableArrayList(custid));
                    cname.setItems(FXCollections.observableArrayList(cust));
                }
                else{
                    ArrayList<Customer> arr = new ArrayList<>();
                    ArrayList<Integer> iarr = new ArrayList<>();
                    cust_map.forEach((K, V) -> {
                        if(K == 0)  return;
                        if(V.getEmp().getId().equals((int)eid.getValue())){
                            iarr.add(V.getId());
                            arr.add(V);
                        }
                    });
                    cname.setItems(FXCollections.observableArrayList(arr));
                    cid.setItems(FXCollections.observableArrayList(iarr));
                }
            }
            if(newValue && !oldValue) eid.getEditor().clear();

        });
        eid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                ename.setValue(emp_map.get((int)newValue));
            }
        });
        
        
        //EName
        ename.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(ename);
                ename.setItems(FXCollections.observableArrayList(emp));
                if(eid.getValue() == null);
                else{
                    ename.getSelectionModel().select(emp_map.get((int)eid.getValue()));
                    ename.setValue(emp_map.get((int)eid.getValue()));
                    robot();
                }
            }
            else ename.setEditable(false);
            
            if(!newValue && oldValue){ ename.setValue(ename.getItems().get(0));
                if(eid.getValue().equals(0)){
                    cid.setItems(FXCollections.observableArrayList(custid));
                    cname.setItems(FXCollections.observableArrayList(cust));
                }
                else{
                    ArrayList<Customer> arr = new ArrayList<>();
                    ArrayList<Integer> iarr = new ArrayList<>();
                    cust_map.forEach((K, V) -> {
                        if(K == 0)  return;
                        if(V.getEmp().getId().equals((int)eid.getValue())){
                            iarr.add(V.getId());
                            arr.add(V);
                        }
                    });
                    cname.setItems(FXCollections.observableArrayList(arr));
                    cid.setItems(FXCollections.observableArrayList(iarr));
                }
            }
        });
        ename.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                ename.setValue(newValue);
                eid.setValue(((Employee)newValue).getId());
            }
        });
        
        //CID
        cid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(cid);
                if(eid.getValue() == null)
                    cid.setItems(FXCollections.observableArrayList(custid));
            }
            else cid.setEditable(false);
            
            if(!newValue && oldValue) if(cid.getValue() == null) cid.setValue(cid.getItems().get(0));
        });
        cid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(!newValue.equals(0))
                    eid.setValue(cust_map.get(Integer.valueOf(newValue.toString())).getEmp().getId());
                cid.setValue(newValue);
                cname.setValue(cust_map.get(Integer.valueOf(newValue.toString())));
            }
        });
        
        //CName
        cname.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(cname);
                if(eid.getValue() == null)
                    cname.setItems(FXCollections.observableArrayList(cust));
                if(cid.getValue() == null);
                else{
                    cname.getSelectionModel().select(cust_map.get((int) cid.getValue()));
                    cname.setValue(cust_map.get((int) cid.getValue()));
                    robot();
                }
            }
            else cname.setEditable(false);
            
            if(!newValue && oldValue) if(cname.getValue() == null) cname.setValue(cname.getItems().get(0));
        });
        cname.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(!((Customer)newValue).getId().equals(0))
                    eid.setValue(((Customer)newValue).getEmp().getId());
                cid.setValue(((Customer)newValue).getId());
            }
        });

    }
    
    public void addPIN(GridPane venGrid, ComboBox eid, ComboBox ename, ComboBox cid, ComboBox cname,
            int eic, int eir, int enc, int enr, int cic, int cir, int cnc, int cnr, int cSpan){

        //Employee ID
        eid.setPrefSize(150, 25);
        eid.setId("VID");
        listen(eid, venid);
        add(venGrid, eid, eic, eir, HPos.LEFT);
        
        //Employee Name
        ename.setMaxSize(350, 25);
        ename.setId("VNAME");
        listen(ename, ven);
        ename.setItems(FXCollections.observableArrayList(ven));
        add(venGrid, ename, enc, enr, HPos.LEFT);
        venGrid.setColumnSpan(ename, cSpan);

        //Product ID
        cid.setPrefSize(150, 25);
        cid.setId("PID");
        cid.setItems(FXCollections.observableArrayList(prodid));
        listen(cid, prodid);
        add(venGrid, cid, cic, cir, HPos.LEFT);
        
        //Product Name
        cname.setMaxSize(350, 25);
        cname.setId("PNAME");
        cname.setItems(FXCollections.observableArrayList(prod));
        listen(cname, prod);
        add(venGrid, cname, cnc, cnr, HPos.LEFT);
        venGrid.setColumnSpan(cname, cSpan);
        
        //Listeners
        //EID
        eid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                eid.getSelectionModel().clearSelection();
                eid.setItems(FXCollections.observableArrayList(venid));
                edit(eid);
            }
            else eid.setEditable(false);
            
            if(!newValue && oldValue){ if(eid.getValue() == null) eid.setValue(eid.getItems().get(0));
                if(eid.getValue().equals(0)){
                    cid.setItems(FXCollections.observableArrayList(prodid));
                    cname.setItems(FXCollections.observableArrayList(prod));
                }
                else{
                    ArrayList<Product> arr = new ArrayList<>();
                    ArrayList<Integer> iarr = new ArrayList<>();
                    prod_map.forEach((K, V) -> {
                        if(K == 0)  return;
                        if(V.getVendor().getId().equals((int)eid.getValue())){
                            iarr.add(V.getId());
                            arr.add(V);
                        }
                    });
                    cname.setItems(FXCollections.observableArrayList(arr));
                    cid.setItems(FXCollections.observableArrayList(iarr));
                }
            }
            if(newValue && !oldValue) eid.getEditor().clear();

        });
        eid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                ename.setValue(ven_map.get((int)newValue));
            }
        });
        
        
        //EName
        ename.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(ename);
                ename.setItems(FXCollections.observableArrayList(ven));
                if(eid.getValue() == null);
                else{
                    ename.getSelectionModel().select(ven_map.get((int)eid.getValue()));
                    ename.setValue(ven_map.get((int)eid.getValue()));
                    robot();
                }
            }
            else ename.setEditable(false);
            
            if(!newValue && oldValue){ ename.setValue(ename.getItems().get(0));
                if(eid.getValue().equals(0)){
                    cid.setItems(FXCollections.observableArrayList(prodid));
                    cname.setItems(FXCollections.observableArrayList(prod));
                }
                else{
                    ArrayList<Product> arr = new ArrayList<>();
                    ArrayList<Integer> iarr = new ArrayList<>();
                    prod_map.forEach((K, V) -> {
                        if(K == 0)  return;
                        if(V.getVendor().getId().equals((int)eid.getValue())){
                            iarr.add(V.getId());
                            arr.add(V);
                        }
                    });
                    cname.setItems(FXCollections.observableArrayList(arr));
                    cid.setItems(FXCollections.observableArrayList(iarr));
                }
            }
        });
        ename.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                ename.setValue(newValue);
                eid.setValue(((Vendor)newValue).getId());
            }
        });
        
        //CID
        cid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(cid);
                if(eid.getValue() == null)
                    cid.setItems(FXCollections.observableArrayList(prodid));
            }
            else cid.setEditable(false);
            
            if(!newValue && oldValue) if(cid.getValue() == null) cid.setValue(cid.getItems().get(0));
        });
        cid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(!newValue.equals(0))
                    eid.setValue(prod_map.get(Integer.valueOf(newValue.toString())).getVendor().getId());
                cid.setValue(newValue);
                cname.setValue(prod_map.get(Integer.valueOf(newValue.toString())));
            }
        });
        
        //CName
        cname.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(cname);
                if(eid.getValue() == null)
                    cname.setItems(FXCollections.observableArrayList(prod));
                if(cid.getValue() == null);
                else{
                    cname.getSelectionModel().select(prod_map.get((int) cid.getValue()));
                    cname.setValue(prod_map.get((int) cid.getValue()));
                    robot();
                }
            }
            else cname.setEditable(false);
            
            if(!newValue && oldValue) if(cname.getValue() == null) cname.setValue(cname.getItems().get(0));
        });
        cname.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                if(!((Product)newValue).getId().equals(0))
                    eid.setValue(((Product)newValue).getVendor().getId());
                cid.setValue(((Product)newValue).getId());
            }
        });

    }
    
    public <T extends Unique> void addCIN(GridPane venGrid, ComboBox cid, ComboBox cname, int ic, int ir, int nc, int nr,
            ArrayList<Integer> custid, ArrayList<T> cust, Map<Integer, T> map){
        //Customer ID
        cid.setPrefSize(150, 25);
        cid.setId("ID");
        cid.setItems(FXCollections.observableArrayList(custid));
        listen(cid, custid);
        add(venGrid, cid, ic, ir, HPos.LEFT);
        
        //Customer Name
        cname.setPrefSize(350, 25);
        cname.setItems(FXCollections.observableArrayList(cust));
        listen(cname, cust);
        add(venGrid, cname, nc, nr, HPos.LEFT);

        //CID
        cid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                cid.setItems(FXCollections.observableArrayList(custid));
                edit(cid);
            }
            else cid.setEditable(false);
            
            if(!newValue && oldValue) if(cid.getValue() == null) cid.setValue(cid.getItems().get(0));
            if(newValue && !oldValue) cid.getEditor().clear();
        });
        cid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                cname.setValue(map.get((int)newValue));
        });
        
        //CName
        cname.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                cname.setItems(FXCollections.observableArrayList(cust));
                edit(cname);
                if(cid.getValue() == null);
                else{
                    cname.getSelectionModel().select(map.get((int) cid.getValue()));
                    cname.setValue(map.get((int) cid.getValue()));
                    robot();
                }
            }
            else cname.setEditable(false);
            
            if(!newValue && oldValue) if(cname.getValue() == null) cname.setValue(cname.getItems().get(0));
        });
        cname.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                cid.setValue(((T)newValue).getId());
        });
        
    }

    public void addEIN(GridPane venGrid, int ic, int ir, int nc, int nr){
        
        //Employee ID
        ComboBox eid = new ComboBox();
        eid.setPrefSize(150, 25);
        eid.setId("EID");
        listen(eid, empid);
        add(venGrid, eid, ic, ir, HPos.LEFT);
        
        //Employee Name
        ComboBox ename = new ComboBox();
        ename.setPrefSize(300, 25);
        ename.setId("ENAME");
        listen(ename, emp);
        ename.setItems(FXCollections.observableArrayList(emp));
        add(venGrid, ename, nc, nr, HPos.LEFT);
        venGrid.setColumnSpan(ename, 1);
                
        //Listeners
        //EID
        eid.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                eid.getSelectionModel().clearSelection();
                eid.setItems(FXCollections.observableArrayList(empid));
                edit(eid);
            }
            else eid.setEditable(false);
            
            if(!newValue && oldValue) if(eid.getValue() == null) eid.setValue(eid.getItems().get(0));
            if(newValue && !oldValue) eid.getEditor().clear();

        });
        eid.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                ename.setValue(emp_map.get((int)newValue));
        });
        
        
        //EName
        ename.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue){
                edit(ename);
                ename.setItems(FXCollections.observableArrayList(emp));
                ename.getSelectionModel().select(emp_map.get((int)eid.getValue()));
                ename.setValue(emp_map.get((int)eid.getValue()));
                robot();
            }
            else ename.setEditable(false);
            
            if(!newValue && oldValue) if(ename.getValue() == null) ename.setValue(ename.getItems().get(0));
        });
        ename.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null)
                eid.setValue(((Employee)newValue).getId());
        });
        

    }
    
    private void robot(){
        try {
            Robot robot = new Robot();
            robot.keyPress(39);
            robot.keyRelease(39);
        } catch (AWTException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void fade(Label node){
        FadeTransition fader = new FadeTransition(Duration.seconds(2), node);
        fader.setFromValue(1);
        fader.setToValue(0);
        new SequentialTransition(node, fader).play();
    }
    
    public GridPane view(TabPane tab, TableView table){
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "View Bill", new Font("Open Sans", 30), HPos.LEFT, 3);
        
        venGrid.add(table, 0, 2);
        venGrid.setRowSpan(table, 7);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        Button view = new Button("View");
        view.setFont(new Font("Roboto", 18));
        view.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Bill b = bill_map.get(row.getId());
            if(b.isType()){
                billWin(b);
            }
            else{
            }
        });
        add(venGrid, view, 0, 9, HPos.LEFT);

        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Bill b = bill_map.get(row.getId());
            Tab node;
            if(b.isType()){
                SaleBill sb = new SaleBill(row.getId());
                node = sb.node();
            }
            else{
                PurchaseBill pb = new PurchaseBill(row.getId());
                node = pb.node();
            }
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
            tab.requestFocus();
        });
        add(venGrid, edit, 2, 9, HPos.LEFT);
        
        /*
        Button del = new Button("Delete");
        del.setFont(new Font("Roboto", 18));
        del.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure?\n Bill #" + row.getId() + " will be deleted!", 
                                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            
            if(alert.getResult() == ButtonType.YES){
                Bill b = bill_map.get(row.getId());
                if(b.isType()){
                    for(Pair <Product, ArrayList<Double>> p: b.getProd())
                        prod_map.get(p.getFirst().getId()).setStock(p.getSecond().get(0));
                }
                else{
                    b.getVen().setDebit(-b.getNetAmt());
                    for(Pair <Product, ArrayList<Double>> p: b.getProd())
                        prod_map.get(p.getFirst().getId()).setStock(-p.getSecond().get(0));
                }
                Set<Object> set = new HashSet<>();
                table.getItems().forEach((obj) -> { if(((BillRow) obj).getId().equals(row.getId())) set.add(obj); });
                set.forEach((obj) -> { table.getItems().remove(obj); });
                bill_map.remove(row.getId());
                try {
                    Data.writeData();
                    Data.setData();
                } catch (IOException ex) {
                    Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(venGrid, del, 3, 9, HPos.LEFT);
        */
        
        Button print = new Button("Print");
        print.setFont(new Font("Roboto", 18));
        print.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            try {
                new Print(row.getId(), print.getScene().getWindow());
            } catch (IOException ex) {
                Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(venGrid, print, 5, 9, HPos.RIGHT);
        
        grid.add(venGrid, 1, 1);
        return grid;
    }
    
    public void billWin(Bill b){
        GridPane pane = createGrid(false, 3, 4, 2.5, 95, 2.5, 5, 10, 10, 70, 5);
        pane.setStyle("-fx-background-color: #F8FBFC");

        String text =  b.getEmp().getName() + "'s " + b.getCust().getName();;
        addLabel(pane, 1, 1, text, new Font("Open Sans", 24), HPos.LEFT);
        addLabel(pane, 1, 2, "Bill #" + b.getNo(), new Font("Open Sans", 18), HPos.RIGHT);
        addLabel(pane, 1, 2, String.format("Total: %.2f", b.getNetAmt()), new Font("Open Sans", 18), HPos.LEFT);

        TableView tv = new TableView();
        tv.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        TableColumn id = new TableColumn("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn prud = new TableColumn("Products");
        prud.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn rate = new TableColumn("Price");
        rate.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn qty = new TableColumn("Quantity");
        qty.setPrefWidth(75);
        qty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        TableColumn vol = new TableColumn("Disc");
        vol.setCellValueFactory(new PropertyValueFactory<>("vol"));
        TableColumn net = new TableColumn("GST");
        net.setCellValueFactory(new PropertyValueFactory<>("netvol"));
        TableColumn amt = new TableColumn("Amount");
        amt.setPrefWidth(75);
        amt.setCellValueFactory(new PropertyValueFactory<>("total"));
        tv.getColumns().setAll(id, prud, rate, qty, vol, net, amt);
        for(Pair<Product, ArrayList<Double>> p: b.getProd()){
            double netprice = p.getSecond().get(0)*p.getSecond().get(1);
            double tdisc = netprice - p.getSecond().get(6) * 100 / (100 + p.getSecond().get(5)) - p.getSecond().get(4);
            tdisc = tdisc > 0 ? tdisc : 0;
            tv.getItems().add(new ProductRow(p.getFirst().getId(), p.getFirst().getName(), 
                    p.getSecond().get(0), tdisc, p.getSecond().get(5), p.getSecond().get(1), p.getSecond().get(6)));
        }
        pane.add(tv, 1, 3);

        final Stage stage = new Stage();
        Scene scene = new Scene(pane, 640, 480);
        scene.getStylesheets().add(this.getClass().getResource("css/myBooks.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

    }
    
    public GridPane payview(TabPane tab, TableView table, String total){
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, "View Payment", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(venGrid, 0, 1, "Total Amount:", new Font("Open Sans", 20), HPos.LEFT, 1);
        addLabel(venGrid, 1, 1, total, new Font("Open Sans", 20), HPos.LEFT, 3);
        venGrid.add(table, 0, 2);
        venGrid.setRowSpan(table, 7);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
        
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Tab node;
            if(pay_map.get(row.getId()).isType()){
                CustomerPayment sb = new CustomerPayment(row.getId());
                node = sb.node();
            }
            else{
                VendorPayment vp = new VendorPayment(row.getId());
                node = vp.node();
            }
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
            tab.requestFocus();
        });
        add(venGrid, edit, 0, 9, HPos.LEFT);

        Button print = new Button("Print");
        print.setFont(new Font("Roboto", 18));
        print.setOnAction((func) -> {
            try {
                Print.printLandNode(table, print.getScene().getWindow());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        add(venGrid, print, 5, 9, HPos.RIGHT);
        
        /*
        Button del = new Button("Delete");
        del.setFont(new Font("Roboto", 18));
        del.setOnAction((func) -> {
            BillRow row = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are You Sure?\n Payment #" + row.getId() + " will be deleted!", 
                                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();
            
            if(alert.getResult() == ButtonType.YES){
                Payment b = pay_map.get(row.getId());
                if(b.isType()){
                    for(Pair<Customer, Pair<Double, String>> p: b.getPay()){
                        p.getFirst().setCredit(-p.getSecond().getFirst());
                    }
                }
            
                else{
                }

                table.getItems().remove(table.getSelectionModel().getFocusedIndex());
                pay_map.remove(row.getId());
                try {
                    Data.writeData();
                    Data.setData();
                } catch (IOException ex) {
                    Logger.getLogger(SaleRegister.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(venGrid, del, 5, 9, HPos.RIGHT);
        */
        grid.add(venGrid, 1, 1);
        return grid;
    }
    
    public List<Bill> custbills(LocalDate fdate, LocalDate tdate, GridPane venGrid){
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isBefore(fdate) || V.getLocalDate().isAfter(tdate))
                set.add(K);
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
                    if(((ComboBox)i).getValue() == null);
                    else if(((ComboBox)i).getValue().equals(0));
                    else{
                        map.forEach((K, V) -> {
                            if(V.getCust().getId() != (int)(((ComboBox)i).getValue()))
                                set.add(K);
                        });
                    }
                }
                    //MyBooks.cid = (int)(((ComboBox)i).getValue());
            }
        });
        set.forEach((value) -> { map.remove(value); } );
        List<Bill> list = new ArrayList(map.values());
        list.sort((Bill b1, Bill b2) -> {
            if(b1.getLocalDate().isBefore(b2.getLocalDate()))
                return -1;
            else if(b1.getLocalDate().isAfter(b2.getLocalDate()))
                return 1;
            else{
                if(b1.getEmp().getName().compareTo(b2.getEmp().getName()) < 0)
                    return -1;
                else if(b1.getEmp().getName().compareTo(b2.getEmp().getName()) > 0)
                    return 1;
                else
                    return b1.getId() - b2.getId();
            }
        });
        return list;
    }
    
    public Map<Integer, Bill> venbills(LocalDate fdate, LocalDate tdate, Integer id){
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(!V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isBefore(fdate) || V.getLocalDate().isAfter(tdate))
                set.add(K);
        });

        if(id == null);
        else if(id.equals(0));
        else{
            map.forEach((K, V) -> {
                if(!V.getVen().getId().equals((int)id))
                    set.add(K);
            });
        }
        return map;
    }
    
    public Map<Integer, Double> dues(LocalDate date, Integer id, Boolean type){
        //type 0: Employee
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                set.add(K);
        });

        if(id == null);
        else if(id.equals(0));
        else{
            if(type)
                map.forEach((K, V) -> {
                    if(V.getCust().getId() != (int)(id))
                        set.add(K);
                });
            else
                map.forEach((K, V) -> {
                    if(V.getCust().getEmp().getId() != (int)(id))
                        set.add(K);
                });
        }

        set.forEach((value) -> { map.remove(value); } );
        Map<Integer, Double> due = new HashMap<>();
        map.forEach((K, V) -> {
            int cid = K;
            Bill b = V;
            Customer c = b.getCust();
            City s = c.getCity();
            due.merge(c.getId(), b.getNetAmt(), (V1, V2) -> V1 + V2);
        });

        Map<Integer, Payment> pmap = new HashMap<>();
        pay_map.forEach((K, V) -> {
            if(V.isType())
                pmap.put(K, V);
        });
        Set<Integer> pset = new HashSet<>();
        pmap.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                pset.add(K);
        });
        pset.forEach((value) -> { pmap.remove(value); } );

        if(id == null);
        else{
            pmap.forEach((K, V) -> {
                for(Pair<Customer, Pair<Double, String>> p: V.getPay())
                    due.merge(p.getFirst().getId(), p.getSecond().getFirst(), (V1, V2) -> V1 - V2);
            });
        }
        
        return due;
    }
    
    public Map<Integer, Double> dr(LocalDate date, Integer id, Boolean type){
        //type 0: Employee
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                set.add(K);
        });

        if(id == null);
        else if(id.equals(0));
        else{
            if(type)
                map.forEach((K, V) -> {
                    if(V.getCust().getId() != (int)(id))
                        set.add(K);
                });
            else
                map.forEach((K, V) -> {
                    if(V.getCust().getEmp().getId() != (int)(id))
                        set.add(K);
                });
        }

        set.forEach((value) -> { map.remove(value); } );
        Map<Integer, Double> due = new HashMap<>();
        map.forEach((K, V) -> {
            int cid = K;
            Bill b = V;
            Customer c = b.getCust();
            City s = c.getCity();
            due.merge(c.getId(), b.getNetAmt(), (V1, V2) -> V1 + V2);
        });
        return due;
    }
    public Map<Integer, Double> cr(LocalDate date, Integer id, Boolean type){
        Map<Integer, Double> due = new HashMap<>();

        Map<Integer, Payment> pmap = new HashMap<>();
        pay_map.forEach((K, V) -> {
            if(V.isType())
                pmap.put(K, V);
        });
        Set<Integer> pset = new HashSet<>();
        pmap.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                pset.add(K);
        });
        pset.forEach((value) -> { pmap.remove(value); } );

        if(id == null);
        else{
            pmap.forEach((K, V) -> {
                for(Pair<Customer, Pair<Double, String>> p: V.getPay())
                    due.merge(p.getFirst().getId(), p.getSecond().getFirst(), (V1, V2) -> V1 + V2);
            });
        }
        
        return due;
    }
    public Map<Integer, Double> vdues(LocalDate date, Integer cid){
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(!V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                set.add(K);
        });

        if(cid == null);
        else if(cid.equals(0));
        else{
            map.forEach((K, V) -> {
                if(V.getVen().getId() != (int)(cid))
                    set.add(K);
            });
        }

        set.forEach((value) -> { map.remove(value); } );
        Map<Integer, Double> due = new HashMap<>();
        map.forEach((K, V) -> {
            int id = K;
            Bill b = V;
            Vendor c = b.getVen();
            due.merge(c.getId(), b.getNetAmt(), (V1, V2) -> V1 + V2);
        });

        Map<Integer, Payment> pmap = new HashMap<>();
        pay_map.forEach((K, V) -> {
            if(!V.isType())
                pmap.put(K, V);
        });

        if(cid == null);
        else if(cid.equals(0)){
            pmap.forEach((K, V) -> {
                due.merge(V.getVen().getId(), V.getNetAmt(), (V1, V2) -> V1 - V2);
            });
        }
        else{
            pmap.forEach((K, V) -> {
                if(cid.equals(V.getVen().getId()))
                    due.merge(V.getVen().getId(), V.getNetAmt(), (V1, V2) -> V1 - V2);
            });
        }
        return due;
    }
    
    public Map<Integer, ArrayList<Double>> dues(LocalDate date, Integer eid, Integer cid){
        Map<Integer, Bill> map = new HashMap<>();
        bill_map.forEach((K, V) -> {
            if(V.isType())
                map.put(K, V);
        });
        Set<Integer> set = new HashSet<>();
        map.forEach((K, V) -> {
            if(V.getLocalDate().isAfter(date))
                set.add(K);
        });

        if(eid == null);
        else if(eid.equals(0));
        else{
            map.forEach((K, V) -> {
                if(V.getEmp().getId() != (int)(eid))
                    set.add(K);
            });
        }

        if(cid == null);
        else if(cid.equals(0));

        set.forEach((value) -> { map.remove(value); } );

        Map<Integer, Payment> pmap = new HashMap<>();
        pay_map.forEach((K, V) -> {
            if(V.isType())
                pmap.put(K, V);
        });
        Set<Integer> pset = new HashSet<>();
        
        
        if(eid == null);
        else if(eid.equals(0));
        else{
            pmap.forEach((K, V) -> {
                if(V.getEmp().getId() != (int)(eid))
                    pset.add(K);
            });
        }
        pset.forEach((value) -> { pmap.remove(value); } );

        Map<Integer, Double> cpay = new HashMap<>();
        cust_map.forEach((K, V) -> {
            if(K == 0) return;
            if(V.getEmp().getId().equals(eid) || eid.equals(0))
                cpay.put(K, 0.0);
        });
        if(cid == null);
        else if(cid.equals(0)){
            pmap.forEach((K, V) -> {
                for(Pair<Customer, Pair<Double, String>> p: V.getPay())
                    cpay.merge(p.getFirst().getId(), p.getSecond().getFirst(), (V1, V2) -> V1 + V2);

            });
        }
        Map<Integer, ArrayList<Double>> due = new HashMap<>();
        Map<Integer, Double> csal = new HashMap<>();
        Map<Integer, Double> csal2 = new HashMap<>();
        Map<Integer, Double> csal3 = new HashMap<>();
        Map<Integer, Double> csal4 = new HashMap<>();
        Map<Integer, Double> csal5 = new HashMap<>();
        cust_map.forEach((K, V) -> {
            if(K == 0) return;
            if(V.getEmp().getId().equals(eid) || eid.equals(0)){
                due.put(K, new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0)));
                csal.put(K, 0.0);
                csal2.put(K, 0.0);
                csal3.put(K, 0.0);
                csal4.put(K, 0.0);
                csal5.put(K, 0.0);
            }
        });
        if(cid == null);
        else if(cid.equals(0)){
            map.forEach((K, V) -> {
                if(V.getLocalDate().isAfter(date.minusDays(90))) return;
                csal.merge(V.getCust().getId(), V.getNetAmt(), (V1, V2) -> V1 + V2);
            });
        }
        csal.forEach((K, V) -> {
            Double p = cpay.get(K) - V;
            cpay.put(K, p > 0 ? p : 0);
            due.get(K).add(0, p < 0 ? -p : 0);
        });

        if(cid == null);
        else if(cid.equals(0)){
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(date.minusDays(89)) || V.getLocalDate().isAfter(date.minusDays(60))) return;
                csal2.merge(V.getCust().getId(), V.getNetAmt(), (V1, V2) -> V1 + V2);
            });
        }
        csal2.forEach((K, V) -> {
            Double p = cpay.get(K) - V;
            cpay.put(K, p > 0 ? p : 0);
            due.get(K).add(1, p < 0 ? -p : 0);
        });
        
        if(cid == null);
        else if(cid.equals(0)){
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(date.minusDays(59)) || V.getLocalDate().isAfter(date.minusDays(30))) return;
                csal3.merge(V.getCust().getId(), V.getNetAmt(), (V1, V2) -> V1 + V2);
            });
        }
        csal3.forEach((K, V) -> {
            Double p = cpay.get(K) - V;
            cpay.put(K, p > 0 ? p : 0);
            due.get(K).add(2, p < 0 ? -p : 0);
        });
        
        if(cid == null);
        else if(cid.equals(0)){
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(date.minusDays(29)) || V.getLocalDate().isAfter(date.minusDays(15))) return;
                csal4.merge(V.getCust().getId(), V.getNetAmt(), (V1, V2) -> V1 + V2);
            });
        }
        csal4.forEach((K, V) -> {
            Double p = cpay.get(K) - V;
            cpay.put(K, p > 0 ? p : 0);
            due.get(K).add(3, p < 0 ? -p : 0);
        });
        
        if(cid == null);
        else if(cid.equals(0)){
            map.forEach((K, V) -> {
                if(V.getLocalDate().isBefore(date.minusDays(14))) return;
                csal5.merge(V.getCust().getId(), V.getNetAmt(), (V1, V2) -> V1 + V2);
            });
        }
        csal5.forEach((K, V) -> {
            Double p = cpay.get(K) - V;
            cpay.put(K, p > 0 ? p : 0);
            due.get(K).add(4, p < 0 ? -p : 0);
        });
        
       return due;
    }
    
}
