import java.net.URL;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
 
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
   
public class RSSReader {
    
    	private static RSSReader instance = null;
		
	private RSSReader() {}
					
	public static RSSReader getInstance() {
		if(instance == null) {
			instance = new RSSReader();	
		}
		return instance;
	}
																	
	public void writeNews() {
		try {
																 
 			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			URL u = new URL("http://www.amazon.com/rss/tag/blu-ray/new/ref=tag_rsh_hl_ersn");
	
			Document doc = builder.parse(u.openStream());
						
			NodeList nodes = doc.getElementsByTagName("item");
			for(int i=0;i<nodes.getLength();i++) {
																				
				Element element = (Element)nodes.item(i);


				System.out.println("Title: " + getElementValue(element,"title"));
				System.out.println("Link: " + getElementValue(element,"link"));
				System.out.println("Publish Date: " + getElementValue(element,"pubDate"));
				System.out.println("Yazar: " + getElementValue(element,"dc:creator"));
				System.out.println("Yorumlar: " + getElementValue(element,"wfw:comment"));
				System.out.println("Tanim: " + getElementValue(element,"description"));
				System.out.println();
			}//for			
		}//try
											catch(Exception ex) {
													ex.printStackTrace();	
															}
																	
																		}
																				
																						
																								private String getCharacterDataFromElement(Element e) {
																											try {
			Node child = e.getFirstChild();
							if(child instanceof CharacterData) {
												CharacterData cd = (CharacterData) child;
																	return cd.getData();
																					}
																								}
																											catch(Exception ex) {
			
						}
									return "";			
											} //private String getCharacterDataFromElement
													
															protected float getFloat(String value) {
																		if(value != null && !value.equals("")) {
																						return Float.parseFloat(value);	
																									}
return 0;
		}
				
						protected String getElementValue(Element parent,String label) {
									return getCharacterDataFromElement((Element)parent.getElementsByTagName(label).item(0));	
											}
													
															public static void main(String[] args) {
																	RSSReader reader = RSSReader.getInstance();
																			reader.writeNews();
																					}
																					}

