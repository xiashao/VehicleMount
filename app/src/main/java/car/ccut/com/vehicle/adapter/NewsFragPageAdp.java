package car.ccut.com.vehicle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import car.ccut.com.vehicle.bean.NewsCate;

public class NewsFragPageAdp extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;
	private List<NewsCate> cateList = new ArrayList<>();

	public NewsFragPageAdp(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public NewsFragPageAdp(FragmentManager fm,
						   ArrayList<Fragment> fragments, List<NewsCate> newsCates) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
		this.cateList = newsCates;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return cateList.get(position).getCateName();
	}


}
