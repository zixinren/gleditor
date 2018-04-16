package animation.editor.modulesmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import animation.editor.ScalableViewer;
import animation.world.Animation;
import animation.world.Module;

/**
 * 模块原始视图, 一组模块排成排
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ModuleSrcViewer extends ScalableViewer {
	public static final int MODULE_WIDTH = 25;
	private static final long serialVersionUID = -6617882798225296700L;

	public ModuleSrcViewer() {
		setViewScale(2.0D);
		zoomViewer();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int dx = 0;
		int dy = 10;
		int i = 0;
		for (Module module : Animation.instance().getModuleList()) {
			g.setColor(Color.RED);
			g.drawString(String.valueOf(i), dx * 2, dy);
			if (!ModuleSelectTool.isSelect()) {
				Animation.aniImage().render((Graphics2D) g, module, dx, dy,
						getTransform(),
						Module.getModel().getSelection() == module);
			} else {
				Animation.aniImage().render((Graphics2D) g, module, dx, dy,
						getTransform(), false);
			}

			dx += MODULE_WIDTH;
			++i;
		}
	}

	@Override
	public Dimension getOriginSize() {
		return new Dimension(Animation.instance().getModuleList().size()
				* MODULE_WIDTH, 45);
	}

}
