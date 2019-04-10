/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookkeeper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Shivam
 * @param <K> type of key
 * @param <V> type of value
 */
public class HashMap<K, V> extends java.util.HashMap<K, V>{
    
    public <T extends Unique> V putAndUpdate(K key, T value) throws InterruptedException, ExecutionException, TimeoutException {
        
        BookKeeper.fc.write(key, value);
        return super.put(key, (V)value);
    }
}
