/*
 *
 */

package com.rapidminer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.EventListenerList;

import com.rapid_i.deployment.update.client.ExtensionDialog;
import com.rapid_i.deployment.update.client.UpdateDialog;
import com.rapidminer.BreakpointListener;
import com.rapidminer.Process;
import com.rapidminer.ProcessLocation;
import com.rapidminer.ProcessStorageListener;
import com.rapidminer.RapidMiner;
import com.rapidminer.gui.actions.AboutAction;
import com.rapidminer.gui.actions.Actions;
import com.rapidminer.gui.actions.AnovaCalculatorAction;
import com.rapidminer.gui.actions.AutoWireAction;
import com.rapidminer.gui.actions.BrowseAction;
import com.rapidminer.gui.actions.CheckForJDBCDriversAction;
import com.rapidminer.gui.actions.ExitAction;
import com.rapidminer.gui.actions.ExportProcessAction;
import com.rapidminer.gui.actions.ExportViewAction;
import com.rapidminer.gui.actions.ImportProcessAction;
import com.rapidminer.gui.actions.ManageBuildingBlocksAction;
import com.rapidminer.gui.actions.ManageTemplatesAction;
import com.rapidminer.gui.actions.NewAction;
import com.rapidminer.gui.actions.NewPerspectiveAction;
import com.rapidminer.gui.actions.OpenAction;
import com.rapidminer.gui.actions.PageSetupAction;
import com.rapidminer.gui.actions.PauseAction;
import com.rapidminer.gui.actions.PrintAction;
import com.rapidminer.gui.actions.PrintPreviewAction;
import com.rapidminer.gui.actions.PropagateRealMetaDataAction;
import com.rapidminer.gui.actions.RedoAction;
import com.rapidminer.gui.actions.RunAction;
import com.rapidminer.gui.actions.RunRemoteAction;
import com.rapidminer.gui.actions.RunRemoteNowAction;
import com.rapidminer.gui.actions.SaveAction;
import com.rapidminer.gui.actions.SaveAsAction;
import com.rapidminer.gui.actions.SaveAsTemplateAction;
import com.rapidminer.gui.actions.SettingsAction;
import com.rapidminer.gui.actions.StopAction;
import com.rapidminer.gui.actions.ToggleAction;
import com.rapidminer.gui.actions.ToggleExpertModeAction;
import com.rapidminer.gui.actions.TourAction;
import com.rapidminer.gui.actions.TutorialAction;
import com.rapidminer.gui.actions.UndoAction;
import com.rapidminer.gui.actions.ValidateAutomaticallyAction;
import com.rapidminer.gui.actions.ValidateProcessAction;
import com.rapidminer.gui.actions.WizardAction;
import com.rapidminer.gui.dialog.ProcessInfoScreen;
import com.rapidminer.gui.dialog.Tutorial;
import com.rapidminer.gui.dialog.UnknownParametersInfoDialog;
import com.rapidminer.gui.docking.RapidDockingToolbar;
import com.rapidminer.gui.flow.ErrorTable;
import com.rapidminer.gui.flow.ProcessPanel;
import com.rapidminer.gui.flow.ProcessUndoManager;
import com.rapidminer.gui.operatortree.OperatorTree;
import com.rapidminer.gui.operatortree.OperatorTreePanel;
import com.rapidminer.gui.operatortree.actions.CutCopyPasteDeleteAction;
import com.rapidminer.gui.operatortree.actions.ToggleBreakpointItem;
import com.rapidminer.gui.plotter.PlotterPanel;
import com.rapidminer.gui.processeditor.CommentEditor;
import com.rapidminer.gui.processeditor.NewOperatorEditor;
import com.rapidminer.gui.processeditor.ProcessContextProcessEditor;
import com.rapidminer.gui.processeditor.ProcessEditor;
import com.rapidminer.gui.processeditor.XMLEditor;
import com.rapidminer.gui.processeditor.results.ResultDisplay;
import com.rapidminer.gui.processeditor.results.ResultDisplayTools;
import com.rapidminer.gui.properties.OperatorPropertyPanel;
import com.rapidminer.gui.templates.SaveAsTemplateDialog;
import com.rapidminer.gui.tools.LoggingViewer;
import com.rapidminer.gui.tools.ResourceAction;
import com.rapidminer.gui.tools.ResourceMenu;
import com.rapidminer.gui.tools.StatusBar;
import com.rapidminer.gui.tools.SwingTools;
import com.rapidminer.gui.tools.SystemMonitor;
import com.rapidminer.gui.tools.WelcomeScreen;
import com.rapidminer.gui.tools.dialogs.ConfirmDialog;
import com.rapidminer.gui.tools.dialogs.DecisionRememberingConfirmDialog;
import com.rapidminer.gui.tools.dialogs.ManageDatabaseConnectionsDialog;
import com.rapidminer.gui.tools.dialogs.ManageDatabaseDriversDialog;
import com.rapidminer.gui.tools.dialogs.wizards.dataimport.BlobImportWizard;
import com.rapidminer.gui.tools.dialogs.wizards.dataimport.DatabaseImportWizard;
import com.rapidminer.gui.tools.dialogs.wizards.dataimport.access.AccessImportWizard;
import com.rapidminer.operator.DebugMode;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorChain;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.UnknownParameterInformation;
import com.rapidminer.operator.nio.CSVImportWizard;
import com.rapidminer.operator.nio.ExcelImportWizard;
import com.rapidminer.operator.nio.xml.XMLImportWizard;
import com.rapidminer.operator.ports.metadata.CompatibilityLevel;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeColor;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.repository.RepositoryException;
import com.rapidminer.repository.gui.RepositoryBrowser;
import com.rapidminer.repository.gui.process.RemoteProcessViewer;
import com.rapidminer.repository.remote.RemoteRepository;
import com.rapidminer.tools.LogService;
import com.rapidminer.tools.Observable;
import com.rapidminer.tools.Observer;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.plugin.Plugin;
import com.rapidminer.tools.usagestats.UsageStatsTransmissionDialog;
import com.vlsolutions.swing.docking.DockGroup;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockingContext;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.toolbars.ToolBarConstraints;
import com.vlsolutions.swing.toolbars.ToolBarContainer;
import com.vlsolutions.swing.toolbars.ToolBarPanel;

/**
 * The abstract base class implementation of the {@link MainUIState} interface.
 * 
 * Created with refactor from {@link MainFrame}.
 * 
 * @author Gabor Bakos
 */
public abstract class AbstractUIState implements MainUIState, ProcessEndHandler {

	/**
	 * The property name for &quot;Maximum number of states in the undo
	 * list.&quot;
	 */
	public static final String PROPERTY_RAPIDMINER_GUI_UNDOLIST_SIZE = "rapidminer.gui.undolist.size";

	/** Updates the list of recently used files. */
	public abstract void updateRecentFileList();

	/**
	 * Sets the window title (RapidMiner + filename + an asterisk if process was
	 * modified.
	 */
	protected abstract void setTitle();

	// public abstract void processChanged();

	// public abstract void setExperiment(Process process);

	// public abstract Process getExperiment();

	// public abstract void changeMode(int mode);

	/**
	 * The property name for &quot;Shows process info screen after
	 * loading?&quot;
	 */
	public static final String PROPERTY_RAPIDMINER_GUI_PROCESSINFO_SHOW = "rapidminer.gui.processinfo.show";
	public static final String PROPERTY_RAPIDMINER_GUI_SAVE_BEFORE_RUN = "rapidminer.gui.save_before_run";
	public static final String PROPERTY_RAPIDMINER_GUI_SAVE_ON_PROCESS_CREATION = "rapidminer.gui.save_on_process_creation";
	/** The title of the frame. */
	public static final String TITLE = "RapidMiner";
	public static final int EDIT_MODE = 0;
	public static final int RESULTS_MODE = 1;
	public static final int WELCOME_MODE = 2;
	public final transient Action AUTO_WIRE = new AutoWireAction(this, "wire",
			CompatibilityLevel.PRE_VERSION_5, false, true);
	public final transient Action AUTO_WIRE_RECURSIVELY = new AutoWireAction(
			this, "wire_recursive", CompatibilityLevel.PRE_VERSION_5, true,
			true);
	public final transient Action REWIRE = new AutoWireAction(this, "rewire",
			CompatibilityLevel.PRE_VERSION_5, false, false);
	public final transient Action REWIRE_RECURSIVELY = new AutoWireAction(this,
			"rewire_recursive", CompatibilityLevel.PRE_VERSION_5, true, false);
	public final transient Action NEW_ACTION = new NewAction(this);
	public final transient Action OPEN_ACTION = new OpenAction();
	public final transient SaveAction SAVE_ACTION = new SaveAction();
	public final transient Action SAVE_AS_ACTION = new SaveAsAction();
	public final transient Action SAVE_AS_TEMPLATE_ACTION = new SaveAsTemplateAction(
			this);
	public final transient Action MANAGE_TEMPLATES_ACTION = new ManageTemplatesAction();
	public final transient Action MANAGE_BUILDING_BLOCKS_ACTION = new ManageBuildingBlocksAction(
			this);
	public final transient Action PRINT_ACTION = new PrintAction(
			this.getWindow(), "all");
	public final transient Action PRINT_PREVIEW_ACTION = new PrintPreviewAction(
			this.getWindow(), "all");
	public final transient Action PAGE_SETUP_ACTION = new PageSetupAction();
	public final transient Action PROPAGATE_REAL_METADATA_ACTION = new PropagateRealMetaDataAction(this);
	public final transient Action IMPORT_CSV_FILE_ACTION = new ResourceAction(
			"import_csv_file") {

		private static final long serialVersionUID = 4632580631996166900L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			// CSVImportWizard wizard = new CSVImportWizard("import_csv_file");
			CSVImportWizard wizard;
			try {
				wizard = new CSVImportWizard();
				wizard.setVisible(true);
			} catch (final OperatorException e1) {
				// should not happen if operator == null
				throw new RuntimeException("Failed to create wizard.", e1);
			}
		}
	};
	public final transient Action IMPORT_EXCEL_FILE_ACTION = new ResourceAction(
			"import_excel_sheet") {

		private static final long serialVersionUID = 975782163819088729L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				final ExcelImportWizard wizard = new ExcelImportWizard();
				wizard.setVisible(true);
			} catch (final OperatorException e1) {
				// should not happen if operator == null
				throw new RuntimeException("Failed to create wizard.", e1);
			}
		}
	};
	public final transient Action IMPORT_XML_FILE_ACTION = new ResourceAction(
			"import_xml_file") {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				final XMLImportWizard wizard = new XMLImportWizard();
				wizard.setVisible(true);
			} catch (final OperatorException e1) {
				// should not happen if operator == null
				throw new RuntimeException("Failed to create wizard.", e1);
			}
		}
	};
	public final transient Action IMPORT_ACCESS_FILE_ACTION = new ResourceAction(
			"import_access_table") {

		private static final long serialVersionUID = 3725652002686421768L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			AccessImportWizard wizard;
			try {
				wizard = new AccessImportWizard("import_access_table");
				wizard.setVisible(true);
			} catch (final SQLException e1) {
				SwingTools.showSimpleErrorMessage(
						"db_connection_failed_simple", e1, e1.getMessage());
			}
		}
	};
	public final transient Action IMPORT_DATABASE_TABLE_ACTION = new ResourceAction(
			"import_database_table") {

		private static final long serialVersionUID = 3725652002686421768L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			DatabaseImportWizard wizard;
			try {
				wizard = new DatabaseImportWizard("import_database_table");
				wizard.setVisible(true);
			} catch (final SQLException e1) {
				SwingTools.showSimpleErrorMessage(
						"db_connection_failed_simple", e1, e1.getMessage());
			}
		}
	};
	public final transient Action IMPORT_PROCESS_ACTION = new ImportProcessAction();
	public final transient Action EXPORT_PROCESS_ACTION = new ExportProcessAction();
	public final transient Action EXPORT_ACTION = new ExportViewAction(
			this.getWindow(), "all");
	public final transient Action EXIT_ACTION = new ExitAction(this);
	public final transient RunRemoteNowAction RUN_REMOTE_NOW_ACTION = new RunRemoteNowAction(this);
	public final transient RunAction RUN_ACTION = new RunAction(this);
	public final transient Action PAUSE_ACTION = new PauseAction(this);
	public final transient Action STOP_ACTION = new StopAction(this);
	public final transient Action RUN_REMOTE_ACTION = new RunRemoteAction();
	public final transient Action VALIDATE_ACTION = new ValidateProcessAction(
			this);
	public final transient ToggleAction VALIDATE_AUTOMATICALLY_ACTION = new ValidateAutomaticallyAction();
	public final transient Action OPEN_TEMPLATE_ACTION = new WizardAction(this);
	public final transient Action NEW_PERSPECTIVE_ACTION = new NewPerspectiveAction(
			this);
	public final transient Action SETTINGS_ACTION = new SettingsAction();
	public final transient ToggleAction TOGGLE_EXPERT_MODE_ACTION = new ToggleExpertModeAction(
			this);
	public final transient Action TUTORIAL_ACTION = new TutorialAction(this);
	public final transient Action TOUR_ACTION = new TourAction();
	public final transient Action UNDO_ACTION = new UndoAction(this);
	public final transient Action REDO_ACTION = new RedoAction(this);
	public final transient Action ANOVA_CALCULATOR_ACTION = new AnovaCalculatorAction();
	public final transient Action CHECK_FOR_JDBC_DRIVERS_ACTION = new CheckForJDBCDriversAction();
	public final transient Action MANAGE_DB_CONNECTIONS_ACTION = new ResourceAction(
			true, "manage_db_connections") {

		private static final long serialVersionUID = 2457587046500212869L;

		@Override
		public void actionPerformed(final ActionEvent e) {
			final ManageDatabaseConnectionsDialog dialog = new ManageDatabaseConnectionsDialog();
			dialog.setVisible(true);
		}
	};
	
	public static final DockGroup DOCK_GROUP_ROOT = new DockGroup("root");
	public static final DockGroup DOCK_GROUP_RESULTS = new DockGroup("results");
	protected final DockingContext dockingContext = new DockingContext();
	protected final DockingDesktop dockingDesktop = new DockingDesktop(
			"mainDesktop", dockingContext);
	protected final Actions actions = new Actions(this);
	protected final WelcomeScreen welcomeScreen = new WelcomeScreen(this);
	protected final ResultDisplay resultDisplay = ResultDisplayTools
			.makeResultDisplay();
	protected final LoggingViewer messageViewer = new LoggingViewer();
	protected final SystemMonitor systemMonitor = new SystemMonitor();

	private final EventListenerList processEditors = new EventListenerList();
	private List<Operator> selectedOperators = Collections.emptyList();

	protected final OperatorDocViewer operatorDocViewer = OperatorDocViewer
			.instantiate();
	protected final OperatorTreePanel operatorTree = new OperatorTreePanel(this);
	protected final ErrorTable errorTable = new ErrorTable(this);
	protected final OperatorPropertyPanel propertyPanel = new OperatorPropertyPanel(
			this);
	protected final XMLEditor xmlEditor = new XMLEditor(this);
	protected final CommentEditor commentEditor = new CommentEditor();
	protected final ProcessContextProcessEditor processContextEditor = new ProcessContextProcessEditor();
	protected final ProcessPanel processPanel = new ProcessPanel(this);
	protected final NewOperatorEditor newOperatorEditor = new NewOperatorEditor(processPanel.getProcessRenderer());
	protected final RepositoryBrowser repositoryBrowser = new RepositoryBrowser(processPanel.getProcessRenderer());
	protected final RemoteProcessViewer remoteProcessViewer = new RemoteProcessViewer();
	protected final Perspectives perspectives = new Perspectives(dockingContext);
	protected boolean changed = false;
	protected boolean tutorialMode = false;
	private int undoIndex;
	protected final JMenuBar menuBar;
	protected final JMenu fileMenu;
	protected final JMenu editMenu;
	protected final JMenu processMenu;
	protected final JMenu toolsMenu;
	protected final JMenu viewMenu;
	protected final JMenu helpMenu;
	
	private DockableMenu dockableMenu;
	
	protected final JMenu recentFilesMenu = new ResourceMenu("recent_files");

	private final ProcessUndoManager undoManager = new ProcessUndoManager();

	private final LinkedList<ProcessStorageListener> storageListeners = new LinkedList<ProcessStorageListener>();

	/** XML representation of the process at last validation. */
	private String lastProcessXML;

	/** the OperatorChain which was last viewed */
	private OperatorChain lastProcessDisplayedOperatorChain;

	/**
	 * The host name of the system. Might be empty (no host name will be shown)
	 * and will be initialized in the first call of {@link #setTitle()}.
	 */
	protected String hostname = null;
	protected transient Process process = null;
	protected transient ProcessThread processThread;
	protected final MetaDataUpdateQueue metaDataUpdateQueue = new MetaDataUpdateQueue(
			this);
	private long lastUpdate = 0;
	private final Timer updateTimer = new Timer(500, new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			updateProcessNow();
		}
	}) {

		private static final long serialVersionUID = 1L;
		{
			setRepeats(false);
		}
	};

	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#addViewSwitchToUndo()
	 */
	@Override
	public void addViewSwitchToUndo() {
		String xmlWithoutGUIInformation = process.getRootOperator().getXML(true, false);
		if (lastProcessDisplayedOperatorChain != null && processPanel.getProcessRenderer().getDisplayedChain() != null &&
				!processPanel.getProcessRenderer().getDisplayedChain().getName().equals(lastProcessDisplayedOperatorChain.getName())) {
			addToUndoList(xmlWithoutGUIInformation, true);
		}
		lastProcessXML = xmlWithoutGUIInformation;
		lastProcessDisplayedOperatorChain = processPanel.getProcessRenderer().getDisplayedChain();
	}

	private void updateProcessNow() {
		lastUpdate = System.currentTimeMillis();
		String xmlWithoutGUIInformation = process.getRootOperator().getXML(true, false);
		if (!xmlWithoutGUIInformation.equals(lastProcessXML)) {
			addToUndoList(xmlWithoutGUIInformation, false);
			validateProcess(false);
		}
		processPanel.getProcessRenderer().repaint();
		lastProcessXML = xmlWithoutGUIInformation;
		lastProcessDisplayedOperatorChain = processPanel.getProcessRenderer().getDisplayedChain();
	}

	@Override
	public void validateProcess(final boolean force) {
		if (force || process.getProcessState() != Process.PROCESS_STATE_RUNNING) {
			metaDataUpdateQueue.validate(process, force);
		}
		fireProcessUpdated();
	}

	public boolean isProcessRendererFocused() {
		return processPanel.getProcessRenderer().hasFocus();
	}

	private transient final Observer<Process> processObserver = new Observer<Process>() {

		@Override
		public void update(Observable<Process> observable, Process arg) {
			// if (process.getProcessState() == Process.PROCESS_STATE_RUNNING) {
			// return;
			// }
			if (System.currentTimeMillis() - lastUpdate > 500) {
				updateProcessNow();
			} else {
				if (process.getProcessState() == Process.PROCESS_STATE_RUNNING) {
					if (!updateTimer.isRunning()) {
						updateTimer.start();
					}
				} else {
					updateProcessNow();
				}
			}
		}
	};

	private transient final BreakpointListener breakpointListener = new BreakpointListener() {

		@Override
		public void breakpointReached(Process process, final Operator operator, final IOContainer ioContainer, int location) {
			if (process.equals(AbstractUIState.this.process)) {
				RUN_ACTION.setState(process.getProcessState());
				ProcessThread.beep("breakpoint");
				Window window = getWindow();
				if (window != null) {
					window.toFront();
				}
				resultDisplay.showData(ioContainer, "Breakpoint in " + operator.getName() + ", application " + operator.getApplyCount());
			}
		}

		/** Since the mainframe triggers the resume itself this method does nothing. */
		@Override
		public void resume() {
			RUN_ACTION.setState(process.getProcessState());
		}
	};
	private final JFrame window;

	/**
	 * Registers all RapidMiner GUI properties. This must often be done centrally in
	 * mainframe to ensure that the properties are set when the GUI is started.
	 */
	static {
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_MATRIXPLOT_SIZE, "The pixel size of each plot in matrix plots.", 1, Integer.MAX_VALUE, 200));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_ROWS_MAXIMUM, "The maximum number of rows used for a plotter, using only a sample of this size if more rows are available.", 1, Integer.MAX_VALUE, PlotterPanel.DEFAULT_MAX_NUMBER_OF_DATA_POINTS));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_LEGEND_CLASSLIMIT, "Limit number of displayed classes plotter legends. -1 for no limit.", -1, Integer.MAX_VALUE, 10));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_LEGEND_MINCOLOR, "The color for minimum values of the plotter legend.", java.awt.Color.blue));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_LEGEND_MAXCOLOR, "The color for maximum values of the plotter legend.", java.awt.Color.red));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_PLOTTER_COLORS_CLASSLIMIT, "Limit number of displayed classes for colorized plots. -1 for no limit.", -1, Integer.MAX_VALUE, 10));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_UNDOLIST_SIZE, "Maximum number of states in the undo list.", 1, Integer.MAX_VALUE, 100));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_ATTRIBUTEEDITOR_ROWLIMIT, "Maximum number of examples to use for the attribute editor. -1 for no limit.", -1, Integer.MAX_VALUE, 50));
		ParameterService.registerParameter(new ParameterTypeBoolean(MainFrame.PROPERTY_RAPIDMINER_GUI_BEEP_SUCCESS, "Beep on process success?", false));
		ParameterService.registerParameter(new ParameterTypeBoolean(MainFrame.PROPERTY_RAPIDMINER_GUI_BEEP_ERROR, "Beep on error?", false));
		ParameterService.registerParameter(new ParameterTypeBoolean(MainFrame.PROPERTY_RAPIDMINER_GUI_BEEP_BREAKPOINT, "Beep when breakpoint reached?", false));
		ParameterService.registerParameter(new ParameterTypeInt(MainFrame.PROPERTY_RAPIDMINER_GUI_MESSAGEVIEWER_ROWLIMIT, "Limit number of displayed rows in the message viewer. -1 for no limit.", -1, Integer.MAX_VALUE, 1000));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_MESSAGEVIEWER_HIGHLIGHT_NOTES, "The color for notes in the message viewer.", new java.awt.Color(51, 151, 51)));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_MESSAGEVIEWER_HIGHLIGHT_WARNINGS, "The color for warnings in the message viewer.", new java.awt.Color(51, 51, 255)));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_MESSAGEVIEWER_HIGHLIGHT_ERRORS, "The color for errors in the message viewer.", new java.awt.Color(255, 51, 204)));
		ParameterService.registerParameter(new ParameterTypeColor(MainFrame.PROPERTY_RAPIDMINER_GUI_MESSAGEVIEWER_HIGHLIGHT_LOGSERVICE, "The color for the logging service indicator in the message viewer.", new java.awt.Color(184, 184, 184)));
		ParameterService.registerParameter(new ParameterTypeBoolean(MainFrame.PROPERTY_RAPIDMINER_GUI_PROCESSINFO_SHOW, "Shows process info screen after loading?", true));
		ParameterService.registerParameter(new ParameterTypeCategory(MainFrame.PROPERTY_RAPIDMINER_GUI_SAVE_BEFORE_RUN, "Save process before running process?", DecisionRememberingConfirmDialog.PROPERTY_VALUES, DecisionRememberingConfirmDialog.FALSE));
		ParameterService.registerParameter(new ParameterTypeBoolean(MainFrame.PROPERTY_RAPIDMINER_GUI_SAVE_ON_PROCESS_CREATION, "Save process when creating them?", false));
		ParameterService.registerParameter(new ParameterTypeCategory(MainFrame.PROPERTY_RAPIDMINER_GUI_AUTO_SWITCH_TO_RESULTVIEW, "Automatically switch to results perspective when results are created?", DecisionRememberingConfirmDialog.PROPERTY_VALUES, DecisionRememberingConfirmDialog.TRUE));
		ParameterService.registerParameter(new ParameterTypeCategory(MainFrame.PROPERTY_RAPIDMINER_GUI_RESULT_DISPLAY_TYPE, "Determines the result display style.", ResultDisplayTools.TYPE_NAMES, 0));
		ParameterService.registerParameter(new ParameterTypeCategory(MainFrame.PROPERTY_RAPIDMINER_GUI_LOG_LEVEL, "Minimum level of messages that are logged in the GUIs log view.", LoggingViewer.SELECTABLE_LEVEL_NAMES, LoggingViewer.DEFAULT_LEVEL_INDEX));
	}

	/**
	 * @param title
	 */
	public AbstractUIState(final String initialPerspective, final JFrame frame,
			final Container contentPane) {
		// super(MainFrame.TITLE);
		window = frame;

		addProcessEditor(actions);
		addProcessEditor(xmlEditor);
		addProcessEditor(commentEditor);
		addProcessEditor(propertyPanel);
		addProcessEditor(operatorTree);
		addProcessEditor(operatorDocViewer);
		addProcessEditor(processPanel);
		addProcessEditor(errorTable);
		addProcessEditor(processContextEditor);
		addProcessEditor(getStatusBar());
		addProcessEditor(resultDisplay);

		if (frame != null) {
			frame.setTitle(AbstractUIState.TITLE);
			SwingTools.setFrameIcon(frame);
		}

		dockingContext.addDesktop(dockingDesktop);
		dockingDesktop.registerDockable(welcomeScreen);
		dockingDesktop.registerDockable(repositoryBrowser);
		dockingDesktop.registerDockable(operatorTree);
		dockingDesktop.registerDockable(propertyPanel);
		dockingDesktop.registerDockable(processPanel);
		dockingDesktop.registerDockable(commentEditor);
		dockingDesktop.registerDockable(xmlEditor);
		dockingDesktop.registerDockable(newOperatorEditor);
		dockingDesktop.registerDockable(errorTable);
		dockingDesktop.registerDockable(resultDisplay);
		dockingDesktop.registerDockable(messageViewer);
		dockingDesktop.registerDockable(systemMonitor);
		dockingDesktop.registerDockable(operatorDocViewer);
		dockingDesktop.registerDockable(processContextEditor);
		dockingDesktop.registerDockable(remoteProcessViewer);
		dockingDesktop.registerDockable(processPanel.getProcessRenderer()
				.getOverviewPanel());

		final ToolBarContainer toolBarContainer = ToolBarContainer
				.createDefaultContainer(true, true, true, true);
		//??? getContentPane)()
		contentPane.add(toolBarContainer, BorderLayout.CENTER);
		toolBarContainer.add(dockingDesktop, BorderLayout.CENTER);

		systemMonitor.startMonitorThread();
		resultDisplay.init(this);

		// menu bar
		menuBar = new JMenuBar();
		if (frame != null) {
			frame.setJMenuBar(menuBar);
		}

		fileMenu = new ResourceMenu("file");
		fileMenu.add(NEW_ACTION);
		fileMenu.add(OPEN_ACTION);
		fileMenu.add(OPEN_TEMPLATE_ACTION);
		updateRecentFileList();
		fileMenu.add(recentFilesMenu);
		fileMenu.addSeparator();
		fileMenu.add(SAVE_ACTION);
		fileMenu.add(SAVE_AS_ACTION);
		fileMenu.add(SAVE_AS_TEMPLATE_ACTION);
		fileMenu.addSeparator();
		final ResourceMenu importMenu = new ResourceMenu("file.import");
		importMenu.add(IMPORT_CSV_FILE_ACTION);
		importMenu.add(IMPORT_EXCEL_FILE_ACTION);
		importMenu.add(IMPORT_XML_FILE_ACTION);
		importMenu.add(IMPORT_ACCESS_FILE_ACTION);
		importMenu.add(IMPORT_DATABASE_TABLE_ACTION);
		importMenu.add(BlobImportWizard.IMPORT_BLOB_ACTION);
		fileMenu.add(importMenu);
		fileMenu.add(IMPORT_PROCESS_ACTION);
		fileMenu.add(EXPORT_PROCESS_ACTION);
		fileMenu.addSeparator();
		fileMenu.add(PRINT_ACTION);
		fileMenu.add(PRINT_PREVIEW_ACTION);
		fileMenu.add(PAGE_SETUP_ACTION);
		fileMenu.add(EXPORT_ACTION);
		fileMenu.addSeparator();
		fileMenu.add(EXIT_ACTION);
		menuBar.add(fileMenu);

		// edit menu
		((ResourceAction) actions.INFO_OPERATOR_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		((ResourceAction) actions.TOGGLE_ACTIVATION_ITEM).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		((ResourceAction) actions.RENAME_OPERATOR_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		((ResourceAction) actions.NEW_OPERATOR_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		((ResourceAction) actions.NEW_BUILDING_BLOCK_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		((ResourceAction) actions.SAVE_BUILDING_BLOCK_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		// not added for ProcessRenderer because there the DELETE_SELECTED_CONNECTION action is active
		((ResourceAction) actions.DELETE_OPERATOR_ACTION).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getOperatorTree());
		// commented out because toggleBreakpoint action is used at various places, especially at operator paramter frame which breaks if action is disabled
//        for (ToggleBreakpointItem item : actions.TOGGLE_BREAKPOINT) {
//        	 ((ResourceAction)item).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
//        }
		((ResourceAction) actions.TOGGLE_ALL_BREAKPOINTS).addToActionMap(JComponent.WHEN_FOCUSED, true, true, null, getProcessPanel().getProcessRenderer(), getOperatorTree());
		editMenu = new ResourceMenu("edit");
		editMenu.add(UNDO_ACTION);
		editMenu.add(REDO_ACTION);
		editMenu.addSeparator();
		editMenu.add(actions.INFO_OPERATOR_ACTION);
		editMenu.add(actions.TOGGLE_ACTIVATION_ITEM.createMenuItem());
		editMenu.add(actions.RENAME_OPERATOR_ACTION);
		editMenu.addSeparator();
		editMenu.add(actions.NEW_OPERATOR_ACTION);
		editMenu.add(actions.NEW_BUILDING_BLOCK_ACTION);
		editMenu.add(actions.SAVE_BUILDING_BLOCK_ACTION);
		editMenu.addSeparator();
		editMenu.add(CutCopyPasteDeleteAction.CUT_ACTION);
		editMenu.add(CutCopyPasteDeleteAction.COPY_ACTION);
		editMenu.add(CutCopyPasteDeleteAction.PASTE_ACTION);
		editMenu.add(CutCopyPasteDeleteAction.DELETE_ACTION);
//        editMenu.add(actions.DELETE_OPERATOR_ACTION);
		editMenu.addSeparator();
		for (final ToggleBreakpointItem item : actions.TOGGLE_BREAKPOINT) {
			editMenu.add(item.createMenuItem());
		}
		editMenu.add(actions.TOGGLE_ALL_BREAKPOINTS.createMenuItem());
		// editMenu.add(actions.MAKE_DIRTY_ACTION);
		menuBar.add(editMenu);

		// process menu
		processMenu = new ResourceMenu("process");
		processMenu.add(RUN_ACTION);
		processMenu.add(PAUSE_ACTION);
		processMenu.add(STOP_ACTION);
		processMenu.addSeparator();
		processMenu.add(VALIDATE_ACTION);
		processMenu.add(VALIDATE_AUTOMATICALLY_ACTION.createMenuItem());
		// JCheckBoxMenuItem onlyDirtyMenu = new JCheckBoxMenuItem(new
		// ResourceAction(true, "execute_only_dirty") {
		// private static final long serialVersionUID = 2158722678316407076L;
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// if (((JCheckBoxMenuItem)e.getSource()).isSelected()) {
		// getProcess().setExecutionMode(ExecutionMode.ONLY_DIRTY);
		// } else {
		// getProcess().setExecutionMode(ExecutionMode.ALWAYS);
		// }
		// }
		// });
		// expMenu.add(onlyDirtyMenu);

		final JCheckBoxMenuItem debugmodeMenu = new JCheckBoxMenuItem(
				new ResourceAction(true, "process_debug_mode") {

					private static final long serialVersionUID = 2158722678316407076L;

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (((JCheckBoxMenuItem) e.getSource()).isSelected()) {
							getProcess().setDebugMode(
									DebugMode.COLLECT_METADATA_AFTER_EXECUTION);
						} else {
							getProcess().setDebugMode(DebugMode.DEBUG_OFF);
						}
					}
				});
		processMenu.add(debugmodeMenu);
		processMenu.addSeparator();

		final JMenu wiringMenu = new ResourceMenu("wiring");
		wiringMenu.add(AUTO_WIRE);
		wiringMenu.add(AUTO_WIRE_RECURSIVELY);
		wiringMenu.add(REWIRE);
		wiringMenu.add(REWIRE_RECURSIVELY);
		processMenu.add(wiringMenu);
		final JMenu orderMenu = new ResourceMenu("execution_order");
		orderMenu
				.add(processPanel.getProcessRenderer().getFlowVisualizer().ALTER_EXECUTION_ORDER
						.createMenuItem());
		orderMenu
				.add(processPanel.getProcessRenderer().getFlowVisualizer().SHOW_EXECUTION_ORDER);
		processMenu.add(orderMenu);
		final JMenu layoutMenu = new ResourceMenu("process_layout");
		layoutMenu
				.add(processPanel.getProcessRenderer().ARRANGE_OPERATORS_ACTION);
		layoutMenu.add(processPanel.getProcessRenderer().AUTO_FIT_ACTION);
		layoutMenu
				.add(processPanel.getProcessRenderer().INCREASE_PROCESS_LAYOUT_WIDTH_ACTION);
		layoutMenu
				.add(processPanel.getProcessRenderer().DECREASE_PROCESS_LAYOUT_WIDTH_ACTION);
		layoutMenu
				.add(processPanel.getProcessRenderer().INCREASE_PROCESS_LAYOUT_HEIGHT_ACTION);
		layoutMenu
				.add(processPanel.getProcessRenderer().DECREASE_PROCESS_LAYOUT_HEIGHT_ACTION);
		processMenu.add(layoutMenu);
		processMenu.addSeparator();
		processMenu.add(RUN_REMOTE_ACTION);
		menuBar.add(processMenu);

		// tools menu
		toolsMenu = new ResourceMenu("tools");
		toolsMenu.add(MANAGE_BUILDING_BLOCKS_ACTION);
		toolsMenu.add(MANAGE_TEMPLATES_ACTION);
		toolsMenu.addSeparator();
		toolsMenu.add(ANOVA_CALCULATOR_ACTION);
		toolsMenu.addSeparator();
		toolsMenu.add(CHECK_FOR_JDBC_DRIVERS_ACTION);
		toolsMenu.add(MANAGE_DB_CONNECTIONS_ACTION);
		toolsMenu.add(ManageDatabaseDriversDialog.SHOW_DIALOG_ACTION);
		toolsMenu.addSeparator();
		toolsMenu.add(UsageStatsTransmissionDialog.SHOW_STATISTICS_ACTION);
		toolsMenu.add(SETTINGS_ACTION);
		menuBar.add(toolsMenu);

		// view menu
		viewMenu = new ResourceMenu("view");
		viewMenu.add(perspectives.getWorkspaceMenu());
		viewMenu.add(NEW_PERSPECTIVE_ACTION);
		viewMenu.add(dockableMenu = new DockableMenu(dockingContext));
		viewMenu.add(perspectives.RESTORE_DEFAULT_ACTION);
		viewMenu.addSeparator();
		viewMenu.add(TOGGLE_EXPERT_MODE_ACTION.createMenuItem());
		menuBar.add(viewMenu);

		// help menu
		helpMenu = new ResourceMenu("help");
		//helpMenu.add(TOUR_ACTION); //TODO reenable when tour is complete
		helpMenu.add(TUTORIAL_ACTION);
		// TODO: Re-add updated manual
		// helpMenu.add(new ResourceAction("gui_manual") {
		// private static final long serialVersionUID = 1L;
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// URL manualResource =
		// Tools.getResource("manual/RapidMinerGUIManual.html");
		// if (manualResource != null)
		// Browser.showDialog(manualResource);
		// else
		// SwingTools.showVerySimpleErrorMessage("Cannot load GUI manual: file not found.");
		// }
		//
		// });
		helpMenu.add(new BrowseAction("help_support", URI
				.create("http://rapid-i.com/content/view/60/89/lang,en/")));
		helpMenu.add(new BrowseAction("help_videotutorials", URI
				.create("http://rapid-i.com/content/view/189/198/")));
		helpMenu.add(new BrowseAction("help_forum", URI
				.create("http://forum.rapid-i.com")));
		helpMenu.add(new BrowseAction("help_wiki", URI
				.create("http://wiki.rapid-i.com")));

		helpMenu.addSeparator();
		// helpMenu.add(CHECK_FOR_UPDATES_ACTION);
		helpMenu.add(UpdateDialog.UPDATE_ACTION);
		helpMenu.add(ExtensionDialog.MANAGE_EXTENSIONS);

		final List allPlugins = Plugin.getAllPlugins();
		if (allPlugins.size() > 0) {
			final JMenu extensionsMenu = new ResourceMenu("about_extensions");
			final Iterator i = allPlugins.iterator();
			while (i.hasNext()) {
				final Plugin plugin = (Plugin) i.next();
				if (plugin.showAboutBox()) {
					extensionsMenu.add(new ResourceAction("about_extension",
							plugin.getName()) {

						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(final ActionEvent e) {
							plugin.createAboutBox(
									AbstractUIState.this.getWindow())
									.setVisible(true);
						}
					});
				}
			}
			helpMenu.add(extensionsMenu);
		}
		helpMenu.add(new AboutAction(this.getWindow()));

		menuBar.add(helpMenu);

		// Tool Bar
		final RapidDockingToolbar fileToolBar = new RapidDockingToolbar("file");
		fileToolBar.add(makeToolbarButton(NEW_ACTION));
		fileToolBar.add(makeToolbarButton(OPEN_ACTION));
		fileToolBar.add(makeToolbarButton(SAVE_ACTION));
		fileToolBar.add(makeToolbarButton(SAVE_AS_ACTION));
		fileToolBar.add(makeToolbarButton(PRINT_ACTION));

		final RapidDockingToolbar editToolBar = new RapidDockingToolbar("edit");
		editToolBar.add(makeToolbarButton(UNDO_ACTION));
		editToolBar.add(makeToolbarButton(REDO_ACTION));

		final RapidDockingToolbar runToolBar = new RapidDockingToolbar("run");
		runToolBar.add(makeToolbarButton(RUN_ACTION));
		runToolBar.add(makeToolbarButton(PAUSE_ACTION));
		runToolBar.add(makeToolbarButton(STOP_ACTION));

		if ("true".equals(System
				.getProperty(RapidMiner.PROPERTY_DEVELOPER_MODE))) {
			runToolBar.addSeparator();
			runToolBar.add(makeToolbarButton(VALIDATE_ACTION));
		}

		final RapidDockingToolbar viewToolBar = perspectives
				.getWorkspaceToolBar();
		//TODO: enable when tour is useable
//		RapidDockingToolbar achievementToolBar = new RapidDockingToolbar("achievement");
//		DropDownButton achievement = TourChooser.makeAchievmentDropDown();
//		achievement.addToToolBar(achievementToolBar);
		
		
		final ToolBarPanel toolBarPanel = toolBarContainer
				.getToolBarPanelAt(BorderLayout.NORTH);
		toolBarPanel.add(fileToolBar, new ToolBarConstraints(0, 0));
		toolBarPanel.add(editToolBar, new ToolBarConstraints(0, 1));
		toolBarPanel.add(runToolBar, new ToolBarConstraints(0, 2));
		toolBarPanel.add(viewToolBar, new ToolBarConstraints(0, 3));
		//TODO: shift achievementToolBar to the right
		// toolBarPanel.add(achievementToolBar, new ToolBarConstraints(0, 4)); 
		contentPane.add(getStatusBar(), BorderLayout.SOUTH);
		getStatusBar().startClockThread();

		setProcess(new Process(), true);
		selectOperator(process.getRootOperator());
		addToUndoList();

		perspectives.showPerspective(initialPerspective);
		metaDataUpdateQueue.start();
	}

	protected JButton makeToolbarButton(final Action action) {
		final JButton button = new JButton(action);
		if (button.getIcon() != null) {
			button.setText(null);
		}
		return button;
	}

	@Override
	public boolean isTutorialMode() {
		return this.tutorialMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#startTutorial()
	 */
	@Override
	public void startTutorial() {
		if (close()) {
			new Tutorial(this).setVisible(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#setTutorialMode(boolean)
	 */
	@Override
	public void setTutorialMode(final boolean mode) {
		this.tutorialMode = mode;
		if (tutorialMode) {
			SAVE_ACTION.setEnabled(false);
			SAVE_AS_ACTION.setEnabled(false);
		} else {
			SAVE_ACTION.setEnabled(false);
			SAVE_AS_ACTION.setEnabled(true);
		}
	}

	@Override
	public void setExpertMode(final boolean expert) {
		TOGGLE_EXPERT_MODE_ACTION.setSelected(expert);
		TOGGLE_EXPERT_MODE_ACTION.actionToggled(null);
	}

	@Override
	public OperatorPropertyPanel getPropertyPanel() {
		return propertyPanel;
	}

	@Override
	public LoggingViewer getMessageViewer() {
		return messageViewer;
	}

	@Override
	public NewOperatorEditor getNewOperatorEditor() {
		return newOperatorEditor;
	}

	@Override
	public OperatorTree getOperatorTree() {
		return operatorTree.getOperatorTree();
	}

	@Override
	public Actions getActions() {
		return actions;
	}

	@Override
	public ResultDisplay getResultDisplay() {
		return resultDisplay;
	}

	@Override
	public int getProcessState() {
		if (process == null) {
			return Process.PROCESS_STATE_UNKNOWN;
		} else {
			return process.getProcessState();
		}
	}

	@Override
	public final Process getProcess() {
		return this.process;
	}

	@Override
	public void newProcess() {
		if (close(false)) {
			// process changed -> clear undo history
			resetUndo();

			stopProcess();
			changed = false;
			setProcess(new Process(), true);
			addToUndoList();
			if (!"false"
					.equals(ParameterService
							.getParameterValue(PROPERTY_RAPIDMINER_GUI_SAVE_ON_PROCESS_CREATION))) {
				SaveAction.save(getProcess());
			}
			SAVE_ACTION.setEnabled(false);
		}
	}

	@Override
	public void runProcess() {
		if (getProcessState() == Process.PROCESS_STATE_STOPPED) {
			// Run
			if ((isChanged() || getProcess().getProcessLocation() == null)
					&& !isTutorialMode()) {
				if (DecisionRememberingConfirmDialog.confirmAction(
						"save_before_run",
						PROPERTY_RAPIDMINER_GUI_SAVE_BEFORE_RUN)) {
					SaveAction.save(getProcess());
				}
			}

			processThread = new ProcessThread(AbstractUIState.this.process);

			try {
				processThread.start();
			} catch (final Exception t) {
				SwingTools.showSimpleErrorMessage("cannot_start_process", t);
			}
		} else {
			process.resume();
		}
	}

	/**
	 * Can be used to stop the currently running process. Please note that
	 * the ProcessThread will still be running in the background until the current
	 * operator is finished.
	 */
	@Override
	public void stopProcess() {
		if (getProcessState() != Process.PROCESS_STATE_STOPPED) {
			getProcess().getLogger().info(
					"Process stopped. Completing current operator.");
			getStatusBar().setSpecialText(
					"Process stopped. Completing current operator.");
			if (processThread != null) {
				if (processThread.isAlive()) {
					processThread.setPriority(Thread.MIN_PRIORITY);
					processThread.stopProcess();
				}
			}
		}
	}

	@Override
	public void pauseProcess() {
		if (getProcessState() == Process.PROCESS_STATE_RUNNING) {
			getProcess().getLogger().info(
					"Process paused. Completing current operator.");
			getStatusBar().setSpecialText(
					"Process paused. Completing current operator.");
			if (processThread != null) {
				processThread.pauseProcess();
			}
		}
	}

	/**
	 * Will be invoked from the process thread after the process was
	 * successfully ended.
	 */
	@Override
	public void processEnded(final Process process, final IOContainer results) {
		if (process.equals(AbstractUIState.this.process)) {
			if (results != null) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						final JFrame window = AbstractUIState.this.getWindow();
						if (window != null) {
							window.toFront();
						}
					}
				});
			}
		}
		if (process.equals(AbstractUIState.this.process)) {
			if (results != null) {
				resultDisplay.showData(results, "Process results");
			}
		}
	}

	/**
	 * Sets a new process and registers the MainFrame's listeners.
	 */
	public void setProcess(Process process, boolean newProcess) {
		setProcess(process, newProcess, false);
	}

	/**
	 * Sets a new process and registers the MainFrame's listeners.
	 */
	public void setProcess(Process process, boolean newProcess, boolean addToUndoList) {
		final boolean firstProcess = this.process == null;
		if (this.process != null) {
			// this.process.getRootOperator().removeObserver(processObserver);
			this.process.removeObserver(processObserver);
		}

		if (getProcessState() != Process.PROCESS_STATE_STOPPED) {
			if (processThread != null) {
				processThread.stopProcess();
			}
		}

		if (process != null) {
			// process.getRootOperator().addObserver(processObserver, true);
			process.addObserver(processObserver, true);

			synchronized (process) {
				this.process = process;
				this.processThread = new ProcessThread(this.process);
				this.process.addBreakpointListener(breakpointListener);
				if (addToUndoList) {
					addToUndoList(process.getRootOperator().getXML(true, false), false);
				}
				fireProcessChanged();
				selectOperator(this.process.getRootOperator());
				if (VALIDATE_AUTOMATICALLY_ACTION.isSelected()) {
					validateProcess(false);
				}
			}
		}
		if (newProcess && !firstProcess) {
			// VLDocking appears to get nervous when applying two perspectives
			// while the
			// window is not yet visible. So to avoid that we set design and
			// then welcome
			// during startup, avoid applying design if this is the first
			// process we create.
			perspectives.showPerspective("design");
		}
		setTitle();
		getStatusBar().setTrafficLight(StatusBar.TRAFFIC_LIGHT_INACTIVE);
		getStatusBar().clearSpecialText();
	}

	/** Returns true if the process has changed since the last save. */
	@Override
	public boolean isChanged() {
		return changed;
	}

	private boolean addToUndoList() {
		return addToUndoList(null, false);
	}

	/**
	 * Adds the current state of the process to the undo list.
	 * 
	 * Note: This method must not be exposed by making it public. It may confuse
	 * the MainFrame such that it can no longer determine correctly whether
	 * validation is possible.
	 * 
	 * @return true if process really differs.
	 */
	private boolean addToUndoList(String currentStateXML, boolean viewSwitch) {
		final String lastStateXML = undoManager.getNumberOfUndos() != 0 ? undoManager.getXml(undoManager.getNumberOfUndos() - 1) : null;
		if (currentStateXML == null) {
			currentStateXML = this.process.getRootOperator().getXML(true);
		}
		if (currentStateXML != null) {
			if (lastStateXML == null || !lastStateXML.equals(currentStateXML) || viewSwitch) {
				if (undoIndex < undoManager.getNumberOfUndos() - 1) {
					while (undoManager.getNumberOfUndos() > undoIndex + 1) {
						undoManager.removeLast();
					}
				}
				undoManager.add(currentStateXML, getProcessPanel().getProcessRenderer().getDisplayedChain(), getFirstSelectedOperator());
				String maxSizeProperty = ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GUI_UNDOLIST_SIZE);
				int maxSize = 20;
				try {
					if (maxSizeProperty != null) {
						maxSize = Integer.parseInt(maxSizeProperty);
					}
				} catch (NumberFormatException e) {
					LogService.getRoot().warning("com.rapidminer.gui.main_frame_warning");
					//LogService.getRoot().warning("Bad integer format for property 'rapidminer.gui.undolist.size', using default size of 20.");
				}
				while (undoManager.getNumberOfUndos() > maxSize) {
					undoManager.removeFirst();
				}
				undoIndex = undoManager.getNumberOfUndos() - 1;
				enableUndoAction();

				boolean oldChangedValue = AbstractUIState.this.changed;
				// mark as changed only if the XML has changed
				if ((currentStateXML == null ? lastStateXML == null : currentStateXML.equals(lastStateXML))) {
					return false;
				}
				AbstractUIState.this.changed = lastStateXML != null;

				if (!oldChangedValue) {
					setTitle();
				}
				if (AbstractUIState.this.process.getProcessLocation() != null && !tutorialMode) {
					AbstractUIState.this.SAVE_ACTION.setEnabled(true);
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public void undo() {
		if (undoIndex > 0) {
			undoIndex--;
			setProcessIntoStateAt(undoIndex, true);
		}
		enableUndoAction();
	}

	@Override
	public void redo() {
		if (undoIndex < undoManager.getNumberOfUndos()) {
			undoIndex++;
			setProcessIntoStateAt(undoIndex, false);
		}
		enableUndoAction();
	}

	private void enableUndoAction() {
		if (undoIndex > 0) {
			UNDO_ACTION.setEnabled(true);
		} else {
			UNDO_ACTION.setEnabled(false);
		}
		if (undoIndex < undoManager.getNumberOfUndos() - 1) {
			REDO_ACTION.setEnabled(true);
		} else {
			REDO_ACTION.setEnabled(false);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasUndoSteps() {
		return undoIndex > 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasRedoSteps() {
		return undoIndex < undoManager.getNumberOfUndos() - 1;
	}

	private void setProcessIntoStateAt(int undoIndex, boolean undo) {
		String stateXML = undoManager.getXml(undoIndex);
		OperatorChain shownOperatorChain = null;
		if (undo) {
			shownOperatorChain = undoManager.getOperatorChain(undoIndex);
		} else {
			shownOperatorChain = undoManager.getOperatorChain(undoIndex);
		}
		Operator selectedOperator = undoManager.getSelectedOperator(undoIndex);
		try {
			synchronized (process) {
				String oldXml = process.getRootOperator().getXML(true);
				Process process = new Process(stateXML, this.process);
				// this.process.setupFromXML(stateXML);
				setProcess(process, false);
				// cannot use method processChanged() because this would add the
				// old state to the undo stack!
				if (!stateXML.equals(oldXml)) {
					this.changed = true;
					setTitle();
					if (this.process.getProcessLocation() != null && !tutorialMode) {
						this.SAVE_ACTION.setEnabled(true);
					}
				}

				// restore selected operator
				if (selectedOperator != null) {
					Operator restoredOperator = getProcess().getOperator(selectedOperator.getName());
					if (restoredOperator != null) {
						selectOperator(restoredOperator);
					}
				}

				// restore process panel view on correct subprocess on undo
				if (shownOperatorChain != null) {
					OperatorChain restoredOperatorChain = (OperatorChain) getProcess().getOperator(shownOperatorChain.getName());
					getProcessPanel().showOperatorChain(restoredOperatorChain);
				}
			}
		} catch (Exception e) {
			SwingTools.showSimpleErrorMessage("while_changing_process", e);
		}

		lastProcessDisplayedOperatorChain = getProcessPanel().getProcessRenderer().getDisplayedChain();
		lastProcessXML = process.getRootOperator().getXML(true, false);
	}

	/**
	 * Closes the current process
	 * @param askForConfirmation if <code>true</code>, will prompt the user if he really wants to close the current process
	 * @return
	 */
	public boolean close(boolean askForConfirmation) {
		if (changed) {
			final ProcessLocation loc = process.getProcessLocation();
			String locName;
			if (loc != null) {
				locName = loc.getShortName();
			} else {
				locName = "unnamed";
			}
			switch (SwingTools.showConfirmDialog("save", ConfirmDialog.YES_NO_CANCEL_OPTION, locName)) {
				case ConfirmDialog.YES_OPTION:
					SaveAction.save(getProcess());

					// it may happen that save() does not actually save the process, because the user hits cancel in the
					// saveAs dialog or an error occurs. In this case the process won't be marked as unchanged. Thus,
					// we return the process changed status.
					return !isChanged();
				case ConfirmDialog.NO_OPTION:
					// ask for confirmation before stopping the currently running process (if askForConfirmation=true)
					if (askForConfirmation) {
						if (RapidMinerGUI.getMainFrame().getProcessState() == Process.PROCESS_STATE_RUNNING ||
								RapidMinerGUI.getMainFrame().getProcessState() == Process.PROCESS_STATE_PAUSED) {
							if (SwingTools.showConfirmDialog("close_running_process", ConfirmDialog.YES_NO_OPTION) == ConfirmDialog.NO_OPTION) {
								return false;
							}
						}
					}
					if (getProcessState() != Process.PROCESS_STATE_STOPPED) {
						synchronized (processThread) {
							processThread.stopProcess();
						}
					}
					return true;
				default: // cancel
					return false;
			}
		} else {
			return true;
		}
	}

	public boolean close() {
		return close(true);
	}

	@Override
	public void setOpenedProcess(final Process process, final boolean showInfo,
									final String sourceName) {
		setProcess(process, true);
		if (process.getImportMessage() != null) {
			SwingTools.showLongMessage("import_message",
					process.getImportMessage());
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SAVE_ACTION.setEnabled(false);
			}
		});

		// process changed -> clear undo history
		resetUndo();

		List<UnknownParameterInformation> unknownParameters = null;
		synchronized (process) {
			RapidMinerGUI.useProcessFile(AbstractUIState.this.process);
			unknownParameters = process.getUnknownParameters();
		}

		addToUndoList();
		updateRecentFileList();
		changed = false;

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SAVE_ACTION.setEnabled(false);
				setTitle();
			}
		});

		// show unsupported parameters info?
		if (unknownParameters != null && unknownParameters.size() > 0) {
			final UnknownParametersInfoDialog unknownParametersInfoDialog = new UnknownParametersInfoDialog(AbstractUIState.this.getWindow(), unknownParameters);
			if (SwingUtilities.isEventDispatchThread()) {
				unknownParametersInfoDialog.setVisible(true);
			} else {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {

						@Override
						public void run() {
							unknownParametersInfoDialog.setVisible(true);
						}
					});
				} catch (Exception e) {
					LogService.getRoot().log(Level.WARNING, "Error opening the unknown parameter dialog: " + e, e);
				}
			}
		} else if (showInfo && Tools.booleanValue(ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GUI_PROCESSINFO_SHOW), true)) {
			// show process info?
			final String text = AbstractUIState.this.process.getRootOperator().getUserDescription();
			if (text != null && text.length() != 0) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						ProcessInfoScreen infoScreen = new ProcessInfoScreen(sourceName, text);
						infoScreen.setVisible(true);
					}
				});
			}
		}
		fireProcessLoaded();
	}

	private void resetUndo() {
		undoIndex = 0;
		undoManager.reset();
		enableUndoAction();
	}

	@Override
	public void saveAsTemplate() {
		synchronized (process) {
			final SaveAsTemplateDialog dialog = new SaveAsTemplateDialog(
					AbstractUIState.this.process);
			dialog.setVisible(true);
			if (dialog.isOk()) {
				try {
					dialog.getTemplate().saveAsUserTemplate(
							AbstractUIState.this.process);
				} catch (final Exception ioe) {
					SwingTools.showSimpleErrorMessage(
							"cannot_write_template_file", ioe);
				}
			}
		}
	}

//NEW
	public void exit(final boolean relaunch) {
		if (changed) {
			final ProcessLocation loc = process.getProcessLocation();
			String locName;
			if (loc != null) {
				locName = loc.getShortName();
			} else {
				locName = "unnamed";
			}
			switch (SwingTools.showConfirmDialog("save", ConfirmDialog.YES_NO_CANCEL_OPTION, locName)) {
				case ConfirmDialog.YES_OPTION:
					SaveAction.save(process);
					if (changed) {
						return;
					}
					break;
				case ConfirmDialog.NO_OPTION:
					break;
				case ConfirmDialog.CANCEL_OPTION:
				default:
					return;
			}
		} else {
			if (!relaunch) { // in this case we have already confirmed
				final int answer = ConfirmDialog.showConfirmDialog("exit", ConfirmDialog.YES_NO_OPTION, RapidMinerGUI.PROPERTY_CONFIRM_EXIT, ConfirmDialog.YES_OPTION);
				if (answer != ConfirmDialog.YES_OPTION) {
					return;
				}
			}
		}
		stopProcess();
		getWindow().dispose();
		RapidMiner.quit(relaunch ? RapidMiner.ExitMode.RELAUNCH : RapidMiner.ExitMode.NORMAL);
	}

	/**
	 * This methods provide plugins the possibility to modify the menus
	 */
	@Override
	public void removeMenu(final int index) {
		menuBar.remove(menuBar.getMenu(index));
	}

	@Override
	public void removeMenuItem(final int menuIndex, final int itemIndex) {
		menuBar.getMenu(menuIndex).remove(itemIndex);
	}

	@Override
	public void addMenuItem(final int menuIndex, final int itemIndex,
							final JMenuItem item) {
		menuBar.getMenu(menuIndex).add(item, itemIndex);
	}

	@Override
	public void addMenu(final int menuIndex, final JMenu menu) {
		menuBar.add(menu, menuIndex);
	}

	@Override
	public void addMenuSeparator(final int menuIndex) {
		menuBar.getMenu(menuIndex).addSeparator();
	}

	@Override
	public List<Operator> getSelectedOperators() {
		return selectedOperators;
	}

	@Override
	public Operator getFirstSelectedOperator() {
		return selectedOperators.isEmpty() ? null : selectedOperators.get(0);
	}

	@Override
	public void addProcessEditor(final ProcessEditor p) {
		processEditors.add(ProcessEditor.class, p);
	}

	public void removeProcessEditor(ProcessEditor p) {
		processEditors.remove(ProcessEditor.class, p);
	}

	@Override
	public void addProcessStorageListener(ProcessStorageListener listener) {
		storageListeners.add(listener);
	}

	@Override
	public void removeProcessStorageListener(ProcessStorageListener listener) {
		storageListeners.remove(listener);
	}

	@Override
	public void selectOperator(Operator currentlySelected) {
		if (currentlySelected == null) {
			currentlySelected = process.getRootOperator();
		}
		selectOperators(Collections.singletonList(currentlySelected));
	}

	@Override
	public void selectOperators(List<Operator> currentlySelected) {
		if (currentlySelected == null) {
			currentlySelected = Collections.<Operator> singletonList(process
					.getRootOperator());
		}
		for (final Operator op : currentlySelected) {
			final Process selectedProcess = op.getProcess();
			if (selectedProcess == null || selectedProcess != process) {
				SwingTools.showVerySimpleErrorMessage("op_deleted",
						op.getName());
				return;
			}
		}
		this.selectedOperators = currentlySelected;
		fireSelectedOperatorChanged(selectedOperators);
	}

	/**
	 * Notifies the main editor of the change of the currently selected
	 * operator.
	 */
	private void fireSelectedOperatorChanged(
												final List<Operator> currentlySelected) {
		for (final ProcessEditor editor : processEditors
				.getListeners(ProcessEditor.class)) {
			editor.setSelection(currentlySelected);
		}
	}

	@Override
	public void fireProcessUpdated() {
		for (final ProcessEditor editor : processEditors
				.getListeners(ProcessEditor.class)) {
			editor.processUpdated(process);
		}
	}

	private void fireProcessChanged() {
		for (final ProcessEditor editor : processEditors
				.getListeners(ProcessEditor.class)) {
			editor.processChanged(process);
		}
	}

	private void fireProcessLoaded() {
		LinkedList<ProcessStorageListener> list = new LinkedList<ProcessStorageListener>(storageListeners);
		for (ProcessStorageListener l : list) {
			l.opened(process);
		}
	}

	@Override
	public DockingDesktop getDockingDesktop() {
		return dockingDesktop;
	}

	@Override
	public Perspectives getPerspectives() {
		return perspectives;
	}

	@Override
	public void handleBrokenProxessXML(final ProcessLocation location,
										final String xml, final Exception e) {
		SwingTools.showSimpleErrorMessage("while_loading", e,
				location.toString(), e.getMessage());
		final Process process = new Process();
		process.setProcessLocation(location);
		setProcess(process, true);
		perspectives.showPerspective("design");
		// TODO: Re-enable this
		// mainEditor.changeToXMLEditor();
		xmlEditor.setText(xml);
	}

	@Override
	public OperatorDocViewer getOperatorDocViewer() {
		return operatorDocViewer;
	}

	@Override
	public ProcessPanel getProcessPanel() {
		return processPanel;
	}

	@Override
	public void registerDockable(final Dockable dockable) {
		dockingDesktop.registerDockable(dockable);
	}

	@Override
	public void processHasBeenSaved() {
		SAVE_ACTION.setEnabled(false);
		changed = false;
		setTitle();
		updateRecentFileList();

		// update RUN_REMOTE_NOW action enabled state
		try {
			if (process.getRepositoryLocation() != null && process.getRepositoryLocation().getRepository() instanceof RemoteRepository) {
				RUN_REMOTE_NOW_ACTION.setEnabled(true);
			} else {
				RUN_REMOTE_NOW_ACTION.setEnabled(false);
			}
		} catch (RepositoryException e) {
			RUN_REMOTE_NOW_ACTION.setEnabled(false);
		}
	}

	@Override
	public ProcessContextProcessEditor getProcessContextEditor() {
		return processContextEditor;
	}

	@Override
	public RepositoryBrowser getRepositoryBrowser() {
		return repositoryBrowser;
	}

	@Override
	public Component getXMLEditor() {
		return xmlEditor;
	}

	/**
	 * This returns the file menu to change menu entries
	 */
	@Override
	public JMenu getFileMenu() {
		return fileMenu;
	}

	/**
	 * This returns the tools menu to change menu entries
	 */
	@Override
	public JMenu getToolsMenu() {
		return toolsMenu;
	}

	/**
	 * This returns the complete menu bar to insert additional menus
	 */
	@Override
	public JMenuBar getMainMenuBar() {
		return menuBar;
	}

	/**
	 * This returns the edit menu to change menu entries
	 */
	@Override
	public JMenu getEditMenu() {
		return editMenu;
	}

	/**
	 * This returns the process menu to change menu entries
	 */
	@Override
	public JMenu getProcessMenu() {
		return processMenu;
	}

	/**
	 * This returns the help menu to change menu entries
	 */
	@Override
	public JMenu getHelpMenu() {
		return helpMenu;
	}

	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#getDockableMenu()
	 */
	@Override
	public DockableMenu getDockableMenu() {
		return dockableMenu;
		
	}

	// The status bar of the application, usually displayed at the bottom
	// of the frame.
	private final StatusBar statusBar = new StatusBar(false, true, true);

	@Override
	public StatusBar getStatusBar() {
		return statusBar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getImportAccessFileAction()
	 */
	@Override
	public Action getImportAccessFileAction() {
		return IMPORT_ACCESS_FILE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getImportCsvFileAction()
	 */
	@Override
	public Action getImportCsvFileAction() {
		return IMPORT_CSV_FILE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getImportDatabaseTableAction()
	 */
	@Override
	public Action getImportDatabaseTableAction() {
		return IMPORT_DATABASE_TABLE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getImportExcelFileAction()
	 */
	@Override
	public Action getImportExcelFileAction() {
		return IMPORT_EXCEL_FILE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#getImportXmlFileAction()
	 */
	@Override
	public Action getImportXmlFileAction() {
		return IMPORT_XML_FILE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getValidateAutomaticallyAction()
	 */
	@Override
	public ToggleAction getValidateAutomaticallyAction() {
		return VALIDATE_AUTOMATICALLY_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#getPropagateRealMetadataAction()
	 */
	@Override
	public Action getPropagateRealMetadataAction() {
		return PROPAGATE_REAL_METADATA_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getRewireRecursively()
	 */
	@Override
	public Action getRewireRecursively() {
		return REWIRE_RECURSIVELY;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#getRunRemoteNowAction()
	 */
	@Override
	public RunRemoteNowAction getRunRemoteNowAction() {
		return RUN_REMOTE_NOW_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getRunAction()
	 */
	@Override
	public RunAction getRunAction() {
		return RUN_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * @see com.rapidminer.gui.MainUIState#getSaveAction()
	 */
	@Override
	public SaveAction getSaveAction() {
		return SAVE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getToggleExpertModeAction()
	 */
	@Override
	public ToggleAction getToggleExpertModeAction() {
		return TOGGLE_EXPERT_MODE_ACTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.rapidminer.gui.MainUIState#getWindow()
	 */
	@Override
	public JFrame getWindow() {
		return window;
	}

	/**
	 * @return the processThread
	 */
	public final ProcessThread getProcessThread() {
		return processThread;
	}

	/**
	 * @return the metaDataUpdateQueue
	 */
	public final MetaDataUpdateQueue getMetaDataUpdateQueue() {
		return metaDataUpdateQueue;
	}
}