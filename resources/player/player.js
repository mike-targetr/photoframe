var IMAGE_SMOOTHING = true;

var canvas;
var lastPhotoA, lastPhotoB;

function init() {
	canvas = document.getElementById("photoframe");
	surface = document.getElementById("surface");
	
	resizeCanvas();
	refreshAddress();
	refreshCanvas();
	
	surface.addEventListener('click', openFullscreen);
	window.addEventListener("resize", resizeCanvas);
	
	setInterval(refreshAddress, 10000);
	setInterval(refreshCanvas, 500);
}

function resizeCanvas() {
    canvas.width  = window.innerWidth  * window.devicePixelRatio;
    canvas.height = window.innerHeight * window.devicePixelRatio;
    lastPhotoA = null;
    lastPhotoB = null;
}

function openFullscreen() {
	var e = document.getElementById("player");
	if (e.requestFullscreen) {
		e.requestFullscreen();
	}
}

function refreshAddress() {
	var httpReq = new XMLHttpRequest();
	httpReq.onreadystatechange = function() {
		if (httpReq.readyState === 4 && httpReq.status === 200) {
			var address = httpReq.responseText;
			var link = document.getElementById("address");
			link.href = address;
			link.innerHTML = address;
		}
	};
	httpReq.open("GET", "/api/address");
	httpReq.send();
}

function refreshCanvas() {
    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function() {
        if (httpReq.readyState === 4 && httpReq.status === 200) {
        	loadAndDrawPhotos(JSON.parse(httpReq.responseText));
        }
    };
    httpReq.open("GET", "/api/next", true);
    httpReq.send();
}

function loadAndDrawPhotos(photos) {
	
	if (photos.length == 0) return;
	
	var photoA = photos[0];
	var photoB = photos.length >= 2 ? photos[1] : photos[0];
	
	if (photoA == lastPhotoA && photoB == lastPhotoB) return;
	
	// Load photos
	var imageA = new Image();
    imageA.onload = function (e) {
    	imageA.photo = photoA;
    	if (imageA.photo && imageB.photo) {
    		drawPhotos(imageA, imageB);
    	}
    }	
    var imageB = new Image();
    imageB.onload = function (e) {
    	imageB.photo = photoB;
    	if (imageA.photo && imageB.photo) {
    		drawPhotos(imageA, imageB);
    	}
    }
    imageA.src = "/photos/" + photoA;
    imageB.src = "/photos/" + photoB;
    
    lastPhotoA = photoA;
    lastPhotoB = photoB;
}

function drawPhotos(imageA, imageB) {
	
	// Decide how to fill the screens with photos
	var parts = imageA.photo.split(".");
	var photoAr = parts[1] / parts[2];

	var usedWidth = canvas.height * photoAr;
	var usedHeight = canvas.width / photoAr;
	
	if (usedWidth*1.75 <= canvas.width) {
		// Space for two similar photos horizontally
		drawPhoto(imageA, 0, 0, canvas.width/2, canvas.height);
		drawPhoto(imageB, canvas.width/2, 0, canvas.width/2, canvas.height);
	}
	else if (usedHeight*1.75 <= canvas.height) {
		// Space for two similar photos vertically
		drawPhoto(imageA, 0, 0, canvas.width, canvas.height/2);
		drawPhoto(imageB, 0, canvas.height/2, canvas.width, canvas.height/2);
	}
	else {
		// Fill with single photo
		drawPhoto(imageA, 0, 0, canvas.width, canvas.height);
	}
}

function drawPhoto(image, x, y, width, height) {
	
	var targetAr = width / height;
	
	var photoY = 0;
	var photoX = 0;
	var photoWidth = width;
	var photoHeight = height;
	
	var parts = image.photo.split(".");
	var photoAr = parts[1] / parts[2];
	
	var ctx = canvas.getContext('2d');
	ctx.imageSmoothingEnabled = IMAGE_SMOOTHING;
	ctx.clearRect(x, y, width, height);
	
	if (targetAr > photoAr) {
		// Crop top and bottom
		photoHeight = photoWidth / photoAr;
		photoY = height / 2 - photoHeight / 2;
		ctx.drawImage(image, x + photoX, y + photoY, photoWidth, photoHeight);
	}
	else {
		// Crop left and right
		photoWidth = photoHeight * photoAr;
		photoX = width / 2 - photoWidth / 2;
		ctx.drawImage(image, x + photoX, y + photoY, photoWidth, photoHeight);
	}
}