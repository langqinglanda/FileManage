package com.xyw.android.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xyw.android.filemanager.R;
import com.xyw.android.filemanager.R.id;

import android.app.Activity;
import android.view.View;

/**
 * @version 0.2
 * @author xyw 
 * 
 * Initialize widgets with reflection.
 */
public class ViewUtil {

	private static Class<?> type;

	public static int initView(Activity activity) {
		int resultCode = 0;
		try {
			Class<id> idCls = R.id.class;
			Class<? extends Activity> cls = activity.getClass();
			Field[] fields = cls.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				type = fields[i].getType();
				if (View.class.isAssignableFrom(type)) {
					fields[i].setAccessible(true);
					Field idField = idCls.getDeclaredField(fields[i].getName());
					Method method = cls.getMethod("findViewById", new Class[] { int.class });
					Object idValue = idField.get(R.id.class.newInstance());
					Object view = method.invoke(activity, idValue);
					fields[i].set(activity, view);
				}
			}
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 1;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 2;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 3;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 4;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 5;
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultCode = 6;
		}
		return resultCode;
	}

}
