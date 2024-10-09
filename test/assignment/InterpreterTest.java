package assignment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/* 
 * To test behaviors, there is one behavior line in TestCritter.cri that runs eat().
 * All successful behavior runs will lead to the nextCodeLine() becoming the eat line
 * which will subsequently run, so this can be detected if TestCritter.ate is true.
 * Otherwise, the infect behavior right after the tested behvarior will run.
 * Thus, TestCritter.ate will be false and we will be able to detect the bug.
 *
 * Therefore, nless otherwise mentioned, the test succeesds if TestCritter.ate == true
 */

public class InterpreterTest {
    private static Interpreter interpreter;
    static CritterTest test;
    private static final int dimensions = 3;
    private static final String fileName = "species/TestCritter.cri";
    private static final int ateNum = 40;
    private static final int numBehavior = 47;

    // This will run ONCE before all other tests. It can be useful to setup up
    // global variables and anything needed for all of the tests.
    @BeforeAll
    static void setupAll() {
        interpreter = new Interpreter();
        CritterSpecies species = interpreter.loadSpecies(fileName);
        int[][] environment = new int[dimensions][dimensions];
        int[][] offAngles = new int[dimensions][dimensions];
        /*
        Environment looks like this
        [[WALL, ENEMY, ALLY],
        [EMPTY, CRITTER, EMPTY],
        [ALLY, ENEMY, WALL]]
         */
        environment[0][0] = Critter.WALL;
        environment[0][1] = Critter.ENEMY;
        environment[0][2] = Critter.ALLY;
        environment[1][0] = Critter.EMPTY;
        environment[1][2] = Critter.EMPTY;
        environment[2][0] = Critter.ALLY;
        environment[2][1] = Critter.ENEMY;
        environment[2][2] = Critter.WALL;
        /*
        I don't really need to make Critter instances so I just set each position to a bearing
        to represent the bearing difference if Critter faces that way

        The bearing 2D array looks like this
        [[BAD, 45, 90],
        [135, Critter, 180],
        [225, 270, BAD]]
         */
        offAngles[0][0] = Critter.BAD;
        offAngles[0][1] = 45;
        offAngles[0][2] = 90;
        offAngles[1][0] = 135;
        offAngles[1][2] = 180;
        offAngles[2][0] = 225;
        offAngles[2][1] = 270;
        offAngles[2][2] = Critter.BAD;
        test = new CritterTest(environment, species, offAngles);
    }

    // Reset all variables before each test
    @BeforeEach
    void setupEach() {
        test.ate = false;
        test.hopped = false;
        test.infected = false;
        Arrays.fill(test.register, 0);
        test.hungerLevel = Critter.HungerLevel.SATISFIED;
        test.heading = 0;
        test.setNextCodeLine(1);
    }

    /*
    For all behaviors, a successful run will lead to the critter running eat() (because it ate)
    Otherwise, it will run infect() (ew)
     */


    // make sure hop works by seeing if test.hopped is true
    @Test
    void testHop() {
        test.hop();
        Assertions.assertTrue(test.hopped);
    }

    // use left to change heading and compare to expected
    @Test
    void testLeft() {
        Assertions.assertEquals(test.heading, 0);
        int bearing = 360;
        for (int i=0; i<8; i++) {
            test.left();
            bearing -= 45;
            Assertions.assertEquals(test.heading, bearing);
        }
        test.left();
        Assertions.assertEquals(test.heading, 315);
    }

    // use right to change heading and compare to expected
    @Test
    void testRight() {
        Assertions.assertEquals(test.heading, 0);
        int bearing = 0;
        for (int i=0; i<8; i++) {
            test.right();
            bearing += 45;
            bearing %= 360;
            Assertions.assertEquals(test.heading, bearing);
        }
    }

    // test infect without parameter by checking test.infected
    @Test
    void testInfectNoParam() {
        test.infect();
        Assertions.assertTrue(test.infected);
    }

    // test infect with parameter, same as before
    @Test
    void testInfect() {
        test.infect(2);
        Assertions.assertTrue(test.infected);
    }

    // test eat by checking test.ate
    @Test
    void testEat() {
        test.eat();
        Assertions.assertTrue(test.ate);
    }

    // test go() with normal number
    @Test
    void testGo() {
        test.setNextCodeLine(1);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
        Assertions.assertFalse(test.infected);
    }

    // test go() with r# format
    @Test
    void testRegisterGo() {
        test.setNextCodeLine(2);
        test.setReg(1, ateNum);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
        Assertions.assertFalse(test.infected);
    }


    // test go() with + format
    @Test
    void testRelativeGo() {
        test.setNextCodeLine(3);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
        Assertions.assertFalse(test.infected);
    }

    // test go() with - format
    @Test
    void testNegativeGo() {
        test.setNextCodeLine(46);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    // run ifRandom a bunch of time, ensuring it sometimes runs and sometimes doesn't
    @Test
    void testIfRandom() {
        boolean ate = false;
        boolean infected = false;
        for (int i=0; i<20; i++) {
            test.ate = false;
            test.infected = false;
            test.setNextCodeLine(4);
            interpreter.executeCritter(test);
            if (test.ate) {
                ate = true;
            }
            else if (test.infected) {
                infected = true;
            }
        }
        Assertions.assertTrue(ate && infected);
    }

    // test ifHungry when HUNGRY
    @Test
    void testIfHungry() {
        test.setNextCodeLine(6);
        test.hungerLevel = Critter.HungerLevel.HUNGRY;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    test ifHungry when SATISFIED. Since Critter is not HUNGRY or STARVING,
    test.ate should be false
     */
    @Test
    void ifHungryNotHungry() {
        test.setNextCodeLine(6);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    /*
    test ifHungry when STARVING.
    Since Critter is STARVING, test.ate should be true
     */
    @Test
    void ifHungryStarving() {
        test.setNextCodeLine(6);
        test.hungerLevel = Critter.HungerLevel.STARVING;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Since all methods call the goToLine helper method to jump to a line,
    we will test this function using a specific one. We are using ifHungry.
    This tests goToLine with a r# format number.
     */
    @Test
    void testGeneralGoWithReg() {
        test.setNextCodeLine(8);
        test.hungerLevel = Critter.HungerLevel.HUNGRY;
        test.setReg(1, ateNum);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Same as previous, but using a + format number
     */
    @Test
    void testGeneralGoWithPositiveRelative() {
        test.setNextCodeLine(10);
        test.hungerLevel = Critter.HungerLevel.HUNGRY;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Same as previous, but using a - format number
     */
    @Test
    void testGeneralGoWithNegativeRelative() {
        test.setNextCodeLine(44);
        test.hungerLevel = Critter.HungerLevel.HUNGRY;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Since Critter is STARVING, test.ate should be true
     */
    @Test
    void testIfStarvingStarving() {
        test.setNextCodeLine(12);
        test.hungerLevel = Critter.HungerLevel.STARVING;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Since HUNGRY is not STARVING, test.ate should be false
     */
    @Test
    void testIfStarvingHungry() {
        test.setNextCodeLine(12);
        test.hungerLevel = Critter.HungerLevel.HUNGRY;
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    /*
    Since SATISFIED is not STARVING, test.ate should be false
     */
    @Test
    void testIfStarvingSatisfied() {
        test.setNextCodeLine(12);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    /*
    Points the critter to an empty location in the harness.
    Therefore, ifEmpty() should run and test.ate should be true.
     */
    @Test
    void testIfEmptyEmpty() {
        test.setNextCodeLine(14);
        test.heading = 45;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Points the critter to an occupied location in the harness.
    Therefore, ifEmpty should not run and test.ate should be false.
     */
    @Test
    void testIfEmptyNotEmpty() {
        test.setNextCodeLine(14);
        test.heading = 90;
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }


    // Points the critter to an ally to test ifAlly()
    @Test
    void testIfAlly() {
        test.setNextCodeLine(16);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Points the critter to a non-ally, so test.ate should be false
    since ifAlly() does not run for non-allies.
     */
    @Test
    void testIfAllyNotAlly() {
        test.setNextCodeLine(16);
        test.heading = 315;
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Points the critter to an enemy, so ifEnemy should jump to eat
    @Test
    void testIfEnemy() {
        test.setNextCodeLine(18);
        test.heading = 315;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Points the critter to a non-enemy, so ifEnemy does not run
    and test.ate is false
     */
    @Test
    void testIfEnemyNotEnemy() {
        test.setNextCodeLine(18);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Points the critter to a wall, so ifWall should jump to eat
    @Test
    void testIfWall() {
        test.setNextCodeLine(20);
        test.heading = 270;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Points the critter to a non-wall, so ifWall should not run
    and test.ate should be false
     */
    @Test
    void testIfWallNotWall() {
        test.setNextCodeLine(20);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    /*
    Points the critter to a space with the expected angle
    so ifAngle() should jump to eat
     */
    @Test
    void testIfAngle() {
        test.setNextCodeLine(22);
        test.heading = 315;
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    /*
    Points the critter to a space that does not have the expected angle
    so test.ate should be false
     */
    @Test
    void testIfAngleBadAngle() {
        test.setNextCodeLine(22);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    /*
    Points the critter to a space with no critter and angle Critter.BAD
    so test.ate should be false
     */
    @Test
    void testIfAngleNoCritter() {
        test.setNextCodeLine(22);
        test.heading = 270;
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // writes 100 to reg 5 and checks if reg 5 is 100
    @Test
    void testWrite() {
        test.setNextCodeLine(24);
        interpreter.executeCritter(test);
        Assertions.assertEquals(test.getReg(5), 100);
    }

    /*
    generates two random numbers, sets r1 and r2 to them, runs add,
    checks if r1 is the sum of the two numbers
     */
    @Test
    void testAdd() {
        test.setNextCodeLine(26);
        int r1Num = CritterTest.randomGen.nextInt(50);
        int r2Num = CritterTest.randomGen.nextInt(50);
        test.setReg(1, r1Num);
        test.setReg(2, r2Num);
        interpreter.executeCritter(test);
        Assertions.assertEquals(test.getReg(1), r1Num + r2Num);
    }

    /*
    generates two random numbers, sets r1 and r2 to them, runs sub,
    checks if r1 is the difference of the two numbers
     */
    @Test
    void testSub() {
        test.setNextCodeLine(28);
        int r1Num = CritterTest.randomGen.nextInt(50);
        int r2Num = CritterTest.randomGen.nextInt(50);
        test.setReg(1, r1Num);
        test.setReg(2, r2Num);
        interpreter.executeCritter(test);
        Assertions.assertEquals(test.getReg(1), r1Num - r2Num);
    }

    // increments a random number of times, checks if r1 is that number after
    @Test
    void testInc() {
        int incNum = CritterTest.randomGen.nextInt(50);
        for (int i=0; i<incNum; i++) {
            test.setNextCodeLine(30);
            interpreter.executeCritter(test);
        }
        Assertions.assertEquals(test.getReg(1), incNum);
    }

    // decrements a random number of times, checks if r1 is -num after
    @Test
    void testDec() {
        int decNum = CritterTest.randomGen.nextInt(50);
        for (int i = 0; i< decNum; i++) {
            test.setNextCodeLine(32);
            interpreter.executeCritter(test);
        }
        Assertions.assertEquals(test.getReg(1), -decNum);
    }

    // Sets r1 to be less than r2, checks if ifLt() ran
    @Test
    void testIfLt() {
        test.setNextCodeLine(34);
        test.setReg(1, 90);
        test.setReg(2, 99);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    // Sets r1 to be equal to r2, checks that ifLt() did not run, test.ate is false
    @Test
    void testIfLtButEq() {
        test.setNextCodeLine(34);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Sets r1 to be greater than r2, checks that test.ate is false
    @Test
    void testLfButGt() {
        test.setNextCodeLine(34);
        test.setReg(1, 90);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Sets r1 to be equal to r2, checks that ifEq() ran and test.ate is true
    @Test
    void testEq() {
        test.setNextCodeLine(36);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    // Sets r1 to be less than r2, checks that test.ate is false
    @Test
    void testEqButLt() {
        test.setNextCodeLine(36);
        test.setReg(1, -90);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Sets r1 to be greater than r2, checks that test.ate is false
    @Test
    void testEqButGt() {
        test.setNextCodeLine(36);
        test.setReg(1, 90);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Sets r1 to be greater than r2, checks that ifGt() ran
    @Test
    void testGt() {
        test.setNextCodeLine(38);
        test.setReg(1, 90);
        interpreter.executeCritter(test);
        Assertions.assertTrue(test.ate);
    }

    // Sets r1 to be equal to r2, checks that test.ate is false
    @Test
    void testGtButEq() {
        test.setNextCodeLine(38);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // Sets r1 to be less than r2, checks that test.ate is false
    @Test
    void testGtButLt() {
        test.setNextCodeLine(38);
        test.setReg(1, -90);
        interpreter.executeCritter(test);
        Assertions.assertFalse(test.ate);
    }

    // make sure IOException is caught with invalid file name
    @Test
    void testLoadBadFileName() {
        interpreter.loadSpecies("reallystupidname");
    }

    // ensure behaviors after an empty line are not read
    @Test
    void testLoadEmptyLineBreak() {
        Assertions.assertEquals(test.getCode().size(), numBehavior);
    }

    // test that the only empty lines case mentioned by Ojas creates a species with empty name
    @Test
    void testLoadTwoEmptyLines() {
        CritterSpecies species = interpreter.loadSpecies("species/TwoEmptyLines.cri");
        Assertions.assertEquals(species.getName(), "");
        Assertions.assertEquals(species.getCode().size(), 0);
    }
}
