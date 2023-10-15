package sourceViewer;

import java.io.File;
import java.io.FileOutputStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sourceEditor.SourceBlock;
import sourceEditor.SourceViewMap;

public class SourceViewerSetUp {
	@Inject MApplication mApplication;
	@Inject IEclipseContext iEclipseContext;
	@Inject EModelService eModelService;
	@Inject MDirtyable dirty;
	@Inject EMenuService eMenuService;
	Document dView=null;

	String xmlID;
	@PostConstruct public void postConstruct(MPart part2, Composite parent) {
		// To retrieve the associated SourceViewMap
		MPart part1	= (MPart) eModelService.find(part2.getElementId(), mApplication);
		SourceViewMap sourceViewMap = SourceViewMap.getSourceViewMap(part1);
		sourceViewMap.referencedBy(part2);
		
		
		int index = sourceViewMap.fileName.lastIndexOf(".");
		xmlID=sourceViewMap.fileName.substring(0, index)+".xml";

		try {
			dView = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xmlID));
		} catch (Throwable e) {
			System.out.println(xmlID+" loading failure");
			return;
		}
		sourceViewMap.referencedBy(dView);

		Element eView = dView.getDocumentElement();

		sourceViewMap.modifierNotifier=new IModifierNotifier() {
			@Override
			public void setDirty(boolean status) {
				dirty.setDirty(status);
			}
		};

		dView.setUserData("EMenuService", eMenuService, null);

		Clauses clauses=new Clauses(parent,eView);

		sourceViewMap.loadSourceBlocks();
		
		FormData fd=new FormData();
		fd.left=new FormAttachment(0);		
		fd.top=new FormAttachment(0);
		fd.right=new FormAttachment(100);		
		fd.bottom=new FormAttachment(100);
		clauses.setLayoutData(fd);
		clauses.layout();

	}


	@Persist void saveIt() {
		try {
			killCR(dView.getDocumentElement(), 0);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "1");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(new DOMSource(dView), new StreamResult(new FileOutputStream(xmlID)));
			dirty.setDirty(false);
		} catch (Throwable t) {
			System.out.println("save xml failure");
		}	
	}

	private void killCR(Node parentNode, int no) {
		((Element)parentNode).setAttribute("NO", Integer.toString(no));
		NodeList nl= parentNode.getChildNodes();
		for (int n=0;n<nl.getLength();) {
			Node node = nl.item(n);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				killCR(node, no+n+1);
				n++;}
			else parentNode.removeChild(node);
		}
	}

}
