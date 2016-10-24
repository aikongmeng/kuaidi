package com.kuaibao.skuaidi.util;

import com.kuaibao.skuaidi.activity.model.SortModel;

import java.util.Comparator;

public class PinyinComparator<T> implements Comparator<T> {

	// 这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
	public int compare(T o1, T o2) {
		SortModel model1 = (SortModel)o1;
		SortModel model2 = (SortModel)o2;
		if (model1.getSortLetters().equals("@") || model2.getSortLetters().equals("#")) {
			return -1;
		} else if (model1.getSortLetters().equals("#")
				|| model2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return model1.getSortLetters().compareTo(model2.getSortLetters());
		}
	}
}
