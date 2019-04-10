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
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.io.Serializable;

/**
 *
 * @author shivam
 */
class Employee implements Serializable, Unique {
    private static final long serialVersionUID = 2L;
    private final Integer id;
    private String name;
    private double sale, credit, debit;
    
    @ServerTimestamp
    public Timestamp getTimestamp() {
        return null;
    }
    
    
    Employee(int id, String name){
        this.id = id;
        this.name = name;
        sale = 0;
        credit = 0;
        debit = 0;
    }

    public Employee(Integer id, String name, double credit, double debit) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.debit = debit;
    }
    
    public Employee(int id, String name, double sale, double credit, double debit){
        this.id = id;
        this.name = name;
        this.sale = sale;
        this.credit = credit;
        this.debit = debit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSale(double sale) {
        this.sale += sale;
    }

    public void setCredit(double credit) {
        this.credit += credit;
    }

    public void setDebit(double debit) {
        this.debit += debit;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSale() {
        return sale;
    }

    public double getCredit() {
        return credit;
    }

    public double getDebit() {
        return debit;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
