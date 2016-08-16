package gov.nist.toolkit.newValidationSystem;


/**
 *
 */
public class AClass {

    static void doit(List<Class<?>> ss) {
        ss.each { println it.name }
    }

}

AClass.doit([AssertionType1, AssertionType2])
