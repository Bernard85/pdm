package p.d.m.studies;

import java.io.File;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import p.d.m.core.Core;

public class Studies {
	
	Document dStudies;
	public static TreeViewer tStudies; 
	@Inject ESelectionService eSelectionService;
	@Inject IEclipseContext iEclipseContext;
	@Optional @Named(IServiceConstants.ACTIVE_SELECTION) Element activeSelection;

	@Inject public void Studies(Composite parent) throws Throwable{
		parent.setLayout(new FillLayout());
		
		File file = new File("C:\\studies\\studies.xml");
		
		dStudies = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		
		tStudies= new TreeViewer(parent, SWT.BORDER);

		tStudies.setContentProvider(new ITreeContentProvider() {
			public boolean hasChildren(Object o) {
				Element e = (Element) o;
				
				if (e.hasAttribute("open") && !Boolean.valueOf(e.getAttribute("open"))) return false;
				
				return getChildren(o).length>0;
			}
			public Object[] getElements(Object o) {
				return getChildren(o);
			}
			public Object[] getChildren(Object o) {
				Node node = (Node) o;
				ArrayList<Element>elements = new ArrayList<Element>();
				for (int c=0;c<node.getChildNodes().getLength();c++) {
					Node node2 = node.getChildNodes().item(c);
					if (!(node2 instanceof Element)) continue;
					elements.add((Element) node2);
				}
				return elements.toArray(new Element[elements.size()]);
			}
			public Object getParent(Object o) {
				Element element = (Element) o;
				return element.getParentNode();
			}
		});
		
		tStudies.setLabelProvider(new LabelProvider() {
			public Image getImage(Object o) { 
				Element element = (Element) o;
				String imageID = element.getNodeName();
				return Core.getImageFromID(imageID);
			}
			public String getText(Object o) {
				Element element = (Element) o;
				return element.getAttribute("text");
			}
		});

		tStudies.setInput(dStudies.getDocumentElement());

		tStudies.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				eSelectionService.setSelection(tStudies.getStructuredSelection().getFirstElement());
			}
		});

		openOpened(dStudies.getDocumentElement());

		refreshIt();

	}

	private void openOpened(Node node) {
		if (node.getNodeType() != Node.ELEMENT_NODE) return;
		Element element = (Element) node;
		NodeList nodes =  element.getChildNodes();
		for (int count = 0; count < nodes.getLength(); count++) {
			openOpened(nodes.item(count));
		}
	}

	public static void refreshIt() { 
	TreeItem[] treeItems = tStudies.getTree().getItems();
    for (TreeItem treeItem : treeItems) {
    	tStudies.expandToLevel(treeItem.getData(), TreeViewer.ALL_LEVELS);
    }
    tStudies.refresh();
	}
	
	@PostConstruct	public void registerContextMenu(Composite parent, EMenuService eMenuService) throws Throwable {
		eMenuService.registerContextMenu(tStudies.getControl(),"p.d.m.studiesmenu");
		tStudies.refresh();
	}

}
