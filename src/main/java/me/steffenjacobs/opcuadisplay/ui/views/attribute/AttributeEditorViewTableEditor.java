package me.steffenjacobs.opcuadisplay.ui.views.attribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
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

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.management.node.domain.BetterValueRank;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedViewNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.HasOnlyAbstract;
import me.steffenjacobs.opcuadisplay.management.node.domain.HasValueRank;
import me.steffenjacobs.opcuadisplay.ui.views.attribute.domain.NodeEntryFactory.NodeEntry;
import me.steffenjacobs.opcuadisplay.ui.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.ui.widgets.DropDownCheckedListBox;
import me.steffenjacobs.opcuadisplay.ui.widgets.GenericComboBox;
import me.steffenjacobs.opcuadisplay.ui.widgets.GenericComboBox.Renderer;

/**
 * The table editor associated with the OPC UA Attribute View
 * 
 * @author Steffen Jacobs
 */
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

					switch (entry.getText()) {
					case "BrowseName":
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
						break;
					case "NodeId":
						break;
					case "DataType":
						Consumer<String> setter2 = new Consumer<String>() {
							@Override
							public void accept(String t) {
								((CachedVariableNode) entry.getCachedNode()).setDataType(NodeId.parse(t));
							}
						};

						Supplier<Object> getter2 = new Supplier<Object>() {
							@Override
							public Object get() {
								return ((CachedVariableNode) entry.getCachedNode()).getDataType();
							}
						};
						textEditor(table, entry, editableColumn, item, setter2, getter2);
						break;
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
						textEditor(table, entry, editableColumn, item, setter, getter);
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
						textEditor(table, entry, editableColumn, item, setter, getter);
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
						textEditor(table, entry, editableColumn, item, setter, getter);
						break;
					case "NodeClass":
						nodeclassEditor(table, entry, editableColumn, item);
						break;
					case "UserWriteMask":
						Consumer<UInteger> setter6 = new Consumer<UInteger>() {

							@Override
							public void accept(UInteger t) {
								entry.getCachedNode().setUserWriteMask(t);
							}
						};
						uintegerEditor(table, entry, editableColumn, item, setter6);
						break;
					case "WriteMask":
						Consumer<UInteger> setter7 = new Consumer<UInteger>() {

							@Override
							public void accept(UInteger t) {
								entry.getCachedNode().setWriteMask(t);
							}
						};
						uintegerEditor(table, entry, editableColumn, item, setter7);
						break;
					case "ValueRank":
						valueRankEditor(table, entry, editableColumn, item);
						break;

					case "MinimumSamplingInterval":
						Consumer<String> setter8 = new Consumer<String>() {
							@Override
							public void accept(String t) {
								((CachedVariableNode) entry.getCachedNode())
										.setMinimumSamplingInterval(Double.parseDouble(t));
							}
						};

						Supplier<Object> getter8 = new Supplier<Object>() {

							@Override
							public Object get() {
								return ((CachedVariableNode) entry.getCachedNode()).getMinimumSamplingInterval();
							}
						};
						textEditor(table, entry, editableColumn, item, setter8, getter8);
						break;
					case "Value":
						handleValue(table, entry, editableColumn, item);
						break;
					case "Historizing":
						Consumer<Boolean> setter9 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								((CachedVariableNode) entry.getCachedNode()).setHistorizing(t);
							}
						};
						booleanEditor(table, entry, editableColumn, item, setter9);
						break;
					case "Executable":
						Consumer<Boolean> setter10 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								((CachedMethodNode) entry.getCachedNode()).setExecutable(t);
							}
						};
						booleanEditor(table, entry, editableColumn, item, setter10);
						break;
					case "UserExecutable":
						Consumer<Boolean> setter11 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								((CachedMethodNode) entry.getCachedNode()).setUserExecutable(t);
							}
						};
						booleanEditor(table, entry, editableColumn, item, setter11);
						break;
					case "Symmetric":
						Consumer<Boolean> setter12 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								((CachedReferenceTypeNode) entry.getCachedNode()).setSymmetric(t);
							}
						};
						booleanEditor(table, entry, editableColumn, item, setter12);
						break;
					case "IsAbstract":
						Consumer<Boolean> setter13 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								if (entry.getCachedNode().getClass() == CachedReferenceTypeNode.class) {
									((CachedReferenceTypeNode) entry.getCachedNode()).setAbstract(t);
								} else {
									((HasOnlyAbstract) entry.getCachedNode()).setAbstract(t);
								}
							}
						};
						booleanEditor(table, entry, editableColumn, item, setter13);
						break;
					case "ContainsNoLoops":
						Consumer<Boolean> setter14 = new Consumer<Boolean>() {
							@Override
							public void accept(Boolean t) {
								((CachedViewNode) entry.getCachedNode()).setContainsNoLoop(t);

							}
						};
						booleanEditor(table, entry, editableColumn, item, setter14);
						break;
					}
				}
			}

			private void handleValue(Table table, NodeEntry<Object> entry, int editableColumn, TableItem item) {
				Supplier<Object> getter = new Supplier<Object>() {

					@Override
					public Object get() {
						return ((CachedVariableNode) entry.getCachedNode()).getValue();
					}
				};

				NodeId datatype = ((CachedVariableNode) entry.getCachedNode()).getDataType();

				if (new NodeId(0, 12).equals(datatype)) {
					// String
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(t);
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 10).equals(datatype)) {
					// float
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(Float.parseFloat(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 11).equals(datatype)) {
					// double
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(Double.parseDouble(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 27).equals(datatype) || new NodeId(0, 4).equals(datatype)
						|| new NodeId(0, 6).equals(datatype)) {
					// integer / Byte/Int16/32
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(Integer.parseInt(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 2).equals(datatype)) {
					// sbyte
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(Byte.parseByte(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 8).equals(datatype)) {
					// int64
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(Long.parseLong(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 28).equals(datatype) || new NodeId(0, 3).equals(datatype)
						|| new NodeId(0, 5).equals(datatype) || new NodeId(0, 7).equals(datatype)
						|| new NodeId(0, 9).equals(datatype)) {
					// UInteger /Byte/UInteger16/32/64
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(UInteger.valueOf(t));
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
				} else if (new NodeId(0, 1).equals(datatype)) {
					// boolean
					Consumer<Boolean> setter = new Consumer<Boolean>() {
						@Override
						public void accept(Boolean t) {
							((CachedVariableNode) entry.getCachedNode()).setValue(t);
						}
					};
					booleanEditor(table, entry, editableColumn, item, setter);
				} else if (new NodeId(0, 13).equals(datatype)) {
					// date time
					Consumer<String> setter = new Consumer<String>() {
						@Override
						public void accept(String t) {
							SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd hh/mm/ss/SSS Z");

							try {
								((CachedVariableNode) entry.getCachedNode()).setValue(new DateTime(sdf.parse(t)));
							} catch (ParseException e) {
								Activator.openMessageBoxError("Error",
										"Invalid date. Please use format 'YYYY/MM/dd hh/mm/ss/SSS Z'.");
							}
						}
					};
					textEditor(table, entry, editableColumn, item, setter, getter);
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

	/** editor for editing strings */
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

	/** editor for editing the node class */
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

	/** editor for editing boolean variables */
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

	/** editor for editing UInteger values */
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

	/** editor for editing the value rank */
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
