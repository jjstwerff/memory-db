package org.memorydb.handler;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Move most of this implementation to Abstract Text for reuse with TextString version.
 */
public class TextFile implements Text, AutoCloseable {
	private List<Source> sources = new ArrayList<>();
	private IntArray positions = new IntArray();
	private int pos = 0;
	private int line = 1;
	private int linePos = 1;
	private String name;
	private FileChannel channel;
	private MappedByteBuffer buffer;
	private static int POS = 0;
	private static int LINE = 1;
	private static int LINE_POS = 2;
	private static int SIZE = 3;

	private static class Source {
		String name;
		FileChannel channel;
		MappedByteBuffer buffer;

		public Source(String name, FileChannel channel, MappedByteBuffer buffer) {
			this.name = name;
			this.channel = channel;
			this.buffer = buffer;
		}
	}

	public TextFile(Path file, String name) {
		this.name = name;
		try {
			this.channel = FileChannel.open(file);
			this.buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		} catch (IOException e) {
			this.channel = null;
			this.buffer = null;
		}
	}

	@Override
	public void include(Path file, String name) {
		positions.add(pos);
		positions.add(line);
		positions.add(linePos);
		pos = 0;
		line = 1;
		linePos = 1;
		sources.add(new Source(name, channel, buffer));
	}

	@Override
	public void restore() {
		Source previous = sources.get(sources.size() - 1);
		try {
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		name = previous.name;
		channel = previous.channel;
		buffer = previous.buffer;
		toPos(positions.size() - SIZE);
		sources.remove(sources.size() - 1);
	}

	@Override
	public String position() {
		return name + " " + line + ":" + linePos;
	}

	public String range(int from) {
		if (from < 0 || from >= positions.size())
			return null;
		return name + " " + positions.get(from + LINE) + ":" + positions.get(from + LINE_POS) + "-" + line + ":" + linePos;
	}

	@Override
	public boolean end() {
		return pos >= buffer.capacity();
	}

	@Override
	public char readChar() {
		int capacity = buffer.capacity();
		if (pos >= capacity)
			return 0;
		linePos++;
		int b0 = buffer.get(pos++) & 0xFF;
		if (b0 < 0xC0 || b0 >= 0xF8 || pos == capacity) {
			if (b0 == '\n')
				line++;
			return (char) b0;
		}
		int b1 = buffer.get(pos++) & 0x3F;
		if (b0 < 0xE0 || pos == capacity)
			return (char) (((b0 & 0x1F) << 6) + b1);
		int b2 = buffer.get(pos++) & 0x3F;
		if (b0 < 0xF0 || pos == capacity)
			return (char) (((b0 & 0x0F) << 12) + (b1 << 6) + b2);
		int b3 = buffer.get(pos++) & 0x3F;
		return (char) (((b0 & 0x07) << 18) + (b1 << 12) + (b2 << 6) + b3);
	}

	@Override
	public boolean match(String str) {
		int pos = addPos();
		for (int i = 0; i < str.length(); i++) {
			if (readChar() != str.charAt(i)) {
				toPos(pos);
				return false;
			}
		}
		freePos(pos);
		return true;
	}

	@Override
	public String substring(int from) {
		if (from < 0 || from >= positions.size())
			return null;
		int till = pos;
		toAnchor(till);
		StringBuilder bld = new StringBuilder();
		while (pos < till)
			bld.append(readChar());
		return bld.toString();
	}

	@Override
	public String tail() {
		int end = buffer.capacity();
		StringBuilder bld = new StringBuilder();
		while (pos < end)
			bld.append(readChar());
		return bld.toString();
	}

	@Override
	public int addPos() {
		int res = positions.size();
		positions.add(pos);
		positions.add(line);
		positions.add(linePos);
		return res;
	}

	@Override
	public void toPos(int anchor) {
		int toSize = positions.size() - SIZE;
		if (anchor != toSize)
			throw new RuntimeException("Not freed in order");
		toAnchor(anchor);
		positions.trim(toSize);
	}

	private void toAnchor(int anchor) {
		pos = positions.get(anchor + POS);
		line = positions.get(anchor + LINE);
		linePos = positions.get(anchor + LINE_POS);
	}

	@Override
	public void freePos(int anchor) {
		int toSize = positions.size() - SIZE;
		if (anchor != toSize)
			throw new RuntimeException("Not freed in order");
		positions.trim(toSize);
	}

	@Override
	public void close() {
		if (!sources.isEmpty() || !positions.isEmpty())
			throw new RuntimeException("Not everything is closed yet");
		try {
			channel.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
