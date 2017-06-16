package me.steffenjacobs.opcuadisplay.ui.views.reference;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
/** @author Steffen Jacobs */
public class ReferenceValueParser {

	private static final Pattern patternLocale = Pattern.compile("loc=(.*);text=(.*)");

	private static final SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd HH/mm/ss/SSS Z");

	public static LocalizedText parseLocalizedText(String s) {
		Matcher m = patternLocale.matcher(s);
		m.find();
		try {
			return new LocalizedText(m.group(1), m.group(2));
		} catch (IllegalStateException e) {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ICON_ERROR);
			messageBox.setMessage("Bad input format.\n\nFormat:'loc=__;text=______'");
			messageBox.open();
			throw e;
		}
	}

	public static String asString(Object value) {
		if (value == null) {
			return "";
		}

		if (value.getClass() == QualifiedName.class) {
			return ((QualifiedName) value).toParseableString();
		} else if (value.getClass() == NodeId.class) {
			return ((NodeId) value).toParseableString();
		} else if (value.getClass() == LocalizedText.class) {
			LocalizedText t = (LocalizedText) value;
			return "loc=" + t.getLocale() + ";text=" + t.getText();
		} else if (value.getClass() == DateTime.class) {
			DateTime dt = (DateTime) value;
			return sdf.format(dt.getJavaDate());
		}
		// UInteger[] for ArrayDimensions
		else if (value.getClass() == UInteger[].class) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			UInteger[] arr = (UInteger[]) value;
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i].intValue());
				if (i + 1 < arr.length) {
					sb.append(", ");
				}
			}
			sb.append("]");
			return sb.toString();
		}
		// all arrays
		else if (value instanceof Object[]) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			Object[] arr = (Object[]) value;
			for (int i = 0; i < arr.length; i++) {
				sb.append(arr[i]);
				if (i + 1 < arr.length) {
					sb.append(", ");
				}
			}
			sb.append("]");
			return sb.toString();
		}

		return value.toString();
	}
}
