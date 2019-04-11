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

/**
 *
 * @author shivam
 */
import static bookkeeper.Data.*;
import static bookkeeper.Utils.createGrid;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
        
public class MyBooks extends Application implements Initializable{
    
    public static int cid, vid;
    public static LocalDate date;
    public static Hyperlink sBill, sCus, sPay, pVen, pBill, mEmp, mPro, mStk, pPay, sale_repo, sale_due, pur_repo, pur_due;
    Node salereg, customer, vendor, purchasereg, employee, product, stock, repo;
    TabPane salebill, payment, purchasebill, vpay, tabPane;
    
    @FXML
    public GridPane myBooks;

    @FXML
    public RowConstraints rsale, rpur, rmisc;
    public GridPane sale_opt, misc_opt, pur_opt;
    public Hyperlink sale, misc, pur;
    public Hyperlink sbill, sreg, spay, scus;
    public Hyperlink pbill, preg, ppay, pven;
    public Hyperlink mleg, mstk, memp, mpro;
    public Hyperlink rep;
    public TabPane s_bill;
    
    @FXML
    private void sale(ActionEvent event){
        color();
        sale.setVisited(true);
        collapse();
        rsale.setPercentHeight(25.0);
        sale_opt.setVisible(true);
    }
    
    @FXML
    private void misc(ActionEvent event){
        color();
        misc.setVisited(true);
        collapse();
        rmisc.setPercentHeight(25.0);
        misc_opt.setVisible(true);
    }
    
    @FXML
    private void pur(ActionEvent event){
        color();
        pur.setVisited(true);
        collapse();
        rpur.setPercentHeight(25.0);
        pur_opt.setVisible(true);
    }
    
    @FXML
    private void report(ActionEvent event) throws IOException{
        color();
        rep.setVisited(true);
        collapse();
        hide();
        FXMLLoader fxml = new FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
        Node node = fxml.load();
        GridPane grid = createGrid(false, 1, 1, 100, 100);
        ScrollPane sp = (ScrollPane) node;
        grid.add(sp, 0, 0);
        repo = grid;
        myBooks.add(repo, 1, 0);
        repo.requestFocus();
        sale_repo = (Hyperlink) fxml.getNamespace().get("sale_repo");
        sale_repo.setOnAction((func) -> { hide(); sale_repo(); });
        sale_due = (Hyperlink) fxml.getNamespace().get("sale_due");
        sale_due.setOnAction((func) -> { hide(); sale_due(); });
        pur_repo = (Hyperlink) fxml.getNamespace().get("pur_repo");
        pur_repo.setOnAction((func) -> { hide(); pur_repo(); });
        pur_due = (Hyperlink) fxml.getNamespace().get("pur_due");
        pur_due.setOnAction((func) -> { hide(); pur_due(); });
    }
    
    @FXML
    private void sbill(ActionEvent event) throws Throwable{
        color();
        sbill.setVisited(true);
        hide();
        //salebill = (Node) FXMLLoader.load(getClass().getResource("saleBill.fxml"));
        //myBooks.add(salebill, 1, 0);
        SaleBill sb = new SaleBill();
        tab(sb.node());
    }
    
    
    @FXML
    private void sreg(ActionEvent event){
        color();
        sreg.setVisited(true);
        hide();
        SaleRegister sr = new SaleRegister();
        salereg = sr.node();
        myBooks.add(salereg, 1, 0);
        salereg.requestFocus();
    }
    
    @FXML
    private void spay(ActionEvent event){
        color();
        spay.setVisited(true);
        hide();
        CustomerPayment pd = new CustomerPayment();
        tab(pd.node());
    }
    
    @FXML
    private void scus(ActionEvent event){
        color();
        scus.setVisited(true);
        hide();
        CustomerDriver cd = new CustomerDriver();
        tabPane = cd.node();
        myBooks.add(tabPane, 1, 0);
    }
    
    @FXML
    private void pbill(ActionEvent event){
        color();
        pbill.setVisited(true);
        hide();
        PurchaseBill pb = new PurchaseBill();
        tab(pb.node());
    }
    
    @FXML
    private void preg(ActionEvent event){
        color();
        preg.setVisited(true);
        hide();
        PurchaseRegister pr = new PurchaseRegister();
        purchasereg = pr.node();
        myBooks.add(purchasereg, 1, 0);
        purchasereg.requestFocus();
    }
    
    @FXML
    private void ppay(ActionEvent event){
        color();
        ppay.setVisited(true);
        hide();
        VendorPayment vp = new VendorPayment();
        tab(vp.node());
    }
    
    @FXML
    private void pven(ActionEvent event){
        color();
        pven.setVisited(true);
        hide();
        VendorDriver vd = new VendorDriver();
        vendor = vd.node();
        myBooks.add(vendor, 1, 0);
        vendor.requestFocus();
    }
    
    @FXML
    private void mleg(ActionEvent event){
        color();
        mleg.setVisited(true);
        hide();
        Ledger l = new Ledger();
        tabPane = l.node();
        myBooks.add(tabPane, 1, 0);
        tabPane.requestFocus();
    }
    
    @FXML
    private void mstk(ActionEvent event){
        color();
        mstk.setVisited(true);
        hide();
        Stock s = new Stock();
        stock = s.node();
        myBooks.add(stock, 1, 0);
        stock.requestFocus();
    }
    
    @FXML
    private void mpro(ActionEvent event){
        color();
        mpro.setVisited(true);
        hide();
        ProductDriver pd = new ProductDriver();
        product = pd.node(-1);
        myBooks.add(product, 1, 0);
        product.requestFocus();
    }
    
    @FXML
    private void memp(ActionEvent event){
        color();
        memp.setVisited(true);
        hide();
        EmployeeDriver ed = new EmployeeDriver();
        employee = ed.node();
        myBooks.add(employee, 1, 0);
        employee.requestFocus();
    }
     
    private void sale_repo(){
        sale_repo.setVisited(false);
        SaleReport sr = new SaleReport();
        repo = sr.node();
        myBooks.add(repo, 1, 0);
        repo.requestFocus();
    }

    private void sale_due(){
        sale_due.setVisited(false);
        SaleDues sd = new SaleDues();
        repo = sd.node();
        myBooks.add(repo, 1, 0);
        repo.requestFocus();
    }

    private void pur_repo(){
        pur_repo.setVisited(false);
        PurchaseReport sr = new PurchaseReport();
        repo = sr.node();
        myBooks.add(repo, 1, 0);
        repo.requestFocus();
    }

    private void pur_due(){
        pur_due.setVisited(false);
        PurchaseDues sr = new PurchaseDues();
        repo = sr.node();
        myBooks.add(repo, 1, 0);
        repo.requestFocus();
    }

    private void tab(Tab tab){
        tabPane = new TabPane();
        tabPane.getTabs().add(tab);
        tabPane.getTabs().get(0).setClosable(false);
        myBooks.add(tabPane, 1, 0);
        tabPane.requestFocus();
        
    }
    private void collapse(){
        sale_opt.setVisible(false);
        pur_opt.setVisible(false);
        misc_opt.setVisible(false);
        rsale.setPercentHeight(0.0);
        rpur.setPercentHeight(0.0);
        rmisc.setPercentHeight(0.0);
    }
    
    private void color(){
        sale.setVisited(false);
        pur.setVisited(false);
        misc.setVisited(false);
        sbill.setVisited(false);
        sreg.setVisited(false);
        spay.setVisited(false);
        scus.setVisited(false);
        pbill.setVisited(false);
        ppay.setVisited(false);
        pven.setVisited(false);
        preg.setVisited(false);
        mleg.setVisited(false);
        mstk.setVisited(false);
        mpro.setVisited(false);
        memp.setVisited(false);
        rep.setVisited(false);
    }
    private void hide(){
        
        myBooks.getChildren().remove(salebill);
        myBooks.getChildren().remove(salereg);
        myBooks.getChildren().remove(payment);
        myBooks.getChildren().remove(customer);
        myBooks.getChildren().remove(purchasebill);
        myBooks.getChildren().remove(purchasereg);
        myBooks.getChildren().remove(vpay);
        myBooks.getChildren().remove(vendor);
        myBooks.getChildren().remove(employee);
        myBooks.getChildren().remove(product);
        myBooks.getChildren().remove(stock);
        myBooks.getChildren().remove(repo);
        myBooks.getChildren().remove(tabPane);
    }
        
  
    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        
        sBill = sbill;
        sCus = scus;
        pVen = pven;
        sPay = spay;
        pBill = pbill;
        mEmp = memp;
        mPro = mpro;
        mStk = mstk;
        pPay = ppay;
        
        collapse();
        //hide();
                
        cust_map.put(0, new Customer(0, "ALL"));
        emp_map.put(0, new Employee(0, "ALL"));
        city_map.put(0, new City(0, "ALL"));
        ven_map.put(0, new Vendor(0, "ALL"));
        prod_map.put(0, new Product(0, "ALL"));
        
        setData();
    }

    @Override
    public void start(Stage stage) throws IOException{
        stage.hide();
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/myBooks.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("/css/myBooks.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

}
