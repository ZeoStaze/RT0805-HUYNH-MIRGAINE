<!DOCTYPE html>
<html>
<head>
	<title>Sport Tracker | consultation</title>
	<link rel="stylesheet" href="/css/bootstrap.min.css">
</head>

<body>
	<nav class="navbar bg-primary" style="height: 250px">
		<div class="container-fluid" style="margin-left: 50px">
			<h2 class="text-light" href="#">Sport Tracker | Portail de consultation</h2>
		</div>
	</nav>
	<div class="card border-primary mb-3" style="max-width: 28rem; margin: auto auto; margin-top: -35px">
		<div class="card-header">Se connecter</div>
		<div class="card-body">
			<form action="auth" method="post">
				<fieldset>
					<div class="form-group" style="margin-bottom: 10px">
						<input class="form-control" name="identifiant" placeholder="Identifiant">
					</div>
					<div class="form-group" style="margin-bottom: 10px">
						<input type="password" class="form-control" name="motDePasse" placeholder="Mot de passe">
					</div>
					<div id="errorContainer"></div>
					<script>
						// Cherche un message d'erreur, l'affiche si besoin
						const queryString = window.location.search
						const urlParams = new URLSearchParams(queryString)
						const error = urlParams.get('error')
						if(error) document.getElementById("errorContainer").innerHTML = "<p class=\"text-danger\">" + error + "</p>"
					</script>
					<button type="submit" class="btn btn-success" style="width: 100%;">Connexion</button>
				</fieldset>
			</form>
		</div>
	</div>

</body>

</html>