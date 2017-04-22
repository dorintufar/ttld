"use strict";

define(["jquery"], function ($) {
    const upload_audio_form = $("#upload_audio_form");
    const file_input = $("#upload_audio_form_file_input");
    const path_input = $("#upload_audio_form_path_input");
    const new_file_modal = $(".filesystem-file-new-modal");

    upload_audio_form.submit(function(e){
        e.preventDefault();
        var formData = new FormData();
        formData.append(file_input.attr("name"), file_input[0].files[0]);
        formData.append(path_input.attr("name"), $(".filesystem-path").val());

        upload_audio_form.find("[type=submit]").attr("disabled", true);
        upload_audio_form.find(".progress").show();

        $.ajax({
            url : upload_audio_form.attr("action"),
            type : upload_audio_form.attr("method"),
            data : formData,
            processData: false,  // tell jQuery not to process the data
            contentType: false,  // tell jQuery not to set contentType
            success : function() {
                new_file_modal.modal("hide");
                upload_audio_form[0].reset();
                upload_audio_form.find("[type=submit]").attr("disabled", false);
                upload_audio_form.find(".progress").hide();
            }
        });
    });
});