/*
 * Copyright (C) 2010 Florian Sundermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.boombuler.games.shift;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cocos2d.nodes.CCDirector;

import com.boombuler.games.shift.Game.Difficulty;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings implements Comparator<Long>{

	public static final int MAX_HIGHSCORE_COUNT = 5;
	private static final Settings fCurrent = new Settings();
	
	public static Settings Current() {
		return fCurrent;
	}
	
	private static final String PREF_HAS_READ_MANUAL = "HAS_READ_MANUAL";
	private static final String HS_EASY = "HS_EASY_%d";
	private static final String HS_NORMAL = "HS_NORMAL_%d";
	private static final String HS_HARD = "HS_HARD_%d";
	
	private boolean mHasReadManual;
	private List<Long> mHighscoreEasy = new ArrayList<Long>(MAX_HIGHSCORE_COUNT+1);
	private List<Long> mHighscoreNormal = new ArrayList<Long>(MAX_HIGHSCORE_COUNT+1);
	private List<Long> mHighscoreHard = new ArrayList<Long>(MAX_HIGHSCORE_COUNT+1);
	
	private Settings() {
		SharedPreferences prefs = getPrefs();
		mHasReadManual = prefs.getBoolean(PREF_HAS_READ_MANUAL, false);
		
		for(int i = 0; i < MAX_HIGHSCORE_COUNT; i++) {
			mHighscoreEasy.add(prefs.getLong(String.format(HS_EASY, i), 0));
			mHighscoreNormal.add(prefs.getLong(String.format(HS_NORMAL, i), 0));
			mHighscoreHard.add(prefs.getLong(String.format(HS_HARD, i), 0));
		}		
	}
	
	private SharedPreferences getPrefs() {
		return CCDirector.sharedDirector().getActivity().getSharedPreferences("settings", 0);
	}
	
	public boolean getHasReadManual() {
		return mHasReadManual;
	}
	
	public void setHasReadManual(boolean value) {
		mHasReadManual = value;
		SharedPreferences prefs = getPrefs();
		Editor edit = prefs.edit();
		edit.putBoolean(PREF_HAS_READ_MANUAL, value);
		edit.commit();
	}
	
	public boolean addToHighscore(Difficulty difficulty, long score) {
		final List<Long> lst;
		final String key;
		if (difficulty == Difficulty.Easy){
			lst = mHighscoreEasy;
			key = HS_EASY;
		} else if (difficulty == Difficulty.Normal) {
			lst = mHighscoreNormal;
			key = HS_NORMAL;
		} else {
			lst = mHighscoreHard;
			key = HS_HARD;
		}
		if (score <= lst.get(MAX_HIGHSCORE_COUNT-1))
			return false;
		
		
		lst.add(score);
		Collections.sort(lst, this);
		SharedPreferences prefs = getPrefs();
		Editor edit = prefs.edit();
		for (int i = 0; i < MAX_HIGHSCORE_COUNT; i++) {
			edit.putLong(String.format(key, i), lst.get(i));
		}
		edit.commit();
		while(lst.size() > MAX_HIGHSCORE_COUNT) {
			lst.remove(MAX_HIGHSCORE_COUNT);
		}
		return true;
	}

	@Override
	public int compare(Long object1, Long object2) {
		return object2.compareTo(object1);
	}
	
	public Long[] getHighscores(Difficulty difficulty) {
		Long[] result = new Long[MAX_HIGHSCORE_COUNT];
		switch(difficulty) {
			case Easy:
				mHighscoreEasy.toArray(result); break;
			case Normal:
				mHighscoreNormal.toArray(result); break;
			default:
				mHighscoreHard.toArray(result); break;
		}
		return result;
	}
}
