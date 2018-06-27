package com.bupt.redis;

public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix) {//0表示永不过期
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int expireSeconds() {//默认0代表永不过期
        return 0;
    }

    /**
     * 每一个模块对应一个类，为了保证前缀的不同，可以使用类名
     * */
    @Override
    public String getPrefix() {
        //获取使用类的类名
        String className = getClass().getSimpleName();
        return className+":"+prefix;
    }
}
