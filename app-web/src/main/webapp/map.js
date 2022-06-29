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

    markers.forEach(pos => {
        new google.maps.Marker({
            map: map,
            position: pos,
            draggable: true
        });
    });

    paths.forEach(path => {
        console.log(path)
        const flightPath = new google.maps.Polyline({
            path: path,
            geodesic: true,
            strokeColor: "#FF0000",
            strokeOpacity: 1.0,
            strokeWeight: 2,
        });

        flightPath.setMap(map);

    });

}
markers = []
paths = []
function addMarker(latitude, longitude) {
    markers.push({ lat: latitude, lng: longitude })
    if (markers.length > 1) {
        paths.push([markers[markers.length - 2], markers[markers.length - 1]])
    }
}