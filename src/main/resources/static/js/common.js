$(document).ready(function () {

    /* changes photo directly when the user selects another */
    $('#programmeImage').change(function () {
        showImageThumbnail(this)
    })

    /* maximize the size of the multiple select to fit all the options */
    const multipleSelect =  document.querySelector(".multiple-select");
    if(multipleSelect){
        multipleSelect.size = multipleSelect.length;
    }

    // to make the table row interactive and link it to details page
    const rows = document.querySelectorAll('.row-with-link');
    rows.forEach(row => {
        row.addEventListener('click', function handleClick(event) {
            if(event.target.nodeName==="TD"){
                window.location.href = event.currentTarget.dataset.link
            }
        });
    });
})

function showImageThumbnail(fileInput) {
    const file = fileInput.files[0]
    const reader = new FileReader();
    reader.onload = function (e) {
        $('#programmeThumbnail').attr('src', e.target.result)
    }
    reader.readAsDataURL(file)
}