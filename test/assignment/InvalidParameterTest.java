package assignment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;

/*
    This class tests only invalid paramters, so the critter will not execute any actions.
    The test is considered passed if no exception is thrown and the program runs successfully.
 */
public class InvalidParameterTest {
    private static Interpreter interpreter;
    static CritterTest test;
    private static final String fileName = "species/StupidCritter.cri";

    // make interpreter, environment/harness doesn't really matter for invalid parameter test
    @BeforeAll
    static void setupAll() {
        interpreter = new Interpreter();
        CritterSpecies species = interpreter.loadSpecies(fileName);
        test = new CritterTest(new int[3][3], species, new int[3][3]);
    }

    // set registers to zero and nextCodeLine to 1
    @BeforeEach
    void setup() {
        Arrays.fill(test.register, 0);
        test.setNextCodeLine(1);
    }

    // tests every behavior when provided too many params
    @Test
    void tooManyParams() {
        for (int i=1; i<=16; i++) {
            test.setNextCodeLine(i);
            interpreter.executeCritter(test);
        }
    }

    // tests every behavior when provided String not parseable as int
    @Test
    void nonIntegerParam() {
        for (int i=17; i<=32; i++) {
            //System.out.println(((String[])test.getCode().get(i-1))[0]);
            test.setNextCodeLine(i);
            interpreter.executeCritter(test);
        }
    }

    // test goToLine() when line is out of bounds
    @Test
    void goOutOfBounds() {
        test.setNextCodeLine(33);
        interpreter.executeCritter(test);
    }

    // test goToLine() using r# format when reg is out of bounds
    @Test
    void goOutOfBoundsThroughReg() {
        test.setNextCodeLine(34);
        interpreter.executeCritter(test);
    }

    // test goToLine() using + format when new line is out of bounds
    @Test
    void goOutOfBoundThroughPlusGo() {
        test.setNextCodeLine(36);
        interpreter.executeCritter(test);
    }

    // test goToLine() using - format when new line is out of bounds
    @Test
    void goOutOfBoundThroughMinusGo() {
        test.setNextCodeLine(37);
        interpreter.executeCritter(test);
    }

    // test executeCritter() when the behavior is not comprehensible
    @Test
    void invalidBehavior() {
        test.setNextCodeLine(38);
        interpreter.executeCritter(test);
    }

    // test r# format when # is not a valid register
    @Test
    void invalidRegisterNum() {
        for (int i=39; i<=52; i++) {
            test.setNextCodeLine(i);
            interpreter.executeCritter(test);
        }
    }
}
