package p.d.m.core;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class Core {
	static Map<String,Image> imageBox = new HashMap<String,Image>();
	static Bundle bundle;

	public static Bundle getBundle() {
		bundle = FrameworkUtil.getBundle(Core.class);
		return bundle;
	}
	
	public static Image getImageFromID(String ID) {

		Image image = imageBox.get(ID);
		
		if (image!=null) return image;
		
		getBundle();
		
		URL url = bundle.getEntry("icons/"+ID+".png");
		ImageDescriptor imageDescriptor= ImageDescriptor.createFromURL(url);
		image = imageDescriptor.createImage();
		
		imageBox.put(ID, image);
		
		return image;
	}
}
