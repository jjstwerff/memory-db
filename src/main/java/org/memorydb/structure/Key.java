package org.memorydb.structure;

public interface Key {
    int compareTo(int rec);
    IndexOperation oper();
}
