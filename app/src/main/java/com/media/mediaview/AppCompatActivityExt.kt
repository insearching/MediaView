package com.media.mediaview

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity


const val READ_STORAGE_PERMISSION_REQUEST_CODE = 1001

fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment) {
    supportFragmentManager.transact {
        replace(R.id.container, fragment)
    }
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun AppCompatActivity.requestPermissions(vararg permissions: String): Boolean {
    val list = permissions.filter {
        ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
    }
    if (list.isEmpty()) {
        return true
    }
    ActivityCompat.requestPermissions(this, list.toTypedArray(), READ_STORAGE_PERMISSION_REQUEST_CODE)
    return false
}