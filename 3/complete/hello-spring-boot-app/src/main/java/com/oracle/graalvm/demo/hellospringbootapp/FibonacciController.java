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
		Class<?> clazz = null;

			clazz = Class.forName("com.oracle.graalvm.demo.hellospringbootapp.FibonacciUtils");
			Object instance = clazz.getConstructor().newInstance();
			Method method = clazz.getDeclaredMethod("get", Integer.class);
			Long result = (Long) method.invoke(instance, parameter);
			return result;

	}

}
