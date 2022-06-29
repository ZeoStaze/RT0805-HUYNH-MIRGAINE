package Rest;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/auth")
public class AuthWebservice {
    
    @POST
    public String get(String utilisateur) throws SAXException, IOException, ParserConfigurationException {
        // Création du XML depuis la requête
        DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
        DocumentBuilder constructeur = fabrique.newDocumentBuilder();
        Document d = constructeur.parse(new InputSource(new StringReader(utilisateur)));
        Element e = d.getDocumentElement();
        NodeList identifiant = e.getElementsByTagName("identifiant");
        NodeList mdp = e.getElementsByTagName("motDePasse");

        // Chargement depuis la bdd
        MongoClient mongoClient = new MongoClient();
        DB database = mongoClient.getDB("RT0805");
        DBCollection collection = database.getCollection("Utilisateurs");
        DBObject query = new BasicDBObject("identifiant", identifiant.item(0).getTextContent());
        DBCursor cursor = collection.find(query);
        String response = "<?xml version=\"1.0\"?><response><value>error</value><error>Mauvais identifiant/mot de passe</error></response>";

        // Vérification du mot de passe
        if(cursor.hasNext()){
            String dbMdp = (String) cursor.one().get("mdp");
            if(mdp.item(0).getTextContent().equals(dbMdp)) {
                response = "<?xml version=\"1.0\"?><response><value>success</value></response>";
            }
        }

        return response;
    }

}
