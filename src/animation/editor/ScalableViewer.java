package animation.editor;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import animation.editor.tool.AbstractTool;
import animation.world.Animation;

/**
 * 可缩放视图
 * 
 * @author 段链
 * @time 2012-9-15
 */
public abstract class ScalableViewer extends JPanel implements Scrollable {
	private static final long serialVersionUID = 5635745204638385201L;

	/**
	 * 缩放因子
	 */
	protected double viewScale = 1.0D;
	/**
	 * 工具
	 */
	private AbstractTool tool;
	/**
	 * 上一次鼠标拖拽的位置
	 */
	protected Point lastMousePoint;
	/**
	 * 组件宽度
	 */
	protected int componentWidth;
	/**
	 * 组件高度
	 */
	protected int componentHeight;
	/**
	 * 位置标签
	 */
	private JLabel locationLabel;
	/**
	 * 位置格式
	 */
	private MessageFormat locationFormat = new MessageFormat(
			"Point: [{0,number,###}, {1,number,###}]");
	/**
	 * 视图坐标系的原点在组件坐标系下的X轴位置
	 */
	private int originX;
	/**
	 * 视图坐标系的原点在组件坐标系下的Y轴位置
	 */
	private int originY;

	/**
	 * 构建可缩放视图, 注册鼠标,按键监听器, 由视图当前设置的编辑工具代理处理这一系列事件
	 */
	public ScalableViewer() {
		addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				translateMousePoint(e);
				if (getTool() != null) {
					getTool().mouseReleased(e);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				translateMousePoint(e);
				AbstractTool tool2 = getTool();
				if (tool2 != null) {
					tool2.mousePressed(e);
				}
				lastMousePoint = e.getPoint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				translateMousePoint(e);
				if (getTool() != null) {
					getTool().mouseExited(e);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				translateMousePoint(e);
				if (getTool() != null) {
					getTool().mouseEntered(e);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				translateMousePoint(e);
				if (getTool() != null) {
					getTool().mouseClicked(e);
				}
			}
		});
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				translateMousePoint(e);
				if (getTool() != null) {
					getTool().mouseMoved(e);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
				scrollRectToVisible(r);

				translateMousePoint(e);
				if (e.getPoint().distance(lastMousePoint) == 0.0D)
					return;
				if (getTool() != null) {
					getTool().mouseDragged(e);
				}
				lastMousePoint = e.getPoint();
			}
		});
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (getTool() != null) {
					getTool().keyTyped(e);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (getTool() != null) {
					getTool().keyReleased(e);
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (getTool() != null) {
					getTool().keyPressed(e);
				}
			}
		});
	}

	/**
	 * 获取视图的原始大小
	 */
	public abstract Dimension getOriginSize();

	/**
	 * 获取视图缩放因子
	 */
	public double getViewScale() {
		return viewScale;
	}

	/**
	 * 设置视图缩放因子
	 * 
	 * @param scale
	 */
	public void setViewScale(double scale) {
		this.viewScale = scale;
		revalidate();
	}

	/**
	 * 获取视图对应的仿射变换
	 */
	public AffineTransform getTransform() {
		AffineTransform result = new AffineTransform();
		Insets insets = getInsets();
		result.translate(insets.left, insets.top);
		result.scale(viewScale, viewScale);
		result.translate(originX, originY);

		return result;
	}

	/**
	 * 设置组件坐标系原点在视图坐标系下的位置
	 * 
	 * @param x
	 * @param y
	 */
	public void setOrigin(int x, int y) {
		originX = -x;
		originY = -y;
		revalidate();
	}

	/**
	 * 获取视图坐标系原点X轴位置
	 */
	public int getXOrigin() {
		return originX;
	}

	/**
	 * 获取视图坐标系原点Y轴位置
	 */
	public int getYOrigin() {
		return originY;
	}

	/**
	 * 获取编辑视图的工具
	 */
	public AbstractTool getTool() {
		return Animation.instance().isReadOnly() ? null : tool;
	}

	/**
	 * 设置编辑视图的工具
	 * 
	 * @param aTool
	 */
	public void setTool(AbstractTool aTool) {
		if (tool != null) {
			tool.endSession();
		}
		tool = aTool;
		tool.beginSession();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		Insets insets = getInsets();
		Dimension originSize = getOriginSize();
		int w = (int) (originSize.width * viewScale);
		int h = (int) (originSize.height * viewScale);
		componentWidth = w + insets.left + insets.right;
		componentHeight = h + insets.top + insets.bottom;
		setPreferredSize(new Dimension(componentWidth, componentHeight));
		super.setBounds(x, y, componentWidth, componentHeight);
	}

	/**
	 * 放大视图
	 */
	public void zoomIn() {
		if (viewScale < 8.0D) {
			viewScale += 1.0D;
			zoomViewer();
		}
	}

	/**
	 * 缩小视图
	 */
	public void zoomOut() {
		if (viewScale > 1.0D) {
			viewScale -= 1.0D;
			zoomViewer();
		}
	}

	/**
	 * 缩放视图
	 */
	protected void zoomViewer() {
		Insets insets = getInsets();
		Dimension originSize = getOriginSize();
		int w = (int) (originSize.width * viewScale);
		int h = (int) (originSize.height * viewScale);
		componentWidth = w + insets.left + insets.right;
		componentHeight = h + insets.top + insets.bottom;
		setPreferredSize(new Dimension(componentWidth, componentHeight));
		revalidate();
		repaint();
	}

	/**
	 * 获取视图上鼠标位置的标签
	 */
	public JLabel getLocationLabel() {
		if (locationLabel == null) {
			locationLabel = new JLabel();
			locationLabel.setPreferredSize(new Dimension(100, 24));
			locationLabel.setVerticalAlignment(JLabel.CENTER);
			locationLabel.setHorizontalAlignment(JLabel.LEFT);
			locationLabel.setText("");
		}

		return locationLabel;
	}

	/**
	 * 把鼠标位置映射到原始视图坐标系中
	 * 
	 * @param e
	 */
	protected void translateMousePoint(MouseEvent e) {
		Point point = transformCoordinate(e.getPoint());
		e.translatePoint(point.x - e.getX(), point.y - e.getY());
		getLocationLabel().setText(
				locationFormat.format(new Integer[] { point.x, point.y }));
	}

	/**
	 * 把点映射到原始视图坐标系中
	 * 
	 * @param p
	 */
	public Point transformCoordinate(Point p) {
		Point result = new Point();
		try {
			getTransform().inverseTransform(p, result);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 把矩形映射到当前视图坐标系中
	 * 
	 * @param r
	 */
	public Rectangle transformCoordinate(Rectangle r) {
		return getTransform().createTransformedShape(r).getBounds();
	}

	public Dimension getPreferredScrollableViewportSize() {
		return null;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 16;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 64;
	}

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
}
