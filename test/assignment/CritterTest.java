package assignment;

import java.util.List;
import java.util.Random;

public class CritterTest implements Critter {
    int nextCodeLine;
    int[] register;
    HungerLevel hungerLevel;
    int heading;
    boolean hopped;
    boolean ate;
    boolean infected;
    int[][] environment;
    int[][] offAngles;
    static Random randomGen = new Random();
    CritterSpecies critterSpecies;

    public CritterTest(int[][] environment, CritterSpecies critterSpecies, int[][] offAngles) {
        this.environment = environment;
        this.critterSpecies = critterSpecies;
        this.offAngles = offAngles;
        this.nextCodeLine = 1;
        this.register = new int[Critter.REGISTERS];
        this.hungerLevel = HungerLevel.SATISFIED;
    }

    @Override
    public List getCode() {
        return critterSpecies.getCode();
    }

    @Override
    public int getNextCodeLine() {
        return nextCodeLine;
    }

    @Override
    public void setNextCodeLine(int i) {
        nextCodeLine = i;
    }

    @Override
    public int getReg(int i) {
        return register[i];
    }

    @Override
    public void setReg(int i, int i1) {
        register[i] = i1;
    }

    @Override
    public HungerLevel getHungerLevel() {
        return hungerLevel;
    }

    // TestCritter doesn't actually need to move, just check if it hopped
    @Override
    public void hop() {
        hopped = true;
    }

    @Override
    public void left() {
        heading -= 45;
        heading = (heading + 360) % 360;
    }

    @Override
    public void right() {
        heading += 45;
        heading %= 360;
    }

    // TestCritter doesn't actually need to eat, just check if it ate
    @Override
    public void eat() {
        ate = true;
    }

    // TestCritter doesn't actually need to infect, just check if it infected
    @Override
    public void infect() {
        infected = true;
    }

    @Override
    public void infect(int i) {
        infected = true;
    }

    // hard coded because harness will just be a 3x3 2D array with Critter in middle
    @Override
    public int getCellContent(int i) {
        i += heading;
        i %= 360;
        switch(i) {
            case 0:
                return environment[0][1];
            case 45:
                return environment[0][2];
            case 90:
                return environment[1][2];
            case 135:
                return environment[2][2];
            case 180:
                return environment[2][1];
            case 225:
                return environment[2][0];
            case 270:
                return environment[1][0];
            case 315:
                return environment[0][0];
            default:
                System.err.println("getCellContent invalid heading");
                return -1;
        }
    }

    // hard coded because harness will just be a 3x3 2D array with Critter in middle
    @Override
    public int getOffAngle(int i) {
        i += heading;
        i %= 360;
        switch(i) {
            case 0:
                return offAngles[0][1];
            case 45:
                return offAngles[0][2];
            case 90:
                return offAngles[1][2];
            case 135:
                return offAngles[2][2];
            case 180:
                return offAngles[2][1];
            case 225:
                return offAngles[2][0];
            case 270:
                return offAngles[1][0];
            case 315:
                return offAngles[0][0];
            default:
                System.err.println("getOffAngle invalid heading");
                return -1;
        }
    }

    @Override
    public boolean ifRandom() {
        return randomGen.nextBoolean();
    }
}
