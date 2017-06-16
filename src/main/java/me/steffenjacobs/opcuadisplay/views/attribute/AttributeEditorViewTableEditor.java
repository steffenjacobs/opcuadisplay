package me.steffenjacobs.opcuadisplay.views.attribute;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.shared.domain.BetterValueRank;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.HasOnlyAbstract;
import me.steffenjacobs.opcuadisplay.shared.domain.HasValueRank;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.shared.widgets.DropDownCheckedListBox;
import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox;
import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox.Renderer;
import me.steffenjacobs.opcuadisplay.views.attribute.domain.NodeEntryFactory.NodeEntry;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;
/** @author Steffen Jacobs */
public class AttributeEditorViewTableEditor {
	private TableEditor editor;

	public AttributeEditorViewTableEditor(Table table, TableViewer viewer) {
		editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int editableColumn = 1;

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent arg0) {

				clearTableEditor();

				// Identify the selected row
				TableItem item = table.getSelection()[0];
				if (item == null) {
					return;
				}

				// add new editor
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();

				if (obj instanceof NodeEntry) {
					@SuppressWarnings("unchecked")
					final NodeEntry<Object> entry = (NodeEntry<Object>) obj;

					// Qualified name -> BrowseName
					if (entry.getValue().getClass() == QualifiedName.class) {
						Consumer<String> setter = new Consumer<String>() {

							@Override
							public void accept(String t) {
								entry.getCachedNode().setBrowseName(QualifiedName.parse(t));
							}
						};

						Supplier<Object> getter = new Supplier<Object>() {
							@Override
							public Object get() {
								return entry.getCachedNode().getBrowseName();
							}
						};
						textEditor(table, entry, editableColumn, item, setter, getter);
					}

					// NodeId -> NodeId, DataType
					else if (entry.getValue().getClass() == NodeId.class) {
						switch (entry.getText()) {
						case "NodeId":
							// no editor for NodeId of BaseNode
							break;
						case "DataType":
							Consumer<String> setter = new Consumer<String>() {
								@Override
								public void accept(String t) {
									((CachedVariableNode) entry.getCachedNode()).setDataType(NodeId.parse(t));
								}
							};

							Supplier<Object> getter = new Supplier<Object>() {
								@Override
								public Object get() {
									return ((CachedVariableNode) entry.getCachedNode()).getDataType();
								}
							};
							textEditor(table, entry, editableColumn, item, setter, getter);
							break;
						}
					}

					// Localized Text -> DisplayName, Description, InverseName
					else if (entry.getValue().getClass() == LocalizedText.class) {

						Consumer<String> setter = null;
						Supplier<Object> getter = null;
						switch (entry.getText()) {
						case "Description":
							setter = new Consumer<String>() {
								@Override
								public void accept(String t) {
									entry.getCachedNode().setDescription(AttributeValueParser.parseLocalizedText(t));
								}
							};

							getter = new Supplier<Object>() {
								@Override
								public Object get() {
									return entry.getCachedNode().getDescription();
								}
							};
							break;
						case "DisplayName":
							setter = new Consumer<String>() {
								@Override
								public void accept(String t) {
									entry.getCachedNode().setDisplayName(AttributeValueParser.parseLocalizedText(t));
									EventBus.getInstance().fireEvent(new AttributeModifiedEvent(entry.getCachedNode()));
								}
							};

							getter = new Supplier<Object>() {
								@Override
								public Object get() {
									return entry.getCachedNode().getDisplayName();
								}
							};
							break;

						case "InverseName":
							setter = new Consumer<String>() {
								@Override
								public void accept(String t) {
									((CachedReferenceTypeNode) entry.getCachedNode())
											.setInverseName(AttributeValueParser.parseLocalizedText(t));
								}
							};

							getter = new Supplier<Object>() {
								@Override
								public Object get() {
									return ((CachedReferenceTypeNode) entry.getCachedNode()).getInverseName();
								}
							};
							break;
						}

						textEditor(table, entry, editableColumn, item, setter, getter);

					}
					// NodeClass -> NodeClass
					else if (entry.getValue().getClass() == NodeClass.class) {
						nodeclassEditor(table, entry, editableColumn, item);
					}
					// UInteger -> WriteMask, UserWriteMask,
					else if (entry.getValue().getClass() == UInteger.class) {
						Consumer<UInteger> setter = null;

						switch (entry.getText()) {
						case "UserWriteMask":
							setter = new Consumer<UInteger>() {

								@Override
								public void accept(UInteger t) {
									entry.getCachedNode().setUserWriteMask(t);
								}
							};
							break;
						case "WriteMask":
							setter = new Consumer<UInteger>() {

								@Override
								public void accept(UInteger t) {
									entry.getCachedNode().setWriteMask(t);
								}
							};
							break;
						}
						uintegerEditor(table, entry, editableColumn, item, setter);
					}
					// Integer -> ValueRank (Variable, VariableType)
					else if (entry.getValue().getClass() == Integer.class) {
						switch (entry.getText()) {
						case "ValueRank":
							valueRankEditor(table, entry, editableColumn, item);
							break;
						}
					}
					// Double -> MinimumSamplingInterval
					else if (entry.getValue().getClass() == Double.class) {
						Consumer<String> setter = null;
						Supplier<Object> getter = null;

						switch (entry.getText()) {
						case "MinimumSamplingInterval":
							setter = new Consumer<String>() {
								@Override
								public void accept(String t) {
									((CachedVariableNode) entry.getCachedNode())
											.setMinimumSamplingInterval(Double.parseDouble(t));
								}
							};

							getter = new Supplier<Object>() {

								@Override
								public Object get() {
									return ((CachedVariableNode) entry.getCachedNode()).getMinimumSamplingInterval();
								}
							};
							break;
						}
						textEditor(table, entry, editableColumn, item, setter, getter);
					}

					// Boolean -> Historizing, Executable, UserExecutable,
					// IsSymmetric, IsAbstract (ReferenceType, ObjectType,
					// VariableType, DataType), ContainsNoLoops
					else if (entry.getValue().getClass() == Boolean.class) {
						Consumer<Boolean> setter = null;

						// CachedVariableNode -> only boolean is historizing
						if (entry.getCachedNode() instanceof CachedVariableNode) {
							setter = new Consumer<Boolean>() {
								@Override
								public void accept(Boolean t) {
									((CachedVariableNode) entry.getCachedNode()).setHistorizing(t);
								}
							};
						}

						// CachedMethodNode -> boolean can be executable or
						// userExecutable
						else if (entry.getCachedNode() instanceof CachedMethodNode) {
							switch (entry.getText()) {
							case "Executable":
								setter = new Consumer<Boolean>() {
									@Override
									public void accept(Boolean t) {
										((CachedMethodNode) entry.getCachedNode()).setExecutable(t);
									}
								};
								break;
							case "UserExecutable":
								setter = new Consumer<Boolean>() {
									@Override
									public void accept(Boolean t) {
										((CachedMethodNode) entry.getCachedNode()).setUserExecutable(t);
									}
								};
								break;
							}
						}

						// Reference Type --> boolean can be IsAbstract or
						// Symmetric
						else if (entry.getCachedNode() instanceof CachedReferenceTypeNode) {
							switch (entry.getText()) {
							case "IsAbstract":
								setter = new Consumer<Boolean>() {
									@Override
									public void accept(Boolean t) {
										((CachedReferenceTypeNode) entry.getCachedNode()).setAbstract(t);
									}
								};
								break;
							case "Symmetric":
								setter = new Consumer<Boolean>() {
									@Override
									public void accept(Boolean t) {
										((CachedReferenceTypeNode) entry.getCachedNode()).setSymmetric(t);
									}
								};
								break;
							}
						}

						// Data Type, Object Type, VariableType -> only boolean
						// is IsAbstract
						if (entry.getCachedNode() instanceof HasOnlyAbstract) {
							setter = new Consumer<Boolean>() {
								@Override
								public void accept(Boolean t) {
									((HasOnlyAbstract) entry.getCachedNode()).setAbstract(t);
								}
							};
						}
						booleanEditor(table, entry, editableColumn, item, setter);
					}
				}
			}
		});
	}

	public TableEditor getTableEditor() {
		return editor;
	}

	public void clearTableEditor() {
		if (getTableEditor() != null && getTableEditor().getEditor() != null) {
			getTableEditor().getEditor().dispose();
		}
	}

	private void textEditor(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item,
			Consumer<String> setter, Supplier<Object> getter) {
		Text newEditor = new Text(table, SWT.NONE);
		newEditor.setText(item.getText(editableColumn));
		newEditor.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.character == 13) {
					save();
					getTableEditor().getEditor().dispose();
				}
			}

			@Override
			public void keyPressed(KeyEvent arg0) {

			}

			private void save() {
				Text text = (Text) editor.getEditor();
				editor.getItem().setText(editableColumn, text.getText());
				setter.accept(text.getText());
				entry.setValue(getter.get());
			}
		});
		newEditor.selectAll();
		newEditor.setFocus();
		editor.setEditor(newEditor, item, editableColumn);
	}

	private void nodeclassEditor(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item) {
		GenericComboBox<NodeClass> newEditor = new GenericComboBox<NodeClass>(table, SWT.NONE,
				new Renderer<NodeClass>() {

					@Override
					public String render(NodeClass obj) {
						return obj.name();
					}
				});
		newEditor.setItems(NodeClass.values());
		newEditor.setSelected((NodeClass) entry.getValue());
		newEditor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				editor.getItem().setText(editableColumn, newEditor.getSelected().toString());
				entry.setValue(newEditor.getSelected());
				entry.getCachedNode().setNodeClass(newEditor.getSelected());
				EventBus.getInstance().fireEvent(new AttributeModifiedEvent(entry.getCachedNode()));
				editor.getEditor().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		editor.setEditor(newEditor, item, editableColumn);
	}

	private void booleanEditor(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item,
			Consumer<Boolean> setter) {
		GenericComboBox<Boolean> newEditor = new GenericComboBox<Boolean>(table, SWT.NONE, new Renderer<Boolean>() {

			@Override
			public String render(Boolean obj) {
				return (obj != null && obj) ? "true" : "false";
			}
		});

		newEditor.setItems(true, false);
		newEditor.setSelected((Boolean) entry.getValue());
		newEditor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				editor.getItem().setText(editableColumn, newEditor.getSelected().toString());
				entry.setValue(newEditor.getSelected());
				setter.accept(newEditor.getSelected());
				EventBus.getInstance().fireEvent(new AttributeModifiedEvent(entry.getCachedNode()));
				editor.getEditor().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		editor.setEditor(newEditor, item, editableColumn);

	}

	private void uintegerEditor(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item,
			Consumer<UInteger> setter) {
		final DropDownCheckedListBox newEditor = new DropDownCheckedListBox(table)
				.setRenderer(BitmaskParser.getInstance().createRenderer())
				.setMenuValues(BitmaskParser.getInstance().fromBitmask((UInteger) entry.getValue()));

		newEditor.setSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				editor.getItem().setText(editableColumn, newEditor.getText());
				UInteger value = UInteger.valueOf(newEditor.getText());
				entry.setValue(value);
				setter.accept(value);
				editor.getEditor().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		editor.setEditor(newEditor, item, editableColumn);
	}

	private void valueRankEditor(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item) {
		GenericComboBox<BetterValueRank> newEditor = new GenericComboBox<BetterValueRank>(table, SWT.NONE,
				new Renderer<BetterValueRank>() {

					@Override
					public String render(BetterValueRank obj) {
						return obj.name();
					}
				});
		newEditor.setItems(BetterValueRank.values());
		newEditor.setSelected((BetterValueRank) BetterValueRank.valueOf((Integer) entry.getValue()));
		newEditor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				editor.getItem().setText(editableColumn, newEditor.getSelected().toString());
				entry.setValue(newEditor.getSelected());
				((HasValueRank) entry.getCachedNode()).setValueRank(newEditor.getSelected().getValue());
				editor.getEditor().dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		editor.setEditor(newEditor, item, editableColumn);
	}

}
