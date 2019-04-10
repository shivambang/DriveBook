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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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

/**
 *
 * @author shivam
 */
public class EmployeeDriver extends Utils {
    int id;
    private final GridPane create, add;
    public EmployeeDriver() {
        id = -1;
        create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        add = createGrid(false, 6, 9, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));        
    }

    public EmployeeDriver(int id) {
        this.id = id;
        create = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        add = createGrid(false, 6, 9, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        add.setPadding(new Insets(0, 0, 10, 50));        
    }
    
    public TabPane node(){
        TabPane pane = new TabPane();
        createEmp(add);
        createCity(add);
        create.add(add, 1, 1);

        pane.getTabs().add(new Tab("Create", create));
        pane.getTabs().add(new Tab("Employee", viewGrid("Emp", pane)));
        pane.getTabs().add(new Tab("City", viewGrid("City", pane)));
        return pane;
    }

    private void createEmp(GridPane add){
        int cid;
        if(id == -1){
            if(empid.size() > 0)
                cid = empid.get(empid.size() - 1) + 1;
            else
                cid = 1;
        } else cid = id;
        addLabel(add, 0, 0, "New Employee", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(add, 0, 2, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 2, 2, String.valueOf(cid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(add, 0, 3, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label label = new Label();
        label.setFont(new Font("Open Sans", 20));
        add(add, label, 3, 2, HPos.CENTER);
        add.setColumnSpan(label, 3);
                
        //Name
        TextField name = new TextField();
        name.setText("EMPLOYEE #".concat(String.valueOf(cid)));
        add(add, name, 2, 3, HPos.CENTER);
        add.setColumnSpan(name, 2);
        
        if(id != -1)
            name.setText(emp_map.get(id).getName());
            
        //Button
        Button done = new Button("Create");
        done.setFont(new Font("Roboto", 20));
        
        done.setOnAction((func) -> {
                    try{
                    Employee e = new Employee(cid, name.getText(), 
                            Double.valueOf(0), Double.valueOf(0)); 
                    emp_map.putAndUpdate(cid, e);
                    
                    //
                    
                    Data.writeData();
                    Data.setData();
                    label.setText("Employee created successfully!");
                    } catch(TimeoutException ex) {
                        label.setText("NO Internet!");
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        Logger.getLogger(EmployeeDriver.class.getName()).log(Level.SEVERE, null, ex);
                        label.setText("Employee NOT created!");
                    } 
            fade(label);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.mEmp.fire())).play();
        });
        add(add, done, 5, 4, HPos.RIGHT);
        
    }

    private void createCity(GridPane add){
        int cid;
        if(id == -1){
            if(cityid.size() > 0)
                cid = cityid.get(cityid.size() - 1) + 1;
            else
                cid = 1;
        } else cid = id;
        
        addLabel(add, 0, 5, "New City", new Font("Open Sans", 30), HPos.LEFT, 3);
        addLabel(add, 0, 7, "ID", new Font("Roboto", 18), HPos.RIGHT);
        addLabel(add, 2, 7, String.valueOf(cid), new Font("Roboto", 18), HPos.LEFT);
        addLabel(add, 0, 8, "Name", new Font("Roboto", 18), HPos.RIGHT);
        
        //Label
        Label clabel = new Label();
        clabel.setFont(new Font("Open Sans", 20));
        add(add, clabel, 3, 7, HPos.CENTER);
        add.setColumnSpan(clabel, 3);
                
        //Name
        TextField cname = new TextField();
        cname.setText("CITY #".concat(String.valueOf(cid)));
        add(add, cname, 2, 8, HPos.CENTER);
        add.setColumnSpan(cname, 2);
        
        if(id != -1)
            cname.setText(city_map.get(id).getName());

        //Button
        Button cdone = new Button("Create");
        cdone.setFont(new Font("Roboto", 20));
        
        cdone.setOnAction((func) -> {
                    try{
                    City c = new City(cid, cname.getText()); 
                    city_map.putAndUpdate(cid, c);
                    
                    //
                    
                    Data.writeData();
                    Data.setData();
                    clabel.setText("City created successfully!");
                    } catch(TimeoutException ex){
                        clabel.setText("NO Internet!");
                    } catch (IOException | InterruptedException | ExecutionException ex) {
                        Logger.getLogger(EmployeeDriver.class.getName()).log(Level.SEVERE, null, ex);
                        clabel.setText("City NOT created!");
                    }
            fade(clabel);
            new Timeline(new KeyFrame(Duration.seconds(1.5), f-> MyBooks.mEmp.fire())).play();
        });
        add(add, cdone, 5, 9, HPos.RIGHT);
        
    }
    private GridPane viewGrid(String label, TabPane tab){
        
        GridPane grid = createGrid(false, 3, 3, 2.5, 95, 2.5, 5, 90, 5);
        GridPane venGrid = createGrid(false, 6, 10, 15, 5, 20, 15, 5, 30, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
        venGrid.setPadding(new Insets(0, 0, 10, 50));
        addLabel(venGrid, 0, 0, label, new Font("Open Sans", 30), HPos.LEFT, 3);
        
        
        TableView table = new TableView();
        //Button
        Button done = new Button("Update");
        done.setFont(new Font("Roboto", 18));
        done.setOnAction((func) -> {
            table.getItems().clear();
            if(label.equalsIgnoreCase("emp"))
            emp_map.forEach((K, V) -> {
                if(K == 0)  return;
                table.getItems().add(new BillRow(K, V));
            });
            else
            city_map.forEach((K, V) -> {
                if(K == 0)  return;
                table.getItems().add(new BillRow(K, V));
            });
                
        });
        add(venGrid, done, 5, 1, HPos.RIGHT);

        TableColumn id = new TableColumn("ID");
        id.setPrefWidth(75);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn cuz = new TableColumn(label.toUpperCase());
        cuz.setPrefWidth(350);
        cuz.setCellValueFactory(new PropertyValueFactory<>(label.toLowerCase()));
        
        table.getColumns().setAll(id, cuz);
        venGrid.add(table, 0, 2);
        venGrid.setRowSpan(table, 7);
        venGrid.setColumnSpan(table, GridPane.REMAINING);
 
        Button edit = new Button("Edit");
        edit.setFont(new Font("Roboto", 18));
        edit.setOnAction((func) -> {
            BillRow r = (BillRow) table.getItems().get(table.getSelectionModel().getFocusedIndex());
            EmployeeDriver cd = new EmployeeDriver(r.getId());
            if(label.equalsIgnoreCase("emp"))
                cd.createEmp(cd.add);
            else
                cd.createCity(cd.add);
            cd.create.add(cd.add, 1, 1);
            Tab node = new Tab("Edit", cd.create);
            tab.getTabs().add(node);
            tab.getSelectionModel().select(node);
        });
        add(venGrid, edit, 0, 9, HPos.LEFT);
        
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
