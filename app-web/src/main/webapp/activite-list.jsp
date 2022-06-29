<%@page contentType="text/html" pageEncoding="UTF-8" %>
	<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c' %>
		<!DOCTYPE html>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
			<title>Sport Tracker | consultation</title>
			<script type="text/javascript" src="/map.js"></script>
			<link rel="stylesheet" href="/css/bootstrap.min.css">
			<title>JSP Page</title>
			<%@ page import="Activite.Activite,Activite.Position,Activite.ActiviteHandler,java.util.List,jakarta.servlet.http.HttpSession;" %>
				<% String utilisateur = "";
					HttpSession sess = request.getSession(false);
					if(sess != null){
						utilisateur = (String) sess.getAttribute("utilisateur");
						pageContext.setAttribute("utilisateur", utilisateur);
					} 
					if (utilisateur == null || utilisateur.equals("")) {
						response.sendRedirect("/?error=Veuillez+vous+identifier");
					}
					List<Activite> activites = ActiviteHandler.getActivites(utilisateur);
					pageContext.setAttribute("activites", activites);
					Activite selectedActivite = new Activite();
					String url = request.getRequestURI();
					url = url.substring(url.lastIndexOf("/") + 1);
					pageContext.setAttribute("url", url);
					if (!url.equals("activite")) {
						for(Activite activite : activites) {
							if(activite.getId().equals(url)) {
								selectedActivite = activite;
								pageContext.setAttribute("selectedActivite", selectedActivite);
							}
						}
					}
				%>
		</head>

		<body>
			<nav class="navbar bg-primary" style="height: 250px; display: flex">
				<div class="container-fluid" style="margin-left: 50px; margin-right: 50px;">
					<h2 class="text-light" href="#">Sport Tracker | Portail de consultation</h2>
					<h5 class="text-light" href="#">Connecté en tant que: <c:out value='${utilisateur}' /><span id="utilisateur"></span></h5>
				</div>
			</nav>
			<div style="display: flex">
				<div class="card mb-3" style="width: 22rem; margin-left: 20px; margin-top: -35px">
					<div class="card-header">Vos activités</div>
					<div class="card-body">
						<ul class="list-group list-group-flush">
							<c:forEach items="${ activites }" var="activite">
								<li class="list-group-item">
									<a href="${pageContext.request.contextPath}/activite/${activite.getId()}" class="side-button">
										<c:out value='${activite.sport}' /> le <c:out value='${activite.dateCreation}' />
									</a>
								</li>
							</c:forEach>
						</ul>
					</div>
				</div>
				<c:if test="${url != 'activite'}">
					<div class="card mb-3" style="width: 55rem; margin-left: 20px; margin-top: -35px">
						<div class="card-header">Information sur l'activité</div>
						<div class="card-body">
							<p>Sport: <c:out value="${selectedActivite.sport}"/></p>
							<p>Date de départ: <c:out value="${selectedActivite.dateCreation}"/> </p>
							<c:if test="${selectedActivite.dateFin != null}">
								<p>Date de fin: <c:out value="${selectedActivite.dateFin}"/> </p>
							</c:if>
							<p>Durée: <c:out value="${selectedActivite.calculDuree()}"/> </p>
							<p>Distance: <c:out value="${selectedActivite.calculDistance()}"/> Km</p>
							<div id="map" style="height: 500px; width: 100%; margin-bottom: 10px;"></div>
						</div>
					</div>
				</c:if>
			</div>
			<script
				src="https://maps.googleapis.com/maps/api/js?key=&callback=initMap&v=weekly"
				defer>
			</script>
			<% 
				List<Position> listePositions = selectedActivite.getPositions();
				double latitude = 0.0;
				double longitude = 0.0;
				
				for(int i = 0; i < listePositions.size(); i++){
						latitude = listePositions.get(i).getLatitude();
						longitude = listePositions.get(i).getLongitude();
						pageContext.setAttribute("latitude", latitude);
						pageContext.setAttribute("longitude", longitude);
						%>
								<script>
									addMarker(<c:out value="${latitude}"/>,<c:out value="${longitude}"/>);
								</script>
						<%
				}
			%>
		</body>

		</html>