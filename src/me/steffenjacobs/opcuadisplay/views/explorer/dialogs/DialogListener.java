package me.steffenjacobs.opcuadisplay.views.explorer.dialogs;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;

public interface DialogListener {

	void onOk(int namespace, String name, int nodeId, CachedBaseNode type);

}
