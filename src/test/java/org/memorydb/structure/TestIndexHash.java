package org.memorydb.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class TestIndexHash {
	private Random random = new Random();

	@Test
	public void testChanges() {
		//random.setSeed(2);
		Map<String, String> values = new HashMap<>();
		StringMap data = new StringMap(random);
		for (int i = 0; i < 2000; i++) {
			String key = randomString();
			String value = randomString();
			values.put(key, value);
			//System.out.println(key + " = " + value + " ");
			data.put(key, value);
			Assert.assertEquals(values.size(), data.size());
			Iterator<Integer> iterator = data.iterator();
			int elm = 0;
			while (iterator.hasNext()) {
				int p = iterator.next();
				if (p < 0 || p * 2 + 1 >= data.data.size())
					throw new RuntimeException("Strange value " + p);
				String iterKey = data.data.get(p * 2);
				Assert.assertEquals(values.get(iterKey), data.data.get(p * 2 + 1));
				elm++;
			}
			Assert.assertEquals(values.size(), elm);
			for (Entry<String, String> entry : values.entrySet()) {
				String found = data.find(entry.getKey());
				//System.out.println(entry.getKey() + " = " + entry.getValue() + " found " + found);
				Assert.assertEquals("cannot find " + entry.getKey(), entry.getValue(), found);
			}
			iterator = data.iterator();
			while (iterator.hasNext()) {
				String iterKey = data.data.get(iterator.next() * 2);
				if (random.nextInt(200) == 0) {
					//System.out.println(" remove " + iterKey);
					iterator.remove();
					values.remove(iterKey);
					for (Entry<String, String> entry : values.entrySet()) {
						String found = data.find(entry.getKey());
						//System.out.println(entry.getKey() + " = " + entry.getValue() + " found " + found);
						Assert.assertEquals("cannot find " + entry.getKey(), entry.getValue(), found);
					}
				}
			}
		}
	}

	@Test
	public void testStringHash() {
		StringHash hash = new StringHash();
		hash.put("start with something already in the data");
		int f = hash.put("the first string");
		hash.put("✓a secönd string¿");
		hash.put("another string");
		Assert.assertTrue(hash.contains("✓a secönd string¿"));
		Assert.assertFalse(hash.contains("second string"));
		Assert.assertEquals(f, hash.put("the first string"));
	}

	@Test
	public void testMultiValue() {
		StringHash hash = new StringHash();
		int a = hash.put("aa");
		hash.put("aaa");
		int b = hash.put("aa");
		Assert.assertEquals(a, b);
	}

	private String randomString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 12; i++) {
			builder.append(Character.toChars(random.nextInt(94) + 33));
		}
		return builder.toString();
	}

	static class StringMap extends IndexHash<String> {
		List<String> data = new ArrayList<>();
		String curKey = null;

		public StringMap(Random random) {
			super(IndexHash.DEFAULT_INITIAL_CAPACITY, IndexHash.DEFAULT_LOAD_FACTOR, random);
		}

		protected String getKey(int value) {
			if (value < 0 || value * 2 + 1 >= data.size())
				throw new RuntimeException("Strange value " + value);
			return data.get(value * 2);
		}

		public String find(String key) {
			int index = get(key);
			return data.get(index * 2 + 1);
		}

		public void put(String key, String value) {
			int pos = data.size();
			data.add(key);
			data.add(value);
			put(pos / 2);
		}

		@Override
		protected int getHash(int pos) {
			return getKey(pos).hashCode();
		}

		@Override
		protected boolean equalsKey(int pos, String key) {
			return getKey(pos).equals(key);
		}

		@Override
		protected int getHash(String key) {
			return key.hashCode();
		}
	}
}
