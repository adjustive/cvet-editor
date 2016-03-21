package io.cvet.editor.gui;

import io.cvet.editor.gfx.Render;
import io.cvet.editor.util.RNG;

import java.util.ArrayList;
import java.util.List;

public class TextArea extends Component {

	private List<Character> buffer;
	private int xOffset, yOffset;
	
	int charWidth = Render.MONOSPACED_FONT.getWidth("a");
	int charHeight = Render.MONOSPACED_FONT.getHeight();
	
	public TextArea(int w, int h) {
		this.w = w;
		this.h = h;
		this.buffer = new ArrayList<Character>();
		for (int i = 0; i < h * charHeight; i++) {
			int bufferWidth = (this.w / charWidth) - 10;
			if (i % bufferWidth == 0) {
				buffer.add('\n');
			}
			buffer.add((char) RNG.range(97, 122));
		}
	}
	
	@Override
	public void init() {
		
	}

	@Override
	public void update() {
		
	}

	public String[] getBufferString() {
		StringBuilder res = new StringBuilder(buffer.size());
		for (int i = 0; i < buffer.size(); i++) {
			res.append(buffer.get(i));
		}
		return res.toString().split("\n");
	}
	
	@Override
	public void render() {
		Render.colour(125, 0, 255);
		Render.rect(x, y, w, h);

		int line = -1; // TODO: fix this
		for (String s : getBufferString()) {
			Render.drawString(s, x + xOffset, y + yOffset + (line * charHeight));
			line++;
		}
	}

}
