var autoRefreshCount = 0;
var autoRefreshIntervalId = null;

function refreshSchoolSchedule() {
    $.getJSON("/schoolSchedule", function (schoolSchedule) {
        refreshSolvingButtons(schoolSchedule.solverStatus != null && schoolSchedule.solverStatus !== "NOT_SOLVING");
        $("#score").text("Score: "+ (schoolSchedule.score == null ? "?" : schoolSchedule.score));

        const schoolScheduleByRoom = $("#schoolScheduleByRoom");
        schoolScheduleByRoom.children().remove();
        const schoolScheduleByTeacher = $("#schoolScheduleByTeacher");
        schoolScheduleByTeacher.children().remove();
        const schoolScheduleByGradeLevel = $("#schoolScheduleByGradeLevel");
        schoolScheduleByGradeLevel.children().remove();
        const unassignedLessons = $("#unassignedLessons");
        unassignedLessons.children().remove();

        const theadByRoom = $("<thead>").appendTo(schoolScheduleByRoom);
        const headerRowByRoom = $("<tr>").appendTo(theadByRoom);
        headerRowByRoom.append($("<th>Timeslot</th>"));
        $.each(schoolSchedule.roomList, (index, room) => {
            headerRowByRoom
            .append($("<th>")
                .append($("<span/>").text(room.name))
                .append($(`
                  <button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1">
                    <small class="fas fa-trash"></small>
                  </button>`
                ).click(() => deleteRoom(room)))
            .append("</th>"));
        });
        const theadByTeacher = $("<thead>").appendTo(schoolScheduleByTeacher);
        const headerRowByTeacher = $("<tr>").appendTo(theadByTeacher);
        headerRowByTeacher.append($("<th>Timeslot</th>"));
        const teacherList = [...new Set(schoolSchedule.lessonList.map(lesson => lesson.teacher))];
        $.each(teacherList, (index, teacher) => {
            headerRowByTeacher
            .append($("<th>")
                .append($("<span />").text(teacher))
            .append("</th>"));
        });
        const theadByGradeLevel = $("<thead>").appendTo(schoolScheduleByGradeLevel);
        const headerRowByGradeLevel = $("<tr>").appendTo(theadByGradeLevel);
        headerRowByGradeLevel.append($("<th>Timeslot</th>"));
        const gradeLevelList = [...new Set(schoolSchedule.lessonList.map(lesson => lesson.gradeLevel))];
        $.each(gradeLevelList, (index, gradeLevel) => {
            headerRowByGradeLevel
            .append($("<th>")
                .append($("<span />").text(gradeLevel))
            .append("</th>"));
        });

        const tbodyByRoom = $("<tbody>").appendTo(schoolScheduleByRoom);
        const tbodyByTeacher = $("<tbody>").appendTo(schoolScheduleByTeacher);
        const tbodyByGradeLevel = $("<tbody>").appendTo(schoolScheduleByGradeLevel);
        $.each(schoolSchedule.timeSlotList, (index, timeSlot) => {
            const rowByRoom = $("<tr>").appendTo(tbodyByRoom);
            rowByRoom
            .append($(`<th class="align-middle">`)
                .append($("<span/>").text(`
                    ${timeSlot.dayOfWeek.charAt(0) + timeSlot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeSlot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeSlot.endTime, "HH:mm:ss").format("HH:mm")}
                `)
                .append($(`
                    <button type="button" class="ml-2 mb-1 btn btn-light btn-sm p-1">
                        <small class="fas fa-trash"></small>
                    </button>
                `).click(() => deleteTimeslot(timeSlot)))
            .append("</th>")));

            const rowByTeacher = $("<tr>").appendTo(tbodyByTeacher);
            rowByTeacher
            .append($(`<th class="align-middle">`)
                .append($("<span/>").text(`
                    ${timeSlot.dayOfWeek.charAt(0) + timeSlot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeSlot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeSlot.endTime, "HH:mm:ss").format("HH:mm")}
                `)
            .append("</th>")));
            $.each(schoolSchedule.roomList, (index, room) => {
                rowByRoom.append($("<td/>").prop("id", `timeSlot${timeSlot.id}room${room.id}`));
            });
            const rowByGradeLevel = $("<tr>").appendTo(tbodyByGradeLevel);
            rowByGradeLevel
            .append($(`<th class="align-middle">`)
                .append($("<span/>").text(`
                    ${timeSlot.dayOfWeek.charAt(0) + timeSlot.dayOfWeek.slice(1).toLowerCase()}
                    ${moment(timeSlot.startTime, "HH:mm:ss").format("HH:mm")}
                    -
                    ${moment(timeSlot.endTime, "HH:mm:ss").format("HH:mm")}
                `))
            .append("</th>"));

            $.each(teacherList, (index, teacher) => {
                rowByTeacher.append($("<td/>").prop("id", `timeSlot${timeSlot.id}teacher${convertToId(teacher)}`));
            });

            $.each(gradeLevelList, (index, gradeLevel) => {
                rowByGradeLevel.append($("<td/>").prop("id", `timeSlot${timeSlot.id}gradeLevel${convertToId(gradeLevel)}`));
            });
        });

        $.each(schoolSchedule.lessonList, (index, lesson) => {
            const color = pickColor(lesson.subject);
            const lessonElementWithoutDelete = $(
            `<div class="card lesson" style="background-color: ${color}">`)
                .append($(`<div class="card-body p-2">`)
                    .append($(`<h5 class="card-title mb-1" />`).text(lesson.subject))
                    .append($(`<p class="card-text text-muted ml-2 mb-1" />`).text(`by ${lesson.teacher}`))
                    .append($(`<small class="ml-2 mt-1 card-text text-muted align-bottom float-right" />`).text(lesson.id))
                    .append($(`<p class="card-text ml-2" />`).text(lesson.gradeLevel))
                .append("</div>"))
            .append(`</div>`);
            const lessonElement = lessonElementWithoutDelete.clone();
            lessonElement.find(".card-body").prepend(
                $(`
                    <button type="button" class="ml-2 btn btn-light btn-sm p-1 float-right">
                        <small class="fas fa-trash"></small>
                    </button>
                `).click(() => deleteLesson(lesson))
            );
            if (lesson.timeSlot == null || lesson.room == null) {
                unassignedLessons.append(lessonElement);
            } else {
                $(`#timeSlot${lesson.timeSlot.id}room${lesson.room.id}`).append(lessonElement);
                $(`#timeSlot${lesson.timeSlot.id}teacher${convertToId(lesson.teacher)}`).append(lessonElementWithoutDelete.clone());
                $(`#timeSlot${lesson.timeSlot.id}gradeLevel${convertToId(lesson.gradeLevel)}`).append(lessonElementWithoutDelete.clone());
            }
        });
    });
}

function convertToId(str) {
    // Base64 encoding without padding to avoid XSS
    return btoa(str).replace(/=/g, "");
}

function solve() {
    $.post("/schoolSchedule/solve", function () {
        refreshSolvingButtons(true);
        autoRefreshCount = 16;
        if (autoRefreshIntervalId == null) {
            autoRefreshIntervalId = setInterval(autoRefresh, 2000);
        }
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Start solving failed.", xhr);
    });
}

function refreshSolvingButtons(solving) {
    if (solving) {
        $("#solveButton").hide();
        $("#stopSolvingButton").show();
    } else {
        $("#solveButton").show();
        $("#stopSolvingButton").hide();
    }
}

function autoRefresh() {
    refreshSchoolSchedule();
    autoRefreshCount--;
    if (autoRefreshCount <= 0) {
        clearInterval(autoRefreshIntervalId);
        autoRefreshIntervalId = null;
    }
}

function stopSolving() {
    $.post("/schoolSchedule/stopSolving", function () {
        refreshSolvingButtons(false);
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Stop solving failed.", xhr);
    });
}

function addLesson() {
    var subject = $("#lesson_subject").val().trim();
    $.post("/lessons", JSON.stringify({
        "subject": subject,
        "teacher": $("#lesson_teacher").val().trim(),
        "gradeLevel": $("#lesson_gradeLevel").val().trim()
    }), function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding lesson (" + subject + ") failed.", xhr);
    });
    $('#lessonDialog').modal('toggle');
}

function deleteLesson(lesson) {
    $.delete("/lessons/" + lesson.id, function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting lesson (" + lesson.name + ") failed.", xhr);
    });
}

function addTimeslot() {
    $.post("/timeSlots", JSON.stringify({
        "dayOfWeek": $("#timeSlot_dayOfWeek").val().trim().toUpperCase(),
        "startTime": $("#timeSlot_startTime").val().trim(),
        "endTime": $("#timeSlot_endTime").val().trim()
    }), function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding timeSlot failed.", xhr);
    });
    $('#timeSlotDialog').modal('toggle');
}

function deleteTimeslot(timeSlot) {
    $.delete("/timeSlots/" + timeSlot.id, function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting timeSlot (" + timeSlot.name + ") failed.", xhr);
    });
}

function addRoom() {
    var name = $("#room_name").val().trim();
    $.post("/rooms", JSON.stringify({
        "name": name
    }), function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Adding room (" + name + ") failed.", xhr);
    });
    $("#roomDialog").modal('toggle');
}

function deleteRoom(room) {
    $.delete("/rooms/" + room.id, function () {
        refreshSchoolSchedule();
    }).fail(function(xhr, ajaxOptions, thrownError) {
        showError("Deleting room (" + room.name + ") failed.", xhr);
    });
}

function showError(title, xhr) {
    const serverErrorMessage = !xhr.responseJSON ? `${xhr.status}: ${xhr.statusText}` : xhr.responseJSON.message;
    console.error(title + "\n" + serverErrorMessage);
    const notification = $(`
    <div class="toast" role="alert" role="alert" aria-live="assertive" aria-atomic="true" style="min-width: 30rem">
        <div class="toast-header bg-danger">
            <strong class="mr-auto text-dark">Error</strong>
            <button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="toast-body">
      `)
        .append($(`<p/>`).text(title))
        .append($(`<pre>`)
            .append($(`<code />`).text(serverErrorMessage))
        .append(`</pre>
        </div>
    </div>`));
    $("#notificationPanel").append(notification);
    notification.toast({delay: 30000});
    notification.toast('show');
}

$(document).ready( function() {
    $.ajaxSetup({
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }
    });
    // Extend jQuery to support $.put() and $.delete()
    jQuery.each( [ "put", "delete" ], function( i, method ) {
        jQuery[method] = function (url, data, callback, type) {
            if (jQuery.isFunction(data)) {
                type = type || callback;
                callback = data;
                data = undefined;
            }
            return jQuery.ajax({
                url: url,
                type: method,
                dataType: type,
                data: data,
                success: callback
            });
        };
    });


    $("#refreshButton").click(function() {
        refreshSchoolSchedule();
    });
    $("#solveButton").click(function() {
        solve();
    });
    $("#stopSolvingButton").click(function() {
        stopSolving();
    });
    $("#addLessonSubmitButton").click(function() {
        addLesson();
    });
    $("#addTimeslotSubmitButton").click(function() {
        addTimeslot();
    });
    $("#addRoomSubmitButton").click(function() {
        addRoom();
    });

    refreshSchoolSchedule();
});

// ****************************************************************************
// TangoColorFactory
// ****************************************************************************

const SEQUENCE_1 = [0x8AE234, 0xFCE94F, 0x729FCF, 0xE9B96E, 0xAD7FA8];
const SEQUENCE_2 = [0x73D216, 0xEDD400, 0x3465A4, 0xC17D11, 0x75507B];

var colorMap = new Map;
var nextColorCount = 0;

function pickColor(object) {
    let color = colorMap[object];
    if (color !== undefined) {
        return color;
    }
    color = nextColor();
    colorMap[object] = color;
    return color;
}

function nextColor() {
    let color;
    let colorIndex = nextColorCount % SEQUENCE_1.length;
    let shadeIndex = Math.floor(nextColorCount / SEQUENCE_1.length);
    if (shadeIndex === 0) {
        color = SEQUENCE_1[colorIndex];
    } else if (shadeIndex === 1) {
        color = SEQUENCE_2[colorIndex];
    } else {
        shadeIndex -= 3;
        let floorColor = SEQUENCE_2[colorIndex];
        let ceilColor = SEQUENCE_1[colorIndex];
        let base = Math.floor((shadeIndex / 2) + 1);
        let divisor = 2;
        while (base >= divisor) {
            divisor *= 2;
        }
        base = (base * 2) - divisor + 1;
        let shadePercentage = base / divisor;
        color = buildPercentageColor(floorColor, ceilColor, shadePercentage);
    }
    nextColorCount++;
    return "#" + color.toString(16);
}

function buildPercentageColor(floorColor, ceilColor, shadePercentage) {
    let red = (floorColor & 0xFF0000) + Math.floor(shadePercentage * ((ceilColor & 0xFF0000) - (floorColor & 0xFF0000))) & 0xFF0000;
    let green = (floorColor & 0x00FF00) + Math.floor(shadePercentage * ((ceilColor & 0x00FF00) - (floorColor & 0x00FF00))) & 0x00FF00;
    let blue = (floorColor & 0x0000FF) + Math.floor(shadePercentage * ((ceilColor & 0x0000FF) - (floorColor & 0x0000FF))) & 0x0000FF;
    return red | green | blue;
}
