package com.boombuler.games.shift.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccConfig;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccBlendFunc;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

public class CCGradientLayer extends CCLayer 
        implements CCNode.CocosNodeSize {
	
    /** Opacity: conforms to CCRGBAProtocol protocol */
    protected ccColor3B color_;
    /** Opacity: conforms to CCRGBAProtocol protocol */
    protected int opacity_;
    /** BlendFunction. Conforms to CCBlendProtocol protocol */
	protected ccBlendFunc	blendFunc_;

    protected FloatBuffer squareVertices_;
    protected FloatBuffer squareColors_;

    protected void init(ccColor4B color, float w, float h) {
        ByteBuffer vbb = ByteBuffer.allocateDirect(4 * 2 * 4);
        vbb.order(ByteOrder.nativeOrder());
        squareVertices_ = vbb.asFloatBuffer();

        ByteBuffer sbb = ByteBuffer.allocateDirect(4 * 4 * 4);
        sbb.order(ByteOrder.nativeOrder());
        squareColors_ = sbb.asFloatBuffer();

        color_ = new ccColor3B(color.r, color.g, color.b);
        opacity_ = color.a;
		blendFunc_ = new ccBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        for (int i = 0; i < (4 * 2); i++) {
            squareVertices_.put(i, 0);
        }
        squareVertices_.position(0);

        updateColor();
        setContentSize(CGSize.make(w, h));
    }

    @Override
    public void draw(GL10 gl) {
        // Default GL states: GL_TEXTURE_2D, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TEXTURE_COORD_ARRAY
        // Needed states: GL_VERTEX_ARRAY, GL_COLOR_ARRAY
        // Unneeded states: GL_TEXTURE_2D, GL_TEXTURE_COORD_ARRAY
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, squareVertices_);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, squareColors_);

        boolean newBlend = false;
        if (blendFunc_.src != ccConfig.CC_BLEND_SRC || blendFunc_.dst != ccConfig.CC_BLEND_DST) {
            newBlend = true;
            gl.glBlendFunc(blendFunc_.src, blendFunc_.dst);
        } else if (opacity_ != 255) {
            newBlend = true;
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        if (newBlend)
            gl.glBlendFunc(ccConfig.CC_BLEND_SRC, ccConfig.CC_BLEND_DST);

        // restore default GL state
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

    // Opacity Protocol
    public void setOpacity(int o) {
        opacity_ = o;
        updateColor();
    }

    public int getOpacity() {
        return opacity_;
    }

    // Size protocol

    public float getWidth() {
        return squareVertices_.get(2);
    }

    public float getHeight() {
        return squareVertices_.get(5);
    }

    @Override
    public void setContentSize(CGSize size) {

        // Layer default ctor calls setContentSize priot to nio alloc
        if (squareVertices_ != null) {
            squareVertices_.put(2, size.width);
            squareVertices_.put(5, size.height);
            squareVertices_.put(6, size.width);
            squareVertices_.put(7, size.height);
        }

        super.setContentSize(size);
    }

    /** change width and height 
     * @since v0.8 */
    public void changeWidthAndHeight(float w, float h) {
        setContentSize(CGSize.make(w, h));
    }

    /** change width */
    public void changeWidth(float w) {
        setContentSize(CGSize.make(w, getHeight()));
    }

    /** change height */
    public void changeHeight(float h) {
        setContentSize(CGSize.make(getWidth(), h));
    }
	
	public enum GradientDirection
	{
		// 00 represents colorFrom
		// 01 represents colorTo
		// 02 represents colorFrom/colorTo average
		TopToBottom 			((byte)00, (byte)00, (byte)01, (byte)01),
		BottomToTop				((byte)01, (byte)01, (byte)00, (byte)00),
		TopRightToBottomLeft	((byte)00, (byte)02, (byte)02, (byte)01),
		TopLeftToBottomRight	((byte)02, (byte)00, (byte)01, (byte)02),
		BottomRightToTopLeft	((byte)02, (byte)01, (byte)00, (byte)02),
		BottomLeftToTopRight	((byte)01, (byte)02, (byte)02, (byte)00);
		
		public final byte TL;
		public final byte TR;
		public final byte BL;
		public final byte BR;
		
		private GradientDirection(byte tl, byte tr, byte bl, byte br) {
			this.TL = tl;
			this.TR = tr;
			this.BL = bl;
			this.BR = br;
		}
	}
	
	protected CCGradientLayer(ccColor4B fromColor, ccColor4B toColor, 
			GradientDirection direction, float width, float height) {
		super();
		initWithColor(fromColor, toColor, direction, width, height);
	}
	
	public static CCGradientLayer node(ccColor4B fromColor, 
			ccColor4B toColor, GradientDirection direction)	
	{
		CGSize size = CCDirector.sharedDirector().winSize();
		return node(fromColor, toColor, direction, size.width, size.height);
	}

	public static CCGradientLayer node(ccColor4B fromColor, ccColor4B toColor, 
			GradientDirection direction, float width, float height)
	{
		return new CCGradientLayer(fromColor, toColor, direction, width, height);
	}

	protected void initWithColor(ccColor4B fromColor, ccColor4B toColor, 
			GradientDirection direction, float width, float height)
	{
		init(fromColor, width, height);
		// this char will have a code for each point in the grid (4 points)
		// 00 meant colorFrom, 01 means colorTo, 02 means the average of the two
		ccColor4B[] colors = new ccColor4B[] { fromColor, toColor,
				ccColor4B.ccc4((fromColor.r+toColor.r)/2, 
						(fromColor.g+toColor.g)/2, 
						(fromColor.b+toColor.b)/2, 
						(fromColor.a+toColor.a)/2) };

		// for each point in the grid
		for (int x = 0; x < 4; x++)
		{
			byte colIndex;
			switch(x) {
				case 0: colIndex = direction.TL; break;
				case 1: colIndex = direction.TL; break;
				case 2: colIndex = direction.BL; break;
				case 3: colIndex = direction.BR; break;
				default: colIndex = 0;
			}
			
			// get the color represented by this index
			ccColor4B col = colors[(int)colIndex];

			// apply it
			setColor(col, x);
		}
	}
	
	protected void setColor(ccColor4B color, int index) {
		// set the color starting from the given index
		squareColors_.put(index*4+0, color.r / 255f);
		squareColors_.put(index*4+1, color.g / 255f);
		squareColors_.put(index*4+2, color.b / 255f);
		squareColors_.put(index*4+3, color.a / 255f);
	}

	public void updateColor() {
		for (int x = 0; x < 4; x++)
			squareColors_.put(x*4+3, opacity_ / 255f);
	}
}
