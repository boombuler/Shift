package com.boombuler.games.shift.render;

import org.cocos2d.opengl.CCBitmapFontAtlas;

public class Label extends CCBitmapFontAtlas {

	public static final float SMALLER = 24f;
	public static final float DEFAULT = 32f;
	private static final float REAL_FS = 48f;
	
	public Label(String string, float fontSize) {
		super(string, "font.fnt");
		this.setScale(fontSize / REAL_FS);
	}
	
	public Label(String string) {
		this(string, DEFAULT);
	}
	
}
