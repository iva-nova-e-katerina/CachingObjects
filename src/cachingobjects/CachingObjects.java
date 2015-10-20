/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/

package cachingobjects;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/*
@author Ekaterina A. Ivanova (C) 2013
*/
public class CachingObjects {
    
    Cache cache;
    private ConcurrentHashMap<String, long[]> controlData = new ConcurrentHashMap<String, long[]>();
    private final long runTime = System.currentTimeMillis() + 150000; // 2.5 minutes
    

    public static void main(String[] args) {
        int threadsCount = (int) (Math.random() * 15);
        CachingObjects main = new CachingObjects();
        main.cache = Cache.getInstance();
        for(int i = 0; i < threadsCount ; i++){
            Thread t = new Thread(main.new Tester());
            t.start();
        }
            
            
    }
    
   
    
    
    class Tester implements Runnable{

        @Override
        public void run() {
            //int objectsCount = (int) (Math.random() * 100);
            int loop = (int) (Math.random() * 1000000);
            for(int i = 0 ; i < loop && System.currentTimeMillis() < runTime ; i++){
                int branch = (int) (Math.random() * 10);
                if(branch < 5){
                    insertNewObject();
                }else {
                    // generate random key from last 10 minutes
                    long past = (long) (System.currentTimeMillis() - Math.random() * 600000);
                     Serializable ob = demandNewObject(past);
                    
                }
               
            }
        }
        
        void insertNewObject(){
             Test object = new Test();
             object.intType -= Math.random()*10;
             object.doubleType -= Math.random()*10;
             object.stringType += Math.random()*100000;
             long key = System.currentTimeMillis();
             System.out.println(Thread.currentThread() + " said: new object created with key=" + key + "   " + object.toString());
             CachingObjects.this.cache.cache(String.valueOf(key), object);
             CachingObjects.this.controlData.put(String.valueOf(key), new long []{key, 0}); 
        }
        
        
         Serializable demandNewObject(long key){   
            Serializable result = ( Serializable) CachingObjects.this.cache.get(String.valueOf(key));
            System.out.println(Thread.currentThread() + " said: accessing to cache with key=" + key + " with result " + result );
            return  result;
        }
        
    }
    

}
