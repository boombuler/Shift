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
package com.boombuler.games.shift.render;

import org.cocos2d.opengl.CCBitmapFontAtlas;

public class Label extends CCBitmapFontAtlas {

	public static final float SMALLER = 24f;
	public static final float DEFAULT = 32f;
	private static final float REAL_FS = 32f;
	
	public Label(String string, float fontSize) {
		super(string, "font.fnt");
		this.setScale(fontSize / REAL_FS);
	}
	
	public Label(String string) {
		this(string, DEFAULT);
	}
	
}
