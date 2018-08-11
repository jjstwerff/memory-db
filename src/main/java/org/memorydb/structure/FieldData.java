package org.memorydb.structure;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface FieldData {
	String name();
	String type();
	String[] enumerate() default {};
	String[] keyNames() default {};
	String[] keyTypes() default {};
	Class<? extends RecordInterface> related() default Nothing.class;
	boolean mandatory() default false;
	boolean condition() default false;
	String when() default "";
	String description() default "";
}
