package animation.editor.modulesmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import animation.editor.ScalableViewer;
import animation.world.Animation;
import animation.world.Module;

/**
 * 模块映射视图
 * 
 * @author 段链
 * @time 2012-9-15
 */
public class ModuleMapViewer extends ScalableViewer {
	private static final long serialVersionUID = 2765358933904033587L;

	public ModuleMapViewer() {
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

			Animation.aniImage().render((Graphics2D) g, module, dx, dy,
					getTransform(), Module.getModel().getSelection() == module);

			ModulesMapData modulesMapData = ModulesStylesData
					.getModulesMapData();
			if (modulesMapData != null) {
				/**
				 * 绘制对当前模块的映射
				 */
				for (int j = 0; j < modulesMapData.getModulesMapSize(); ++j) {
					if (i != modulesMapData.getModulesMapSrcData(j))
						continue;
					g.drawString(
							i + ">" + modulesMapData.getModulesMapDesData(j),
							dx * 2, dy + 60);
				}
			}

			dx += ModuleSrcViewer.MODULE_WIDTH;
			++i;
		}
	}

	@Override
	public Dimension getOriginSize() {
		return new Dimension(Animation.instance().getModuleList().size()
				* ModuleSrcViewer.MODULE_WIDTH, 50);
	}

}
