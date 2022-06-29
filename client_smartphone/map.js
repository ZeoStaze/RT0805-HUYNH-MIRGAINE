let map;

function initMap() {
	map = new google.maps.Map(document.getElementById("map"), {
		center: { lat: 49.244, lng: 4.06 },
		zoom: 17,
	});

	map.addListener('click', function (e) {
		console.log(e);
		addMarker(e.latLng);
	});
}

function addMarker(latLng) {
	console.log(latLng)
	let marker = new google.maps.Marker({
		map: map,
		position: latLng,
		draggable: true
	});

	// Requête d'ajout de position dur l'activité
	const xmlStr = `<?xml version="1.0"?>
<activite id="${sessionStorage.getItem("activiteId")}">
    <listePositions>
        <position>
            <latitude>${latLng.lat()}</latitude>
            <longitude>${latLng.lng()}</longitude>
            <horodatage>${new Date().toISOString()}</horodatage>
        </position>
    </listePositions>
</activite>`
		;
	const parser = new DOMParser();
	const doc = parser.parseFromString(xmlStr, "application/xml");
	console.log(xmlStr)

	// Création de la requête
	var url = "http://localhost:8080/api/activite"

	var updateRequest = new XMLHttpRequest()
	updateRequest.onreadystatechange = function () {
		if (this.readyState == 4) {
			// Réponse OK
			if (this.status == 200) {
				var xmlResponse = parser.parseFromString(this.responseText, "application/xml")
				var responseValue = xmlResponse.evaluate("/response/value/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				var idValue = xmlResponse.evaluate("/response/oid/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				// OK
				if (responseValue.data === 'success') {
					document.getElementById("errorContainer").innerHTML = ""
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
	updateRequest.overrideMimeType('text/xml; charset=utf-8');
	updateRequest.open("PUT", url)

	updateRequest.setRequestHeader('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept')
	updateRequest.send(doc)

}

window.initMap = initMap;

function terminerRequest() {
	// Création du XML
	const xmlStr = `<?xml version="1.0"?>
	<activite id="${sessionStorage.getItem("activiteId")}">
		<dateFin>${new Date().toISOString()}</dateFin>
	</activite>`;

	const parser = new DOMParser()
	const doc = parser.parseFromString(xmlStr, "application/xml")

	// Création de la requête
	var url = "http://localhost:8080/api/activite"

	var createRequest = new XMLHttpRequest()
	createRequest.onreadystatechange = function () {
		if (this.readyState == 4) {
			// OK
			if (this.status == 200) {
				var xmlResponse = parser.parseFromString(this.responseText, "application/xml")
				var responseValue = xmlResponse.evaluate("/response/value/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				var idValue = xmlResponse.evaluate("/response/oid/text()", xmlResponse, null, XPathResult.ANY_TYPE, null).iterateNext()
				if (responseValue.data === 'success') {
					window.location = "./creation.html"
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