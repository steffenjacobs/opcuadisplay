package me.steffenjacobs.opcuadisplay.shared.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * A generic combo box
 * 
 * @author sjacobs
 */
public class GenericComboBox<T> extends Composite {

	public static interface Renderer<S> {
		String render(S obj);
	}

	private final Combo innerCombo;
	private final Renderer<T> renderer;
	private final List<T> itemList = new ArrayList<>();

	private static final int LINE_WIDTH = 120;
	private static final int LINE_HEIGHT = 23;

	public GenericComboBox(Composite parent, int style, Renderer<T> displayRenderer) {
		super(parent, SWT.NONE);
		this.innerCombo = new Combo(this, SWT.NONE);
		this.renderer = displayRenderer;

		this.setLayout(new FillLayout());
	}

	/** selects the corresponding item in the combobox */
	public void setSelected(T selected) {
		checkWidget();
		innerCombo.select(itemList.indexOf(selected));
	}

	/** @return the selected item */
	public T getSelected() {
		checkWidget();
		return itemList.get(innerCombo.getSelectionIndex());
	}

	/** enables and disables the widget */
	@Override
	public void setEnabled(boolean enabled) {
		checkWidget();
		innerCombo.setEnabled(enabled);
	}

	/**
	 * listener will be called, when the selected value changed or the combobox
	 * is double clicked with the mouse
	 */
	public void addSelectionListener(SelectionListener listener) {
		this.innerCombo.addSelectionListener(listener);
		this.innerCombo.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				listener.widgetSelected(null);
			}
		});
	}

	/** set the items in the combo box */
	@SafeVarargs
	public final void setItems(T... items) {
		checkWidget();
		itemList.clear();
		List<String> names = new ArrayList<>();
		for (T item : items) {
			itemList.add(item);
			names.add(renderer.render(item));
		}
		String[] arrNames = new String[names.size()];
		names.toArray(arrNames);

		innerCombo.setItems(arrNames);
	}

	/** @return the size of the widget */
	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = wHint != SWT.DEFAULT ? Math.min(wHint, LINE_WIDTH) : LINE_WIDTH;
		int height = hHint != SWT.DEFAULT ? Math.min(hHint, LINE_HEIGHT) : LINE_HEIGHT;

		return new Point(width, height);
	}
}
