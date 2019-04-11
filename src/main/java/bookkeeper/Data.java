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

import static bookkeeper.FirebaseController.db;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static HashMap<String, Timestamp> sync_map = new HashMap<>();
    
    public static <T extends Unique> HashMap<Integer, T> read(Class<T> c){
        String s = c.toString();
        s = s.substring(s.indexOf('.')+1);
        HashMap<Integer, T> map = new HashMap<>();
        File file;
        FileInputStream inp;
        ObjectInputStream readObj;
        file = new File(System.getProperty("user.dir")+"\\data\\");
        if(!file.exists())  file.mkdir();
        file = new File(System.getProperty("user.dir")+"\\data\\"+s.concat(".txt"));
        
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
        file = new File(System.getProperty("user.dir")+"\\data\\");
        if(!file.exists())  file.mkdir();
        file = new File(System.getProperty("user.dir")+"\\data\\"+s.concat(".txt"));
        file.createNewFile();
        out = new FileOutputStream(file, false);
        writeObj = new ObjectOutputStream(out);
        for(Map.Entry<Integer, T> entry: map.entrySet()){
            writeObj.writeObject(entry.getValue());
        }
        writeObj.close();
        out.close();
    }
    
    public static <T extends Unique> void update(Class<T> c, Map<Integer, T> map) throws InterruptedException, ExecutionException, TimeoutException{
        String s = c.toString();
        s = s.substring(s.indexOf('.')+1);
        Timestamp time;
        File file;
        FileInputStream inp;
        FileOutputStream out;
        ObjectInputStream readObj;
        ObjectOutputStream writeObj;
        file = new File(System.getProperty("user.dir")+"\\data\\");
        if(!file.exists())  file.mkdir();
        file = new File(System.getProperty("user.dir")+"\\data\\"+"sync.txt");
        try{
        if(file.createNewFile())    time = Timestamp.MIN_VALUE;
        else{
            try {
                inp = new FileInputStream(file);
                readObj = new ObjectInputStream(inp);
                ((HashMap<String, Timestamp>) readObj.readObject())
                        .forEach((K, V) -> {  sync_map.put(K, V);  });
                time = sync_map.getOrDefault(s, Timestamp.MIN_VALUE);
                readObj.close();
                inp.close();
            } catch (EOFException | ClassNotFoundException ex) {
                time = Timestamp.MIN_VALUE;
            }
        }
        ApiFuture<QuerySnapshot> future = db.collection(s).whereGreaterThan("timestamp", time).orderBy("timestamp").get();
        List<QueryDocumentSnapshot> documents = future.get(120, TimeUnit.SECONDS).getDocuments();
        for(DocumentSnapshot document: documents) {
            map.put(Integer.parseInt(document.getId()), document.toObject(c));
            Object obj = document.get("timestamp");
            time = obj.getClass().equals(time.getClass()) ? (Timestamp)obj : Timestamp.of((Date)obj);
        }
        
        sync_map.put(s, time);
        out = new FileOutputStream(file, false);
        writeObj = new ObjectOutputStream(out);
        writeObj.writeObject(sync_map);
        writeObj.close();
        out.close();
        write(c, map);
        }   catch(IOException ex){ 
            System.out.println(ex);
        }
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
    
    public static void updateData() throws InterruptedException, ExecutionException, TimeoutException  {
        update(Customer.class, cust_map);
        update(City.class, city_map);
        update(Employee.class, emp_map);
        update(Product.class, prod_map);
        update(Bill.class, bill_map);
        update(Vendor.class, ven_map);
        update(Payment.class, pay_map);
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
