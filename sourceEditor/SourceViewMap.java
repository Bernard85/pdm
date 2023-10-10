package sourceEditor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import sourceViewer.IModifierNotifier;

public class SourceViewMap {
	public static final String SOURCE_VIEW_MAP = "SOURCE_VIEW_MAP";
	public Element eStudy;
	public Document dView;
	public Element eSelected, eClicked;
	public String buffer;
	public String fileName;
	public IModifierNotifier modifierNotifier;
}
