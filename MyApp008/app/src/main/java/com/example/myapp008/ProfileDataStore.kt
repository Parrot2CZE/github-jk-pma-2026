package com.example.myapp008

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// 1. Vytvoření instance DataStore na úrovni souboru (tzv. top-level).
//    Musí existovat jen JEDNA instance pro daný soubor "gamer_profile",
//    proto ji máme tady a sdílí ji oba fragmenty (Dashboard i Nastavení).
val Context.dataStore by preferencesDataStore(name = "gamer_profile")

// 2. Definice klíčů (DataStore je na rozdíl od SharedPreferences typově bezpečný)
object ProfileKeys {
    val NICKNAME = stringPreferencesKey("NICKNAME")
    val HARDCORE = booleanPreferencesKey("HARDCORE")
    val SAVE_COUNT = intPreferencesKey("SAVE_COUNT")   // kolikrát už byl profil uložen
}
