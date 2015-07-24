package com.dangdang.light.http.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.CharMatcher;

public final class CollUtil {

	public static <K, V> Map<K, V> mergeMap(Map<K, V> m1, Map<K, V> m2) {
		Map<K, V> core = new HashMap<>();
		if (m1 != null) {
			for (Entry<K, V> entry : m1.entrySet()) {
				core.put(entry.getKey(), entry.getValue());
			}
		}
		if (m2 != null) {
			for (Entry<K, V> entry : m2.entrySet()) {
				core.put(entry.getKey(), entry.getValue());
			}
		}
		return core;
	}

	public static <K, V> Map<K, V> asMap(K k, V v) {
		Map<K, V> map = new HashMap<K, V>();
		map.put(k, v);
		return map;
	}

	public static List<Long> parseLong(String[] strArr) {
		List<Long> longs = new ArrayList<>();
		if (strArr != null) {
			for (String str : strArr) {
				String numStr = str.trim();
				if (CharMatcher.DIGIT.matchesAllOf(numStr)) {
					longs.add(Long.parseLong(numStr));
				}
			}
		}
		return longs;
	}
}
