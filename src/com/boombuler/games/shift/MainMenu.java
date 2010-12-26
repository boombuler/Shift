package com.boombuler.games.shift;

import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.menus.*;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.transitions.CCTransitionScene;

import com.boombuler.games.shift.Game.Difficulty;
import com.boombuler.games.shift.render.Background;
import com.boombuler.games.shift.render.Label;




public class MainMenu extends CCLayer {

	private static CCScene fCurrent = null;
	
	public static CCScene scene() {
		if (fCurrent == null) {
			fCurrent = CCScene.node();
			fCurrent.addChild(new Background());
			fCurrent.addChild(new MainMenu());
		}
		return fCurrent;
	}
	
	private CCLayer getMenu() {	
		CCMenuItem easy = getTextItem(R.string.easy, "startEasy");
		CCMenuItem normal = getTextItem(R.string.normal, "startNormal");
		CCMenuItem quit = getTextItem(R.string.quit, "onQuit");
		CCMenuItem help = getTextItem(R.string.show_help, "showHelp");
		
        CCMenu result = CCMenu.menu(easy, normal, help, quit);
		result.alignItemsVertically();
		return result;
	}
	
	private CCMenuItem getTextItem(int resourceId, String selector) {
		Label lbl = new Label(MyResources.string(resourceId), Label.DEFAULT);
		return CCMenuItemAtlasFont.item(lbl, this, selector);
	}
	
	private MainMenu() {
		this.addChild(getMenu());
	}
	
	public void onQuit() {
		CCDirector.sharedDirector().getActivity().finish();		
	}
	
	public void startEasy() {
		Game.Current().setDifficulty(Difficulty.Easy);
		CCTransitionScene board = Main.getTransisionFor(Board.scene());
		CCDirector.sharedDirector().replaceScene(board);
	}
	
	public void startNormal() {
		Game.Current().setDifficulty(Difficulty.Normal);
		CCTransitionScene board = Main.getTransisionFor(Board.scene());
		CCDirector.sharedDirector().replaceScene(board);		
	}
	
	public void showHelp() {
		CCTransitionScene helpScrn = Main.getTransisionFor(HelpScreen.scene(scene()));
		CCDirector.sharedDirector().replaceScene(helpScrn);
	}
	
}
