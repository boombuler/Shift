package com.boombuler.games.shift.render;

import org.cocos2d.nodes.CCSprite;

public class TextEntry extends CCSprite {
	
	public enum TextBoxType 
	{
		Selected,
		NotSelected,
		Normal		
	}
	
	private final Label mLabel;
	
	public TextEntry(TextBoxType type, String text) {
		super(type == TextBoxType.Normal ? "textentry.png" : 
				(type == TextBoxType.Selected ? "textentry_sel.png" : "textentry_nosel.png"));
		if (text != null) {
			mLabel = new Label(text);
			mLabel.setAnchorPoint(0f, 0f);
			if (type == TextBoxType.Normal) 
				mLabel.setPosition(10f, 3f);
			else
				mLabel.setPosition(40f,3f);
			this.addChild(mLabel);
		}
		else mLabel = null;		
	}

	public Label getLabel() {
		return mLabel;
	}
}
