<%@ page pageEncoding="UTF-8" %> <%@include file="../../head.jsp" %> <%@include
file="../../header.jsp" %>

<div class="main flex flex-col items-center justify-center grow gap-2">
  <a href="/blog/create" class="rounded border border-gray-500 p-2"
    >+ Ajouter un piggy</a
  >

  <c:forEach items="${blogs}" var="blog">
    <div class="rounded border border-gray-500 p-2">
      <a href="/blog/${blog.id}">${blog.title}</a>
    </div>
  </c:forEach>
</div>
