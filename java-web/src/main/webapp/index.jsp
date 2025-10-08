<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.structure.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.theme.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/common.css"/>"/>
<script type="text/javascript" src="<c:url value="/resources/lib/jquery-1.12.4.js"/>"></script>
<script>
$(function() {
	setTimeout(function() {
		location.href = 'program.jsp';
	}, 2000);
});
</script>
</head>
<body>
    <div id='div_program_intro'>
        <h2>Please wait...</h2>
        <div style="width: 100%; text-align: center;"><progress style="text-align: center; margin-top: 300px;"></progress></div>
        <div style='width: 100%; height: 0px; overflow-y: auto;'>
            <iframe src='loading.jsp'></iframe>
        </div>
    </div>
</body>
</html>
