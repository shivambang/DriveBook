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
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
/**
 *
 * @author shivam
 */
public class Bill implements Serializable, Unique {
    private static final long serialVersionUID = 8L;
    private int id, no;
    private int type; //True = Sale; False = Purchase;
    private LocalDate date;
    private Integer cust;
    private Integer emp;
    private Integer ven;
    private ArrayList<Pair<Product, ArrayList<Double>>> prod;
    private double disc, netAmt;
    
    @ServerTimestamp
    public Timestamp getTimestamp() {
        return null;
    }
    
    public Bill(int id, LocalDate date, Employee emp, Customer cust, ArrayList<Pair<Product, ArrayList<Double>>> prod, double disc, double netAmt) {
        this.type = 1;
        this.id = id;
        this.date = date;
        this.cust = cust.getId();
        this.emp = emp.getId();
        this.prod = prod;
        this.disc = disc;
        this.netAmt = netAmt;
    }

    public Bill(int id, int no, int type, LocalDate date, Employee emp, Customer cust, ArrayList<Pair<Product, ArrayList<Double>>> prod, double disc, double netAmt) {
        this.type = type;
        this.id = id;
        this.no = no;
        this.date = date;
        this.cust = cust.getId();
        this.emp = emp.getId();
        this.prod = prod;
        this.disc = disc;
        this.netAmt = netAmt;
    }

    public Bill(int id, LocalDate date, Vendor ven, ArrayList<Pair<Product, ArrayList<Double>>> prod, double disc, double netAmt) {
        this.type = 0;
        this.id = id;
        this.date = date;
        this.ven = ven.getId();
        this.prod = prod;
        this.disc = disc;
        this.netAmt = netAmt;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public boolean boolType() {
        return type != 0;
    }

    public int getType() {
        return type;
    }

    public Integer getVenId() {
        return ven;
    }
    public Vendor ven() {
        return ven_map.get(ven);
    }
    
    @Override
    public Integer getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public String getDate() {
        return date.toString();
    }

    public LocalDate localDate(){
        return date;
    }
    
    public Integer getCustId() {
        return cust;
    }
    public Customer cust()  {
        return cust_map.get(cust);
    }
    
    public Integer getEmpId() {
        return emp;
    }
    public Employee emp() {
        return emp_map.get(emp);
    }

    public ArrayList<Pair<Product, ArrayList<Double>>> getProd() {
        return prod;
    }

    public double getDisc() {
        return disc;
    }

    public double getNetAmt() {
        return netAmt;
    }

    @Override
    public String toString() {
        return "Bill{" + "# " + id + '}';
    }

}
