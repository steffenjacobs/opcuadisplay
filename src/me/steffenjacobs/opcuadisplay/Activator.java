package me.steffenjacobs.opcuadisplay;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import me.steffenjacobs.opcuadisplay.shared.util.Images;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "me.steffenjacobs.opcuadisplay"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String identifier) {
		return Activator.getDefault().getImageRegistry().getDescriptor(identifier);
	}
	
	public static Image getImage(String identifier){
		return Activator.getDefault().getImageRegistry().get(identifier);
	}
	
	private void loadImage(Bundle bundle, ImageRegistry registry, String strPath, String strName){
		IPath path = new Path(strPath);
		URL url = FileLocator.find(bundle, path, null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		registry.put(strName, desc);
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		//load base images
		for (Images img : Images.values()) {
			loadImage(bundle, registry, img.getPath(), img.getIdentifier());
		}
		
		//load view images
		for(Images.ExplorerView img : Images.ExplorerView.values()){
			loadImage(bundle, registry, img.getPath(), img.getIdentifier());
		}
	}
}
