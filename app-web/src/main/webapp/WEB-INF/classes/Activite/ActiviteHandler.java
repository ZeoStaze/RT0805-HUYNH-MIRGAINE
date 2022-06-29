package Activite;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ActiviteHandler {
	public static List<Activite> getActivites(String requestUser) {

		String requestBody = "<?xml version=\"1.0\"?><utilisateur><identifiant>" + requestUser
				+ "</identifiant></utilisateur>";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest postRequest = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/activite"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		try {
			HttpResponse<String> postResponse = client.send(postRequest,
					HttpResponse.BodyHandlers.ofString());

			if (postResponse.body().indexOf("error") < 0) {
				// Parse le retour de la requête
				DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
				DocumentBuilder constructeur = fabrique.newDocumentBuilder();
				Document d = constructeur.parse(new InputSource(new StringReader(postResponse.body())));
				NodeList activitesListe = d.getElementsByTagName("activite");

				List<Activite> retour = new ArrayList<Activite>();

				for (int i = 0; i < activitesListe.getLength(); i++) {

					List<Position> lPositions = new ArrayList<Position>();

					Element activite = (Element) activitesListe.item(i);
					String id = activite.getAttribute("id");

					NodeList utilisateur = activite.getElementsByTagName("utilisateur");
					NodeList sport = activite.getElementsByTagName("sport");
					NodeList dateCreation = activite.getElementsByTagName("dateCreation");
					NodeList dateFin = activite.getElementsByTagName("dateFin");

					NodeList positionsListe = activite.getElementsByTagName("listePositions");

					for (int j = 0; j < positionsListe.getLength(); j++) {
						Element position = (Element) positionsListe.item(j);

						NodeList latitude = position.getElementsByTagName("latitude");
						NodeList longitude = position.getElementsByTagName("longitude");
						NodeList horodatage = position.getElementsByTagName("horodatage");

						lPositions.add(
								new Position(
										OffsetDateTime.parse(horodatage.item(0).getFirstChild().getNodeValue()),
										Float.parseFloat(latitude.item(0).getFirstChild().getNodeValue()),
										Float.parseFloat(longitude.item(0).getFirstChild().getNodeValue())));
					}
					retour.add(
							new Activite(
									id,
									utilisateur.item(0).getFirstChild().getNodeValue(),
									sport.item(0).getFirstChild().getNodeValue(),
									lPositions,
									OffsetDateTime.parse(dateCreation.item(0).getFirstChild().getNodeValue()),
									dateFin.item(0).getFirstChild() == null ? null : OffsetDateTime.parse(dateFin.item(0).getFirstChild().getNodeValue())));
				}
				return retour;
			} else {
				Activite mockActivite = new Activite("toto", postResponse.body());
				Activite mockActivite2 = new Activite("titi", "tata");
				List<Activite> mockReturn = Arrays.asList(mockActivite, mockActivite2);
				return mockReturn;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return Arrays.asList();
	}
}
