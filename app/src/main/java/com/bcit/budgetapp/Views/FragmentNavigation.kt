package com.bcit.budgetapp.Views

import androidx.fragment.app.Fragment

interface FragmentNavigation {
    fun navigateFrag(fragment:Fragment, addToStack: Boolean)
}