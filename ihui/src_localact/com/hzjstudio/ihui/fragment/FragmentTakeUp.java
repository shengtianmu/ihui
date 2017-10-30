package com.hzjstudio.ihui.fragment;

import com.zpstudio.datacenter.db.op.IhuiClientApi;



public class FragmentTakeUp extends IndianaBaseFragment {
	
	protected String getIndianaType()
	{
		return IhuiClientApi.INDIANA_TYPE_all_people_asc;
	}
	
}