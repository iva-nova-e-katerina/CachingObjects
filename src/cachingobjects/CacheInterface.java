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

public interface CacheInterface {
    boolean cache( Serializable key,  Serializable value);
    Serializable get( Serializable key);
    Serializable delete( Serializable key);
    
}
