package org.memorydb.structure;

import java.io.Closeable;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.memorydb.handler.CorruptionException;

import sun.misc.Unsafe;

public class Store implements Closeable {
	private final StringHash strings;
	private static final Method mmap = null;
	private static final Method unmmap = null;
	private static final Unsafe unsafe;
	private String filename;
	private long addr;
	private int length; // length in 8 byte chunks
	private static final boolean DO_CHECKS = true;
	private static final boolean DO_VERIFY = true;
	private static Logger logger = LoggerFactory.getLogger(Store.class);

	//private final ThreadLocal<Session> session = new ThreadLocal<Session>();
	private boolean commit;

	@SuppressWarnings("unused")
	private static Method getMethod(Class<?> cls, String name, Class<?>... params) throws Exception {
		Method m = cls.getDeclaredMethod(name, params);
		m.setAccessible(true);
		return m;
	}

	static { // Create the unsafe base object. Initialize the memory map methods.
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			unsafe = (Unsafe) singleoneInstanceField.get(null);
			Path temp = Files.createTempFile("once", ".txt");
			try (RandomAccessFile dd = new RandomAccessFile(temp.toFile(), "rw"); FileChannel channel = dd.getChannel()) {
				//mmap = getMethod(channel.getClass(), "map0", int.class, long.class, long.class); // int protection, long position, long length
				//unmmap = getMethod(channel.getClass(), "unmap0", long.class, long.class);
			}
		} catch (Exception e) {
			throw new InputOutputException(e);
		}
	}

	/**
	 * Allocate an in memory store of blocks with an initial index of free spaces.
	 */
	public Store(int indexes) {
		this.filename = null;
		length = 20000;
		addr = unsafe.allocateMemory(8L * length);
		if (addr == 0)
			throw new CorruptionException("could not allocate store");
		this.strings = new StringHash();
		start(indexes);
	}

	/**
	 * Allocate a store inside a memory mapped file
	 */
	public Store(int initial, String filename, int indexes) {
		assert mmap != null;
		this.filename = filename;
		try (RandomAccessFile file = new RandomAccessFile(filename, "rw")) {
			length = (int) (file.length() >> 3);
			if (length == 0)
				file.setLength(initial);
			length = (int) (file.length() >> 3);
			try (FileChannel ch = file.getChannel()) {
				addr = (long) mmap.invoke(ch, 1, 0L, 8L * length);
			}
		} catch (Exception e) {
			throw new CorruptionException(e);
		}
		this.strings = new StringHash();
		start(indexes);
	}

	// Keep spaces for indexes and allocate the rest of the store to free memory
	private void start(int indexes) {
		int len = (indexes + 8) / 2; // number of 8 bytes space needed for these indexes
		internalSetInt(0, len);
		for (int i = 0; i < indexes + 2; i++) // free structure & rest of indexes
			unsafe.putInt(addr + i * 4 + 4, 0);
		Free free = new Free(length, 4);
		internalSetInt(len, -length + len);
		free.insert(len);
		if (DO_VERIFY)
			verify();
	}

	/**
	 *  Validate the free structure & if all memory is accounted for
	 */
	private void verify() {
		Free free = new Free(0, 4);
		free.verify();
		Free toFree = new Free(0, 8);
		toFree.verify();
		Set<Integer> freeSpace = new HashSet<>();
		int pos = 0;
		while (pos < length) {
			int next = internalGetInt(pos);
			if (next == 0)
				throw new CorruptionException("Allocation error");
			if (next < 0) { // freed memery
				freeSpace.add(pos);
				pos -= next;
			} else {
				pos += next;
			}
		}
		if (pos != length)
			throw new CorruptionException("Not all memory accounted for");
		pos = free.first();
		while (pos > 0) {
			if (!freeSpace.contains(pos))
				throw new CorruptionException("Free structure pointing to non freed memory");
			freeSpace.remove(pos);
			pos = free.next(pos);
		}
		pos = toFree.first();
		while (pos > 0) {
			if (!freeSpace.contains(pos))
				throw new CorruptionException("Free structure pointing to non freed memory");
			freeSpace.remove(pos);
			pos = toFree.next(pos);
		}
		if (!freeSpace.isEmpty())
			throw new CorruptionException("Freed memory not in free space index");
	}

	// validate that the pos is a correct position for a record
	public boolean validate(int rec, int pos) {
		if (DO_CHECKS && ((rec < 2 && pos < 12) || rec >= length))
			throw new CorruptionException("Record " + rec + " outside allocation with size " + length);
		if (DO_CHECKS) {
			int len = unsafe.getInt(addr + 8L * rec);
			if (len <= 1)
				throw new CorruptionException("Record " + rec + " is marked as deleted");
			if (pos < 4 || pos >= 8L * len)
				throw new CorruptionException("Position " + rec + "." + pos + " outside valid record");
		}
		return rec >= 0 && rec < length;
	}

	public boolean validate(int rec) {
		if (DO_CHECKS && (rec < 0 || rec >= length))
			throw new CorruptionException("Record " + rec + " outside allocation with size " + length);
		return rec >= 0 && rec < length;
	}

	// when pos is an incorrect or removed record return 0 else leave it the same
	public int correct(int pos) {
		if (pos <= 0 || pos >= length || internalGetInt(pos) < 0)
			return 0;
		return pos;
	}

	public void internalSetInt(int rec, int value) {
		if (DO_CHECKS)
			assert validate(rec);
		unsafe.putInt(addr + 8L * rec, value);
	}

	public int internalGetInt(int rec) {
		if (DO_CHECKS)
			assert validate(rec);
		return unsafe.getInt(addr + 8L * rec);
	}

	public void setInt(int rec, int pos, int value) {
		if (DO_CHECKS)
			assert validate(rec, pos + 3);
		if (logger.isDebugEnabled())
			logger.debug("setInt {}/{}={}", rec, pos, value);
		unsafe.putInt(addr + 8L * rec + pos, value);
	}

	public int getInt(int rec, int pos) {
		if (DO_CHECKS)
			assert validate(rec, pos + 3);
		int res = unsafe.getInt(addr + 8L * rec + pos);
		if (logger.isDebugEnabled())
			logger.debug("getInt {}/{}={}", rec, pos, res);
		return res;
	}

	public void setShort(int rec, int pos, int value) {
		if (DO_CHECKS)
			assert validate(rec, pos + 1);
		if (logger.isDebugEnabled())
			logger.debug("setShort {}/{}={}", rec, pos, value);
		unsafe.putShort(addr + 8L * rec + pos, (short) value);
	}

	public short getShort(int rec, int pos) {
		if (DO_CHECKS)
			assert validate(rec, pos + 1);
		short res = unsafe.getShort(addr + 8L * rec + pos);
		if (logger.isDebugEnabled())
			logger.debug("getShort {}/{}={}", rec, pos, res);
		return res;
	}

	public void setByte(int rec, int pos, int value) {
		if (DO_CHECKS)
			assert validate(rec, pos);
		if (logger.isDebugEnabled())
			logger.debug("setByte {}/{}={}", rec, pos, value);
		unsafe.putByte(addr + 8L * rec + pos, (byte) value);
	}

	public byte getByte(int rec, int pos) {
		if (DO_CHECKS)
			assert validate(rec, pos);
		byte res = unsafe.getByte(addr + 8L * rec + pos);
		if (logger.isDebugEnabled())
			logger.debug("getByte {}/{}={}", rec, pos, res);
		return res;
	}

	public void setLong(int rec, int pos, long value) {
		if (DO_CHECKS)
			assert validate(rec, pos + 7);
		if (logger.isDebugEnabled())
			logger.debug("setLong {}/{}={}", rec, pos, value);
		unsafe.putLong(addr + 8L * rec + pos, value);
	}

	public long getLong(int rec, int pos) {
		if (DO_CHECKS)
			assert validate(rec, pos + 7);
		long res = unsafe.getLong(addr + 8L * rec + pos);
		if (logger.isDebugEnabled())
			logger.debug("getLong {}/{}={}", rec, pos, res);
		return res;
	}

	@Override
	public void close() {
		//if (session.get() != null) throw new RuntimeException("First commit or rollback the current session");
		if (DO_VERIFY)
			verify();
		if (filename != null) {
			assert unmmap != null;
			try (RandomAccessFile file = new RandomAccessFile(filename, "rw"); FileChannel ch = file.getChannel()) {
				unmmap.invoke(ch, addr, 8L * length);
			} catch (Exception e) {
				throw new CorruptionException(e);
			}
		}
	}

	public int putString(String str) {
		if (str == null)
			return -1;
		return strings.put(str);
	}

	public String getString(int strPos) {
		if (strPos < 0)
			return null;
		return strings.get(strPos);
	}

	/**
	 * Search the free space inside the store for the next bigger free block.
	 * When this is much larger than the requested block: split it in two.
	 * When nothing if found increase the store length.
	 */
	public int allocate(int size) {
		if (DO_VERIFY)
			verify();
		assert size >= 2 && size < 100000;
		Free free = new Free(size, 4); // try to find next bigger free block
		if (free.nextRec == 0) { // only too small free spaces remain
			int len = Math.max(length + size, length * 17 / 10);
			check(len);
			free = new Free(size, 4);
		}
		int rec = free.nextRec;
		free.remove(rec);
		int s = -internalGetInt(rec);
		if (s * 10 >= size * 17) { // split too large free block
			internalSetInt(rec + size, -s + size);
			free.insert(rec + size);
			s = size;
		}
		internalSetInt(rec, s);
		if (DO_VERIFY)
			verify();
		return rec;
	}

	/**
	 * When size is higher than length claim more memory to make it fit.
	 */
	private void check(int size) {
		if (size <= length)
			return;
		int previousLength = length;
		if (filename == null) {
			length = (size + 0xff) & ~0xff;
			addr = unsafe.reallocateMemory(addr, 8L * length);
			if (addr == 0)
				throw new CorruptionException("could not reallocate store");
		} else {
			assert unmmap != null;
			try (RandomAccessFile file = new RandomAccessFile(filename, "rw"); FileChannel ch = file.getChannel()) {
				unmmap.invoke(ch, addr, 8L * length);
				long len = (8L * size + 0xfffL) & ~0xfffL; // size round up to a block
				file.setLength(len);
				file.seek(len - 1);
				file.write(0);
				file.seek(0);
				addr = (long) mmap.invoke(ch, 1, 0L, len);
				length = (int) (file.length() >> 3);
			} catch (Exception e) {
				throw new CorruptionException(e);
			}
		}
		Free free = new Free(0, 4); // add the newly created space to the free structure
		int f = free.first();
		int lastFree = f;
		while (f != 0) {
			if (f > lastFree)
				lastFree = f;
			f = free.next(f);
		}
		if (lastFree == 0 || lastFree - internalGetInt(lastFree) != previousLength) {
			internalSetInt(previousLength, previousLength - length);
			free.insert(previousLength);
		} else
			internalSetInt(lastFree, lastFree - length);
	}

	public int resize(int rec, int size) {
		int curSize = internalGetInt(rec);
		if (size < curSize)
			return rec;
		if (DO_VERIFY)
			verify();
		int newSize = size * 17 / 10;
		int pos = allocate(newSize);
		unsafe.copyMemory(addr + 8L * rec + 4, addr + 8L * pos + 4, curSize * 8L - 4); // move data but not the size
		free(rec);
		if (logger.isDebugEnabled())
			logger.debug("resize: rec={}, pos={}, curSize={}, newSize={}", rec, pos, curSize, newSize);
		if (DO_VERIFY)
			verify();
		return pos;
	}

	public void copy(int rec, int from, int to, int size) {
		long rdata = addr + 8L * rec;
		int len = unsafe.getInt(rdata);
		if (from < 0 || size < 0 || from + size > 8 * len || to + size > 8 * len)
			throw new RuntimeException("Copy out of bound");
		unsafe.copyMemory(rdata + 4 + from, rdata + 4 + to, size);
	}

	public void free(int rec) {
		Free free = new Free(0, 4);
		int size = internalGetInt(rec);
		if (size <= 0)
			throw new CorruptionException("double freed record");
		internalSetInt(rec, -size);
		free.insert(rec);
	}

	public boolean isCommit() {
		return commit;
	}

	public StringHash getStrings() {
		return strings;
	}

	/* Free records: 2 size = 16 bytes minimum
	 *   0 : length (negative for free) .. positive for other allocations
	 *   4 : next free record
	 *   8 : previous free record
	 *  12 : red/black bit
	 */
	private class Free extends RedBlackTree {
		int nextRec;
		private final int index;

		public Free(int size, int index) {
			this.index = index;
			nextRec = find(new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert (validate(recNr));
					return Integer.compare(size, -internalGetInt(recNr));
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.GE;
				}
			});
		}

		@Override
		protected boolean readRed(int recNr) {
			assert (validate(recNr));
			return unsafe.getByte(addr + 8L * recNr + 12) > 0;
		}

		@Override
		protected void changeRed(int recNr, boolean value) {
			assert (validate(recNr));
			unsafe.putByte(addr + 8L * recNr + 12, (byte) (value ? 1 : 0));
		}

		@Override
		protected int readLeft(int recNr) {
			assert (validate(recNr));
			return unsafe.getInt(addr + 8L * recNr + 4);
		}

		@Override
		protected void changeLeft(int recNr, int value) {
			assert (validate(recNr));
			unsafe.putInt(addr + 8L * recNr + 4, value);
		}

		@Override
		protected int readRight(int recNr) {
			assert (validate(recNr));
			return unsafe.getInt(addr + 8L * recNr + 8);
		}

		@Override
		protected void changeRight(int recNr, int value) {
			assert (validate(recNr));
			unsafe.putInt(addr + 8L * recNr + 8, value);
		}

		@Override
		protected int readTop() {
			return unsafe.getInt(addr + index);
		}

		@Override
		protected void changeTop(int value) {
			unsafe.putInt(addr + index, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			int o = Integer.compare(-internalGetInt(a), -internalGetInt(b));
			if (o != 0)
				return o;
			return Integer.compare(a, b);
		}
	}

	public String analyze() {
		StringBuilder res = new StringBuilder();
		int pos = 0;
		int used = 0;
		int records = 0;
		int free = 0;
		int spaces = 0;
		while (pos < length) {
			int next = internalGetInt(pos);
			if (next < 0) {
				spaces++;
				free -= next;
			} else {
				records++;
				used += next;
			}
			pos += Math.abs(next);
		}
		res.append("used: " + (used * 8 / 1024) + "kb records:" + records + " size: " + (length * 8 / 1024) + "kb\n");
		res.append("free: " + (free * 8 / 1024) + "kb spaces:" + spaces + "\n");
		res.append(strings.analyze());
		return res.toString();
	}
}
