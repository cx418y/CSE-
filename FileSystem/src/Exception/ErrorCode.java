package Exception;

import java.util.HashMap;
import java.util.Map;

public class ErrorCode extends RuntimeException {
    public static final int IO_EXCEPTION = 1;
    public static final int CHECKSUM_CHECK_FAILED = 2;
    public static final int FILE_NOT_FOUND = 3;
    public static final int CLASS_NOT_FOUND = 4;
    public static final int INVALID_LENGTH = 5;
    public static final int READ_END_OF_FILE = 6;  //读文件长度超过文件长度
    public static final int MOVE_OUT_OF_FILE = 7; //移动光标offset超过文件长度
    public static final int WRITE_OUT_OF_BOUND = 8;
    public static final int INVALID_ARGUMENT_WHERE = 9;
    public static final int LOGIC_BLOCK_BROKEN = 10;
    public static final int BLOCK_BROKEN = 11;
    public static final int FILE_HAVE_EXIST = 12;
    public static final int BLOCK_NOT_FOUND = 13;

    // ... and more
    public static final int UNKNOWN = 1000;

    private static final Map<Integer, String> ErrorCodeMap = new HashMap<>();

    static {
        ErrorCodeMap.put(IO_EXCEPTION, "IO exception");
        ErrorCodeMap.put(CHECKSUM_CHECK_FAILED, "block checksum check failed exception");
        ErrorCodeMap.put(UNKNOWN, "unknown exception");
        ErrorCodeMap.put(FILE_NOT_FOUND,"file not found exception");
        ErrorCodeMap.put(CLASS_NOT_FOUND,"class not found exception");
        ErrorCodeMap.put(INVALID_LENGTH,"invalid length of negative exception");
        ErrorCodeMap.put(READ_END_OF_FILE,"read out of bound");
        ErrorCodeMap.put(MOVE_OUT_OF_FILE, "invalid move offset exception");
        ErrorCodeMap.put(WRITE_OUT_OF_BOUND,"write out of bound");
        ErrorCodeMap.put(INVALID_ARGUMENT_WHERE,"invalid argument where");
        ErrorCodeMap.put(LOGIC_BLOCK_BROKEN,"logic block broken");
        ErrorCodeMap.put(BLOCK_BROKEN,"block broken");
        ErrorCodeMap.put(FILE_HAVE_EXIST,"file have exist");
        ErrorCodeMap.put(BLOCK_NOT_FOUND,"block not exist");
    }

    public static String getErrorText(int errorCode) {
        return ErrorCodeMap.getOrDefault(errorCode, "invalid");
    }

    private int errorCode;

    public ErrorCode(int errorCode) {
        super(String.format("error code '%d' \"%s\"", errorCode, getErrorText(errorCode)));
        this.errorCode = errorCode;
    }


    public int getErrorCode() {
        return errorCode;
    } }
