public class jType extends Instr {

    public String opCode;
    public int address;

    public jType(String opCode, int address) {
        this.opCode = opCode;
        this.address = address;
    }

    public String code() {
        return opCode;
    }


    @Override
    public String toString()
    {
        return opCode + String.format("%32s", Integer.toBinaryString(address)).replace(' ', '0').substring(6, 32);
    }

}
