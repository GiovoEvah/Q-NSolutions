package com.example.qnsolutions.common

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.TextView


class CustomSearchView : SearchView
{
    var mSearchSrcTextView: AutoCompleteTextView? = null
    var listener: OnQueryTextListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    override fun setOnQueryTextListener(listener: OnQueryTextListener?)
    {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        //faccio in modo di richiamare setOnQueryText anche se il campo di testo è vuoto
        mSearchSrcTextView = findViewById(super.getContext().resources.getIdentifier("android:id/search_src_text", null, null))
        mSearchSrcTextView?.setOnEditorActionListener()
        { _: TextView?, _: Int, _: KeyEvent? ->
            listener?.onQueryTextSubmit(query.toString())
            true
        }
    }
}