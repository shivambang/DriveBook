/*
 * Copyright (C) 2019 Shivam
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package bookkeeper;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
/**
 *
 * @author Shivam
 */
public class FirebaseController {
    
    static Firestore db;
    public FirebaseController() throws FileNotFoundException, IOException {
        FileInputStream serviceAccount = new FileInputStream("serviceAccountKey.json");
        FirebaseOptions options;
        options = new FirebaseOptions.Builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .setDatabaseUrl("https://drivebook-firetest.firebaseio.com/")
        .setConnectTimeout(5000)
        .build();
        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();
    }
    
    public <K, V extends Unique> void write(K col, V doc) throws InterruptedException, ExecutionException, TimeoutException {
        DocumentReference docRef = db.collection(doc.getClass().getSimpleName()).document(col.toString());
        ApiFuture<WriteResult> result = docRef.set(doc);
        try{
            System.out.println("Update time : " + result.get(10, TimeUnit.SECONDS).getUpdateTime().toDate());
        } catch(TimeoutException ex){
            result.cancel(true);
            throw new TimeoutException();
        }
    }
    
    public void test() throws Exception{
        DocumentReference docRef = db.collection("users").document("alovelace");
        // Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", "Ada");
        data.put("last", "Lovelace");
        data.put("born", 1815);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        // ...
        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime().toDate());          
    }
    
    
}
