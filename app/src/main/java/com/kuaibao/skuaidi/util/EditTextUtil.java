package com.kuaibao.skuaidi.util;

import android.text.Editable;
import android.text.Selection;
import android.widget.EditText;

public class EditTextUtil {

	/**
	 * EditText 输入最大长度检查
	 * 
	 * @param
	 * @return
	 */
	public static Editable checkMaxLength(EditText eeditText, Editable editable, int maxLength) {

		int selEndIndex = Selection.getSelectionEnd(editable);
		String str = editable.toString();
		// 截取新字符串
		String newStr = str.substring(0, maxLength);
		eeditText.setText(newStr);
		editable = eeditText.getText();
		// 新字符串的长度
		int newLen = editable.length();
		// 旧光标位置超过字符串长度
		if (selEndIndex > newLen) {
			selEndIndex = editable.length();
		}
		// 设置新光标所在的位置
		Selection.setSelection(editable, selEndIndex);
		return editable;
	}

}
