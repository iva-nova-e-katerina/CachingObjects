/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/


package cachingobjects;

import static java.lang.String.valueOf;
import static java.lang.String.valueOf;
import java.util.logging.Logger;

/*
 @author Ekaterina A. Ivanova (C) 2013
 */

class Test implements java.io.Serializable {

    int intType = 6;
    double doubleType = -7.67d;
    String stringType = "hello";

    @Override
    public String toString() {
        return valueOf(intType) + "|" + valueOf(doubleType) + "|" + stringType;
    }
    private static final Logger LOG = Logger.getLogger(Test.class.getName());
}