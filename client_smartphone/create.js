function createRequest() {
	var sportSelect = document.getElementById("sportSelect")
	// Création du XML
	const xmlStr = `<?xml version="1.0"?>
	<activite id="">
			<utilisateur>${sessionStorage.getItem('utilisateur')}</utilisateur>
			<sport>${sportSelect.value}</sport>
			<dateCreation>${new Date().toISOString()}</dateCreation>
	</activite>`;

	const parser = new DOMParser()
	const doc = parser.parseFromString(xmlStr, "application/xml")

	// Création de la requête
	var url = "http://localhost:8080/api/activite"

	var createRequest = new XMLHttpRequest()
	createRequest.onreadystatechange = function () {
		if (this.readyState == 4) {
			// Réponse OK
			if (this.status == 200) {
				var xmlResponse = parser.parseFromString(this.responseText, "application/xml")
				var responseValue = xmlResponse.evaluate("/response/value/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				var idValue = xmlResponse.evaluate("/response/oid/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				// OK
				if (responseValue.data === 'success') {
					sessionStorage.setItem('activiteId', idValue.data)
					window.location = "./activite.html"
				}
				// KO
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
	createRequest.overrideMimeType('text/xml; charset=utf-8');
	createRequest.open("PUT", url)

	createRequest.setRequestHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
	createRequest.send(doc)
}