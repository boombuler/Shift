package com.boombuler.games.shift;

import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;


public class TestScene extends Layer{

	public static Scene scene() {
		Scene scene = Scene.node();
		Layer layer = new TestScene();

		scene.addChild(layer);

		return scene;
	}
	
	
	protected TestScene() {
        this.setIsTouchEnabled(true);
        Label lbl = Label.label("Hello World!", "DroidSans", 24);
        addChild(lbl, 0);
        lbl.setPosition(160, 240);
    }

	
	
	
	
	
}
