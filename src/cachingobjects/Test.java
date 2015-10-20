/*
Copyright (c) 2013 Ivanova Ekaterina Alexeevna. All rights reserved.
PROPRIETARY. For demo purposes only, not for redistribution or any commercial 
use.
*/


package cachingobjects;
/*
 @author Ekaterina A. Ivanova (C) 2013
 */

public class Test implements java.io.Serializable {

    int intType = 6;
    double doubleType = -7.67d;
    String stringType = "hello";

    @Override
    public String toString() {
        return String.valueOf(intType) + "|" + String.valueOf(doubleType) + "|" + stringType;
    }
}