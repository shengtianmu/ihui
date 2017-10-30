package com.zpstudio.ui.util;

import java.util.ArrayList;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.zpstudio.R;

public class AddressPickerAct extends Activity {
	private static final String EXTRA_ADDRESS_CONFIG = "address_config";
	public  static final String EXTRA_RESULT_SELECTED = "selected_address";
	AddressConfig mAddressConfig = null;
	Address mAddress = null;
	String countries[] = null;
	String provinces[] = null;
	String cities[][] = null;
	WheelView wvProvinc = null;
	WheelView wvCity    = null;
	LinearLayout grpMain = null;
	LinearLayout grpLoading = null;
	Handler mHandler = new Handler();
	Activity mActivity = null;
	Gson gson = new Gson();
	public static void addressPick(Activity activity ,int requestCode , 
			String config)
	{
		Intent i = new Intent(activity , AddressPickerAct.class);
		i.putExtra(EXTRA_ADDRESS_CONFIG, config);
		activity.startActivityForResult(i, requestCode);
	}
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.addressespicker_act);
        
        mActivity = this;
        grpMain = (LinearLayout)findViewById(R.id.main);;
        grpLoading = (LinearLayout)findViewById(R.id.loading);
        
        wvProvinc = (WheelView) findViewById(R.id.province);
        
        wvProvinc.addChangingListener(new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wvCity.setViewAdapter(new ArrayWheelAdapter<String>(mActivity,cities[newValue]));
				wvCity.setCurrentItem(cities[newValue].length / 2);
			}
		});
        
        wvCity = (WheelView) findViewById(R.id.city);
        
        
        findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View arg0) {
				mHandler.postDelayed(new Runnable() {
					
					public void run() {
						StringBuilder sb = new StringBuilder();
						sb.append(provinces[wvProvinc.getCurrentItem()]);
						sb.append(cities[wvProvinc.getCurrentItem()][wvCity.getCurrentItem()]);
						
						
						Bundle bundle = new Bundle();
						SingleAddress selectedAddress = new SingleAddress();
						selectedAddress.country = countries[0];
						selectedAddress.province = provinces[wvProvinc.getCurrentItem()];
						selectedAddress.city = cities[wvProvinc.getCurrentItem()][wvCity.getCurrentItem()];
						bundle.putString(EXTRA_RESULT_SELECTED,  gson.toJson(selectedAddress));   

						mActivity.setResult(RESULT_OK,mActivity.getIntent().putExtras(bundle));
						mActivity.finish();
					}
				}, 400);
				
			}
		});
        
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int)(display.getWidth()); 
        getWindow().setAttributes(lp); 
        
        Intent i = getIntent();
        String config = i.getStringExtra(EXTRA_ADDRESS_CONFIG);
        mAddressConfig = gson.fromJson(config, AddressConfig.class);
        mAddress = mAddressConfig.addressTree;
        updateView();
    }
    
    public void updateView()
    {
    	if(null != mAddress)
    	{
    		countries = mAddress.getCountries();
    		if(countries.length > 0)
    		{
    			provinces = mAddress.findCountry(countries[0]).getProvinces();
    			cities = new String[provinces.length][];
    			for(int i = 0; i < provinces.length ;i ++)
    			{
    				cities[i] = mAddress.findCountry(countries[0]).findProvince(provinces[i]).getCities();
    			}
    		}
    	}
    	
    	wvProvinc.setViewAdapter(new ArrayWheelAdapter<String>(this,provinces));
    	wvProvinc.setVisibleItems(6);
        wvProvinc.setCyclic(true);
    	wvProvinc.setCurrentItem(0);
    	
    	wvCity.setViewAdapter(new ArrayWheelAdapter<String>(this,cities[wvProvinc.getCurrentItem()]));
    	wvCity.setVisibleItems(10);
        wvCity.setCurrentItem(cities[wvProvinc.getCurrentItem()].length / 2);
        
        grpMain.setVisibility(View.VISIBLE);
        grpLoading.setVisibility(View.GONE);
    }
    
    public static class AddressConfig{
    	public Address addressTree;
    	public SingleAddress inputAddress;
    }
    
    public static class SingleAddress{
    	public String country;
    	public String province;
    	public String city;
    	
    }
    
    public static class Address
    {
    	public List<Country> countries;
    	Country findCountry(String name)
    	{
    		Country country = null;
    		if(null == countries)
    		{
    			return null;
    		}
    		for(int i = 0; i < countries.size() ;i ++)
    		{
    			if(countries.get(i).name.equals(name))
    			{
    				country = countries.get(i);
    				break;
    			}
    		}
    		return country;
    	}
    	public String[] getCountries()
    	{
    		List<String> list = new ArrayList<String>();
    		if(null != countries)
    		{
    			for(int i = 0 ; i < countries.size() ;i ++)
    			{
    				list.add(countries.get(i).name);
    			}
    		}
    		return list.toArray(new String[0]);
    	}
		Country addCountry(String name)
    	{
    		Country country = findCountry(name);
    		if(null == country)
    		{
    			country = new Country();
    			country.name = name;
    			if(null == countries)
    			{
    				countries = new ArrayList<Country>();
    				
    			}
    			countries.add(country);
    		}
    		return country;
    	}
    	static public class Country
    	{
    		String name;
    		public List<Province> provinces;
    		Province findProvince(String name)
    		{
    			Province province = null;
    			if(null == provinces)
    			{
    				return null;
    			}
    			for(int i = 0; i < provinces.size() ;i ++)
    			{
    				if(provinces.get(i).name.equals(name))
    				{
    					province = provinces.get(i);
    					break;
    				}
    			}
    			return province;
    		}
    		
    		public String[] getProvinces()
			{
				
				List<String> list = new ArrayList<String>();
				if(null != provinces)
				{
					for(int i = 0 ; i < provinces.size() ;i ++)
					{
						list.add(provinces.get(i).name);
					}
				}
				return list.toArray(new String[0]);
			}

			Province addProvince(String name)
    		{
    			Province province = findProvince(name);
    			if(null == province)
    			{
    				province = new Province();
    				province.name = name;
    				if(null == provinces)
    				{
    					provinces = new ArrayList<Province>();
    					
    				}
    				provinces.add(province);
    			}
    			return province;
    		}
    	}
    	
    	static public class Province
    	{
    		String name;
    		public List<City> cities;
    		City findCity(String name)
    		{
    			City city = null;
    			if(null == cities)
    			{
    				return null;
    			}
    			for(int i = 0; i < cities.size() ;i ++)
    			{
    				if(cities.get(i).name.equals(name))
    				{
    					city = cities.get(i);
    					break;
    				}
    			}
    			return city;
    		}
    		public String[] getCities()
    		{
    			
    			List<String> list = new ArrayList<String>();
    			if(null != cities)
    			{
    				for(int i = 0 ; i < cities.size() ;i ++)
    				{
    					list.add(cities.get(i).name);
    				}
    			}
    			return list.toArray(new String[0]);
    		}
			City addCity(String name)
    		{
    			City city = findCity(name);
    			if(null == city)
    			{
    				city = new City();
    				city.name = name;
    				if(null == cities)
    				{
    					cities = new ArrayList<City>();
    					
    				}
    				cities.add(city);
    			}
    			return city;
    		}
    	}
    	
    	static public class City
    	{
    		String name;
    	}
    	
    }
}

