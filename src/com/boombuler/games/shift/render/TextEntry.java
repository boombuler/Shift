package com.boombuler.games.shift.render;

import org.cocos2d.nodes.CCSprite;

public class TextEntry extends CCSprite {
	
	public TextEntry(boolean selected) {
		super(selected ? "textentry_sel.png" : "textentry.png");		
	}

}
