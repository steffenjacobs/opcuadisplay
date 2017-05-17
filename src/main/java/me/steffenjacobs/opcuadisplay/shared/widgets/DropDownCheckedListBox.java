package me.steffenjacobs.opcuadisplay.shared.widgets;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox.Renderer;
/** @author Steffen Jacobs */
public class DropDownCheckedListBox extends Composite {

	private static final int LINE_WIDTH = 120;
	private static final int LINE_HEIGHT = 23;

	private Text textbox;
	private Menu popupMenu;
	private Renderer<Map<String, Boolean>> renderer;
	private SelectionListener listener;

	public DropDownCheckedListBox(Composite container) {
		super(container, SWT.NONE);

		textbox = new Text(this, SWT.BORDER_SOLID);
		textbox.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				getPopup().setVisible(!getPopup().isVisible());
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});
		textbox.setEditable(false);

		this.setLayout(new FillLayout());
	}

	private void render() {
		checkWidget();
		if (renderer != null) {
			textbox.setText(renderer.render(getMenuValues()));
		}
		if (listener != null) {
			listener.widgetSelected(null);
		}
	}

	public DropDownCheckedListBox setMenuValues(Map<String, Boolean> data) {
		checkWidget();
		popupMenu = new Menu(textbox);
		data.forEach((k, v) -> {
			MenuItem item = new MenuItem(popupMenu, SWT.CHECK);
			item.setText(k);
			item.setSelection(v);
			item.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent arg0) {
					render();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent arg0) {
				}
			});
		});

		textbox.setMenu(popupMenu);
		render();
		return this;
	}

	public DropDownCheckedListBox setSelectionListener(SelectionListener listener) {
		checkWidget();
		this.listener = listener;
		return this;
	}

	public DropDownCheckedListBox setRenderer(Renderer<Map<String, Boolean>> renderer) {
		checkWidget();
		this.renderer = renderer;
		render();
		return this;
	}

	private Map<String, Boolean> getMenuValues() {
		if (popupMenu == null) {
			return new LinkedHashMap<>();
		}
		return Arrays.stream(popupMenu.getItems()).collect(Collectors.toMap(MenuItem::getText, MenuItem::getSelection));
	}

	private Menu getPopup() {
		checkWidget();
		return popupMenu;
	}

	public String getText() {
		checkWidget();
		return textbox.getText();
	}

	@Override
	public Point computeSize(int wHint, int hHint, boolean changed) {
		int width = wHint != SWT.DEFAULT ? Math.min(wHint, LINE_WIDTH) : LINE_WIDTH;
		int height = hHint != SWT.DEFAULT ? Math.min(hHint, LINE_HEIGHT) : LINE_HEIGHT;

		return new Point(width, height);
	}
}
