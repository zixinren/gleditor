package animation.editor.modulesmap;

import java.util.ArrayList;

/**
 * 模块映射数据
 * 
 * @author 段链
 * @time 2012-9-19
 */
public class ModulesMapData {
	private String modulesMapName;
	private ArrayList<int[]> modulesMapData;

	public ModulesMapData() {
		modulesMapData = new ArrayList<int[]>();
	}

	/**
	 * 设置模块映射名称
	 * 
	 * @param mapName
	 */
	public void setMapName(String mapName) {
		modulesMapName = mapName;
	}

	/**
	 * 获取模块映射名称
	 */
	public String getMapName() {
		return modulesMapName;
	}

	/**
	 * 设置模块映射数据
	 * 
	 * @param data
	 */
	public void setMapData(ArrayList<int[]> data) {
		modulesMapData = new ArrayList<int[]>(data);
	}

	/**
	 * 获取模块映射数据
	 */
	public ArrayList<int[]> getMapData() {
		return modulesMapData;
	}

	/**
	 * 添加模块映射数据
	 * 
	 * @param src
	 * @param des
	 */
	public void addModulesMapData(int src, int des) {
		addModulesMapData(new int[] { src, des });
	}

	/**
	 * 添加模块映射数据
	 * 
	 * @param mapData
	 */
	public void addModulesMapData(int[] mapData) {
		modulesMapData.add(mapData);
	}

	/**
	 * 获取模块映射数据, 这个是副本而不是本体
	 * 
	 * @param index
	 */
	public int[] getModulesMapData(int index) {
		int[] src = modulesMapData.get(index);
		int[] des = new int[src.length];
		System.arraycopy(src, 0, des, 0, src.length);

		return des;
	}

	/**
	 * 获取模块映射的原始索引
	 * 
	 * @param index
	 */
	public int getModulesMapSrcData(int index) {
		return modulesMapData.get(index)[0];
	}

	/**
	 * 获取模块映射的目标索引
	 * 
	 * @param index
	 */
	public int getModulesMapDesData(int index) {
		return modulesMapData.get(index)[1];
	}

	/**
	 * 设置模块映射数据
	 * 
	 * @param data
	 * @param index
	 */
	public void setModulesMapData(int[] data, int index) {
		modulesMapData.set(index, data);
	}

	/**
	 * 移除模块映射数据
	 * 
	 * @param index
	 */
	public void removeMapData(int index) {
		modulesMapData.remove(index);
	}

	/**
	 * 获取模块映射数据个数
	 */
	public int getModulesMapSize() {
		return modulesMapData != null ? modulesMapData.size() : 0;
	}
}
