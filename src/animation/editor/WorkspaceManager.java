package animation.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * 编辑器工作台布局管理器
 * 
 * @author 段链
 * 
 * @time 2012-9-14
 * 
 */
public class WorkspaceManager {
	public static final String DEFAULT_LAYOUT = "DefaultLayout";
	public static final String MODULE_LAYOUT = "ModuleLayout";
	public static final String SPRITE_LAYOUT = "SpriteLayout";
	public static final String FRAME_LAYOUT = "FrameLayout";
	public static final String ACTION_LAYOUT = "ActionLayout";

	private String layout;
	private JPanel workspace;

	ModulePane modulePane = new ModulePane();
	ModuleListPane moduleListPane = new ModuleListPane();

	SpritePane spritePane = new SpritePane();
	FramePane framePane = new FramePane();
	SequencePane sequencePane = new SequencePane();
	ActionPane actionPane = new ActionPane();
	MechModelPane mechModelPane = new MechModelPane();

	ActorPane actorPane = new ActorPane();
	TemplateActorPane templateActorPane = new TemplateActorPane();

	TemplatePane templatePane = new TemplatePane();
	TemplateFramePane templateFramePane = new TemplateFramePane();

	public WorkspaceManager(JPanel workspace) {
		this.workspace = workspace;
		workspace.setLayout(new BorderLayout());

		moduleListPane.setPreferredSize(new Dimension(160, 400));
		modulePane.setPreferredSize(new Dimension(640, 480));

		framePane.setPreferredSize(new Dimension(160, 400));
		spritePane.setPreferredSize(new Dimension(160, 400));
		actionPane.setPreferredSize(new Dimension(160, 400));
		sequencePane.setPreferredSize(new Dimension(160, 400));
		mechModelPane.setPreferredSize(new Dimension(160, 200));

		actorPane.setPreferredSize(new Dimension(640, 480));

		templatePane.setPreferredSize(new Dimension(160, 400));
		templateFramePane.setPreferredSize(new Dimension(160, 400));
	}

	public void layout(String layout) {
		this.layout = layout;
		workspace.removeAll();
		if (DEFAULT_LAYOUT.equals(layout)) {
			modulePane.dragButton.setVisible(true);
			modulePane.dragButton.doClick();

			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					actionPane, sequencePane);
			leftSplit.setDividerLocation(400);

			JSplitPane leftSplit2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					spritePane, framePane);
			leftSplit2.setDividerLocation(300);

			JSplitPane leftSplit3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					templatePane, templateFramePane);
			leftSplit3.setDividerLocation(400);

			JSplitPane leftSplit4 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					modulePane, templateActorPane);
			leftSplit4.setDividerLocation(300);

			JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					leftSplit4, actorPane);

			JSplitPane mainSplit2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit3, mainSplit);
			mainSplit2.setDividerLocation(200);

			JSplitPane mainSplit3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit2, mainSplit2);
			mainSplit3.setDividerLocation(200);

			workspace.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit, mainSplit3), BorderLayout.CENTER);
		} else if (MODULE_LAYOUT.equals(layout)) {
			modulePane.dragButton.setVisible(false);
			modulePane.moveModuleButton.doClick();

			workspace.add(modulePane, BorderLayout.CENTER);
			workspace.add(moduleListPane, BorderLayout.EAST);
		} else if (SPRITE_LAYOUT.equals(layout)) {
			modulePane.dragButton.setVisible(true);
			modulePane.dragButton.doClick();
			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					framePane, spritePane);
			leftSplit.setDividerLocation(400);

			workspace.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit, modulePane), BorderLayout.CENTER);
		} else if (FRAME_LAYOUT.equals(layout)) {
			modulePane.dragButton.setVisible(true);
			modulePane.dragButton.doClick();

			JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					modulePane, actorPane);
			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					spritePane, framePane);
			leftSplit.setDividerLocation(400);

			workspace.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit, mainSplit), BorderLayout.CENTER);
		} else if (ACTION_LAYOUT.equals(layout)) {
			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					actionPane, sequencePane);
			leftSplit.setDividerLocation(400);
			JSplitPane leftSplit2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					mechModelPane, framePane);
			leftSplit2.setDividerLocation(180);
			JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit2, actorPane);

			workspace.add(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					leftSplit, mainSplit), BorderLayout.CENTER);
		}
		workspace.revalidate();
		workspace.repaint();
	}

	public String getLayout() {
		return layout;
	}
}
