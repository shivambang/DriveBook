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
import java.io.Serializable;

/**
 *
 * @author shivam
 */
public class Product implements Serializable, Unique {
    private static final long serialVersionUID = 6L;
    private int id;
    private String HSN, name;
    private Double vol, tax, pprice, sprice, stock;
    private Vendor vendor;

    public Product(int id, String name) {
        this.id = id;
        this.name = name;
        this.stock = 0.0;
    }

    public Product(int id, String name, double pprice, double sprice) {
        this.id = id;
        this.name = name.toUpperCase();
        this.pprice = pprice;
        this.sprice = sprice;
        this.vol = 1.0;
        this.stock = 0.0;
    }

    public Product(int id, String HSN, String name, Double vol, Double tax, Double pprice, Double sprice, Double stock, Vendor vendor) {
        this.id = id;
        this.HSN = HSN;
        this.name = name;
        this.vol = vol;
        this.tax = tax;
        this.pprice = pprice;
        this.sprice = sprice;
        this.stock = stock;
        this.vendor = vendor;
    }

    public Product(int id, String HSN, String name, Double vol, Double tax, Double pprice, Double sprice, Vendor vendor) {
        this.id = id;
        this.HSN = HSN;
        this.name = name;
        this.vol = vol;
        this.tax = tax;
        this.pprice = pprice;
        this.sprice = sprice;
        this.vendor = vendor;
        this.stock = 0.0;
    }


    @Override
    public Integer getId() {
        return id;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock += stock;
    }

    public String getName() {
        return name;
    }

    public Double getPprice() {
        return pprice;
    }

    public Double getSprice() {
        return sprice;
    }

    public Double getTax() {
        return tax;
    }

    public Double getVol() {
        return vol;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public String getHSN() {
        return HSN;
    }
    
    @Override
    public String toString(){
        return this.name;
    }
}
