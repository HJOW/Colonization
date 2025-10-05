<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<%@ include file="/common/header.jsp"  %>
<script type="text/babel">
$(function() {
    var root = ReactDOM.createRoot(document.getElementById('div_colonization_root'));
    root.render(<Colonization ctx={$.ctx}/>);
});
</script>
</head>
<body>
    <div id='div_colonization_root'>
        
    </div>
</body>
</html>
