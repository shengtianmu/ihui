package com.zpstudio.datacenter.db;

import com.zpstudio.bbs.bean.BBSPost;

public class Bater extends BBSPost{
	private ExtraDataInfo extraDatainfo;
	
	public ExtraDataInfo getExtraDatainfo() {
		return extraDatainfo;
	}

	public void setExtraDatainfo(ExtraDataInfo extraDatainfo) {
		this.extraDatainfo = extraDatainfo;
	}

	public static class ExtraDataInfo
	{
		public double price ;
		public String address ;
		public String contact ;
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		} 
		
		
	}
	
}
