$(document).ready(function () {

    /* changes photo directly when the user selects another */
    $('#programmeImage').change(function () {
        showImageThumbnail(this)
    })

    /* maximize the size of the multiple select to fit all the options */
    const multipleSelect =  document.querySelector(".multiple-select");
    multipleSelect.size = multipleSelect.length;
})

function showImageThumbnail(fileInput) {
    const file = fileInput.files[0]
    const reader = new FileReader();
    reader.onload = function (e) {
        $('#programmeThumbnail').attr('src', e.target.result)
    }
    reader.readAsDataURL(file)
}