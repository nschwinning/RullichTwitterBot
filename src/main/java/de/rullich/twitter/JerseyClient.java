package de.rullich.twitter;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JerseyClient {

	public List<String> getTitles() {
		List<String> titles = new ArrayList<String>();
		
		try {

			Client client = Client.create();

			WebResource webResource = client.resource("https://www.derwesten.de/staedte/essen/rss");

			ClientResponse response = webResource.accept("application/xml").get(ClientResponse.class);

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}

			String output = response.getEntity(String.class);
			
			//System.out.println(output);

			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		            .parse(new InputSource(new StringReader(output)));
			NodeList itemNodes = doc.getElementsByTagName("item");
			for (int i=0; i<itemNodes.getLength();i++){
				Node node = itemNodes.item(i);
				NodeList children = node.getChildNodes();
				for (int j=0;j<children.getLength();j++){
					if (children.item(j).getNodeName().equals("title")){
						titles.add(children.item(j).getTextContent());
					}
				}
			}

		} catch (Exception e) {

			e.printStackTrace();

		}

		return titles;

	}

}
