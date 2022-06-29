package Auth;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "authServ", urlPatterns = "/auth")
public class AuthServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String requestBody = "<?xml version=\"1.0\"?><utilisateur><identifiant>" + request.getParameter("identifiant")
				+ "</identifiant><motDePasse>" + request.getParameter("motDePasse") + "</motDePasse></utilisateur>";

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest postRequest = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/auth"))
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		try {
			HttpResponse<String> postResponse = client.send(postRequest,
					HttpResponse.BodyHandlers.ofString());

				if(postResponse.body().indexOf("success") > 0) {
					HttpSession sess = request.getSession(true);
					sess.setAttribute("utilisateur", request.getParameter("identifiant"));
					response.sendRedirect("activite");
				}
				else {
					response.sendRedirect("/?error=Mauvais+identifiant%2Fmot+de+passe");
				}
		} catch (IOException e) {
			response.sendRedirect("/?error=Erreur+IOExceotion");
		} catch (InterruptedException e) {
			response.sendRedirect("/?error=Erreur+InterruptedException");
		}

	}

	@Override
	public String getServletInfo() {
		return "Servlet responsable du systeme d'authentification.";
	}
}