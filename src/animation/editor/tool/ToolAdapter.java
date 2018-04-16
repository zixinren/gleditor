package animation.editor.tool;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import animation.editor.ScalableViewer;

/**
 * 工具规范简单实现
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ToolAdapter implements AbstractTool {
	protected ScalableViewer viewer;

	public ToolAdapter(ScalableViewer viewer) {
		this.viewer = viewer;
	}

	public ScalableViewer getViewer() {
		return viewer;
	}

	public void setViewer(ScalableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void beginSession() {

	}

	@Override
	public void endSession() {

	}

	@Override
	public void resetSession() {

	}

	@Override
	public String getDisplayName() {

		return null;
	}

}
