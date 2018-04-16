package animation.editor;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import animation.world.AniFrame;
import animation.world.Animation;
import animation.world.ImageProvider;
import animation.world.Sprite;
import animation.world.Template;
import animation.world.TemplateFrame;

/**
 * 模板角色视图
 * 
 * @author 段链
 * @time 2012-9-18
 */
public class TemplateActorViewer extends ScalableViewer implements Localizable {
	private static final long serialVersionUID = 4808605159928603175L;

	private AniFrame aniFrame;
	/**
	 * 是否绘制网格
	 */
	private boolean hasGrid = true;
	/**
	 * 是否显示碰撞框和攻击框
	 */
	private boolean showBox = false;

	public TemplateActorViewer() {
		try {
			jbInit();
			updateLocalization();
			listenerInit();
			setViewScale(Configuration.VIEW_SCALE);
			setOrigin(-160, -160);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化模板角色视图
	 */
	private void jbInit() {
		setDoubleBuffered(false);
		setAutoscrolls(true);
	}

	@Override
	public void updateLocalization() {
	}

	/**
	 * 初始化监听器
	 * <p>
	 * <li>配置改变则重绘模板角色视图</li>
	 * <li>模板帧表选择改变则重新设置帧属性</li>
	 */
	private void listenerInit() {
		Configuration.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}
		});
		TemplateFrame.getModel().addSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						setFrame(TemplateFrame.getModel().getSelection());
					}
				});
	}

	/**
	 * 获取模板角色视图当前帧
	 */
	public AniFrame getFrame() {
		return aniFrame;
	}

	/**
	 * 设置模板角色视图当前帧
	 * 
	 * @param frame
	 */
	public void setFrame(AniFrame frame) {
		if (aniFrame != frame) {
			aniFrame = frame;
			repaint();
		}
	}

	@Override
	public Dimension getOriginSize() {
		return new Dimension(320, 320);
	}

	/**
	 * 设置角色碰撞框和攻击框是否显示
	 * 
	 * @param b
	 */
	public void setShowBox(boolean b) {
		this.showBox = b;
		repaint();
	}

	/**
	 * 设置是否显示网格
	 * 
	 * @param enable
	 */
	public void setEnableGird(boolean enable) {
		this.hasGrid = enable;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(Configuration.color_background);
		g2.fill(g2.getClipBounds());

		g2.setStroke(new BasicStroke(1.0F));

		if (hasGrid) {
			paintGrid(g2);
		}

		Template temp = Template.getModel().getSelection();
		if (temp != null) {
			ImageProvider imageProvider = temp.getAnimation()
					.getImageProvider();
			paintFrame(g2, imageProvider);
			if (showBox) {
				paintCollisionBox(g2);
				paintAttackBox(g2);
			}
		}
		paintAxis(g2);
	}

	/**
	 * 绘制网格
	 * 
	 * @param g2
	 */
	private void paintGrid(Graphics2D g2) {
		int w = 160;
		int h = 160;

		g2.setColor(Configuration.color_grid);
		for (int x = 0; x <= w; x += Configuration.size_grid) {
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-x, -h, -x, h)));
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(x, -h, x, h)));
		}
		for (int y = 0; y <= h; y += Configuration.size_grid) {
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-w, -y, w, -y)));
			g2.draw(getTransform().createTransformedShape(
					new Line2D.Double(-w, y, w, y)));
		}
	}

	/**
	 * 绘制角色当前帧
	 * 
	 * @param g2
	 * @param imageProvider
	 */
	private void paintFrame(Graphics2D g2, ImageProvider imageProvider) {
		int id = 0;
		for (Sprite sprite : aniFrame.getSprites()) {
			boolean xor = Sprite.getModel().getSelectionModel()
					.isSelectedIndex(id);
			if (sprite.module != null) {
				imageProvider.render(g2, sprite.module, sprite, getTransform(),
						xor);
			} else {
				Animation ani = sprite.templateWind.getAnimation();
				ani.getImageProvider().renderTemplateFrame(g2, getTransform(),
						ani.getFrameList().get(sprite.frameIDWind), ani,
						sprite, xor);
			}
			++id;
		}
	}

	/**
	 * 绘制模板角色当前帧所有碰撞框
	 * 
	 * @param g2
	 */
	private void paintCollisionBox(Graphics2D g2) {
		g2.setColor(Configuration.color_collision_box);
		g2.draw(getTransform().createTransformedShape(aniFrame.collisionBox));
	}

	/**
	 * 绘制模板角色当前帧所有攻击框
	 * 
	 * @param g2
	 */
	private void paintAttackBox(Graphics2D g2) {
		g2.setColor(Configuration.color_attack_box);
		g2.draw(getTransform().createTransformedShape(aniFrame.attackBox));
	}

	/**
	 * 绘制坐标轴
	 * 
	 * @param g2
	 */
	private void paintAxis(Graphics2D g2) {
		g2.setColor(Configuration.color_axis);
		g2.draw(getTransform().createTransformedShape(
				new Line2D.Double(-160.0D, 0.0D, 160.0D, 0.0D)));
		g2.draw(getTransform().createTransformedShape(
				new Line2D.Double(0.0D, -160.0D, 0.0D, 160.0D)));
	}
}
