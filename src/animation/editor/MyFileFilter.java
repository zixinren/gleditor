package animation.editor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;

/**
 * JFileChooser用的文件拓展名过滤器
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class MyFileFilter extends FileFilter {
	/**
	 * [拓展名/过滤器]
	 */
	private HashMap<String, FileFilter> extensionMap;
	/**
	 * 文件描述
	 */
	private String description;
	/**
	 * 完整的过滤器描述: "过滤器描述 (拓展名,... 拓展名)"
	 */
	private String fullDescription;
	/**
	 * 过滤器描述后是否追加拓展名列表
	 */
	private boolean useExtensionsInDescription = true;

	public MyFileFilter() {
		extensionMap = new HashMap<String, FileFilter>();
	}

	public MyFileFilter(String extension) {
		this(extension, null);
	}

	public MyFileFilter(String extension, String description) {
		if (extension != null)
			addExtension(extension);
		if (description != null)
			setDescription(description);
	}

	public MyFileFilter(String[] filters) {
		this(filters, null);
	}

	public MyFileFilter(String[] filters, String description) {
		if (filters != null)
			for (String filter : filters) {
				addExtension(filter);
			}

		if (description != null)
			setDescription(description);
	}

	@Override
	public boolean accept(File f) {
		if (f != null) {
			if (f.isDirectory())
				return true;
			String extension = getExtension(f);
			if (extension != null && extensionMap.containsKey(extension))
				return true;
		}
		return false;
	}

	/**
	 * 添加拓展名, 都会转换为小写
	 * 
	 * @param extension
	 */
	public void addExtension(String extension) {
		if (extensionMap == null) {
			extensionMap = new HashMap<String, FileFilter>();
		}
		extensionMap.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	/**
	 * 获取文件的拓展名
	 * 
	 * @param f
	 * @return 拓展名
	 */
	public String getExtension(File f) {
		if (f != null) {
			String filename = f.getName();
			int index = filename.lastIndexOf(".");
			if (index > 0 && index < filename.length() - 1)
				return filename.substring(index + 1);
		}

		return null;
	}

	/**
	 * 设置过滤器描述
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	@Override
	public String getDescription() {
		if (fullDescription == null) {
			if (description == null || isExtensionListInDescription()) {
				StringBuilder sb = new StringBuilder();
				sb.append(description == null ? "" : description).append("(");
				Iterator<String> iter = extensionMap.keySet().iterator();
				if (iter.hasNext()) {
					sb.append(".").append(iter.next());
					while (iter.hasNext()) {
						sb.append(", .").append(iter.next());
					}
				}
				sb.append(")");
				fullDescription = sb.toString();
			} else {
				fullDescription = description;
			}
		}

		return fullDescription == null ? "" : fullDescription;
	}

	/**
	 * 设置是否把扩展名列表加入过滤器描述中
	 * 
	 * @param b
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * 是否把扩展名列表加入过滤器描述中
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}
}
