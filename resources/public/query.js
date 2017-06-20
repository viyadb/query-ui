(function($) {

  window.add_element = function(type) {
    var target = $("#" + type + "s");
    target.children().last().find("button").toggleClass("hide");
    var cnt = target.children().length;
    var new_elem = $("#" + type).clone();
    new_elem.find("[id]").addBack("[id]").each(function() {
      this.id = this.id + "-" + cnt;
    });
    target.append(new_elem);
  }

  window.remove_element = function(btn) {
    $(btn).closest(".form-group").remove();
  }

  function show_error(error) {
    $("#alert").removeClass("hide").text(error);
  }

  function draw_data(data) {
    var content = Array();
    content.push("<tr>");
    $("#grouppings select, #metrics select").each(function() {
      content.push("<th>");
      content.push($(this).val());
      content.push("</th>");
    });
    content.push("</tr>");

    for (var r = 0; r < data.length; ++r) {
      content.push("<tr>");
      for (var c = 0; c < data[r].length; ++c) {
        content.push("<td>");
        content.push(data[r][c]);
        content.push("</td>");
      }
      content.push("</tr>");
    }
    $("#table tbody").html(content.join(""));
    $("#stats-rows").text("rows=" + data.length);
  }

  $(function() {
    $.get("/meta", function(data) {
      if (!data) {
        show_error("ViaDB instance seems to be down");
        $("button[type=submit]").attr("disabled", "disabled");
        return;
      }
      $.each(data.dimensions, function(key, value) {
        $("#filter-field").append($("<option>").text(value.name).attr("value", value.name));
        $("#dim-name").append($("<option>").text(value.name).attr("value", value.name));
        $("#sort-col").append($("<option>").text(value.name).attr("value", value.name));
      });
      $.each(data.metrics, function(key, value) {
        $("#metric-name").append($("<option>").text(value.name).attr("value", value.name));
        $("#sort-col").append($("<option>").text(value.name).attr("value", value.name));
      });
      add_element("filter");
      add_element("groupping");
      add_element("metric");
      add_element("sort");
    });

    $("#query-form").submit(function(e) {
      $("#table tbody").empty();
      $("#progress").removeClass("hide");
      $("#alert").addClass("hide");
      $("#stats-rows,#stats-time").text("");

      var form = $(this);
      var req_start = new Date().getTime();
      $.ajax({
        type: form.attr("method"),
        url: form.attr("action"),
        data: form.serialize(),
        success: function(data) {
          if (!data) {
            show_error("ViyaDB process is not responding");
          } else {
            draw_data(data);
          }
        },
        complete: function() {
          $("#progress").addClass("hide");
          $("#stats-time").text("time=" + (new Date().getTime() - req_start) + "ms");
        }
      });
      e.preventDefault();
    });
  });

})(jQuery);
