package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for loading critter species from text files and interpreting the
 * simple Critter language.
 * <p>
 * For more information on the purpose of the below two methods, see the
 * included API/ folder and the project description.
 */
public class Interpreter implements CritterInterpreter {
    // set to false before each execution, set to true after an error to break from while
    static boolean errorCaught;

    // continuously runs behavior lines until an action is taken or error
    public void executeCritter(Critter c) {
        errorCaught = false;
        List<String[]> critterCode = c.getCode();
        int nextLine = c.getNextCodeLine();
        String[] behaviorLine;
        while (!errorCaught) {
            nextLine = c.getNextCodeLine();
            if (nextLine > critterCode.size() || nextLine < 1) {
                System.err.println("Code line out of bounds");
                return;
            }
            behaviorLine = critterCode.get(nextLine - 1);
            if (behaviorLine == null) {
                System.err.println("Behavior line is null");
                return;
            }
            String behavior = behaviorLine[0];
            if (behavior == null) {
                System.err.println("Behavior name is null");
                return;
            }
            // calls helper method depending on behavior name
            switch (behavior) {
                case "hop":
                    c.hop();
                    c.setNextCodeLine(nextLine + 1);
                    return;
                case "left":
                    c.left();
                    c.setNextCodeLine(nextLine + 1);
                    return;
                case "right":
                    c.right();
                    c.setNextCodeLine(nextLine + 1);
                    return;
                case "infect":
                    infect(behaviorLine, c);
                    return;
                case "eat":
                    c.eat();
                    c.setNextCodeLine(nextLine + 1);
                    return;
                case "go": {
                    go(behaviorLine, c);
                    break;
                }
                case "ifrandom": {
                    ifrandom(behaviorLine, c);
                    break;
                }
                case "ifhungry": {
                    ifhungry(behaviorLine, c);
                    break;
                }
                case "ifstarving": {
                    ifstarving(behaviorLine, c);
                    break;
                }
                case "ifempty": {
                    ifempty(behaviorLine, c);
                    break;
                }
                case "ifally": {
                    ifally(behaviorLine, c);
                    break;
                }
                case "ifenemy": {
                    ifenemy(behaviorLine, c);
                    break;
                }
                case "ifwall": {
                    ifwall(behaviorLine, c);
                    break;
                }
                case "ifangle": {
                    ifangle(behaviorLine, c);
                    break;
                }
                case "write": {
                    write(behaviorLine, c);
                    break;
                }
                case "add": {
                    add(behaviorLine, c);
                    break;
                }
                case "sub": {
                    sub(behaviorLine, c);
                    break;
                }
                case "inc": {
                    inc(behaviorLine, c);
                    break;
                }
                case "dec": {
                    dec(behaviorLine, c);
                    break;
                }
                case "iflt": {
                    iflt(behaviorLine, c);
                    break;
                }
                case "ifeq": {
                    ifeq(behaviorLine, c);
                    break;
                }
                case "ifgt": {
                    ifgt(behaviorLine, c);
                    break;
                }
                default: {
                    System.err.println("Unknown behavior");
                    return;
                }
            }
        }

    }

    // loads species and reads behavior commands from .cri file
    public CritterSpecies loadSpecies(String filename) {
        String name = null;
        List<String[]> behavior = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            while (br.ready()) {
                if (name == null) {
                    name = br.readLine();
                }
                else {
                    String next = br.readLine();
                    next = next.trim();
                    if (!next.isEmpty()) {
                        //ignores white space
                        behavior.add(next.split(" +"));
                    }
                    else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IOException during loadSpecies");
            return null;
        }
        return new CritterSpecies(name, behavior);
    }

    // helper, ensures behavior line parameters are not null
    private boolean hasNulls (String[] strArray) {
        for (String s: strArray) {
            if (s == null) {
                System.err.println("Null found in behavior line input");
                errorCaught = true;
                return true;
            }
        }
        return false;
    }

    // checks if parameter is parseable as int
    private boolean isNotInteger(String str) {
        try {
            Integer.parseInt(str);
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Integer cannot be parsed in behavior line input");
            errorCaught = true;
            return true;
        }
    }

    // helper method an actual "go" command
    private void go (String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        if (behaviorLine.length != 2) {
            System.err.println("Invalid number of parameters for 'go' command");
            errorCaught = true;
            return;
        }
        String jumpNumber = behaviorLine[1];
        goToLine(jumpNumber, c);
    }

    // helper method for any behavior that jumps to a line
    private void goToLine(String jumpNumber, Critter c) {
        int goToLine;
        // relative jump
        if (jumpNumber.charAt(0) == '+' || jumpNumber.charAt(0) == '-') {
            if (isNotInteger(jumpNumber.substring(1))) {
                return;
            }
            int lineChange = Integer.parseInt(jumpNumber);
            goToLine = c.getNextCodeLine() + lineChange;
        }
        // register jump
        else if (jumpNumber.charAt(0) == 'r') {
            if (isNotInteger(jumpNumber.substring(1))) {
                return;
            }
            int regNum = Integer.parseInt(jumpNumber.substring(1));
            if (invalidReg(regNum)) {
                return;
            }
            goToLine = c.getReg(regNum);
        }
        // absolute jump
        else {
            if (isNotInteger(jumpNumber)) {
                return;
            }
            goToLine = Integer.parseInt(jumpNumber);
        }
        c.setNextCodeLine(goToLine);
    }

    // infect helper method
    private void infect(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        int infectParameter;
        // checks for optional parameter
        if (behaviorLine.length == 2) {
            if (isNotInteger(behaviorLine[1])) {
                return;
            }
            infectParameter = Integer.parseInt(behaviorLine[1]);
        }
        // defaults to 1 if optional parameter not provided
        else if (behaviorLine.length == 1) {
            infectParameter = 1;
        }
        // invalid num of parameters
        else {
            System.err.println("infect invalid number of parameters");
            errorCaught = true;
            return;
        }
        c.setNextCodeLine(c.getNextCodeLine() + 1);
        c.infect(infectParameter);
    }

    // ifrandom() helper
    private void ifrandom(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 2) {
            System.err.println("ifrandom invalid number of parameters");
            errorCaught = true;
            return;
        }
        String randomParameter = behaviorLine[1];
        if (c.ifRandom()) {
            goToLine(randomParameter, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // if hungry helper
    private void ifhungry(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 2) {
            System.err.println("ifhungry invalid number of parameters");
            errorCaught = true;
            return;
        }
        String parameter = behaviorLine[1];
        Critter.HungerLevel hungerLevel = c.getHungerLevel();
        // either HUNGRY or STARVING will lead to a jump
        if (hungerLevel == Critter.HungerLevel.HUNGRY
                || hungerLevel == Critter.HungerLevel.STARVING) {
            goToLine(parameter, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifstarving helper
    private void ifstarving(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 2) {
            System.err.println("ifstarving invalid number of parameters");
            errorCaught = true;
            return;
        }
        String parameter = behaviorLine[1];
        Critter.HungerLevel hungerLevel = c.getHungerLevel();
        // STARVING will lead to a jump
        if (hungerLevel == Critter.HungerLevel.STARVING) {
            goToLine(parameter, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifempty helper
    private void ifempty(String behaviorLine[], Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("ifempty invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1])) {
            return;
        }
        int bearing = Integer.parseInt(behaviorLine[1]);
        String lineNum = behaviorLine[2];
        // empty cell will lead to a jump
        if (c.getCellContent(bearing) == 0) {
            goToLine(lineNum, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifally helper
    private void ifally(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("ifally invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1])) {
            return;
        }
        int bearing = Integer.parseInt(behaviorLine[1]);
        String lineNum = behaviorLine[2];
        // cell with ally will lead to a jump
        if (c.getCellContent(bearing) == Critter.ALLY) {
            goToLine(lineNum, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifenemy helper
    private void ifenemy(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("ifenemy invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1])) {
            return;
        }
        int bearing = Integer.parseInt(behaviorLine[1]);
        String lineNum = behaviorLine[2];
        // cell with enemy will lead to a jump
        if (c.getCellContent(bearing) == Critter.ENEMY) {
            goToLine(lineNum, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifwall helper
    private void ifwall(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("ifwall invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1])) {
            return;
        }
        int bearing = Integer.parseInt(behaviorLine[1]);
        String lineNum = behaviorLine[2];
        // cell with wall will lead to a jump
        if (c.getCellContent(bearing) == Critter.WALL) {
            goToLine(lineNum, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifangle helper
    private void ifangle(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 4) {
            System.err.println("ifangle invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1])) {
            return;
        }
        if (isNotInteger(behaviorLine[2])) {
            return;
        }
        int bearing1 = Integer.parseInt(behaviorLine[1]);
        int offAngle = Integer.parseInt(behaviorLine[2]);
        String lineNum = behaviorLine[3];
        // equal offangle will lead to a jump
        if (c.getOffAngle(bearing1) == offAngle) {
            goToLine(lineNum, c);
        } else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // write helper
    private void write(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("write invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[2])) {
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        int newValue = Integer.parseInt(behaviorLine[2]);
        int registerNum = Integer.parseInt(behaviorLine[1].substring(1));
        if (invalidReg(registerNum)) {
            return;
        }
        // write new value into register
        c.setReg(registerNum, newValue);
        c.setNextCodeLine(c.getNextCodeLine() + 1);
    }

    // add helper
    private void add(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("add invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        if (isNotInteger(behaviorLine[2].substring(1))) {
            return;
        }
        int registerNum1 = Integer.parseInt(behaviorLine[1].substring(1));
        int registerNum2 = Integer.parseInt(behaviorLine[2].substring(1));
        if (invalidReg(registerNum1) || invalidReg(registerNum2)) {
            return;
        }
        // add values into register
        int newValue = c.getReg(registerNum1) + c.getReg(registerNum2);
        c.setReg(registerNum1, newValue);
        c.setNextCodeLine(c.getNextCodeLine() + 1);
    }

    // sub helper
    private void sub(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 3) {
            System.err.println("sub invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        if (isNotInteger(behaviorLine[2].substring(1))) {
            return;
        }
        int registerNum1 = Integer.parseInt(behaviorLine[1].substring(1));
        int registerNum2 = Integer.parseInt(behaviorLine[2].substring(1));
        if (invalidReg(registerNum1) || invalidReg(registerNum2)) {
            return;
        }
        // subtract values into register
        int newValue = c.getReg(registerNum1) - c.getReg(registerNum2);
        c.setReg(registerNum1, newValue);
        c.setNextCodeLine(c.getNextCodeLine() + 1);
    }

    // inc helper
    private void inc(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 2) {
            System.err.println("inc invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        int registerNum = Integer.parseInt(behaviorLine[1].substring(1));
        if (invalidReg(registerNum)) {
            return;
        }
        // add 1 to register
        int newValue = c.getReg(registerNum) + 1;
        c.setReg(registerNum, newValue);
        c.setNextCodeLine(c.getNextCodeLine() + 1);
    }

    // dec helper
    private void dec(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 2) {
            System.err.println("dec invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        int registerNum = Integer.parseInt(behaviorLine[1].substring(1));
        if (invalidReg(registerNum)) {
            return;
        }
        // sub 1 to register
        int newValue = c.getReg(registerNum) - 1;
        c.setReg(registerNum, newValue);
        c.setNextCodeLine(c.getNextCodeLine() + 1);
    }

    // iflt helper
    private void iflt(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 4) {
            System.err.println("iflt invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        if (isNotInteger(behaviorLine[2].substring(1))) {
            return;
        }
        // first register being less than second will lead to a jump
        int registerNum1 = Integer.parseInt(behaviorLine[1].substring(1));
        int registerNum2 = Integer.parseInt(behaviorLine[2].substring(1));
        if (invalidReg(registerNum1) || invalidReg(registerNum2)) {
            return;
        }
        if (c.getReg(registerNum1) < c.getReg(registerNum2)) {
            goToLine(behaviorLine[3], c);
        }
        else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifeq helper
    private void ifeq(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 4) {
            System.err.println("ifeq invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        if (isNotInteger(behaviorLine[2].substring(1))) {
            return;
        }
        // first register being equal to second will lead to a jump
        int registerNum1 = Integer.parseInt(behaviorLine[1].substring(1));
        int registerNum2 = Integer.parseInt(behaviorLine[2].substring(1));
        if (invalidReg(registerNum1) || invalidReg(registerNum2)) {
            return;
        }
        if (c.getReg(registerNum1) == c.getReg(registerNum2)) {
            goToLine(behaviorLine[3], c);
        }
        else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // ifgt helper
    private void ifgt(String[] behaviorLine, Critter c) {
        if (hasNulls(behaviorLine)) {
            return;
        }
        // invalid num of parameters
        if (behaviorLine.length != 4) {
            System.err.println("ifgt invalid number of parameters");
            errorCaught = true;
            return;
        }
        if (isNotInteger(behaviorLine[1].substring(1))) {
            return;
        }
        if (isNotInteger(behaviorLine[2].substring(1))) {
            return;
        }
        int registerNum1 = Integer.parseInt(behaviorLine[1].substring(1));
        int registerNum2 = Integer.parseInt(behaviorLine[2].substring(1));
        if (invalidReg(registerNum1) || invalidReg(registerNum2)) {
            return;
        }
        // first register being greater than second will lead to a jump
        if (c.getReg(registerNum1) > c.getReg(registerNum2)) {
            goToLine(behaviorLine[3], c);
        }
        else {
            c.setNextCodeLine(c.getNextCodeLine() + 1);
        }
    }

    // checks that a register number is within [1, REGISTERS]
    private boolean invalidReg(int reg) {
        if (reg < 1 || reg > Critter.REGISTERS) {
            System.err.println("Invalid register number");
            errorCaught = true;
            return true;
        }
        return false;
    }
}
