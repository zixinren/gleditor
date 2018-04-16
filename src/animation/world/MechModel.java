package animation.world;

import static animation.world.Util.getInt;

import org.jdom2.Element;

/**
 * 运动模型
 * 
 * @author 段链
 * @time 2012-9-16
 */
public class MechModel implements Cloneable {
	public static final int SPEED_X_MASK = 1;
	public static final int SPEED_Y_MASK = 2;
	public static final int ACCELERATION_X_MASK = 4;
	public static final int ACCELERATION_Y_MASK = 8;

	public boolean hasSpeedX;
	public boolean hasSpeedY;
	public boolean hasAccelerationX;
	public boolean hasAccelerationY;

	public double speedX;
	public double speedY;
	public double accelerationX;
	public double accelerationY;

	public double adjust(double d) {
		d = (int) (d * 256.0D);
		return d / 256.0D;
	}

	/**
	 * 获取运动模型标记
	 */
	public int getFlag() {
		return (hasSpeedX ? SPEED_X_MASK : 0) + (hasSpeedY ? SPEED_Y_MASK : 0)
				+ (hasAccelerationX ? ACCELERATION_X_MASK : 0)
				+ (hasAccelerationY ? ACCELERATION_Y_MASK : 0);
	}

	/**
	 * 设置运动模型标记
	 * 
	 * @param flag
	 */
	public void setFlag(int flag) {
		hasSpeedX = (flag & SPEED_X_MASK) != 0;
		hasSpeedY = (flag & SPEED_Y_MASK) != 0;
		hasAccelerationX = (flag & ACCELERATION_X_MASK) != 0;
		hasAccelerationY = (flag & ACCELERATION_Y_MASK) != 0;
	}

	@Override
	public Object clone() {
		MechModel mechModel = new MechModel();

		mechModel.hasSpeedX = hasSpeedX;
		mechModel.hasSpeedY = hasSpeedY;
		mechModel.hasAccelerationX = hasAccelerationX;
		mechModel.hasAccelerationY = hasAccelerationY;
		mechModel.speedX = speedX;
		mechModel.speedY = speedY;
		mechModel.accelerationX = accelerationX;
		mechModel.accelerationY = accelerationY;

		return mechModel;
	}

	/**
	 * 从XML文件载入动作的运动模型
	 * 
	 * @param action
	 */
	static MechModel loadMechModelFromXml(Element action) {
		MechModel mechModel = new MechModel();

		Element e = action.getChild("MechModel");

		mechModel.setFlag(getInt(e, "flag"));
		mechModel.speedX = stringToDouble(e.getAttributeValue("vx"));
		mechModel.speedY = stringToDouble(e.getAttributeValue("vy"));
		mechModel.accelerationX = stringToDouble(e.getAttributeValue("ax"));
		mechModel.accelerationY = stringToDouble(e.getAttributeValue("ay"));

		return mechModel;
	}

	/**
	 * 把力学模型转换成XML数据
	 * 
	 * @param mechModel
	 */
	static Element saveMechModelToXml(MechModel mechModel) {
		Element result = new Element("MechModel");

		result.setAttribute("flag", Integer.toString(mechModel.getFlag()));
		result.setAttribute("vx", doubleToString(mechModel.speedX));
		result.setAttribute("vy", doubleToString(mechModel.speedY));
		result.setAttribute("ax", doubleToString(mechModel.accelerationX));
		result.setAttribute("ay", doubleToString(mechModel.accelerationY));

		return result;
	}

	private static double stringToDouble(String value) {
		double d = Integer.parseInt(value);
		return d / 256.0D;
	}

	private static String doubleToString(double d) {
		int n = (int) (d * 256.0D);
		return Integer.toString(n);
	}
}
