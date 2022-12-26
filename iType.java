public class iType extends Instr {
    public String opCode;
    public String destReg;
    public String sourceReg;
    public int immediate;


    public iType(String destReg, String sourceReg, int immediate, String opCode)
    {
        this.opCode = opCode;
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.immediate = immediate;
    }

    public String code() {
        return opCode;
    }

    @Override
    public String toString()
    {
        return opCode + sourceReg + destReg + String.format("%32s", Integer.toBinaryString(immediate)).replace(' ', '0').substring(16, 32);
    }

}

