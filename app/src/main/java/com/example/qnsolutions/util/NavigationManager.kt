package com.example.qnsolutions.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class NavigationManager
{
    fun apriFragment(activity: AppCompatActivity, layout: Int, fragment: Fragment, tag: String)
    {
        activity.supportFragmentManager.beginTransaction().add(layout, fragment, tag).addToBackStack(tag).commit()
    }

    fun scambiaFragment(activity: AppCompatActivity, layout: Int, fragment: Fragment, tag: String, root_tag: String, leaf: Boolean, bundle : Bundle?)
    {
        val manager = activity.supportFragmentManager

        if(leaf && manager.getBackStackEntryAt(manager.backStackEntryCount-1).name != root_tag)
        {
                manager.popBackStack()
        }

        if(bundle != null)
        {
            fragment.arguments = bundle
        }

        manager.beginTransaction().replace(layout, fragment, tag).addToBackStack(tag).commit()
    }

    fun rimuoviFragment(activity: AppCompatActivity, tag: String)
    {
        val fragment = activity.supportFragmentManager.findFragmentByTag(tag)

        if(fragment != null)
        {
            activity.supportFragmentManager.beginTransaction().remove(fragment).commit()
            activity.supportFragmentManager.popBackStack()
        }
    }
}