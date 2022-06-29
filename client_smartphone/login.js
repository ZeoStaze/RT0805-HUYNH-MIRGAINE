function loginRequest() {
	var idInput = document.getElementById("idInput")
	var mdpInput = document.getElementById("mdpInput")

	// Vérification des entrées
	if (!idInput.value || !mdpInput.value) {
		document.getElementById("errorContainer").innerHTML =
			`<p class="text-danger">Veuillez renseigner un identifiant et un mot de passe.</p>`
		return;
	}

	// Création du XML
	const xmlStr = `<?xml version="1.0"?>
	<utilisateur>
			<identifiant>${idInput.value}</identifiant>
			<motDePasse>${mdpInput.value}</motDePasse>
	</utilisateur>`;

	const parser = new DOMParser()
	const doc = parser.parseFromString(xmlStr, "application/xml")

	// Création de la requête
	var url = "http://localhost:8080/api/auth"

	var loginRequest = new XMLHttpRequest()
	loginRequest.onreadystatechange = function () {
		if (this.readyState == 4) {
			// Réponse OK
			if (this.status == 200) {
				var xmlResponse = parser.parseFromString(this.responseText, "application/xml")
				var responseValue = xmlResponse.evaluate("/response/value/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				console.log("titi", responseValue.data);
				// Login OK
				if (responseValue.data === 'success') {
					sessionStorage.setItem('utilisateur', idInput.value)
					window.location = "./creation.html"
				}
				// Login KO
				else {
					var errorValue = xmlResponse.evaluate("/response/error/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
					document.getElementById("errorContainer").innerHTML =
					`<p class="text-danger">${errorValue.data}</p>`
				}
			}
			// Erreur sur la requête
			else {
				document.getElementById("errorContainer").innerHTML =
				`<p class="text-danger">Erreur de connexion.</p>`
			}
		}
	};
	loginRequest.overrideMimeType('text/xml; charset=utf-8');
	loginRequest.open("POST", url)
	loginRequest.setRequestHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
	loginRequest.send(doc)

}