package com.zpstudio.datacenter.db.im;

import java.util.Comparator;


public class ComparatorCellPhoneContact implements Comparator<IMCellphoneContact> {

	@Override
	public int compare(IMCellphoneContact lhs, IMCellphoneContact rhs) {
		String str1 = lhs.getPy();
		String str2 = rhs.getPy();
		return str1.compareToIgnoreCase(str2);
	}
}
