/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/
package cachingobjects;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 @author Ekaterina A. Ivanova (C) 2013
 */
public class Cache implements CacheInterface<Boolean, Serializable, Serializable> {

    public static Supplier<ConcurrentHashMap> mapSupplier = ConcurrentHashMap::new;
    private static long lastTimeRebuilded = System.currentTimeMillis() - 99000; // less then minute after start
    private final FileCache fileCache = new FileCache();
    // private CacheRebuilder rebuilder;
    private final ConcurrentHashMap<Serializable, Serializable> cache = mapSupplier.get();
          //  new ConcurrentHashMap<>();
    // to stay in memory more then 600 seconds
    private final ConcurrentHashMap<Serializable, long[]> systemData = mapSupplier.get();//new ConcurrentHashMap<>();

    private Cache() {
    }

    public Cacher<Boolean, Serializable,Serializable> cacheIt = this::cache;

    // прошло больше двух секунд с момента последнего доступа к объекту?
    private static boolean checkFreq(long[] value) {
        return value[0] + 2000 > System.currentTimeMillis();
    }

    public static Cache getInstance() {
        return CacheInner.INNER_INSTANCE;
    }
    @Override
    public Serializable get(Serializable pkey) {
        // System.currentTimeMillis()
       // Clock.systemDefaultZone().millis()
        // rebuild cache not more then once in 19 second
        if (lastTimeRebuilded + 19000 < System.currentTimeMillis()) {
            Thread t = new Thread(() -> {
                // move to files all less frequently accessed objects

                System.out.println(Thread.currentThread() + " CacheRebuilder said:  cache rebuild was started at " + new Date());
                //Cache.this.cache.forEach((key, serializable2) -> {
                Cache.this.cache.forEachKey(90_000, (key) -> {
                    long[] value = Cache.this.systemData.get(key);
                    // if value accessable less than necessary
                    // then move from memory to disk
                    //System.err.println(Thread.currentThread() + " Cache said: key=" + key + " was last accessed =" + (value[0] - System.currentTimeMillis()) /1000 + " seconds ago ");
                    if (checkFreq(value)) {
                        System.out.println(Thread.currentThread() + " CacheRebuilder said: moving TO file the object with key" + key);
                        Serializable object = Cache.this.cache.get(key);
                        Cache.this.delete(key);
                        Cache.this.fileCache.cache(key, object);
                        // move from disk to memory
                    }
                });
            });
            t.start();
            lastTimeRebuilded = System.currentTimeMillis();
        }
        Serializable value;
        if (cache.containsKey(pkey)) {
            value = cache.get(pkey);

        } else {
            value = fileCache.get(pkey);

            //Serializable object = Cache.this.fileCache.get(pkey);
            if(value != null) {
                System.out.println(Thread.currentThread() + " Cache said: moving FROM file the object with key" + pkey);
                Cache.this.fileCache.delete(pkey);
                Cache.this.cache(pkey, value);
            } else {
                //throw new NullPointerException("ERROR! NOT AN OBJECT IN SERIALIZED FILE");
            }
        }
        if (value != null) {
            updateStatInformation(pkey);
            System.out.println(Thread.currentThread() + " Cache said:  in memory cache has been found key:" + pkey + " and object=" + value);
            return value;
        } else {
            return null;
        }
    }

    @Override
    public Serializable delete(Serializable key) {
        if (cache.containsKey(key)) {
            return cache.remove(key);
        } else {
            return fileCache.delete(key);
        }

    }

    private CacheLevel determCacheLevel(Serializable key) {

        return CacheLevel.MEMORY;

    }

    private void updateStatInformation(Serializable key) {
        long[] tuple = systemData.get(key);
        if (tuple == null || tuple.length < 2)
            return;
        if (tuple[0] + 60000 > System.currentTimeMillis()) {
            tuple[1]++;
        }
        tuple[0] = System.currentTimeMillis();
        systemData.put(key, tuple);
    }



    @Override
    public Boolean cache(Serializable key, Serializable value) {
        switch (determCacheLevel(key)) {
            case MEMORY:
                updateStatInformation(key);
                //TODO: what is that? why place twice?
                systemData.put(key, new long[]{System.currentTimeMillis(), 0});
                System.out.println(Thread.currentThread() + " Cache said: caching to memory with key=" + key + " object=" + value);
                cache.putIfAbsent(key, value);

                break;
            case FILE:
                fileCache.cache(key, value);
                break;
            default:
                cache.putIfAbsent(key, value);
        }
        return true;
    }

    private enum CacheLevel {

        MEMORY, FILE
    }

    private static class CacheInner {

        private static final Cache INNER_INSTANCE = new Cache();
    }

    public class FileCache implements CacheInterface <Boolean, Serializable, Serializable>{

        private final static String dir = "objects/";

        private FileCache() {
            File files = new File(dir);
            if (!files.exists()) {
                files.mkdirs();
            }
        }

        private void checkDir(String path) {
            File subdir = new File(path);
            if (!subdir.exists()) {
                subdir.mkdirs();
            }
        }

        private Predicate<String> ifExist = (path) -> {
            File subdir = new File(path);
            return subdir.exists();
        };



        @Override
        public Boolean cache(Serializable key, Serializable object) {
            if (object == null)
                return false;


            String subDir = getSubdirPath(key);
            checkDir(subDir);
            String filename = getFilenameString(key, subDir);

            //boolean alreadyInCache =  false;
            if (ifExist.test(filename)) {
                System.out.println(Thread.currentThread() + " Cache said: ERROR! file exists with name =" + filename);
                return false;
            }

            try (FileOutputStream s = new FileOutputStream(filename);
                 ObjectOutputStream out =  new ObjectOutputStream(s);
                    ){

                System.out.println(Thread.currentThread() + " FileCache said: saving to file with name:" + filename + " the key=" + key + " object " + object.getClass().getCanonicalName());
                out.writeObject(key);
                out.flush();
                // out.reset();

                out.writeObject(object);
                out.flush();
                out.close();

                return true;
            } catch (IOException ex) {
                Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;

        }

        @Override

        public Serializable get(Serializable key) {
            String subDir =  getSubdirPath(key);
            File dir = new File(subDir);
            File[] files = dir.listFiles();
            if (files == null || files.length <= 0)
                return null;
            for (File test : files) {
                try (
                        InputStream inputStream = new BufferedInputStream(new FileInputStream(test));
                        ObjectInputStream outputStream = new ObjectInputStream(inputStream);
                ) {

                    Serializable keyValue = (Serializable) outputStream.readObject();
                    if (!key.equals(keyValue)) {
                        outputStream.close();
                    } else {
                        System.out.println(Thread.currentThread() + " FileCache said: key found:" + keyValue + " in file " + test);
                        Serializable object = (Serializable) outputStream.readObject();
                        System.out.println(Thread.currentThread() + " FileCache said: reading from file:" + test.getAbsolutePath() + " an object=" + object + " with key " + key);
                        Cache.this.updateStatInformation(keyValue);
                        if (checkFreq(Cache.this.systemData.get(keyValue))) {

                            Cache.this.fileCache.delete(keyValue);
                            Cache.this.cache(keyValue, object);
                        }
                        return object;
                    }
                } catch (ClassNotFoundException ex){
                    Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                } catch(IOException ex) {
                    System.out.println("Can not read file " + test.getAbsolutePath());
                    Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return null;
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Serializable delete(Serializable key) {
            String subDir = getSubdirPath(key);

            String filename = getFilenameString(key, subDir);
            File cached = new File(filename);
            if(cached.exists()) {
                cached.delete();
                System.out.println(Thread.currentThread() + " FileCache said: FILE DELETED:" + filename);
            }
            return true;
        }

        private String getFilenameString(Serializable key, String subDir) {
            return subDir + String.valueOf(System.identityHashCode(key)) + ".ser";
        }

        private String getSubdirPath(Serializable key) {
            return dir + String.valueOf(key) + "/";
        }
    }


}