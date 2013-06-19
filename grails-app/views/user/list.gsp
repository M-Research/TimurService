
<%@ page import="timurapp.User" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="mobile">
        <g:set var="entityName" value="${message(code: 'user.label', default: 'User')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
	<body>
		<div data-role="header" data-position="fixed">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<div data-role="navbar">
				<ul>
					<li><a data-icon="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
					<li><g:link data-icon="plus" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
				</ul>
			</div>
		</div>
		<div data-role="content">
			<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
			</g:if>
			<ul data-role="listview" data-split-icon="gear" data-filter="true">
				<g:each in="${userInstanceList}" status="i" var="userInstance">
					<li>
						<g:link action="show" id="${userInstance.id}">${userInstance}</g:link>
						<g:link action="edit" id="${userInstance.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					</li>
				</g:each>
			</ol>
			<fieldset class="pagination">
				<g:paginate total="${userInstanceTotal}" />
			</fieldset>
		</div>
		<div data-role="footer">
		</div>
    </body>
</html>
