package com.hzjstudio.ihui.fragment;

import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class FragmentPopularity extends IndianaBaseFragment {
	
	protected String getIndianaType()
	{
		return IhuiClientApi.INDIANA_TYPE_people_desc;
	}
	
}
