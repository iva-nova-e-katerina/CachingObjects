/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/

package cachingobjects;

import java.io.Serializable;
import java.time.Clock;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/*
* @author Ekaterina A. Ivanova (C) 2013
*/
class CachingObjects {
    
    private static Cache cache;
    private static final ArrayList<String> controlData = new ArrayList<>(1999999);// , long[]> controlData = Cache.mapSupplier.get();// new ConcurrentHashMap<String, long[]>();
    private static final long runTime = System.currentTimeMillis() + 150000; // 2.5 minutes
    

    public static void main(String[] args) {
        int threadsCount = (int) (Math.random() * 15);
        CachingObjects main = new CachingObjects();
        main.cache = Cache.getInstance();
        for(int i = 0; i < threadsCount ; i++){
            Thread t = new Thread(() -> {
                int loop = (int) (Math.random() * 1000000);
                for (int h = 0; h < loop && System.currentTimeMillis() < runTime; h++) {
                    int branch = (int) (Math.random() * 10);
                    if (branch < 5) {
                        insertNewObject();
                    } else if (controlData.size() > 0) {

                        int past = (int) (Math.random() * controlData.size());//(long) (System.currentTimeMillis() - Math.random() * 600000);

                        demandNewObject(controlData.get(past));

                    }

                }} /*main.new Tester()*/);
            t.start();
        }
            
            
    }

    static void insertNewObject(){
        Test object = new Test();
        object.intType -= Math.random()*10;
        object.doubleType -= Math.random()*10;
        object.stringType += Math.random()*100000;
        long key = System.currentTimeMillis();
        System.out.println(Thread.currentThread() + " said: new object created with key=" + key + "   " + object.toString());
        cache.cacheIt.cache(String.valueOf(key), object);
        controlData.add(String.valueOf(key));//, new long []{key, 0});
    }


    static Serializable demandNewObject(String key){
        Serializable result = cache.get(key);
        if(result != null ) {
            System.out.println("");
            System.out.println(Thread.currentThread() + " said: ACCESSING to cache with key=" + key + " with result " + result);
            System.out.println("");
        }

        return  result;
    }
    
    


}
