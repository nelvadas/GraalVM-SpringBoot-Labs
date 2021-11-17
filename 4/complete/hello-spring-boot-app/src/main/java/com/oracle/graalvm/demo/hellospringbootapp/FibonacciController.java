package com.oracle.graalvm.demo.hellospringbootapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.stream.Stream;

@RestController
public class FibonacciController {

	@GetMapping("/fibonacci/{parameter}")
	public Long fibonacci(@PathVariable Integer parameter) throws ReflectiveOperationException {
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
