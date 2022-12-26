public class rType extends Instr {

    public String opCode = "000000";
    public String funct;
    public String destReg;
    public String sourceReg;
    public String targetReg;
    public String shamt = "00000";


    public rType(String destReg, String sourceReg, String targetReg, String funct)
    {
        this.opCode = opCode;
        this.destReg = destReg;
        this.sourceReg = sourceReg;
        this.targetReg = targetReg;
        this.funct = funct;
        this.shamt = shamt;
        if (this.funct.equals("000000"))
        {
            this.shamt = sourceReg;
            this.sourceReg = "00000";
        }

    }

    public String code() {
        return funct;
    }

    @Override
    public String toString()
    {
        return opCode + sourceReg + targetReg + destReg + shamt + funct;
    }


}
