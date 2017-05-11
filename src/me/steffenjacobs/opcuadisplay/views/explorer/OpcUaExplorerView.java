package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.shared.util.Images;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.xml.XmlExport;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.xml.XmlImport;
import me.steffenjacobs.opcuadisplay.views.CloseableView;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.AddDialogType;
import me.steffenjacobs.opcuadisplay.views.explorer.events.ChangeSelectedNodeEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.events.RootUpdatedEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.events.SelectedNodeChangedEvent;
import me.steffenjacobs.opcuadisplay.wizard.exp.OpcUaExportWizard;
import me.steffenjacobs.opcuadisplay.wizard.exp.events.ExportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.wizard.imp.OpcUaImportWizard;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardOpenEvent;
import me.steffenjacobs.opcuadisplay.wizard.newProject.events.NewProjectWizardFinishEvent;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class OpcUaExplorerView extends CloseableView {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "me.steffenjacobs.opcuadisplay.views.explorer.OpcUaExplorerView";

	private TreeViewer viewer;
	private Action doubleClickAction, selectionChangedAction;
	private Action openImportWizard, openExportWizard;
	private Action collapseAllAction, expandAllAction;
	private Action addVariable, addMethod, addObject, addProperty, addObjectType, addVariableType, addDataType;
	private Action removeAction;
	private OpcUaConnector connector;

	private CachedObjectNode cachedRoot;

	@Override
	public String getIdentifier() {
		return ID;
	}

	private void hookDoubleClickAction() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				selectionChangedAction.run();

			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		connector = new OpcUaConnector(this.viewer.getControl().getShell());
		NodeNavigator.getInstance().setRoot(CachedBaseNode.getDummyNoData());
		viewer.setContentProvider(connector);
		viewer.setInput(getViewSite());
		viewer.setLabelProvider(new NodeClassLabelProvider());

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "me.steffenjacobs.opcuadisplay.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		registerListeners();
	}

	private void registerListeners() {

		// listener for attribute modification
		EventBus.getInstance().addListener(this, AttributeModifiedEvent.IDENTIFIER,
				new EventListener<EventBus.Event>() {
					@Override
					public void onAction(Event event) {
						viewer.refresh();
					}
				});

		// listener when the selection in the tree viewer should change
		EventBus.getInstance().addListener(this, ChangeSelectedNodeEvent.IDENTIFIER,
				new EventListener<ChangeSelectedNodeEvent>() {
					@Override
					public void onAction(ChangeSelectedNodeEvent event) {
						onChangeSelectedNode(event);
					}
				});

		// listener for import finished
		EventBus.getInstance().addListener(this, RootUpdatedEvent.IDENTIFIER, new EventListener<RootUpdatedEvent>() {
			@Override
			public void onAction(RootUpdatedEvent event) {
				viewer.refresh();
				expandToDefaultState();
				EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(event.getNode(), false));
			}
		});

		// listeners for import wizard
		EventBus.getInstance().addListener(this, ImportWizardOpenEvent.IDENTIFIER,
				new EventListener<ImportWizardOpenEvent>() {
					@Override
					public void onAction(ImportWizardOpenEvent event) {
						onWizardOpen();
					}
				});

		EventBus.getInstance().addListener(this, ImportWizardCancelEvent.IDENTIFIER,
				new EventListener<ImportWizardCancelEvent>() {
					@Override
					public void onAction(ImportWizardCancelEvent event) {
						onWizardCancel();
					}
				});

		EventBus.getInstance().addListener(this, ImportWizardFinishEvent.IDENTIFIER,
				new EventListener<ImportWizardFinishEvent>() {
					@Override
					public void onAction(ImportWizardFinishEvent event) {
						onImportWizardFinish(event.getUrl(), event.isServer(), event.isBaseDataTypesImplicit());
					}
				});

		// listener for export wizard
		EventBus.getInstance().addListener(this, ExportWizardFinishEvent.IDENTIFIER,
				new EventListener<ExportWizardFinishEvent>() {
					@Override
					public void onAction(ExportWizardFinishEvent event) {
						onExportWizardFinish(event.getUrl());
					}
				});

		// listener for create-new wizard
		EventBus.getInstance().addListener(this, NewProjectWizardFinishEvent.IDENTIFIER,
				new EventListener<NewProjectWizardFinishEvent>() {
					@Override
					public void onAction(NewProjectWizardFinishEvent event) {
						onNewProjectWizardFinish(event.isGenerateFolders(), event.isGenerateBaseTypes());
					}
				});
	}

	public void onChangeSelectedNode(ChangeSelectedNodeEvent event) {
		viewer.setSelection(new StructuredSelection(event.getNode()), event.isRevealInTree());
		// not necessary to fire SelectedNodeChangedEvent, because it will be
		// fired by SelectionListener of Treeviewer
		viewer.refresh();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				OpcUaExplorerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(openImportWizard);
		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
	}

	/** adds the available edit options */
	private void addAvailableEditOptions(IMenuManager manager, CachedBaseNode selectedNode) {
		// TODO: maybe copy & paste
		if (ConstraintChecker.getIntsance().isRemovalAllowed(selectedNode)) {
			manager.add(removeAction);
		}
		if (ConstraintChecker.getIntsance().isAddObjectAllowed(selectedNode)) {
			manager.add(addObject);
		}
		if (ConstraintChecker.getIntsance().isAddMethodAllowed(selectedNode)) {
			manager.add(addMethod);
		}
		if (ConstraintChecker.getIntsance().isAddVariableAllowed(selectedNode)) {
			manager.add(addVariable);
		}
		if (ConstraintChecker.getIntsance().isAddPropertyAllowed(selectedNode)) {
			manager.add(addProperty);
		}
		if (ConstraintChecker.getIntsance().isAddDataTypeAllowed(selectedNode)) {
			manager.add(addDataType);
		}
		if (ConstraintChecker.getIntsance().isAddObjectTypeAllowed(selectedNode)) {
			manager.add(addObjectType);
		}
		if (ConstraintChecker.getIntsance().isAddVariableTypeAllowed(selectedNode)) {
			manager.add(addVariableType);
		}
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(openImportWizard);
		manager.add(openExportWizard);
		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
		manager.add(new Separator());
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof CachedBaseNode) {
			addAvailableEditOptions(manager, (CachedBaseNode) obj);
		}
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(openImportWizard);
		manager.add(openExportWizard);
		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
		manager.add(new Separator());
	}

	/** can be called, when the import wizard is started */
	public void onWizardOpen() {
		cachedRoot = NodeNavigator.getInstance().getRoot();
		NodeNavigator.getInstance().setRoot(CachedBaseNode.getDummyLoading());
		viewer.refresh();
	}

	/** can be called, when the import wizard had been canceled */
	public void onWizardCancel() {
		NodeNavigator.getInstance().setRoot(cachedRoot);
		viewer.refresh();
		expandToDefaultState();
	}

	/** can be called, after the export wizard has finished */
	public void onExportWizardFinish(String exportUrl) {
		Job job = new Job("Exporting OPC UA nodes...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					XmlExport.getInstance().writeToFile(exportUrl, NodeNavigator.getInstance().getRoot());
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(new Shell(), "OPC UA Display", e.getLocalizedMessage());
						}
					});
					return Status.CANCEL_STATUS;
				}
			}
		};

		job.setUser(true);
		job.schedule();
	}

	/** can be called, after the new project wizard has finished */
	private void onNewProjectWizardFinish(boolean generateFolders, boolean generateBaseTypes) {
		Job job = new Job("Generating OPC UA nodes...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					if (!generateFolders) {
						NodeGenerator.getInstance().generateRoot();

					} else {
						if (!generateBaseTypes) {
							NodeGenerator.getInstance().generateFolders();
						} else {
							NodeGenerator.getInstance().generateBaseTypes();
						}
					}

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							EventBus.getInstance()
									.fireEvent(new RootUpdatedEvent(NodeNavigator.getInstance().getRoot()));
						}
					});
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(new Shell(), "OPC UA Display", e.getLocalizedMessage());
						}
					});
					return Status.CANCEL_STATUS;
				}
			}
		};

		job.setUser(true);
		job.schedule();
	}

	/** can be called, after the import wizard has finished */
	public void onImportWizardFinish(String importUrl, boolean server, final boolean baseDataTypesImplicit) {
		if (!server) {
			Job job = new Job("Importing OPC UA nodes...") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						NodeNavigator.getInstance().setRoot(XmlImport.getInstance().parseFile(importUrl, baseDataTypesImplicit));

						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								EventBus.getInstance()
										.fireEvent(new RootUpdatedEvent(NodeNavigator.getInstance().getRoot()));
							}
						});
						return Status.OK_STATUS;
					} catch (Exception e) {
						e.printStackTrace();
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(new Shell(), "OPC UA Display", e.getLocalizedMessage());
							}
						});
						return Status.CANCEL_STATUS;
					}
				}
			};

			job.setUser(true);
			job.schedule();
		}

		else {
			EventBus.getInstance().fireEvent(new SelectedNodeChangedEvent(null));
			connector.loadVariables(importUrl);
			// this will call an event which will then be caught above
		}
	}

	private void expandToDefaultState() {
		viewer.setExpandedElements(NodeNavigator.getInstance().getRoot().getChildren());
		viewer.setExpandedState(NodeNavigator.getInstance().getRoot(), true);
	}

	private void onExplorerDoubleClick() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof CachedBaseNode) {
			if (((CachedBaseNode) obj).isDummy()) {
				// open import wizard
				new WizardDialog(new Shell(), new OpcUaImportWizard()).open();
			} else {
				EventBus.getInstance().fireEvent(new SelectedNodeChangedEvent((CachedBaseNode) obj));
				viewer.setExpandedState(obj, !viewer.getExpandedState(obj));
			}
		}
	}

	private void openAddDialog(AddDialogType type) {
		DialogFactory.getInstance().createAddDialog(type,
				(CachedBaseNode) (((IStructuredSelection) viewer.getSelection()).getFirstElement())).open();
	}

	private void makeEditActions() {
		// remove node action
		removeAction = new Action() {
			public void run() {
				NodeGenerator.getInstance()
						.removeNode((CachedBaseNode) ((IStructuredSelection) viewer.getSelection()).getFirstElement());
			}
		};
		removeAction.setText("Delete Node");
		removeAction.setToolTipText("Deletes a Node.");
		removeAction.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.REMOVE.getIdentifier()));

		// add variable action
		addVariable = new Action() {
			public void run() {
				openAddDialog(AddDialogType.VARIABLE);
			}
		};
		addVariable.setText("Add Variable");
		addVariable.setToolTipText("Add Variable");
		addVariable.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.VARIABLE.getIdentifier()));

		// add method action
		addMethod = new Action() {
			public void run() {
				openAddDialog(AddDialogType.METHOD);
			}
		};
		addMethod.setText("Add Method");
		addMethod.setToolTipText("Add Method");
		addMethod.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.METHOD.getIdentifier()));

		// add object action
		addObject = new Action() {
			public void run() {
				openAddDialog(AddDialogType.OBJECT);
			}
		};
		addObject.setText("Add Object");
		addObject.setToolTipText("Add Object");
		addObject.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.OBJECT.getIdentifier()));

		// add Property action
		addProperty = new Action() {
			public void run() {
				openAddDialog(AddDialogType.PROPERTY);
			}
		};
		addProperty.setText("Add Property");
		addProperty.setToolTipText("Add Property");
		addProperty.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.PROPERTY.getIdentifier()));

		// add ObjectType action
		addObjectType = new Action() {
			public void run() {
				openAddDialog(AddDialogType.OBJECT_TYPE);
			}
		};
		addObjectType.setText("Add ObjectType");
		addObjectType.setToolTipText("Add ObjectType");
		addObjectType.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.OBJECT_TYPE.getIdentifier()));

		// add VariableType action
		addVariableType = new Action() {
			public void run() {
				openAddDialog(AddDialogType.VARIABLE_TYPE);
			}
		};
		addVariableType.setText("Add VariableType");
		addVariableType.setToolTipText("Add VariableType");
		addVariableType
				.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.VARIABLE_TYPE.getIdentifier()));

		// add DataType action
		addDataType = new Action() {
			public void run() {
				openAddDialog(AddDialogType.DATA_TYPE);
			}
		};
		addDataType.setText("Add DataType");
		addDataType.setToolTipText("Add DataType");
		addDataType.setImageDescriptor(Activator.getImageDescriptor(Images.ExplorerView.DATA_TYPE.getIdentifier()));
	}

	private void makeActions() {
		makeEditActions();

		// open import wizard
		openImportWizard = new Action() {
			public void run() {
				new WizardDialog(new Shell(), new OpcUaImportWizard()).open();
			}
		};
		openImportWizard.setText("Import OPC UA Model...");
		openImportWizard.setToolTipText("Import OPC UA Model...");
		openImportWizard.setImageDescriptor(Activator.getImageDescriptor(Images.IMG_IMPORT.getIdentifier()));

		// open export wizard
		openExportWizard = new Action() {
			public void run() {
				new WizardDialog(new Shell(), new OpcUaExportWizard()).open();
			}
		};
		openExportWizard.setText("Export OPC UA Model...");
		openExportWizard.setToolTipText("Export OPC UA Model...");
		openExportWizard.setImageDescriptor(Activator.getImageDescriptor(Images.IMG_EXPORT.getIdentifier()));

		// double click action
		doubleClickAction = new Action() {
			public void run() {
				onExplorerDoubleClick();
			}
		};

		// click action
		selectionChangedAction = new Action() {
			public void run() {
				// translate the event to update the attribute view.
				Object obj = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
				EventBus.getInstance().fireEvent(new SelectedNodeChangedEvent((CachedBaseNode) obj));

			}
		};

		// collapse all action
		collapseAllAction = new Action() {
			public void run() {
				viewer.collapseAll();
				expandToDefaultState();
			}
		};
		collapseAllAction.setText("Collapse All");
		collapseAllAction.setToolTipText("Collapse All");
		collapseAllAction.setImageDescriptor(Activator.getImageDescriptor(Images.IMG_COLLAPSE_ALL.getIdentifier()));

		// expand all action
		expandAllAction = new Action() {
			public void run() {
				viewer.expandAll();
			}
		};
		expandAllAction.setText("Expand All");
		expandAllAction.setToolTipText("Expand All");
		expandAllAction.setImageDescriptor(Activator.getImageDescriptor(Images.IMG_EXPAND_ALL.getIdentifier()));
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
