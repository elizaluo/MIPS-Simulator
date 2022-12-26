import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.Scanner;
import java.util.*;

public class Simulator {
    private final File filename;
    public HashMap<String, Integer> labelMap = new HashMap<>();
    public HashMap<String, String> register = new HashMap<>();
    public HashMap<String, Integer> mipsRegisters = new HashMap<>();
    public HashMap<String, String> funct = new HashMap<>();
    public int programCounter = 0;
    public int[] dataMemory = new int[8192];

    public int GHR = 0;
    public int GHRSize = 2;
    public int correctPred = 0;
    public int totalPred = 0;
    int[] counterArray;
    public Simulator(File filename) {
        this.filename = filename;
    }

    public void addToLabelMap() {
        try {
            Scanner myReader = new Scanner(filename);
            int lineCounter = -1;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.trim();
                String dataCopy = data;
                if (data.split("#").length != 0) {
                    data = data.split("#")[0];
                } else {
                    continue;
                }
                if (data != null && data.length() > 0) {
                    lineCounter += 1;
                }
                if (data.contains(":")) {
                    String[] remaining = dataCopy.split(":");
                    data = data.split(":")[0];
                    labelMap.put(data, lineCounter);
                    if (remaining.length == 1) {
                        lineCounter -= 1;
                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }
    }

    public void intializeRegisters() {
        register.put("$0", "00000");
        register.put("$zero", "00000");
        register.put("$v0", "00010");
        register.put("$v1", "00011");
        register.put("$a0", "00100");
        register.put("$a1", "00101");
        register.put("$a2", "00110");
        register.put("$a3", "00111");
        register.put("$t0", "01000");
        register.put("$t1", "01001");
        register.put("$t2", "01010");
        register.put("$t3", "01011");
        register.put("$t4", "01100");
        register.put("$t5", "01101");
        register.put("$t6", "01110");
        register.put("$t7", "01111");
        register.put("$s0", "10000");
        register.put("$s1", "10001");
        register.put("$s2", "10010");
        register.put("$s3", "10011");
        register.put("$s4", "10100");
        register.put("$s5", "10101");
        register.put("$s6", "10110");
        register.put("$s7", "10111");
        register.put("$t8", "11000");
        register.put("$t9", "11001");
        register.put("$ra", "11111");
        register.put("$sp", "11101");
    }

    public void intializeMIPSRegisters() {
        //mipsRegisters.put("00000", 0);
        mipsRegisters.put("00000", 0);
        mipsRegisters.put("00010", 0);
        mipsRegisters.put("00011", 0);
        mipsRegisters.put("00100", 0);
        mipsRegisters.put("00101", 0);
        mipsRegisters.put("00110", 0);
        mipsRegisters.put("00111", 0);
        mipsRegisters.put("01000", 0);
        mipsRegisters.put("01001", 0);
        mipsRegisters.put("01010", 0);
        mipsRegisters.put("01011", 0);
        mipsRegisters.put("01100", 0);
        mipsRegisters.put("01101", 0);
        mipsRegisters.put("01110", 0);
        mipsRegisters.put("01111", 0);
        mipsRegisters.put("10000", 0);
        mipsRegisters.put("10001", 0);
        mipsRegisters.put("10010", 0);
        mipsRegisters.put("10011", 0);
        mipsRegisters.put("10100", 0);
        mipsRegisters.put("10101", 0);
        mipsRegisters.put("10110", 0);
        mipsRegisters.put("10111", 0);
        mipsRegisters.put("11000", 0);
        mipsRegisters.put("11001", 0);
        mipsRegisters.put("11111", 0);
        mipsRegisters.put("11101", 0);
    }

    public void intializeOpcodesFunct() {
        funct.put("addi", "001000");
        funct.put("and", "100100");
        funct.put("or", "100101");
        funct.put("add", "100000");
        funct.put("sub", "100010");
        funct.put("sll", "000000");
        funct.put("slt", "101010");
        funct.put("beq", "000100");
        funct.put("bne", "000101");
        funct.put("lw", "100011");
        funct.put("sw", "101011");
        funct.put("j", "000010");
        funct.put("jr", "001000");
        funct.put("jal", "000011");

    }

    public ArrayList<Instr> parseFile() {
        intializeRegisters();
        intializeOpcodesFunct();
        addToLabelMap();

        ArrayList<Instr> instrList = new ArrayList<Instr>();

        try {
            Scanner myReader = new Scanner(filename);
            String instr;
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data = data.trim();
                if (data.split("#").length != 0) {
                    data = data.split("#")[0];
                } else {
                    continue;
                }
                if (data != null && data.length() > 0) {

                    if (data.contains(":")) {
                        String[] line = data.split(":");
                        if (line.length == 2) {
                            data = line[1];
                        } else {
                            continue;
                        }

                    }


                    if (data.charAt(0) == 'j' && data.charAt(1) == ' ') {
                        instr = "j";
                        data = data.split(" ")[1].trim();
                        String myFunct = funct.get(instr);
                        int address = labelMap.get(data);
                        Instr myInstr = new jType(myFunct, address);
                        instrList.add(myInstr);
                        continue;
                    } else if (data.charAt(0) == 'j' && data.charAt(1) == 'a' && data.charAt(2) == 'l' && data.charAt(3) == ' ') {
                        instr = "jal";
                        data = data.split(" ")[1].trim();
                        String myFunct = funct.get(instr);
                        int address = labelMap.get(data);
                        Instr myInstr = new jType(myFunct, address);
                        instrList.add(myInstr);
                        continue;
                    }

                    String remaining = data.substring(data.indexOf("$"));
                    instr = data.substring(0, data.indexOf("$"));
                    instr = instr.trim();

                    if (funct.get(instr) == null) {
                        for (int i = 0; i < instrList.size(); i++) {
                            System.out.println(instrList.get(i));
                        }
                        System.out.println("invalid instruction: " + instr);
                        return instrList;

                    }

                    if (instr.equals("jr")) {
                        String myFunct = funct.get(instr);
                        remaining = remaining.trim();
                        String source = register.get(remaining);
                        Instr myInstr = new rType("00000", source, "00000", myFunct);
                        instrList.add(myInstr);
                        continue;
                    }
                    String[] registerList = remaining.split(",");


                    if (instr.equals("sw") || instr.equals("lw")) {
                        String myFunct = funct.get(instr);
                        String dest = register.get(registerList[0].trim());
                        String[] offsetList = registerList[1].split("\\(");
                        int immediate = Integer.parseInt(offsetList[0].trim());
                        String source = register.get(offsetList[1].substring(0, 3));
                        Instr myInstr = new iType(dest, source, immediate, myFunct);
                        instrList.add(myInstr);
                        continue;
                    }
                    if (instr.equals("bne") || instr.equals("beq")) {
                        String myFunct = funct.get(instr);
                        String source = register.get(registerList[0].trim());
                        String target = register.get(registerList[1].trim());
                        int immediate = labelMap.get(registerList[2].trim());
                        immediate = immediate - (instrList.size() + 1);

                        Instr myInstr = new iType(target, source, immediate, myFunct);
                        instrList.add(myInstr);
                        continue;
                    }
                    if (instr.equals("sll")) {
                        String myFunct = funct.get(instr);
                        String dest = register.get(registerList[0].trim());
                        String target = register.get(registerList[1].trim());
                        String source = registerList[2].trim();

                        if (source.contains("$")) {
                            source = register.get(registerList[2].trim());
                        } else {
                            source = String.format("%32s", Integer.toBinaryString(Integer.parseInt(source))).replace(' ', '0').substring(27, 32);
                        }
                        Instr myInstr = new rType(dest, source, target, myFunct);
                        instrList.add(myInstr);
                        continue;
                    }

                    if (registerList[registerList.length - 1].contains("$")) {
                        String myFunct = funct.get(instr);
                        String rd = register.get(registerList[0].trim());
                        String source = register.get(registerList[1].trim());
                        String target = register.get(registerList[2].trim());
                        Instr myInstr = new rType(rd, source, target, myFunct);
                        instrList.add(myInstr);
                    } else {
                        String myFunct = funct.get(instr);
                        String rd = register.get(registerList[0].trim());
                        String source = register.get(registerList[1].trim());
                        int immediate = Integer.parseInt(registerList[2].trim());
                        Instr myInstr = new iType(rd, source, immediate, myFunct);
                        instrList.add(myInstr);

                    }
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
        }

        return instrList;
    }

    public String[] registerOrder() {
        String[] order = {"$0", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3", "$t0",
                "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$s0", "$s1",
                "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$t8", "$t9", "$sp", "$ra"};
        return order;
    }

    public void executeOneInstr(ArrayList<Instr> instrList) {
        if (programCounter < instrList.size())
        {
        // addi
        if (instrList.get(programCounter).code().equals("001000") && instrList.get(programCounter) instanceof iType) {
            iType instr = (iType) instrList.get(programCounter);
            int sourceReg = mipsRegisters.get(instr.sourceReg);
            int result = sourceReg + instr.immediate;
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;

        }
        // add
        else if (instrList.get(programCounter).code().equals("100000")) {
            rType instr = (rType) instrList.get(programCounter);
            int sourceReg = mipsRegisters.get(instr.sourceReg);
            int targetReg = mipsRegisters.get(instr.targetReg);
            int result = sourceReg + targetReg;
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;

        }
        // sw
        else if (instrList.get(programCounter).code().equals("101011")) {
            iType instr = (iType) instrList.get(programCounter);
            int getReg = mipsRegisters.get(instr.sourceReg); // has address
            int regPlusOffset = getReg + instr.immediate;
            dataMemory[regPlusOffset] = mipsRegisters.get(instr.destReg);
            programCounter += 1;

        }
        // bne
        else if (instrList.get(programCounter).code().equals("000101")) {
            String bin = String.format("%32s",Integer.toBinaryString(GHR)).replace(' ', '0');
            bin = bin.substring(bin.length()-GHRSize);
            int GHRBits = Integer.parseInt(bin, 2);
            int prediction = counterArray[GHRBits];
            iType instr = (iType) instrList.get(programCounter);
            // if branch taken
            if (!mipsRegisters.get(instr.sourceReg).equals(mipsRegisters.get(instr.destReg))) {
                programCounter = programCounter + instr.immediate;
                if (prediction == 2 || prediction == 3)
                {
                    correctPred += 1;
                }
                // update GHR array
                if (counterArray[GHRBits] != 3)
                {
                    counterArray[GHRBits] += 1 ;
                }
                // update the GHR
                GHR = (GHR << 1) + 1;
            }
            // if branch not taken
            else
            {
                if (prediction == 0 || prediction == 1)
                {
                    correctPred += 1;
                }
                // update GHR array
                if (counterArray[GHRBits] != 0)
                {
                    counterArray[GHRBits] -= 1 ;
                }
                // update the GHR
                GHR = GHR << 1;
            }
            totalPred += 1;
            programCounter += 1;

        }
        // slt
        else if (instrList.get(programCounter).code().equals("101010")) {
            rType instr = (rType) instrList.get(programCounter);
            if (mipsRegisters.get(instr.sourceReg) < mipsRegisters.get(instr.targetReg)) {
                mipsRegisters.put(instr.destReg, 1);
            } else {
                mipsRegisters.put(instr.destReg, 0);
            }
            programCounter += 1;
        }
        // jal
        else if (instrList.get(programCounter).code().equals("000011")) {
            jType instr = (jType) instrList.get(programCounter);
            mipsRegisters.put("11111", programCounter + 1);
            programCounter = instr.address;
        }
        // j
        else if (instrList.get(programCounter).code().equals("000010")) {
            jType instr = (jType) instrList.get(programCounter);
            programCounter = instr.address;
        }
        // and
        else if (instrList.get(programCounter).code().equals("100100")) {
            rType instr = (rType) instrList.get(programCounter);
            int sourceReg = mipsRegisters.get(instr.sourceReg);
            int targetReg = mipsRegisters.get(instr.targetReg);
            int result = sourceReg & targetReg;
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;

        }
        // or
        else if (instrList.get(programCounter).code().equals("100101")) {
            rType instr = (rType) instrList.get(programCounter);
            int sourceReg = mipsRegisters.get(instr.sourceReg);
            int targetReg = mipsRegisters.get(instr.targetReg);
            int result = sourceReg | targetReg;
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;

        }
        // sub
        else if (instrList.get(programCounter).code().equals("100010")) {
            rType instr = (rType) instrList.get(programCounter);
            int sourceReg = mipsRegisters.get(instr.sourceReg);
            int targetReg = mipsRegisters.get(instr.targetReg);
            int result = sourceReg - targetReg;
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;
        }
        // sll
        else if (instrList.get(programCounter).code().equals("000000"))
        {
            rType instr = (rType)instrList.get(programCounter);
            int result = (mipsRegisters.get(instr.targetReg)) << Integer.parseInt(instr.shamt, 2);
            mipsRegisters.put(instr.destReg, result);
            programCounter += 1;
        }
        // beq
        else if (instrList.get(programCounter).code().equals("000100")) {
            String bin = String.format("%32s",Integer.toBinaryString(GHR)).replace(' ', '0');
            bin = bin.substring(bin.length()-GHRSize);
            int GHRBits = Integer.parseInt(bin, 2);
            int prediction = counterArray[GHRBits];
            iType instr = (iType) instrList.get(programCounter);
            if (mipsRegisters.get(instr.sourceReg).equals(mipsRegisters.get(instr.destReg))) {
                programCounter = programCounter + instr.immediate;
                if (prediction == 2 || prediction == 3)
                {
                    correctPred += 1;
                }
                // update GHR array
                if (counterArray[GHRBits] != 3)
                {
                    counterArray[GHRBits] += 1 ;
                }
                // update the GHR
                GHR = (GHR << 1) + 1;
            }
            // if branch not taken
            else
            {
                if (prediction == 0 || prediction == 1)
                {
                    correctPred += 1;
                }
                // update GHR array
                if (counterArray[GHRBits] != 0)
                {
                    counterArray[GHRBits] -= 1 ;
                }
                // update the GHR
                GHR = GHR << 1;
            }
            totalPred += 1;
            programCounter += 1;
        }
        // lw
        else if (instrList.get(programCounter).code().equals("100011")) {
            iType instr = (iType) instrList.get(programCounter);
            int number = dataMemory[mipsRegisters.get(instr.sourceReg) + instr.immediate];
            mipsRegisters.put(instr.destReg, number);
            programCounter += 1;

        }
        // jr
        else if (instrList.get(programCounter).code().equals("001000")) {
            rType instr = (rType) instrList.get(programCounter);
            programCounter = mipsRegisters.get(instr.sourceReg);
        }
    }
}
    public void interactiveMode() {
        System.out.print("mips> ");
        Scanner myObj = new Scanner(System.in);
        String userInput = myObj.nextLine().trim();
        String[] userInputList = userInput.split(" ");
        ArrayList<Instr> instrList = parseFile();
        String[] registerNames = registerOrder();
        while (!userInput.equals("q")) {
            // (h) show help menu
            if (userInputList[0].equals("h")) {
                System.out.println();
                System.out.println("h = show help");
                System.out.println("d = dump register state");
                System.out.println("b = branch prediction accuracy");
                System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
                System.out.println("s num = step through num instructions of the program");
                System.out.println("r = run until the program ends");
                System.out.println("m num1 num2 = display data memory from location num1 to num2");
                System.out.println("c = clear all registers, memory, and the program counter to 0");
                System.out.println("q = exit the program");
                System.out.println();
            }
            if (userInput.equals("b"))
            {
                System.out.println();
                System.out.print("accuracy ");
                System.out.printf("%.2f", ((float)correctPred/(float)totalPred)*100);
                System.out.println("% (" + correctPred + " correct predictions, " + totalPred + " predictions)");
                System.out.println();
            }
            // (d) dump register state
            if (userInput.equals("d"))
            {
                System.out.println();
                System.out.println("pc = " + programCounter);
                int counter = 0;
                for (String key : registerNames) {
                    if (counter == 4) {
                        System.out.println();
                        counter = 0;
                    }
                    counter++;
                    String binary = register.get(key);
                    System.out.print(key + " = " + mipsRegisters.get(binary) + "        ");
                }
                System.out.println();
                System.out.println();
            }
            // (s) single step through 1 instruction and stop
            if (userInputList[0].equals("s") && userInputList.length == 1)
            {
                executeOneInstr(instrList);
                System.out.println("     1 instruction(s) executed");
            }
            //  (s num) step through num instructions of the program
            if (userInputList[0].equals("s") && userInputList.length == 2) {
                int numInstr = Integer.parseInt(userInputList[1]);
                for (int i=0; i<numInstr; i++)
                {
                    executeOneInstr(instrList);
                }
                System.out.println("    " + numInstr + " instruction(s) executed");
            }
            // (r) run until the program ends
            if (userInputList[0].equals("r")) {
                while (programCounter < instrList.size())
                {
                    executeOneInstr(instrList);
                }
            }
            // (m num1 num2) display data memory from num1 to num2
            if (userInputList[0].equals("m")) {
                System.out.println();
                int first = Integer.parseInt(userInputList[1]);
                int second = Integer.parseInt(userInputList[2]);
                for (int i = first; i <= second; i++) {
                    System.out.println("[" + i + "] = " + dataMemory[i]);
                }
                System.out.println();
            }
            // (c) clear all register, memory, and program counter = 0
            if (userInputList[0].equals("c")) {
                programCounter = 0;
                for (String key : mipsRegisters.keySet()) {
                    mipsRegisters.put(key, 0);
                }
                Arrays.fill(dataMemory, 0);
                System.out.println("    Simulator reset");
                System.out.println();
            }

            System.out.print("mips> ");
            userInput = myObj.nextLine().trim();
            userInputList = userInput.split(" ");
        }
    }

    public void scriptMode(String userInput, String[] userInputList) {
        System.out.print("mips> " + userInput + "\n");
        ArrayList<Instr> instrList = parseFile();
        String[] registerNames = registerOrder();
        if (!userInputList.equals("q")) {
            // (h) show help menu
            if (userInputList[0].equals("h")) {
                System.out.println();
                System.out.println("h = show help");
                System.out.println("d = dump register state");
                System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
                System.out.println("s num = step through num instructions of the program");
                System.out.println("r = run until the program ends");
                System.out.println("m num1 num2 = display data memory from location num1 to num2");
                System.out.println("c = clear all registers, memory, and the program counter to 0");
                System.out.println("q = exit the program");
                System.out.println();
            }
            if (userInput.equals("b"))
            {
                System.out.println();
                System.out.print("accuracy ");
                System.out.printf("%.2f", ((float)correctPred/(float)totalPred)*100);
                System.out.println("% (" + correctPred + " correct predictions, " + totalPred + " predictions)");
                System.out.println();
            }
            // (d) dump register state
            if (userInputList[0].equals("d"))
            {
                System.out.println();
                System.out.println("pc = " + programCounter);
                int counter = 0;
                for (String key : registerNames) {
                    if (counter == 4) {
                        System.out.println();
                        counter = 0;
                    }
                    counter++;
                    String binary = register.get(key);
                    System.out.print(key + " = " + mipsRegisters.get(binary) + "        ");
                }
                System.out.println();
                System.out.println();
            }
            // (s) single step through 1 instruction and stop
            if (userInputList[0].equals("s") && userInputList.length == 1)
            {
                executeOneInstr(instrList);
                System.out.println("     1 instruction(s) executed");
            }
            //  (s num) step through num instructions of the program
            if (userInputList[0].equals("s") && userInputList.length == 2) {
                int numInstr = Integer.parseInt(userInputList[1]);
                for (int i=0; i<numInstr; i++)
                {
                    executeOneInstr(instrList);
                }
                System.out.println("    " + numInstr + " instruction(s) executed");
            }
            // (r) run until the program ends
            if (userInputList[0].equals("r")) {
                while (programCounter < instrList.size())
                {
                    executeOneInstr(instrList);
                }
            }
            // (m num1 num2) display data memory from num1 to num2
            if (userInputList[0].equals("m")) {
                System.out.println();
                int first = Integer.parseInt(userInputList[1]);
                int second = Integer.parseInt(userInputList[2]);
                for (int i = first; i <= second; i++) {
                    System.out.println("[" + i + "] = " + dataMemory[i]);
                }
                System.out.println();
            }
            // (c) clear all register, memory, and program counter = 0
            if (userInputList[0].equals("c")) {
                programCounter = 0;
                for (String key : mipsRegisters.keySet()) {
                    mipsRegisters.put(key, 0);
                }
                Arrays.fill(dataMemory, 0);
                System.out.println("    Simulator reset");
                System.out.println();
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public static void main(String args[])
    {
        File myFile = new File(args[0]);
        Simulator myParse = new Simulator(myFile);
        myParse.intializeMIPSRegisters();
        myParse.parseFile();
        //System.out.println(args[1]);
        if (args.length > 2 || args.length == 2 && !isInteger(args[1]))
        {
            try {
                File myScript = new File(args[1]);
                if (args.length == 3)
                {
                    myParse.GHRSize = Integer.parseInt(args[2]);
                    myParse.counterArray = new int[(int)Math.pow(2, myParse.GHRSize)];
                }
                else
                {
                    myParse.counterArray = new int[(int)Math.pow(2, 2)];
                }
                Scanner myReader = new Scanner(myScript);
                while (myReader.hasNextLine())
                {
                    String userInput = myReader.nextLine().trim();
                    if (!userInput.equals(""))
                    {
                        String[] userInputList = userInput.split(" ");
                        myParse.scriptMode(userInput, userInputList);
                    }
                }

            } catch (Exception e) {
                System.out.println("File Not Found.");
            }

        }
        else
        {
            if (args.length == 2)
            {
                myParse.GHRSize = Integer.parseInt(args[1]);
                myParse.counterArray = new int[(int) Math.pow(2, myParse.GHRSize)];
            }
            else
            {
                myParse.counterArray = new int[(int) Math.pow(2, 2)];
            }
            myParse.interactiveMode();
        }
    }
}


