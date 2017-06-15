(function($) {

  function add_element(template_sel, target_sel) {
    var target = $(target_sel);
    target.children().last().find("button").toggleClass("hide");
    var cnt = target.children().length;
    var new_elem = $(template_sel).clone();
    new_elem.find("[id]").addBack("[id]").each(function() {
      this.id = this.id + "-" + cnt;
    });
    target.append(new_elem);
  }
  window.add_element = add_element;

  function remove_element(btn, selector) {
    $(btn).closest(selector).remove();
  }
  window.remove_element = remove_element;

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
  }

  $(function() {
    $.get("/meta", function(data) {
      $.each(data.dimensions, function(key, value) {
        $("#filter-field").append($("<option>").text(value.name).attr("value", value.name));
        $("#dim-name").append($("<option>").text(value.name).attr("value", value.name));
      });

      $.each(data.metrics, function(key, value) {
        $("#metric-name").append($("<option>").text(value.name).attr("value", value.name));
      });

      add_element('#filter', '#filters');
      add_element('#groupping', '#grouppings');
      add_element('#metric', '#metrics');
    });

    $("#query-form").submit(function(e) {
      $("#table tbody").empty();
      $("#progress").removeClass("hide");
      var form = $(this);
      $.ajax({
        type: form.attr("method"),
        url: form.attr("action"),
        data: form.serialize(),
        success: function(data) {
          draw_data(data);
        },
        complete: function() {
          $("#progress").addClass("hide");
        }
      });
      e.preventDefault();
    });
  });

})(jQuery);
