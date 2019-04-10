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

import static bookkeeper.Data.emp_map;
import static bookkeeper.Data.ven_map;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author shivam
 */
public class Payment implements Unique, Serializable {
    private static final long serialVersionUID = 4L;
    private boolean type;
    private Integer id, no;
    private LocalDate date;
    private Integer emp;
    private Integer ven;
    private ArrayList<Pair<Customer, Pair<Double, String>>> pay;
    private String part;
    private Double netAmt;
    
    @ServerTimestamp
    public Timestamp getTimestamp() {
        return null;
    }
    

    public Payment(Integer id, Integer no, LocalDate date, Employee emp, ArrayList<Pair<Customer, Pair<Double, String>>> pay, Double netAmt) {
        this.id = id;
        this.no = no;
        this.date = date;
        this.netAmt = netAmt;
        this.emp = emp.getId();
        this.pay = pay;
        this.type = true;
    }

    public Payment(Integer id, LocalDate date, Vendor ven, Double netAmt, String part) {
        this.id = id;
        this.date = date;
        this.ven = ven.getId();
        this.netAmt = netAmt;
        this.part = part;
        this.type = false;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public boolean isType() {
        return type;
    }
    public boolean boolType() {
        return type;
    }

    public String getDate() {
        return date.toString();
    }
    public LocalDate localDate(){
        return date;
    }

    public Integer getVenId() {
        return ven;
    }
    public Vendor ven() {
        return ven_map.get(ven);
    }

    public String getPart() {
        return part;
    }

    public Double getNetAmt() {
        return netAmt;
    }

    public Integer getEmpId() {
        return emp;
    }
    public Employee emp() {
        return emp_map.get(emp);
    }

    public ArrayList<Pair<Customer, Pair<Double, String>>> getPay() {
        return pay;
    }

    @Override
    public String toString() {
        return "Payment{" + "# " + id + '}';
    }

    
    
}
