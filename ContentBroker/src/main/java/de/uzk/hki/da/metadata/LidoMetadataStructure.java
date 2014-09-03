package de.uzk.hki.da.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.input.BOMInputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uzk.hki.da.utils.XMLUtils;


public class LidoMetadataStructure extends MetadataStructure{
	
	private static final Namespace LIDO_NS = Namespace.getNamespace("http://www.lido-schema.org");
	private Document doc;
	private List<Element> lidoLinkResources;
	private File currentMetadataFile;
	
	public LidoMetadataStructure(File metadataFile) throws FileNotFoundException, JDOMException,
			IOException {
		super(metadataFile);
		
		currentMetadataFile = metadataFile;
		
		SAXBuilder builder = XMLUtils.createNonvalidatingSaxBuilder();
		FileInputStream fileInputStream = new FileInputStream(metadataFile);
		BOMInputStream bomInputStream = new BOMInputStream(fileInputStream);
		
		doc = builder.build(bomInputStream);
		lidoLinkResources = parseLinkResourceElements();
	}
	
	public List<String> getLidoLinkResources() {
		List<String> linkResources = new ArrayList<String>();
		for(Element element : lidoLinkResources) {
			linkResources.add(element.getValue());
		}
		return linkResources;
	}
	
	public List<Element> parseLinkResourceElements() {
		
		List<Element> currentLinkResources = new ArrayList<Element>();
		
		@SuppressWarnings("unchecked")
		List<Element> lidoElements = doc.getRootElement().getChildren();
		for(Element element : lidoElements) {
			if(element.getName().equalsIgnoreCase("lido")) {
				Element currentLinkResource = element
				.getChild("administrativeMetadata", LIDO_NS)
				.getChild("resourceWrap", LIDO_NS)
				.getChild("resourceSet", LIDO_NS)
				.getChild("resourceRepresentation", LIDO_NS)
				.getChild("linkResource", LIDO_NS);
				currentLinkResources.add(currentLinkResource);
			}
		}
		return currentLinkResources;
	}
	
	public void replaceRefResources(HashMap<String, String> linkResourceReplacements) throws IOException {
		
		for(String sourceLinkResource : linkResourceReplacements.keySet()) {
			for(int i=0; i<lidoLinkResources.size(); i++) {
				if(sourceLinkResource.equals(lidoLinkResources.get(i).getValue())) {
					lidoLinkResources.get(i).setText(linkResourceReplacements.get(sourceLinkResource));
				}
			}
		}
		
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(doc, new FileWriter(currentMetadataFile));
	}

	@Override
	public boolean isValid() {
		return isValid;
	}

}
