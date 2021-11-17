package com.oracle.graalvm.demo.hellospringbootapp;

import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Stream;

public class FibonacciUtils implements Serializable {

    public FibonacciUtils(){

    }
    /**
     * Get the fibonacci value for the input parameter
     * @param parameter
     * @return
     */
    public Long get(Integer parameter){

        long result = 0;
        result = Stream.iterate( new int[]{0,1}, fib-> new int[]{fib[1], fib[0]+fib[1]} )
                .limit(parameter)
                .map(x->x[0])
                .max(Comparator.naturalOrder())
                .get()
                .longValue();

        return result;
    }
}
