package org.memorydb.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface RecordData {
	String name();
	String description() default "";
	String[] keyFields() default {};
}
