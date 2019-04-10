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

import java.time.LocalDate;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author shivam
 */
public class BillRow {
    private Integer id, no;
    private LocalDate date;
    private Employee emp;
    private Customer cust;
    private Vendor ven;
    private City city;
    private Product prod;
    private Double price;
    private Double amt;
    private String part;
    private Double iqty, rqty, sale, due;
    private Double due1, due2, due3, due4, due5;
    private Hyperlink link;
    
    public BillRow(Integer id, Customer cust, City city, Product prod, Double iqty, Double sale, Double amt) {
        this.id = id;
        this.cust = cust;
        this.city = city;
        this.prod = prod;
        this.iqty = iqty;
        this.sale = sale;
        this.amt = amt;
    }
    public BillRow(Integer id, Product prod, Double iqty, Double sale, Double amt, Double price) {
        this.id = id;
        this.prod = prod;
        this.iqty = iqty;
        this.sale = sale;
        this.amt = amt;
        this.price = price;
    }

    public BillRow(Integer id, Vendor ven, Product prod, Double sale) {
        this.id = id;
        this.ven = ven;
        this.prod = prod;
        this.sale = sale;
    }

    public BillRow(Integer id, Integer no, LocalDate date, String part, Employee emp, Customer cust, City city, Double amt) {
        this.id = id;
        this.no = no;
        this.date = date;
        this.part = part;
        this.emp = emp;
        this.cust = cust;
        this.city = city;
        this.amt = amt;
    }

    public BillRow(Integer id, LocalDate date, Vendor ven, Product prod, Double price, Double rqty, Double amt) {
        this.id = id;
        this.date = date;
        this.ven = ven;
        this.prod = prod;
        this.price = price;
        this.rqty = rqty;
        this.amt = amt;
    }

    public BillRow(Integer id, Customer cust, Double amt, String part) {
        this.id = id;
        this.cust = cust;
        this.amt = amt;
        this.part = part;
    }

    public BillRow(Integer id, Integer no, Employee emp, Customer cust, Double amt, String part) {
        this.id = id;
        this.no = no;
        this.emp = emp;
        this.cust = cust;
        this.amt = amt;
        this.part = part;
    }
    
    public BillRow(Integer id, Integer no, LocalDate date, Employee emp, Customer cust, Double amt, String part) {
        this.id = id;
        this.no = no;
        this.date = date;
        this.emp = emp;
        this.cust = cust;
        this.amt = amt;
        this.part = part;
    }

    public BillRow(Integer id, Vendor ven, Double amt, String part) {
        this.id = id;
        this.ven = ven;
        this.amt = amt;
        this.part = part;
    }

    public BillRow(Integer id, Product prod, Double iqty, Double rqty) {
        this.id = id;
        this.prod = prod;
        this.iqty = iqty;
        this.rqty = rqty;
    }

    public BillRow(Integer id, Customer cust, City city, Double due1, Double due2, Double due3, Double due4, Double due5) {
        this.id = id;
        this.cust = cust;
        this.city = city;
        this.due1 = due1;
        this.due2 = due2;
        this.due3 = due3;
        this.due4 = due4;
        this.due5 = due5;
    }


    public BillRow(Integer id, Vendor ven, Double due) {
        this.id = id;
        this.ven = ven;
        this.due = due;
    }

    public BillRow(Integer id, Customer cust, City city, Employee emp) {
        this.id = id;
        this.cust = cust;
        this.city = city;
        this.emp = emp;
    }

    public BillRow(Integer id, LocalDate date, Vendor ven, Double iqty, Double rqty, String part) {
        this.id = id;
        this.date = date;
        this.ven = ven;
        this.iqty = iqty;
        this.rqty = rqty;
        this.part = part;
    }

    public BillRow(Integer id, LocalDate date, Customer cust, Double iqty, Double rqty, String part) {
        this.id = id;
        this.date = date;
        this.cust = cust;
        this.part = part;
        this.iqty = iqty;
        this.rqty = rqty;
    }

    public BillRow(Integer id, LocalDate date, Customer cust, Double iqty, Double rqty, Hyperlink link) {
        this.id = id;
        this.date = date;
        this.cust = cust;
        this.iqty = iqty;
        this.rqty = rqty;
        this.link = link;
    }

    public BillRow(Integer id, Employee emp) {
        this.id = id;
        this.emp = emp;
    }

    public BillRow(Integer id, City city) {
        this.id = id;
        this.city = city;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNo() {
        return no;
    }

    public LocalDate getDate() {
        return date;
    }

    public Customer getCust() {
        return cust;
    }

    public Vendor getVen() {
        return ven;
    }

    public City getCity() {
        return city;
    }

    public Employee getEmp() {
        return emp;
    }

    public Product getProd() {
        return prod;
    }

    public Double getPrice() {
        return price;
    }

    public Double getAmt() {
        return amt;
    }

    public String getPart() {
        return part;
    }

    public Hyperlink getLink() {
        return link;
    }

    public Double getIqty() {
        return iqty;
    }

    public Double getRqty() {
        return rqty;
    }

    public Double getSale() {
        return sale;
    }

    public Double getDue() {
        return due;
    }

    public Double getDue1() {
        return due1;
    }

    public Double getDue2() {
        return due2;
    }

    public Double getDue3() {
        return due3;
    }

    public Double getDue4() {
        return due4;
    }

    public Double getDue5() {
        return due5;
    }

    public void setDue(Double due) {
        this.due = due;
    }
    
    
}
