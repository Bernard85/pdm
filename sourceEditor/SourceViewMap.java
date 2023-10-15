package sourceEditor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.custom.StyledText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sourceViewer.IModifierNotifier;

public class SourceViewMap {
	public static final String SOURCE_VIEW_MAP = "SOURCE_VIEW_MAP";
	public Element eStudy;
	public Document dView;
	public Element eSelected, eNewSelected, eClicked;
	public String buffer;
	public String fileName;
	public IModifierNotifier modifierNotifier;
	public List<SourceBlock>sourceBlocks= new ArrayList<SourceBlock>();
	public StyledText styledText;
	
	public void loadSourceBlocks() {
		sourceBlocks.clear();
		loadSourceBlocks2(dView.getFirstChild());
	}

	private void loadSourceBlocks2(Node node) {
		
		if (node.getNodeType()!=Node.ELEMENT_NODE) return;
		
		Element e =(Element) node;
		if (e.hasAttribute("block")) {
			sourceBlocks.add(new SourceBlock(e));
		}
		
		NodeList nl = node.getChildNodes();
		for (int n=0;n<nl.getLength();n++) loadSourceBlocks2(nl.item(n));
		
	}

	public static SourceViewMap getSourceViewMap(Element element) {
		return (SourceViewMap)element.getOwnerDocument().getUserData(SOURCE_VIEW_MAP);
	}
	
	public static SourceViewMap getSourceViewMap(MPart part) {
		return (SourceViewMap)part.getTransientData().get(SOURCE_VIEW_MAP);
	}
	
	public void referencedBy(MPart part) {
		part.getTransientData().put(SOURCE_VIEW_MAP, this);
	}
	public void referencedBy(Document dView) {
		dView.setUserData(SOURCE_VIEW_MAP, this, null);
		this.dView=dView;
	}
}
