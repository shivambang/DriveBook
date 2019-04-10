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
import static bookkeeper.Data.city_map;
import static bookkeeper.Data.emp_map;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author shivam
 */
public class Customer implements Serializable, Unique {
    private static final long serialVersionUID = 8L;
    private final Integer id;
    private String name;
    private Integer city;
    private Integer emp;
    private Double credit, debit;
    private LocalDate opDate;
    private Double due;
    private Integer days;
    
    @ServerTimestamp
    public Timestamp getTimestamp() {
        return null;
    }
    
        
    public Customer(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Customer(Integer id, String name, City city, Employee emp, Double credit, Double debit, LocalDate opDate, Double due, Integer days) {
        this.id = id;
        this.name = name;
        this.city = city.getId();
        this.emp = emp.getId();
        this.credit = credit;
        this.debit = debit;
        this.opDate = opDate;
        this.due = due;
        this.days = days;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCredit(double credit) {
        this.credit += credit;
    }

    public void setDebit(double debit) {
        this.debit += debit;
    }

    
    public String getName() {
        return name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public Integer getCityId() {
        return city;
    }
    public City city() {
        return city_map.get(city);
    }

    public Integer getEmpId() {
        return emp;
    }
    public Employee emp() {
        return emp_map.get(emp);
    }

    public Double getCredit() {
        return credit;
    }

    public Double getDebit() {
        return debit;
    }

    public String getDate() {
        return opDate.toString();
    }
    public LocalDate localDate(){
        return opDate;
    }

    public Double getDue() {
        return due;
    }

    public Integer getDays() {
        return days;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
