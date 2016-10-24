package com.kuaibao.skuaidi.sortlistview;

import com.kuaibao.skuaidi.entry.MyExpressBrandEntry;

import java.util.Comparator;

/**
 * 
 * @author 顾冬冬
 *
 */
public class PinyinComparator implements Comparator<MyExpressBrandEntry> {

	public int compare(MyExpressBrandEntry o1, MyExpressBrandEntry o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
