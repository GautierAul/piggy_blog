<%@ page pageEncoding="UTF-8" %> <%@include file="../../head.jsp" %> <%@include
file="../../header.jsp" %>
<div class="main flex flex-col items-center justify-center grow gap-2">
  <a href="/blog" class="underline text-lg">&#8592; Retour</a>
  <c:if test="${blog.title == null || blog.title == 'undefined'}">
    <p>Ce blog n'existe pas.</p>
  </c:if>
  <c:if test="${blog != null}">
    <div class="flex flex-col">
      <h1 class="text-2xl text-center">${blog.title} ${sessionValue}</h1>
      <div class="text-center">${blog.content}</div>
    </div>
    <c:if test="${author == null}">
      <p>Par : inconnu</p>
    </c:if>

    <c:if test="${author != null}">
      <p>Par : ${author.username}</p>
      <p>Le : ${blog.createdAt}</p>
    </c:if>
  </c:if>
  <div class="flex flex-row gap-2" id="buttons">
    <button class="bg-blue-500 py-2 px-4 rounded-lg">
      <a class="h-full w-full" href="/blog/edit/${blog.id}"> Editer ce blog </a>
    </button>
    <button class="bg-red-700 py-2 px-4 rounded-lg">
      <a class="h-full w-full" href="/blog/delete/${blog.id}">
        Supprimer ce blog</a
      >
    </button>
    <input type="hidden" id="author" value="${author.id}" />
  </div>
</div>

<script>
  window.onload = showButtons;
  function showButtons() {
    var author = document.getElementById("author").value;
    console.log(author != sessionStorage.getItem("userId"));
    if (author != sessionStorage.getItem("userId")) {
      document.getElementById("buttons").style.display = "none";
    } else {
      document.getElementById("buttons").style.display = "flex";
    }
  }
</script>
