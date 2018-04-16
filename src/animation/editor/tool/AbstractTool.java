package animation.editor.tool;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * π§æﬂπÊ∑∂
 * 
 * @author ∂Œ¡¥
 * @time 2012-9-15
 */
public interface AbstractTool extends KeyListener, MouseListener,
		MouseMotionListener {
	public abstract void beginSession();

	public abstract void endSession();

	public abstract void resetSession();

	public abstract String getDisplayName();
}
