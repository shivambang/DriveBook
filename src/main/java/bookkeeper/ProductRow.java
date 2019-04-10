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
public class ProductRow {
    private int id;// = Integer.valueOf(sb_pid.getValue().toString());
    private String name, gst;// = sb_pname.getValue().toString();
    private Double qty, vol, netvol, price, netprice, ldisc, pdisc, disc, freight, tax, total, stock;

    public ProductRow(int id, String name, Double stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }
            
    public ProductRow(int id, String name, Double qty, Double vol, Double price, Double total) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.vol = vol;
        this.price = price;
        this.total = total;
        this.netvol = qty*vol;
    }

    public ProductRow(int id, String name, Double qty, Double vol, Double netvol, Double price, Double total) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.vol = vol;
        this.price = price;
        this.total = total;
        this.netvol = netvol;
    }

    public ProductRow(int id, String name, Double qty, Double vol, Double price, Double ldisc, Double pdisc, Double freight, Double tax, Double total) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.vol = vol;
        this.price = price;
        this.ldisc = ldisc;
        this.pdisc = pdisc;
        this.freight = freight;
        this.tax = tax;
        this.total = total;
        this.netvol = qty*vol;
    }

    public ProductRow(int id, String name, Double qty, Double price, Double netprice, Double disc, Double freight, Double tax, Double total) {
        this.id = id;
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.netprice = netprice;
        this.disc = disc;
        this.freight = freight;
        this.tax = tax;
        //this.gst = String.format("%.2f", (netprice - disc + freight) * tax / 100, tax);
        this.gst = String.format("%.1f", tax);
        this.total = total;
    }

    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getStock() {
        return stock;
    }

    public Double getQty() {
        return qty;
    }

    public Double getVol() {
        return vol;
    }

    public Double getNetvol() {
        return netvol;
    }

    public String getPrice() {
        return String.format("%.1f", price);
    }

    public String getTotal() {
        return String.format("%.0f", total);
    }

    public Double getLDisc() {
        return ldisc;
    }

    public Double getPDisc() {
        return pdisc;
    }

    public Double getLdisc() {
        return ldisc;
    }

    public Double getPdisc() {
        return pdisc;
    }

    public String getNetprice() {
        return String.format("%.0f", netprice);
    }

    public String getDisc() {
        return String.format("%.1f", disc);
    }

    public Double getFreight() {
        return freight;
    }

    public Double getTax() {
        return tax;
    }

    public String getGst() {
        return gst;
    }
    
}
