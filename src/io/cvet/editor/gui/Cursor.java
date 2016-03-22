package io.cvet.editor.gui;

import io.cvet.editor.Editor;
import io.cvet.editor.config.Settings;
import io.cvet.editor.gfx.Colour;
import io.cvet.editor.gfx.Render;

import org.lwjgl.input.Keyboard;

public class Cursor extends Component {
    public static enum CursorStyle {
        Block,
        Line,
    }

    private TextArea owner;
    private Colour colour = new Colour(30, 30, 30);
    
    // where the cursor is positioned in the buffer
    // insertion x, y
    private int ix, iy;
    
    private CursorStyle cursorStyle;
    private int xOffset, yOffset;
    private int charWidth, charHeight;
    private int padding;

    private boolean hungryBackspace;
    private boolean matchBraces;
    
    private CursorAction cursorAction = null;
    
    public Cursor(TextArea owner, CursorStyle style) {
        this.owner = owner;
        this.cursorStyle = style;
        this.ix = iy = 0;
        charWidth = Render.MONOSPACED_FONT.getWidth("a");
        charHeight = Render.MONOSPACED_FONT.getHeight();
        this.h = charHeight;
        this.w = cursorStyle == CursorStyle.Block ? charWidth : 1;
        
        this.hungryBackspace = (boolean) Settings.getSetting("hungry_backspace");
        this.matchBraces = (boolean) Settings.getSetting("match_braces");
    }
    
    @Override
    public void init() {
        
    }
    
    public void handleControlCombo() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                int keyCode = Keyboard.getEventKey();
                switch (keyCode) {
                case Keyboard.KEY_C: // copy
                    // TODO:
                    break;
                case Keyboard.KEY_N:
                    
                    break;
                case Keyboard.KEY_W: // close file
                    Editor.getInstance().closeCurrentBuffer();
                    break;
                case Keyboard.KEY_S: // save
                    Editor.getInstance().getCurrentBuffer().save();
                    // TODO:
                    break;
                case Keyboard.KEY_L: // line goto
                    Editor.getInstance().showCommandPalette("goto ");
                    break;
                case Keyboard.KEY_LEFT: // left word
                    // TODO: 
                    break;
                case Keyboard.KEY_RIGHT: // right word
                    // TODO:
                    break;
                case Keyboard.KEY_D: // delete line
                    if (iy >= 0 && iy < owner.getLineCount() - 1) {
                        owner.deleteLine(iy);
                        carriageReturn();
                    } else if (iy == owner.getLineCount() - 1 && owner.getLine(iy).length() == 0) {
                        if (iy != 0) {
                            owner.deleteLine(iy);
                            move(owner.getLine(iy - 1).length(), -1);
                        }
                    } else if (owner.getLine(iy).toString().trim().length() == 0) {
                        owner.deleteLine(iy);
                        move(owner.getLine(iy - 1).length(), -1);
                    } else {
                        System.out.println("hey");
                        owner.clearLine(iy);
                        carriageReturn();
                    }
                    break;
                default:
                    System.out.println(keyCode);
                    break;
                // IGNORE THESE
                case Keyboard.KEY_LSHIFT:
                case Keyboard.KEY_RSHIFT:
                case Keyboard.KEY_LCONTROL:
                case Keyboard.KEY_RCONTROL:
                    break;
                }
            }
        }
    }
    
    public void handleShiftCombo() {
        
    }

    public void handleKeyCode(int keyCode) {
        switch (keyCode) {
        case Keyboard.KEY_LSHIFT:
        case Keyboard.KEY_RSHIFT:
        case Keyboard.KEY_LCONTROL:
        case Keyboard.KEY_RCONTROL:
        case Keyboard.KEY_LMETA:
        case Keyboard.KEY_RMETA:
        case Keyboard.KEY_LMENU:
        case Keyboard.KEY_RMENU:
        case Keyboard.KEY_F2:
        case Keyboard.KEY_ESCAPE:
            // nothing
            break;
        case Keyboard.KEY_BACK:
            if (hungryBackspace && ix - owner.getTabSize() >= 0) {
                String cut = owner.getLine(iy).substring(ix - owner.getTabSize(), ix);
                // if the last X characters are == to our tabSize
                // and if we trim the whitespace and it becomes 
                // the length of zero, they are all spaces we can
                // remove.
                if (cut.length() == owner.getTabSize()
                        && cut.trim().length() == 0) {
                    for (int i = 0; i < owner.getTabSize(); i++) {
                        owner.backspace(this, ix, iy);
                    }
                } else {
                    owner.backspace(this, ix, iy);
                }
            } else {
                owner.backspace(this, ix, iy);
            }
            break;
        case Keyboard.KEY_LEFT:
            if (ix > 0) {
                move(-1, 0);
            }
            break;
        case Keyboard.KEY_RIGHT:
            if (ix < owner.getLine(iy).length()) {
                move(1, 0);
            }
            break;
        case Keyboard.KEY_DELETE:
            owner.delete(ix, iy);
            break;
        case Keyboard.KEY_LBRACKET:
            char c = Keyboard.getEventCharacter();
            owner.place(c, ix, iy);
            move(1, 0);
            if (matchBraces) {
                owner.place((char) ((int) (c + 2)), ix, iy);
            }
            break;
        case Keyboard.KEY_UP:
            if (iy > 0) {
                int prevLineLen = owner.getLine(iy - 1).length();
                if (ix >= prevLineLen) {
                    move(prevLineLen - ix, -1);
                } else {
                    move(0, -1);
                }
            }
            break;
        case Keyboard.KEY_DOWN:
            if (ix >= owner.getLine(iy).length()
                && iy < owner.getLineCount() - 1) {
                int nextLineLen = owner.getLine(iy + 1).length();
                if (ix <= nextLineLen) {
                    move(nextLineLen - ix, 1);
                } else if (ix >= nextLineLen) {
                    move(nextLineLen - ix, 1);
                }
            } else {
                if (iy < owner.getLineCount() - 1) {
                    int nextLineLen = owner.getLine(iy + 1).length();
                    if (ix >= nextLineLen) {
                        move(nextLineLen - ix, 1);
                    } else {
                        move(0, 1);
                    }
                }
            }
            break;
        case Keyboard.KEY_HOME:
            if (ix > 0) {
                move(-ix, 0);
            }
            break;
        case Keyboard.KEY_END:
            if (ix < owner.getLine(iy).length()) {
                move(owner.getLine(iy).length() - ix, 0);
            }
            break;
        case Keyboard.KEY_RETURN:
            owner.newline(ix, iy);
            move(-ix, 1);
            carriageReturn();
            break;
        case Keyboard.KEY_TAB:
            move(owner.tab(ix, iy), 0);
            break;
        default:
            owner.place(Keyboard.getEventCharacter(), ix, iy);
            move(1, 0);
            break;
        }
    }
    
    @Override
    public void update() {
        this.x = owner.x;
        this.y = owner.y;
        this.visible = owner.visible;
        
        // TODO: cleanup
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) 
                || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) { 
            handleControlCombo();
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) 
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            handleShiftCombo();
        }
        
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                int keyCode = Keyboard.getEventKey();
                
                if (cursorAction != null 
                        && cursorAction.keyPress(keyCode)) {
                    return;
                }
                handleKeyCode(keyCode);
            }
        }
    }

    @Override
    public void render() {
        Render.colour(colour);
        Render.rect(x + xOffset + padding, y + yOffset + padding, w, h);
    }
    
    public void carriageReturn() {
        xOffset = 0;
        ix = 0;
    }
    
    public void setCursorAction(CursorAction action) {
        this.cursorAction = action;
    }
    
    public void move(int x, int y) {
        ix += x;
        iy += y;
        xOffset += charWidth * x;
        yOffset += charHeight * y;
    }

    public void setOffset(int padding) {
        this.padding = padding;
    }

    public void reset() {
        yOffset = xOffset = ix = iy = 0;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    public void setHungryBackspace(boolean hungryBackspace) {
        this.hungryBackspace = hungryBackspace;
    }
    
}
