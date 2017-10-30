package com.hzjstudio.ihui.fragment;

import com.zpstudio.datacenter.db.op.IhuiClientApi;



public class FragmentTakeDown extends IndianaBaseFragment {
	
	protected String getIndianaType()
	{
		return IhuiClientApi.INDIANA_TYPE_all_people_desc;
	}
	
}