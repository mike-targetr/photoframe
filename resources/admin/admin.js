// Image resolutions applied to photos before uplaoding
var PREVIEW_SIZE = 300;
var PHOTO_MAX_SIZE = 3840;

// Queue of files to upload
var uploadQueue = [];

// Currently selected photo id
var selected;

// Overlay buttons to be displayed on selected item
var deleteButton;
var selectButton;

function init() {
	
	deleteButton = document.createElement("button");		
	deleteButton.innerHTML = "Delete";
	deleteButton.classList.add("delete-btn");
	deleteButton.addEventListener('click', function() { performAction("delete", selected); });
	
	selectButton = document.createElement("button");		
	selectButton.innerHTML = "Cast";
	selectButton.classList.add("select-btn");
	selectButton.addEventListener('click', function() { performAction("select", selected); });
	
	refresh();
}

function handleFileSelection() {

	// Add photos to upload queue
	for (var i=0; i<event.target.files.length; i++) {
		var file = event.target.files[i];
		if (file.type.match(/image.*/)) {
			uploadQueue.push(file);
		}
	}
	
	updateStatus();
	processQueue();
}


function processQueue() {
	
	if (uploadQueue.length == 0) return;

	// Take first file off the queue
	var file = uploadQueue.shift();
	var id = new Date().getTime();

    // Read the file
    var reader = new FileReader();
    reader.onload = function (readerEvent) {
        var image = new Image();
        image.onload = function (imageEvent) {
            // Resize and upload preview with size PREVIEW_SIZE
        	resizeAndUploadPreview(image, id);
        	// Resize and upload photo with maximum size of IMAGE_MAX_SIZE
        	resizeAndUploadPhoto(image, id);
        }
        // Start loading image
        image.src = readerEvent.target.result;
    }
    reader.readAsDataURL(file);
}

function resizeAndUploadPreview(image, id) {

	var x = 0;
    var y = 0;
    var width = image.width;
    var height = image.height;
    
    var sourceAspectRatio = width / height;
    
	if (width > height) {
		// Crop left and right
		height = PREVIEW_SIZE;
		width = height * sourceAspectRatio;
		x = (PREVIEW_SIZE - width) / 2;
	}
	else {
		// Crop top and bottom
		width = PREVIEW_SIZE;
		height = width / sourceAspectRatio;
		y = (PREVIEW_SIZE - height) / 2;
	}
    
    var canvas = document.createElement('canvas');
    canvas.width = PREVIEW_SIZE;
    canvas.height = PREVIEW_SIZE;
    canvas.getContext('2d').drawImage(image, x, y, width, height);
    
    var dataUrl = canvas.toDataURL('image/jpeg', 0.95);
    upload(id, PREVIEW_SIZE, PREVIEW_SIZE, dataUrl);
}

function resizeAndUploadPhoto(image, id) {

	// Calculate optimal resolution to upload
    var ar = image.width / image.height;
    var width = PHOTO_MAX_SIZE;
    var height = PHOTO_MAX_SIZE;
    
    // Maintain aspect ratio
    if (ar > 1) {
    	// Landscape
    	height = Math.round(width / ar);
    }
    else {
    	// Portrait
    	width = Math.round(height * ar);
    }
    
    var canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    canvas.getContext('2d').drawImage(image, 0, 0, width, height);
    
    var dataUrl = canvas.toDataURL('image/jpeg', 0.8);
    upload(id, width, height, dataUrl);
}

function upload(id, width, height, image) {
    
    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function() {
        if (httpReq.readyState === 4 && httpReq.status === 200) {
            showPreviews(JSON.parse(httpReq.responseText));
            updateStatus();
            processQueue();
        }
    };
    
    httpReq.open("POST", "/api/upload", true);
    httpReq.setRequestHeader('Content-Type', 'application/x-data-url');
    httpReq.setRequestHeader('X-PhotoId', id);
    httpReq.setRequestHeader('X-Width', width);
    httpReq.setRequestHeader('X-Height', height);
    httpReq.send(image);
}

function refresh() {
    
    var httpReq = new XMLHttpRequest();
    httpReq.onreadystatechange = function() {
        if (httpReq.readyState === 4 && httpReq.status === 200) {
        	showPreviews(JSON.parse(httpReq.responseText));
        }
    };

    httpReq.open("GET", "/api/previews");
    httpReq.send();
}

function showPreviews(photos) {
	
	var container = document.getElementById("container");
	
	// Clear all existing preview items
	var items = document.getElementsByClassName("photo-item");
	while (items[0]) {
		container.removeChild(items[0]);
	}
	
	// Add new preview items
	for (var i=0; i<photos.length; i++) {
		var name = photos[i];
		var parts = name.split(".");
		var id = parts[0];
		
		var item = document.createElement("div");
		item.classList.add("photo-item");
		item.style.backgroundImage = "url('/photos/" + name + "')";
		item.addEventListener('click', createClickHandler(item, id));
		container.insertBefore(item, container.children[1]);
	}
	
	// Show player button if there are items
	var playerButton = document.getElementById("player-button");
	playerButton.style.display = photos.length > 0 ? "block" : "none";
	playerButton.addEventListener('click', function() { window.open("/player/"); });
}

function updateStatus() {
	
	// Upload button to display the queue size when
	// multiple photos are queued for resize and upload
	var text = "Upload Photos";
	if (uploadQueue.length > 0) {
		text = uploadQueue.length;
	}
    var uploadButton = document.getElementById("upload-button");
    uploadButton.innerHTML = text;
}

function performAction(action, id) {
	
	var httpReq = new XMLHttpRequest();
	httpReq.onreadystatechange = function() {
		if (httpReq.readyState === 4 && httpReq.status === 200) {
			showPreviews(JSON.parse(httpReq.responseText));
		}
	};
	httpReq.open("POST", "/api/" + action);
	httpReq.send(id);
}

function createClickHandler(item, id) {
	return function(e) {
		// Preview item clicked, show buttons
		selected = id;
		item.appendChild(selectButton);
		item.appendChild(deleteButton);
	};
}