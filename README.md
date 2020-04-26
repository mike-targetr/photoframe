# PhotoFrame
Photo management and display software for Raspberry Pi.

## Features

* A small, lightweight Java web server for receiving, storing and serving photos on the local network.
* A web player for displaying a sequence of photos fullscreen.
* A web interface for uploading new photos and selecting photos to be displayed by the web player.

This project is designed to be self contained. It does not require any additional libraries or cloud services. Any device with Java installed can run the software and any recent web browser can be used to upload, manage and display photos.

A typical use case is to run this on a Raspberry Pi connected to a screen. Other users on the local network can upload photos to the screen.

Photos are stored at 4K resolution. A Raspberry Pi 4 can be used to display photos at native resolution on a 4K screen.

## Quick Start
```
git clone https://github.com/mike-targetr/photoframe.git
cd photoframe
cd dist
java -jar photoframe.jar
```

Chromium will launch fullscreen displaying an address in the bottom right corner. Use this address upload and manage photos. If Chromium fails to start open http://localhost:9090/player/ in a web browser on the local device.
