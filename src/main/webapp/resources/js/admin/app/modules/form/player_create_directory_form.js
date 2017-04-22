define(["jquery", "os_meta"], function($, os_meta) {
    const create_directory_form = $("#create_directory_form");
    const path_input = create_directory_form.find("#directory_path");
    const new_directory_modal = $(".filesystem-directory-new-modal");

    create_directory_form.submit(function(e){
        e.preventDefault();
        var formData = new FormData();
        formData.append(path_input.attr("name"), $(".filesystem-path").val() + os_meta.file_separator + path_input.val());

        create_directory_form.find("[type=submit]").attr("disabled", true);

        $.ajax({
            url : create_directory_form.attr("action"),
            type : create_directory_form.attr("method"),
            data : formData,
            processData: false,  // tell jQuery not to process the data
            contentType: false,  // tell jQuery not to set contentType
            success : function() {
                new_directory_modal.modal("hide");
                path_input.val("");
                create_directory_form.find("[type=submit]").attr("disabled", false);
                create_directory_form.find(".progress").hide();
            }
        });
    });
});