package animation.editor;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * ±‡º≠∆˜≈‰÷√
 * 
 * @author ∂Œ¡¥
 * 
 * @time 2012-9-14
 * 
 */
public class Configuration {
	public static final String ENGLISH_RESOURCE_BUNDLE_NAME = "animation.resource.EnglishEditorResourceBundle";
	public static final String CHINESE_RESOURCE_BUNDLE_NAME = "animation.resource.ChineseEditorResourceBundle";
	public static final String CFG_NAME = "AnimationEditor.cfg";

	public static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(ENGLISH_RESOURCE_BUNDLE_NAME);
	public static String default_filePath = ".";
	public static Color color_background = Color.LIGHT_GRAY;
	public static Color color_module_border = Color.ORANGE;
	public static Color color_selected_border = Color.GREEN;

	public static Color color_axis = Color.BLUE;
	public static Color color_grid = Color.ORANGE;
	public static Color color_attack_box = Color.RED;
	public static Color color_collision_box = Color.GREEN;

	public static Color color_viewer_border = Color.DARK_GRAY;

	public static int size_grid = 8;
	/**
	 * ∂Øª≠—≠ª∑µ•¥Œ ±º‰, µ•Œª:∫¡√Î
	 */
	public static int tick = 100;

	public static double EXPAND_SCALE = 1.5D;
	public static double VIEW_SCALE = 2.0D;
	static final int ACTOR_VIEW_WIDTH = 320;
	static final int ACTOR_VIEW_HEIGHT = 320;

	private static PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			new Configuration());

	private static void setPropertyColor(Properties prop, String key,
			Color value) {
		prop.setProperty(key, Integer.toString(value.getRGB()));
	}

	private static void setPropertyInt(Properties prop, String key, int value) {
		prop.setProperty(key, Integer.toString(value));
	}

	private static Color getPropertyColor(Properties prop, String key,
			Color defaultValue) {
		String colorStr = prop.getProperty(key,
				Integer.toString(defaultValue.getRGB()));
		try {
			int colorInt = Integer.parseInt(colorStr);
			return new Color(colorInt);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return defaultValue;
	}

	private static int getPropertyInt(Properties prop, String key,
			int defaultValue) {
		String intStr = prop.getProperty(key, Integer.toString(defaultValue));
		try {
			return Integer.parseInt(intStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return defaultValue;
	}

	/**
	 * ‘ÿ»Î±‡º≠∆˜≈‰÷√
	 */
	public static void load() {
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(CFG_NAME);
			prop.load(fis);
			fis.close();
		} catch (FileNotFoundException ex) {
			return;
		} catch (IOException e) {
			e.printStackTrace();
		}
		resourceBundle = ResourceBundle.getBundle(prop.getProperty(
				"resourceBundle", resourceBundle.getClass().getName()));
		default_filePath = prop.getProperty("default_filePath",
				default_filePath);
		color_background = getPropertyColor(prop, "color_background",
				color_background);
		color_module_border = getPropertyColor(prop, "color_module_border",
				color_module_border);
		color_selected_border = getPropertyColor(prop, "color_selected_border",
				color_selected_border);
		color_axis = getPropertyColor(prop, "color_axis", color_axis);
		color_grid = getPropertyColor(prop, "color_grid", color_grid);
		color_attack_box = getPropertyColor(prop, "color_attack_box",
				color_attack_box);
		color_collision_box = getPropertyColor(prop, "color_collision_box",
				color_collision_box);
		size_grid = getPropertyInt(prop, "size_grid", size_grid);
		tick = getPropertyInt(prop, "tick", tick);
	}

	/**
	 * ±£¥Ê±‡º≠∆˜≈‰÷√
	 */
	public static void save() {
		Properties prop = new Properties();
		prop.setProperty("resourceBundle", resourceBundle.getClass().getName());
		prop.setProperty("default_filePath", default_filePath);
		setPropertyColor(prop, "color_background", color_background);
		setPropertyColor(prop, "color_module_border", color_module_border);
		setPropertyColor(prop, "color_selected_border", color_selected_border);
		setPropertyColor(prop, "color_axis", color_axis);
		setPropertyColor(prop, "color_grid", color_grid);
		setPropertyColor(prop, "color_attack_box", color_attack_box);
		setPropertyColor(prop, "color_collision_box", color_collision_box);
		setPropertyInt(prop, "size_grid", size_grid);
		setPropertyInt(prop, "tick", tick);
		try {
			FileOutputStream fos = new FileOutputStream(CFG_NAME);
			prop.store(fos, "Animation Editor Configuration File");
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public static void removePropertyChangeListener(
			PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public static void firePropertyChange() {
		changeSupport.firePropertyChange("property", false, true);
	}
}
