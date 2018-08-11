package org.memorydb.file;

class StructureError extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public StructureError(String string) {
		super(string);
	}
}
