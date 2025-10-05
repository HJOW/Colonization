<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<script type="text/javascript" src="<c:url value="/resources/jquery-1.12.4.js"/>"></script>
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
        <div style="width: 100%;"><progress style="text-align: center; margin-top: 300px;"></progress></div>
        <div style='width: 100%; height: 0px; overflow-y: auto;'>
            <iframe src='loading.jsp'></iframe>
        </div>
    </div>
</body>
</html>
