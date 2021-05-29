var uploadedFileName = "";
var uploadedFileType = "";
var uploadedFileContent = "";
var fileReadyToSend = true;
var MAX_SIZE = 1024 * 1024;

function resetUploadFile() {
    document.getElementById("upload").value = "";
    uploadedFileName = "";
    uploadedFileType = "";
    uploadedFileContent = "";
    fileReadyToSend = true;
}

function handleFileSelect() {
    var file = document.querySelector('#upload').files[0];
    console.log('Selected file size:' + file.size);
    if (file.size > MAX_SIZE) {
        console.log('Selected file to big:' + file.size);
        resetUploadFile();
        return;
    }
    getBase64(file);
}

function getBase64(file) {
    uploadedFileName = file.name;
    uploadedFileType = file.type;
    fileReadyToSend = false;
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
        uploadedFileContent = reader.result;
        fileReadyToSend = true;
    };
    reader.onerror = function (error) {
        console.log('Upload error: ', error);
        resetUploadFile()
    };
}

document.getElementById('upload').addEventListener('change', handleFileSelect)

function getFile(path) {
    jQuery.get(url + path);
}