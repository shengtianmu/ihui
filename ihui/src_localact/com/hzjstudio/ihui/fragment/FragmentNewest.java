package com.hzjstudio.ihui.fragment;

import com.zpstudio.datacenter.db.op.IhuiClientApi;

public class FragmentNewest extends IndianaBaseFragment {
	
	protected String getIndianaType()
	{
		return IhuiClientApi.INDIANA_TYPE_indiana_id_desc;
	}
	
}