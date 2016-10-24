package com.kuaibao.skuaidi.dialog;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkuaidiBigAboutCheckListManager {
	private static List<Context> contexts = new ArrayList<Context>();
	private static Map<Class<?>,Map<Integer,Map<Integer,String>>> checks = new HashMap<Class<?>, Map<Integer,Map<Integer,String>>>();
	public static void setContext(Context context){
		contexts.add(context);
	}
	public static boolean isShowed(Context context){
		for (int i = 0; i < contexts.size(); i++) {
			if(context.getClass() .equals(contexts.get(i).getClass())){
				return true;
			}
		}
		return false;
	}
	@SuppressLint("UseSparseArrays")
	public static void setCheck(Context context,int parentPosition,int childPosition,String childTitle){
		if(checks.get(context.getClass())==null){
			Map<Integer,Map<Integer,String>> childs = new HashMap<Integer, Map<Integer,String>>();
			Map<Integer,String> child = new HashMap<Integer, String>();
			child.put(childPosition, childTitle);
			childs.put(parentPosition, child);
			checks.put(context.getClass(), childs);
		}else if(checks.get(context.getClass()).get(parentPosition)==null){
			Map<Integer,String> child = new HashMap<Integer, String>();
			child.put(childPosition, childTitle);
			checks.get(context.getClass()).put(parentPosition, child);
		}else{
			checks.get(context.getClass()).get(parentPosition).clear();
			checks.get(context.getClass()).get(parentPosition).put(childPosition, childTitle);
			checks.get(context.getClass()).put(parentPosition, checks.get(context.getClass()).get(parentPosition));
		}
		
	}
	public static Map<Integer,Map<Integer,String>> getChecks(Context context){
		return checks.get(context.getClass());
	}
}
