/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/
package cachingobjects;
/*
@author Ekaterina A. Ivanova (C) 2013
*/
import java.io.Serializable;

interface CacheInterface<R, K,V> extends Cacher<R, K,V>{

    Serializable get( Serializable key);
    Serializable delete( Serializable key);
    
}
