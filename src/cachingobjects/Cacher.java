/*
Copyright (c) 2016 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial
use.
*/
package cachingobjects;

/*
*
* @author @author Ekaterina Alexeevna Ivanova
* @since 21 February 2016
*
*/




@FunctionalInterface
public interface Cacher<R, K, V> {

    /**
     *
     * @param key
     * @param value
     * @return
     */
    R cache(K key, V value);
}
