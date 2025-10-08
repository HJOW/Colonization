<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%
String ctx = request.getSession().getServletContext().getContextPath();
request.setAttribute("ctx", ctx);
%>
<meta charset="UTF-8"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.structure.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/jqueryui/jquery-ui.theme.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/common.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/tm_dark.css"/>"/>
<link rel="stylesheet" href="<c:url value="/resources/lib/program/program.css"/>"/>
<link rel="preconnect" href="https://fonts.googleapis.com"/>
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin/>
<link href="https://fonts.googleapis.com/css2?family=Nanum+Gothic&family=Nanum+Gothic+Coding&display=swap" rel="stylesheet"/>
<script type="text/javascript" src="<c:url value="/resources/lib/jquery-1.12.4.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/jqueryui/jquery-ui.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/moment.min.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/chart.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/common.js"/>"></script>
<script type="text/javascript" src="<c:url value="/resources/lib/react/babel.js"/>"></script>
<script type="text/javascript">$.ctx = "<c:out value="${ctx}"/>";</script>
<script type="text/babel" src="<c:url value="/resources/lib/react/react.production.min.js"/>"></script>
<script type="text/babel" src="<c:url value="/resources/lib/react/react-dom.production.min.js"/>"></script>
<script type="text/babel" src="<c:url value="/resources/lib/program/program.js"/>"></script>