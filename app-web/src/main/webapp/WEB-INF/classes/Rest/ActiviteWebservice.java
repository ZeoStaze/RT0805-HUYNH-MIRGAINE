package Rest;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bson.types.ObjectId;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;

@Path("/activite")
public class ActiviteWebservice {

	@POST
	public String get(String request) throws UnknownHostException {
		String response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible.</error></response>";
		try {
			// Création du XML depuis la requête
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			Document d = constructeur.parse(new InputSource(new StringReader(request)));
			Element e = d.getDocumentElement();
			String utilisateur = e.getElementsByTagName("identifiant").item(0).getTextContent().replace("\n", "")
					.replace("\r", "").replace(" ", "");

			MongoClient mongoClient = new MongoClient();
			DB database = mongoClient.getDB("RT0805");
			DBCollection collection = database.getCollection("Activites");

			DBObject query = new BasicDBObject("utilisateur", utilisateur);
			DBCursor cursor = collection.find(query);

			// Création du XML de réponse
			Document activiteListeDocument = constructeur.newDocument();
			Element racine = activiteListeDocument.createElement("listeActivites");
			activiteListeDocument.appendChild(racine);

			// Element test = activiteListeDocument.createElement("titi");
			// test.appendChild(activiteListeDocument.createTextNode(String.valueOf(cursor.count())));
			// racine.appendChild(test);

			// Itération sur chacun des résultats
			try {
				while (cursor.hasNext()) {
					DBObject currentActiviteDb = cursor.next();
					Element currentActiviteElement = (Element) activiteListeDocument.createElement("activite");
					racine.appendChild(currentActiviteElement);

					// Ajout de l'id
					Attr idAttr = activiteListeDocument.createAttribute("id");
					idAttr.setValue(((ObjectId) currentActiviteDb.get("_id")).toHexString());
					currentActiviteElement.setAttributeNodeNS(idAttr);

					// Ajout de l'utilisateur
					Element utilisateurElement = activiteListeDocument.createElement("utilisateur");
					utilisateurElement
							.appendChild(activiteListeDocument.createTextNode((String) currentActiviteDb.get("utilisateur")));
					currentActiviteElement.appendChild(utilisateurElement);

					// Ajout du sport
					Element sportElement = activiteListeDocument.createElement("sport");
					sportElement.appendChild(activiteListeDocument.createTextNode((String) currentActiviteDb.get("sport")));
					currentActiviteElement.appendChild(sportElement);

					// Ajout de la date de création
					Element dateCreationElement = activiteListeDocument.createElement("dateCreation");
					dateCreationElement
							.appendChild(activiteListeDocument.createTextNode((String) currentActiviteDb.get("dateCreation")));
					currentActiviteElement.appendChild(dateCreationElement);

					// Ajout de la date de fin
					if(activiteListeDocument.createElement("dateFin") != null) {
						Element dateFinElement = activiteListeDocument.createElement("dateFin");
						dateFinElement
								.appendChild(activiteListeDocument.createTextNode((String) currentActiviteDb.get("dateFin")));
						currentActiviteElement.appendChild(dateFinElement);
					}

					// Création du tableau positions
					//Element positionsElement = activiteListeDocument.createElement("listePositions");
					BasicDBList positionsList = (BasicDBList) currentActiviteDb.get("listePositions");

					// Itération sur chaque position
					if (positionsList != null) {

						for (Object position : positionsList) {
							Element positionsElement = activiteListeDocument.createElement("listePositions");

							Element latitudeElement = activiteListeDocument.createElement("latitude");
							latitudeElement.appendChild(
									activiteListeDocument.createTextNode(((BasicDBObject) position).get("latitude").toString()));
									positionsElement.appendChild(latitudeElement);

							Element longitudeElement = activiteListeDocument.createElement("longitude");
							longitudeElement.appendChild(
									activiteListeDocument.createTextNode(((BasicDBObject) position).get("longitude").toString()));
									positionsElement.appendChild(longitudeElement);

							Element horodatageElement = activiteListeDocument.createElement("horodatage");
							horodatageElement.appendChild(
									activiteListeDocument.createTextNode(((BasicDBObject) position).get("horodatage").toString()));
									positionsElement.appendChild(horodatageElement);

							//positionsElement.appendChild(positionsElement);
							currentActiviteElement.appendChild(positionsElement);
							
						}
					}
				}
			} catch (DOMException e1) {
				response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. DOMException.</error></response>";
				e1.printStackTrace();
			} finally {
				cursor.close();
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(activiteListeDocument);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);

			transformer.transform(source, result);

			response = writer.toString();
		} catch (UnknownHostException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. UnknownHostException</error></response>";
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. ParserConfigurationException.</error></response>";
			e.printStackTrace();
		} catch (SAXException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. SAXException.</error></response>";
			e.printStackTrace();
		} catch (IOException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. IOException.</error></response>";
			e.printStackTrace();
		} catch (TransformerException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Récupération impossible. TransformerException.</error></response>"
					+ e.toString();
			e.printStackTrace();
		}
		return response;
	}

	@PUT
	public String createOrUpdate(String request) {
		String response = "<?xml version=\"1.0\"?><response><value>error</value><error>Création impossible.</error></response>";
		try {
			// Création du XML depuis la requête
			DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
			DocumentBuilder constructeur = fabrique.newDocumentBuilder();
			Document d = constructeur.parse(new InputSource(new StringReader(request)));
			Element e = d.getDocumentElement();

			// Connexion à la bdd
			MongoClient mongoClient = new MongoClient();
			DB database = mongoClient.getDB("RT0805");
			DBCollection collection = database.getCollection("Activites");
			/*
			 * Regarde si le nœud racine (<activite>) possède un id:
			 * Si non -> création
			 * Si oui -> mise à jour
			 */
			if (e.getAttribute("id").equals("")) {
				// Récupération des éléments du XML requête
				NodeList utilisateur = e.getElementsByTagName("utilisateur");
				NodeList sport = e.getElementsByTagName("sport");
				NodeList dateCreation = e.getElementsByTagName("dateCreation");

				// Insertion de la nouvelle activité
				ObjectId oid = new ObjectId();
				collection.insert(
						BasicDBObjectBuilder.start()
								.add("_id", oid)
								.add("utilisateur", utilisateur.item(0).getTextContent())
								.add("sport", sport.item(0).getTextContent())
								.add("dateCreation", dateCreation.item(0).getTextContent())
								.get());

				response = "<?xml version=\"1.0\"?><response><value>success</value><oid>" + oid.toHexString()
						+ "</oid></response>";
			} else {
				// Récupération des éléments du XML requête
				String activiteId = e.getAttribute("id");
				NodeList latitude = e.getElementsByTagName("latitude");
				NodeList longitude = e.getElementsByTagName("longitude");
				NodeList horodatage = e.getElementsByTagName("horodatage");
				NodeList dateFin = e.getElementsByTagName("dateFin");
				
				DBObject query = new BasicDBObject("_id", new ObjectId(activiteId));
				BasicDBObject newDocument = new BasicDBObject();
				
				if(latitude.item(0) != null) {
					DBObject newPosition = BasicDBObjectBuilder.start()
							.add("latitude", latitude.item(0).getTextContent())
							.add("longitude", longitude.item(0).getTextContent())
							.add("horodatage", horodatage.item(0).getTextContent()).get();
	
					// Object updatedActivite = toUpdateActivite.put("positions", newPosition);
	
					newDocument.append("$push", new BasicDBObject().append("listePositions", newPosition));
	
					collection.update(collection.findOne(query), newDocument);
				} if(dateFin.item(0) != null) {
					newDocument.append("$set", new BasicDBObject().append("dateFin", dateFin.item(0).getTextContent()));
	
					collection.update(collection.findOne(query), newDocument);
				}

				response = "<?xml version=\"1.0\"?><response><value>success</value></response>";
			}
		} catch (UnknownHostException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Création impossible. UnknownHostException</error></response>";
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Création impossible. ParserConfigurationException.</error></response>";
			e.printStackTrace();
		} catch (SAXException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Création impossible. SAXException.</error></response>";
			e.printStackTrace();
		} catch (IOException e) {
			response = "<?xml version=\"1.0\"?><response><value>error</value><error>Création impossible. IOException.</error></response>";
			e.printStackTrace();
		}
		return response;
	}

	// @DELETE
	// public void delete() {

	// }

}
