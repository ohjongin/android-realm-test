package me.ji5.restracker.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.ji5.restracker.R;

/**
 * Created by ohjongin on 5/12/14.
 */
public class FragmentFactory extends FragmentPagerAdapter {
    protected FragmentFactory mInstance;
    protected static Fragment mFragments[];
    protected String[] mSectionTitle;
    protected Context mContext;
    protected FragmentManager mFragmentManager;

    public FragmentFactory getIntance(Context context, FragmentManager fm) {
        if (mInstance == null) {
            mInstance = new FragmentFactory(context, fm);
        }

        return mInstance;
    }

    public FragmentFactory(Context context, FragmentManager fm) {
        super(fm);
        mSectionTitle = context.getResources().getStringArray(R.array.sections);
        mFragments = new Fragment[mSectionTitle.length];
        mContext = context;
        mFragmentManager = fm;
    }

    public Fragment getFragment(int position) {
        return getItem(position);
    }

    @Override
    public Fragment getItem(int position) {
        if (position < 0 || position >= mFragments.length) {
            return null;
        }

        Fragment fragment = mFragments[position];
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = MainFragment.newInstance();
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            mFragments[position] = fragment;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return mSectionTitle.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mSectionTitle[position];
    }
}
