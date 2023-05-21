

package cachingobjects;

import static cachingobjects.Cache.getInstance;
import java.io.Serializable;
import static java.lang.Math.random;
import static java.lang.String.valueOf;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;
import java.util.ArrayList;
import java.util.logging.Logger;

/*
* @author Ekaterina A. Ivanova (C) 2013
*/
class CachingObjects {
    
    private static Cache cache;
    private static final ArrayList<String> controlData = new ArrayList<>(1999999);// , long[]> controlData = Cache.mapSupplier.get();// new ConcurrentHashMap<String, long[]>();
    private static final long runTime = currentTimeMillis() + 150000; // 2.5 minutes
    

    public static void main(String[] args) {
        int threadsCount = (int) (random() * 15);
        CachingObjects main = new CachingObjects();
        cache = getInstance();
        for(int i = 0; i < threadsCount ; i++){
            Thread t = new Thread(() -> {
                int loop = (int) (random() * 1000000);
                for (int h = 0; h < loop && currentTimeMillis() < runTime; h++) {
                    int branch = (int) (random() * 10);
                    if (branch < 5) {
                        insertNewObject();
                    } else if (controlData.size() > 0) {

                        int past = (int) (random() * controlData.size());//(long) (System.currentTimeMillis() - Math.random() * 600000);

                        demandNewObject(controlData.get(past));

                    }

                }} /*main.new Tester()*/);
            t.start();
        }
            
            
    }

    static void insertNewObject(){
        Test object = new Test();
        object.intType -= random()*10;
        object.doubleType -= random()*10;
        object.stringType += random()*100000;
        long key = currentTimeMillis();
        out.println(currentThread() + " said: new object created with key=" + key + "   " + object.toString());
        cache.cacheIt.cache(valueOf(key), object);
        controlData.add(valueOf(key));//, new long []{key, 0});
    }


    static Serializable demandNewObject(String key){
        Serializable result = cache.get(key);
        if(result != null ) {
            out.println("");
            out.println(currentThread() + " said: ACCESSING to cache with key=" + key + " with result " + result);
            out.println("");
        }

        return  result;
    }
    private static final Logger LOG = Logger.getLogger(CachingObjects.class.getName());
    
    


}
