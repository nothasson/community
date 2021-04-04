$(function () {
    $("#publishBtn").click(publish);
});

function publish() {
    $("#publishModal").modal("hide");
    //获取内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    console.log(title);
    console.log(content);
    $.post(
        "/discuss/add",
        {"title": title, "content": content},
        function (data) {
            data = $.parseJSON(data);
            $('hintBody').text(data.msg);
            $("#hintModal").modal("show");
            setTimeout(function () {
                $("#hintModal").modal("hide");
                if (data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    )

}