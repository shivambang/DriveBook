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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 *
 * @author shivam
 */
public class Data {
    public static ArrayList<Employee> emp;
    public static ArrayList<City> city;
    public static ArrayList<Customer> cust;
    public static ArrayList<Product> prod;
    public static ArrayList<Bill> bill;
    public static ArrayList<Vendor> ven;
    public static ArrayList<Payment> pay;
    public static ArrayList<Integer> empid, custid, prodid, billid, cityid, venid, payid;
    public static HashMap<Integer, Customer> cust_map = new HashMap<>();
    public static HashMap<Integer, Employee> emp_map = new HashMap<>();
    public static HashMap<Integer, Product> prod_map = new HashMap<>();
    public static HashMap<Integer, City> city_map = new HashMap<>();
    public static HashMap<Integer, Bill> bill_map = new HashMap<>();
    public static HashMap<Integer, Vendor> ven_map = new HashMap<>();
    public static HashMap<Integer, Payment> pay_map = new HashMap<>();
    
    public static <T extends Unique> HashMap<Integer, T> read(Class<T> c){
        String s = c.toString();
        s = s.substring(s.indexOf('.')+1);
        HashMap<Integer, T> map = new HashMap<>();
        File file;
        FileInputStream inp;
        ObjectInputStream readObj;
        file = new File(s.concat(".txt"));
        
        try {
            file.createNewFile();
            inp = new FileInputStream(file);
            readObj = new ObjectInputStream(inp);
        
        while(true){
            try{
                T t = (T) readObj.readObject();
                map.put(t.getId(), t);
            } catch(EOFException | ClassNotFoundException ex){
                //Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
        readObj.close();
        inp.close();
        } catch (IOException ex) {
            return map;
        }
        return map;
    }
    
    public static <T extends Unique> void write(Class<T> c, Map<Integer, T> map) throws IOException{
        String s = c.toString();
        s = s.substring(s.indexOf('.')+1);
        File file;
        FileOutputStream out;
        ObjectOutputStream writeObj;
        file = new File(s.concat(".txt"));
        file.createNewFile();
        out = new FileOutputStream(file, false);
        writeObj = new ObjectOutputStream(out);
        for(Map.Entry<Integer, T> entry: map.entrySet()){
            writeObj.writeObject(entry.getValue());
        }
        writeObj.close();
        out.close();
    }
    
    public static void readData(){
        SortedSet<Integer> set;
        cust_map = read(Customer.class);
        cust = new ArrayList<>(cust_map.values());
        set = new TreeSet<>(cust_map.keySet());
        custid = new ArrayList<>(set);
        emp_map = read(Employee.class);
        emp = new ArrayList<>(emp_map.values());
        set = new TreeSet<>(emp_map.keySet());
        empid = new ArrayList<>(set);
        city_map = read(City.class);
        city = new ArrayList<>(city_map.values());
        set = new TreeSet<>(city_map.keySet());
        cityid = new ArrayList<>(set);
        prod_map = read(Product.class);
        prod = new ArrayList<>(prod_map.values());
        set = new TreeSet<>(prod_map.keySet());
        prodid = new ArrayList<>(set);
        bill_map = read(Bill.class);
        bill = new ArrayList<>(bill_map.values());
        set = new TreeSet<>(bill_map.keySet());
        billid = new ArrayList<>(set);
        
        ven_map = read(Vendor.class);
        ven = new ArrayList<>(ven_map.values());
        set = new TreeSet<>(ven_map.keySet());
        venid = new ArrayList<>(set);
        pay_map = read(Payment.class);
        pay = new ArrayList<>(pay_map.values());
        set = new TreeSet<>(pay_map.keySet());
        payid = new ArrayList<>(set);

    }
    
    public static void setData(){
        SortedSet<Integer> set;
        cust = new ArrayList<>(cust_map.values());
        set = new TreeSet<>(cust_map.keySet());
        custid = new ArrayList<>(set);
        emp = new ArrayList<>(emp_map.values());
        set = new TreeSet<>(emp_map.keySet());
        empid = new ArrayList<>(set);
        city = new ArrayList<>(city_map.values());
        set = new TreeSet<>(city_map.keySet());
        cityid = new ArrayList<>(set);
        prod = new ArrayList<>(prod_map.values());
        set = new TreeSet<>(prod_map.keySet());
        prodid = new ArrayList<>(set);
        bill = new ArrayList<>(bill_map.values());
        set = new TreeSet<>(bill_map.keySet());
        billid = new ArrayList<>(set);
        
        ven = new ArrayList<>(ven_map.values());
        set = new TreeSet<>(ven_map.keySet());
        venid = new ArrayList<>(set);
        pay = new ArrayList<>(pay_map.values());
        set = new TreeSet<>(pay_map.keySet());
        payid = new ArrayList<>(set);

    }
    
    public static void writeData() throws IOException{
        write(Customer.class, cust_map);
        write(City.class, city_map);
        write(Employee.class, emp_map);
        write(Product.class, prod_map);
        write(Bill.class, bill_map);
        write(Vendor.class, ven_map);
        write(Payment.class, pay_map);
    }
}
