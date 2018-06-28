package com.bupt.Exception;

import com.bupt.result.CodeMsg;

public class GlobalException extends RuntimeException {
    private static final long SerialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
